package com.swag.vyom

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
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


    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS, 0
            )
        }

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

