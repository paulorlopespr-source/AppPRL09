package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

enum class FitnessGoal(val label: String, val description: String) {
    GANHO_PESO_HIPERTROFIA(
        label = "Ganho de Peso & Hipertrofia",
        description = "Foco em superávit calórico controlado, sobrecarga progressiva e ganho de massa magra."
    ),
    PERDA_PESO_EMAGRECIMENTO(
        label = "Perda de Peso & Emagrecimento",
        description = "Foco em déficit calórico, preservação de massa magra e aumento de gasto com cardio."
    ),
    DEFINICAO_MUSCULAR(
        label = "Definição Muscular",
        description = "Equilíbrio entre queima de gordura e densidade muscular com treinos intensos."
    ),
    CONDICIONAMENTO_GERAL(
        label = "Condicionamento & Saúde",
        description = "Melhora da resistência cardiorrespiratória, postura e qualidade de vida."
    )
}

@JsonClass(generateAdapter = true)
@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Atleta",
    val age: Int = 26,
    val heightCm: Double = 175.0,
    val startingWeightKg: Double = 78.0,
    val currentWeightKg: Double = 78.0,
    val targetWeightKg: Double = 82.0,
    val goal: FitnessGoal = FitnessGoal.GANHO_PESO_HIPERTROFIA,
    val defaultGymLocation: String = "Academia Smart Fit",
    val weeklyGoalDays: Int = 5,
    val defaultRestSeconds: Int = 60,
    val aiCaloricAdvice: String = ""
)

@JsonClass(generateAdapter = true)
@Entity(tableName = "body_measurements")
data class BodyMeasurement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val timestampMillis: Long = System.currentTimeMillis(),
    val weightKg: Double,
    val bodyFatPercentage: Double? = null,
    val chestCm: Double? = null,
    val waistCm: Double? = null,
    val armCm: Double? = null,
    val thighCm: Double? = null,
    val calfCm: Double? = null,
    val notes: String = ""
)

@JsonClass(generateAdapter = true)
@Entity(tableName = "progress_photos")
data class ProgressPhoto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val timestampMillis: Long = System.currentTimeMillis(),
    val imageUri: String,
    val weightKg: Double? = null,
    val monthLabel: String = "",
    val isInitial: Boolean = false,
    val bodyFatPercentage: Double? = null,
    val notes: String = ""
)

