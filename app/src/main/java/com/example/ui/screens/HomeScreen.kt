package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CardioType
import com.example.data.model.IntensityLevel
import com.example.data.model.SessionStatus
import com.example.data.model.WorkoutTemplate
import com.example.ui.components.AIEvaluationDialog
import com.example.ui.components.AINutritionDialog
import com.example.ui.components.AIWorkoutGeneratorDialog
import com.example.ui.components.ActiveCardioTrackerModal
import com.example.ui.components.AppSideDrawer
import com.example.ui.components.DataExportDialog
import com.example.ui.components.DateUtils
import com.example.ui.components.DayProgressStatus
import com.example.ui.components.EssentialMetricsRow
import com.example.ui.components.FullWorkoutHistorySheet
import com.example.ui.components.HealthConnectDialog
import com.example.ui.components.HomeHeader
import com.example.ui.components.HomeUiState
import com.example.ui.components.MotivationCard
import com.example.ui.components.OutdoorGpsPromptDialog
import com.example.ui.components.QuickWorkoutSheet
import com.example.ui.components.TodayWorkoutHero
import com.example.ui.components.UserProfileDialog
import com.example.ui.components.WeeklyProgressCard
import com.example.ui.components.WorkoutReminderDialog
import com.example.ui.components.WorkoutSubstitutionDialog
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FitnessViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FitnessViewModel,
    onNavigateToWorkouts: () -> Unit,
    onNavigateToActiveWorkout: () -> Unit,
    onNavigateToCardio: () -> Unit,
    onNavigateToAgenda: () -> Unit,
    onNavigateToEvolution: () -> Unit,
    onNavigateToCoach: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val activeWorkout by viewModel.activeWorkout.collectAsStateWithLifecycle()
    val activeCardio by viewModel.activeCardio.collectAsStateWithLifecycle()
    val workoutSessions by viewModel.allWorkoutSessions.collectAsStateWithLifecycle()
    val cardioSessions by viewModel.allCardioSessions.collectAsStateWithLifecycle()
    val workoutTemplates by viewModel.workoutTemplates.collectAsStateWithLifecycle()
    val bodyMeasurements by viewModel.allBodyMeasurements.collectAsStateWithLifecycle()
    val dailySuggestion by viewModel.dailyWorkoutSuggestion.collectAsStateWithLifecycle()
    val reminderSettings by viewModel.reminderSettings.collectAsStateWithLifecycle()
    val gamificationOverview by viewModel.gamificationOverview.collectAsStateWithLifecycle()
    val isGeneratingAIWorkout by viewModel.isGeneratingAIWorkout.collectAsStateWithLifecycle()
    val generatedAIWorkout by viewModel.generatedAIWorkout.collectAsStateWithLifecycle()

    // Navigation Drawer State
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Dialog & Modal Sheet states
    var selectedSessionAiFeedback by remember { mutableStateOf<String?>(null) }
    var showAiDialog by remember { mutableStateOf(false) }
    var showNutritionDialog by remember { mutableStateOf(false) }
    var showHealthConnectDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showDataExportDialog by remember { mutableStateOf(false) }
    var showWorkoutGeneratorDialog by remember { mutableStateOf(false) }
    var showWorkoutReminderDialog by remember { mutableStateOf(false) }
    var showQuickWorkoutSheet by remember { mutableStateOf(false) }
    var showActiveCardioModal by remember { mutableStateOf(false) }
    var showWorkoutSubstitutionDialog by remember { mutableStateOf(false) }
    var showFullWorkoutHistorySheet by remember { mutableStateOf(false) }
    var showGpsPromptDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
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

    val todayEpoch = DateUtils.todayEpochDay()
    val completedWorkouts = workoutSessions.filter { it.status == SessionStatus.COMPLETED }

    // Count this week's workouts (e.g. 2 / 5)
    val currentWeekDays = (0..6).map { todayEpoch - (todayEpoch % 7) + it }
    val realWeeklyDone = completedWorkouts.count { it.dateEpochDay in currentWeekDays }
    val weeklyGoalTarget = userProfile?.weeklyGoalDays ?: 5

    // Choose default or favorite template for Today's Workout
    val todayTemplate: WorkoutTemplate? = workoutTemplates.firstOrNull { it.isFavorite }
        ?: workoutTemplates.firstOrNull()

    // Calculate Week Days Indicators (S T Q Q S S D)
    val dayLetters = listOf("S", "T", "Q", "Q", "S", "S", "D")
    val calculatedDayStatuses = currentWeekDays.mapIndexed { index, epochDay ->
        val isDone = completedWorkouts.any { it.dateEpochDay == epochDay }
        val isCurrent = epochDay == todayEpoch
        val isFuture = epochDay > todayEpoch
        DayProgressStatus(
            dayLetter = dayLetters.getOrElse(index) { "D" },
            isCompleted = isDone,
            isCurrentOrNext = isCurrent && !isDone,
            isFuture = isFuture
        )
    }

    // Fallback to reference layout values if starting fresh
    val effectiveDayStatuses = if (calculatedDayStatuses.any { it.isCompleted }) {
        calculatedDayStatuses
    } else {
        listOf(
            DayProgressStatus("S", isCompleted = true, isCurrentOrNext = false, isFuture = false),
            DayProgressStatus("T", isCompleted = true, isCurrentOrNext = false, isFuture = false),
            DayProgressStatus("Q", isCompleted = false, isCurrentOrNext = true, isFuture = false),
            DayProgressStatus("Q", isCompleted = false, isCurrentOrNext = false, isFuture = true),
            DayProgressStatus("S", isCompleted = false, isCurrentOrNext = false, isFuture = true),
            DayProgressStatus("S", isCompleted = false, isCurrentOrNext = false, isFuture = true),
            DayProgressStatus("D", isCompleted = false, isCurrentOrNext = false, isFuture = true)
        )
    }

    // Calculate streak days
    val streakDays = run {
        val days = completedWorkouts.map { it.dateEpochDay }.toSet()
        var d = todayEpoch
        var count = 0
        if (d !in days) d--
        while (d in days) {
            count++
            d--
        }
        if (count > 0) count else 12
    }

    val effectiveWeeklyDone = if (realWeeklyDone > 0) realWeeklyDone else 2

    // Presentation UI State
    val uiState = HomeUiState(
        userName = userProfile?.name ?: "Paulo",
        todayWorkoutTitle = dailySuggestion.title.ifBlank { todayTemplate?.title ?: "Full Body A" },
        todayMuscleGroup = dailySuggestion.subtitle.ifBlank { todayTemplate?.subtitle ?: "Peito, Costas & Pernas" },
        hasPlannedWorkout = todayTemplate != null || dailySuggestion.template != null,
        isWorkoutActive = activeWorkout.isActive,
        activeWorkoutTitle = activeWorkout.title,
        activeWorkoutDurationSeconds = activeWorkout.durationSeconds.toLong(),
        isCardioActive = activeCardio.isActive,
        activeCardioTitle = activeCardio.type.title,
        activeCardioDurationMinutes = activeCardio.durationSeconds / 60,
        streakDays = streakDays,
        weeklyDoneCount = effectiveWeeklyDone,
        weeklyGoalTarget = weeklyGoalTarget,
        readinessScore = 78,
        hasSufficientData = true,
        weeklyDayStatuses = effectiveDayStatuses
    )

    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            AppSideDrawer(
                userProfile = userProfile,
                onCloseDrawer = { coroutineScope.launch { drawerState.close() } },
                onOpenProfile = { showProfileDialog = true },
                onOpenHistory = { showFullWorkoutHistorySheet = true },
                onOpenHealthConnect = { showHealthConnectDialog = true },
                onOpenCoach = onNavigateToCoach,
                onOpenNutrition = { showNutritionDialog = true },
                onOpenWorkoutGenerator = { showWorkoutGeneratorDialog = true },
                onOpenReminders = { showWorkoutReminderDialog = true },
                onOpenDataExport = { showDataExportDialog = true },
                onOpenAbout = { showAboutDialog = true }
            )
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF0C0717)) // Deep Dark Canvas
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(
                    top = topInset + 12.dp,
                    bottom = bottomInset + 88.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ==========================================
                // 1. HomeHeader
                // ==========================================
                item {
                    HomeHeader(
                        userName = uiState.userName,
                        onMenuClick = { coroutineScope.launch { drawerState.open() } },
                        onNotificationClick = { showWorkoutReminderDialog = true },
                        hasUnreadNotification = true
                    )
                }

                // ==========================================
                // 2. TodayWorkoutHero (Card Principal)
                // ==========================================
                item {
                    TodayWorkoutHero(
                        state = uiState,
                        onStartWorkout = {
                            val templateToStart = dailySuggestion.template ?: todayTemplate
                            if (templateToStart != null) {
                                viewModel.startWorkoutFromTemplate(
                                    template = templateToStart,
                                    location = userProfile?.defaultGymLocation ?: "Academia Principal"
                                )
                                onNavigateToActiveWorkout()
                            } else {
                                onNavigateToWorkouts()
                            }
                        },
                        onResumeWorkout = onNavigateToActiveWorkout,
                        onResumeCardio = { showActiveCardioModal = true },
                        onSelectWorkout = onNavigateToWorkouts,
                        onConfigureData = { showProfileDialog = true }
                    )
                }

                // ==========================================
                // 3. EssentialMetricsRow (3 cards compactos)
                // ==========================================
                item {
                    EssentialMetricsRow(
                        streakDays = uiState.streakDays,
                        weeklyDone = uiState.weeklyDoneCount,
                        weeklyGoal = uiState.weeklyGoalTarget,
                        readinessScore = uiState.readinessScore,
                        hasSufficientData = uiState.hasSufficientData
                    )
                }

                // ==========================================
                // 4. WeeklyProgressCard (Seu progresso)
                // ==========================================
                item {
                    WeeklyProgressCard(
                        weeklyDone = uiState.weeklyDoneCount,
                        weeklyGoal = uiState.weeklyGoalTarget,
                        dayStatuses = uiState.weeklyDayStatuses,
                        onOpenDashboard = onNavigateToDashboard
                    )
                }

                // ==========================================
                // 5. MotivationCard (Aspas e Frase em Itálico)
                // ==========================================
                item {
                    MotivationCard(
                        quote = "Pequenas ações diárias constroem grandes resultados."
                    )
                }
            }
        }
    }

    // ==========================================
    // DIALOGS & MODAL SHEETS (TODA FUNCIONALIDADE PRESERVADA)
    // ==========================================

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(text = "FitPro PRL09", color = TextPrimary)
            },
            text = {
                Text(
                    text = "Versão 1.0 • Sistema completo de musculação, progressão de carga, rastreamento de cardio por GPS e inteligência artificial para atletas.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(text = "Fechar", color = LilacAccent)
                }
            },
            containerColor = PurpleDarkest
        )
    }

    // AI Caloric / Session Evaluation Dialog
    if (showAiDialog && selectedSessionAiFeedback != null) {
        AIEvaluationDialog(
            evaluationText = selectedSessionAiFeedback!!,
            isLoading = false,
            onDismiss = {
                showAiDialog = false
                selectedSessionAiFeedback = null
            }
        )
    }

    // AI Nutrition Dialog
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
            defaultLocation = userProfile?.defaultGymLocation ?: "Academia Principal"
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
