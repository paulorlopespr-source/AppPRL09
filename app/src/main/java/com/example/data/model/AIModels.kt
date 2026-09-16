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

@JsonClass(generateAdapter = true)
data class VolumeNutritionEvaluationResult(
    val title: String = "Avaliação Nutricional Baseada no Volume",
    val trainingVolumeSummary: String = "",
    val recommendedDailyCalories: Int = 2600,
    val calorieAdjustmentReason: String = "",
    val proteinGrams: Double = 160.0,
    val proteinPerKg: Double = 2.0,
    val carbsGrams: Double = 330.0,
    val carbsPerKg: Double = 4.2,
    val fatsGrams: Double = 70.0,
    val fatsPerKg: Double = 0.9,
    val preWorkoutNutrition: String = "",
    val postWorkoutNutrition: String = "",
    val hydrationLitres: Double = 3.5,
    val dietAdjustments: List<String> = emptyList(),
    val volumeInsight: String = "",
    val isFromGeminiAI: Boolean = true
)

@JsonClass(generateAdapter = true)
data class ExerciseExecutionGuideResult(
    val exerciseName: String,
    val targetMuscle: String = "",
    val equipment: String = "",
    val setupInstructions: List<String> = emptyList(),
    val executionSteps: List<String> = emptyList(),
    val biomechanicsAndBreathing: String = "",
    val commonMistakes: List<String> = emptyList(),
    val mindMuscleConnectionTip: String = "",
    val isFromGeminiAI: Boolean = true
)
