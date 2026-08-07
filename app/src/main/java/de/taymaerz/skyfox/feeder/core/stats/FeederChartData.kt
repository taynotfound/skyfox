package de.taymaerz.skyfox.feeder.core.stats

import de.taymaerz.skyfox.common.chart.ChartPoint

data class BeastChartData(val messageRate: List<ChartPoint>)

data class MlatChartData(
    val messageRate: List<ChartPoint>,
    val outlierPercent: List<ChartPoint>,
)
