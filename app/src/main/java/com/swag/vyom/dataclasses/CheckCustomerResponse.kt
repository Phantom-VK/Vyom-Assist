package com.swag.vyom.dataclasses

data class CheckCustomerResponse(
    val success: Boolean,
    val msg: String,
    val data: CustomerData? = null
)

data class CustomerData(
    val customer: Boolean,
    val registered: Boolean,
    val aadhaar:String,
    val mobile_number:String,
    val id: Int? = null
)
