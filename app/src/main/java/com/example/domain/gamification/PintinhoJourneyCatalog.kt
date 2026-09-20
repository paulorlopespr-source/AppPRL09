package com.example.domain.gamification

/**
 * Catálogo da primeira jornada. Os [assetKey] são contratos estáveis para os
 * vinte cards ilustrados que serão incluídos posteriormente em drawable-nodpi.
 */
enum class PintinhoObjectiveType { WORKOUTS_COMPLETED, SETS_RECORDED, WEEKLY_WORKOUTS, PROGRESSION, EDUCATION }

enum class PintinhoChest(val label: String) { COMUM("Baú comum"), RARO("Baú raro"), EVOLUCAO("Baú de evolução") }

data class PintinhoJourneyCard(
    val level: Int,
    val title: String,
    val description: String,
    val objectiveType: PintinhoObjectiveType,
    val target: Int,
    val xpReward: Int,
    val assetKey: String,
    val chest: PintinhoChest? = null
)

object PintinhoJourneyCatalog {
    const val journeyTitle = "Jornada I • O Despertar"
    const val assetDirectory = "drawable-nodpi"

    val cards: List<PintinhoJourneyCard> = listOf(
        card(1, "O Primeiro Sinal", "Conclua seu primeiro treino.", PintinhoObjectiveType.WORKOUTS_COMPLETED, 1, 100),
        card(2, "Nasceu um Frango", "Conclua 2 treinos.", PintinhoObjectiveType.WORKOUTS_COMPLETED, 2, 100),
        card(3, "Descobrindo os Halteres", "Registre todas as séries de um treino.", PintinhoObjectiveType.SETS_RECORDED, 3, 120),
        card(4, "Primeiros Passos", "Conclua 3 treinos.", PintinhoObjectiveType.WORKOUTS_COMPLETED, 3, 120),
        card(5, "Pequeno, mas Persistente", "Complete sua primeira semana planejada.", PintinhoObjectiveType.WEEKLY_WORKOUTS, 3, 150, PintinhoChest.COMUM),
        card(6, "Aprendendo a Treinar", "Conclua 5 treinos.", PintinhoObjectiveType.WORKOUTS_COMPLETED, 5, 150),
        card(7, "Hora do Treino", "Conclua 3 treinos nesta semana.", PintinhoObjectiveType.WEEKLY_WORKOUTS, 3, 150),
        card(8, "Criando Ritmo", "Conclua 7 treinos.", PintinhoObjectiveType.WORKOUTS_COMPLETED, 7, 180),
        card(9, "Disciplina em Formação", "Conclua 4 treinos nesta semana.", PintinhoObjectiveType.WEEKLY_WORKOUTS, 4, 180),
        card(10, "Já Não Sou o Mesmo", "Conclua 10 treinos.", PintinhoObjectiveType.WORKOUTS_COMPLETED, 10, 250, PintinhoChest.RARO),
        card(11, "Mais Uma Repetição", "Conclua 12 treinos.", PintinhoObjectiveType.WORKOUTS_COMPLETED, 12, 200),
        card(12, "Primeira Evolução", "Registre uma progressão de carga.", PintinhoObjectiveType.PROGRESSION, 1, 220),
        card(13, "Treinar Também é Aprender", "Conclua 15 treinos.", PintinhoObjectiveType.WORKOUTS_COMPLETED, 15, 220),
        card(14, "Corpo em Movimento", "Conclua 18 treinos.", PintinhoObjectiveType.WORKOUTS_COMPLETED, 18, 240),
        card(15, "A Chama da Constância", "Conclua 5 treinos nesta semana.", PintinhoObjectiveType.WEEKLY_WORKOUTS, 5, 260, PintinhoChest.COMUM),
        card(16, "Força em Construção", "Conclua 20 treinos.", PintinhoObjectiveType.WORKOUTS_COMPLETED, 20, 260),
        card(17, "O Hábito Ganhou Forma", "Conclua 24 treinos.", PintinhoObjectiveType.WORKOUTS_COMPLETED, 24, 280),
        card(18, "Disciplina que Inspira", "Conclua 28 treinos.", PintinhoObjectiveType.WORKOUTS_COMPLETED, 28, 300),
        card(19, "Pronto para Evoluir", "Conclua 30 treinos.", PintinhoObjectiveType.WORKOUTS_COMPLETED, 30, 350),
        card(20, "Adeus, Pintinho!", "Conclua 35 treinos para chegar a Frango.", PintinhoObjectiveType.WORKOUTS_COMPLETED, 35, 500, PintinhoChest.EVOLUCAO)
    )

    private fun card(level: Int, title: String, description: String, objectiveType: PintinhoObjectiveType, target: Int, xp: Int, chest: PintinhoChest? = null) =
        PintinhoJourneyCard(level, title, description, objectiveType, target, xp, "journey_pintinho_card_${level.toString().padStart(2, '0')}", chest)
}
