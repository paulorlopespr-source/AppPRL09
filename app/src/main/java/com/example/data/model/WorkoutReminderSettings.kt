package com.example.data.model

import com.squareup.moshi.JsonClass

enum class ReminderTone(val label: String, val emoji: String, val sampleText: String) {
    MOTIVACIONAL(
        label = "Foco & Motivação",
        emoji = "🔥",
        sampleText = "Hora do treino! Cada gota de suor aproxima você do seu melhor físico."
    ),
    DISCIPLINA(
        label = "Disciplina & Consistência",
        emoji = "🛡️",
        sampleText = "Seu treino programado está esperando. A consistência constrói campeões!"
    ),
    HIPERTROFIA(
        label = "Hipertrofia & Força",
        emoji = "💪",
        sampleText = "Hora de buscar novos PRs e estimular o crescimento muscular hoje!"
    ),
    SAUDE(
        label = "Saúde & Longevidade",
        emoji = "🌱",
        sampleText = "Cuide do seu corpo e mente. Mantenha seu ritmo de atividades ativas."
    ),
    DIRETO(
        label = "Curto & Objetivo",
        emoji = "⚡",
        sampleText = "Hora do seu treino agendado no FitPr09."
    )
}

@JsonClass(generateAdapter = true)
data class WorkoutReminderSettings(
    val isEnabled: Boolean = true,
    val hour: Int = 18,
    val minute: Int = 0,
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5), // 1 = Monday ... 7 = Sunday
    val tone: ReminderTone = ReminderTone.MOTIVACIONAL,
    val customMessage: String = "",
    val notifyOnRestDays: Boolean = false,
    val vibrate: Boolean = true
) {
    val formattedTime: String
        get() = String.format(java.util.Locale.getDefault(), "%02d:%02d", hour, minute)

    fun isDayActive(dayOfWeek: Int): Boolean = daysOfWeek.contains(dayOfWeek)
}
