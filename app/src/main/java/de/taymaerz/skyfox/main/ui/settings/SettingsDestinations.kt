package de.taymaerz.skyfox.main.ui.settings

import de.taymaerz.skyfox.common.navigation.NavigationDestination
import kotlinx.serialization.Serializable

@Serializable
data object DestinationSettingsIndex : NavigationDestination

@Serializable
data object DestinationGeneralSettings : NavigationDestination

@Serializable
data object DestinationMapSettings : NavigationDestination

@Serializable
data object DestinationFeederSettings : NavigationDestination

@Serializable
data object DestinationWatchSettings : NavigationDestination

@Serializable
data object DestinationAcknowledgements : NavigationDestination

@Serializable
data object DestinationSupport : NavigationDestination

@Serializable
data object DestinationBackupRestore : NavigationDestination
