package com.swag.vyom.ui.screens

import android.R.attr.enabled
import android.os.Build
import android.os.Build.VERSION_CODES.S
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.swag.vyom.R
import com.swag.vyom.SharedPreferencesHelper
import com.swag.vyom.dataclasses.PriorityLevel
import com.swag.vyom.dataclasses.SupportMode
import com.swag.vyom.dataclasses.Ticket
import com.swag.vyom.dataclasses.UrgencyLevel
import com.swag.vyom.dataclasses.UserDetails
import com.swag.vyom.ui.components.AudioRecorderDialog
import com.swag.vyom.ui.components.CustomDialog
import com.swag.vyom.ui.components.CustomDropdown
import com.swag.vyom.ui.components.DatePickerModal
import com.swag.vyom.ui.components.TimePickerDialog
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.ui.theme.LightSkyBlue
import com.swag.vyom.ui.theme.SkyBlue
import com.swag.vyom.viewmodels.CameraViewModel
import com.swag.vyom.viewmodels.TicketViewModel
import com.swag.vyom.viewmodels.UserViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@RequiresApi(S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketGenerationScreen(
    ticketViewModel: TicketViewModel,
    userVM: UserViewModel,
    navController: NavController
) {
    // State variables
    var category by remember { mutableStateOf("") }
    var subCategory by remember { mutableStateOf("") }
    var urgencyLevel by remember { mutableStateOf(UrgencyLevel.Low) }
    var supportMode by remember { mutableStateOf<SupportMode?>(null) }
    var languagePreference by remember { mutableStateOf("English") }
    var queryDescription by remember { mutableStateOf("") }
    var priorityLevel by remember { mutableStateOf(PriorityLevel.Normal) }
    var formattedDateTime by remember { mutableStateOf("") }

    var audioFilePath by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf("") }

    var uploadedAudioFileUrl by remember { mutableStateOf("") }
    var uploadedImageUrl by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var showAudioRecorder by remember { mutableStateOf(false) }
    var showCameraScreen by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    val userDetails by userVM.userDetails.collectAsState()

    // Create a CoroutineScope for launching coroutines
    val coroutineScope = rememberCoroutineScope()

    // Handle dialog and camera screen
    if (showDialog) {
        CustomDialog(
            title = "Success!",
            message = "Your Ticket has been submitted successfully.",
            onConfirm = { showDialog = false },
            onDismiss = { showDialog = false }
        )
    }

    if (showAudioRecorder) {
        AudioRecorderDialog(
            userID = userDetails?.id,
            onDismiss = { showAudioRecorder = false },
            onSubmit = { fileURL -> audioFilePath = fileURL }
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
                    IconButton(onClick = {
                        navController.navigateUp()
                        showDialog = false
                        showAudioRecorder = false
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    // Launch a coroutine to handle file uploads and ticket submission
                    coroutineScope.launch {
                        isLoading = true // Start loading

                        try {

                            // Upload files concurrently if they exist
                            if (audioFilePath.isNotBlank() || imagePath.isNotBlank()) {
                                val audioUploadJob = if (audioFilePath.isNotBlank()) {
                                    async { ticketViewModel.uploadFileCoroutine(File(audioFilePath)) }
                                } else null

                                val imageUploadJob = if (imagePath.isNotBlank()) {
                                    async { ticketViewModel.uploadFileCoroutine(File(imagePath)) }
                                } else null

                                // Await the results
                                audioUploadJob?.let { uploadedAudioFileUrl = it.await() }
                                imageUploadJob?.let { uploadedImageUrl = it.await() }
                            }
                            showDialog = true

                            // Now create the ticket with the URLs
                            handleTicketSubmission(
                                ticketViewModel,
                                userDetails,
                                category,
                                subCategory,
                                urgencyLevel,
                                supportMode,
                                formattedDateTime,
                                languagePreference,
                                queryDescription,
                                priorityLevel,
                                uploadedImageUrl = uploadedImageUrl,
                                uploadedAudioFileUrl = uploadedAudioFileUrl,
                                navController
                            )

                        } catch (e: Exception) {
                            Log.e("Upload", "Error uploading files: ${e.message}")
                            // You may want to show an error dialog here
                        } finally {
                            isLoading = false // Stop loading
                        }
                    }
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
        Box(modifier = Modifier.fillMaxSize()) {
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

                AttachmentOptionsSection(
                    onRecordVideoImage = { showCameraScreen = true },
                    onRecordAudio = { showAudioRecorder = true }
                )

                QueryDescriptionSection(
                    queryDescription = queryDescription,
                    onQueryDescriptionChange = { queryDescription = it }
                )

                Spacer(modifier = Modifier.height(80.dp))
            }

            if(isLoading){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)) // Semi-transparent background
                        .clickable { /* Prevent clicks while loading */ },
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = SkyBlue, // Customize the color
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            if (showCameraScreen) {
                Dialog(
                    onDismissRequest = { showCameraScreen = false } // Close the dialog when the user clicks outside
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp) // Adjust the height as needed
                            .background(Color.White, RoundedCornerShape(16.dp)) // Add rounded corners
                    ) {
                        CameraScreen(
                            cameraVM = CameraViewModel(),
                            userID = userDetails?.id ?: 0,
                            onPhotoTaken = { uri ->
                                imagePath = uri.path.toString()
                                showCameraScreen = false // Close the dialog after capturing the photo
                            }
                        )
                    }
                }
            }
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

// Reusable function for handling ticket submission
@RequiresApi(Build.VERSION_CODES.O)
private fun handleTicketSubmission(
    ticketViewModel: TicketViewModel,
    userDetails: UserDetails?,
    category: String,
    subCategory: String,
    urgencyLevel: UrgencyLevel,
    supportMode: SupportMode?,
    formattedDateTime: String,
    languagePreference: String,
    queryDescription: String,
    priorityLevel: PriorityLevel,
    uploadedImageUrl: String,
    uploadedAudioFileUrl: String,
    navController: NavController
) {


        val ticket = Ticket(
            user_id = userDetails?.id ?: 1,
            category = category,
            sub_category = subCategory,
            urgency_level = urgencyLevel.toString(),
            preferred_support_mode = supportMode?.toString() ?: "",
            available_timedate = formattedDateTime.ifEmpty {
                LocalDateTime.now().plusDays(1)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            },
            language_preference = languagePreference,
            description = queryDescription,
            audio_file_link = uploadedAudioFileUrl,
            video_file_link = "",
            attached_image_link = uploadedImageUrl,
            assigned_department = "",
            priority_level = priorityLevel.toString()
        )

        ticketViewModel.createTicket(ticket)
        navController.navigate("home_screen")

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
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                shape = RoundedCornerShape(size = 7.dp)
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
                    Text("Select Date", fontSize = 12.sp)
                }
            }

            Button(
                onClick = { showTimePicker = true },
                modifier = Modifier
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                shape = RoundedCornerShape(size = 7.dp)
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
                    Text("Select Time", fontSize = 12.sp)
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
//        TODO("Fix Date and Time Picker UI")

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
private fun AttachmentOptionsSection(
    onRecordVideoImage: () -> Unit,
    onRecordAudio: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AttachmentOptions(
            icon = R.drawable.record,
            text = "Record Video/Image",
            onClick = onRecordVideoImage
        )
        AttachmentOptions(
            icon = R.drawable.attachment,
            text = "Attach File"
        )
        AttachmentOptions(
            icon = R.drawable.recording_icon,
            text = "Record Audio",
            onClick = onRecordAudio
        )
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
            .clickable(onClick = onClick )
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
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QueryDescriptionSection(
    queryDescription: String,
    onQueryDescriptionChange: (String) -> Unit
) {
    TextField(
        value = queryDescription,
        onValueChange = onQueryDescriptionChange,
        placeholder = {
            Row {
                Text("Brief Description", color = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = "Edit Description",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp))

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
}

@RequiresApi(S)
@Preview(showBackground = true)
@Composable
fun PreviewTicketScreen() {
    val context = LocalContext.current
    val ticketViewModel = TicketViewModel()
     val sharedPreferencesHelper by lazy { SharedPreferencesHelper(context) }
     val userVM by lazy { UserViewModel(sharedPreferencesHelper) }
    val navController = rememberNavController()
    TicketGenerationScreen(
        ticketViewModel = ticketViewModel,
        userVM = userVM,
        navController = navController
    )
}

