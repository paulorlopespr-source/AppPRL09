package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.viewmodel.CoachPRL09ViewModel

@Composable
fun CoachPRL09Screen(vm: CoachPRL09ViewModel = viewModel()) {
    val state by vm.ui.collectAsStateWithLifecycle()
    var question by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("45") }
    var equipment by remember { mutableStateOf("halteres, barra") }
    var focus by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Coach PRL09", style = MaterialTheme.typography.headlineMedium)
        Text("As métricas exibidas ao Coach são calculadas pelo app. A IA interpreta os dados, mas não deve inventar números ausentes.")
        state.context?.let { c ->
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text("Contexto atual", style = MaterialTheme.typography.titleMedium)
                    Text("7 dias: ${c.last7Days.strengthSessions} treinos • %.0f kg • %.1f km cardio".format(c.last7Days.strengthVolumeKg, c.last7Days.cardioDistanceKm))
                    Text("28 dias: ${c.last28Days.strengthSessions} treinos • %.0f kg • %.1f km cardio".format(c.last28Days.strengthVolumeKg, c.last28Days.cardioDistanceKm))
                    Text("Readiness: ${c.readinessScore?.let { "$it/100" } ?: "sem check-in"}")
                }
            }
        }

        OutlinedTextField(question, { question = it }, label = { Text("Pergunte sobre treino, progresso ou planejamento") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        Button(onClick = { vm.ask(question) }, enabled = question.isNotBlank() && !state.isLoading, modifier = Modifier.fillMaxWidth()) { Text("PERGUNTAR AO COACH") }
        state.answer?.let { answer -> Text(answer.text) }

        HorizontalDivider()
        Text("Treino adaptado", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(minutes, { minutes = it.filter(Char::isDigit) }, label = { Text("Tempo disponível (min)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(equipment, { equipment = it }, label = { Text("Equipamentos") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(focus, { focus = it }, label = { Text("Foco opcional") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = { vm.generateWorkout(minutes.toIntOrNull() ?: 45, equipment.split(',').map { it.trim() }.filter { it.isNotBlank() }, focus.takeIf { it.isNotBlank() }) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) { Text("GERAR SESSÃO ADAPTADA") }
        state.generatedWorkout?.let { plan -> Text(plan.toString()) }
        if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}
