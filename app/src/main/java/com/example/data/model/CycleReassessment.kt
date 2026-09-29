package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cycle_reassessments")
data class CycleReassessment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cycleId: Long,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val weightKg: Double,
    val chestCm: Double? = null,
    val waistCm: Double? = null,
    val armCm: Double? = null,
    val thighCm: Double? = null,
    val fitnessLevel: FitnessLevel,
    val goal: FitnessGoal,
    val notes: String = ""
)
