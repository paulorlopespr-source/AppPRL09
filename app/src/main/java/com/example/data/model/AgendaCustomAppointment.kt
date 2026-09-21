package com.example.data.model

import com.squareup.moshi.JsonClass
import java.util.UUID

enum class AgendaAppointmentStatus {
    PLANNED,
    COMPLETED,
    CANCELLED,
    RESCHEDULED
}

@JsonClass(generateAdapter = true)
data class AgendaCustomAppointment(
    val id: String = UUID.randomUUID().toString(),
    val epochDay: Long,
    val typeName: String, // STRENGTH, CARDIO, MEAL, REST
    val startTime: String,
    val endTime: String? = null,
    val title: String,
    val subtitle: String,
    val isCompleted: Boolean = false,
    val status: AgendaAppointmentStatus = if (isCompleted) AgendaAppointmentStatus.COMPLETED else AgendaAppointmentStatus.PLANNED
)
