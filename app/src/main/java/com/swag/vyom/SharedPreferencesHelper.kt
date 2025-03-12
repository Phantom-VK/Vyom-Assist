package com.swag.vyom

import android.content.Context
import com.swag.vyom.dataclasses.UserDetails
import androidx.core.content.edit
import com.swag.vyom.dataclasses.AccountType
import com.swag.vyom.dataclasses.RiskProfile

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
            last_name = sharedPreferences.getString("last_name", "") ?: "",
            account_type = when (sharedPreferences.getString("account_type", "UNKNOWN")) {
                "SAVINGS" -> AccountType.SAVINGS
                "CURRENT" -> AccountType.CURRENT
                "BUSINESS" -> AccountType.BUSINESS
                "FIXED_DEPOSIT" -> AccountType.FIXED_DEPOSIT
                "RECURRING_DEPOSIT" -> AccountType.RECURRING_DEPOSIT
                else -> AccountType.UNKNOWN
            },
            email = sharedPreferences.getString("email", null),
            address = sharedPreferences.getString("address", null),
            cibil_score = sharedPreferences.getString("cibil_score", null)?.toIntOrNull(),
            total_assets = sharedPreferences.getString("total_assets", null)?.toDoubleOrNull(),
            risk_profile = when (sharedPreferences.getString("risk_profile", "UNKNOWN")) {
                "LOW" -> RiskProfile.LOW
                "MEDIUM" -> RiskProfile.MEDIUM
                "HIGH" -> RiskProfile.HIGH
                else -> RiskProfile.UNKNOWN
            },
            last_transaction_date = sharedPreferences.getString("last_transaction_date", null),
            total_transactions_count = sharedPreferences.getString("total_transactions_count", null)?.toIntOrNull(),
            last_ticket_id = sharedPreferences.getString("last_ticket_id", null),
            gender = sharedPreferences.getString("gender", null),
            active_loan = sharedPreferences.getString("active_loan", null)?.toDoubleOrNull(),
            age = sharedPreferences.getString("age", null)?.toIntOrNull() ?: 0
        )
    }

    // Clear all user data from SharedPreferences
    fun clearUserDetails() {
        sharedPreferences.edit() { clear() }
    }
}