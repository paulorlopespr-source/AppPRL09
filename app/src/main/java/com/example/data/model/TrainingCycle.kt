package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FitnessLevel { SEDENTARY, BEGINNER, INTERMEDIATE, ADVANCED }

@Entity(tableName = "training_cycles")
data class TrainingCycle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startedAtEpochDay: Long,
    val durationWeeks: Int = 12,
    val currentWeek: Int = 1,
    val fitnessLevel: FitnessLevel = FitnessLevel.BEGINNER,
    val goal: FitnessGoal = FitnessGoal.CONDICIONAMENTO_GERAL,
    val availableDays: Int = 3,
    val isActive: Boolean = true,
    val completedAtEpochDay: Long? = null
) {
    /** Semana do ciclo calculada a partir da data de início (1..durationWeeks). */
    fun elapsedWeek(todayEpochDay: Long): Int =
        (((todayEpochDay - startedAtEpochDay) / 7L).toInt() + 1)
            .coerceIn(1, durationWeeks)
}
