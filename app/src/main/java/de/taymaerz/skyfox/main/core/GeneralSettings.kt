package de.taymaerz.skyfox.main.core

import android.content.Context
import android.os.Build
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import de.taymaerz.skyfox.common.datastore.createValue
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.theming.ThemeColor
import de.taymaerz.skyfox.common.theming.ThemeMode
import de.taymaerz.skyfox.common.theming.ThemeStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import de.taymaerz.skyfox.common.datastore.createValue as createJsonValue

@Singleton
class GeneralSettings @Inject constructor(
    @param:ApplicationContext private val context: Context,
    json: Json,
) {

    private val Context.dataStore by preferencesDataStore(name = "settings_core")

    val deviceLabel = context.dataStore.createValue("core.device.label", Build.DEVICE)

    val isAutoReportingEnabled = context.dataStore.createValue("debug.bugreport.automatic.enabled", true)
    val isUpdateCheckEnabled = context.dataStore.createValue("updater.check.enabled", false)
    val dismissedUpdateVersion = context.dataStore.createValue<String?>("updater.dismissed.version", null)

    val isOnboardingFinished = context.dataStore.createValue("core.onboarding.finished", false)

    val themeMode = context.dataStore.createJsonValue("core.ui.theme.mode", ThemeMode.SYSTEM, json, onErrorFallbackToDefault = true)
    val themeStyle = context.dataStore.createJsonValue("core.ui.theme.style", ThemeStyle.DEFAULT, json, onErrorFallbackToDefault = true)
    val themeColor = context.dataStore.createJsonValue("core.ui.theme.color", ThemeColor.FOX, json, onErrorFallbackToDefault = true)

    val airplanesLiveApiKey = context.dataStore.createValue<String?>("core.airplaneslive.api.key", null)
    val apiKeyValid = MutableStateFlow<Boolean?>(null)

    val searchLocationDismissed = context.dataStore.createValue("search.location.dismissed", false)

    companion object {
        internal val TAG = logTag("Core", "Settings")
    }
}
