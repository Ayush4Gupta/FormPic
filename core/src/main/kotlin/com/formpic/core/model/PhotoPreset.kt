package com.formpic.core.model

enum class PresetCategory {
    QUICK_KB,
    GOVERNMENT_EXAMS,
    CUSTOM
}

data class PhotoPreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val targetMaxKb: Int,
    val widthPx: Int,
    val heightPx: Int,
    val widthMm: Float = 0f,
    val heightMm: Float = 0f,
    val category: PresetCategory,
    val officialSource: String = "",
    val verifiedDate: String = "",
    val tips: String = "",
    val requiresWhiteBackground: Boolean = false,
    val isWhiteBackgroundMandatory: Boolean = false
) {
    val aspectRatio: Float
        get() = if (heightPx > 0) widthPx.toFloat() / heightPx.toFloat() else 35f / 45f

    val displayDimensionString: String
        get() = when {
            widthMm > 0 && heightMm > 0 -> "%.0f × %.0f mm (%d × %d px)".format(widthMm, heightMm, widthPx, heightPx)
            else -> "%d × %d px".format(widthPx, heightPx)
        }
}
