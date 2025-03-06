package com.swag.vyom

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.swag.vyom.ui.navigation.Navigation
import com.swag.vyom.ui.theme.VyomTheme
import com.swag.vyom.viewmodels.AuthViewModel
import com.swag.vyom.viewmodels.TicketViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VyomTheme {

                val navController = rememberNavController()
                val ticketViewModel = TicketViewModel()
                val authVM = AuthViewModel()

              Navigation(
                  navController = navController,
                  ticketViewModel = ticketViewModel,
                  authVM = authVM

              )

            }
        }
    }
}

