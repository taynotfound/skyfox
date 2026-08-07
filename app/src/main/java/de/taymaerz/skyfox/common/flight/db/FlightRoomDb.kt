package de.taymaerz.skyfox.common.flight.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.taymaerz.skyfox.common.room.InstantConverter

@Database(
    entities = [
        AirportEntity::class,
        FlightRouteEntity::class,
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
    exportSchema = true,
)
@TypeConverters(InstantConverter::class)
abstract class FlightRoomDb : RoomDatabase() {
    abstract fun airports(): AirportDao
    abstract fun routes(): FlightRouteDao
}
