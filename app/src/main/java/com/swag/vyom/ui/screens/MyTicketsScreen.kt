package com.swag.vyom.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTicketsScreen(
    navController: NavController,
    tickets: List<SupportTicket> = emptyList<SupportTicket>()
) {
    //TODO Implement Fetch TIckets and pass ticket list
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "My Tickets") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Navigate to create ticket screen */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Ticket")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (tickets.isEmpty()) {
                Text(
                    text = "No tickets found.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(tickets) { ticket ->
                        SupportTicketItem(
                            ticketId = ticket.ticketId.toString(),
                            dateTime = ticket.dateTime,
                            category = ticket.category,
                            supportMode = ticket.supportMode,
                            status = ticket.status,
                            subCategory = ticket.subCategory,
                            urgencyLevel = ticket.urgencyLevel,
                            onContactSupport = { /* Handle contact support action */ }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}