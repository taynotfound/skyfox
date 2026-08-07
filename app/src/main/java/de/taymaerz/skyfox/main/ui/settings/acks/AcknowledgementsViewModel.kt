package de.taymaerz.skyfox.main.ui.settings.acks

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.WebpageTool
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.uix.ViewModel4
import javax.inject.Inject

@HiltViewModel
class AcknowledgementsViewModel @Inject constructor(
    @Suppress("unused") private val handle: SavedStateHandle,
    dispatcherProvider: DispatcherProvider,
    private val webpageTool: WebpageTool,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Settings", "Acknowledgements", "VM"),
) {

    fun openUrl(url: String) {
        webpageTool.open(url)
    }
}
