package com.swag.vyom.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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
    authVM: AuthViewModel
) {
    // Get screen dimensions to make UI responsive
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val showDialog = remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    if(showDialog.value){
        CustomDialog(
            title = "Login Error",
            message = "Invalid credentials",
            onDismiss = { showDialog.value = false},
            onConfirm = { showDialog.value = false }

        )
    }

    if(isLoading){
        CustomLoadingScreen()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = screenWidth.times(0.06f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RoundedCornerCard(
            screenWidth,
            screenHeight
        )

        var mobileNo by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        CustomEditText(
            value = mobileNo,
            onValueChange = { mobileNo = it },
            label = "Mobile Number"
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
                isLoading = true
                val loginRequest = UserLoginRequest(
                    mobile_number = mobileNo,
                    password = password
                )
                authVM.login(loginRequest)
                if(authVM.loginStatus.value == true){
                    isLoading = false
                    navController.navigate("home_screen")
                }else{
                    isLoading = false
                    // Handle login failure
                    showDialog.value = true

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
}
