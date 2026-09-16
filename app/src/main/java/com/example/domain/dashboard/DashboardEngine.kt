package com.example.domain.dashboard

import com.example.data.model.*
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.math.roundToInt

data class MuscleVolume(val muscleGroup: String, val sets: Int, val volumeKg: Double, val previousWeekVolumeKg: Double) {
    val weeklyChangePercent: Double? get() = if (previousWeekVolumeKg > 0) ((volumeKg - previousWeekVolumeKg) / previousWeekVolumeKg) * 100.0 else null
}
data class EvolutionTrend(val currentWeightKg: Double?, val weightChangeKg: Double?, val measurementsCount: Int, val photosCount: Int, val workoutFrequency: Int, val workoutVolumeKg: Double, val cardioDistanceKm: Double, val cardioMinutes: Int)
data class DashboardSummary(val todayWorkout: WorkoutSession?, val weeklyWorkouts: Int, val weeklyGoal: Int, val streakDays: Int, val currentWeightKg: Double?, val weeklyVolumeKg: Double, val weeklyCardioKm: Double, val readinessScore: Int, val readinessLabel: String)

object DashboardEngine {
    fun home(profile: UserProfile?, workouts: List<WorkoutSession>, cardio: List<CardioSession>, measurements: List<BodyMeasurement>, today: LocalDate = LocalDate.now()): DashboardSummary {
        val start = today.with(DayOfWeek.MONDAY).toEpochDay(); val end = today.toEpochDay()
        val week = workouts.filter { it.dateEpochDay in start..end && it.status == SessionStatus.COMPLETED }
        val weekCardio = cardio.filter { it.dateEpochDay in start..end }
        val weight = measurements.maxByOrNull { it.dateEpochDay }?.weightKg ?: profile?.currentWeightKg
        val streak = calculateStreak(workouts, cardio, today)
        val volume = week.sumOf { it.totalWeightLiftedKg }
        val cardioKm = weekCardio.sumOf { it.distanceKm ?: 0.0 }
        val readiness = readiness(week.size, profile?.weeklyGoalDays ?: 0, streak)
        return DashboardSummary(workouts.firstOrNull { it.dateEpochDay == today.toEpochDay() && it.status != SessionStatus.COMPLETED }, week.size, profile?.weeklyGoalDays ?: 0, streak, weight, volume, cardioKm, readiness, when { readiness >= 80 -> "Pronto"; readiness >= 60 -> "Moderado"; else -> "Recuperação" })
    }

    fun evolution(workouts: List<WorkoutSession>, cardio: List<CardioSession>, measurements: List<BodyMeasurement>, photos: List<ProgressPhoto>, days: Long = 28, today: LocalDate = LocalDate.now()): EvolutionTrend {
        val from = today.minusDays(days).toEpochDay(); val recentMeasures = measurements.filter { it.dateEpochDay >= from }.sortedBy { it.dateEpochDay }
        val recentWorkouts = workouts.filter { it.dateEpochDay >= from && it.status == SessionStatus.COMPLETED }; val recentCardio = cardio.filter { it.dateEpochDay >= from }
        return EvolutionTrend(recentMeasures.lastOrNull()?.weightKg, if (recentMeasures.size >= 2) recentMeasures.last().weightKg - recentMeasures.first().weightKg else null, recentMeasures.size, photos.count { it.dateEpochDay >= from }, recentWorkouts.size, recentWorkouts.sumOf { it.totalWeightLiftedKg }, recentCardio.sumOf { it.distanceKm ?: 0.0 }, recentCardio.sumOf { it.durationMinutes })
    }

    fun muscleVolume(records: List<ExerciseExecutionRecord>, today: LocalDate = LocalDate.now()): List<MuscleVolume> {
        val thisStart = today.with(DayOfWeek.MONDAY).toEpochDay(); val prevStart = thisStart - 7; val prevEnd = thisStart - 1
        return records.groupBy { it.muscleGroup }.map { (group, all) ->
            val current = all.filter { it.sessionDateEpochDay in thisStart..today.toEpochDay() }.flatMap { it.completedSets }
            val previous = all.filter { it.sessionDateEpochDay in prevStart..prevEnd }.flatMap { it.completedSets }
            MuscleVolume(group, current.size, current.sumOf { it.weightKg * it.reps }, previous.sumOf { it.weightKg * it.reps })
        }.sortedByDescending { it.volumeKg }
    }

    private fun calculateStreak(workouts: List<WorkoutSession>, cardio: List<CardioSession>, today: LocalDate): Int {
        val activeDays = (workouts.filter { it.status == SessionStatus.COMPLETED }.map { it.dateEpochDay } + cardio.map { it.dateEpochDay }).toSet(); var day = today.toEpochDay(); var streak = 0
        if (day !in activeDays) day--
        while (day in activeDays) { streak++; day-- }
        return streak
    }
    private fun readiness(done: Int, goal: Int, streak: Int): Int {
        if (goal <= 0) return 70
        val load = done.toDouble() / goal.coerceAtLeast(1); val loadScore = when { load <= 0.8 -> 90; load <= 1.0 -> 80; load <= 1.2 -> 65; else -> 50 }
        return (loadScore + streak.coerceAtMost(7) * 2).coerceIn(0, 100)
    }
}
