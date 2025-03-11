package com.swag.vyom.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.swag.vyom.dataclasses.SupportTicket
import com.swag.vyom.ui.components.SupportTicketItem

@Composable
fun MyTicketsScreen(
    navController: NavController
){

//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text(text = "My Tickets") }
//            )
//        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { /* Handle FAB click (e.g., navigate to create ticket) */ }
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Add Ticket")
//            }
//        }
//    ) { innerPadding ->
//
////        LazyColumn(
////            modifier = Modifier.padding(innerPadding)
////        ) {
////                SupportTicketItem(
////                    ticketId = ticket.ticketId,
////                    dateTime = ticket.dateTime,
////                    category = ticket.category,
////                    supportMode = ticket.supportMode,
////                    status = ticket.status,
////                    subCategory = ticket.subCategory,
////                    urgencyLevel = ticket.urgencyLevel,
////                    onContactSupport = { /* Handle contact support action */ }
////                )
////                Spacer(modifier = Modifier.height(8.dp))
////
////
//
//    }
    Column(modifier = Modifier.padding(16.dp)) {
        SupportTicketItem(
            ticketId = "1414",
            dateTime = "10:25:01",
            category = "Loan",
            supportMode = "Video",
            status = "Active",
            subCategory = "Loan",
            urgencyLevel = "Low",
            onContactSupport = { /* Handle contact support action */ }
        )
    }
}