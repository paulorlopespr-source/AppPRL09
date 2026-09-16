package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MobileOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.Widgets
import com.example.ui.components.FullWorkoutHistorySheet
import com.example.ui.components.WorkoutSubstitutionDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat
import com.example.ui.components.OutdoorGpsPromptDialog
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CardioSession
import com.example.data.model.CardioType
import com.example.data.model.IntensityLevel
import com.example.data.model.MedalRarity
import com.example.data.model.SessionStatus
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutTemplate
import com.example.ui.components.AIEvaluationDialog
import com.example.ui.components.AIInsightCard
import com.example.ui.components.AINutritionDialog
import com.example.ui.components.ActiveCardioTrackerModal
import com.example.ui.components.CentralMenuSheet
import com.example.ui.components.HealthConnectDialog
import com.example.ui.components.BentoCard
import com.example.ui.components.DateUtils
import com.example.ui.components.LiquidGlassSurface
import com.example.ui.components.MetricBentoCard
import com.example.ui.components.PrimaryButton
import com.example.ui.components.ScalableVectorMedalBadge
import com.example.ui.components.SecondaryButton
import com.example.ui.components.SupersetHeaderBadge
import com.example.ui.components.UserAvatarView
import com.example.ui.components.UserProfileDialog
import com.example.ui.components.UserProfileMenuSheet
import com.example.ui.components.DataExportDialog
import com.example.ui.components.AIWorkoutGeneratorDialog
import com.example.ui.components.QuickWorkoutSheet
import com.example.ui.components.WorkoutReminderDialog
import com.example.ui.theme.AmberSubtle
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassSurfaceDeep
import com.example.ui.theme.GlowPurple
import com.example.ui.theme.GradientAction
import com.example.ui.theme.GradientCardDeep
import com.example.ui.theme.GradientHeroPrimary
import com.example.ui.theme.GradientVibrant
import com.example.ui.theme.LilacAccent
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurplePrimaryDark
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FitnessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FitnessViewModel,
    onNavigateToWorkouts: () -> Unit,
    onNavigateToActiveWorkout: () -> Unit,
    onNavigateToCardio: () -> Unit,
    onNavigateToAgenda: () -> Unit,
    onNavigateToEvolution: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val activeWorkout by viewModel.activeWorkout.collectAsStateWithLifecycle()
    val activeCardio by viewModel.activeCardio.collectAsStateWithLifecycle()
    val workoutSessions by viewModel.allWorkoutSessions.collectAsStateWithLifecycle()
    val cardioSessions by viewModel.allCardioSessions.collectAsStateWithLifecycle()
    val workoutTemplates by viewModel.workoutTemplates.collectAsStateWithLifecycle()
    val bodyMeasurements by viewModel.allBodyMeasurements.collectAsStateWithLifecycle()

    var selectedSessionAiFeedback by remember { mutableStateOf<String?>(null) }
    var showAiDialog by remember { mutableStateOf(false) }
    var showNutritionDialog by remember { mutableStateOf(false) }
    var showHealthConnectDialog by remember { mutableStateOf(false) }
    var showCentralMenu by remember { mutableStateOf(false) }
    var showProfileMenuSheet by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showDataExportDialog by remember { mutableStateOf(false) }
    var showWorkoutGeneratorDialog by remember { mutableStateOf(false) }
    var showWorkoutReminderDialog by remember { mutableStateOf(false) }
    var showQuickWorkoutSheet by remember { mutableStateOf(false) }
    var showActiveCardioModal by remember { mutableStateOf(false) }
    var showWorkoutSubstitutionDialog by remember { mutableStateOf(false) }
    var showFullWorkoutHistorySheet by remember { mutableStateOf(false) }
    var showGpsPromptDialog by remember { mutableStateOf(false) }
    var pendingOutdoorCardioType by remember { mutableStateOf<CardioType?>(null) }

    val context = LocalContext.current
    val gpsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val granted = fineGranted || coarseGranted
        val targetType = pendingOutdoorCardioType ?: CardioType.CAMINHADA_AR_LIVRE
        viewModel.startLiveCardio(
            type = targetType,
            location = if (targetType == CardioType.TRILHA) "Trilha / Trekking" else "Rua / Parque",
            intensity = IntensityLevel.MODERADA,
            targetMinutes = 25,
            enableGps = granted
        )
        showActiveCardioModal = true
        pendingOutdoorCardioType = null
    }

    val onOutdoorCardioClick: (CardioType) -> Unit = { type ->
        pendingOutdoorCardioType = type
        showGpsPromptDialog = true
    }

    val healthDailyMetrics by viewModel.healthDailyMetrics.collectAsStateWithLifecycle()
    val permissionsGranted by viewModel.healthPermissionsGranted.collectAsStateWithLifecycle()
    val isGeneratingAIWorkout by viewModel.isGeneratingAIWorkout.collectAsStateWithLifecycle()
    val generatedAIWorkout by viewModel.generatedAIWorkout.collectAsStateWithLifecycle()
    val reminderSettings by viewModel.reminderSettings.collectAsStateWithLifecycle()
    val gamificationOverview by viewModel.gamificationOverview.collectAsStateWithLifecycle()
    val dailySuggestion by viewModel.dailyWorkoutSuggestion.collectAsStateWithLifecycle()
    val keepScreenOn by viewModel.keepScreenOn.collectAsStateWithLifecycle()

    val todayEpoch = DateUtils.todayEpochDay()
    val completedWorkouts = workoutSessions.filter { it.status == SessionStatus.COMPLETED }

    // Count this week's workouts (e.g. 4/5)
    val currentWeekDays = (0..6).map { todayEpoch - (todayEpoch % 7) + it }
    val weeklyDoneCount = completedWorkouts.count { it.dateEpochDay in currentWeekDays }
    val weeklyGoalTarget = userProfile?.weeklyGoalDays ?: 5

    // Choose default or favorite template for Today's Workout
    val todayTemplate: WorkoutTemplate? = workoutTemplates.firstOrNull { it.isFavorite }
        ?: workoutTemplates.firstOrNull()

    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PurpleDarkest)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = topInset + 16.dp, bottom = bottomInset + 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ==========================================
        // 1. TOP HEADER (Foto do Usuário, Saudação & Menu Central de Informações)
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // User Avatar with photo preview and edit trigger
                    UserAvatarView(
                        photoUri = userProfile?.photoUri,
                        userName = userProfile?.name ?: "Atleta",
                        size = 46.dp,
                        showEditBadge = true,
                        onClick = { showProfileMenuSheet = true },
                        modifier = Modifier.testTag("btn_header_profile")
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Olá, ${userProfile?.name ?: "Atleta"}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (activeWorkout.isActive) "Treino em andamento" else "Seu treino está pronto.",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = if (activeWorkout.isActive) EmeraldSuccess else LilacSoft
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Smartwatch / Health Connect Hub Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (permissionsGranted) EmeraldSubtle else PurpleDarkSurface
                            )
                            .border(
                                1.dp,
                                if (permissionsGranted) EmeraldSuccess else GlassBorderSubtle,
                                CircleShape
                            )
                            .clickable { showHealthConnectDialog = true }
                            .testTag("btn_header_health_connect"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Watch,
                            contentDescription = "Smartwatch & Health Connect",
                            tint = if (permissionsGranted) EmeraldSuccess else LilacAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Central Menu Trigger Button (Menu com todas as informações)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PurpleDeepCard)
                            .border(1.2.dp, LilacAccent, CircleShape)
                            .clickable { showCentralMenu = true }
                            .testTag("btn_header_menu"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = "Menu Principal",
                            tint = LilacAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 2. HERO CARD — TREINO DE HOJE (FULL BODY A) OU CARDIO ATIVO
        // ==========================================
        if (activeCardio.isActive) {
            item {
                LiquidGlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .clickable { showActiveCardioModal = true }
                        .testTag("banner_active_cardio"),
                    shape = RoundedCornerShape(26.dp),
                    backgroundColor = PurpleDeepCard,
                    borderColor = EmeraldSuccess
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(EmeraldSubtle)
                                    .border(1.dp, EmeraldSuccess, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldSuccess)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (activeCardio.isPaused) "CARDIO PAUSADO" else "CARDIO EM ANDAMENTO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldSuccess,
                                    letterSpacing = 1.sp
                                )
                            }

                            Text(
                                text = DateUtils.formatSecondsToTime(activeCardio.durationSeconds),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldSuccess
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = activeCardio.type.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${activeCardio.location} • Ritmo: ${if (activeCardio.paceMinKm > 0) String.format("%.1f min/km", activeCardio.paceMinKm) else "--"} • Distância: ${String.format("%.2f", activeCardio.distanceKm)} km",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PrimaryButton(
                            text = "VER TEMPO & GPS EM TELA CHEIA",
                            icon = Icons.Default.DirectionsRun,
                            onClick = { showActiveCardioModal = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        item {
            if (activeWorkout.isActive) {
                // Live Active Workout Banner
                LiquidGlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToActiveWorkout() }
                        .testTag("banner_active_workout"),
                    shape = RoundedCornerShape(26.dp),
                    backgroundColor = PurpleDeepCard,
                    borderColor = LilacAccent
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(EmeraldSubtle)
                                    .border(1.dp, EmeraldSuccess, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldSuccess)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "EM ANDAMENTO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldSuccess,
                                    letterSpacing = 1.sp
                                )
                            }

                            Text(
                                text = DateUtils.formatSecondsToTime(activeWorkout.durationSeconds),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = LilacSoft
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = activeWorkout.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${activeWorkout.location} • Cronômetro inteligente ativo",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PrimaryButton(
                            text = "CONTINUAR TREINO",
                            icon = Icons.Default.PlayArrow,
                            onClick = onNavigateToActiveWorkout,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                // Today's Intelligent Suggested Workout Card
                val currentTemplate = dailySuggestion.template ?: todayTemplate
                val templateTitle = dailySuggestion.title
                val templateDuration = dailySuggestion.estimatedDurationMinutes
                val templateCalories = (templateDuration * 5.9).toInt().coerceAtLeast(248)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 14.dp,
                            shape = RoundedCornerShape(26.dp),
                            ambientColor = GlowPurple,
                            spotColor = LilacAccent
                        )
                        .testTag("card_today_workout"),
                    shape = RoundedCornerShape(26.dp),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GradientHeroPrimary)
                            .border(1.2.dp, LilacSoft.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
                            .padding(20.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "SUGESTÃO DO DIA • ${dailySuggestion.dayOfWeekName.uppercase()}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Surface(
                                    onClick = { showWorkoutSubstitutionDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    color = PurpleDarkest.copy(alpha = 0.45f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent.copy(alpha = 0.5f)),
                                    modifier = Modifier.testTag("btn_substitute_daily_workout")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SwapHoriz,
                                            contentDescription = "Substituir Treino",
                                            tint = LilacAccent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Substituir",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LilacAccent
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = templateTitle,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = dailySuggestion.subtitle,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LilacSoft
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // AI Explanation
                            Text(
                                text = dailySuggestion.explanationReason,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Muscle group badges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                dailySuggestion.muscleGroups.take(4).forEach { group ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(PurpleDarkest.copy(alpha = 0.35f))
                                            .border(0.8.dp, LilacSoft.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 7.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = group.displayName,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LilacSoft
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Metrics Badges inside Hero Card
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$templateDuration min",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FitnessCenter,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${currentTemplate?.exerciseCount ?: 6} exercícios",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "~$templateCalories kcal",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Main Call-To-Action Button: INICIAR TREINO
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (currentTemplate != null) {
                                            viewModel.startWorkoutFromTemplate(
                                                template = currentTemplate,
                                                location = userProfile?.defaultGymLocation ?: "Academia"
                                            )
                                            onNavigateToActiveWorkout()
                                        } else {
                                            onNavigateToWorkouts()
                                        }
                                    }
                                    .testTag("btn_start_today_workout"),
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 14.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = PurplePrimaryDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "INICIAR TREINO DO DIA",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = PurplePrimaryDark,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Access: Training History & Screen On Quick Toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Ver Histórico Completo
                Surface(
                    onClick = { showFullWorkoutHistorySheet = true },
                    shape = RoundedCornerShape(16.dp),
                    color = PurpleDeepCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_home_history")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PurpleDarkSurface)
                                .border(1.dp, LilacAccent.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = LilacAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Histórico",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${completedWorkouts.size} realizados",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Toggle Tela Ativa
                Surface(
                    onClick = { viewModel.toggleKeepScreenOn() },
                    shape = RoundedCornerShape(16.dp),
                    color = if (keepScreenOn) EmeraldSubtle else PurpleDeepCard,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (keepScreenOn) EmeraldSuccess.copy(alpha = 0.5f) else GlassBorderSubtle
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_home_keep_screen_on")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (keepScreenOn) EmeraldSuccess.copy(alpha = 0.2f) else PurpleDarkSurface)
                                .border(1.dp, if (keepScreenOn) EmeraldSuccess else GlassBorderSubtle, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (keepScreenOn) Icons.Default.PhoneAndroid else Icons.Default.MobileOff,
                                contentDescription = null,
                                tint = if (keepScreenOn) EmeraldSuccess else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Tela Ativa",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (keepScreenOn) EmeraldSuccess else TextPrimary
                            )
                            Text(
                                text = if (keepScreenOn) "Ligada" else "Padrão",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (keepScreenOn) EmeraldSuccess else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2.5 QUICK WORKOUT & FAST CARDIO ACCESS (CAMINHADA, CORRIDA & FORÇA)
        // ==========================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Banner Principal Treino Rápido
                LiquidGlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { showQuickWorkoutSheet = true }
                        .testTag("btn_quick_workout"),
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = PurpleDarkSurface,
                    borderColor = AmberWarning.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
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
                                            colors = listOf(AmberWarning, PurplePrimary, PurpleVibrant)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "TREINO RÁPIDO",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AmberSubtle)
                                            .border(0.8.dp, AmberWarning, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "10 a 20 min",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AmberWarning
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Caminhada, Corrida com GPS ou Força Express",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(GradientAction)
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Abrir",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }

                // Atalhos Diretos de 1 Toque: Caminhada, Corrida e Trilha (Opção de Ativar GPS)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Atalho 1: Caminhada
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, EmeraldSuccess.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .clickable {
                                onOutdoorCardioClick(CardioType.CAMINHADA_AR_LIVRE)
                            }
                            .testTag("btn_shortcut_walk"),
                        color = PurpleDeepCard
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSubtle),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsWalk,
                                    contentDescription = null,
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Caminhada",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "GPS / Mapa",
                                fontSize = 9.sp,
                                color = EmeraldSuccess,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Atalho 2: Corrida
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, LilacAccent.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .clickable {
                                onOutdoorCardioClick(CardioType.CORRIDA)
                            }
                            .testTag("btn_shortcut_run"),
                        color = PurpleDeepCard
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(LilacAccent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsRun,
                                    contentDescription = null,
                                    tint = LilacAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Corrida",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Ritmo + GPS",
                                fontSize = 9.sp,
                                color = LilacSoft,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Atalho 3: Trilha / Trekking
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFFF9800).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .clickable {
                                onOutdoorCardioClick(CardioType.TRILHA)
                            }
                            .testTag("btn_shortcut_trail"),
                        color = PurpleDeepCard
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF9800).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Landscape,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9800),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Trilha",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Relevo + GPS",
                                fontSize = 9.sp,
                                color = Color(0xFFFFB74D),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. BENTO GRID (2x2 Metrics + Weekly Progress)
        // ==========================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Bento Row 1: Calorias & Duração
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBentoCard(
                        iconEmoji = "🔥",
                        value = "248",
                        label = "kcal previstas",
                        accentColor = LilacAccent,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBentoCard(
                        iconEmoji = "⏱️",
                        value = "42 min",
                        label = "tempo treino",
                        accentColor = PurpleVibrant,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Bento Row 2: Treinos da Semana & Sequência
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBentoCard(
                        iconEmoji = "🎯",
                        value = "$weeklyDoneCount/$weeklyGoalTarget",
                        label = "treinos semanais",
                        accentColor = EmeraldSuccess,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBentoCard(
                        iconEmoji = "⚡",
                        value = "3 dias",
                        label = "sequência ativa",
                        accentColor = LilacSoft,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Bento Row 3: Progresso Semanal
                val weeklyProgressPercent = if (weeklyGoalTarget > 0) {
                    ((weeklyDoneCount.toFloat() / weeklyGoalTarget.toFloat()) * 100).toInt().coerceIn(0, 100)
                } else 82

                BentoCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToEvolution() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PROGRESSO SEMANAL",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = LilacSoft,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "$weeklyProgressPercent%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = LilacAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Vibrant Glowing Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(PurpleDarkest)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = weeklyProgressPercent / 100f)
                                .height(10.dp)
                                .clip(CircleShape)
                                .background(GradientVibrant)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$weeklyDoneCount de $weeklyGoalTarget sessões concluídas esta semana",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // ==========================================
        // 4. ✦ ASSISTENTE DE TREINO IA (Contextual Insight)
        // ==========================================
        item {
            AIInsightCard(
                title = "Sugestão para hoje",
                message = "Seu último treino de peito foi há 3 dias. Você pode manter a carga atual e tentar aumentar uma repetição no Supino Reto.",
                actionLabel = "Ver Análise IA",
                onActionClick = {
                    selectedSessionAiFeedback = "Análise Contextual IA:\n• Frequência Semanal: Excelente (4/5 sessões).\n• Carga Total Progressiva: 8.420 kg (+5.2% vs semana anterior).\n• Recomendação: No Full Body A de hoje, mantenha os 60 kg no Supino Reto e tente buscar 11 reps na primeira série!"
                    showAiDialog = true
                }
            )
        }

        // ==========================================
        // 4.1 🥗 NUTRIÇÃO IA & GAMIFICAÇÃO RÁPIDA
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Nutrition & Meal Estimator Card
                Surface(
                    color = PurpleDeepCard,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showNutritionDialog = true }
                        .testTag("btn_open_nutrition_ai")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSubtle)
                                    .border(1.dp, EmeraldSuccess.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = null,
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "✦ IA Gemini",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = LilacAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Nutrição & Calorias",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Estimar refeição por IA",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                // Gamification / Trophies Card
                Surface(
                    color = PurpleDeepCard,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToEvolution() }
                        .testTag("btn_open_gamification_hub")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ScalableVectorMedalBadge(
                                rarity = MedalRarity.MASTER_SUPERAÇÃO,
                                isUnlocked = true,
                                size = 34.dp,
                                iconEmoji = "👑",
                                animated = true
                            )
                            Text(
                                text = "Medalhas",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Recordes & Conquistas",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Metas e PRs batidos",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // ==========================================
        // 4.2 ⌚ SMARTWATCH & HEALTH CONNECT SYNC CARD
        // ==========================================
        item {
            Surface(
                color = PurpleDeepCard,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (permissionsGranted) EmeraldSuccess.copy(alpha = 0.5f) else LilacAccent.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showHealthConnectDialog = true }
                    .testTag("btn_open_health_connect_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (permissionsGranted) EmeraldSubtle else PurpleDarkSurface
                                )
                                .border(
                                    1.dp,
                                    if (permissionsGranted) EmeraldSuccess else LilacAccent.copy(alpha = 0.5f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Watch,
                                contentDescription = null,
                                tint = if (permissionsGranted) EmeraldSuccess else LilacAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Smartwatch & Health Connect",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                if (permissionsGranted) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldSuccess)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = if (permissionsGranted) {
                                    "👟 ${"%,d".format(healthDailyMetrics.steps)} passos • 🔥 ${healthDailyMetrics.totalCaloriesBurned.toInt()} kcal hoje"
                                } else {
                                    "Sincronizar com Galaxy Watch, Garmin, Wear OS"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (permissionsGranted) LilacSoft else TextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Abrir Sincronização",
                        tint = LilacAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // ==========================================
        // 5. SUPERSET HIGHLIGHTS (Estrutura do Treino)
        // ==========================================
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Superséries de Hoje",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    TextButton(onClick = onNavigateToWorkouts) {
                        Text(
                            text = "Ver Todos",
                            color = LilacAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SupersetPreviewRow(
                        number = 1,
                        firstExercise = "Supino Reto",
                        secondExercise = "Remada Curvada",
                        setsReps = "4 séries × 10-12 reps"
                    )
                    SupersetPreviewRow(
                        number = 2,
                        firstExercise = "Terra Romeno",
                        secondExercise = "Desenvolvimento",
                        setsReps = "3 séries × 10 reps"
                    )
                    SupersetPreviewRow(
                        number = 3,
                        firstExercise = "Rosca Direta",
                        secondExercise = "Tríceps Pulley",
                        setsReps = "3 séries × 12 reps"
                    )
                }
            }
        }

        // ==========================================
        // 6. TIMELINE / HISTÓRICO VISUAL
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Histórico de Treinos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                TextButton(onClick = onNavigateToAgenda) {
                    Text(
                        text = "Timeline Completa",
                        color = LilacAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        if (workoutSessions.isEmpty() && cardioSessions.isEmpty()) {
            // Default styled sample items matching the prompt specification
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TimelineItemCard(
                        tag = "HOJE",
                        title = "Full Body A",
                        duration = "42 min",
                        calories = "248 kcal",
                        exercisesSummary = "Supino, Remada, Terra, Rosca",
                        onClick = onNavigateToWorkouts
                    )
                    TimelineItemCard(
                        tag = "TERÇA",
                        title = "Full Body B",
                        duration = "51 min",
                        calories = "302 kcal",
                        exercisesSummary = "Agachamento, Puxada, Elevação Lateral",
                        onClick = onNavigateToWorkouts
                    )
                    TimelineItemCard(
                        tag = "DOMINGO",
                        title = "Cardio & HIIT",
                        duration = "35 min",
                        calories = "286 kcal",
                        exercisesSummary = "Bike Indoor + Corrida Leve",
                        onClick = onNavigateToCardio
                    )
                }
            }
        } else {
            items(workoutSessions.take(3)) { session ->
                TimelineWorkoutSessionCard(
                    session = session,
                    onViewAi = { feedback ->
                        selectedSessionAiFeedback = feedback
                        showAiDialog = true
                    }
                )
            }

            items(cardioSessions.take(2)) { cardio ->
                TimelineCardioSessionCard(cardio = cardio)
            }
        }
    }

    // AI Context Evaluation Dialog
    if (showAiDialog) {
        AIEvaluationDialog(
            title = "✦ Avaliação do Assistente IA",
            evaluationText = selectedSessionAiFeedback,
            isLoading = false,
            onDismiss = { showAiDialog = false }
        )
    }

    // AI Nutrition & Calorie Estimator Dialog
    if (showNutritionDialog) {
        AINutritionDialog(
            viewModel = viewModel,
            onDismiss = { showNutritionDialog = false }
        )
    }

    // Smartwatch & Health Connect Dialog
    if (showHealthConnectDialog) {
        HealthConnectDialog(
            viewModel = viewModel,
            onDismiss = { showHealthConnectDialog = false }
        )
    }

    // Central Menu Modal Sheet (All features organized cleanly)
    if (showCentralMenu) {
        CentralMenuSheet(
            userProfile = userProfile,
            gamificationOverview = gamificationOverview,
            isSmartwatchConnected = permissionsGranted,
            onDismiss = { showCentralMenu = false },
            onOpenEditProfile = { showProfileMenuSheet = true },
            onNavigateToWorkouts = onNavigateToWorkouts,
            onNavigateToActiveWorkout = onNavigateToActiveWorkout,
            onNavigateToCardio = onNavigateToCardio,
            onNavigateToAgenda = onNavigateToAgenda,
            onNavigateToEvolution = onNavigateToEvolution,
            onOpenAiWorkoutGenerator = { showWorkoutGeneratorDialog = true },
            onOpenNutritionAi = { showNutritionDialog = true },
            onOpenHealthConnect = { showHealthConnectDialog = true },
            onOpenReminders = { showWorkoutReminderDialog = true },
            onOpenQuickWorkout = { showQuickWorkoutSheet = true }
        )
    }

    // User Profile Menu Bottom Sheet
    if (showProfileMenuSheet && userProfile != null) {
        UserProfileMenuSheet(
            userProfile = userProfile!!,
            gamificationOverview = gamificationOverview,
            onDismiss = { showProfileMenuSheet = false },
            onOpenEditDialog = { showProfileDialog = true },
            onPhotoSelected = { uri ->
                viewModel.updateUserPhoto(uri)
            },
            onPhotoRemoved = {
                viewModel.removeUserPhoto()
            },
            onOpenHealthConnect = { showHealthConnectDialog = true },
            onOpenReminders = { showWorkoutReminderDialog = true },
            onOpenDataExport = { showDataExportDialog = true },
            onNavigateToEvolution = onNavigateToEvolution
        )
    }

    // Dedicated User Profile Field Edit Dialog
    if (showProfileDialog && userProfile != null) {
        UserProfileDialog(
            userProfile = userProfile!!,
            onDismiss = { showProfileDialog = false },
            onSaveProfile = { updated ->
                viewModel.updateUserProfile(updated)
                showProfileDialog = false
            },
            onPhotoSelected = { uri ->
                viewModel.updateUserPhoto(uri)
            },
            onPhotoRemoved = {
                viewModel.removeUserPhoto()
            }
        )
    }

    // Data Export & Backup Dialog
    if (showDataExportDialog) {
        DataExportDialog(
            userProfile = userProfile,
            workoutSessions = workoutSessions,
            cardioSessions = cardioSessions,
            measurements = bodyMeasurements,
            onDismiss = { showDataExportDialog = false }
        )
    }

    // AI Workout Generator Dialog
    if (showWorkoutGeneratorDialog) {
        AIWorkoutGeneratorDialog(
            isGenerating = isGeneratingAIWorkout,
            generatedWorkout = generatedAIWorkout,
            onGenerate = { prompt -> viewModel.generateAIWorkout(prompt) },
            onSaveTemplate = { plan ->
                viewModel.saveGeneratedAIWorkoutAsTemplate(plan)
                showWorkoutGeneratorDialog = false
            },
            onStartWorkoutNow = { plan ->
                viewModel.startWorkoutFromAIPlan(plan)
                showWorkoutGeneratorDialog = false
                onNavigateToActiveWorkout()
            },
            onDismiss = { showWorkoutGeneratorDialog = false }
        )
    }

    // Workout Reminders Dialog
    if (showWorkoutReminderDialog) {
        WorkoutReminderDialog(
            initialSettings = reminderSettings,
            onSaveSettings = { updated ->
                viewModel.updateReminderSettings(updated)
                showWorkoutReminderDialog = false
            },
            onTestNotification = {
                viewModel.triggerTestReminderNotification()
            },
            onDismiss = { showWorkoutReminderDialog = false }
        )
    }

    // Active Cardio Live Tracker Modal
    if (activeCardio.isActive && showActiveCardioModal) {
        ActiveCardioTrackerModal(
            viewModel = viewModel,
            onDismiss = { showActiveCardioModal = false }
        )
    }

    // Outdoor Cardio GPS Activation Prompt Dialog
    if (showGpsPromptDialog && pendingOutdoorCardioType != null) {
        OutdoorGpsPromptDialog(
            cardioType = pendingOutdoorCardioType!!,
            onConfirmGps = {
                showGpsPromptDialog = false
                val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                if (hasFine || hasCoarse) {
                    val targetType = pendingOutdoorCardioType ?: CardioType.CAMINHADA_AR_LIVRE
                    viewModel.startLiveCardio(
                        type = targetType,
                        location = if (targetType == CardioType.TRILHA) "Trilha / Trekking" else "Rua / Parque",
                        intensity = IntensityLevel.MODERADA,
                        targetMinutes = 25,
                        enableGps = true
                    )
                    showActiveCardioModal = true
                    pendingOutdoorCardioType = null
                } else {
                    gpsPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            onConfirmNoGps = {
                showGpsPromptDialog = false
                val targetType = pendingOutdoorCardioType ?: CardioType.CAMINHADA_AR_LIVRE
                viewModel.startLiveCardio(
                    type = targetType,
                    location = if (targetType == CardioType.TRILHA) "Trilha / Trekking" else "Rua / Parque",
                    intensity = IntensityLevel.MODERADA,
                    targetMinutes = 25,
                    enableGps = false
                )
                showActiveCardioModal = true
                pendingOutdoorCardioType = null
            },
            onDismiss = {
                showGpsPromptDialog = false
                pendingOutdoorCardioType = null
            }
        )
    }

    // Quick Workout Modal Sheet (Strength & Cardio Express)
    if (showQuickWorkoutSheet) {
        QuickWorkoutSheet(
            onDismiss = { showQuickWorkoutSheet = false },
            onStartStrengthWorkout = { title, location, plans ->
                viewModel.startWorkoutWithPlans(
                    title = title,
                    location = location,
                    plans = plans
                )
                showQuickWorkoutSheet = false
                onNavigateToActiveWorkout()
            },
            onStartCardio = { type, location, intensity, targetMinutes, enableGps ->
                viewModel.startLiveCardio(
                    type = type,
                    location = location,
                    intensity = intensity,
                    targetMinutes = targetMinutes,
                    enableGps = enableGps
                )
                showQuickWorkoutSheet = false
                showActiveCardioModal = true
            },
            defaultLocation = userProfile?.defaultGymLocation ?: "Academia Smart Fit"
        )
    }

    // Workout Substitution Dialog (for today's workout)
    if (showWorkoutSubstitutionDialog) {
        WorkoutSubstitutionDialog(
            currentTemplate = dailySuggestion.template ?: todayTemplate,
            availableTemplates = workoutTemplates,
            onDismiss = { showWorkoutSubstitutionDialog = false },
            onSelectTemplate = { selectedTemplate ->
                viewModel.overrideDailyWorkoutSuggestion(selectedTemplate)
                showWorkoutSubstitutionDialog = false
            }
        )
    }

    // Full Training History BottomSheet
    if (showFullWorkoutHistorySheet) {
        FullWorkoutHistorySheet(
            workoutSessions = workoutSessions,
            cardioSessions = cardioSessions,
            onDismiss = { showFullWorkoutHistorySheet = false },
            onDeleteWorkoutSession = { sessionId -> viewModel.deleteWorkoutSession(sessionId) },
            onDeleteCardioSession = { cardioId -> viewModel.deleteCardioSession(cardioId) },
            onViewAiFeedback = { aiFeedback ->
                selectedSessionAiFeedback = aiFeedback
                showAiDialog = true
            }
        )
    }
}

@Composable
private fun SupersetPreviewRow(
    number: Int,
    firstExercise: String,
    secondExercise: String,
    setsReps: String
) {
    BentoCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        borderWidth = 1.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SupersetHeaderBadge(number = number)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = setsReps,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = firstExercise,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "  ↓  ",
                        color = LilacAccent,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = secondExercise,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineItemCard(
    tag: String,
    title: String,
    duration: String,
    calories: String,
    exercisesSummary: String,
    onClick: () -> Unit
) {
    BentoCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LilacAccent.copy(alpha = 0.18f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = LilacAccent,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = exercisesSummary,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = LilacSoft,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = duration,
                    fontSize = 12.sp,
                    color = LilacSoft,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = calories,
                    fontSize = 12.sp,
                    color = EmeraldSuccess,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TimelineWorkoutSessionCard(
    session: WorkoutSession,
    onViewAi: (String) -> Unit
) {
    BentoCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_session_${session.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (session.status == SessionStatus.COMPLETED) EmeraldSubtle
                            else PurpleDeepCard
                        )
                        .border(
                            1.dp,
                            if (session.status == SessionStatus.COMPLETED) EmeraldSuccess else GlassBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = DateUtils.formatEpochDayShort(session.dateEpochDay).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = if (session.status == SessionStatus.COMPLETED) EmeraldSuccess else LilacAccent
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Text(
                text = "${session.durationSeconds / 60} min",
                style = MaterialTheme.typography.bodySmall,
                color = LilacSoft,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = LilacAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = session.location,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "~${session.estimatedCalories} kcal",
                    fontSize = 12.sp,
                    color = EmeraldSuccess,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (session.aiCaloricEvaluation.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = "Ver Análise da IA",
                icon = Icons.Default.AutoAwesome,
                onClick = { onViewAi(session.aiCaloricEvaluation) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TimelineCardioSessionCard(cardio: CardioSession) {
    BentoCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_cardio_${cardio.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PurpleDeepCard)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = DateUtils.formatEpochDayShort(cardio.dateEpochDay).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = LilacAccent
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = cardio.type.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Text(
                text = "${cardio.durationMinutes} min",
                style = MaterialTheme.typography.bodySmall,
                color = LilacSoft,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${cardio.location} • ${cardio.intensity.label}",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "~${cardio.caloriesBurned} kcal",
                    fontSize = 12.sp,
                    color = EmeraldSuccess,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
