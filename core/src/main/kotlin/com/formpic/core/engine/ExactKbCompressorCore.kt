package com.formpic.core.engine

/**
 * Pure algorithmic compression and binary search engine.
 * Decoupled from Android framework for fast JVM testing and clean architecture.
 */
object ExactKbCompressorCore {

    data class QualitySearchResult(
        val quality: Int,
        val sizeBytes: Long
    )

    /**
     * Binary searches for the highest JPEG quality (between minQuality and maxQuality)
     * such that the size returned by [sizeForQuality] is strictly <= targetBytes.
     *
     * Returns the optimal (quality, size) pair, or null if even minQuality exceeds targetBytes.
     */
    fun findOptimalQuality(
        targetBytes: Long,
        minQuality: Int = 15,
        maxQuality: Int = 98,
        sizeForQuality: (quality: Int) -> Long
    ): QualitySearchResult? {
        require(targetBytes > 0) { "Target bytes must be positive" }

        var low = minQuality
        var high = maxQuality
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

        return if (bestQuality != -1) QualitySearchResult(bestQuality, bestSize) else null
    }

    /**
     * Calculates adaptive downsampling scale factor if quality at current resolution
     * drops below threshold or cannot meet target.
     */
    fun calculateAdaptiveScale(targetBytes: Long, currentBytes: Long): Float {
        if (currentBytes <= 0) return 1.0f
        val ratio = targetBytes.toDouble() / currentBytes.toDouble()
        val scale = Math.sqrt(ratio) * 0.92
        return scale.coerceIn(0.4, 0.9).toFloat()
    }

    /**
     * Verifies that the final file size is strictly within the allowed target bytes.
     */
    fun verifySizeWithinLimit(actualBytes: Long, targetMaxKb: Int): Boolean {
        return actualBytes <= (targetMaxKb * 1024L)
    }
}
