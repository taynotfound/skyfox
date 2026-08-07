package de.taymaerz.skyfox.feeder.ui.settings

import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.datastore.value
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.uix.ViewModel4
import de.taymaerz.skyfox.feeder.core.config.FeederSettings
import de.taymaerz.skyfox.feeder.core.monitor.FeederWorkerHelper
import kotlinx.coroutines.flow.map
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class FeederSettingsViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val settings: FeederSettings,
    private val feederWorkerHelper: FeederWorkerHelper,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Settings", "Feeder", "VM"),
) {

    val state = settings.feederMonitorInterval.flow.map { interval ->
        State(currentIntervalMinutes = interval.toMinutes().toFloat())
    }.asStateFlow()

    fun updateFeederInterval(interval: Duration) = launch {
        log(tag) { "updateFeederInterval($interval)" }
        settings.feederMonitorInterval.value(interval)
        feederWorkerHelper.updateWorker()
    }

    fun resetFeederInterval() = launch {
        log(tag) { "resetFeederInterval()" }
        settings.feederMonitorInterval.value(FeederSettings.DEFAULT_CHECK_INTERVAL)
        feederWorkerHelper.updateWorker()
    }

    data class State(
        val currentIntervalMinutes: Float,
    )
}
