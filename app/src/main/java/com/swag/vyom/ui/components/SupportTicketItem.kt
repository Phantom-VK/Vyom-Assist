package com.swag.vyom.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri

@Composable
fun SupportTicketItem(
    ticketId: String,
    dateTime: String,
    category: String,
    supportMode: String,
    status: String,
    subCategory: String,
    urgencyLevel: String
) {
    val context = LocalContext.current

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
            Text(text = "Ticket ID: $ticketId", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Date & Time: $dateTime", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Category: $category", fontSize = 14.sp)
            Text(text = "Sub Category: $subCategory", fontSize = 14.sp)
            Text(text = "Support Mode: $supportMode", fontSize = 14.sp)
            Text(text = "Status: $status", fontSize = 14.sp)
            Text(text = "Urgency Level: $urgencyLevel", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW,
                            "https://www.unionbankonline.co.in/ContactUs.html".toUri())
                        context.startActivity(intent)
                    }
                ) {
                    Text(text = "Contact Support", fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { /* Handle view details action */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "View Details")
                }
            }
        }
    }
}