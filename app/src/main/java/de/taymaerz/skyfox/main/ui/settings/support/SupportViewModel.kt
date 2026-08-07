package de.taymaerz.skyfox.main.ui.settings.support

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.PrivacyPolicy
import de.taymaerz.skyfox.common.WebpageTool
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.debug.recorder.core.RecorderModule
import de.taymaerz.skyfox.common.uix.ViewModel4
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor(
    @Suppress("unused") private val handle: SavedStateHandle,
    dispatcherProvider: DispatcherProvider,
    private val recorderModule: RecorderModule,
    private val webpageTool: WebpageTool,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Settings", "Support", "VM"),
) {

    val isRecording = recorderModule.state.map { it.isRecording }.asStateFlow()

    fun openDocumentation() {
        webpageTool.open("https://github.com/taynotfound/skyfox")
    }

    fun openIssueTracker() {
        webpageTool.open("https://github.com/taynotfound/skyfox/issues")
    }

    fun openAirplanesLiveDiscord() {
        webpageTool.open("https://discord.gg/adsb")
    }

    fun openDarkensDiscord() {
        webpageTool.open("https://discord.gg/ENtVkMHqZg")
    }

    fun openPrivacyPolicy() {
        webpageTool.open(PrivacyPolicy.URL)
    }

    fun startDebugLog() = launch {
        log(tag) { "startDebugLog()" }
        recorderModule.startRecorder()
    }

    fun stopDebugLog() = launch {
        log(tag) { "stopDebugLog()" }
        recorderModule.stopRecorder()
    }
}
