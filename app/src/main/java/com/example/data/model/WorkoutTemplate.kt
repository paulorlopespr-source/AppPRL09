package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

enum class WorkoutCategory(val label: String) {
    INICIANTE("Iniciante (ABC)"),
    INTERMEDIARIO("Intermediário (ABC)"),
    AVANCADO("Avançado (ABC)"),
    HIPERTROFIA("Hipertrofia"),
    FORCA("Força"),
    DEFINICAO("Definição"),
    RESISTENCIA("Resistência"),
    FEMININO("Foco Feminino"),
    FULLBODY("Full Body"),
    PERSONALIZADO("Personalizado")
}

@JsonClass(generateAdapter = true)
@Entity(tableName = "workout_templates")
data class WorkoutTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String,
    val category: WorkoutCategory,
    val defaultRestSeconds: Int = 60,
    val executionDurationMinutes: Int = 50,
    val isPreset: Boolean = false,
    val isFavorite: Boolean = false,
    val exercisesJson: String = "[]",
    val description: String = "",
    val createdAtEpochDay: Long = 0,
    val timesCompleted: Int = 0
) {
    val exerciseCount: Int
        get() {
            return try {
                val count = exercisesJson.split("\"exerciseName\"").size - 1
                if (count > 0) count else 4
            } catch (_: Exception) {
                4
            }
        }
}
