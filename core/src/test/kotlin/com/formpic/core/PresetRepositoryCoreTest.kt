package com.formpic.core

import com.formpic.core.model.PresetCategory
import com.formpic.core.repository.PresetRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PresetRepositoryCoreTest {

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
        assertTrue(targets.contains(25))
        assertTrue(targets.contains(50))
        assertTrue(targets.contains(75))
        assertTrue(targets.contains(100))
    }

    @Test
    fun testOfficialIndianPassportSpecifications() {
        val passport = PresetRepository.officialExamPresets.find { it.id == "official_passport_india" }
        assertNotNull(passport)
        assertEquals(100, passport!!.targetMaxKb)
        assertEquals(35f, passport.widthMm)
        assertEquals(45f, passport.heightMm)
        assertTrue(passport.requiresWhiteBackground)
        assertTrue(passport.officialSource.contains("passportindia.gov.in"))
    }

    @Test
    fun testSscExamSpecifications() {
        val ssc = PresetRepository.officialExamPresets.find { it.id == "official_ssc" }
        assertNotNull(ssc)
        assertEquals(50, ssc!!.targetMaxKb)
        assertEquals(35f, ssc.widthMm)
        assertEquals(45f, ssc.heightMm)
    }

    @Test
    fun testUpscExamSpecifications() {
        val upsc = PresetRepository.officialExamPresets.find { it.id == "official_upsc" }
        assertNotNull(upsc)
        assertEquals(1.0f, upsc!!.aspectRatio, 0.01f)
        assertTrue(upsc.targetMaxKb <= 300)
    }

    @Test
    fun testIbpsExamSpecifications() {
        val ibps = PresetRepository.officialExamPresets.find { it.id == "official_ibps_sbi" }
        assertNotNull(ibps)
        assertEquals(50, ibps!!.targetMaxKb)
        assertEquals(200, ibps.widthPx)
        assertEquals(230, ibps.heightPx)
    }

    @Test
    fun testCustomPresetSanitization() {
        val customLow = PresetRepository.createCustomPreset(targetKb = 5, widthPx = 50, heightPx = 50)
        assertTrue(customLow.targetMaxKb >= 15)
        assertTrue(customLow.widthPx >= 150)

        val customHigh = PresetRepository.createCustomPreset(targetKb = 10000, widthPx = 8000, heightPx = 9000)
        assertTrue(customHigh.targetMaxKb <= 2000)
        assertTrue(customHigh.widthPx <= 4000)
    }
}
