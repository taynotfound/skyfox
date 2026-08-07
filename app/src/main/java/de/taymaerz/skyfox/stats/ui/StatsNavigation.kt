package de.taymaerz.skyfox.stats.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import de.taymaerz.skyfox.common.navigation.NavigationEntry
import javax.inject.Inject

class StatsNavigation @Inject constructor() : NavigationEntry {
    override fun EntryProviderScope<NavKey>.setup() {
        entry<DestinationStats> {
            StatsScreen()
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class StatsNavigationModule {
    @Binds
    @IntoSet
    abstract fun navigation(nav: StatsNavigation): NavigationEntry
}
