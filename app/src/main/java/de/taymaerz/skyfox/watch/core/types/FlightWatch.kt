package de.taymaerz.skyfox.watch.core.types

import de.taymaerz.skyfox.main.core.aircraft.Aircraft
import de.taymaerz.skyfox.main.core.aircraft.Callsign
import de.taymaerz.skyfox.watch.core.WatchId
import de.taymaerz.skyfox.watch.core.db.types.BaseWatchEntity
import de.taymaerz.skyfox.watch.core.db.types.FlightWatchEntity
import de.taymaerz.skyfox.watch.core.history.WatchCheck
import java.time.Instant


data class FlightWatch(
    private val base: BaseWatchEntity,
    private val specific: FlightWatchEntity,
) : Watch {
    override val id: WatchId
        get() = base.id
    override val addedAt: Instant
        get() = base.createdAt
    override val note: String
        get() = base.userNote
    override val isNotificationEnabled: Boolean
        get() = base.notificationEnabled

    val callsign: Callsign
        get() = specific.callsign

    override fun matches(ac: Aircraft): Boolean {
        return ac.callsign?.uppercase() == callsign.uppercase()
    }

    data class Status(
        override val watch: FlightWatch,
        override val lastCheck: WatchCheck?,
        override val lastHit: WatchCheck?,
        override val tracked: Set<Aircraft> = emptySet(),
    ) : Watch.Status {

        val callsign: Callsign
            get() = watch.callsign
    }
}