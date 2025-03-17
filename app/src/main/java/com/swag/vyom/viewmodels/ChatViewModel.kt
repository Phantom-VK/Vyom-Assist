package com.swag.vyom.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swag.vyom.api.ApiClient
import com.swag.vyom.dataclasses.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> get() = _chatMessages

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
}