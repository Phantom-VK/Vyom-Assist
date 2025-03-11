package com.swag.vyom.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swag.vyom.dataclasses.Ticket

@Composable
fun SupportTicketItem(
    ticketId: String,
    dateTime: String,
    category: String,
    supportMode: String,
    status: String,
    subCategory: String,
    urgencyLevel: String,
    onContactSupport: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = ticketId, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = dateTime, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Category: $category", fontSize = 14.sp)
            Text(text = "Support Mode: $supportMode", fontSize = 14.sp)
            Text(text = "Status: $status", fontSize = 14.sp)
            Text(text = "Sub Category: $subCategory", fontSize = 14.sp)
            Text(text = "Urgency Level: $urgencyLevel", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onContactSupport,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Contact Support")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyTicketsScreen() {


    Column(modifier = Modifier.padding(16.dp)) {
        SupportTicketItem(
            ticketId = "1414",
            dateTime = "10:25:01",
            category = "Loan",
            supportMode = "Video",
            status = "Active",
            subCategory = "Loan",
            urgencyLevel = "Low",
            onContactSupport = { /* Handle contact support action */ }
        )
    }
}