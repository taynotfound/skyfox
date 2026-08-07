package de.taymaerz.skyfox.feeder.ui.add

import android.net.Uri
import de.taymaerz.skyfox.feeder.core.ReceiverId
import de.taymaerz.skyfox.feeder.core.config.FeederPosition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class NewFeederQR(
    @SerialName("receiverId") val receiverId: ReceiverId,
    @SerialName("receiverLabel") val receiverLabel: String? = null,
    @SerialName("receiverIpv4Address") val receiverIpv4Address: String? = null,
    @SerialName("position") val position: FeederPosition? = null,
) {
    fun toUri(json: Json): Uri {
        val jsonData = json.encodeToString(this)
        return Uri.parse("$PREFIX?data=$jsonData")
    }

    companion object {
        const val PREFIX = "de_taymaerz_skyfox://feeder"

        fun isValid(url: String): Boolean {
            val uri = Uri.parse(url)
            return uri.scheme == "de_taymaerz_skyfox" && uri.host == "feeder"
        }

        fun fromUri(uri: Uri, json: Json): NewFeederQR? {
            if (!uri.toString().startsWith(PREFIX)) return null
            val jsonData = uri.getQueryParameter("data") ?: return null
            return json.decodeFromString<NewFeederQR>(jsonData)
        }
    }
}
