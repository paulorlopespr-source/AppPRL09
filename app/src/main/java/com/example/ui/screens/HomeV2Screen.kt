package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SessionStatus
import com.example.ui.components.DateUtils
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FitnessViewModel
import kotlinx.coroutines.launch

/**
 * Fase 8 — Tela Início.
 *
 * Implementação orientada pela referência visual aprovada em 17/09/2026:
 * - conteúdo essencial na superfície;
 * - recursos secundários preservados no menu lateral;
 * - sessão ativa ganha prioridade;
 * - nenhum botão central flutuante pertence a esta experiência.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeV2Screen(
    viewModel: FitnessViewModel,
    onNavigateToWorkouts: () -> Unit,
    onNavigateToActiveWorkout: () -> Unit,
    onNavigateToCardio: () -> Unit,
    onNavigateToAgenda: () -> Unit,
    onNavigateToEvolution: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val activeWorkout by viewModel.activeWorkout.collectAsStateWithLifecycle()
    val activeCardio by viewModel.activeCardio.collectAsStateWithLifecycle()
    val sessions by viewModel.allWorkoutSessions.collectAsStateWithLifecycle()
    val dailySuggestion by viewModel.dailyWorkoutSuggestion.collectAsStateWithLifecycle()
    val gamification by viewModel.gamificationOverview.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val today = DateUtils.todayEpochDay()
    val weekStart = today - (today % 7)
    val weekDays = (0..6).map { weekStart + it }
    val completed = sessions.filter { it.status == SessionStatus.COMPLETED }
    val weeklyDone = completed.count { it.dateEpochDay in weekDays }
    val weeklyGoal = profile?.weeklyGoalDays ?: 5
    val streak = gamification.currentStreak
    val readiness = gamification.readinessScore.coerceIn(0, 100)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = PurpleDeepCard,
                drawerContentColor = TextPrimary
            ) {
                Spacer(Modifier.height(28.dp))
                Text("AppPRL09", modifier = Modifier.padding(horizontal = 20.dp), fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("Mais recursos", modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp), color = TextSecondary)
                Spacer(Modifier.height(14.dp))
                NavigationDrawerItem(
                    label = { Text("Evolução e medidas") },
                    selected = false,
                    icon = { Icon(Icons.Default.TrendingUp, null) },
                    onClick = { scope.launch { drawerState.close() }; onNavigateToEvolution() }
                )
                NavigationDrawerItem(
                    label = { Text("Cardio e GPS") },
                    selected = false,
                    icon = { Icon(Icons.Default.DirectionsRun, null) },
                    onClick = { scope.launch { drawerState.close() }; onNavigateToCardio() }
                )
                NavigationDrawerItem(
                    label = { Text("Agenda") },
                    selected = false,
                    icon = { Icon(Icons.Default.CalendarMonth, null) },
                    onClick = { scope.launch { drawerState.close() }; onNavigateToAgenda() }
                )
                NavigationDrawerItem(
                    label = { Text("Histórico e treinos") },
                    selected = false,
                    icon = { Icon(Icons.Default.FitnessCenter, null) },
                    onClick = { scope.launch { drawerState.close() }; onNavigateToWorkouts() }
                )
            }
        }
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize().background(PurpleDarkest),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 24.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }, modifier = Modifier.testTag("home_menu")) {
                        Icon(Icons.Default.Menu, "Abrir menu", tint = LilacSoft)
                    }
                    Column(Modifier.weight(1f).padding(start = 4.dp)) {
                        Text("Olá, ${profile?.name ?: "Atleta"}", fontSize = 26.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text("Disciplina hoje, resultados amanhã.", fontSize = 13.sp, color = LilacSoft)
                    }
                    IconButton(onClick = onNavigateToAgenda) {
                        Icon(Icons.Default.Notifications, "Agenda e lembretes", tint = LilacAccent)
                    }
                }
            }

            item {
                if (activeWorkout.isActive) {
                    ActiveSessionHero(
                        title = activeWorkout.title,
                        subtitle = "Treino em andamento • ${DateUtils.formatSecondsToTime(activeWorkout.durationSeconds)}",
                        buttonText = "RETOMAR TREINO",
                        onClick = onNavigateToActiveWorkout
                    )
                } else if (activeCardio.isActive) {
                    ActiveSessionHero(
                        title = activeCardio.type.title,
                        subtitle = "Cardio em andamento • ${String.format("%.2f", activeCardio.distanceKm)} km",
                        buttonText = "VER CARDIO / GPS",
                        onClick = onNavigateToCardio
                    )
                } else {
                    TodayWorkoutHero(
                        title = dailySuggestion.title,
                        subtitle = dailySuggestion.subtitle,
                        duration = dailySuggestion.estimatedDurationMinutes,
                        onClick = onNavigateToWorkouts
                    )
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MiniMetric("🔥", streak.toString(), "Dias seguidos", Modifier.weight(1f))
                    MiniMetric("🏋", "$weeklyDone / $weeklyGoal", "Treinos semanais", Modifier.weight(1f))
                    MiniMetric("◎", readiness.toString(), "Seu foco hoje", Modifier.weight(1f), EmeraldSuccess)
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToEvolution() }.testTag("home_weekly_progress"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassSurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TrendingUp, null, tint = LilacAccent)
                                Spacer(Modifier.width(8.dp))
                                Text("Seu progresso", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            }
                            Icon(Icons.Default.KeyboardArrowRight, "Ver painel", tint = LilacSoft)
                        }
                        Spacer(Modifier.height(14.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Treinos da semana", color = TextSecondary, fontSize = 13.sp)
                            Text("$weeklyDone / $weeklyGoal", color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(10.dp))
                        Box(Modifier.fillMaxWidth().height(8.dp).background(Color.White.copy(alpha = .08f), CircleShape)) {
                            Box(
                                Modifier.fillMaxWidth((weeklyDone.toFloat() / weeklyGoal.coerceAtLeast(1)).coerceIn(0f, 1f))
                                    .height(8.dp)
                                    .background(Brush.horizontalGradient(listOf(LilacAccent, PurpleVibrant)), CircleShape)
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf("S", "T", "Q", "Q", "S", "S", "D").forEachIndexed { index, label ->
                                val done = completed.any { it.dateEpochDay == weekDays[index] }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        Modifier.size(24.dp).background(if (done) EmeraldSuccess else Color.Transparent, CircleShape)
                                            .border(1.dp, if (done) EmeraldSuccess else TextMuted, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) { Text(if (done) "✓" else "", color = PurpleDarkest, fontWeight = FontWeight.Black, fontSize = 11.sp) }
                                    Spacer(Modifier.height(4.dp))
                                    Text(label, color = if (done) EmeraldSuccess else TextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PurpleDeepCard.copy(alpha = .72f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                ) {
                    Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("“", fontSize = 42.sp, fontWeight = FontWeight.Black, color = LilacAccent)
                        Spacer(Modifier.width(8.dp))
                        Text("Pequenas ações diárias\nconstroem grandes resultados.", color = LilacSoft, fontSize = 15.sp, lineHeight = 21.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayWorkoutHero(title: String, subtitle: String, duration: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("home_today_workout"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent.copy(alpha = .55f))
    ) {
        Column(
            Modifier.background(Brush.linearGradient(listOf(Color(0xFF24114C), Color(0xFF6D22D7), Color(0xFF30105E)))).padding(22.dp)
        ) {
            Text("UM DIA MAIS FORTE COMEÇA", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text("AGORA.", fontSize = 26.sp, fontWeight = FontWeight.Black, color = LilacAccent)
            Spacer(Modifier.height(14.dp))
            Text(title, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("$subtitle • ~${duration} min", fontSize = 12.sp, color = LilacSoft)
            Spacer(Modifier.height(18.dp))
            Box(
                Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(PurpleVibrant, LilacAccent)), RoundedCornerShape(14.dp))
                    .clickable(onClick = onClick).padding(vertical = 14.dp).testTag("home_start_today"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlayArrow, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("INICIAR TREINO DE HOJE", color = Color.White, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun ActiveSessionHero(title: String, subtitle: String, buttonText: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleDeepCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(9.dp).background(EmeraldSuccess, CircleShape))
                Spacer(Modifier.width(8.dp))
                Text("SESSÃO ATIVA", color = EmeraldSuccess, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(12.dp))
            Text(title, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text(subtitle, color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth().background(EmeraldSuccess.copy(alpha = .16f), RoundedCornerShape(12.dp)).padding(13.dp), contentAlignment = Alignment.Center) {
                Text(buttonText, color = EmeraldSuccess, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun MiniMetric(icon: String, value: String, label: String, modifier: Modifier = Modifier, accent: Color = LilacAccent) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GlassSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 14.dp)) {
            Text(icon, fontSize = 18.sp)
            Spacer(Modifier.height(6.dp))
            Text(value, color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.Black)
            Text(label, color = accent, fontSize = 9.sp, lineHeight = 11.sp)
        }
    }
}
