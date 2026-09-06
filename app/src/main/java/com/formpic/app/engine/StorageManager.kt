package com.formpic.app.engine

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Manages modern scoped storage saving, sharing via FileProvider, and cache hygiene.
 */
object StorageManager {

    private const val ALBUM_NAME = "FormPic"

    /**
     * Saves photo byte array directly into device Pictures/FormPic gallery.
     * Uses MediaStore with IS_PENDING on Android 10+ for zero storage permission friction.
     */
    fun saveImageToGallery(context: Context, imageBytes: ByteArray, prefix: String = "Passport"): Uri? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val filename = "FormPic_${prefix}_$timestamp.jpg"

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$ALBUM_NAME")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return null

        return try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(imageBytes)
                outputStream.flush()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            }
            uri
        } catch (e: Exception) {
            e.printStackTrace()
            // Clean up incomplete file
            resolver.delete(uri, null, null)
            null
        }
    }

    /**
     * Prepares an image for Android Share Intent using FileProvider.
     */
    fun createShareIntent(context: Context, imageBytes: ByteArray): Intent? {
        return try {
            val shareDir = File(context.cacheDir, "shared").apply { mkdirs() }
            val shareFile = File(shareDir, "FormPic_Shared.jpg")
            FileOutputStream(shareFile).use { fos ->
                fos.write(imageBytes)
                fos.flush()
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                shareFile
            )

            Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Cleans up temporary cache files to maintain device storage hygiene.
     */
    fun clearTemporaryCache(context: Context) {
        try {
            val sharedDir = File(context.cacheDir, "shared")
            if (sharedDir.exists()) sharedDir.deleteRecursively()

            val tempDir = File(context.cacheDir, "temp")
            if (tempDir.exists()) tempDir.deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
