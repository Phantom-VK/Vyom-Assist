package com.swag.vyom.dataclasses

data class SupportTicket(
    val ticket_id: String,
    val ticket_created_at: String,
    val category: String,
    val preferred_support_mode: String,
    val status: String,
    val sub_category: String,
    val urgency_level: String,
    val connection_way: String
)


data class SupportTicketResponse(
    val success: Boolean,
    val msg: String?,
    val data: List<SupportTicket>
)

