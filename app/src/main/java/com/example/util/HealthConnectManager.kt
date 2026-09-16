package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import com.example.data.model.CardioSession
import com.example.data.model.CardioType
import com.example.data.model.WorkoutSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

enum class HealthConnectAvailability {
    AVAILABLE,
    NOT_INSTALLED,
    NOT_SUPPORTED
}

data class HealthConnectDailyMetrics(
    val steps: Long = 0,
    val totalCaloriesBurned: Double = 0.0,
    val activeCaloriesBurned: Double = 0.0,
    val averageHeartRateBpm: Int? = null,
    val latestWeightKg: Double? = null,
    val isConnected: Boolean = false,
    val lastSyncTimeMillis: Long = 0L
)

data class HeartRateSample(
    val timeMillis: Long,
    val bpm: Long
)

data class HealthSyncResult(
    val success: Boolean,
    val syncedWorkoutsCount: Int = 0,
    val syncedCardiosCount: Int = 0,
    val message: String = ""
)

class HealthConnectManager(private val context: Context) {

    companion object {
        private const val TAG = "HealthConnectManager"
        const val HEALTH_CONNECT_PACKAGE = "com.google.android.apps.healthdata"

        val REQUIRED_PERMISSIONS = setOf(
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            HealthPermission.getWritePermission(ExerciseSessionRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
            HealthPermission.getWritePermission(ActiveCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
            HealthPermission.getWritePermission(DistanceRecord::class),
            HealthPermission.getReadPermission(WeightRecord::class),
            HealthPermission.getWritePermission(WeightRecord::class)
        )
    }

    private val healthConnectClient: HealthConnectClient? by lazy {
        if (checkAvailability() == HealthConnectAvailability.AVAILABLE) {
            HealthConnectClient.getOrCreate(context)
        } else {
            null
        }
    }

    fun checkAvailability(): HealthConnectAvailability {
        return try {
            val status = HealthConnectClient.getSdkStatus(context)
            when (status) {
                HealthConnectClient.SDK_AVAILABLE -> HealthConnectAvailability.AVAILABLE
                HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthConnectAvailability.NOT_INSTALLED
                else -> HealthConnectAvailability.NOT_SUPPORTED
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Health Connect availability", e)
            HealthConnectAvailability.NOT_SUPPORTED
        }
    }

    fun createPermissionResultContract(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }

    suspend fun hasAllPermissions(): Boolean = withContext(Dispatchers.IO) {
        val client = healthConnectClient ?: return@withContext false
        try {
            val granted = client.permissionController.getGrantedPermissions()
            REQUIRED_PERMISSIONS.all { it in granted }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking granted permissions", e)
            false
        }
    }

    suspend fun getGrantedPermissions(): Set<String> = withContext(Dispatchers.IO) {
        val client = healthConnectClient ?: return@withContext emptySet()
        try {
            client.permissionController.getGrantedPermissions()
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving granted permissions", e)
            emptySet()
        }
    }

    fun getOpenHealthConnectIntent(): Intent {
        return Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS)
    }

    fun getInstallHealthConnectIntent(): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=$HEALTH_CONNECT_PACKAGE")
            setPackage("com.android.vending")
        }
    }

    /**
     * Writes a strength workout session to Health Connect
     */
    suspend fun writeStrengthWorkoutSession(session: WorkoutSession): Result<String> = withContext(Dispatchers.IO) {
        val client = healthConnectClient ?: return@withContext Result.failure(Exception("Health Connect não disponível"))
        try {
            val startInstant = Instant.ofEpochMilli(session.startTimeMillis)
            val durationSecs = if (session.durationSeconds > 0) session.durationSeconds else 3600
            val endInstant = if (session.endTimeMillis > session.startTimeMillis) {
                Instant.ofEpochMilli(session.endTimeMillis)
            } else {
                startInstant.plusSeconds(durationSecs.toLong())
            }

            val zoneOffset = ZoneId.systemDefault().rules.getOffset(startInstant)

            val exerciseRecord = ExerciseSessionRecord(
                startTime = startInstant,
                startZoneOffset = zoneOffset,
                endTime = endInstant,
                endZoneOffset = zoneOffset,
                exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING,
                title = session.title,
                notes = buildString {
                    append("Carga total: ${session.totalWeightLiftedKg.toInt()} kg | ")
                    append("Local: ${session.location} | ")
                    if (session.notes.isNotBlank()) append("Notas: ${session.notes}")
                }
            )

            val recordsToInsert = mutableListOf<androidx.health.connect.client.records.Record>(exerciseRecord)

            // Add estimated calories if available
            if (session.estimatedCalories > 0) {
                val caloriesRecord = TotalCaloriesBurnedRecord(
                    startTime = startInstant,
                    startZoneOffset = zoneOffset,
                    endTime = endInstant,
                    endZoneOffset = zoneOffset,
                    energy = Energy.calories(session.estimatedCalories.toDouble())
                )
                val activeCaloriesRecord = ActiveCaloriesBurnedRecord(
                    startTime = startInstant,
                    startZoneOffset = zoneOffset,
                    endTime = endInstant,
                    endZoneOffset = zoneOffset,
                    energy = Energy.calories(session.estimatedCalories.toDouble())
                )
                recordsToInsert.add(caloriesRecord)
                recordsToInsert.add(activeCaloriesRecord)
            }

            val response = client.insertRecords(recordsToInsert)
            Result.success(response.recordIdsList.firstOrNull() ?: "synced")
        } catch (e: Exception) {
            Log.e(TAG, "Error writing strength workout session", e)
            Result.failure(e)
        }
    }

    /**
     * Writes a cardio session (running, indoor cycling, walking, football) to Health Connect
     */
    suspend fun writeCardioSession(session: CardioSession): Result<String> = withContext(Dispatchers.IO) {
        val client = healthConnectClient ?: return@withContext Result.failure(Exception("Health Connect não disponível"))
        try {
            val startInstant = Instant.ofEpochMilli(session.timestampMillis)
            val durationSecs = session.durationMinutes * 60L
            val endInstant = startInstant.plusSeconds(durationSecs)
            val zoneOffset = ZoneId.systemDefault().rules.getOffset(startInstant)

            val exerciseType = when (session.type) {
                CardioType.CORRIDA -> ExerciseSessionRecord.EXERCISE_TYPE_RUNNING
                CardioType.TRILHA -> ExerciseSessionRecord.EXERCISE_TYPE_HIKING
                CardioType.BICICLETA_INDOOR -> ExerciseSessionRecord.EXERCISE_TYPE_BIKING_STATIONARY
                CardioType.CAMINHADA_ESTEIRA, CardioType.CAMINHADA_AR_LIVRE -> ExerciseSessionRecord.EXERCISE_TYPE_WALKING
                CardioType.FUTEBOL -> ExerciseSessionRecord.EXERCISE_TYPE_SOCCER
            }

            val exerciseRecord = ExerciseSessionRecord(
                startTime = startInstant,
                startZoneOffset = zoneOffset,
                endTime = endInstant,
                endZoneOffset = zoneOffset,
                exerciseType = exerciseType,
                title = session.type.title,
                notes = "Intensidade: ${session.intensity.label}. ${session.notes}"
            )

            val recordsToInsert = mutableListOf<androidx.health.connect.client.records.Record>(exerciseRecord)

            if (session.caloriesBurned > 0) {
                recordsToInsert.add(
                    TotalCaloriesBurnedRecord(
                        startTime = startInstant,
                        startZoneOffset = zoneOffset,
                        endTime = endInstant,
                        endZoneOffset = zoneOffset,
                        energy = Energy.calories(session.caloriesBurned.toDouble())
                    )
                )
                recordsToInsert.add(
                    ActiveCaloriesBurnedRecord(
                        startTime = startInstant,
                        startZoneOffset = zoneOffset,
                        endTime = endInstant,
                        endZoneOffset = zoneOffset,
                        energy = Energy.calories(session.caloriesBurned.toDouble())
                    )
                )
            }

            if (session.distanceKm != null && session.distanceKm > 0) {
                recordsToInsert.add(
                    DistanceRecord(
                        startTime = startInstant,
                        startZoneOffset = zoneOffset,
                        endTime = endInstant,
                        endZoneOffset = zoneOffset,
                        distance = Length.meters(session.distanceKm * 1000.0)
                    )
                )
            }

            val response = client.insertRecords(recordsToInsert)
            Result.success(response.recordIdsList.firstOrNull() ?: "synced")
        } catch (e: Exception) {
            Log.e(TAG, "Error writing cardio session", e)
            Result.failure(e)
        }
    }

    /**
     * Writes weight measurement to Health Connect (e.g. from app body metrics)
     */
    suspend fun writeWeight(weightKg: Double, timestampMillis: Long = System.currentTimeMillis()): Result<Boolean> = withContext(Dispatchers.IO) {
        val client = healthConnectClient ?: return@withContext Result.failure(Exception("Health Connect não disponível"))
        try {
            val instant = Instant.ofEpochMilli(timestampMillis)
            val zoneOffset = ZoneId.systemDefault().rules.getOffset(instant)
            val weightRecord = WeightRecord(
                time = instant,
                zoneOffset = zoneOffset,
                weight = Mass.kilograms(weightKg)
            )
            client.insertRecords(listOf(weightRecord))
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error writing weight", e)
            Result.failure(e)
        }
    }

    /**
     * Reads today's aggregated metrics from Health Connect (Steps, Calories, Heart Rate, Weight)
     */
    suspend fun readTodayHealthMetrics(): HealthConnectDailyMetrics = withContext(Dispatchers.IO) {
        val client = healthConnectClient ?: return@withContext HealthConnectDailyMetrics()
        try {
            val zoneId = ZoneId.systemDefault()
            val startOfDay = LocalDate.now().atStartOfDay(zoneId).toInstant()
            val now = Instant.now()

            val timeRange = TimeRangeFilter.between(startOfDay, now)

            // Aggregate steps
            val stepsResponse = try {
                client.aggregate(
                    AggregateRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = timeRange
                    )
                )
            } catch (e: Exception) { null }
            val steps = stepsResponse?.get(StepsRecord.COUNT_TOTAL) ?: 0L

            // Aggregate Total Calories
            val caloriesResponse = try {
                client.aggregate(
                    AggregateRequest(
                        metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL, ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL),
                        timeRangeFilter = timeRange
                    )
                )
            } catch (e: Exception) { null }

            val totalCalories = caloriesResponse?.get(TotalCaloriesBurnedRecord.ENERGY_TOTAL)?.inKilocalories ?: 0.0
            val activeCalories = caloriesResponse?.get(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL)?.inKilocalories ?: 0.0

            // Read latest weight in the last 30 days
            val weightResponse = try {
                client.readRecords(
                    ReadRecordsRequest(
                        recordType = WeightRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(now.minus(30, ChronoUnit.DAYS), now),
                        ascendingOrder = false,
                        pageSize = 1
                    )
                )
            } catch (e: Exception) { null }
            val latestWeight = weightResponse?.records?.firstOrNull()?.weight?.inKilograms

            // Read latest heart rate samples today
            val hrResponse = try {
                client.readRecords(
                    ReadRecordsRequest(
                        recordType = HeartRateRecord::class,
                        timeRangeFilter = timeRange,
                        ascendingOrder = false,
                        pageSize = 5
                    )
                )
            } catch (e: Exception) { null }

            val avgHr = hrResponse?.records?.flatMap { it.samples }?.map { it.beatsPerMinute }?.average()?.toInt()

            HealthConnectDailyMetrics(
                steps = steps,
                totalCaloriesBurned = totalCalories,
                activeCaloriesBurned = activeCalories,
                averageHeartRateBpm = if (avgHr != null && avgHr > 0) avgHr else null,
                latestWeightKg = latestWeight,
                isConnected = true,
                lastSyncTimeMillis = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error reading today's health metrics", e)
            HealthConnectDailyMetrics(isConnected = false)
        }
    }

    /**
     * Reads heart rate samples during a specific workout interval (from smartwatch sensors)
     */
    suspend fun readHeartRateForInterval(startMillis: Long, endMillis: Long): List<HeartRateSample> = withContext(Dispatchers.IO) {
        val client = healthConnectClient ?: return@withContext emptyList()
        try {
            val startInstant = Instant.ofEpochMilli(startMillis)
            val endInstant = Instant.ofEpochMilli(endMillis)

            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startInstant, endInstant),
                    ascendingOrder = true
                )
            )

            response.records.flatMap { record ->
                record.samples.map { sample ->
                    HeartRateSample(
                        timeMillis = sample.time.toEpochMilli(),
                        bpm = sample.beatsPerMinute
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading heart rate for interval", e)
            emptyList()
        }
    }

    /**
     * Batch synchronization of all completed workouts and cardio sessions
     */
    suspend fun syncAllHistory(
        workouts: List<WorkoutSession>,
        cardios: List<CardioSession>
    ): HealthSyncResult = withContext(Dispatchers.IO) {
        var syncedWorkouts = 0
        var syncedCardios = 0

        for (workout in workouts.filter { it.status == com.example.data.model.SessionStatus.COMPLETED }) {
            val res = writeStrengthWorkoutSession(workout)
            if (res.isSuccess) syncedWorkouts++
        }

        for (cardio in cardios) {
            val res = writeCardioSession(cardio)
            if (res.isSuccess) syncedCardios++
        }

        HealthSyncResult(
            success = true,
            syncedWorkoutsCount = syncedWorkouts,
            syncedCardiosCount = syncedCardios,
            message = "Sincronização concluída: $syncedWorkouts treinos de força e $syncedCardios cardios sincronizados com o Health Connect!"
        )
    }
}
