package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Incremental local checkpoint for recovery of an active cardio session. */
@Entity(tableName = "active_cardio_sessions", indices = [Index(value = ["updatedAtMillis"])])
data class ActiveCardioSessionRecord(
    @PrimaryKey val sessionUuid: String,
    val cardioType: String,
    val intensity: String,
    val status: String,
    val startedAtMillis: Long,
    val durationSeconds: Long,
    val accumulatedDurationSeconds: Long,
    val resumeStartedElapsedRealtime: Long,
    val bootCount: Int,
    val distanceKm: Double,
    val currentSpeedKmh: Double,
    val avgSpeedKmh: Double,
    val currentPaceMinKm: String,
    val avgPaceMinKm: String,
    val caloriesBurned: Int,
    val elevationGainMeters: Double,
    val accuracyMeters: Float?,
    val currentLocationJson: String?,
    val routePointsJson: String,
    val splitsJson: String,
    val locationName: String,
    val targetMinutes: Int?,
    val steps: Long?,
    val stepSensorAvailable: Boolean?,
    val plannedSessionCode: String?,
    val pausedBySystem: Boolean,
    val userWeightKg: Double?,
    @androidx.room.ColumnInfo(defaultValue = "''")
    val intervalConfigJson: String = "",
    val gpsEnabled: Boolean = false,
    val updatedAtMillis: Long = System.currentTimeMillis()
)
