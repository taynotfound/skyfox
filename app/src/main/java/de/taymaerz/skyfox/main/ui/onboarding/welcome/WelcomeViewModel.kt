package de.taymaerz.skyfox.main.ui.onboarding.welcome

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.uix.ViewModel4
import de.taymaerz.skyfox.main.ui.DestinationPrivacy
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    @Suppress("UNUSED_PARAMETER") handle: SavedStateHandle,
    dispatcherProvider: DispatcherProvider,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Welcome", "ViewModel"),
) {

    fun finishWelcome() {
        navTo(DestinationPrivacy)
    }
}
