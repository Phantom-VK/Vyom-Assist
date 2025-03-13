package com.swag.vyom.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.swag.vyom.ui.screens.ChatScreen
import com.swag.vyom.ui.screens.CustomerSupportScreen
import com.swag.vyom.ui.screens.FaceAuth
import com.swag.vyom.ui.screens.HomeScreen
import com.swag.vyom.ui.screens.LoginScreen
import com.swag.vyom.ui.screens.MyTicketsScreen
import com.swag.vyom.ui.screens.NumberVerificationScreen
import com.swag.vyom.ui.screens.RegistrationScreen
import com.swag.vyom.ui.screens.SplashScreen
import com.swag.vyom.ui.screens.TicketGenerationScreen
import com.swag.vyom.viewmodels.AuthViewModel
import com.swag.vyom.viewmodels.TicketViewModel
import com.swag.vyom.viewmodels.UserViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Navigation(
    navController: NavHostController,
    ticketViewModel: TicketViewModel,
    authVM: AuthViewModel,
    userVM: UserViewModel

) {
    NavHost(
        navController = navController,
        startDestination = "splash_screen"
    ) {
        composable("splash_screen") {
            SplashScreen(navController)
        }
        composable("customer_verification") {
            NumberVerificationScreen(navController, authVM, userVM)
        }
        composable("face_auth") {
            FaceAuth(navController)
        }
        composable("home_screen") {
            HomeScreen(navController)
        }
        composable("ticket_screen") {
            TicketGenerationScreen(ticketViewModel = ticketViewModel,userVM = userVM, navController = navController)
        }
        composable("register_screen") {
            RegistrationScreen(navController, authVM)
        }
        composable("login_screen"){
            LoginScreen(navController = navController,
                authVM = authVM)
        }
        composable("support_screen"){
            CustomerSupportScreen(
                navController = navController
            ){
                navController.navigateUp()
            }
        }
        composable("myticket_screen") {

            //TODO Implement Fetch TIckets and pass ticket list
            MyTicketsScreen(
                navController

            )
        }
        composable("chatbot_screen") {
            ChatScreen(){
                navController.navigateUp()
            }
        }


    }
}