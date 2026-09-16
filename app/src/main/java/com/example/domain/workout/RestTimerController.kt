package com.example.domain.workout

/** Lógica pura do timer para ser usada pelo ViewModel/UI sem acoplar cálculo à tela. */
data class RestTimerState(
    val totalSeconds: Int = 60,
    val remainingSeconds: Int = 0,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val sourceSetNumber: Int? = null
) {
    val isFinished: Boolean get() = remainingSeconds == 0 && !isRunning && sourceSetNumber != null
}

object RestTimerController {
    fun start(seconds: Int, setNumber: Int): RestTimerState {
        val safe = seconds.coerceAtLeast(0)
        return RestTimerState(safe, safe, safe > 0, false, setNumber)
    }

    fun tick(state: RestTimerState): RestTimerState {
        if (!state.isRunning || state.isPaused || state.remainingSeconds <= 0) return state
        val next = state.remainingSeconds - 1
        return state.copy(remainingSeconds = next, isRunning = next > 0)
    }

    fun pause(state: RestTimerState) = state.copy(isPaused = true)
    fun resume(state: RestTimerState) = state.copy(isPaused = false, isRunning = state.remainingSeconds > 0)
    fun addSeconds(state: RestTimerState, seconds: Int) = state.copy(
        totalSeconds = (state.totalSeconds + seconds).coerceAtLeast(0),
        remainingSeconds = (state.remainingSeconds + seconds).coerceAtLeast(0),
        isRunning = state.remainingSeconds + seconds > 0
    )
    fun stop() = RestTimerState()
}
