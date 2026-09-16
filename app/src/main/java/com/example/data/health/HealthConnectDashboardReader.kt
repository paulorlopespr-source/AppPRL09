package com.example.data.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.domain.dashboard.HealthDashboardSnapshot
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class HealthConnectDashboardReader(private val context: Context) {
    val permissions = setOf(
        "android.permission.health.READ_STEPS",
        "android.permission.health.READ_HEART_RATE",
        "android.permission.health.READ_ACTIVE_CALORIES_BURNED",
        "android.permission.health.READ_EXERCISE",
        "android.permission.health.READ_WEIGHT"
    )

    fun isAvailable(): Boolean = HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE

    suspend fun readToday(now: Instant = Instant.now()): HealthDashboardSnapshot {
        if (!isAvailable()) return HealthDashboardSnapshot(healthConnectAvailable = false)
        val client = HealthConnectClient.getOrCreate(context)
        val granted = client.permissionController.getGrantedPermissions()
        if (!granted.containsAll(permissions)) return HealthDashboardSnapshot(healthConnectAvailable = true)

        val zone = ZoneId.systemDefault()
        val start = LocalDate.now(zone).atStartOfDay(zone).toInstant()
        val range = TimeRangeFilter.between(start, now)
        val aggregate = client.aggregate(AggregateRequest(setOf(StepsRecord.COUNT_TOTAL, ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL), range))
        val exercises = client.readRecords(ReadRecordsRequest(ExerciseSessionRecord::class, range)).records
        val heart = client.readRecords(ReadRecordsRequest(HeartRateRecord::class, range)).records
        val weightRange = TimeRangeFilter.between(now.minus(Duration.ofDays(90)), now)
        val weights = client.readRecords(ReadRecordsRequest(WeightRecord::class, weightRange)).records
        val minutes = exercises.sumOf { Duration.between(it.startTime, it.endTime).toMinutes().toInt().coerceAtLeast(0) }
        val bpm = heart.flatMap { it.samples }.map { it.beatsPerMinute }.minOrNull()?.toInt()
        val latestWeight = weights.maxByOrNull { it.time }?.weight?.inKilograms
        return HealthDashboardSnapshot(
            stepsToday = aggregate[StepsRecord.COUNT_TOTAL] ?: 0L,
            restingHeartRateBpm = bpm,
            activeCaloriesToday = (aggregate[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories ?: 0.0).toInt(),
            exerciseMinutesToday = minutes,
            weightKg = latestWeight,
            lastSyncMillis = System.currentTimeMillis(),
            healthConnectAvailable = true
        )
    }
}
