package com.swag.vyom.dataclasses

enum class UrgencyLevel {
    Low, Medium, High, Critical
}
data class Ticket(
    val user_id: Int,
    val category: String,
    val sub_category: String,
    val urgency_level: String,
    val preferred_support_mode: String,
    val available_timedate: String,
    val language_preference: String,
    val description: String,
    val audio_file_link: String? = null,
    val video_file_link: String? = null,
    val attached_image_link: String? = null,
    val assigned_department: String? = null,
    val priority_level: String = "",
    val attached_file: String
)



enum class CustomerSegment {
    PREMIUM, BUSINESS, REGULAR, SENIOR, NEW
}

data class CustomerHistory(
    val customerSegment: CustomerSegment,
    val recentTicketsCount: Int = 0,
    val hasUnresolvedTickets: Boolean = false
)