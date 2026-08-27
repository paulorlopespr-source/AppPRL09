package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "exercise_targets")
data class ExercisePerformanceTarget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: Long? = null,
    val exerciseName: String,
    val targetWeightKg: Double,
    val targetReps: Int = 8,
    val currentWeightKg: Double = 0.0,
    val currentReps: Int = 0,
    val targetDateEpochDay: Long? = null,
    val isAchieved: Boolean = false,
    val achievedDateEpochDay: Long? = null,
    val notes: String = "",
    val createdAtEpochDay: Long = 0
) {
    val progressPercentage: Float
        get() {
            if (isAchieved) return 1f
            if (targetWeightKg <= 0.0) return 0f
            return (currentWeightKg.toFloat() / targetWeightKg.toFloat()).coerceIn(0f, 1f)
        }
}
