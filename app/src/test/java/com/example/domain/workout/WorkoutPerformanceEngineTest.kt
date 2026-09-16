package com.example.domain.workout

import com.example.data.model.ExerciseExecutionRecord
import com.example.data.model.ExerciseSetEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutPerformanceEngineTest {
    private fun set(n: Int, kg: Double, reps: Int, rir: Int? = null, rpe: Double? = null) =
        ExerciseSetEntry(n, kg, reps, isCompleted = true, rir = rir, rpe = rpe)

    private fun record(vararg sets: ExerciseSetEntry) = ExerciseExecutionRecord(
        sessionId = 1, workoutTitle = "Treino", sessionDateEpochDay = 1,
        sessionStartTimeMillis = 1, location = "Academia", exerciseId = 1,
        exerciseName = "Supino", muscleGroup = "Peito", sets = sets.toList()
    )

    @Test fun snapshotCalculatesVolumeAnd1RM() {
        val result = WorkoutPerformanceEngine.snapshot(listOf(set(1, 80.0, 10), set(2, 80.0, 8)))
        assertEquals(1440.0, result.totalVolumeKg, 0.01)
        assertTrue(result.estimated1RM > 100.0)
    }

    @Test fun detectsMultiplePRTypes() {
        val history = listOf(record(set(1, 80.0, 8)))
        val prs = WorkoutPerformanceEngine.detectPRs(listOf(set(1, 82.5, 10)), history)
        assertTrue(prs.any { it.type == PersonalRecordType.MAX_WEIGHT })
        assertTrue(prs.any { it.type == PersonalRecordType.MAX_REPS })
        assertTrue(prs.any { it.type == PersonalRecordType.ESTIMATED_1RM })
    }

    @Test fun controlledEffortSuggestsSmallLoadIncrease() {
        val target = WorkoutPerformanceEngine.progressiveTarget(listOf(record(set(1, 80.0, 10, rir = 3, rpe = 7.0))))
        assertEquals(82.5, target.suggestedWeightKg, 0.01)
    }

    @Test fun timerStartsFromCompletedSetRest() {
        val timer = RestTimerController.start(90, 2)
        assertEquals(90, timer.remainingSeconds)
        assertTrue(timer.isRunning)
        assertEquals(2, timer.sourceSetNumber)
    }
}
