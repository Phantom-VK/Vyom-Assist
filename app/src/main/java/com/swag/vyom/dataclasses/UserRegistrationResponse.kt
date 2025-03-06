package com.swag.vyom.dataclasses

data class UserRegistrationResponse(
    val success: Boolean,
    val msg: String,
    val data: UserData
)

data class UserData(
    val id: Int,
    val email: String,
    val name: String,
)


