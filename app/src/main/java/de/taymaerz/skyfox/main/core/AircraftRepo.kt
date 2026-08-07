package de.taymaerz.skyfox.main.core

import de.taymaerz.skyfox.common.coroutine.AppScope
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.flow.replayingShare
import de.taymaerz.skyfox.main.core.aircraft.Aircraft
import de.taymaerz.skyfox.main.core.aircraft.AircraftHex
import de.taymaerz.skyfox.main.core.db.AircraftDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AircraftRepo @Inject constructor(
    @param:AppScope private val appScope: CoroutineScope,
    private val aircraftDatabase: AircraftDatabase,
) {

    val aircraft: Flow<Map<AircraftHex, Aircraft>> = aircraftDatabase.current()
        .map { acs -> acs.associateBy { it.hex } }
        .replayingShare(appScope)

    suspend fun update(toUpdate: Collection<Aircraft>) {
        log(TAG) { "update(aircraft=${toUpdate.size})" }
        val before = aircraft.first().size
        aircraftDatabase.update(toUpdate)
        val after = aircraft.first().size
        log(TAG) { "Aircraft cache updated (before=${before}, after=${after})" }
    }

    companion object {
        private val TAG = logTag("Aircraft", "Repo")
    }
}