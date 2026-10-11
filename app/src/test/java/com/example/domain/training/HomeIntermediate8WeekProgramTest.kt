package com.example.domain.training

import com.example.data.model.Equipment
import com.example.data.model.Exercise
import com.example.data.model.MuscleGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeIntermediate8WeekProgramTest {
    private fun catalog() = listOf(
        Exercise(id = 1, name = "Supino reto com barra", muscleGroup = MuscleGroup.PEITO, equipment = Equipment.BARRA),
        Exercise(id = 2, name = "Desenvolvimento militar com barra", muscleGroup = MuscleGroup.OMBROS, equipment = Equipment.BARRA),
        Exercise(id = 3, name = "Supino reto com halteres", muscleGroup = MuscleGroup.PEITO, equipment = Equipment.HALTERES),
        Exercise(id = 4, name = "Elevação lateral com halteres", muscleGroup = MuscleGroup.OMBROS, equipment = Equipment.HALTERES),
        Exercise(id = 5, name = "Tríceps testa com barra EZ", muscleGroup = MuscleGroup.TRICEPS, equipment = Equipment.BARRA),
        Exercise(id = 6, name = "Tríceps coice com halter", muscleGroup = MuscleGroup.TRICEPS, equipment = Equipment.HALTERES),
        Exercise(id = 7, name = "Agachamento livre com barra", muscleGroup = MuscleGroup.QUADRICEPS, equipment = Equipment.BARRA),
        Exercise(id = 8, name = "Levantamento terra romeno", muscleGroup = MuscleGroup.POSTERIOR_GLUTEOS, equipment = Equipment.BARRA),
        Exercise(id = 9, name = "Remada curvada com barra", muscleGroup = MuscleGroup.COSTAS, equipment = Equipment.BARRA),
        Exercise(id = 10, name = "Afundo com halteres (passada)", muscleGroup = MuscleGroup.QUADRICEPS, equipment = Equipment.HALTERES),
        Exercise(id = 11, name = "Remada unilateral com halter", muscleGroup = MuscleGroup.COSTAS, equipment = Equipment.HALTERES),
        Exercise(id = 12, name = "Rosca direta com barra", muscleGroup = MuscleGroup.BICEPS, equipment = Equipment.BARRA),
        Exercise(id = 13, name = "Panturrilha com halteres", muscleGroup = MuscleGroup.PANTURRILHA, equipment = Equipment.HALTERES),
        Exercise(id = 14, name = "Supino fechado com barra", muscleGroup = MuscleGroup.PEITO, equipment = Equipment.BARRA),
        Exercise(id = 15, name = "Desenvolvimento sentado com halteres", muscleGroup = MuscleGroup.OMBROS, equipment = Equipment.HALTERES),
        Exercise(id = 16, name = "Agachamento búlgaro", muscleGroup = MuscleGroup.QUADRICEPS, equipment = Equipment.HALTERES),
        Exercise(id = 17, name = "Crucifixo com halteres", muscleGroup = MuscleGroup.PEITO, equipment = Equipment.HALTERES),
        Exercise(id = 18, name = "Rosca martelo com halteres", muscleGroup = MuscleGroup.BICEPS, equipment = Equipment.HALTERES),
        Exercise(id = 19, name = "Tríceps francês com halter", muscleGroup = MuscleGroup.TRICEPS, equipment = Equipment.HALTERES)
    )

    @Test
    fun createsStrengthCardioAndRestSessionsWithoutDuplicatingExerciseRecords() {
        val templates = HomeIntermediate8WeekProgram.templates(catalog())

        assertEquals(7, templates.size)
        assertTrue(templates.all { it.isPreset && it.title.startsWith(HomeIntermediate8WeekProgram.TITLE_PREFIX) })
        val strengthTemplates = templates.filter(HomeIntermediate8WeekProgram::isStrengthTemplate)
        assertEquals(3, strengthTemplates.size)
        assertEquals(3, templates.count(HomeIntermediate8WeekProgram::isCardioTemplate))
        assertEquals(1, templates.count(HomeIntermediate8WeekProgram::isRestTemplate))
        assertTrue(strengthTemplates.all { template -> catalog().map { it.id }.containsAll(
            com.squareup.moshi.Moshi.Builder().add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
                .adapter<List<com.example.data.model.WorkoutExercisePlan>>(
                    com.squareup.moshi.Types.newParameterizedType(List::class.java, com.example.data.model.WorkoutExercisePlan::class.java)
                ).fromJson(template.exercisesJson)!!.map { it.exerciseId }
        ) })
        assertEquals("Supino reto com barra", com.squareup.moshi.Moshi.Builder()
            .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
            .adapter<List<com.example.data.model.WorkoutExercisePlan>>(
                com.squareup.moshi.Types.newParameterizedType(List::class.java, com.example.data.model.WorkoutExercisePlan::class.java)
            ).fromJson(strengthTemplates.last().exercisesJson)!!.first().exerciseName)
        assertTrue(templates.first { HomeIntermediate8WeekProgram.cardioCode(it) == "CARDIO_B" }.description.contains("6–8 ciclos de 1 min forte + 2 min leve"))
        assertEquals("home_intermediate_8w_v1:CARDIO_A]", HomeIntermediate8WeekProgram.cardioMarker("CARDIO_A").removePrefix("["))
    }

    @Test
    fun weeksFollowTheEightWeekTrainingPhasesAndDeloadReducesSets() {
        assertEquals(HomeIntermediate8WeekProgram.Phase.CALIBRATION, HomeIntermediate8WeekProgram.weekForCompletedStrengthSessions(0).phase)
        assertEquals(HomeIntermediate8WeekProgram.Phase.PROGRESSION, HomeIntermediate8WeekProgram.weekForCompletedStrengthSessions(6).phase)
        assertEquals(HomeIntermediate8WeekProgram.Phase.DELOAD, HomeIntermediate8WeekProgram.weekForCompletedStrengthSessions(12).phase)
        assertEquals(HomeIntermediate8WeekProgram.Phase.INTENSIFICATION, HomeIntermediate8WeekProgram.weekForCompletedStrengthSessions(15).phase)
        assertEquals(HomeIntermediate8WeekProgram.Phase.CONSOLIDATION, HomeIntermediate8WeekProgram.weekForCompletedStrengthSessions(21).phase)

        val original = HomeIntermediate8WeekProgram.templates(catalog()).first()
        val plan = com.squareup.moshi.Moshi.Builder().add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
            .adapter<List<com.example.data.model.WorkoutExercisePlan>>(
                com.squareup.moshi.Types.newParameterizedType(List::class.java, com.example.data.model.WorkoutExercisePlan::class.java)
            ).fromJson(original.exercisesJson)!!.first()
        assertEquals(4, plan.sets.size)
        assertEquals(3, HomeIntermediate8WeekProgram.applyWeek(plan, 5).sets.size)
        assertTrue(HomeIntermediate8WeekProgram.applyWeek(plan, 5).notes.contains("Deload"))
    }

    @Test
    fun sequenceKeepsThePendingSessionAndOnlyAdvancesAfterEachPlannedWorkout() {
        val events = listOf("A", "B", "CARDIO_A").mapIndexed { index, code ->
            HomeIntermediate8WeekProgram.CompletionEvent(index.toLong(), code)
        }
        val progress = HomeIntermediate8WeekProgram.progress(events)

        assertEquals(1, progress.weekNumber)
        assertEquals("B", progress.nextSessionCode)
        assertEquals(2, progress.completedInWeek)
    }

    @Test
    fun fullEightWeekSequenceCompletesAndMarksRecoveryAfterCardioC() {
        val events = (1..8).flatMap { week ->
            listOf("A", "CARDIO_A", "B", "CARDIO_B", "C", "CARDIO_C").mapIndexed { index, code ->
                HomeIntermediate8WeekProgram.CompletionEvent((week * 10L) + index, code)
            }
        }
        val progress = HomeIntermediate8WeekProgram.progress(events)

        assertEquals(8, progress.weekNumber)
        assertEquals(null, progress.nextSessionCode)
        assertTrue(progress.isComplete)
        assertTrue(progress.recoveryRecommended)
        assertEquals("CARDIO_C", HomeIntermediate8WeekProgram.cardioCodeFromNotes("${HomeIntermediate8WeekProgram.cardioMarker("CARDIO_C")} · caminhada"))
    }
}
