package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

enum class MuscleGroup(val displayName: String, val iconName: String) {
    PEITO("Peito", "FitnessCenter"),
    COSTAS("Costas", "Accessibility"),
    QUADRICEPS("Quadríceps (Pernas)", "DirectionsRun"),
    POSTERIOR_GLUTEOS("Posterior & Glúteos", "SportsGymnastics"),
    OMBROS("Ombros", "SportsHandball"),
    BICEPS("Bíceps", "FitnessCenter"),
    TRICEPS("Tríceps", "FitnessCenter"),
    ABDOMEN("Abdômen", "SelfImprovement"),
    PANTURRILHA("Panturrilhas", "DirectionsWalk")
}

enum class Equipment(val displayName: String) {
    BARRA("Barra"), HALTERES("Halteres"), MAQUINA("Máquina"), POLIA("Polia / Cabo"),
    PESO_CORPO("Peso Corporal"), ELASTICO("Elástico / Outro")
}

enum class SetTag(val code: String, val label: String, val shortLabel: String, val badgeColorHex: Long) {
    NORMAL("N", "Série Normal", "Normal", 0xFF8E8EA0),
    WARMUP("W", "Aquecimento", "Aquec.", 0xFFFFB300),
    FEEDER("P", "Preparatória / Feeder", "Prep.", 0xFF00BCD4),
    DROPSET("D", "Drop-Set", "Drop", 0xFFFF5722),
    FAILURE("F", "Até a Falha (RPE 10)", "Falha", 0xFFE91E63)
}

@JsonClass(generateAdapter = true)
@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val muscleGroup: MuscleGroup,
    val equipment: Equipment,
    val defaultSets: Int = 4,
    val defaultReps: Int = 10,
    val defaultRestSeconds: Int = 60,
    val instructions: String = "",
    val executionTips: String = ""
)

/** Registro completo de uma série. Campos novos têm defaults para manter compatibilidade com JSON antigo. */
@JsonClass(generateAdapter = true)
data class ExerciseSetEntry(
    val setNumber: Int,
    var weightKg: Double,
    var reps: Int,
    var isCompleted: Boolean = false,
    var restSeconds: Int = 60,
    var setTag: SetTag = SetTag.NORMAL,
    var rir: Int? = null,
    var rpe: Double? = null,
    var notes: String = "",
    var completedAtMillis: Long? = null
) {
    val volumeKg: Double get() = if (isCompleted) weightKg * reps else 0.0
    val estimated1RM: Double get() = if (!isCompleted || reps <= 0) 0.0 else weightKg * (1.0 + reps / 30.0)

    fun normalized(): ExerciseSetEntry = copy(
        rir = rir?.coerceIn(0, 10),
        rpe = rpe?.coerceIn(1.0, 10.0),
        restSeconds = restSeconds.coerceAtLeast(0)
    )
}

@JsonClass(generateAdapter = true)
data class WorkoutExercisePlan(
    val exerciseId: Long,
    val exerciseName: String,
    val muscleGroup: String,
    val sets: List<ExerciseSetEntry>,
    val targetRestSeconds: Int = 60,
    val notes: String = ""
)
