package de.taymaerz.skyfox.search.ui

sealed interface SearchEvents {
    data object RequestLocationPermission : SearchEvents
    data class SearchError(val error: Throwable) : SearchEvents
}