package com.example.data.local

import com.example.data.model.CardioSession
import com.example.data.model.CardioType
import com.example.data.model.GpsPoint
import com.example.data.model.IntensityLevel
import com.example.data.model.KmSplit
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object OutdoorCardioPersistence {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val gpsListType = Types.newParameterizedType(List::class.java, GpsPoint::class.java)
    private val splitListType = Types.newParameterizedType(List::class.java, KmSplit::class.java)

    private val gpsAdapter = moshi.adapter<List<GpsPoint>>(gpsListType)
    private val splitAdapter = moshi.adapter<List<KmSplit>>(splitListType)

    fun serializeRoutePoints(points: List<GpsPoint>): String {
        return try {
            gpsAdapter.toJson(points)
        } catch (_: Exception) {
            "[]"
        }
    }

    fun parseRoutePoints(json: String?): List<GpsPoint> {
        if (json.isNullOrBlank() || json == "[]") return emptyList()
        return try {
            gpsAdapter.fromJson(json) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun serializeSplits(splits: List<KmSplit>): String {
        return try {
            splitAdapter.toJson(splits)
        } catch (_: Exception) {
            "[]"
        }
    }

    fun parseSplits(json: String?): List<KmSplit> {
        if (json.isNullOrBlank() || json == "[]") return emptyList()
        return try {
            splitAdapter.fromJson(json) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Builds a CardioSession entity with all aggregated metrics, splits, and route points.
     */
    fun buildSession(
        type: CardioType,
        dateEpochDay: Long,
        timestampMillis: Long = System.currentTimeMillis(),
        durationMinutes: Int,
        distanceKm: Double?,
        avgHeartRateBpm: Int? = null,
        intensity: IntensityLevel = IntensityLevel.MODERADA,
        location: String = "Corrida ao Ar Livre",
        caloriesBurned: Int = 0,
        notes: String = "",
        aiEvaluation: String = "",
        routePoints: List<GpsPoint> = emptyList(),
        splits: List<KmSplit> = emptyList(),
        elevationGainMeters: Double? = 0.0,
        avgPaceMinKm: String = "--:--"
    ): CardioSession {
        return CardioSession(
            type = type,
            dateEpochDay = dateEpochDay,
            timestampMillis = timestampMillis,
            durationMinutes = durationMinutes,
            distanceKm = distanceKm,
            avgHeartRateBpm = avgHeartRateBpm,
            intensity = intensity,
            location = location,
            caloriesBurned = caloriesBurned,
            notes = notes,
            aiEvaluation = aiEvaluation,
            routePointsJson = serializeRoutePoints(routePoints),
            splitsJson = serializeSplits(splits),
            elevationGainMeters = elevationGainMeters,
            avgPaceMinKm = avgPaceMinKm
        )
    }
}
