package de.taymaerz.skyfox.watch.ui

import android.location.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.WebpageTool
import de.taymaerz.skyfox.common.chart.ChartPoint
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.location.LocationManager2
import de.taymaerz.skyfox.common.planespotters.PlanespottersMeta
import de.taymaerz.skyfox.common.uix.ViewModel4
import de.taymaerz.skyfox.main.core.AircraftRepo
import de.taymaerz.skyfox.main.core.aircraft.Aircraft
import de.taymaerz.skyfox.main.core.findByCallsign
import de.taymaerz.skyfox.main.core.findByHex
import de.taymaerz.skyfox.search.ui.DestinationSearch
import de.taymaerz.skyfox.search.ui.actions.DestinationSearchAction
import de.taymaerz.skyfox.watch.core.WatchId
import de.taymaerz.skyfox.watch.core.WatchRepo
import de.taymaerz.skyfox.watch.core.alerts.WatchMonitor
import de.taymaerz.skyfox.watch.core.history.WatchActivityCheck
import de.taymaerz.skyfox.watch.core.history.WatchHistoryRepo
import de.taymaerz.skyfox.watch.core.types.AircraftWatch
import de.taymaerz.skyfox.watch.core.types.FlightWatch
import de.taymaerz.skyfox.watch.core.types.LocationWatch
import de.taymaerz.skyfox.watch.core.types.SquawkWatch
import de.taymaerz.skyfox.watch.core.types.Watch
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import de.taymaerz.skyfox.common.datastore.value
import de.taymaerz.skyfox.common.flow.combine
import de.taymaerz.skyfox.watch.core.WatchSettings
import de.taymaerz.skyfox.watch.core.WatchSortMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class WatchListViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val watchRepo: WatchRepo,
    private val watchMonitor: WatchMonitor,
    private val webpageTool: WebpageTool,
    private val locationManager2: LocationManager2,
    private val aircraftRepo: AircraftRepo,
    private val historyRepo: WatchHistoryRepo,
    private val watchSettings: WatchSettings,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Watch", "List", "ViewModel"),
) {

    private val refreshTimer = callbackFlow {
        while (isActive) {
            refresh()
            send(Unit)
            delay(60 * 1000)
        }
        awaitClose()
    }

    private val sparklineCache = MutableStateFlow<Map<WatchId, WatchSparklineData>>(emptyMap())

    init {
        // Initial load of all sparklines
        launch {
            loadAllSparklines()
        }

        // Incremental updates when new checks arrive
        historyRepo.firehose
            .mapNotNull { it }
            .onEach { check ->
                val since7d = Instant.now().minus(Duration.ofDays(7))
                val watches = watchRepo.watches.first()
                val watch = watches.find { it.id == check.watchId } ?: return@onEach
                val data = loadSparkline(check.watchId, watch, since7d)
                sparklineCache.update { it + (check.watchId to data) }
            }
            .launchInViewModel()
    }

    private suspend fun loadAllSparklines() {
        val watches = watchRepo.watches.first()
        if (watches.isEmpty()) return

        val since7d = Instant.now().minus(Duration.ofDays(7))
        val allIds = watches.map { it.id }.toSet()
        val batchRows = historyRepo.getSparklineDataBatch(allIds, since7d)

        val result = watches.associate { watch ->
            val rows = batchRows[watch.id] ?: emptyList()
            val data = when (watch) {
                is AircraftWatch, is FlightWatch -> WatchSparklineData.Activity(
                    rows.map { WatchActivityCheck(it.checkedAt, it.aircraftCount) }
                )
                is SquawkWatch, is LocationWatch -> WatchSparklineData.Count(
                    rows.map { ChartPoint(it.checkedAt, it.aircraftCount.toDouble()) }
                )
            }
            watch.id to data
        }
        sparklineCache.update { it + result }
    }

    private suspend fun loadSparkline(watchId: WatchId, watch: Watch, since: Instant): WatchSparklineData {
        return when (watch) {
            is AircraftWatch, is FlightWatch -> {
                val data = historyRepo.getActivityData(watchId, since)
                WatchSparklineData.Activity(data.checks)
            }
            is SquawkWatch, is LocationWatch -> {
                val data = historyRepo.getCountChartData(watchId, since)
                WatchSparklineData.Count(data.counts)
            }
        }
    }

    val state = combine(
        refreshTimer,
        watchRepo.status,
        locationManager2.state,
        watchRepo.isRefreshing,
        sparklineCache,
        watchSettings.watchSortMode.flow,
    ) { _, alerts, locationState, isRefreshing, sparklines, sortMode ->
        val ourLocation = (locationState as? LocationManager2.State.Available)?.location

        val sorted = when (sortMode) {
            WatchSortMode.BY_NOTE -> alerts.sortedWith(
                compareBy<Watch.Status> { it.note.isBlank() }
                    .thenBy { it.note }
                    .thenByDescending { it.watch.addedAt }
            )

            WatchSortMode.BY_LAST_SEEN -> alerts.sortedWith(
                compareBy<Watch.Status> { it.lastSeenAt == null }
                    .thenByDescending { it.lastSeenAt }
                    .thenByDescending { it.watch.addedAt }
            )

            WatchSortMode.BY_CREATED -> alerts.sortedByDescending { it.watch.addedAt }
        }

        val items = sorted
            .map { alert ->
                when (alert) {
                    is AircraftWatch.Status -> {
                        val aircraft = aircraftRepo.findByHex(alert.hex)
                        WatchItem.Single(
                            status = alert,
                            aircraft = aircraft,
                            ourLocation = ourLocation,
                            sparkline = sparklines[alert.id] as? WatchSparklineData.Activity,
                        )
                    }

                    is FlightWatch.Status -> {
                        val aircraft = aircraftRepo.findByCallsign(alert.callsign)
                        WatchItem.Single(
                            status = alert,
                            aircraft = aircraft,
                            ourLocation = ourLocation,
                            sparkline = sparklines[alert.id] as? WatchSparklineData.Activity,
                        )
                    }

                    is SquawkWatch.Status -> WatchItem.Multi(
                        status = alert,
                        ourLocation = ourLocation,
                        sparkline = sparklines[alert.id] as? WatchSparklineData.Count,
                    )

                    is LocationWatch.Status -> WatchItem.Multi(
                        status = alert,
                        ourLocation = ourLocation,
                        sparkline = sparklines[alert.id] as? WatchSparklineData.Count,
                    )
                }
            }
        State(
            items = items,
            isRefreshing = isRefreshing,
            currentSortMode = sortMode,
        )
    }.asStateFlow()

    fun setSortMode(mode: WatchSortMode) = launch {
        log(tag) { "setSortMode($mode)" }
        watchSettings.watchSortMode.value(mode)
    }

    fun refresh() = launch {
        log(tag) { "refresh()" }
        watchMonitor.check()
    }

    fun openWatchDetails(watchId: String) {
        navTo(DestinationWatchDetails(watchId = watchId))
    }

    fun openThumbnail(meta: PlanespottersMeta) {
        navTo(de.taymaerz.skyfox.gallery.ui.DestinationGallery(hex = meta.hex))
    }

    fun showAircraftDetails(aircraft: Aircraft) {
        navTo(DestinationSearchAction(hex = aircraft.hex))
    }

    fun showSquawkInSearch(squawk: String) {
        navTo(DestinationSearch(targetSquawks = listOf(squawk)))
    }

    fun deleteSelected(ids: Set<WatchId>) = launch {
        if (ids.isEmpty()) return@launch
        log(tag) { "deleteSelected(${ids.size} items)" }
        watchRepo.deleteBatch(ids)
    }

    fun showAddWatchOptions(type: WatchType) {
        when (type) {
            WatchType.FLIGHT -> navTo(DestinationCreateFlightWatch())
            WatchType.AIRCRAFT -> navTo(DestinationCreateAircraftWatch())
            WatchType.SQUAWK -> navTo(DestinationCreateSquawkWatch())
            WatchType.LOCATION -> navTo(DestinationCreateLocationWatch())
        }
    }

    enum class WatchType { FLIGHT, AIRCRAFT, SQUAWK, LOCATION }

    sealed interface WatchSparklineData {
        data class Count(val points: List<ChartPoint>) : WatchSparklineData
        data class Activity(val checks: List<WatchActivityCheck>) : WatchSparklineData
    }

    sealed interface WatchItem {
        val status: Watch.Status

        data class Single(
            override val status: Watch.Status,
            val aircraft: Aircraft?,
            val ourLocation: Location?,
            val sparkline: WatchSparklineData.Activity? = null,
        ) : WatchItem

        data class Multi(
            override val status: Watch.Status,
            val ourLocation: Location?,
            val sparkline: WatchSparklineData.Count? = null,
        ) : WatchItem
    }

    data class State(
        val items: List<WatchItem>,
        val isRefreshing: Boolean = false,
        val currentSortMode: WatchSortMode = WatchSortMode.BY_NOTE,
    )
}

private val Watch.Status.lastSeenAt: Instant?
    get() = tracked.maxOfOrNull { it.seenAt } ?: lastHit?.checkAt
