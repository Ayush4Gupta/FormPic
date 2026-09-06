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
        assertEquals(false, defaultPreset.requiresWhiteBackground)
        assertEquals(false, defaultPreset.isWhiteBackgroundMandatory)
        assertEquals(false, defaultPreset.isSignature)
        assertEquals(true, defaultPreset.isPassportSize)
        assertEquals(false, defaultPreset.isPassportSizeMandatory)
    }

    @Test
    fun testQuickKbPresetsContainStandardTiers() {
        val targets = PresetRepository.quickKbPresets.map { it.targetMaxKb }
        assertTrue(targets.contains(25))
        assertTrue(targets.contains(50))
        assertTrue(targets.contains(75))
        assertTrue(targets.contains(100))
        PresetRepository.quickKbPresets.forEach {
            assertEquals(false, it.requiresWhiteBackground)
            assertEquals(false, it.isWhiteBackgroundMandatory)
            assertEquals(false, it.isSignature)
            assertEquals(true, it.isPassportSize)
            assertEquals(false, it.isPassportSizeMandatory)
        }
    }

    @Test
    fun testOfficialIndianPassportSpecifications() {
        val passport = PresetRepository.officialExamPresets.find { it.id == "official_passport_india" }
        assertNotNull(passport)
        assertEquals(100, passport!!.targetMaxKb)
        assertEquals(35f, passport.widthMm)
        assertEquals(45f, passport.heightMm)
        assertTrue(passport.requiresWhiteBackground)
        assertTrue(passport.isWhiteBackgroundMandatory)
        assertEquals(false, passport.isSignature)
        assertEquals(true, passport.isPassportSize)
        assertEquals(true, passport.isPassportSizeMandatory)
        assertTrue(passport.officialSource.contains("passportindia.gov.in"))
    }

    @Test
    fun testSscExamSpecifications() {
        val ssc = PresetRepository.officialExamPresets.find { it.id == "official_ssc" }
        assertNotNull(ssc)
        assertEquals(50, ssc!!.targetMaxKb)
        assertEquals(35f, ssc.widthMm)
        assertEquals(45f, ssc.heightMm)
        assertTrue(ssc.requiresWhiteBackground)
        assertTrue(ssc.isWhiteBackgroundMandatory)
        assertEquals(true, ssc.isPassportSize)
        assertEquals(true, ssc.isPassportSizeMandatory)
    }

    @Test
    fun testUpscExamSpecifications() {
        val upsc = PresetRepository.officialExamPresets.find { it.id == "official_upsc" }
        assertNotNull(upsc)
        assertEquals(1.0f, upsc!!.aspectRatio, 0.01f)
        assertTrue(upsc.targetMaxKb <= 300)
        assertTrue(upsc.requiresWhiteBackground)
        assertEquals(false, upsc.isPassportSize)
        assertEquals(true, upsc.isPassportSizeMandatory)
    }

    @Test
    fun testIbpsExamSpecifications() {
        val ibps = PresetRepository.officialExamPresets.find { it.id == "official_ibps_sbi" }
        assertNotNull(ibps)
        assertEquals(50, ibps!!.targetMaxKb)
        assertEquals(200, ibps.widthPx)
        assertEquals(230, ibps.heightPx)
        assertTrue(ibps.requiresWhiteBackground)
        assertEquals(true, ibps.isPassportSize)
    }

    @Test
    fun testOfficialSignaturePresets() {
        val sigs = PresetRepository.officialSignaturePresets
        assertEquals(3, sigs.size)

        val sscSig = sigs.find { it.id == "official_signature_ssc" }
        assertNotNull(sscSig)
        assertEquals(20, sscSig!!.targetMaxKb)
        assertEquals(false, sscSig.requiresWhiteBackground)
        assertEquals(true, sscSig.isSignature)
        assertEquals(false, sscSig.isPassportSize)

        val ibpsSig = sigs.find { it.id == "official_signature_ibps" }
        assertNotNull(ibpsSig)
        assertEquals(20, ibpsSig!!.targetMaxKb)
        assertEquals(false, ibpsSig.requiresWhiteBackground)
        assertEquals(true, ibpsSig.isSignature)
        assertEquals(false, ibpsSig.isPassportSize)

        val upscSig = sigs.find { it.id == "official_signature_upsc" }
        assertNotNull(upscSig)
        assertEquals(200, upscSig!!.targetMaxKb)
        assertEquals(false, upscSig.requiresWhiteBackground)
        assertEquals(true, upscSig.isSignature)
        assertEquals(false, upscSig.isPassportSize)

        assertEquals(
            PresetRepository.officialExamPresets.size + sigs.size,
            PresetRepository.allOfficialPresets.size
        )
    }

    @Test
    fun testCustomPresetSanitization() {
        val customLow = PresetRepository.createCustomPreset(targetKb = 5, widthPx = 50, heightPx = 50)
        assertTrue(customLow.targetMaxKb >= 15)
        assertTrue(customLow.widthPx >= 150)
        assertEquals(false, customLow.requiresWhiteBackground)
        assertEquals(false, customLow.isSignature)
        assertEquals(false, customLow.isPassportSize)

        val customHigh = PresetRepository.createCustomPreset(targetKb = 10000, widthPx = 8000, heightPx = 9000)
        assertTrue(customHigh.targetMaxKb <= 2000)
        assertTrue(customHigh.widthPx <= 4000)
        assertEquals(false, customHigh.requiresWhiteBackground)
        assertEquals(false, customHigh.isSignature)
        assertEquals(false, customHigh.isPassportSize)
    }
}
