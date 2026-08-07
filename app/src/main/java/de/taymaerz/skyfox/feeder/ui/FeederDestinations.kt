package de.taymaerz.skyfox.feeder.ui

import de.taymaerz.skyfox.common.navigation.NavigationDestination
import kotlinx.serialization.Serializable

@Serializable
data object DestinationFeederList : NavigationDestination

@Serializable
data class DestinationFeederAction(
    val receiverId: String,
) : NavigationDestination

@Serializable
data class DestinationAddFeeder(
    val qrData: String? = null,
) : NavigationDestination
