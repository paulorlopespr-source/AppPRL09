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
    private val loboAssetKeys = listOf(
        "journey_lobo_card_01_o_chamado_da_matilha", "journey_lobo_card_02_pegadas_no_caminho",
        "journey_lobo_card_03_cacador_disciplinado", "journey_lobo_card_04_mais_forte_na_cacada",
        "journey_lobo_card_05_coracao_de_guerreiro", "journey_lobo_card_06_escolhas_inteligentes",
        "journey_lobo_card_07_carga_e_progresso", "journey_lobo_card_08_flexibilidade_e_longevidade",
        "journey_lobo_card_09_a_forca_do_descanso", "journey_lobo_card_10_topo_da_jornada_especial",
        "journey_lobo_card_11_persistencia", "journey_lobo_card_12_resistencia_em_acao",
        "journey_lobo_card_13_hidratacao_e_forca", "journey_lobo_card_14_pernas_fortes",
        "journey_lobo_card_15_forca_da_matilha", "journey_lobo_card_16_evolucao_constante",
        "journey_lobo_card_17_descanso_inteligente", "journey_lobo_card_18_nutricao_e_poder",
        "journey_lobo_card_19_movimento_liberta", "journey_lobo_card_20_matilha_vencedora_especial"
    )
    private val gorilaAssetKeys = listOf(
        "jornada_gorila_card_61_a_fera_despertou", "jornada_gorila_card_62_forca_em_construcao",
        "jornada_gorila_card_63_peso_sob_controle", "jornada_gorila_card_64_forca_com_tecnica",
        "jornada_gorila_card_65_punhos_de_aco", "jornada_gorila_card_66_carga_constante",
        "jornada_gorila_card_67_intensidade_em_alta", "jornada_gorila_card_68_resistencia_em_acao",
        "jornada_gorila_card_69_mente_e_musculo", "jornada_gorila_card_70_especial",
        "jornada_gorila_card_71_supere_seus_limites", "jornada_gorila_card_72_movimento_gera_progresso",
        "jornada_gorila_card_73_novos_horizontes", "jornada_gorila_card_74_execucao_gera_resultado",
        "jornada_gorila_card_75_base_solida_grandes_conquistas", "jornada_gorila_card_76_costas_mais_fortes",
        "jornada_gorila_card_77_peito_mais_forte", "jornada_gorila_card_78_pernas_mais_fortes",
        "jornada_gorila_card_79_biceps_mais_fortes", "jornada_gorila_card_80_evolucao_especial"
    )
    private val leaoAssetKeys = listOf(
        "jornada_leao_card_81_o_leao_despertou", "jornada_leao_card_82_forca_sob_comando",
        "jornada_leao_card_83_precisao_de_cacador", "jornada_leao_card_84_instinto_e_tecnica",
        "jornada_leao_card_85_marcando_territorio", "jornada_leao_card_86_ritmo_do_predador",
        "jornada_leao_card_87_sempre_evoluindo", "jornada_leao_card_88_controle_absoluto",
        "jornada_leao_card_89", "jornada_leao_card_90_especial_diamante",
        "jornada_leao_card_91", "jornada_leao_card_92", "jornada_leao_card_93",
        "jornada_leao_card_94", "jornada_leao_card_95", "jornada_leao_card_96",
        "jornada_leao_card_97", "jornada_leao_card_98_rei_por_merecimento",
        "jornada_leao_card_99", "jornada_leao_card_100_especial_diamante"
    )
    private val dragaoAssetKeys = (101..120).map { "journey_dragao_card_$it" }

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
            } else if (rank == "Lobo") {
                loboAssetKeys[(level - 41).coerceIn(0, loboAssetKeys.lastIndex)]
            } else if (rank == "Gorila") {
                gorilaAssetKeys[(level - 61).coerceIn(0, gorilaAssetKeys.lastIndex)]
            } else if (rank == "Leão") {
                leaoAssetKeys[(level - 81).coerceIn(0, leaoAssetKeys.lastIndex)]
            } else if (rank == "Dragão") {
                dragaoAssetKeys[(level - 101).coerceIn(0, dragaoAssetKeys.lastIndex)]
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
