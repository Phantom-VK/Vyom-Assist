package com.swag.vyom.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.swag.vyom.SharedPreferencesHelper
import com.swag.vyom.dataclasses.UserLoginRequest
import com.swag.vyom.ui.components.CustomDialog
import com.swag.vyom.ui.components.CustomEditText
import com.swag.vyom.ui.components.CustomLoadingScreen
import com.swag.vyom.ui.components.PasswordEditText
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    authVM: AuthViewModel,
    preferencesHelper : SharedPreferencesHelper
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val context = LocalContext.current
    val activity = context as? Activity



    var mobileNo by remember { mutableStateOf(preferencesHelper.getmobile() ?: "") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }


    // Handle back press to close the app
    BackHandler {
        activity?.finish()
    }

    // Show error dialog
    if (showDialog) {
        CustomDialog(
            title = "Login Error",
            message = errorMessage,
            onDismiss = { showDialog = false },
            onConfirm = { showDialog = false }
        )
    }

    // Main UI
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = screenWidth.times(0.06f)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RoundedCornerCard(screenWidth, screenHeight)

            CustomEditText(
                value = mobileNo,
                onValueChange = { mobileNo = it },
                label = "Mobile Number",
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordEditText(
                value = password,
                onValueChange = { password = it },
                label = "Password"
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (mobileNo.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        val loginRequest = UserLoginRequest(
                            mobile_number = mobileNo,
                            password = password
                        )
                        authVM.login(loginRequest) { isSuccess ->
                            isLoading = false
                            if (isSuccess) {
                                navController.navigate("face_auth") {
                                    // Clear back stack to prevent going back to login
                                    popUpTo("login_screen") { inclusive = true }
                                }
                            } else {
                                errorMessage = "Invalid credentials. Please try again."
                                showDialog = true
                            }
                        }
                    } else {
                        errorMessage = "Please fill in all fields."
                        showDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = "Login",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            CustomerCareInfo()

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Show loading screen
        if (isLoading) {
            CustomLoadingScreen()
        }
    }
}