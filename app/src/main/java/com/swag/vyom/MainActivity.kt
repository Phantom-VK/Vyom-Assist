package com.swag.vyom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.swag.vyom.ui.navigation.Navigation
import com.swag.vyom.ui.theme.VyomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VyomTheme {

                val navController = rememberNavController()  

              Navigation(
                  navController = navController
              )

            }
        }
    }
}
