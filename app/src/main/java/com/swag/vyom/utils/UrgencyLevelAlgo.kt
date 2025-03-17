package com.swag.vyom.utils

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.swag.vyom.dataclasses.UrgencyLevel
import java.time.DayOfWeek
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
fun calculateUrgencyLevel(
    category: String,
    subCategory: String,
    timeOfRequest: String,
    userPrefs: SharedPreferences? = null
): UrgencyLevel {

    val parsedTimeOfRequest = LocalDateTime.parse(timeOfRequest)

    // Base score calculation system (0-100 scale)
    var urgencyScore = 0

    // 1. Category weighting (contributes up to 40 points)
    val categoryScore = when (category) {
        // High priority categories
        "Digital Banking" -> 38
        "Card Services" -> 35
        "IT Support" -> 35
        "Payments & Transfers" -> 33
        "Account Services" -> 30

        // Medium priority categories
        "Loans & Credit" -> 25
        "Branch Operations" -> 22
        "ATM Services" -> 22
        "Mobile Banking" -> 22
        "UPI Services" -> 22

        // Lower priority categories
        "Investment & Wealth" -> 18
        "Finance" -> 15
        "Customer Support" -> 15
        "HR" -> 12
        "Career" -> 10
        "Feedback" -> 8
        else -> 15
    }

    // 2. Subcategory multiplier (adjusts the category score)
    val subCategoryMultiplier = when {
        // Critical subcategories (increased multipliers for important issues)
        subCategory == "System Outage" -> 1.5
        subCategory == "Security Breach" -> 1.5
        subCategory == "Fraud Alert" -> 1.45
        subCategory == "Security Concerns" -> 1.4
        subCategory == "Transaction Dispute" -> 1.3
        subCategory == "Unauthorized Transaction" -> 1.3
        subCategory == "Card Blocking" -> 1.3
        subCategory == "Account Hacking" -> 1.3
        subCategory == "Failed Transactions" -> 1.25
        subCategory == "Fund Transfer Failure" -> 1.25
        subCategory == "Password Reset" -> 1.2
        subCategory == "Network Connectivity" -> 1.2
        subCategory == "UPI Problems" -> 1.2
        subCategory == "OTP Issues" -> 1.2
        subCategory == "Account Freeze" -> 1.2
        subCategory == "Login Problems" -> 1.15

        // Pattern-based critical issues
        subCategory.contains("Security") -> 1.3
        subCategory.contains("Outage") -> 1.35
        subCategory.contains("Failed") -> 1.25
        subCategory.contains("Fraud") -> 1.35
        subCategory.contains("Blocking") -> 1.25
        subCategory.contains("Dispute") -> 1.2
        subCategory.contains("Access") -> 1.15
        subCategory.contains("Password") -> 1.15
        subCategory.contains("Hacking") -> 1.4
        subCategory.contains("Unauthorized") -> 1.3
        subCategory.contains("Error") -> 1.15
        subCategory.contains("Failure") -> 1.2
        subCategory.contains("Urgent") -> 1.25
        subCategory.contains("Emergency") -> 1.4

        // Low urgency subcategories
        subCategory == "Feedback" -> 0.5
        subCategory == "Training Request" -> 0.6
        subCategory == "Balance Inquiry" -> 0.7
        subCategory == "EMI Inquiry" -> 0.75
        subCategory == "Statement Request" -> 0.7
        subCategory == "Cheque Book Request" -> 0.8
        subCategory == "Change of Address" -> 0.8
        subCategory == "Account Statement" -> 0.75
        subCategory == "Branch Timing" -> 0.6
        subCategory == "Interest Rate Inquiry" -> 0.7

        // Pattern-based low urgency issues
        subCategory.contains("Information") -> 0.7
        subCategory.contains("Feedback") -> 0.6
        subCategory.contains("Request") -> 0.8
        subCategory.contains("Training") -> 0.6
        subCategory.contains("Advisory") -> 0.7
        subCategory.contains("Inquiry") -> 0.75
        subCategory.contains("Schedule") -> 0.7
        subCategory.contains("Update") -> 0.8

        else -> 1.0
    }

    // Apply category and subcategory score
    urgencyScore += (categoryScore * subCategoryMultiplier).toInt()

    // 3. Time-based factors (contributes up to 60 points)
    val hour = parsedTimeOfRequest.hour
    val minute = parsedTimeOfRequest.minute
    val dayOfWeek = parsedTimeOfRequest.dayOfWeek
    val dayOfMonth = parsedTimeOfRequest.dayOfMonth
    val monthValue = parsedTimeOfRequest.monthValue
    val monthLength = parsedTimeOfRequest.month.length(parsedTimeOfRequest.toLocalDate().isLeapYear)

    // Banking hours factor
    val isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
    val isBankHoliday = isBankHoliday(parsedTimeOfRequest) // New helper function for bank holidays
    val morningBankingHours = hour in 9..12
    val afternoonBankingHours = hour in 13..17
    val isDuringBankingHours = (morningBankingHours || afternoonBankingHours) && !isWeekend && !isBankHoliday

    if (!isDuringBankingHours) {
        // Outside banking hours gets higher urgency
        urgencyScore += 25
    }

    // End-of-day requests that might affect overnight processing
    if (hour in 16..18 && !isWeekend) {
        urgencyScore += 18
    }

    // Month-end financial closing period (last 3 days of month)
    if (dayOfMonth >= monthLength - 2) {
        urgencyScore += 20
    }

    // Weekend factor (increased weight)
    if (isWeekend) {
        urgencyScore += 15
    }

    // Bank holiday factor (new)
    if (isBankHoliday) {
        urgencyScore += 18
    }

    // Early morning banking preparation time
    if (hour in 6..8 && !isWeekend) {
        urgencyScore += 12
    }

    // Peak banking hours (added)
    if ((hour in 10..11 || hour in 14..15) && !isWeekend) {
        urgencyScore += 8
    }

    // Special banking days consideration - month start
    if (dayOfMonth <= 3 && !isWeekend) {
        // First few days of month often have higher transaction volumes
        urgencyScore += 10
    }

    // End of fiscal quarter consideration (Mar, Jun, Sep, Dec)
    val fiscalQuarterEndMonths = listOf(3, 6, 9, 12)
    if (fiscalQuarterEndMonths.contains(monthValue) && dayOfMonth >= monthLength - 5) {
        urgencyScore += 15
    }

    // Salary days consideration (typically 1st and last week of month)
    if ((dayOfMonth <= 7 || dayOfMonth >= monthLength - 7) && !isWeekend) {
        urgencyScore += 10
    }

    // Tax payment deadline months (March, July, September, December)
    val taxDeadlineMonths = listOf(3, 7, 9, 12)
    if (taxDeadlineMonths.contains(monthValue) && dayOfMonth >= 15) {
        urgencyScore += 8
    }

    // GST filing dates (typically around 20th of month)
    if (dayOfMonth in 18..22) {
        urgencyScore += 8
    }

    // User history and preferences consideration (new)
    userPrefs?.let {
        val userPriority = it.getInt("user_priority", 0) // 0-5 scale
        val userComplaintHistory = it.getInt("complaint_count", 0)

        // Add score based on user priority level
        urgencyScore += userPriority * 3

        // Add score based on previous complaint history
        if (userComplaintHistory > 10) {
            urgencyScore += 10
        } else if (userComplaintHistory > 5) {
            urgencyScore += 5
        }
    }

    // Special case overrides for critical combinations
    if ((category == "Digital Banking" && subCategory == "Security Concerns") ||
        (category == "Digital Banking" && subCategory == "Security Breach") ||
        (category == "Digital Banking" && subCategory == "Fraud Alert") ||
        (category == "IT Support" && subCategory == "System Outage") ||
        (category == "Card Services" && subCategory == "Card Blocking") ||
        (category == "Card Services" && subCategory == "Unauthorized Transaction") ||
        (category == "Digital Banking" && subCategory.contains("Fraud")) ||
        (category == "Digital Banking" && subCategory.contains("Hack")) ||
        (category == "Account Services" && subCategory.contains("Unauthorized")) ||
        (category == "Payments & Transfers" && subCategory == "Failed Transactions" && !isDuringBankingHours) ||
        (category == "UPI Services" && subCategory.contains("Failed") && !isDuringBankingHours)
    ) {
        return UrgencyLevel.Critical
    }

    // Convert final score to UrgencyLevel
    return when {
        urgencyScore >= 70 -> UrgencyLevel.Critical
        urgencyScore >= 50 -> UrgencyLevel.High
        urgencyScore >= 30 -> UrgencyLevel.Medium
        else -> UrgencyLevel.Low
    }
}

/**
 * Helper function to check if a given date is a bank holiday
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun isBankHoliday(date: LocalDateTime): Boolean {
    val day = date.dayOfMonth
    val month = date.monthValue

    // Major national holidays in India
    val nationalHolidays = listOf(
        // Republic Day
        Pair(26, 1),
        // Independence Day
        Pair(15, 8),
        // Gandhi Jayanti
        Pair(2, 10),
        // Common bank holidays (sample dates, should be updated annually)
        Pair(1, 1),  // New Year
        Pair(15, 1), // Makar Sankranti
        Pair(26, 1), // Republic Day
        Pair(19, 2), // Shivaji Jayanti
        Pair(29, 3), // Holi
        Pair(14, 4), // Ambedkar Jayanti
        Pair(1, 5),  // May Day
        Pair(15, 8), // Independence Day
        Pair(19, 8), // Janmashtami
        Pair(10, 9), // Ganesh Chaturthi
        Pair(2, 10), // Gandhi Jayanti
        Pair(24, 10), // Dussehra
        Pair(12, 11), // Diwali
        Pair(25, 12)  // Christmas
    )

    return nationalHolidays.contains(Pair(day, month))
}

/**
 * Helper function to check if a user is a premium/priority customer
 * This can be expanded based on bank's criteria
 */
private fun isUserPriority(userPrefs: SharedPreferences?): Boolean {
    userPrefs?.let {
        val accountType = it.getString("account_type", "")
        val accountBalance = it.getFloat("account_balance", 0f)

        return accountType == "Premium" ||
                accountType == "Corporate" ||
                accountType == "Salary+" ||
                accountBalance > 100000f
    }
    return false
}