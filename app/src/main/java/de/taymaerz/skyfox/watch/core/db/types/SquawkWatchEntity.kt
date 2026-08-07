package de.taymaerz.skyfox.watch.core.db.types

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import de.taymaerz.skyfox.main.core.aircraft.SquawkCode
import de.taymaerz.skyfox.watch.core.WatchId

@Entity(
    tableName = "watch_squawk",
    foreignKeys = [
        ForeignKey(
            entity = BaseWatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SquawkWatchEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: WatchId,
    @ColumnInfo(name = "code") val code: SquawkCode,
) : WatchType {
    companion object {
        const val TYPE_KEY = "squawk"
    }
}