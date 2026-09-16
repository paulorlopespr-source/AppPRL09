package com.example.domain.calculator

import com.example.data.model.FitnessGoal
import com.example.data.model.UserProfile
import kotlin.math.roundToInt

data class NutritionTargets(
    val bmrKcal: Int,
    val maintenanceCalories: Int,
    val targetCalories: Int,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatsGrams: Double,
    val hydrationLitres: Double
)

/**
 * Deterministic nutrition target calculator.
 *
 * Training tonnage is deliberately NOT converted directly into calories. Strength-training
 * volume is useful context for coaching, but it is not a reliable kcal conversion factor.
 * AI should explain/adapt these calculated targets rather than invent the base numbers.
 */
object NutritionCalculator {

    fun calculate(profile: UserProfile): NutritionTargets {
        val weight = profile.currentWeightKg.coerceAtLeast(1.0)
        val height = profile.heightCm.coerceAtLeast(1.0)
        val age = profile.age.coerceIn(14, 100)

        // Current profile has no sex field. Keep the legacy +5 constant for compatibility,
        // but use the REAL profile age instead of the previous hard-coded age of 28.
        // When sex is added to UserProfile, select the appropriate Mifflin-St Jeor constant.
        val bmr = 10.0 * weight + 6.25 * height - 5.0 * age + 5.0

        val activityMultiplier = when {
            profile.weeklyGoalDays >= 6 -> 1.65
            profile.weeklyGoalDays >= 5 -> 1.55
            profile.weeklyGoalDays >= 3 -> 1.40
            else -> 1.25
        }
        val maintenance = bmr * activityMultiplier

        val target = when (profile.goal) {
            FitnessGoal.GANHO_PESO_HIPERTROFIA -> maintenance + 250.0
            FitnessGoal.PERDA_PESO_EMAGRECIMENTO -> maintenance - 350.0
            FitnessGoal.DEFINICAO_MUSCULAR -> maintenance - 250.0
            FitnessGoal.CONDICIONAMENTO_GERAL -> maintenance
        }.coerceAtLeast(1400.0)

        val proteinPerKg = when (profile.goal) {
            FitnessGoal.PERDA_PESO_EMAGRECIMENTO,
            FitnessGoal.DEFINICAO_MUSCULAR -> 2.2
            else -> 2.0
        }
        val protein = weight * proteinPerKg
        val fat = weight * 0.9
        val remainingCalories = (target - protein * 4.0 - fat * 9.0).coerceAtLeast(0.0)
        val carbs = remainingCalories / 4.0
        val hydration = (weight * 0.035).coerceAtLeast(2.0)

        return NutritionTargets(
            bmrKcal = bmr.roundToInt(),
            maintenanceCalories = maintenance.roundToInt(),
            targetCalories = target.roundToInt(),
            proteinGrams = oneDecimal(protein),
            carbsGrams = oneDecimal(carbs),
            fatsGrams = oneDecimal(fat),
            hydrationLitres = oneDecimal(hydration)
        )
    }

    private fun oneDecimal(value: Double): Double = (value * 10.0).roundToInt() / 10.0
}
