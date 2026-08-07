package de.taymaerz.skyfox.watch.core.db.types

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import de.taymaerz.skyfox.main.core.aircraft.Callsign
import de.taymaerz.skyfox.watch.core.WatchId

@Entity(
    tableName = "watch_flight",
    foreignKeys = [
        ForeignKey(
            entity = BaseWatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FlightWatchEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: WatchId,
    @ColumnInfo(name = "callsign") val callsign: Callsign,
) : WatchType {
    companion object {
        const val TYPE_KEY = "flight"
    }
}