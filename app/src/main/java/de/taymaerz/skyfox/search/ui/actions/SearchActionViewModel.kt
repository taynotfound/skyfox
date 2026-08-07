package de.taymaerz.skyfox.search.ui.actions

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.flight.FlightRepo
import de.taymaerz.skyfox.common.flight.FlightRoute
import de.taymaerz.skyfox.common.flow.combine
import de.taymaerz.skyfox.common.flow.replayingShare
import de.taymaerz.skyfox.common.location.LocationManager2
import de.taymaerz.skyfox.common.uix.ViewModel4
import de.taymaerz.skyfox.main.core.AircraftRepo
import de.taymaerz.skyfox.main.core.aircraft.Aircraft
import de.taymaerz.skyfox.main.core.aircraft.AircraftHex
import de.taymaerz.skyfox.main.core.getByHex
import de.taymaerz.skyfox.map.core.MapOptions
import de.taymaerz.skyfox.map.ui.DestinationMap
import de.taymaerz.skyfox.watch.core.WatchRepo
import de.taymaerz.skyfox.watch.core.types.AircraftWatch
import de.taymaerz.skyfox.watch.core.types.Watch
import de.taymaerz.skyfox.watch.ui.DestinationCreateAircraftWatch
import de.taymaerz.skyfox.watch.ui.DestinationWatchDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class SearchActionViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val aircraftRepo: AircraftRepo,
    private val watchRepo: WatchRepo,
    private val locationManager2: LocationManager2,
    private val flightRepo: FlightRepo,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Search", "Action", "ViewModel"),
) {

    private var aircraftHex: AircraftHex = ""
    private val hexFlow = MutableStateFlow<AircraftHex?>(null)

    fun init(hex: AircraftHex) {
        if (this.aircraftHex == hex) return
        this.aircraftHex = hex
        hexFlow.value = hex
        log(tag) { "Loading for $aircraftHex" }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val aircraft = hexFlow
        .filterNotNull()
        .flatMapLatest { aircraftRepo.getByHex(it) }
        .filterNotNull()
        .replayingShare(viewModelScope)

    init {
        aircraft
            .onEach { ac -> flightRepo.prefetch(ac.hex, ac.callsign) }
            .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val route: Flow<FlightRoute?> = aircraft
        .flatMapLatest { ac -> flightRepo.getByHex(ac.hex) }
        .distinctUntilChanged()

    val state = combine(
        watchRepo.watches,
        aircraft,
        locationManager2.state,
        route,
    ) { watches, ac, locationState, flightRoute ->
        State(
            aircraft = ac,
            distanceInMeter = run {
                if (locationState !is LocationManager2.State.Available) return@run null
                val location = ac.location ?: return@run null
                locationState.location.distanceTo(location)
            },
            watch = watches.filterIsInstance<AircraftWatch>().firstOrNull { it.matches(ac) },
            route = flightRoute,
        )
    }.asStateFlow()

    fun showMap() = launch {
        log(tag) { "showMap()" }
        val ac = aircraft.firstOrNull()
        val mapOptions = ac?.let { MapOptions.focus(it) } ?: MapOptions.focus(aircraftHex)
        navTo(DestinationMap(mapOptions = mapOptions))
    }

    fun showWatch() = launch {
        log(tag) { "showWatch()" }
        val watch = state.firstOrNull()?.watch
        if (watch != null) {
            navTo(DestinationWatchDetails(watchId = watch.id))
        } else {
            navTo(DestinationCreateAircraftWatch(hex = aircraftHex))
        }
    }

    data class State(
        val aircraft: Aircraft,
        val distanceInMeter: Float?,
        val watch: Watch?,
        val route: FlightRoute? = null,
    )
}
