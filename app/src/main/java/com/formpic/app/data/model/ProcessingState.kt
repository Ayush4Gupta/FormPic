package com.formpic.app.data.model

import android.graphics.Bitmap
import android.net.Uri

/**
 * High-level state representing current processing phase.
 */
sealed interface ProcessingPhase {
    data object Idle : ProcessingPhase
    data class Loading(val message: String = "Loading photo safely…") : ProcessingPhase
    data class DetectingFace(val message: String = "Detecting face & calculating passport frame…") : ProcessingPhase
    data class RemovingBackground(val message: String = "Isolating subject & applying white background…") : ProcessingPhase
    data class Compressing(val message: String = "Compressing to exact target file size…") : ProcessingPhase
    data class Success(val result: ProcessingResult) : ProcessingPhase
    data class Failure(val errorMessage: String, val recoveryAction: String = "Please try another photo") : ProcessingPhase
}

/**
 * Output data resulting from successful execution of the image processing pipeline.
 */
data class ProcessingResult(
    val originalUri: Uri?,
    val originalBitmap: Bitmap,
    val segmentedBitmap: Bitmap,
    val finalBitmap: Bitmap,
    val finalBytes: ByteArray,
    val finalSizeBytes: Long,
    val targetMaxKb: Int,
    val width: Int,
    val height: Int,
    val preset: PhotoPreset,
    val qualityReport: QualityCheckReport
) {
    val formattedFileSize: String
        get() {
            val kb = finalSizeBytes / 1024.0
            return "%.1f KB".format(kb)
        }

    val isWithinLimit: Boolean
        get() = finalSizeBytes <= (targetMaxKb * 1024L)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProcessingResult

        if (originalUri != other.originalUri) return false
        if (finalSizeBytes != other.finalSizeBytes) return false
        if (targetMaxKb != other.targetMaxKb) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (preset != other.preset) return false

        return true
    }

    override fun hashCode(): Int {
        var result = originalUri?.hashCode() ?: 0
        result = 31 * result + finalSizeBytes.hashCode()
        result = 31 * result + targetMaxKb
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + preset.hashCode()
        return result
    }
}
