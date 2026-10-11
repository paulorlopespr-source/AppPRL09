package com.example.domain.cardio

import kotlin.math.max

/** Pure calculations used by the foreground cardio service and covered by unit tests. */
object CardioSessionMath {
    fun elapsedSeconds(
        accumulatedSeconds: Long,
        resumedAtElapsedRealtime: Long,
        nowElapsedRealtime: Long,
        isRunning: Boolean,
        sameBoot: Boolean
    ): Long = if (isRunning && sameBoot && resumedAtElapsedRealtime > 0L) {
        accumulatedSeconds + max(0L, nowElapsedRealtime - resumedAtElapsedRealtime) / 1_000L
    } else accumulatedSeconds.coerceAtLeast(0L)

    /** Converts TYPE_STEP_COUNTER's device-lifetime total into a session-relative value. */
    fun stepCounterUpdate(sessionBase: Long, baseline: Float?, sensorTotal: Float): Pair<Long, Float> {
        if (baseline == null) return sessionBase.coerceAtLeast(0L) to sensorTotal
        val delta = max(0f, sensorTotal - baseline).toLong()
        return (sessionBase.coerceAtLeast(0L) + delta) to baseline
    }
}
