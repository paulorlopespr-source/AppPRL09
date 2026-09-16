package com.example.domain.backup

import com.example.data.model.BodyMeasurement
import com.example.data.model.CardioSession
import com.example.data.model.ExercisePerformanceTarget
import com.example.data.model.MealLog
import com.example.data.model.ProgressPhoto
import com.example.data.model.UserMedal
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutTemplate

/** Snapshot portátil dos dados pessoais do AppPRL09. */
data class FitnessBackup(
    val formatVersion: Int = CURRENT_FORMAT_VERSION,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val profile: UserProfile?,
    val workoutTemplates: List<WorkoutTemplate>,
    val workoutSessions: List<WorkoutSession>,
    val cardioSessions: List<CardioSession>,
    val bodyMeasurements: List<BodyMeasurement>,
    val progressPhotos: List<ProgressPhoto>,
    val exerciseTargets: List<ExercisePerformanceTarget>,
    val mealLogs: List<MealLog>,
    val medals: List<UserMedal>
) {
    companion object {
        const val CURRENT_FORMAT_VERSION = 1
    }
}
