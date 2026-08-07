package de.taymaerz.skyfox.gallery.ui

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.WebpageTool
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.debug.logging.Logging.Priority.ERROR
import de.taymaerz.skyfox.common.debug.logging.asLog
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.planespotters.api.PlanespottersApi
import de.taymaerz.skyfox.common.planespotters.api.PlanespottersEndpoint
import de.taymaerz.skyfox.common.uix.ViewModel4
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    @Suppress("unused") private val handle: SavedStateHandle,
    dispatcherProvider: DispatcherProvider,
    private val endpoint: PlanespottersEndpoint,
    private val webpageTool: WebpageTool,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Gallery", "VM"),
) {

    data class State(
        val isLoading: Boolean = true,
        val photos: List<PlanespottersApi.Photo> = emptyList(),
    )

    private val _state = MutableStateFlow(State())
    val state = _state

    fun load(hex: String, registration: String?) = launch {
        _state.value = State(isLoading = true)
        val photos = try {
            // registration usually yields more photos than hex
            val byReg = registration?.let { endpoint.getPhotosByRegistration(it) } ?: emptyList()
            if (byReg.isNotEmpty()) byReg else endpoint.getPhotosByHex(hex)
        } catch (e: Exception) {
            log(tag, ERROR) { "Failed to load photos: ${e.asLog()}" }
            emptyList()
        }
        _state.value = State(isLoading = false, photos = photos)
    }

    fun openLink(url: String) {
        webpageTool.open(url)
    }
}
