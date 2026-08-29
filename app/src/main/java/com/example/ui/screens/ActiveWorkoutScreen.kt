package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExerciseExecutionRecord
import com.example.data.model.ProgressionSuggestion
import com.example.data.model.WorkoutExercisePlan
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
import com.example.ui.components.PrimaryButton
import com.example.ui.components.ProgressiveOverloadAlertBanner
import com.example.ui.components.RestTimerOverlay
import com.example.ui.components.SecondaryButton
import com.example.ui.components.SupersetHeaderBadge
import com.example.ui.components.WorkoutIntervalTimer
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
    val context = LocalContext.current

    var showFinishDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showIntervalTimerDialog by remember { mutableStateOf(false) }
    var showAudioSheet by remember { mutableStateOf(false) }
    var historyDialogExerciseName by remember { mutableStateOf<String?>(null) }
    var selectedVisualGuideExercise by remember { mutableStateOf<com.example.data.model.Exercise?>(null) }

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
                // Location & Gym Switcher Banner
                item {
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
                itemsIndexed(activeState.exercises) { exIndex, plan ->
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
                        onApplyProgression = { suggestedWeight ->
                            viewModel.applyProgressionSuggestion(exIndex, suggestedWeight)
                            triggerVibration(context)
                        },
                        onDismissProgression = {
                            viewModel.dismissProgressionSuggestion(plan.exerciseName)
                        },
                        onUpdateSet = { setIndex, weight, reps, completed ->
                            viewModel.updateSet(exIndex, setIndex, weight, reps, completed)
                            if (completed) {
                                triggerVibration(context)
                            }
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
                            text = "✦ A IA do FitTreino irá calcular seu gasto calórico metabólico e avaliar sua sobrecarga progressiva.",
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
                        viewModel.finishActiveWorkout {
                            onFinished()
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
}

@Composable
fun ActiveExerciseCard(
    exerciseIndex: Int,
    plan: WorkoutExercisePlan,
    lastExecution: ExerciseExecutionRecord?,
    progressionSuggestion: ProgressionSuggestion?,
    appliedWeight: Double?,
    onViewFullHistory: () -> Unit,
    onViewVisualGuide: () -> Unit,
    onApplyProgression: (Double) -> Unit,
    onDismissProgression: () -> Unit,
    onUpdateSet: (setIndex: Int, weight: Double, reps: Int, completed: Boolean) -> Unit,
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
        AutomaticLastExecutionCard(
            lastExecution = lastExecution,
            onViewFullHistory = onViewFullHistory
        )

        // 2. PROGRESSIVE OVERLOAD BANNER (Never increases without confirmation)
        if (progressionSuggestion != null && !progressionSuggestion.isAccepted && !progressionSuggestion.isDismissed && appliedWeight == null) {
            Spacer(modifier = Modifier.height(10.dp))
            ProgressiveOverloadAlertBanner(
                suggestion = progressionSuggestion,
                onAccept = { onApplyProgression(progressionSuggestion.suggestedWeightKg) },
                onDismiss = onDismissProgression
            )
        } else if (appliedWeight != null) {
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
                        text = "✦ Sobrecarga Progressiva Aplicada: ${appliedWeight.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }} kg",
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
                text = "SÉRIE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                modifier = Modifier.width(42.dp)
            )
            Text(
                text = "CARGA (KG)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                modifier = Modifier.width(90.dp)
            )
            Text(
                text = "REPETIÇÕES",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                modifier = Modifier.width(90.dp)
            )
            Text(
                text = "CHECK",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                modifier = Modifier.width(48.dp)
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
                isCompleted = setEntry.isCompleted,
                isNextActive = isNextActive,
                canDelete = plan.sets.size > 1,
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
    isCompleted: Boolean,
    isNextActive: Boolean = false,
    canDelete: Boolean,
    onWeightChanged: (Double) -> Unit,
    onRepsChanged: (Int) -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
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

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = rowBgColor,
        border = androidx.compose.foundation.BorderStroke(if (isNextActive) 1.5.dp else 1.dp, rowBorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Set Number Circle with NEXT indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> EmeraldSuccess
                                isNextActive -> LilacAccent
                                else -> PurpleDeepCard
                            }
                        )
                ) {
                    Text(
                        text = "$setNumber",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        color = if (isCompleted || isNextActive) Color.White else LilacAccent
                    )
                }
            }

            // Weight Increment/Decrement Control
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(95.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { onWeightChanged((weightKg - 2.5).coerceAtLeast(0.0)) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "-2.5kg", tint = TextSecondary, modifier = Modifier.size(13.dp))
                }
                Text(
                    text = "${weightKg.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }}kg",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
                IconButton(
                    onClick = { onWeightChanged(weightKg + 2.5) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "+2.5kg", tint = TextSecondary, modifier = Modifier.size(13.dp))
                }
            }

            // Reps Increment/Decrement Control
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(95.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { onRepsChanged((reps - 1).coerceAtLeast(1)) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "-1 rep", tint = TextSecondary, modifier = Modifier.size(13.dp))
                }
                Text(
                    text = "$reps",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                IconButton(
                    onClick = { onRepsChanged(reps + 1) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "+1 rep", tint = TextSecondary, modifier = Modifier.size(13.dp))
                }
            }

            // Complete check button with smooth microinteraction
            Box(
                modifier = Modifier
                    .size(36.dp)
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
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
