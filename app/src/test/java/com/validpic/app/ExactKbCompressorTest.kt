package com.validpic.app

import com.validpic.app.engine.ExactKbCompressor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for ExactKbCompressor algorithm.
 * Tests strict adherence to target byte limits:
 * Under 25 KB, Under 50 KB, Under 75 KB, Under 100 KB, and Custom 30 KB.
 */
class ExactKbCompressorTest {

    // Realistic JPEG size simulation curve where size increases monotonically with quality:
    // Base header + content size scaling quadratically with quality
    private fun simulatedJpegSize(quality: Int, complexityFactor: Double = 1.0): Long {
        val baseHeaderBytes = 620L
        val dynamicPayload = (quality * quality * 8.5 * complexityFactor).toLong()
        return baseHeaderBytes + dynamicPayload
    }

    @Test
    fun testUnder25KbTargetEnforced() {
        val targetKb = 25
        val targetBytes = targetKb * 1024L // 25,600 bytes

        val result = ExactKbCompressor.findOptimalQuality(targetBytes) { q ->
            simulatedJpegSize(q, complexityFactor = 1.2)
        }

        assertNotNull("Optimal quality should be found", result)
        val (quality, finalSizeBytes) = result!!

        assertTrue("Quality must be in valid range [15..98]", quality in 15..98)
        assertTrue(
            "Final size ($finalSizeBytes bytes) must be <= target ($targetBytes bytes)",
            finalSizeBytes <= targetBytes
        )
    }

    @Test
    fun testUnder50KbTargetEnforced() {
        val targetKb = 50
        val targetBytes = targetKb * 1024L // 51,200 bytes

        val result = ExactKbCompressor.findOptimalQuality(targetBytes) { q ->
            simulatedJpegSize(q, complexityFactor = 1.5)
        }

        assertNotNull("Optimal quality should be found", result)
        val (quality, finalSizeBytes) = result!!

        assertTrue(
            "Final size ($finalSizeBytes bytes) must be strictly <= 50 KB ($targetBytes bytes)",
            finalSizeBytes <= targetBytes
        )
    }

    @Test
    fun testUnder75KbTargetEnforced() {
        val targetKb = 75
        val targetBytes = targetKb * 1024L // 76,800 bytes

        val result = ExactKbCompressor.findOptimalQuality(targetBytes) { q ->
            simulatedJpegSize(q, complexityFactor = 2.0)
        }

        assertNotNull(result)
        val (_, finalSizeBytes) = result!!

        assertTrue(
            "Final size ($finalSizeBytes bytes) must be strictly <= 75 KB ($targetBytes bytes)",
            finalSizeBytes <= targetBytes
        )
    }

    @Test
    fun testUnder100KbTargetEnforced() {
        val targetKb = 100
        val targetBytes = targetKb * 1024L // 102,400 bytes

        val result = ExactKbCompressor.findOptimalQuality(targetBytes) { q ->
            simulatedJpegSize(q, complexityFactor = 2.5)
        }

        assertNotNull(result)
        val (_, finalSizeBytes) = result!!

        assertTrue(
            "Final size ($finalSizeBytes bytes) must be strictly <= 100 KB ($targetBytes bytes)",
            finalSizeBytes <= targetBytes
        )
    }

    @Test
    fun testCustom30KbTargetEnforced() {
        val targetKb = 30
        val targetBytes = targetKb * 1024L // 30,720 bytes

        val result = ExactKbCompressor.findOptimalQuality(targetBytes) { q ->
            simulatedJpegSize(q, complexityFactor = 1.1)
        }

        assertNotNull(result)
        val (_, finalSizeBytes) = result!!

        assertTrue(
            "Final size ($finalSizeBytes bytes) must be strictly <= 30 KB ($targetBytes bytes)",
            finalSizeBytes <= targetBytes
        )
    }

    @Test
    fun testOptimalQualityMaximizesVisualFidelity() {
        val targetBytes = 50 * 1024L // 51,200 bytes

        val result = ExactKbCompressor.findOptimalQuality(targetBytes) { q ->
            simulatedJpegSize(q, complexityFactor = 0.5)
        }

        assertNotNull(result)
        val (quality, _) = result!!

        // For a low-complexity image, binary search should pick high quality (>= 80)
        assertTrue(
            "Expected high quality for clean/low complexity photo, got: $quality",
            quality >= 80
        )
    }
}
