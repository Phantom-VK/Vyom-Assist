package com.swag.vyom.ui.screens

import android.R.attr.contentDescription
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.swag.vyom.R
import com.swag.vyom.dataclasses.*
import com.swag.vyom.ui.components.CustomDialog
import com.swag.vyom.ui.components.CustomDropdown
import com.swag.vyom.ui.components.DatePickerModal
import com.swag.vyom.ui.components.TimePickerDialog
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.ui.theme.LightSkyBlue
import com.swag.vyom.ui.theme.SkyBlue
import com.swag.vyom.viewmodels.ChatbotViewModel
import com.swag.vyom.viewmodels.TicketViewModel
import com.swag.vyom.viewmodels.UserViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketGenerationScreen(
    ticketViewModel: TicketViewModel,
    userVM: UserViewModel,
    navController: NavController,
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

    var formattedDateTime by remember { mutableStateOf("") }

    val userDetails by userVM.userDetails.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog){
        CustomDialog(
            title = "Success!",
            message = "Your Ticket has been submitted successfully.",
            onConfirm = {
                showDialog = false
            },
            onDismiss = {
                showDialog= false
            }
        )
    }


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
                        user_id = userDetails?.id ?: 1,
                        category = category,
                        sub_category = subCategory,
                        urgency_level = urgencyLevel.toString(),
                        preferred_support_mode = supportMode?.toString() ?: "",
                        available_timedate = formattedDateTime.ifEmpty {
                            LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        },
                        language_preference = languagePreference,
                        description = queryDescription,
                        audio_file_link = "",
                        video_file_link = "",
                        attached_image_link = "",
                        assigned_department = "",
                        priority_level = priorityLevel.toString()
                    )
                    ticketViewModel.createTicket(ticket)
                    navController.navigate("home_screen")

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
                onTimeSlotSelected = { formattedDateTime = it },
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SupportModeSection(
    onSupportModeSelected: (SupportMode) -> Unit,
    onTimeSlotSelected: (String) -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var formattedDateTime by remember { mutableStateOf("") }

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

        // Display the selected date and time
        if (selectedDate != null || selectedTime != null) {
            Text(
                text = "Selected: ${
                    if (selectedDate != null) {
                        val date = Instant.ofEpochMilli(selectedDate!!).atZone(ZoneId.systemDefault()).toLocalDate()
                        date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                    } else ""
                } ${
                    if (selectedTime != null) {
                        selectedTime!!.format(DateTimeFormatter.ofPattern("hh:mm a"))
                    } else ""
                }",
                modifier = Modifier.padding(vertical = 8.dp),
                color = SkyBlue,
                fontWeight = FontWeight.Medium
            )
        }

        // Replace the dropdown with two buttons for date and time
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier
                    .height(48.dp)
                    .width(140.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange, // Replace with your calendar icon
                        contentDescription = "Select Date",
                        tint = Color.White
                    )
                    Text("Select Date")
                }
            }

            Button(
                onClick = { showTimePicker = true },
                modifier = Modifier
                    .height(48.dp)
                    .width(140.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.clock), // Replace with your clock icon
                        contentDescription = "Select Time",
                        tint = Color.White
                    )
                    Text("Select Time")
                }
            }
        }

        // Show DatePicker when button is clicked
        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { dateMillis ->
                    selectedDate = dateMillis
                    // Format date time if both date and time are selected
                    if (selectedDate != null && selectedTime != null) {
                        val localDate = Instant.ofEpochMilli(selectedDate!!).atZone(ZoneId.systemDefault()).toLocalDate()
                        val dateTime = LocalDateTime.of(localDate, selectedTime)
                        formattedDateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        onTimeSlotSelected(formattedDateTime)
                    }
                },
                onDismiss = { showDatePicker = false }
            )
        }

        // Create TimePicker when button is clicked
        if (showTimePicker) {
            TimePickerDialog(
                onTimeSelected = { time ->
                    selectedTime = time
                    // Format date time if both date and time are selected
                    if (selectedDate != null && selectedTime != null) {
                        val localDate = Instant.ofEpochMilli(selectedDate!!).atZone(ZoneId.systemDefault()).toLocalDate()
                        val dateTime = LocalDateTime.of(localDate, selectedTime)
                        formattedDateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        onTimeSlotSelected(formattedDateTime)
                    }
                },
                onDismiss = { showTimePicker = false }
            )
        }
        TODO("Fix Date and Time Picker UI")

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

//@RequiresApi(Build.VERSION_CODES.O)
//@Preview(showBackground = true)
//@Composable
//fun PreviewTicketScreen() {
//    val ticketViewModel = TicketViewModel()
//    TicketGenerationScreen(ticketViewModel = ticketViewModel)
//}