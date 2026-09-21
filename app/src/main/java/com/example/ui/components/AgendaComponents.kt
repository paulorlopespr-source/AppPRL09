package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

// ==========================================
// 1. Modelos de Apresentação (AgendaUiState)
// ==========================================

enum class AppointmentType(
    val title: String,
    val barColor: Color,
    val containerColor: Color,
    val iconTint: Color,
    val icon: ImageVector
) {
    STRENGTH(
        title = "Treino de Força",
        barColor = Color(0xFF22C55E),
        containerColor = Color(0xFF073B2E),
        iconTint = Color(0xFF22C55E),
        icon = Icons.Default.FitnessCenter
    ),
    CARDIO(
        title = "Cardio",
        barColor = Color(0xFF38BDF8),
        containerColor = Color(0xFF0C2D48),
        iconTint = Color(0xFF38BDF8),
        icon = Icons.Default.DirectionsRun
    ),
    MEAL(
        title = "Refeição",
        barColor = Color(0xFFF97316),
        containerColor = Color(0xFF451A03),
        iconTint = Color(0xFFF97316),
        icon = Icons.Default.Restaurant
    ),
    REST(
        title = "Descanso",
        barColor = Color(0xFFA855F7),
        containerColor = Color(0xFF2E1065),
        iconTint = Color(0xFFA855F7),
        icon = Icons.Default.Bedtime
    )
}

data class AgendaAppointment(
    val id: String,
    val type: AppointmentType,
    val startTime: String,
    val endTime: String? = null,
    val title: String,
    val subtitle: String,
    val isCompleted: Boolean,
    val workoutSessionId: Long? = null,
    val cardioSessionId: Long? = null,
    val mealLogId: Long? = null,
    val customAppointmentId: String? = null,
    val rawNotes: String? = null
)

enum class DayDotColor(val color: Color) {
    NONE(Color(0xFF3B2D54)),
    GREEN(Color(0xFF22C55E)),
    BLUE(Color(0xFF38BDF8)),
    ORANGE(Color(0xFFF97316)),
    PURPLE(Color(0xFFA855F7)),
    WHITE(Color.White)
}

data class DayScheduleStatus(
    val date: LocalDate,
    val dotColor: DayDotColor,
    val hasCompletedWorkout: Boolean = false,
    val hasCardio: Boolean = false,
    val hasMeal: Boolean = false,
    val hasScheduledWorkout: Boolean = false,
    val totalItems: Int = 0
)

data class AgendaUiState(
    val selectedMonth: YearMonth,
    val selectedDate: LocalDate,
    val dayStatusMap: Map<LocalDate, DayScheduleStatus>,
    val appointments: List<AgendaAppointment>,
    val isToday: Boolean,
    val dayTitle: String,
    val daySubtitle: String
)

// ==========================================
// 2. AgendaHeader
// ==========================================

@Composable
fun AgendaHeader(
    selectedMonth: YearMonth,
    onOpenDrawer: () -> Unit,
    onOpenMonthPicker: () -> Unit,
    onOpenReminders: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ptLocale = remember { Locale("pt", "BR") }
    val monthName = remember(selectedMonth) {
        val raw = selectedMonth.month.getDisplayName(TextStyle.FULL, ptLocale)
        raw.replaceFirstChar { if (it.isLowerCase()) it.titlecase(ptLocale) else it.toString() }
    }
    val formattedMonthYear = "$monthName ${selectedMonth.year}"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hambúrguer + Título + Subtítulo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = onOpenDrawer,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("btn_agenda_drawer")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu Lateral",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column {
                Text(
                    text = "Agenda",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Organize seus treinos, mantenha o foco.",
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8),
                    maxLines = 1
                )
            }
        }

        // Lado Direito: Seletor de Mês + Sino de Notificações
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                onClick = onOpenMonthPicker,
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF15111F),
                border = BorderStroke(1.dp, Color(0xFF2E204A)),
                modifier = Modifier.testTag("btn_agenda_month_picker")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Selecionar Mês",
                        tint = Color(0xFFC4B5FD),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formattedMonthYear,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFFC4B5FD),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            IconButton(
                onClick = onOpenReminders,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("btn_agenda_notifications")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Lembretes de Treino",
                        tint = Color(0xFFC4B5FD),
                        modifier = Modifier.size(24.dp)
                    )
                    // Pequeno ponto de badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFA855F7))
                    )
                }
            }
        }
    }
}

// ==========================================
// 3. WeekCalendarStrip & DayChip
// ==========================================

@Composable
fun WeekCalendarStrip(
    selectedDate: LocalDate,
    onSelectDate: (LocalDate) -> Unit,
    dayStatusMap: Map<LocalDate, DayScheduleStatus>,
    modifier: Modifier = Modifier,
    onPreviousWeek: () -> Unit = {},
    onNextWeek: () -> Unit = {}
) {
    // Calcula os 7 dias da semana contendo selectedDate, começando no Domingo (conforme DOM 13 na imagem)
    val dayOfWeek = selectedDate.dayOfWeek.value % 7 // Domingo = 0, Segunda = 1 ... Sábado = 6
    val sunday = selectedDate.minusDays(dayOfWeek.toLong())
    val weekDays = remember(selectedDate) {
        (0..6).map { sunday.plusDays(it.toLong()) }
    }

    val weekdayLabels = listOf("DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_week_calendar_strip"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15111F)),
        border = BorderStroke(1.dp, Color(0xFF7C3AED).copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            weekDays.forEachIndexed { index, date ->
                val isSelected = date == selectedDate
                val status = dayStatusMap[date]
                val dotColor = if (isSelected) DayDotColor.WHITE else (status?.dotColor ?: DayDotColor.NONE)

                DayChip(
                    dayLabel = weekdayLabels[index],
                    dayNumber = date.dayOfMonth,
                    isSelected = isSelected,
                    dotColor = dotColor,
                    onClick = { onSelectDate(date) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DayChip(
    dayLabel: String,
    dayNumber: Int,
    isSelected: Boolean,
    dotColor: DayDotColor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFF7C3AED) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayLabel,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$dayNumber",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Dot indicador
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(dotColor.color)
            )
        }
    }
}

// ==========================================
// 4. DayTitleSection
// ==========================================

@Composable
fun DayTitleSection(
    dayTitle: String,
    daySubtitle: String,
    onViewWeek: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = dayTitle,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = daySubtitle,
                fontSize = 14.sp,
                color = Color(0xFF94A3B8)
            )
        }

        Surface(
            onClick = onViewWeek,
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF15111F),
            border = BorderStroke(1.dp, Color(0xFF7C3AED).copy(alpha = 0.4f)),
            modifier = Modifier.testTag("btn_view_week_expanded")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ver semana",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFC4B5FD)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFFC4B5FD),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ==========================================
// 5. AppointmentsList & AppointmentCard
// ==========================================

@Composable
fun AppointmentsList(
    appointments: List<AgendaAppointment>,
    onToggleStatus: (AgendaAppointment) -> Unit,
    onSelectAppointment: (AgendaAppointment) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        appointments.forEach { item ->
            AppointmentCard(
                appointment = item,
                onToggleStatus = { onToggleStatus(item) },
                onClick = { onSelectAppointment(item) }
            )
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: AgendaAppointment,
    onToggleStatus: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_appointment_${appointment.id}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15111F)),
        border = BorderStroke(1.dp, Color(0xFF2E204A))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
        ) {
            // Barra vertical colorida à esquerda (4dp)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .background(appointment.type.barColor)
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Horário (largura fixa alinhada)
                Column(
                    modifier = Modifier.width(52.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = appointment.startTime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (!appointment.endTime.isNullOrBlank()) {
                        Text(
                            text = appointment.endTime,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Ícone em container arredondado com fundo temático
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(appointment.type.containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = appointment.type.icon,
                        contentDescription = appointment.type.title,
                        tint = appointment.type.iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Título e Subtítulo
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = appointment.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = appointment.subtitle,
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8),
                        maxLines = 1
                    )
                }

                // Indicador de status (círculo verde com check ou outline vazio)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable(onClick = onToggleStatus)
                        .testTag("toggle_status_${appointment.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (appointment.isCompleted) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Concluído",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(1.5.dp, Color(0xFF52525B), CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Chevron ">" ao final
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF71717A),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ==========================================
// 6. AddAppointmentButton
// ==========================================

@Composable
fun AddAppointmentButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("btn_add_appointment"),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF7C3AED).copy(alpha = 0.15f),
        border = BorderStroke(1.dp, Color(0xFF7C3AED).copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF7C3AED)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Adicionar compromisso",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC4B5FD)
            )
        }
    }
}

// ==========================================
// 7. MotivationCard (Com paisagem artística)
// ==========================================

@Composable
fun AgendaMotivationCard(
    quote: String = "Disciplina hoje,\nresultados amanhã.",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_agenda_motivation"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15111F)),
        border = BorderStroke(1.dp, Color(0xFF2E204A))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.img_agenda_mountain),
                contentDescription = null,
                // O asset oficial é vertical; o recorte central evita exibir
                // apenas a área escura inferior da paisagem.
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "“",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFA78BFA),
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = quote,
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFF1F5F9),
                    lineHeight = 23.sp
                )
            }
        }
    }
}

@Composable
fun AgendaMountainSunsetCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Gradiente de fundo base escuro
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF15111F),
                    Color(0xFF191128),
                    Color(0xFF2A1542)
                )
            )
        )

        // Disco da Lua / Sol crepuscular roxo-lilás suave
        val moonCenter = Offset(w * 0.85f, h * 0.40f)
        val moonRadius = h * 0.28f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFF5D0FE).copy(alpha = 0.85f),
                    Color(0xFFE879F9).copy(alpha = 0.50f),
                    Color(0xFFA855F7).copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = moonCenter,
                radius = moonRadius * 1.5f
            ),
            radius = moonRadius * 1.5f,
            center = moonCenter
        )

        drawCircle(
            color = Color(0xFFF5D0FE).copy(alpha = 0.90f),
            radius = moonRadius,
            center = moonCenter
        )

        // Camada 1 de Montanhas distantes (mais escuras/translúcidas)
        val backPeaks = Path().apply {
            moveTo(w * 0.50f, h)
            lineTo(w * 0.62f, h * 0.55f)
            lineTo(w * 0.72f, h * 0.35f)
            lineTo(w * 0.82f, h * 0.52f)
            lineTo(w * 0.92f, h * 0.32f)
            lineTo(w, h * 0.45f)
            lineTo(w, h)
            close()
        }
        drawPath(
            path = backPeaks,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF381B5E).copy(alpha = 0.60f),
                    Color(0xFF15111F).copy(alpha = 0.90f)
                ),
                startY = h * 0.32f,
                endY = h
            )
        )

        // Camada 2 de Montanhas frontais
        val frontPeaks = Path().apply {
            moveTo(w * 0.58f, h)
            lineTo(w * 0.70f, h * 0.62f)
            lineTo(w * 0.79f, h * 0.42f)
            lineTo(w * 0.88f, h * 0.60f)
            lineTo(w * 0.96f, h * 0.38f)
            lineTo(w, h * 0.48f)
            lineTo(w, h)
            close()
        }
        drawPath(
            path = frontPeaks,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1B0F2E),
                    Color(0xFF120B1D)
                ),
                startY = h * 0.38f,
                endY = h
            )
        )

        // Contorno de crista sutil
        val ridge = Path().apply {
            moveTo(w * 0.58f, h)
            lineTo(w * 0.70f, h * 0.62f)
            lineTo(w * 0.79f, h * 0.42f)
            lineTo(w * 0.88f, h * 0.60f)
            lineTo(w * 0.96f, h * 0.38f)
            lineTo(w, h * 0.48f)
        }
        drawPath(
            path = ridge,
            color = Color(0xFFA855F7).copy(alpha = 0.35f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
        )
    }
}

// ==========================================
// 8. Diálogos: Adicionar Compromisso & Seletor de Mês
// ==========================================

@Composable
fun AddAppointmentDialog(
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (type: AppointmentType, startTime: String, endTime: String?, title: String, subtitle: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(AppointmentType.STRENGTH) }
    var startTime by remember { mutableStateOf("06:00") }
    var endTime by remember { mutableStateOf("07:00") }
    var title by remember { mutableStateOf("Treino de Força") }
    var subtitle by remember { mutableStateOf("Peito e Tríceps") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Novo Compromisso",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Tipo:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppointmentType.values().forEach { type ->
                        val isSelected = selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedType = type
                                when (type) {
                                    AppointmentType.STRENGTH -> {
                                        title = "Treino de Força"
                                        subtitle = "Peito e Tríceps"
                                        startTime = "06:00"
                                        endTime = "07:00"
                                    }
                                    AppointmentType.CARDIO -> {
                                        title = "Cardio"
                                        subtitle = "30 minutos"
                                        startTime = "12:00"
                                        endTime = "12:30"
                                    }
                                    AppointmentType.MEAL -> {
                                        title = "Refeição"
                                        subtitle = "Pós-treino"
                                        startTime = "19:00"
                                        endTime = "19:30"
                                    }
                                    AppointmentType.REST -> {
                                        title = "Descanso"
                                        subtitle = "Hora de recuperar"
                                        startTime = "21:00"
                                        endTime = ""
                                    }
                                }
                            },
                            label = { Text(type.title, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF7C3AED),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E1530),
                                labelColor = Color(0xFF94A3B8)
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFA78BFA),
                        unfocusedBorderColor = Color(0xFF3B2D54)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Subtítulo / Descrição") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFA78BFA),
                        unfocusedBorderColor = Color(0xFF3B2D54)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Início (HH:mm)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFA78BFA),
                            unfocusedBorderColor = Color(0xFF3B2D54)
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("Fim (opcional)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFA78BFA),
                            unfocusedBorderColor = Color(0xFF3B2D54)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedType, startTime, endTime.ifBlank { null }, title, subtitle)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_confirm_add_appointment")
            ) {
                Text("Adicionar", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF94A3B8))
            }
        },
        containerColor = Color(0xFF15111F),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun AgendaMonthPickerDialog(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onMonthChange: (YearMonth) -> Unit,
    onDateSelect: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val ptLocale = remember { Locale("pt", "BR") }
    val monthName = remember(currentMonth) {
        currentMonth.month.getDisplayName(TextStyle.FULL, ptLocale).replaceFirstChar { it.titlecase(ptLocale) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Mês anterior", tint = Color(0xFFA78BFA))
                }
                Text(
                    text = "$monthName ${currentMonth.year}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Próximo mês", tint = Color(0xFFA78BFA))
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Dias da semana
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("D", "S", "T", "Q", "Q", "S", "S").forEach { d ->
                        Text(
                            text = d,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.width(36.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val firstDay = currentMonth.atDay(1)
                val daysInMonth = currentMonth.lengthOfMonth()
                val startDayOfWeek = firstDay.dayOfWeek.value % 7
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
                                    val isSelected = cellDate == selectedDate
                                    val isToday = cellDate == LocalDate.now()

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) Color(0xFF7C3AED)
                                                else if (isToday) Color(0xFF2E204A)
                                                else Color.Transparent
                                            )
                                            .clickable {
                                                onDateSelect(cellDate)
                                                onDismiss()
                                            }
                                    ) {
                                        Text(
                                            text = "$dayNumber",
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else Color(0xFFF1F5F9)
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(36.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar", color = Color(0xFFA78BFA), fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFF15111F),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun AppointmentDetailDialog(
    appointment: AgendaAppointment,
    onDismiss: () -> Unit,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
    onStartWorkout: (() -> Unit)? = null,
    onViewAiEvaluation: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(appointment.type.containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = appointment.type.icon,
                        contentDescription = null,
                        tint = appointment.type.iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = appointment.title,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                    Text(
                        text = appointment.type.title,
                        fontSize = 12.sp,
                        color = appointment.type.barColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Horário:", color = Color(0xFF94A3B8), fontSize = 13.sp)
                    Text(
                        text = if (!appointment.endTime.isNullOrBlank()) "${appointment.startTime} - ${appointment.endTime}" else appointment.startTime,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Status:", color = Color(0xFF94A3B8), fontSize = 13.sp)
                    Text(
                        text = if (appointment.isCompleted) "Concluído ✓" else "Pendente",
                        fontWeight = FontWeight.Bold,
                        color = if (appointment.isCompleted) Color(0xFF22C55E) else Color(0xFFF97316),
                        fontSize = 14.sp
                    )
                }

                if (appointment.subtitle.isNotBlank()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Descrição / Detalhes:", color = Color(0xFF94A3B8), fontSize = 13.sp)
                        Text(
                            text = appointment.subtitle,
                            color = Color(0xFFE2E8F0),
                            fontSize = 14.sp
                        )
                    }
                }

                if (!appointment.rawNotes.isNullOrBlank()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Observações:", color = Color(0xFF94A3B8), fontSize = 13.sp)
                        Text(
                            text = appointment.rawNotes,
                            color = Color(0xFFC4B5FD),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Ações
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (onStartWorkout != null) {
                        Button(
                            onClick = {
                                onDismiss()
                                onStartWorkout()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Iniciar Treino Agora", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (onViewAiEvaluation != null) {
                        Button(
                            onClick = {
                                onDismiss()
                                onViewAiEvaluation()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF064E3B)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ver Avaliação IA ⚡", fontWeight = FontWeight.Bold, color = Color(0xFF6EE7B7))
                        }
                    }

                    Button(
                        onClick = {
                            onToggleStatus()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (appointment.isCompleted) Color(0xFF332042) else Color(0xFF166534)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (appointment.isCompleted) "Marcar como Pendente" else "Marcar como Concluído ✓",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    TextButton(
                        onClick = {
                            onDelete()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Excluir Compromisso", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar", color = Color(0xFFA78BFA), fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFF15111F),
        shape = RoundedCornerShape(20.dp)
    )
}
