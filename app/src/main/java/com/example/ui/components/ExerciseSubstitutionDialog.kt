package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Equipment
import com.example.data.model.Exercise
import com.example.data.model.MuscleGroup
import com.example.data.model.WorkoutExercisePlan
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ExerciseSubstitutionDialog(
    currentPlan: WorkoutExercisePlan,
    availableExercises: List<Exercise>,
    onDismiss: () -> Unit,
    onSelectExercise: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedEquipmentFilter by remember { mutableStateOf<Equipment?>(null) }

    // Identify target muscle group from current plan or available exercises
    val currentExercise = availableExercises.find { it.name.equals(currentPlan.exerciseName, ignoreCase = true) }
    val targetMuscleGroup = currentExercise?.muscleGroup

    // Filter exercises primarily by same muscle group
    val sameGroupExercises = remember(availableExercises, targetMuscleGroup, currentPlan.muscleGroup) {
        availableExercises.filter { ex ->
            if (targetMuscleGroup != null) {
                ex.muscleGroup == targetMuscleGroup
            } else {
                ex.muscleGroup.displayName.contains(currentPlan.muscleGroup, ignoreCase = true) ||
                        currentPlan.muscleGroup.contains(ex.muscleGroup.displayName, ignoreCase = true)
            }
        }.filter { it.name != currentPlan.exerciseName } // Exclude the one already selected
    }

    val displayList = remember(sameGroupExercises, searchQuery, selectedEquipmentFilter) {
        sameGroupExercises.filter { ex ->
            val matchesQuery = searchQuery.isBlank() ||
                    ex.name.contains(searchQuery, ignoreCase = true) ||
                    ex.equipment.displayName.contains(searchQuery, ignoreCase = true)
            val matchesEquip = selectedEquipmentFilter == null || ex.equipment == selectedEquipmentFilter
            matchesQuery && matchesEquip
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary.copy(alpha = 0.2f))
                            .border(1.dp, LilacAccent.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = LilacAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Substituir Exercício",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Mesmo grupo: ${targetMuscleGroup?.displayName ?: currentPlan.muscleGroup}",
                            style = MaterialTheme.typography.labelSmall,
                            color = LilacAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
            ) {
                // Currently replacing banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PurpleDeepCard,
                    border = BorderStroke(1.dp, GlassBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Substituindo:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentPlan.exerciseName,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar alternativa...", color = TextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = LilacSoft, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_search_substitute_exercise"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LilacAccent,
                        unfocusedBorderColor = GlassBorderSubtle,
                        focusedContainerColor = PurpleDarkSurface,
                        unfocusedContainerColor = PurpleDarkSurface,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Equipment Filter chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedEquipmentFilter == null,
                            onClick = { selectedEquipmentFilter = null },
                            label = { Text("Todos", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PurplePrimary,
                                selectedLabelColor = Color.White,
                                containerColor = PurpleDarkSurface,
                                labelColor = TextSecondary
                            )
                        )
                    }
                    items(listOf(Equipment.HALTERES, Equipment.BARRA, Equipment.MAQUINA, Equipment.POLIA, Equipment.PESO_CORPO)) { equip ->
                        val isSelected = selectedEquipmentFilter == equip
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedEquipmentFilter = if (isSelected) null else equip },
                            label = { Text(equip.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PurplePrimary,
                                selectedLabelColor = Color.White,
                                containerColor = PurpleDarkSurface,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // List of alternative exercises
                if (displayList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Nenhum exercício encontrado com esses filtros",
                                color = TextMuted,
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tente limpar a busca ou selecionar outro equipamento.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(displayList, key = { it.id }) { ex ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PurpleDeepCard,
                                border = BorderStroke(1.dp, GlassBorderSubtle),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectExercise(ex) }
                                    .testTag("item_substitute_exercise_${ex.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(PurpleDarkSurface),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.FitnessCenter,
                                                contentDescription = null,
                                                tint = LilacAccent,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = ex.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = ex.equipment.displayName,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = LilacSoft
                                                )
                                                Text(text = " • ", color = TextMuted)
                                                Text(
                                                    text = "${ex.defaultSets} séries x ${ex.defaultReps} reps",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = TextMuted
                                                )
                                            }
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EmeraldSuccess.copy(alpha = 0.15f),
                                        border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = "Escolher",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldSuccess,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextMuted)
            }
        },
        containerColor = PurpleDarkSurface,
        shape = RoundedCornerShape(22.dp)
    )
}
