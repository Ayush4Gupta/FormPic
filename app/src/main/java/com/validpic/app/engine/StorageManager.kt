package com.validpic.app.engine

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
 * Storage destination presets for candidate convenience.
 */
enum class SaveFolderOption(
    val id: String,
    val title: String,
    val directoryType: String,
    val relativeSubpath: String,
    val description: String
) {
    PICTURES(
        id = "pictures",
        title = "Pictures / ValidPic",
        directoryType = Environment.DIRECTORY_PICTURES,
        relativeSubpath = "Pictures/ValidPic",
        description = "Standard gallery location — visible in Google Photos, Gallery & Camera apps"
    ),
    DOWNLOADS(
        id = "downloads",
        title = "Downloads / ValidPic",
        directoryType = Environment.DIRECTORY_DOWNLOADS,
        relativeSubpath = "Download/ValidPic",
        description = "Recommended for exam portals — easiest to locate when uploading in browser (SSC, UPSC)"
    ),
    DCIM(
        id = "dcim",
        title = "DCIM / ValidPic",
        directoryType = Environment.DIRECTORY_DCIM,
        relativeSubpath = "DCIM/ValidPic",
        description = "Camera roll album — saved alongside your device camera photos"
    )
}

/**
 * Manages modern scoped storage saving, sharing via FileProvider, and cache hygiene.
 */
object StorageManager {

    private const val ALBUM_NAME = "ValidPic"
    private const val PREFS_NAME = "validpic_storage_prefs"
    private const val KEY_SAVE_FOLDER = "key_save_folder"

    /**
     * Retrieves the candidate's preferred save location. Defaults to Pictures/ValidPic.
     */
    fun getSaveFolderOption(context: Context): SaveFolderOption {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedId = prefs.getString(KEY_SAVE_FOLDER, SaveFolderOption.PICTURES.id)
        return SaveFolderOption.values().firstOrNull { it.id == savedId } ?: SaveFolderOption.PICTURES
    }

    /**
     * Updates the candidate's preferred save location.
     */
    fun setSaveFolderOption(context: Context, option: SaveFolderOption) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SAVE_FOLDER, option.id).apply()
    }

    /**
     * Saves photo byte array directly into candidate's chosen directory.
     * Uses MediaStore with IS_PENDING on Android 10+ for zero storage permission friction.
     */
    fun saveImageToGallery(context: Context, imageBytes: ByteArray, prefix: String = "Passport"): Uri? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val filename = "ValidPic_${prefix}_$timestamp.jpg"
        val saveOption = getSaveFolderOption(context)

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "${saveOption.directoryType}/$ALBUM_NAME")
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
     * Opens the device gallery or files viewer to easily access saved ValidPic files.
     */
    fun openSavedPhotosFolder(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val fallbackIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*"
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fallbackIntent)
            } catch (e2: Exception) {
                // Device does not have an activity capable of handling
            }
        }
    }

    /**
     * Prepares an image for Android Share Intent using FileProvider.
     */
    fun createShareIntent(context: Context, imageBytes: ByteArray): Intent? {
        return try {
            val shareDir = File(context.cacheDir, "shared").apply { mkdirs() }
            val shareFile = File(shareDir, "ValidPic_Shared.jpg")
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
