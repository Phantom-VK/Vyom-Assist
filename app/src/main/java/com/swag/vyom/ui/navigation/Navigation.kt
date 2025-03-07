package com.swag.vyom.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.swag.vyom.ui.screens.FaceAuth
import com.swag.vyom.ui.screens.HomeScreen
import com.swag.vyom.ui.screens.LoginScreen
import com.swag.vyom.ui.screens.NumberVerificationScreen
import com.swag.vyom.ui.screens.RegistrationScreen
import com.swag.vyom.ui.screens.SplashScreen
import com.swag.vyom.ui.screens.TicketGenerationScreen
import com.swag.vyom.viewmodels.AuthViewModel
import com.swag.vyom.viewmodels.TicketViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
    navController: NavHostController,
    ticketViewModel: TicketViewModel,
    authVM: AuthViewModel

) {
    NavHost(
        navController = navController,
        startDestination = "splash_screen"
    ) {
        composable("splash_screen") {
            SplashScreen(navController)
        }
        composable("customer_verification") {
            NumberVerificationScreen(navController, authVM)
        }
        composable("face_auth") {
            FaceAuth(navController)
        }
        composable("home_screen") {
            HomeScreen(navController)
        }
        composable("ticket_screen") {
            TicketGenerationScreen(ticketViewModel = ticketViewModel, navController = navController, onBackClick = {
                navController.navigateUp()
            })
        }
        composable("register_screen") {
            RegistrationScreen(navController, authVM)
        }
        composable("login_screen"){
            LoginScreen(navController = navController,
                authVM = authVM)
        }


    }
}