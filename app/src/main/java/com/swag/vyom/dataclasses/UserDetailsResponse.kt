package com.swag.vyom.dataclasses



data class UserDetailsResponse(
    val success: Boolean,
    val msg: String,
    val data: UserDetails?
)

data class UserDetails(
    val id: Int,
    val mobile_number: String,
    val aadhaar: String,
    val account_number: String,
    val account_type: AccountType, // Enum for account types
    val first_name: String,
    val image_link: String,
    val last_name: String,
    val email: String?,
    val address: String?,
    val cibil_score: Int?,
    val total_assets: Double?,
    val risk_profile: RiskProfile, // Enum for risk profile
    val last_transaction_date: String?,
    val total_transactions_count: Int?,
    val last_ticket_id: String?,
    val gender: String?,
    val active_loan: Double?,
    val age:Int
)

// Enum for account types
enum class AccountType {
    Savings, Current, Salary, FIXED_DEPOSIT, UNKNOWN
}
// Enum for risk profile
enum class RiskProfile {
    Low, Medium, High
}


