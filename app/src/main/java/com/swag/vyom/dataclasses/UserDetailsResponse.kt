package com.swag.vyom.dataclasses



data class UserDetailsResponse(
    val success: Boolean,
    val msg: String,
    val data: UserDetails?
)

data class UserDetails(
    val id: Int,
    val mobileNumber: String,
    val aadhaar: String,
    val accountNumber: String,
    val accountType: AccountType, // Enum for account types
    val firstName: String,
    val lastName: String,
    val email: String?,
    val address: String?,
    val cibilScore: Int?,
    val totalAssets: Double?,
    val riskProfile: RiskProfile, // Enum for risk profile
    val lastTransactionDate: String?,
    val totalTransactionsCount: Int?,
    val lastTicketId: String?
)

// Enum for account types
enum class AccountType {
    SAVINGS, CURRENT, BUSINESS, FIXED_DEPOSIT, RECURRING_DEPOSIT, UNKNOWN
}

// Enum for risk profile
enum class RiskProfile {
    LOW, MEDIUM, HIGH, UNKNOWN
}


