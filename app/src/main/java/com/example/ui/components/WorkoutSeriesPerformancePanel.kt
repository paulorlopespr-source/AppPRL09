package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ExerciseExecutionRecord
import com.example.data.model.ExerciseSetEntry
import com.example.domain.workout.PersonalRecord
import com.example.domain.workout.PersonalRecordType
import com.example.domain.workout.ProgressiveTarget
import com.example.domain.workout.WorkoutPerformanceEngine

/** Painel compacto que fecha o fluxo da Fase 2 sem obrigar o atleta a sair do exercício. */
@Composable
fun WorkoutSeriesPerformancePanel(
    currentSets: List<ExerciseSetEntry>,
    history: List<ExerciseExecutionRecord>,
    onEffortChanged: (setIndex: Int, rir: Int?, rpe: Double?) -> Unit,
    onNotesChanged: (setIndex: Int, notes: String) -> Unit
) {
    val target: ProgressiveTarget = remember(history) { WorkoutPerformanceEngine.progressiveTarget(history) }
    val prs: List<PersonalRecord> = remember(currentSets, history) { WorkoutPerformanceEngine.detectPRs(currentSets, history) }
    val last = target.lastPerformance

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(shape = RoundedCornerShape(14.dp), tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("DESEMPENHO & META", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black)
                if (last != null) {
                    Text("Último: ${fmt(last.maxWeightKg)} kg • ${last.maxReps} reps • ${fmt(last.totalVolumeKg)} kg volume • 1RM ${fmt(last.estimated1RM)} kg")
                } else Text("Sem treino anterior deste exercício.")
                Text("Meta de hoje: ${fmt(target.suggestedWeightKg)} kg × ${target.suggestedReps} reps", fontWeight = FontWeight.Bold)
                Text(target.reason, style = MaterialTheme.typography.bodySmall)
            }
        }

        currentSets.forEachIndexed { index, set ->
            if (!set.isCompleted) return@forEachIndexed
            var note by remember(set.notes) { mutableStateOf(set.notes) }
            Surface(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text("Série ${set.setNumber} concluída • ${fmt(set.weightKg)} kg × ${set.reps}", fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("RIR")
                        (0..4).forEach { value ->
                            FilterChip(selected = set.rir == value, onClick = { onEffortChanged(index, value, 10.0 - value.coerceAtMost(4)) }, label = { Text("$value") })
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("RPE")
                        listOf(7.0, 8.0, 9.0, 10.0).forEach { value ->
                            FilterChip(selected = set.rpe == value, onClick = { onEffortChanged(index, (10.0 - value).toInt(), value) }, label = { Text(value.toInt().toString()) })
                        }
                    }
                    OutlinedTextField(value = note, onValueChange = { note = it; onNotesChanged(index, it) }, modifier = Modifier.fillMaxWidth(), label = { Text("Observação da série") }, singleLine = true)
                }
            }
        }

        if (prs.isNotEmpty()) {
            Column(Modifier.fillMaxWidth().background(Color(0x222ECC71), RoundedCornerShape(14.dp)).border(1.dp, Color(0x662ECC71), RoundedCornerShape(14.dp)).padding(12.dp)) {
                Text("NOVO PR!", fontWeight = FontWeight.Black, color = Color(0xFF2ECC71))
                prs.forEach { pr -> Text(prLabel(pr), style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

private fun prLabel(pr: PersonalRecord): String = when (pr.type) {
    PersonalRecordType.MAX_WEIGHT -> "Carga: ${fmt(pr.previousValue)} → ${fmt(pr.newValue)} kg"
    PersonalRecordType.MAX_REPS -> "Repetições: ${pr.previousValue.toInt()} → ${pr.newValue.toInt()}"
    PersonalRecordType.TOTAL_VOLUME -> "Volume: ${fmt(pr.previousValue)} → ${fmt(pr.newValue)} kg"
    PersonalRecordType.ESTIMATED_1RM -> "1RM estimado: ${fmt(pr.previousValue)} → ${fmt(pr.newValue)} kg"
}
private fun fmt(v: Double) = if (v % 1.0 == 0.0) v.toInt().toString() else "%.1f".format(v)
