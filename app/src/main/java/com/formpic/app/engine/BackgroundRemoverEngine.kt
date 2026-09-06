package com.formpic.app.engine

import android.graphics.Bitmap
import android.graphics.Color
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.SegmentationMask
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.nio.ByteBuffer
import kotlin.coroutines.resume

/**
 * On-device background segmentation and pure-white replacement engine using ML Kit Selfie Segmentation.
 */
class BackgroundRemoverEngine {

    private val segmenterOptions = SelfieSegmenterOptions.Builder()
        .setDeliveryMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
        .build()

    private val segmenter = Segmentation.getClient(segmenterOptions)

    data class SegmentationResult(
        val outputBitmap: Bitmap,
        val maskBitmap: Bitmap // Used for manual touch-up editor
    )

    /**
     * Isolates foreground subject and blends with pure white (#FFFFFF) background.
     */
    suspend fun removeAndReplaceBackgroundWithWhite(sourceBitmap: Bitmap): SegmentationResult {
        val mask = generateMask(sourceBitmap) ?: return SegmentationResult(sourceBitmap, createFullMask(sourceBitmap.width, sourceBitmap.height))
        return applyMaskToWhiteBackground(sourceBitmap, mask)
    }

    private suspend fun generateMask(bitmap: Bitmap): SegmentationMask? = suspendCancellableCoroutine { continuation ->
        val image = InputImage.fromBitmap(bitmap, 0)
        segmenter.process(image)
            .addOnSuccessListener { mask ->
                if (continuation.isActive) continuation.resume(mask)
            }
            .addOnFailureListener {
                if (continuation.isActive) continuation.resume(null)
            }
    }

    /**
     * Blends the foreground pixels with pure white (#FFFFFF) using alpha anti-aliasing.
     */
    fun applyMaskToWhiteBackground(source: Bitmap, mask: SegmentationMask): SegmentationResult {
        val width = source.width
        val height = source.height
        val maskWidth = mask.width
        val maskHeight = mask.height
        val maskBuffer: ByteBuffer = mask.buffer

        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8)

        val srcPixels = IntArray(width * height)
        source.getPixels(srcPixels, 0, width, 0, 0, width, height)

        val outPixels = IntArray(width * height)
        val maskPixels = ByteArray(width * height)

        maskBuffer.rewind()

        // Iterate through pixels and blend with white
        val scaleX = maskWidth.toFloat() / width.toFloat()
        val scaleY = maskHeight.toFloat() / height.toFloat()

        for (y in 0 until height) {
            val maskY = (y * scaleY).toInt().coerceIn(0, maskHeight - 1)
            for (x in 0 until width) {
                val maskX = (x * scaleX).toInt().coerceIn(0, maskWidth - 1)
                val bufferIndex = (maskY * maskWidth + maskX) * 4

                val confidence = if (bufferIndex + 3 < maskBuffer.capacity()) {
                    maskBuffer.getFloat(bufferIndex)
                } else {
                    1.0f
                }

                val pixelIndex = y * width + x
                val srcColor = srcPixels[pixelIndex]

                // Smoothstep curve for soft natural hair transitions without edge halos
                val alpha = smoothStep(0.35f, 0.75f, confidence)

                val srcR = Color.red(srcColor)
                val srcG = Color.green(srcColor)
                val srcB = Color.blue(srcColor)

                // White background: 255, 255, 255
                val finalR = (srcR * alpha + 255 * (1f - alpha)).toInt().coerceIn(0, 255)
                val finalG = (srcG * alpha + 255 * (1f - alpha)).toInt().coerceIn(0, 255)
                val finalB = (srcB * alpha + 255 * (1f - alpha)).toInt().coerceIn(0, 255)

                outPixels[pixelIndex] = Color.rgb(finalR, finalG, finalB)
                maskPixels[pixelIndex] = (alpha * 255).toInt().toByte()
            }
        }

        outputBitmap.setPixels(outPixels, 0, width, 0, 0, width, height)
        maskBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(maskPixels))

        return SegmentationResult(outputBitmap, maskBitmap)
    }

    /**
     * Applies an updated manual touchup mask to the source bitmap and replaces background with pure white.
     */
    fun applyTouchUpMask(source: Bitmap, maskBitmap: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val srcPixels = IntArray(width * height)
        val maskPixels = IntArray(width * height)
        val outPixels = IntArray(width * height)

        source.getPixels(srcPixels, 0, width, 0, 0, width, height)
        maskBitmap.getPixels(maskPixels, 0, width, 0, 0, width, height)

        for (i in 0 until width * height) {
            val maskAlpha = Color.alpha(maskPixels[i]) / 255f
            val src = srcPixels[i]

            val r = (Color.red(src) * maskAlpha + 255 * (1f - maskAlpha)).toInt().coerceIn(0, 255)
            val g = (Color.green(src) * maskAlpha + 255 * (1f - maskAlpha)).toInt().coerceIn(0, 255)
            val b = (Color.blue(src) * maskAlpha + 255 * (1f - maskAlpha)).toInt().coerceIn(0, 255)

            outPixels[i] = Color.rgb(r, g, b)
        }

        output.setPixels(outPixels, 0, width, 0, 0, width, height)
        return output
    }

    private fun smoothStep(edge0: Float, edge1: Float, x: Float): Float {
        val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0f, 1f)
        return t * t * (3f - 2f * t)
    }

    private fun createFullMask(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.WHITE)
        return bitmap
    }
}
