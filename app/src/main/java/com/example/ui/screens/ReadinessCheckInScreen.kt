package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.readiness.DailyCheckIn
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ReadinessCheckInScreen(
    onSave: (DailyCheckIn) -> Unit,
    onCancel: () -> Unit = {}
) {
    var sleep by remember { mutableIntStateOf(3) }
    var energy by remember { mutableIntStateOf(3) }
    var soreness by remember { mutableIntStateOf(3) }
    var motivation by remember { mutableIntStateOf(3) }
    var stress by remember { mutableIntStateOf(3) }

    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleDarkest)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = topInset + 8.dp, bottom = bottomInset + 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with back button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Check-in Diário",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Avalie sua recuperação física e mental hoje",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Text(
                text = "Escala de 1 a 5. Em dor e estresse, números maiores indicam maior fadiga ou desgaste acumulado.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                lineHeight = 16.sp
            )

            // Sliders
            ScaleCard(
                title = "Qualidade do Sono",
                subtitle = when (sleep) {
                    1 -> "Muito ruim / insônia"
                    2 -> "Sono insuficiente"
                    3 -> "Regular / normal"
                    4 -> "Bom e restaurador"
                    else -> "Excelente / renovado"
                },
                icon = Icons.Default.Bedtime,
                value = sleep,
                iconTint = LilacAccent,
                onChange = { sleep = it }
            )

            ScaleCard(
                title = "Nível de Energia",
                subtitle = when (energy) {
                    1 -> "Exausto / sem disposição"
                    2 -> "Energia baixa"
                    3 -> "Normal"
                    4 -> "Boa energia"
                    else -> "Energia no pico!"
                },
                icon = Icons.Default.Bolt,
                value = energy,
                iconTint = EmeraldSuccess,
                onChange = { energy = it }
            )

            ScaleCard(
                title = "Dor Muscular (DOMS)",
                subtitle = when (soreness) {
                    1 -> "Nenhuma dor"
                    2 -> "Leve sensação muscular"
                    3 -> "Dor moderada tolerável"
                    4 -> "Bastante dolorido"
                    else -> "Dor extrema / travado"
                },
                icon = Icons.Default.Healing,
                value = soreness,
                iconTint = Color(0xFFFFB74D),
                onChange = { soreness = it }
            )

            ScaleCard(
                title = "Motivação para Treinar",
                subtitle = when (motivation) {
                    1 -> "Nenhuma vontade hoje"
                    2 -> "Pouco motivado"
                    3 -> "Neutro / pronto pra cumprir"
                    4 -> "Motivado"
                    else -> "Super motivado e focado!"
                },
                icon = Icons.Default.Mood,
                value = motivation,
                iconTint = LilacAccent,
                onChange = { motivation = it }
            )

            ScaleCard(
                title = "Nível de Estresse Geral",
                subtitle = when (stress) {
                    1 -> "Muito calmo e relaxado"
                    2 -> "Baixo estresse"
                    3 -> "Moderado (dia a dia)"
                    4 -> "Estresse alto"
                    else -> "Extremamente estressado"
                },
                icon = Icons.Default.Psychology,
                value = stress,
                iconTint = Color(0xFFFF6E40),
                onChange = { stress = it }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    onSave(DailyCheckIn(sleep, energy, soreness, motivation, stress))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SALVAR CHECK-IN E CALCULAR READINESS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            Text(
                text = "O score de Readiness é um cálculo algorítmico do app para ajudar no seu planejamento e não substitui acompanhamento médico ou de um profissional de Educação Física.",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun ScaleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    value: Int,
    iconTint: Color,
    onChange: (Int) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PurpleDarkSurface,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = iconTint
                        )
                    }
                }
                Text(
                    text = "$value/5",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Slider(
                value = value.toFloat(),
                onValueChange = { onChange(it.toInt().coerceIn(1, 5)) },
                valueRange = 1f..5f,
                steps = 3,
                colors = SliderDefaults.colors(
                    thumbColor = LilacAccent,
                    activeTrackColor = LilacAccent,
                    inactiveTrackColor = PurpleDeepCard
                )
            )
        }
    }
}
