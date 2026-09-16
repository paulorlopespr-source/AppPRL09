package com.example.domain.gamification

import com.example.data.model.CardioSession
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
    val weeklyQuests: List<JourneyQuest>
)

object Phase7JourneyEngine {
    fun build(
        workouts: List<WorkoutSession>,
        cardio: List<CardioSession>,
        medals: List<UserMedal>,
        today: LocalDate = LocalDate.now()
    ): JourneySnapshot {
        val completedWorkouts = workouts.filter { it.status == SessionStatus.COMPLETED }
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

        val quests = listOf(
            JourneyQuest("weekly_consistency", "3 atividades na semana", "Construa consistência sem depender de motivação.", weeklyActivities, 3, 150),
            JourneyQuest("weekly_cardio", "60 min de cardio", "Some qualquer modalidade cardiovascular registrada.", weeklyCardioMinutes, 60, 120),
            JourneyQuest("weekly_volume", "5 toneladas de volume", "Volume total dos treinos de força concluídos.", weeklyVolumeTons, 5, 180)
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
            weeklyQuests = quests
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
