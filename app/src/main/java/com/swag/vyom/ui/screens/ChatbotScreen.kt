package com.swag.vyom.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.dialogflow.v2.*
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
fun ChatbotScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var userMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()

    // Scroll to the bottom whenever a new message is added
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            lazyListState.scrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
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
        },
        bottomBar = {
            BottomChatInput(
                value = userMessage,
                onValueChange = { userMessage = it },
                onSendClick = {
                    if (userMessage.isNotEmpty()) {
                        val message = ChatMessage(userMessage, true)
                        messages.add(message)
                        userMessage = ""
                        isLoading = true

                        scope.launch {
                            val botReply = sendMessageToDialogflow(message.content, context)
                            messages.add(ChatMessage(botReply, false))
                            isLoading = false
                        }
                    }
                },
                isLoading = isLoading
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            state = lazyListState,
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    ChatMessageItem(message = message)
                }
            }
        }
    }
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
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
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
    onSendClick: () -> Unit,
    isLoading: Boolean
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
                    Text("Type a message...")
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
                    .background(Color(0xFF0066CC)),
                enabled = !isLoading
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
            "Sorry, I couldn't process your request. Please try again later."
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatbotScreen()
}