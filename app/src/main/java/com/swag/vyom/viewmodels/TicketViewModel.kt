package com.swag.vyom.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swag.vyom.api.RetrofitClient
import com.swag.vyom.dataclasses.PriorityLevel
import com.swag.vyom.dataclasses.SupportMode
import com.swag.vyom.dataclasses.Ticket
import com.swag.vyom.dataclasses.UrgencyLevel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TicketViewModel: ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun createTicket(ticket: Ticket) {
        viewModelScope.launch {
            try {
                // Create a Ticket object
//                val ticket1 = Ticket(
//                    user_id = 1,
//                    category = "Loan Assistance",
//                    sub_category = "Home Loan Inquiry",
//                    urgency_level = UrgencyLevel.High.toString(),
//                    preferred_support_mode = SupportMode.Video_Call.toString(),
//                    available_timedate = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
//                    language_preference = "English",
//                    description = "I need information about home loan interest rates and eligibility.",
//                    audio_file_link = "https://example.com/audio123.mp3",
//                    video_file_link = "https://example.com/video123.mp4",
//                    attached_image_link = "https://example.com/image123.jpg",
//                    assigned_department = "Loan Department",
//                    priority_level = PriorityLevel.VIP.toString()
//                )

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
}