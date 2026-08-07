package de.taymaerz.skyfox.watch.ui.create

import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.uix.ViewModel4
import de.taymaerz.skyfox.main.core.aircraft.Callsign
import de.taymaerz.skyfox.watch.core.WatchRepo
import javax.inject.Inject

@HiltViewModel
class CreateFlightWatchViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val watchRepo: WatchRepo,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Flight", "Create", "VM"),
) {

    fun create(callsign: Callsign, note: String) = launch {
        log(tag) { "create($callsign, $note)" }
        watchRepo.createFlight(callsign, note.trim())
        navUp()
    }
}
