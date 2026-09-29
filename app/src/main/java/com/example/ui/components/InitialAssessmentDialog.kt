package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.FitnessGoal
import com.example.data.model.FitnessLevel

@Composable
fun InitialAssessmentDialog(onDismiss: () -> Unit, onSubmit: (age: Int, sex: String, height: Double, weight: Double, goal: FitnessGoal, level: FitnessLevel, days: Int) -> Unit) {
    var age by remember { mutableStateOf("") }; var height by remember { mutableStateOf("") }; var weight by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("Masculino") }; var level by remember { mutableStateOf(FitnessLevel.BEGINNER) }; var days by remember { mutableIntStateOf(3) }
    var goal by remember { mutableStateOf(FitnessGoal.CONDICIONAMENTO_GERAL) }
    val valid = age.toIntOrNull()?.let { it in 12..100 } == true && (height.toDoubleOrNull() ?: 0.0) in 100.0..240.0 && (weight.toDoubleOrNull() ?: 0.0) in 30.0..300.0
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Avaliação inicial") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Vamos montar seu plano de 12 semanas.", style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) { OutlinedTextField(age, { age = it }, label = { Text("Idade") }, modifier = Modifier.weight(1f)); OutlinedTextField(sex, { sex = it }, label = { Text("Perfil") }, modifier = Modifier.weight(1f)) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) { OutlinedTextField(height, { height = it }, label = { Text("Altura cm") }, modifier = Modifier.weight(1f)); OutlinedTextField(weight, { weight = it }, label = { Text("Peso kg") }, modifier = Modifier.weight(1f)) }
            Text("Nível", style = MaterialTheme.typography.labelMedium); SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) { FitnessLevel.values().forEach { item -> SegmentedButton(selected = level == item, onClick = { level = item }, shape = SegmentedButtonDefaults.itemShape(FitnessLevel.values().indexOf(item), FitnessLevel.values().size)) { Text(item.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) } } }
            Text("Dias disponíveis", style = MaterialTheme.typography.labelMedium); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf(3,4,5).forEach { value -> FilterChip(selected = days == value, onClick = { days = value }, label = { Text("$value dias") }) } }
        }
    }, confirmButton = { Button(enabled = valid, onClick = { onSubmit(age.toInt(), sex, height.toDouble(), weight.toDouble(), goal, level, days) }) { Text("Criar plano") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Depois") } })
}
