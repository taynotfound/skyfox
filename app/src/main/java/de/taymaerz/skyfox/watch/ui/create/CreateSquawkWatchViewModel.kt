package de.taymaerz.skyfox.watch.ui.create

import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.uix.ViewModel4
import de.taymaerz.skyfox.main.core.aircraft.SquawkCode
import de.taymaerz.skyfox.watch.core.WatchRepo
import javax.inject.Inject

@HiltViewModel
class CreateSquawkWatchViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val watchRepo: WatchRepo,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Squawk", "Create", "VM"),
) {

    fun create(code: SquawkCode, note: String) = launch {
        log(tag) { "create($code, $note)" }
        watchRepo.createSquawk(code, note.trim())
        navUp()
    }
}
