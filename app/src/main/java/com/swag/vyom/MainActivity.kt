package com.swag.vyom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swag.vyom.ui.screens.FaceAuth
import com.swag.vyom.ui.screens.HomeScreen
import com.swag.vyom.ui.screens.SigninScreen
import com.swag.vyom.ui.screens.SplashScreen
import com.swag.vyom.ui.theme.VyomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VyomTheme {

                val navController = rememberNavController()  

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
        }
    }
}
