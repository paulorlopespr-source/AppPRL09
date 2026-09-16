package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MuscleGroup
import com.example.data.model.WorkoutTemplate
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RetroactiveWorkoutDialog(
    initialEpochDay: Long,
    templates: List<WorkoutTemplate>,
    defaultGymLocation: String = "Academia Smart Fit",
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        epochDay: Long,
        muscleGroups: List<MuscleGroup>,
        durationMinutes: Int,
        location: String,
        rpe: Int,
        notes: String,
        templateId: Long?
    ) -> Unit
) {
    var selectedEpochDay by remember { mutableLongStateOf(initialEpochDay) }
    var selectedMuscleGroups by remember { mutableStateOf<Set<MuscleGroup>>(emptySet()) }
    var selectedTemplate by remember { mutableStateOf<WorkoutTemplate?>(null) }
    var customTitle by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableIntStateOf(50) }
    var location by remember { mutableStateOf(defaultGymLocation) }
    var rpe by remember { mutableIntStateOf(8) }
    var notes by remember { mutableStateOf("") }

    val todayEpoch = remember { DateUtils.todayEpochDay() }

    // Auto generated title if user hasn't typed custom title
    val effectiveTitle by remember {
        derivedStateOf {
            if (customTitle.isNotBlank()) {
                customTitle
            } else if (selectedTemplate != null) {
                selectedTemplate!!.title
            } else if (selectedMuscleGroups.isNotEmpty()) {
                "Treino de " + selectedMuscleGroups.joinToString(", ") { it.displayName }
            } else {
                "Treino de Musculação"
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .heightIn(max = 760.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(EmeraldSuccess.copy(alpha = 0.15f))
                            .border(1.dp, EmeraldSuccess.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Marcar Treino Realizado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Registrar dia treinado no calendário",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Data do Treino (com Stepper Dia Anterior / Hoje / Próximo)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PurpleDeepCard,
                    border = BorderStroke(1.dp, GlassBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DATA DO TREINO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LilacAccent,
                                letterSpacing = 1.sp
                            )
                            if (selectedEpochDay == todayEpoch) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldSuccess.copy(alpha = 0.15f),
                                    border = BorderStroke(0.8.dp, EmeraldSuccess.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "HOJE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = EmeraldSuccess,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            } else if (selectedEpochDay == todayEpoch - 1) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = LilacAccent.copy(alpha = 0.15f),
                                    border = BorderStroke(0.8.dp, LilacAccent.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "ONTEM",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = LilacAccent,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { selectedEpochDay-- },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PurpleDarkSurface)
                                    .border(1.dp, GlassBorderSubtle, CircleShape)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Dia Anterior",
                                    tint = LilacAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = DateUtils.formatEpochDayWithWeekday(selectedEpochDay),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = DateUtils.formatEpochDayFull(selectedEpochDay),
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            IconButton(
                                onClick = { if (selectedEpochDay < todayEpoch + 365) selectedEpochDay++ },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PurpleDarkSurface)
                                    .border(1.dp, GlassBorderSubtle, CircleShape)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Próximo Dia",
                                    tint = LilacAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Quick date jumps: Hoje, Ontem, Anteontem
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "Hoje" to todayEpoch,
                                "Ontem" to (todayEpoch - 1),
                                "Anteontem" to (todayEpoch - 2)
                            ).forEach { (label, epoch) ->
                                val isSelected = selectedEpochDay == epoch
                                Surface(
                                    onClick = { selectedEpochDay = epoch },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) PurplePrimary.copy(alpha = 0.25f) else PurpleDarkSurface,
                                    border = BorderStroke(1.dp, if (isSelected) LilacAccent else GlassBorderSubtle),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) LilacAccent else TextSecondary,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Grupos Musculares Treinados (Principal Solicitação do Usuário)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GRUPOS MUSCULARES TREINADOS *",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LilacAccent,
                            letterSpacing = 1.sp
                        )
                        if (selectedMuscleGroups.isNotEmpty()) {
                            Text(
                                text = "${selectedMuscleGroups.size} selecionado(s)",
                                fontSize = 11.sp,
                                color = EmeraldSuccess,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Presets rápidos (Push, Pull, Legs, Braços)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val presets = listOf(
                            "Push (Peito/Ombro/Tríceps)" to setOf(MuscleGroup.PEITO, MuscleGroup.OMBROS, MuscleGroup.TRICEPS),
                            "Pull (Costas/Bíceps)" to setOf(MuscleGroup.COSTAS, MuscleGroup.BICEPS),
                            "Legs (Pernas Completo)" to setOf(MuscleGroup.QUADRICEPS, MuscleGroup.POSTERIOR_GLUTEOS, MuscleGroup.PANTURRILHA),
                            "Braços" to setOf(MuscleGroup.BICEPS, MuscleGroup.TRICEPS),
                            "Superiores" to setOf(MuscleGroup.PEITO, MuscleGroup.COSTAS, MuscleGroup.OMBROS, MuscleGroup.BICEPS, MuscleGroup.TRICEPS),
                            "Quadríceps" to setOf(MuscleGroup.QUADRICEPS),
                            "Glúteos & Posterior" to setOf(MuscleGroup.POSTERIOR_GLUTEOS)
                        )

                        presets.forEach { (name, groupSet) ->
                            val isSelected = selectedMuscleGroups == groupSet
                            Surface(
                                onClick = {
                                    selectedMuscleGroups = if (isSelected) emptySet() else groupSet
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) EmeraldSuccess.copy(alpha = 0.2f) else PurpleDarkSurface,
                                border = BorderStroke(1.dp, if (isSelected) EmeraldSuccess else GlassBorderSubtle)
                            ) {
                                Text(
                                    text = name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) EmeraldSuccess else LilacSoft,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // FlowRow de todos os grupos musculares individuais
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MuscleGroup.values().forEach { group ->
                            val isChecked = selectedMuscleGroups.contains(group)
                            FilterChip(
                                selected = isChecked,
                                onClick = {
                                    selectedMuscleGroups = if (isChecked) {
                                        selectedMuscleGroups - group
                                    } else {
                                        selectedMuscleGroups + group
                                    }
                                },
                                label = {
                                    Text(
                                        text = group.displayName,
                                        fontSize = 12.sp,
                                        fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                leadingIcon = if (isChecked) {
                                    {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = EmeraldSuccess,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                } else null,
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldSuccess.copy(alpha = 0.15f),
                                    selectedLabelColor = EmeraldSuccess,
                                    containerColor = PurpleDarkSurface,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isChecked,
                                    borderColor = GlassBorderSubtle,
                                    selectedBorderColor = EmeraldSuccess.copy(alpha = 0.6f),
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 1.2.dp
                                ),
                                modifier = Modifier.testTag("chip_muscle_${group.name}")
                            )
                        }
                    }
                }

                // 3. Vincular a Modelo Existente (Opcional)
                if (templates.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "VINCULAR A MODELO DE TREINO (OPCIONAL)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LilacAccent,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                onClick = { selectedTemplate = null },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selectedTemplate == null) PurplePrimary.copy(alpha = 0.2f) else PurpleDarkSurface,
                                border = BorderStroke(1.dp, if (selectedTemplate == null) LilacAccent else GlassBorderSubtle)
                            ) {
                                Text(
                                    text = "Treino Avulso",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTemplate == null) LilacAccent else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }

                            templates.forEach { tmpl ->
                                val isSelected = selectedTemplate?.id == tmpl.id
                                Surface(
                                    onClick = {
                                        selectedTemplate = if (isSelected) null else tmpl
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) PurplePrimary.copy(alpha = 0.25f) else PurpleDarkSurface,
                                    border = BorderStroke(1.dp, if (isSelected) LilacAccent else GlassBorderSubtle)
                                ) {
                                    Text(
                                        text = tmpl.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) LilacAccent else TextSecondary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Nome / Título do Treino
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "TÍTULO DO TREINO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LilacAccent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = if (customTitle.isNotBlank()) customTitle else effectiveTitle,
                        onValueChange = { customTitle = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_retroactive_workout_title"),
                        placeholder = { Text("Ex: Treino de Peito & Tríceps", color = TextMuted) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = PurpleDarkSurface,
                            unfocusedContainerColor = PurpleDarkSurface
                        )
                    )
                }

                // 5. Duração & Intensidade (RPE)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Duração
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DURAÇÃO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LilacAccent,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(35, 45, 60, 75, 90).forEach { mins ->
                                val isSel = durationMinutes == mins
                                Surface(
                                    onClick = { durationMinutes = mins },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) PurplePrimary else PurpleDarkSurface,
                                    border = BorderStroke(1.dp, if (isSel) LilacAccent else GlassBorderSubtle)
                                ) {
                                    Text(
                                        text = "${mins}m",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else TextSecondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Intensidade RPE
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ESFORÇO (RPE)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LilacAccent,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                "Leve" to 6,
                                "Médio" to 7,
                                "Forte" to 9
                            ).forEach { (label, rpeVal) ->
                                val isSel = rpe == rpeVal
                                Surface(
                                    onClick = { rpe = rpeVal },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) EmeraldSuccess.copy(alpha = 0.2f) else PurpleDarkSurface,
                                    border = BorderStroke(1.dp, if (isSel) EmeraldSuccess else GlassBorderSubtle),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) EmeraldSuccess else TextSecondary,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 6. Local do Treino
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "LOCAL DO TREINO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LilacAccent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Academia Smart Fit", "Bluefit", "Academia do Bairro", "Em Casa").forEach { loc ->
                            val isSel = location == loc
                            Surface(
                                onClick = { location = loc },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) PurplePrimary.copy(alpha = 0.25f) else PurpleDarkSurface,
                                border = BorderStroke(1.dp, if (isSel) LilacAccent else GlassBorderSubtle)
                            ) {
                                Text(
                                    text = loc,
                                    fontSize = 11.sp,
                                    color = if (isSel) LilacAccent else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = LilacAccent, modifier = Modifier.size(16.dp))
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = PurpleDarkSurface,
                            unfocusedContainerColor = PurpleDarkSurface
                        )
                    )
                }

                // 7. Observações Opcionais
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "OBSERVAÇÕES (OPCIONAL)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LilacAccent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ex: Foco em supino inclinado, ótima progressão", color = TextMuted, fontSize = 12.sp) },
                        maxLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = PurpleDarkSurface,
                            unfocusedContainerColor = PurpleDarkSurface
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        effectiveTitle,
                        selectedEpochDay,
                        selectedMuscleGroups.toList(),
                        durationMinutes,
                        location,
                        rpe,
                        notes,
                        selectedTemplate?.id
                    )
                },
                enabled = selectedMuscleGroups.isNotEmpty() || customTitle.isNotBlank() || selectedTemplate != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldSuccess,
                    contentColor = PurpleDarkest
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_confirm_retroactive_workout")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Salvar no Calendário", fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextMuted)
            }
        },
        containerColor = PurpleDarkSurface,
        shape = RoundedCornerShape(26.dp)
    )
}
