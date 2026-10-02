package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            Text("Nível de condicionamento", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(FitnessLevel.values().take(2), FitnessLevel.values().drop(2)).forEach { levelRow ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        levelRow.forEach { item ->
                            val selected = level == item
                            val label = when (item) {
                                FitnessLevel.SEDENTARY -> "Sedentário"
                                FitnessLevel.BEGINNER -> "Iniciante"
                                FitnessLevel.INTERMEDIATE -> "Intermediário"
                                FitnessLevel.ADVANCED -> "Avançado"
                            }
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { level = item },
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) Color(0xFF5722A8) else Color(0xFF171321),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (selected) 1.5.dp else 1.dp,
                                    if (selected) Color(0xFFC084FC) else Color(0xFF352B46)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        lineHeight = 16.sp,
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                        color = if (selected) Color.White else Color(0xFFD6D0E2),
                                        maxLines = 1
                                    )
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Selecionado",
                                            tint = Color.White,
                                            modifier = Modifier.padding(start = 5.dp).size(15.dp)
                                        )
                                    }
                                }
                            }
                        }
                        if (levelRow.size == 1) androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                    }
                }
            }
            Text("Dias disponíveis", style = MaterialTheme.typography.labelMedium); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf(3,4,5).forEach { value -> FilterChip(selected = days == value, onClick = { days = value }, label = { Text("$value dias") }) } }
        }
    }, confirmButton = { Button(enabled = valid, onClick = { onSubmit(age.toInt(), sex, height.toDouble(), weight.toDouble(), goal, level, days) }) { Text("Criar plano") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Depois") } })
}
