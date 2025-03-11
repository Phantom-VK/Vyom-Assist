package com.swag.vyom.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.swag.vyom.ui.theme.SkyBlue
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(12) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var isAM by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Time",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SkyBlue,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour picker
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Hour", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))

                        // Hour selection
                        Column(
                            modifier = Modifier
                                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            // Up arrow for hour
                            IconButton(onClick = {
                                selectedHour = if (selectedHour == 12) 1 else selectedHour + 1
                            }) {
                                Text("▲", fontSize = 20.sp, color = SkyBlue)
                            }

                            // Hour value
                            Text(
                                text = selectedHour.toString().padStart(2, '0'),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            // Down arrow for hour
                            IconButton(onClick = {
                                selectedHour = if (selectedHour == 1) 12 else selectedHour - 1
                            }) {
                                Text("▼", fontSize = 20.sp, color = SkyBlue)
                            }
                        }
                    }

                    Text(":", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))

                    // Minute picker
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Minute", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))

                        // Minute selection
                        Column(
                            modifier = Modifier
                                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            // Up arrow for minute
                            IconButton(onClick = {
                                selectedMinute = (selectedMinute + 1) % 60
                            }) {
                                Text("▲", fontSize = 20.sp, color = SkyBlue)
                            }

                            // Minute value
                            Text(
                                text = selectedMinute.toString().padStart(2, '0'),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            // Down arrow for minute
                            IconButton(onClick = {
                                selectedMinute = if (selectedMinute == 0) 59 else selectedMinute - 1
                            }) {
                                Text("▼", fontSize = 20.sp, color = SkyBlue)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // AM/PM selector
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("AM/PM", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))

                        // AM/PM selection
                        Column(
                            modifier = Modifier
                                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            // Button for AM
                            Button(
                                onClick = { isAM = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isAM) SkyBlue else Color.LightGray
                                ),
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .width(60.dp)
                            ) {
                                Text("AM", color = if (isAM) Color.White else Color.Black)
                            }

                            // Button for PM
                            Button(
                                onClick = { isAM = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!isAM) SkyBlue else Color.LightGray
                                ),
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .width(60.dp)
                            ) {
                                Text("PM", color = if (!isAM) Color.White else Color.Black)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons for confirmation or dismissal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            // Convert 12-hour format to 24-hour for LocalTime
                            var hour24 = selectedHour
                            if (!isAM && selectedHour != 12) hour24 += 12
                            if (isAM && selectedHour == 12) hour24 = 0

                            val time = LocalTime.of(hour24, selectedMinute)
                            onTimeSelected(time)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}