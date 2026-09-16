package com.example.domain.coach

import com.example.data.model.*
import com.example.domain.dashboard.DashboardEngine
import com.example.domain.readiness.ReadinessResult
import java.time.LocalDate

data class CoachPeriodSummary(
    val days: Int,
    val strengthSessions: Int,
    val strengthVolumeKg: Double,
    val averageRpe: Double?,
    val cardioSessions: Int,
    val cardioMinutes: Int,
    val cardioDistanceKm: Double
)

data class CoachPRL09Context(
    val generatedAtEpochDay: Long,
    val goal: String,
    val weeklyGoalDays: Int,
    val currentWeightKg: Double?,
    val weightChange28dKg: Double?,
    val streakDays: Int,
    val last7Days: CoachPeriodSummary,
    val last28Days: CoachPeriodSummary,
    val readinessScore: Int?,
    val readinessLabel: String?,
    val readinessReasons: List<String>,
    val readinessRecommendation: String?,
    val recentPersonalRecords: List<String>,
    val equipment: List<String>
) {
    /** Texto factual fornecido à IA. Números são calculados localmente e devem ser tratados como fonte de verdade. */
    fun asGroundedText(): String = buildString {
        appendLine("CONTEXTO DETERMINÍSTICO DO APP — NÃO RECALCULAR NEM INVENTAR NÚMEROS")
        appendLine("Objetivo: $goal")
        appendLine("Meta semanal: $weeklyGoalDays dias")
        appendLine("Peso atual: ${currentWeightKg?.let { "%.1f kg".format(it) } ?: "não registrado"}")
        appendLine("Variação de peso em 28 dias: ${weightChange28dKg?.let { "%+.1f kg".format(it) } ?: "indisponível"}")
        appendLine("Streak: $streakDays dias")
        appendLine(periodText("Últimos 7 dias", last7Days))
        appendLine(periodText("Últimos 28 dias", last28Days))
        appendLine("Readiness: ${readinessScore?.let { "$it/100" } ?: "sem check-in"}${readinessLabel?.let { " — $it" } ?: ""}")
        if (readinessReasons.isNotEmpty()) appendLine("Motivos readiness: ${readinessReasons.joinToString("; ")}")
        readinessRecommendation?.let { appendLine("Recomendação determinística: $it") }
        if (recentPersonalRecords.isNotEmpty()) appendLine("PRs recentes: ${recentPersonalRecords.joinToString("; ")}")
        appendLine("Equipamentos informados: ${equipment.ifEmpty { listOf("não informado") }.joinToString()}")
    }

    private fun periodText(label: String, p: CoachPeriodSummary) =
        "$label: musculação=${p.strengthSessions} sessões, volume=${"%.0f".format(p.strengthVolumeKg)} kg, RPE médio=${p.averageRpe?.let { "%.1f".format(it) } ?: "--"}; cardio=${p.cardioSessions} sessões, ${p.cardioMinutes} min, ${"%.1f".format(p.cardioDistanceKm)} km"
}

object CoachContextBuilder {
    fun build(
        profile: UserProfile?,
        workouts: List<WorkoutSession>,
        cardio: List<CardioSession>,
        measurements: List<BodyMeasurement>,
        readiness: ReadinessResult?,
        personalRecords: List<String> = emptyList(),
        equipment: List<String> = emptyList(),
        today: LocalDate = LocalDate.now()
    ): CoachPRL09Context {
        val dashboard = DashboardEngine.home(profile, workouts, cardio, measurements, today)
        val evolution = DashboardEngine.evolution(workouts, cardio, measurements, emptyList(), 28, today)
        return CoachPRL09Context(
            generatedAtEpochDay = today.toEpochDay(),
            goal = profile?.goal?.label ?: "não informado",
            weeklyGoalDays = profile?.weeklyGoalDays ?: 0,
            currentWeightKg = dashboard.currentWeightKg,
            weightChange28dKg = evolution.weightChangeKg,
            streakDays = dashboard.streakDays,
            last7Days = summarize(7, workouts, cardio, today),
            last28Days = summarize(28, workouts, cardio, today),
            readinessScore = readiness?.score,
            readinessLabel = readiness?.label,
            readinessReasons = readiness?.reasons.orEmpty(),
            readinessRecommendation = readiness?.recommendation,
            recentPersonalRecords = personalRecords,
            equipment = equipment
        )
    }

    private fun summarize(days: Long, workouts: List<WorkoutSession>, cardio: List<CardioSession>, today: LocalDate): CoachPeriodSummary {
        val from = today.minusDays(days - 1).toEpochDay()
        val to = today.toEpochDay()
        val w = workouts.filter { it.status == SessionStatus.COMPLETED && it.dateEpochDay in from..to }
        val c = cardio.filter { it.dateEpochDay in from..to }
        val rpes = w.map { it.perceivedExertion }.filter { it > 0 }
        return CoachPeriodSummary(
            days = days.toInt(),
            strengthSessions = w.size,
            strengthVolumeKg = w.sumOf { it.totalWeightLiftedKg },
            averageRpe = rpes.takeIf { it.isNotEmpty() }?.average(),
            cardioSessions = c.size,
            cardioMinutes = c.sumOf { it.durationMinutes },
            cardioDistanceKm = c.sumOf { it.distanceKm ?: 0.0 }
        )
    }
}
