package de.taymaerz.skyfox.watch.core.db.history

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.taymaerz.skyfox.watch.core.WatchId
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface WatchCheckDao {
    @Query("SELECT * FROM watch_checks WHERE watch_id = :watchId ORDER BY checked_at DESC LIMIT 1")
    suspend fun getLastCheck(watchId: String): WatchCheckEntity?

    @Query("SELECT * FROM watch_checks WHERE watch_id = :watchId AND aircraft_count > 0 ORDER BY checked_at DESC LIMIT 1")
    suspend fun getLastHit(watchId: String): WatchCheckEntity?

    @Query("SELECT * FROM watch_checks ORDER BY checked_at DESC")
    fun observeAll(): Flow<List<WatchCheckEntity>>

    @Query("SELECT * FROM watch_checks")
    suspend fun getAll(): List<WatchCheckEntity>

    @Query("SELECT * FROM watch_checks ORDER BY checked_at DESC LIMIT 1")
    fun firehose(): Flow<WatchCheckEntity?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(watch: WatchCheckEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(checks: List<WatchCheckEntity>)

    @Query("DELETE FROM watch_checks WHERE watch_id = :watchId")
    suspend fun deleteForWatch(watchId: WatchId): Int

    @Query("DELETE FROM watch_checks WHERE watch_id IN (:watchIds)")
    suspend fun deleteForWatches(watchIds: Set<WatchId>): Int

    @Query("SELECT COUNT(*) FROM watch_checks")
    suspend fun count(): Int

    data class WatchCheckChartRow(
        @ColumnInfo(name = "watch_id") val watchId: String,
        @ColumnInfo(name = "checked_at") val checkedAt: Instant,
        @ColumnInfo(name = "aircraft_count") val aircraftCount: Int,
    )

    @Query("SELECT watch_id, checked_at, aircraft_count FROM watch_checks WHERE watch_id = :watchId AND checked_at >= :since ORDER BY checked_at")
    suspend fun getChartDataSince(watchId: String, since: Instant): List<WatchCheckChartRow>

    @Query("SELECT watch_id, checked_at, aircraft_count FROM watch_checks WHERE watch_id IN (:watchIds) AND checked_at >= :since ORDER BY checked_at")
    suspend fun getChartDataSince(watchIds: Set<String>, since: Instant): List<WatchCheckChartRow>

    @Query("DELETE FROM watch_checks WHERE checked_at < :before")
    suspend fun deleteBefore(before: Instant): Int

    data class TopHexRow(
        @ColumnInfo(name = "hex") val hex: String,
        @ColumnInfo(name = "sightings") val sightings: Int,
    )

    // ponytail: SQLite json_each trick to split CSV hex list — works for small sets, rebuild as proper FK table if volumes grow
    @Query("""
        SELECT hex, COUNT(*) as sightings
        FROM (
            SELECT TRIM(value) as hex
            FROM watch_checks, json_each('["' || REPLACE(REPLACE(COALESCE(seen_hexes,''), ',', '","'), ' ', '') || '"]')
            WHERE seen_hexes IS NOT NULL AND seen_hexes != ''
        )
        WHERE hex != ''
        GROUP BY hex ORDER BY sightings DESC LIMIT :limit
    """)
    suspend fun getTopHexes(limit: Int = 10): List<TopHexRow>

    @Query("SELECT COUNT(*) FROM watch_checks WHERE aircraft_count > 0")
    suspend fun totalHits(): Int

    @Query("SELECT COALESCE(SUM(CASE WHEN aircraft_count > 0 THEN 1 ELSE 0 END), 0) FROM watch_checks WHERE checked_at >= :since")
    suspend fun hitsInPeriod(since: Instant): Int
}
