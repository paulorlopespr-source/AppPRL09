package com.example.domain.gamification

import com.example.data.model.CardioSession
import com.example.data.model.AgendaCustomAppointment
import com.example.data.model.AgendaAppointmentStatus
import com.example.data.model.SessionStatus
import com.example.data.model.UserMedal
import com.example.data.model.WorkoutSession
import java.time.DayOfWeek
import java.time.LocalDate

data class JourneyQuest(
    val id: String,
    val title: String,
    val subtitle: String,
    val current: Int,
    val target: Int,
    val rewardXp: Int
) {
    val progress: Float get() = if (target <= 0) 1f else (current.toFloat() / target).coerceIn(0f, 1f)
    val completed: Boolean get() = current >= target
}

data class JourneySnapshot(
    val totalXp: Int,
    val level: Int,
    val levelTitle: String,
    val levelProgress: Float,
    val xpIntoLevel: Int,
    val xpForNextLevel: Int,
    val currentStreakDays: Int,
    val longestStreakDays: Int,
    val activeDays: Int,
    val strengthSessions: Int,
    val cardioSessions: Int,
    val totalMinutes: Int,
    val totalDistanceKm: Double,
    val totalVolumeKg: Double,
    val unlockedMedals: Int,
    val totalMedals: Int,
    val weeklyQuests: List<JourneyQuest>,
    val pintinhoProgress: PintinhoJourneyProgress
    ,val validWorkoutCount: Int = 0,
    val performanceProgressions: Int = 0,
    val adherencePercent: Int? = null,
    val evolutionReady: Boolean = false,
    val evolutionMessage: String = ""
    ,val cycleAdherencePercent: Int? = null
)

data class PintinhoJourneyProgress(
    val completedLevels: Int,
    val currentCard: PintinhoJourneyCard?,
    val objectiveCurrent: Int,
    val totalJourneyXp: Int
)

/** Resultado do vínculo entre uma sessão concluída e a Jornada. */
data class WorkoutJourneyImpact(
    val sessionId: Long,
    val completedLevels: List<Int>,
    val xpEarned: Int,
    val journeyLevel: Int,
    val objectiveProgress: Int,
    val objectiveTarget: Int
)

object Phase7JourneyEngine {
    data class ValidTraining(val dateEpochDay: Long, val title: String)

    fun evaluateWorkout(
        session: WorkoutSession,
        sessionsBefore: List<WorkoutSession>,
        cardio: List<CardioSession> = emptyList(),
        medals: List<UserMedal> = emptyList(),
        today: LocalDate = LocalDate.now()
    ): WorkoutJourneyImpact {
        val before = build(sessionsBefore, cardio, medals, today)
        val after = build(sessionsBefore + session, cardio, medals, today)
        val first = before.pintinhoProgress.completedLevels
        val last = after.pintinhoProgress.completedLevels
        return WorkoutJourneyImpact(
            sessionId = session.id,
            completedLevels = (first until last).map { it + 1 },
            xpEarned = after.pintinhoProgress.totalJourneyXp - before.pintinhoProgress.totalJourneyXp,
            journeyLevel = after.level,
            objectiveProgress = after.pintinhoProgress.objectiveCurrent,
            objectiveTarget = after.pintinhoProgress.currentCard?.target ?: 0
        )
    }

    /** Fonte única para todos os cálculos da Jornada. */
    fun validWorkouts(workouts: List<WorkoutSession>): List<WorkoutSession> = workouts
        .asSequence()
        .filter { it.status == SessionStatus.COMPLETED && it.dateEpochDay > 0 && (it.durationSeconds > 0 || it.exercisesDoneJson != "[]" || it.totalWeightLiftedKg > 0.0) }
        .distinctBy { "${it.dateEpochDay}|${it.title.trim().lowercase()}" }
        .toList()

    fun detectPerformanceProgress(workouts: List<WorkoutSession>): Int = workouts
        .filter { it.status == SessionStatus.COMPLETED }
        .groupBy { it.title.trim().lowercase() }
        .values.sumOf { sessions ->
            sessions.sortedBy { it.dateEpochDay }.zipWithNext().count { (a, b) ->
                b.totalWeightLiftedKg > a.totalWeightLiftedKg || b.durationSeconds > a.durationSeconds
            }
        }

    fun build(
        workouts: List<WorkoutSession>,
        cardio: List<CardioSession>,
        medals: List<UserMedal>,
        today: LocalDate = LocalDate.now(),
        weeklyGoalDays: Int? = null,
        agendaAppointments: List<AgendaCustomAppointment> = emptyList()
    ): JourneySnapshot {
        val completedWorkouts = validWorkouts(workouts)
        val activityDays = (completedWorkouts.map { it.dateEpochDay } + cardio.map { it.dateEpochDay }).distinct().sorted()
        val activeDates = activityDays.map(LocalDate::ofEpochDay).toSet()

        val currentStreak = calculateCurrentStreak(activeDates, today)
        val longestStreak = calculateLongestStreak(activityDays)
        val activeDays = activityDays.size
        val strengthSessions = completedWorkouts.size
        val cardioSessions = cardio.size
        val totalMinutes = completedWorkouts.sumOf { it.durationSeconds / 60 } + cardio.sumOf { it.durationMinutes }
        val totalDistance = cardio.sumOf { it.distanceKm ?: 0.0 }
        val totalVolume = completedWorkouts.sumOf { it.totalWeightLiftedKg }
        val unlocked = medals.filter { it.isUnlocked }

        val activityXp = (strengthSessions + cardioSessions) * 50
        val consistencyXp = activeDays * 20
        val medalXp = unlocked.sumOf { it.xpReward }
        val totalXp = activityXp + consistencyXp + medalXp
        val level = totalXp / 1000 + 1
        val xpIntoLevel = totalXp % 1000
        val levelTitle = levelTitle(level)

        val weekStart = today.with(DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(6)
        val weekWorkouts = completedWorkouts.filter { it.dateEpochDay in weekStart.toEpochDay()..weekEnd.toEpochDay() }
        val weekCardio = cardio.filter { it.dateEpochDay in weekStart.toEpochDay()..weekEnd.toEpochDay() }
        val weeklyActivities = weekWorkouts.size + weekCardio.size
        val weeklyCardioMinutes = weekCardio.sumOf { it.durationMinutes }
        val weeklyVolumeTons = (weekWorkouts.sumOf { it.totalWeightLiftedKg } / 1000.0).toInt()
        val plannedStrength = agendaAppointments.filter { appointment ->
            appointment.typeName.equals("STRENGTH", ignoreCase = true) &&
                appointment.status != AgendaAppointmentStatus.CANCELLED &&
                appointment.status != AgendaAppointmentStatus.RESCHEDULED &&
                appointment.epochDay in weekStart.toEpochDay()..weekEnd.toEpochDay()
        }
        val plannedCount = plannedStrength.size.takeIf { it > 0 } ?: weeklyGoalDays?.takeIf { it > 0 }
        val adherencePercent = plannedCount?.let {
            ((weekWorkouts.size.toFloat() / it).coerceIn(0f, 1f) * 100).toInt()
        }
        val cycleStart = today.minusWeeks(3).with(DayOfWeek.MONDAY)
        val cycleAppointments = agendaAppointments.count { appointment ->
            appointment.typeName.equals("STRENGTH", ignoreCase = true) &&
                appointment.status != AgendaAppointmentStatus.CANCELLED &&
                appointment.status != AgendaAppointmentStatus.RESCHEDULED &&
                appointment.epochDay in cycleStart.toEpochDay()..today.toEpochDay()
        }
        val cyclePlanned = cycleAppointments.takeIf { it > 0 } ?: weeklyGoalDays?.takeIf { it > 0 }?.times(4)
        val cycleCompleted = completedWorkouts.count { it.dateEpochDay in cycleStart.toEpochDay()..today.toEpochDay() }
        val cycleAdherencePercent = cyclePlanned?.let { ((cycleCompleted.toFloat() / it).coerceIn(0f, 1f) * 100).toInt() }

        val quests = listOf(
            JourneyQuest("weekly_consistency", "3 atividades na semana", "Construa consistência sem depender de motivação.", weeklyActivities, 3, 150),
            JourneyQuest("weekly_cardio", "60 min de cardio", "Some qualquer modalidade cardiovascular registrada.", weeklyCardioMinutes, 60, 120),
            JourneyQuest("weekly_volume", "5 toneladas de volume", "Volume total dos treinos de força concluídos.", weeklyVolumeTons, 5, 180)
        )

        val recordedSets = completedWorkouts.count { it.totalWeightLiftedKg > 0.0 }
        val performanceProgressions = detectPerformanceProgress(completedWorkouts)
        val hasProgression = performanceProgressions > 0 || completedWorkouts.any { it.totalWeightLiftedKg > 0.0 }
        fun objectiveValue(card: PintinhoJourneyCard) = when (card.objectiveType) {
            PintinhoObjectiveType.WORKOUTS_COMPLETED -> strengthSessions
            PintinhoObjectiveType.SETS_RECORDED -> recordedSets
            PintinhoObjectiveType.WEEKLY_WORKOUTS -> weeklyActivities
            PintinhoObjectiveType.PROGRESSION -> if (hasProgression) 1 else 0
            PintinhoObjectiveType.EDUCATION -> 0
        }
        val completedCards = PintinhoJourneyCatalog.allCards.takeWhile { objectiveValue(it) >= it.target }
        val currentCard = PintinhoJourneyCatalog.allCards.getOrNull(completedCards.size)
        val rank = PintinhoJourneyCatalog.rankFor(completedCards.size + 1)
        val requiredTrainings = when (rank) { "Pintinho" -> 10; "Frango" -> 18; "Lobo" -> 28; "Gorila" -> 38; "Leão" -> 48; else -> 65 }
        val adherenceRequired = when (rank) { "Pintinho", "Frango" -> 80; "Lobo", "Gorila" -> 85; else -> 90 }
        val evolutionReady = strengthSessions >= requiredTrainings && performanceProgressions >= when (rank) { "Pintinho" -> 1; "Frango" -> 2; "Lobo" -> 4; "Gorila" -> 6; else -> 8 } && (cycleAdherencePercent == null || cycleAdherencePercent >= adherenceRequired)
        val journeyProgress = PintinhoJourneyProgress(
            completedLevels = completedCards.size,
            currentCard = currentCard,
            objectiveCurrent = currentCard?.let(::objectiveValue) ?: 0,
            totalJourneyXp = completedCards.sumOf { it.xpReward }
        )

        return JourneySnapshot(
            totalXp = totalXp,
            level = level,
            levelTitle = levelTitle,
            levelProgress = xpIntoLevel / 1000f,
            xpIntoLevel = xpIntoLevel,
            xpForNextLevel = 1000,
            currentStreakDays = currentStreak,
            longestStreakDays = longestStreak,
            activeDays = activeDays,
            strengthSessions = strengthSessions,
            cardioSessions = cardioSessions,
            totalMinutes = totalMinutes,
            totalDistanceKm = totalDistance,
            totalVolumeKg = totalVolume,
            unlockedMedals = unlocked.size,
            totalMedals = medals.size,
            weeklyQuests = quests,
            pintinhoProgress = journeyProgress
            ,validWorkoutCount = completedWorkouts.size,
            performanceProgressions = performanceProgressions,
            evolutionReady = evolutionReady,
            evolutionMessage = if (evolutionReady) "Requisitos de evolução concluídos." else "XP suficiente não substitui os requisitos de evolução."
            ,adherencePercent = adherencePercent,
            cycleAdherencePercent = cycleAdherencePercent
        )
    }

    private fun calculateCurrentStreak(activeDates: Set<LocalDate>, today: LocalDate): Int {
        if (activeDates.isEmpty()) return 0
        var cursor = if (today in activeDates) today else today.minusDays(1)
        var streak = 0
        while (cursor in activeDates) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    private fun calculateLongestStreak(sortedEpochDays: List<Long>): Int {
        if (sortedEpochDays.isEmpty()) return 0
        var longest = 1
        var current = 1
        for (i in 1 until sortedEpochDays.size) {
            if (sortedEpochDays[i] == sortedEpochDays[i - 1] + 1) {
                current++
                longest = maxOf(longest, current)
            } else {
                current = 1
            }
        }
        return longest
    }

    private fun levelTitle(level: Int): String = when {
        level >= 10 -> "Elite PRL09"
        level >= 7 -> "Veterano"
        level >= 4 -> "Consistente"
        level >= 2 -> "Em evolução"
        else -> "Iniciante"
    }
}
