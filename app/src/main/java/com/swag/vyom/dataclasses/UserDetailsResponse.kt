package com.swag.vyom.dataclasses



data class UserDetailsResponse(
    val success: Boolean,
    val msg: String,
    val data: UserDetails?
)

data class UserDetails(
    val id: Int,
    val mobile_number: String,
    val aadhaar: String,
    val account_number: String,
    val first_name: String,
    val last_name: String
)

