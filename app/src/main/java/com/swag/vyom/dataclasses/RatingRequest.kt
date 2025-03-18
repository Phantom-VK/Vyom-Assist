package com.swag.vyom.dataclasses

data class RatingRequest(
    val agent_id: Int,
    val ticket_id: Int,
    val rating: Float
)
