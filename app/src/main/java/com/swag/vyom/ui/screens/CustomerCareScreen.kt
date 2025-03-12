package com.swag.vyom.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.swag.vyom.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSupportScreen(
    navController: NavController,
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CustomerSupportTopAppBar(onBackClick = onBackClick)
        }
    ) { paddingValues ->
        CustomerSupportContent(
            paddingValues = paddingValues,
            navController = navController
        )
    }
}

@Composable
private fun CustomerSupportContent(
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(16.dp), // Add padding for better spacing
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        QuickTaskCard(
            iconResId = R.drawable.ic_support,
            title = "Create Ticket",
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .padding(8.dp) // Add padding between cards
        ) {
            navController.navigate("ticket_screen")
        }

        QuickTaskCard(
            iconResId = R.drawable.ic_support,
            title = "My Tickets",
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .padding(8.dp) // Add padding between cards
        ) {
            navController.navigate("myticket_screen")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomerSupportTopAppBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "VyomAI",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color(0xFF0066CC),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}