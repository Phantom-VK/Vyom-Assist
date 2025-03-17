package com.swag.vyom.api

import com.swag.vyom.dataclasses.ApiTicketResponse
import com.swag.vyom.dataclasses.ChatResponse
import com.swag.vyom.dataclasses.CheckCustomerResponse
import com.swag.vyom.dataclasses.FaceCompareResponse
import com.swag.vyom.dataclasses.FileUploadResponse
import com.swag.vyom.dataclasses.SupportTicketResponse
import com.swag.vyom.dataclasses.Ticket
import com.swag.vyom.dataclasses.UserDetailsResponse
import com.swag.vyom.dataclasses.UserImageUploadResponse
import com.swag.vyom.dataclasses.UserLoginRequest
import com.swag.vyom.dataclasses.UserLoginResponse
import com.swag.vyom.dataclasses.UserRegistrationRequest
import com.swag.vyom.dataclasses.UserRegistrationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

    @GET("user/fetch_user_primary_details.php")
    suspend fun getUserDetails(
        @Query("mobile_number") mobileNumber: String? = null,
        @Query("aadhaar") aadhaar: String? = null
    ): UserDetailsResponse


    @Multipart
    @POST("user/file_upload.php")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): FileUploadResponse

    @Multipart
    @POST("user/upload_faceimg.php")
    suspend fun uploadUserImage(
        @Part("aadhaar") aadhaar: String,
        @Part file: MultipartBody.Part
    ): UserImageUploadResponse

    @GET("user/fetch_tickets_by_user.php")
    suspend fun fetchTicketsByUserId(
        @Query("user_id") userId: Int
    ): SupportTicketResponse


    @Multipart
    @POST("/compare_faces")
    suspend fun compareFaces(
        @Part("image_url") imageUrl: RequestBody,
        @Part image2: MultipartBody.Part
    ): FaceCompareResponse

    @GET("fetch_msg_history.php")
    suspend fun fetchChatHistory(
        @Query("conversation_id") conversationId: Int
    ): ChatResponse

}

