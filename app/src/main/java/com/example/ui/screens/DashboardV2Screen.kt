package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domain.dashboard.*
import java.util.Locale

@Composable
fun DashboardV2Screen(summary: DashboardSummary, health: HealthDashboardSnapshot, muscleVolumes: List<MuscleVolume>, evolution: EvolutionTrend, onStartTodayWorkout: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Hoje", style = MaterialTheme.typography.headlineMedium) }
        item {
            ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(summary.todayWorkout?.title ?: "Nenhum treino pendente hoje", style = MaterialTheme.typography.titleLarge)
                if (summary.todayWorkout != null) Button(onClick = onStartTodayWorkout) { Text("INICIAR TREINO") }
            } }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Metric4("Passos", health.stepsToday.toString()); Metric4("Peso", summary.currentWeightKg?.let { "%.1f kg".format(Locale.US, it) } ?: "--")
                Metric4("Readiness", "${summary.readinessScore}%"); Metric4("Streak", "${summary.streakDays} d")
            }
        }
        item { Text("Semana", style = MaterialTheme.typography.titleLarge) }
        item { Text("${summary.weeklyWorkouts}/${summary.weeklyGoal} treinos • ${"%.0f".format(Locale.US, summary.weeklyVolumeKg)} kg volume • ${"%.1f".format(Locale.US, summary.weeklyCardioKm)} km cardio") }
        item { LinearProgressIndicator(progress = { if (summary.weeklyGoal > 0) (summary.weeklyWorkouts.toFloat()/summary.weeklyGoal).coerceIn(0f,1f) else 0f }, modifier = Modifier.fillMaxWidth()) }
        item { Text("Volume muscular", style = MaterialTheme.typography.titleLarge) }
        items(muscleVolumes) { m -> ListItem(headlineContent = { Text(m.muscleGroup) }, supportingContent = { Text("${m.sets} séries • ${"%.0f".format(Locale.US, m.volumeKg)} kg") }, trailingContent = { Text(m.weeklyChangePercent?.let { "%+.0f%%".format(Locale.US, it) } ?: "novo") }) }
        item { Text("Evolução — últimos 28 dias", style = MaterialTheme.typography.titleLarge) }
        item { ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Peso: ${evolution.currentWeightKg?.let { "%.1f kg".format(Locale.US,it) } ?: "--"} ${evolution.weightChangeKg?.let { "(%+.1f kg)".format(Locale.US,it) } ?: ""}")
            Text("Frequência: ${evolution.workoutFrequency} treinos • Volume: ${"%.0f".format(Locale.US,evolution.workoutVolumeKg)} kg")
            Text("Cardio: ${"%.1f".format(Locale.US,evolution.cardioDistanceKm)} km • ${evolution.cardioMinutes} min")
            Text("Registros: ${evolution.measurementsCount} medidas • ${evolution.photosCount} fotos")
        } } }
        item { Text("Health Connect", style = MaterialTheme.typography.titleLarge) }
        item { Text("${health.stepsToday} passos • ${health.restingHeartRateBpm?.let { "$it bpm" } ?: "FC --"} • ${health.activeCaloriesToday} kcal • ${health.exerciseMinutesToday} min") }
    }
}

@Composable private fun Metric4(label: String, value: String) { Column { Text(value, style = MaterialTheme.typography.titleMedium); Text(label, style = MaterialTheme.typography.labelSmall) } }
