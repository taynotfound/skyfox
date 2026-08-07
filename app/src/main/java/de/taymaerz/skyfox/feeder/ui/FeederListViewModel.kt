package de.taymaerz.skyfox.feeder.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.WebpageTool
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.datastore.value
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.uix.ViewModel4
import de.taymaerz.skyfox.feeder.core.Feeder
import de.taymaerz.skyfox.feeder.core.FeederRepo
import de.taymaerz.skyfox.feeder.core.ReceiverId
import de.taymaerz.skyfox.feeder.core.config.FeederSettings
import de.taymaerz.skyfox.feeder.core.config.FeederSortMode
import de.taymaerz.skyfox.common.chart.ChartPoint
import de.taymaerz.skyfox.feeder.core.stats.FeederStatsDatabase
import de.taymaerz.skyfox.map.core.AirplanesLive
import de.taymaerz.skyfox.map.core.MapOptions
import de.taymaerz.skyfox.map.core.toMapFeedId
import de.taymaerz.skyfox.map.ui.DestinationMap
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class FeederListViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val feederRepo: FeederRepo,
    private val webpageTool: WebpageTool,
    private val feederSettings: FeederSettings,
    private val feederStatsDatabase: FeederStatsDatabase,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Feeder", "List", "ViewModel"),
) {

    private val refreshTimer = callbackFlow {
        while (isActive) {
            send(Unit)
            delay(1000)
        }
        awaitClose()
    }

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    private val sparklineData = combine(
        feederStatsDatabase.beastStats.firehose().debounce(2_000),
        feederSettings.feederGroup.flow,
    ) { _, group ->
        val since7d = Instant.now().minus(Duration.ofDays(7))
        group.configs.associate { config ->
            config.receiverId to feederRepo.getBeastChartData(config.receiverId, since7d).messageRate
        }
    }.stateIn(vmScope, SharingStarted.Eagerly, emptyMap())

    val state = combine(
        refreshTimer,
        feederRepo.feeders,
        feederRepo.isRefreshing,
        feederSettings.feederSortMode.flow,
        sparklineData,
    ) { _, feeders, isRefreshing, sortMode, sparklines ->
        val offlineStates = feeders.associate { it.id to feederRepo.isOffline(it) }

        val sortedFeeders = when (sortMode) {
            FeederSortMode.BY_LABEL -> feeders.sortedBy { it.label }
            FeederSortMode.BY_MESSAGE_RATE -> feeders.sortedByDescending { it.beastMessageRate }
        }

        val feederItems = sortedFeeders.map { feeder ->
            FeederItem(
                feeder = feeder,
                isOffline = offlineStates[feeder.id]!!,
                beastSparkline = sparklines[feeder.id] ?: emptyList(),
            )
        }

        State(
            feeders = feederItems,
            feederCount = feederItems.size,
            isRefreshing = isRefreshing,
            hasOfflineFeeders = offlineStates.values.any { it },
            currentSortMode = sortMode,
        )
    }.asStateFlow()

    fun refresh() = launch {
        log(tag) { "refresh()" }
        feederRepo.refresh()
    }

    fun startFeeding() = launch {
        webpageTool.open(AirplanesLive.URL_START_FEEDING)
    }

    fun setSortMode(mode: FeederSortMode) = launch {
        log(tag) { "setSortMode($mode)" }
        feederSettings.feederSortMode.value(mode)
    }

    fun openFeederAction(feederId: String) {
        navTo(DestinationFeederAction(receiverId = feederId))
    }

    fun showFeedsOnMap(feederIds: Set<String>) = launch {
        log(tag) { "showFeedsOnMap($feederIds)" }
        val ids = feederIds.map { it.toMapFeedId() }.toSet()
        navTo(DestinationMap(mapOptions = MapOptions(feeds = ids)))
    }

    fun goToAddFeeder() {
        navTo(DestinationAddFeeder())
    }

    data class FeederItem(
        val feeder: Feeder,
        val isOffline: Boolean,
        val beastSparkline: List<ChartPoint> = emptyList(),
    )

    data class State(
        val feeders: List<FeederItem>,
        val feederCount: Int,
        val isRefreshing: Boolean = false,
        val hasOfflineFeeders: Boolean = false,
        val currentSortMode: FeederSortMode = FeederSortMode.BY_LABEL,
    )
}
