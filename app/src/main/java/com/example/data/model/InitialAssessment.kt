package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "initial_assessments")
data class InitialAssessment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val age: Int,
    val sex: String,
    val heightCm: Double,
    val weightKg: Double,
    val chestCm: Double? = null,
    val waistCm: Double? = null,
    val armCm: Double? = null,
    val thighCm: Double? = null,
    val goal: FitnessGoal,
    val fitnessLevel: FitnessLevel,
    val availableDays: Int,
    val cycleId: Long? = null
)
