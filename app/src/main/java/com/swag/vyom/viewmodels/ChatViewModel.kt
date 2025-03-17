package com.swag.vyom.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swag.vyom.api.ApiClient
import com.swag.vyom.dataclasses.ChatMessage
import com.swag.vyom.dataclasses.SendMessageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import java.text.SimpleDateFormat
import java.util.Date

class ChatViewModel : ViewModel() {

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> get() = _chatMessages
    private var lastMessageId: Int? = null
    private var pollingJob: Job? = null

    fun startPolling(conversationId: Int) {
        stopPolling() // Prevent duplicate polling
        pollingJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    val response = ApiClient.instance.getMessages(conversationId, lastMessageId ?: 0)
                    if (response.status == "success") {
                        val newMessages = response.messages.filter { newMsg ->
                            _chatMessages.value.none { it.id == newMsg.id } // Avoid duplicates
                        }
                        if (newMessages.isNotEmpty()) {
                            lastMessageId = newMessages.last().id
                            _chatMessages.update { it + newMessages }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "Error polling messages", e)
                }
                delay(3000)
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    fun fetchMessages(conversationId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.instance.fetchChatHistory(conversationId)
                if (response.status == "success") {
                    _chatMessages.value = response.messages
                    lastMessageId = response.messages.lastOrNull()?.id
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error fetching chat history", e)
            }
        }
    }

    fun sendMessage(senderId: Int, receiverId: Int, message: String, conversationId: Int) {
        val tempMessage = ChatMessage(
            id = -1, // Temporary ID until server responds
            sender_id = senderId,
            message = message,
            sent_at = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
            conversation_id = conversationId
        )

        // Optimistically add the message to the UI
        _chatMessages.update { it + tempMessage }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = SendMessageRequest(senderId, receiverId, message, conversationId)
                val response = ApiClient.instance.sendMessage(request).awaitResponse()
                if (response.isSuccessful && response.body()?.status == "success") {

                    val serverMessage = ChatMessage(
                        id = response.body()?.message_id ?: -1,
                        sender_id = senderId,
                        message = message,
                        sent_at = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                        conversation_id = conversationId
                    )
                    // Replace the temporary message with the server-confirmed one
                    _chatMessages.update { currentMessages ->
                        currentMessages.filter { it.id != -1 } + serverMessage
                    }
                    lastMessageId = serverMessage.id
                    Log.d("ChatViewModel", "Message sent successfully: ${response.body()?.message}")
                } else {
                    // Remove the temp message on failure
                    _chatMessages.update { it.filter { msg -> msg.id != -1 } }
                    Log.e("ChatViewModel", "Failed to send message: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _chatMessages.update { it.filter { msg -> msg.id != -1 } }
                Log.e("ChatViewModel", "Error sending message", e)
            }
        }
    }

    override fun onCleared() {
        stopPolling()
        super.onCleared()
    }
}