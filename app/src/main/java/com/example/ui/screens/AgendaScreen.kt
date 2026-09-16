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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.CheckCircle
import com.example.ui.components.WorkoutReminderDialog
import com.example.ui.components.RetroactiveWorkoutDialog
import com.example.data.model.MuscleGroup
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import com.example.ui.components.FullWorkoutHistorySheet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SessionStatus
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutTemplate
import com.example.ui.components.AIEvaluationDialog
import com.example.ui.components.BentoCard
import com.example.ui.components.DateUtils
import com.example.ui.components.LiquidGlassSurface
import com.example.ui.components.LocationSelector
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GradientAction
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.RedDestructive
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FitnessViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    viewModel: FitnessViewModel,
    onStartScheduledWorkout: (WorkoutTemplate, String, Long?, Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val workoutSessions by viewModel.allWorkoutSessions.collectAsStateWithLifecycle()
    val cardioSessions by viewModel.allCardioSessions.collectAsStateWithLifecycle()
    val templates by viewModel.workoutTemplates.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val isAiEvaluating by viewModel.isAIEvaluating.collectAsStateWithLifecycle()
    val lastAiEvaluation by viewModel.lastAiEvaluation.collectAsStateWithLifecycle()
    val reminderSettings by viewModel.reminderSettings.collectAsStateWithLifecycle()

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var showScheduleDialog by remember { mutableStateOf(false) }
    var showRetroactiveDialog by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var showFullHistorySheet by remember { mutableStateOf(false) }
    var selectedSessionAiFeedback by remember { mutableStateOf<String?>(null) }
    var showAiDialog by remember { mutableStateOf(false) }

    val ptLocale = remember { Locale("pt", "BR") }

    val selectedEpochDay = selectedDate.toEpochDay()
    val workoutsForSelectedDay = workoutSessions.filter { it.dateEpochDay == selectedEpochDay }
    val cardiosForSelectedDay = cardioSessions.filter { it.dateEpochDay == selectedEpochDay }

    val completedWorkoutDaysSet = remember(workoutSessions) {
        workoutSessions.filter { it.status == SessionStatus.COMPLETED }.map { it.dateEpochDay }.toSet()
    }
    val scheduledWorkoutDaysSet = remember(workoutSessions) {
        workoutSessions.filter { it.status == SessionStatus.SCHEDULED }.map { it.dateEpochDay }.toSet()
    }
    val cardioDaysSet = remember(cardioSessions) { cardioSessions.map { it.dateEpochDay }.toSet() }

    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PurpleDarkest)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = topInset + 16.dp, bottom = bottomInset + 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Agenda & Planejamento",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Gerencie sua frequência e lembretes diários",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = { showFullHistorySheet = true },
                            shape = RoundedCornerShape(14.dp),
                            color = PurpleDarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent.copy(alpha = 0.4f)),
                            modifier = Modifier.testTag("btn_open_full_history_agenda")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.History,
                                    contentDescription = "Histórico de Treinos",
                                    tint = LilacAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Histórico",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = LilacAccent
                                )
                            }
                        }

                        Surface(
                            onClick = { showReminderDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            color = if (reminderSettings.isEnabled) PurplePrimary.copy(alpha = 0.15f) else PurpleDarkSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (reminderSettings.isEnabled) PurplePrimary.copy(alpha = 0.4f) else GlassBorderSubtle
                            ),
                            modifier = Modifier.testTag("btn_open_reminders_header")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.NotificationsActive,
                                    contentDescription = "Configurar Lembretes",
                                    tint = if (reminderSettings.isEnabled) LilacAccent else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (reminderSettings.isEnabled) reminderSettings.formattedTime else "Lembretes",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (reminderSettings.isEnabled) LilacAccent else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Daily Reminders Bento Card
            item {
                BentoCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showReminderDialog = true }
                        .testTag("card_daily_reminder_settings")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (reminderSettings.isEnabled) PurplePrimary.copy(alpha = 0.2f)
                                        else PurpleDarkSurface
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Alarm,
                                    contentDescription = null,
                                    tint = if (reminderSettings.isEnabled) LilacAccent else TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (reminderSettings.isEnabled) "Lembretes Diários: Ativos (${reminderSettings.formattedTime})"
                                    else "Lembretes Diários Desativados",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (reminderSettings.isEnabled) TextPrimary else TextSecondary
                                )
                                Text(
                                    text = if (reminderSettings.isEnabled)
                                        "${reminderSettings.tone.emoji} ${reminderSettings.daysOfWeek.size} dias por semana programados"
                                    else "Toque para configurar notificações de treino",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                        Text(
                            text = "Configurar",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = PurplePrimary
                        )
                    }
                }
            }

            // Month Navigation Bento Card
            item {
                BentoCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Mês anterior", tint = LilacAccent)
                        }

                        Text(
                            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, ptLocale).replaceFirstChar { it.titlecase(ptLocale) }} ${currentMonth.year}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )

                        IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Próximo mês", tint = LilacAccent)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Weekday headers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB").forEach { day ->
                            Text(
                                text = day,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                modifier = Modifier.width(38.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Month Days Grid
                    val firstDayOfMonth = currentMonth.atDay(1)
                    val daysInMonth = currentMonth.lengthOfMonth()
                    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
                    val totalCells = ((startDayOfWeek + daysInMonth + 6) / 7) * 7

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (row in 0 until (totalCells / 7)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                for (col in 0 until 7) {
                                    val dayIndex = row * 7 + col
                                    val dayNumber = dayIndex - startDayOfWeek + 1
                                    if (dayNumber in 1..daysInMonth) {
                                        val cellDate = currentMonth.atDay(dayNumber)
                                        val cellEpoch = cellDate.toEpochDay()
                                        val isSelected = cellDate == selectedDate
                                        val isToday = cellDate == LocalDate.now()
                                        val hasCompletedWorkout = completedWorkoutDaysSet.contains(cellEpoch)
                                        val hasScheduledWorkout = scheduledWorkoutDaysSet.contains(cellEpoch)
                                        val hasCardio = cardioDaysSet.contains(cellEpoch)

                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) PurpleVibrant
                                                    else if (isToday) PurpleDeepCard
                                                    else Color.Transparent
                                                )
                                                .border(
                                                    width = if (isToday && !isSelected) 1.5.dp else 0.dp,
                                                    color = LilacAccent,
                                                    shape = CircleShape
                                                )
                                                .clickable { selectedDate = cellDate }
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "$dayNumber",
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected || isToday) FontWeight.Black else FontWeight.Normal,
                                                    color = if (isSelected) Color.White else TextPrimary
                                                )

                                                // Dots for sessions
                                                if (hasCompletedWorkout || hasScheduledWorkout || hasCardio) {
                                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                        if (hasCompletedWorkout) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(5.dp)
                                                                    .clip(CircleShape)
                                                                    .background(if (isSelected) Color.White else EmeraldSuccess)
                                                            )
                                                        }
                                                        if (hasScheduledWorkout) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(5.dp)
                                                                    .clip(CircleShape)
                                                                    .background(if (isSelected) Color.White else LilacAccent)
                                                            )
                                                        }
                                                        if (hasCardio) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(5.dp)
                                                                    .clip(CircleShape)
                                                                    .background(if (isSelected) Color.White else LilacSoft)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.size(38.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Calendar Legend: Realizado (verde), Agendado (roxo), Cardio (lilás)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(EmeraldSuccess))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Realizado", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.width(16.dp))

                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(LilacAccent))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agendado", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.width(16.dp))

                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(LilacSoft))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cardio", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Selected Date Summary & Action Bar
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = DateUtils.formatEpochDayWithWeekday(selectedEpochDay),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            if (selectedEpochDay == DateUtils.todayEpochDay()) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldSuccess.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, EmeraldSuccess.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "HOJE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = EmeraldSuccess,
                                        letterSpacing = 0.5.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Action Buttons Row: symmetrical, equal height, no text wrapping
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            onClick = { showRetroactiveDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldSuccess.copy(alpha = 0.14f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.55f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_log_retroactive_workout_agenda")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Já Treinei",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }

                        Surface(
                            onClick = { showScheduleDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            color = PurplePrimary,
                            border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_schedule_workout_agenda")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Agendar",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }

            // List of Workouts on Selected Day
            if (workoutsForSelectedDay.isEmpty() && cardiosForSelectedDay.isEmpty()) {
                item {
                    BentoCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(PurpleDeepCard)
                                    .border(1.dp, GlassBorderSubtle, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = LilacSoft, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Nenhum registro para este dia",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Nenhum treino agendado ou realizado neste dia.\nUse os botões acima para registrar ou agendar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(workoutsForSelectedDay, key = { it.id }) { session ->
                    AgendaWorkoutItemCard(
                        session = session,
                        templates = templates,
                        onStart = { template, location, sessionId, dateEpoch ->
                            onStartScheduledWorkout(template, location, sessionId, dateEpoch)
                        },
                        onDelete = { viewModel.deleteWorkoutSession(session.id) },
                        onViewAiFeedback = { feedback ->
                            selectedSessionAiFeedback = feedback
                            showAiDialog = true
                        },
                        onAnalyzeWithAi = {
                            userProfile?.let { prof ->
                                viewModel.evaluateSessionWithAI(session, prof)
                                showAiDialog = true
                            }
                        }
                    )
                }

                items(cardiosForSelectedDay, key = { it.id }) { cardio ->
                    BentoCard(modifier = Modifier.fillMaxWidth()) {
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
                                        .background(PurpleDarkSurface)
                                        .border(1.dp, LilacAccent.copy(alpha = 0.4f), CircleShape)
                                ) {
                                    Icon(
                                        Icons.Default.DirectionsBike,
                                        contentDescription = null,
                                        tint = LilacAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = cardio.type.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "📍 ${cardio.location} • ${cardio.durationMinutes} min",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Text(
                                text = "~${cardio.caloriesBurned} kcal",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black,
                                color = EmeraldSuccess
                            )
                        }
                    }
                }
            }
        }
    }

    // AI Evaluation Dialog
    if (showAiDialog) {
        val evaluationToDisplay = if (selectedSessionAiFeedback.isNullOrBlank()) {
            lastAiEvaluation
        } else {
            selectedSessionAiFeedback
        }
        AIEvaluationDialog(
            title = "Avaliação de Calorias & Desempenho",
            evaluationText = evaluationToDisplay,
            isLoading = isAiEvaluating,
            onDismiss = {
                showAiDialog = false
                selectedSessionAiFeedback = null
            }
        )
    }

    // Workout Reminder Configuration Dialog
    if (showReminderDialog) {
        WorkoutReminderDialog(
            initialSettings = reminderSettings,
            onSaveSettings = { newSettings ->
                viewModel.updateReminderSettings(newSettings)
            },
            onTestNotification = {
                viewModel.triggerTestReminderNotification()
            },
            onDismiss = { showReminderDialog = false }
        )
    }

    // Retroactive Workout Dialog
    if (showRetroactiveDialog) {
        RetroactiveWorkoutDialog(
            initialEpochDay = selectedEpochDay,
            templates = templates,
            onDismiss = { showRetroactiveDialog = false },
            onConfirm = { title, epochDay, muscleGroups, durationMinutes, location, rpe, notes, templateId ->
                viewModel.logRetroactiveWorkout(
                    title = title,
                    epochDay = epochDay,
                    muscleGroups = muscleGroups,
                    durationMinutes = durationMinutes,
                    location = location,
                    rpe = rpe,
                    notes = notes,
                    templateId = templateId
                )
                showRetroactiveDialog = false
            }
        )
    }

    // Schedule Dialog
    if (showScheduleDialog) {
        ScheduleWorkoutDialog(
            selectedDateEpoch = selectedEpochDay,
            templates = templates,
            onDismiss = { showScheduleDialog = false },
            onConfirm = { title, location, templateId, coordinates ->
                viewModel.scheduleWorkout(
                    title = title,
                    epochDay = selectedEpochDay,
                    location = location,
                    templateId = templateId,
                    latitude = coordinates?.latitude,
                    longitude = coordinates?.longitude,
                    locationAddress = coordinates?.formattedAddress ?: coordinates?.locality
                )
                showScheduleDialog = false
            }
        )
    }

    // Full Training History BottomSheet
    if (showFullHistorySheet) {
        FullWorkoutHistorySheet(
            workoutSessions = workoutSessions,
            cardioSessions = cardioSessions,
            onDismiss = { showFullHistorySheet = false },
            onDeleteWorkoutSession = { sessionId -> viewModel.deleteWorkoutSession(sessionId) },
            onDeleteCardioSession = { cardioId -> viewModel.deleteCardioSession(cardioId) },
            onViewAiFeedback = { aiFeedback ->
                selectedSessionAiFeedback = aiFeedback
                showAiDialog = true
            }
        )
    }
}

@Composable
fun AgendaWorkoutItemCard(
    session: WorkoutSession,
    templates: List<WorkoutTemplate>,
    onStart: (WorkoutTemplate, String, Long, Long) -> Unit,
    onDelete: () -> Unit,
    onViewAiFeedback: (String) -> Unit = {},
    onAnalyzeWithAi: () -> Unit = {}
) {
    val isCompleted = session.status == SessionStatus.COMPLETED

    BentoCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_agenda_session_${session.id}")
    ) {
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
                        .background(if (isCompleted) EmeraldSuccess.copy(alpha = 0.15f) else PurpleDarkSurface)
                        .border(1.dp, if (isCompleted) EmeraldSuccess else LilacAccent.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = if (isCompleted) EmeraldSuccess else LilacAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = session.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = LilacAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = session.location,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }
                    if (session.latitude != null && session.longitude != null) {
                        Text(
                            text = "📍 GPS: ${String.format(java.util.Locale.US, "%.4f, %.4f", session.latitude, session.longitude)}",
                            fontSize = 10.sp,
                            color = EmeraldSuccess,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isCompleted) EmeraldSuccess.copy(alpha = 0.15f) else PurpleDarkSurface)
                    .border(1.dp, if (isCompleted) EmeraldSuccess.copy(alpha = 0.4f) else LilacAccent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isCompleted) "Realizado" else "Agendado",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) EmeraldSuccess else LilacAccent
                )
            }
        }

        if (isCompleted) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⏱️ ${session.durationSeconds / 60} min • Volume: ${session.totalWeightLiftedKg.toInt()}kg",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Text(
                    text = "~${session.estimatedCalories} kcal",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Black,
                    color = EmeraldSuccess
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            if (session.notes.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PurpleDarkSurface,
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, GlassBorderSubtle),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "💪 ${session.notes}",
                        fontSize = 11.sp,
                        color = LilacAccent,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (session.aiCaloricEvaluation.isNotBlank()) {
                        Surface(
                            onClick = { onViewAiFeedback(session.aiCaloricEvaluation) },
                            shape = RoundedCornerShape(10.dp),
                            color = PurpleDarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = LilacAccent
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Avaliação IA",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LilacAccent
                                )
                            }
                        }
                    } else {
                        Surface(
                            onClick = onAnalyzeWithAi,
                            shape = RoundedCornerShape(10.dp),
                            color = PurpleDarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = LilacSoft
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Calcular Calorias & IA",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LilacSoft
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir Registro", tint = RedDestructive.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                }
            }
        } else {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Remover", tint = RedDestructive)
                }

                val template = templates.find { it.id == session.templateId } ?: templates.firstOrNull()
                if (template != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(GradientAction)
                            .clickable { onStart(template, session.location, session.id, session.dateEpochDay) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Iniciar Agora", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleWorkoutDialog(
    selectedDateEpoch: Long,
    templates: List<WorkoutTemplate>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, location: String, templateId: Long?, coordinates: com.example.util.WorkoutLocationCoordinates?) -> Unit
) {
    var selectedTemplate by remember { mutableStateOf(templates.firstOrNull()) }
    var location by remember { mutableStateOf("Smart Fit Paulista") }
    var capturedCoordinates by remember { mutableStateOf<com.example.util.WorkoutLocationCoordinates?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Agendar para ${DateUtils.formatEpochDayShort(selectedDateEpoch)}",
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Selecione o Modelo de Treino:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    templates.forEach { tmpl ->
                        val isSelected = selectedTemplate?.id == tmpl.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTemplate = tmpl },
                            label = { Text(tmpl.title, fontSize = 11.sp) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PurpleDeepCard,
                                selectedLabelColor = LilacAccent,
                                containerColor = PurpleDarkSurface,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                LocationSelector(
                    selectedLocation = location,
                    onLocationSelected = { location = it },
                    capturedCoordinates = capturedCoordinates,
                    onCoordinatesCaptured = { capturedCoordinates = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val title = selectedTemplate?.title ?: "Treino de Musculação"
                    onConfirm(title, location, selectedTemplate?.id, capturedCoordinates)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_confirm_schedule_dialog")
            ) {
                Text("Agendar", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextMuted)
            }
        },
        containerColor = PurpleDarkSurface,
        shape = RoundedCornerShape(24.dp)
    )
}
