package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AIWorkoutPlanResult(
    val title: String,
    val subtitle: String,
    val category: WorkoutCategory,
    val durationMinutes: Int,
    val description: String,
    val exercises: List<WorkoutExercisePlan>,
    val aiBiomechanicalTips: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AICoachMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: AICoachSender,
    val text: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val keyPoints: List<String> = emptyList(),
    val suggestedFollowUps: List<String> = emptyList()
)

enum class AICoachSender {
    USER,
    COACH
}
