package com.formpic.app

import com.formpic.app.data.model.PresetCategory
import com.formpic.app.data.repository.PresetRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying official Indian exam and passport specifications.
 */
class PresetRepositoryTest {

    @Test
    fun testDefaultPresetIsUnder50Kb() {
        val defaultPreset = PresetRepository.defaultPreset
        assertEquals("quick_50kb", defaultPreset.id)
        assertEquals(50, defaultPreset.targetMaxKb)
        assertEquals(PresetCategory.QUICK_KB, defaultPreset.category)
    }

    @Test
    fun testQuickKbPresetsContainStandardTiers() {
        val targets = PresetRepository.quickKbPresets.map { it.targetMaxKb }
        assertTrue("Must contain 25 KB", targets.contains(25))
        assertTrue("Must contain 50 KB", targets.contains(50))
        assertTrue("Must contain 75 KB", targets.contains(75))
        assertTrue("Must contain 100 KB", targets.contains(100))
    }

    @Test
    fun testOfficialIndianPassportSpecifications() {
        val passport = PresetRepository.officialExamPresets.find { it.id == "official_passport_india" }
        assertNotNull("Indian Passport preset must exist", passport)
        assertEquals(100, passport!!.targetMaxKb)
        assertEquals(35f, passport.widthMm)
        assertEquals(45f, passport.heightMm)
        assertTrue("Passport requires white background", passport.requiresWhiteBackground)
        assertTrue("Source must cite official Passport Seva / MEA", passport.officialSource.contains("passportindia.gov.in"))
    }

    @Test
    fun testSscExamSpecifications() {
        val ssc = PresetRepository.officialExamPresets.find { it.id == "official_ssc" }
        assertNotNull("SSC preset must exist", ssc)
        assertEquals(50, ssc!!.targetMaxKb)
        assertEquals(35f, ssc.widthMm)
        assertEquals(45f, ssc.heightMm)
    }

    @Test
    fun testUpscExamSpecifications() {
        val upsc = PresetRepository.officialExamPresets.find { it.id == "official_upsc" }
        assertNotNull("UPSC preset must exist", upsc)
        assertEquals(1.0f, upsc!!.aspectRatio, 0.01f) // Square aspect ratio 1:1
        assertTrue("UPSC allows up to 200/300 KB", upsc.targetMaxKb <= 300)
    }

    @Test
    fun testIbpsExamSpecifications() {
        val ibps = PresetRepository.officialExamPresets.find { it.id == "official_ibps_sbi" }
        assertNotNull("IBPS preset must exist", ibps)
        assertEquals(50, ibps!!.targetMaxKb)
        assertEquals(200, ibps.widthPx)
        assertEquals(230, ibps.heightPx)
    }

    @Test
    fun testCustomPresetSanitization() {
        // Clamping check: very large or very small inputs
        val customLow = PresetRepository.createCustomPreset(targetKb = 5, widthPx = 50, heightPx = 50)
        assertTrue("Target KB should be clamped to min 15", customLow.targetMaxKb >= 15)
        assertTrue("Width should be clamped to min 150", customLow.widthPx >= 150)

        val customHigh = PresetRepository.createCustomPreset(targetKb = 10000, widthPx = 8000, heightPx = 9000)
        assertTrue("Target KB should be clamped to max 2000", customHigh.targetMaxKb <= 2000)
        assertTrue("Width should be clamped to max 4000", customHigh.widthPx <= 4000)
    }
}
