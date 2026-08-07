package de.taymaerz.skyfox.map.ui.settings

import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.datastore.valueBlocking
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.uix.ViewModel4
import de.taymaerz.skyfox.map.core.MapLayer
import de.taymaerz.skyfox.map.core.MapOverlay
import de.taymaerz.skyfox.map.core.MapSettings
import de.taymaerz.skyfox.map.core.SavedCamera
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class MapSettingsViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val mapSettings: MapSettings,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Settings", "Map", "VM"),
) {

    val state = combine(
        mapSettings.isRestoreLastViewEnabled.flow,
        mapSettings.isNativeInfoPanelEnabled.flow,
        mapSettings.isHoverInfoEnabled.flow,
        mapSettings.mapLayer.flow,
        mapSettings.enabledOverlays.flow,
        mapSettings.homeLocation.flow,
    ) { values ->
        State(
            isRestoreLastViewEnabled = values[0] as Boolean,
            isNativeInfoPanelEnabled = values[1] as Boolean,
            isHoverInfoEnabled = values[2] as Boolean,
            mapLayer = MapLayer.fromKey(values[3] as String),
            @Suppress("UNCHECKED_CAST")
            enabledOverlays = (values[4] as? Set<String>) ?: emptySet(),
            homeLocation = values[5] as? SavedCamera,
        )
    }.asStateFlow()

    fun toggleRestoreLastView() {
        log(tag) { "toggleRestoreLastView()" }
        mapSettings.isRestoreLastViewEnabled.valueBlocking = !mapSettings.isRestoreLastViewEnabled.valueBlocking
    }

    fun toggleNativeInfoPanel() {
        log(tag) { "toggleNativeInfoPanel()" }
        mapSettings.isNativeInfoPanelEnabled.valueBlocking = !mapSettings.isNativeInfoPanelEnabled.valueBlocking
    }

    fun toggleHoverInfo() {
        log(tag) { "toggleHoverInfo()" }
        mapSettings.isHoverInfoEnabled.valueBlocking = !mapSettings.isHoverInfoEnabled.valueBlocking
    }

    fun setMapLayer(layer: MapLayer) {
        log(tag) { "setMapLayer($layer)" }
        mapSettings.mapLayer.valueBlocking = layer.key
    }

    fun toggleOverlay(overlay: MapOverlay) {
        log(tag) { "toggleOverlay($overlay)" }
        val current = mapSettings.enabledOverlays.valueBlocking ?: emptySet()
        val updated = if (overlay.key in current) current - overlay.key else current + overlay.key
        mapSettings.enabledOverlays.valueBlocking = updated.ifEmpty { null }
    }

    fun setHomeLocation(lat: Double, lon: Double, zoom: Double = 9.0) {
        log(tag) { "setHomeLocation($lat, $lon, $zoom)" }
        mapSettings.homeLocation.valueBlocking = SavedCamera(lat, lon, zoom)
    }

    fun clearHomeLocation() {
        log(tag) { "clearHomeLocation()" }
        mapSettings.homeLocation.valueBlocking = null
    }

    data class State(
        val isRestoreLastViewEnabled: Boolean,
        val isNativeInfoPanelEnabled: Boolean,
        val isHoverInfoEnabled: Boolean,
        val mapLayer: MapLayer,
        val enabledOverlays: Set<String>,
        val homeLocation: SavedCamera? = null,
    )
}
