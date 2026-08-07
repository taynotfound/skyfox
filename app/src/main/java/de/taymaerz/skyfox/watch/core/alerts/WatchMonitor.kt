package de.taymaerz.skyfox.watch.core.alerts

import de.taymaerz.skyfox.common.datastore.value
import de.taymaerz.skyfox.common.debug.logging.Logging.Priority.VERBOSE
import de.taymaerz.skyfox.common.debug.logging.Logging.Priority.WARN
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.main.core.aircraft.Aircraft
import de.taymaerz.skyfox.search.core.SearchQuery
import de.taymaerz.skyfox.search.core.SearchRepo
import de.taymaerz.skyfox.watch.core.WatchRepo
import de.taymaerz.skyfox.watch.core.WatchSettings
import de.taymaerz.skyfox.watch.core.history.WatchHistoryRepo
import de.taymaerz.skyfox.watch.core.types.AircraftWatch
import de.taymaerz.skyfox.watch.core.types.FlightWatch
import de.taymaerz.skyfox.watch.core.types.LocationWatch
import de.taymaerz.skyfox.watch.core.types.SquawkWatch
import de.taymaerz.skyfox.watch.core.types.Watch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchMonitor @Inject constructor(
    private val settings: WatchSettings,
    private val watchRepo: WatchRepo,
    private val historyRepo: WatchHistoryRepo,
    private val searchRepo: SearchRepo,
    private val notifications: WatchAlertNotifications,
) {
    private val mutex = Mutex()

    suspend fun check() = mutex.withLock {
        log(TAG) { "check()" }
        val currentWatches = watchRepo.watches.first()
        val alerts = mutableMapOf<Watch, Collection<Aircraft>>()

        suspend fun Watch.process(results: Collection<Aircraft>) {
            log(TAG, VERBOSE) { "Checking $this" }
            // TODO filter for position
            val matches = results.filter { matches(it) }

            if (matches.isNotEmpty()) {
                when {
                    !isNotificationEnabled -> {
                        log(TAG) { "Notifications are disabled for $this" }
                    }

                    historyRepo.getLastCheck(id)?.aircraftCount != 0 -> {
                        log(TAG, VERBOSE) { "Skipping snoozed alert" }
                    }

                    else -> {
                        log(TAG) { "Will notify about $this" }
                        alerts[this] = matches
                    }
                }
            } else {
                log(TAG, VERBOSE) { "No matching aircraft for ${id}" }
            }

            historyRepo.addCheck(id, matches.size)
        }

        currentWatches.filterIsInstance<AircraftWatch>().let { ws ->
            if (ws.isEmpty()) return@let
            val batchResults = searchRepo.search(SearchQuery.Hex(ws.map { it.hex }.toSet()))
            ws.forEach { it.process(batchResults.aircraft) }
        }

        delay(200)

        currentWatches.filterIsInstance<FlightWatch>().let { ws ->
            if (ws.isEmpty()) return@let
            val batchResults = searchRepo.search(SearchQuery.Callsign(ws.map { it.callsign }.toSet()))
            ws.forEach { it.process(batchResults.aircraft) }
        }

        delay(200)

        currentWatches.filterIsInstance<SquawkWatch>().let { ws ->
            if (ws.isEmpty()) return@let
            val batchResults = searchRepo.search(SearchQuery.Squawk(ws.map { it.code }.toSet()))
            ws.forEach { it.process(batchResults.aircraft) }
        }

        currentWatches.filterIsInstance<LocationWatch>().forEach { watch ->
            val results = searchRepo.search(SearchQuery.Position(watch.center, watch.radiusInMeters.toLong()))
            watch.process(results.aircraft)
        }

        log(TAG) { "Notifying of ${alerts.size} watch matches" }
        alerts.forEach { notifications.alert(it.key, it.value) }

        val lastCleanup = settings.lastCleanup.value()
        if (Duration.between(lastCleanup, Instant.now()) > Duration.ofDays(1)) {
            try {
                historyRepo.cleanupOldChecks()
                settings.lastCleanup.update { Instant.now() }
            } catch (e: Exception) {
                log(TAG, WARN) { "Cleanup failed: $e" }
            }
        }
    }

    companion object {
        private val TAG = logTag("Watch", "Monitor")
    }
}
