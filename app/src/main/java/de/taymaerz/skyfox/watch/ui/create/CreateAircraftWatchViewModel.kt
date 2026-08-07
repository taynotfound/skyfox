package de.taymaerz.skyfox.watch.ui.create

import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.uix.ViewModel4
import de.taymaerz.skyfox.main.core.aircraft.AircraftHex
import de.taymaerz.skyfox.watch.core.WatchRepo
import javax.inject.Inject

@HiltViewModel
class CreateAircraftWatchViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val watchRepo: WatchRepo,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Aircraft", "Create", "VM"),
) {

    fun create(hex: AircraftHex, note: String) = launch {
        log(tag) { "create($hex, $note)" }
        watchRepo.createAircraft(hex.uppercase(), note.trim())
        navUp()
    }
}
