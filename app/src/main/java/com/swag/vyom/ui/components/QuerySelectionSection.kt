package com.swag.vyom.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swag.vyom.ui.theme.AppRed

@Composable
fun QuerySelectionSection(
    onCategorySelected: (String) -> Unit,
    onSubCategorySelected: (String, String) -> Unit
) {
    // State to track the selected category
    var selectedCategory by remember { mutableStateOf("") }

    Text(
        modifier = Modifier.padding(start = 16.dp, top = 20.dp),
        text = "Query Selection",
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = AppRed
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Main categories dropdown with bank-specific categories
        val mainCategories = listOf(
            "Account Services",
            "Loans & Credit",
            "Digital Banking",
            "Card Services",
            "Payments & Transfers",
            "Investment & Wealth",
            "Customer Support",
            "IT Support",
            "Finance",
            "HR",
            "Branch Operations"
        )

        CustomDropdown(
            placeHolder = "Category",
            options = mainCategories,
            onOptionSelected = { category ->
                selectedCategory = category
                onCategorySelected(category)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subcategories that change based on selected main category
        val subCategories = when (selectedCategory) {
            "Account Services" -> listOf(
                "Account Opening",
                "Account Closure",
                "Balance Inquiry",
                "Statement Request",
                "KYC Update",
                "Nominee Registration",
                "Cheque Book Request"
            )

            "Loans & Credit" -> listOf(
                "Personal Loan",
                "Home Loan",
                "Car Loan",
                "Education Loan",
                "Business Loan",
                "Loan Statement",
                "EMI Inquiry",
                "Interest Rate Query"
            )

            "Digital Banking" -> listOf(
                "Mobile Banking Issues",
                "Internet Banking Access",
                "UPI Problems",
                "Password Reset",
                "Failed Transactions",
                "App Installation",
                "Security Concerns"
            )

            "Card Services" -> listOf(
                "Credit Card Application",
                "Debit Card Issues",
                "Card Activation",
                "Card Blocking",
                "PIN Generation",
                "Transaction Dispute",
                "Reward Points"
            )

            "Payments & Transfers" -> listOf(
                "NEFT Transfer",
                "RTGS Payment",
                "IMPS Issues",
                "International Transfer",
                "Recurring Payments",
                "Bill Payments",
                "Standing Instructions"
            )

            "Investment & Wealth" -> listOf(
                "Fixed Deposit",
                "Recurring Deposit",
                "Mutual Funds",
                "Insurance Products",
                "Tax-Saving Schemes",
                "Portfolio Review",
                "Investment Advisory"
            )

            "Customer Support" -> listOf(
                "Complaints",
                "Feedback",
                "Service Quality",
                "Branch Information",
                "Document Collection",
                "Service Charges Query"
            )

            "IT Support" -> listOf(
                "Technical Issue",
                "Software Request",
                "Access Rights",
                "Hardware Problem",
                "Network Connectivity",
                "System Outage"
            )

            "Finance" -> listOf(
                "Salary Processing",
                "Reimbursement",
                "Budget Allocation",
                "Expense Tracking",
                "Financial Reporting",
                "Audit Support"
            )

            "HR" -> listOf(
                "Employee Onboarding",
                "Leave Management",
                "Performance Review",
                "Training Request",
                "Benefits Inquiry",
                "ID Card Issue"
            )

            "Branch Operations" -> listOf(
                "Cash Management",
                "Vault Operations",
                "Teller Support",
                "Queue Management",
                "Branch Security",
                "Document Processing"
            )

            else -> listOf(
                "Select a category first"
            )
        }

        CustomDropdown(
            placeHolder = "Sub Category",
            options = subCategories,
            onOptionSelected = { subCategory ->
                onSubCategorySelected(selectedCategory, subCategory)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))


    }
}