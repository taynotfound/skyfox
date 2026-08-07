package de.taymaerz.skyfox.watch.ui.details

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.chart.ChartState
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.flight.FlightRepo
import de.taymaerz.skyfox.common.flight.FlightRoute
import de.taymaerz.skyfox.common.flow.SingleEventFlow
import de.taymaerz.skyfox.common.flow.combine
import de.taymaerz.skyfox.common.flow.replayingShare
import de.taymaerz.skyfox.common.location.LocationManager2
import de.taymaerz.skyfox.common.uix.ViewModel4
import de.taymaerz.skyfox.main.core.AircraftRepo
import de.taymaerz.skyfox.main.core.aircraft.Aircraft
import de.taymaerz.skyfox.main.core.findByCallsign
import de.taymaerz.skyfox.main.core.findByHex
import de.taymaerz.skyfox.map.core.MapOptions
import de.taymaerz.skyfox.map.ui.DestinationMap
import de.taymaerz.skyfox.search.core.SearchQuery
import de.taymaerz.skyfox.search.core.SearchRepo
import de.taymaerz.skyfox.watch.core.WatchId
import de.taymaerz.skyfox.watch.core.WatchRepo
import de.taymaerz.skyfox.watch.core.history.WatchActivityData
import de.taymaerz.skyfox.watch.core.history.WatchCountChartData
import de.taymaerz.skyfox.watch.core.history.WatchHistoryRepo
import de.taymaerz.skyfox.watch.core.types.AircraftWatch
import de.taymaerz.skyfox.watch.core.types.FlightWatch
import de.taymaerz.skyfox.watch.core.types.LocationWatch
import de.taymaerz.skyfox.watch.core.types.SquawkWatch
import de.taymaerz.skyfox.watch.core.types.Watch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import java.time.Duration
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WatchDetailsViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val watchRepo: WatchRepo,
    private val searchRepo: SearchRepo,
    private val aircraftRepo: AircraftRepo,
    private val locationManager2: LocationManager2,
    private val flightRepo: FlightRepo,
    private val historyRepo: WatchHistoryRepo,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Watch", "Action", "Dialog", "ViewModel"),
) {

    private var watchId: WatchId = ""
    private var chartLoadJob: Job? = null
    val events = SingleEventFlow<WatchDetailsEvents>()
    private val trigger = MutableStateFlow(UUID.randomUUID())
    private val chartData = MutableStateFlow<WatchDetailChartData?>(null)

    fun init(watchId: WatchId) {
        if (this.watchId == watchId) return
        this.watchId = watchId

        watchRepo.status
            .map { alerts -> alerts.singleOrNull { it.id == watchId } }
            .filter { it == null }
            .take(1)
            .onEach {
                log(tag) { "Alert data for $watchId is no longer available" }
                navUp()
            }
            .launchInViewModel()

        chartLoadJob?.cancel()
        chartData.value = null
        chartLoadJob = viewModelScope.launch {
            val since30d = Instant.now().minus(Duration.ofDays(30))
            val watches = watchRepo.watches.first()
            val watch = watches.find { it.id == watchId } ?: return@launch
            chartData.value = when (watch) {
                is SquawkWatch, is LocationWatch -> {
                    WatchDetailChartData.Count(historyRepo.getCountChartData(watchId, since30d))
                }
                is AircraftWatch, is FlightWatch -> {
                    WatchDetailChartData.Activity(historyRepo.getActivityData(watchId, since30d))
                }
            }
        }
    }

    private val status = watchRepo.status
        .mapNotNull { data -> data.singleOrNull { it.id == watchId } }
        .replayingShare(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val aircraft = status
        .mapLatest { alert ->
            when (alert) {
                is AircraftWatch.Status -> alert.tracked.firstOrNull() ?: aircraftRepo.findByHex(alert.hex)
                is FlightWatch.Status -> alert.tracked.firstOrNull() ?: aircraftRepo.findByCallsign(alert.callsign)
                is SquawkWatch.Status -> null
                is LocationWatch.Status -> null
            }
        }
        .distinctUntilChanged()
        .replayingShare(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val route: Flow<FlightRoute?> = aircraft
        .onEach { ac -> ac?.let { flightRepo.prefetch(it.hex, it.callsign) } }
        .flatMapLatest { ac ->
            if (ac == null) flowOf(null)
            else flightRepo.getByHex(ac.hex)
        }
        .distinctUntilChanged()
        .onStart { emit(null) }

    val state = combine(
        trigger,
        locationManager2.state,
        status,
        aircraft,
        route,
        chartData,
    ) { _, locationState, alert, aircraft, flightRoute, chart ->
        State(
            status = alert,
            aircraft = aircraft,
            distanceInMeter = run {
                if (locationState !is LocationManager2.State.Available) return@run null
                val location = aircraft?.location ?: return@run null
                locationState.location.distanceTo(location)
            },
            route = flightRoute,
            chartState = when {
                chart == null -> ChartState.Loading
                chart is WatchDetailChartData.Count && chart.chartData.counts.size < 2 -> ChartState.NoData
                chart is WatchDetailChartData.Activity && chart.activityData.checks.size < 2 -> ChartState.NoData
                else -> ChartState.Ready(chart)
            },
        )
    }.asStateFlow()

    fun removeAlert(confirmed: Boolean = false) = launch {
        log(tag) { "removeAlert()" }
        if (!confirmed) {
            events.emit(WatchDetailsEvents.RemovalConfirmation(watchId))
            return@launch
        }
        watchRepo.delete(state.first()?.status?.id ?: return@launch)
    }

    fun showOnMap() = launch {
        log(tag) { "showOnMap()" }
        val mapOptions = when (val watchStatus = status.first()) {
            is AircraftWatch.Status -> MapOptions.focus(watchStatus.hex)
            is SquawkWatch.Status -> {
                val hexes = searchRepo.search(SearchQuery.Squawk(watchStatus.squawk))
                MapOptions.focusAircraft(hexes.aircraft)
            }

            is FlightWatch.Status -> {
                val hexes = searchRepo.search(SearchQuery.Callsign(watchStatus.callsign))
                MapOptions.focusAircraft(hexes.aircraft)
            }

            is LocationWatch.Status -> {
                val results = searchRepo.search(
                    SearchQuery.Position(watchStatus.watch.center, watchStatus.watch.radiusInMeters.toLong())
                )
                if (results.aircraft.isNotEmpty()) {
                    MapOptions.focusAircraft(results.aircraft)
                } else {
                    MapOptions(
                        camera = MapOptions.Camera(
                            lat = watchStatus.watch.latitude,
                            lon = watchStatus.watch.longitude,
                            zoom = 9.0,
                        )
                    )
                }
            }
        }
        navTo(DestinationMap(mapOptions = mapOptions))
    }

    fun updateNote(note: String) = launch {
        log(tag) { "updateNote($note)" }
        watchRepo.updateNote(watchId, note.trim())
    }

    fun enableNotifications(enabled: Boolean) = launch {
        log(tag) { "enableNotification($enabled)" }
        watchRepo.setNotification(watchId, enabled)
    }

    fun updateLocation(latitude: Double, longitude: Double, radiusInMeters: Float, label: String) = launch {
        log(tag) { "updateLocation($latitude, $longitude, $radiusInMeters, $label)" }
        watchRepo.updateLocation(watchId, latitude, longitude, radiusInMeters, label.trim())
    }

    sealed interface WatchDetailChartData {
        data class Count(val chartData: WatchCountChartData) : WatchDetailChartData
        data class Activity(val activityData: WatchActivityData) : WatchDetailChartData
    }

    data class State(
        val status: Watch.Status,
        val aircraft: Aircraft?,
        val distanceInMeter: Float?,
        val route: FlightRoute? = null,
        val chartState: ChartState<WatchDetailChartData> = ChartState.Loading,
    )
}
