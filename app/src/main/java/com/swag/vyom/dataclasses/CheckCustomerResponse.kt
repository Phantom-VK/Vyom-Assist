package com.swag.vyom.dataclasses

data class CheckCustomerResponse(
    val success: Boolean,
    val msg: String,
    val data: CustomerData? = null
)

data class CustomerData(
    val id: Int? = null
)
