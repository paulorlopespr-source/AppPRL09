package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassSurfaceDeep
import com.example.ui.theme.GradientAction
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class BarType(
    val id: String,
    val name: String,
    val weightKg: Double,
    val shortLabel: String
)

val BAR_TYPES = listOf(
    BarType("olympic_20", "Barra Olímpica (20kg)", 20.0, "20 kg"),
    BarType("olympic_15", "Olímpica Feminina (15kg)", 15.0, "15 kg"),
    BarType("ez_bar", "Barra W / EZ (12kg)", 12.0, "12 kg"),
    BarType("standard_10", "Barra Reta Padrão (10kg)", 10.0, "10 kg"),
    BarType("zero_bar", "Sem Barra / Máquina (0kg)", 0.0, "0 kg")
)

data class PlateSpec(
    val weightKg: Double,
    val color: Color,
    val textColor: Color,
    val heightFraction: Float,
    val widthDp: Int
)

val STANDARD_PLATES = listOf(
    PlateSpec(25.0, Color(0xFFD32F2F), Color.White, 1.0f, 18),
    PlateSpec(20.0, Color(0xFF1976D2), Color.White, 0.92f, 16),
    PlateSpec(15.0, Color(0xFFFBC02D), Color.Black, 0.82f, 14),
    PlateSpec(10.0, Color(0xFF388E3C), Color.White, 0.72f, 12),
    PlateSpec(5.0, Color(0xFFECEFF1), Color.Black, 0.58f, 10),
    PlateSpec(2.5, Color(0xFF212121), Color.White, 0.46f, 8),
    PlateSpec(1.25, Color(0xFF9E9E9E), Color.Black, 0.36f, 6)
)

data class PlateCount(
    val plate: PlateSpec,
    val countPerSide: Int
)

/**
 * Calculadora Visual de Anilhas (Plate Calculator)
 * Calcula as anilhas exatas por lado para qualquer barra ou carga e exibe o diagrama gráfico.
 */
@Composable
fun PlateCalculatorDialog(
    initialTargetWeightKg: Double = 80.0,
    onDismiss: () -> Unit,
    onApplyWeight: ((Double) -> Unit)? = null
) {
    var targetWeightKg by remember { mutableDoubleStateOf(initialTargetWeightKg.coerceAtLeast(0.0)) }
    var selectedBar by remember { mutableStateOf(BAR_TYPES.first()) }
    var rawText by remember { mutableStateOf(if (targetWeightKg % 1.0 == 0.0) targetWeightKg.toInt().toString() else targetWeightKg.toString()) }

    val weightToDistribute = (targetWeightKg - selectedBar.weightKg).coerceAtLeast(0.0)
    val weightPerSide = weightToDistribute / 2.0

    // Compute plate configuration per side
    val calculatedPlates = remember(weightPerSide) {
        var remaining = weightPerSide
        val result = mutableListOf<PlateCount>()
        for (plate in STANDARD_PLATES) {
            if (remaining >= plate.weightKg) {
                val count = (remaining / plate.weightKg).toInt()
                if (count > 0) {
                    result.add(PlateCount(plate, count))
                    remaining -= count * plate.weightKg
                }
            }
        }
        result
    }

    val totalPlatesWeight = calculatedPlates.sumOf { it.plate.weightKg * it.countPerSide * 2 }
    val exactLoadedWeight = selectedBar.weightKg + totalPlatesWeight
    val remainderKg = targetWeightKg - exactLoadedWeight

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = PurpleDarkest,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, GlassBorder),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GradientAction),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Calculadora de Anilhas",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "Distribuição precisa por lado da barra",
                                style = MaterialTheme.typography.bodySmall,
                                color = LilacSoft
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Target Weight Input & Increment Controls
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PurpleDarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CARGA TOTAL ALVO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LilacAccent,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = {
                                    targetWeightKg = (targetWeightKg - 2.5).coerceAtLeast(0.0)
                                    rawText = if (targetWeightKg % 1.0 == 0.0) targetWeightKg.toInt().toString() else targetWeightKg.toString()
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PurpleDeepCard)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "-2.5kg", tint = TextPrimary)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            OutlinedTextField(
                                value = rawText,
                                onValueChange = { input ->
                                    rawText = input
                                    input.toDoubleOrNull()?.let { targetWeightKg = it.coerceAtLeast(0.0) }
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                suffix = { Text("kg", fontWeight = FontWeight.Bold, color = LilacAccent) },
                                textStyle = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    textAlign = TextAlign.Center
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LilacAccent,
                                    unfocusedBorderColor = GlassBorder,
                                    focusedContainerColor = PurpleDarkest,
                                    unfocusedContainerColor = PurpleDarkest
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.width(140.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            IconButton(
                                onClick = {
                                    targetWeightKg += 2.5
                                    rawText = if (targetWeightKg % 1.0 == 0.0) targetWeightKg.toInt().toString() else targetWeightKg.toString()
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PurpleDeepCard)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "+2.5kg", tint = TextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick increment pills
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(40.0, 60.0, 80.0, 100.0, 120.0, 140.0).forEach { preset ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (targetWeightKg == preset) LilacAccent else PurpleDeepCard,
                                    modifier = Modifier.clickable {
                                        targetWeightKg = preset
                                        rawText = preset.toInt().toString()
                                    }
                                ) {
                                    Text(
                                        text = "${preset.toInt()} kg",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (targetWeightKg == preset) PurpleDarkest else TextSecondary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bar Selector
                Text(
                    text = "TIPO DE BARRA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BAR_TYPES.forEach { bar ->
                        val isSelected = selectedBar.id == bar.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) PurplePrimary else PurpleDarkSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) LilacAccent else GlassBorderSubtle
                            ),
                            modifier = Modifier.clickable { selectedBar = bar }
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Text(
                                    text = bar.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else TextPrimary
                                )
                                Text(
                                    text = "Peso: ${bar.shortLabel}",
                                    fontSize = 10.sp,
                                    color = if (isSelected) LilacSoft else TextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Barbell Visual Graphic Diagram
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PurpleDarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MONTAGEM DA BARRA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LilacAccent,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Cada lado: ${if (weightPerSide % 1.0 == 0.0) weightPerSide.toInt().toString() else "%.1f".format(weightPerSide)} kg",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldSuccess
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Visual Olympic Barbell & Plates Rendering
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(PurpleDarkest)
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Bar Shaft (Metallic Silver Bar)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.92f)
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFF9E9E9E),
                                                Color(0xFFE0E0E0),
                                                Color(0xFF757575)
                                            )
                                        )
                                    )
                            )

                            // Center knurling mark
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF616161))
                            )

                            // Left Collar / Sleeve Stopper
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left Sleeve Plates Stack
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    // Left Stopper
                                    Box(
                                        modifier = Modifier
                                            .width(8.dp)
                                            .height(42.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Color(0xFFBDBDBD))
                                    )

                                    Spacer(modifier = Modifier.width(3.dp))

                                    // Left Plates (Ordered inside-out)
                                    calculatedPlates.forEach { item ->
                                        repeat(item.countPerSide) {
                                            PlateVisualDisk(item.plate)
                                            Spacer(modifier = Modifier.width(2.dp))
                                        }
                                    }
                                }

                                // Right Sleeve Plates Stack
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    // Right Plates (Ordered outside-in)
                                    calculatedPlates.reversed().forEach { item ->
                                        repeat(item.countPerSide) {
                                            PlateVisualDisk(item.plate)
                                            Spacer(modifier = Modifier.width(2.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(3.dp))

                                    // Right Stopper
                                    Box(
                                        modifier = Modifier
                                            .width(8.dp)
                                            .height(42.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Color(0xFFBDBDBD))
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Plate list breakdown
                        if (calculatedPlates.isEmpty()) {
                            Text(
                                text = if (targetWeightKg == selectedBar.weightKg) "Apenas o peso da barra!" else "Insira uma carga superior ao peso da barra.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        } else {
                            Text(
                                text = "Coloque em cada lado da barra:",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    calculatedPlates.forEach { item ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(item.plate.color)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "${item.countPerSide}x ${if (item.plate.weightKg % 1.0 == 0.0) item.plate.weightKg.toInt().toString() else item.plate.weightKg.toString()}kg",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = item.plate.textColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Fechar", color = TextMuted, fontWeight = FontWeight.Bold)
                    }

                    if (onApplyWeight != null) {
                        PrimaryButton(
                            text = "Aplicar ${if (targetWeightKg % 1.0 == 0.0) targetWeightKg.toInt().toString() else targetWeightKg.toString()} kg",
                            icon = Icons.Default.Check,
                            onClick = {
                                onApplyWeight(targetWeightKg)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlateVisualDisk(plate: PlateSpec) {
    Box(
        modifier = Modifier
            .width(plate.widthDp.dp)
            .height((84 * plate.heightFraction).dp)
            .clip(RoundedCornerShape(3.dp))
            .background(plate.color)
            .border(0.5.dp, Color.Black.copy(alpha = 0.4f), RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (plate.weightKg >= 5.0) {
            Text(
                text = "${plate.weightKg.toInt()}",
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                color = plate.textColor
            )
        }
    }
}
