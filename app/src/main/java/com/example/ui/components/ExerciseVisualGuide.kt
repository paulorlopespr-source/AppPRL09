package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.Exercise
import com.example.data.model.MuscleGroup
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
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.RedDestructive
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YellowWarning

/**
 * Exercise Visual Execution Guide Modal with Anatomical Visual Diagram,
 * Dynamic Muscle Activation Heatmap (Front & Back), Biomechanical Movement Stages,
 * and Form Corrections.
 */
@Composable
fun ExerciseVisualGuideDialog(
    exercise: Exercise,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Guia Visual", "Mapa Anatômico", "Biomecânica", "Erros Comuns")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(720.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, GlassBorder, RoundedCornerShape(24.dp)),
            color = PurpleDarkest
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
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
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(PurpleVibrant, LilacAccent))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = exercise.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = "${exercise.muscleGroup.displayName} • ${exercise.equipment.displayName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = LilacSoft
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_exercise_guide")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Selector
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = GlassSurfaceDark,
                    contentColor = LilacAccent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = LilacAccent
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) LilacAccent else TextMuted
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Content
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> VisualExecutionTab(exercise)
                        1 -> AnatomicalBodyMapTab(exercise)
                        2 -> MuscleBiomechanicalTab(exercise)
                        3 -> CommonMistakesTab(exercise)
                    }
                }
            }
        }
    }
}

@Composable
private fun VisualExecutionTab(exercise: Exercise) {
    val scrollState = rememberScrollState()
    val illustrationRes = getExerciseIllustrationResId(exercise)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Exercise Illustration or Motion Canvas
        if (illustrationRes != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF0F081D))
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(18.dp))
            ) {
                Image(
                    painter = painterResource(id = illustrationRes),
                    contentDescription = "Execução de ${exercise.name}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Muscle Activation Overlay Legend Tag
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF3B30))
                        )
                        Text(
                            text = "Principal",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFCC00))
                        )
                        Text(
                            text = "Secundário",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            // Exercise Motion Diagram Canvas
            ExerciseMotionCanvas(
                exercise = exercise,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassSurfaceDark)
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(16.dp))
            )
        }

        // Execution Stages
        Text(
            text = "Passo a Passo da Execução",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        StageStepItem(
            stepNumber = "1",
            title = "Posicionamento & Setup",
            description = getSetupInstruction(exercise),
            color = LilacAccent
        )

        StageStepItem(
            stepNumber = "2",
            title = "Fase Concêntrica (Subida / Força)",
            description = getConcentricInstruction(exercise),
            color = CyanAccent
        )

        StageStepItem(
            stepNumber = "3",
            title = "Fase Excêntrica (Descida Controlada)",
            description = getEccentricInstruction(exercise),
            color = EmeraldSuccess
        )

        // Golden Tip
        BentoCard(
            backgroundColor = PurpleDeepCard,
            borderColor = LilacAccent.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = YellowWarning,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Dica Biomecânica de Ouro",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = YellowWarning
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = exercise.executionTips.ifBlank { "Mantenha o abdômen contraído durante todo o movimento e controle a respiração: expire na força e inspire no retorno." },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

/**
 * Interactive Full Body Anatomical Map with Front & Back Vector Views
 * Highlighting Primary (Red/Orange) & Secondary (Yellow) Activation
 */
@Composable
private fun AnatomicalBodyMapTab(exercise: Exercise) {
    val scrollState = rememberScrollState()
    var viewMode by remember { mutableStateOf("FRENTE") } // "FRENTE" or "COSTAS"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // View Toggle & Legend Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Front/Back Buttons
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlassSurfaceDark)
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(20.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (viewMode == "FRENTE") LilacAccent else Color.Transparent)
                        .clickable { viewMode = "FRENTE" }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Vista Frontal",
                        fontSize = 11.sp,
                        fontWeight = if (viewMode == "FRENTE") FontWeight.Black else FontWeight.Normal,
                        color = if (viewMode == "FRENTE") PurpleDarkest else TextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (viewMode == "COSTAS") LilacAccent else Color.Transparent)
                        .clickable { viewMode = "COSTAS" }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Vista Dorsal",
                        fontSize = 11.sp,
                        fontWeight = if (viewMode == "COSTAS") FontWeight.Black else FontWeight.Normal,
                        color = if (viewMode == "COSTAS") PurpleDarkest else TextSecondary
                    )
                }
            }

            // Legend Badges
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    color = Color(0xFFFF3B30).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF3B30))
                ) {
                    Text(
                        text = "■ Agonista",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF453A),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
                Surface(
                    color = Color(0xFFFFD60A).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD60A))
                ) {
                    Text(
                        text = "■ Sinergista",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD60A),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Body Map Canvas Container
        BentoCard(
            backgroundColor = Color(0xFF100A1F),
            borderColor = GlassBorder,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (viewMode == "FRENTE") "Mapa de Ativação Muscular Anterior" else "Mapa de Ativação Muscular Posterior",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                DynamicAnatomicalBodyCanvas(
                    exercise = exercise,
                    isFrontView = viewMode == "FRENTE",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                )
            }
        }

        // Target vs Secondary Details Card
        BentoCard(
            backgroundColor = GlassSurfaceDark,
            borderColor = GlassBorderSubtle,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Músculo Alvo Principal:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = exercise.muscleGroup.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFF453A)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Músculos Sinergistas:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = getSynergistMuscles(exercise),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD60A)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Estabilizadores:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "Core, Eretores e Escápulas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CyanAccent
                    )
                }
            }
        }
    }
}

/**
 * Custom Canvas Vector Anatomical Human Figure with Dynamic Muscle Group Highlighting
 */
@Composable
fun DynamicAnatomicalBodyCanvas(
    exercise: Exercise,
    isFrontView: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f

        // Theme colors
        val bodyBaseColor = Color(0xFF281E3E)
        val bodyBorderColor = Color(0xFF4C3D6E)
        val primaryHighlight = Color(0xFFFF3B30)
        val secondaryHighlight = Color(0xFFFFD60A)
        val stabilizerColor = Color(0xFF64D2FF).copy(alpha = 0.8f)

        val mg = exercise.muscleGroup

        // Background grid dots
        val dotPaint = Color(0x18FFFFFF)
        for (gx in 20..(w.toInt() - 20) step 30) {
            for (gy in 20..(h.toInt() - 20) step 30) {
                drawCircle(dotPaint, radius = 1.5f, center = Offset(gx.toFloat(), gy.toFloat()))
            }
        }

        // --- HEAD & NECK ---
        drawCircle(
            color = bodyBaseColor,
            radius = 18f,
            center = Offset(cx, h * 0.10f)
        )
        drawCircle(
            color = bodyBorderColor,
            radius = 18f,
            center = Offset(cx, h * 0.10f),
            style = Stroke(width = 2f)
        )

        // Trapezius / Neck
        val isTrapsPrimary = mg == MuscleGroup.COSTAS || mg == MuscleGroup.OMBROS
        val trapsColor = if (isTrapsPrimary && !isFrontView) primaryHighlight else if (isTrapsPrimary) secondaryHighlight else bodyBaseColor
        val trapsPath = Path().apply {
            moveTo(cx - 10f, h * 0.12f)
            lineTo(cx - 32f, h * 0.17f)
            lineTo(cx + 32f, h * 0.17f)
            lineTo(cx + 10f, h * 0.12f)
            close()
        }
        drawPath(trapsPath, trapsColor)
        drawPath(trapsPath, bodyBorderColor, style = Stroke(width = 1.5f))

        if (isFrontView) {
            // ================= FRONT VIEW =================

            // --- CHEST (PEITORAL) ---
            val isChestPrimary = mg == MuscleGroup.PEITO
            val isChestSecondary = mg == MuscleGroup.TRICEPS || mg == MuscleGroup.OMBROS
            val chestColor = when {
                isChestPrimary -> primaryHighlight
                isChestSecondary -> secondaryHighlight
                else -> bodyBaseColor
            }

            // Left Pec
            val leftPec = Path().apply {
                moveTo(cx - 5f, h * 0.18f)
                lineTo(cx - 30f, h * 0.18f)
                lineTo(cx - 32f, h * 0.26f)
                lineTo(cx - 5f, h * 0.27f)
                close()
            }
            drawPath(leftPec, chestColor)
            drawPath(leftPec, bodyBorderColor, style = Stroke(width = 1.5f))

            // Right Pec
            val rightPec = Path().apply {
                moveTo(cx + 5f, h * 0.18f)
                lineTo(cx + 30f, h * 0.18f)
                lineTo(cx + 32f, h * 0.26f)
                lineTo(cx + 5f, h * 0.27f)
                close()
            }
            drawPath(rightPec, chestColor)
            drawPath(rightPec, bodyBorderColor, style = Stroke(width = 1.5f))

            // --- DELTOIDS (ANTERIOR / LATERAL) ---
            val isShouldersPrimary = mg == MuscleGroup.OMBROS
            val isShouldersSecondary = mg == MuscleGroup.PEITO || mg == MuscleGroup.TRICEPS
            val deltsColor = when {
                isShouldersPrimary -> primaryHighlight
                isShouldersSecondary -> secondaryHighlight
                else -> bodyBaseColor
            }

            // Left Deltoid
            val leftDelt = Path().apply {
                moveTo(cx - 32f, h * 0.17f)
                lineTo(cx - 50f, h * 0.21f)
                lineTo(cx - 42f, h * 0.27f)
                lineTo(cx - 32f, h * 0.23f)
                close()
            }
            drawPath(leftDelt, deltsColor)
            drawPath(leftDelt, bodyBorderColor, style = Stroke(width = 1.5f))

            // Right Deltoid
            val rightDelt = Path().apply {
                moveTo(cx + 32f, h * 0.17f)
                lineTo(cx + 50f, h * 0.21f)
                lineTo(cx + 42f, h * 0.27f)
                lineTo(cx + 32f, h * 0.23f)
                close()
            }
            drawPath(rightDelt, deltsColor)
            drawPath(rightDelt, bodyBorderColor, style = Stroke(width = 1.5f))

            // --- BICEPS ---
            val isBicepsPrimary = mg == MuscleGroup.BICEPS
            val isBicepsSecondary = mg == MuscleGroup.COSTAS
            val bicepsColor = when {
                isBicepsPrimary -> primaryHighlight
                isBicepsSecondary -> secondaryHighlight
                else -> bodyBaseColor
            }

            // Left Bicep
            drawRoundRect(
                color = bicepsColor,
                topLeft = Offset(cx - 52f, h * 0.27f),
                size = Size(14f, h * 0.12f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawRoundRect(
                color = bodyBorderColor,
                topLeft = Offset(cx - 52f, h * 0.27f),
                size = Size(14f, h * 0.12f),
                cornerRadius = CornerRadius(6f, 6f),
                style = Stroke(width = 1.5f)
            )

            // Right Bicep
            drawRoundRect(
                color = bicepsColor,
                topLeft = Offset(cx + 38f, h * 0.27f),
                size = Size(14f, h * 0.12f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawRoundRect(
                color = bodyBorderColor,
                topLeft = Offset(cx + 38f, h * 0.27f),
                size = Size(14f, h * 0.12f),
                cornerRadius = CornerRadius(6f, 6f),
                style = Stroke(width = 1.5f)
            )

            // Forearms (Antebraços)
            val forearmColor = if (mg == MuscleGroup.BICEPS || mg == MuscleGroup.COSTAS) secondaryHighlight else bodyBaseColor
            drawRoundRect(
                color = forearmColor,
                topLeft = Offset(cx - 54f, h * 0.40f),
                size = Size(13f, h * 0.12f),
                cornerRadius = CornerRadius(5f, 5f)
            )
            drawRoundRect(
                color = forearmColor,
                topLeft = Offset(cx + 41f, h * 0.40f),
                size = Size(13f, h * 0.12f),
                cornerRadius = CornerRadius(5f, 5f)
            )

            // --- ABDOMINALS & CORE ---
            val isAbsPrimary = mg == MuscleGroup.ABDOMEN
            val isAbsSecondary = mg == MuscleGroup.QUADRICEPS || mg == MuscleGroup.POSTERIOR_GLUTEOS
            val absColor = when {
                isAbsPrimary -> primaryHighlight
                isAbsSecondary -> stabilizerColor
                else -> bodyBaseColor
            }

            // Rectus Abdominis (6-pack grid)
            val absPath = Path().apply {
                moveTo(cx - 24f, h * 0.28f)
                lineTo(cx + 24f, h * 0.28f)
                lineTo(cx + 20f, h * 0.46f)
                lineTo(cx - 20f, h * 0.46f)
                close()
            }
            drawPath(absPath, absColor)
            drawPath(absPath, bodyBorderColor, style = Stroke(width = 1.5f))

            // Internal abs segments
            drawLine(bodyBorderColor, Offset(cx, h * 0.28f), Offset(cx, h * 0.46f), strokeWidth = 1.5f)
            drawLine(bodyBorderColor, Offset(cx - 22f, h * 0.34f), Offset(cx + 22f, h * 0.34f), strokeWidth = 1.5f)
            drawLine(bodyBorderColor, Offset(cx - 21f, h * 0.40f), Offset(cx + 21f, h * 0.40f), strokeWidth = 1.5f)

            // --- QUADRICEPS (PERNAS ANTERIOR) ---
            val isQuadsPrimary = mg == MuscleGroup.QUADRICEPS
            val isQuadsSecondary = mg == MuscleGroup.POSTERIOR_GLUTEOS || mg == MuscleGroup.PANTURRILHA
            val quadsColor = when {
                isQuadsPrimary -> primaryHighlight
                isQuadsSecondary -> secondaryHighlight
                else -> bodyBaseColor
            }

            // Left Quad
            val leftQuad = Path().apply {
                moveTo(cx - 22f, h * 0.47f)
                lineTo(cx - 4f, h * 0.47f)
                lineTo(cx - 7f, h * 0.68f)
                lineTo(cx - 25f, h * 0.68f)
                close()
            }
            drawPath(leftQuad, quadsColor)
            drawPath(leftQuad, bodyBorderColor, style = Stroke(width = 1.5f))

            // Right Quad
            val rightQuad = Path().apply {
                moveTo(cx + 4f, h * 0.47f)
                lineTo(cx + 22f, h * 0.47f)
                lineTo(cx + 25f, h * 0.68f)
                lineTo(cx + 7f, h * 0.68f)
                close()
            }
            drawPath(rightQuad, quadsColor)
            drawPath(rightQuad, bodyBorderColor, style = Stroke(width = 1.5f))

            // Knees
            drawCircle(bodyBaseColor, radius = 6f, center = Offset(cx - 16f, h * 0.70f))
            drawCircle(bodyBaseColor, radius = 6f, center = Offset(cx + 16f, h * 0.70f))

            // --- CALVES (ANTERIOR / TIBIALIS) ---
            val isCalvesPrimary = mg == MuscleGroup.PANTURRILHA
            val isCalvesSecondary = mg == MuscleGroup.QUADRICEPS
            val calvesColor = when {
                isCalvesPrimary -> primaryHighlight
                isCalvesSecondary -> secondaryHighlight
                else -> bodyBaseColor
            }

            drawRoundRect(
                color = calvesColor,
                topLeft = Offset(cx - 23f, h * 0.73f),
                size = Size(14f, h * 0.17f),
                cornerRadius = CornerRadius(5f, 5f)
            )
            drawRoundRect(
                color = bodyBorderColor,
                topLeft = Offset(cx - 23f, h * 0.73f),
                size = Size(14f, h * 0.17f),
                cornerRadius = CornerRadius(5f, 5f),
                style = Stroke(width = 1.5f)
            )

            drawRoundRect(
                color = calvesColor,
                topLeft = Offset(cx + 9f, h * 0.73f),
                size = Size(14f, h * 0.17f),
                cornerRadius = CornerRadius(5f, 5f)
            )
            drawRoundRect(
                color = bodyBorderColor,
                topLeft = Offset(cx + 9f, h * 0.73f),
                size = Size(14f, h * 0.17f),
                cornerRadius = CornerRadius(5f, 5f),
                style = Stroke(width = 1.5f)
            )

        } else {
            // ================= BACK VIEW (DORSAL) =================

            // --- LATS & UPPER BACK (COSTAS / DORSAIS / TRAPÉZIO) ---
            val isBackPrimary = mg == MuscleGroup.COSTAS
            val isBackSecondary = mg == MuscleGroup.OMBROS || mg == MuscleGroup.POSTERIOR_GLUTEOS
            val backColor = when {
                isBackPrimary -> primaryHighlight
                isBackSecondary -> secondaryHighlight
                else -> bodyBaseColor
            }

            // Dorsals / V-Taper
            val latsPath = Path().apply {
                moveTo(cx - 36f, h * 0.18f)
                lineTo(cx + 36f, h * 0.18f)
                lineTo(cx + 20f, h * 0.44f)
                lineTo(cx - 20f, h * 0.44f)
                close()
            }
            drawPath(latsPath, backColor)
            drawPath(latsPath, bodyBorderColor, style = Stroke(width = 1.5f))

            // Spine line
            drawLine(bodyBorderColor, Offset(cx, h * 0.17f), Offset(cx, h * 0.46f), strokeWidth = 2f)

            // --- TRICEPS (BRAÇO POSTERIOR) ---
            val isTricepsPrimary = mg == MuscleGroup.TRICEPS
            val isTricepsSecondary = mg == MuscleGroup.PEITO || mg == MuscleGroup.OMBROS
            val tricepsColor = when {
                isTricepsPrimary -> primaryHighlight
                isTricepsSecondary -> secondaryHighlight
                else -> bodyBaseColor
            }

            // Left Tricep
            drawRoundRect(
                color = tricepsColor,
                topLeft = Offset(cx - 52f, h * 0.24f),
                size = Size(14f, h * 0.15f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawRoundRect(
                color = bodyBorderColor,
                topLeft = Offset(cx - 52f, h * 0.24f),
                size = Size(14f, h * 0.15f),
                cornerRadius = CornerRadius(6f, 6f),
                style = Stroke(width = 1.5f)
            )

            // Right Tricep
            drawRoundRect(
                color = tricepsColor,
                topLeft = Offset(cx + 38f, h * 0.24f),
                size = Size(14f, h * 0.15f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawRoundRect(
                color = bodyBorderColor,
                topLeft = Offset(cx + 38f, h * 0.24f),
                size = Size(14f, h * 0.15f),
                cornerRadius = CornerRadius(6f, 6f),
                style = Stroke(width = 1.5f)
            )

            // Rear Deltoids
            val isRearDelts = mg == MuscleGroup.OMBROS || mg == MuscleGroup.COSTAS
            val rearDeltsColor = if (isRearDelts) secondaryHighlight else bodyBaseColor
            drawCircle(rearDeltsColor, radius = 9f, center = Offset(cx - 38f, h * 0.20f))
            drawCircle(rearDeltsColor, radius = 9f, center = Offset(cx + 38f, h * 0.20f))

            // --- GLUTES (GLÚTEOS) ---
            val isGlutesPrimary = mg == MuscleGroup.POSTERIOR_GLUTEOS
            val isGlutesSecondary = mg == MuscleGroup.QUADRICEPS
            val glutesColor = when {
                isGlutesPrimary -> primaryHighlight
                isGlutesSecondary -> secondaryHighlight
                else -> bodyBaseColor
            }

            // Left Glute
            val leftGlute = Path().apply {
                moveTo(cx - 23f, h * 0.45f)
                lineTo(cx - 3f, h * 0.45f)
                lineTo(cx - 3f, h * 0.55f)
                lineTo(cx - 25f, h * 0.55f)
                close()
            }
            drawPath(leftGlute, glutesColor)
            drawPath(leftGlute, bodyBorderColor, style = Stroke(width = 1.5f))

            // Right Glute
            val rightGlute = Path().apply {
                moveTo(cx + 3f, h * 0.45f)
                lineTo(cx + 23f, h * 0.45f)
                lineTo(cx + 25f, h * 0.55f)
                lineTo(cx + 3f, h * 0.55f)
                close()
            }
            drawPath(rightGlute, glutesColor)
            drawPath(rightGlute, bodyBorderColor, style = Stroke(width = 1.5f))

            // --- HAMSTRINGS (ISQUIOTIBIAIS / POSTERIOR DE COXA) ---
            val isHamstringsPrimary = mg == MuscleGroup.POSTERIOR_GLUTEOS
            val isHamstringsSecondary = mg == MuscleGroup.QUADRICEPS
            val hamsColor = when {
                isHamstringsPrimary -> primaryHighlight
                isHamstringsSecondary -> secondaryHighlight
                else -> bodyBaseColor
            }

            // Left Hamstring
            drawRoundRect(
                color = hamsColor,
                topLeft = Offset(cx - 24f, h * 0.56f),
                size = Size(18f, h * 0.13f),
                cornerRadius = CornerRadius(5f, 5f)
            )
            drawRoundRect(
                color = bodyBorderColor,
                topLeft = Offset(cx - 24f, h * 0.56f),
                size = Size(18f, h * 0.13f),
                cornerRadius = CornerRadius(5f, 5f),
                style = Stroke(width = 1.5f)
            )

            // Right Hamstring
            drawRoundRect(
                color = hamsColor,
                topLeft = Offset(cx + 6f, h * 0.56f),
                size = Size(18f, h * 0.13f),
                cornerRadius = CornerRadius(5f, 5f)
            )
            drawRoundRect(
                color = bodyBorderColor,
                topLeft = Offset(cx + 6f, h * 0.56f),
                size = Size(18f, h * 0.13f),
                cornerRadius = CornerRadius(5f, 5f),
                style = Stroke(width = 1.5f)
            )

            // --- CALVES (GASTROCNÊMIO / PANTURRILHAS POSTERIOR) ---
            val isCalvesBackPrimary = mg == MuscleGroup.PANTURRILHA
            val isCalvesBackSecondary = mg == MuscleGroup.POSTERIOR_GLUTEOS || mg == MuscleGroup.QUADRICEPS
            val calvesBackColor = when {
                isCalvesBackPrimary -> primaryHighlight
                isCalvesBackSecondary -> secondaryHighlight
                else -> bodyBaseColor
            }

            // Left Calf diamond bulge
            val leftCalf = Path().apply {
                moveTo(cx - 16f, h * 0.72f)
                lineTo(cx - 6f, h * 0.78f)
                lineTo(cx - 16f, h * 0.88f)
                lineTo(cx - 26f, h * 0.78f)
                close()
            }
            drawPath(leftCalf, calvesBackColor)
            drawPath(leftCalf, bodyBorderColor, style = Stroke(width = 1.5f))

            // Right Calf diamond bulge
            val rightCalf = Path().apply {
                moveTo(cx + 16f, h * 0.72f)
                lineTo(cx + 26f, h * 0.78f)
                lineTo(cx + 16f, h * 0.88f)
                lineTo(cx + 6f, h * 0.78f)
                close()
            }
            drawPath(rightCalf, calvesBackColor)
            drawPath(rightCalf, bodyBorderColor, style = Stroke(width = 1.5f))
        }
    }
}

/**
 * Returns matching illustration resource if available
 */
fun getExerciseIllustrationResId(exercise: Exercise): Int? {
    val name = exercise.name.lowercase()
    return when {
        name.contains("supino") || name.contains("bench press") || name.contains("peito") -> R.drawable.img_bench_press
        name.contains("agachamento") || name.contains("squat") || name.contains("leg press") -> R.drawable.img_barbell_squat
        name.contains("puxada") || name.contains("remada") || name.contains("pulldown") || name.contains("row") || name.contains("barra fixa") -> R.drawable.img_lat_pulldown
        else -> null
    }
}

@Composable
private fun MuscleBiomechanicalTab(exercise: Exercise) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Target Muscle Breakdown
        BentoCard(
            backgroundColor = GlassSurfaceDark,
            borderColor = GlassBorder,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Músculos Envolvidos",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Músculo Alvo Principal (Agonista):",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Surface(
                        color = Color(0xFFFF3B30).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF3B30))
                    ) {
                        Text(
                            text = exercise.muscleGroup.displayName,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFF453A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Músculos Secundários (Sinergistas):",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = getSynergistMuscles(exercise),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD60A)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Estabilizadores:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "Core, Lombar & Escápulas",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanAccent
                    )
                }
            }
        }

        // Biomechanical Details
        BentoCard(
            backgroundColor = GlassSurfaceDark,
            borderColor = GlassBorder,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Parâmetros Recomendados",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoPill(label = "Séries Padrão", value = "${exercise.defaultSets} séries")
                    InfoPill(label = "Repetições Alvo", value = "${exercise.defaultReps} reps")
                    InfoPill(label = "Descanso Ideal", value = "${exercise.defaultRestSeconds}s")
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Instruções Completas:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = exercise.instructions.ifBlank { "Execute o exercício com técnica rigorosa, evitando impulsos ou compensações posturais com a lombar." },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun CommonMistakesTab(exercise: Exercise) {
    val scrollState = rememberScrollState()
    val mistakes = getCommonMistakesForExercise(exercise)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Erros Frequentes e Como Evitar",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        mistakes.forEach { mistake ->
            BentoCard(
                backgroundColor = GlassSurfaceDark,
                borderColor = RedDestructive.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = RedDestructive,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = mistake.title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = RedDestructive
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "❌ Erro: ${mistake.errorDesc}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "✅ Correção: ${mistake.correction}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldSuccess
                    )
                }
            }
        }
    }
}

@Composable
private fun StageStepItem(
    stepNumber: String,
    title: String,
    description: String,
    color: Color
) {
    BentoCard(
        backgroundColor = GlassSurfaceDark,
        borderColor = color.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f))
                    .border(1.dp, color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun InfoPill(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(PurpleDeepCard)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = LilacAccent)
    }
}

/**
 * Visual Canvas Drawing Custom Biomechanical Curves and Vector Figures for each Muscle Group
 */
@Composable
fun ExerciseMotionCanvas(exercise: Exercise, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Background subtle grid
        val gridColor = Color(0x15FFFFFF)
        for (x in 0..(w.toInt()) step 40) {
            drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), h), 1f)
        }
        for (y in 0..(h.toInt()) step 40) {
            drawLine(gridColor, Offset(0f, y.toFloat()), Offset(w, y.toFloat()), 1f)
        }

        val primaryColor = LilacAccent
        val accentColor = CyanAccent

        // Anatomical Silhouette & Motion Path based on muscle group
        when (exercise.muscleGroup) {
            MuscleGroup.PEITO -> {
                // Bench Press arc motion
                val path = Path().apply {
                    moveTo(w * 0.25f, h * 0.70f)
                    cubicTo(w * 0.40f, h * 0.35f, w * 0.60f, h * 0.35f, w * 0.75f, h * 0.70f)
                }
                drawPath(path, primaryColor, style = Stroke(width = 4f, cap = StrokeCap.Round))
                // Bar representation
                drawLine(accentColor, Offset(w * 0.30f, h * 0.40f), Offset(w * 0.70f, h * 0.40f), strokeWidth = 8f, cap = StrokeCap.Round)
                // Dumbbell/Plate indicators
                drawCircle(primaryColor, radius = 14f, center = Offset(w * 0.30f, h * 0.40f))
                drawCircle(primaryColor, radius = 14f, center = Offset(w * 0.70f, h * 0.40f))
            }
            MuscleGroup.COSTAS -> {
                // Lat pulldown / Row trajectory
                val path = Path().apply {
                    moveTo(w * 0.50f, h * 0.25f)
                    lineTo(w * 0.50f, h * 0.75f)
                }
                drawPath(path, accentColor, style = Stroke(width = 4f, cap = StrokeCap.Round))
                drawLine(primaryColor, Offset(w * 0.25f, h * 0.30f), Offset(w * 0.75f, h * 0.30f), strokeWidth = 8f, cap = StrokeCap.Round)
                drawCircle(EmeraldSuccess, radius = 10f, center = Offset(w * 0.50f, h * 0.75f))
            }
            MuscleGroup.QUADRICEPS, MuscleGroup.POSTERIOR_GLUTEOS, MuscleGroup.PANTURRILHA -> {
                // Squat / Deadlift vertical depth line and angles
                val legPath = Path().apply {
                    moveTo(w * 0.35f, h * 0.85f)
                    lineTo(w * 0.45f, h * 0.55f)
                    lineTo(w * 0.55f, h * 0.65f)
                    lineTo(w * 0.65f, h * 0.85f)
                }
                drawPath(legPath, primaryColor, style = Stroke(width = 6f, cap = StrokeCap.Round))
                drawLine(accentColor, Offset(w * 0.30f, h * 0.35f), Offset(w * 0.70f, h * 0.35f), strokeWidth = 7f, cap = StrokeCap.Round)
            }
            else -> {
                // Generic Curl / Extension dynamic arc
                val path = Path().apply {
                    moveTo(w * 0.30f, h * 0.75f)
                    cubicTo(w * 0.45f, h * 0.25f, w * 0.65f, h * 0.25f, w * 0.75f, h * 0.65f)
                }
                drawPath(path, primaryColor, style = Stroke(width = 5f, cap = StrokeCap.Round))
                drawCircle(CyanAccent, radius = 12f, center = Offset(w * 0.75f, h * 0.65f))
            }
        }
    }
}

// Helpers for biomechanics content
private fun getSetupInstruction(exercise: Exercise): String {
    val name = exercise.name.lowercase()
    return when {
        name.contains("supino") -> "Deite com as escápulas aduzidas e deprimidas, pés bem apoiados ao chão e curvatura natural da lombar preservada."
        name.contains("agachamento") -> "Pés alinhados à largura dos ombros, pontas levemente voltadas para fora, abdômen (core) contraído com pressão intra-abdominal (Bracing)."
        name.contains("puxada") || name.contains("barra fixa") -> "Segure com pegada firme, peito apontando para cima e projete os ombros para baixo antes de iniciar a tração."
        name.contains("desenvolvimento") || name.contains("ombro") -> "Cotovelos levemente apontados à frente (plano escapular), punhos alinhados com o antebraço."
        name.contains("rosca") -> "Cotovelos fixos ao lado do tronco, sem balançar os ombros ou a lombar."
        name.contains("tríceps") || name.contains("triceps") -> "Mantenha o braço estático e concentre a força exclusivamente na extensão do cotovelo."
        else -> "Ajuste o equipamento de acordo com sua estatura e assegure uma base estável antes de iniciar a carga."
    }
}

private fun getConcentricInstruction(exercise: Exercise): String {
    val name = exercise.name.lowercase()
    return when {
        name.contains("supino") -> "Empurre o peso em direção ao teto, contraindo o peitoral no topo sem perder a retração das escápulas."
        name.contains("agachamento") -> "Empurre o chão através dos calcanhares e meio do pé, estendendo joelhos e quadris simultaneamente."
        name.contains("puxada") || name.contains("remada") -> "Puxe os cotovelos em direção ao quadril, espremendo as dorsais e aproximando as escápulas."
        name.contains("elevação") || name.contains("elevacao") -> "Eleve os braços no plano escapular até a altura dos ombros, com o abdômen firme."
        else -> "Realize a fase de força com aceleração controlada (1 a 2 segundos), focando na contração máxima do músculo alvo."
    }
}

private fun getEccentricInstruction(exercise: Exercise): String {
    return "Controle a descida em 2 a 3 segundos, resistindo à gravidade e sentindo o alongamento do músculo sem soltar o peso abruptamente."
}

private fun getSynergistMuscles(exercise: Exercise): String {
    return when (exercise.muscleGroup) {
        MuscleGroup.PEITO -> "Tríceps & Deltóide Anterior"
        MuscleGroup.COSTAS -> "Bíceps, Braquial & Trapézio"
        MuscleGroup.OMBROS -> "Tríceps & Trapézio Superior"
        MuscleGroup.QUADRICEPS -> "Glúteos & Panturrilhas"
        MuscleGroup.POSTERIOR_GLUTEOS -> "Glúteos & Eretores da Espinha"
        MuscleGroup.BICEPS -> "Braquial & Braquiorradial"
        MuscleGroup.TRICEPS -> "Ancôneo & Peitoral Maior"
        MuscleGroup.ABDOMEN -> "Oblíquos & Transverso"
        MuscleGroup.PANTURRILHA -> "Sóleo & Gastrocnêmio"
    }
}

data class ExerciseMistake(
    val title: String,
    val errorDesc: String,
    val correction: String
)

private fun getCommonMistakesForExercise(exercise: Exercise): List<ExerciseMistake> {
    val name = exercise.name.lowercase()
    return when {
        name.contains("supino") -> listOf(
            ExerciseMistake(
                title = "Cotovelos Muito Abertos (90°)",
                errorDesc = "Gera sobrecarga excessiva no manguito rotador e articulação glenoumeral.",
                correction = "Mantenha os cotovelos a cerca de 45° a 70° em relação ao tronco."
            ),
            ExerciseMistake(
                title = "Tirar a Cabeça ou Glúteos do Banco",
                errorDesc = "Perde estabilidade da coluna e dispersa a força motora do peitoral.",
                correction = "Mantenha pés no chão, glúteos colados no banco e escápulas travadas."
            )
        )
        name.contains("agachamento") -> listOf(
            ExerciseMistake(
                title = "Valgo Dinâmico (Joelhos para dentro)",
                errorDesc = "Os joelhos colapsam para dentro durante a subida, sobrecarregando ligamentos.",
                correction = "Aponte os joelhos na direção da ponta dos pés e ative os glúteos médios."
            ),
            ExerciseMistake(
                title = "Arredondar a Lombar (Butt Wink)",
                errorDesc = "Flexão da coluna lombar no ponto mais baixo do movimento.",
                correction = "Desça apenas até onde consegue manter a pelve neutra e o peito aberto."
            )
        )
        name.contains("rosca") -> listOf(
            ExerciseMistake(
                title = "Balanço do Tronco (Roubo)",
                errorDesc = "Usar impulso lombar para subir o peso em vez de usar os bíceps.",
                correction = "Reduza a carga se necessário e mantenha o tronco totalmente imóvel."
            )
        )
        else -> listOf(
            ExerciseMistake(
                title = "Movimento Curto (Amplitude Reduzida)",
                errorDesc = "Não alongar ou contrair completamente o músculo alvo.",
                correction = "Priorize amplitude completa e movimento controlado com cadência 2-0-2."
            ),
            ExerciseMistake(
                title = "Falta de Respiração Adequada",
                errorDesc = "Prender a respiração de forma descontrolada sem técnica de Valsalva.",
                correction = "Expire no esforço (fase concêntrica) e inspire no retorno (fase excêntrica)."
            )
        )
    }
}
