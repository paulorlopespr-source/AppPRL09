package com.example.domain.gamification

import com.example.data.model.CardioSession
import com.example.data.model.CardioType
import com.example.data.model.SessionStatus
import com.example.data.model.UserMedal
import com.example.data.model.WorkoutSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class Phase7JourneyEngineTest {
    @Test
    fun pintinhoCatalogHasTwentyUniqueCardsAndMilestoneChests() {
        val cards = PintinhoJourneyCatalog.cards

        assertEquals((1..20).toList(), cards.map { it.level })
        assertEquals(20, cards.map { it.assetKey }.distinct().size)
        assertEquals(listOf(5, 10, 15, 20), cards.filter { it.chest != null }.map { it.level })
    }

    @Test
    fun buildsJourneyFromRecordedDataOnly() {
        val today = LocalDate.of(2026, 9, 16)
        val workouts = listOf(
            WorkoutSession(title = "A", dateEpochDay = today.toEpochDay(), status = SessionStatus.COMPLETED, durationSeconds = 3600, totalWeightLiftedKg = 3000.0),
            WorkoutSession(title = "B", dateEpochDay = today.minusDays(1).toEpochDay(), status = SessionStatus.COMPLETED, durationSeconds = 1800, totalWeightLiftedKg = 2500.0)
        )
        val cardio = listOf(
            CardioSession(type = CardioType.CORRIDA, dateEpochDay = today.minusDays(2).toEpochDay(), durationMinutes = 30, distanceKm = 5.0)
        )
        val medals = listOf(
            UserMedal(id = "m1", title = "Primeira", description = "", category = "CONSISTENCIA", iconEmoji = "🏅", isUnlocked = true, xpReward = 100)
        )

        val result = Phase7JourneyEngine.build(workouts, cardio, medals, today)

        assertEquals(3, result.currentStreakDays)
        assertEquals(3, result.longestStreakDays)
        assertEquals(2, result.strengthSessions)
        assertEquals(1, result.cardioSessions)
        assertEquals(120, result.totalMinutes)
        assertEquals(5.0, result.totalDistanceKm, 0.001)
        assertEquals(5500.0, result.totalVolumeKg, 0.001)
        assertEquals(1, result.unlockedMedals)
        assertEquals(310, result.totalXp)
        assertTrue(result.weeklyQuests.first().completed)
        assertEquals(2, result.pintinhoProgress.completedLevels)
        assertEquals(3, result.pintinhoProgress.currentCard?.level)
    }

    @Test
    fun evaluatesFreeWorkoutAgainstJourneyProgress() {
        val today = LocalDate.of(2026, 9, 16)
        val first = WorkoutSession(title = "Livre", dateEpochDay = today.toEpochDay(), status = SessionStatus.COMPLETED, durationSeconds = 1800, totalWeightLiftedKg = 100.0)
        val impact = Phase7JourneyEngine.evaluateWorkout(first, emptyList(), today = today)

        assertEquals(first.id, impact.sessionId)
        assertTrue(impact.xpEarned >= 0)
        assertTrue(impact.objectiveProgress <= impact.objectiveTarget || impact.objectiveTarget == 0)
    }
}
