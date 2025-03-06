package com.swag.vyom.api

import com.swag.vyom.dataclasses.ApiTicketResponse
import com.swag.vyom.dataclasses.ChatRequest
import com.swag.vyom.dataclasses.ChatResponse
import com.swag.vyom.dataclasses.Ticket
import com.swag.vyom.dataclasses.UserLoginRequest
import com.swag.vyom.dataclasses.UserLoginResponse
import com.swag.vyom.dataclasses.UserRegistrationRequest
import com.swag.vyom.dataclasses.UserRegistrationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {


    @POST("ticket_gen.php")
    suspend fun generateTicket(
        @Body ticket: Ticket
    ): ApiTicketResponse

    @POST("register.php")
    suspend fun register(
        @Body userRegistrationRequest: UserRegistrationRequest
    ): UserRegistrationResponse

    @POST("login.php")
    suspend fun login(
        @Body userLoginRequest: UserLoginRequest
    ): UserLoginResponse




}

interface ChatbotApiService {
    @POST("chat")
    suspend fun sendMessage(
        @Body request: ChatRequest
    ): ChatResponse
}