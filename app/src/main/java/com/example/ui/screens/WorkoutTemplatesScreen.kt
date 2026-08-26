package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Exercise
import com.example.data.model.ExerciseSetEntry
import com.example.data.model.MuscleGroup
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutExercisePlan
import com.example.data.model.WorkoutTemplate
import com.example.ui.components.LocationSelector
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.LimeSuccess
import com.example.ui.theme.OrangeLight
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.RedDestructive
import com.example.ui.viewmodel.FitnessViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Composable
fun WorkoutTemplatesScreen(
    viewModel: FitnessViewModel,
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val templates by viewModel.workoutTemplates.collectAsStateWithLifecycle()
    val allExercises by viewModel.allExercises.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var selectedCategoryFilter by remember { mutableStateOf<WorkoutCategory?>(null) }
    var showCreateCustomDialog by remember { mutableStateOf(false) }
    var templateToStart by remember { mutableStateOf<WorkoutTemplate?>(null) }
    var selectedLocation by remember {
        mutableStateOf(userProfile?.defaultGymLocation ?: "Academia Smart Fit")
    }

    val filteredTemplates = if (selectedCategoryFilter == null) {
        templates
    } else {
        templates.filter { it.category == selectedCategoryFilter }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Modelos & Treinos",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Escolha sua rotina ou crie seu próprio treino personalizado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Category Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null },
                        label = { Text("Todos") },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimary.copy(alpha = 0.2f),
                            selectedLabelColor = OrangePrimary
                        ),
                        modifier = Modifier.testTag("filter_category_all")
                    )

                    WorkoutCategory.values().forEach { cat ->
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text(cat.label) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OrangePrimary.copy(alpha = 0.2f),
                                selectedLabelColor = OrangePrimary
                            ),
                            modifier = Modifier.testTag("filter_category_${cat.name}")
                        )
                    }
                }
            }

            if (filteredTemplates.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Nenhum treino nesta categoria",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { showCreateCustomDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                            ) {
                                Text("Criar Treino Personalizado")
                            }
                        }
                    }
                }
            } else {
                items(filteredTemplates, key = { it.id }) { template ->
                    WorkoutTemplateCard(
                        template = template,
                        onStartClick = {
                            templateToStart = template
                        },
                        onDeleteClick = if (!template.isPreset) {
                            { viewModel.deleteWorkoutTemplate(template.id) }
                        } else null
                    )
                }
            }
        }

        // Floating button to create custom workout
        FloatingActionButton(
            onClick = { showCreateCustomDialog = true },
            containerColor = OrangePrimary,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 20.dp)
                .testTag("fab_create_workout")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "Criar Treino")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Criar Treino", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Dialog before starting workout to confirm gym/training location
    if (templateToStart != null) {
        val template = templateToStart!!
        AlertDialog(
            onDismissRequest = { templateToStart = null },
            title = {
                Text(
                    text = "Iniciar ${template.title}",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Onde você vai treinar hoje?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LocationSelector(
                        selectedLocation = selectedLocation,
                        onLocationSelected = { selectedLocation = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "⏱️ Tempo estimado: ${template.executionDurationMinutes} min • Descanso padrão: ${template.defaultRestSeconds}s",
                        style = MaterialTheme.typography.bodySmall,
                        color = OrangeLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val currentTemplate = templateToStart
                        templateToStart = null
                        if (currentTemplate != null) {
                            viewModel.startWorkoutFromTemplate(currentTemplate, selectedLocation)
                            onStartWorkout()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_confirm_start_workout")
                ) {
                    Text("Começar Agora", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { templateToStart = null }) {
                    Text("Cancelar")
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Create Custom Workout Dialog
    if (showCreateCustomDialog) {
        CreateCustomWorkoutDialog(
            allExercises = allExercises,
            onDismiss = { showCreateCustomDialog = false },
            onSave = { title, subtitle, category, exercises, desc, duration, rest ->
                viewModel.createAndSaveWorkoutTemplate(
                    title = title,
                    subtitle = subtitle,
                    category = category,
                    exercises = exercises,
                    description = desc,
                    durationMin = duration,
                    restSec = rest
                )
                showCreateCustomDialog = false
            }
        )
    }
}

@Composable
fun WorkoutTemplateCard(
    template: WorkoutTemplate,
    onStartClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    val moshi = remember { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
    val planListType = remember { Types.newParameterizedType(List::class.java, WorkoutExercisePlan::class.java) }
    val plansAdapter = remember { moshi.adapter<List<WorkoutExercisePlan>>(planListType) }

    val plans: List<WorkoutExercisePlan> = remember(template.exercisesJson) {
        try {
            plansAdapter.fromJson(template.exercisesJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("card_template_${template.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = template.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = template.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(OrangePrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = template.category.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = OrangeLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Duration and rest info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = OrangeLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "~${template.executionDurationMinutes} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${plans.size} exercícios",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "Descanso: ${template.defaultRestSeconds}s",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (template.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = template.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            // Exercise list expander
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text(
                        text = "Lista de Exercícios:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OrangeLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    plans.forEachIndexed { idx, p ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${idx + 1}. ${p.exerciseName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${p.muscleGroup} • ${p.sets.size} séries x ${p.sets.firstOrNull()?.reps ?: 10} reps",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "${p.targetRestSeconds}s descanso",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OrangeLight
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.testTag("btn_expand_${template.id}")
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = OrangeLight
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (expanded) "Recolher" else "Ver Exercícios",
                        color = OrangeLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onDeleteClick != null) {
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.testTag("btn_delete_template_${template.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar treino",
                                tint = RedDestructive
                            )
                        }
                    }

                    Button(
                        onClick = onStartClick,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_start_template_${template.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Iniciar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateCustomWorkoutDialog(
    allExercises: List<Exercise>,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        subtitle: String,
        category: WorkoutCategory,
        exercises: List<WorkoutExercisePlan>,
        description: String,
        durationMinutes: Int,
        restSeconds: Int
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(WorkoutCategory.PERSONALIZADO) }
    var restSeconds by remember { mutableStateOf(60) }
    var durationMinutes by remember { mutableStateOf(50) }
    var description by remember { mutableStateOf("") }

    val selectedExerciseIds = remember { mutableStateOf(mutableSetOf<Long>()) }
    var selectedMuscleGroupFilter by remember { mutableStateOf<MuscleGroup?>(null) }

    val filteredExercises = if (selectedMuscleGroupFilter == null) {
        allExercises
    } else {
        allExercises.filter { it.muscleGroup == selectedMuscleGroupFilter }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Criar Treino Personalizado",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nome do Treino *") },
                    placeholder = { Text("Ex: Treino de Braços & Peitoral") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_custom_title")
                )

                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Subtítulo / Divisão") },
                    placeholder = { Text("Ex: Foco em Tríceps e Peitoral Superior") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                Text(
                    text = "Categoria:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    WorkoutCategory.values().forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.label, fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // Rest & Duration settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = durationMinutes.toString(),
                        onValueChange = { durationMinutes = it.toIntOrNull() ?: 45 },
                        label = { Text("Duração (min)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = restSeconds.toString(),
                        onValueChange = { restSeconds = it.toIntOrNull() ?: 60 },
                        label = { Text("Descanso (s)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Exercises Picker
                Text(
                    text = "Selecione os Exercícios (${selectedExerciseIds.value.size} selecionados):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = OrangeLight
                )

                // Muscle Group filter for exercise picker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedMuscleGroupFilter == null,
                        onClick = { selectedMuscleGroupFilter = null },
                        label = { Text("Todos", fontSize = 11.sp) },
                        shape = RoundedCornerShape(8.dp)
                    )
                    MuscleGroup.values().forEach { mg ->
                        FilterChip(
                            selected = selectedMuscleGroupFilter == mg,
                            onClick = { selectedMuscleGroupFilter = mg },
                            label = { Text(mg.displayName, fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                // Exercises list checklist
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    filteredExercises.forEach { ex ->
                        val isChecked = selectedExerciseIds.value.contains(ex.id)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isChecked) OrangePrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val currentSet = selectedExerciseIds.value.toMutableSet()
                                    if (isChecked) currentSet.remove(ex.id)
                                    else currentSet.add(ex.id)
                                    selectedExerciseIds.value = currentSet
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ex.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${ex.muscleGroup.displayName} • ${ex.equipment.displayName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (isChecked) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selecionado",
                                        tint = OrangePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && selectedExerciseIds.value.isNotEmpty()) {
                        val chosenExercises = allExercises.filter { selectedExerciseIds.value.contains(it.id) }
                        val plans = chosenExercises.map { ex ->
                            val sets = (1..ex.defaultSets).map {
                                ExerciseSetEntry(
                                    setNumber = it,
                                    weightKg = 20.0,
                                    reps = ex.defaultReps,
                                    isCompleted = false,
                                    restSeconds = restSeconds
                                )
                            }
                            WorkoutExercisePlan(
                                exerciseId = ex.id,
                                exerciseName = ex.name,
                                muscleGroup = ex.muscleGroup.displayName,
                                sets = sets,
                                targetRestSeconds = restSeconds,
                                notes = ex.executionTips
                            )
                        }
                        onSave(
                            title.trim(),
                            subtitle.ifBlank { "${chosenExercises.size} Exercícios" },
                            category,
                            plans,
                            description,
                            durationMinutes,
                            restSeconds
                        )
                    }
                },
                enabled = title.isNotBlank() && selectedExerciseIds.value.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_save_custom_workout")
            ) {
                Text("Salvar Treino", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(24.dp)
    )
}
