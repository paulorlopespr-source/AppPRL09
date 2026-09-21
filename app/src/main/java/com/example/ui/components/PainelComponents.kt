package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.AppDestination
import java.util.Locale

/**
 * Representação de estado completa para a tela Painel
 */
data class PainelUiState(
    val selectedDateText: String = "Hoje, 16 Set",
    val selectedEpochDay: Long = 0L,
    val greetingHeadline: String = "Bom dia!",
    val greetingSubtitle: String = "Disciplina hoje, resultados amanhã.",
    val workoutsDone: Int = 2,
    val workoutsGoal: Int = 5,
    val caloriesKcal: Int = 412,
    val distanceKm: Double = 3.2,
    val readinessScore: Int = 78,
    val weeklyBarData: List<DayBarData> = emptyList(),
    val motivationQuote: String = "Pequenas ações diárias\nconstroem grandes resultados.",
    val todayFocusTitle: String = "Seu foco hoje",
    val todayFocusDescription: String = "Manter a consistência dos treinos.",
    val todayFocusProgress: Int = 78,
    val hasSufficientData: Boolean = true
)

data class DayBarData(
    val dayLabel: String,
    val dateEpochDay: Long,
    val volumeKg: Double,
    val hasWorkout: Boolean,
    val intensityRatio: Float
)

/**
 * 1. PainelHeader
 * Ícone de hambúrguer à esquerda, título "Painel" + subtítulo,
 * e seletor de data à direita (ícone de calendário + data + chevron).
 */
@Composable
fun PainelHeader(
    selectedDateText: String,
    onOpenDrawer: () -> Unit,
    onOpenDatePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("header_painel"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onOpenDrawer,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("btn_painel_menu")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menu lateral",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "Painel",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Seu progresso em um só lugar.",
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Surface(
            onClick = onOpenDatePicker,
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF15111F),
            border = BorderStroke(1.dp, Color(0xFF2E204A)),
            modifier = Modifier.testTag("btn_painel_date_selector")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Calendário",
                    tint = Color(0xFFA78BFA),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedDateText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * 2. GreetingCard
 * Card com background atmosférico (montanhas + lua/pôr do sol roxo)
 * Saudação dinâmica ("Bom dia!" / "Boa tarde!" / "Boa noite!") + subtítulo.
 */
@Composable
fun GreetingCard(
    headline: String = "Bom dia!",
    subtitle: String = "Disciplina hoje, resultados amanhã.",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_painel_greeting"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF130E20)),
        border = BorderStroke(1.dp, Color(0xFF2E204A).copy(alpha = 0.6f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(135.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.img_painel_mountain),
                contentDescription = null,
                // O asset oficial é vertical; o recorte central mantém a
                // montanha e a lua visíveis no banner horizontal.
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            )

            // Sombra suave no texto para contraste impecável
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0F0B1A).copy(alpha = 0.85f),
                                Color(0xFF0F0B1A).copy(alpha = 0.45f),
                                Color.Transparent
                            ),
                            startX = 0f,
                            endX = 500f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = headline,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color(0xFFCBD5E1)
                )
            }
        }
    }
}

/**
 * Desenho de fundo em Canvas para o GreetingCard:
 * Céu crepuscular roxo, sol/lua nas montanhas e silhuetas de picos.
 */
@Composable
fun GreetingMountainBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Céu crepuscular escuro
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF24133F),
                    Color(0xFF1A0E31),
                    Color(0xFF0F071C)
                )
            )
        )

        // 2. Sol/Lua roxo-magenta suave no quadrante superior direito
        val orbCenter = Offset(w * 0.82f, h * 0.38f)
        val orbRadius = w * 0.12f

        // Brilho difuso externo
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFC084FC).copy(alpha = 0.45f),
                    Color(0xFF8B5CF6).copy(alpha = 0.20f),
                    Color.Transparent
                ),
                center = orbCenter,
                radius = orbRadius * 2.8f
            ),
            center = orbCenter,
            radius = orbRadius * 2.8f
        )

        // Disco celestial
        drawCircle(
            color = Color(0xFFE9D5FF).copy(alpha = 0.70f),
            center = orbCenter,
            radius = orbRadius
        )

        // 3. Montanhas distantes (roxo intermediário)
        val distant = Path().apply {
            moveTo(w * 0.35f, h)
            lineTo(w * 0.50f, h * 0.52f)
            lineTo(w * 0.64f, h * 0.65f)
            lineTo(w * 0.78f, h * 0.42f)
            lineTo(w * 0.90f, h * 0.58f)
            lineTo(w, h * 0.48f)
            lineTo(w, h)
            close()
        }
        drawPath(
            path = distant,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF4C1D95).copy(alpha = 0.55f),
                    Color(0xFF2E1065).copy(alpha = 0.85f)
                ),
                startY = h * 0.42f,
                endY = h
            )
        )

        // 4. Montanhas do primeiro plano (roxo escuro profundo)
        val foreground = Path().apply {
            moveTo(w * 0.42f, h)
            lineTo(w * 0.60f, h * 0.68f)
            lineTo(w * 0.72f, h * 0.36f)
            lineTo(w * 0.86f, h * 0.68f)
            lineTo(w, h * 0.52f)
            lineTo(w, h)
            close()
        }
        drawPath(
            path = foreground,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1B0F30),
                    Color(0xFF0F081D)
                ),
                startY = h * 0.36f,
                endY = h
            )
        )

        // Linha de contorno iluminada no pico principal
        val ridgeLine = Path().apply {
            moveTo(w * 0.42f, h)
            lineTo(w * 0.60f, h * 0.68f)
            lineTo(w * 0.72f, h * 0.36f)
            lineTo(w * 0.86f, h * 0.68f)
            lineTo(w, h * 0.52f)
        }
        drawPath(
            path = ridgeLine,
            color = Color(0xFFA855F7).copy(alpha = 0.35f),
            style = Stroke(width = 2f)
        )
    }
}

/**
 * 3. KeyMetricsGrid
 * 4 cards em Row com peso igual:
 * Card 1: Treinos (halteres, roxo)
 * Card 2: kcal (fogo, laranja)
 * Card 3: km (corrida, azul)
 * Card 4: Readiness (troféu, verde)
 */
@Composable
fun KeyMetricsGrid(
    workoutsDone: Int,
    workoutsGoal: Int,
    caloriesKcal: Int,
    distanceKm: Double,
    readinessScore: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("grid_key_metrics"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Card 1: Treinos
        val workoutProgress = if (workoutsGoal > 0) {
            workoutsDone.toFloat() / workoutsGoal
        } else {
            0.4f
        }
        KeyMetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.FitnessCenter,
            iconTint = Color(0xFFA78BFA),
            value = "$workoutsDone / $workoutsGoal",
            label = "Treinos",
            progress = workoutProgress,
            barColor = Color(0xFF8B5CF6),
            trackColor = Color(0xFF281F3B),
            testTag = "card_metric_treinos"
        )

        // Card 2: kcal
        val calProgress = (caloriesKcal / 600f).coerceIn(0.1f, 1f)
        KeyMetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.LocalFireDepartment,
            iconTint = Color(0xFFF97316),
            value = "$caloriesKcal",
            label = "kcal",
            progress = calProgress,
            barColor = Color(0xFFF97316),
            trackColor = Color(0xFF331D18),
            testTag = "card_metric_kcal"
        )

        // Card 3: km
        val kmText = String.format(Locale("pt", "BR"), "%.1f", distanceKm)
        val kmProgress = (distanceKm.toFloat() / 5f).coerceIn(0.1f, 1f)
        KeyMetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.DirectionsRun,
            iconTint = Color(0xFF38BDF8),
            value = kmText,
            label = "km",
            progress = kmProgress,
            barColor = Color(0xFF0284C7),
            trackColor = Color(0xFF16253B),
            testTag = "card_metric_km"
        )

        // Card 4: Readiness
        val readinessProgress = (readinessScore / 100f).coerceIn(0.1f, 1f)
        KeyMetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.EmojiEvents,
            iconTint = Color(0xFFFBBF24),
            value = "$readinessScore",
            label = "Readiness",
            progress = readinessProgress,
            barColor = Color(0xFF22C55E),
            trackColor = Color(0xFF173024),
            testTag = "card_metric_readiness"
        )
    }
}

/**
 * Card individual de métrica principal com mini barra horizontal
 */
@Composable
fun KeyMetricCard(
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String,
    progress: Float,
    barColor: Color,
    trackColor: Color,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Card(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15111F)),
        border = BorderStroke(1.dp, Color(0xFF2E204A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(10.dp))
            // Mini barra horizontal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(trackColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress.coerceIn(0.08f, 1f))
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(barColor)
                )
            }
        }
    }
}

/**
 * 4. WeeklyProgressChart
 * Gráfico de 7 barras verticais (S T Q Q S S D)
 * Com gradiente roxo/lilás nos dias com treino e cinza escuro nos demais.
 */
@Composable
fun WeeklyProgressChart(
    dayBars: List<DayBarData>,
    onVerMais: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_weekly_progress_chart"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15111F)),
        border = BorderStroke(1.dp, Color(0xFF2E204A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header do gráfico
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = Color(0xFFA78BFA),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Seu progresso da semana",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier
                        .clickable { onVerMais() }
                        .padding(vertical = 4.dp)
                        .testTag("btn_chart_ver_mais"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ver mais",
                        fontSize = 13.sp,
                        color = Color(0xFFA78BFA)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFFA78BFA),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 7 Barras Verticais
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                dayBars.forEachIndexed { index, day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.testTag("weekly_progress_bar_$index")
                    ) {
                        Box(
                            modifier = Modifier
                                .height(95.dp)
                                .width(24.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            if (day.hasWorkout) {
                                val barHeight = (95 * day.intensityRatio).coerceIn(28f, 95f).dp
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(barHeight)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    Color(0xFFC084FC),
                                                    Color(0xFF7C3AED)
                                                )
                                            )
                                        )
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(18.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF251D36))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = day.dayLabel,
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * 6. TodayFocusCard ("Seu foco hoje")
 * Anel circular de prontidão verde grosso com "78 / 100" no centro à esquerda,
 * ícone de alvo, recomendação e botão pill com gradiente roxo à direita.
 */
@Composable
fun TodayFocusCard(
    score: Int = 78,
    maxScore: Int = 100,
    title: String = "Seu foco hoje",
    description: String = "Manter a consistência dos treinos.",
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_today_focus"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15111F)),
        border = BorderStroke(1.dp, Color(0xFF2E204A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lado Esquerdo: CircularProgressIndicator customizado
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .testTag("ring_focus_score"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeW = 8.dp.toPx()
                    val paddingOffset = strokeW / 2f
                    val arcSize = androidx.compose.ui.geometry.Size(
                        size.width - strokeW,
                        size.height - strokeW
                    )

                    // Track circular escuro
                    drawArc(
                        color = Color(0xFF1E293B),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(paddingOffset, paddingOffset),
                        size = arcSize,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )

                    // Arco de progresso verde
                    val sweep = (score.toFloat() / maxScore.coerceAtLeast(1)).coerceIn(0f, 1f) * 360f
                    drawArc(
                        color = Color(0xFF22C55E),
                        startAngle = -90f,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = Offset(paddingOffset, paddingOffset),
                        size = arcSize,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$score",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "/ $maxScore",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Lado Direito: Alvo, texto e botão
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrackChanges,
                        contentDescription = null,
                        tint = Color(0xFFA855F7),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Botão Pill com gradiente roxo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF7C3AED),
                                    Color(0xFFA855F7)
                                )
                            )
                        )
                        .clickable { onActionClick() }
                        .padding(horizontal = 16.dp)
                        .testTag("btn_focus_action"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ver treinos de hoje",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 7. MainBottomNavigation
 * Barra inferior fixa com 5 destinos: Início, Treinos, Painel (ativo), Jornada, Agenda.
 * Roxo suave no item ativo e linha indicadora inferior.
 */
@Composable
fun MainBottomNavigation(
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        AppDestination.HOME,
        AppDestination.WORKOUTS,
        AppDestination.DASHBOARD,
        AppDestination.JOURNEY,
        AppDestination.AGENDA
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Black.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, Color(0xFF1E1530)),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { dest ->
                val isSelected = currentDestination == dest ||
                    (dest == AppDestination.DASHBOARD && currentDestination == AppDestination.EVOLUTION)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNavigate(dest) }
                        .testTag(dest.testTag),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = dest.icon,
                        contentDescription = dest.title,
                        tint = if (isSelected) Color(0xFFA78BFA) else Color(0xFF71717A),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dest.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFFA78BFA) else Color(0xFF71717A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Traço indicador sutil sob a aba ativa
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(3.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF8B5CF6),
                                            Color(0xFFA78BFA),
                                            Color(0xFF8B5CF6)
                                        )
                                    )
                                )
                        )
                    } else {
                        Spacer(modifier = Modifier.height(3.dp))
                    }
                }
            }
        }
    }
}
