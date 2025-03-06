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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.swag.vyom.ui.components.CustomDropdown
import com.swag.vyom.ui.components.CustomEditText
import com.swag.vyom.ui.components.PasswordEditText
import com.swag.vyom.ui.theme.AppRed

@Composable
fun RegistrationScreen(navController: NavHostController) {
    // Get screen dimensions to make UI responsive
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

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
        // Form fields
        var aadharNo by remember { mutableStateOf("") }
        var mobileNo by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var language by remember { mutableStateOf("") }

        CustomEditText(
            value = aadharNo,
            onValueChange = { aadharNo = it },
            label = "Aadhar Card Number"
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomEditText(
            value = mobileNo,
            onValueChange = { mobileNo = it },
            label = "Mobile Number"
        )

        Text(
            text = "*Enter mobile number which is connected to your bank account",
            fontSize = 11.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomEditText(
            value = email,
            onValueChange = { email = it },
            label = "Email address"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordEditText(
            value = password,
            onValueChange = { password = it },
            label = "Password"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordEditText(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password"
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomDropdown(
            placeHolder = "Select App Language",
            options = listOf("English", "Hindi", "Tamil", "Telugu", "Kannada", "Malayalam"),
            onOptionSelected = { language = it }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
//                navController.navigate("otp_verification") {
//                    popUpTo("registration_screen") { inclusive = true }
//                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppRed),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(
                text = "Next",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        CustomerCareInfo()

        Spacer(modifier = Modifier.height(24.dp))
    }
}





@Preview(showBackground = true)
@Composable
fun PreviewRegistrationScreen() {
    RegistrationScreen(rememberNavController())
}