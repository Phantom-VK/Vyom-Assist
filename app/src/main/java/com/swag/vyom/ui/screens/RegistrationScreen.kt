package com.swag.vyom.ui.screens

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.swag.vyom.SharedPreferencesHelper
import com.swag.vyom.dataclasses.UserRegistrationRequest
import com.swag.vyom.ui.components.CustomDropdown
import com.swag.vyom.ui.components.CustomEditText
import com.swag.vyom.ui.components.PasswordEditText
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.utils.checkFaceAndSpoof
import com.swag.vyom.viewmodels.AuthViewModel
import com.swag.vyom.viewmodels.CameraViewModel
import com.swag.vyom.viewmodels.TicketViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun RegistrationScreen(
    navController: NavHostController,
    authVM: AuthViewModel,
    preferencesHelper: SharedPreferencesHelper,
    ticketVM: TicketViewModel
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val id = preferencesHelper.getid()

    // Form States
    var aadharNo by remember { mutableStateOf(preferencesHelper.getaadhaar() ?: "") }
    var mobileNo by remember { mutableStateOf(preferencesHelper.getmobile() ?: "") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("") }

    // UI States
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showCameraScreen by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var registrationStep by remember { mutableStateOf(1) } // Step 1: Form, Step 2: Image Capture
    var formErrors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var showRegistrationSuccess by remember { mutableStateOf(false) }

    val cameraVM = remember { CameraViewModel() }
    BackHandler {
        activity?.finish() // Close the app
    }

    // Step 1: Validate form before moving to Step 2
    fun validateForm(): Boolean {
        val errors = mutableMapOf<String, String>()

        if (aadharNo.trim().length != 12) {
            errors["aadhar"] = "Please enter a valid 12-digit Aadhar number"
        }

        if (mobileNo.trim().length != 10) {
            errors["mobile"] = "Please enter a valid 10-digit mobile number"
        }

        if (!email.contains('@') || !email.contains('.')) {
            errors["email"] = "Please enter a valid email address"
        }

        if (password.length < 8) {
            errors["password"] = "Password must be at least 8 characters"
        }

        if (password != confirmPassword) {
            errors["confirmPassword"] = "Passwords do not match"
        }

        if (language.isEmpty()) {
            errors["language"] = "Please select a language"
        }

        formErrors = errors
        return errors.isEmpty()
    }

    // Registration Success Dialog
    if (showRegistrationSuccess) {
        AlertDialog(
            onDismissRequest = {
                showRegistrationSuccess = false
                navController.navigate("login_screen") {
                    popUpTo("registration_screen") { inclusive = true }
                }
            },
            title = { Text("Registration Successful") },
            text = {
                Text(
                    "Your account has been created successfully. Please log in to continue.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRegistrationSuccess = false
                        navController.navigate("login_screen") {
                            popUpTo("registration_screen") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppRed)
                ) {
                    Text("Login Now")
                }
            },
            containerColor = Color.White
        )
    }

    // Camera Dialog
    if (showCameraScreen) {
        Dialog(onDismissRequest = { showCameraScreen = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(550.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            ) {
                CameraScreen(
                    cameraVM = cameraVM,
                    userID = id!!,
                ) { uri, _ ->

                    showCameraScreen = false

                    checkFaceAndSpoof(imagePath = uri.path.toString(), context = context) { faceDetected ->
                        if (faceDetected) {
                            capturedImageUri = uri
                            Toast.makeText(context, "Face detected successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "No face detected! Please try again.", Toast.LENGTH_LONG).show()
                            capturedImageUri = null
                            cameraVM.clearPhoto()
                        }
                    }
                }
            }
        }
    }

    // Main UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with back button for step 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (registrationStep == 2) {
                IconButton(
                    onClick = { registrationStep = 1 }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Go Back",
                        tint = AppRed
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = "Create Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        // Progress indicator
        LinearProgressIndicator(
        progress = { if (registrationStep == 1) 0.5f else 0.9f },
        modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
        color = AppRed,
        trackColor = Color.LightGray,
        strokeCap = StrokeCap.Round,
        )

        // Step indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Step $registrationStep of 2",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = if (registrationStep == 1) "Personal Details" else "Identity Verification",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppRed
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Step 1: Registration Form
        AnimatedVisibility(
            visible = registrationStep == 1,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Aadhar Number
                CustomEditText(
                    value = aadharNo,
                    onValueChange = { if (it.length <= 12) aadharNo = it },
                    label = "Aadhar Card Number",
                    keyboardType = KeyboardType.Number,
                    iserror = formErrors.containsKey("aadhar")
                )
                if (formErrors.containsKey("aadhar")) {
                    Text(
                        text = formErrors["aadhar"] ?: "",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Mobile Number
                CustomEditText(
                    value = mobileNo,
                    onValueChange = { if (it.length <= 10) mobileNo = it },
                    label = "Mobile Number",
                    keyboardType = KeyboardType.Phone,
                    iserror = formErrors.containsKey("mobile")
                )
                Text(
                    text = "*Enter mobile number connected to your bank account",
                    fontSize = 11.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 4.dp),
                    color = if (formErrors.containsKey("mobile")) Color.Red else Color.Gray
                )
                if (formErrors.containsKey("mobile")) {
                    Text(
                        text = formErrors["mobile"] ?: "",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Email
                CustomEditText(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email address",
                    keyboardType = KeyboardType.Email,
                    iserror = formErrors.containsKey("email")
                )
                if (formErrors.containsKey("email")) {
                    Text(
                        text = formErrors["email"] ?: "",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Password
                PasswordEditText(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    iserror = formErrors.containsKey("password")
                )
                if (formErrors.containsKey("password")) {
                    Text(
                        text = formErrors["password"] ?: "",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password
                PasswordEditText(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm Password",
                    iserror = formErrors.containsKey("confirmPassword")
                )
                if (formErrors.containsKey("confirmPassword")) {
                    Text(
                        text = formErrors["confirmPassword"] ?: "",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Language Selection
                CustomDropdown(
                    options = listOf("English", "Hindi"),
                    placeHolder = "Select App Language",
                    onOptionSelected = { language = it }
                )
                if (formErrors.containsKey("language")) {
                    Text(
                        text = formErrors["language"] ?: "",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Next Button
                Button(
                    onClick = {
                        if (validateForm()) {
                            registrationStep = 2
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Next", color = Color.White, fontSize = 16.sp)
                }

                // Login option
                TextButton(
                    onClick = {
                        navController.navigate("login_screen")
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Already have an account? Login",
                        color = AppRed,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Step 2: Photo Capture and Submission
        AnimatedVisibility(
            visible = registrationStep == 2,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Image capture section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (capturedImageUri != null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Image Captured",
                                    tint = Color.Green,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "Image captured successfully",
                                    color = Color.Green,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )                            }
                        } else {
                            Text(
                                text = "Capture your live photo for verification",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Capture Image Button
                Button(
                    onClick = { showCameraScreen = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isLoading
                ) {
                    Text(
                        text = if (capturedImageUri == null) "Capture Image" else "Retake Image",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Register Button
                Button(
                    onClick = {
                        val user = UserRegistrationRequest(
                            id = preferencesHelper.getid(),
                            mobile_number = mobileNo,
                            aadhaar = aadharNo,
                            account_number = " ",
                            first_name = " ",
                            last_name = " ",
                            date_of_birth = " ",
                            gender = " ",
                            email = email,
                            password = password,
                            image_link = "",
                            aadhaar_image_link = "",
                            address = "",
                            country = "India",
                            language_preference = language
                        )
                        authVM.register(user)
                        if (capturedImageUri == null) {
                            Toast.makeText(context, "Please capture your live photo!", Toast.LENGTH_LONG).show()
                        } else {
                            isLoading = true
                            // Upload image first
                            capturedImageUri?.let { uri ->
                                val file = File(uri.path)
                                ticketVM.uploadUserImage(aadharNo, file) { imageUrl ->
                                    if (imageUrl.isNotEmpty()) {

                                        // Show success dialog and navigate to login screen
                                        isLoading = false
                                        showRegistrationSuccess = true
                                        navController.navigate("login_screen")
                                    } else {
                                        Toast.makeText(context, "Failed to upload image!", Toast.LENGTH_LONG).show()
                                        isLoading = false
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                    shape = RoundedCornerShape(10.dp),
                    enabled = capturedImageUri != null && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text(text = "Register", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}