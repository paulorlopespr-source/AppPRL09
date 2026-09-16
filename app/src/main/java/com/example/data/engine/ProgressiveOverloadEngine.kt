package com.example.data.engine

import com.example.data.model.ExerciseExecutionRecord
import com.example.data.model.ExerciseSetEntry

data class ProgressiveOverloadTarget(
    val exerciseName: String,
    val previousWeightKg: Double,
    val previousReps: Int,
    val previousVolumeKg: Double,
    val previous1RMEstimated: Double,
    val previousRIR: Int?,
    val previousRPE: Double?,
    val targetWeightKg: Double,
    val targetReps: Int,
    val targetStrategy: OverloadStrategy,
    val guidanceMessage: String
)

enum class OverloadStrategy(val title: String, val shortDescription: String) {
    INCREASE_WEIGHT("Aumentar Carga (+2.5kg)", "RIR alto na sessão anterior permitindo progressão de carga linear."),
    ADD_REPETITIONS("Adicionar Repetições (+1 a +2 reps)", "Consolidar a carga atual com repetições extras antes de subir peso."),
    IMPROVE_RIR_FORM("Consolidar Técnica & RIR", "Sessão anterior próxima à falha. Manter peso com cadência perfeita."),
    FIRST_BASELINE("Estabelecer Padrão Base", "Primeira sessão registrada. Estabelecer carga e repetições de referência.")
}

object ProgressiveOverloadEngine {

    /**
     * Calcula o 1RM estimado usando a fórmula de Epley:
     */
    fun calculate1RM(weightKg: Double, reps: Int): Double {
        if (reps <= 1) return weightKg
        val val1RM = weightKg * (1.0 + reps / 30.0)
        return Math.round(val1RM * 10.0) / 10.0
    }

    /**
     * Gera meta de sobrecarga progressiva com base no último desempenho e nos dados de esforço (RIR/RPE).
     */
    fun calculateOverloadTarget(
        exerciseName: String,
        lastRecord: ExerciseExecutionRecord?
    ): ProgressiveOverloadTarget {
        if (lastRecord == null || lastRecord.completedSets.isEmpty()) {
            return ProgressiveOverloadTarget(
                exerciseName = exerciseName,
                previousWeightKg = 0.0,
                previousReps = 0,
                previousVolumeKg = 0.0,
                previous1RMEstimated = 0.0,
                previousRIR = null,
                previousRPE = null,
                targetWeightKg = 20.0,
                targetReps = 10,
                targetStrategy = OverloadStrategy.FIRST_BASELINE,
                guidanceMessage = "Primeira sessão deste exercício: Registre suas séries para calibrar sua progressão nas próximas semanas."
            )
        }

        val completed = lastRecord.completedSets
        val topSet = completed.maxByOrNull { it.weightKg } ?: completed.first()
        val prevWeight = topSet.weightKg
        val prevReps = topSet.reps
        val prevVolume = completed.sumOf { it.weightKg * it.reps }
        val prev1RM = calculate1RM(prevWeight, prevReps)
        val avgRir = completed.mapNotNull { it.rir }.average().takeIf { !it.isNaN() }?.toInt()
        val avgRpe = completed.mapNotNull { it.rpe }.average().takeIf { !it.isNaN() }

        return when {
            // Caso 1: RIR >= 2 ou RPE <= 8.0 -> Atleta tinha reserva, pronto para aumento de carga
            (avgRir != null && avgRir >= 2) || (avgRpe != null && avgRpe <= 8.0) -> {
                val newWeight = prevWeight + 2.5
                ProgressiveOverloadTarget(
                    exerciseName = exerciseName,
                    previousWeightKg = prevWeight,
                    previousReps = prevReps,
                    previousVolumeKg = prevVolume,
                    previous1RMEstimated = prev1RM,
                    previousRIR = avgRir,
                    previousRPE = avgRpe,
                    targetWeightKg = newWeight,
                    targetReps = prevReps,
                    targetStrategy = OverloadStrategy.INCREASE_WEIGHT,
                    guidanceMessage = "✦ RIR de sobra ($avgRir reps na reserva). Meta: Subir para ${newWeight}kg mantendo ${prevReps} repetições com técnica sólida!"
                )
            }
            // Caso 2: RIR = 1 ou RPE 8.5 a 9.0 -> Próximo do ponto ideal, progredir em repetições
            (avgRir != null && avgRir == 1) || (avgRpe != null && avgRpe in 8.5..9.2) -> {
                val targetReps = prevReps + 1
                ProgressiveOverloadTarget(
                    exerciseName = exerciseName,
                    previousWeightKg = prevWeight,
                    previousReps = prevReps,
                    previousVolumeKg = prevVolume,
                    previous1RMEstimated = prev1RM,
                    previousRIR = avgRir,
                    previousRPE = avgRpe,
                    targetWeightKg = prevWeight,
                    targetReps = targetReps,
                    targetStrategy = OverloadStrategy.ADD_REPETITIONS,
                    guidanceMessage = "✦ Intensidade calibrada. Meta: Buscar +1 rep (${targetReps} reps com ${prevWeight}kg) antes de adicionar peso nas anilhas."
                )
            }
            // Caso 3: RIR = 0 (falha total) ou RPE >= 9.5 -> Manter carga e focar na qualidade de execução
            (avgRir != null && avgRir == 0) || (avgRpe != null && avgRpe >= 9.5) -> {
                ProgressiveOverloadTarget(
                    exerciseName = exerciseName,
                    previousWeightKg = prevWeight,
                    previousReps = prevReps,
                    previousVolumeKg = prevVolume,
                    previous1RMEstimated = prev1RM,
                    previousRIR = avgRir,
                    previousRPE = avgRpe,
                    targetWeightKg = prevWeight,
                    targetReps = prevReps,
                    targetStrategy = OverloadStrategy.IMPROVE_RIR_FORM,
                    guidanceMessage = "✦ Treino no limite (RPE alto/Falha). Meta: Manter ${prevWeight}kg x ${prevReps} reps melhorando a velocidade excêntrica e conexão muscular."
                )
            }
            // Caso padrão (quando não havia RIR informado no histórico anterior)
            else -> {
                if (prevReps >= 12) {
                    val newWeight = prevWeight + 2.5
                    ProgressiveOverloadTarget(
                        exerciseName = exerciseName,
                        previousWeightKg = prevWeight,
                        previousReps = prevReps,
                        previousVolumeKg = prevVolume,
                        previous1RMEstimated = prev1RM,
                        previousRIR = null,
                        previousRPE = null,
                        targetWeightKg = newWeight,
                        targetReps = 10,
                        targetStrategy = OverloadStrategy.INCREASE_WEIGHT,
                        guidanceMessage = "✦ Você alcançou 12+ reps na sessão anterior. Meta sugerida: ${newWeight}kg para 10 reps."
                    )
                } else {
                    ProgressiveOverloadTarget(
                        exerciseName = exerciseName,
                        previousWeightKg = prevWeight,
                        previousReps = prevReps,
                        previousVolumeKg = prevVolume,
                        previous1RMEstimated = prev1RM,
                        previousRIR = null,
                        previousRPE = null,
                        targetWeightKg = prevWeight,
                        targetReps = prevReps + 1,
                        targetStrategy = OverloadStrategy.ADD_REPETITIONS,
                        guidanceMessage = "✦ Meta: Bater ${prevReps + 1} repetições com ${prevWeight}kg (sobrecarga por volume)."
                    )
                }
            }
        }
    }
}
