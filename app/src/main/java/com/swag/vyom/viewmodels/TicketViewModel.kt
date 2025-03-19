package com.swag.vyom.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swag.vyom.SharedPreferencesHelper
import com.swag.vyom.api.ApiClient
import com.swag.vyom.dataclasses.RatingRequest
import com.swag.vyom.dataclasses.RatingResponse
import com.swag.vyom.dataclasses.SupportTicket
import com.swag.vyom.dataclasses.Ticket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.coroutines.resume

class TicketViewModel(private val preferenceHelper: SharedPreferencesHelper): ViewModel() {

    private val _tickets = MutableStateFlow<List<SupportTicket>>(emptyList())
    val tickets: StateFlow<List<SupportTicket>> = _tickets

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _ratingResponse = MutableStateFlow<RatingResponse?>(null)
    val ratingResponse: StateFlow<RatingResponse?> get() = _ratingResponse

    suspend fun giveRating(agentId: Int, ticketId: Int, rating: Float) {
        try {
            val response = ApiClient.instance.giveRating(RatingRequest(agentId, ticketId, rating))
            _ratingResponse.value = response
        } catch (e: Exception) {
            // Handle error
            _ratingResponse.value = RatingResponse(false, e.message, null)
        }
    }


    fun fetchTicketsByUserId() {
        viewModelScope.launch {
            try {
                val userId = preferenceHelper.getid() // Get user ID from preferences
                if (userId != null) {
                    val response = ApiClient.instance.fetchTicketsByUserId(userId)
                    if (response.success) {
                        val fetchedTickets = response.data.map { ticketResponse ->
                            SupportTicket(
                                ticket_id = ticketResponse.ticket_id,
                                ticket_created_at = ticketResponse.ticket_created_at,
                                category = ticketResponse.category,
                                preferred_support_mode = ticketResponse.preferred_support_mode,
                                status = ticketResponse.status,
                                sub_category = ticketResponse.sub_category,
                                urgency_level = ticketResponse.urgency_level,
                                connection_way = ticketResponse.connection_way,
                                assigned_agent_id = ticketResponse.assigned_agent_id,
                                isRated = ticketResponse.isRated
                            )
                        }
                        _tickets.value = fetchedTickets
                        Log.d("TicketViewModel", "Tickets fetched successfully: ${fetchedTickets.size}")

                    } else {
                        _error.value = response.msg ?: "Unknown error"
                        Log.e("TicketViewModel", "Error fetching tickets: ${response.msg}")
                    }
                } else {
                    _error.value = "User ID not found"
                    Log.e("TicketViewModel", "User ID not found in preferences")
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown exception"
                Log.e("TicketViewModel", "Exception: ${e.message} ${e.cause}")
            }
        }
    }


    fun createTicket(ticket: Ticket) {
        viewModelScope.launch {
            try {
                // Make the API call
                val response = ApiClient.instance.generateTicket(ticket)

                if (response.success) {
                    val createdTicket = response.data
                    Log.d("TicketViewModel", "Ticket created: ${createdTicket.ticket_id}")
                } else {
                    Log.e("TicketViewModel", "Error creating ticket: ${response.msg}")
                }
            } catch (e: Exception) {
                Log.e("TicketViewModel", "Exception: ${e.message }\n${e.cause}")
            }
        }
    }

    fun uploadUserImage(
        aadhaar: String,
        file: File,
        onCompletion: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Prepare Aadhaar number as a RequestBody

                // Prepare file for upload
                val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestFile)

                // Upload the file
                val response = ApiClient.instance.uploadUserImage(aadhaar, multipartBody)
                if (response.success) {
                    Log.d("UserViewModel", "File Uploaded Successfully: ${response.file_url}")
                    onCompletion(response.file_url ?: "")
                } else {
                    Log.e("UserViewModel", "Error uploading file: ${response.msg}")
                    onCompletion("")  // Return empty string on error
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Exception: ${e.message} ${e.cause}")
                onCompletion("")  // Return empty string on exception
            }
        }
    }


    fun uploadFile(
        uploadFile: File,
        isVideo: Boolean,
        onCompletion: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("TicketViewModel", "File size: ${uploadFile.length()}")



                // Prepare the file for upload
                val requestFile = uploadFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("file", uploadFile.name, requestFile)

                // Upload the file
                val response = ApiClient.instance.uploadFile(multipartBody)
                if (response.success) {
                    val data = response.data
                    Log.d("TicketViewModel", "File Uploaded Successfully: ${data.file_url}")
                    onCompletion(data.file_url)
                } else {
                    Log.e("TicketViewModel", "Error uploading file: ${response.msg}")
                    onCompletion("")  // Return empty string on error
                }
            } catch (e: Exception) {
                Log.e("TicketViewModel", "Exception: ${e.message} ${e.cause}")
                onCompletion("")  // Return empty string on exception
            }
        }
    }


    // New suspend function that returns a result
    suspend fun uploadFileCoroutine(file: File, isVideo: Boolean): String {
        return suspendCancellableCoroutine { continuation ->
            uploadFile(file, isVideo) { url ->
                continuation.resume(url)
            }
        }
    }
}