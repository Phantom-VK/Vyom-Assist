package com.swag.vyom.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swag.vyom.R
import com.swag.vyom.SharedPreferencesHelper
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferencesHelper = remember { SharedPreferencesHelper(context) }

    LaunchedEffect(key1 = true) {
        delay(2000)
        val userId = sharedPreferencesHelper.getid()
        if (userId != null) {
            navController.navigate("login_screen")
        } else {
            navController.navigate("customer_verification")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(150.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "splash logo"
        )
    }
}