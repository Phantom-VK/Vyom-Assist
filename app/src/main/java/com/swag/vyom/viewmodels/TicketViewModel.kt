package com.swag.vyom.viewmodels

import android.content.Context
import android.telecom.VideoProfile.isVideo
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.swag.vyom.api.RetrofitClient
import com.swag.vyom.dataclasses.Ticket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.coroutines.resume

class TicketViewModel: ViewModel() {

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


    fun uploadFile(
        file: File,
        isVideo: Boolean,
        onCompletion: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Compress video if it's a large file
                val uploadFile = if (file.length() > 10 * 1024 * 1024 && isVideo) { // 10 MB
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