package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BodyMeasurement
import com.example.data.model.FitnessGoal
import com.example.data.model.ProgressPhoto
import com.example.ui.components.AIInsightCard
import com.example.ui.components.AddProgressPhotoDialog
import com.example.ui.components.BentoCard
import com.example.ui.components.DateUtils
import com.example.ui.components.GamificationTrophySection
import com.example.ui.components.GoalSettingSection
import com.example.ui.components.HealthConnectDialog
import com.example.ui.components.MedalDetailModalDialog
import com.example.ui.components.MedalUnlockedDialog
import com.example.ui.components.PhotoDetailDialog
import com.example.ui.components.PrimaryButton
import com.example.ui.components.ProgressPhotosSection
import com.example.ui.theme.EmeraldSubtle
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

@Composable
fun EvolutionScreen(
    viewModel: FitnessViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val measurements by viewModel.allBodyMeasurements.collectAsStateWithLifecycle()
    val progressPhotos by viewModel.allProgressPhotos.collectAsStateWithLifecycle()
    val initialProgressPhoto by viewModel.initialProgressPhoto.collectAsStateWithLifecycle()
    val isAIEvaluating by viewModel.isAIEvaluating.collectAsStateWithLifecycle()
    val aiCoachAdvice by viewModel.aiCoachAdvice.collectAsStateWithLifecycle()
    val exerciseTargets by viewModel.allExerciseTargets.collectAsStateWithLifecycle()
    val allWorkoutSessions by viewModel.allWorkoutSessions.collectAsStateWithLifecycle()
    val allExercises by viewModel.allExercises.collectAsStateWithLifecycle()
    val allUserMedals by viewModel.allMedals.collectAsStateWithLifecycle()
    val gamificationOverview by viewModel.gamificationOverview.collectAsStateWithLifecycle()
    val selectedMedalForDetail by viewModel.selectedMedalForDetail.collectAsStateWithLifecycle()
    val activeMedalUnlocked by viewModel.activeMedalUnlocked.collectAsStateWithLifecycle()

    val todayEpoch = DateUtils.todayEpochDay()
    val startOfWeekEpoch = todayEpoch - java.time.LocalDate.now().dayOfWeek.value + 1
    val completedThisWeek = allWorkoutSessions.count {
        it.dateEpochDay >= startOfWeekEpoch && it.status == com.example.data.model.SessionStatus.COMPLETED
    }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddMeasurementDialog by remember { mutableStateOf(false) }
    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var isAddingInitialPhoto by remember { mutableStateOf(false) }
    var selectedPhotoDetail by remember { mutableStateOf<ProgressPhoto?>(null) }
    var showHealthConnectDialog by remember { mutableStateOf(false) }

    val healthPermissionsGranted by viewModel.healthPermissionsGranted.collectAsStateWithLifecycle()

    val currentWeight = userProfile?.currentWeightKg ?: 78.0
    val startWeight = userProfile?.startingWeightKg ?: 75.0
    val targetWeight = userProfile?.targetWeightKg ?: 82.0
    val goal = userProfile?.goal ?: FitnessGoal.GANHO_PESO_HIPERTROFIA

    val weightDelta = currentWeight - startWeight

    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PurpleDarkest)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = topInset + 16.dp, bottom = bottomInset + 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Medidas & Evolução",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Acompanhamento biométrico, fotos e metas de performance",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (healthPermissionsGranted) EmeraldSubtle else PurpleDarkSurface
                                )
                                .border(
                                    1.dp,
                                    if (healthPermissionsGranted) EmeraldSuccess else GlassBorder,
                                    CircleShape
                                )
                                .clickable { showHealthConnectDialog = true }
                                .testTag("btn_evolution_health_connect"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Watch,
                                contentDescription = "Health Connect & Smartwatch",
                                tint = if (healthPermissionsGranted) EmeraldSuccess else LilacAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(PurpleDarkSurface)
                                .border(1.dp, GlassBorder, CircleShape)
                                .clickable { showEditProfileDialog = true }
                                .testTag("btn_edit_profile"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar perfil", tint = LilacAccent, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Goal & Weight Overview Bento Card
            item {
                BentoCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_evolution_goal")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "OBJETIVO DO ATLETA",
                                fontSize = 10.sp,
                                color = LilacAccent,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = goal.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(PurpleDarkSurface)
                                .border(1.dp, LilacAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (goal == FitnessGoal.GANHO_PESO_HIPERTROFIA) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = LilacAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (weightDelta >= 0) "+${String.format(java.util.Locale.US, "%.1f", weightDelta)} kg" else "${String.format(java.util.Locale.US, "%.1f", weightDelta)} kg",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = LilacAccent
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Weights row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Peso Inicial", fontSize = 11.sp, color = TextMuted)
                            Text("${startWeight}kg", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Peso Atual", fontSize = 11.sp, color = LilacAccent, fontWeight = FontWeight.Bold)
                            Text("${currentWeight}kg", fontSize = 24.sp, fontWeight = FontWeight.Black, color = LilacAccent)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Peso Alvo", fontSize = 11.sp, color = EmeraldSuccess)
                            Text("${targetWeight}kg", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress bar
                    val totalDistance = kotlin.math.abs(targetWeight - startWeight).coerceAtLeast(0.1)
                    val achieved = when (goal) {
                        FitnessGoal.GANHO_PESO_HIPERTROFIA -> (currentWeight - startWeight).coerceAtLeast(0.0)
                        FitnessGoal.PERDA_PESO_EMAGRECIMENTO -> (startWeight - currentWeight).coerceAtLeast(0.0)
                        else -> kotlin.math.abs(currentWeight - startWeight)
                    }
                    val fraction = (achieved / totalDistance).toFloat().coerceIn(0f, 1f)

                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = LilacAccent,
                        trackColor = PurpleDarkest
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${(fraction * 100).toInt()}% da meta alcançada",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            // Goal Setting & Specific Exercise Targets
            item {
                GoalSettingSection(
                    userProfile = userProfile,
                    targets = exerciseTargets,
                    completedWorkoutsThisWeek = completedThisWeek,
                    availableExercises = allExercises,
                    onUpdateWeeklyGoalDays = { days ->
                        viewModel.updateWeeklyGoalDays(days)
                    },
                    onSaveTarget = { target ->
                        viewModel.saveExerciseTarget(target)
                    },
                    onToggleTargetAchieved = { target ->
                        viewModel.toggleExerciseTargetAchieved(target)
                    },
                    onDeleteTarget = { id ->
                        viewModel.deleteExerciseTarget(id)
                    }
                )
            }

            // Gamification Medals & Trophies Section
            item {
                GamificationTrophySection(
                    userMedals = allUserMedals,
                    overview = gamificationOverview,
                    onMedalClick = { medal ->
                        viewModel.selectMedalForDetail(medal)
                    },
                    onCelebrateMedal = { medal ->
                        viewModel.triggerCelebrationForMedal(medal)
                    }
                )
            }

            // Progress & Body Photos Evolution Section
            item {
                ProgressPhotosSection(
                    photos = progressPhotos,
                    initialPhoto = initialProgressPhoto,
                    onAddPhotoClick = { isInitial ->
                        isAddingInitialPhoto = isInitial
                        showAddPhotoDialog = true
                    },
                    onPhotoClick = { photo ->
                        selectedPhotoDetail = photo
                    }
                )
            }

            // AI Coach & Calorie Analysis
            item {
                AIInsightCard(
                    title = "COACH & NUTRIÇÃO COM IA",
                    message = aiCoachAdvice ?: "Clique no botão abaixo para receber uma análise profunda da sua sobrecarga progressiva, balanço calórico e estratégias para sua meta atual."
                )
                Spacer(modifier = Modifier.height(10.dp))
                PrimaryButton(
                    text = if (isAIEvaluating) "Avaliando..." else "Consultar Coach IA Sobre Minha Meta",
                    icon = Icons.Default.AutoAwesome,
                    onClick = { viewModel.requestAICoachAdvice() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_request_ai_coach")
                )
            }

            // Body Measurements Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Histórico de Medidas Corporais",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(PurpleDeepCard)
                            .border(1.dp, LilacAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .clickable { showAddMeasurementDialog = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("btn_add_measurement"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = LilacAccent, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nova Medida", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LilacAccent)
                        }
                    }
                }
            }

            if (measurements.isEmpty()) {
                item {
                    BentoCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Straighten, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Nenhuma medição registrada ainda", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(
                                text = "Registre suas medidas (braço, tórax, cintura, coxa) para acompanhar seu ganho de massa magra.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(measurements, key = { it.id }) { item ->
                    BodyMeasurementCard(
                        measurement = item,
                        onDelete = { viewModel.deleteBodyMeasurement(item.id) }
                    )
                }
            }
        }
    }

    // Edit Profile & Goals Dialog
    if (showEditProfileDialog && userProfile != null) {
        var name by remember { mutableStateOf(userProfile!!.name) }
        var currentW by remember { mutableStateOf(userProfile!!.currentWeightKg.toString()) }
        var targetW by remember { mutableStateOf(userProfile!!.targetWeightKg.toString()) }
        var heightStr by remember { mutableStateOf(userProfile!!.heightCm.toString()) }
        var ageStr by remember { mutableStateOf(userProfile!!.age.toString()) }
        var selectedGoal by remember { mutableStateOf(userProfile!!.goal) }
        var weeklyDays by remember { mutableStateOf(userProfile!!.weeklyGoalDays.toString()) }
        var defaultGym by remember { mutableStateOf(userProfile!!.defaultGymLocation) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Configurar Metas & Perfil", fontWeight = FontWeight.Black, color = TextPrimary) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome do Atleta") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Objetivo Principal:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        FitnessGoal.values().forEach { fg ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (selectedGoal == fg) PurpleDeepCard else PurpleDarkSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedGoal == fg) LilacAccent else GlassBorderSubtle),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedGoal = fg }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(fg.label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(fg.description, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                    }
                                    if (selectedGoal == fg) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = LilacAccent)
                                    }
                                }
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = currentW,
                            onValueChange = { currentW = it },
                            label = { Text("Peso Atual (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = LilacAccent,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = targetW,
                            onValueChange = { targetW = it },
                            label = { Text("Peso Alvo (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = LilacAccent,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = heightStr,
                            onValueChange = { heightStr = it },
                            label = { Text("Altura (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = LilacAccent,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = ageStr,
                            onValueChange = { ageStr = it },
                            label = { Text("Idade") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = LilacAccent,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = weeklyDays,
                        onValueChange = { weeklyDays = it },
                        label = { Text("Meta Semanal (dias de treino)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = userProfile!!.copy(
                            name = name.ifBlank { "Atleta" },
                            currentWeightKg = currentW.toDoubleOrNull() ?: userProfile!!.currentWeightKg,
                            targetWeightKg = targetW.toDoubleOrNull() ?: userProfile!!.targetWeightKg,
                            heightCm = heightStr.toDoubleOrNull() ?: userProfile!!.heightCm,
                            age = ageStr.toIntOrNull() ?: userProfile!!.age,
                            goal = selectedGoal,
                            weeklyGoalDays = weeklyDays.toIntOrNull() ?: userProfile!!.weeklyGoalDays,
                            defaultGymLocation = defaultGym
                        )
                        viewModel.updateUserProfile(updated)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_save_profile")
                ) {
                    Text("Salvar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = PurpleDarkSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Add Measurement Dialog
    if (showAddMeasurementDialog) {
        var weightStr by remember { mutableStateOf(currentWeight.toString()) }
        var chestStr by remember { mutableStateOf("") }
        var waistStr by remember { mutableStateOf("") }
        var armStr by remember { mutableStateOf("") }
        var thighStr by remember { mutableStateOf("") }
        var calfStr by remember { mutableStateOf("") }
        var fatStr by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddMeasurementDialog = false },
            title = { Text("Registrar Novas Medidas", fontWeight = FontWeight.Black, color = TextPrimary) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = weightStr,
                        onValueChange = { weightStr = it },
                        label = { Text("Peso Corporal (kg) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = chestStr,
                            onValueChange = { chestStr = it },
                            label = { Text("Tórax (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = LilacAccent,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = waistStr,
                            onValueChange = { waistStr = it },
                            label = { Text("Cintura (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = LilacAccent,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = armStr,
                            onValueChange = { armStr = it },
                            label = { Text("Braço (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = LilacAccent,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = thighStr,
                            onValueChange = { thighStr = it },
                            label = { Text("Coxa (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = LilacAccent,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = calfStr,
                            onValueChange = { calfStr = it },
                            label = { Text("Panturrilha (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = LilacAccent,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = fatStr,
                            onValueChange = { fatStr = it },
                            label = { Text("% Gordura") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = LilacAccent,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Observações (Ex: jejum, pós treino)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val m = BodyMeasurement(
                            dateEpochDay = DateUtils.todayEpochDay(),
                            weightKg = weightStr.toDoubleOrNull() ?: currentWeight,
                            chestCm = chestStr.toDoubleOrNull(),
                            waistCm = waistStr.toDoubleOrNull(),
                            armCm = armStr.toDoubleOrNull(),
                            thighCm = thighStr.toDoubleOrNull(),
                            calfCm = calfStr.toDoubleOrNull(),
                            bodyFatPercentage = fatStr.toDoubleOrNull(),
                            notes = notes
                        )
                        viewModel.addBodyMeasurement(m)
                        showAddMeasurementDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_confirm_add_measurement")
                ) {
                    Text("Salvar Medidas", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMeasurementDialog = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = PurpleDarkSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Add Progress Photo Dialog
    if (showAddPhotoDialog) {
        AddProgressPhotoDialog(
            initialWeight = if (isAddingInitialPhoto) startWeight else currentWeight,
            isSettingInitial = isAddingInitialPhoto,
            onDismiss = { showAddPhotoDialog = false },
            onSave = { uri, weight, monthLabel, isInitial, fat, notes ->
                viewModel.saveProgressPhoto(
                    sourceUri = uri,
                    weightKg = weight,
                    monthLabel = monthLabel,
                    isInitial = isInitial,
                    bodyFatPercentage = fat,
                    notes = notes,
                    onComplete = {
                        showAddPhotoDialog = false
                    }
                )
            }
        )
    }

    // Photo Detail Dialog
    selectedPhotoDetail?.let { photo ->
        PhotoDetailDialog(
            photo = photo,
            initialPhoto = initialProgressPhoto,
            onDismiss = { selectedPhotoDetail = null },
            onDelete = {
                viewModel.deleteProgressPhoto(photo)
                selectedPhotoDetail = null
            }
        )
    }

    // Medal Detail Modal Dialog
    selectedMedalForDetail?.let { medal ->
        MedalDetailModalDialog(
            medal = medal,
            onDismiss = { viewModel.dismissMedalDetail() }
        )
    }

    // Medal Celebration Modal Dialog
    activeMedalUnlocked?.let { medal ->
        MedalUnlockedDialog(
            medal = medal,
            onDismiss = { viewModel.dismissMedalUnlockedDialog() }
        )
    }

    // Health Connect & Smartwatch Dialog
    if (showHealthConnectDialog) {
        HealthConnectDialog(
            viewModel = viewModel,
            onDismiss = { showHealthConnectDialog = false }
        )
    }
}

@Composable
fun BodyMeasurementCard(
    measurement: BodyMeasurement,
    onDelete: () -> Unit
) {
    BentoCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PurpleDarkSurface)
                        .border(1.dp, LilacAccent.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(Icons.Default.Straighten, contentDescription = null, tint = LilacAccent, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "${measurement.weightKg} kg",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = DateUtils.formatEpochDayShort(measurement.dateEpochDay),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = RedDestructive, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Measurement badges
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            measurement.chestCm?.let {
                MeasurementBadge(label = "Tórax", value = "${it}cm")
            }
            measurement.waistCm?.let {
                MeasurementBadge(label = "Cintura", value = "${it}cm")
            }
            measurement.armCm?.let {
                MeasurementBadge(label = "Braço", value = "${it}cm")
            }
            measurement.thighCm?.let {
                MeasurementBadge(label = "Coxa", value = "${it}cm")
            }
            measurement.calfCm?.let {
                MeasurementBadge(label = "Panturrilha", value = "${it}cm")
            }
            measurement.bodyFatPercentage?.let {
                MeasurementBadge(label = "Gordura", value = "${it}%")
            }
        }

        if (measurement.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "📝 ${measurement.notes}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun MeasurementBadge(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = PurpleDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$label: ", fontSize = 11.sp, color = TextMuted)
            Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LilacAccent)
        }
    }
}
