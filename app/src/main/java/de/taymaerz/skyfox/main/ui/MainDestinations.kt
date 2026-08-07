package de.taymaerz.skyfox.main.ui

import de.taymaerz.skyfox.common.navigation.NavigationDestination
import kotlinx.serialization.Serializable

@Serializable
data object DestinationMain : NavigationDestination

@Serializable
data object DestinationWelcome : NavigationDestination

@Serializable
data object DestinationPrivacy : NavigationDestination
