package com.swag.vyom.dataclasses

data class FileUploadResponse(
    val success : Boolean,
    val msg : String,
    val data : FileUploadData

)
data class FileUploadData(
    val file_url : String
)

