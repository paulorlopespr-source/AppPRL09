package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.GpsOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.CardioType
import com.example.data.model.IntensityLevel
import com.example.ui.theme.AmberSubtle
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlowPurple
import com.example.ui.theme.GradientHeroPrimary
import com.example.ui.theme.GradientVibrant
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurplePrimaryDark
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.RedDestructive
import com.example.ui.theme.RedSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.ActiveCardioState
import com.example.ui.viewmodel.FitnessViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveCardioTrackerModal(
    viewModel: FitnessViewModel,
    onDismiss: () -> Unit,
    activeCardio: ActiveCardioState = viewModel.activeCardio.collectAsStateWithLifecycle().value,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    var showFinishDialog by remember { mutableStateOf(false) }
    var showDiscardConfirm by remember { mutableStateOf(false) }

    // GPS Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.toggleCardioGps(true)
        }
    }

    fun handleToggleGps() {
        if (!activeCardio.gpsEnabled) {
            val hasFine = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val hasCoarse = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasFine || hasCoarse) {
                viewModel.toggleCardioGps(true)
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } else {
            viewModel.toggleCardioGps(false)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PurpleDarkest,
        contentColor = TextPrimary,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 44.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(LilacSoft.copy(alpha = 0.4f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .testTag("modal_active_cardio_tracker")
        ) {
            // Header: Cardio Type & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(EmeraldSuccess, PurpleVibrant)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCardioIcon(activeCardio.type),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeCardio.type.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "${activeCardio.location} • Intensidade ${activeCardio.intensity.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Minimizar", tint = LilacSoft)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stopwatch Hero Display
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.2.dp, LilacAccent.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                color = PurpleDeepCard
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (activeCardio.isPaused) AmberSubtle else EmeraldSubtle)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (activeCardio.isPaused) AmberWarning else EmeraldSuccess)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (activeCardio.isPaused) "PAUSADO" else "EM TEMPO REAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = if (activeCardio.isPaused) AmberWarning else EmeraldSuccess,
                                letterSpacing = 1.sp
                            )
                        }

                        // Target minutes progress badge
                        if (activeCardio.targetMinutes != null && activeCardio.targetMinutes > 0) {
                            val targetSec = activeCardio.targetMinutes * 60
                            val pct = ((activeCardio.durationSeconds.toFloat() / targetSec.toFloat()) * 100).toInt().coerceIn(0, 100)
                            Text(
                                text = "Meta: ${activeCardio.targetMinutes}m ($pct%)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LilacAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Big Clock Display (HH:MM:SS)
                    val hours = activeCardio.durationSeconds / 3600
                    val minutes = (activeCardio.durationSeconds % 3600) / 60
                    val seconds = activeCardio.durationSeconds % 60
                    val timeFormatted = if (hours > 0) {
                        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
                    } else {
                        String.format(Locale.US, "%02d:%02d", minutes, seconds)
                    }

                    Text(
                        text = timeFormatted,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = "TEMPO DECORRIDO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )

                    // Target duration progress bar
                    if (activeCardio.targetMinutes != null && activeCardio.targetMinutes > 0) {
                        val targetSec = activeCardio.targetMinutes * 60
                        val progressFraction = (activeCardio.durationSeconds.toFloat() / targetSec.toFloat()).coerceIn(0f, 1f)

                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(PurpleDarkest)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = progressFraction)
                                    .height(6.dp)
                                    .clip(CircleShape)
                                    .background(GradientVibrant)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metrics 2x2 Bento (Distância, Pace, Calorias, Velocidade)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Metric 1: Distância
                Surface(
                    modifier = Modifier.weight(1f),
                    color = PurpleDeepCard,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("DISTÂNCIA", fontSize = 10.sp, fontWeight = FontWeight.Black, color = LilacSoft, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.US, "%.2f", activeCardio.distanceKm),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text("quilômetros (km)", fontSize = 10.sp, color = TextMuted)
                    }
                }

                // Metric 2: Ritmo / Pace
                val paceMin = activeCardio.paceMinKm.toInt()
                val paceSec = ((activeCardio.paceMinKm - paceMin) * 60).toInt()
                val paceStr = if (activeCardio.paceMinKm > 0 && activeCardio.paceMinKm < 30) {
                    String.format(Locale.US, "%d'%02d\"", paceMin, paceSec)
                } else "--'--\""

                Surface(
                    modifier = Modifier.weight(1f),
                    color = PurpleDeepCard,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("RITMO (PACE)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = LilacSoft, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = paceStr,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = LilacAccent
                        )
                        Text("/km médio", fontSize = 10.sp, color = TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Metric 3: Calorias
                Surface(
                    modifier = Modifier.weight(1f),
                    color = PurpleDeepCard,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("QUEIMA", fontSize = 10.sp, fontWeight = FontWeight.Black, color = LilacSoft, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "~${activeCardio.caloriesBurned}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = EmeraldSuccess
                        )
                        Text("kcal estimadas", fontSize = 10.sp, color = TextMuted)
                    }
                }

                // Metric 4: Velocidade Atual
                Surface(
                    modifier = Modifier.weight(1f),
                    color = PurpleDeepCard,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("VELOCIDADE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = LilacSoft, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.US, "%.1f", activeCardio.speedKmh),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberWarning
                        )
                        Text("km/h atual", fontSize = 10.sp, color = TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // GPS LIVE TRACKING CARD & SATELLITE STATUS
            // ==========================================
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        1.dp,
                        if (activeCardio.gpsEnabled) EmeraldSuccess.copy(alpha = 0.6f) else LilacAccent.copy(alpha = 0.35f),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { handleToggleGps() }
                    .testTag("card_gps_tracking_toggle"),
                color = PurpleDeepCard
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (activeCardio.gpsEnabled) EmeraldSubtle else PurpleDarkSurface)
                                    .border(
                                        1.dp,
                                        if (activeCardio.gpsEnabled) EmeraldSuccess else GlassBorderSubtle,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (activeCardio.gpsEnabled) Icons.Default.GpsFixed else Icons.Default.GpsOff,
                                    contentDescription = null,
                                    tint = if (activeCardio.gpsEnabled) EmeraldSuccess else TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (activeCardio.gpsEnabled) "GPS do Celular: Ativo" else "GPS do Celular: Desativado",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    if (activeCardio.gpsEnabled) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(EmeraldSuccess)
                                        )
                                    }
                                }
                                Text(
                                    text = if (activeCardio.gpsEnabled) {
                                        val acc = activeCardio.gpsAccuracyMeters
                                        if (acc != null) "Precisão de sinal: ±${acc.toInt()}m (${activeCardio.gpsPointsCount} pts)"
                                        else "Buscando satélites GPS..."
                                    } else {
                                        "Toque para ativar o rastreamento por GPS em tempo real"
                                    },
                                    fontSize = 11.sp,
                                    color = if (activeCardio.gpsEnabled) LilacSoft else TextSecondary
                                )
                            }
                        }

                        // Toggle Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (activeCardio.gpsEnabled) EmeraldSuccess else PurpleDarkSurface)
                                .border(1.dp, if (activeCardio.gpsEnabled) EmeraldSuccess else GlassBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (activeCardio.gpsEnabled) "Ligado" else "Ativar",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeCardio.gpsEnabled) Color.White else LilacAccent
                            )
                        }
                    }

                    // Coordinates display if active
                    if (activeCardio.gpsEnabled && activeCardio.latitude != null && activeCardio.longitude != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(PurpleDarkSurface.copy(alpha = 0.6f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format(Locale.US, "Lat: %.4f • Lon: %.4f", activeCardio.latitude, activeCardio.longitude),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = "Percurso Ativo",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons: Play/Pause, Finalizar, Descartar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pause / Resume
                Surface(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (activeCardio.isPaused) {
                                viewModel.resumeLiveCardio()
                            } else {
                                viewModel.pauseLiveCardio()
                            }
                        }
                        .testTag("btn_pause_resume_cardio"),
                    color = if (activeCardio.isPaused) EmeraldDark else PurpleDarkSurface,
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(
                        1.2.dp,
                        if (activeCardio.isPaused) EmeraldSuccess else LilacAccent
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (activeCardio.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (activeCardio.isPaused) "Continuar" else "Pausar",
                            tint = if (activeCardio.isPaused) EmeraldSuccess else LilacAccent,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Finalizar & Salvar Button
                Button(
                    onClick = { showFinishDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("btn_modal_finish_cardio"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Finalizar & Salvar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                // Discard Button
                Surface(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .clickable { showDiscardConfirm = true }
                        .testTag("btn_modal_discard_cardio"),
                    color = PurpleDarkSurface,
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, RedDestructive.copy(alpha = 0.6f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Descartar",
                            tint = RedDestructive,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Finish Dialog
    if (showFinishDialog) {
        var heartRateStr by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = {
                Text(
                    text = "Finalizar ${activeCardio.type.title}",
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PurpleDeepCard,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Resumo da Sessão:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LilacSoft
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "⏱️ Tempo: ${DateUtils.formatSecondsToTime(activeCardio.durationSeconds)} • 📏 Distância: ${String.format(Locale.US, "%.2f", activeCardio.distanceKm)} km",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "🔥 Queima Estimada: ~${activeCardio.caloriesBurned} kcal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                        }
                    }

                    OutlinedTextField(
                        value = heartRateStr,
                        onValueChange = { heartRateStr = it },
                        label = { Text("Frequência Cardíaca Média (bpm) - Opcional") },
                        placeholder = { Text("Ex: 145") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Anotações do Treino") },
                        placeholder = { Text("Ex: Treino outdoor no parque, ritmo excelente!") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = GlassBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hr = heartRateStr.toIntOrNull()
                        viewModel.finishLiveCardio(
                            distanceKm = activeCardio.distanceKm,
                            heartRate = hr,
                            notes = notes,
                            onFinished = {
                                showFinishDialog = false
                                onDismiss()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_confirm_finish_cardio")
                ) {
                    Text("Salvar Treino & IA", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Voltar", color = TextMuted)
                }
            },
            containerColor = PurpleDarkSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Discard Confirm Dialog
    if (showDiscardConfirm) {
        AlertDialog(
            onDismissRequest = { showDiscardConfirm = false },
            title = { Text("Descartar Treino?", fontWeight = FontWeight.Black, color = TextPrimary) },
            text = {
                Text(
                    text = "Deseja realmente cancelar este treino de cardio? O tempo e dados não serão salvos.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.discardLiveCardio()
                        showDiscardConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDestructive),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sim, Descartar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardConfirm = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = PurpleDarkSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

fun getCardioIcon(type: CardioType): ImageVector {
    return when (type) {
        CardioType.BICICLETA_INDOOR -> Icons.Default.DirectionsBike
        CardioType.CAMINHADA_ESTEIRA -> Icons.Default.DirectionsWalk
        CardioType.CAMINHADA_AR_LIVRE -> Icons.Default.Park
        CardioType.CORRIDA -> Icons.Default.DirectionsRun
        CardioType.FUTEBOL -> Icons.Default.SportsSoccer
    }
}
