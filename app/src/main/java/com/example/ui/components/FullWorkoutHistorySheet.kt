package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.model.CardioSession
import com.example.data.model.SessionStatus
import com.example.data.model.WorkoutExercisePlan
import com.example.data.model.WorkoutSession
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.RedDestructive
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.Locale

private enum class HistoryFilterType(val label: String) {
    ALL("Todos"),
    STRENGTH("Musculação"),
    CARDIO("Cardio"),
    AI_EVALUATED("Com IA")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullWorkoutHistorySheet(
    workoutSessions: List<WorkoutSession>,
    cardioSessions: List<CardioSession>,
    onDismiss: () -> Unit,
    onDeleteWorkoutSession: (Long) -> Unit,
    onDeleteCardioSession: (Long) -> Unit,
    onViewAiFeedback: (String) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var selectedFilter by remember { mutableStateOf(HistoryFilterType.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var sessionToDeleteId by remember { mutableStateOf<Long?>(null) }
    var cardioToDeleteId by remember { mutableStateOf<Long?>(null) }

    val completedStrength = remember(workoutSessions) {
        workoutSessions.filter { it.status == SessionStatus.COMPLETED }
    }

    val totalStrengthCount = completedStrength.size
    val totalCardioCount = cardioSessions.size
    val totalVolumeTons = remember(completedStrength) {
        completedStrength.sumOf { it.totalWeightLiftedKg } / 1000.0
    }
    val totalCalories = remember(completedStrength, cardioSessions) {
        completedStrength.sumOf { it.estimatedCalories } + cardioSessions.sumOf { it.caloriesBurned }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PurpleDarkest,
        dragHandle = null,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary.copy(alpha = 0.2f))
                            .border(1.dp, LilacAccent.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = LilacAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Histórico de Treinos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "$totalStrengthCount musculação • $totalCardioCount cardio concluídos",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_close_history_sheet")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Summary Metric Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricSummaryBadge(
                    label = "Sessões",
                    value = "${totalStrengthCount + totalCardioCount}",
                    color = LilacAccent,
                    modifier = Modifier.weight(1f)
                )
                MetricSummaryBadge(
                    label = "Volume Total",
                    value = String.format(Locale.getDefault(), "%.1ft", totalVolumeTons),
                    color = EmeraldSuccess,
                    modifier = Modifier.weight(1f)
                )
                MetricSummaryBadge(
                    label = "Calorias",
                    value = "$totalCalories kcal",
                    color = LilacSoft,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar por nome do treino ou exercício...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = LilacSoft) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_history_search"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LilacAccent,
                    unfocusedBorderColor = GlassBorderSubtle,
                    focusedContainerColor = PurpleDeepCard,
                    unfocusedContainerColor = PurpleDarkSurface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(HistoryFilterType.values()) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter.label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurplePrimary,
                            selectedLabelColor = Color.White,
                            containerColor = PurpleDarkSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) LilacAccent else GlassBorderSubtle,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter items
            val filteredWorkouts = remember(completedStrength, selectedFilter, searchQuery) {
                if (selectedFilter == HistoryFilterType.CARDIO) emptyList()
                else completedStrength.filter { session ->
                    val matchesQuery = searchQuery.isBlank() ||
                            session.title.contains(searchQuery, ignoreCase = true) ||
                            session.exercisesDoneJson.contains(searchQuery, ignoreCase = true)
                    val matchesAi = selectedFilter != HistoryFilterType.AI_EVALUATED || session.aiCaloricEvaluation.isNotBlank()
                    matchesQuery && matchesAi
                }
            }

            val filteredCardios = remember(cardioSessions, selectedFilter, searchQuery) {
                if (selectedFilter == HistoryFilterType.STRENGTH || selectedFilter == HistoryFilterType.AI_EVALUATED) emptyList()
                else cardioSessions.filter { cardio ->
                    searchQuery.isBlank() ||
                            cardio.type.title.contains(searchQuery, ignoreCase = true) ||
                            cardio.location.contains(searchQuery, ignoreCase = true)
                }
            }

            if (filteredWorkouts.isEmpty() && filteredCardios.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "Nenhum resultado para \"$searchQuery\""
                            else "Nenhum treino realizado encontrado",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ao concluir seus treinos de musculação e cardio, eles ficarão registrados aqui com todo o histórico.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredWorkouts, key = { "workout_${it.id}" }) { session ->
                        HistoryWorkoutCard(
                            session = session,
                            onDelete = { sessionToDeleteId = session.id },
                            onViewAi = { onViewAiFeedback(session.aiCaloricEvaluation) }
                        )
                    }

                    items(filteredCardios, key = { "cardio_${it.id}" }) { cardio ->
                        HistoryCardioCard(
                            cardio = cardio,
                            onDelete = { cardioToDeleteId = cardio.id }
                        )
                    }
                }
            }
        }
    }

    // Delete Workout Confirmation
    if (sessionToDeleteId != null) {
        AlertDialog(
            onDismissRequest = { sessionToDeleteId = null },
            title = { Text("Excluir Treino?", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Este treino será removido do seu histórico e as métricas serão recalculadas.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        sessionToDeleteId?.let { onDeleteWorkoutSession(it) }
                        sessionToDeleteId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDestructive)
                ) {
                    Text("Excluir", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { sessionToDeleteId = null }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = PurpleDarkSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Delete Cardio Confirmation
    if (cardioToDeleteId != null) {
        AlertDialog(
            onDismissRequest = { cardioToDeleteId = null },
            title = { Text("Excluir Cardio?", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Esta sessão de cardio será removida do seu histórico.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        cardioToDeleteId?.let { onDeleteCardioSession(it) }
                        cardioToDeleteId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDestructive)
                ) {
                    Text("Excluir", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { cardioToDeleteId = null }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = PurpleDarkSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun MetricSummaryBadge(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = PurpleDeepCard,
        border = BorderStroke(1.dp, GlassBorderSubtle),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontWeight = FontWeight.Black, fontSize = 14.sp, color = color)
            Text(text = label, fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun HistoryWorkoutCard(
    session: WorkoutSession,
    onDelete: () -> Unit,
    onViewAi: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val exercisePlans = remember(session.exercisesDoneJson) {
        try {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val listType = Types.newParameterizedType(List::class.java, WorkoutExercisePlan::class.java)
            val adapter = moshi.adapter<List<WorkoutExercisePlan>>(listType)
            adapter.fromJson(session.exercisesDoneJson) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PurpleDeepCard,
        border = BorderStroke(1.dp, GlassBorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_history_session_${session.id}")
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
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(EmeraldSubtle)
                            .border(1.dp, EmeraldSuccess.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = session.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = DateUtils.formatEpochDay(session.dateEpochDay),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = LilacAccent
                            )
                            Text(text = " • ", color = TextMuted)
                            Text(
                                text = session.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Excluir treino",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(PurpleDarkSurface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⏱️ ${session.durationSeconds / 60} min",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "🏋️ ${session.totalWeightLiftedKg.toInt()} kg volume",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "🔥 ${session.estimatedCalories} kcal",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldSuccess
                )
            }

            // AI feedback button if present
            if (session.aiCaloricEvaluation.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    onClick = onViewAi,
                    shape = RoundedCornerShape(10.dp),
                    color = PurpleDarkSurface,
                    border = BorderStroke(1.dp, LilacAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = LilacAccent,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ver Análise de Calorias & Biomecânica IA",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = LilacAccent
                        )
                    }
                }
            }

            // Expand exercises button
            if (exercisePlans.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${exercisePlans.size} exercícios executados",
                        style = MaterialTheme.typography.labelMedium,
                        color = LilacSoft,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = LilacSoft,
                        modifier = Modifier.size(18.dp)
                    )
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        exercisePlans.forEach { plan ->
                            val completedSets = plan.sets.filter { it.isCompleted }
                            val totalReps = completedSets.sumOf { it.reps }
                            val maxWeight = completedSets.maxOfOrNull { it.weightKg } ?: 0.0

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PurpleDarkSurface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = plan.exerciseName,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = plan.muscleGroup,
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                    Text(
                                        text = "${completedSets.size} séries • Top ${maxWeight.toInt()}kg",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = LilacAccent
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
private fun HistoryCardioCard(
    cardio: CardioSession,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PurpleDeepCard,
        border = BorderStroke(1.dp, GlassBorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_history_cardio_${cardio.id}")
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
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(EmeraldSubtle)
                            .border(1.dp, EmeraldSuccess.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.DirectionsBike,
                            contentDescription = null,
                            tint = LilacAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = cardio.type.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = DateUtils.formatEpochDay(cardio.dateEpochDay),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = LilacAccent
                            )
                            Text(text = " • ", color = TextMuted)
                            Text(
                                text = cardio.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Excluir cardio",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(PurpleDarkSurface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⏱️ ${cardio.durationMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                if (cardio.distanceKm != null && cardio.distanceKm > 0) {
                    Text(
                        text = "📍 ${String.format(Locale.getDefault(), "%.2f km", cardio.distanceKm)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "🔥 ${cardio.caloriesBurned} kcal",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldSuccess
                )
            }
        }
    }
}
