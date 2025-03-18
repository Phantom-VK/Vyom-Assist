package com.swag.vyom.dataclasses

data class RatingResponse(
    val success: Boolean,
    val msg: String?,
    val data: RatingData?
)

data class RatingData(
    val new_rating: Float,
    val rating_count: Int
)
