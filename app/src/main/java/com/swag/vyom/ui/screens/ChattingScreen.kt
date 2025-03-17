package com.swag.vyom.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.swag.vyom.dataclasses.ChatMessage
import com.swag.vyom.viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(conversationId: Int, chatViewModel: ChatViewModel = viewModel()) {
    val messages by chatViewModel.chatMessages.collectAsState()

    LaunchedEffect(Unit) {
        chatViewModel.fetchMessages(conversationId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Chat") })

        LazyColumn(
            modifier = Modifier.weight(1f).padding(8.dp),
            reverseLayout = true
        ) {
            items(messages) { message ->
                ChatBubble(message)
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Card(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Sender: ${message.sender_id}", )
            Text(text = message.message,)
            Text(text = message.sent_at, )
        }
    }
}
