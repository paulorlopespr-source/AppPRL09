package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OutdoorCardioPersistence
import com.example.data.model.CardioSession
import com.example.data.model.CardioType
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OutdoorActivityHistoryCard(
    cardio: CardioSession,
    onViewAi: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedSplits by remember { mutableStateOf(false) }

    val routePoints = remember(cardio.routePointsJson) {
        OutdoorCardioPersistence.parseRoutePoints(cardio.routePointsJson)
    }
    val splits = remember(cardio.splitsJson) {
        OutdoorCardioPersistence.parseSplits(cardio.splitsJson)
    }

    val timeFormatted = remember(cardio.timestampMillis) {
        val sdf = SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale("pt", "BR"))
        sdf.format(Date(cardio.timestampMillis))
    }

    val paceDisplay = remember(cardio.avgPaceMinKm, cardio.distanceKm, cardio.durationMinutes) {
        if (cardio.avgPaceMinKm != "--:--" && cardio.avgPaceMinKm.isNotBlank()) {
            "${cardio.avgPaceMinKm}/km"
        } else if (cardio.distanceKm != null && cardio.distanceKm > 0.05) {
            val paceSec = ((cardio.durationMinutes * 60) / cardio.distanceKm).toInt()
            val m = paceSec / 60
            val s = paceSec % 60
            String.format("%d:%02d/km", m, s)
        } else {
            "--"
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, LilacAccent.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .testTag("outdoor_history_card_${cardio.id}"),
        color = PurpleDeepCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Icon, Type Title, Date and Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PurplePrimary, LilacAccent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getOutdoorCardioIcon(cardio.type),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = cardio.type.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = timeFormatted,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (cardio.aiEvaluation.isNotBlank()) {
                        IconButton(
                            onClick = { onViewAi(cardio.aiEvaluation) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = "Ver Análise IA",
                                tint = LilacAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Excluir",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Metrics Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(PurpleDarkSurface.copy(alpha = 0.7f))
                    .padding(vertical = 12.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Metric 1: Distance
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("DISTÂNCIA", fontSize = 9.sp, fontWeight = FontWeight.Black, color = LilacSoft)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (cardio.distanceKm != null && cardio.distanceKm > 0.0) {
                            String.format(Locale.US, "%.2f km", cardio.distanceKm)
                        } else {
                            "--"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }

                // Metric 2: Duration
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("DURAÇÃO", fontSize = 9.sp, fontWeight = FontWeight.Black, color = LilacSoft)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${cardio.durationMinutes} min",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }

                // Metric 3: Pace
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("RITMO MÉD", fontSize = 9.sp, fontWeight = FontWeight.Black, color = LilacSoft)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = paceDisplay,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = LilacAccent
                    )
                }

                // Metric 4: Calories
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("CALORIAS", fontSize = 9.sp, fontWeight = FontWeight.Black, color = LilacSoft)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${cardio.caloriesBurned} kcal",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldSuccess
                    )
                }
            }

            // Route Points & Elevation Chips
            val hasElevation = cardio.elevationGainMeters != null && cardio.elevationGainMeters > 0.5
            val hasGpsRoute = routePoints.isNotEmpty()

            if (hasElevation || hasGpsRoute || cardio.location.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hasGpsRoute) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldSubtle)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("${routePoints.size} pts GPS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                            }
                        }
                    }

                    if (hasElevation) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PurpleDarkSurface)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = LilacAccent, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("+${cardio.elevationGainMeters?.toInt()}m ganho", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LilacAccent)
                            }
                        }
                    }

                    if (cardio.location.isNotBlank()) {
                        Text(
                            text = "📍 ${cardio.location}",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }

            // Km Splits Section (if available)
            if (splits.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { expandedSplits = !expandedSplits },
                    color = PurpleDarkSurface.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = LilacSoft, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Parciais de Km (${splits.size} splits)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Icon(
                            imageVector = if (expandedSplits) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = expandedSplits) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        splits.forEach { split ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PurpleDarkSurface.copy(alpha = 0.4f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Km ${split.kmIndex}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LilacAccent
                                )
                                Text(
                                    text = "Ritmo: ${split.avgPaceMinKm}/km",
                                    fontSize = 11.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = String.format(Locale.US, "%.1f km/h", split.avgSpeedKmh),
                                    fontSize = 11.sp,
                                    color = EmeraldSuccess
                                )
                            }
                        }
                    }
                }
            }

            // Notes if present
            if (cardio.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "“${cardio.notes}”",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

fun getOutdoorCardioIcon(type: CardioType): ImageVector {
    return when (type) {
        CardioType.BICICLETA_INDOOR -> Icons.AutoMirrored.Filled.DirectionsBike
        CardioType.CAMINHADA_ESTEIRA -> Icons.AutoMirrored.Filled.DirectionsWalk
        CardioType.CAMINHADA_AR_LIVRE -> Icons.Default.Park
        CardioType.CORRIDA -> Icons.AutoMirrored.Filled.DirectionsRun
        CardioType.FUTEBOL -> Icons.Default.SportsSoccer
    }
}
