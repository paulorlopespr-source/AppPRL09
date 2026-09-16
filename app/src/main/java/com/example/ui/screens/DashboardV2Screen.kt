package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domain.dashboard.*
import com.example.domain.readiness.ReadinessResult
import java.util.Locale

@Composable
fun DashboardV2Screen(summary:DashboardSummary,health:HealthDashboardSnapshot,muscleVolumes:List<MuscleVolume>,evolution:EvolutionTrend,readiness:ReadinessResult?,onStartTodayWorkout:()->Unit,onOpenCheckIn:()->Unit){
 LazyColumn(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  item{Text("Hoje",style=MaterialTheme.typography.headlineMedium)}
  item{ElevatedCard(Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp)){Text(summary.todayWorkout?.title?:"Nenhum treino pendente hoje",style=MaterialTheme.typography.titleLarge);if(summary.todayWorkout!=null)Button(onClick=onStartTodayWorkout){Text("INICIAR TREINO")}}}}
  item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Metric("Passos",health.stepsToday.toString());Metric("Peso",summary.currentWeightKg?.let{"%.1f kg".format(Locale.US,it)}?:"--");Metric("Readiness",readiness?.let{"${it.score}%"}?:"--");Metric("Streak","${summary.streakDays} d")}}
  item{ElevatedCard(Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){Text("Readiness",style=MaterialTheme.typography.titleMedium);Text(readiness?.label?:"Faça seu check-in");readiness?.reasons?.take(2)?.forEach{Text("• $it")};readiness?.let{Text(it.recommendation)};Button(onClick=onOpenCheckIn){Text("CHECK-IN")}}}}
  item{Text("Semana",style=MaterialTheme.typography.titleLarge);Text("${summary.weeklyWorkouts}/${summary.weeklyGoal} treinos • ${"%.0f".format(Locale.US,summary.weeklyVolumeKg)} kg • ${"%.1f".format(Locale.US,summary.weeklyCardioKm)} km cardio")}
  item{Text("Volume muscular",style=MaterialTheme.typography.titleLarge)}
  items(muscleVolumes){m->ListItem(headlineContent={Text(m.muscleGroup)},supportingContent={Text("${m.sets} séries • ${"%.0f".format(Locale.US,m.volumeKg)} kg")},trailingContent={Text(m.weeklyChangePercent?.let{"%+.0f%%".format(Locale.US,it)}?:"novo")})}
  item{Text("Evolução — 28 dias",style=MaterialTheme.typography.titleLarge);ElevatedCard(Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp)){Text("Peso ${evolution.currentWeightKg?.let{"%.1f kg".format(Locale.US,it)}?:"--"}");Text("${evolution.workoutFrequency} treinos • ${"%.0f".format(Locale.US,evolution.workoutVolumeKg)} kg");Text("Cardio ${"%.1f".format(Locale.US,evolution.cardioDistanceKm)} km • ${evolution.cardioMinutes} min");Text("${evolution.measurementsCount} medidas • ${evolution.photosCount} fotos")}}}
  item{Text("Health Connect",style=MaterialTheme.typography.titleLarge);Text("${health.stepsToday} passos • ${health.restingHeartRateBpm?.let{"$it bpm"}?:"FC --"} • ${health.activeCaloriesToday} kcal • ${health.exerciseMinutesToday} min")}
 }
}
@Composable private fun Metric(label:String,value:String){Column{Text(value,style=MaterialTheme.typography.titleMedium);Text(label,style=MaterialTheme.typography.labelSmall)}}
