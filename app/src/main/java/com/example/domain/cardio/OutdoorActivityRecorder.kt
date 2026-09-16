package com.example.domain.cardio

import com.example.data.model.CardioType
import com.example.data.model.RoutePoint

enum class OutdoorActivityStatus { IDLE, RECORDING, PAUSED, FINISHED }

data class OutdoorActivityState(
    val type: CardioType? = null,
    val status: OutdoorActivityStatus = OutdoorActivityStatus.IDLE,
    val startedAtMillis: Long? = null,
    val finishedAtMillis: Long? = null,
    val route: List<RoutePoint> = emptyList()
)

object OutdoorActivityRecorder {
    fun start(type: CardioType, timestampMillis: Long): OutdoorActivityState =
        OutdoorActivityState(type, OutdoorActivityStatus.RECORDING, timestampMillis)

    fun addPoint(state: OutdoorActivityState, point: RoutePoint): OutdoorActivityState {
        if (state.status != OutdoorActivityStatus.RECORDING) return state
        if (point.accuracyMeters != null && point.accuracyMeters > 50f) return state
        return state.copy(route = state.route + point)
    }

    fun pause(state: OutdoorActivityState): OutdoorActivityState =
        if (state.status == OutdoorActivityStatus.RECORDING) state.copy(status = OutdoorActivityStatus.PAUSED) else state

    fun resume(state: OutdoorActivityState): OutdoorActivityState =
        if (state.status == OutdoorActivityStatus.PAUSED) state.copy(status = OutdoorActivityStatus.RECORDING) else state

    fun finish(state: OutdoorActivityState, timestampMillis: Long): OutdoorActivityState =
        if (state.status == OutdoorActivityStatus.RECORDING || state.status == OutdoorActivityStatus.PAUSED)
            state.copy(status = OutdoorActivityStatus.FINISHED, finishedAtMillis = timestampMillis)
        else state
}
