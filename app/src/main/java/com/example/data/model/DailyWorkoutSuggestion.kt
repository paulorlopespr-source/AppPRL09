package com.example.data.model

data class DailyWorkoutSuggestion(
    val template: WorkoutTemplate?,
    val title: String,
    val subtitle: String,
    val muscleGroups: List<MuscleGroup>,
    val explanationReason: String,
    val estimatedDurationMinutes: Int,
    val intensity: IntensityLevel,
    val dayOfWeekName: String,
    val alternativeTemplates: List<WorkoutTemplate> = emptyList()
)
