package de.taymaerz.skyfox.map.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.TouchApp
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Layers
import androidx.compose.material.icons.twotone.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.taymaerz.skyfox.R
import de.taymaerz.skyfox.common.compose.aplContentWindowInsets
import de.taymaerz.skyfox.common.error.ErrorEventHandler
import de.taymaerz.skyfox.common.navigation.NavigationEventHandler
import de.taymaerz.skyfox.common.settings.SettingsPreferenceItem
import de.taymaerz.skyfox.common.settings.SettingsSwitchItem
import de.taymaerz.skyfox.map.core.MapLayer
import de.taymaerz.skyfox.map.core.MapOverlay

@Composable
fun MapSettingsScreenHost(
    vm: MapSettingsViewModel = hiltViewModel(),
) {
    NavigationEventHandler(vm)
    ErrorEventHandler(vm)

    val state by vm.state.collectAsState(initial = null)
    state?.let {
        MapSettingsScreen(
            state = it,
            onBack = { vm.navUp() },
            onToggleRestoreLastView = { vm.toggleRestoreLastView() },
            onToggleNativeInfoPanel = { vm.toggleNativeInfoPanel() },
            onToggleHoverInfo = { vm.toggleHoverInfo() },
            onSetMapLayer = { vm.setMapLayer(it) },
            onToggleOverlay = { vm.toggleOverlay(it) },
            onSetHomeLocation = { lat, lon -> vm.setHomeLocation(lat, lon) },
            onClearHomeLocation = { vm.clearHomeLocation() },
        )
    }
}

@Composable
fun MapSettingsScreen(
    state: MapSettingsViewModel.State,
    onBack: () -> Unit,
    onToggleRestoreLastView: () -> Unit,
    onToggleNativeInfoPanel: () -> Unit,
    onToggleHoverInfo: () -> Unit,
    onSetMapLayer: (MapLayer) -> Unit,
    onToggleOverlay: (MapOverlay) -> Unit,
    onSetHomeLocation: (Double, Double) -> Unit,
    onClearHomeLocation: () -> Unit,
) {
    var showLayerDialog by remember { mutableStateOf(false) }
    var showHomeDialog by remember { mutableStateOf(false) }
    Scaffold(
        contentWindowInsets = aplContentWindowInsets(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.map_settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.TwoTone.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding,
        ) {
            item {
                SettingsSwitchItem(
                    title = stringResource(R.string.map_settings_restore_last_view_title),
                    summary = stringResource(R.string.map_settings_restore_last_view_summary),
                    checked = state.isRestoreLastViewEnabled,
                    icon = Icons.TwoTone.Restore,
                    onCheckedChange = { onToggleRestoreLastView() },
                )
            }
            item {
                SettingsSwitchItem(
                    title = stringResource(R.string.map_settings_native_info_panel_title),
                    summary = stringResource(R.string.map_settings_native_info_panel_summary),
                    checked = state.isNativeInfoPanelEnabled,
                    icon = Icons.TwoTone.Info,
                    onCheckedChange = { onToggleNativeInfoPanel() },
                )
            }
            item {
                SettingsSwitchItem(
                    title = stringResource(R.string.map_settings_hover_info_title),
                    summary = stringResource(R.string.map_settings_hover_info_summary),
                    checked = state.isHoverInfoEnabled,
                    icon = Icons.TwoTone.TouchApp,
                    enabled = state.isNativeInfoPanelEnabled,
                    onCheckedChange = { onToggleHoverInfo() },
                )
            }
            item {
                SettingsPreferenceItem(
                    title = stringResource(R.string.map_settings_layer_title),
                    summary = stringResource(state.mapLayer.labelRes),
                    icon = Icons.TwoTone.Layers,
                    onClick = { showLayerDialog = true },
                )
            }
            item {
                SettingsPreferenceItem(
                    title = stringResource(R.string.map_settings_home_location_title),
                    summary = state.homeLocation?.let {
                        "%.4f, %.4f".format(it.lat, it.lon)
                    } ?: stringResource(R.string.map_settings_home_location_summary),
                    icon = Icons.TwoTone.Home,
                    onClick = { showHomeDialog = true },
                )
            }
            item {
                Text(
                    text = stringResource(R.string.map_settings_overlays_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
                )
            }
            MapOverlay.Category.entries.forEach { category ->
                val overlaysInCategory = MapOverlay.entries.filter { it.category == category }
                item {
                    Text(
                        text = stringResource(category.labelRes),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
                overlaysInCategory.forEach { overlay ->
                    item {
                        SettingsSwitchItem(
                            title = stringResource(overlay.labelRes),
                            checked = overlay.key in state.enabledOverlays,
                            onCheckedChange = { onToggleOverlay(overlay) },
                        )
                    }
                }
            }
        }
    }

    if (showLayerDialog) {
        MapLayerDialog(
            selected = state.mapLayer,
            onSelect = { layer ->
                onSetMapLayer(layer)
                showLayerDialog = false
            },
            onDismiss = { showLayerDialog = false },
        )
    }

    if (showHomeDialog) {
        HomeLocationDialog(
            current = state.homeLocation,
            onSet = { lat, lon ->
                onSetHomeLocation(lat, lon)
                showHomeDialog = false
            },
            onClear = {
                onClearHomeLocation()
                showHomeDialog = false
            },
            onDismiss = { showHomeDialog = false },
        )
    }
}

@Composable
private fun HomeLocationDialog(
    current: de.taymaerz.skyfox.map.core.SavedCamera?,
    onSet: (Double, Double) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
) {
    var latText by remember { mutableStateOf(current?.lat?.toString() ?: "") }
    var lonText by remember { mutableStateOf(current?.lon?.toString() ?: "") }
    val lat = latText.trim().replace(',', '.').toDoubleOrNull()
    val lon = lonText.trim().replace(',', '.').toDoubleOrNull()
    val valid = lat != null && lon != null && lat in -90.0..90.0 && lon in -180.0..180.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.map_settings_home_location_title)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.map_settings_home_location_dialog_hint),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                OutlinedTextField(
                    value = latText,
                    onValueChange = { latText = it },
                    label = { Text(stringResource(R.string.map_settings_home_location_lat_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = lonText,
                    onValueChange = { lonText = it },
                    label = { Text(stringResource(R.string.map_settings_home_location_lon_label)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (lat != null && lon != null) onSet(lat, lon) },
                enabled = valid,
            ) {
                Text(stringResource(R.string.common_save_action))
            }
        },
        dismissButton = {
            Row {
                if (current != null) {
                    TextButton(onClick = onClear) {
                        Text(stringResource(R.string.map_settings_home_location_clear_action))
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.common_cancel_action))
                }
            }
        },
    )
}

@Composable
private fun MapLayerDialog(
    selected: MapLayer,
    onSelect: (MapLayer) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.map_settings_layer_title)) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                MapLayer.entries.forEach { layer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(layer) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = layer == selected,
                            onClick = { onSelect(layer) },
                        )
                        Text(
                            text = stringResource(layer.labelRes),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel_action))
            }
        },
    )
}
