package de.taymaerz.skyfox.common.navigation

import de.taymaerz.skyfox.common.flow.SingleEventFlow

interface NavigationEventSource {
    val navEvents: SingleEventFlow<NavEvent>
}
