package com.swag.vyom.ui.screens

import android.util.Log
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
import com.swag.vyom.dataclasses.UserRegistrationRequest
import com.swag.vyom.ui.components.CustomDropdown
import com.swag.vyom.ui.components.CustomEditText
import com.swag.vyom.ui.components.PasswordEditText
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.viewmodels.AuthViewModel

@Composable
fun RegistrationScreen(
    navController: NavHostController,
    authVM: AuthViewModel
) {
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

        RoundedCornerCard(screenWidth, screenHeight)

        var aadharNo by remember { mutableStateOf("") }
        var mobileNo by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var language by remember { mutableStateOf("") }

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
            options = listOf("English", "Hindi", "Tamil", "Telugu", "Kannada", "Malayalam"),
            onOptionSelected = { language = it }
        )
        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                if (password == confirmPassword) {
                    val user = UserRegistrationRequest(
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
                    navController.navigate("home_screen") {
                        popUpTo("customer_verification") { inclusive = true }
                    }
                } else {
                    Log.e("AuthViewModel", "Passwords do not match")
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

        Spacer(modifier = Modifier.height(24.dp))
        CustomerCareInfo()
        Spacer(modifier = Modifier.height(24.dp))
    }
}






//@Preview(showBackground = true)
//@Composable
//fun PreviewRegistrationScreen() {
//    RegistrationScreen(rememberNavController())
//}