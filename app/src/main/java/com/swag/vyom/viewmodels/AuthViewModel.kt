package com.swag.vyom.viewmodels

import android.R.attr.password
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swag.vyom.api.RetrofitClient
import com.swag.vyom.dataclasses.UserLoginRequest
import com.swag.vyom.dataclasses.UserRegistrationRequest
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {

    fun register(userRegistrationRequest: UserRegistrationRequest){
        viewModelScope.launch {
            try {

//                Log.d("AuthViewModel", "User data: ${userRegistrationRequest.mobile_number}" +
//                        " ${userRegistrationRequest.aadhaar}" +
//                        " ${userRegistrationRequest.account_number}" +
//                        " ${userRegistrationRequest.first_name}" +
//                        " ${userRegistrationRequest.last_name}" +
//                        " ${userRegistrationRequest.date_of_birth}" +
//                        " ${userRegistrationRequest.gender}" +
//                        " ${userRegistrationRequest.email}" +
//                        " ${userRegistrationRequest.password}" +
//                        " ${userRegistrationRequest.image_link}" +
//                        " ${userRegistrationRequest.aadhaar_image_link}" +
//                        " ${userRegistrationRequest.address}" +
//                        " ${userRegistrationRequest.country}" +
//                        " ${userRegistrationRequest.language_preference}")

                val response = RetrofitClient.instance.register(userRegistrationRequest)

                if(response.success){
                    val createdUser = response.data
                    Log.d("AuthViewModel", "User created: ${createdUser.id}")
                } else {
                    Log.e("AuthViewModel", "Error creating user: ${response.msg}")
                }
                }catch (e: Exception){
                    Log.e("AuthViewModel", "Exception: ${e.message } ${e.cause}")

            }
        }
    }
    fun login(userLoginRequest: UserLoginRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.login(userLoginRequest)
                if (response.success) {
                    val createdUser = response.data
                    Log.d("AuthViewModel", "User Exists: ${createdUser?.id}")
                } else {
                    Log.e("AuthViewModel", "User does not exists: ${response.msg}")
                }
            }catch (e: Exception){
                Log.e("AuthViewModel", "Exception: ${e.message } ${e.cause}")

            }
        }
    }



}