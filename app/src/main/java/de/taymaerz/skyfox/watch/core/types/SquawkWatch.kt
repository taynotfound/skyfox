package de.taymaerz.skyfox.watch.core.types

import de.taymaerz.skyfox.main.core.aircraft.Aircraft
import de.taymaerz.skyfox.main.core.aircraft.SquawkCode
import de.taymaerz.skyfox.watch.core.WatchId
import de.taymaerz.skyfox.watch.core.db.types.BaseWatchEntity
import de.taymaerz.skyfox.watch.core.db.types.SquawkWatchEntity
import de.taymaerz.skyfox.watch.core.history.WatchCheck
import java.time.Instant

data class SquawkWatch(
    private val base: BaseWatchEntity,
    private val specific: SquawkWatchEntity,
) : Watch {
    override val id: WatchId
        get() = base.id
    override val addedAt: Instant
        get() = base.createdAt
    override val note: String
        get() = base.userNote
    override val isNotificationEnabled: Boolean
        get() = base.notificationEnabled

    val code: SquawkCode
        get() = specific.code

    override fun matches(ac: Aircraft): Boolean {
        return ac.squawk?.uppercase() == code.uppercase()
    }

    data class Status(
        override val watch: SquawkWatch,
        override val lastCheck: WatchCheck?,
        override val lastHit: WatchCheck?,
        override val tracked: Set<Aircraft> = emptySet(),
    ) : Watch.Status {
        val squawk: SquawkCode
            get() = watch.code
    }
}