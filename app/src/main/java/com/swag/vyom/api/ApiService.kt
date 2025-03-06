package com.swag.vyom.api

import com.swag.vyom.dataclasses.ApiTicketResponse
import com.swag.vyom.dataclasses.ChatRequest
import com.swag.vyom.dataclasses.ChatResponse
import com.swag.vyom.dataclasses.Ticket
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {


    @POST("ticket_gen.php")
    suspend fun generateTicket(
        @Body ticket: Ticket
    ): ApiTicketResponse




}

interface ChatbotApiService {
    @POST("chat")
    suspend fun sendMessage(
        @Body request: ChatRequest
    ): ChatResponse
}