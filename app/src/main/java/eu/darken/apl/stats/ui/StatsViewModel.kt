package eu.darken.apl.stats.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import eu.darken.apl.common.coroutine.DispatcherProvider
import eu.darken.apl.common.debug.logging.logTag
import eu.darken.apl.common.uix.ViewModel4
import eu.darken.apl.watch.core.db.WatchDatabase
import eu.darken.apl.watch.core.db.history.WatchCheckDao
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val database: WatchDatabase,
) : ViewModel4(dispatcherProvider, logTag("Stats", "ViewModel")) {

    private val dao: WatchCheckDao get() = database.checks

    data class StatsState(
        val totalHits: Int = 0,
        val hitsToday: Int = 0,
        val hitsThisWeek: Int = 0,
        val topHexes: List<WatchCheckDao.TopHexRow> = emptyList(),
    )

    val state = flow {
        val now = Instant.now()
        emit(
            StatsState(
                totalHits = dao.totalHits(),
                hitsToday = dao.hitsInPeriod(now.truncatedTo(ChronoUnit.DAYS)),
                hitsThisWeek = dao.hitsInPeriod(now.minus(7, ChronoUnit.DAYS)),
                topHexes = dao.getTopHexes(10),
            )
        )
    }.asStateFlow()
}
