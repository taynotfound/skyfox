package de.taymaerz.skyfox.main.ui.onboarding.privacy

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.BuildConfigWrap
import de.taymaerz.skyfox.common.PrivacyPolicy
import de.taymaerz.skyfox.common.WebpageTool
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.datastore.value
import de.taymaerz.skyfox.common.datastore.valueBlocking
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.uix.ViewModel4
import de.taymaerz.skyfox.main.core.GeneralSettings
import de.taymaerz.skyfox.main.ui.DestinationMain
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class PrivacyViewModel @Inject constructor(
    @Suppress("unused") private val handle: SavedStateHandle,
    dispatcherProvider: DispatcherProvider,
    private val generalSettings: GeneralSettings,
    private val webpageTool: WebpageTool,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Privacy", "ViewModel"),
) {

    val state = combine(
        generalSettings.isUpdateCheckEnabled.flow,
        flowOf(Unit)
    ) { isUpdateCheckEnabled, _ ->
        State(
            isUpdateCheckEnabled = isUpdateCheckEnabled,
            isUpdateCheckSupported = BuildConfigWrap.FLAVOR == BuildConfigWrap.Flavor.FOSS,
        )
    }
        .asStateFlow()

    fun goPrivacyPolicy() {
        log(tag) { "goPrivacyPolicy()" }
        webpageTool.open(PrivacyPolicy.URL)
    }

    fun toggleUpdateCheck() {
        log(tag) { "toggleUpdateCheck()" }
        generalSettings.isUpdateCheckEnabled.valueBlocking = !generalSettings.isUpdateCheckEnabled.valueBlocking
    }

    fun finishPrivacy() = launch {
        generalSettings.isOnboardingFinished.value(true)
        navTo(DestinationMain, popUpTo = DestinationMain, inclusive = true)
    }

    data class State(
        val isUpdateCheckEnabled: Boolean,
        val isUpdateCheckSupported: Boolean,
    )
}
