package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MuscleGroup
import com.example.data.model.WorkoutSession
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSubtle
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
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YellowWarning

data class MuscleWeeklyVolume(
    val muscleGroup: MuscleGroup,
    val completedSetsThisWeek: Int,
    val targetMinSets: Int = 10,
    val targetMaxSets: Int = 20
) {
    val progressFraction: Float
        get() = (completedSetsThisWeek.toFloat() / targetMaxSets.toFloat()).coerceIn(0f, 1f)

    val volumeStatus: VolumeStatus
        get() = when {
            completedSetsThisWeek == 0 -> VolumeStatus.NENHUM
            completedSetsThisWeek < targetMinSets -> VolumeStatus.MANUTENCAO
            completedSetsThisWeek <= targetMaxSets -> VolumeStatus.IDEAL_HIPERTROFIA
            else -> VolumeStatus.ALTO_VOLUME
        }
}

enum class VolumeStatus(val label: String, val color: Color, val tagColor: Color) {
    NENHUM("Sem estímulo", TextMuted, PurpleDarkSurface),
    MANUTENCAO("Volume Mínimo", YellowWarning, YellowWarning.copy(alpha = 0.15f)),
    IDEAL_HIPERTROFIA("Hipertrofia Ideal (10-20s)", EmeraldSuccess, EmeraldDark.copy(alpha = 0.35f)),
    ALTO_VOLUME("Alto Volume / MRV", LilacAccent, PurpleDeepCard)
}

/**
 * Mapa de Volume Muscular Semanal (Weekly Muscle Volume Heatmap)
 * Analisa o número de séries diretas por grupo muscular na semana atual e compara
 * com a literatura científica de hipertrofia (10 a 20 séries/semana).
 */
@Composable
fun MuscleVolumeHeatmapSection(
    weeklyVolumes: List<MuscleWeeklyVolume>,
    modifier: Modifier = Modifier
) {
    var showScienceInfo by remember { mutableStateOf(false) }

    val totalWeeklySets = weeklyVolumes.sumOf { it.completedSetsThisWeek }
    val musclesInOptimalRange = weeklyVolumes.count { it.volumeStatus == VolumeStatus.IDEAL_HIPERTROFIA }

    BentoCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_muscle_volume_heatmap")
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
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GradientAction),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "VOLUME MUSCULAR SEMANAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LilacAccent,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Mapa de Séries por Grupo Muscular",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PurpleDarkSurface)
                    .clickable { showScienceInfo = !showScienceInfo },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Informações Científicas",
                    tint = if (showScienceInfo) LilacAccent else TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        if (showScienceInfo) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PurpleDarkSurface)
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "📚 Faixas de Volume por Semana (Schoenfeld / Israetel):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LilacAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• <10 séries: Volume de Manutenção (MEV)\n• 10-20 séries: Faixa Ótima de Hipertrofia (MAV)\n• >20 séries: Volume Máximo Recuperável (MRV)",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Highlights row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = PurpleDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "TOTAL DE SÉRIES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(
                        text = "$totalWeeklySets séries",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = PurpleDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "GRUPOS NO ALVO IDEAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(
                        text = "$musclesInOptimalRange / ${weeklyVolumes.size}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = EmeraldSuccess
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Muscle list breakdown bars
        weeklyVolumes.forEach { item ->
            val animatedProgress by animateFloatAsState(
                targetValue = item.progressFraction,
                label = "vol_prog_${item.muscleGroup.name}"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.muscleGroup.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${item.completedSetsThisWeek} / ${item.targetMaxSets} séries",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = item.volumeStatus.color
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(item.volumeStatus.tagColor)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.volumeStatus.label,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = item.volumeStatus.color
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(PurpleDarkSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        item.volumeStatus.color.copy(alpha = 0.7f),
                                        item.volumeStatus.color
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}
