package com.swag.vyom.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.swag.vyom.R
import com.swag.vyom.SharedPreferencesHelper
import com.swag.vyom.dataclasses.SupportMode
import com.swag.vyom.dataclasses.Ticket
import com.swag.vyom.dataclasses.UrgencyLevel
import com.swag.vyom.dataclasses.UserDetails
import com.swag.vyom.ui.components.AttachedFileSection
import com.swag.vyom.ui.components.AudioRecorderDialog
import com.swag.vyom.ui.components.CustomDialog
import com.swag.vyom.ui.components.CustomDropdown
import com.swag.vyom.ui.components.CustomLoadingScreen
import com.swag.vyom.ui.components.DatePickerModal
import com.swag.vyom.ui.components.FilePickerDialog
import com.swag.vyom.ui.components.TimePickerDialog
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.ui.theme.LightSkyBlue
import com.swag.vyom.ui.theme.SkyBlue
import com.swag.vyom.utils.calculateUrgencyLevel
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
    var supportMode by remember { mutableStateOf<SupportMode?>(null) }
    var languagePreference by remember { mutableStateOf("English") }
    var queryDescription by remember { mutableStateOf("") }
    var formattedDateTime by remember { mutableStateOf("") }

    var audioFilePath by remember { mutableStateOf("") }
    var attachedFileName by remember { mutableStateOf("") } // To store the file name
    var attachedFileThumbnail by remember { mutableStateOf<Bitmap?>(null) } // To store the thumbnail for images




    var mediaPath by remember { mutableStateOf("") }
    var isVideoMedia by remember { mutableStateOf(false) }

    var showFilePicker by remember { mutableStateOf(false) }
    var attachedFilePath by remember { mutableStateOf("") }



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
            onConfirm = {
                showDialog = false
                navController.navigate("home_screen")
            },
            onDismiss = {
                showDialog = false
                navController.navigate("home_screen")
            }
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
        Log.d("TicketGenScreen", "File Picker Should Open")
        FilePickerDialog(
            onFileSelected = { uri ->
                attachedFilePath = uri.path.toString()
                attachedFileName = File(attachedFilePath).name // Store the file name

                // If the file is an image, generate a thumbnail
                if (attachedFilePath.endsWith(".jpg") || attachedFilePath.endsWith(".png")) {
                    attachedFileThumbnail = BitmapFactory.decodeFile(uri.path)
                } else {
                    attachedFileThumbnail = null // Clear thumbnail for non-image files
                }

                showFilePicker = false
            },
            onDismiss = { showFilePicker = false }
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
                            var uploadedMediaUrl = ""
                            var uploadedAudioFileUrl = ""
                            var uploadedFileUrl = ""

                            val mediaUploadJob = if (mediaPath.isNotBlank()) {
                                async { ticketViewModel.uploadFileCoroutine(File(mediaPath), isVideoMedia) }
                            } else null

                            val audioUploadJob = if (audioFilePath.isNotBlank()) {
                                async { ticketViewModel.uploadFileCoroutine(File(audioFilePath), isVideoMedia) }
                            } else null

                            val fileUploadJob = if (attachedFilePath.isNotBlank()) {
                                // Check if the attachedFilePath is a valid file path or URI
                                val file = if (attachedFilePath.startsWith("content://")) {
                                    // Handle content URI
                                    // You may need to retrieve the file from the URI using ContentResolver
                                    // This will require more code based on your use case
                                    null
                                } else {
                                    // Handle regular file path
                                    File(attachedFilePath)
                                }

                                if(file != null) {
                                    async { ticketViewModel.uploadFileCoroutine(file, false) }

                                } else null

                            } else null

                            // Await the results
                            mediaUploadJob?.let { uploadedMediaUrl = it.await() }
                            audioUploadJob?.let { uploadedAudioFileUrl = it.await() }
                            fileUploadJob?.let { uploadedFileUrl = it.await() }

                            // Assign to the appropriate field based on media type
                            val uploadedImageUrl = if (!isVideoMedia) uploadedMediaUrl else ""
                            val uploadedVideoUrl = if (isVideoMedia) uploadedMediaUrl else ""


                            showDialog = true

                            // Now create the ticket with the URLs
                            handleTicketSubmission(
                                ticketViewModel,
                                userDetails,
                                category,
                                subCategory,
                                urgencyLevel = calculateUrgencyLevel(
                                    category,
                                    subCategory,
                                    formattedDateTime
                                ),
                                supportMode,
                                formattedDateTime,
                                languagePreference,
                                queryDescription,
                                uploadedImageUrl = uploadedImageUrl,
                                uploadedAudioFileUrl = uploadedAudioFileUrl,
                                uploadedVideoUrl = uploadedVideoUrl  ,
                                attachedFileUrl = uploadedFileUrl
                            )
                        } catch (e: Exception) {
                            Log.e("Upload", "Error uploading files: ${e.message}")
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
                    onCategorySelected = {
                        category = it
                    },
                    onSubCategorySelected = { mainCategory, selectedSubCategory ->
                        subCategory = selectedSubCategory
                    }
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
                    onRecordAudio = { showAudioRecorder = true },
                    onAttachFile = { showFilePicker = true }
                )

                // Display attached file name and thumbnail
                AttachedFileSection(
                    fileName = attachedFileName,
                    thumbnail = attachedFileThumbnail,
                    onRemoveFile = {
                        attachedFilePath = ""
                        attachedFileName = ""
                        attachedFileThumbnail = null
                    }
                )

                QueryDescriptionSection(
                    queryDescription = queryDescription,
                    onQueryDescriptionChange = { queryDescription = it }
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
            if (isLoading) {
                CustomLoadingScreen()
            }

            // Replace the showCameraScreen Dialog with this
            if (showCameraScreen) {
                Dialog(
                    onDismissRequest = {
                        showCameraScreen = false
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .background(
                                Color.White,
                                RoundedCornerShape(16.dp)
                            )
                    ) {
                        CameraScreen(
                            cameraVM = CameraViewModel(),
                            userID = userDetails?.id ?: 0,
                            onMediaCaptured = { uri, isVideo ->
                                mediaPath = uri.path.toString()
                                isVideoMedia = isVideo
                                Log.d("TicketGenScreen", mediaPath)
                                Log.d("TicketGenScreen", isVideoMedia.toString())
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
    uploadedImageUrl: String,
    uploadedAudioFileUrl: String,
    uploadedVideoUrl: String,
    attachedFileUrl: String // Add this parameter
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
        video_file_link = uploadedVideoUrl,
        attached_image_link = uploadedImageUrl,
        assigned_department = "",
        attached_file_link = attachedFileUrl
    )

    ticketViewModel.createTicket(ticket)
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

        Spacer(modifier = Modifier.height(20.dp))

        // Display the selected date and time
        if (selectedDate != null || selectedTime != null) {
            Text(
                text = "Selected: ${
                    if (selectedDate != null) {
                        val date =
                            Instant.ofEpochMilli(selectedDate!!).atZone(ZoneId.systemDefault())
                                .toLocalDate()
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
                        val localDate =
                            Instant.ofEpochMilli(selectedDate!!).atZone(ZoneId.systemDefault())
                                .toLocalDate()
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
                    if (selectedDate != null && selectedTime != null) {
                        val localDate =
                            Instant.ofEpochMilli(selectedDate!!).atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        val dateTime = LocalDateTime.of(localDate, selectedTime)
                        formattedDateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        onTimeSlotSelected(formattedDateTime)
                    }
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
    onRecordAudio: () -> Unit,
    onAttachFile: () -> Unit // Add this parameter
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
            textSize = 12.sp,
            text = "Capture Video/Image",
            onClick = onRecordVideoImage
        )
        AttachmentOptions(
            icon = R.drawable.attachment,
            text = "Attach File",
            onClick = onAttachFile // Pass the file picker callback
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
    textSize: TextUnit = 14.sp,
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
                fontSize = textSize,
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

