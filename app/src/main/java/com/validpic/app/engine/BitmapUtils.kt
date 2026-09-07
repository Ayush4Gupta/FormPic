package com.validpic.app.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.InputStream
import kotlin.math.max

/**
 * Utility functions for memory-safe bitmap decoding, EXIF orientation correction,
 * and dimensional scaling.
 */
object BitmapUtils {

    private const val MAX_DECODE_DIMENSION = 2048 // Prevents OutOfMemory on 50MP/108MP photos

    /**
     * Safely decodes a bitmap from Uri with automatic inSampleSize downsampling
     * and EXIF orientation correction.
     */
    fun decodeSampledBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        var inputStream: InputStream? = null
        return try {
            // 1. Measure dimensions without loading pixels into memory
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            if (options.outWidth <= 0 || options.outHeight <= 0) return null

            // 2. Calculate safe inSampleSize
            options.inSampleSize = calculateInSampleSize(options, MAX_DECODE_DIMENSION, MAX_DECODE_DIMENSION)
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            // 3. Decode scaled bitmap
            inputStream = context.contentResolver.openInputStream(uri)
            val decodedBitmap = BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            if (decodedBitmap == null) return null

            // 4. Correct EXIF rotation
            val rotationDegrees = getExifOrientationDegrees(context, uri)
            if (rotationDegrees != 0) {
                rotateBitmap(decodedBitmap, rotationDegrees)
            } else {
                decodedBitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            inputStream?.close()
        }
    }

    /**
     * Reads EXIF orientation degrees from Uri.
     */
    private fun getExifOrientationDegrees(context: Context, uri: Uri): Int {
        var inputStream: InputStream? = null
        return try {
            inputStream = context.contentResolver.openInputStream(uri) ?: return 0
            val exif = ExifInterface(inputStream)
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            0
        } finally {
            inputStream?.close()
        }
    }

    /**
     * Rotates bitmap by given degrees.
     */
    fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        if (degrees == 0) return bitmap
        val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated != bitmap) {
            bitmap.recycle()
        }
        return rotated
    }

    /**
     * Computes the power-of-two inSampleSize.
     */
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return max(1, inSampleSize)
    }

    /**
     * Crops bitmap to specified rectangular bounds safely without going out of image bounds.
     */
    fun cropBitmap(source: Bitmap, left: Int, top: Int, width: Int, height: Int): Bitmap {
        val safeLeft = left.coerceIn(0, source.width - 1)
        val safeTop = top.coerceIn(0, source.height - 1)
        val safeWidth = width.coerceIn(1, source.width - safeLeft)
        val safeHeight = height.coerceIn(1, source.height - safeTop)

        return Bitmap.createBitmap(source, safeLeft, safeTop, safeWidth, safeHeight)
    }

    /**
     * Scales bitmap to specified target width and height with bilinear filtering.
     */
    fun scaleBitmap(source: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        if (source.width == targetWidth && source.height == targetHeight) return source
        return Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)
    }
}
