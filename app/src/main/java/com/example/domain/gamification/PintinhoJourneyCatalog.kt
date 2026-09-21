package com.example.domain.gamification

/**
 * Catálogo da primeira jornada. Os [assetKey] são contratos estáveis para os
 * vinte cards ilustrados que serão incluídos posteriormente em drawable-nodpi.
 */
enum class PintinhoObjectiveType { WORKOUTS_COMPLETED, SETS_RECORDED, WEEKLY_WORKOUTS, PROGRESSION, EDUCATION }

enum class PintinhoChest(val label: String) { COMUM("Baú comum"), RARO("Baú raro"), EPICO("Baú épico"), EVOLUCAO("Baú de evolução") }

data class PintinhoJourneyCard(
    val level: Int,
    val title: String,
    val description: String,
    val objectiveType: PintinhoObjectiveType,
    val target: Int,
    val xpReward: Int,
    val assetKey: String,
    val chest: PintinhoChest? = null,
    val rank: String = "Pintinho"
)

object PintinhoJourneyCatalog {
    const val journeyTitle = "Jornada I • O Despertar"
    const val assetDirectory = "drawable-nodpi"

    private val frangoAssetKeys = listOf(
        "journey_frango_card_01_saiu_do_ninho", "journey_frango_card_02_hora_de_crescer",
        "journey_frango_card_03_mais_peso_com_calma", "journey_frango_card_04_tecnica_primeiro",
        "journey_frango_card_05_frango_em_construcao", "journey_frango_card_06_sentindo_a_evolucao",
        "journey_frango_card_07_treino_de_verdade", "journey_frango_card_08_disciplina_tambem_no_prato",
        "journey_frango_card_09_recarga_para_evoluir", "journey_frango_card_10_frango_nivel_up_especial_dourada",
        "journey_frango_card_11_saude_em_movimento", "journey_frango_card_12_forca_em_progresso",
        "journey_frango_card_13_habitos_de_campeao", "journey_frango_card_14_ver_o_progresso",
        "journey_frango_card_15_progresso_sempre", "journey_frango_card_16_frango_forte",
        "journey_frango_card_17_nutricao_em_acao", "journey_frango_card_18_movimento_sem_limites",
        "journey_frango_card_19_habitos_em_dia", "journey_frango_card_20_evolucao_frango_para_lobo_lendaria"
    )

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

    /** Catálogo completo das seis jornadas. Os 20 cards legados acima permanecem
     * estáveis para retrocompatibilidade; a UI e o motor usam esta coleção. */
    val allCards: List<PintinhoJourneyCard> = cards + (21..120).map { level ->
        val rank = rankFor(level)
        PintinhoJourneyCard(
            level = level,
            title = when (level % 20) { 0 -> "Desafio de Evolução" else -> "${rank} em evolução" },
            description = "Mantenha consistência e cumpra o objetivo do estágio.",
            objectiveType = PintinhoObjectiveType.WORKOUTS_COMPLETED,
            target = targetFor(level),
            xpReward = 100 + ((level - 1) % 20) * 15,
            assetKey = if (rank == "Frango") {
                frangoAssetKeys[(level - 21).coerceIn(0, frangoAssetKeys.lastIndex)]
            } else {
                "journey_${rank.lowercase()}_card_${((level - 1) % 20 + 1).toString().padStart(2, '0')}"
            },
            chest = when ((level - 1) % 20 + 1) { 5 -> PintinhoChest.COMUM; 10 -> PintinhoChest.RARO; 15 -> PintinhoChest.EPICO; 20 -> PintinhoChest.EVOLUCAO; else -> null },
            rank = rank
        )
    }

    fun rankFor(level: Int): String = when (level) {
        in 1..20 -> "Pintinho"; in 21..40 -> "Frango"; in 41..60 -> "Lobo"
        in 61..80 -> "Gorila"; in 81..100 -> "Leão"; else -> "Dragão"
    }

    fun targetFor(level: Int): Int = when (rankFor(level)) {
        "Pintinho" -> maxOf(1, level / 2)
        "Frango" -> 18 + (level - 21) / 3
        "Lobo" -> 28 + (level - 41) / 2
        "Gorila" -> 38 + (level - 61) / 2
        "Leão" -> 48 + (level - 81) / 2
        else -> 65 + (level - 101) / 2
    }

    private fun card(level: Int, title: String, description: String, objectiveType: PintinhoObjectiveType, target: Int, xp: Int, chest: PintinhoChest? = null) =
        PintinhoJourneyCard(level, title, description, objectiveType, target, xp, "journey_pintinho_card_${level.toString().padStart(2, '0')}", chest)
}
