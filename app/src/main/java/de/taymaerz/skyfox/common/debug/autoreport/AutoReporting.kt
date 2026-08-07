package de.taymaerz.skyfox.common.debug.autoreport

import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.main.core.GeneralSettings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoReporting @Inject constructor(
    private val generalSettings: GeneralSettings,
) {

    fun setup() {
        val isEnabled = generalSettings.isAutoReportingEnabled.flow
        log(TAG) { "setup(): isEnabled=$isEnabled" }
    }

    companion object {
        private val TAG = logTag("Debug", "AutoReporting")
    }
}