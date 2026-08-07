package de.taymaerz.skyfox.main.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import de.taymaerz.skyfox.common.error.ErrorEventHandler
import de.taymaerz.skyfox.common.navigation.LocalNavigationController
import de.taymaerz.skyfox.common.navigation.NavigationEventHandler
import de.taymaerz.skyfox.main.ui.DestinationWelcome
import de.taymaerz.skyfox.map.ui.DestinationMap

@Composable
fun MainScreenHost(
    vm: MainViewModel = hiltViewModel(),
) {
    val navController = LocalNavigationController.current ?: return

    NavigationEventHandler(vm)
    ErrorEventHandler(vm)

    val isOnboardingFinished by vm.isOnboardingFinished.collectAsState(initial = true)

    if (!isOnboardingFinished) {
        navController.goTo(DestinationWelcome)
        return
    }

    LaunchedEffect(Unit) {
        navController.replace(DestinationMap())
    }
}
