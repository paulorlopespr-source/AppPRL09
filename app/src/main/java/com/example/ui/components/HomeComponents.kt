package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Presentation UI State for the Home Screen.
 * Centralizes presentation-only data to keep the composables declarative and clean.
 */
data class HomeUiState(
    val userName: String = "Paulo",
    val todayWorkoutTitle: String = "Full Body A",
    val todayMuscleGroup: String = "Peito, Costas & Pernas",
    val hasPlannedWorkout: Boolean = true,
    val isWorkoutActive: Boolean = false,
    val activeWorkoutTitle: String = "",
    val activeWorkoutDurationSeconds: Long = 0L,
    val isCardioActive: Boolean = false,
    val activeCardioTitle: String = "",
    val activeCardioDurationMinutes: Int = 0,
    val streakDays: Int = 12,
    val weeklyDoneCount: Int = 2,
    val weeklyGoalTarget: Int = 5,
    val readinessScore: Int = 78,
    val hasSufficientData: Boolean = true,
    val weeklyDayStatuses: List<DayProgressStatus> = listOf(
        DayProgressStatus("S", isCompleted = true, isCurrentOrNext = false, isFuture = false),
        DayProgressStatus("T", isCompleted = true, isCurrentOrNext = false, isFuture = false),
        DayProgressStatus("Q", isCompleted = false, isCurrentOrNext = true, isFuture = false),
        DayProgressStatus("Q", isCompleted = false, isCurrentOrNext = false, isFuture = true),
        DayProgressStatus("S", isCompleted = false, isCurrentOrNext = false, isFuture = true),
        DayProgressStatus("S", isCompleted = false, isCurrentOrNext = false, isFuture = true),
        DayProgressStatus("D", isCompleted = false, isCurrentOrNext = false, isFuture = true)
    )
)

data class DayProgressStatus(
    val dayLetter: String,
    val isCompleted: Boolean,
    val isCurrentOrNext: Boolean,
    val isFuture: Boolean
)

/**
 * 1. HomeHeader
 * - Hamburger menu icon on left (opens AppSideDrawer)
 * - Personalized greeting: "Olá, Paulo" (24sp, bold, white)
 * - Subtitle: "Disciplina hoje, resultados amanhã." (14sp, light lilac)
 * - Notification bell on right with subtle badge
 */
@Composable
fun HomeHeader(
    userName: String,
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit,
    hasUnreadNotification: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(42.dp)
                    .testTag("btn_home_drawer")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menu lateral",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Olá, $userName",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Disciplina hoje, resultados amanhã.",
                    fontSize = 14.sp,
                    color = Color(0xFFC4B5FD)
                )
            }
        }

        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier
                .size(42.dp)
                .testTag("btn_home_notifications")
        ) {
            BadgedBox(
                badge = {
                    if (hasUnreadNotification) {
                        Badge(
                            containerColor = Color(0xFFA855F7),
                            modifier = Modifier.size(8.dp)
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificações e Lembretes",
                    tint = Color(0xFFC4B5FD),
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

/**
 * 2. TodayWorkoutHero
 * High-impact motivational hero card matching the reference image:
 * - Mountain sunset background with athletic man silhouette
 * - Typography: "UM DIA\nMAIS FORTE\nCOMEÇA\nAGORA." (AGORA. in purple/lilac, rest white)
 * - Subtitle: "Disciplina é liberdade."
 * - Full-width CTA pill button: "Iniciar treino de hoje" (or contextual state)
 */
@Composable
fun TodayWorkoutHero(
    state: HomeUiState,
    onStartWorkout: () -> Unit,
    onResumeWorkout: () -> Unit,
    onResumeCardio: () -> Unit,
    onSelectWorkout: () -> Unit,
    onConfigureData: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isWorkoutActive) {
        ActiveSessionBanner(
            title = state.activeWorkoutTitle.ifBlank { "Treino em Andamento" },
            subtitle = "Cronômetro inteligente ativo",
            tag = "EM ANDAMENTO",
            durationSeconds = state.activeWorkoutDurationSeconds,
            actionLabel = "Retomar treino",
            icon = Icons.Default.PlayArrow,
            onAction = onResumeWorkout,
            modifier = modifier
        )
        return
    }

    if (state.isCardioActive) {
        ActiveSessionBanner(
            title = state.activeCardioTitle.ifBlank { "Atividade ao Ar Livre" },
            subtitle = "GPS e monitoramento em tempo real",
            tag = "CARDIO ATIVO",
            durationSeconds = state.activeCardioDurationMinutes.toLong() * 60,
            actionLabel = "Retomar atividade",
            icon = Icons.Default.DirectionsRun,
            onAction = onResumeCardio,
            modifier = modifier
        )
        return
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_today_workout_hero"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF130E20)),
        border = BorderStroke(1.dp, Color(0xFF2E204A).copy(alpha = 0.6f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
        ) {
            // Mountain Sunset Silhouette Artwork
            HeroSunsetMountainBackground(
                modifier = Modifier.fillMaxSize()
            )

            // Dark readability gradients
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0F0B1A).copy(alpha = 0.94f),
                                Color(0xFF0F0B1A).copy(alpha = 0.72f),
                                Color(0xFF0F0B1A).copy(alpha = 0.25f)
                            ),
                            startX = 0f,
                            endX = 900f
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF0F0B1A).copy(alpha = 0.65f),
                                Color(0xFF0F0B1A).copy(alpha = 0.95f)
                            ),
                            startY = 140f
                        )
                    )
            )

            // Foreground Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    val headline = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                letterSpacing = 0.5.sp
                            )
                        ) {
                            append("UM DIA\nMAIS FORTE\nCOMEÇA ")
                        }
                        withStyle(
                            SpanStyle(
                                color = Color(0xFFA855F7),
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                letterSpacing = 0.5.sp
                            )
                        ) {
                            append("AGORA.")
                        }
                    }

                    Text(
                        text = headline,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Disciplina é liberdade.",
                        fontSize = 14.sp,
                        color = Color(0xFFE2E8F0)
                    )
                }

                // CTA Button depending on State
                val (ctaLabel, ctaAction) = when {
                    !state.hasSufficientData -> Pair("Configurar / registrar dados", onConfigureData)
                    !state.hasPlannedWorkout -> Pair("Escolher treino", onSelectWorkout)
                    else -> Pair("Iniciar treino de hoje", onStartWorkout)
                }

                Button(
                    onClick = ctaAction,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF7C3AED),
                                    Color(0xFF9333EA),
                                    Color(0xFFA855F7)
                                )
                            )
                        )
                        .testTag("btn_hero_cta")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ctaLabel,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Canvas drawing the atmospheric sunset mountains and athletic male silhouette
 */
@Composable
fun HeroSunsetMountainBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Twilight sunset sky gradient
        drawRect(
            brush = Brush.verticalGradient(
                listOf(
                    Color(0xFF140827), // deep twilight night sky
                    Color(0xFF280B45), // rich purple
                    Color(0xFF5B168C), // magenta purple
                    Color(0xFFB45309), // sunset warm amber glow at horizon
                    Color(0xFF431407)  // deep mountain base
                )
            )
        )

        // 2. Sunset sun glow behind mountain/athlete
        val sunCenter = Offset(w * 0.72f, h * 0.42f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFB056).copy(alpha = 0.55f),
                    Color(0xFFEA580C).copy(alpha = 0.25f),
                    Color.Transparent
                ),
                center = sunCenter,
                radius = w * 0.38f
            ),
            radius = w * 0.38f,
            center = sunCenter
        )

        // 3. Distant mountain peaks (Deep Violet)
        val distantMountain = Path().apply {
            moveTo(0f, h * 0.65f)
            lineTo(w * 0.20f, h * 0.50f)
            lineTo(w * 0.38f, h * 0.58f)
            lineTo(w * 0.55f, h * 0.44f)
            lineTo(w * 0.72f, h * 0.54f)
            lineTo(w * 0.88f, h * 0.42f)
            lineTo(w, h * 0.48f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = distantMountain,
            color = Color(0xFF261048)
        )

        // 4. Closer mountain ridges (Dark Purple)
        val closerMountain = Path().apply {
            moveTo(0f, h * 0.78f)
            lineTo(w * 0.28f, h * 0.64f)
            lineTo(w * 0.50f, h * 0.72f)
            lineTo(w * 0.70f, h * 0.56f)
            lineTo(w * 0.85f, h * 0.62f)
            lineTo(w, h * 0.58f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = closerMountain,
            color = Color(0xFF17092E)
        )

        // 5. Athletic muscular man silhouette (back view, standing strong)
        val manX = w * 0.72f
        val manY = h * 0.45f
        val manScale = h * 0.0032f

        val silhouettePath = Path().apply {
            // Head
            val headRadius = 14f * manScale
            val headCenterY = manY - 55f * manScale

            // Neck & Traps
            moveTo(manX - 8f * manScale, headCenterY + headRadius)
            // Left Trap to broad left shoulder
            lineTo(manX - 34f * manScale, manY - 26f * manScale)
            // Left Deltoid curve
            lineTo(manX - 38f * manScale, manY - 14f * manScale)
            // Left Arm / Triceps down to waist
            lineTo(manX - 35f * manScale, manY + 36f * manScale)
            // Left Lat / Torso tapering into waist
            lineTo(manX - 22f * manScale, manY + 50f * manScale)
            // Lower waist / base
            lineTo(manX + 22f * manScale, manY + 50f * manScale)
            // Right Lat / Torso
            lineTo(manX + 35f * manScale, manY + 36f * manScale)
            // Right Deltoid / Arm
            lineTo(manX + 38f * manScale, manY - 14f * manScale)
            // Right Shoulder
            lineTo(manX + 34f * manScale, manY - 26f * manScale)
            // Right Trap back to head
            lineTo(manX + 8f * manScale, headCenterY + headRadius)
            close()
        }

        // Draw body silhouette
        drawPath(path = silhouettePath, color = Color(0xFF090412))
        // Draw Head
        drawCircle(
            color = Color(0xFF090412),
            radius = 14f * manScale,
            center = Offset(manX, manY - 55f * manScale)
        )

        // Warm rim light edge on athlete shoulders
        drawLine(
            color = Color(0xFFFFBE76).copy(alpha = 0.45f),
            start = Offset(manX - 34f * manScale, manY - 26f * manScale),
            end = Offset(manX - 8f * manScale, manY - 40f * manScale),
            strokeWidth = 2.5f
        )
        drawLine(
            color = Color(0xFFFFBE76).copy(alpha = 0.45f),
            start = Offset(manX + 8f * manScale, manY - 40f * manScale),
            end = Offset(manX + 34f * manScale, manY - 26f * manScale),
            strokeWidth = 2.5f
        )
    }
}

/**
 * Banner for Active Workout or Active Cardio session
 */
@Composable
fun ActiveSessionBanner(
    title: String,
    subtitle: String,
    tag: String,
    durationSeconds: Long,
    actionLabel: String,
    icon: ImageVector,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("banner_active_session"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF170F2A)),
        border = BorderStroke(1.2.dp, Color(0xFFA855F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF22C55E).copy(alpha = 0.2f))
                        .border(1.dp, Color(0xFF22C55E), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = tag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF22C55E),
                        letterSpacing = 1.sp
                    )
                }

                val minutes = durationSeconds / 60
                val seconds = durationSeconds % 60
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC4B5FD)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAction,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF7C3AED),
                                Color(0xFF9333EA),
                                Color(0xFFA855F7)
                            )
                        )
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = actionLabel, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

/**
 * 3. EssentialMetricsRow (3 cards compactos, Row com weight igual)
 * - Card 1: 🔥 flame (orange) + "12" + "Dias seguidos"
 * - Card 2: 🏋️ dumbbell (purple) + "2 / 5" + "Treinos semanais"
 * - Card 3: 🎯 target (green) + "78" + "Seu foco hoje"
 */
@Composable
fun EssentialMetricsRow(
    streakDays: Int,
    weeklyDone: Int,
    weeklyGoal: Int,
    readinessScore: Int,
    hasSufficientData: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Card 1: Streak
        EssentialMetricCard(
            icon = Icons.Default.LocalFireDepartment,
            iconTint = Color(0xFFFB923C), // Amber/Orange Flame
            value = if (hasSufficientData) streakDays.toString() else "--",
            label = "Dias seguidos",
            testTag = "card_metric_streak",
            modifier = Modifier.weight(1f)
        )

        // Card 2: Weekly Workouts
        EssentialMetricCard(
            icon = Icons.Default.FitnessCenter,
            iconTint = Color(0xFFA855F7), // Purple/Lilac Dumbbell
            value = if (hasSufficientData) "$weeklyDone / $weeklyGoal" else "--",
            label = "Treinos semanais",
            testTag = "card_metric_weekly",
            modifier = Modifier.weight(1f)
        )

        // Card 3: Focus / Readiness
        EssentialMetricCard(
            icon = Icons.Default.GpsFixed,
            iconTint = Color(0xFF22C55E), // Emerald/Green Target
            value = if (hasSufficientData) readinessScore.toString() else "--",
            label = "Seu foco hoje",
            testTag = "card_metric_focus",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EssentialMetricCard(
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF130E20)),
        border = BorderStroke(1.dp, Color(0xFF2E204A).copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 14.sp
            )
        }
    }
}

/**
 * 4. WeeklyProgressCard
 * - Header: Bar chart icon + "Seu progresso" + Chevron right (leads to Dashboard)
 * - Row: "Treinos da semana" ... "2 / 5"
 * - Horizontal Progress Bar
 * - 7 Day circles (S T Q Q S S D)
 */
@Composable
fun WeeklyProgressCard(
    weeklyDone: Int,
    weeklyGoal: Int,
    dayStatuses: List<DayProgressStatus>,
    onOpenDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenDashboard() }
            .testTag("card_weekly_progress"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF130E20)),
        border = BorderStroke(1.dp, Color(0xFF2E204A).copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = Color(0xFFA855F7),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Seu progresso",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Ir para o painel",
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Workouts count line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Treinos da semana",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )

                val countText = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)) {
                        append("$weeklyDone ")
                    }
                    withStyle(SpanStyle(color = Color(0xFF94A3B8), fontWeight = FontWeight.Normal, fontSize = 14.sp)) {
                        append("/ $weeklyGoal")
                    }
                }
                Text(text = countText)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal Progress Bar
            val progressFraction = if (weeklyGoal > 0) (weeklyDone.toFloat() / weeklyGoal).coerceIn(0f, 1f) else 0f
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF241838))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progressFraction)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF8B5CF6),
                                    Color(0xFFA855F7)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7 Circular Day Indicators (S T Q Q S S D)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                dayStatuses.forEach { day ->
                    DayCircleIndicator(status = day)
                }
            }
        }
    }
}

@Composable
fun DayCircleIndicator(status: DayProgressStatus) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        when {
            status.isCompleted -> {
                // Filled Green Circle with Checkmark
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Concluído",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = status.dayLetter,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF22C55E)
                )
            }
            status.isCurrentOrNext -> {
                // Hollow circle with purple border
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFFA855F7), CircleShape)
                        .background(Color.Transparent)
                )
                Text(
                    text = status.dayLetter,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA855F7)
                )
            }
            else -> {
                // Hollow circle with gray border (future days)
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color(0xFF3F3F46), CircleShape)
                        .background(Color.Transparent)
                )
                Text(
                    text = status.dayLetter,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF71717A)
                )
            }
        }
    }
}

/**
 * 5. MotivationCard
 * - Quote icon in purple
 * - Italic text: "Pequenas ações diárias constroem grandes resultados."
 * - Subtle purple decorative bar
 */
@Composable
fun MotivationCard(
    quote: String = "Pequenas ações diárias constroem grandes resultados.",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_motivation"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF130E20)),
        border = BorderStroke(1.dp, Color(0xFF2E204A).copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "“",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFA855F7),
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quote,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(2.5.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFA855F7))
                )
            }
        }
    }
}

/**
 * 6. AppSideDrawer
 * Clean, organized side navigation drawer hosting all secondary features:
 * - User Profile & Avatar
 * - Workout History
 * - Health Connect
 * - Coach PRL09
 * - AI Nutrition & Workouts
 * - Settings & Reminders
 * - Data Export
 * - About / Version
 */
@Composable
fun AppSideDrawer(
    userProfile: UserProfile?,
    onCloseDrawer: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenHealthConnect: () -> Unit,
    onOpenCoach: () -> Unit,
    onOpenNutrition: () -> Unit,
    onOpenWorkoutGenerator: () -> Unit,
    onOpenReminders: () -> Unit,
    onOpenDataExport: () -> Unit,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
            .width(310.dp)
            .fillMaxHeight(),
        drawerContainerColor = Color(0xFF0F0B1A),
        drawerTonalElevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Header: User Profile
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onCloseDrawer()
                                onOpenProfile()
                            }
                    ) {
                        UserAvatarView(
                            photoUri = userProfile?.photoUri,
                            userName = userProfile?.name ?: "Paulo",
                            size = 46.dp,
                            showEditBadge = false
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = userProfile?.name ?: "Paulo",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = userProfile?.goal?.label ?: "Atleta FitPro PRL09",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    IconButton(onClick = onCloseDrawer) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar menu",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFF2E204A).copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(12.dp))

                // Menu Items
                DrawerMenuItem(
                    icon = Icons.Default.Person,
                    title = "Meu Perfil & Metas",
                    subtitle = "Dados corporais e foco semanal",
                    tint = Color(0xFFA855F7),
                    onClick = {
                        onCloseDrawer()
                        onOpenProfile()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.History,
                    title = "Histórico de Treinos",
                    subtitle = "Todas as sessões e cardio anteriores",
                    tint = Color(0xFF38BDF8),
                    onClick = {
                        onCloseDrawer()
                        onOpenHistory()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.Watch,
                    title = "Saúde & Health Connect",
                    subtitle = "Sincronização com relógio e sensores",
                    tint = Color(0xFF22C55E),
                    onClick = {
                        onCloseDrawer()
                        onOpenHealthConnect()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.Psychology,
                    title = "Coach IA PRL09",
                    subtitle = "Instruções inteligentes de treino",
                    tint = Color(0xFFC084FC),
                    onClick = {
                        onCloseDrawer()
                        onOpenCoach()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.Restaurant,
                    title = "Nutrição & Dieta IA",
                    subtitle = "Sugestões de calorias e macros",
                    tint = Color(0xFFF59E0B),
                    onClick = {
                        onCloseDrawer()
                        onOpenNutrition()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.AutoAwesome,
                    title = "Gerador de Fichas IA",
                    subtitle = "Criar novos treinos personalizados",
                    tint = Color(0xFFA78BFA),
                    onClick = {
                        onCloseDrawer()
                        onOpenWorkoutGenerator()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.Notifications,
                    title = "Configurações & Lembretes",
                    subtitle = "Horários de treino e alertas",
                    tint = Color(0xFFE2E8F0),
                    onClick = {
                        onCloseDrawer()
                        onOpenReminders()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.CloudDownload,
                    title = "Exportar Dados",
                    subtitle = "Backup em JSON e relatórios",
                    tint = Color(0xFF94A3B8),
                    onClick = {
                        onCloseDrawer()
                        onOpenDataExport()
                    }
                )
            }

            // Footer: Version & Info
            Column {
                HorizontalDivider(color = Color(0xFF2E204A).copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onCloseDrawer()
                            onOpenAbout()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF71717A),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FitPro PRL09 • v1.0",
                            fontSize = 12.sp,
                            color = Color(0xFF71717A)
                        )
                    }
                    Text(
                        text = "Sobre",
                        fontSize = 12.sp,
                        color = Color(0xFFA855F7),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
