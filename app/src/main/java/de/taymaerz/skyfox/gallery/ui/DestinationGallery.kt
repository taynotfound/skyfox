package de.taymaerz.skyfox.gallery.ui

import de.taymaerz.skyfox.common.navigation.NavigationDestination
import kotlinx.serialization.Serializable

@Serializable
data class DestinationGallery(
    val hex: String,
    val registration: String? = null,
) : NavigationDestination
