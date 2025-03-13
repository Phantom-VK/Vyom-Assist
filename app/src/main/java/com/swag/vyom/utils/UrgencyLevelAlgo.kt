package com.swag.vyom.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.swag.vyom.dataclasses.UrgencyLevel
import java.time.DayOfWeek
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
fun calculateUrgencyLevel(
    category: String,
    subCategory: String,
    timeOfRequest: String
): UrgencyLevel {

    val timeOfRequest = LocalDateTime.parse(timeOfRequest)

    // Base score calculation system (0-100 scale)
    var urgencyScore = 0

    // 1. Category weighting (contributes up to 40 points)
    val categoryScore = when (category) {
        "Digital Banking" -> 35
        "Card Services" -> 30
        "IT Support" -> 30
        "Account Services" -> 25
        "Payments & Transfers" -> 25
        "Loans & Credit" -> 20
        "Branch Operations" -> 20
        "Investment & Wealth" -> 15
        "Finance" -> 15
        "Customer Support" -> 10
        "HR" -> 10
        else -> 15
    }

    // 2. Subcategory multiplier (adjusts the category score)
    val subCategoryMultiplier = when {
        // Critical subcategories
        subCategory == "System Outage" -> 1.3
        subCategory == "Security Concerns" -> 1.25
        subCategory == "Transaction Dispute" -> 1.2
        subCategory == "Card Blocking" -> 1.2
        subCategory == "Failed Transactions" -> 1.15
        subCategory == "Password Reset" -> 1.1
        subCategory == "Network Connectivity" -> 1.15
        subCategory == "UPI Problems" -> 1.1

        // Pattern-based critical issues
        subCategory.contains("Security") -> 1.2
        subCategory.contains("Outage") -> 1.2
        subCategory.contains("Failed") -> 1.15
        subCategory.contains("Fraud") -> 1.2
        subCategory.contains("Blocking") -> 1.15
        subCategory.contains("Dispute") -> 1.1
        subCategory.contains("Access") -> 1.1
        subCategory.contains("Password") -> 1.05

        // Low urgency subcategories
        subCategory == "Feedback" -> 0.6
        subCategory == "Training Request" -> 0.7
        subCategory == "Balance Inquiry" -> 0.8
        subCategory == "EMI Inquiry" -> 0.85
        subCategory == "Statement Request" -> 0.75
        subCategory == "Cheque Book Request" -> 0.8

        // Pattern-based low urgency issues
        subCategory.contains("Information") -> 0.8
        subCategory.contains("Feedback") -> 0.7
        subCategory.contains("Request") -> 0.9
        subCategory.contains("Training") -> 0.7
        subCategory.contains("Advisory") -> 0.8
        subCategory.contains("Inquiry") -> 0.85

        else -> 1.0
    }

    // Apply category and subcategory score
    urgencyScore += (categoryScore * subCategoryMultiplier).toInt()

    // 3. Time-based factors (contributes up to 60 points to compensate for removed parameters)
    val hour = timeOfRequest.hour
    val dayOfWeek = timeOfRequest.dayOfWeek
    val dayOfMonth = timeOfRequest.dayOfMonth
    val monthLength = timeOfRequest.month.length(timeOfRequest.toLocalDate().isLeapYear)

    // Banking hours factor (more weight now)
    val isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
    val isDuringBankingHours = hour in 9..17 && !isWeekend

    if (!isDuringBankingHours) {
        // Outside banking hours gets higher urgency (increased from original)
        urgencyScore += 20
    }

    // Critical timing factors (increased weights)
    // End-of-day requests that might affect overnight processing
    if (hour in 16..18 && !isWeekend) {
        urgencyScore += 15
    }

    // Month-end financial closing period (last 3 days of month)
    if (dayOfMonth >= monthLength - 2) {
        urgencyScore += 15
    }

    // Weekend factor (increased weight)
    if (isWeekend) {
        urgencyScore += 12
    }

    // Early morning banking preparation time
    if (hour in 6..8 && !isWeekend) {
        urgencyScore += 10
    }

    // Special banking days consideration - month start (increased from 0)
    if (dayOfMonth <= 3 && !isWeekend) {
        // First few days of month often have higher transaction volumes
        urgencyScore += 8
    }

    // End of fiscal quarter consideration (Mar, Jun, Sep, Dec)
    val fiscalQuarterEndMonths = listOf(3, 6, 9, 12)
    if (fiscalQuarterEndMonths.contains(timeOfRequest.monthValue) && dayOfMonth >= monthLength - 5) {
        urgencyScore += 10
    }

    // Special case overrides for critical combinations
    if ((category == "Digital Banking" && subCategory == "Security Concerns") ||
        (category == "IT Support" && subCategory == "System Outage") ||
        (category == "Card Services" && subCategory == "Card Blocking") ||
        (category == "Digital Banking" && subCategory.contains("Fraud")) ||
        (category == "Payments & Transfers" && subCategory == "Failed Transactions" && !isDuringBankingHours)
    ) {
        return UrgencyLevel.Critical
    }

    // Convert final score to UrgencyLevel
    // Adjusted thresholds to account for the removal of other parameters
    return when {
        urgencyScore >= 65 -> UrgencyLevel.Critical
        urgencyScore >= 45 -> UrgencyLevel.High
        urgencyScore >= 25 -> UrgencyLevel.Medium
        else -> UrgencyLevel.Low
    }
}

