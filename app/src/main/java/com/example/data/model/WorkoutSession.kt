package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

enum class SessionStatus(val label: String) {
    COMPLETED("Concluído"),
    SCHEDULED("Agendado"),
    IN_PROGRESS("Em Andamento"),
    SKIPPED("Não Realizado")
}

@JsonClass(generateAdapter = true)
@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val templateId: Long? = null,
    val title: String,
    val dateEpochDay: Long, // LocalDate.toEpochDay()
    val startTimeMillis: Long = System.currentTimeMillis(),
    val endTimeMillis: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val location: String = "Academia Smart Fit", // "Smart Fit", "Bluefit", "Academia do Bairro", "Ao Ar Livre", "Em Casa", etc.
    val status: SessionStatus = SessionStatus.COMPLETED,
    val totalWeightLiftedKg: Double = 0.0,
    val estimatedCalories: Int = 0,
    val perceivedExertion: Int = 7, // 1 to 10 RPE
    val notes: String = "",
    val exercisesDoneJson: String = "[]",
    val aiCaloricEvaluation: String = ""
)
