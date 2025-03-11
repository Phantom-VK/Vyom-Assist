package com.swag.vyom.dataclasses

data class SupportTicket(
    val ticketId: String,
    val dateTime: String,
    val category: String,
    val supportMode: String,
    val status: String,
    val subCategory: String,
    val urgencyLevel: String
)