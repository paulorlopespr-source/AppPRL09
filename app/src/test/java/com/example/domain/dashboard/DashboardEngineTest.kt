package com.example.domain.dashboard

import com.example.data.model.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class DashboardEngineTest {
    private val today = LocalDate.of(2026, 9, 16)
    @Test fun homeSummarizesWeekAndStreak() {
        val workouts = listOf(
            WorkoutSession(title="A", dateEpochDay=today.toEpochDay(), status=SessionStatus.COMPLETED, totalWeightLiftedKg=3000.0),
            WorkoutSession(title="B", dateEpochDay=today.minusDays(1).toEpochDay(), status=SessionStatus.COMPLETED, totalWeightLiftedKg=2000.0)
        )
        val result = DashboardEngine.home(UserProfile(weeklyGoalDays=5), workouts, emptyList(), emptyList(), today)
        assertEquals(2, result.weeklyWorkouts); assertEquals(2, result.streakDays); assertEquals(5000.0, result.weeklyVolumeKg, 0.01)
    }
    @Test fun evolutionCombinesStrengthAndCardio() {
        val workouts = listOf(WorkoutSession(title="A", dateEpochDay=today.toEpochDay(), totalWeightLiftedKg=1000.0))
        val cardio = listOf(CardioSession(type=CardioType.CORRIDA, dateEpochDay=today.toEpochDay(), durationMinutes=30, distanceKm=5.0))
        val trend = DashboardEngine.evolution(workouts, cardio, emptyList(), emptyList(), today=today)
        assertEquals(1, trend.workoutFrequency); assertEquals(5.0, trend.cardioDistanceKm, 0.01); assertEquals(30, trend.cardioMinutes)
    }
    @Test fun readinessStaysWithinBounds() {
        val score = ReadinessEngine.calculate(ReadinessInputs(HealthDashboardSnapshot(stepsToday=7000, exerciseMinutesToday=45), 3, 5))
        assertTrue(score in 0..100)
    }
}
