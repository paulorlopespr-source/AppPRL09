package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExerciseExecutionRecord
import com.example.data.model.ProgressionSuggestion
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

// ==========================================
// 1. AUTOMATIC LAST EXECUTION DISPLAY CARD
// ==========================================

@Composable
fun AutomaticLastExecutionCard(
    lastExecution: ExerciseExecutionRecord?,
    onViewFullHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = PurpleDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(LilacAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = LilacAccent,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ÚLTIMA EXECUÇÃO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = LilacAccent,
                        letterSpacing = 0.8.sp
                    )
                }

                if (lastExecution != null) {
                    Text(
                        text = "Ver histórico",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LilacSoft,
                        modifier = Modifier.clickable { onViewFullHistory() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (lastExecution != null && lastExecution.completedSets.isNotEmpty()) {
                val weightStr = lastExecution.primaryWeightKg.let {
                    if (it % 1.0 == 0.0) "${it.toInt()} kg" else "$it kg"
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = weightStr,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )

                    Text(
                        text = DateUtils.formatEpochDay(lastExecution.sessionDateEpochDay),
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Sets detail pill row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    lastExecution.completedSets.take(4).forEach { set ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PurpleDeepCard)
                                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Série ${set.setNumber} — ${set.reps} reps",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = LilacSoft
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "Primeiro registro deste exercício. Suas cargas e repetições serão lembradas automaticamente!",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

// ==========================================
// 2. PROGRESSIVE OVERLOAD ALERT BANNER
// ==========================================

@Composable
fun ProgressiveOverloadAlertBanner(
    suggestion: ProgressionSuggestion,
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LiquidGlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("banner_progressive_overload"),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = GlassSurfaceDeep,
        borderColor = LilacAccent.copy(alpha = 0.6f),
        borderWidth = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(GradientAction),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✦ PROGRESSÃO DETECTADA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = LilacAccent,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(EmeraldSuccess.copy(alpha = 0.18f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "+${suggestion.weightDeltaKg.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }} kg",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldSuccess
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Você atingiu sua meta nas últimas sessões.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Sugestão: ",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Text(
                    text = "Próximo treino: ${suggestion.suggestedWeightKg.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }} kg",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row: [USAR SUGESTÃO] & [MANTER XX KG]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Confirm Overload Button
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("btn_accept_progression")
                ) {
                    Text(
                        text = "USAR SUGESTÃO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Keep current load button
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextSecondary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("btn_keep_current_weight")
                ) {
                    val currentWeightStr = suggestion.currentWeightKg.let {
                        if (it % 1.0 == 0.0) it.toInt().toString() else it.toString()
                    }
                    Text(
                        text = "MANTER $currentWeightStr KG",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

// ==========================================
// 3. FULL EXERCISE HISTORY DIALOG
// ==========================================

@Composable
fun FullExerciseHistoryDialog(
    exerciseName: String,
    records: List<ExerciseExecutionRecord>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = LilacAccent, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Evolução do Exercício",
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = exerciseName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LilacSoft
                )
            }
        },
        text = {
            if (records.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhum histórico anterior registrado para este exercício.", color = TextMuted, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(records) { record ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PurpleDeepCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = DateUtils.formatEpochDay(record.sessionDateEpochDay),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${record.primaryWeightKg.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }} kg",
                                        fontWeight = FontWeight.Black,
                                        color = LilacAccent
                                    )
                                }
                                Text(
                                    text = "${record.workoutTitle} • ${record.location}",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                // Sets list
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    record.completedSets.forEach { set ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(PurpleDarkSurface)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "S${set.setNumber}: ${set.reps}x ${set.weightKg.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }}kg",
                                                fontSize = 10.sp,
                                                color = LilacSoft,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                if (record.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Obs: ${record.notes}",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar", color = LilacAccent, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = PurpleDarkSurface,
        shape = RoundedCornerShape(22.dp)
    )
}
