package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material.icons.outlined.Redeem
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R

// ==========================================
// Modelos de Dados da Tela Jornada
// ==========================================

enum class JornadaTab(val title: String) {
    VISAO_GERAL("Visão Geral"),
    MISSOES("Missões"),
    CONQUISTAS("Conquistas"),
    NIVEIS("Níveis"),
    COLECAO("Coleção"),
    RECOMPENSAS("Recompensas")
}

enum class MilestoneStatus {
    COMPLETED,
    CURRENT,
    LOCKED
}

data class TrailMilestoneData(
    val id: String,
    val title: String,
    val subtitle: String,
    val status: MilestoneStatus,
    val daysLabel: String = ""
)

data class QuestData(
    val id: String,
    val title: String,
    val description: String,
    val current: Int,
    val target: Int,
    val rewardXp: Int = 100
) {
    val progress: Float get() = if (target > 0) (current.toFloat() / target).coerceIn(0f, 1f) else 0f
    val completed: Boolean get() = current >= target
}

data class AchievementData(
    val id: String,
    val title: String,
    val description: String,
    val badgeName: String = "Nível Prata",
    val current: Int,
    val target: Int,
    val isUnlocked: Boolean = false,
    val rarity: String = "Prata"
) {
    val progress: Float get() = if (target > 0) (current.toFloat() / target).coerceIn(0f, 1f) else 0f
}

data class RewardData(
    val id: String,
    val title: String,
    val description: String,
    val currentXp: Int,
    val targetXp: Int,
    val isUnlocked: Boolean = false
) {
    val progress: Float get() = if (targetXp > 0) (currentXp.toFloat() / targetXp).coerceIn(0f, 1f) else 0f
    val remainingXp: Int get() = (targetXp - currentXp).coerceAtLeast(0)
}

data class HistoricalMilestoneData(
    val id: String,
    val title: String,
    val status: MilestoneStatus,
    val dateOrStatusLabel: String
)

data class JornadaUiState(
    val selectedTab: JornadaTab = JornadaTab.VISAO_GERAL,
    val level: Int = 12,
    val levelTitle: String = "Lobo Determinado",
    val currentXp: Int = 1250,
    val targetXp: Int = 2000,
    val streakDays: Int = 45,
    val totalAchievements: Int = 12,
    val onTimeGoalsPercent: Int = 78,
    val totalPoints: Int = 1250,
    val motivationalQuoteHeader: String = "“Pequenas ações diárias\nconstroem grandes resultados.”",
    val motivationalQuoteLevel: String = "“Mais forte\nque ontem.”",
    val trailMilestones: List<TrailMilestoneData> = emptyList(),
    val currentQuest: QuestData = QuestData(
        id = "quest_5_treinos",
        title = "Completar 5 treinos nesta semana",
        description = "Mantenha o foco e avance na sua jornada.",
        current = 2,
        target = 5,
        rewardXp = 150
    ),
    val questList: List<QuestData> = emptyList(),
    val currentAchievement: AchievementData = AchievementData(
        id = "ach_semana_ferro",
        title = "Semana de Ferro",
        description = "Complete 7 treinos na semana.",
        badgeName = "Nível Prata",
        current = 2,
        target = 7,
        isUnlocked = false
    ),
    val achievementList: List<AchievementData> = emptyList(),
    val nextReward: RewardData = RewardData(
        id = "reward_bau_evolucao",
        title = "Baú da Evolução",
        description = "Complete mais 750 XP para desbloquear.",
        currentXp = 1250,
        targetXp = 2000,
        isUnlocked = false
    ),
    val rewardList: List<RewardData> = emptyList(),
    val historicalMilestones: List<HistoricalMilestoneData> = emptyList(),
    val activeTrailName: String = "Trilha Principal"
)

// ==========================================
// 1. JornadaHeader
// ==========================================

@Composable
fun JornadaHeader(
    motivationalQuote: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Jornada",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.testTag("jornada_header_title")
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Disciplina hoje, um eu melhor amanhã.",
                fontSize = 14.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = motivationalQuote,
            fontSize = 12.sp,
            fontStyle = FontStyle.Italic,
            color = Color(0xFFCBD5E1),
            textAlign = TextAlign.End,
            lineHeight = 16.sp,
            modifier = Modifier.weight(0.9f)
        )
    }
}

// ==========================================
// 2. TabsRow
// ==========================================

@Composable
fun TabsRow(
    selectedTab: JornadaTab,
    onTabSelected: (JornadaTab) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(JornadaTab.values()) { tab ->
            val isSelected = tab == selectedTab
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTabSelected(tab) }
                    .testTag("tab_${tab.name.lowercase()}"),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(0xFF7C3AED) else Color(0xFF15111F),
                border = if (!isSelected) BorderStroke(1.dp, Color(0xFF261D3B)) else null
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.title,
                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ==========================================
// 3. LevelCard
// ==========================================

@Composable
fun LevelCard(
    level: Int,
    levelTitle: String,
    currentXp: Int,
    targetXp: Int,
    motivationalQuote: String,
    avatarRes: Int = R.drawable.journey_avatar_pintinho,
    modifier: Modifier = Modifier
) {
    val progress = if (targetXp > 0) (currentXp.toFloat() / targetXp).coerceIn(0f, 1f) else 0f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF15111F),
        border = BorderStroke(1.dp, Color(0xFF261D3B)),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lado esquerdo: Avatar com Lobo e Glow Neon
            WolfAvatar(
                avatarRes = avatarRes,
                size = 80.dp,
                modifier = Modifier.testTag("journey_pintinho_avatar")
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Centro: Nível, Título, Barra e XP
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Nível $level",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = levelTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFA78BFA)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Barra de Progresso Roxa
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF261D3B))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED))
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${formatNumber(currentXp)} / ${formatNumber(targetXp)} XP",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Divisor vertical sutil
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .padding(vertical = 4.dp)
                    .background(Color(0xFF261D3B))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Lado direito: Coroa de Louros e Frase Motivacional
            Column(
                modifier = Modifier.width(96.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CrownLaurelIcon(size = 38.dp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = motivationalQuote,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// ==========================================
// 4. StatsRow & StatCard
// ==========================================

@Composable
fun StatsRow(
    streakDays: Int,
    totalAchievements: Int,
    onTimeGoalsPercent: Int,
    totalPoints: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = { FlameIcon(size = 22.dp) },
            value = "$streakDays",
            label = "Dias seguidos",
            tag = "stat_streak"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = { TrophyIcon(size = 22.dp) },
            value = "$totalAchievements",
            label = "Conquistas",
            tag = "stat_achievements"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = { TrendingChartIcon(size = 22.dp) },
            value = "$onTimeGoalsPercent%",
            label = "Metas no prazo",
            tag = "stat_goals"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = { StarIcon(size = 22.dp) },
            value = formatNumber(totalPoints),
            label = "Pontos totais",
            tag = "stat_points"
        )
    }
}

@Composable
fun StatCard(
    icon: @Composable () -> Unit,
    value: String,
    label: String,
    tag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.testTag(tag),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF15111F),
        border = BorderStroke(1.dp, Color(0xFF261D3B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(26.dp),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==========================================
// 5. JourneyTrailCard & TrailMilestone
// ==========================================

@Composable
fun JourneyTrailCard(
    activeTrailName: String,
    milestones: List<TrailMilestoneData>,
    onSelectTrail: () -> Unit,
    onMilestoneClick: (TrailMilestoneData) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF15111F),
        border = BorderStroke(1.dp, Color(0xFF261D3B))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            // Fundo de montanhas ao entardecer (Canvas vetorial)
            MountainLandscapeCanvas(modifier = Modifier.fillMaxSize())

            // Conteúdo sobreposto
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header do Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Sua Jornada",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Cada treino te leva mais longe.",
                            fontSize = 13.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }

                    // Chip Trilha Principal
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectTrail() }
                            .testTag("chip_select_trail"),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xD91E1533),
                        border = BorderStroke(1.dp, Color(0xFF3B2A5E))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Terrain,
                                contentDescription = "Trilha",
                                tint = Color(0xFFC4B5FD),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = activeTrailName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE2E8F0)
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Selecionar",
                                tint = Color(0xFFC4B5FD),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Trilha de Marcos Horizontal
                val displayMilestones = if (milestones.isNotEmpty()) milestones else defaultMilestones()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 16.dp, bottom = 6.dp)
                ) {
                    // Linha pontilhada conectando os marcos
                    Canvas(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(top = 22.dp)
                    ) {
                        val strokeWidth = 2.dp.toPx()
                        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                        drawLine(
                            color = Color(0xFFC4B5FD).copy(alpha = 0.5f),
                            start = Offset(20.dp.toPx(), 0f),
                            end = Offset(size.width - 20.dp.toPx(), 0f),
                            strokeWidth = strokeWidth,
                            pathEffect = pathEffect
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(28.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        displayMilestones.forEach { milestone ->
                            TrailMilestone(
                                milestone = milestone,
                                onClick = { onMilestoneClick(milestone) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrailMilestone(
    milestone: TrailMilestoneData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_${milestone.id}")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier
            .width(88.dp)
            .clickable { onClick() }
            .testTag("milestone_${milestone.id}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Círculo do marco
        when (milestone.status) {
            MilestoneStatus.COMPLETED -> {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                        .border(2.dp, Color(0xFF34D399), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Concluído",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            MilestoneStatus.CURRENT -> {
                Box(
                    modifier = Modifier.size(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Glow pulsante roxo
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFA855F7).copy(alpha = glowAlpha * 0.4f))
                    )
                    // Círculo roxo com ícone do lobo
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFFA855F7), Color(0xFF6B21A8))
                                )
                            )
                            .border(2.dp, Color(0xFFE9D5FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        WolfIconSmall(size = 20.dp)
                    }
                }
            }
            MilestoneStatus.LOCKED -> {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B).copy(alpha = 0.85f))
                        .border(1.5.dp, Color(0xFF475569), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (milestone.id == "lenda" || milestone.title.contains("Lenda")) {
                        Icon(
                            imageVector = Icons.Outlined.Flag,
                            contentDescription = "Lenda",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Bloqueado",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Título do marco
        Text(
            text = milestone.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Subtítulo do marco
        Text(
            text = milestone.subtitle,
            fontSize = 10.sp,
            color = if (milestone.status == MilestoneStatus.CURRENT) Color(0xFFC4B5FD) else Color(0xFFCBD5E1),
            textAlign = TextAlign.Center,
            lineHeight = 13.sp,
            maxLines = 2
        )
    }
}

// ==========================================
// 6. TwoColumnCardsRow #1 (Missão + Conquista)
// ==========================================

@Composable
fun TwoColumnCardsRow1(
    quest: QuestData,
    achievement: AchievementData,
    onViewAllQuests: () -> Unit,
    onViewAllAchievements: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MissaoAtualCard(
            quest = quest,
            onViewAll = onViewAllQuests,
            modifier = Modifier.weight(1f)
        )
        ConquistaProgressoCard(
            achievement = achievement,
            onViewAll = onViewAllAchievements,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MissaoAtualCard(
    quest: QuestData,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF15111F),
        border = BorderStroke(1.dp, Color(0xFF261D3B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Header com Alvo Roxo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TrackChanges,
                        contentDescription = "Missão",
                        tint = Color(0xFFA855F7),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Missão Atual",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = quest.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 19.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = quest.description,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 16.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Barra de progresso verde
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
                                .background(Color(0xFF10B981))
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

            Spacer(modifier = Modifier.height(14.dp))

            // Botão "Ver todas as missões >"
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onViewAll() }
                    .testTag("btn_view_all_quests"),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1B142B),
                border = BorderStroke(1.dp, Color(0xFF38265E))
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ver todas as missões  >",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC4B5FD)
                    )
                }
            }
        }
    }
}

@Composable
fun ConquistaProgressoCard(
    achievement: AchievementData,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF15111F),
        border = BorderStroke(1.dp, Color(0xFF261D3B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Header com Medalha Dourada
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MilitaryTech,
                        contentDescription = "Conquista",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Conquista em Progresso",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = achievement.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )

                    // Badge circular Prata com troféu
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF33254A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.EmojiEvents,
                                contentDescription = "Prata",
                                tint = Color(0xFFE2E8F0),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = achievement.badgeName,
                            fontSize = 9.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = achievement.description,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 16.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Barra de progresso laranja
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
                                .fillMaxWidth(achievement.progress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF97316))
                        )
                    }
                    Text(
                        text = "${achievement.current} / ${achievement.target}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Botão "Ver todas as conquistas >"
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onViewAll() }
                    .testTag("btn_view_all_achievements"),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1B142B),
                border = BorderStroke(1.dp, Color(0xFF38265E))
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ver todas as conquistas  >",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC4B5FD)
                    )
                }
            }
        }
    }
}

// ==========================================
// 7. TwoColumnCardsRow #2 (Recompensa + Marcos)
// ==========================================

@Composable
fun TwoColumnCardsRow2(
    reward: RewardData,
    milestones: List<HistoricalMilestoneData>,
    onViewRewardDetails: () -> Unit,
    onViewAllMilestones: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProximaRecompensaCard(
            reward = reward,
            onClick = onViewRewardDetails,
            modifier = Modifier.weight(1f)
        )
        MarcosJornadaCard(
            milestones = milestones,
            onViewAll = onViewAllMilestones,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ProximaRecompensaCard(
    reward: RewardData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF15111F),
        border = BorderStroke(1.dp, Color(0xFF261D3B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Header com Presente Roxo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Redeem,
                        contentDescription = "Recompensa",
                        tint = Color(0xFFA855F7),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Próxima Recompensa",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Ilustração central do Baú do Tesouro Mágico (100% Canvas, zero fotos)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.journey_evolution_chest),
                        contentDescription = "Baú da Evolução",
                        modifier = Modifier.fillMaxHeight().fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = reward.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = reward.description,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Barra de progresso roxa
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1F2937))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(reward.progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF7C3AED))
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${formatNumber(reward.currentXp)} / ${formatNumber(reward.targetXp)} XP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun MarcosJornadaCard(
    milestones: List<HistoricalMilestoneData>,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayList = if (milestones.isNotEmpty()) milestones else defaultHistoricalMilestones()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF15111F),
        border = BorderStroke(1.dp, Color(0xFF261D3B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Header com Bandeira e Link "Ver todos"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Flag,
                            contentDescription = "Marcos",
                            tint = Color(0xFFA855F7),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Marcos da Jornada",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "Ver todos",
                        fontSize = 12.sp,
                        color = Color(0xFFA78BFA),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onViewAll() }
                            .testTag("link_view_all_milestones")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Lista de marcos históricos
                displayList.take(5).forEachIndexed { index, item ->
                    MarcoItem(item = item)
                    if (index < 4) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MarcoItem(
    item: HistoricalMilestoneData,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (item.status) {
                MilestoneStatus.COMPLETED -> {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completo",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                MilestoneStatus.CURRENT -> {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFFA855F7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFA855F7))
                        )
                    }
                }
                MilestoneStatus.LOCKED -> {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color(0xFF475569), CircleShape)
                    )
                }
            }

            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = if (item.status != MilestoneStatus.LOCKED) FontWeight.Medium else FontWeight.Normal,
                color = if (item.status != MilestoneStatus.LOCKED) Color.White else Color(0xFF94A3B8)
            )
        }

        Text(
            text = item.dateOrStatusLabel,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = when (item.status) {
                MilestoneStatus.COMPLETED -> Color(0xFF10B981)
                MilestoneStatus.CURRENT -> Color(0xFFA78BFA)
                MilestoneStatus.LOCKED -> Color(0xFF64748B)
            }
        )
    }
}

// ==========================================
// 8. Diálogos e Seções Detalhadas
// ==========================================

@Composable
fun TrailSelectorDialog(
    currentTrail: String,
    onSelectTrail: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val trails = listOf(
        "Trilha Principal" to "Foco em consistência, hipertrofia e progressão geral",
        "Trilha de Força Pura" to "Metas de cargas em supino, agachamento e terra",
        "Trilha Cardio & Resistência" to "Desafios de corrida, ciclismo e fôlego",
        "Trilha Queima & Definição" to "Frequência alta e controle calórico diário"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1A1429),
            border = BorderStroke(1.dp, Color(0xFF38265E)),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Selecionar Trilha da Jornada",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Escolha sua rota de evolução para alinhar os marcos com seus objetivos atuais.",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(16.dp))

                trails.forEach { (name, desc) ->
                    val isSelected = name == currentTrail
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                onSelectTrail(name)
                                onDismiss()
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFF2E1A47) else Color(0xFF140E22),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFFA855F7) else Color(0xFF261D3B)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF7C3AED)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selecionada",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Fechar", color = Color(0xFFC4B5FD))
                }
            }
        }
    }
}

// Helpers de formatação e valores padrão
private fun formatNumber(value: Int): String {
    return if (value >= 1000) {
        String.format("%,d", value).replace(',', '.')
    } else {
        value.toString()
    }
}

fun defaultMilestones(): List<TrailMilestoneData> = listOf(
    TrailMilestoneData(
        id = "inicio",
        title = "Início",
        subtitle = "Hábito\ndesenvolvido",
        status = MilestoneStatus.COMPLETED
    ),
    TrailMilestoneData(
        id = "consistencia",
        title = "Consistência",
        subtitle = "7 dias\ncompletos",
        status = MilestoneStatus.COMPLETED
    ),
    TrailMilestoneData(
        id = "disciplina",
        title = "Disciplina",
        subtitle = "30 dias\ncompletos",
        status = MilestoneStatus.COMPLETED
    ),
    TrailMilestoneData(
        id = "evolucao",
        title = "Evolução",
        subtitle = "60 dias\nem andamento",
        status = MilestoneStatus.CURRENT
    ),
    TrailMilestoneData(
        id = "alta_performance",
        title = "Alta Performance",
        subtitle = "90 dias",
        status = MilestoneStatus.LOCKED
    ),
    TrailMilestoneData(
        id = "lenda",
        title = "Lenda",
        subtitle = "180 dias",
        status = MilestoneStatus.LOCKED
    )
)

fun defaultHistoricalMilestones(): List<HistoricalMilestoneData> = listOf(
    HistoricalMilestoneData("m1", "Primeiro treino", MilestoneStatus.COMPLETED, "10/08/2026"),
    HistoricalMilestoneData("m2", "7 dias seguidos", MilestoneStatus.COMPLETED, "17/08/2026"),
    HistoricalMilestoneData("m3", "30 dias seguidos", MilestoneStatus.COMPLETED, "09/09/2026"),
    HistoricalMilestoneData("m4", "60 dias seguidos", MilestoneStatus.CURRENT, "Em andamento"),
    HistoricalMilestoneData("m5", "90 dias seguidos", MilestoneStatus.LOCKED, "Bloqueado")
)
