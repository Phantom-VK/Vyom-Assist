package com.swag.vyom.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swag.vyom.api.ChatbotApiService
import com.swag.vyom.dataclasses.ChatRequest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatbotViewModel: ViewModel() {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val chatbotService = retrofit.create(ChatbotApiService::class.java)

    fun askQuestion(query:String) {
        viewModelScope.launch {
            val response = chatbotService.sendMessage(
                ChatRequest(
                    query = query,
                    user_id = ""
                )
            )

            Log.d("TicketViewModel", "Chatbot Response ${response.response}")

        }
    }
}