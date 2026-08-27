package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExerciseSetHistory(
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val isCompleted: Boolean = true,
    val restSeconds: Int = 60,
    val durationSeconds: Int = 0,
    val rpe: Int? = null
)

@JsonClass(generateAdapter = true)
data class ExerciseExecutionRecord(
    val sessionId: Long,
    val workoutTitle: String,
    val sessionDateEpochDay: Long,
    val sessionStartTimeMillis: Long,
    val location: String,
    val exerciseId: Long?,
    val exerciseName: String,
    val muscleGroup: String,
    val sets: List<ExerciseSetEntry>,
    val notes: String = "",
    val targetRestSeconds: Int = 60
) {
    val completedSets: List<ExerciseSetEntry>
        get() = sets.filter { it.isCompleted }

    val primaryWeightKg: Double
        get() = completedSets.maxOfOrNull { it.weightKg } ?: sets.firstOrNull()?.weightKg ?: 0.0

    val totalReps: Int
        get() = completedSets.sumOf { it.reps }

    val totalVolumeKg: Double
        get() = completedSets.sumOf { it.weightKg * it.reps }

    val summarySetsString: String
        get() = completedSets.joinToString(" • ") { "Série ${it.setNumber}: ${it.reps} reps" }
}

data class ProgressionSuggestion(
    val exerciseName: String,
    val exerciseId: Long?,
    val currentWeightKg: Double,
    val suggestedWeightKg: Double,
    val weightDeltaKg: Double,
    val reason: String,
    val recentSessionsSummary: String,
    val suggestedReps: Int = 10,
    val isAccepted: Boolean = false,
    val isDismissed: Boolean = false
)

data class ExerciseEvolutionSummary(
    val exerciseName: String,
    val totalSessionsLogged: Int,
    val startingWeightKg: Double,
    val currentWeightKg: Double,
    val maxWeightKg: Double,
    val weightGainKg: Double,
    val estimated1RM: Double,
    val historyRecords: List<ExerciseExecutionRecord>
)
