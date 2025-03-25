package com.swag.vyom.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.S
import android.util.Log
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.swag.vyom.R
import com.swag.vyom.SharedPreferencesHelper
import com.swag.vyom.dataclasses.Ticket
import com.swag.vyom.dataclasses.UrgencyLevel
import com.swag.vyom.dataclasses.UserDetails
import com.swag.vyom.ui.components.*
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.ui.theme.SkyBlue
import com.swag.vyom.utils.calculateUrgencyLevel
import com.swag.vyom.utils.getFilePathFromUri
import com.swag.vyom.viewmodels.CameraViewModel
import com.swag.vyom.viewmodels.TicketViewModel
import com.swag.vyom.viewmodels.UserViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketGenerationScreen(
    ticketViewModel: TicketViewModel,
    userVM: UserViewModel,
    navController: NavController
) {
    val userDetails by userVM.userDetails.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Form state
    var category by remember { mutableStateOf("") }
    var subCategory by remember { mutableStateOf("") }
    var supportMode by remember { mutableStateOf("") }
    var languagePreference by remember { mutableStateOf("English") }
    var queryDescription by remember { mutableStateOf("") }
    var formattedDateTime by remember { mutableStateOf("") }

    // Attachment state
    var audioFilePath by remember { mutableStateOf("") }
    var mediaPath by remember { mutableStateOf("") }
    var isVideoMedia by remember { mutableStateOf(false) }
    var attachedFileUri by remember { mutableStateOf<Uri?>(null) }
    var attachedFilePath by remember { mutableStateOf("") }

    // UI state
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showAudioRecorder by remember { mutableStateOf(false) }
    var showCameraScreen by remember { mutableStateOf(false) }
    var showFilePicker by remember { mutableStateOf(false) }
    var showChatbotDialog by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) { showChatbotDialog = true }

    // Dialogs
    if (showChatbotDialog) {
        VyomChatbotDialog(onDismiss = { showChatbotDialog = false })
    }
    if (showDialog) {
        CustomDialog(
            title = "Success!",
            message = "Your Ticket has been submitted successfully.",
            onConfirm = { navController.navigate("home_screen") },
            onDismiss = { navController.navigate("home_screen") }
        )
    }
    if (showAudioRecorder) {
        AudioRecorderDialog(
            userID = userDetails?.id,
            onDismiss = { showAudioRecorder = false },
            onSubmit = { fileURL -> audioFilePath = fileURL }
        )
    }
    if (showFilePicker) {
        FilePickerDialog(
            onFileSelected = { uri ->
                attachedFileUri = uri
                attachedFilePath = getFilePathFromUri(context, uri) ?: ""
                showFilePicker = false
            },
            onDismiss = { showFilePicker = false }
        )
    }
    if (showCameraScreen) {
        Dialog(onDismissRequest = { showCameraScreen = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
            ) {
                CameraScreen(
                    cameraVM = CameraViewModel(),
                    userID = userDetails?.id ?: 0,
                    onMediaCaptured = { uri, isVideo ->
                        mediaPath = uri.path.toString()
                        isVideoMedia = isVideo
                        showCameraScreen = false // Close on successful capture
                    }
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Generate Ticket",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 25.sp,
                        color = SkyBlue
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Go back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        try {
                            val uploadJobs = buildList {
                                if (mediaPath.isNotBlank()) add(async { ticketViewModel.uploadFileCoroutine(File(mediaPath), isVideoMedia) })
                                if (audioFilePath.isNotBlank()) add(async { ticketViewModel.uploadFileCoroutine(File(audioFilePath), false) })
                                if (attachedFilePath.isNotBlank()) add(async { ticketViewModel.uploadFileCoroutine(File(attachedFilePath), false) })
                            }
                            val uploadResults = uploadJobs.awaitAll()
                            val uploadedMediaUrl = uploadResults.getOrNull(0) ?: ""
                            val uploadedAudioUrl = uploadResults.getOrNull(1) ?: ""
                            val uploadedFileUrl = uploadResults.getOrNull(2) ?: ""

                            handleTicketSubmission(
                                ticketViewModel,
                                userDetails,
                                category,
                                subCategory,
                                calculateUrgencyLevel(category, subCategory, formattedDateTime),
                                supportMode,
                                formattedDateTime,
                                languagePreference,
                                queryDescription,
                                if (!isVideoMedia) uploadedMediaUrl else "",
                                uploadedAudioUrl,
                                if (isVideoMedia) uploadedMediaUrl else "",
                                uploadedFileUrl
                            )
                            showDialog = true
                        } catch (e: Exception) {
                            Log.e("TicketGenScreen", "Error: ${e.message}")
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                shape = RoundedCornerShape(10.dp),
                enabled = category.isNotBlank() && queryDescription.isNotBlank() &&
                        subCategory.isNotBlank() && supportMode.isNotBlank() && !isLoading
            ) {
                Text("Generate", color = Color.White, modifier = Modifier.padding(8.dp))
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
                    onSubCategorySelected = { _, selected -> subCategory = selected }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    thickness = 1.dp,
                    color = Color.Black
                )
                SupportModeSection(
                    selectedSupportMode = supportMode,
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
                    onRecordAudio = { showAudioRecorder = true },
                    onAttachFile = { showFilePicker = true }
                )
                AttachedFilesSummary(
                    mediaPath = mediaPath,
                    audioFilePath = audioFilePath,
                    attachedFilePath = attachedFilePath
                )
                QueryDescriptionSection(
                    queryDescription = queryDescription,
                    onQueryDescriptionChange = { queryDescription = it }
                )
                Spacer(modifier = Modifier.height(80.dp))
            }
            if (isLoading) CustomLoadingScreen()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SupportModeSection(
    selectedSupportMode: String,
    onSupportModeSelected: (String) -> Unit,
    onTimeSlotSelected: (String) -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Preferred Support Mode",
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = AppRed,
            modifier = Modifier.padding(start = 0.dp, top = 20.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SupportModeOption(
                icon = R.drawable.fluent_video_person_call_16_regular,
                text = "Video Call",
                isSelected = selectedSupportMode == "Video Call",
                onSelect = { onSupportModeSelected("Video Call") }
            )
            SupportModeOption(
                icon = R.drawable.call_icon,
                text = "Audio Call",
                isSelected = selectedSupportMode == "Voice Call",
                onSelect = { onSupportModeSelected("Voice Call") }
            )
            SupportModeOption(
                icon = R.drawable.gridicons_chat,
                text = "Chat",
                isSelected = selectedSupportMode == "Text Message",
                onSelect = { onSupportModeSelected("Text Message") }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (selectedDate != null || selectedTime != null) {
            Text(
                text = "Selected: ${selectedDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) } ?: ""} " +
                        "${selectedTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: ""}",
                modifier = Modifier.padding(vertical = 8.dp),
                color = SkyBlue,
                fontWeight = FontWeight.Medium
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                shape = RoundedCornerShape(7.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.DateRange, "Select Date", tint = Color.White)
                    Text("Select Date", fontSize = 12.sp)
                }
            }
            Button(
                onClick = { showTimePicker = true },
                modifier = Modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                shape = RoundedCornerShape(7.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(painterResource(R.drawable.clock), "Select Time", tint = Color.White)
                    Text("Select Time", fontSize = 12.sp)
                }
            }
        }
        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { dateMillis ->
                    selectedDate = dateMillis
                    updateDateTime(selectedDate, selectedTime, onTimeSlotSelected)
                },
                onDismiss = { showDatePicker = false }
            )
        }
        if (showTimePicker) {
            TimePickerDialog(
                onTimeSelected = { time ->
                    selectedTime = time
                    updateDateTime(selectedDate, selectedTime, onTimeSlotSelected)
                },
                onDismiss = { showTimePicker = false }
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
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
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .border(1.5.dp, Color.Black, RoundedCornerShape(10.dp))
            .background(if (isSelected) SkyBlue else Color.White, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onSelect)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painterResource(icon), text, Modifier.size(40.dp), tint = if (isSelected) Color.White else Color.Black)
            Text(text, fontSize = 15.sp, color = if (isSelected) Color.White else Color.Black)
        }
    }
}

@Composable
fun AttachmentOptionsSection(onRecordVideoImage: () -> Unit, onRecordAudio: () -> Unit, onAttachFile: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AttachmentOptions(R.drawable.record, "Capture Video/Image", onClick = onRecordVideoImage)
        AttachmentOptions(R.drawable.attachment, "Attach File", onClick = onAttachFile)
        AttachmentOptions(R.drawable.recording_icon, "Record Audio", onClick = onRecordAudio)
    }
}

@Composable
fun AttachmentOptions(icon: Int, text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(120.dp)
            .width(120.dp)
            .border(1.5.dp, Color.Black, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painterResource(icon), text, Modifier.size(40.dp))
            Text(text, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun AttachedFilesSummary(mediaPath: String, audioFilePath: String, attachedFilePath: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        if (mediaPath.isNotBlank()) {
            Text(
                "Attached Media: ${File(mediaPath).name}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        if (audioFilePath.isNotBlank()) {
            Text(
                "Attached Audio: ${File(audioFilePath).name}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        if (attachedFilePath.isNotBlank()) {
            Text(
                "Attached File: ${File(attachedFilePath).name}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
fun QueryDescriptionSection(queryDescription: String, onQueryDescriptionChange: (String) -> Unit) {
    TextField(
        value = queryDescription,
        onValueChange = onQueryDescriptionChange,
        placeholder = {
            Row {
                Text("Brief Description", color = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.Create, "Edit Description", tint = Color.Gray, modifier = Modifier.size(16.dp))
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(16.dp)),
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

@RequiresApi(Build.VERSION_CODES.O)
private fun handleTicketSubmission(
    ticketViewModel: TicketViewModel,
    userDetails: UserDetails?,
    category: String,
    subCategory: String,
    urgencyLevel: UrgencyLevel,
    supportMode: String,
    formattedDateTime: String,
    languagePreference: String,
    queryDescription: String,
    uploadedImageUrl: String,
    uploadedAudioFileUrl: String,
    uploadedVideoUrl: String,
    attachedFileUrl: String
) {
    val ticket = Ticket(
        user_id = userDetails?.id ?: 1,
        category = category,
        sub_category = subCategory,
        urgency_level = urgencyLevel.toString(),
        preferred_support_mode = supportMode,
        available_timedate = formattedDateTime.ifEmpty { LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) },
        language_preference = languagePreference,
        description = queryDescription,
        audio_file_link = uploadedAudioFileUrl,
        video_file_link = uploadedVideoUrl,
        attached_image_link = uploadedImageUrl,
        assigned_department = "",
        attached_file = attachedFileUrl
    )
    ticketViewModel.createTicket(ticket)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun updateDateTime(selectedDate: Long?, selectedTime: LocalTime?, onTimeSlotSelected: (String) -> Unit) {
    if (selectedDate != null && selectedTime != null) {
        val localDate = Instant.ofEpochMilli(selectedDate).atZone(ZoneId.systemDefault()).toLocalDate()
        val dateTime = LocalDateTime.of(localDate, selectedTime)
        onTimeSlotSelected(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    }
}


@RequiresApi(S)
@Preview(showBackground = true)
@Composable
fun PreviewTicketScreen() {
    val context = LocalContext.current

    val sharedPreferencesHelper by lazy { SharedPreferencesHelper(context) }
    val userVM by lazy { UserViewModel(sharedPreferencesHelper) }
    val navController = rememberNavController()
    val ticketViewModel = TicketViewModel(sharedPreferencesHelper)
    TicketGenerationScreen(
        ticketViewModel = ticketViewModel,
        userVM = userVM,
        navController = navController
    )
}