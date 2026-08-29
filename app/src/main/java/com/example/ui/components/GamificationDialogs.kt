package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.GoalPeriod
import com.example.data.model.MedalRarity
import com.example.data.model.PersonalRecordCelebration
import com.example.data.model.UserMedal
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassSurfaceDeep
import com.example.ui.theme.GlowPurple
import com.example.ui.theme.GradientAction
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
import com.example.utils.SoundEffectManager

/**
 * Celebration modal when a Personal Record (PR) is beaten!
 */
@Composable
fun PersonalRecordCelebrationDialog(
    celebration: PersonalRecordCelebration,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var confettiKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(celebration) {
        SoundEffectManager.playCelebrationFanfare()
        SoundEffectManager.vibrateCelebration(context)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pr_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pr_scale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(26.dp)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Brush.linearGradient(listOf(YellowWarning, LilacAccent)), RoundedCornerShape(26.dp)),
                color = PurpleDarkest
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Scalable Vector Medallion with pulsing aura
                    Box(
                        modifier = Modifier.scale(scale),
                        contentAlignment = Alignment.Center
                    ) {
                        ScalableVectorMedalBadge(
                            rarity = MedalRarity.OURO,
                            isUnlocked = true,
                            size = 96.dp,
                            iconEmoji = "🏆",
                            animated = true
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "⚡ NOVO RECORDE PESSOAL (PR)!",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = YellowWarning,
                            letterSpacing = 1.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = celebration.exerciseName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats comparison card
                    Surface(
                        color = PurpleDeepCard,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, GlassBorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Carga Anterior", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${celebration.previousWeightKg.toInt()} kg",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                            }

                            Text(text = "➔", style = MaterialTheme.typography.titleLarge, color = LilacAccent)

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Nova Carga Máxima", style = MaterialTheme.typography.labelSmall, color = YellowWarning)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${celebration.newWeightKg.toInt()} kg",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = YellowWarning
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Parabéns! Você aplicou sobrecarga progressiva com sucesso e quebrou seu limite!",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PrimaryButton(
                        text = "Continuar Treino 💪",
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().testTag("btn_dismiss_pr_celebration")
                    )
                }
            }

            // Confetti explosion overlay
            key(confettiKey) {
                AchievementConfettiCelebration(
                    modifier = Modifier.fillMaxSize(),
                    particleCount = 50,
                    primaryColor = YellowWarning
                )
            }
        }
    }
}

/**
 * Celebration modal when a gamification medal is unlocked or replayed.
 * Uses Scalable Vector Medallions, Confetti Explosions, and Sound/Haptic feedback.
 */
@Composable
fun MedalUnlockedDialog(
    medal: UserMedal,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var confettiKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(medal) {
        SoundEffectManager.playCelebrationFanfare()
        SoundEffectManager.vibrateCelebration(context)
    }

    val rarityColor = when (medal.rarityEnum) {
        MedalRarity.BRONZE -> MedalPalette.BronzeBase
        MedalRarity.PRATA -> MedalPalette.SilverLight
        MedalRarity.OURO -> MedalPalette.GoldBase
        MedalRarity.MASTER_SUPERAÇÃO -> MedalPalette.MasterBase
    }

    val cycleLabel = when (medal.period) {
        GoalPeriod.SEMANAL.name -> "📅 Ciclo Semanal"
        GoalPeriod.MENSAL.name -> "🗓️ Ciclo Mensal"
        GoalPeriod.ANUAL.name -> "🏆 Ciclo Anual"
        else -> "⚡ Marco Especial"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .clip(RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, rarityColor, RoundedCornerShape(28.dp)),
                color = PurpleDarkest
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Scalable Vector Medallion with Hero Dimensions
                    ScalableVectorMedalBadge(
                        rarity = medal.rarityEnum,
                        isUnlocked = true,
                        size = 110.dp,
                        iconEmoji = medal.iconEmoji,
                        showShimmer = true,
                        animated = true
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "🏆 CONQUISTA DESBLOQUEADA!",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = rarityColor,
                            letterSpacing = 1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = medal.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = medal.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            lineHeight = 20.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cycle & XP Badges Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = PurpleDeepCard,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, rarityColor.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = medal.rarity,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = rarityColor
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            color = PurpleDeepCard,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, LilacAccent.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = cycleLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LilacAccent
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            color = PurpleDeepCard,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "+${medal.xpReward} XP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldSuccess
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                confettiKey++
                                SoundEffectManager.playCelebrationFanfare()
                                SoundEffectManager.vibrateCelebration(context)
                            },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, LilacAccent.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = LilacAccent),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Replay, contentDescription = "Replay", modifier = Modifier.size(16.dp))
                                Text("Replay 🎆", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        PrimaryButton(
                            text = "Sensacional! 🚀",
                            onClick = onDismiss,
                            modifier = Modifier.weight(1.4f).testTag("btn_dismiss_medal_dialog")
                        )
                    }
                }
            }

            // Confetti Explosion Overlay
            key(confettiKey) {
                AchievementConfettiCelebration(
                    modifier = Modifier.fillMaxSize(),
                    particleCount = 55,
                    primaryColor = rarityColor
                )
            }
        }
    }
}

/**
 * Detailed Information Modal when clicking any medal (locked or unlocked).
 * Allows user to see vector badge in high-res and replay achievement animation if unlocked.
 */
@Composable
fun MedalDetailModalDialog(
    medal: UserMedal,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var confettiKey by remember { mutableIntStateOf(0) }

    val rarityColor = when (medal.rarityEnum) {
        MedalRarity.BRONZE -> MedalPalette.BronzeBase
        MedalRarity.PRATA -> MedalPalette.SilverLight
        MedalRarity.OURO -> MedalPalette.GoldBase
        MedalRarity.MASTER_SUPERAÇÃO -> MedalPalette.MasterBase
    }

    val progressFraction = if (medal.progressMax > 0) {
        (medal.progressCurrent.toFloat() / medal.progressMax.toFloat()).coerceIn(0f, 1f)
    } else if (medal.isUnlocked) 1f else 0f

    val cycleLabel = when (medal.period) {
        GoalPeriod.SEMANAL.name -> "📅 Ciclo Semanal"
        GoalPeriod.MENSAL.name -> "🗓️ Ciclo Mensal"
        GoalPeriod.ANUAL.name -> "🏆 Ciclo Anual"
        else -> "⚡ Marco Especial"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(26.dp)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, if (medal.isUnlocked) rarityColor else GlassBorderSubtle, RoundedCornerShape(26.dp)),
                color = PurpleDarkest
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Scalable Vector Medallion (Large)
                    ScalableVectorMedalBadge(
                        rarity = medal.rarityEnum,
                        isUnlocked = medal.isUnlocked,
                        size = 88.dp,
                        iconEmoji = medal.iconEmoji,
                        showShimmer = medal.isUnlocked,
                        animated = medal.isUnlocked
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = PurpleDeepCard,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, rarityColor.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = medal.rarity,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = rarityColor
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            color = PurpleDeepCard,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, LilacAccent.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = cycleLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LilacAccent
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            color = PurpleDeepCard,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "+${medal.xpReward} XP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldSuccess
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = medal.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = medal.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            lineHeight = 18.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress Bar Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = PurpleDeepCard,
                        border = BorderStroke(1.dp, GlassBorderSubtle)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (medal.isUnlocked) "Status: Conquistada ✓" else "Progresso da Meta:",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (medal.isUnlocked) EmeraldSuccess else TextSecondary
                                    )
                                )
                                Text(
                                    text = if (medal.progressMax > 0) "${medal.progressCurrent} / ${medal.progressMax}" else if (medal.isUnlocked) "100%" else "0%",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = if (medal.isUnlocked) EmeraldSuccess else LilacAccent
                                    )
                                )
                            }

                            LinearProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (medal.isUnlocked) EmeraldSuccess else rarityColor,
                                trackColor = PurpleDarkest
                            )

                            Text(
                                text = if (medal.isUnlocked) {
                                    val epoch = medal.unlockedDateEpochDay
                                    if (epoch != null) {
                                        val date = java.time.LocalDate.ofEpochDay(epoch)
                                        "Desbloqueada com sucesso em ${date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}"
                                    } else {
                                        "Desbloqueada com sucesso!"
                                    }
                                } else {
                                    "Faltam ${(medal.progressMax - medal.progressCurrent).coerceAtLeast(1)} unidades para a conquista!"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (medal.isUnlocked) {
                            OutlinedButton(
                                onClick = {
                                    confettiKey++
                                    SoundEffectManager.playCelebrationFanfare()
                                    SoundEffectManager.vibrateCelebration(context)
                                },
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, LilacAccent.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = LilacAccent),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "Animar", modifier = Modifier.size(16.dp))
                                    Text("Celebrar 🎆", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        PrimaryButton(
                            text = "Fechar",
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).testTag("btn_close_medal_detail")
                        )
                    }
                }
            }

            // Confetti particles if triggered
            if (confettiKey > 0) {
                key(confettiKey) {
                    AchievementConfettiCelebration(
                        modifier = Modifier.fillMaxSize(),
                        particleCount = 50,
                        primaryColor = rarityColor
                    )
                }
            }
        }
    }
}
