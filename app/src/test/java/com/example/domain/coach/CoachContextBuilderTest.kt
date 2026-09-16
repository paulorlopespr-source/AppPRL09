package com.example.domain.coach

import com.example.data.model.*
import com.example.domain.readiness.ReadinessResult
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class CoachContextBuilderTest {
    @Test fun contextUsesRecordedMetricsWithoutAiCalculation() {
        val today = LocalDate.of(2026, 9, 16)
        val workouts = listOf(
            WorkoutSession(title="A", dateEpochDay=today.toEpochDay(), status=SessionStatus.COMPLETED, totalWeightLiftedKg=5000.0, perceivedExertion=8),
            WorkoutSession(title="B", dateEpochDay=today.minusDays(2).toEpochDay(), status=SessionStatus.COMPLETED, totalWeightLiftedKg=4000.0, perceivedExertion=6)
        )
        val cardio = listOf(CardioSession(dateEpochDay=today.minusDays(1).toEpochDay(), durationMinutes=30, distanceKm=5.0))
        val readiness = ReadinessResult(72,"Atenção",listOf("Carga elevada"),"Treino conservador",false)
        val context = CoachContextBuilder.build(null, workouts, cardio, emptyList(), readiness, today=today)
        assertEquals(2, context.last7Days.strengthSessions)
        assertEquals(9000.0, context.last7Days.strengthVolumeKg, 0.001)
        assertEquals(7.0, context.last7Days.averageRpe!!, 0.001)
        assertEquals(30, context.last7Days.cardioMinutes)
        assertEquals(5.0, context.last7Days.cardioDistanceKm, 0.001)
        assertEquals(72, context.readinessScore)
        assertTrue(context.asGroundedText().contains("9000 kg"))
    }
}
