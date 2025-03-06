package com.swag.vyom.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavHostController){


    // Get screen dimensions to make UI responsive
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    LaunchedEffect(Unit) {
        delay(3000)
        navController.navigate("signin_screen") {
            popUpTo("splash_screen") { inclusive = true }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 0.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RoundedCornerCard(
            screenWidth,
            screenHeight
        )
        CircularProgressIndicator()
    }
}
