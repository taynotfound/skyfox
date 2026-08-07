package de.taymaerz.skyfox.common.debug

import de.taymaerz.skyfox.common.debug.logging.Logging.Priority.VERBOSE
import de.taymaerz.skyfox.common.debug.logging.Logging.Priority.WARN
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag

object Bugs {
    var ready = false
    fun report(exception: Exception) {
        log(TAG, VERBOSE) { "Reporting $exception" }
        if (!ready) {
            log(TAG, WARN) { "Bug tracking not initialized yet." }
            return
        }
    }

    private val TAG = logTag("Debug", "Bugs")
}