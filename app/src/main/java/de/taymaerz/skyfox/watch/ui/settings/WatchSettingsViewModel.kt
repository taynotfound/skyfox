package de.taymaerz.skyfox.watch.ui.settings

import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.datastore.value
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.uix.ViewModel4
import de.taymaerz.skyfox.watch.core.WatchSettings
import de.taymaerz.skyfox.watch.core.alerts.WatchWorkerHelper
import kotlinx.coroutines.flow.map
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class WatchSettingsViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val settings: WatchSettings,
    private val watchWorkerHelper: WatchWorkerHelper,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Settings", "Watch", "VM"),
) {

    val state = settings.watchMonitorInterval.flow.map { interval ->
        State(currentIntervalMinutes = interval.toMinutes().toFloat())
    }.asStateFlow()

    fun updateWatchInterval(interval: Duration) = launch {
        log(tag) { "updateWatchInterval($interval)" }
        settings.watchMonitorInterval.value(interval)
        watchWorkerHelper.updateWorker()
    }

    fun resetWatchInterval() = launch {
        log(tag) { "resetWatchInterval()" }
        settings.watchMonitorInterval.value(WatchSettings.DEFAULT_CHECK_INTERVAL)
        watchWorkerHelper.updateWorker()
    }

    data class State(
        val currentIntervalMinutes: Float,
    )
}
