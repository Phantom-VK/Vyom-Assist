package com.swag.vyom.api

import com.swag.vyom.dataclasses.ApiTicketResponse
import com.swag.vyom.dataclasses.ChatRequest
import com.swag.vyom.dataclasses.ChatResponse
import com.swag.vyom.dataclasses.Ticket
import com.swag.vyom.dataclasses.UserLoginRequest
import com.swag.vyom.dataclasses.UserLoginResponse
import com.swag.vyom.dataclasses.UserRegistrationRequest
import com.swag.vyom.dataclasses.UserRegistrationResponse
import com.swag.vyom.dataclasses.CheckCustomerResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {


    @POST("user/ticket_gen.php")
    suspend fun generateTicket(
        @Body ticket: Ticket
    ): ApiTicketResponse

    @POST("user/register.php")
    suspend fun register(
        @Body userRegistrationRequest: UserRegistrationRequest
    ): UserRegistrationResponse

    @POST("user/login.php")
    suspend fun login(
        @Body userLoginRequest: UserLoginRequest
    ): UserLoginResponse


    @GET("bankdb/check_customer.php")
    suspend fun checkCustomer(
        @Query("mobile_number") mobileNumber: String? = null,
        @Query("aadhaar") aadhaar: String? = null
    ): CheckCustomerResponse



}

interface ChatbotApiService {
    @POST("chat")
    suspend fun sendMessage(
        @Body request: ChatRequest
    ): ChatResponse
}