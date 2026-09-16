package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.viewmodel.Phase45ViewModel
import com.example.util.HealthConnectManager

@Composable
fun Phase45HubScreen(onStartTodayWorkout: () -> Unit, vm: Phase45ViewModel = viewModel()) {
    val summary by vm.summary.collectAsStateWithLifecycle()
    val health by vm.health.collectAsStateWithLifecycle()
    val evolution by vm.evolution.collectAsStateWithLifecycle()
    val readiness by vm.readiness.collectAsStateWithLifecycle()
    val history by vm.checkInHistory.collectAsStateWithLifecycle()
    val muscleVolumes by vm.muscleVolumes.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val healthManager = remember(context) { HealthConnectManager(context) }
    var checkIn by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = healthManager.createPermissionResultContract()
    ) { granted ->
        val requested = vm.healthPermissions()
        val allGranted = requested.all { it in granted }
        if (allGranted) {
            vm.refreshHealthConnect()
            Toast.makeText(context, "Health Connect conectado.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Algumas permissões de saúde não foram concedidas.", Toast.LENGTH_SHORT).show()
        }
    }

    if (checkIn) {
        ReadinessCheckInScreen { vm.saveCheckIn(it); checkIn = false }
        return
    }
    val s = summary
    val e = evolution
    if (s == null || e == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { CircularProgressIndicator() }
        return
    }

    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Painel", style = MaterialTheme.typography.headlineMedium) }
        item {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(s.todayWorkout?.title ?: "Nenhum treino pendente hoje")
                    if (s.todayWorkout != null) Button(onClick = onStartTodayWorkout) { Text("INICIAR TREINO") }
                }
            }
        }
        item { Text("${health.stepsToday} passos • ${s.currentWeightKg?.let { "%.1f kg".format(it) } ?: "peso --"} • streak ${s.streakDays} dias") }
        item { Text("Semana: ${s.weeklyWorkouts}/${s.weeklyGoal} treinos • %.0f kg • %.1f km cardio".format(s.weeklyVolumeKg, s.weeklyCardioKm)) }

        item { HorizontalDivider(); Text("Readiness", style = MaterialTheme.typography.titleLarge) }
        item {
            readiness?.let { r ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("${r.score}% — ${r.label}", style = MaterialTheme.typography.titleLarge)
                        Text(r.recommendation)
                        r.reasons.forEach { Text("• $it") }
                    }
                }
            } ?: Button(onClick = { checkIn = true }, modifier = Modifier.fillMaxWidth()) { Text("FAZER CHECK-IN") }
        }
        if (readiness != null) item { OutlinedButton(onClick = { checkIn = true }, modifier = Modifier.fillMaxWidth()) { Text("ATUALIZAR CHECK-IN") } }

        item { Text("Volume muscular — semana", style = MaterialTheme.typography.titleLarge) }
        if (muscleVolumes.isEmpty()) {
            item { Text("Conclua um treino com séries registradas para gerar o volume por grupo muscular.") }
        } else {
            items(muscleVolumes) { muscle ->
                val change = muscle.weeklyChangePercent?.let { " • %+.0f%% vs. semana anterior".format(it) } ?: ""
                Text("${muscle.muscleGroup}: ${muscle.sets} séries • %.0f kg%s".format(muscle.volumeKg, change))
            }
        }

        item { Text("Evolução — 28 dias", style = MaterialTheme.typography.titleLarge); Text("${e.workoutFrequency} treinos • %.0f kg volume • %.1f km cardio • ${e.measurementsCount} medidas • ${e.photosCount} fotos".format(e.workoutVolumeKg, e.cardioDistanceKm)) }
        item {
            Text("Health Connect", style = MaterialTheme.typography.titleLarge)
            Text(if (health.healthConnectAvailable) "${health.restingHeartRateBpm ?: 0} bpm • ${health.activeCaloriesToday} kcal • ${health.exerciseMinutesToday} min" else "Health Connect indisponível")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { permissionLauncher.launch(vm.healthPermissions()) }) { Text("CONECTAR / PERMITIR") }
                OutlinedButton(onClick = vm::refreshHealthConnect) { Text("SINCRONIZAR") }
            }
        }
        if (history.isNotEmpty()) {
            item { Text("Histórico de check-ins", style = MaterialTheme.typography.titleLarge) }
            items(history.take(7)) { c -> Text("Sono ${c.sleep}/5 • Energia ${c.energy}/5 • Dor ${c.soreness}/5 • Motivação ${c.motivation}/5 • Estresse ${c.stress}/5") }
        }
    }
}
