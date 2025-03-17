package com.swag.vyom.ui.screens

import androidx.compose.ui.tooling.preview.Preview


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.dialogflow.v2.DetectIntentRequest
import com.google.cloud.dialogflow.v2.QueryInput
import com.google.cloud.dialogflow.v2.SessionName
import com.google.cloud.dialogflow.v2.SessionsClient
import com.google.cloud.dialogflow.v2.SessionsSettings
import com.google.cloud.dialogflow.v2.TextInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Date = Date()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBackClick: () -> Unit = {}
) {
    ChatScreen()

//    val messages = remember { mutableStateListOf(
//        ChatMessage("Hi Kitsbase, Let me know you need help and you can ask us any questions.", false, Date(System.currentTimeMillis() - 60000)),
//        ChatMessage("How to create a FinX Stock account?", true, Date())
//    )}
//
//    var messageText by remember { mutableStateOf("") }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = "VyomAI",
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 24.sp,
//                        color = Color(0xFF0066CC),
//                        textAlign = TextAlign.Center,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Go back"
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.White
//                )
//            )
//        },
//        bottomBar = {
//            BottomChatInput(
//                value = messageText,
//                onValueChange = { messageText = it },
//                onSendClick = {
//                    if (messageText.isNotEmpty()) {
//                        messages.add(ChatMessage(messageText, true))
//                        messageText = ""
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(horizontal = 16.dp),
//            reverseLayout = false
//        ) {
//            items(messages) { message ->
//                ChatMessageItem(message = message)
//            }
//
//            // Add empty space at the bottom for better scrolling experience
//            item {
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//        }
//    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val timeString = dateFormat.format(message.timestamp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isFromUser) 16.dp else 0.dp,
                        bottomEnd = if (message.isFromUser) 0.dp else 16.dp
                    )
                )
                .background(
                    if (message.isFromUser) Color(0xFFD7EAFF) else Color(0xFFF0F0F0)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                color = Color.Black,
                fontSize = 16.sp
            )
        }

        Text(
            text = timeString,
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun BottomChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text("Message")
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE6F2FF),
                    unfocusedContainerColor = Color(0xFFE6F2FF),
                    disabledContainerColor = Color(0xFFE6F2FF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSendClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0066CC))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message",
                    tint = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen()
}


data class Message(val text: String, val isUser: Boolean)

@Composable
fun ChatScreen() {
    var userMessage by remember { mutableStateOf(TextFieldValue("")) }
    val messages = remember { mutableStateListOf<Message>() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                ChatBubble(message)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = userMessage,
                onValueChange = { userMessage = it },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.LightGray, RoundedCornerShape(16.dp))
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    val text = userMessage.text.trim()
                    if (text.isNotEmpty()) {
                        messages.add(Message(text, isUser = true))
                        userMessage = TextFieldValue("")

                        scope.launch {
                            val botReply = sendMessageToDialogflow(text, context)
                            messages.add(Message(botReply, isUser = false))
                        }
                    }
                }
            ) {
                Text("Send")
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (message.isUser) Color.Blue else Color.Gray,
                    RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

suspend fun sendMessageToDialogflow(query: String, context: android.content.Context): String {
    return withContext(Dispatchers.IO) {
        try {
            val stream = context.assets.open("dialogflow-key.json")
            val credentials = GoogleCredentials.fromStream(stream)
                .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))

            val sessionsSettings = SessionsSettings.newBuilder()
                .setCredentialsProvider { credentials }
                .build()

            val sessionsClient = SessionsClient.create(sessionsSettings)
            val session = SessionName.of("bank-chatbot-eqxe", UUID.randomUUID().toString())

            val textInput = TextInput.newBuilder().setText(query).setLanguageCode("en-US").build()
            val queryInput = QueryInput.newBuilder().setText(textInput).build()
            val request = DetectIntentRequest.newBuilder().setSession(session.toString()).setQueryInput(queryInput).build()

            val response = sessionsClient.detectIntent(request)
            response.queryResult.fulfillmentText
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
