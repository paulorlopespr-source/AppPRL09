package com.example

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ActiveCardioTrackerModal
import com.example.ui.components.QuickWorkoutSheet
import com.example.ui.screens.ActiveWorkoutScreen
import com.example.ui.screens.AgendaScreen
import com.example.ui.screens.CardioScreen
import com.example.ui.screens.CoachPRL09Screen
import com.example.ui.screens.EvolutionScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JornadaScreen
import com.example.ui.screens.PainelScreen
import com.example.ui.screens.Phase45HubScreen
import com.example.ui.screens.Phase7JourneyScreen
import com.example.ui.screens.WorkoutTemplatesScreen
import com.example.ui.theme.EmeraldSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.FitPr09Theme
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlowPurple
import com.example.ui.theme.GradientAction
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.FitnessViewModel

enum class AppDestination(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    HOME("Início", Icons.Default.Home, "tab_home"),
    WORKOUTS("Treinos", Icons.Default.FitnessCenter, "tab_workouts"),
    DASHBOARD("Painel", Icons.Default.BarChart, "tab_dashboard"),
    JOURNEY("Jornada", Icons.Default.EmojiEvents, "tab_journey"),
    AGENDA("Agenda", Icons.Default.CalendarMonth, "tab_agenda"),
    COACH("Coach", Icons.Default.Psychology, "tab_coach"),
    EVOLUTION("Progresso", Icons.Default.BarChart, "tab_evolution"),
    CARDIO("Cardio", Icons.Default.DirectionsBike, "tab_cardio"),
    ACTIVE_WORKOUT("Treino Ativo", Icons.Default.FitnessCenter, "tab_active_workout")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitPr09Theme {
                val viewModel: FitnessViewModel = viewModel()
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: FitnessViewModel) {
    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }
    val activeWorkoutState by viewModel.activeWorkout.collectAsStateWithLifecycle()
    val activeCardioState by viewModel.activeCardio.collectAsStateWithLifecycle()
    val keepScreenOn by viewModel.keepScreenOn.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val shouldKeepScreenOn = keepScreenOn || activeWorkoutState.isActive || activeCardioState.isActive
    DisposableEffect(shouldKeepScreenOn) {
        val window = (context as? Activity)?.window
        if (shouldKeepScreenOn) window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }

    var showQuickStartSheet by remember { mutableStateOf(false) }
    var showActiveCardioModal by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showBottomBar = currentDestination != AppDestination.ACTIVE_WORKOUT

    Scaffold(containerColor = PurpleDarkest, modifier = Modifier.fillMaxSize()) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (currentDestination) {
                    AppDestination.HOME -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToWorkouts = { currentDestination = AppDestination.WORKOUTS },
                        onNavigateToActiveWorkout = { currentDestination = AppDestination.ACTIVE_WORKOUT },
                        onNavigateToCardio = { currentDestination = AppDestination.CARDIO },
                        onNavigateToAgenda = { currentDestination = AppDestination.AGENDA },
                        onNavigateToEvolution = { currentDestination = AppDestination.EVOLUTION },
                        onNavigateToCoach = { currentDestination = AppDestination.COACH },
                        onNavigateToDashboard = { currentDestination = AppDestination.DASHBOARD }
                    )
                    AppDestination.WORKOUTS -> WorkoutTemplatesScreen(
                        viewModel = viewModel,
                        onStartWorkout = { currentDestination = AppDestination.ACTIVE_WORKOUT }
                    )
                    AppDestination.DASHBOARD -> PainelScreen(
                        onStartTodayWorkout = { currentDestination = AppDestination.WORKOUTS },
                        fitnessVm = viewModel
                    )
                    AppDestination.JOURNEY -> JornadaScreen(
                        onOpenCoach = { currentDestination = AppDestination.COACH },
                        onOpenDashboard = { currentDestination = AppDestination.DASHBOARD },
                        onStartWorkout = { currentDestination = AppDestination.WORKOUTS },
                        fitnessVm = viewModel
                    )
                    AppDestination.COACH -> CoachPRL09Screen()
                    AppDestination.CARDIO -> CardioScreen(viewModel = viewModel)
                    AppDestination.AGENDA -> AgendaScreen(
                        viewModel = viewModel,
                        onStartScheduledWorkout = { template, location, scheduledId, dateEpoch ->
                            viewModel.startWorkoutFromTemplate(template, location, scheduledId, dateEpoch)
                            currentDestination = AppDestination.ACTIVE_WORKOUT
                        }
                    )
                    AppDestination.EVOLUTION -> EvolutionScreen(viewModel = viewModel)
                    AppDestination.ACTIVE_WORKOUT -> ActiveWorkoutScreen(
                        viewModel = viewModel,
                        onBack = { currentDestination = AppDestination.WORKOUTS },
                        onFinished = { currentDestination = AppDestination.HOME }
                    )
                }
            }

            if (activeCardioState.isActive && !showActiveCardioModal && showBottomBar) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 84.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { showActiveCardioModal = true }
                        .testTag("floating_active_cardio_bar"),
                    color = PurpleDeepCard,
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, EmeraldSuccess)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp).clip(CircleShape).background(EmeraldSuccess))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("${activeCardioState.type.title} em Andamento", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(
                                    String.format("%02d:%02d • %.2f km • ~%d kcal", activeCardioState.durationSeconds / 60, activeCardioState.durationSeconds % 60, activeCardioState.distanceKm, activeCardioState.caloriesBurned),
                                    fontSize = 10.sp,
                                    color = EmeraldSuccess,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Box(Modifier.clip(RoundedCornerShape(8.dp)).background(EmeraldSubtle).padding(horizontal = 10.dp, vertical = 5.dp)) {
                            Text("VER TEMPO/GPS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = EmeraldSuccess)
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Active Workout Resume Banner when active workout is running
                    if (activeWorkoutState.isActive && currentDestination != AppDestination.ACTIVE_WORKOUT) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .clickable { currentDestination = AppDestination.ACTIVE_WORKOUT }
                                .testTag("floating_active_workout_bar"),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF1B122C),
                            border = BorderStroke(1.dp, Color(0xFF9333EA).copy(alpha = 0.7f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF9333EA)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Retomar Treino",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Treino em Andamento",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = activeWorkoutState.title,
                                            fontSize = 10.sp,
                                            color = Color(0xFFC4B5FD)
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF9333EA).copy(alpha = 0.25f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "RETOMAR",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFC4B5FD)
                                    )
                                }
                            }
                        }
                    }

                    MainBottomNavigation(
                        currentDestination = currentDestination,
                        onNavigate = { currentDestination = it }
                    )
                }
            }
        }
    }

    if (activeCardioState.isActive && showActiveCardioModal) {
        ActiveCardioTrackerModal(viewModel = viewModel, onDismiss = { showActiveCardioModal = false })
    }

    if (showQuickStartSheet) {
        QuickWorkoutSheet(
            onDismiss = { showQuickStartSheet = false },
            onStartStrengthWorkout = { title, location, plans ->
                viewModel.startWorkoutWithPlans(title = title, location = location, plans = plans)
                showQuickStartSheet = false
                currentDestination = AppDestination.ACTIVE_WORKOUT
            },
            onStartCardio = { type, location, intensity, targetMinutes, enableGps ->
                viewModel.startLiveCardio(type = type, location = location, intensity = intensity, targetMinutes = targetMinutes, enableGps = enableGps)
                showQuickStartSheet = false
                showActiveCardioModal = true
            },
            sheetState = sheetState
        )
    }
}

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
        color = Color(0xFF090812),
        border = BorderStroke(0.5.dp, Color(0xFF21192F)),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(top = 7.dp, bottom = 5.dp),
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
                        modifier = Modifier.size(23.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = dest.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFFA78BFA) else Color(0xFF71717A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Subtle glowing indicator pill underneath the active tab
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(2.5.dp)
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
