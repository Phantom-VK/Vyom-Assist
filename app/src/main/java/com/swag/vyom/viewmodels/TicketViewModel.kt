package com.swag.vyom.viewmodels

import android.R.attr.category
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.swag.vyom.SharedPreferencesHelper
import com.swag.vyom.api.RetrofitClient
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

    fun fetchTicketsByUserId() {
        viewModelScope.launch {
            try {
                val userId = preferenceHelper.getid() // Get user ID from preferences
                if (userId != null) {
                    val response = RetrofitClient.instance.fetchTicketsByUserId(userId)
                    if (response.success) {
                        val fetchedTickets = response.data.map { ticketResponse ->
                            SupportTicket(
                                ticket_id = ticketResponse.ticket_id ,
                                ticket_created_at = ticketResponse.ticket_created_at,
                                category = ticketResponse.category,
                                preferred_support_mode = ticketResponse.preferred_support_mode,
                                status = ticketResponse.status,
                                sub_category = ticketResponse.sub_category,
                                urgency_level = ticketResponse.urgency_level
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
                val response = RetrofitClient.instance.generateTicket(ticket)

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
                val response = RetrofitClient.instance.uploadUserImage(aadhaar, multipartBody)
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
        file: File,
        isVideo: Boolean,
        onCompletion: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("TicketViewModel", "File size: ${file.length()}")
                // Compress video if it's a large file
                val uploadFile = if (isVideo && file.length() > 10 * 1024 * 1024 ) { // 10 MB
                    // Compress video before upload
                    compressVideo(file) ?: file // Fallback to original file if compression fails
                } else {
                    file
                }

                // Prepare the file for upload
                val requestFile = uploadFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("file", uploadFile.name, requestFile)

                // Upload the file
                val response = RetrofitClient.instance.uploadFile(multipartBody)
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


    // Function to compress video using FFmpegKit
    private fun compressVideo(file: File): File? {

        Log.d("TicketViewModel", "File size: ${file.length()}" +
                "Inside File Compression")
            val outputFile = File(file.parent, "compressed_${file.name}")

            try {
                // FFmpeg command to compress the video
                val command = "-i ${file.absolutePath} -vf scale=640:360 -c:v libx264 -crf 28 ${outputFile.absolutePath}"

                // Execute FFmpeg command synchronously
                val session = FFmpegKit.execute(command)

                // Check if the command was successful
                if (ReturnCode.isSuccess(session.returnCode)) {
                    Log.d("VideoCompression", "Video compressed successfully: ${outputFile.absolutePath}")
                    outputFile // Return the compressed file
                } else {
                    Log.e("VideoCompression", "Video compression failed: ${session.failStackTrace}")
                    null // Return null if compression fails
                }
            } catch (e: Exception) {
                Log.e("VideoCompression", "Exception during video compression: ${e.message}")
                null // Return null if an exception occurs
            }

        return file

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