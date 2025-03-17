package com.swag.vyom.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.swag.vyom.SharedPreferencesHelper
import com.swag.vyom.ui.screens.ChatScreen
import com.swag.vyom.ui.screens.ChatbotScreen
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
    userVM: UserViewModel,
    preferencesHelper: SharedPreferencesHelper

) {
    NavHost(
        navController = navController,
        startDestination = "splash_screen"
    ) {
        composable("splash_screen") {
            SplashScreen(navController)
        }
        composable("customer_verification") {
            NumberVerificationScreen(navController, authVM, userVM, preferencesHelper)
        }
        composable("face_auth") {
            FaceAuth(navController, authVM)
        }
        composable("home_screen") {
            HomeScreen(navController, userVM = userVM, authVM = authVM, preferencesHelper = preferencesHelper )
        }
        composable("ticket_screen") {
            TicketGenerationScreen(ticketViewModel = ticketViewModel,userVM = userVM, navController = navController)
        }
        composable("register_screen") {
            RegistrationScreen(navController, authVM, preferencesHelper, ticketVM = ticketViewModel)
        }
        composable("login_screen"){
            LoginScreen(navController = navController,
                authVM = authVM,
                preferencesHelper = preferencesHelper)
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
                navController,
                ticketViewModel
            )
        }
        composable("chatbot_screen") {
            ChatbotScreen(){
                navController.navigateUp()
            }
        }
        composable(
            route = "chatting_screen/{ticketId}",
            arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId")
            ChatScreen(conversationId = ticketId!!.toInt())
        }


    }
}