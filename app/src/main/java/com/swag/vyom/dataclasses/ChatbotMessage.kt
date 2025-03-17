package com.swag.vyom.dataclasses

import java.util.Date

data class ChatBotMessage(
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Date = Date()
)