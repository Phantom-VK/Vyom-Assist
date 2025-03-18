package com.swag.vyom.ui.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.ui.theme.LightSkyBlue
import com.swag.vyom.ui.theme.SkyBlue
import com.swag.vyom.viewmodels.TicketViewModel
import kotlinx.coroutines.launch

@Composable
fun SupportTicketItem(
    ticketId: String,
    dateTime: String,
    category: String,
    supportMode: String,
    connectionWay: String,
    isRated: Int,
    status: String,
    subCategory: String,
    navController: NavController,
    urgencyLevel: String,
    assignedAgentId: Int,
    viewModel: TicketViewModel
) {
    val context = LocalContext.current
    var rating by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()
    var rated by remember { mutableStateOf(false) }

    // Union Bank of India Color Scheme
    val accentColor = Color(0xFFFFD700) // Gold
    val backgroundColor = Color(0xFFF5F5F5) // Light Gray
    val textColor = Color(0xFF333333) // Dark Gray for text

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AppRed)
                .padding(16.dp)
        ) {
            // Ticket ID
            Text(
                text = "Ticket ID: $ticketId",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Ticket Details
            Text(
                text = "Date & Time: $dateTime",
                fontSize = 14.sp,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Category: $category",
                fontSize = 14.sp,
                color = textColor
            )
            Text(
                text = "Sub Category: $subCategory",
                fontSize = 14.sp,
                color = textColor
            )
            Text(
                text = "Support Mode: $supportMode",
                fontSize = 14.sp,
                color = textColor
            )
            Text(
                text = "Status: $status",
                fontSize = 14.sp,
                color = textColor,
                fontWeight = if (status == "Resolved") FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = "Urgency Level: $urgencyLevel",
                fontSize = 14.sp,
                color = textColor,
                fontWeight = if (urgencyLevel == "High") FontWeight.Bold else FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Rating Section (Visible for Resolved Tickets)
            if (status == "Resolved" && isRated == 0 && !rated) {
                Text(
                    text = "Rate your experience:",
                    fontSize = 14.sp,
                    color = textColor
                )
                Slider(
                    value = rating,
                    onValueChange = { rating = it },
                    valueRange = 0f..5f,
                    steps = 4,
                    colors = SliderDefaults.colors(
                        thumbColor = accentColor,
                        activeTrackColor = SkyBlue,
                        inactiveTrackColor = backgroundColor
                    )
                )
                Button(
                    onClick = {
                        rated = true
                        coroutineScope.launch {
                            viewModel.giveRating(
                                agentId = assignedAgentId,
                                ticketId = ticketId.toInt(),
                                rating = rating
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Submit Rating", color = Color.White)
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW,
                            "https://www.unionbankonline.co.in/ContactUs.html".toUri())
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Contact Support", fontSize = 13.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (supportMode == "Text Message") {
                            navController.navigate("chatting_screen/$ticketId")
                        } else if (supportMode == "Video Call") {

                            if(connectionWay.isEmpty() || connectionWay.isBlank()){
                                Toast.makeText(context,"No meet scheduled yet!", Toast.LENGTH_LONG).show()
                            }else {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    connectionWay.toUri()
                                )
                                context.startActivity(intent)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Get Support", fontSize = 13.sp, color = Color.White)
                }
            }
        }
    }
}