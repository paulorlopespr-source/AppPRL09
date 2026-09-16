package com.example.domain.readiness

import com.example.data.model.CardioSession
import com.example.data.model.SessionStatus
import com.example.data.model.WorkoutSession
import java.time.DayOfWeek
import java.time.LocalDate

/** Converte o histórico real do app em carga semanal usada pelo ReadinessEngine. */
object TrainingLoadCalculator {
    fun currentWeek(workouts: List<WorkoutSession>, cardio: List<CardioSession>, today: LocalDate = LocalDate.now()): WeeklyTrainingLoad {
        val start = today.with(DayOfWeek.MONDAY).toEpochDay()
        return range(workouts, cardio, start, today.toEpochDay())
    }

    fun previousWeek(workouts: List<WorkoutSession>, cardio: List<CardioSession>, today: LocalDate = LocalDate.now()): WeeklyTrainingLoad {
        val thisStart = today.with(DayOfWeek.MONDAY).toEpochDay()
        return range(workouts, cardio, thisStart - 7, thisStart - 1)
    }

    private fun range(workouts: List<WorkoutSession>, cardio: List<CardioSession>, start: Long, end: Long): WeeklyTrainingLoad {
        val strength = workouts.filter { it.status == SessionStatus.COMPLETED && it.dateEpochDay in start..end }
        val aerobic = cardio.filter { it.dateEpochDay in start..end }
        val rpes = strength.map { it.perceivedExertion.toDouble() }.filter { it in 1.0..10.0 }
        // WorkoutSession não persiste contagem de séries em coluna própria; volume e RPE são usados sem inventar séries.
        return WeeklyTrainingLoad(
            strengthVolumeKg = strength.sumOf { it.totalWeightLiftedKg },
            strengthSets = 0,
            cardioMinutes = aerobic.sumOf { it.durationMinutes },
            cardioDistanceKm = aerobic.sumOf { it.distanceKm ?: 0.0 },
            sessions = strength.size + aerobic.size,
            averageRpe = rpes.takeIf { it.isNotEmpty() }?.average()
        )
    }
}
