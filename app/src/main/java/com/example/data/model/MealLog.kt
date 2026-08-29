package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "meal_logs")
@JsonClass(generateAdapter = true)
data class MealLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateEpochDay: Long,
    val timestampMillis: Long = System.currentTimeMillis(),
    val mealType: String, // Café da Manhã, Almoço, Pré-Treino, Pós-Treino, Jantar, Ceia/Lanche
    val description: String,
    val estimatedCalories: Int,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatsGrams: Double,
    val fiberGrams: Double = 0.0,
    val aiInsight: String = "",
    val healthRating: String = "Excelente" // Excelente, Bom, Equilibrado, Atenção
)

@JsonClass(generateAdapter = true)
data class MealAnalysisResult(
    val title: String,
    val estimatedCalories: Int,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatsGrams: Double,
    val fiberGrams: Double,
    val healthRating: String,
    val summary: String,
    val suggestions: List<String>
)
