package de.taymaerz.skyfox.main.ui.main

import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.uix.ViewModel4
import de.taymaerz.skyfox.main.core.GeneralSettings
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    generalSettings: GeneralSettings,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Main", "ViewModel")
) {

    val isOnboardingFinished = generalSettings.isOnboardingFinished.flow
        .asStateFlow()
}
