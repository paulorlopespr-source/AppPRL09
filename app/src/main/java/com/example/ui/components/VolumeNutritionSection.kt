package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CardioSession
import com.example.data.model.SessionStatus
import com.example.data.model.UserProfile
import com.example.data.model.VolumeNutritionEvaluationResult
import com.example.data.model.WorkoutSession
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
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
import java.util.Locale

/**
 * Módulo de Avaliação Nutricional com Gemini AI
 * Analisa o volume total de treino registrado no histórico e sugere ajustes precisos na dieta.
 */
@Composable
fun VolumeNutritionSection(
    userProfile: UserProfile?,
    workoutSessions: List<WorkoutSession>,
    cardioSessions: List<CardioSession>,
    evaluationResult: VolumeNutritionEvaluationResult?,
    isLoading: Boolean,
    onEvaluateClick: () -> Unit,
    onApplyToProfile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var hasAppliedToProfile by remember { mutableStateOf(false) }

    val completedSessions = remember(workoutSessions) {
        workoutSessions.filter { it.status == SessionStatus.COMPLETED }
    }
    val totalVolumeKg = remember(completedSessions) {
        completedSessions.sumOf { it.totalWeightLiftedKg }
    }
    val workoutsCount = completedSessions.size
    val cardioMinutes = remember(cardioSessions) { cardioSessions.sumOf { it.durationMinutes } }

    BentoCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_volume_nutrition_evaluation")
    ) {
        // Module Header
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
                        .background(PurpleDarkSurface)
                        .border(1.dp, LilacAccent.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = LilacAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MÓDULO NUTRICIONAL COM GEMINI IA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LilacAccent,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(LilacAccent.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "VOLUME LOAD",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = LilacSoft
                            )
                        }
                    }
                    Text(
                        text = "Ajustes na Dieta pelo Volume de Treino",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // History Volume Snapshot
        Surface(
            color = PurpleDarkSurface,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Volume Real no Histórico",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${String.format(Locale.US, "%,.0f", totalVolumeKg)} kg levantados",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Treinos", fontSize = 10.sp, color = TextMuted)
                        Text("$workoutsCount sessões", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LilacSoft)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Cardio", fontSize = 10.sp, color = TextMuted)
                        Text("${cardioMinutes}m", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Trigger Button
        PrimaryButton(
            text = if (isLoading) "Calculando Ajustes com IA..." else if (evaluationResult != null) "Reavaliar Dieta com Gemini IA" else "Avaliar Dieta com Gemini IA",
            icon = Icons.Default.AutoAwesome,
            onClick = {
                hasAppliedToProfile = false
                onEvaluateClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_evaluate_volume_nutrition")
        )

        // Loading Indicator
        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = LilacAccent,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "O Gemini AI está cruzando seu volume de treino com seu gasto calórico...",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        }

        // Evaluation Result Display
        if (evaluationResult != null && !isLoading) {
            Spacer(modifier = Modifier.height(16.dp))

            // Calories & Target Card
            Surface(
                color = PurpleDarkSurface,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = AmberWarning,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "META CALÓRICA AJUSTADA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberWarning
                            )
                        }

                        Surface(
                            color = LilacAccent.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (evaluationResult.isFromGeminiAI) "Gemini 3.5 AI" else "Ciência Nutricional",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = LilacSoft,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${evaluationResult.recommendedDailyCalories} kcal / dia",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = evaluationResult.calorieAdjustmentReason,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Macro Targets Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroCard(
                    title = "PROTEÍNA",
                    grams = evaluationResult.proteinGrams,
                    perKg = evaluationResult.proteinPerKg,
                    color = LilacAccent,
                    modifier = Modifier.weight(1f)
                )
                MacroCard(
                    title = "CARBOS",
                    grams = evaluationResult.carbsGrams,
                    perKg = evaluationResult.carbsPerKg,
                    color = CyanAccent,
                    modifier = Modifier.weight(1f)
                )
                MacroCard(
                    title = "GORDURAS",
                    grams = evaluationResult.fatsGrams,
                    perKg = evaluationResult.fatsPerKg,
                    color = EmeraldSuccess,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Specific Diet Adjustments from Volume
            Text(
                text = "AJUSTES DIETÉTICOS RECOMENDADOS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = LilacAccent,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                evaluationResult.dietAdjustments.forEach { adjustment ->
                    Surface(
                        color = PurpleDarkSurface.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = LilacAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = adjustment,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nutrient Timing (Pre & Post Workout)
            Surface(
                color = PurpleDarkSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = LilacSoft,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TIMING NUTRICIONAL PARA SUSTENTAR O VOLUME",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LilacSoft
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Pré-Treino: ${evaluationResult.preWorkoutNutrition}",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "• Pós-Treino: ${evaluationResult.postWorkoutNutrition}",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Hidratação Mínima: ${evaluationResult.hydrationLitres} L/dia",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Volume Scientific Insight
            Surface(
                color = PurpleDeepCard.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Speed,
                        contentDescription = null,
                        tint = LilacAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = evaluationResult.volumeInsight,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Save to Profile Button
            Surface(
                color = if (hasAppliedToProfile) EmeraldDark.copy(alpha = 0.4f) else PurpleDarkSurface,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (hasAppliedToProfile) EmeraldSuccess else LilacAccent.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val summaryText = "Meta: ${evaluationResult.recommendedDailyCalories} kcal | P: ${evaluationResult.proteinGrams}g, C: ${evaluationResult.carbsGrams}g, G: ${evaluationResult.fatsGrams}g"
                        onApplyToProfile(summaryText)
                        hasAppliedToProfile = true
                    }
                    .testTag("btn_save_nutrition_targets_to_profile")
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (hasAppliedToProfile) Icons.Default.Check else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (hasAppliedToProfile) EmeraldSuccess else LilacAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasAppliedToProfile) "Metas Salvas no Seu Perfil!" else "Aplicar Metas Nutricionais ao Meu Perfil",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasAppliedToProfile) EmeraldSuccess else TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroCard(
    title: String,
    grams: Double,
    perKg: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = PurpleDarkSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text("${grams.toInt()}g", fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
            Text("${String.format(Locale.US, "%.1f", perKg)}g/kg", fontSize = 10.sp, color = TextSecondary)
        }
    }
}
