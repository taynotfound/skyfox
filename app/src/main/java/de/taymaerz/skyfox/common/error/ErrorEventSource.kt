package de.taymaerz.skyfox.common.error

import de.taymaerz.skyfox.common.flow.SingleEventFlow

interface ErrorEventSource {
    val errorEvents: SingleEventFlow<Throwable>
}
