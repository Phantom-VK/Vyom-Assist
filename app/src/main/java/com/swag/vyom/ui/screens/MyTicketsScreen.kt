package com.swag.vyom.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.swag.vyom.ui.components.SupportTicketItem
import com.swag.vyom.viewmodels.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTicketsScreen(
    navController: NavController,
    ticketViewModel: TicketViewModel,
) {
    //TODO Implement Fetch TIckets and pass ticket list

    LaunchedEffect(key1 = true) {
        ticketViewModel.fetchTicketsByUserId()
    }

    val tickets by ticketViewModel.tickets.collectAsState()
    val error by ticketViewModel.error.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(

                title = { Text(text = "My Tickets") },
                        navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()

                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        },

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
                    tickets.forEach {  ticket ->
                        item {
                                SupportTicketItem(
                                    ticketId = ticket.ticket_id.toString(),
                                    dateTime = ticket.ticket_created_at,
                                    category = ticket.category,
                                    supportMode = ticket.preferred_support_mode,
                                    status = ticket.status,
                                    subCategory = ticket.sub_category,
                                    urgencyLevel = ticket.urgency_level,
                                    connectionWay = ticket.connection_way,
                                    navController = navController
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            if (error != null){
                Text(text = error!!)
            }
        }
    }
}