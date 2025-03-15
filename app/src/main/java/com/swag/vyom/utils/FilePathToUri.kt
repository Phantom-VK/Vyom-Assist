package com.swag.vyom.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun getFilePathFromUri(context: Context, uri: Uri): String? {
    var filePath: String? = null

    if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        // Content URI
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameColumnIndex != -1) {
                    val displayName = it.getString(displayNameColumnIndex)
                    val file = File(context.cacheDir, displayName)
                    try {
                        val inputStream = contentResolver.openInputStream(uri)
                        inputStream?.use { input ->
                            FileOutputStream(file).use { output ->
                                input.copyTo(output)
                            }
                        }
                        filePath = file.absolutePath
                    } catch (e: IOException) {
                        Log.e("FilePath", "Error creating file from URI: ${e.message}")
                    }
                }
            }
        }
    } else if (uri.scheme == ContentResolver.SCHEME_FILE) {
        // File URI
        filePath = uri.path
    }
    return filePath
}