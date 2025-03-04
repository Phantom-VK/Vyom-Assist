package com.swag.vyom.dataclasses

data class ApiTicketResponse(
    val success:Boolean,
    val msg:String,
    val data:TicketResponse
)

data class TicketResponse(
    val ticket_id:Int,
    val user_id:String
)

