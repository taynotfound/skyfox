package de.taymaerz.skyfox.common.uix

import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.debug.logging.asLog
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.error.ErrorEventSource
import de.taymaerz.skyfox.common.flow.SingleEventFlow
import de.taymaerz.skyfox.common.navigation.NavEvent
import de.taymaerz.skyfox.common.navigation.NavigationDestination
import de.taymaerz.skyfox.common.navigation.NavigationEventSource
import kotlinx.coroutines.CoroutineExceptionHandler

abstract class ViewModel4(
    dispatcherProvider: DispatcherProvider,
    override val tag: String = defaultTag(),
) : ViewModel2(dispatcherProvider, tag), ErrorEventSource, NavigationEventSource {

    override val errorEvents = SingleEventFlow<Throwable>()
    override val navEvents = SingleEventFlow<NavEvent>()

    override var launchErrorHandler: CoroutineExceptionHandler? = CoroutineExceptionHandler { _, ex ->
        log(tag) { "Error during launch: ${ex.asLog()}" }
        errorEvents.emitBlocking(ex)
    }

    fun navTo(
        destination: NavigationDestination,
        popUpTo: NavigationDestination? = null,
        inclusive: Boolean = false,
    ) {
        log(tag) { "navTo($destination)" }
        navEvents.tryEmit(NavEvent.GoTo(destination, popUpTo, inclusive))
    }

    fun navUp() {
        log(tag) { "navUp()" }
        navEvents.tryEmit(NavEvent.Up)
    }

    companion object {
        private fun defaultTag(): String = this::class.simpleName ?: "VM4"
    }
}
