package com.swag.vyom.dataclasses

data class SendMessageRequest(
    val sender_id: Int,
    val receiver_id: Int,
    val message: String,
    val conversation_id: Int
)


