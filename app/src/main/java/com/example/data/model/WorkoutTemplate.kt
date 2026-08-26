package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

enum class WorkoutCategory(val label: String) {
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
    val exercisesJson: String = "[]",
    val description: String = ""
)
