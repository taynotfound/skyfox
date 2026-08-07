package de.taymaerz.skyfox.watch.core.types

import de.taymaerz.skyfox.main.core.aircraft.Aircraft
import de.taymaerz.skyfox.watch.core.WatchId
import de.taymaerz.skyfox.watch.core.history.WatchCheck
import java.time.Instant

sealed interface Watch {

    val id: WatchId
    val addedAt: Instant
    val note: String
    val isNotificationEnabled: Boolean

    fun matches(ac: Aircraft): Boolean

    sealed interface Status {
        val watch: Watch
        val id: WatchId
            get() = watch.id
        val note: String
            get() = watch.note

        val lastCheck: WatchCheck?
        val lastHit: WatchCheck?

        val tracked: Set<Aircraft>
    }
}