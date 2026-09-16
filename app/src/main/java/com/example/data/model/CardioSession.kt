package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

enum class CardioType(
    val title: String,
    val description: String,
    val iconKey: String,
    val metLight: Double,
    val metModerate: Double,
    val metIntense: Double
) {
    BICICLETA_INDOOR(
        title = "Bicicleta Indoor / Spinning",
        description = "Pedalada em bicicleta ergométrica ou spinning. Excelente queima calórica com baixo impacto articular.",
        iconKey = "PedalBike",
        metLight = 5.5,
        metModerate = 7.0,
        metIntense = 9.5
    ),
    CAMINHADA_ESTEIRA(
        title = "Caminhada na Esteira",
        description = "Caminhada controlada com opção de inclinação e velocidade constante na esteira ergométrica.",
        iconKey = "DirectionsWalk",
        metLight = 3.3,
        metModerate = 4.3,
        metIntense = 5.5
    ),
    CAMINHADA_AR_LIVRE(
        title = "Caminhada ao Ar Livre",
        description = "Caminhada em parques, praças ou na rua com variação natural de terreno e ar puro.",
        iconKey = "Park",
        metLight = 3.5,
        metModerate = 4.8,
        metIntense = 6.0
    ),
    CORRIDA(
        title = "Corrida",
        description = "Corrida contínua ou intervalada (HIIT). Alto estímulo cardiovascular e condicionamento de elite.",
        iconKey = "DirectionsRun",
        metLight = 7.5,
        metModerate = 9.8,
        metIntense = 12.0
    ),
    FUTEBOL(
        title = "Futebol",
        description = "Partida em campo, society ou futsal. Exercício intermitente de alta intensidade com agilidade.",
        iconKey = "SportsSoccer",
        metLight = 7.0,
        metModerate = 8.5,
        metIntense = 10.0
    )
}

enum class IntensityLevel(val label: String) {
    LEVE("Leve"),
    MODERADA("Moderada"),
    INTENSA("Intensa")
}

@JsonClass(generateAdapter = true)
@Entity(
    tableName = "cardio_sessions",
    indices = [
        Index(value = ["dateEpochDay"])
    ]
)
data class CardioSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: CardioType,
    val dateEpochDay: Long,
    val timestampMillis: Long = System.currentTimeMillis(),
    val durationMinutes: Int,
    val distanceKm: Double? = null,
    val avgHeartRateBpm: Int? = null,
    val intensity: IntensityLevel = IntensityLevel.MODERADA,
    val location: String = "Academia Smart Fit",
    val caloriesBurned: Int = 0,
    val notes: String = "",
    val aiEvaluation: String = ""
)
