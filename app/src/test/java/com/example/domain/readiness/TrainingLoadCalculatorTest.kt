package com.example.domain.readiness

import com.example.data.model.CardioSession
import com.example.data.model.CardioType
import com.example.data.model.WorkoutSession
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class TrainingLoadCalculatorTest {
    @Test fun usesPersistedWorkoutRpeVolumeAndCardio() {
        val today = LocalDate.of(2026, 9, 16)
        val day = today.toEpochDay()
        val workouts = listOf(WorkoutSession(title="A", dateEpochDay=day, totalWeightLiftedKg=5000.0, perceivedExertion=8))
        val cardio = listOf(CardioSession(type=CardioType.CORRIDA, dateEpochDay=day, durationMinutes=30, distanceKm=5.0))
        val load = TrainingLoadCalculator.currentWeek(workouts, cardio, today)
        assertEquals(5000.0, load.strengthVolumeKg, 0.0)
        assertEquals(30, load.cardioMinutes)
        assertEquals(5.0, load.cardioDistanceKm, 0.0)
        assertEquals(8.0, load.averageRpe ?: 0.0, 0.0)
        assertEquals(2, load.sessions)
    }
}
