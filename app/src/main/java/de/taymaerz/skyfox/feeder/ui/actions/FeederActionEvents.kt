package de.taymaerz.skyfox.feeder.ui.actions

import de.taymaerz.skyfox.feeder.core.Feeder
import de.taymaerz.skyfox.feeder.core.ReceiverId
import de.taymaerz.skyfox.feeder.ui.add.NewFeederQR

sealed class FeederActionEvents {
    data class Rename(val feeder: Feeder) : FeederActionEvents()
    data class ChangeIpAddress(val feeder: Feeder) : FeederActionEvents()
    data class RemovalConfirmation(val id: ReceiverId) : FeederActionEvents()
    data class ShowQrCode(val qr: NewFeederQR) : FeederActionEvents()
}
