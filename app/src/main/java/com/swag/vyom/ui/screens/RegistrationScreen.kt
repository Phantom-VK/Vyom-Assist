package com.swag.vyom.ui.screens

import android.R.attr.password
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import java.io.File

@Composable
fun RegistrationScreen(
    navController: NavHostController,
    authVM: AuthViewModel,
    preferencesHelper: SharedPreferencesHelper,
    ticketVM: TicketViewModel // Add TicketViewModel
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val context  = LocalContext.current
    var id = preferencesHelper.getid()

    var aadharNo by remember { mutableStateOf(preferencesHelper.getaadhaar() ?: " ") }
    var mobileNo by remember { mutableStateOf(preferencesHelper.getmobile() ?: " ") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("") }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) } // To store the captured image URI
    var showCameraScreen by remember { mutableStateOf(false) }
    val cameraVM by lazy{ CameraViewModel() }


    if (showCameraScreen){
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
                    cameraVM = cameraVM,
                    userID = id!!,
                ) { uri, isVideo ->


                        checkFaceAndSpoof(imagePath = uri.path.toString(), context = context){
                            if(it){
                                Toast.makeText(context, "Face Detected", Toast.LENGTH_LONG).show()
                                Log.d("FaceDetection", "Face detected")
                            }else{
                                Toast.makeText(context, "No Face Detected! Please click image again", Toast.LENGTH_LONG).show()
                                capturedImageUri = null
                                cameraVM.clearPhoto()

                                Log.d("FaceDetection", "No face detected")
                            }

                    }
                    capturedImageUri = uri

                }
            }}

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = screenWidth.times(0.06f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RoundedCornerCard(screenWidth, screenHeight)

        CustomEditText(value = aadharNo, onValueChange = { aadharNo = it }, label = "Aadhar Card Number")
        Spacer(modifier = Modifier.height(16.dp))

        CustomEditText(value = mobileNo, onValueChange = { mobileNo = it }, label = "Mobile Number")
        Text(
            text = "*Enter mobile number which is connected to your bank account",
            fontSize = 11.sp,
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 4.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        CustomEditText(value = email, onValueChange = { email = it }, label = "Email address")
        Spacer(modifier = Modifier.height(16.dp))

        PasswordEditText(value = password, onValueChange = { password = it }, label = "Password")
        Spacer(modifier = Modifier.height(16.dp))

        PasswordEditText(value = confirmPassword, onValueChange = { confirmPassword = it }, label = "Confirm Password")
        Spacer(modifier = Modifier.height(16.dp))

        CustomDropdown(
            placeHolder = "Select App Language",
            options = listOf("English", "Hindi"),
            onOptionSelected = { language = it }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Button to navigate to CameraScreen
        Button(
            onClick = {
               showCameraScreen = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppRed),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Capture Image", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if(capturedImageUri != null) {
                    if(aadharNo.isNotEmpty() && mobileNo.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && language.isNotEmpty()) {
                        if (password == confirmPassword) {
                            // Call registration function
                            val user = UserRegistrationRequest(
                                id = id,
                                mobile_number = mobileNo,
                                aadhaar = aadharNo,
                                account_number = " ", // Empty
                                first_name = " ", // Empty
                                last_name = " ", // Empty
                                date_of_birth = " ", // Empty
                                gender = " ", // Empty
                                email = email,
                                password = password,
                                image_link = "", // Empty
                                aadhaar_image_link = "", // Empty
                                address = "", // Empty
                                country = "India",
                                language_preference = language
                            )
                            authVM.register(user)

                            // Call image upload function if image is captured
                            capturedImageUri?.let { uri ->
                                val file = File(uri.path)
                                ticketVM.uploadUserImage(aadharNo, file) { imageUrl ->
                                    if (imageUrl.isNotEmpty()) {
                                        Log.d(
                                            "RegistrationScreen",
                                            "Image uploaded successfully: $imageUrl"
                                        )
                                    } else {
                                        Log.e("RegistrationScreen", "Failed to upload image")
                                    }
                                }
                            }

                            navController.navigate("home_screen") {
                                popUpTo("customer_verification") { inclusive = true }
                            }
                        } else {
                            Log.e("AuthViewModel", "Passwords do not match")
                        }
                    }else{
                        Toast.makeText(context, "Please fill all the fields!", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(context, "Please upload your live photo!", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppRed),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Register", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        CustomerCareInfo()
        Spacer(modifier = Modifier.height(24.dp))
    }
}
