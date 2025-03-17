package com.swag.vyom.dataclasses


data class FaceCompareResponse(
    val euclidean_distance: Float,
    val is_match: Boolean,
    val threshold: Float
)
