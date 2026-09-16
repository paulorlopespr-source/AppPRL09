package com.example.domain.cardio

import com.example.data.model.RoutePoint
import org.junit.Assert.*
import org.junit.Test

class OutdoorActivityEngineTest {
    @Test fun summarizesRouteDistanceAndTime() {
        val points = listOf(RoutePoint(-10.0,-40.0,0L,speedMetersPerSecond=2f,altitudeMeters=400.0), RoutePoint(-10.0,-39.991,300_000L,speedMetersPerSecond=3f,altitudeMeters=410.0))
        val result = OutdoorActivityEngine.summarize(points)
        assertTrue(result.distanceKm > 0.5)
        assertEquals(300, result.elapsedSeconds)
        assertTrue(result.elevationGainMeters >= 10.0)
    }
    @Test fun emptyRouteIsSafe() { val result=OutdoorActivityEngine.summarize(emptyList()); assertEquals(0.0,result.distanceKm,0.0); assertTrue(result.splits.isEmpty()) }
}
