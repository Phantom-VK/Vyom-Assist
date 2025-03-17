package com.swag.vyom.utils

import android.content.SharedPreferences
import com.swag.vyom.dataclasses.Product
import com.swag.vyom.dataclasses.RiskProfile

fun getRecommendedProducts(sharedPreferences: SharedPreferences): List<Product> {
    val cibilScore = sharedPreferences.getString("cibil_score", null)?.toIntOrNull()
    val age = sharedPreferences.getString("age", null)?.toIntOrNull()
    val  riskProfile = when (sharedPreferences.getString("risk_profile", "UNKNOWN")) {
        "Low" -> RiskProfile.Low
        "Medium" -> RiskProfile.Medium
        "High" -> RiskProfile.High
        else -> RiskProfile.Low
    }
    val totalAssets = sharedPreferences.getString("total_assets", null)?.toDoubleOrNull()
    val activeLoan = sharedPreferences.getString("active_loan", null)?.toDoubleOrNull()
    val accountType = sharedPreferences.getString("account_type", null)

    val recommendedProducts = mutableListOf<Product>()

    // Rule 1: Based on CIBIL Score
    when (cibilScore) {
        in 750..900 -> {
            recommendedProducts.add(
                Product(
                    id = 1,
                    name = "Premium Credit Card",
                    category = "Credit Card",
                    description = "Enjoy exclusive rewards and cashback.",
                    eligibilityCriteria = "CIBIL Score > 750",
                    interestRate = null,
                    benefits = listOf("5% cashback on dining", "10x reward points on travel")
                )
            )
            recommendedProducts.add(
                Product(
                    id = 2,
                    name = "Personal Loan",
                    category = "Loan",
                    description = "Get instant personal loans at low interest rates.",
                    eligibilityCriteria = "CIBIL Score > 750",
                    interestRate = 9.5
                )
            )
        }
        in 600..749 -> {
            recommendedProducts.add(
                Product(
                    id = 3,
                    name = "Gold Loan",
                    category = "Loan",
                    description = "Get a loan against your gold assets.",
                    eligibilityCriteria = "CIBIL Score > 600",
                    interestRate = 12.0
                )
            )
        }
        else -> {
            recommendedProducts.add(
                Product(
                    id = 4,
                    name = "Secured Credit Card",
                    category = "Credit Card",
                    description = "Build your credit score with a secured card.",
                    eligibilityCriteria = "CIBIL Score < 600",
                    interestRate = null
                )
            )
        }
    }

    // Rule 2: Based on Age
    when (age) {
        in 18..25 -> {
            recommendedProducts.add(
                Product(
                    id = 5,
                    name = "Education Loan",
                    category = "Loan",
                    description = "Finance your education with low-interest loans.",
                    eligibilityCriteria = "Age 18-25",
                    interestRate = 8.5
                )
            )
        }
        in 26..50 -> {
            recommendedProducts.add(
                Product(
                    id = 6,
                    name = "Home Loan",
                    category = "Loan",
                    description = "Buy your dream home with flexible repayment options.",
                    eligibilityCriteria = "Age 26-50",
                    interestRate = 7.5
                )
            )
        }
        in 51..100 -> {
            recommendedProducts.add(
                Product(
                    id = 7,
                    name = "Fixed Deposit",
                    category = "Savings",
                    description = "Earn high interest with a fixed deposit.",
                    eligibilityCriteria = "Age 50+",
                    interestRate = 6.5
                )
            )
        }
    }

    // Rule 3: Based on Risk Profile
    when (riskProfile) {
        RiskProfile.Low  -> {
            recommendedProducts.add(
                Product(
                    id = 8,
                    name = "Recurring Deposit",
                    category = "Savings",
                    description = "Save regularly and earn high interest.",
                    eligibilityCriteria = "Low Risk Profile",
                    interestRate = 6.0
                )
            )
        }
        RiskProfile.Medium -> {
            recommendedProducts.add(
                Product(
                    id = 9,
                    name = "Balanced Mutual Fund",
                    category = "Investment",
                    description = "Invest in a mix of equity and debt for steady returns.",
                    eligibilityCriteria = "Medium Risk Profile",
                    interestRate = null
                )
            )
        }
        RiskProfile.High  -> {
            recommendedProducts.add(
                Product(
                    id = 10,
                    name = "Equity Fund",
                    category = "Investment",
                    description = "Invest in high-growth stocks for maximum returns.",
                    eligibilityCriteria = "High Risk Profile",
                    interestRate = null
                )
            )
        }
//Allocated high to nnull
        null -> {
            recommendedProducts.add(
                Product(
                    id = 10,
                    name = "Equity Fund",
                    category = "Investment",
                    description = "Invest in high-growth stocks for maximum returns.",
                    eligibilityCriteria = "High Risk Profile",
                    interestRate = null
                )
            )
        }
    }

    // Rule 4: Based on Total Assets
    if (totalAssets != null && totalAssets > 1000000) {
        recommendedProducts.add(
            Product(
                id = 11,
                name = "Wealth Management",
                category = "Service",
                description = "Get personalized financial advice and investment solutions.",
                eligibilityCriteria = "Total Assets > ₹10,00,000",
                interestRate = null
            )
        )
    }

    // Rule 5: Based on Active Loan
    if (activeLoan != null && activeLoan > 0) {
        recommendedProducts.add(
            Product(
                id = 12,
                name = "Loan Top-Up",
                category = "Loan",
                description = "Increase your loan amount with a top-up.",
                eligibilityCriteria = "Active Loan",
                interestRate = 10.0
            )
        )
    }

    return recommendedProducts
}