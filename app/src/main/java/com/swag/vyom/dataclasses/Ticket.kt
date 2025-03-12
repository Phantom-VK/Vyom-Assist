package com.swag.vyom.dataclasses

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class UrgencyLevel {
    Low, Medium, High, Critical
}

enum class SupportMode {
    Video_Call, Voice_Call, Text_Message
}

enum class PriorityLevel {
    Normal, High, VIP
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
    val priority_level: String? = null
)