package com.example.domain.cardio

import com.example.data.model.ActivitySplit
import com.example.data.model.RoutePoint
import kotlin.math.*

data class OutdoorActivitySummary(
    val distanceKm: Double,
    val elapsedSeconds: Int,
    val movingSeconds: Int,
    val averagePaceSecondsPerKm: Int?,
    val averageSpeedKmh: Double,
    val elevationGainMeters: Double,
    val minElevationMeters: Double?,
    val maxElevationMeters: Double?,
    val splits: List<ActivitySplit>
)

object OutdoorActivityEngine {
    private const val EARTH_RADIUS_M = 6_371_000.0
    private const val MOVING_SPEED_THRESHOLD_MPS = 0.5
    private const val ELEVATION_NOISE_METERS = 2.0

    fun summarize(points: List<RoutePoint>): OutdoorActivitySummary {
        val route = points.sortedBy { it.timestampMillis }
        if (route.size < 2) return OutdoorActivitySummary(0.0, 0, 0, null, 0.0, 0.0, route.mapNotNull { it.altitudeMeters }.minOrNull(), route.mapNotNull { it.altitudeMeters }.maxOrNull(), emptyList())

        var distanceM = 0.0
        var movingMillis = 0L
        var elevationGain = 0.0
        val cumulative = mutableListOf<Pair<RoutePoint, Double>>()
        cumulative += route.first() to 0.0

        route.zipWithNext().forEach { (a, b) ->
            val segment = haversineMeters(a, b)
            val dt = (b.timestampMillis - a.timestampMillis).coerceAtLeast(0)
            val speed = b.speedMetersPerSecond?.toDouble() ?: if (dt > 0) segment / (dt / 1000.0) else 0.0
            if (segment.isFinite() && segment < 1000.0) {
                distanceM += segment
                if (speed >= MOVING_SPEED_THRESHOLD_MPS) movingMillis += dt
            }
            val aAlt = a.altitudeMeters
            val bAlt = b.altitudeMeters
            if (aAlt != null && bAlt != null && bAlt - aAlt > ELEVATION_NOISE_METERS) elevationGain += bAlt - aAlt
            cumulative += b to distanceM
        }

        val elapsed = ((route.last().timestampMillis - route.first().timestampMillis) / 1000L).toInt().coerceAtLeast(0)
        val moving = (movingMillis / 1000L).toInt()
        val km = distanceM / 1000.0
        val speedKmh = if (moving > 0) km / (moving / 3600.0) else 0.0
        val pace = if (km > 0.0 && moving > 0) (moving / km).roundToInt() else null
        val altitudes = route.mapNotNull { it.altitudeMeters }
        return OutdoorActivitySummary(km, elapsed, moving, pace, speedKmh, elevationGain, altitudes.minOrNull(), altitudes.maxOrNull(), buildSplits(cumulative))
    }

    private fun buildSplits(cumulative: List<Pair<RoutePoint, Double>>): List<ActivitySplit> {
        if (cumulative.size < 2) return emptyList()
        val totalKm = floor(cumulative.last().second / 1000.0).toInt()
        if (totalKm <= 0) return emptyList()
        val startTime = cumulative.first().first.timestampMillis
        var previousSplitTime = startTime
        return (1..totalKm).mapNotNull { km ->
            val target = km * 1000.0
            val hit = cumulative.firstOrNull { it.second >= target }?.first ?: return@mapNotNull null
            val splitSeconds = ((hit.timestampMillis - previousSplitTime) / 1000L).toInt().coerceAtLeast(0)
            previousSplitTime = hit.timestampMillis
            ActivitySplit(km, ((hit.timestampMillis - startTime) / 1000L).toInt(), splitSeconds, splitSeconds)
        }
    }

    private fun haversineMeters(a: RoutePoint, b: RoutePoint): Double {
        val lat1 = Math.toRadians(a.latitude); val lat2 = Math.toRadians(b.latitude)
        val dLat = lat2 - lat1; val dLon = Math.toRadians(b.longitude - a.longitude)
        val h = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        return 2 * EARTH_RADIUS_M * asin(sqrt(h.coerceIn(0.0, 1.0)))
    }
}
