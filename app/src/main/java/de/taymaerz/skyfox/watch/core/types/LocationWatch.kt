package de.taymaerz.skyfox.watch.core.types

import android.location.Location
import de.taymaerz.skyfox.main.core.aircraft.Aircraft
import de.taymaerz.skyfox.watch.core.WatchId
import de.taymaerz.skyfox.watch.core.db.types.BaseWatchEntity
import de.taymaerz.skyfox.watch.core.db.types.LocationWatchEntity
import de.taymaerz.skyfox.watch.core.history.WatchCheck
import java.time.Instant

data class LocationWatch(
    private val base: BaseWatchEntity,
    private val specific: LocationWatchEntity,
) : Watch {
    override val id: WatchId
        get() = base.id
    override val addedAt: Instant
        get() = base.createdAt
    override val note: String
        get() = base.userNote
    override val isNotificationEnabled: Boolean
        get() = base.notificationEnabled

    val label: String
        get() = specific.label

    val latitude: Double
        get() = base.latitude!!

    val longitude: Double
        get() = base.longitude!!

    val radiusInMeters: Float
        get() = base.radius!!

    val center: Location
        get() = Location("watch").apply {
            latitude = this@LocationWatch.latitude
            longitude = this@LocationWatch.longitude
        }

    override fun matches(ac: Aircraft): Boolean {
        val acLocation = ac.location ?: return false
        return center.distanceTo(acLocation) <= radiusInMeters
    }

    data class Status(
        override val watch: LocationWatch,
        override val lastCheck: WatchCheck?,
        override val lastHit: WatchCheck?,
        override val tracked: Set<Aircraft> = emptySet(),
    ) : Watch.Status {
        val label: String
            get() = watch.label
        val radiusInMeters: Float
            get() = watch.radiusInMeters
    }
}
