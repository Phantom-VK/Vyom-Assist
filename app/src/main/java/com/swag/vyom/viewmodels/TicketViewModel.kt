package com.swag.vyom.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swag.vyom.api.RetrofitClient
import com.swag.vyom.dataclasses.Ticket
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

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
        onCompletion: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = RetrofitClient.instance.uploadFile(multipartBody)
                if (response.success) {
                    val data = response.data
                    Log.d("TicketViewModel", "File Uploaded Successfully: ${data.file_url}")
                    onCompletion(data.file_url)
                } else {
                    Log.e("TicketViewModel", "Error uploading file: ${response.msg}")
                }
            } catch (e: Exception) {
                Log.e("TicketViewModel", "Exception: ${e.message} ${e.cause}")
            }
        }
    }


}