package com.example

import com.example.data.local.DefaultFitnessData
import com.example.data.model.FitnessGoal
import com.example.data.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FitPr09LogicTest {

    @Test
    fun testDefaultUserProfile() {
        val profile = DefaultFitnessData.getDefaultUserProfile()
        assertNotNull(profile)
        assertEquals("Atleta Fit", profile.name)
        assertTrue(profile.currentWeightKg > 0)
        assertTrue(profile.weeklyGoalDays in 1..7)
        assertEquals(FitnessGoal.GANHO_PESO_HIPERTROFIA, profile.goal)
    }

    @Test
    fun testDefaultWorkoutTemplates() {
        val exercises = DefaultFitnessData.getDefaultExercises()
        assertTrue("Exercises list should not be empty", exercises.isNotEmpty())
        val templates = DefaultFitnessData.getDefaultWorkoutTemplates(exercises)
        assertTrue("Templates should not be empty", templates.isNotEmpty())
        for (tpl in templates) {
            assertTrue("Template title must not be blank", tpl.title.isNotBlank())
            assertTrue("Template exercisesJson must not be blank", tpl.exercisesJson.isNotBlank())
            assertTrue("Template exerciseCount must be positive", tpl.exerciseCount > 0)
        }
    }

    @Test
    fun testVolumeCalculation() {
        val weights = listOf(50.0, 60.0, 70.0)
        val reps = listOf(10, 10, 8)
        val totalVolume = weights.zip(reps) { w, r -> w * r }.sum()
        assertEquals(500.0 + 600.0 + 560.0, totalVolume, 0.001)
    }

    @Test
    fun testUserProfileUpdate() {
        val original = DefaultFitnessData.getDefaultUserProfile()
        val updated = original.copy(
            name = "Carlos Silva",
            currentWeightKg = 82.5,
            targetWeightKg = 88.0,
            goal = FitnessGoal.DEFINICAO_MUSCULAR
        )
        assertEquals("Carlos Silva", updated.name)
        assertEquals(82.5, updated.currentWeightKg, 0.001)
        assertEquals(88.0, updated.targetWeightKg, 0.001)
        assertEquals(FitnessGoal.DEFINICAO_MUSCULAR, updated.goal)
    }

    @Test
    fun testQuickWorkoutPlansGeneration() {
        val fullBodyPlans = com.example.ui.components.generateCustomQuickStrengthPlans("Full Body", 20)
        assertTrue("Full body quick workout should contain exercises", fullBodyPlans.isNotEmpty())
        for (plan in fullBodyPlans) {
            assertTrue("Plan exercise name should not be blank", plan.exerciseName.isNotBlank())
            assertTrue("Plan sets should not be empty", plan.sets.isNotEmpty())
        }

        val upperPlans = com.example.ui.components.generateCustomQuickStrengthPlans("Superiores", 15)
        assertTrue("Upper quick workout should contain exercises", upperPlans.isNotEmpty())

        val corePlans = com.example.ui.components.generateCustomQuickStrengthPlans("Core", 10)
        assertTrue("Core quick workout should contain exercises", corePlans.isNotEmpty())
    }
}
