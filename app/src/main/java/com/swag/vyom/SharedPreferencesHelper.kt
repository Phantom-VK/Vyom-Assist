package com.swag.vyom

import android.content.Context
import android.util.Log
import com.swag.vyom.dataclasses.UserDetails
import androidx.core.content.edit
import com.swag.vyom.dataclasses.AccountType
import com.swag.vyom.dataclasses.RiskProfile

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveaadhaar(aadhaar: String) {
        sharedPreferences.edit {
            putString("aadhaar", aadhaar)
        }

    }

    fun getaadhaar(): String? {
        return sharedPreferences.getString("aadhaar", null)
    }

    fun getmobile(): String? {
        return sharedPreferences.getString("mobile_number", null)
    }
    fun savemobile(mobile: String) {
        sharedPreferences.edit {
            putString("mobile_number", mobile)
        }

    }
    fun saveid(id: Int) {
        sharedPreferences.edit {
            putInt("id", id)
        }

    }
    fun getid(): Int? {
        val id = sharedPreferences.getInt("id", 0)
        return if(id != 0) id else null
    }

    fun getUserImageLink(): String? {
        return sharedPreferences.getString("image_link", null)

    }

    // Save UserDetails to SharedPreferences
    fun saveUserDetails(userDetails: UserDetails?) {
        if(userDetails != null) {
            with(sharedPreferences.edit()) {
                putInt("id", userDetails.id)
                putString("mobile_number", userDetails.mobile_number)
                putString("aadhaar", userDetails.aadhaar)
                putString("account_number", userDetails.account_number)
                putString("first_name", userDetails.first_name)
                putString("last_name", userDetails.last_name)
                putString("email", userDetails.email)
                putString("address", userDetails.address)
                putString("cibil_score", userDetails.cibil_score?.toString())
                putString("total_assets", userDetails.total_assets?.toString())
                putString("risk_profile", userDetails.risk_profile.toString())
                putString("last_transaction_date", userDetails.last_transaction_date)
                putString(
                    "total_transactions_count",
                    userDetails.total_transactions_count?.toString()
                )
                putString("last_ticket_id", userDetails.last_ticket_id)
                putString("gender", userDetails.gender)
                putString("active_loan", userDetails.active_loan?.toString())
                putString("age", userDetails.age.toString())
                putString("image_link", userDetails.image_link)
                putString("account_type", userDetails.account_type.toString())


                apply()
            }
        }else{
            Log.e("AuthViewModel", "UserDetails is null in authvm")
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
                "Savings" -> AccountType.Savings
                "Current" -> AccountType.Current
                "Salary" -> AccountType.Salary
                "Fixed Deposit" -> AccountType.Fixed_Deposit
                else -> AccountType.UNKNOWN
            },
            email = sharedPreferences.getString("email", null).toString(),
            address = sharedPreferences.getString("address", null),
            cibil_score = sharedPreferences.getString("cibil_score", null)?.toIntOrNull(),
            total_assets = sharedPreferences.getString("total_assets", null)?.toDoubleOrNull(),
            risk_profile = when (sharedPreferences.getString("risk_profile", "UNKNOWN")) {
                "Low" -> RiskProfile.Low
                "Medium" -> RiskProfile.Medium
                "High" -> RiskProfile.High
                else -> RiskProfile.Low
            },
            last_transaction_date = sharedPreferences.getString("last_transaction_date", null),
            total_transactions_count = sharedPreferences.getString("total_transactions_count", null)
                ?.toIntOrNull(),
            last_ticket_id = sharedPreferences.getString("last_ticket_id", null),
            gender = sharedPreferences.getString("gender", null),
            active_loan = sharedPreferences.getString("active_loan", null)?.toDoubleOrNull(),
            age = sharedPreferences.getString("age", null)?.toIntOrNull() ?: 0,
            image_link = sharedPreferences.getString("image_link", "") ?: ""
        )
    }

    // Clear all user data from SharedPreferences
    fun clearUserDetails() {
        sharedPreferences.edit() { clear() }
    }
}