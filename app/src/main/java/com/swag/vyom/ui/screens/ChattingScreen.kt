package com.swag.vyom.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.swag.vyom.SharedPreferencesHelper
import com.swag.vyom.dataclasses.ChatMessage
import com.swag.vyom.viewmodels.ChatViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(conversationId: Int, chatViewModel: ChatViewModel = viewModel()) {
    val messages by chatViewModel.chatMessages.collectAsState(initial = emptyList())
    val context = LocalContext.current
    val sharedPreferencesHelper = remember { SharedPreferencesHelper(context) }
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Fetch messages initially
    LaunchedEffect(conversationId) {
        chatViewModel.fetchMessages(conversationId)
    }

    LaunchedEffect(conversationId) {
        while (true) {
            chatViewModel.getMessages(conversationId)
            delay(3000)
        }
    }

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Chat") })

        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f),
            state = listState
        ) {
            items(messages) { message ->
                ChatBubble(message, sharedPreferencesHelper)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                label = { Text("Enter message...") }
            )

            Button(
                onClick = {
                    val senderId = sharedPreferencesHelper.getid() ?: return@Button
                    val receiverId = 1
                    if (messageText.isNotBlank()) {
                        chatViewModel.sendMessage(senderId, receiverId, messageText, conversationId)
                        messageText = ""
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Send")
            }
        }
    }
}



@Composable
fun ChatBubble(message: ChatMessage, sharedPreferencesHelper: SharedPreferencesHelper) {
    val loggedInUserId = sharedPreferencesHelper.getid()
    val isSentByUser = loggedInUserId == message.sender_id

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.padding(8.dp),
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                if (!isSentByUser) {
                    Text(text = "Sender: ${message.sender_id}")
                }
                Text(text = message.message)
                Text(text = message.sent_at)
            }
        }
    }
}
