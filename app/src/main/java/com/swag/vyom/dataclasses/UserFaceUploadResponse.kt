package com.swag.vyom.dataclasses

data class UserImageUploadResponse(
    val success: Boolean,
    val msg: String,
    val file_path: String?
)


