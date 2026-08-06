package eu.darken.apl.stats.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import eu.darken.apl.common.chart.ChartPoint
import eu.darken.apl.common.coroutine.DispatcherProvider
import eu.darken.apl.common.debug.logging.logTag
import eu.darken.apl.common.uix.ViewModel4
import eu.darken.apl.main.core.AircraftRepo
import eu.darken.apl.main.core.aircraft.Aircraft
import eu.darken.apl.search.ui.DestinationSearch
import eu.darken.apl.watch.core.db.WatchDatabase
import eu.darken.apl.watch.core.db.history.WatchCheckDao
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val database: WatchDatabase,
    private val aircraftRepo: AircraftRepo,
) : ViewModel4(dispatcherProvider, logTag("Stats", "ViewModel")) {

    private val dao: WatchCheckDao get() = database.checks

    data class TopAircraft(
        val hex: String,
        val sightings: Int,
        val aircraft: Aircraft?,
    )

    data class StatsState(
        val totalHits: Int = 0,
        val hitsToday: Int = 0,
        val hitsThisWeek: Int = 0,
        val uniqueAircraft: Int = 0,
        val currentStreak: Int = 0,
        val bestStreak: Int = 0,
        val busiestHour: Int? = null,
        val busiestDayName: String? = null,
        val topAircraft: List<TopAircraft> = emptyList(),
        val activity: List<ChartPoint> = emptyList(),
    )

    val state = combine(
        flow { emit(dao.getAll()) },
        aircraftRepo.aircraft,
    ) { checks, aircraftMap ->
        val zone = ZoneId.systemDefault()
        val now = Instant.now()
        val hits = checks.filter { it.aircraftcount > 0 }

        // hex -> sightings
        val hexCounts = hits
            .mapNotNull { it.seenHexes }
            .flatMap { it.split(",") }
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
            .groupingBy { it }
            .eachCount()

        // streaks over days with hits
        val hitDays = hits.map { it.checkedAt.atZone(zone).toLocalDate() }.toSortedSet()
        var best = 0
        var run = 0
        var prev: LocalDate? = null
        for (day in hitDays) {
            run = if (prev != null && prev.plusDays(1) == day) run + 1 else 1
            if (run > best) best = run
            prev = day
        }
        val today = LocalDate.now(zone)
        val current = when {
            hitDays.isEmpty() -> 0
            hitDays.last() != today && hitDays.last() != today.minusDays(1) -> 0
            else -> {
                var c = 1
                var d = hitDays.last()
                while (hitDays.contains(d.minusDays(1))) {
                    c++; d = d.minusDays(1)
                }
                c
            }
        }

        // busiest hour + weekday
        val busiestHour = hits.groupingBy { it.checkedAt.atZone(zone).hour }
            .eachCount().maxByOrNull { it.value }?.key
        val busiestDay = hits.groupingBy { it.checkedAt.atZone(zone).dayOfWeek }
            .eachCount().maxByOrNull { it.value }?.key

        // last 14 days activity for chart
        val since = now.minus(Duration.ofDays(14))
        val perDay = hits.filter { it.checkedAt >= since }
            .groupingBy { it.checkedAt.atZone(zone).toLocalDate() }
            .eachCount()
        val activity = (0..13).map { offset ->
            val day = today.minusDays((13 - offset).toLong())
            ChartPoint(day.atStartOfDay(zone).toInstant(), (perDay[day] ?: 0).toDouble())
        }

        StatsState(
            totalHits = hits.size,
            hitsToday = hits.count { it.checkedAt >= today.atStartOfDay(zone).toInstant() },
            hitsThisWeek = hits.count { it.checkedAt >= now.minus(7, ChronoUnit.DAYS) },
            uniqueAircraft = hexCounts.size,
            currentStreak = current,
            bestStreak = best,
            busiestHour = busiestHour,
            busiestDayName = busiestDay?.getDisplayName(
                java.time.format.TextStyle.FULL,
                java.util.Locale.getDefault()
            ),
            topAircraft = hexCounts.entries.sortedByDescending { it.value }.take(10).map {
                TopAircraft(hex = it.key, sightings = it.value, aircraft = aircraftMap[it.key])
            },
            activity = activity,
        )
    }.asStateFlow()

    fun showAircraft(hex: String) {
        navTo(DestinationSearch(targetHexes = listOf(hex)))
    }
}
