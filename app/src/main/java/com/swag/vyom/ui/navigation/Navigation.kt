package com.swag.vyom.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.swag.vyom.ui.screens.FaceAuth
import com.swag.vyom.ui.screens.HomeScreen
import com.swag.vyom.ui.screens.SigninScreen
import com.swag.vyom.ui.screens.SplashScreen

@Composable
fun Navigation(
    navController: NavHostController

) {
    NavHost(
        navController = navController,
        startDestination = "splash_screen"
    ) {
        composable("splash_screen") {
            SplashScreen(navController)
        }
        composable("signin_screen") {
            SigninScreen(navController)
        }
        composable("face_auth") {
            FaceAuth(navController)
        }
        composable("home_screen") {
            HomeScreen(navController)
        }
    }
}