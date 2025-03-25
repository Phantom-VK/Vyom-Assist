package com.swag.vyom.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat


@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
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
