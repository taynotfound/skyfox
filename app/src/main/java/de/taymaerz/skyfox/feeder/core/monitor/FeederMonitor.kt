package de.taymaerz.skyfox.feeder.core.monitor

import de.taymaerz.skyfox.common.debug.logging.Logging.Priority.ERROR
import de.taymaerz.skyfox.common.debug.logging.Logging.Priority.INFO
import de.taymaerz.skyfox.common.debug.logging.asLog
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.feeder.core.FeederRepo
import de.taymaerz.skyfox.feeder.core.config.FeederSettings
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeederMonitor @Inject constructor(
    private val settings: FeederSettings,
    private val feederRepo: FeederRepo,
    private val notifications: FeederMonitorNotifications,
) {
    suspend fun check() {
        log(TAG) { "check()" }

        try {
            feederRepo.refresh()
        } catch (e: Exception) {
            log(TAG, ERROR) { "Failed to refresh ${e.asLog()}" }
        }

        val offlineDevices = feederRepo.feeders.first()
            .filter { feederRepo.isOffline(it) }
            .onEach { log(TAG, INFO) { "Feeder has been offline for a while... $it" } }

        notifications.notifyOfOfflineDevices(offlineDevices)
    }

    companion object {
        private val TAG = logTag("Feeder", "Monitor")
    }
}
