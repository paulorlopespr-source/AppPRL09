package com.example.domain.cardio

import com.example.data.model.ActivitySplit
import com.example.data.model.CardioSession
import com.example.data.model.CardioType
import com.example.data.model.IntensityLevel
import com.example.data.model.RoutePoint
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/** Centraliza a transformação da rota GPS em uma CardioSession persistível no Room v9. */
object OutdoorCardioPersistence {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val routeType = Types.newParameterizedType(List::class.java, RoutePoint::class.java)
    private val splitType = Types.newParameterizedType(List::class.java, ActivitySplit::class.java)
    private val routeAdapter = moshi.adapter<List<RoutePoint>>(routeType)
    private val splitAdapter = moshi.adapter<List<ActivitySplit>>(splitType)

    fun encodeRoute(points: List<RoutePoint>): String = routeAdapter.toJson(points)
    fun decodeRoute(json: String): List<RoutePoint> = try { routeAdapter.fromJson(json).orEmpty() } catch (_: Exception) { emptyList() }
    fun encodeSplits(splits: List<ActivitySplit>): String = splitAdapter.toJson(splits)
    fun decodeSplits(json: String): List<ActivitySplit> = try { splitAdapter.fromJson(json).orEmpty() } catch (_: Exception) { emptyList() }

    fun buildSession(
        type: CardioType,
        dateEpochDay: Long,
        timestampMillis: Long = System.currentTimeMillis(),
        intensity: IntensityLevel,
        location: String,
        caloriesBurned: Int,
        avgHeartRateBpm: Int?,
        notes: String,
        points: List<RoutePoint>
    ): CardioSession {
        val summary = OutdoorActivityEngine.summarize(points)
        return CardioSession(
            type = type,
            dateEpochDay = dateEpochDay,
            timestampMillis = timestampMillis,
            durationMinutes = (summary.elapsedSeconds / 60).coerceAtLeast(1),
            distanceKm = summary.distanceKm.takeIf { it > 0.0 },
            avgHeartRateBpm = avgHeartRateBpm,
            intensity = intensity,
            location = location,
            caloriesBurned = caloriesBurned,
            notes = notes,
            movingTimeSeconds = summary.movingSeconds,
            routeJson = encodeRoute(points),
            splitsJson = encodeSplits(summary.splits),
            elevationGainMeters = summary.elevationGainMeters,
            minElevationMeters = summary.minElevationMeters,
            maxElevationMeters = summary.maxElevationMeters,
            avgSpeedKmh = summary.averageSpeedKmh,
            avgPaceSecondsPerKm = summary.averagePaceSecondsPerKm
        )
    }
}
