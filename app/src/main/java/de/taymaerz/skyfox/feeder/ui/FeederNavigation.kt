package de.taymaerz.skyfox.feeder.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import de.taymaerz.skyfox.common.navigation.BottomSheetSceneStrategy
import de.taymaerz.skyfox.common.navigation.NavigationEntry
import de.taymaerz.skyfox.feeder.ui.actions.FeederActionSheetHost
import de.taymaerz.skyfox.feeder.ui.add.AddFeederScreenHost
import javax.inject.Inject

class FeederNavigation @Inject constructor() : NavigationEntry {
    override fun EntryProviderScope<NavKey>.setup() {
        entry<DestinationFeederList> {
            FeederListScreenHost()
        }
        entry<DestinationFeederAction>(
            metadata = BottomSheetSceneStrategy.bottomSheet(),
        ) { dest ->
            FeederActionSheetHost(receiverId = dest.receiverId)
        }
        entry<DestinationAddFeeder> { dest ->
            AddFeederScreenHost(qrData = dest.qrData)
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class FeederNavigationModule {
    @Binds
    @IntoSet
    abstract fun navigation(nav: FeederNavigation): NavigationEntry
}
