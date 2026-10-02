package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.Equipment
import com.example.data.model.HomeEquipmentProfile
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeEquipmentDialog(
    initialProfile: HomeEquipmentProfile,
    onDismiss: () -> Unit,
    onSave: (HomeEquipmentProfile) -> Unit
) {
    var selected by remember(initialProfile) { mutableStateOf(initialProfile.availableEquipment + Equipment.PESO_CORPO) }
    var barLoad by remember(initialProfile) { mutableStateOf(initialProfile.barLoadKg.toInput()) }
    var dumbbellLoad by remember(initialProfile) { mutableStateOf(initialProfile.dumbbellLoadPerHandKg.toInput()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Equipamentos disponíveis", color = TextPrimary, fontWeight = FontWeight.Black) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text("Marque os equipamentos disponíveis e informe a carga que costuma usar. Esse valor é uma referência inicial, nunca um limite do treino.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Equipment.values().toList().chunked(2).forEach { pair ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                pair.forEach { equipment ->
                                    FilterChip(
                                        modifier = Modifier.weight(1f),
                                        selected = equipment in selected,
                                        onClick = {
                                            if (equipment != Equipment.PESO_CORPO) {
                                                selected = if (equipment in selected) selected - equipment else selected + equipment
                                            }
                                        },
                                        label = { Text(equipment.displayName, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    if (Equipment.ELASTICO in selected) {
                        Text(
                            "A biblioteca ainda não tem exercícios cadastrados com elástico. O plano usará os outros equipamentos selecionados e peso corporal.",
                            color = TextMuted,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    if (Equipment.BARRA in selected || Equipment.HALTERES in selected) {
                        Text("Suas cargas de referência", color = LilacAccent, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        if (Equipment.BARRA in selected) EquipmentNumberField("Carga habitual na barra (kg)", barLoad, { barLoad = it })
                        if (Equipment.HALTERES in selected) EquipmentNumberField("Carga habitual em cada halter (kg)", dumbbellLoad, { dumbbellLoad = it })
                        Text("Você pode ajustar a carga livremente durante o treino. Não há limite fixo de anilhas ou peso.", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        HomeEquipmentProfile(
                            availableEquipment = selected + Equipment.PESO_CORPO,
                            barLoadKg = barLoad.toOptionalKg(),
                            dumbbellLoadPerHandKg = dumbbellLoad.toOptionalKg(),
                            configured = true
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) { Text("Montar treino", color = TextPrimary, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = TextMuted) } },
        containerColor = PurpleDarkSurface,
        shape = RoundedCornerShape(22.dp)
    )
}

@Composable
private fun EquipmentNumberField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { input -> if (input.matches(Regex("^\\d*([,.]\\d*)?$"))) onValueChange(input) },
        label = { Text(label) },
        suffix = { Text("kg") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = LilacAccent, unfocusedBorderColor = TextMuted),
        modifier = Modifier.fillMaxWidth()
    )
}

private fun Double?.toInput(): String = this?.toString().orEmpty()
private fun String.toOptionalKg(): Double? = replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 }
