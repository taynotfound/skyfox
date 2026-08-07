package de.taymaerz.skyfox.common.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import de.taymaerz.skyfox.common.debug.logging.Logging.Priority.WARN
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

inline fun <reified T> jsonReader(
    json: Json,
    defaultValue: T,
    onErrorFallbackToDefault: Boolean = false,
): (Any?) -> T {
    val serializer = json.serializersModule.serializer<T>()
    return { rawValue ->
        rawValue as String?
        if (rawValue == null) {
            defaultValue
        } else if (onErrorFallbackToDefault) {
            try {
                json.decodeFromString(serializer, rawValue)
            } catch (e: SerializationException) {
                log(logTag("DataStore", "ValueJson"), WARN) { "Failed to decode DataStore value, falling back to default. Error: ${e.message}" }
                defaultValue
            }
        } else {
            json.decodeFromString(serializer, rawValue)
        }
    }
}

inline fun <reified T> jsonWriter(
    json: Json,
): (T) -> Any? {
    val serializer = json.serializersModule.serializer<T>()
    return { newValue: T ->
        newValue?.let { json.encodeToString(serializer, it) }
    }
}

inline fun <reified T : Any?> DataStore<Preferences>.createValue(
    key: String,
    defaultValue: T = null as T,
    json: Json,
    onErrorFallbackToDefault: Boolean = false,
) = DataStoreValue(
    dataStore = this,
    key = stringPreferencesKey(key),
    reader = jsonReader(json, defaultValue, onErrorFallbackToDefault),
    writer = jsonWriter(json),
)