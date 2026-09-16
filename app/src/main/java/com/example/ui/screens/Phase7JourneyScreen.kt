package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.viewmodel.Phase7ViewModel

@Composable
fun Phase7JourneyScreen(
    onOpenCoach: () -> Unit,
    onOpenDashboard: () -> Unit,
    onStartWorkout: () -> Unit,
    vm: Phase7ViewModel = viewModel()
) {
    val journey by vm.journey.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Jornada PRL09", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        Text("Seu progresso em uma visão simples: consistência, nível, missões e acesso rápido ao Coach.")

        val data = journey
        if (data == null) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            return@Column
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Nível ${data.level} • ${data.levelTitle}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${data.totalXp} XP total • ${data.xpIntoLevel}/${data.xpForNextLevel} XP para o próximo nível")
                LinearProgressIndicator(progress = { data.levelProgress }, modifier = Modifier.fillMaxWidth())
                Text("Sequência atual: ${data.currentStreakDays} dias • Melhor sequência: ${data.longestStreakDays} dias")
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Resumo da jornada", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${data.activeDays} dias ativos")
                Text("${data.strengthSessions} treinos de força • ${data.cardioSessions} sessões de cardio")
                Text("${data.totalMinutes} min registrados")
                Text("%.1f km de cardio • %.0f kg de volume".format(data.totalDistanceKm, data.totalVolumeKg))
                Text("${data.unlockedMedals}/${data.totalMedals} medalhas desbloqueadas")
            }
        }

        Text("Missões da semana", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        data.weeklyQuests.forEach { quest ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(quest.title, fontWeight = FontWeight.Bold)
                    Text(quest.subtitle, style = MaterialTheme.typography.bodySmall)
                    LinearProgressIndicator(progress = { quest.progress }, modifier = Modifier.fillMaxWidth())
                    Text("${quest.current}/${quest.target} • +${quest.rewardXp} XP${if (quest.completed) " • concluída" else ""}")
                }
            }
        }

        Spacer(Modifier.height(2.dp))
        Button(onClick = onStartWorkout, modifier = Modifier.fillMaxWidth()) { Text("INICIAR TREINO") }
        OutlinedButton(onClick = onOpenCoach, modifier = Modifier.fillMaxWidth()) { Text("ABRIR COACH PRL09") }
        OutlinedButton(onClick = onOpenDashboard, modifier = Modifier.fillMaxWidth()) { Text("VER PAINEL E READINESS") }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Fase 7", style = MaterialTheme.typography.labelMedium)
            Text("UX + gamificação consolidada", style = MaterialTheme.typography.labelMedium)
        }
    }
}
