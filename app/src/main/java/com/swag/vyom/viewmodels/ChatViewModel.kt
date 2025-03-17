package com.swag.vyom.viewmodels


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swag.vyom.api.ApiClient
import com.swag.vyom.dataclasses.ChatMessage
import com.swag.vyom.dataclasses.SendMessageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.apache.http.HttpException
import retrofit2.awaitResponse
import java.io.IOException

class ChatViewModel : ViewModel() {

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> get() = _chatMessages
    private var lastMessageId: Int? = null

    fun getMessages(conversationId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    val response = ApiClient.instance.getMessages(conversationId, lastMessageId ?: 0)
                    if (response.status == "success") {
                        val newMessages = response.messages
                        if (newMessages.isNotEmpty()) {
                            lastMessageId = newMessages.last().id
                            _chatMessages.emit(_chatMessages.value + newMessages)
                        }
                    }
                } catch (e: IOException) {
                    Log.e("ChatViewModel", "Network error while fetching messages", e)
                } catch (e: HttpException) {
                    Log.e("ChatViewModel", "HTTP error while fetching messages", e)
                }

                delay(3000) // Poll every 3 seconds
            }
        }
    }



    fun fetchMessages(conversationId: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.instance.fetchChatHistory(conversationId)
                if (response.status == "success") {
                    _chatMessages.value = response.messages
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(senderId: Int, receiverId: Int, message: String, conversationId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = SendMessageRequest(senderId, receiverId, message, conversationId)
                val response = ApiClient.instance.sendMessage(request).awaitResponse()
                if (response.isSuccessful && response.body()?.success == true) {
                    Log.d("ChatViewModel", "Message sent successfully: ${response.body()?.message}")
                } else {
                    Log.e("ChatViewModel", "Failed to send message: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending message", e)
            }
        }
    }
}