package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Exercise
import com.example.data.model.ExerciseSetEntry
import com.example.data.model.WorkoutExercisePlan
import com.example.ui.components.DateUtils
import com.example.ui.components.LocationSelector
import com.example.ui.components.RestTimerOverlay
import com.example.ui.components.triggerVibration
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RedDestructive
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.RoyalBlueSubtle
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
    val context = LocalContext.current

    var showFinishDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }

    if (!activeState.isActive) {
        // Fallback view when no workout is running
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = RoyalBlue,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nenhum Treino em Execução",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Escolha um treino na lista de modelos para começar a registrar suas séries!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Voltar aos Modelos", color = Color.White)
            }
        }
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = activeState.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = RoyalBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = DateUtils.formatSecondsToTime(activeState.durationSeconds),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalBlue
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = activeState.location,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldSuccess,
                                    modifier = Modifier.clickable { showLocationDialog = true }
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                if (activeState.restTimerVisible) {
                                    viewModel.toggleRestTimerPause()
                                } else {
                                    viewModel.triggerRestTimer(60)
                                }
                            },
                            modifier = Modifier.testTag("btn_quick_rest_timer")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Cronômetro de descanso",
                                tint = if (activeState.restTimerVisible && activeState.restTimerPaused) AmberWarning else RoyalBlue
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
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 180.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Location Bar Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                            .clickable { showLocationDialog = true }
                            .testTag("card_active_location")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = RoyalBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Local de Treino Atual:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = activeState.location,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            Text(
                                text = "Alterar",
                                style = MaterialTheme.typography.labelSmall,
                                color = RoyalBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Exercises in active workout
                itemsIndexed(activeState.exercises) { exIndex, plan ->
                    ActiveExerciseCard(
                        exerciseIndex = exIndex,
                        plan = plan,
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

                // Add exercise to active workout button
                item {
                    OutlinedButton(
                        onClick = { showAddExerciseDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_add_exercise_to_workout")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = RoyalBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("+ Adicionar Mais Um Exercício", fontWeight = FontWeight.Bold, color = RoyalBlue)
                    }
                }

                // Perceived Exertion (RPE 1-10)
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Percepção de Esforço (RPE):",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${activeState.perceivedExertion} / 10",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalBlue
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Slider(
                                value = activeState.perceivedExertion.toFloat(),
                                onValueChange = { viewModel.updateActiveRPE(it.toInt()) },
                                valueRange = 1f..10f,
                                steps = 8,
                                colors = SliderDefaults.colors(
                                    thumbColor = RoyalBlue,
                                    activeTrackColor = RoyalBlue
                                ),
                                modifier = Modifier.testTag("slider_rpe")
                            )
                        }
                    }
                }

                // Finish Workout Button
                item {
                    Button(
                        onClick = { showFinishDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("btn_finish_workout")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FINALIZAR TREINO & AVALIAR COM IA",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }
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
                .padding(bottom = 16.dp)
        )
    }

    // Dialog to change location
    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text("Alterar Local de Treino") },
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
                    Text("OK")
                }
            }
        )
    }

    // Confirm Finish Dialog
    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Finalizar Sessão de Treino?", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Duração total: ${DateUtils.formatSecondsToTime(activeState.durationSeconds)}")
                    Text("Local: ${activeState.location}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Ao finalizar, a IA do FitTreino irá calcular seu gasto calórico metabólico e avaliar sua sobrecarga.",
                        style = MaterialTheme.typography.bodySmall,
                        color = RoyalBlue
                    )
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
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_confirm_finish")
                ) {
                    Text("Sim, Finalizar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Cancelar")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Confirm Discard Dialog
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Descartar Treino Atual?") },
            text = { Text("Todos os dados desta sessão em andamento serão perdidos.") },
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
                    Text("Descartar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Continuar Treino")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Add Extra Exercise Dialog
    if (showAddExerciseDialog) {
        AlertDialog(
            onDismissRequest = { showAddExerciseDialog = false },
            title = { Text("Adicionar Exercício ao Treino") },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(allExercises) { ex ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.addExerciseToActiveWorkout(ex)
                                    showAddExerciseDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(ex.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                    Text(
                                        "${ex.muscleGroup.displayName} • ${ex.equipment.displayName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(Icons.Default.Add, contentDescription = null, tint = RoyalBlue)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddExerciseDialog = false }) {
                    Text("Fechar")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun ActiveExerciseCard(
    exerciseIndex: Int,
    plan: WorkoutExercisePlan,
    onUpdateSet: (setIndex: Int, weight: Double, reps: Int, completed: Boolean) -> Unit,
    onAddSet: () -> Unit,
    onRemoveSet: (setIndex: Int) -> Unit,
    onTriggerRest: (seconds: Int) -> Unit
) {
    var showTips by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                RoundedCornerShape(20.dp)
            )
            .testTag("card_active_exercise_$exerciseIndex")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Exercise Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plan.exerciseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${plan.muscleGroup} • Descanso alvo: ${plan.targetRestSeconds}s",
                        style = MaterialTheme.typography.bodySmall,
                        color = RoyalBlue
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onTriggerRest(plan.targetRestSeconds) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = RoyalBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (plan.notes.isNotBlank()) {
                        IconButton(
                            onClick = { showTips = !showTips },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Dicas",
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            if (showTips && plan.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 Dica: ${plan.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Set Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SÉRIE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(42.dp)
                )
                Text(
                    text = "PESO (KG)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(90.dp)
                )
                Text(
                    text = "REPETIÇÕES",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(90.dp)
                )
                Text(
                    text = "CHECK",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sets List
            plan.sets.forEachIndexed { setIdx, setEntry ->
                SetItemRow(
                    setNumber = setEntry.setNumber,
                    weightKg = setEntry.weightKg,
                    reps = setEntry.reps,
                    isCompleted = setEntry.isCompleted,
                    canDelete = plan.sets.size > 1,
                    onWeightChanged = { newW -> onUpdateSet(setIdx, newW, setEntry.reps, setEntry.isCompleted) },
                    onRepsChanged = { newR -> onUpdateSet(setIdx, setEntry.weightKg, newR, setEntry.isCompleted) },
                    onToggleComplete = { onUpdateSet(setIdx, setEntry.weightKg, setEntry.reps, !setEntry.isCompleted) },
                    onDelete = { onRemoveSet(setIdx) }
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add Set Button
            TextButton(
                onClick = onAddSet,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ Adicionar Série", color = RoyalBlue, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SetItemRow(
    setNumber: Int,
    weightKg: Double,
    reps: Int,
    isCompleted: Boolean,
    canDelete: Boolean,
    onWeightChanged: (Double) -> Unit,
    onRepsChanged: (Int) -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isCompleted) EmeraldSuccess.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isCompleted) androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.4f)) else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Set Number Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) EmeraldSuccess else RoyalBlueSubtle)
            ) {
                Text(
                    text = "$setNumber",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) Color.White else RoyalBlue
                )
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
                    Icon(Icons.Default.Remove, contentDescription = "-2.5kg", modifier = Modifier.size(14.dp))
                }
                Text(
                    text = "${weightKg.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }}kg",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
                IconButton(
                    onClick = { onWeightChanged(weightKg + 2.5) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "+2.5kg", modifier = Modifier.size(14.dp))
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
                    Icon(Icons.Default.Remove, contentDescription = "-1 rep", modifier = Modifier.size(14.dp))
                }
                Text(
                    text = "$reps",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                IconButton(
                    onClick = { onRepsChanged(reps + 1) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "+1 rep", modifier = Modifier.size(14.dp))
                }
            }

            // Complete check button
            FilledIconButton(
                onClick = onToggleComplete,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isCompleted) EmeraldSuccess else MaterialTheme.colorScheme.surface,
                    contentColor = if (isCompleted) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = if (isCompleted) "Concluído" else "Marcar",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
