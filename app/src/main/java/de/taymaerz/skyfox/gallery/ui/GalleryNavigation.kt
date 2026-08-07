package de.taymaerz.skyfox.gallery.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import de.taymaerz.skyfox.common.navigation.NavigationEntry
import javax.inject.Inject

class GalleryNavigation @Inject constructor() : NavigationEntry {
    override fun EntryProviderScope<NavKey>.setup() {
        entry<DestinationGallery> { dest ->
            GalleryScreenHost(hex = dest.hex, registration = dest.registration)
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class GalleryNavigationModule {
    @Binds
    @IntoSet
    abstract fun navigation(nav: GalleryNavigation): NavigationEntry
}
