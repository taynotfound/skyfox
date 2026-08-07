package de.taymaerz.skyfox.common.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.BarChart
import androidx.compose.material.icons.twotone.Map
import androidx.compose.material.icons.twotone.TravelExplore
import androidx.compose.material.icons.twotone.Visibility
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.taymaerz.skyfox.R
import de.taymaerz.skyfox.common.navigation.LocalNavigationController
import de.taymaerz.skyfox.map.ui.DestinationMap
import de.taymaerz.skyfox.search.ui.DestinationSearch
import de.taymaerz.skyfox.stats.ui.DestinationStats
import de.taymaerz.skyfox.watch.ui.DestinationWatchList

val LocalIsInternetAvailable = staticCompositionLocalOf { true }

@Composable
fun BottomNavBar(
    selectedTab: Int,
    modifier: Modifier = Modifier,
) {
    val navController = LocalNavigationController.current ?: return
    val isInternetAvailable = LocalIsInternetAvailable.current

    Column(modifier = modifier) {
        AnimatedVisibility(visible = !isInternetAvailable) {
            Text(
                text = stringResource(R.string.common_internet_unavailable_message),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 32.dp, vertical = 4.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        NavigationBar {
            NavigationBarItem(
                selected = selectedTab == 0,
                onClick = { if (selectedTab != 0) navController.replace(DestinationMap()) },
                icon = { Icon(Icons.TwoTone.Map, contentDescription = null) },
                label = { Text(stringResource(R.string.map_page_label)) },
            )
            NavigationBarItem(
                selected = selectedTab == 1,
                onClick = { if (selectedTab != 1) navController.replace(DestinationSearch()) },
                icon = { Icon(Icons.TwoTone.TravelExplore, contentDescription = null) },
                label = { Text(stringResource(R.string.search_page_label)) },
            )
            NavigationBarItem(
                selected = selectedTab == 2,
                onClick = { if (selectedTab != 2) navController.replace(DestinationWatchList()) },
                icon = { Icon(Icons.TwoTone.Visibility, contentDescription = null) },
                label = { Text(stringResource(R.string.watch_list_page_label)) },
            )
            NavigationBarItem(
                selected = selectedTab == 3,
                onClick = { if (selectedTab != 3) navController.replace(DestinationStats) },
                icon = { Icon(Icons.TwoTone.BarChart, contentDescription = null) },
                label = { Text("Stats") },
            )
        }
    }
}
