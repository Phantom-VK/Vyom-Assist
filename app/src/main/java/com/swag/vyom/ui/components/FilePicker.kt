package com.swag.vyom.ui.components

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import java.io.InputStream

@Composable
fun FilePickerDialog(
    onFileSelected: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val maxFileSizeInBytes = 5 * 1024 * 1024 // 5MB

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            if (uri != null) {
                val fileSize = getFileSize(uri, context)
                if (fileSize != null && fileSize <= maxFileSizeInBytes) {
                    onFileSelected(uri)
                } else {
                    Toast.makeText(context, "File size exceeds 5MB limit.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        onDismiss()
    }

    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "*/*" // Allow all file types
        addCategory(Intent.CATEGORY_OPENABLE)
    }
    //Use LaunchedEffect to launch only one time
    LaunchedEffect(key1 = Unit){
        filePickerLauncher.launch(intent)
    }
}

private fun getFileSize(uri: Uri, context: android.content.Context): Long? {
    var fileSize: Long? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1 && !it.isNull(sizeIndex)) {
                    fileSize = it.getLong(sizeIndex)
                }
            }
        }
    } else if (uri.scheme == "file") {
        fileSize = java.io.File(uri.path ?: "").length()
    }
    return fileSize
}