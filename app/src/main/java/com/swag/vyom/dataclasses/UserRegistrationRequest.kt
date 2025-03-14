package com.swag.vyom.dataclasses

data class UserRegistrationRequest(
    val id:Int?,
    val mobile_number: String,
    val aadhaar: String,
    val account_number: String,
    val first_name: String,
    val last_name: String,
    val date_of_birth: String,
    val gender: String,
    val email: String,
    val password: String,
    val image_link: String,
    val aadhaar_image_link: String,
    val address: String,
    val country: String,
    val language_preference: String
)
