package com.swag.vyom.viewmodels

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
            }
            //TODO: Handle null case
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
