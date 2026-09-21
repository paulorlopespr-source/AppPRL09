package com.example.domain.gamification

import com.example.data.model.SessionStatus
import com.example.data.model.WorkoutSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class JourneyAdherenceTest {
    private val today = LocalDate.of(2026, 9, 21)

    @Test
    fun plannedRestAndUnplannedWeekAreNeutral() {
        val empty = Phase7JourneyEngine.build(emptyList(), emptyList(), emptyList(), today)
        assertNull(empty.adherencePercent)
        assertNull(empty.cycleAdherencePercent)
    }

    @Test
    fun adherenceUsesPlanAndNeverExceeds100Percent() {
        val workouts = (0..4).map { day ->
            WorkoutSession(title = "Treino $day", dateEpochDay = today.plusDays(day.toLong()).toEpochDay(), durationSeconds = 1800)
        }
        val snapshot = Phase7JourneyEngine.build(workouts, emptyList(), emptyList(), today, weeklyGoalDays = 3)
        assertEquals(100, snapshot.adherencePercent)
    }

    @Test
    fun cancelledAndDuplicateSessionsDoNotCount() {
        val date = today.toEpochDay()
        val workouts = listOf(
            WorkoutSession(title = "Força", dateEpochDay = date, durationSeconds = 1800),
            WorkoutSession(title = "Força", dateEpochDay = date, durationSeconds = 1800),
            WorkoutSession(title = "Cancelado", dateEpochDay = today.toEpochDay(), status = SessionStatus.SKIPPED, durationSeconds = 1800)
        )
        val snapshot = Phase7JourneyEngine.build(workouts, emptyList(), emptyList(), today, weeklyGoalDays = 3)
        assertEquals(1, snapshot.validWorkoutCount)
        assertEquals(33, snapshot.adherencePercent)
        assertTrue(snapshot.evolutionMessage.isNotBlank())
    }
}
