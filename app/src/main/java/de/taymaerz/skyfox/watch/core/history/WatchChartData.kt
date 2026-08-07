package de.taymaerz.skyfox.watch.core.history

import de.taymaerz.skyfox.common.chart.ChartPoint
import java.time.Instant

data class WatchCountChartData(val counts: List<ChartPoint>)

data class WatchActivityData(val checks: List<WatchActivityCheck>)

data class WatchActivityCheck(val timestamp: Instant, val aircraftCount: Int)
