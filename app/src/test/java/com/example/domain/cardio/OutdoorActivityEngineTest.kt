package com.example.domain.cardio

import com.example.data.model.CardioType
import com.example.data.model.RoutePoint
import org.junit.Assert.*
import org.junit.Test

class OutdoorActivityEngineTest {
    @Test fun calculatesDistancePaceAndElevation() {
        val points = listOf(
            RoutePoint(-10.0, -40.0, 0, speedMetersPerSecond = 2f, altitudeMeters = 100.0),
            RoutePoint(-10.0, -39.99, 600_000, speedMetersPerSecond = 2f, altitudeMeters = 110.0)
        )
        val result = OutdoorActivityEngine.summarize(points)
        assertTrue(result.distanceKm > 1.0)
        assertTrue(result.movingSeconds > 0)
        assertEquals(10.0, result.elevationGainMeters, 0.1)
        assertTrue(result.splits.isNotEmpty())
    }

    @Test fun recorderIgnoresPointsWhilePaused() {
        var state = OutdoorActivityRecorder.start(CardioType.TRILHA, 0)
        state = OutdoorActivityRecorder.pause(state)
        state = OutdoorActivityRecorder.addPoint(state, RoutePoint(-10.0, -40.0, 1000))
        assertTrue(state.route.isEmpty())
        state = OutdoorActivityRecorder.resume(state)
        state = OutdoorActivityRecorder.addPoint(state, RoutePoint(-10.0, -40.0, 2000))
        assertEquals(1, state.route.size)
    }

    @Test fun poorAccuracyPointIsRejected() {
        var state = OutdoorActivityRecorder.start(CardioType.CORRIDA, 0)
        state = OutdoorActivityRecorder.addPoint(state, RoutePoint(-10.0, -40.0, 1000, accuracyMeters = 80f))
        assertTrue(state.route.isEmpty())
    }
}
