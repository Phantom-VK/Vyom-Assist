package com.swag.vyom.dataclasses

data class SendMessageResponse(
    val status: String,
    val conversation_id: Int,
    val message_id: Int,
    val message: String
)
