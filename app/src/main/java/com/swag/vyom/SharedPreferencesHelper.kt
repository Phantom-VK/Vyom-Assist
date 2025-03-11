package com.swag.vyom

import android.content.Context
import com.swag.vyom.dataclasses.UserDetails
import androidx.core.content.edit

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Save UserDetails to SharedPreferences
    fun saveUserDetails(userDetails: UserDetails) {
        with(sharedPreferences.edit()) {
            putInt("id", userDetails.id)
            putString("mobile_number", userDetails.mobile_number)
            putString("aadhaar", userDetails.aadhaar)
            putString("account_number", userDetails.account_number)
            putString("first_name", userDetails.first_name)
            putString("last_name", userDetails.last_name)
            apply()
        }
    }

    // Retrieve UserDetails from SharedPreferences
    fun getUserDetails(): UserDetails? {
        val id = sharedPreferences.getInt("id", -1)
        if (id == -1) return null // No data found

        return UserDetails(
            id = id,
            mobile_number = sharedPreferences.getString("mobile_number", "") ?: "",
            aadhaar = sharedPreferences.getString("aadhaar", "") ?: "",
            account_number = sharedPreferences.getString("account_number", "") ?: "",
            first_name = sharedPreferences.getString("first_name", "") ?: "",
            last_name = sharedPreferences.getString("last_name", "") ?: ""
        )
    }

    // Clear all user data from SharedPreferences
    fun clearUserDetails() {
        sharedPreferences.edit() { clear() }
    }
}