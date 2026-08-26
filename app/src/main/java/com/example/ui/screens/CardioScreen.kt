package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CardioSession
import com.example.data.model.CardioType
import com.example.data.model.IntensityLevel
import com.example.ui.components.AIEvaluationDialog
import com.example.ui.components.DateUtils
import com.example.ui.components.LocationSelector
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RedDestructive
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.RoyalBlueSubtle
import com.example.ui.viewmodel.FitnessViewModel

fun getCardioIcon(type: CardioType): ImageVector {
    return when (type) {
        CardioType.BICICLETA_INDOOR -> Icons.Default.DirectionsBike
        CardioType.CAMINHADA_ESTEIRA -> Icons.Default.DirectionsWalk
        CardioType.CAMINHADA_AR_LIVRE -> Icons.Default.Park
        CardioType.CORRIDA -> Icons.Default.DirectionsRun
        CardioType.FUTEBOL -> Icons.Default.SportsSoccer
    }
}

@Composable
fun CardioScreen(
    viewModel: FitnessViewModel,
    modifier: Modifier = Modifier
) {
    val cardioSessions by viewModel.allCardioSessions.collectAsStateWithLifecycle()
    val activeCardio by viewModel.activeCardio.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var showManualLogDialog by remember { mutableStateOf(false) }
    var showStartLiveDialog by remember { mutableStateOf(false) }
    var selectedCardioForLive by remember { mutableStateOf(CardioType.BICICLETA_INDOOR) }
    var showFinishLiveDialog by remember { mutableStateOf(false) }

    var selectedSessionAiFeedback by remember { mutableStateOf<String?>(null) }
    var showAiDialog by remember { mutableStateOf(false) }

    val totalCardioCalories = cardioSessions.sumOf { it.caloriesBurned }
    val totalMinutes = cardioSessions.sumOf { it.durationMinutes }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header
            item {
                Column {
                    Text(
                        text = "Treinos Cardiovasculares",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Bicicleta indoor, caminhada (esteira e ar livre), corrida e futebol",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Live Cardio in Progress Card
            if (activeCardio.isActive) {
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = RoyalBlueSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, RoyalBlue, RoundedCornerShape(22.dp))
                            .testTag("card_live_cardio_active")
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(RoyalBlue.copy(alpha = 0.2f))
                                    ) {
                                        Icon(
                                            imageVector = getCardioIcon(activeCardio.type),
                                            contentDescription = null,
                                            tint = RoyalBlue,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "EM ANDAMENTO: ${activeCardio.type.title.uppercase()}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = RoyalBlue,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = activeCardio.location,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.discardLiveCardio() },
                                    modifier = Modifier.testTag("btn_discard_cardio")
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Cancelar", tint = RedDestructive)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = DateUtils.formatSecondsToTime(activeCardio.durationSeconds),
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text("Tempo Decorrido", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "~${activeCardio.caloriesBurned}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Black,
                                        color = EmeraldSuccess
                                    )
                                    Text("Calorias (kcal)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { showFinishLiveDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_finish_live_cardio")
                            ) {
                                Text("Finalizar e Salvar Cardio", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Quick Stats Banner
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Tempo Total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$totalMinutes min", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = RoyalBlue)
                        }
                    }
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Queima Acumulada", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$totalCardioCalories kcal", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                        }
                    }
                }
            }

            // Cardio Modes Selection Grid
            item {
                Text(
                    text = "Escolha a Modalidade de Cardio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CardioType.values().forEach { cType ->
                        CardioTypeItemCard(
                            type = cType,
                            onStartLive = {
                                selectedCardioForLive = cType
                                showStartLiveDialog = true
                            }
                        )
                    }
                }
            }

            // History Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Histórico de Cardios",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(
                        onClick = { showManualLogDialog = true },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Registrar Manual", fontSize = 12.sp)
                    }
                }
            }

            if (cardioSessions.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Nenhuma sessão de cardio registrada",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Inicie uma bicicleta, corrida, caminhada ou partida de futebol para calcular seu gasto calórico com IA!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(cardioSessions, key = { it.id }) { cardio ->
                    CardioSessionHistoryItem(
                        cardio = cardio,
                        onViewAi = { feedback ->
                            selectedSessionAiFeedback = feedback
                            showAiDialog = true
                        },
                        onDelete = { viewModel.deleteCardioSession(cardio.id) }
                    )
                }
            }
        }
    }

    // Start Live Cardio Dialog
    if (showStartLiveDialog) {
        var location by remember { mutableStateOf("Academia Smart Fit") }
        var intensity by remember { mutableStateOf(IntensityLevel.MODERADA) }

        AlertDialog(
            onDismissRequest = { showStartLiveDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = getCardioIcon(selectedCardioForLive),
                        contentDescription = null,
                        tint = RoyalBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Iniciar ${selectedCardioForLive.title}", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = selectedCardioForLive.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LocationSelector(
                        selectedLocation = location,
                        onLocationSelected = { location = it }
                    )

                    Text(
                        text = "Intensidade Estimada:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IntensityLevel.values().forEach { lvl ->
                            FilterChip(
                                selected = intensity == lvl,
                                onClick = { intensity = lvl },
                                label = { Text(lvl.label, fontSize = 12.sp) },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.startLiveCardio(selectedCardioForLive, location, intensity)
                        showStartLiveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_confirm_start_cardio")
                ) {
                    Text("Iniciar Agora", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartLiveDialog = false }) {
                    Text("Cancelar")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Finish Live Cardio Dialog (to input distance / heart rate optionally)
    if (showFinishLiveDialog) {
        var distanceStr by remember { mutableStateOf("") }
        var heartRateStr by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showFinishLiveDialog = false },
            title = { Text("Finalizar Sessão de Cardio", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Tempo Total: ${DateUtils.formatSecondsToTime(activeCardio.durationSeconds)} • Gasto: ~${activeCardio.caloriesBurned} kcal",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess
                    )

                    OutlinedTextField(
                        value = distanceStr,
                        onValueChange = { distanceStr = it },
                        label = { Text("Distância Percorrida (km) - Opcional") },
                        placeholder = { Text("Ex: 4.5") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = heartRateStr,
                        onValueChange = { heartRateStr = it },
                        label = { Text("Frequência Cardíaca Média (bpm) - Opcional") },
                        placeholder = { Text("Ex: 142") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Observações") },
                        placeholder = { Text("Ex: Foco em cadência alta") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val dist = distanceStr.toDoubleOrNull()
                        val hr = heartRateStr.toIntOrNull()
                        viewModel.finishLiveCardio(dist, hr, notes)
                        showFinishLiveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Salvar & Avaliar com IA", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishLiveDialog = false }) {
                    Text("Voltar")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Manual Log Dialog
    if (showManualLogDialog) {
        ManualCardioLogDialog(
            onDismiss = { showManualLogDialog = false },
            onSave = { cardio ->
                viewModel.logManualCardio(cardio)
                showManualLogDialog = false
            }
        )
    }

    // AI Feedback Dialog
    if (showAiDialog) {
        AIEvaluationDialog(
            title = "Avaliação de Cardio & Calorias com IA",
            evaluationText = selectedSessionAiFeedback,
            isLoading = false,
            onDismiss = { showAiDialog = false }
        )
    }
}

@Composable
fun CardioTypeItemCard(
    type: CardioType,
    onStartLive: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_cardio_type_${type.name}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(RoyalBlueSubtle)
                ) {
                    Icon(
                        imageVector = getCardioIcon(type),
                        contentDescription = null,
                        tint = RoyalBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = type.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = type.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onStartLive,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_start_cardio_${type.name}")
            ) {
                Text("Iniciar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun CardioSessionHistoryItem(
    cardio: CardioSession,
    onViewAi: (String) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(RoyalBlueSubtle)
                    ) {
                        Icon(
                            imageVector = getCardioIcon(cardio.type),
                            contentDescription = null,
                            tint = RoyalBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = cardio.type.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = DateUtils.formatEpochDayShort(cardio.dateEpochDay),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldSuccess.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${cardio.durationMinutes} min • ~${cardio.caloriesBurned} kcal",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = RedDestructive, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "📍 ${cardio.location}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "⚡ Intensidade: ${cardio.intensity.label}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (cardio.distanceKm != null) {
                    Text(
                        text = "📏 ${cardio.distanceKm} km",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (cardio.aiEvaluation.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                FilledTonalButton(
                    onClick = { onViewAi(cardio.aiEvaluation) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = RoyalBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ver Avaliação Calórica da IA", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun ManualCardioLogDialog(
    onDismiss: () -> Unit,
    onSave: (CardioSession) -> Unit
) {
    var type by remember { mutableStateOf(CardioType.BICICLETA_INDOOR) }
    var durationMinutes by remember { mutableStateOf(30) }
    var distanceStr by remember { mutableStateOf("") }
    var intensity by remember { mutableStateOf(IntensityLevel.MODERADA) }
    var location by remember { mutableStateOf("Academia Smart Fit") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Sessão de Cardio", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Tipo de Cardio:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CardioType.values().forEach { cType ->
                        FilterChip(
                            selected = type == cType,
                            onClick = { type = cType },
                            label = { Text(cType.title, fontSize = 11.sp) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                LocationSelector(
                    selectedLocation = location,
                    onLocationSelected = { location = it }
                )

                OutlinedTextField(
                    value = durationMinutes.toString(),
                    onValueChange = { durationMinutes = it.toIntOrNull() ?: 20 },
                    label = { Text("Duração (minutos) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = distanceStr,
                    onValueChange = { distanceStr = it },
                    label = { Text("Distância (km) - Opcional") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Intensidade:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IntensityLevel.values().forEach { lvl ->
                        FilterChip(
                            selected = intensity == lvl,
                            onClick = { intensity = lvl },
                            label = { Text(lvl.label, fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val met = when (intensity) {
                        IntensityLevel.LEVE -> type.metLight
                        IntensityLevel.MODERADA -> type.metModerate
                        IntensityLevel.INTENSA -> type.metIntense
                    }
                    val estCalories = ((met * 3.5 * 75.0 / 200.0) * durationMinutes).toInt()

                    val session = CardioSession(
                        type = type,
                        dateEpochDay = DateUtils.todayEpochDay(),
                        timestampMillis = System.currentTimeMillis(),
                        durationMinutes = durationMinutes,
                        distanceKm = distanceStr.toDoubleOrNull(),
                        intensity = intensity,
                        location = location,
                        caloriesBurned = estCalories,
                        notes = notes
                    )
                    onSave(session)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Salvar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}
