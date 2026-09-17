package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.readiness.DailyCheckIn
import com.example.ui.theme.EmeraldSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.Phase45ViewModel
import com.example.util.HealthConnectManager
import java.util.Locale

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

    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = healthManager.createPermissionResultContract()
    ) { granted ->
        val requested = vm.healthPermissions()
        val allGranted = requested.all { it in granted }
        if (allGranted) {
            vm.refreshHealthConnect()
            Toast.makeText(context, "Health Connect conectado com sucesso!", Toast.LENGTH_SHORT).show()
        } else {
            vm.refreshHealthConnect()
            Toast.makeText(context, "Permissões atualizadas no Health Connect.", Toast.LENGTH_SHORT).show()
        }
    }

    if (checkIn) {
        ReadinessCheckInScreen(
            onSave = {
                vm.saveCheckIn(it)
                checkIn = false
            },
            onCancel = {
                checkIn = false
            }
        )
        return
    }

    val s = summary
    val e = evolution

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleDarkest)
    ) {
        if (s == null || e == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LilacAccent)
            }
            return
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = topInset + 16.dp,
                bottom = bottomInset + 100.dp
            ),
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
                            text = "Painel Integrado",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Readiness, Health Connect & Carga Semanal",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PurpleDeepCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔥 ${s.streakDays}d streak",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LilacAccent
                            )
                        }
                    }
                }
            }

            // Today Workout Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PurpleDarkSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GlassBorderSubtle, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(PurpleDeepCard, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FitnessCenter,
                                        contentDescription = null,
                                        tint = LilacAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Treino de Hoje",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = s.todayWorkout?.title ?: "Nenhum treino agendado",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }

                            if (s.todayWorkout != null) {
                                Button(
                                    onClick = onStartTodayWorkout,
                                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("INICIAR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Quick Metrics Row (Steps, Weight, Streak, Weekly Workouts)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardMetricCard(
                        title = "Passos Hoje",
                        value = "${health.stepsToday}",
                        subtitle = if (health.healthConnectAvailable) "Health Connect" else "Estimado",
                        icon = Icons.Default.DirectionsRun,
                        accentColor = LilacAccent,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardMetricCard(
                        title = "Peso Atual",
                        value = s.currentWeightKg?.let { String.format(Locale.US, "%.1f kg", it) } ?: "--",
                        subtitle = "Bioimpedância/Histórico",
                        icon = Icons.Default.Speed,
                        accentColor = EmeraldSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Readiness Score Card
            item {
                val r = readiness
                val scoreColor = when {
                    r == null -> TextMuted
                    r.score >= 80 -> EmeraldSuccess
                    r.score >= 60 -> LilacAccent
                    r.score >= 45 -> Color(0xFFFFB74D)
                    else -> Color(0xFFFF6E40)
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PurpleDarkSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GlassBorderSubtle, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(PurpleDeepCard, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = scoreColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Readiness & Prontidão",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = if (r != null) r.label else "Check-in pendente hoje",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = scoreColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            if (r != null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = scoreColor.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, scoreColor.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "${r.score}%",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = scoreColor,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        if (r != null) {
                            Text(
                                text = r.recommendation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )

                            if (r.reasons.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    r.reasons.forEach { reason ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(scoreColor)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = reason,
                                                fontSize = 12.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            if (r.suggestDeload) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0x33FF6E40),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6E40)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF6E40))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Recomendação de Deload: carga semanal significativamente superior à recuperação física.",
                                            fontSize = 11.sp,
                                            color = Color(0xFFFF6E40),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            OutlinedButton(
                                onClick = { checkIn = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ATUALIZAR CHECK-IN DIÁRIO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LilacAccent)
                            }
                        } else {
                            Text(
                                text = "Faça seu check-in rápido de 30 segundos (sono, energia, dor e estresse) para calcular sua prontidão para treinar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )

                            Button(
                                onClick = { checkIn = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("FAZER CHECK-IN DE HOJE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Weekly Training Summary Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PurpleDarkSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GlassBorderSubtle, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Resumo da Semana",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "${s.weeklyWorkouts} / ${s.weeklyGoal}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = LilacAccent
                                )
                                Text("Treinos feitos", fontSize = 11.sp, color = TextSecondary)
                            }

                            Column {
                                Text(
                                    text = String.format(Locale.US, "%.0f kg", s.weeklyVolumeKg),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldSuccess
                                )
                                Text("Volume levantado", fontSize = 11.sp, color = TextSecondary)
                            }

                            Column {
                                Text(
                                    text = String.format(Locale.US, "%.1f km", s.weeklyCardioKm),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFFB74D)
                                )
                                Text("Cardio ao ar livre", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            // Muscle Volume by Group
            item {
                Text(
                    text = "Volume por Grupo Muscular (Esta Semana)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            if (muscleVolumes.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PurpleDarkSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(16.dp))
                    ) {
                        Text(
                            text = "Conclua um treino com séries registradas para visualizar a tonelagem e séries por grupo muscular.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(muscleVolumes) { muscle ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = PurpleDarkSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = muscle.muscleGroup,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${muscle.sets} séries • ${String.format(Locale.US, "%.0f kg", muscle.volumeKg)}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            muscle.weeklyChangePercent?.let { change ->
                                val isPositive = change >= 0
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isPositive) EmeraldSubtle else PurpleDeepCard
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%+.0f%%", change),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPositive) EmeraldSuccess else LilacSoft,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            } ?: Text(
                                text = "novo",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            // Health Connect Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PurpleDarkSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GlassBorderSubtle, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(PurpleDeepCard, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HealthAndSafety,
                                        contentDescription = null,
                                        tint = EmeraldSuccess,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Google Health Connect",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = if (health.healthConnectAvailable) "Sincronização em tempo real" else "Disponível no dispositivo",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (health.healthConnectAvailable) EmeraldSuccess else TextMuted
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = health.restingHeartRateBpm?.let { "$it bpm" } ?: "--",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text("FC Repouso", fontSize = 11.sp, color = TextSecondary)
                            }
                            Column {
                                Text(
                                    text = "${health.activeCaloriesToday} kcal",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text("Calorias Ativas", fontSize = 11.sp, color = TextSecondary)
                            }
                            Column {
                                Text(
                                    text = "${health.exerciseMinutesToday} min",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text("Min. Exercício", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { permissionLauncher.launch(vm.healthPermissions()) },
                                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("PERMISSÕES", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { vm.refreshHealthConnect() },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("SINCRONIZAR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LilacAccent)
                            }
                        }
                    }
                }
            }

            // Evolution 28 Days Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PurpleDarkSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GlassBorderSubtle, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Evolução dos Últimos 28 Dias",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "${e.workoutFrequency} treinos de musculação • ${String.format(Locale.US, "%.0f kg", e.workoutVolumeKg)} de carga total • ${String.format(Locale.US, "%.1f km", e.cardioDistanceKm)} de cardio (${e.cardioMinutes} min) • ${e.measurementsCount} pesagens registradas • ${e.photosCount} fotos de evolução",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Check-in History
            if (history.isNotEmpty()) {
                item {
                    Text(
                        text = "Histórico de Check-ins Recentes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                items(history.take(7)) { c ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = PurpleDarkSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Sono: ${c.sleep}/5", fontSize = 11.sp, color = LilacAccent, fontWeight = FontWeight.Bold)
                                Text("Energia: ${c.energy}/5", fontSize = 11.sp, color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                                Text("Dor: ${c.soreness}/5", fontSize = 11.sp, color = Color(0xFFFFB74D), fontWeight = FontWeight.Bold)
                                Text("Motivação: ${c.motivation}/5", fontSize = 11.sp, color = LilacSoft, fontWeight = FontWeight.Bold)
                                Text("Estresse: ${c.stress}/5", fontSize = 11.sp, color = Color(0xFFFF6E40), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PurpleDarkSurface,
        modifier = modifier.border(1.dp, GlassBorderSubtle, RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, fontSize = 11.sp, color = TextSecondary)
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
            }
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Text(text = subtitle, fontSize = 10.sp, color = TextMuted)
        }
    }
}
