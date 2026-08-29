package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlowPurple
import com.example.ui.theme.GradientAction
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.RedDestructive
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.utils.SoundEffectManager

fun triggerVibration(context: Context) {
    SoundEffectManager.vibrate(context, 400)
}

/**
 * Cronômetro de Descanso (Rest Timer) com visual Liquid Glass & Dark Mode Premium
 * com anel circular de contagem regressiva, presets (30s, 45s, 60s, 90s, 120s),
 * controles rápidos (+/- 15s, pausar, pular) e alertas sonoros em 5s e finalização.
 */
@Composable
fun RestTimerOverlay(
    isVisible: Boolean,
    remainingSeconds: Int,
    totalSeconds: Int,
    isPaused: Boolean,
    onPauseResume: () -> Unit,
    onAddSeconds: (Int) -> Unit,
    onSelectPreset: ((Int) -> Unit)? = null,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Trigger audio alerts and haptic vibration for 5s countdown and completion
    LaunchedEffect(remainingSeconds, isVisible, isPaused) {
        if (isVisible && !isPaused) {
            when (remainingSeconds) {
                5, 4, 3, 2, 1 -> {
                    SoundEffectManager.playCountdownBeep(remainingSeconds)
                    SoundEffectManager.vibrate(context, 80)
                }
                0 -> {
                    SoundEffectManager.playRestTimerFinished()
                    triggerVibration(context)
                }
            }
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        val progress by animateFloatAsState(
            targetValue = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds else 0f,
            label = "timer_progress"
        )

        val ringColor by animateColorAsState(
            targetValue = if (isPaused) AmberWarning else LilacAccent,
            label = "timer_ring_color"
        )

        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = GlowPurple,
                    spotColor = PurpleVibrant
                )
                .testTag("rest_timer_overlay")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlassSurfaceDark)
                    .border(1.2.dp, GlassBorder, RoundedCornerShape(28.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PurpleDeepCard)
                                    .border(1.dp, LilacAccent.copy(alpha = 0.4f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = ringColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Descanso Entre Séries",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isPaused) "⏸️ Pausado" else "⏳ Recuperando Energia",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ringColor
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PurpleDarkSurface)
                                .clickable { onDismiss() }
                                .testTag("btn_close_timer"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar timer",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Circular Countdown Ring Display
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(140.dp)
                    ) {
                        // Track Background Ring
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.size(140.dp),
                            color = PurpleDarkest,
                            strokeWidth = 10.dp,
                        )
                        // Dynamic Progress Ring
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(140.dp),
                            color = ringColor,
                            strokeWidth = 10.dp,
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = DateUtils.formatSecondsToTime(remainingSeconds),
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = if (isPaused) "PAUSADO" else "DESCANSO",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = ringColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Presets Row (30s, 45s, 60s, 90s, 120s)
                    if (onSelectPreset != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(30, 45, 60, 90, 120).forEach { presetSeconds ->
                                val isSelected = totalSeconds == presetSeconds && !isPaused
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) PurpleDeepCard else PurpleDarkSurface)
                                        .border(
                                            1.dp,
                                            if (isSelected) LilacAccent else GlassBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { onSelectPreset(presetSeconds) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${presetSeconds}s",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) LilacAccent else TextSecondary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Controls: -15s, Play/Pause, +15s, Pular
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // -15s
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PurpleDarkSurface)
                                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                                .clickable { onAddSeconds(-15) }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("btn_timer_sub_15"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Remove, contentDescription = "-15s", tint = TextPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("15s", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                            }
                        }

                        // Play/Pause Main FAB
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(GradientAction)
                                .border(1.dp, LilacSoft.copy(alpha = 0.5f), CircleShape)
                                .clickable { onPauseResume() }
                                .testTag("btn_timer_pause_resume"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = if (isPaused) "Iniciar / Retomar" else "Pausar",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        // +15s
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PurpleDarkSurface)
                                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                                .clickable { onAddSeconds(15) }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("btn_timer_add_15"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = "+15s", tint = TextPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("15s", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                            }
                        }

                        // Pular
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PurpleDarkSurface)
                                .border(1.dp, RedDestructive.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .clickable { onDismiss() }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("btn_timer_skip"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Pular", fontWeight = FontWeight.Bold, color = RedDestructive, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
