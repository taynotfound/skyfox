package de.taymaerz.skyfox.common.chart

sealed interface ChartState<out T> {
    data object Loading : ChartState<Nothing>
    data class Ready<T>(val data: T) : ChartState<T>
    data object NoData : ChartState<Nothing>
}
