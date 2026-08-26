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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BodyMeasurement
import com.example.data.model.FitnessGoal
import com.example.data.model.UserProfile
import com.example.ui.components.AIEvaluationDialog
import com.example.ui.components.DateUtils
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RedDestructive
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.RoyalBlueSubtle
import com.example.ui.viewmodel.FitnessViewModel

@Composable
fun EvolutionScreen(
    viewModel: FitnessViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val measurements by viewModel.allBodyMeasurements.collectAsStateWithLifecycle()
    val isAIEvaluating by viewModel.isAIEvaluating.collectAsStateWithLifecycle()
    val aiCoachAdvice by viewModel.aiCoachAdvice.collectAsStateWithLifecycle()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddMeasurementDialog by remember { mutableStateOf(false) }
    var showAiCoachDialog by remember { mutableStateOf(false) }

    val currentWeight = userProfile?.currentWeightKg ?: 78.0
    val startWeight = userProfile?.startingWeightKg ?: 75.0
    val targetWeight = userProfile?.targetWeightKg ?: 82.0
    val goal = userProfile?.goal ?: FitnessGoal.GANHO_PESO_HIPERTROFIA

    val weightDelta = currentWeight - startWeight

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
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
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Acompanhe seu peso, medidas corporais e metas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier.testTag("btn_edit_profile")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar perfil", tint = RoyalBlue)
                    }
                }
            }

            // Goal & Weight Overview Card
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(22.dp)
                        )
                        .testTag("card_evolution_goal")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "OBJETIVO DO ATLETA",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RoyalBlue,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = goal.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(RoyalBlueSubtle)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (goal == FitnessGoal.GANHO_PESO_HIPERTROFIA) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                        contentDescription = null,
                                        tint = RoyalBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (weightDelta >= 0) "+${String.format(java.util.Locale.US, "%.1f", weightDelta)} kg" else "${String.format(java.util.Locale.US, "%.1f", weightDelta)} kg",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalBlue
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
                                Text("Peso Inicial", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${startWeight}kg", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Peso Atual", style = MaterialTheme.typography.labelSmall, color = RoyalBlue)
                                Text("${currentWeight}kg", fontSize = 24.sp, fontWeight = FontWeight.Black, color = RoyalBlue)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Peso Alvo", style = MaterialTheme.typography.labelSmall, color = EmeraldSuccess)
                                Text("${targetWeight}kg", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
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
                            color = RoyalBlue,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${(fraction * 100).toInt()}% da meta alcançada",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }

            // AI Coach & Calorie Analysis Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                                        .background(RoyalBlueSubtle)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Coach & Nutrição com IA", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text("Feedback bioenergético para sua meta", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (aiCoachAdvice != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = aiCoachAdvice!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Button(
                            onClick = {
                                viewModel.requestAICoachAdvice()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_request_ai_coach")
                        ) {
                            if (isAIEvaluating) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Avaliando...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Consultar Coach IA Sobre Minha Meta", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
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
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { showAddMeasurementDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_add_measurement")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nova Medida", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            if (measurements.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Straighten, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Nenhuma medição registrada ainda", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Registre suas medidas (braço, tórax, cintura, coxa, etc.) para ver sua evolução física!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            title = { Text("Configurar Metas & Perfil", fontWeight = FontWeight.Bold) },
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
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Objetivo Principal:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        FitnessGoal.values().forEach { fg ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (selectedGoal == fg) RoyalBlueSubtle else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (selectedGoal == fg) androidx.compose.foundation.BorderStroke(1.5.dp, RoyalBlue) else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                                        Text(fg.label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                        Text(fg.description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    if (selectedGoal == fg) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = RoyalBlue)
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
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = targetW,
                            onValueChange = { targetW = it },
                            label = { Text("Peso Alvo (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = heightStr,
                            onValueChange = { heightStr = it },
                            label = { Text("Altura (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = ageStr,
                            onValueChange = { ageStr = it },
                            label = { Text("Idade") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = weeklyDays,
                        onValueChange = { weeklyDays = it },
                        label = { Text("Meta Semanal (dias de treino)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_save_profile")
                ) {
                    Text("Salvar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancelar")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
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
            title = { Text("Registrar Novas Medidas", fontWeight = FontWeight.Bold) },
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
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = chestStr,
                            onValueChange = { chestStr = it },
                            label = { Text("Tórax (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = waistStr,
                            onValueChange = { waistStr = it },
                            label = { Text("Cintura (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = armStr,
                            onValueChange = { armStr = it },
                            label = { Text("Braço (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = thighStr,
                            onValueChange = { thighStr = it },
                            label = { Text("Coxa (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = calfStr,
                            onValueChange = { calfStr = it },
                            label = { Text("Panturrilha (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = fatStr,
                            onValueChange = { fatStr = it },
                            label = { Text("% Gordura") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Observações (Ex: jejum, pós treino)") },
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
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_confirm_add_measurement")
                ) {
                    Text("Salvar Medidas", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMeasurementDialog = false }) {
                    Text("Cancelar")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun BodyMeasurementCard(
    measurement: BodyMeasurement,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                            .background(RoyalBlueSubtle)
                    ) {
                        Icon(Icons.Default.Straighten, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${measurement.weightKg} kg",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = DateUtils.formatEpochDayShort(measurement.dateEpochDay),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MeasurementBadge(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$label: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
