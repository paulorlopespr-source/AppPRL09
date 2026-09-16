package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GpsPoint(
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double? = null,
    val timestampMillis: Long = System.currentTimeMillis(),
    val speedKmh: Double = 0.0,
    val accuracyMeters: Float? = null
)

@JsonClass(generateAdapter = true)
data class KmSplit(
    val kmIndex: Int,
    val splitDurationSeconds: Int,
    val totalElapsedSeconds: Int,
    val avgSpeedKmh: Double,
    val avgPaceMinKm: String,
    val elevationGainMeters: Double = 0.0
)

enum class OutdoorTrackingStatus {
    STOPPED,
    RUNNING,
    PAUSED
}

data class OutdoorSessionState(
    val status: OutdoorTrackingStatus = OutdoorTrackingStatus.STOPPED,
    val durationSeconds: Long = 0L,
    val distanceKm: Double = 0.0,
    val currentSpeedKmh: Double = 0.0,
    val avgSpeedKmh: Double = 0.0,
    val currentPaceMinKm: String = "--:--",
    val avgPaceMinKm: String = "--:--",
    val caloriesBurned: Int = 0,
    val elevationGainMeters: Double = 0.0,
    val accuracyMeters: Float? = null,
    val currentLocation: GpsPoint? = null,
    val routePoints: List<GpsPoint> = emptyList(),
    val splits: List<KmSplit> = emptyList(),
    val cardioType: CardioType = CardioType.CORRIDA,
    val intensity: IntensityLevel = IntensityLevel.MODERADA,
    val locationName: String = "Outdoor Tracker",
    val targetMinutes: Int? = null
) {
    val isRunning: Boolean get() = status == OutdoorTrackingStatus.RUNNING
    val isPaused: Boolean get() = status == OutdoorTrackingStatus.PAUSED
    val isStopped: Boolean get() = status == OutdoorTrackingStatus.STOPPED
    val isActive: Boolean get() = status != OutdoorTrackingStatus.STOPPED
}
