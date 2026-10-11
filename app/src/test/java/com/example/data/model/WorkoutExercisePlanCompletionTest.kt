package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutExercisePlanCompletionTest {
    private fun plan() = WorkoutExercisePlan(
        exerciseId = 7,
        exerciseName = "Supino reto",
        muscleGroup = "Peito",
        sets = listOf(
            ExerciseSetEntry(setNumber = 1, weightKg = 40.0, reps = 10, isCompleted = true),
            ExerciseSetEntry(setNumber = 2, weightKg = 40.0, reps = 10, isCompleted = false),
            ExerciseSetEntry(setNumber = 3, weightKg = 40.0, reps = 10, isCompleted = true)
        )
    )

    @Test
    fun navigationCompletionDoesNotMarkUncheckedSetsAsPerformed() {
        val advanced = plan().markCompletedForNavigation()

        assertTrue(advanced.isCompleted)
        assertEquals(listOf(true, false, true), advanced.sets.map { it.isCompleted })
    }

    @Test
    fun partialSessionHistoryContainsOnlyCheckedSetsAndExercises() {
        val plans = listOf(plan(), plan().copy(exerciseId = 8, exerciseName = "Agachamento", sets = emptyList()))

        val saved = plans.completedSetsForHistory()

        assertEquals(1, saved.size)
        assertEquals(listOf(1, 3), saved.single().sets.map { it.setNumber })
        assertTrue(saved.single().sets.all { it.isCompleted })
        assertFalse(saved.single().sets.any { !it.isCompleted })
    }
}
