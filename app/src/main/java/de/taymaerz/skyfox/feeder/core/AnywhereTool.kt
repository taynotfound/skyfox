package de.taymaerz.skyfox.feeder.core

import dagger.Reusable
import de.taymaerz.skyfox.feeder.core.config.FeederPosition
import javax.inject.Inject

@Reusable
class AnywhereTool @Inject constructor() {

    suspend fun createLink(ids: Set<AnywhereId>, center: FeederPosition?): String {
        val feedLink = "$LINK_PREFIX${ids.joinToString(",")}"

        return if (center != null) {
            "$feedLink&siteLat=${center.latitude}&siteLon=${center.longitude}&centerReceiver"
        } else {
            feedLink
        }
    }

    companion object {
        private const val LINK_PREFIX = "https://globe.airplanes.live/?feed="
    }
}