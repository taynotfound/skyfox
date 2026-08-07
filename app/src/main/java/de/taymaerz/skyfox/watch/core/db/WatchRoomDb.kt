package de.taymaerz.skyfox.watch.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.taymaerz.skyfox.common.room.InstantConverter
import de.taymaerz.skyfox.watch.core.db.history.WatchCheckDao
import de.taymaerz.skyfox.watch.core.db.history.WatchCheckEntity
import de.taymaerz.skyfox.watch.core.db.types.AircraftWatchEntity
import de.taymaerz.skyfox.watch.core.db.types.BaseWatchEntity
import de.taymaerz.skyfox.watch.core.db.types.FlightWatchEntity
import de.taymaerz.skyfox.watch.core.db.types.LocationWatchEntity
import de.taymaerz.skyfox.watch.core.db.types.SquawkWatchEntity
import de.taymaerz.skyfox.watch.core.db.types.WatchDao

@Database(
    entities = [
        BaseWatchEntity::class,
        FlightWatchEntity::class,
        SquawkWatchEntity::class,
        AircraftWatchEntity::class,
        LocationWatchEntity::class,
        WatchCheckEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
@TypeConverters(InstantConverter::class)
abstract class WatchRoomDb : RoomDatabase() {
    abstract fun watches(): WatchDao
    abstract fun checks(): WatchCheckDao
}