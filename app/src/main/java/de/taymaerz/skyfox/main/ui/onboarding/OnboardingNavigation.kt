package de.taymaerz.skyfox.main.ui.onboarding

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import de.taymaerz.skyfox.common.navigation.NavigationEntry
import de.taymaerz.skyfox.main.ui.DestinationPrivacy
import de.taymaerz.skyfox.main.ui.DestinationWelcome
import de.taymaerz.skyfox.main.ui.onboarding.privacy.PrivacyScreenHost
import de.taymaerz.skyfox.main.ui.onboarding.welcome.WelcomeScreenHost
import javax.inject.Inject

class OnboardingNavigation @Inject constructor() : NavigationEntry {
    override fun EntryProviderScope<NavKey>.setup() {
        entry<DestinationWelcome> {
            WelcomeScreenHost()
        }
        entry<DestinationPrivacy> {
            PrivacyScreenHost()
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingNavigationModule {
    @Binds
    @IntoSet
    abstract fun navigation(nav: OnboardingNavigation): NavigationEntry
}
