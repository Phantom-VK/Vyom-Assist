package com.swag.vyom.dataclasses

data class FaceAuthResponse(
    val euclidean_distance: Double,
    val is_match: Boolean,
    val threshold: Double
)
