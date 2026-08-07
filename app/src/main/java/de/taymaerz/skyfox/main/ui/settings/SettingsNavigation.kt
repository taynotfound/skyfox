package de.taymaerz.skyfox.main.ui.settings

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import de.taymaerz.skyfox.backup.ui.BackupRestoreScreenHost
import de.taymaerz.skyfox.common.navigation.NavigationEntry
import de.taymaerz.skyfox.feeder.ui.settings.FeederSettingsScreenHost
import de.taymaerz.skyfox.main.ui.settings.acks.AcknowledgementsScreenHost
import de.taymaerz.skyfox.main.ui.settings.general.GeneralSettingsScreenHost
import de.taymaerz.skyfox.main.ui.settings.support.SupportScreenHost
import de.taymaerz.skyfox.map.ui.settings.MapSettingsScreenHost
import de.taymaerz.skyfox.watch.ui.settings.WatchSettingsScreenHost
import javax.inject.Inject

class SettingsNavigation @Inject constructor() : NavigationEntry {
    override fun EntryProviderScope<NavKey>.setup() {
        entry<DestinationSettingsIndex> {
            SettingsIndexScreenHost()
        }
        entry<DestinationGeneralSettings> {
            GeneralSettingsScreenHost()
        }
        entry<DestinationMapSettings> {
            MapSettingsScreenHost()
        }
        entry<DestinationFeederSettings> {
            FeederSettingsScreenHost()
        }
        entry<DestinationWatchSettings> {
            WatchSettingsScreenHost()
        }
        entry<DestinationAcknowledgements> {
            AcknowledgementsScreenHost()
        }
        entry<DestinationSupport> {
            SupportScreenHost()
        }
        entry<DestinationBackupRestore> {
            BackupRestoreScreenHost()
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsNavigationModule {
    @Binds
    @IntoSet
    abstract fun navigation(nav: SettingsNavigation): NavigationEntry
}
