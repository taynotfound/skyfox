package de.taymaerz.skyfox.common.planespotters

import android.content.Context
import de.taymaerz.skyfox.R
import de.taymaerz.skyfox.main.core.aircraft.AircraftHex

data class PlanespottersMeta(
    val hex: AircraftHex,
    val author: String?,
    val link: String,
) {
    fun getCaption(context: Context): String = if (author != null) {
        context.getString(R.string.thumbnail_caption_by_x, author)
    } else {
        context.getString(R.string.thumbnail_caption_prompt)
    }
}