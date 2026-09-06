package com.formpic.app.data.repository

import com.formpic.app.data.model.PhotoPreset
import com.formpic.app.data.model.PresetCategory

/**
 * Official Indian and General Exam/Passport Presets.
 * Sources cross-referenced with official portals (Passport Seva, SSC, UPSC, IBPS, NTA).
 */
object PresetRepository {

    val defaultPreset: PhotoPreset = PhotoPreset(
        id = "quick_50kb",
        title = "Under 50 KB",
        subtitle = "Standard size for SSC, IBPS, State PSCs & College Forms",
        targetMaxKb = 50,
        widthPx = 600,
        heightPx = 750,
        widthMm = 35f,
        heightMm = 45f,
        category = PresetCategory.QUICK_KB,
        tips = "Most Indian application portals require photos under 50 KB.",
        requiresWhiteBackground = false,
        isWhiteBackgroundMandatory = false,
        isSignature = false,
        isPassportSize = true,
        isPassportSizeMandatory = false
    )

    val quickKbPresets: List<PhotoPreset> = listOf(
        PhotoPreset(
            id = "quick_25kb",
            title = "Under 25 KB",
            subtitle = "Very strict portals (e.g. State Police, older recruitment boards)",
            targetMaxKb = 25,
            widthPx = 400,
            heightPx = 500,
            widthMm = 35f,
            heightMm = 45f,
            category = PresetCategory.QUICK_KB,
            tips = "Optimized to achieve compact size while preserving facial features.",
            requiresWhiteBackground = false,
            isWhiteBackgroundMandatory = false,
            isSignature = false,
            isPassportSize = true,
            isPassportSizeMandatory = false
        ),
        defaultPreset,
        PhotoPreset(
            id = "quick_75kb",
            title = "Under 75 KB",
            subtitle = "Medium-high quality for university and corporate job portals",
            targetMaxKb = 75,
            widthPx = 630,
            heightPx = 810,
            widthMm = 35f,
            heightMm = 45f,
            category = PresetCategory.QUICK_KB,
            tips = "Crisp resolution balanced with file size limits.",
            requiresWhiteBackground = false,
            isWhiteBackgroundMandatory = false,
            isSignature = false,
            isPassportSize = true,
            isPassportSizeMandatory = false
        ),
        PhotoPreset(
            id = "quick_100kb",
            title = "Under 100 KB",
            subtitle = "Passport Seva & high-res document uploads",
            targetMaxKb = 100,
            widthPx = 630,
            heightPx = 810,
            widthMm = 35f,
            heightMm = 45f,
            category = PresetCategory.QUICK_KB,
            tips = "High quality conforming to digital passport guidelines.",
            requiresWhiteBackground = false,
            isWhiteBackgroundMandatory = false,
            isSignature = false,
            isPassportSize = true,
            isPassportSizeMandatory = false
        )
    )

    val officialExamPresets: List<PhotoPreset> = listOf(
        PhotoPreset(
            id = "official_passport_india",
            title = "Indian Passport (Passport Seva)",
            subtitle = "35 × 45 mm (Ratio 7:9) • White Background",
            targetMaxKb = 100,
            widthPx = 630,
            heightPx = 810,
            widthMm = 35f,
            heightMm = 45f,
            category = PresetCategory.GOVERNMENT_EXAMS,
            officialSource = "passportindia.gov.in (Ministry of External Affairs)",
            verifiedDate = "2024–2026 Standards",
            tips = "Plain white background, full face centered, 70–80% head height.",
            requiresWhiteBackground = true,
            isWhiteBackgroundMandatory = true,
            isSignature = false,
            isPassportSize = true,
            isPassportSizeMandatory = true
        ),
        PhotoPreset(
            id = "official_ssc",
            title = "SSC (CGL, CHSL, MTS, GD)",
            subtitle = "20 KB – 50 KB • 3.5 × 4.5 cm • Light/White Background",
            targetMaxKb = 50,
            widthPx = 420,
            heightPx = 540,
            widthMm = 35f,
            heightMm = 45f,
            category = PresetCategory.GOVERNMENT_EXAMS,
            officialSource = "ssc.gov.in Official Notice",
            verifiedDate = "Current Commission Notice",
            tips = "No cap, no spectacles with tint, both ears visible.",
            requiresWhiteBackground = true,
            isWhiteBackgroundMandatory = true,
            isSignature = false,
            isPassportSize = true,
            isPassportSizeMandatory = true
        ),
        PhotoPreset(
            id = "official_upsc",
            title = "UPSC (Civil Services, NDA, CDS)",
            subtitle = "20 KB – 300 KB • Min 350 × 350 px • 3/4th Face Coverage",
            targetMaxKb = 200,
            widthPx = 600,
            heightPx = 600,
            category = PresetCategory.GOVERNMENT_EXAMS,
            officialSource = "upsconline.nic.in",
            verifiedDate = "UPSC One-Time Registration Guidelines",
            tips = "Photo must show clear front view with 75% face coverage.",
            requiresWhiteBackground = true,
            isWhiteBackgroundMandatory = true,
            isSignature = false,
            isPassportSize = false,
            isPassportSizeMandatory = true
        ),
        PhotoPreset(
            id = "official_ibps_sbi",
            title = "IBPS & SBI (PO, Clerk, SO)",
            subtitle = "20 KB – 50 KB • 200 × 230 px (4.5 × 3.5 cm)",
            targetMaxKb = 50,
            widthPx = 200,
            heightPx = 230,
            widthMm = 35f,
            heightMm = 45f,
            category = PresetCategory.GOVERNMENT_EXAMS,
            officialSource = "ibps.in / sbi.co.in/careers",
            verifiedDate = "IBPS Recruitment Guidelines",
            tips = "Strict maximum 50 KB limit. Upload server immediately rejects larger files.",
            requiresWhiteBackground = true,
            isWhiteBackgroundMandatory = true,
            isSignature = false,
            isPassportSize = true,
            isPassportSizeMandatory = true
        ),
        PhotoPreset(
            id = "official_neet_nta",
            title = "NEET UG / CUET (NTA)",
            subtitle = "10 KB – 200 KB • 80% Face Coverage • White Background",
            targetMaxKb = 100,
            widthPx = 600,
            heightPx = 750,
            widthMm = 35f,
            heightMm = 45f,
            category = PresetCategory.GOVERNMENT_EXAMS,
            officialSource = "exams.nta.ac.in/NEET",
            verifiedDate = "NTA Information Bulletin",
            tips = "White background without mask, ears clearly visible.",
            requiresWhiteBackground = true,
            isWhiteBackgroundMandatory = true,
            isSignature = false,
            isPassportSize = true,
            isPassportSizeMandatory = true
        ),
        PhotoPreset(
            id = "official_rrb",
            title = "Railway (RRB NTPC, ALP, Group D)",
            subtitle = "20 KB – 50 KB • 35 × 45 mm • Plain White Background",
            targetMaxKb = 50,
            widthPx = 420,
            heightPx = 540,
            widthMm = 35f,
            heightMm = 45f,
            category = PresetCategory.GOVERNMENT_EXAMS,
            officialSource = "rrbcdg.gov.in Centralized Employment Notice",
            verifiedDate = "RRB CEN Guidelines",
            tips = "Clear front view without goggles, hats or tinted glasses.",
            requiresWhiteBackground = true,
            isWhiteBackgroundMandatory = true,
            isSignature = false,
            isPassportSize = true,
            isPassportSizeMandatory = true
        ),
        PhotoPreset(
            id = "official_pan",
            title = "PAN Card (NSDL / UTIITSL)",
            subtitle = "Under 50 KB • 2.5 × 3.5 cm • White Background",
            targetMaxKb = 50,
            widthPx = 300,
            heightPx = 420,
            widthMm = 25f,
            heightMm = 35f,
            category = PresetCategory.GOVERNMENT_EXAMS,
            officialSource = "onlineservices.nsdl.com / pan.utiitsl.com",
            verifiedDate = "Income Tax / NSDL Portal",
            tips = "Clean color photo with pure white background.",
            requiresWhiteBackground = true,
            isWhiteBackgroundMandatory = true,
            isSignature = false,
            isPassportSize = true,
            isPassportSizeMandatory = true
        )
    )

    val officialSignaturePresets: List<PhotoPreset> = listOf(
        PhotoPreset(
            id = "official_signature_ssc",
            title = "SSC Signature (10–20 KB)",
            subtitle = "10 KB – 20 KB • 4.0 × 2.0 cm (140 × 60 px) • Black Ink",
            targetMaxKb = 20,
            widthPx = 280,
            heightPx = 140,
            widthMm = 40f,
            heightMm = 20f,
            category = PresetCategory.GOVERNMENT_EXAMS,
            officialSource = "ssc.gov.in Official Notice",
            verifiedDate = "Current Commission Notice",
            tips = "Sign on white paper with black ink. Pure white background removal is bypassed to preserve signature stroke fidelity.",
            requiresWhiteBackground = false,
            isWhiteBackgroundMandatory = false,
            isSignature = true,
            isPassportSize = false,
            isPassportSizeMandatory = false
        ),
        PhotoPreset(
            id = "official_signature_ibps",
            title = "IBPS / Bank Signature (10–20 KB)",
            subtitle = "10 KB – 20 KB • 140 × 60 px • White Paper Black Ink",
            targetMaxKb = 20,
            widthPx = 280,
            heightPx = 120,
            widthMm = 45f,
            heightMm = 20f,
            category = PresetCategory.GOVERNMENT_EXAMS,
            officialSource = "ibps.in Recruitment Guidelines",
            verifiedDate = "IBPS Current Standards",
            tips = "Strict maximum 20 KB limit. Background removal bypassed for clean ink signature.",
            requiresWhiteBackground = false,
            isWhiteBackgroundMandatory = false,
            isSignature = true,
            isPassportSize = false,
            isPassportSizeMandatory = false
        ),
        PhotoPreset(
            id = "official_signature_upsc",
            title = "UPSC Signature (20–300 KB)",
            subtitle = "20 KB – 300 KB • Min 350 × 350 px • White Paper",
            targetMaxKb = 200,
            widthPx = 500,
            heightPx = 500,
            category = PresetCategory.GOVERNMENT_EXAMS,
            officialSource = "upsconline.nic.in OTR Guidelines",
            verifiedDate = "UPSC OTR Standards",
            tips = "Signature must be clear with black or blue ink on white paper.",
            requiresWhiteBackground = false,
            isWhiteBackgroundMandatory = false,
            isSignature = true,
            isPassportSize = false,
            isPassportSizeMandatory = false
        )
    )

    val allOfficialPresets: List<PhotoPreset>
        get() = officialExamPresets + officialSignaturePresets

    fun createCustomPreset(targetKb: Int, widthPx: Int = 600, heightPx: Int = 750): PhotoPreset {
        val safeKb = targetKb.coerceIn(15, 2000)
        val safeW = widthPx.coerceIn(150, 4000)
        val safeH = heightPx.coerceIn(150, 4000)
        return PhotoPreset(
            id = "custom_${safeKb}kb_${safeW}x${safeH}",
            title = "Custom ($safeKb KB)",
            subtitle = "%d × %d px • Target Under %d KB".format(safeW, safeH, safeKb),
            targetMaxKb = safeKb,
            widthPx = safeW,
            heightPx = safeH,
            category = PresetCategory.CUSTOM,
            tips = "Custom configuration with guaranteed size limit.",
            requiresWhiteBackground = false,
            isWhiteBackgroundMandatory = false,
            isSignature = false,
            isPassportSize = false,
            isPassportSizeMandatory = false
        )
    }
}
