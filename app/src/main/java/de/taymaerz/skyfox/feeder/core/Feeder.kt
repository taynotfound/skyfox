package de.taymaerz.skyfox.feeder.core

import de.taymaerz.skyfox.feeder.core.config.FeederConfig
import de.taymaerz.skyfox.feeder.core.stats.BeastStatsEntity
import de.taymaerz.skyfox.feeder.core.stats.MlatStatsEntity
import java.time.Instant

data class Feeder(
    val config: FeederConfig,
    val beastStats: BeastStatsEntity? = null,
    val mlatStats: MlatStatsEntity? = null,
) {

    val label: String
        get() = config.label ?: config.receiverId.takeLast(5)

    val lastSeen: Instant?
        get() = listOfNotNull(beastStats?.receivedAt).maxOrNull()

    val id: ReceiverId
        get() = config.receiverId

    val beastMessageRate: Double
        get() = beastStats?.messageRate ?: 0.0

}
