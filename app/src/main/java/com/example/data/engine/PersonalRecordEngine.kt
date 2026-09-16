package com.example.data.engine

import com.example.data.model.ExerciseSetEntry
import com.example.data.model.WorkoutExercisePlan
import com.example.data.model.WorkoutSession
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class PersonalRecordEvaluation(
    val isPR: Boolean,
    val prType: String, // "Carga", "1RM", "Volume", "Repetições"
    val previousRecordValue: Double,
    val newRecordValue: Double,
    val badgeLabel: String,
    val celebrationDescription: String
)

data class ExerciseAllTimeBests(
    val exerciseName: String,
    val maxWeightKg: Double = 0.0,
    val maxEstimated1RM: Double = 0.0,
    val maxSetVolumeKg: Double = 0.0,
    val maxReps: Int = 0,
    val totalLoggedSets: Int = 0
)

object PersonalRecordEngine {

    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val planListType = Types.newParameterizedType(List::class.java, WorkoutExercisePlan::class.java)
    private val plansAdapter = moshi.adapter<List<WorkoutExercisePlan>>(planListType)

    /**
     * Calcula o 1RM estimado usando a fórmula de Epley:
     * 1RM = Carga * (1 + Reps / 30)
     */
    fun calculate1RM(weightKg: Double, reps: Int): Double {
        if (reps <= 1) return weightKg
        val val1RM = weightKg * (1.0 + reps / 30.0)
        return Math.round(val1RM * 10.0) / 10.0
    }

    /**
     * Extrai todos os recordes históricos para um dado exercício a partir das sessões de treino.
     */
    fun computeAllTimeBests(exerciseName: String, sessions: List<WorkoutSession>): ExerciseAllTimeBests {
        var maxWeight = 0.0
        var max1RM = 0.0
        var maxVolume = 0.0
        var maxReps = 0
        var setCounter = 0

        val normalizedName = exerciseName.trim().lowercase()

        sessions.forEach { session ->
            val plans = try {
                plansAdapter.fromJson(session.exercisesDoneJson) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            plans.filter { it.exerciseName.trim().lowercase() == normalizedName }.forEach { plan ->
                plan.sets.filter { it.isCompleted }.forEach { set ->
                    setCounter++
                    if (set.weightKg > maxWeight) maxWeight = set.weightKg
                    val set1RM = calculate1RM(set.weightKg, set.reps)
                    if (set1RM > max1RM) max1RM = set1RM
                    val vol = set.weightKg * set.reps
                    if (vol > maxVolume) maxVolume = vol
                    if (set.reps > maxReps) maxReps = set.reps
                }
            }
        }

        return ExerciseAllTimeBests(
            exerciseName = exerciseName,
            maxWeightKg = maxWeight,
            maxEstimated1RM = max1RM,
            maxSetVolumeKg = maxVolume,
            maxReps = maxReps,
            totalLoggedSets = setCounter
        )
    }

    /**
     * Avalia se uma nova série executada representa um Recorde Pessoal (PR).
     */
    fun evaluateSetForPR(
        exerciseName: String,
        weightKg: Double,
        reps: Int,
        sessions: List<WorkoutSession>
    ): PersonalRecordEvaluation? {
        if (weightKg <= 0 || reps <= 0) return null

        val bests = computeAllTimeBests(exerciseName, sessions)
        if (bests.totalLoggedSets == 0) {
            // Primeiro registro é a primeira marca do atleta
            val current1RM = calculate1RM(weightKg, reps)
            return PersonalRecordEvaluation(
                isPR = true,
                prType = "Primeira Marca",
                previousRecordValue = 0.0,
                newRecordValue = weightKg,
                badgeLabel = "NOVA MARCA",
                celebrationDescription = "Primeira marca registrada: ${weightKg}kg para ${reps} reps!"
            )
        }

        val current1RM = calculate1RM(weightKg, reps)
        val currentVolume = weightKg * reps

        // 1. Recorde de Carga Absoluta
        if (weightKg > bests.maxWeightKg) {
            val delta = Math.round((weightKg - bests.maxWeightKg) * 10.0) / 10.0
            return PersonalRecordEvaluation(
                isPR = true,
                prType = "Carga",
                previousRecordValue = bests.maxWeightKg,
                newRecordValue = weightKg,
                badgeLabel = "PR DE CARGA",
                celebrationDescription = "🏆 NOVO RECORDE DE CARGA! +${delta}kg (Anterior: ${bests.maxWeightKg}kg)"
            )
        }

        // 2. Recorde de 1RM Estimado (Força Absoluta convertida)
        if (current1RM > bests.maxEstimated1RM && current1RM - bests.maxEstimated1RM >= 0.5) {
            val delta = Math.round((current1RM - bests.maxEstimated1RM) * 10.0) / 10.0
            return PersonalRecordEvaluation(
                isPR = true,
                prType = "1RM",
                previousRecordValue = bests.maxEstimated1RM,
                newRecordValue = current1RM,
                badgeLabel = "PR DE 1RM",
                celebrationDescription = "🔥 NOVO 1RM ESTIMADO: ${current1RM}kg (+${delta}kg de ganho de força real)"
            )
        }

        // 3. Recorde de Volume por Série (Tonelagem do set)
        if (currentVolume > bests.maxSetVolumeKg && currentVolume - bests.maxSetVolumeKg >= 10.0) {
            val delta = Math.round((currentVolume - bests.maxSetVolumeKg) * 10.0) / 10.0
            return PersonalRecordEvaluation(
                isPR = true,
                prType = "Volume",
                previousRecordValue = bests.maxSetVolumeKg,
                newRecordValue = currentVolume,
                badgeLabel = "PR DE VOLUME",
                celebrationDescription = "⚡ NOVO RECORDE DE VOLUME NA SÉRIE! ${currentVolume.toInt()}kg levantados (+${delta.toInt()}kg)"
            )
        }

        // 4. Recorde de Repetições
        if (reps > bests.maxReps && weightKg >= bests.maxWeightKg * 0.7) {
            return PersonalRecordEvaluation(
                isPR = true,
                prType = "Repetições",
                previousRecordValue = bests.maxReps.toDouble(),
                newRecordValue = reps.toDouble(),
                badgeLabel = "PR DE REPS",
                celebrationDescription = "💪 RECORDE DE REPETIÇÕES: ${reps} reps com ${weightKg}kg!"
            )
        }

        return null
    }
}
