package com.swag.vyom.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swag.vyom.SharedPreferencesHelper
import com.swag.vyom.dataclasses.UserDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class UserViewModel (private val sharedPreferencesHelper: SharedPreferencesHelper) : ViewModel() {

    private val _userDetails = MutableStateFlow<UserDetails?>(null)
    val userDetails: StateFlow<UserDetails?> = _userDetails


    fun saveUserDetails(userDetails: UserDetails?) {
        viewModelScope.launch {
            if (userDetails != null) {
                sharedPreferencesHelper.saveUserDetails(userDetails)
                _userDetails.value = userDetails
            }else{
                Log.d("Userdetails", "saveUserDetails: User Details is null")
            }
            //TODO: Handle null case
        }
    }

    fun saveAadhaarOrMobile(aadhaar : String = "", mobile : String = ""){
        if(aadhaar.isNotEmpty()){
            viewModelScope.launch {
                sharedPreferencesHelper.saveaadhaar(aadhaar)
            }
        }else if(mobile.isNotEmpty()){
            viewModelScope.launch {
                sharedPreferencesHelper.savemobile(mobile)
            }
        }
    }

    fun loadUserDetails() {
        viewModelScope.launch {
            _userDetails.value = sharedPreferencesHelper.getUserDetails()
        }
    }

    fun clearUserDetails() {
        viewModelScope.launch {
            sharedPreferencesHelper.clearUserDetails()
            _userDetails.value = null
        }
    }
}
