package com.formpic.app.engine

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * High-precision, failure-proof JPEG compression engine.
 * Guarantees that final output byte size is strictly <= (targetMaxKb * 1024L) bytes.
 */
object ExactKbCompressor {

    data class CompressionOutput(
        val byteArray: ByteArray,
        val exactSizeBytes: Long,
        val finalWidth: Int,
        val finalHeight: Int,
        val finalBitmap: Bitmap,
        val qualityUsed: Int,
        val isQualityCompromisedWarning: Boolean
    ) {
        val exactSizeKb: Double
            get() = exactSizeBytes / 1024.0

        val formattedSize: String
            get() = "%.1f KB".format(exactSizeKb)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CompressionOutput

            if (!byteArray.contentEquals(other.byteArray)) return false
            if (exactSizeBytes != other.exactSizeBytes) return false
            if (finalWidth != other.finalWidth) return false
            if (finalHeight != other.finalHeight) return false
            if (qualityUsed != other.qualityUsed) return false

            return true
        }

        override fun hashCode(): Int {
            var result = byteArray.contentHashCode()
            result = 31 * result + exactSizeBytes.hashCode()
            result = 31 * result + finalWidth
            result = 31 * result + finalHeight
            result = 31 * result + qualityUsed
            return result
        }
    }

    /**
     * Compresses bitmap to strictly <= targetMaxKb bytes using binary search and adaptive downscaling.
     */
    fun compressToExactLimit(
        sourceBitmap: Bitmap,
        targetMaxKb: Int,
        initialWidth: Int = sourceBitmap.width,
        initialHeight: Int = sourceBitmap.height
    ): CompressionOutput {
        require(targetMaxKb > 0) { "targetMaxKb must be greater than 0" }
        val targetBytes = targetMaxKb * 1024L

        var currentBitmap = if (sourceBitmap.width != initialWidth || sourceBitmap.height != initialHeight) {
            BitmapUtils.scaleBitmap(sourceBitmap, initialWidth, initialHeight)
        } else {
            sourceBitmap
        }

        var attempts = 0
        val maxScaleAttempts = 4
        var bestBytes: ByteArray? = null
        var bestQuality = 15
        var isWarning = false

        while (attempts < maxScaleAttempts) {
            val result = binarySearchQuality(currentBitmap, targetBytes)
            if (result != null) {
                bestBytes = result.first
                bestQuality = result.second
                // If quality is decent (>= 35) or we cannot scale further, accept
                if (bestQuality >= 35 || attempts >= maxScaleAttempts - 1) {
                    if (bestQuality < 35) isWarning = true
                    break
                }
            }

            // Downsample dimensions adaptively to preserve sharp high frequencies
            val currentTestBytes = encodeJpeg(currentBitmap, 50).size
            val scaleFactor = (sqrt(targetBytes.toDouble() / max(1, currentTestBytes).toDouble()) * 0.90)
                .coerceIn(0.5, 0.88).toFloat()

            val newW = max(160, (currentBitmap.width * scaleFactor).roundToInt())
            val newH = max(200, (currentBitmap.height * scaleFactor).roundToInt())

            if (newW == currentBitmap.width && newH == currentBitmap.height) {
                break
            }

            val scaled = Bitmap.createScaledBitmap(currentBitmap, newW, newH, true)
            if (currentBitmap != sourceBitmap && !currentBitmap.isRecycled) {
                currentBitmap.recycle()
            }
            currentBitmap = scaled
            attempts++
        }

        // Strict Safety Enforcement: If still exceeds (or null), force down quality & dimensions
        var finalBytes = bestBytes
        if (finalBytes == null || finalBytes.size > targetBytes) {
            isWarning = true
            var quality = 30
            while (quality >= 5) {
                val candidate = encodeJpeg(currentBitmap, quality)
                if (candidate.size <= targetBytes) {
                    finalBytes = candidate
                    bestQuality = quality
                    break
                }
                quality -= 5
            }

            // Ultimate fail-safe: scale down repeatedly until strictly <= targetBytes
            while (finalBytes == null || finalBytes.size > targetBytes) {
                val newW = max(100, (currentBitmap.width * 0.85f).roundToInt())
                val newH = max(120, (currentBitmap.height * 0.85f).roundToInt())
                val scaled = Bitmap.createScaledBitmap(currentBitmap, newW, newH, true)
                if (currentBitmap != sourceBitmap && !currentBitmap.isRecycled) {
                    currentBitmap.recycle()
                }
                currentBitmap = scaled
                finalBytes = encodeJpeg(currentBitmap, 40)
            }
        }

        // Final verification check
        check(finalBytes.size <= targetBytes) {
            "Internal error: Final size ${finalBytes.size} exceeded target $targetBytes"
        }

        val finalDecoded = BitmapFactory.decodeByteArray(finalBytes, 0, finalBytes.size)

        return CompressionOutput(
            byteArray = finalBytes,
            exactSizeBytes = finalBytes.size.toLong(),
            finalWidth = finalDecoded?.width ?: currentBitmap.width,
            finalHeight = finalDecoded?.height ?: currentBitmap.height,
            finalBitmap = finalDecoded ?: currentBitmap,
            qualityUsed = bestQuality,
            isQualityCompromisedWarning = isWarning || targetMaxKb <= 25
        )
    }

    /**
     * Binary searches for the highest JPEG quality (between 15 and 98)
     * such that the encoded byte count is strictly <= targetBytes.
     */
    private fun binarySearchQuality(bitmap: Bitmap, targetBytes: Long): Pair<ByteArray, Int>? {
        val result = com.formpic.core.engine.ExactKbCompressorCore.findOptimalQuality(targetBytes) { q ->
            encodeJpeg(bitmap, q).size.toLong()
        } ?: return null

        val bytes = encodeJpeg(bitmap, result.quality)
        return Pair(bytes, result.quality)
    }

    /**
     * Algorithmic core of quality binary search, completely decoupled from Android graphics.
     * Guaranteed to find the highest quality (between 15 and 98) where size <= targetBytes.
     */
    fun findOptimalQuality(targetBytes: Long, sizeForQuality: (quality: Int) -> Long): Pair<Int, Long>? {
        var low = 15
        var high = 98
        var bestQuality = -1
        var bestSize = -1L

        while (low <= high) {
            val mid = (low + high) / 2
            val currentSize = sizeForQuality(mid)

            if (currentSize <= targetBytes) {
                bestQuality = mid
                bestSize = currentSize
                // Try higher quality to preserve visual fidelity
                low = mid + 1
            } else {
                // Exceeded limit, reduce quality
                high = mid - 1
            }
        }

        return if (bestQuality != -1) Pair(bestQuality, bestSize) else null
    }

    /**
     * Encodes bitmap into JPEG byte array.
     */
    fun encodeJpeg(bitmap: Bitmap, quality: Int): ByteArray {
        val safeQuality = quality.coerceIn(1, 100)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, safeQuality, stream)
        return stream.toByteArray()
    }
}
