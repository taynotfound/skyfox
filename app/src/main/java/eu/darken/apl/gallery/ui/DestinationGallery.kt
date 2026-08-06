package eu.darken.apl.gallery.ui

import eu.darken.apl.common.navigation.NavigationDestination
import kotlinx.serialization.Serializable

@Serializable
data class DestinationGallery(
    val hex: String,
    val registration: String? = null,
) : NavigationDestination
