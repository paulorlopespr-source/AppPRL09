package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

enum class CardioType(val title: String, val description: String, val iconKey: String, val metLight: Double, val metModerate: Double, val metIntense: Double) {
    BICICLETA_INDOOR("Bicicleta Indoor / Spinning", "Pedalada em bicicleta ergométrica ou spinning.", "PedalBike", 5.5, 7.0, 9.5),
    BIKE_OUTDOOR("Bike Outdoor", "Ciclismo ao ar livre com GPS, velocidade e altimetria.", "PedalBike", 5.0, 8.0, 12.0),
    CAMINHADA_ESTEIRA("Caminhada na Esteira", "Caminhada controlada em esteira.", "DirectionsWalk", 3.3, 4.3, 5.5),
    CAMINHADA_AR_LIVRE("Caminhada ao Ar Livre", "Caminhada com GPS em parques, trilhas leves ou ruas.", "Park", 3.5, 4.8, 6.0),
    TRILHA("Trilha", "Caminhada em terreno natural com rota e altimetria.", "Terrain", 4.5, 6.5, 9.0),
    CORRIDA("Corrida", "Corrida contínua ao ar livre ou em ambiente controlado.", "DirectionsRun", 7.5, 9.8, 12.0),
    CORRIDA_TRILHA("Corrida em Trilha", "Trail running com GPS, splits e ganho de elevação.", "Landscape", 8.0, 10.5, 13.5),
    CORRIDA_INTERVALADA("Corrida Intervalada", "Sessão intervalada com blocos rápidos e recuperação.", "Timer", 7.0, 10.0, 13.0),
    BIKE_INTERVALADA("Bike Intervalada", "Ciclismo intervalado com blocos de esforço e recuperação.", "Timer", 6.0, 9.0, 12.0),
    FUTEBOL("Futebol", "Partida em campo, society ou futsal.", "SportsSoccer", 7.0, 8.5, 10.0)
}

enum class IntensityLevel(val label: String) { LEVE("Leve"), MODERADA("Moderada"), INTENSA("Intensa") }

@JsonClass(generateAdapter = true)
data class RoutePoint(
    val latitude: Double,
    val longitude: Double,
    val timestampMillis: Long,
    val accuracyMeters: Float? = null,
    val speedMetersPerSecond: Float? = null,
    val altitudeMeters: Double? = null
)

@JsonClass(generateAdapter = true)
data class ActivitySplit(
    val kilometer: Int,
    val elapsedSeconds: Int,
    val movingSeconds: Int,
    val paceSecondsPerKm: Int,
    val elevationGainMeters: Double = 0.0
)

@JsonClass(generateAdapter = true)
@Entity(tableName = "cardio_sessions")
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
    val aiEvaluation: String = "",
    val movingTimeSeconds: Int = 0,
    val routeJson: String = "[]",
    val splitsJson: String = "[]",
    val elevationGainMeters: Double = 0.0,
    val minElevationMeters: Double? = null,
    val maxElevationMeters: Double? = null,
    val avgSpeedKmh: Double? = null,
    val avgPaceSecondsPerKm: Int? = null
)
