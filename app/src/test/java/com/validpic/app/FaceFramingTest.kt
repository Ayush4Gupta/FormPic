package com.validpic.app

import android.graphics.Rect
import com.validpic.app.engine.FaceDetectorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying passport face framing crop calculations.
 */
class FaceFramingTest {

    @Test
    fun testCropBoundsCalculation() {
        val engine = FaceDetectorEngine()
        val imageW = 1000
        val imageH = 1500
        val faceBox = Rect(400, 300, 600, 600) // 200x300 face centered at x=500
        val targetAspectRatio = 35f / 45f // ~0.777

        val crop = engine.calculatePassportCropRect(imageW, imageH, faceBox, targetAspectRatio)

        // Verify crop stays within original image bounds
        assertTrue("Crop left >= 0", crop.left >= 0)
        assertTrue("Crop top >= 0", crop.top >= 0)
        assertTrue("Crop right <= image width", crop.right <= imageW)
        assertTrue("Crop bottom <= image height", crop.bottom <= imageH)

        // Verify face is roughly centered horizontally
        val cropCenterX = (crop.left + crop.right) / 2
        val faceCenterX = faceBox.centerX()
        assertTrue("Crop should center around face X", Math.abs(cropCenterX - faceCenterX) <= 20)
    }
}
