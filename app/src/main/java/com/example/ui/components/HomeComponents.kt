package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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
    val userName: String = "Atleta",
    val todayWorkoutTitle: String = "Treino do Dia",
    val todayMuscleGroup: String = "",
    val hasPlannedWorkout: Boolean = false,
    val isWorkoutActive: Boolean = false,
    val activeWorkoutTitle: String = "",
    val activeWorkoutDurationSeconds: Long = 0L,
    val isCardioActive: Boolean = false,
    val activeCardioTitle: String = "",
    val activeCardioDurationMinutes: Int = 0,
    val streakDays: Int = 0,
    val weeklyDoneCount: Int = 0,
    val weeklyGoalTarget: Int = 5,
    val readinessScore: Int? = null,
    val readinessLabel: String = "",
    val hasReadinessData: Boolean = false,
    val hasSufficientData: Boolean = true,
    val weeklyDayStatuses: List<DayProgressStatus> = emptyList()
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
                    .size(38.dp)
                    .testTag("btn_home_drawer")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menu lateral",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "Olá, $userName",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Disciplina hoje, resultados amanhã.",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
        }

        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier
                .size(38.dp)
                .testTag("btn_home_notifications")
        ) {
            BadgedBox(
                badge = {
                    if (hasUnreadNotification) {
                        Badge(
                            containerColor = Color(0xFFA855F7),
                            modifier = Modifier.size(7.dp)
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificações e Lembretes",
                    tint = Color(0xFFC084FC),
                    modifier = Modifier.size(23.dp)
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF11101A))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(288.dp)
        ) {
            // Background Artwork: Asset oficial ou Artwork de altíssima fidelidade à referência
            Image(
                painter = painterResource(id = R.drawable.img_hero_workout_card),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradiente escuro suave no lado esquerdo para contraste e legibilidade impecável dos textos
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF080712).copy(alpha = 0.94f),
                                Color(0xFF080712).copy(alpha = 0.72f),
                                Color(0xFF080712).copy(alpha = 0.18f),
                                Color.Transparent
                            ),
                            startX = 0f,
                            endX = 820f
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
                                Color(0xFF080712).copy(alpha = 0.24f),
                                Color(0xFF080712).copy(alpha = 0.82f)
                            ),
                            startY = 170f
                        )
                    )
            )

            // Foreground Content idêntico à referência visual do usuário
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    val headline = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 27.sp,
                                letterSpacing = 0.3.sp
                            )
                        ) {
                            append("UM DIA\nMAIS FORTE\nCOMEÇA\n")
                        }
                        withStyle(
                            SpanStyle(
                                color = Color(0xFFC084FC), // Lilás / violeta luminoso da referência
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 27.sp,
                                letterSpacing = 0.3.sp
                            )
                        ) {
                            append("AGORA.")
                        }
                    }

                    Text(
                        text = headline,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Disciplina é liberdade.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFCBD5E1)
                    )
                }

                // CTA Button: Estilo vibrante idêntico à referência
                val (ctaLabel, ctaAction) = when {
                    !state.hasSufficientData -> Pair("Configurar / registrar dados", onConfigureData)
                    !state.hasPlannedWorkout -> Pair("Escolher treino", onSelectWorkout)
                    else -> Pair("Iniciar treino de hoje", onStartWorkout)
                }

                Button(
                    onClick = ctaAction,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C3AED) // Roxo sólido vibrante da referência
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
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
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ctaLabel,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3. EssentialMetricsRow (3 cards compactos, Row com weight igual)
 * - Card 1: 🔥 flame (orange) + streak real (ex: "12" ou "0") + "Dias seguidos"
 * - Card 2: 🏋️ dumbbell (purple) + "2 / 5" + "Treinos semanais"
 * - Card 3: 🎯 target (green) + real score (ex: "78" ou "—") + "Seu foco hoje"
 */
@Composable
fun EssentialMetricsRow(
    streakDays: Int,
    weeklyDone: Int,
    weeklyGoal: Int,
    readinessScore: Int?,
    readinessLabel: String = "",
    hasReadinessData: Boolean,
    onReadinessClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Card 1: Streak (dados reais)
        EssentialMetricCard(
            icon = Icons.Default.LocalFireDepartment,
            iconTint = Color(0xFFF97316), // Laranja vibrante da referência
            value = if (streakDays > 0) "$streakDays" else "0",
            label = "Dias seguidos",
            testTag = "card_metric_streak",
            modifier = Modifier.weight(1f)
        )

        // Card 2: Weekly Workouts (dados reais)
        EssentialMetricCard(
            icon = Icons.Default.FitnessCenter,
            iconTint = Color(0xFFC084FC), // Lilás / violeta luminoso da referência
            value = "$weeklyDone / $weeklyGoal",
            label = "Treinos semanais",
            testTag = "card_metric_weekly",
            modifier = Modifier.weight(1f)
        )

        // Card 3: Focus / Readiness (dados reais com affordance para check-in)
        EssentialMetricCard(
            icon = Icons.Default.GpsFixed,
            iconTint = Color(0xFF22C55E), // Verde esmeralda da referência
            customIcon = {
                TargetFocusIcon(
                    tint = Color(0xFF22C55E),
                    modifier = Modifier.size(24.dp)
                )
            },
            value = if (hasReadinessData && readinessScore != null) "$readinessScore" else "—",
            label = "Seu foco hoje",
            testTag = "card_metric_focus",
            onClick = onReadinessClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EssentialMetricsRow(
    streakDays: Int,
    weeklyDone: Int,
    weeklyGoal: Int,
    readinessScore: Int,
    hasSufficientData: Boolean,
    modifier: Modifier = Modifier
) {
    EssentialMetricsRow(
        streakDays = streakDays,
        weeklyDone = weeklyDone,
        weeklyGoal = weeklyGoal,
        readinessScore = if (hasSufficientData) readinessScore else null,
        readinessLabel = if (hasSufficientData) "Readiness" else "",
        hasReadinessData = hasSufficientData,
        onReadinessClick = {},
        modifier = modifier
    )
}

@Composable
fun EssentialMetricCard(
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String,
    testTag: String,
    customIcon: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF12101B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 11.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (customIcon != null) {
                customIcon()
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = label,
                fontSize = 10.sp,
                color = Color(0xFF9CA3AF),
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
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF12101B))
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

            Spacer(modifier = Modifier.height(10.dp))

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
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF251A3C))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progressFraction)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
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

            Spacer(modifier = Modifier.height(12.dp))

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
        if (status.isCompleted) {
            // Círculo Verde Preenchido com Checkmark
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Concluído",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
            Text(
                text = status.dayLetter,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
            )
        } else {
            // Círculo com Anel Roxo Fino (Dias futuros ou pendentes)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color(0xFF581C87), CircleShape)
                    .background(Color.Transparent)
            )
            Text(
                text = status.dayLetter,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF94A3B8)
            )
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
    quote: String = "Pequenas ações diárias\nconstroem grandes resultados.",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_motivation"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF12101B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "“",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFA855F7),
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quote,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .width(28.dp)
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
