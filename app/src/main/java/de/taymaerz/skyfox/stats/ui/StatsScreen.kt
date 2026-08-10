package de.taymaerz.skyfox.stats.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.BarChart
import androidx.compose.material.icons.twotone.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.taymaerz.skyfox.common.chart.MetricLineChart
import de.taymaerz.skyfox.common.compose.BottomNavBar
import de.taymaerz.skyfox.common.navigation.NavigationEventHandler
import de.taymaerz.skyfox.common.error.ErrorEventHandler
import de.taymaerz.skyfox.common.planespotters.PlanespottersThumbnail
import de.taymaerz.skyfox.common.planespotters.coil.AircraftThumbnailQuery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    vm: StatsViewModel = hiltViewModel(),
) {
    NavigationEventHandler(vm)
    ErrorEventHandler(vm)
    val state by vm.state.collectAsState(initial = StatsViewModel.StatsState())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stats") },
                actions = {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    androidx.compose.material3.IconButton(onClick = {
                        val text = buildString {
                            appendLine("✈️ My plane spotting stats")
                            appendLine("Today: ${state.hitsToday} · Week: ${state.hitsThisWeek} · All time: ${state.totalHits}")
                            appendLine("Unique aircraft: ${state.uniqueAircraft} · Streak: ${state.currentStreak}d (best ${state.bestStreak}d)")
                            state.topAircraft.firstOrNull()?.let {
                                appendLine("Most seen: ${it.aircraft?.registration ?: it.hex.uppercase()} (${it.sightings}×)")
                            }
                        }
                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, text)
                        }
                        context.startActivity(android.content.Intent.createChooser(intent, null))
                    }) {
                        Icon(Icons.TwoTone.Share, contentDescription = "Share stats")
                    }
                },
            )
        },
        bottomBar = { BottomNavBar(destination = DestinationStats) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard("Today", state.hitsToday.toString(), Modifier.weight(1f))
                    StatCard("This Week", state.hitsThisWeek.toString(), Modifier.weight(1f))
                    StatCard("All Time", state.totalHits.toString(), Modifier.weight(1f))
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard("Unique Aircraft", state.uniqueAircraft.toString(), Modifier.weight(1f))
                    StatCard(
                        "Streak",
                        "${state.currentStreak}d",
                        Modifier.weight(1f),
                        sub = "best ${state.bestStreak}d",
                    )
                }
            }
            if (state.busiestHour != null || state.busiestDayName != null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        state.busiestHour?.let {
                            StatCard("Busiest Hour", "%02d:00".format(it), Modifier.weight(1f))
                        }
                        state.busiestDayName?.let {
                            StatCard("Busiest Day", it, Modifier.weight(1f))
                        }
                    }
                }
            }

            if (state.activity.any { it.value > 0 }) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    ) {
                        MetricLineChart(
                            title = "Sightings — last 14 days",
                            data = state.activity,
                            lineColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                        )
                    }
                }
            }

            if (state.topAircraft.isNotEmpty()) {
                item {
                    Text(
                        "Most Seen Aircraft",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                items(state.topAircraft, key = { it.hex }) { top ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { vm.showAircraft(top.hex) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PlanespottersThumbnail(
                                query = AircraftThumbnailQuery(
                                    hex = top.hex,
                                    registration = top.aircraft?.registration,
                                ),
                                modifier = Modifier.width(96.dp),
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    top.aircraft?.registration ?: top.hex.uppercase(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                )
                                val desc = top.aircraft?.description
                                    ?: top.aircraft?.operator
                                    ?: "Hex ${top.hex.uppercase()}"
                                Text(
                                    desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Text(
                                "${top.sightings}×",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.TwoTone.BarChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "No sightings yet — add a watch to start tracking",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            if (state.recentSightings.isNotEmpty()) {
                item {
                    Text(
                        "Recent Sightings",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                items(state.recentSightings, key = { "${it.hex}-${it.at}" }) { s ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { vm.showAircraft(s.hex) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                s.aircraft?.registration ?: s.hex.uppercase(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                            )
                            (s.aircraft?.description ?: s.aircraft?.operator)?.let {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        Text(
                            android.text.format.DateUtils.getRelativeTimeSpanString(
                                s.at.toEpochMilli(),
                            ).toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier, sub: String? = null) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            if (sub != null) {
                Text(
                    sub,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                )
            }
        }
    }
}
