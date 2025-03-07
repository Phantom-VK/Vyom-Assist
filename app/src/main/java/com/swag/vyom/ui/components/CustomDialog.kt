package com.swag.vyom.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swag.vyom.ui.theme.AppRed

@Composable
fun CustomDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: (() -> Unit)? = null,
    confirmButtonText: String = "OK",
    dismissButtonText: String = "Cancel",
    showCancelButton: Boolean = true
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Text(text = message, fontSize = 14.sp, textAlign = TextAlign.Center)
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm?.invoke()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppRed)
            ) {
                Text(text = confirmButtonText, color = Color.White)
            }
        },
        dismissButton = {
            if (showCancelButton) {
                OutlinedButton(onClick = onDismiss) {
                    Text(text = dismissButtonText)
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(12.dp)
    )
}
