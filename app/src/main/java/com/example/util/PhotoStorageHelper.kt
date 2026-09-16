package com.example.util

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object PhotoStorageHelper {
    private const val TAG = "PhotoStorageHelper"

    /**
     * Copies an image from a content/file Uri to the app's internal private storage,
     * ensuring it remains permanently accessible across app restarts, reboots, and permission revocations.
     *
     * @return Absolute file path on the local filesystem (e.g. /data/user/0/.../files/profile_avatar/user_avatar_12345.jpg)
     */
    fun saveImageToInternalStorage(
        context: Context,
        sourceUri: Uri,
        directoryName: String = "profile_avatar",
        prefix: String = "user_avatar"
    ): String {
        return try {
            val dir = File(context.filesDir, directoryName)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val destFile = File(dir, "${prefix}_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return sourceUri.toString()

            Log.d(TAG, "Successfully saved photo to permanent storage: ${destFile.absolutePath}")
            destFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy image to internal storage", e)
            sourceUri.toString()
        }
    }
}
