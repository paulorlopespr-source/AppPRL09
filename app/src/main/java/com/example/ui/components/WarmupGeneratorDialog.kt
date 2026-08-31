package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ExerciseSetEntry
import com.example.data.model.SetTag
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GradientAction
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YellowWarning

data class WarmupStepProposal(
    val title: String,
    val percentageLabel: String,
    val weightKg: Double,
    val reps: Int,
    val tag: SetTag,
    val purpose: String
)

/**
 * Diálogo para Geração Científica de Séries de Aquecimento & Preparação (Warm-up Sets)
 * Prepara o sistema nervoso central (SNC) e a circulação sinovial sem acumular fadiga metabólica.
 */
@Composable
fun WarmupGeneratorDialog(
    exerciseName: String,
    workingWeightKg: Double,
    workingReps: Int = 10,
    workingSetsCount: Int = 3,
    onApplyWarmupSets: (List<ExerciseSetEntry>) -> Unit,
    onDismiss: () -> Unit
) {
    val roundToNearest2_5 = { w: Double ->
        (Math.round(w / 2.5) * 2.5).coerceAtLeast(0.0)
    }

    val warmupSteps = remember(workingWeightKg, workingReps) {
        val w50 = roundToNearest2_5(workingWeightKg * 0.50)
        val w70 = roundToNearest2_5(workingWeightKg * 0.70)
        val w85 = roundToNearest2_5(workingWeightKg * 0.85)

        listOf(
            WarmupStepProposal(
                title = "Aquecimento Geral",
                percentageLabel = "50% da carga",
                weightKg = w50,
                reps = 10,
                tag = SetTag.WARMUP,
                purpose = "Irrigação sanguínea e lubrificação articular"
            ),
            WarmupStepProposal(
                title = "Aquecimento Específico",
                percentageLabel = "70% da carga",
                weightKg = w70,
                reps = 5,
                tag = SetTag.WARMUP,
                purpose = "Ativação de unidades motoras e padrão de movimento"
            ),
            WarmupStepProposal(
                title = "Série Feeder (Preparatória)",
                percentageLabel = "85% da carga",
                weightKg = w85,
                reps = 2,
                tag = SetTag.FEEDER,
                purpose = "Aclimatação neural do SNC sem fadiga residual"
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = PurpleDarkest,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, GlassBorder),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GradientAction),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Aquecimento Científico",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = exerciseName,
                                style = MaterialTheme.typography.bodySmall,
                                color = LilacSoft
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Working Weight Card info
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PurpleDarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CARGA DE TRABALHO DEFINIDA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = LilacAccent,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "${workingWeightKg.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }} kg • $workingReps repetições",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PurpleDeepCard)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "$workingSetsCount séries normais",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "PROGRESSÃO SUGERIDA:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Warmup steps list
                warmupSteps.forEachIndexed { index, step ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PurpleDarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(step.tag.badgeColorHex).copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(step.tag.badgeColorHex)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = step.tag.code,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = step.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "(${step.percentageLabel})",
                                            fontSize = 11.sp,
                                            color = LilacSoft
                                        )
                                    }
                                    Text(
                                        text = step.purpose,
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${step.weightKg.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }} kg",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldSuccess
                                )
                                Text(
                                    text = "${step.reps} reps",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar", color = TextMuted, fontWeight = FontWeight.Bold)
                    }

                    PrimaryButton(
                        text = "Inserir Aquecimentos",
                        icon = Icons.Default.Check,
                        onClick = {
                            val combinedSets = mutableListOf<ExerciseSetEntry>()
                            var setCounter = 1
                            warmupSteps.forEach { step ->
                                combinedSets.add(
                                    ExerciseSetEntry(
                                        setNumber = setCounter++,
                                        weightKg = step.weightKg,
                                        reps = step.reps,
                                        isCompleted = false,
                                        restSeconds = if (step.tag == SetTag.FEEDER) 60 else 45,
                                        setTag = step.tag
                                    )
                                )
                            }
                            repeat(workingSetsCount) {
                                combinedSets.add(
                                    ExerciseSetEntry(
                                        setNumber = setCounter++,
                                        weightKg = workingWeightKg,
                                        reps = workingReps,
                                        isCompleted = false,
                                        restSeconds = 90,
                                        setTag = SetTag.NORMAL
                                    )
                                )
                            }
                            onApplyWarmupSets(combinedSets)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.8f)
                    )
                }
            }
        }
    }
}
