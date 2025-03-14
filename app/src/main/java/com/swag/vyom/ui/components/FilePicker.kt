package com.swag.vyom.ui.components

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun FilePickerDialog(
    onFileSelected: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    var shouldLaunchFilePicker by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                onFileSelected(uri)
            }
        }
        onDismiss()
        shouldLaunchFilePicker = false // Reset the flag
    }
    // Launch the file picker
    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "*/*" // Allow all file types
        addCategory(Intent.CATEGORY_OPENABLE)
    }

    if (shouldLaunchFilePicker) {
        filePickerLauncher.launch(intent)
    }

    // Button to trigger the file picker
    Button(onClick = {
        shouldLaunchFilePicker = true
    }) {
        Text("Open File Picker")
    }
}