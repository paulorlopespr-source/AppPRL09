package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.FitnessGoal
import com.example.data.model.FitnessLevel

@Composable
fun CycleReassessmentDialog(
    initialWeight: Double,
    onDismiss: () -> Unit,
    onStartSecondCycle: (Double, FitnessLevel, FitnessGoal, String) -> Unit
) {
    var weight by remember { mutableStateOf("$initialWeight") }
    var chest by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    var arm by remember { mutableStateOf("") }
    var thigh by remember { mutableStateOf("") }
    var level by remember { mutableStateOf(FitnessLevel.BEGINNER) }
    var goal by remember { mutableStateOf(FitnessGoal.CONDICIONAMENTO_GERAL) }
    var notes by remember { mutableStateOf("") }
    val parsedWeight = weight.toDoubleOrNull()
    val weightDelta = parsedWeight?.minus(initialWeight)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reavaliação do ciclo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Compare seus resultados e prepare o próximo plano de 12 semanas.", style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(weight, { weight = it }, label = { Text("Peso atual (kg)") }, modifier = Modifier.fillMaxWidth())
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Comparação com a avaliação inicial", style = MaterialTheme.typography.titleSmall)
                        Text("Inicial: ${"%.1f".format(initialWeight)} kg   •   Atual: ${parsedWeight?.let { "%.1f".format(it) } ?: "—"} kg")
                        if (weightDelta != null) {
                            val progress = (parsedWeight / initialWeight).toFloat().coerceIn(0.1f, 2f) / 2f
                            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                            Text(if (weightDelta >= 0) "Variação: +${"%.1f".format(weightDelta)} kg" else "Variação: ${"%.1f".format(weightDelta)} kg", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(chest, { chest = it }, label = { Text("Peito") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(waist, { waist = it }, label = { Text("Cintura") }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(arm, { arm = it }, label = { Text("Braço") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(thigh, { thigh = it }, label = { Text("Coxa") }, modifier = Modifier.weight(1f))
                }
                Text("Novo nível", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                    FitnessLevel.values().forEach { item -> FilterChip(selected = level == item, onClick = { level = item }, label = { Text(item.name.take(3)) }) }
                }
                OutlinedTextField(notes, { notes = it }, label = { Text("Observações") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { Button(enabled = parsedWeight != null && parsedWeight in 30.0..300.0, onClick = { onStartSecondCycle(parsedWeight!!, level, goal, notes) }) { Text("Iniciar segundo ciclo") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Agora não") } }
    )
}
