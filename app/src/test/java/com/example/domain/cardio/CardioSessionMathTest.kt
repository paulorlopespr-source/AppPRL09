package com.example.domain.cardio

import org.junit.Assert.assertEquals
import org.junit.Test

class CardioSessionMathTest {
    @Test fun monotonicClockIncludesTimeInBackgroundAndExcludesPausedDuration() {
        assertEquals(30L, CardioSessionMath.elapsedSeconds(10, 20_000, 40_000, true, true))
        assertEquals(10L, CardioSessionMath.elapsedSeconds(10, 20_000, 40_000, false, true))
        assertEquals(10L, CardioSessionMath.elapsedSeconds(10, 20_000, 40_000, true, false))
        assertEquals(10L, CardioSessionMath.elapsedSeconds(10, 50_000, 40_000, true, true))
    }

    @Test fun deviceLifetimeStepCounterBecomesSessionRelativeAndDoesNotDoubleCount() {
        val first = CardioSessionMath.stepCounterUpdate(sessionBase = 0, baseline = null, sensorTotal = 4_000f)
        assertEquals(0L, first.first)
        assertEquals(4_000f, first.second)

        val next = CardioSessionMath.stepCounterUpdate(sessionBase = 0, baseline = first.second, sensorTotal = 4_025f)
        assertEquals(25L, next.first)

        val resumed = CardioSessionMath.stepCounterUpdate(sessionBase = 25, baseline = null, sensorTotal = 5_000f)
        assertEquals(25L, resumed.first)
        val afterResume = CardioSessionMath.stepCounterUpdate(sessionBase = resumed.first, baseline = resumed.second, sensorTotal = 5_003f)
        assertEquals(28L, afterResume.first)
    }
}
