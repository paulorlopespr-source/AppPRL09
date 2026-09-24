package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.Exercise
import com.example.data.model.ExerciseSetEntry
import com.example.data.model.MuscleGroup
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutExercisePlan
import com.example.data.model.WorkoutTemplate
import com.example.ui.components.BentoCard
import com.example.ui.components.LiquidGlassSurface
import com.example.ui.components.LocationSelector
import com.example.ui.components.PrimaryButton
import com.example.ui.components.QuickWorkoutSheet
import com.example.ui.components.SecondaryButton
import com.example.ui.components.WorkoutReminderDialog
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
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
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutTemplatesScreen(
    viewModel: FitnessViewModel,
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier,
    openExerciseLibrary: Boolean = false,
    onExerciseLibraryOpened: () -> Unit = {}
) {
    val templates by viewModel.workoutTemplates.collectAsStateWithLifecycle()
    val allExercises by viewModel.allExercises.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val reminderSettings by viewModel.reminderSettings.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Meus Treinos, 1: Treino Rápido, 2: Favoritos
    var selectedCategoryFilter by remember { mutableStateOf<WorkoutCategory?>(null) }
    var filterOnlyFavorites by remember { mutableStateOf(false) }
    var filterOnlyCustom by remember { mutableStateOf(false) }
    var showCreateCustomDialog by remember { mutableStateOf(false) }
    var showExerciseLibrary by remember { mutableStateOf(false) }
    var showQuickWorkoutSheet by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var templateToStart by remember { mutableStateOf<WorkoutTemplate?>(null) }
    var selectedLocation by remember {
        mutableStateOf(userProfile?.defaultGymLocation ?: "Smart Fit Paulista")
    }

    LaunchedEffect(openExerciseLibrary) {
        if (openExerciseLibrary) {
            showExerciseLibrary = true
            onExerciseLibraryOpened()
        }
    }

    val filteredTemplates = templates.filter { template ->
        val matchesTab = when (selectedTabIndex) {
            0 -> true // Meus Treinos (todos os treinos padrão/usuário)
            1 -> template.executionDurationMinutes <= 45 || template.subtitle.contains("Rápido", ignoreCase = true)
            2 -> template.isFavorite
            else -> true
        }
        val matchesCategory = selectedCategoryFilter == null || template.category == selectedCategoryFilter
        val matchesFavorites = !filterOnlyFavorites || template.isFavorite
        val matchesCustom = !filterOnlyCustom || !template.isPreset
        matchesTab && matchesCategory && matchesFavorites && matchesCustom
    }

    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0A14))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = topInset + 12.dp, bottom = bottomInset + 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header (Menu + Título "Treinos" + Subtítulo + Notificações com Badge)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = { /* Menu de opções / atalhos */ },
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("btn_workout_menu")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color(0xFFC4B5FD),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Treinos",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Escolha seu treino e evolua.",
                                fontSize = 14.sp,
                                color = Color(0xFFC4B5FD)
                            )
                        }
                    }

                    IconButton(
                        onClick = { showReminderDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("btn_workout_notifications")
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = Color(0xFFA855F7),
                                    modifier = Modifier.size(8.dp)
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Lembretes e Notificações",
                                tint = Color(0xFFC4B5FD),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }

            // 2. Tabs (Meus Treinos / Treino Rápido / Favoritos)
            item {
                val tabTitles = listOf("Meus Treinos", "Treino Rápido", "Favoritos")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        val isSelected = selectedTabIndex == index
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable {
                                    selectedTabIndex = index
                                    if (index == 1) {
                                        showQuickWorkoutSheet = true
                                    }
                                }
                                .testTag("tab_workout_pill_$index"),
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.05f),
                            border = if (isSelected) null else BorderStroke(1.dp, Color(0xFF6D28D9).copy(alpha = 0.35f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .then(
                                        if (isSelected) {
                                            Modifier.background(
                                                Brush.horizontalGradient(
                                                    listOf(Color(0xFF9333EA), Color(0xFF8B5CF6))
                                                )
                                            )
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .padding(horizontal = 20.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    color = if (isSelected) Color.White else Color(0xFFC4B5FD).copy(alpha = 0.8f),
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Optional secondary category filter chips when exploring
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
                        label = { Text("Todas Categorias") },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurpleDeepCard,
                            selectedLabelColor = LilacAccent,
                            containerColor = PurpleDarkSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategoryFilter == null,
                            borderColor = if (selectedCategoryFilter == null) LilacAccent else GlassBorder
                        ),
                        modifier = Modifier.testTag("filter_category_all")
                    )

                    WorkoutCategory.values().forEach { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategoryFilter = if (isSelected) null else cat
                            },
                            label = { Text(cat.label.substringBefore(" (").trim()) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PurpleDeepCard,
                                selectedLabelColor = LilacAccent,
                                containerColor = PurpleDarkSurface,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) LilacAccent else GlassBorder
                            ),
                            modifier = Modifier.testTag("filter_category_${cat.name}")
                        )
                    }
                }
            }

            // 3. Cards de Treino (itemsIndexed)
            if (filteredTemplates.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFF6D28D9).copy(alpha = 0.4f)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0B18)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = when (selectedTabIndex) {
                                    2 -> "Nenhum treino marcado como favorito"
                                    1 -> "Nenhum treino rápido curto encontrado"
                                    else -> "Nenhum treino encontrado nesta categoria"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                onClick = {
                                    if (selectedTabIndex == 1) showQuickWorkoutSheet = true
                                    else showCreateCustomDialog = true
                                },
                                shape = RoundedCornerShape(50),
                                color = Color.Transparent,
                                modifier = Modifier.clip(RoundedCornerShape(50))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFF9333EA), Color(0xFF8B5CF6))
                                            )
                                        )
                                        .padding(horizontal = 22.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = if (selectedTabIndex == 1) "Abrir Treino Rápido" else "Criar Rotina Personalizada",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                itemsIndexed(filteredTemplates, key = { _, template -> template.id }) { index, template ->
                    WorkoutTemplateCard(
                        template = template,
                        onStartClick = {
                            templateToStart = template
                        },
                        onToggleFavorite = {
                            viewModel.toggleFavoriteWorkoutTemplate(template)
                        },
                        onDuplicate = {
                            viewModel.duplicateWorkoutTemplate(template)
                        },
                        onDeleteClick = if (!template.isPreset) {
                            { viewModel.deleteWorkoutTemplate(template.id) }
                        } else null
                    )
                }
            }
        }

        // Floating Action Button to create custom workout
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 86.dp, end = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(GradientAction)
                    .clickable { showCreateCustomDialog = true }
                    .padding(horizontal = 18.dp, vertical = 14.dp)
                    .testTag("fab_create_workout"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Criar Treino", tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Criar Treino", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }

    // Quick Workout Sheet modal
    if (showQuickWorkoutSheet) {
        QuickWorkoutSheet(
            onDismiss = { showQuickWorkoutSheet = false },
            onStartStrengthWorkout = { title, location, plans ->
                viewModel.startWorkoutWithPlans(title = title, location = location, plans = plans)
                showQuickWorkoutSheet = false
                onStartWorkout()
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
            }
        )
    }

    // Reminder Dialog
    if (showReminderDialog) {
        WorkoutReminderDialog(
            initialSettings = reminderSettings,
            onSaveSettings = { updated ->
                viewModel.updateReminderSettings(updated)
                showReminderDialog = false
            },
            onTestNotification = {
                viewModel.triggerTestReminderNotification()
            },
            onDismiss = { showReminderDialog = false }
        )
    }

    // Dialog before starting workout to confirm gym/training location
    if (templateToStart != null) {
        val template = templateToStart!!
        AlertDialog(
            onDismissRequest = { templateToStart = null },
            title = {
                Text(
                    text = "Iniciar ${template.title}",
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Onde você vai treinar agora?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LocationSelector(
                        selectedLocation = selectedLocation,
                        onLocationSelected = { selectedLocation = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(PurpleDeepCard)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "⏱️ Estimado: ${template.executionDurationMinutes} min • Descanso: ${template.defaultRestSeconds}s",
                            style = MaterialTheme.typography.bodySmall,
                            color = LilacSoft,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
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
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_confirm_start_workout")
                ) {
                    Text("Começar Agora", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { templateToStart = null }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = PurpleDarkSurface,
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

    if (showExerciseLibrary) {
        ExerciseLibraryDialog(
            allExercises = allExercises,
            onDismiss = { showExerciseLibrary = false }
        )
    }
}

@Composable
private fun ExerciseLibraryDialog(
    allExercises: List<Exercise>,
    onDismiss: () -> Unit
) {
    var selectedGroup by remember { mutableStateOf<MuscleGroup?>(null) }
    val context = LocalContext.current
    val filteredExercises = allExercises.filter {
        (selectedGroup == null || it.muscleGroup == selectedGroup) && exerciseAssetId(it.name, context) != null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Biblioteca de Exercícios", color = TextPrimary, fontWeight = FontWeight.Black)
        },
        text = {
            Column(Modifier.fillMaxWidth().height(520.dp)) {
                Text("${filteredExercises.size} exercícios disponíveis", color = TextSecondary, fontSize = 12.sp)
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedGroup == null,
                        onClick = { selectedGroup = null },
                        label = { Text("Todos") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PurplePrimary)
                    )
                    MuscleGroup.values().forEach { muscle ->
                        FilterChip(
                            selected = selectedGroup == muscle,
                            onClick = { selectedGroup = muscle },
                            label = { Text(muscle.displayName) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PurplePrimary)
                        )
                    }
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredExercises, key = { it.id }) { exercise ->
                        Surface(
                            color = PurpleDarkSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, GlassBorderSubtle)
                        ) {
                            Row(
                                Modifier.fillMaxWidth().padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                exerciseLibraryImage(exercise.name)?.let { resId ->
                                    Image(
                                        painter = painterResource(resId),
                                        contentDescription = exercise.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(62.dp).clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(Modifier.width(10.dp))
                                }
                                Column {
                                    Text(exercise.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Text(exercise.muscleGroup.displayName, color = LilacAccent, fontSize = 12.sp)
                                    Text(exercise.equipment.displayName, color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fechar", color = LilacAccent) }
        },
        containerColor = PurpleDarkSurface,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun WorkoutTemplateCard(
    template: WorkoutTemplate,
    onStartClick: () -> Unit,
    onToggleFavorite: (() -> Unit)? = null,
    onDuplicate: (() -> Unit)? = null,
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

    // Extract clean display title and muscle group subtitle to match reference
    val (displayTitle, displaySubtitle) = remember(template.title, template.subtitle) {
        when {
            template.title.contains(" - ") && template.title.contains(":") -> {
                val afterDash = template.title.substringAfter(" - ")
                val mainTitle = afterDash.substringBefore(":").trim()
                val sub = afterDash.substringAfter(":").trim()
                Pair(mainTitle, sub)
            }
            template.title.contains(":") -> {
                val mainTitle = template.title.substringBefore(":").trim()
                val sub = template.title.substringAfter(":").trim()
                Pair(mainTitle, sub)
            }
            template.subtitle.isNotBlank() && !template.subtitle.startsWith("Divisão") -> {
                Pair(template.title, template.subtitle)
            }
            template.title.contains("Treino", ignoreCase = true) -> {
                val sub = when {
                    template.title.contains("A", ignoreCase = true) -> "Peito, Ombros e Tríceps"
                    template.title.contains("B", ignoreCase = true) -> "Costas e Bíceps"
                    template.title.contains("C", ignoreCase = true) -> "Pernas e Core"
                    else -> template.category.label.substringBefore(" (").trim()
                }
                Pair(template.title, sub)
            }
            else -> Pair(template.title, template.subtitle.ifBlank { template.category.label })
        }
    }

    // Workout category level label (e.g. "Intermediário", "Iniciante", "Avançado")
    val levelLabel = remember(template.category) {
        when (template.category) {
            WorkoutCategory.INICIANTE -> "Iniciante"
            WorkoutCategory.INTERMEDIARIO -> "Intermediário"
            WorkoutCategory.AVANCADO -> "Avançado"
            else -> template.category.label.substringBefore(" (").trim()
        }
    }

    // High quality background workout photo
    val workoutImageRes = remember(displayTitle, displaySubtitle, template.id) {
        when {
            displayTitle.contains("A", ignoreCase = true) || displaySubtitle.contains("Peito", ignoreCase = true) || displaySubtitle.contains("Push", ignoreCase = true) -> R.drawable.img_bench_press
            displayTitle.contains("B", ignoreCase = true) || displaySubtitle.contains("Costas", ignoreCase = true) || displaySubtitle.contains("Pull", ignoreCase = true) -> R.drawable.img_lat_pulldown
            displayTitle.contains("C", ignoreCase = true) || displaySubtitle.contains("Perna", ignoreCase = true) || displaySubtitle.contains("Leg", ignoreCase = true) -> R.drawable.img_barbell_squat
            else -> when (template.id % 3) {
                0L -> R.drawable.img_bench_press
                1L -> R.drawable.img_lat_pulldown
                else -> R.drawable.img_barbell_squat
            }
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF6D28D9).copy(alpha = 0.4f)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0B18)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_template_${template.id}")
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Right-aligned background photo
            Image(
                painter = painterResource(id = workoutImageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .align(Alignment.CenterEnd)
            )

            // Horizontal gradient overlay: Dark on left -> Transparent on right for high contrast text
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0F0B18),
                                Color(0xFF0F0B18).copy(alpha = 0.95f),
                                Color(0xFF0F0B18).copy(alpha = 0.65f),
                                Color(0xFF0F0B18).copy(alpha = 0.25f)
                            )
                        )
                    )
            )

            // Card Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Top row: Title + Subtitle and Circular Chevron Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = displayTitle,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (template.isFavorite) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Favorito",
                                    tint = AmberWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = displaySubtitle,
                            color = Color(0xFFC4B5FD),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Circular Chevron IconButton in top right with purple border
                    Surface(
                        onClick = { expanded = !expanded },
                        shape = CircleShape,
                        color = Color(0xFF1F1435).copy(alpha = 0.65f),
                        border = BorderStroke(1.dp, Color(0xFF6D28D9).copy(alpha = 0.7f)),
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("btn_details_${template.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ChevronRight,
                                contentDescription = "Ver detalhes",
                                tint = Color(0xFFC4B5FD),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Metadata row: Schedule + Duration and BarChart + Level
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFFC4B5FD),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "~ ${template.executionDurationMinutes} min",
                            color = Color(0xFFC4B5FD),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = Color(0xFFC4B5FD),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = levelLabel,
                            color = Color(0xFFC4B5FD),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom row: Quick action icons (if present) & Iniciar treino Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onToggleFavorite != null) {
                            IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = if (template.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = if (template.isFavorite) "Desfavoritar" else "Favoritar",
                                    tint = if (template.isFavorite) AmberWarning else Color(0xFFC4B5FD).copy(alpha = 0.5f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        if (onDuplicate != null) {
                            IconButton(onClick = onDuplicate, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Duplicar",
                                    tint = Color(0xFFC4B5FD).copy(alpha = 0.5f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        if (onDeleteClick != null) {
                            IconButton(
                                onClick = onDeleteClick,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("btn_delete_template_${template.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Deletar treino",
                                    tint = RedDestructive.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Button "Iniciar treino": shape = RoundedCornerShape(50), purple/violet gradient, white bold text
                    Surface(
                        onClick = onStartClick,
                        shape = RoundedCornerShape(50),
                        color = Color.Transparent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .testTag("btn_start_template_${template.id}")
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF9333EA), Color(0xFF8B5CF6))
                                    )
                                )
                                .padding(horizontal = 24.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Iniciar treino",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Expanded exercise list
                AnimatedVisibility(visible = expanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            text = "LISTA DE EXERCÍCIOS (${plans.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC4B5FD),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        plans.forEachIndexed { idx, p ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF171029).copy(alpha = 0.8f),
                                border = BorderStroke(1.dp, Color(0xFF6D28D9).copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${idx + 1}. ${p.exerciseName}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${p.muscleGroup} • ${p.sets.size} séries x ${p.sets.firstOrNull()?.reps ?: 10} reps",
                                            fontSize = 11.sp,
                                            color = Color(0xFFC4B5FD).copy(alpha = 0.8f)
                                        )
                                    }
                                    Text(
                                        text = "${p.targetRestSeconds}s descanso",
                                        fontSize = 11.sp,
                                        color = Color(0xFFC4B5FD),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
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
    var searchQuery by remember { mutableStateOf("") }
    var creationMode by remember { mutableStateOf("Rápido") }
    val selectedExerciseIds = remember { mutableStateOf(mutableSetOf<Long>()) }
    var selectedMuscleGroupFilter by remember { mutableStateOf<MuscleGroup?>(null) }
    var editingExercise by remember { mutableStateOf<Exercise?>(null) }
    val context = LocalContext.current
    LaunchedEffect(allExercises) {
        if (selectedExerciseIds.value.isEmpty() && allExercises.isNotEmpty()) {
            val suggested = allExercises.filter { exercise ->
                listOf("supino", "crucifixo", "paralelas").any { key -> exercise.name.contains(key, ignoreCase = true) }
            }.take(4)
            if (suggested.isNotEmpty()) selectedExerciseIds.value = suggested.map { it.id }.toMutableSet()
        }
    }
    val selectedExercises = allExercises.filter { selectedExerciseIds.value.contains(it.id) }
    val availableExercises = allExercises.filter { exercise ->
        !selectedExerciseIds.value.contains(exercise.id) &&
            (selectedMuscleGroupFilter == null || exercise.muscleGroup == selectedMuscleGroupFilter) &&
            exercise.name.contains(searchQuery, ignoreCase = true) && exerciseAssetId(exercise.name, context) != null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.ChevronRight, "Voltar", tint = LilacAccent, modifier = Modifier.rotate(180f))
                }
                Text("Criar treino", modifier = Modifier.weight(1f), fontWeight = FontWeight.Black, color = TextPrimary, fontSize = 24.sp)
                Icon(Icons.Default.StarBorder, "Salvar modelo", tint = LilacAccent)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(PurpleDarkest).padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Rápido", "Personalizado").forEach { mode ->
                        Surface(
                            modifier = Modifier.weight(1f).clickable { creationMode = mode },
                            shape = RoundedCornerShape(11.dp),
                            color = if (creationMode == mode) PurplePrimary else Color.Transparent
                        ) {
                            Text(mode, modifier = Modifier.padding(vertical = 10.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = if (creationMode == mode) Color.White else TextSecondary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text("Qual é o foco de hoje?", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(MuscleGroup.PEITO, MuscleGroup.COSTAS, MuscleGroup.QUADRICEPS, MuscleGroup.OMBROS, MuscleGroup.BICEPS, MuscleGroup.TRICEPS, MuscleGroup.ABDOMEN).forEach { group ->
                        FilterChip(
                            selected = selectedMuscleGroupFilter == group,
                            onClick = { selectedMuscleGroupFilter = if (selectedMuscleGroupFilter == group) null else group },
                            label = { Text(group.displayName) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PurplePrimary)
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nome do Treino *") },
                    placeholder = { Text("Ex: Peito e tríceps") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = LilacAccent,
                        unfocusedBorderColor = GlassBorder,
                        focusedLabelColor = LilacAccent,
                        unfocusedLabelColor = TextSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_custom_title")
                )

                Text(
                    text = "Nome do seu treino",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedMuscleGroupFilter == null,
                        onClick = { selectedMuscleGroupFilter = null },
                        label = { Text("Todos", fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurpleDeepCard,
                            selectedLabelColor = LilacAccent,
                            containerColor = PurpleDarkSurface,
                            labelColor = TextSecondary
                        )
                    )
                    MuscleGroup.values().forEach { mg ->
                        FilterChip(
                            selected = selectedMuscleGroupFilter == mg,
                            onClick = { selectedMuscleGroupFilter = mg },
                            label = { Text(mg.displayName, fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PurpleDeepCard,
                                selectedLabelColor = LilacAccent,
                                containerColor = PurpleDarkSurface,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                Text(
                    text = "Exercícios do treino · ${selectedExercises.size}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = LilacAccent
                )

                TextButton(
                    onClick = { searchQuery = "" },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("+ Adicionar exercício", color = LilacAccent, fontWeight = FontWeight.Bold) }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar exercício") },
                    placeholder = { Text("Ex.: supino, remada, agachamento") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                        focusedBorderColor = LilacAccent, unfocusedBorderColor = GlassBorder,
                        focusedLabelColor = LilacAccent, unfocusedLabelColor = TextSecondary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_exercise_search")
                )
                Text(
                    text = if (searchQuery.isBlank()) "Digite para encontrar um exercício com imagem" else "${availableExercises.size} resultado(s)",
                    style = MaterialTheme.typography.labelSmall, color = TextSecondary
                )

                if (selectedExercises.isNotEmpty()) {
                    Text(
                        text = "No seu treino (${selectedExercises.size}) · 3 séries × 10 repetições",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    selectedExercises.forEach { ex ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PurpleDeepCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent),
                            modifier = Modifier.fillMaxWidth().clickable { editingExercise = ex }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                exerciseLibraryImage(ex.name)?.let { resId ->
                                    Image(painter = painterResource(resId), contentDescription = ex.name, contentScale = ContentScale.Crop, modifier = Modifier.size(52.dp).clip(RoundedCornerShape(8.dp)))
                                    Spacer(Modifier.width(10.dp))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ex.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${ex.muscleGroup.displayName} • ${ex.equipment.displayName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }

                                IconButton(onClick = {
                                    selectedExerciseIds.value = selectedExerciseIds.value.toMutableSet().apply { remove(ex.id) }
                                }) { Icon(Icons.Default.Remove, "Remover ${ex.name}", tint = RedDestructive) }
                            }
                        }
                    }
                    availableExercises.take(20).forEach { ex ->
                        Surface(
                            shape = RoundedCornerShape(10.dp), color = PurpleDarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                exerciseLibraryImage(ex.name)?.let { resId ->
                                    Image(painter = painterResource(resId), contentDescription = ex.name, contentScale = ContentScale.Crop, modifier = Modifier.size(52.dp).clip(RoundedCornerShape(8.dp)))
                                    Spacer(Modifier.width(10.dp))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(ex.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("${ex.muscleGroup.displayName} · ${ex.equipment.displayName}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                                IconButton(onClick = {
                                    selectedExerciseIds.value = selectedExerciseIds.value.toMutableSet().apply { add(ex.id) }
                                }) { Icon(Icons.Default.Add, "Adicionar ${ex.name}", tint = LilacAccent) }
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
                        val chosenExercises = selectedExercises
                        val plans = chosenExercises.map { ex ->
                            val sets = (1..3).map {
                                ExerciseSetEntry(
                                    setNumber = it,
                                    weightKg = 20.0,
                                    reps = 10,
                                    isCompleted = false,
                                    restSeconds = 60
                                )
                            }
                            WorkoutExercisePlan(
                                exerciseId = ex.id,
                                exerciseName = ex.name,
                                muscleGroup = ex.muscleGroup.displayName,
                                sets = sets,
                                targetRestSeconds = 60,
                                notes = ex.executionTips
                            )
                        }
                        onSave(
                            title.trim(),
                            "${chosenExercises.size} exercícios · 3×10",
                            WorkoutCategory.PERSONALIZADO,
                            plans,
                            "",
                            45,
                            60
                        )
                    }
                },
                enabled = title.isNotBlank() && selectedExerciseIds.value.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_save_custom_workout")
            ) {
                Text("Salvar Treino", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextMuted)
            }
        },
        containerColor = PurpleDarkSurface,
        shape = RoundedCornerShape(24.dp)
    )

    editingExercise?.let { exercise ->
        AlertDialog(
            onDismissRequest = { editingExercise = null },
            title = { Text(exercise.name, color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${exercise.muscleGroup.displayName} · ${exercise.equipment.displayName}", color = LilacAccent)
                    Text("3 séries · 10 repetições", color = TextSecondary)
                    Text("Toque em remover para retirar este exercício do treino.", color = TextSecondary, fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedExerciseIds.value = selectedExerciseIds.value.toMutableSet().apply { remove(exercise.id) }
                    editingExercise = null
                }) { Text("Remover", color = RedDestructive) }
            },
            dismissButton = { TextButton(onClick = { editingExercise = null }) { Text("Fechar", color = LilacAccent) } },
            containerColor = PurpleDarkSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun exerciseLibraryImage(name: String): Int? {
    return exerciseAssetId(name, LocalContext.current)
}

private fun exerciseAssetId(name: String, context: android.content.Context): Int? {
    val slug = java.text.Normalizer.normalize(name.lowercase(), java.text.Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .replace("[^a-z0-9]+".toRegex(), "_")
        .trim('_')
    return context.resources.getIdentifier("exercise_$slug", "drawable", context.packageName).takeIf { it != 0 }
}
