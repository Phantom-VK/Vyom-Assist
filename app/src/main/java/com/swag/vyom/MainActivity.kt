package com.swag.vyom

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import com.swag.vyom.audioPlayer.AndroidAudioPlayer
import com.swag.vyom.audioRecorder.AndroidAudioRecorder
import com.swag.vyom.ui.navigation.Navigation
import com.swag.vyom.ui.theme.VyomTheme
import com.swag.vyom.viewmodels.AuthViewModel
import com.swag.vyom.viewmodels.TicketViewModel
import com.swag.vyom.viewmodels.UserViewModel

class MainActivity : ComponentActivity() {

    private val ticketViewModel by lazy { TicketViewModel()}
    private val authVM by lazy {  AuthViewModel()}
    private val sharedPreferencesHelper by lazy { SharedPreferencesHelper(this) }
    private val userVM by lazy { UserViewModel(sharedPreferencesHelper) }




    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.RECORD_AUDIO),
            0
        )
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            VyomTheme {

              Navigation(
                  navController = navController,
                  ticketViewModel = ticketViewModel,
                  authVM = authVM,
                  userVM = userVM

              )

            }
        }
    }
}

