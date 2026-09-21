package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MobileOff
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import com.example.ui.components.ExerciseSubstitutionDialog
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExerciseExecutionRecord
import com.example.data.model.ProgressionSuggestion
import com.example.data.model.SetTag
import com.example.data.model.WorkoutExercisePlan
import com.example.data.model.WorkoutSession
import com.example.ui.components.AIExerciseExecutionModal
import com.example.ui.components.AudioPlayerBottomSheet
import com.example.ui.components.AutomaticLastExecutionCard
import com.example.ui.components.BentoCard
import com.example.ui.components.DateUtils
import com.example.ui.components.ExerciseVisualGuideDialog
import com.example.ui.components.FullExerciseHistoryDialog
import com.example.ui.components.LiquidGlassSurface
import com.example.ui.components.LocationSelector
import com.example.ui.components.MedalUnlockedDialog
import com.example.ui.components.PersonalRecordCelebrationDialog
import com.example.ui.components.PlateCalculatorDialog
import com.example.ui.components.PrimaryButton
import com.example.ui.components.ProgressiveOverloadAlertBanner
import com.example.ui.components.RestTimerOverlay
import com.example.ui.components.SecondaryButton
import com.example.ui.components.SupersetHeaderBadge
import com.example.ui.components.WarmupGeneratorDialog
import com.example.ui.components.WorkoutIntervalTimer
import com.example.ui.components.WorkoutShareStoryDialog
import com.example.ui.components.triggerVibration
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
import com.example.ui.theme.RedDestructive
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FitnessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    viewModel: FitnessViewModel,
    onBack: () -> Unit,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeState by viewModel.activeWorkout.collectAsStateWithLifecycle()
    val allExercises by viewModel.allExercises.collectAsStateWithLifecycle()
    val appliedProgressions by viewModel.appliedProgressions.collectAsStateWithLifecycle()
    val dismissedProgressions by viewModel.dismissedProgressions.collectAsStateWithLifecycle()
    val activeMedalUnlocked by viewModel.activeMedalUnlocked.collectAsStateWithLifecycle()
    val activePRCelebration by viewModel.activePRCelebration.collectAsStateWithLifecycle()
    val isTtsVoiceEnabled by viewModel.isTtsVoiceEnabled.collectAsStateWithLifecycle()
    val keepScreenOn by viewModel.keepScreenOn.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showFinishDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showIntervalTimerDialog by remember { mutableStateOf(false) }
    var showAudioSheet by remember { mutableStateOf(false) }
    var showExerciseSubstitutionDialog by remember { mutableStateOf(false) }
    var exerciseToSubstitute by remember { mutableStateOf<WorkoutExercisePlan?>(null) }
    var historyDialogExerciseName by remember { mutableStateOf<String?>(null) }
    var selectedVisualGuideExercise by remember { mutableStateOf<com.example.data.model.Exercise?>(null) }
    var plateCalculatorWeightKg by remember { mutableStateOf<Double?>(null) }
    var plateCalculatorExerciseName by remember { mutableStateOf("") }
    var warmupGeneratorExerciseIndex by remember { mutableStateOf<Int?>(null) }
    var completedSessionForStory by remember { mutableStateOf<WorkoutSession?>(null) }
    var focusedExerciseIndex by remember { mutableStateOf(0) }

    var showAIExecutionModal by remember { mutableStateOf(false) }
    var selectedAIExerciseName by remember { mutableStateOf("") }
    var selectedAIMuscleGroup by remember { mutableStateOf("") }
    var selectedAIEquipment by remember { mutableStateOf("") }

    val activeExerciseExecutionGuide by viewModel.activeExerciseExecutionGuide.collectAsStateWithLifecycle()
    val isLoadingExerciseExecutionGuide by viewModel.isLoadingExerciseExecutionGuide.collectAsStateWithLifecycle()

    if (!activeState.isActive) {
        // Fallback view when no workout is running
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(PurpleDarkest)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PurpleDeepCard)
                    .border(1.5.dp, LilacAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = LilacAccent,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Nenhum Treino em Execução",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Selecione um treino na lista de modelos para começar a registrar suas séries!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(
                text = "Explorar Modelos de Treino",
                onClick = onBack
            )
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PurpleDarkest)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = activeState.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = LilacAccent,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = DateUtils.formatSecondsToTime(activeState.durationSeconds),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = LilacAccent
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = LilacSoft,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = activeState.location,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LilacSoft,
                                    modifier = Modifier.clickable { showLocationDialog = true }
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = TextPrimary)
                        }
                    },
                    actions = {
                        // Screen On / WakeLock toggle
                        IconButton(
                            onClick = { viewModel.toggleKeepScreenOn() },
                            modifier = Modifier.testTag("btn_toggle_screen_on_active")
                        ) {
                            Icon(
                                imageVector = if (keepScreenOn) Icons.Default.PhoneAndroid else Icons.Default.MobileOff,
                                contentDescription = if (keepScreenOn) "Tela Ativa (Ligada)" else "Tela Bloqueio Automático",
                                tint = if (keepScreenOn) EmeraldSuccess else TextMuted
                            )
                        }
                        // TTS Voice Coach toggle
                        IconButton(
                            onClick = { viewModel.toggleTtsVoice() },
                            modifier = Modifier.testTag("btn_toggle_tts_voice")
                        ) {
                            Icon(
                                imageVector = if (isTtsVoiceEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = if (isTtsVoiceEnabled) "Voz do Coach Ativa" else "Voz do Coach Silenciada",
                                tint = if (isTtsVoiceEnabled) EmeraldSuccess else TextMuted
                            )
                        }
                        // Quick Audio / Music streaming launcher
                        IconButton(
                            onClick = { showAudioSheet = true },
                            modifier = Modifier.testTag("btn_open_music_player")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Headphones,
                                contentDescription = "Músicas e Áudio de Treino",
                                tint = LilacAccent
                            )
                        }
                        // Quick interval / HIIT Timer dialog button
                        IconButton(
                            onClick = { showIntervalTimerDialog = true },
                            modifier = Modifier.testTag("btn_open_interval_timer")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Timer Intervalado / HIIT",
                                tint = LilacAccent
                            )
                        }
                        IconButton(
                            onClick = { showDiscardDialog = true },
                            modifier = Modifier.testTag("btn_discard_workout")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Descartar", tint = RedDestructive)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PurpleDarkest
                    )
                )
            },
            containerColor = PurpleDarkest
        ) { paddingValues ->
            val bottomNavPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = bottomNavPadding + 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Visual overview aligned with the active-workout reference. It uses
                // drawable-name lookups so final PNG/WebP assets can be added without
                // another UI rewrite.
                item {
                    if (false) return@item
                    val currentPlan = activeState.exercises.firstOrNull { plan ->
                        plan.sets.any { !it.isCompleted }
                    } ?: activeState.exercises.firstOrNull()
                    val completedExercises = activeState.exercises.count { plan ->
                        plan.sets.isNotEmpty() && plan.sets.all { it.isCompleted }
                    }
                    ActiveWorkoutOverviewCard(
                        workoutTitle = activeState.title,
                        muscleSummary = activeState.exercises
                            .map { it.muscleGroup }
                            .filter { it.isNotBlank() }
                            .distinct()
                            .take(3)
                            .joinToString(" • "),
                        currentExercise = currentPlan,
                        completedExercises = completedExercises,
                        totalExercises = activeState.exercises.size
                    )
                }

                // Location & Gym Switcher Banner
                item {
                    if (false) return@item
                    LiquidGlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLocationDialog = true }
                            .testTag("card_active_location"),
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = PurpleDeepCard,
                        borderColor = GlassBorderSubtle
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
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(PurpleDarkSurface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = LilacAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "LOCAL DO TREINO",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = activeState.location,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }
                            Text(
                                text = "Alterar",
                                style = MaterialTheme.typography.labelSmall,
                                color = LilacAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Exercises in active workout
                item {
                    val hasExercises = activeState.exercises.isNotEmpty()
                    val totalExercises = activeState.exercises.size
                    val progress = if (totalExercises == 0) 0f else (focusedExerciseIndex + 1).toFloat() / totalExercises
                    val exerciseGroups = activeState.exercises.map { it.muscleGroup }.distinct().joinToString(" • ")
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = exerciseGroups.ifBlank { "Treino em execução" },
                            style = MaterialTheme.typography.labelMedium,
                            color = LilacSoft,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.material3.LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.weight(1f).height(5.dp),
                                color = LilacAccent,
                                trackColor = PurpleDarkSurface
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("Exercício ${if (hasExercises) focusedExerciseIndex + 1 else 0} de $totalExercises", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                val previous = (focusedExerciseIndex - 1).coerceAtLeast(0)
                                focusedExerciseIndex = previous
                            },
                            enabled = hasExercises && focusedExerciseIndex > 0,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Exercício anterior")
                            Spacer(Modifier.width(6.dp))
                            Text("Anterior")
                        }
                        OutlinedButton(
                            onClick = {
                                val next = (focusedExerciseIndex + 1).coerceAtMost(activeState.exercises.lastIndex)
                                focusedExerciseIndex = next
                            },
                            enabled = hasExercises && focusedExerciseIndex < activeState.exercises.lastIndex,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Próximo")
                            Spacer(Modifier.width(6.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = "Próximo exercício")
                        }
                    }
                }
                item {
                    val exIndex = focusedExerciseIndex.coerceIn(0, (activeState.exercises.lastIndex).coerceAtLeast(0))
                    val plan = activeState.exercises.getOrNull(exIndex) ?: return@item
                    val lastExecution = remember(plan.exerciseName, activeState.exercises.size) {
                        viewModel.getLastExerciseExecution(plan.exerciseName, plan.exerciseId)
                    }
                    val progressionSuggestion = remember(plan.exerciseName, plan.sets, appliedProgressions, dismissedProgressions) {
                        viewModel.getProgressionSuggestion(plan.exerciseName, plan)
                    }

                    ActiveExerciseCard(
                        exerciseIndex = exIndex,
                        plan = plan,
                        lastExecution = lastExecution,
                        progressionSuggestion = progressionSuggestion,
                        appliedWeight = appliedProgressions[plan.exerciseName],
                        onViewFullHistory = { historyDialogExerciseName = plan.exerciseName },
                        onOpenAIExecutionGuide = {
                            val matched = allExercises.find { it.id == plan.exerciseId || it.name.equals(plan.exerciseName, ignoreCase = true) }
                            val equipStr = matched?.equipment?.displayName ?: "Halteres / Máquina"
                            selectedAIExerciseName = plan.exerciseName
                            selectedAIMuscleGroup = plan.muscleGroup
                            selectedAIEquipment = equipStr
                            showAIExecutionModal = true
                            viewModel.requestExerciseExecutionGuide(
                                exerciseName = plan.exerciseName,
                                muscleGroup = plan.muscleGroup,
                                equipment = equipStr
                            )
                        },
                        onViewVisualGuide = {
                            val found = allExercises.find { it.id == plan.exerciseId || it.name.equals(plan.exerciseName, ignoreCase = true) }
                            selectedVisualGuideExercise = found ?: com.example.data.model.Exercise(
                                name = plan.exerciseName,
                                muscleGroup = com.example.data.model.MuscleGroup.PEITO,
                                equipment = com.example.data.model.Equipment.HALTERES,
                                executionTips = plan.notes.ifBlank { "Mantenha postura firme, controle o ritmo na descida e contraia no topo." },
                                defaultSets = 3,
                                defaultReps = 10,
                                defaultRestSeconds = plan.targetRestSeconds
                            )
                        },
                        onOpenPlateCalculator = { weight ->
                            plateCalculatorWeightKg = weight
                            plateCalculatorExerciseName = plan.exerciseName
                        },
                        onOpenWarmupGenerator = {
                            warmupGeneratorExerciseIndex = exIndex
                        },
                        onApplyProgression = { suggestedWeight ->
                            viewModel.applyProgressionSuggestion(exIndex, suggestedWeight)
                            triggerVibration(context)
                        },
                        onDismissProgression = {
                            viewModel.dismissProgressionSuggestion(plan.exerciseName)
                        },
                        onSubstituteExercise = {
                            exerciseToSubstitute = plan
                            showExerciseSubstitutionDialog = true
                        },
                        onUpdateSet = { setIndex, weight, reps, completed ->
                            viewModel.updateSet(exIndex, setIndex, weight, reps, completed)
                            if (completed) {
                                triggerVibration(context)
                            }
                        },
                        onUpdateSetDetails = { setIndex, weight, reps, completed, tag, rir, rpe, notes, restSec ->
                            viewModel.updateSet(
                                exerciseIndex = exIndex,
                                setIndex = setIndex,
                                weight = weight,
                                reps = reps,
                                completed = completed,
                                tag = tag,
                                autoRest = false,
                                rir = rir,
                                rpe = rpe,
                                notes = notes,
                                restSeconds = restSec
                            )
                        },
                        onUpdateSetTag = { setIndex, tag ->
                            viewModel.updateSetTag(exIndex, setIndex, tag)
                        },
                        onAddSet = { viewModel.addSetToExercise(exIndex) },
                        onRemoveSet = { setIndex -> viewModel.removeSetFromExercise(exIndex, setIndex) },
                        onTriggerRest = { seconds -> viewModel.triggerRestTimer(seconds) }
                    )
                }

                // Next Exercise Preview (Training Companion)
                if (activeState.exercises.size > 1) {
                    val nextExercise = activeState.exercises.firstOrNull { ex -> ex.sets.any { !it.isCompleted } }
                    if (nextExercise != null) {
                        item {
                            if (false) return@item
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = PurpleDarkSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(LilacAccent.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FitnessCenter,
                                            contentDescription = null,
                                            tint = LilacAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "EXERCÍCIO ATUAL / EM ANDAMENTO",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = LilacAccent,
                                            letterSpacing = 0.8.sp
                                        )
                                        Text(
                                            text = "${nextExercise.exerciseName} (${nextExercise.muscleGroup})",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Add exercise to active workout button
                item {
                    if (false) return@item
                    SecondaryButton(
                        text = "+ Adicionar Mais Um Exercício",
                        icon = Icons.Default.Add,
                        onClick = { showAddExerciseDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_add_exercise_to_workout")
                    )
                }

                // Perceived Exertion (RPE 1-10)
                item {
                    if (false) return@item
                    BentoCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Percepção de Esforço (RPE):",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PurpleDarkest)
                                    .border(1.dp, LilacAccent, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${activeState.perceivedExertion} / 10",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black,
                                    color = LilacAccent
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(
                            value = activeState.perceivedExertion.toFloat(),
                            onValueChange = { viewModel.updateActiveRPE(it.toInt()) },
                            valueRange = 1f..10f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = LilacAccent,
                                activeTrackColor = PurpleVibrant,
                                inactiveTrackColor = PurpleDarkest
                            ),
                            modifier = Modifier.testTag("slider_rpe")
                        )
                    }
                }

                // Finish Workout Button with Gradient
                item {
                    PrimaryButton(
                        text = "FINALIZAR TREINO & AVALIAR COM IA",
                        icon = Icons.Default.Check,
                        onClick = { showFinishDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("btn_finish_workout")
                    )
                }
            }
        }

        // Rest Timer Floating Overlay
        RestTimerOverlay(
            isVisible = activeState.restTimerVisible,
            remainingSeconds = activeState.restTimerRemainingSeconds,
            totalSeconds = activeState.restTimerTotalSeconds,
            isPaused = activeState.restTimerPaused,
            onPauseResume = { viewModel.toggleRestTimerPause() },
            onAddSeconds = { delta -> viewModel.addRestSeconds(delta) },
            onSelectPreset = { preset -> viewModel.triggerRestTimer(preset) },
            onDismiss = { viewModel.dismissRestTimer() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        )
    }

    // Dialog to change location
    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = {
                Text(
                    "Alterar Local de Treino",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                LocationSelector(
                    selectedLocation = activeState.location,
                    onLocationSelected = {
                        viewModel.updateActiveLocation(it)
                        showLocationDialog = false
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text("OK", color = LilacAccent, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = PurpleDarkSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Confirm Finish Dialog
    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = {
                Text(
                    "Finalizar Sessão de Treino?",
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text("Duração total: ${DateUtils.formatSecondsToTime(activeState.durationSeconds)}", color = TextSecondary)
                    Text("Local: ${activeState.location}", color = TextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PurpleDeepCard)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "✦ A IA do FitPr09 irá calcular seu gasto calórico metabólico e avaliar sua sobrecarga progressiva.",
                            style = MaterialTheme.typography.bodySmall,
                            color = LilacSoft,
                            lineHeight = 18.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFinishDialog = false
                        viewModel.finishActiveWorkout { savedSession ->
                            completedSessionForStory = savedSession
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_confirm_finish")
                ) {
                    Text("Sim, Finalizar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = PurpleDarkSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Story Share Dialog upon workout completion
    if (completedSessionForStory != null) {
        val profile by viewModel.userProfile.collectAsStateWithLifecycle()
        WorkoutShareStoryDialog(
            session = completedSessionForStory!!,
            userName = profile?.name ?: "Atleta FitPr09",
            onDismiss = {
                completedSessionForStory = null
                onFinished()
            }
        )
    }

    // Plate Calculator Dialog
    if (plateCalculatorWeightKg != null) {
        PlateCalculatorDialog(
            initialTargetWeightKg = plateCalculatorWeightKg!!,
            onDismiss = { plateCalculatorWeightKg = null }
        )
    }

    // Warmup Sets Generator Dialog
    if (warmupGeneratorExerciseIndex != null && warmupGeneratorExerciseIndex!! in activeState.exercises.indices) {
        val plan = activeState.exercises[warmupGeneratorExerciseIndex!!]
        val topWeight = plan.sets.firstOrNull()?.weightKg ?: 60.0
        WarmupGeneratorDialog(
            exerciseName = plan.exerciseName,
            workingWeightKg = topWeight,
            onDismiss = { warmupGeneratorExerciseIndex = null },
            onApplyWarmupSets = { generatedSets ->
                viewModel.applyWarmupSets(warmupGeneratorExerciseIndex!!, generatedSets)
                warmupGeneratorExerciseIndex = null
            }
        )
    }

    // Confirm Discard Dialog
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Descartar Treino Atual?", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Todos os dados desta sessão em andamento serão perdidos.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showDiscardDialog = false
                        viewModel.discardActiveWorkout()
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDestructive),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Descartar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Continuar Treino", color = LilacAccent)
                }
            },
            containerColor = PurpleDarkSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Add Extra Exercise Dialog
    if (showAddExerciseDialog) {
        AlertDialog(
            onDismissRequest = { showAddExerciseDialog = false },
            title = { Text("Adicionar Exercício", fontWeight = FontWeight.Black, color = TextPrimary) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allExercises) { ex ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PurpleDeepCard,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.addExerciseToActiveWorkout(ex)
                                    showAddExerciseDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = ex.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${ex.muscleGroup.displayName} • ${ex.equipment.displayName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LilacSoft
                                    )
                                }
                                Icon(Icons.Default.Add, contentDescription = null, tint = LilacAccent)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddExerciseDialog = false }) {
                    Text("Fechar", color = LilacAccent)
                }
            },
            containerColor = PurpleDarkSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Advanced Interval Timer Dialog
    if (showIntervalTimerDialog) {
        AlertDialog(
            onDismissRequest = { showIntervalTimerDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Temporizador HIIT", fontWeight = FontWeight.Black, color = TextPrimary)
                    IconButton(onClick = { showIntervalTimerDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                    }
                }
            },
            text = {
                WorkoutIntervalTimer(
                    initialWorkSeconds = 45,
                    initialRestSeconds = 30,
                    initialRounds = 5,
                    onWorkoutCompleted = {}
                )
            },
            confirmButton = {
                Button(
                    onClick = { showIntervalTimerDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Concluir", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = PurpleDarkSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Full Exercise History Dialog
    if (historyDialogExerciseName != null) {
        val records = viewModel.getExerciseHistoryRecords(historyDialogExerciseName!!)
        FullExerciseHistoryDialog(
            exerciseName = historyDialogExerciseName!!,
            records = records,
            onDismiss = { historyDialogExerciseName = null }
        )
    }

    // Audio & Music Streaming App Launcher Sheet
    if (showAudioSheet) {
        AudioPlayerBottomSheet(
            onDismiss = { showAudioSheet = false }
        )
    }

    // Biomechanical Visual Guide Dialog
    if (selectedVisualGuideExercise != null) {
        ExerciseVisualGuideDialog(
            exercise = selectedVisualGuideExercise!!,
            onDismiss = { selectedVisualGuideExercise = null }
        )
    }

    // Personal Record (PR) Celebration Dialog
    if (activePRCelebration != null) {
        PersonalRecordCelebrationDialog(
            celebration = activePRCelebration!!,
            onDismiss = { viewModel.dismissPRCelebration() }
        )
    }

    // Gamification Medal Unlocked Dialog
    if (activeMedalUnlocked != null) {
        MedalUnlockedDialog(
            medal = activeMedalUnlocked!!,
            onDismiss = { viewModel.dismissMedalUnlockedDialog() }
        )
    }

    // Exercise Substitution Dialog (Same Muscle Group)
    if (showExerciseSubstitutionDialog && exerciseToSubstitute != null) {
        val currentPlan = exerciseToSubstitute!!
        val candidateExercises = remember(currentPlan.muscleGroup) {
            viewModel.getExercisesForMuscleGroup(currentPlan.muscleGroup)
        }
        ExerciseSubstitutionDialog(
            currentPlan = currentPlan,
            availableExercises = candidateExercises,
            onDismiss = {
                showExerciseSubstitutionDialog = false
                exerciseToSubstitute = null
            },
            onSelectExercise = { newExercise ->
                viewModel.substituteExerciseInActiveWorkout(
                    oldExerciseId = currentPlan.exerciseId,
                    newExercise = newExercise
                )
                showExerciseSubstitutionDialog = false
                exerciseToSubstitute = null
            }
        )
    }

    // Gemini AI Exercise Execution Guide Modal
    if (showAIExecutionModal) {
        AIExerciseExecutionModal(
            exerciseName = selectedAIExerciseName,
            muscleGroup = selectedAIMuscleGroup,
            equipment = selectedAIEquipment,
            guide = activeExerciseExecutionGuide,
            isLoading = isLoadingExerciseExecutionGuide,
            onDismiss = {
                showAIExecutionModal = false
                viewModel.clearExerciseExecutionGuide()
            },
            onSpeakTip = { tip ->
                viewModel.speakExerciseTip(tip)
            }
        )
    }
}

/**
 * Summary area for the active-workout experience.  The image is resolved by drawable name so
 * the final isolated PNG/WebP files can be delivered independently of the Compose layout.
 */
@Composable
private fun ActiveWorkoutOverviewCard(
    workoutTitle: String,
    muscleSummary: String,
    currentExercise: WorkoutExercisePlan?,
    completedExercises: Int,
    totalExercises: Int
) {
    val safeTotal = totalExercises.coerceAtLeast(1)
    val safeCompleted = completedExercises.coerceIn(0, safeTotal)
    val progress = safeCompleted.toFloat() / safeTotal.toFloat()
    val nextWeight = currentExercise?.sets?.firstOrNull { !it.isCompleted }?.weightKg
        ?: currentExercise?.sets?.firstOrNull()?.weightKg

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_active_workout_overview"),
        color = PurpleDeepCard,
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WorkoutVisualAssetSlot(
                exerciseName = currentExercise?.exerciseName.orEmpty(),
                muscleGroup = currentExercise?.muscleGroup.orEmpty(),
                modifier = Modifier.size(86.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SEU OBJETIVO",
                    style = MaterialTheme.typography.labelSmall,
                    color = LilacAccent,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = workoutTitle.ifBlank { "Treino ativo" },
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = muscleSummary.ifBlank { "Preparando seus exercícios" },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(9.dp))
                Text(
                    text = if (nextWeight != null) {
                        "Próxima carga: ${formatWorkoutWeight(nextWeight)} kg"
                    } else {
                        "Defina suas séries para começar"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(70.dp)) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = PurpleVibrant,
                    trackColor = PurpleDarkSurface,
                    strokeWidth = 7.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$safeCompleted/$safeTotal",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "EXERCÍCIOS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 7.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutVisualAssetSlot(
    exerciseName: String,
    muscleGroup: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resourceId = remember(context, exerciseName, muscleGroup) {
        var resolvedId = 0
        for (candidate in workoutAssetCandidates(exerciseName, muscleGroup)) {
            val candidateId = context.resources.getIdentifier(
                candidate,
                "drawable",
                context.packageName
            )
            if (candidateId != 0) {
                resolvedId = candidateId
                break
            }
        }
        resolvedId
    }

    Surface(
        modifier = modifier.clip(RoundedCornerShape(15.dp)),
        color = PurpleDarkSurface,
        shape = RoundedCornerShape(15.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (resourceId != 0) {
                Image(
                    painter = painterResource(resourceId),
                    contentDescription = "Ilustração de $exerciseName",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = "Asset do exercício pendente",
                    tint = LilacAccent,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

private fun formatWorkoutWeight(weight: Double): String =
    if (weight % 1.0 == 0.0) weight.toInt().toString() else String.format("%.1f", weight)

private fun workoutAssetCandidates(exerciseName: String, muscleGroup: String): List<String> {
    val exerciseToken = normalizeWorkoutAssetToken(exerciseName)
    val semanticExercise = when {
        exerciseToken.contains("supino") && exerciseToken.contains("inclinado") &&
            exerciseToken.contains("halter") -> "exercise_incline_dumbbell_press"
        exerciseToken.contains("supino") && exerciseToken.contains("barra") ->
            "exercise_bench_press_barbell"
        exerciseToken.contains("agachamento") -> "exercise_barbell_squat"
        exerciseToken.contains("puxada") -> "exercise_lat_pulldown"
        else -> null
    }
    val includedFallback = when (semanticExercise) {
        "exercise_bench_press_barbell", "exercise_incline_dumbbell_press" -> "img_bench_press"
        "exercise_barbell_squat" -> "img_barbell_squat"
        "exercise_lat_pulldown" -> "img_lat_pulldown"
        else -> null
    }
    return listOfNotNull(
        semanticExercise,
        exerciseToken.takeIf { it.isNotBlank() }?.let { "exercise_$it" },
        workoutMuscleAssetName(muscleGroup),
        includedFallback
    ).distinct()
}

private fun workoutMuscleAssetName(muscleGroup: String): String? = when {
    normalizeWorkoutAssetToken(muscleGroup).contains("peito") ||
        normalizeWorkoutAssetToken(muscleGroup).contains("chest") -> "muscle_chest"
    normalizeWorkoutAssetToken(muscleGroup).contains("costas") ||
        normalizeWorkoutAssetToken(muscleGroup).contains("back") -> "muscle_back"
    normalizeWorkoutAssetToken(muscleGroup).contains("ombro") ||
        normalizeWorkoutAssetToken(muscleGroup).contains("shoulder") -> "muscle_shoulders"
    normalizeWorkoutAssetToken(muscleGroup).contains("biceps") -> "muscle_biceps"
    normalizeWorkoutAssetToken(muscleGroup).contains("triceps") -> "muscle_triceps"
    normalizeWorkoutAssetToken(muscleGroup).contains("quadriceps") -> "muscle_quadriceps"
    normalizeWorkoutAssetToken(muscleGroup).contains("posterior") ||
        normalizeWorkoutAssetToken(muscleGroup).contains("hamstring") -> "muscle_hamstrings"
    normalizeWorkoutAssetToken(muscleGroup).contains("glute") -> "muscle_glutes"
    normalizeWorkoutAssetToken(muscleGroup).contains("panturrilha") ||
        normalizeWorkoutAssetToken(muscleGroup).contains("calf") -> "muscle_calves"
    normalizeWorkoutAssetToken(muscleGroup).contains("abdomen") ||
        normalizeWorkoutAssetToken(muscleGroup).contains("core") -> "muscle_core"
    else -> null
}

private fun normalizeWorkoutAssetToken(value: String): String =
    java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .lowercase()
        .replace("[^a-z0-9]+".toRegex(), "_")
        .trim('_')

@Composable
fun ActiveExerciseCard(
    exerciseIndex: Int,
    plan: WorkoutExercisePlan,
    lastExecution: ExerciseExecutionRecord?,
    progressionSuggestion: ProgressionSuggestion?,
    appliedWeight: Double?,
    onViewFullHistory: () -> Unit,
    onOpenAIExecutionGuide: () -> Unit,
    onViewVisualGuide: () -> Unit,
    onOpenPlateCalculator: (Double) -> Unit,
    onOpenWarmupGenerator: () -> Unit,
    onApplyProgression: (Double) -> Unit,
    onDismissProgression: () -> Unit,
    onSubstituteExercise: () -> Unit = {},
    onUpdateSet: (setIndex: Int, weight: Double, reps: Int, completed: Boolean) -> Unit,
    onUpdateSetDetails: (setIndex: Int, weight: Double, reps: Int, completed: Boolean, tag: SetTag?, rir: Int?, rpe: Double?, notes: String?, restSeconds: Int?) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onUpdateSetTag: (setIndex: Int, tag: SetTag) -> Unit,
    onAddSet: () -> Unit,
    onRemoveSet: (setIndex: Int) -> Unit,
    onTriggerRest: (seconds: Int) -> Unit
) {
    var showTips by remember { mutableStateOf(false) }
    val nextActiveSetIndex = remember(plan.sets) { plan.sets.indexOfFirst { !it.isCompleted } }

    BentoCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_active_exercise_$exerciseIndex")
    ) {
        // Exercise Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SupersetHeaderBadge(number = (exerciseIndex / 2) + 1)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = plan.muscleGroup.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LilacSoft,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = plan.exerciseName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Descanso padrão: ${plan.targetRestSeconds}s",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Gemini AI Execution Guide Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PurpleDarkSurface)
                        .border(1.dp, LilacAccent, CircleShape)
                        .clickable { onOpenAIExecutionGuide() }
                        .testTag("btn_gemini_execution_guide_$exerciseIndex"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Execução Correta com Gemini AI",
                        tint = LilacAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Biomechanical Execution Visual Guide Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PurpleDarkSurface)
                        .border(1.dp, LilacAccent.copy(alpha = 0.5f), CircleShape)
                        .clickable { onViewVisualGuide() }
                        .testTag("btn_visual_guide_$exerciseIndex"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Guia Visual e Biomecânica",
                        tint = LilacAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PurpleDarkSurface)
                        .border(1.dp, GlassBorderSubtle, CircleShape)
                        .clickable { onTriggerRest(plan.targetRestSeconds) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Iniciar Timer",
                        tint = LilacAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (plan.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PurpleDarkSurface)
                            .border(1.dp, GlassBorderSubtle, CircleShape)
                            .clickable { showTips = !showTips },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Dicas",
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Quick Tools Toolbar: Plate Calculator, Warm-up Generator & Exercise Substitution
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val topWeight = plan.sets.firstOrNull()?.weightKg ?: 60.0

            // Gemini AI Execution Guide Pill
            Surface(
                onClick = onOpenAIExecutionGuide,
                shape = RoundedCornerShape(10.dp),
                color = PurpleDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent.copy(alpha = 0.5f)),
                modifier = Modifier
                    .weight(1.1f)
                    .testTag("btn_ai_guide_pill_$exerciseIndex")
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = LilacAccent,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Execução IA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LilacAccent,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            // Warm-up sets generator
            Surface(
                onClick = onOpenWarmupGenerator,
                shape = RoundedCornerShape(10.dp),
                color = PurpleDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Aquecimento",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB74D),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            // Plate Calculator
            Surface(
                onClick = { onOpenPlateCalculator(topWeight) },
                shape = RoundedCornerShape(10.dp),
                color = PurpleDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = LilacAccent,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Anilhas",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LilacAccent,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            // Exercise Substitution (Mesmo Grupo Muscular)
            Surface(
                onClick = onSubstituteExercise,
                shape = RoundedCornerShape(10.dp),
                color = PurpleDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Substituir",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        if (showTips && plan.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PurpleDarkSurface)
                    .border(1.dp, EmeraldSuccess.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(10.dp)
            ) {
                Text(
                    text = "💡 Dica: ${plan.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 1. AUTOMATIC LAST EXECUTION DISPLAY
        if (false) AutomaticLastExecutionCard(
            lastExecution = lastExecution,
            onViewFullHistory = onViewFullHistory
        )

        // 2. PROGRESSIVE OVERLOAD BANNER (Never increases without confirmation)
        if (false && progressionSuggestion != null && !progressionSuggestion!!.isAccepted && !progressionSuggestion!!.isDismissed && appliedWeight == null) {
            Spacer(modifier = Modifier.height(10.dp))
            ProgressiveOverloadAlertBanner(
                suggestion = progressionSuggestion!!,
                onAccept = { onApplyProgression(progressionSuggestion!!.suggestedWeightKg) },
                onDismiss = onDismissProgression
            )
        } else if (false && appliedWeight != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(EmeraldDark.copy(alpha = 0.25f))
                    .border(1.dp, EmeraldSuccess.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "✦ Sobrecarga Progressiva Aplicada: ${appliedWeight!!.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }} kg",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Set Table Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TIPO",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                modifier = Modifier.width(36.dp)
            )
            Text(
                text = "CARGA",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
            Text(
                text = "REPS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
            Text(
                text = "RIR / ESF.",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
            Text(
                text = "CHECK",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                modifier = Modifier.width(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sets List with companion highlighting
        plan.sets.forEachIndexed { setIdx, setEntry ->
            val isNextActive = setIdx == nextActiveSetIndex
            SetItemRow(
                setNumber = setEntry.setNumber,
                weightKg = setEntry.weightKg,
                reps = setEntry.reps,
                tag = setEntry.setTag,
                rir = setEntry.rir,
                rpe = setEntry.rpe,
                notes = setEntry.notes,
                restSeconds = setEntry.restSeconds,
                isPR = setEntry.isPR,
                prType = setEntry.prType,
                isCompleted = setEntry.isCompleted,
                isNextActive = isNextActive,
                canDelete = plan.sets.size > 1,
                onTagChanged = { newTag -> onUpdateSetTag(setIdx, newTag) },
                onRirChanged = { newRir ->
                    val calcRpe = when (newRir) {
                        0 -> 10.0
                        1 -> 9.0
                        2 -> 8.0
                        3 -> 7.0
                        else -> 6.0
                    }
                    onUpdateSetDetails(
                        setIdx,
                        setEntry.weightKg,
                        setEntry.reps,
                        setEntry.isCompleted,
                        setEntry.setTag,
                        newRir,
                        calcRpe,
                        setEntry.notes,
                        setEntry.restSeconds
                    )
                },
                onNotesChanged = { newNotes ->
                    onUpdateSetDetails(
                        setIdx,
                        setEntry.weightKg,
                        setEntry.reps,
                        setEntry.isCompleted,
                        setEntry.setTag,
                        setEntry.rir,
                        setEntry.rpe,
                        newNotes,
                        setEntry.restSeconds
                    )
                },
                onRestSecondsChanged = { newRest ->
                    onUpdateSetDetails(
                        setIdx,
                        setEntry.weightKg,
                        setEntry.reps,
                        setEntry.isCompleted,
                        setEntry.setTag,
                        setEntry.rir,
                        setEntry.rpe,
                        setEntry.notes,
                        newRest
                    )
                },
                onOpenPlateCalculator = { onOpenPlateCalculator(setEntry.weightKg) },
                onWeightChanged = { newW -> onUpdateSet(setIdx, newW, setEntry.reps, setEntry.isCompleted) },
                onRepsChanged = { newR -> onUpdateSet(setIdx, setEntry.weightKg, newR, setEntry.isCompleted) },
                onToggleComplete = { onUpdateSet(setIdx, setEntry.weightKg, setEntry.reps, !setEntry.isCompleted) },
                onDelete = { onRemoveSet(setIdx) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Add Set Button
        TextButton(
            onClick = onAddSet,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = LilacAccent, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("+ Adicionar Série", color = LilacAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun SetItemRow(
    setNumber: Int,
    weightKg: Double,
    reps: Int,
    tag: SetTag = SetTag.NORMAL,
    rir: Int? = null,
    rpe: Double? = null,
    notes: String = "",
    restSeconds: Int = 60,
    isPR: Boolean = false,
    prType: String = "",
    isCompleted: Boolean,
    isNextActive: Boolean = false,
    canDelete: Boolean,
    onTagChanged: (SetTag) -> Unit,
    onRirChanged: (Int?) -> Unit = {},
    onNotesChanged: (String) -> Unit = {},
    onRestSecondsChanged: (Int) -> Unit = {},
    onOpenPlateCalculator: () -> Unit,
    onWeightChanged: (Double) -> Unit,
    onRepsChanged: (Int) -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    var showTagMenu by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.7f),
        label = "set_scale"
    )

    val rowBgColor by animateColorAsState(
        targetValue = when {
            isCompleted -> EmeraldSubtle
            isNextActive -> PurpleDeepCard
            else -> PurpleDarkSurface
        },
        label = "set_bg_color"
    )

    val rowBorderColor by animateColorAsState(
        targetValue = when {
            isCompleted -> EmeraldSuccess.copy(alpha = 0.5f)
            isNextActive -> LilacAccent.copy(alpha = 0.8f)
            else -> GlassBorder
        },
        label = "set_border_color"
    )

    val (tagColor, tagBg) = when (tag) {
        SetTag.WARMUP -> Color(0xFFFFB74D) to Color(0x33FFB74D)
        SetTag.FEEDER -> Color(0xFF4DD0E1) to Color(0x334DD0E1)
        SetTag.DROPSET -> Color(0xFFFF4081) to Color(0x33FF4081)
        SetTag.FAILURE -> Color(0xFFFF5252) to Color(0x33FF5252)
        SetTag.NORMAL -> LilacAccent to PurpleDeepCard
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = rowBgColor,
        border = androidx.compose.foundation.BorderStroke(if (isNextActive) 1.5.dp else 1.dp, rowBorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Set Tag Selector Box (W, P, 1, 2, D, F) with optional PR badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isCompleted) EmeraldSuccess else tagBg)
                                .border(
                                    1.dp,
                                    if (isCompleted) EmeraldSuccess else tagColor.copy(alpha = 0.6f),
                                    CircleShape
                                )
                                .clickable { showTagMenu = true }
                        ) {
                            Text(
                                text = if (tag == SetTag.NORMAL) "$setNumber" else tag.shortLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black,
                                color = if (isCompleted) Color.White else tagColor
                            )
                        }

                        DropdownMenu(
                            expanded = showTagMenu,
                            onDismissRequest = { showTagMenu = false }
                        ) {
                            SetTag.values().forEach { t ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = t.shortLabel,
                                                fontWeight = FontWeight.Black,
                                                color = when (t) {
                                                    SetTag.WARMUP -> Color(0xFFFFB74D)
                                                    SetTag.FEEDER -> Color(0xFF4DD0E1)
                                                    SetTag.DROPSET -> Color(0xFFFF4081)
                                                    SetTag.FAILURE -> Color(0xFFFF5252)
                                                    SetTag.NORMAL -> LilacAccent
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(t.label, style = MaterialTheme.typography.bodySmall)
                                        }
                                    },
                                    onClick = {
                                        onTagChanged(t)
                                        showTagMenu = false
                                    }
                                )
                            }
                        }
                    }

                    if (isPR) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFD700))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "🏆 PR",
                                color = Color.Black,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                // Weight Increment/Decrement Control + Click to Open Plate Calculator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { onWeightChanged((weightKg - 2.5).coerceAtLeast(0.0)) },
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "-2.5kg", tint = TextSecondary, modifier = Modifier.size(12.dp))
                    }
                    Text(
                        text = "${weightKg.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }}kg",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier
                            .clickable { onOpenPlateCalculator() }
                            .padding(horizontal = 2.dp)
                    )
                    IconButton(
                        onClick = { onWeightChanged(weightKg + 2.5) },
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "+2.5kg", tint = TextSecondary, modifier = Modifier.size(12.dp))
                    }
                }

                // Reps Increment/Decrement Control
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { onRepsChanged((reps - 1).coerceAtLeast(1)) },
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "-1 rep", tint = TextSecondary, modifier = Modifier.size(12.dp))
                    }
                    Text(
                        text = "$reps",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                    IconButton(
                        onClick = { onRepsChanged(reps + 1) },
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "+1 rep", tint = TextSecondary, modifier = Modifier.size(12.dp))
                    }
                }

                // RIR / Effort Pill with quick toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (rir != null) LilacAccent.copy(alpha = 0.18f) else PurpleDeepCard)
                        .border(
                            1.dp,
                            if (rir != null) LilacAccent else GlassBorderSubtle,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { showDetails = !showDetails }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (rir != null) "RIR $rir" else "RIR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (rir != null) LilacAccent else TextMuted
                    )
                }

                // Complete check button with smooth microinteraction
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isCompleted) EmeraldSuccess else PurpleDeepCard)
                        .border(
                            1.dp,
                            if (isCompleted) EmeraldSuccess else GlassBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onToggleComplete() }
                        .testTag("btn_check_set_$setNumber"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = if (isCompleted) "Concluído" else "Marcar",
                        tint = if (isCompleted) Color.White else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Expanded Inline Details (RIR selector, Rest time, Notes & 1RM)
            if (showDetails) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(GlassBorderSubtle))
                Spacer(modifier = Modifier.height(6.dp))

                // RIR Selector Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("RIR:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    listOf(0 to "Falha", 1 to "1", 2 to "2", 3 to "3+").forEach { (rVal, label) ->
                        val isSelected = rir == rVal
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) LilacAccent else PurpleDeepCard)
                                .clickable { onRirChanged(if (isSelected) null else rVal) }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 1RM estimate
                    val est1RM = if (reps <= 1) weightKg else Math.round(weightKg * (1.0 + reps / 30.0) * 10.0) / 10.0
                    Text(
                        text = "1RM: ${est1RM}kg",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Rest Time & Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Descanso:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        listOf(30, 60, 90, 120).forEach { secs ->
                            val isSelected = restSeconds == secs
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) PurpleDeepCard else Color.Transparent)
                                    .border(1.dp, if (isSelected) LilacAccent else GlassBorderSubtle, RoundedCornerShape(6.dp))
                                    .clickable { onRestSecondsChanged(secs) }
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "${secs}s",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) LilacAccent else TextSecondary
                                )
                            }
                        }
                    }

                    if (canDelete) {
                        Text(
                            text = "Excluir série",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = RedDestructive,
                            modifier = Modifier.clickable { onDelete() }
                        )
                    }
                }
            }
        }
    }
}
