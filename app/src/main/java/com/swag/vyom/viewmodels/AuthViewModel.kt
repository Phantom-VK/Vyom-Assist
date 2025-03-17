package com.swag.vyom.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swag.vyom.SharedPreferencesHelper
import com.swag.vyom.api.ApiClient
import com.swag.vyom.dataclasses.CheckCustomerResponse
import com.swag.vyom.dataclasses.UserDetailsResponse
import com.swag.vyom.dataclasses.UserLoginRequest
import com.swag.vyom.dataclasses.UserRegistrationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File

class AuthViewModel(private val preferencesHelper: SharedPreferencesHelper) : ViewModel() {

    private val _registrationStatus = MutableStateFlow<Boolean?>(null)
    val registrationStatus: StateFlow<Boolean?> = _registrationStatus

    private val _loginStatus = MutableStateFlow<Boolean?>(null)
    val loginStatus: StateFlow<Boolean?> = _loginStatus

    private val _customerStatus = MutableStateFlow<CheckCustomerResponse?>(null)
    val customerStatus: StateFlow<CheckCustomerResponse?> = _customerStatus

    fun register(userRegistrationRequest: UserRegistrationRequest) {
        viewModelScope.launch {
            try {
                val response = ApiClient.instance.register(userRegistrationRequest)
                _registrationStatus.emit(response.success)
                if (response.success) {
                    Log.d("AuthViewModel", "User registered successfully: ${response.data.id}")
                } else {
                    Log.e("AuthViewModel", "Registration failed: ${response.msg}")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception during registration: ${e.localizedMessage}")
                _registrationStatus.emit(false)
            }
        }
    }

    fun login(userLoginRequest: UserLoginRequest) {
        viewModelScope.launch {
            try {
                val response = ApiClient.instance.login(userLoginRequest)
                _loginStatus.emit(response.success)
                if (response.success) {
                    Log.d("AuthViewModel", "User login successful")
                } else {
                    Log.e("AuthViewModel", "Login failed: ${response.msg}")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception during login: ${e.localizedMessage}")
                _loginStatus.emit(false)
            }
        }
    }

    fun faceAuth(bitmap: Bitmap, image_link: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
//                val requestFile = image.asRequestBody("image/*".toMediaTypeOrNull())
//                val multipartBody = MultipartBody.Part.createFormData("image2", image.name, requestFile)
//
//                Log.d("AuthViewModel", "Sending request with image_url: $image_link and file: ${image.name}")
//
//                val response = ApiClient.faceAuthInstance.compareFaces(imageUrl = image_link, image2 = multipartBody)
//
//                Log.d("AuthViewModel", "Response received: ${response.toString()}")
//
//                if (response.is_match) {
//                    Log.d("AuthViewModel", "Face Authentication successful")
//                    onResult(true)
//                } else {/
//                    Log.e("AuthViewModel", "Face Authentication failed: ${response.euclidean_distance}")
//                    onResult(false)
//                }

//                image_link.toRequestBody("text/plain".toMediaTypeOrNull())
                val requestBody = "https://sggsapp.co.in/vyom/uploads/userfaces/user_678967896789.jpg?t=1741944772".toRequestBody("text/plain".toMediaTypeOrNull())
                val imagePart = bitmapToMultipart(bitmap!!, "image2")
                val response = ApiClient.faceAuthInstance.compareFaces(requestBody, imagePart)

                withContext(Dispatchers.Main) {
                    if (response.is_match) {
                        Log.d("AuthViewModel", "Face Authentication successful")
                        onResult(true)
                    } else {
                        Log.e("AuthViewModel", "Face Authentication failed: ${response.euclidean_distance}")
                        onResult(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Face Authentication Exception: ${e.localizedMessage}")
                onResult(false)
            }
        }
    }

    private fun bitmapToMultipart(bitmap: Bitmap, paramName: String): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(paramName, "selfie.jpg", requestBody)
    }


    suspend fun checkCustomer(mobileNumber: String?, aadhaar: String?) {
        if (mobileNumber.isNullOrBlank() && aadhaar.isNullOrBlank()) {
            Log.e("AuthViewModel", "Both fields are empty")
            _customerStatus.emit(
                CheckCustomerResponse(
                    success = false,
                    msg = "Mobile number or Aadhaar is required",
                    data = null
                )
            )
            return
        }

        viewModelScope.launch {
            try {
                val response = ApiClient.instance.checkCustomer(mobileNumber, aadhaar)
                Log.d("AuthViewModel", "User's data in checkcustomer function ${response.data}")
                _customerStatus.emit(response)

                if (response.success) {
                    if (response.data?.registered == true) {
                        Log.d("AuthViewModel", "Redirect to Login Page")
                    } else {
                        Log.d("AuthViewModel", "Redirect to Register Page")
                    }
                } else {
                    Log.e("AuthViewModel", "Customer not found: ${response.msg}")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception during customer check: ${e.localizedMessage}")
                _customerStatus.emit(
                    CheckCustomerResponse(
                        success = false,
                        msg = "Exception occurred",
                        data = null
                    )
                )
            }
        }
    }

    suspend fun getUserDetails(
        mobileNumber: String? = null,
        aadhaar: String? = null,
        onResponse : (UserDetailsResponse) -> Unit
    ) {
            try {
                Log.d("AuthViewModel", "Inside authvm")
                val response = ApiClient.instance.getUserDetails(mobileNumber, aadhaar)

                onResponse(response)

                Log.d("AuthViewModel", "Saved data")


            } catch (e: Exception) {

                Log.d("AuthViewModel", "Error fetching details ${e.localizedMessage}")
            }

    }


}
