package com.swag.vyom.dataclasses

data class UserLoginResponse(
    val success: Boolean,
    val msg: String,
    val data: UserData?
)


