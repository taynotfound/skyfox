package de.taymaerz.skyfox.watch.ui.details

sealed class WatchDetailsEvents {
    data class RemovalConfirmation(val id: String) : WatchDetailsEvents()
}