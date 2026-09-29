package com.example.domain.training

enum class PlannedStatus { PLANNED, COMPLETED, CANCELLED, RESCHEDULED, REST }

data class AdherenceSummary(val planned: Int, val completed: Int, val cancelled: Int, val rescheduled: Int, val restDays: Int) {
    val percentage: Int get() = if (planned <= 0) 0 else ((completed.toDouble() / planned) * 100).toInt().coerceIn(0, 100)
}

object TrainingAdherence {
    fun calculate(statuses: List<PlannedStatus>): AdherenceSummary {
        val planned = statuses.count { it != PlannedStatus.REST }
        return AdherenceSummary(planned, statuses.count { it == PlannedStatus.COMPLETED }, statuses.count { it == PlannedStatus.CANCELLED }, statuses.count { it == PlannedStatus.RESCHEDULED }, statuses.count { it == PlannedStatus.REST })
    }
}
