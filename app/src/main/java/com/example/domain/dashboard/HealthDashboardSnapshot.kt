package com.example.domain.dashboard

/** Snapshot único para a Home. A fonte pode ser Health Connect ou dados locais quando indisponível. */
data class HealthDashboardSnapshot(
    val stepsToday: Long = 0,
    val restingHeartRateBpm: Int? = null,
    val activeCaloriesToday: Int = 0,
    val exerciseMinutesToday: Int = 0,
    val weightKg: Double? = null,
    val lastSyncMillis: Long? = null,
    val healthConnectAvailable: Boolean = false
)

data class ReadinessInputs(
    val health: HealthDashboardSnapshot,
    val workoutsThisWeek: Int,
    val weeklyGoal: Int
)

object ReadinessEngine {
    /** Indicador de orientação, não diagnóstico médico. */
    fun calculate(input: ReadinessInputs): Int {
        var score = 70
        if (input.health.stepsToday in 3_000..12_000) score += 5
        if (input.health.exerciseMinutesToday in 20..90) score += 5
        val ratio = if (input.weeklyGoal > 0) input.workoutsThisWeek.toDouble() / input.weeklyGoal else 0.0
        if (ratio > 1.2) score -= 15 else if (ratio in 0.4..1.0) score += 5
        return score.coerceIn(0, 100)
    }
}
