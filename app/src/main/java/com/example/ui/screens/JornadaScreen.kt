package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AchievementData
import com.example.ui.components.AllAchievementsDialog
import com.example.ui.components.AllQuestsDialog
import com.example.ui.components.HistoricalMilestoneData
import com.example.ui.components.JornadaHeader
import com.example.ui.components.JourneyCardCelebrationDialog
import com.example.ui.components.JornadaTab
import com.example.ui.components.JornadaUiState
import com.example.ui.components.JourneyTrailCard
import com.example.ui.components.LevelCard
import com.example.ui.components.MilestoneDetailDialog
import com.example.ui.components.MilestoneStatus
import com.example.ui.components.ProximaRecompensaCard
import com.example.ui.components.QuestData
import com.example.ui.components.RewardData
import com.example.ui.components.RewardDetailDialog
import com.example.ui.components.StatsRow
import com.example.ui.components.TabsRow
import com.example.ui.components.TrailMilestoneData
import com.example.ui.components.TrailSelectorDialog
import com.example.ui.components.TwoColumnCardsRow1
import com.example.ui.components.TwoColumnCardsRow2
import com.example.ui.components.defaultHistoricalMilestones
import com.example.ui.components.defaultMilestones
import com.example.ui.viewmodel.FitnessViewModel
import com.example.ui.viewmodel.Phase7ViewModel
import com.example.domain.gamification.PintinhoJourneyCatalog
import com.example.domain.gamification.PintinhoJourneyCard

/**
 * Tela "Jornada" (Gamificação e Progressão)
 * Orquestração pura segundo o padrão arquitetural solicitado, mantendo 100% das regras
 * e o layout fiel à imagem de referência.
 */
@Composable
fun JornadaScreen(
    onOpenCoach: () -> Unit = {},
    onOpenDashboard: () -> Unit = {},
    onStartWorkout: () -> Unit = {},
    phase7Vm: Phase7ViewModel = viewModel(),
    fitnessVm: FitnessViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val journeySnapshot by phase7Vm.journey.collectAsStateWithLifecycle()
    val allMedals by fitnessVm.allMedals.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(JornadaTab.VISAO_GERAL) }
    var activeTrailName by remember { mutableStateOf("Trilha Principal") }

    // Estados de diálogos interativos
    var showTrailDialog by remember { mutableStateOf(false) }
    var showQuestsDialog by remember { mutableStateOf(false) }
    var showAchievementsDialog by remember { mutableStateOf(false) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var selectedMilestoneForDetail by remember { mutableStateOf<TrailMilestoneData?>(null) }
    var unlockedCardForCelebration by remember { mutableStateOf<PintinhoJourneyCard?>(null) }

    // Mapeamento dinâmico e consolidação do JornadaUiState
    val data = journeySnapshot
    val journeyProgress = data?.pintinhoProgress
    val completedJourneyLevels = journeyProgress?.completedLevels ?: 0
    val context = LocalContext.current

    LaunchedEffect(completedJourneyLevels) {
        val preferences = context.getSharedPreferences("journey_card_celebrations", android.content.Context.MODE_PRIVATE)
        val lastCelebratedLevel = preferences.getInt("last_celebrated_level", 0)
        if (completedJourneyLevels > lastCelebratedLevel) {
            unlockedCardForCelebration = PintinhoJourneyCatalog.cards.getOrNull(completedJourneyLevels - 1)
            preferences.edit().putInt("last_celebrated_level", completedJourneyLevels).apply()
        }
    }
    val level = completedJourneyLevels + 1
    val levelTitle = if (completedJourneyLevels >= PintinhoJourneyCatalog.cards.size) "Frango • nova jornada" else "Pintinho • nível $level"
    val avatarRes = when {
        level >= 91 -> com.example.R.drawable.journey_avatar_dragao
        level >= 71 -> com.example.R.drawable.journey_avatar_gorila
        level >= 51 -> com.example.R.drawable.journey_avatar_leao
        level >= 36 -> com.example.R.drawable.journey_avatar_lobo
        level >= 21 -> com.example.R.drawable.journey_avatar_frango
        else -> com.example.R.drawable.journey_avatar_pintinho
    }
    // XP exibido no cartão é o progresso do nível atual, não o total histórico.
    // Assim a jornada começa de forma clara em "Nível 1 • 0 / 100 XP".
    val totalJourneyXp = journeyProgress?.totalJourneyXp ?: 0
    val xpFromCompletedLevels = PintinhoJourneyCatalog.cards
        .take(completedJourneyLevels)
        .sumOf { it.xpReward }
    val currentXp = (totalJourneyXp - xpFromCompletedLevels).coerceAtLeast(0)
    val targetXp = journeyProgress?.currentCard?.xpReward ?: 100
    val streakDays = data?.currentStreakDays ?: 0
    val totalAchievements = allMedals.count { it.isUnlocked }
    val onTimeGoalsPercent = ((data?.weeklyQuests?.firstOrNull()?.progress ?: 0f) * 100).toInt()
    val totalPoints = currentXp

    val currentQuest = journeyProgress?.currentCard?.let { card ->
        QuestData("pintinho_${card.level}", "Nível ${card.level} • ${card.title}", card.description, journeyProgress.objectiveCurrent.coerceAtMost(card.target), card.target, card.xpReward)
    } ?: if (data != null && data.weeklyQuests.isNotEmpty()) {
        val q = data.weeklyQuests.first()
        QuestData(
            id = q.id,
            title = "Completar 5 treinos nesta semana",
            description = q.subtitle,
            current = q.current,
            target = 5,
            rewardXp = q.rewardXp
        )
    } else {
        QuestData(
            id = "quest_weekly",
            title = "Completar 5 treinos nesta semana",
            description = "Mantenha o foco e avance na sua jornada.",
            current = 0,
            target = 5,
            rewardXp = 150
        )
    }

    val questList = data?.weeklyQuests?.map { q ->
        QuestData(
            id = q.id,
            title = q.title,
            description = q.subtitle,
            current = q.current,
            target = q.target,
            rewardXp = q.rewardXp
        )
    } ?: listOf(
        currentQuest,
        QuestData("q2", "60 min de cardio na semana", "Some qualquer modalidade cardiovascular.", 35, 60, 120),
        QuestData("q3", "5 toneladas de volume", "Volume acumulado dos treinos de força.", 3, 5, 180)
    )

    val currentAchievement = AchievementData(
        id = "ach_semana_ferro",
        title = "Semana de Ferro",
        description = "Complete 7 treinos na semana.",
        badgeName = "Nível Prata",
        current = 2,
        target = 7,
        isUnlocked = false
    )

    val achievementList = if (allMedals.isNotEmpty()) {
        allMedals.map { m ->
            AchievementData(
                id = m.id,
                title = m.title,
                description = m.description,
                badgeName = m.rarity,
                current = m.progressCurrent,
                target = m.progressMax,
                isUnlocked = m.isUnlocked,
                rarity = m.rarity
            )
        }
    } else {
        listOf(
            currentAchievement,
            AchievementData("a1", "Início de Ouro", "Faça seu primeiro treino registrado", "Bronze", 1, 1, true),
            AchievementData("a2", "Consistência 7", "Mantenha 7 dias consecutivos de treinos", "Prata", 7, 7, true),
            AchievementData("a3", "Foco Implacável", "Alcance 30 dias de sequência", "Ouro", 30, 30, true),
            AchievementData("a4", "Monstro dos Pesos", "Levante mais de 10 toneladas de volume total", "Master", 6, 10, false)
        )
    }

    val nextReward = RewardData(
        id = "reward_bau_evolucao",
        title = "Baú da Evolução",
        description = "Complete mais 750 XP para desbloquear.",
        currentXp = currentXp,
        targetXp = targetXp,
        isUnlocked = false
    )

    val uiState = JornadaUiState(
        selectedTab = selectedTab,
        level = level,
        levelTitle = levelTitle,
        currentXp = currentXp,
        targetXp = targetXp,
        streakDays = streakDays,
        totalAchievements = totalAchievements,
        onTimeGoalsPercent = onTimeGoalsPercent,
        totalPoints = totalPoints,
        trailMilestones = PintinhoJourneyCatalog.cards.map { card ->
            TrailMilestoneData(
                id = "pintinho_${card.level}", title = card.level.toString(), subtitle = card.title,
                status = when {
                    card.level <= completedJourneyLevels -> MilestoneStatus.COMPLETED
                    card.level == level && completedJourneyLevels < PintinhoJourneyCatalog.cards.size -> MilestoneStatus.CURRENT
                    else -> MilestoneStatus.LOCKED
                }
            )
        },
        currentQuest = currentQuest,
        questList = questList,
        currentAchievement = currentAchievement,
        achievementList = achievementList,
        nextReward = nextReward,
        historicalMilestones = defaultHistoricalMilestones(),
        activeTrailName = activeTrailName
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0814))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("jornada_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 1. JornadaHeader
            item(key = "header") {
                JornadaHeader(
                    motivationalQuote = uiState.motivationalQuoteHeader
                )
            }

            // 2. TabsRow (Navegação Interna)
            item(key = "tabs") {
                TabsRow(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            // Conteúdo baseado na aba selecionada
            when (selectedTab) {
                JornadaTab.VISAO_GERAL -> {
                    // 3. LevelCard
                    item(key = "level_card") {
                        LevelCard(
                            level = uiState.level,
                            levelTitle = uiState.levelTitle,
                            currentXp = uiState.currentXp,
                            targetXp = uiState.targetXp,
                            motivationalQuote = uiState.motivationalQuoteLevel,
                            avatarRes = avatarRes
                        )
                    }

                    // 4. StatsRow (4 cards iguais)
                    item(key = "stats_row") {
                        StatsRow(
                            streakDays = uiState.streakDays,
                            totalAchievements = uiState.totalAchievements,
                            onTimeGoalsPercent = uiState.onTimeGoalsPercent,
                            totalPoints = uiState.totalPoints
                        )
                    }

                    // 5. JourneyTrailCard ("Sua Jornada" com trilha e montanhas vetoriais)
                    item(key = "journey_trail") {
                        JourneyTrailCard(
                            activeTrailName = uiState.activeTrailName,
                            milestones = uiState.trailMilestones,
                            onSelectTrail = { showTrailDialog = true },
                            onMilestoneClick = { selectedMilestoneForDetail = it }
                        )
                    }

                    // 6. TwoColumnCardsRow #1 (Missão Atual + Conquista em Progresso)
                    item(key = "two_col_row_1") {
                        TwoColumnCardsRow1(
                            quest = uiState.currentQuest,
                            achievement = uiState.currentAchievement,
                            onViewAllQuests = { showQuestsDialog = true },
                            onViewAllAchievements = { showAchievementsDialog = true }
                        )
                    }

                    // 7. TwoColumnCardsRow #2 (Próxima Recompensa + Marcos da Jornada)
                    item(key = "two_col_row_2") {
                        TwoColumnCardsRow2(
                            reward = uiState.nextReward,
                            milestones = uiState.historicalMilestones,
                            onViewRewardDetails = { showRewardDialog = true },
                            onViewAllMilestones = { selectedTab = JornadaTab.CONQUISTAS }
                        )
                    }

                    // Acesso rápido adicional preservando ações do app
                    item(key = "quick_actions") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onStartWorkout,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("jornada_btn_start_workout"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                    Text("INICIAR NOVO TREINO", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onOpenCoach,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38265E))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Outlined.SmartToy, contentDescription = null, tint = Color(0xFFC4B5FD))
                                        Text("COACH IA", color = Color(0xFFC4B5FD), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                OutlinedButton(
                                    onClick = onOpenDashboard,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38265E))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Outlined.Insights, contentDescription = null, tint = Color(0xFFC4B5FD))
                                        Text("PAINEL", color = Color(0xFFC4B5FD), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                JornadaTab.MISSOES -> {
                    item(key = "tab_missoes_header") {
                        Text(
                            text = "Missões Diárias e Semanais",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(uiState.questList) { quest ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF15111F),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF261D3B)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = quest.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "+${quest.rewardXp} XP",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFA78BFA)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = quest.description,
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF1F2937))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(quest.progress)
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (quest.completed) Color(0xFF10B981) else Color(0xFF7C3AED))
                                        )
                                    }
                                    Text(
                                        text = "${quest.current} / ${quest.target}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                JornadaTab.CONQUISTAS -> {
                    item(key = "tab_conquistas_header") {
                        Text(
                            text = "Galeria de Conquistas e Medalhas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(uiState.achievementList) { ach ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF15111F),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF261D3B)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(if (ach.isUnlocked) Color(0xFFD97706) else Color(0xFF281B40)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (ach.isUnlocked) Icons.Outlined.FitnessCenter else Icons.Filled.Lock,
                                        contentDescription = null,
                                        tint = if (ach.isUnlocked) Color.White else Color(0xFF94A3B8),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = ach.title,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = ach.badgeName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (ach.isUnlocked) Color(0xFFFDE68A) else Color(0xFF94A3B8)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = ach.description,
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }

                JornadaTab.NIVEIS -> {
                    item(key = "tab_niveis_content") {
                        LevelCard(
                            level = uiState.level,
                            levelTitle = uiState.levelTitle,
                            currentXp = uiState.currentXp,
                            targetXp = uiState.targetXp,
                            motivationalQuote = uiState.motivationalQuoteLevel
                        )
                    }
                    items(PintinhoJourneyCatalog.cards) { card ->
                        val lvl = card.level
                        val isCurrent = lvl == uiState.level
                        val isPassed = lvl <= completedJourneyLevels
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isCurrent) Color(0xFF2E1A47) else Color(0xFF15111F),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isCurrent) Color(0xFFA855F7) else Color(0xFF261D3B)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Nível $lvl • ${card.title}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) Color(0xFFC4B5FD) else Color.White
                                    )
                                    Text(
                                        text = card.description,
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                Text(
                                    text = if (isPassed) "Alcançado ✓" else if (isCurrent) "Nível Atual" else "Bloqueado 🔒",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPassed) Color(0xFF10B981) else if (isCurrent) Color(0xFFA78BFA) else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                JornadaTab.COLECAO -> {
                    item(key = "collection_header") {
                        Text("Coleção Pintinho • $completedJourneyLevels / ${PintinhoJourneyCatalog.cards.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                    items(PintinhoJourneyCatalog.cards) { card ->
                        val unlocked = card.level <= completedJourneyLevels
                        val context = LocalContext.current
                        val artworkResId = context.resources.getIdentifier(card.assetKey, "drawable", context.packageName)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (unlocked) Color(0xFF21163A) else Color(0xFF15111F),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (unlocked) Color(0xFF7C3AED) else Color(0xFF261D3B)),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).testTag("journey_card_${card.level}")
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                if (artworkResId != 0) {
                                    Image(
                                        painter = painterResource(artworkResId),
                                        contentDescription = "Card ${card.level}: ${card.title}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(width = 72.dp, height = 106.dp).clip(RoundedCornerShape(10.dp))
                                    )
                                } else {
                                    Surface(shape = CircleShape, color = if (unlocked) Color(0xFF7C3AED) else Color(0xFF2A2439), modifier = Modifier.size(42.dp)) {
                                        Box(contentAlignment = Alignment.Center) { Text(if (unlocked) "${card.level}" else "🔒", color = Color.White, fontWeight = FontWeight.Bold) }
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(card.title, color = Color.White, fontWeight = FontWeight.Bold)
                                    Text(card.description, color = Color(0xFFB8A9D8), fontSize = 12.sp)
                                    Text("+${card.xpReward} XP${card.chest?.let { " • ${it.label}" } ?: ""}", color = Color(0xFFC4B5FD), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    item(key = "collection_assets_note") { Text("Os 20 espaços de arte já estão preparados: journey_pintinho_card_01 a journey_pintinho_card_20.", color = Color(0xFF94A3B8), fontSize = 12.sp, modifier = Modifier.padding(16.dp)) }
                }

                JornadaTab.RECOMPENSAS -> {
                    item(key = "tab_recompensas_content") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Baús e Recompensas Especiais",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            ProximaRecompensaCard(
                                reward = uiState.nextReward,
                                onClick = { showRewardDialog = true }
                            )
                        }
                    }
                }
            }
        }

        // Diálogos de Detalhe
        if (showTrailDialog) {
            TrailSelectorDialog(
                currentTrail = activeTrailName,
                onSelectTrail = { activeTrailName = it },
                onDismiss = { showTrailDialog = false }
            )
        }

        if (showQuestsDialog) {
            AllQuestsDialog(
                quests = uiState.questList,
                onClaimReward = {},
                onDismiss = { showQuestsDialog = false }
            )
        }

        if (showAchievementsDialog) {
            AllAchievementsDialog(
                achievements = uiState.achievementList,
                onDismiss = { showAchievementsDialog = false }
            )
        }

        if (showRewardDialog) {
            RewardDetailDialog(
                reward = uiState.nextReward,
                onDismiss = { showRewardDialog = false }
            )
        }

        selectedMilestoneForDetail?.let { milestone ->
            MilestoneDetailDialog(
                milestone = milestone,
                onDismiss = { selectedMilestoneForDetail = null }
            )
        }

        unlockedCardForCelebration?.let { card ->
            JourneyCardCelebrationDialog(
                card = card,
                onOpenCollection = {
                    selectedTab = JornadaTab.COLECAO
                    unlockedCardForCelebration = null
                },
                onDismiss = { unlockedCardForCelebration = null }
            )
        }
    }
}
