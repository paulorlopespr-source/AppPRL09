package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.CardioIntervalCodec
import com.example.data.model.CardioIntervalTemplate
import com.example.domain.cardio.CardioIntervalPlan
import com.example.ui.theme.*

@Composable
fun CardioIntervalBuilder(
    templates: List<CardioIntervalTemplate>,
    onChanged: (CardioIntervalPlan?) -> Unit,
    onSave: (CardioIntervalPlan) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("Treino intervalado FITPR09") }
    var effortLabel by rememberSaveable { mutableStateOf("CORRER") }
    var effort by rememberSaveable { mutableStateOf("120") }
    var rest by rememberSaveable { mutableStateOf("120") }
    var cycles by rememberSaveable { mutableStateOf("6") }
    var warmup by rememberSaveable { mutableStateOf("0") }
    var cooldown by rememberSaveable { mutableStateOf("0") }
    val plan = runCatching {
        CardioIntervalPlan(name.trim(), effortLabel.trim(), effort.toInt(), rest.toInt(), cycles.toInt(), warmup.toInt(), cooldown.toInt())
    }.getOrNull()
    LaunchedEffect(plan) { onChanged(plan) }
    fun applyPlan(value: CardioIntervalPlan) {
        name = value.name; effortLabel = value.effortLabel; effort = value.effortSeconds.toString()
        rest = value.restSeconds.toString(); cycles = value.cycles.toString()
        warmup = value.warmupSeconds.toString(); cooldown = value.cooldownSeconds.toString()
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("CONSTRUIR TREINO INTERVALADO", color = LilacAccent)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton(onClick = { applyPlan(CardioIntervalPlan()) }) { Text("2 min / 2 min") }
            TextButton(onClick = { applyPlan(CardioIntervalPlan(name = "Tabata", effortLabel = "ESFORÇO", effortSeconds = 20, restSeconds = 10, cycles = 8)) }) { Text("Tabata 20 / 10") }
        }
        OutlinedTextField(name, { name = it }, label = { Text("Nome do treino") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(effortLabel, { effortLabel = it }, label = { Text("Atividade de esforço (correr, boxear…)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        IntervalNumber("Esforço em segundos", effort) { effort = it }
        IntervalNumber("Descanso em segundos", rest) { rest = it }
        IntervalNumber("Quantidade de ciclos", cycles) { cycles = it }
        IntervalNumber("Aquecimento em segundos (0 = sem)", warmup) { warmup = it }
        IntervalNumber("Desaquecimento em segundos (0 = sem)", cooldown) { cooldown = it }
        Text(plan?.let { "Duração: ${DateUtils.formatSecondsToTime(it.totalSeconds.toInt())} • descanso somente entre ciclos" } ?: "Use esforço de 1 a 3600 s, descanso de 0 a 3600 s e 1 a 100 ciclos.", color = if (plan == null) RedDestructive else LilacAccent)
        Button(onClick = { plan?.let(onSave) }, enabled = plan != null, modifier = Modifier.fillMaxWidth()) { Text("Salvar como modelo") }
        if (templates.isNotEmpty()) {
            Text("Meus modelos", color = TextPrimary)
            templates.forEach { template ->
                TextButton(onClick = { CardioIntervalCodec.decode(template.configJson)?.let(::applyPlan) }) { Text(template.name) }
            }
        }
    }
}

@Composable
private fun IntervalNumber(label: String, value: String, onValue: (String) -> Unit) {
    OutlinedTextField(value, { text -> if (text.length <= 4 && text.all(Char::isDigit)) onValue(text) }, label = { Text(label) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())
}
