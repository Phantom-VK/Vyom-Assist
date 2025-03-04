package com.swag.vyom.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swag.vyom.R
import com.swag.vyom.dataclasses.*
import com.swag.vyom.ui.components.CustomDropdown
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.ui.theme.LightSkyBlue
import com.swag.vyom.ui.theme.SkyBlue
import com.swag.vyom.viewmodels.TicketViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketGenerationScreen(
    ticketViewModel: TicketViewModel,
    onBackClick: () -> Unit = {}
) {
    var category by remember { mutableStateOf("") }
    var subCategory by remember { mutableStateOf("") }
    var urgencyLevel by remember { mutableStateOf(UrgencyLevel.Low) }
    var supportMode by remember { mutableStateOf<SupportMode?>(null) }
    var availableTimeSlot by remember { mutableStateOf("") }
    var languagePreference by remember { mutableStateOf("English") }
    var queryDescription by remember { mutableStateOf("") }
    var priorityLevel by remember { mutableStateOf(PriorityLevel.Normal) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Generate Ticket",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 25.sp,
                        color = SkyBlue
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val ticket = Ticket(
                        user_id = 1, // Replace with actual user ID
                        category = category,
                        sub_category = subCategory,
                        urgency_level = urgencyLevel.toString(),
                        preferred_support_mode = supportMode?.toString() ?: "",
                        available_timedate = LocalDateTime.now().plusDays(1)
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        language_preference = languagePreference,
                        description = queryDescription,
                        audio_file_link = "", // Add if needed
                        video_file_link = "", // Add if needed
                        attached_image_link = "", // Add if needed
                        assigned_department = "", // Add if needed
                        priority_level = priorityLevel.toString()
                    )
                    ticketViewModel.createTicket(ticket)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                shape = RoundedCornerShape(10.dp),
                enabled = category.isNotBlank() &&
                        queryDescription.isNotBlank() &&
                        subCategory.isNotBlank() &&
                        supportMode != null
            ) {
                Text(
                    text = "Generate",
                    color = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            QuerySelectionSection(
                onCategorySelected = { category = it },
                onSubCategorySelected = { subCategory = it },
                onUrgencyLevelSelected = { urgencyLevel = it },
                onPriorityLevelSelected = { priorityLevel = it }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Black
            )

            SupportModeSection(
                onSupportModeSelected = { supportMode = it },
                onTimeSlotSelected = { availableTimeSlot = it },
                onLanguageSelected = { languagePreference = it }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Black
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AttachmentOptions(
                    icon = R.drawable.record,
                    text = "Record Video/Issue"
                )
                AttachmentOptions(
                    icon = R.drawable.attachment,
                    text = "Attach Image"
                )
            }

            TextField(
                value = queryDescription,
                onValueChange = { queryDescription = it },
                placeholder = {
                    Row {
                        Text("Brief Description", color = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "Edit Description",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                    .background(Color.White),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Gray,
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun QuerySelectionSection(
    onCategorySelected: (String) -> Unit,
    onSubCategorySelected: (String) -> Unit,
    onUrgencyLevelSelected: (UrgencyLevel) -> Unit,
    onPriorityLevelSelected: (PriorityLevel) -> Unit
) {
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
        CustomDropdown(
            placeHolder = "Category",
            options = listOf("IT Support", "Finance", "HR", "Sales"),
            onOptionSelected = onCategorySelected
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomDropdown(
            placeHolder = "Sub Category",
            options = listOf("Technical Issue", "Software Request", "Access Rights"),
            onOptionSelected = onSubCategorySelected
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomDropdown(
            placeHolder = "Urgency Level",
            options = UrgencyLevel.entries.map { it.toString() },
            onOptionSelected = { onUrgencyLevelSelected(UrgencyLevel.valueOf(it)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomDropdown(
            placeHolder = "Priority Level",
            options = PriorityLevel.entries.map { it.toString() },
            onOptionSelected = { onPriorityLevelSelected(PriorityLevel.valueOf(it)) }
        )
    }
}

@Composable
fun SupportModeSection(
    onSupportModeSelected: (SupportMode) -> Unit,
    onTimeSlotSelected: (String) -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    Text(
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp),
        text = "Preferred Support Mode",
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = AppRed
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SupportModeOption(
                icon = R.drawable.fluent_video_person_call_16_regular,
                text = "Video Call",
                onSelect = { onSupportModeSelected(SupportMode.Video_Call) }
            )
            SupportModeOption(
                icon = R.drawable.call_icon,
                text = "Audio Call",
                onSelect = { onSupportModeSelected(SupportMode.Voice_Call) }
            )
            SupportModeOption(
                icon = R.drawable.gridicons_chat,
                text = "Chat",
                onSelect = { onSupportModeSelected(SupportMode.Text_Message) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomDropdown(
            placeHolder = "Available Time Slot",
            options = listOf("Morning", "Afternoon", "Evening"),
            onOptionSelected = onTimeSlotSelected
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomDropdown(
            placeHolder = "Language Preference",
            options = listOf("English", "Hindi", "Marathi"),
            onOptionSelected = onLanguageSelected
        )
    }
}

@Composable
fun SupportModeOption(
    icon: Int,
    text: String,
    onSelect: () -> Unit
) {
    var isSelected by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(100.dp)
            .border(
                2.dp,
                color = if (isSelected) SkyBlue else Color.Black,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = if (isSelected) LightSkyBlue else Color.White,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                isSelected = !isSelected
                onSelect()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = text,
                modifier = Modifier.size(40.dp),
                tint = if (isSelected) SkyBlue else Color.Black
            )

            Text(
                text = text,
                fontSize = 15.sp,
                color = if (isSelected) SkyBlue else Color.Black
            )
        }
    }
}

@Composable
fun AttachmentOptions(
    icon: Int,
    text: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .height(120.dp)
            .width(120.dp)
            .border(
                2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = text,
                modifier = Modifier.size(40.dp)
            )

            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewTicketScreen() {
    val ticketViewModel = TicketViewModel()
    TicketGenerationScreen(ticketViewModel = ticketViewModel)
}