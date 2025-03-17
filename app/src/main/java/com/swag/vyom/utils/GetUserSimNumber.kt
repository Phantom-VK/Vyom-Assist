package com.swag.vyom.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.swag.vyom.ui.components.CustomEditText

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SimNumbersScreen() {
    val context = LocalContext.current
    var simNumbers by remember { mutableStateOf<List<String>>(emptyList()) }
    var permissionGranted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Create permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            permissionGranted = isGranted
            if (isGranted) {
                isLoading = true
                simNumbers = getUserSimNumber(context)
                isLoading = false
            }
        }
    )

    // Check permission status when the screen loads
    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_NUMBERS
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            permissionGranted = true
            isLoading = true
            simNumbers = getUserSimNumber(context)
            isLoading = false
        } else {
            permissionLauncher.launch(Manifest.permission.READ_PHONE_NUMBERS)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            permissionGranted -> {
                if (simNumbers.isEmpty()) {
                    simNumbers = listOf("No SIM number found")
                }

                simNumbers.forEachIndexed { index, number ->

                        CustomEditText(
                            value = number,
                            onValueChange = {  },
                            label = "Mobile Number"
                        )


                    if (index < simNumbers.size - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            else -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Permission Required",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please grant permission to access your phone number",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                permissionLauncher.launch(Manifest.permission.READ_PHONE_NUMBERS)
                            }
                        ) {
                            Text("Grant Permission")
                        }
                    }
                }
            }
        }
    }
}

fun getUserSimNumber(context: Context): List<String> {
    val numbers = mutableListOf<String>()

    try {
        // Try SubscriptionManager for multi-SIM support
        val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED) {
            val subscriptionInfoList = subscriptionManager?.activeSubscriptionInfoList

            subscriptionInfoList?.forEach { info ->
                val number = info.number
                if (!number.isNullOrEmpty()) {
                    numbers.add(number)
                }
            }
        }

        // Fallback: Try TelephonyManager for single SIM or older devices
        if (numbers.isEmpty()) {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED) {
                val lineNumber = telephonyManager.line1Number
                if (!lineNumber.isNullOrEmpty()) {
                    numbers.add(lineNumber)
                }
            }
        }
    } catch (e: Exception) {
        // Log the exception or handle it appropriately
    }

    return numbers
}

@Preview(showBackground = true)
@Composable
fun UserNumberPreview(){
    SimNumbersScreen()
}