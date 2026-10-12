package com.example.domain.cardio

/** Duration is derived from active monotonic time, never from UI ticks. */
data class CardioIntervalPlan(
    val name: String = "Treino intervalado FITPR09",
    val effortLabel: String = "CORRER",
    val effortSeconds: Int = 120,
    val restSeconds: Int = 120,
    val cycles: Int = 6,
    val warmupSeconds: Int = 0,
    val cooldownSeconds: Int = 0,
    val outdoor: Boolean = false
) {
    init {
        require(name.isNotBlank() && effortLabel.isNotBlank())
        require(effortSeconds in 1..3600 && restSeconds in 0..3600)
        require(cycles in 1..100 && warmupSeconds in 0..3600 && cooldownSeconds in 0..3600)
    }

    val totalSeconds: Long get() = warmupSeconds.toLong() + cycles * effortSeconds.toLong() + (cycles - 1) * restSeconds.toLong() + cooldownSeconds

    fun phases(): List<CardioIntervalPhase> = buildList {
        if (warmupSeconds > 0) add(CardioIntervalPhase("AQUECER", warmupSeconds, 0))
        for (cycle in 1..cycles) {
            add(CardioIntervalPhase(effortLabel, effortSeconds, cycle))
            if (cycle < cycles && restSeconds > 0) add(CardioIntervalPhase("DESCANSAR", restSeconds, cycle))
        }
        if (cooldownSeconds > 0) add(CardioIntervalPhase("DESAQUECER", cooldownSeconds, cycles))
    }

    fun progress(activeSeconds: Long): CardioIntervalProgress {
        var elapsed = activeSeconds.coerceAtLeast(0L)
        val phases = phases()
        phases.forEachIndexed { index, phase ->
            if (elapsed < phase.seconds) return CardioIntervalProgress(phase.label, phase.seconds - elapsed, phase.cycle, index, phases.getOrNull(index + 1)?.label, false)
            elapsed -= phase.seconds
        }
        return CardioIntervalProgress("CONCLUÍDO", 0, cycles, phases.size, null, true)
    }
}

data class CardioIntervalPhase(val label: String, val seconds: Int, val cycle: Int)
data class CardioIntervalProgress(val label: String, val remainingSeconds: Long, val cycle: Int, val phaseIndex: Int, val nextLabel: String?, val completed: Boolean)
