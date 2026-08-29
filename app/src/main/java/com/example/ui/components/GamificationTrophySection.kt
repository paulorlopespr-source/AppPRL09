package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GamificationOverview
import com.example.data.model.GoalPeriod
import com.example.data.model.MedalRarity
import com.example.data.model.UserMedal
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlowPurple
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YellowWarning

/**
 * Cycle definitions for organizing medals in weekly, monthly, annual, and master tracks.
 */
enum class MedalCycleTab(val title: String, val subtitle: String, val icon: String) {
    TODOS("Todas", "Visão Geral", "✨"),
    SEMANAL("Semanal", "Ciclo de 7 Dias", "📅"),
    MENSAL("Mensal", "Ciclo de 30 Dias", "🗓️"),
    ANUAL("Anual", "Ciclo de 365 Dias", "🏆"),
    ESPECIAL("Especiais", "Marcos Históricos", "⚡")
}

/**
 * Modern M3 Gamification Trophy & Medals Section organizing badges by cycles
 * (Semanal, Mensal, Anual, Especial) and Rarity tiers (Bronze, Prata, Ouro, Master da Superação).
 * Renders Scalable Vector Medallions with metallic gradients, sunburst rays, and achievement animations.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GamificationTrophySection(
    userMedals: List<UserMedal>,
    overview: GamificationOverview? = null,
    onMedalClick: (UserMedal) -> Unit = {},
    onCelebrateMedal: (UserMedal) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // If overview is null, calculate fallback
    val safeOverview = overview ?: run {
        val unlocked = userMedals.filter { it.isUnlocked }
        val totalXp = unlocked.sumOf { it.xpReward }
        val lvl = 1 + (totalXp / 500)
        val curXp = totalXp % 500
        GamificationOverview(
            totalXp = totalXp,
            currentLevel = lvl,
            currentLevelTitle = when {
                lvl >= 20 -> "Ouro Master da Superação ⚡"
                lvl >= 10 -> "Mestre da Força ⚔️"
                lvl >= 5 -> "Gladiador de Aço 🛡️"
                else -> "Guerreiro em Evolução 🔥"
            },
            currentLevelXp = curXp,
            nextLevelXp = 500,
            levelProgressPercent = (curXp.toFloat() / 500f).coerceIn(0f, 1f),
            unlockedMedalsCount = unlocked.size,
            totalMedalsCount = userMedals.size,
            bronzeCount = unlocked.count { it.rarity.contains("Bronze", ignoreCase = true) },
            prataCount = unlocked.count { it.rarity.contains("Prata", ignoreCase = true) },
            ouroCount = unlocked.count { it.rarity.equals("Ouro", ignoreCase = true) },
            masterCount = unlocked.count { it.rarity.contains("Master", ignoreCase = true) || it.rarity.contains("Superação", ignoreCase = true) }
        )
    }

    var selectedCycleIndex by remember { mutableIntStateOf(0) }
    val cycleTabs = MedalCycleTab.entries

    var selectedRarityFilter by remember { mutableStateOf<String?>("TODAS") }
    var selectedStatusFilter by remember { mutableStateOf("TODAS") } // "TODAS", "CONQUISTADAS", "EM_PROGRESSO"
    var isGridView by remember { mutableStateOf(false) }

    // Filter logic
    val filteredMedals = userMedals.filter { medal ->
        val matchesCycle = when (cycleTabs[selectedCycleIndex]) {
            MedalCycleTab.SEMANAL -> medal.period.equals(GoalPeriod.SEMANAL.name, ignoreCase = true)
            MedalCycleTab.MENSAL -> medal.period.equals(GoalPeriod.MENSAL.name, ignoreCase = true)
            MedalCycleTab.ANUAL -> medal.period.equals(GoalPeriod.ANUAL.name, ignoreCase = true)
            MedalCycleTab.ESPECIAL -> medal.period.equals(GoalPeriod.ESPECIAL.name, ignoreCase = true) || medal.period.isEmpty()
            MedalCycleTab.TODOS -> true
        }

        val matchesRarity = when (selectedRarityFilter) {
            "BRONZE" -> medal.rarity.contains("Bronze", ignoreCase = true)
            "PRATA" -> medal.rarity.contains("Prata", ignoreCase = true)
            "OURO" -> medal.rarity.equals("Ouro", ignoreCase = true)
            "MASTER" -> medal.rarity.contains("Master", ignoreCase = true) || medal.rarity.contains("Superação", ignoreCase = true)
            else -> true
        }

        val matchesStatus = when (selectedStatusFilter) {
            "CONQUISTADAS" -> medal.isUnlocked
            "EM_PROGRESSO" -> !medal.isUnlocked
            else -> true
        }

        matchesCycle && matchesRarity && matchesStatus
    }

    // Cycle stats
    val cycleMedalsTotal = when (cycleTabs[selectedCycleIndex]) {
        MedalCycleTab.SEMANAL -> userMedals.filter { it.period.equals(GoalPeriod.SEMANAL.name, ignoreCase = true) }
        MedalCycleTab.MENSAL -> userMedals.filter { it.period.equals(GoalPeriod.MENSAL.name, ignoreCase = true) }
        MedalCycleTab.ANUAL -> userMedals.filter { it.period.equals(GoalPeriod.ANUAL.name, ignoreCase = true) }
        MedalCycleTab.ESPECIAL -> userMedals.filter { it.period.equals(GoalPeriod.ESPECIAL.name, ignoreCase = true) || it.period.isEmpty() }
        MedalCycleTab.TODOS -> userMedals
    }
    val cycleUnlockedCount = cycleMedalsTotal.count { it.isUnlocked }
    val cycleProgressFraction = if (cycleMedalsTotal.isNotEmpty()) {
        cycleUnlockedCount.toFloat() / cycleMedalsTotal.size.toFloat()
    } else 0f

    BentoCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("section_gamification_trophies")
    ) {
        // ==========================================
        // 1. HERO & LEVEL XP HEADER
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ScalableVectorMedalBadge(
                    rarity = MedalRarity.MASTER_SUPERAÇÃO,
                    isUnlocked = true,
                    size = 46.dp,
                    iconEmoji = "👑",
                    animated = true
                )

                Column {
                    Text(
                        text = "Galeria de Medalhas & Ciclos",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Bronze, Prata, Ouro & Master da Superação",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = LilacSoft
                        )
                    )
                }
            }

            // Unlocked counter chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(PurpleDarkSurface)
                    .border(1.dp, LilacAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "🏆",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${safeOverview.unlockedMedalsCount}/${safeOverview.totalMedalsCount}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = LilacAccent
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 2. LEVEL PROGRESSION & RARITY PILL COUNTERS
        // ==========================================
        Surface(
            color = PurpleDarkSurface,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, LilacAccent.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Nível ${safeOverview.currentLevel}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(LilacAccent.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = safeOverview.currentLevelTitle,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LilacAccent
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${safeOverview.totalXp} XP Total Acumulado",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldSuccess.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "+${safeOverview.nextLevelXp - safeOverview.currentLevelXp} XP para Nível ${safeOverview.currentLevel + 1}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { safeOverview.levelProgressPercent.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = LilacAccent,
                    trackColor = PurpleDarkest
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Metallic Rarity Summary Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RaritySummaryPill(
                        label = "Bronze",
                        count = safeOverview.bronzeCount,
                        rarity = MedalRarity.BRONZE,
                        isSelected = selectedRarityFilter == "BRONZE",
                        onClick = {
                            selectedRarityFilter = if (selectedRarityFilter == "BRONZE") "TODAS" else "BRONZE"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    RaritySummaryPill(
                        label = "Prata",
                        count = safeOverview.prataCount,
                        rarity = MedalRarity.PRATA,
                        isSelected = selectedRarityFilter == "PRATA",
                        onClick = {
                            selectedRarityFilter = if (selectedRarityFilter == "PRATA") "TODAS" else "PRATA"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    RaritySummaryPill(
                        label = "Ouro",
                        count = safeOverview.ouroCount,
                        rarity = MedalRarity.OURO,
                        isSelected = selectedRarityFilter == "OURO",
                        onClick = {
                            selectedRarityFilter = if (selectedRarityFilter == "OURO") "TODAS" else "OURO"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    RaritySummaryPill(
                        label = "Master",
                        count = safeOverview.masterCount,
                        rarity = MedalRarity.MASTER_SUPERAÇÃO,
                        isSelected = selectedRarityFilter == "MASTER",
                        onClick = {
                            selectedRarityFilter = if (selectedRarityFilter == "MASTER") "TODAS" else "MASTER"
                        },
                        modifier = Modifier.weight(1.1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 3. CYCLE ORGANIZATION TABS (Semanal, Mensal, Anual, Especial, Todos)
        // ==========================================
        ScrollableTabRow(
            selectedTabIndex = selectedCycleIndex,
            containerColor = Color.Transparent,
            contentColor = LilacAccent,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedCycleIndex]),
                    color = LilacAccent,
                    height = 3.dp
                )
            },
            divider = {}
        ) {
            cycleTabs.forEachIndexed { index, tab ->
                val isSelected = selectedCycleIndex == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedCycleIndex = index },
                    modifier = Modifier.testTag("tab_cycle_${tab.name.lowercase()}"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = tab.icon, fontSize = 13.sp)
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                color = if (isSelected) LilacAccent else TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ==========================================
        // 4. ACTIVE CYCLE SUMMARY BANNER & CONTROLS
        // ==========================================
        Surface(
            color = PurpleDeepCard,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, GlassBorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "${cycleTabs[selectedCycleIndex].icon} ${cycleTabs[selectedCycleIndex].subtitle}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "• $cycleUnlockedCount de ${cycleMedalsTotal.size} conquistadas",
                            fontSize = 10.sp,
                            color = LilacSoft
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { cycleProgressFraction },
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (cycleProgressFraction >= 1f) EmeraldSuccess else LilacAccent,
                        trackColor = PurpleDarkest
                    )
                }

                // Grid / List toggle
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(PurpleDarkSurface)
                        .border(1.dp, GlassBorderSubtle, CircleShape)
                        .clickable { isGridView = !isGridView }
                        .testTag("btn_toggle_medals_view_mode"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isGridView) Icons.Default.Reorder else Icons.Default.GridView,
                        contentDescription = "Alternar Visualização",
                        tint = LilacAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ==========================================
        // 5. STATUS FILTER CHIPS (Todas, Conquistadas, Em Progresso)
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChipPill(
                label = "Todas as Medalhas",
                isSelected = selectedStatusFilter == "TODAS" && selectedRarityFilter == "TODAS",
                onClick = {
                    selectedStatusFilter = "TODAS"
                    selectedRarityFilter = "TODAS"
                }
            )
            FilterChipPill(
                label = "🏆 Conquistadas (${safeOverview.unlockedMedalsCount})",
                isSelected = selectedStatusFilter == "CONQUISTADAS",
                onClick = {
                    selectedStatusFilter = if (selectedStatusFilter == "CONQUISTADAS") "TODAS" else "CONQUISTADAS"
                }
            )
            FilterChipPill(
                label = "⏳ Em Progresso (${safeOverview.totalMedalsCount - safeOverview.unlockedMedalsCount})",
                isSelected = selectedStatusFilter == "EM_PROGRESSO",
                onClick = {
                    selectedStatusFilter = if (selectedStatusFilter == "EM_PROGRESSO") "TODAS" else "EM_PROGRESSO"
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ==========================================
        // 6. SCALABLE VECTOR MEDALS GALLERY (List vs Grid)
        // ==========================================
        if (filteredMedals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🔍", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Nenhuma medalha encontrada para este filtro.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            AnimatedContent(
                targetState = isGridView,
                transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(200)) },
                label = "medals_view_switch"
            ) { targetIsGrid ->
                if (targetIsGrid) {
                    // --- COMPACT VECTOR GRID VIEW ---
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        maxItemsInEachRow = 3
                    ) {
                        filteredMedals.forEach { medal ->
                            ScalableMedalGridItem(
                                medal = medal,
                                onClick = { onMedalClick(medal) },
                                onCelebrate = { onCelebrateMedal(medal) }
                            )
                        }
                    }
                } else {
                    // --- DETAILED VECTOR LIST VIEW ---
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        filteredMedals.forEach { medal ->
                            ScalableMedalListItem(
                                medal = medal,
                                onClick = { onMedalClick(medal) },
                                onCelebrate = { onCelebrateMedal(medal) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Summary Pill Card for Metallic Rarity Tiers.
 */
@Composable
private fun RaritySummaryPill(
    label: String,
    count: Int,
    rarity: MedalRarity,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when (rarity) {
        MedalRarity.BRONZE -> MedalPalette.BronzeBase
        MedalRarity.PRATA -> MedalPalette.SilverLight
        MedalRarity.OURO -> MedalPalette.GoldBase
        MedalRarity.MASTER_SUPERAÇÃO -> MedalPalette.MasterBase
    }

    Surface(
        color = if (isSelected) borderColor.copy(alpha = 0.22f) else PurpleDeepCard,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) borderColor else borderColor.copy(alpha = 0.35f)
        ),
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("filter_rarity_${label.lowercase()}")
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScalableVectorMedalBadge(
                rarity = rarity,
                isUnlocked = true,
                size = 28.dp,
                iconEmoji = when (rarity) {
                    MedalRarity.BRONZE -> "🥉"
                    MedalRarity.PRATA -> "🥈"
                    MedalRarity.OURO -> "🥇"
                    MedalRarity.MASTER_SUPERAÇÃO -> "👑"
                },
                showShimmer = isSelected,
                animated = isSelected
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 9.sp,
                color = if (isSelected) TextPrimary else TextMuted,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
            Text(
                text = "$count",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = borderColor
            )
        }
    }
}

/**
 * Scalable Vector Medal List Item with progress tracking, cycle tag, and celebration launcher.
 */
@Composable
fun ScalableMedalListItem(
    medal: UserMedal,
    onClick: () -> Unit,
    onCelebrate: () -> Unit = {}
) {
    val rarityColor = when (medal.rarityEnum) {
        MedalRarity.BRONZE -> MedalPalette.BronzeBase
        MedalRarity.PRATA -> MedalPalette.SilverLight
        MedalRarity.OURO -> MedalPalette.GoldBase
        MedalRarity.MASTER_SUPERAÇÃO -> MedalPalette.MasterBase
    }

    val periodTag = when (medal.period) {
        GoalPeriod.SEMANAL.name -> "📅 Semanal"
        GoalPeriod.MENSAL.name -> "🗓️ Mensal"
        GoalPeriod.ANUAL.name -> "🏆 Anual"
        else -> "⚡ Especial"
    }

    val progressFraction = if (medal.progressMax > 0) {
        (medal.progressCurrent.toFloat() / medal.progressMax.toFloat()).coerceIn(0f, 1f)
    } else if (medal.isUnlocked) 1f else 0f

    Surface(
        color = if (medal.isUnlocked) PurpleDeepCard else PurpleDarkSurface.copy(alpha = 0.6f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            1.dp,
            if (medal.isUnlocked) rarityColor.copy(alpha = 0.55f) else GlassBorderSubtle
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("medal_item_${medal.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Scalable Vector Medallion
                    ScalableVectorMedalBadge(
                        rarity = medal.rarityEnum,
                        isUnlocked = medal.isUnlocked,
                        size = 52.dp,
                        iconEmoji = medal.iconEmoji,
                        showShimmer = medal.isUnlocked,
                        animated = medal.isUnlocked
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = medal.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (medal.isUnlocked) TextPrimary else TextSecondary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (medal.isUnlocked) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Conquistada",
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = medal.rarity,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = rarityColor
                            )
                            Text(text = "•", fontSize = 10.sp, color = TextMuted)
                            Text(
                                text = periodTag,
                                fontSize = 10.sp,
                                color = LilacSoft,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // XP Reward & Celebrate Trigger
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (medal.isUnlocked) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(LilacAccent.copy(alpha = 0.15f))
                                .border(1.dp, LilacAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable { onCelebrate() }
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎆", fontSize = 11.sp)
                        }
                    }

                    Surface(
                        color = PurpleDarkSurface,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, LilacAccent.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = "+${medal.xpReward} XP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = LilacAccent,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = medal.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (medal.isUnlocked) TextSecondary else TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar and numeric goal indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (medal.isUnlocked) EmeraldSuccess else rarityColor,
                    trackColor = PurpleDarkest
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (medal.progressMax > 0) "${medal.progressCurrent}/${medal.progressMax}" else if (medal.isUnlocked) "100%" else "0%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (medal.isUnlocked) EmeraldSuccess else TextMuted
                )
            }
        }
    }
}

/**
 * Compact Vector Grid Card for high-density medals showcase.
 */
@Composable
fun ScalableMedalGridItem(
    medal: UserMedal,
    onClick: () -> Unit,
    onCelebrate: () -> Unit = {}
) {
    val rarityColor = when (medal.rarityEnum) {
        MedalRarity.BRONZE -> MedalPalette.BronzeBase
        MedalRarity.PRATA -> MedalPalette.SilverLight
        MedalRarity.OURO -> MedalPalette.GoldBase
        MedalRarity.MASTER_SUPERAÇÃO -> MedalPalette.MasterBase
    }

    Surface(
        color = if (medal.isUnlocked) PurpleDeepCard else PurpleDarkSurface.copy(alpha = 0.6f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            if (medal.isUnlocked) rarityColor.copy(alpha = 0.5f) else GlassBorderSubtle
        ),
        modifier = Modifier
            .width(106.dp)
            .clickable(onClick = onClick)
            .testTag("medal_grid_item_${medal.id}")
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScalableVectorMedalBadge(
                rarity = medal.rarityEnum,
                isUnlocked = medal.isUnlocked,
                size = 54.dp,
                iconEmoji = medal.iconEmoji,
                showShimmer = medal.isUnlocked,
                animated = medal.isUnlocked
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = medal.title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (medal.isUnlocked) FontWeight.Bold else FontWeight.Normal,
                    color = if (medal.isUnlocked) TextPrimary else TextMuted,
                    fontSize = 10.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "+${medal.xpReward} XP",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = if (medal.isUnlocked) LilacAccent else TextMuted
            )
        }
    }
}

/**
 * Filter Chip Pill for cycle and status filtering.
 */
@Composable
private fun FilterChipPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) LilacAccent.copy(alpha = 0.2f) else PurpleDarkSurface)
            .border(
                1.dp,
                if (isSelected) LilacAccent else GlassBorderSubtle,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
            color = if (isSelected) LilacAccent else TextSecondary
        )
    }
}
