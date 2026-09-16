package com.example.domain.cardio

import com.example.data.model.CardioType
import com.example.data.model.IntensityLevel
import com.example.data.model.RoutePoint
import org.junit.Assert.*
import org.junit.Test

class OutdoorCardioPersistenceTest {
    @Test fun routeRoundTripPreservesGpsFields() {
        val route = listOf(RoutePoint(-10.0, -40.0, 1_000L, 4f, 2.5f, 410.0), RoutePoint(-10.0, -39.99, 301_000L, 5f, 3f, 420.0))
        val json = OutdoorCardioPersistence.encodeRoute(route)
        val restored = OutdoorCardioPersistence.decodeRoute(json)
        assertEquals(route, restored)
    }

    @Test fun builderPersistsCalculatedOutdoorMetrics() {
        val route = listOf(RoutePoint(-10.0, -40.0, 1_000L, speedMetersPerSecond = 2f, altitudeMeters = 400.0), RoutePoint(-10.0, -39.99, 301_000L, speedMetersPerSecond = 3f, altitudeMeters = 412.0))
        val session = OutdoorCardioPersistence.buildSession(CardioType.TRILHA, 1L, intensity = IntensityLevel.MODERADA, location = "Trilha", caloriesBurned = 300, avgHeartRateBpm = 140, notes = "", points = route)
        assertTrue(session.routeJson.length > 2)
        assertTrue(session.distanceKm ?: 0.0 > 0.5)
        assertTrue(session.movingTimeSeconds > 0)
        assertTrue(session.elevationGainMeters >= 12.0)
    }
}
