package com.formpic.core

import com.formpic.core.engine.ExactKbCompressorCore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying exact KB compression limits.
 * Validates:
 * - Under 25 KB
 * - Under 50 KB
 * - Under 75 KB
 * - Under 100 KB
 * - Custom 30 KB
 * - High-frequency vs low-frequency image compression behavior
 */
class ExactKbCompressorCoreTest {

    // Realistic JPEG size simulation curve where size increases monotonically with quality:
    // Base header + content size scaling quadratically with quality
    private fun simulatedJpegSize(quality: Int, complexityFactor: Double = 1.0): Long {
        val baseHeaderBytes = 620L
        val dynamicPayload = (quality * quality * 8.5 * complexityFactor).toLong()
        return baseHeaderBytes + dynamicPayload
    }

    @Test
    fun testUnder25KbStrictByteEnforcement() {
        val targetKb = 25
        val targetBytes = targetKb * 1024L // 25,600 bytes

        val result = ExactKbCompressorCore.findOptimalQuality(targetBytes) { q ->
            simulatedJpegSize(q, complexityFactor = 1.2)
        }

        assertNotNull("Optimal quality must be found", result)
        val quality = result!!.quality
        val actualBytes = result.sizeBytes

        assertTrue("Quality must be in valid range [15..98]", quality in 15..98)
        assertTrue(
            "Final size ($actualBytes bytes) MUST be <= target ($targetBytes bytes)",
            actualBytes <= targetBytes
        )
        assertTrue(
            "Verification method must confirm within limit",
            ExactKbCompressorCore.verifySizeWithinLimit(actualBytes, targetKb)
        )
    }

    @Test
    fun testUnder50KbStrictByteEnforcement() {
        val targetKb = 50
        val targetBytes = targetKb * 1024L // 51,200 bytes

        val result = ExactKbCompressorCore.findOptimalQuality(targetBytes) { q ->
            simulatedJpegSize(q, complexityFactor = 1.5)
        }

        assertNotNull(result)
        val actualBytes = result!!.sizeBytes

        assertTrue(
            "Final size ($actualBytes bytes) MUST be strictly <= 50 KB ($targetBytes bytes)",
            actualBytes <= targetBytes
        )
        assertTrue(ExactKbCompressorCore.verifySizeWithinLimit(actualBytes, targetKb))
    }

    @Test
    fun testUnder75KbStrictByteEnforcement() {
        val targetKb = 75
        val targetBytes = targetKb * 1024L // 76,800 bytes

        val result = ExactKbCompressorCore.findOptimalQuality(targetBytes) { q ->
            simulatedJpegSize(q, complexityFactor = 2.0)
        }

        assertNotNull(result)
        val actualBytes = result!!.sizeBytes

        assertTrue(
            "Final size ($actualBytes bytes) MUST be strictly <= 75 KB ($targetBytes bytes)",
            actualBytes <= targetBytes
        )
    }

    @Test
    fun testUnder100KbStrictByteEnforcement() {
        val targetKb = 100
        val targetBytes = targetKb * 1024L // 102,400 bytes

        val result = ExactKbCompressorCore.findOptimalQuality(targetBytes) { q ->
            simulatedJpegSize(q, complexityFactor = 2.5)
        }

        assertNotNull(result)
        val actualBytes = result!!.sizeBytes

        assertTrue(
            "Final size ($actualBytes bytes) MUST be strictly <= 100 KB ($targetBytes bytes)",
            actualBytes <= targetBytes
        )
    }

    @Test
    fun testCustom30KbStrictByteEnforcement() {
        val targetKb = 30
        val targetBytes = targetKb * 1024L // 30,720 bytes

        val result = ExactKbCompressorCore.findOptimalQuality(targetBytes) { q ->
            simulatedJpegSize(q, complexityFactor = 1.1)
        }

        assertNotNull(result)
        val actualBytes = result!!.sizeBytes

        assertTrue(
            "Final size ($actualBytes bytes) MUST be strictly <= 30 KB ($targetBytes bytes)",
            actualBytes <= targetBytes
        )
    }

    @Test
    fun testOptimalQualityMaximizesVisualFidelity() {
        val targetBytes = 50 * 1024L // 51,200 bytes

        val result = ExactKbCompressorCore.findOptimalQuality(targetBytes) { q ->
            simulatedJpegSize(q, complexityFactor = 0.5) // Clean, smooth image
        }

        assertNotNull(result)
        val quality = result!!.quality

        // For a clean photo, binary search should pick high quality (>= 80)
        assertTrue(
            "Expected high quality for clean/smooth photo, got: $quality",
            quality >= 80
        )
    }

    @Test
    fun testAdaptiveDownsamplingScaleFactor() {
        val targetBytes = 50 * 1024L // 51,200 bytes
        val currentBytes = 200 * 1024L // 204,800 bytes (4x larger)

        val scale = ExactKbCompressorCore.calculateAdaptiveScale(targetBytes, currentBytes)

        assertTrue("Scale must be downscaling (< 1.0)", scale < 1.0f)
        assertTrue("Scale must be bounded above minimum 0.4", scale >= 0.4f)
    }
}
