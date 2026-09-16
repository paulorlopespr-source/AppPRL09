package com.example.domain.coach

import org.junit.Assert.*
import org.junit.Test

class CoachGroundingContractTest {
    @Test fun groundedTextMarksMissingDataExplicitly() {
        val context = CoachPRL09Context(
            generatedAtEpochDay = 0,
            goal = "não informado",
            weeklyGoalDays = 0,
            currentWeightKg = null,
            weightChange28dKg = null,
            streakDays = 0,
            last7Days = CoachPeriodSummary(7,0,0.0,null,0,0,0.0),
            last28Days = CoachPeriodSummary(28,0,0.0,null,0,0,0.0),
            readinessScore = null,
            readinessLabel = null,
            readinessReasons = emptyList(),
            readinessRecommendation = null,
            recentPersonalRecords = emptyList(),
            equipment = emptyList()
        )
        val text = context.asGroundedText()
        assertTrue(text.contains("não registrado"))
        assertTrue(text.contains("sem check-in"))
        assertTrue(text.contains("NÃO RECALCULAR NEM INVENTAR NÚMEROS"))
    }
}
