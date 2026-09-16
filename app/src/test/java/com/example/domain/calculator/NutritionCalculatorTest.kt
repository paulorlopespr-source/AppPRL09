package com.example.domain.calculator

import com.example.data.model.FitnessGoal
import com.example.data.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NutritionCalculatorTest {

    @Test
    fun `uses profile age instead of hard coded age`() {
        val younger = UserProfile(age = 28, currentWeightKg = 80.0, heightCm = 175.0)
        val older = younger.copy(age = 43)

        val youngerTargets = NutritionCalculator.calculate(younger)
        val olderTargets = NutritionCalculator.calculate(older)

        assertTrue(youngerTargets.bmrKcal > olderTargets.bmrKcal)
        assertEquals(75, youngerTargets.bmrKcal - olderTargets.bmrKcal)
    }

    @Test
    fun `fat loss target is below maintenance`() {
        val profile = UserProfile(
            age = 43,
            currentWeightKg = 90.0,
            heightCm = 175.0,
            goal = FitnessGoal.PERDA_PESO_EMAGRECIMENTO
        )

        val targets = NutritionCalculator.calculate(profile)

        assertTrue(targets.targetCalories < targets.maintenanceCalories)
        assertTrue(targets.proteinGrams > 0)
        assertTrue(targets.carbsGrams >= 0)
        assertTrue(targets.fatsGrams > 0)
    }

    @Test
    fun `hypertrophy target is above maintenance`() {
        val profile = UserProfile(
            age = 43,
            currentWeightKg = 80.0,
            heightCm = 175.0,
            goal = FitnessGoal.GANHO_PESO_HIPERTROFIA
        )

        val targets = NutritionCalculator.calculate(profile)

        assertEquals(targets.maintenanceCalories + 250, targets.targetCalories)
    }
}
