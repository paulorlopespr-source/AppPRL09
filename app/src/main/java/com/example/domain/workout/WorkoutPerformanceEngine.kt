package com.example.domain.workout

import com.example.data.model.ExerciseExecutionRecord
import com.example.data.model.ExerciseSetEntry
import kotlin.math.round

data class ExercisePerformanceSnapshot(
    val maxWeightKg: Double,
    val maxReps: Int,
    val totalVolumeKg: Double,
    val estimated1RM: Double
)

enum class PersonalRecordType { MAX_WEIGHT, MAX_REPS, TOTAL_VOLUME, ESTIMATED_1RM }

data class PersonalRecord(
    val type: PersonalRecordType,
    val previousValue: Double,
    val newValue: Double
)

data class ProgressiveTarget(
    val suggestedWeightKg: Double,
    val suggestedReps: Int,
    val reason: String,
    val lastPerformance: ExercisePerformanceSnapshot?,
    val bestPerformance: ExercisePerformanceSnapshot?
)

object WorkoutPerformanceEngine {
    fun snapshot(sets: List<ExerciseSetEntry>): ExercisePerformanceSnapshot {
        val completed = sets.filter { it.isCompleted && it.reps > 0 && it.weightKg >= 0 }
        return ExercisePerformanceSnapshot(
            maxWeightKg = completed.maxOfOrNull { it.weightKg } ?: 0.0,
            maxReps = completed.maxOfOrNull { it.reps } ?: 0,
            totalVolumeKg = completed.sumOf { it.weightKg * it.reps },
            estimated1RM = completed.maxOfOrNull { it.estimated1RM } ?: 0.0
        )
    }

    fun detectPRs(current: List<ExerciseSetEntry>, history: List<ExerciseExecutionRecord>): List<PersonalRecord> {
        val now = snapshot(current)
        val previous = history.map { snapshot(it.sets) }
        if (previous.isEmpty()) return emptyList()
        val bestWeight = previous.maxOf { it.maxWeightKg }
        val bestReps = previous.maxOf { it.maxReps }.toDouble()
        val bestVolume = previous.maxOf { it.totalVolumeKg }
        val best1RM = previous.maxOf { it.estimated1RM }
        return buildList {
            if (now.maxWeightKg > bestWeight) add(PersonalRecord(PersonalRecordType.MAX_WEIGHT, bestWeight, now.maxWeightKg))
            if (now.maxReps > bestReps) add(PersonalRecord(PersonalRecordType.MAX_REPS, bestReps, now.maxReps.toDouble()))
            if (now.totalVolumeKg > bestVolume) add(PersonalRecord(PersonalRecordType.TOTAL_VOLUME, bestVolume, now.totalVolumeKg))
            if (now.estimated1RM > best1RM) add(PersonalRecord(PersonalRecordType.ESTIMATED_1RM, best1RM, now.estimated1RM))
        }
    }

    fun progressiveTarget(history: List<ExerciseExecutionRecord>, incrementKg: Double = 2.5): ProgressiveTarget {
        val ordered = history.sortedByDescending { it.sessionStartTimeMillis }
        val last = ordered.firstOrNull()?.let { snapshot(it.sets) }
        val best = ordered.map { snapshot(it.sets) }.maxByOrNull { it.estimated1RM }
        if (last == null) return ProgressiveTarget(0.0, 10, "Primeiro registro: use uma carga confortável e preserve 2-3 RIR.", null, null)

        val completed = ordered.first().sets.filter { it.isCompleted }
        val avgRir = completed.mapNotNull { it.rir }.takeIf { it.isNotEmpty() }?.average()
        val avgRpe = completed.mapNotNull { it.rpe }.takeIf { it.isNotEmpty() }?.average()
        val canProgress = (avgRir != null && avgRir >= 2.0) || (avgRpe != null && avgRpe <= 8.0)
        val suggestedWeight = if (canProgress) roundToHalf(last.maxWeightKg + incrementKg) else last.maxWeightKg
        val suggestedReps = if (canProgress) completed.maxOfOrNull { it.reps } ?: 10 else ((completed.maxOfOrNull { it.reps } ?: 10) + 1)
        val reason = if (canProgress) "Esforço controlado no último treino: pequena progressão de carga sugerida." else "Esforço alto ou sem RIR/RPE suficiente: consolide repetições antes de subir a carga."
        return ProgressiveTarget(suggestedWeight, suggestedReps, reason, last, best)
    }

    private fun roundToHalf(value: Double): Double = round(value * 2.0) / 2.0
}
