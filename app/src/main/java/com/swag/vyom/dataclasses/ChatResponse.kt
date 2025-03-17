package com.swag.vyom.dataclasses

import com.swag.vyom.ui.screens.ChatBotMessage

data class ChatResponse(
    val status: String,
    val messages: List<ChatMessage>
)


data class ChatMessage(
    val id: Int,
    val conversation_id: Int,
    val sender_id: Int,
    val message: String,
    val sent_at: String
)