package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.data.model.Exercise
import com.example.data.model.ExercisePerformanceTarget
import com.example.data.model.UserProfile
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
import java.time.LocalDate

@Composable
fun GoalSettingSection(
    userProfile: UserProfile?,
    targets: List<ExercisePerformanceTarget>,
    completedWorkoutsThisWeek: Int,
    availableExercises: List<Exercise> = emptyList(),
    onUpdateWeeklyGoalDays: (Int) -> Unit,
    onSaveTarget: (ExercisePerformanceTarget) -> Unit,
    onToggleTargetAchieved: (ExercisePerformanceTarget) -> Unit,
    onDeleteTarget: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddEditDialog by remember { mutableStateOf(false) }
    var targetToEdit by remember { mutableStateOf<ExercisePerformanceTarget?>(null) }

    val weeklyGoal = userProfile?.weeklyGoalDays ?: 5
    val weeklyProgress = (completedWorkoutsThisWeek.toFloat() / weeklyGoal.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Weekly Training Frequency Goal Bento Card ---
        BentoCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_weekly_frequency_goal")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PurpleDarkSurface)
                            .border(1.dp, LilacAccent.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = LilacAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Frequência Semanal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Meta de dias de treino por semana",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (completedWorkoutsThisWeek >= weeklyGoal) EmeraldSuccess.copy(alpha = 0.15f) else PurpleDarkSurface)
                        .border(1.dp, if (completedWorkoutsThisWeek >= weeklyGoal) EmeraldSuccess else LilacAccent.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$completedWorkoutsThisWeek / $weeklyGoal dias",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = if (completedWorkoutsThisWeek >= weeklyGoal) EmeraldSuccess else LilacAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Frequency chip selector (1-7 days)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (1..7).forEach { days ->
                    val isSelected = weeklyGoal == days
                    FilterChip(
                        selected = isSelected,
                        onClick = { onUpdateWeeklyGoalDays(days) },
                        label = {
                            Text(
                                text = "${days}x",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurpleVibrant,
                            selectedLabelColor = Color.White,
                            containerColor = PurpleDarkSurface,
                            labelColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("chip_goal_${days}d")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { weeklyProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (completedWorkoutsThisWeek >= weeklyGoal) EmeraldSuccess else LilacAccent,
                trackColor = PurpleDarkest
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (completedWorkoutsThisWeek >= weeklyGoal) "🏆 Meta semanal batida!" else "Faltam ${weeklyGoal - completedWorkoutsThisWeek} treinos",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (completedWorkoutsThisWeek >= weeklyGoal) EmeraldSuccess else TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${(weeklyProgress * 100).toInt()}% concluído",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = LilacAccent
                )
            }
        }

        // --- 2. Exercise Performance Targets (Cargas e Desempenho) ---
        BentoCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_exercise_performance_targets")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PurpleDarkSurface)
                            .border(1.dp, AmberWarning.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrackChanges,
                            contentDescription = null,
                            tint = AmberWarning,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Metas de Força & Cargas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Recordes e progressão por exercício",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(PurpleDeepCard)
                        .border(1.dp, LilacAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .clickable {
                            targetToEdit = null
                            showAddEditDialog = true
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("btn_add_exercise_target"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = LilacAccent, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nova Meta", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LilacAccent)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (targets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma meta de carga cadastrada.\nToque em '+ Nova Meta' para registrar seus recordes almejados!",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    targets.forEach { target ->
                        ExerciseTargetItemCard(
                            target = target,
                            onToggleAchieved = { onToggleTargetAchieved(target) },
                            onEdit = {
                                targetToEdit = target
                                showAddEditDialog = true
                            },
                            onDelete = { onDeleteTarget(target.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddEditDialog) {
        AddEditExerciseTargetDialog(
            targetToEdit = targetToEdit,
            availableExercises = availableExercises,
            onDismiss = {
                showAddEditDialog = false
                targetToEdit = null
            },
            onSave = { newTarget ->
                onSaveTarget(newTarget)
                showAddEditDialog = false
                targetToEdit = null
            }
        )
    }
}

@Composable
fun ExerciseTargetItemCard(
    target: ExercisePerformanceTarget,
    onToggleAchieved: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = target.progressPercentage

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (target.isAchieved) PurpleDeepCard else PurpleDarkSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (target.isAchieved) EmeraldSuccess.copy(alpha = 0.6f) else GlassBorderSubtle
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onToggleAchieved,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (target.isAchieved) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Concluir meta",
                            tint = if (target.isAchieved) EmeraldSuccess else TextMuted
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = target.exerciseName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        if (target.notes.isNotBlank()) {
                            Text(
                                text = target.notes,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (target.isAchieved) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldSuccess.copy(alpha = 0.2f),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("BATIDA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                            }
                        }
                    }

                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp), tint = LilacAccent)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir", modifier = Modifier.size(16.dp), tint = RedDestructive)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Current vs Target Weight & Reps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text("Atual", fontSize = 10.sp, color = TextMuted)
                        Text(
                            text = "${target.currentWeightKg.toInt()} kg × ${target.currentReps} reps",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }
                    Column {
                        Text("Meta Alvo", fontSize = 10.sp, color = if (target.isAchieved) EmeraldSuccess else LilacAccent)
                        Text(
                            text = "${target.targetWeightKg.toInt()} kg × ${target.targetReps} reps",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Black,
                            color = if (target.isAchieved) EmeraldSuccess else LilacAccent
                        )
                    }
                }

                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = if (target.isAchieved) EmeraldSuccess else LilacAccent
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (target.isAchieved) EmeraldSuccess else LilacAccent,
                trackColor = PurpleDarkest
            )
        }
    }
}

@Composable
fun AddEditExerciseTargetDialog(
    targetToEdit: ExercisePerformanceTarget?,
    availableExercises: List<Exercise>,
    onDismiss: () -> Unit,
    onSave: (ExercisePerformanceTarget) -> Unit
) {
    var exerciseName by remember { mutableStateOf(targetToEdit?.exerciseName ?: "") }
    var targetWeightText by remember { mutableStateOf(targetToEdit?.targetWeightKg?.let { if (it > 0) it.toInt().toString() else "" } ?: "100") }
    var targetRepsText by remember { mutableStateOf(targetToEdit?.targetReps?.toString() ?: "8") }
    var currentWeightText by remember { mutableStateOf(targetToEdit?.currentWeightKg?.let { if (it > 0) it.toInt().toString() else "" } ?: "70") }
    var currentRepsText by remember { mutableStateOf(targetToEdit?.currentReps?.toString() ?: "8") }
    var notes by remember { mutableStateOf(targetToEdit?.notes ?: "") }

    val quickExercises = listOf(
        "Supino Reto com Barra",
        "Agachamento Livre",
        "Levantamento Terra",
        "Desenvolvimento Halteres",
        "Barra Fixa (Pull-Up)",
        "Remada Curvada"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (targetToEdit == null) "🎯 Nova Meta de Força" else "✏️ Editar Meta",
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = { Text("Nome do Exercício") },
                    placeholder = { Text("Ex: Supino Reto com Barra") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = LilacAccent,
                        unfocusedBorderColor = GlassBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick exercise suggestions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    quickExercises.forEach { name ->
                        val isSelected = exerciseName.equals(name, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { exerciseName = name },
                            label = { Text(name, fontSize = 10.sp) },
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PurpleDeepCard,
                                selectedLabelColor = LilacAccent,
                                containerColor = PurpleDarkSurface,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = targetWeightText,
                        onValueChange = { targetWeightText = it },
                        label = { Text("Carga Alvo (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorder
                        )
                    )
                    OutlinedTextField(
                        value = targetRepsText,
                        onValueChange = { targetRepsText = it },
                        label = { Text("Reps Alvo") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorder
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = currentWeightText,
                        onValueChange = { currentWeightText = it },
                        label = { Text("Carga Atual (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorder
                        )
                    )
                    OutlinedTextField(
                        value = currentRepsText,
                        onValueChange = { currentRepsText = it },
                        label = { Text("Reps Atuais") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorder
                        )
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Estratégia / Observações") },
                    placeholder = { Text("Ex: Sobrecarga progressiva de 2kg por semana") },
                    maxLines = 2,
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
                    if (exerciseName.isNotBlank()) {
                        val target = ExercisePerformanceTarget(
                            id = targetToEdit?.id ?: 0L,
                            exerciseName = exerciseName.trim(),
                            targetWeightKg = targetWeightText.toDoubleOrNull() ?: 100.0,
                            targetReps = targetRepsText.toIntOrNull() ?: 8,
                            currentWeightKg = currentWeightText.toDoubleOrNull() ?: 0.0,
                            currentReps = currentRepsText.toIntOrNull() ?: 0,
                            targetDateEpochDay = targetToEdit?.targetDateEpochDay ?: (LocalDate.now().toEpochDay() + 60),
                            isAchieved = targetToEdit?.isAchieved ?: false,
                            achievedDateEpochDay = targetToEdit?.achievedDateEpochDay,
                            notes = notes.trim(),
                            createdAtEpochDay = targetToEdit?.createdAtEpochDay ?: LocalDate.now().toEpochDay()
                        )
                        onSave(target)
                    }
                },
                enabled = exerciseName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Text("Salvar Meta", color = Color.White, fontWeight = FontWeight.Bold)
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
}
