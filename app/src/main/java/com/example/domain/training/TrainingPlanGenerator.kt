package com.example.domain.training

import com.example.data.model.FitnessLevel
import com.example.data.model.PlannedWorkout
import com.example.data.model.TrainingCycle

/** Deterministic 12-week split used by the initial assessment flow. */
object TrainingPlanGenerator {
    fun generate(cycle: TrainingCycle, templateIds: List<Long>): List<PlannedWorkout> {
        val days = cycle.availableDays.coerceIn(3, 5)
        val split = when (days) {
            3 -> listOf("Corpo inteiro A", "Corpo inteiro B", "Corpo inteiro C")
            4 -> listOf("Superior A", "Inferior A", "Superior B", "Inferior B")
            else -> listOf("Peito e tríceps", "Costas e bíceps", "Pernas", "Ombros", "Core e condicionamento")
        }
        val levelSuffix = when (cycle.fitnessLevel) {
            FitnessLevel.SEDENTARY -> " • adaptação"
            FitnessLevel.BEGINNER -> " • iniciante"
            FitnessLevel.INTERMEDIATE -> " • progressão"
            FitnessLevel.ADVANCED -> " • avançado"
        }
        return (1..cycle.durationWeeks).flatMap { week ->
            split.mapIndexed { index, title ->
                PlannedWorkout(
                    cycleId = cycle.id,
                    templateId = templateIds.takeIf { it.isNotEmpty() }?.let { it[index % it.size] },
                    weekNumber = week,
                    dayOfWeek = (index * 2 + 1).coerceAtMost(7),
                    title = title + levelSuffix
                )
            }
        }
    }
}
