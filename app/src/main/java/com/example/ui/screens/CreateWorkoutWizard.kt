package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Exercise
import com.example.data.model.ExerciseSetEntry
import com.example.data.model.MuscleGroup
import com.example.data.model.SetTag
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutExercisePlan
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GradientAction
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.Normalizer
import kotlin.math.abs

private data class WorkoutExerciseDraft(
    val exercise: Exercise,
    val sets: Int = 3,
    val repsMin: Int = 8,
    val repsMax: Int = 12,
    val restSeconds: Int = 90,
    val expanded: Boolean = false
)

private enum class WorkoutFocusType(val label: String, val category: WorkoutCategory) {
    STRENGTH("Força", WorkoutCategory.FORCA),
    HYPERTROPHY("Hipertrofia", WorkoutCategory.HIPERTROFIA),
    ENDURANCE("Resistência", WorkoutCategory.RESISTENCIA)
}

@Composable
fun CreateWorkoutWizardScreen(
    allExercises: List<Exercise>,
    onDismiss: () -> Unit,
    onSave: (String, String, WorkoutCategory, List<WorkoutExercisePlan>, String, Int, Int, Boolean) -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var name by remember { mutableStateOf("") }
    var focusType by remember { mutableStateOf(WorkoutFocusType.HYPERTROPHY) }
    var focusGroup by remember { mutableStateOf(MuscleGroup.PEITO) }
    var query by remember { mutableStateOf("") }
    var groupFilter by remember { mutableStateOf<MuscleGroup?>(null) }
    var draftExercises by remember { mutableStateOf(emptyList<WorkoutExerciseDraft>()) }
    var showDiscardConfirmation by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val statusInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navigationInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val isDirty = name.isNotBlank() || draftExercises.isNotEmpty()
    val availableExercises = remember(allExercises, query, groupFilter, draftExercises) {
        allExercises.filter { exercise ->
            draftExercises.none { it.exercise.id == exercise.id } &&
                (groupFilter == null || exercise.muscleGroup == groupFilter) &&
                (query.isBlank() || exercise.name.contains(query.trim(), ignoreCase = true)) &&
                workoutExerciseAssetId(exercise.name, context) != null
        }
    }
    val representativeExercise = remember(allExercises, focusGroup) {
        allExercises.firstOrNull { it.muscleGroup == focusGroup && workoutExerciseAssetId(it.name, context) != null }
    }
    val summaryGroups = draftExercises.map { it.exercise.muscleGroup.displayName }.distinct().joinToString(" • ")
    val totalDuration = draftExercises.sumOf { it.sets * (1 + it.restSeconds / 60) }.coerceAtLeast(1)
    val minimumStageRequirementsMet = when (step) {
        1 -> name.isNotBlank()
        2 -> draftExercises.isNotEmpty()
        else -> draftExercises.isNotEmpty()
    }

    fun requestLeave() {
        if (isDirty) showDiscardConfirmation = true else onDismiss()
    }

    fun moveExercise(id: Long, direction: Int) {
        val index = draftExercises.indexOfFirst { it.exercise.id == id }
        val target = (index + direction).coerceIn(0, draftExercises.lastIndex)
        if (index < 0 || target == index) return
        draftExercises = draftExercises.toMutableList().apply {
            add(target, removeAt(index))
        }
    }

    fun toPlans(): List<WorkoutExercisePlan> = draftExercises.map { item ->
        WorkoutExercisePlan(
            exerciseId = item.exercise.id,
            exerciseName = item.exercise.name,
            muscleGroup = item.exercise.muscleGroup.displayName,
            sets = (1..item.sets).map { setIndex ->
                ExerciseSetEntry(
                    setNumber = setIndex,
                    weightKg = 0.0,
                    reps = item.repsMin,
                    isCompleted = false,
                    restSeconds = item.restSeconds,
                    setTag = SetTag.NORMAL
                )
            },
            targetRestSeconds = item.restSeconds,
            notes = item.exercise.executionTips
        )
    }

    BackHandler(enabled = true) {
        if (step > 1) step -= 1 else requestLeave()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(PurpleDarkest)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = statusInset + 2.dp, start = 8.dp, end = 16.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { if (step > 1) step -= 1 else requestLeave() }, modifier = Modifier.size(48.dp).testTag("btn_create_workout_back")) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = LilacAccent)
            }
            Text("Criar Treino", modifier = Modifier.weight(1f), color = TextPrimary, fontSize = 23.sp, fontWeight = FontWeight.Black)
            Text("${step}/4", color = LilacAccent, fontWeight = FontWeight.Bold)
        }

        StepIndicator(step = step, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))

        AnimatedContent(
            targetState = step,
            modifier = Modifier.weight(1f),
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "create_workout_step"
        ) { currentStep ->
            when (currentStep) {
                1 -> NameAndFocusStep(
                    name = name,
                    onNameChange = { name = it },
                    focusType = focusType,
                    onFocusTypeChange = { focusType = it },
                    focusGroup = focusGroup,
                    onFocusGroupChange = { focusGroup = it },
                    allExercises = allExercises
                )
                2 -> ExerciseSelectionStep(
                    allExercises = allExercises,
                    availableExercises = availableExercises,
                    addedIds = draftExercises.mapTo(hashSetOf()) { it.exercise.id },
                    query = query,
                    onQueryChange = { query = it },
                    groupFilter = groupFilter,
                    onGroupFilterChange = { groupFilter = it },
                    count = draftExercises.size,
                    onAdd = { exercise -> draftExercises = draftExercises + WorkoutExerciseDraft(exercise) }
                )
                3 -> ExerciseOrderStep(
                    exercises = draftExercises,
                    onExercisesChange = { draftExercises = it },
                    onMove = ::moveExercise,
                    onAddMore = { step = 2 }
                )
                else -> WorkoutReviewStep(name, summaryGroups, draftExercises, totalDuration, ::toPlans)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().background(PurpleDarkest).padding(horizontal = 18.dp).padding(bottom = navigationInset + 12.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            if (step < 4) {
                Button(
                    onClick = { if (minimumStageRequirementsMet) step += 1 },
                    enabled = minimumStageRequirementsMet,
                    modifier = Modifier.fillMaxWidth().height(54.dp).testTag("btn_create_workout_next_$step"),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleVibrant, disabledContainerColor = PurpleDeepCard)
                ) {
                    Text(if (step == 2) "Próximo · ${draftExercises.size} exercícios" else "Próximo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    OutlinedActionButton("Salvar para depois", Modifier.weight(1f).height(52.dp).testTag("btn_save_workout_later")) {
                        onSave(name.trim(), summaryGroups, focusType.category, toPlans(), "", totalDuration, 90, false)
                    }
                    Button(
                        onClick = { onSave(name.trim(), summaryGroups, focusType.category, toPlans(), "", totalDuration, 90, true) },
                        modifier = Modifier.weight(1.25f).height(52.dp).testTag("btn_save_and_start_custom_workout"),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleVibrant)
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null)
                        Spacer(Modifier.width(5.dp))
                        Text("Salvar e iniciar", fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
            }
        }
    }

    if (showDiscardConfirmation) {
        AlertDialog(
            onDismissRequest = { showDiscardConfirmation = false },
            title = { Text("Descartar treino?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("O nome e os exercícios escolhidos serão perdidos.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { showDiscardConfirmation = false; onDismiss() }, modifier = Modifier.testTag("btn_confirm_discard_workout_draft")) {
                    Text("Descartar", color = Color(0xFFFF7A90), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showDiscardConfirmation = false }) { Text("Continuar editando", color = LilacAccent) } },
            containerColor = PurpleDarkSurface
        )
    }
}

@Composable
private fun StepIndicator(step: Int, modifier: Modifier = Modifier) {
    val labels = listOf("Nome e foco", "Exercícios", "Ordem e séries", "Revisão")
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        labels.forEachIndexed { index, label ->
            val stepNumber = index + 1
            val active = stepNumber <= step
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp)) {
                Box(
                    modifier = Modifier.size(if (step == stepNumber) 34.dp else 28.dp)
                        .clip(CircleShape)
                        .background(brush = if (active) GradientAction else Brush.horizontalGradient(listOf(PurpleDarkSurface, PurpleDarkSurface)))
                        .border(1.dp, if (active) LilacAccent else GlassBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (stepNumber < step) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    else Text(stepNumber.toString(), color = if (active) Color.White else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text(label, color = if (step == stepNumber) LilacAccent else TextMuted, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
            }
            if (index < labels.lastIndex) {
                Box(Modifier.weight(1f).height(2.dp).background(if (stepNumber < step) LilacAccent else GlassBorder))
            }
        }
    }
}

@Composable
private fun NameAndFocusStep(
    name: String,
    onNameChange: (String) -> Unit,
    focusType: WorkoutFocusType,
    onFocusTypeChange: (WorkoutFocusType) -> Unit,
    focusGroup: MuscleGroup,
    onFocusGroupChange: (MuscleGroup) -> Unit,
    allExercises: List<Exercise>
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Nome do treino", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth().testTag("input_custom_title"),
            placeholder = { Text("Ex.: Treino Push") },
            singleLine = true,
            shape = RoundedCornerShape(13.dp),
            colors = workoutTextFieldColors()
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Tipo de treino", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WorkoutFocusType.entries.forEach { type ->
                    val selected = focusType == type
                    Surface(
                        onClick = { onFocusTypeChange(type) },
                        modifier = Modifier.weight(1f).heightIn(min = 74.dp).testTag("workout_type_${type.name.lowercase()}"),
                        shape = RoundedCornerShape(15.dp),
                        color = if (selected) Color(0xFF24133E) else Color(0xFF0D1420),
                        border = BorderStroke(if (selected) 1.5.dp else 1.dp, if (selected) LilacAccent else GlassBorderSubtle)
                    ) {
                        Column(Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(if (type == WorkoutFocusType.STRENGTH) Icons.Default.FitnessCenter else if (type == WorkoutFocusType.HYPERTROPHY) Icons.Default.FlashOn else Icons.Default.Tune, contentDescription = null, tint = if (selected) LilacAccent else TextSecondary)
                            Text(type.label, color = if (selected) Color.White else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        Text("Foco principal", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        MuscleGroup.values().toList().chunked(3).forEach { rowGroups ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowGroups.forEach { group ->
                    val selected = focusGroup == group
                    val groupExercise = allExercises.firstOrNull { it.muscleGroup == group && workoutExerciseAssetId(it.name, context) != null }
                    val asset = groupExercise?.let { workoutExerciseAssetId(it.name, context) }
                    Surface(
                        onClick = { onFocusGroupChange(group) },
                        modifier = Modifier.weight(1f).height(112.dp).testTag("focus_group_${group.name.lowercase()}"),
                        shape = RoundedCornerShape(13.dp),
                        color = if (selected) Color(0xFF25113F) else Color(0xFF0C121C),
                        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) LilacAccent else GlassBorderSubtle)
                    ) {
                        Column(Modifier.fillMaxSize().padding(7.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            if (asset != null) Image(painterResource(asset), contentDescription = null, modifier = Modifier.weight(1f).fillMaxWidth(), contentScale = ContentScale.Fit)
                            else Icon(Icons.Default.FitnessCenter, null, tint = LilacAccent, modifier = Modifier.weight(1f).size(36.dp))
                            Text(group.shortMuscleLabel(), color = if (selected) Color.White else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
                repeat(3 - rowGroups.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun ExerciseSelectionStep(
    allExercises: List<Exercise>,
    availableExercises: List<Exercise>,
    addedIds: Set<Long>,
    query: String,
    onQueryChange: (String) -> Unit,
    groupFilter: MuscleGroup?,
    onGroupFilterChange: (MuscleGroup?) -> Unit,
    count: Int,
    onAdd: (Exercise) -> Unit
) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(top = 4.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Selecionar exercícios", modifier = Modifier.weight(1f), color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Text("$count adicionados", color = LilacAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 14.dp, vertical = 9.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(selected = groupFilter == null, onClick = { onGroupFilterChange(null) }, label = { Text("Todos") })
            MuscleGroup.values().forEach { group ->
                FilterChip(selected = groupFilter == group, onClick = { onGroupFilterChange(group) }, label = { Text(group.shortMuscleLabel()) })
            }
        }
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp).testTag("input_exercise_search"),
            placeholder = { Text("Buscar exercício...") },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = LilacAccent) },
            singleLine = true,
            shape = RoundedCornerShape(13.dp),
            colors = workoutTextFieldColors()
        )
        if (availableExercises.isEmpty()) {
            Column(Modifier.fillMaxSize().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(if (query.isBlank() && groupFilter == null) "A biblioteca ainda está carregando." else "Nenhum exercício encontrado.", color = TextSecondary, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableExercises, key = { it.id }) { exercise ->
                    val asset = workoutExerciseAssetId(exercise.name, context)
                    Surface(
                        onClick = { onAdd(exercise) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 76.dp).testTag("add_exercise_${exercise.id}"),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0B111B),
                        border = BorderStroke(1.dp, GlassBorderSubtle)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(9.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (asset != null) Image(painterResource(asset), exercise.name, modifier = Modifier.size(58.dp).clip(RoundedCornerShape(10.dp)).background(PurpleDarkSurface), contentScale = ContentScale.Crop)
                            Column(Modifier.weight(1f)) {
                                Text(exercise.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(exercise.muscleGroup.displayName, color = LilacAccent, fontSize = 11.sp)
                                Text(exercise.equipment.displayName, color = TextMuted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Box(Modifier.size(38.dp).clip(CircleShape).background(brush = GradientAction), contentAlignment = Alignment.Center) {
                                Icon(if (exercise.id in addedIds) Icons.Default.Check else Icons.Default.Add, if (exercise.id in addedIds) "Adicionado" else "Adicionar", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseOrderStep(
    exercises: List<WorkoutExerciseDraft>,
    onExercisesChange: (List<WorkoutExerciseDraft>) -> Unit,
    onMove: (Long, Int) -> Unit,
    onAddMore: () -> Unit
) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text("Exercícios adicionados (${exercises.size})", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
        if (exercises.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { Text("Adicione pelo menos um exercício.", color = TextSecondary) }
        } else {
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(bottom = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(exercises, key = { it.exercise.id }) { draft ->
                    val index = exercises.indexOfFirst { it.exercise.id == draft.exercise.id }
                    val asset = workoutExerciseAssetId(draft.exercise.name, context)
                    Surface(
                        modifier = Modifier.fillMaxWidth().testTag("draft_exercise_${draft.exercise.id}"),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0B111B),
                        border = BorderStroke(1.dp, if (draft.expanded) LilacAccent.copy(alpha = .65f) else GlassBorderSubtle)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(
                                    Icons.Default.DragHandle,
                                    contentDescription = "Segure para reordenar",
                                    tint = LilacAccent,
                                    modifier = Modifier.size(34.dp).pointerInput(draft.exercise.id) {
                                        var accumulated = 0f
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = { accumulated = 0f },
                                            onDragEnd = { accumulated = 0f },
                                            onDragCancel = { accumulated = 0f },
                                            onDrag = { change, amount ->
                                                change.consume()
                                                accumulated += amount.y
                                                if (abs(accumulated) >= 72f) {
                                                    onMove(draft.exercise.id, if (accumulated > 0) 1 else -1)
                                                    accumulated = 0f
                                                }
                                            }
                                        )
                                    }
                                )
                                if (asset != null) Image(painterResource(asset), draft.exercise.name, modifier = Modifier.size(54.dp).clip(RoundedCornerShape(9.dp)), contentScale = ContentScale.Crop)
                                Column(Modifier.weight(1f).clickable {
                                    onExercisesChange(exercises.map { it.copy(expanded = it.exercise.id == draft.exercise.id && !draft.expanded) })
                                }) {
                                    Text(draft.exercise.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    Text("${draft.sets} × ${draft.repsMin}–${draft.repsMax} · ${draft.restSeconds}s", color = LilacAccent, fontSize = 11.sp)
                                }
                                IconButton(onClick = { onExercisesChange(exercises.filterNot { it.exercise.id == draft.exercise.id }) }, modifier = Modifier.size(42.dp).testTag("remove_draft_exercise_${draft.exercise.id}")) {
                                    Icon(Icons.Default.Delete, "Remover ${draft.exercise.name}", tint = Color(0xFFFF7A90))
                                }
                            }
                            if (draft.expanded) {
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    DraftStepper("Séries", draft.sets, 1, 10, Modifier.weight(1f)) { value -> onExercisesChange(exercises.map { if (it.exercise.id == draft.exercise.id) it.copy(sets = value) else it }) }
                                    DraftStepper("Rep. mín.", draft.repsMin, 1, draft.repsMax, Modifier.weight(1f)) { value -> onExercisesChange(exercises.map { if (it.exercise.id == draft.exercise.id) it.copy(repsMin = value) else it }) }
                                }
                                Spacer(Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    DraftStepper("Rep. máx.", draft.repsMax, draft.repsMin, 50, Modifier.weight(1f)) { value -> onExercisesChange(exercises.map { if (it.exercise.id == draft.exercise.id) it.copy(repsMax = value) else it }) }
                                    DraftStepper("Descanso (s)", draft.restSeconds, 15, 300, Modifier.weight(1f), step = 15) { value -> onExercisesChange(exercises.map { if (it.exercise.id == draft.exercise.id) it.copy(restSeconds = value) else it }) }
                                }
                            }
                        }
                    }
                }
                item {
                    OutlinedActionButton("+ Adicionar mais exercícios", Modifier.fillMaxWidth().height(48.dp).testTag("btn_add_more_exercises"), onClick = onAddMore)
                }
            }
        }
    }
}

@Composable
private fun DraftStepper(label: String, value: Int, minimum: Int, maximum: Int, modifier: Modifier = Modifier, step: Int = 1, onChange: (Int) -> Unit) {
    Surface(modifier, shape = RoundedCornerShape(11.dp), color = PurpleDarkSurface, border = BorderStroke(1.dp, GlassBorderSubtle)) {
        Column(Modifier.padding(horizontal = 5.dp, vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = TextMuted, fontSize = 9.sp, maxLines = 1)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                IconButton(onClick = { onChange((value - step).coerceAtLeast(minimum)) }, modifier = Modifier.size(30.dp)) { Icon(Icons.Default.Remove, null, tint = LilacAccent, modifier = Modifier.size(16.dp)) }
                Text(value.toString(), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = { onChange((value + step).coerceAtMost(maximum)) }, modifier = Modifier.size(30.dp)) { Icon(Icons.Default.Add, null, tint = LilacAccent, modifier = Modifier.size(16.dp)) }
            }
        }
    }
}

@Composable
private fun WorkoutReviewStep(name: String, groups: String, exercises: List<WorkoutExerciseDraft>, duration: Int, toPlans: () -> List<WorkoutExercisePlan>) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Resumo do treino", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFF100C1D), border = BorderStroke(1.dp, LilacAccent.copy(alpha = .45f)), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val leadAsset = exercises.firstOrNull()?.let { workoutExerciseAssetId(it.exercise.name, context) }
                if (leadAsset != null) Image(painterResource(leadAsset), null, modifier = Modifier.size(88.dp).clip(RoundedCornerShape(13.dp)), contentScale = ContentScale.Crop)
                else Icon(Icons.Default.FitnessCenter, null, tint = LilacAccent, modifier = Modifier.size(72.dp))
                Column {
                    Text(name.ifBlank { "Treino personalizado" }, color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(groups.ifBlank { "Grupos variados" }, color = LilacAccent, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(7.dp))
                    Text("${exercises.size} exercícios  ·  ~${duration.coerceAtLeast(1)} min", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
        exercises.forEachIndexed { index, draft ->
            Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF0B111B), border = BorderStroke(1.dp, GlassBorderSubtle), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(Modifier.size(28.dp).clip(CircleShape).background(PurpleDeepCard), contentAlignment = Alignment.Center) { Text("${index + 1}", color = LilacAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    Column(Modifier.weight(1f)) {
                        Text(draft.exercise.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text("${draft.sets} × ${draft.repsMin}–${draft.repsMax} · ${draft.restSeconds}s", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }
        Text("Confira séries, repetições e descanso antes de salvar.", color = TextMuted, fontSize = 11.sp)
    }
}

@Composable
private fun OutlinedActionButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(14.dp), color = Color.Transparent, border = BorderStroke(1.dp, LilacAccent)) {
        Box(contentAlignment = Alignment.Center) { Text(text, color = LilacAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }
    }
}

@Composable
private fun workoutTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedBorderColor = LilacAccent,
    unfocusedBorderColor = GlassBorder,
    focusedLabelColor = LilacAccent,
    unfocusedLabelColor = TextSecondary,
    cursorColor = LilacAccent
)

private fun workoutExerciseAssetId(name: String, context: android.content.Context): Int? {
    val slug = Normalizer.normalize(name.lowercase(), Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .replace("[^a-z0-9]+".toRegex(), "_")
        .trim('_')
    context.resources.getIdentifier("exercise_$slug", "drawable", context.packageName).takeIf { it != 0 }?.let { return it }
    for (number in 1..12) {
        val indexed = context.resources.getIdentifier("exercise_${number.toString().padStart(2, '0')}_$slug", "drawable", context.packageName)
        if (indexed != 0) return indexed
    }
    return null
}

private fun MuscleGroup.shortMuscleLabel(): String = when (this) {
    MuscleGroup.QUADRICEPS -> "Pernas"
    MuscleGroup.POSTERIOR_GLUTEOS -> "Glúteos"
    else -> displayName
}
