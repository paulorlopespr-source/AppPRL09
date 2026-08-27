package com.example.ui.components

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RedDestructive
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.RoyalBlueSubtle
import kotlinx.coroutines.delay

/**
 * Helper to sound acoustic feedback & vibration notifications for workout intervals.
 */
object WorkoutTimerFeedbackHelper {

    fun playBeepTone(isHighPitch: Boolean = false) {
        try {
            val toneType = if (isHighPitch) ToneGenerator.TONE_PROP_BEEP2 else ToneGenerator.TONE_PROP_BEEP
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
            toneGen.startTone(toneType, 180)
        } catch (_: Exception) {}
    }

    fun playCompletionFanfare() {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 95)
            toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 400)
        } catch (_: Exception) {}
    }

    fun vibrateIntervalTransition(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                val effect = VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 250), -1)
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 150, 100, 250), -1)
            }
        } catch (_: Exception) {}
    }

    fun vibrateCountdownWarning(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(70, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(70)
            }
        } catch (_: Exception) {}
    }
}

/**
 * Reusable, high-performance Interval & Rest Timer Composable for workout sessions.
 * Supports customizable Work intervals, Rest intervals, Rounds, visual circular feedback,
 * and multi-sensory completion notifications (Haptic vibration + Audio tone).
 */
@Composable
fun WorkoutIntervalTimer(
    modifier: Modifier = Modifier,
    initialWorkSeconds: Int = 45,
    initialRestSeconds: Int = 60,
    initialRounds: Int = 4,
    title: String = "Temporizador de Intervalos (HIIT / Séries)",
    onIntervalCompleted: ((round: Int, isWork: Boolean) -> Unit)? = null,
    onWorkoutCompleted: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
) {
    val context = LocalContext.current

    var workDuration by remember { mutableIntStateOf(initialWorkSeconds) }
    var restDuration by remember { mutableIntStateOf(initialRestSeconds) }
    var totalRounds by remember { mutableIntStateOf(initialRounds) }

    var currentRound by remember { mutableIntStateOf(1) }
    var isWorkPhase by remember { mutableStateOf(true) }
    var remainingSeconds by remember { mutableIntStateOf(initialWorkSeconds) }
    var isRunning by remember { mutableStateOf(false) }
    var isSessionComplete by remember { mutableStateOf(false) }
    var soundEnabled by remember { mutableStateOf(true) }
    var isCustomizing by remember { mutableStateOf(false) }

    val currentTotalPhaseSeconds = if (isWorkPhase) workDuration else restDuration

    // Timer coroutine loop
    LaunchedEffect(isRunning, remainingSeconds, isWorkPhase, currentRound, totalRounds) {
        if (isRunning && !isSessionComplete) {
            if (remainingSeconds > 0) {
                delay(1000L)
                val nextSec = remainingSeconds - 1
                remainingSeconds = nextSec

                // 3-2-1 Audio & Haptic warnings
                if (nextSec in 1..3 && soundEnabled) {
                    WorkoutTimerFeedbackHelper.playBeepTone(false)
                    WorkoutTimerFeedbackHelper.vibrateCountdownWarning(context)
                }
            } else {
                // Phase completed!
                if (soundEnabled) {
                    WorkoutTimerFeedbackHelper.playBeepTone(true)
                    WorkoutTimerFeedbackHelper.vibrateIntervalTransition(context)
                }
                onIntervalCompleted?.invoke(currentRound, isWorkPhase)

                if (isWorkPhase) {
                    // Transition to Rest
                    if (restDuration > 0) {
                        isWorkPhase = false
                        remainingSeconds = restDuration
                    } else {
                        // If 0 rest, advance directly
                        if (currentRound < totalRounds) {
                            currentRound += 1
                            isWorkPhase = true
                            remainingSeconds = workDuration
                        } else {
                            isSessionComplete = true
                            isRunning = false
                            if (soundEnabled) WorkoutTimerFeedbackHelper.playCompletionFanfare()
                            onWorkoutCompleted?.invoke()
                        }
                    }
                } else {
                    // Rest finished -> Advance round or finish
                    if (currentRound < totalRounds) {
                        currentRound += 1
                        isWorkPhase = true
                        remainingSeconds = workDuration
                    } else {
                        isSessionComplete = true
                        isRunning = false
                        if (soundEnabled) WorkoutTimerFeedbackHelper.playCompletionFanfare()
                        onWorkoutCompleted?.invoke()
                    }
                }
            }
        }
    }

    // Colors & Animations
    val phaseColor by animateColorAsState(
        targetValue = when {
            isSessionComplete -> EmeraldSuccess
            !isRunning -> MaterialTheme.colorScheme.primary
            isWorkPhase -> AmberWarning
            else -> RoyalBlue
        },
        label = "phase_color"
    )

    val progressValue by animateFloatAsState(
        targetValue = if (currentTotalPhaseSeconds > 0) {
            remainingSeconds.toFloat() / currentTotalPhaseSeconds.toFloat()
        } else 0f,
        label = "interval_progress"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, phaseColor.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .testTag("workout_interval_timer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = phaseColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isWorkPhase) Icons.Default.FitnessCenter else Icons.Default.SelfImprovement,
                                contentDescription = null,
                                tint = phaseColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isSessionComplete) "🎉 Treino Concluído!"
                            else if (isWorkPhase) "🔥 Intervalo de Trabalho (Esforço)"
                            else "💧 Intervalo de Recuperação (Descanso)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = phaseColor
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { soundEnabled = !soundEnabled },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = "Alertas Sonoros",
                            tint = if (soundEnabled) phaseColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    if (onDismiss != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar temporizador",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Central Circular Progress & Digital Counter
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(150.dp)
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(150.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 10.dp
                )
                CircularProgressIndicator(
                    progress = { progressValue },
                    modifier = Modifier.size(150.dp),
                    color = phaseColor,
                    strokeWidth = 10.dp
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isSessionComplete) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(42.dp)
                        )
                        Text(
                            text = "CONCLUÍDO",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess
                        )
                    } else {
                        val minutes = remainingSeconds / 60
                        val secs = remainingSeconds % 60
                        Text(
                            text = String.format("%02d:%02d", minutes, secs),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = phaseColor.copy(alpha = 0.15f),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = if (isWorkPhase) "TRABALHO" else "DESCANSO",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = phaseColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Rounds / Sets Progress Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Série / Round: ",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$currentRound de $totalRounds",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Black,
                    color = phaseColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Control Buttons (Start / Pause, Skip, Reset)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Subtract 5s
                FilledTonalButton(
                    onClick = { remainingSeconds = (remainingSeconds - 5).coerceAtLeast(0) },
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSessionComplete
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "-5s", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("5s", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Play / Pause / Restart
                IconButton(
                    onClick = {
                        if (isSessionComplete) {
                            currentRound = 1
                            isWorkPhase = true
                            remainingSeconds = workDuration
                            isSessionComplete = false
                            isRunning = true
                        } else {
                            isRunning = !isRunning
                        }
                    },
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(phaseColor)
                        .testTag("btn_timer_toggle")
                ) {
                    Icon(
                        imageVector = when {
                            isSessionComplete -> Icons.Default.Refresh
                            isRunning -> Icons.Default.Pause
                            else -> Icons.Default.PlayArrow
                        },
                        contentDescription = "Controle de timer",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Add 5s
                FilledTonalButton(
                    onClick = { remainingSeconds += 5 },
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSessionComplete
                ) {
                    Icon(Icons.Default.Add, contentDescription = "+5s", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("5s", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Skip to Next Phase
                FilledTonalIconButton(
                    onClick = {
                        remainingSeconds = 0
                    },
                    enabled = isRunning && !isSessionComplete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.FastForward, contentDescription = "Pular Fase", modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expandable Interval Customization Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { isCustomizing = !isCustomizing }) {
                    Text(
                        text = if (isCustomizing) "Ocultar Ajustes de Intervalo" else "⚙️ Ajustar Trabalho & Descanso",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                FilledTonalButton(
                    onClick = {
                        isRunning = false
                        currentRound = 1
                        isWorkPhase = true
                        remainingSeconds = workDuration
                        isSessionComplete = false
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reiniciar", fontSize = 11.sp)
                }
            }

            AnimatedVisibility(visible = isCustomizing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Work Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Trabalho:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(20, 30, 45, 60, 90).forEach { sec ->
                                FilterChip(
                                    selected = workDuration == sec,
                                    onClick = {
                                        workDuration = sec
                                        if (isWorkPhase && !isRunning) remainingSeconds = sec
                                    },
                                    label = { Text("${sec}s", fontSize = 10.sp) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }

                    // Rest Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Descanso:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(15, 30, 60, 90, 120).forEach { sec ->
                                FilterChip(
                                    selected = restDuration == sec,
                                    onClick = {
                                        restDuration = sec
                                        if (!isWorkPhase && !isRunning) remainingSeconds = sec
                                    },
                                    label = { Text("${sec}s", fontSize = 10.sp) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }

                    // Rounds Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rounds:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(3, 4, 5, 8, 10).forEach { r ->
                                FilterChip(
                                    selected = totalRounds == r,
                                    onClick = { totalRounds = r },
                                    label = { Text("$r", fontSize = 10.sp) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
