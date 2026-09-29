package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planned_workouts")
data class PlannedWorkout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cycleId: Long,
    val templateId: Long? = null,
    val agendaAppointmentId: String? = null,
    val workoutSessionId: Long? = null,
    val weekNumber: Int,
    val dayOfWeek: Int,
    val title: String,
    val status: String = "PLANNED"
)
