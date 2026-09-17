package com.example

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.screens.ActiveWorkoutScreen
import com.example.ui.screens.AgendaScreen
import com.example.ui.screens.CardioScreen
import com.example.ui.screens.CoachPRL09Screen
import com.example.ui.screens.EvolutionScreen
import com.example.ui.screens.HomeV2Screen
import com.example.ui.screens.Phase45HubScreen
import com.example.ui.screens.Phase7JourneyScreen
import com.example.ui.screens.WorkoutTemplatesScreen
import com.example.ui.theme.EmeraldSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.FitPr09Theme
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlowPurple
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.viewmodel.FitnessViewModel

enum class AppDestination(val title: String, val icon: ImageVector, val testTag: String) {
    HOME("Início", Icons.Default.Home, "tab_home"),
    WORKOUTS("Treinos", Icons.Default.FitnessCenter, "tab_workouts"),
    DASHBOARD("Painel", Icons.Default.TrendingUp, "tab_dashboard"),
    JOURNEY("Jornada", Icons.Default.EmojiEvents, "tab_journey"),
    AGENDA("Agenda", Icons.Default.CalendarMonth, "tab_agenda"),
    COACH("Coach", Icons.Default.Psychology, "tab_coach"),
    EVOLUTION("Progresso", Icons.Default.TrendingUp, "tab_evolution"),
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
                MainAppScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: FitnessViewModel) {
    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }
    var showActiveCardioModal by remember { mutableStateOf(false) }
    val activeWorkout by viewModel.activeWorkout.collectAsStateWithLifecycle()
    val activeCardio by viewModel.activeCardio.collectAsStateWithLifecycle()
    val keepScreenOn by viewModel.keepScreenOn.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val shouldKeepScreenOn = keepScreenOn || activeWorkout.isActive || activeCardio.isActive

    DisposableEffect(shouldKeepScreenOn) {
        val window = (context as? Activity)?.window
        if (shouldKeepScreenOn) window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }

    val showBottomBar = currentDestination != AppDestination.ACTIVE_WORKOUT

    Scaffold(containerColor = PurpleDarkest, modifier = Modifier.fillMaxSize()) { _ ->
        Box(Modifier.fillMaxSize()) {
            when (currentDestination) {
                AppDestination.HOME -> HomeV2Screen(
                    viewModel = viewModel,
                    onNavigateToWorkouts = { currentDestination = AppDestination.WORKOUTS },
                    onNavigateToActiveWorkout = { currentDestination = AppDestination.ACTIVE_WORKOUT },
                    onNavigateToCardio = {
                        if (activeCardio.isActive) showActiveCardioModal = true
                        else currentDestination = AppDestination.CARDIO
                    },
                    onNavigateToAgenda = { currentDestination = AppDestination.AGENDA },
                    onNavigateToEvolution = { currentDestination = AppDestination.EVOLUTION }
                )
                AppDestination.WORKOUTS -> WorkoutTemplatesScreen(
                    viewModel = viewModel,
                    onStartWorkout = { currentDestination = AppDestination.ACTIVE_WORKOUT }
                )
                AppDestination.DASHBOARD -> Phase45HubScreen(
                    onStartTodayWorkout = { currentDestination = AppDestination.WORKOUTS }
                )
                AppDestination.JOURNEY -> Phase7JourneyScreen(
                    onOpenCoach = { currentDestination = AppDestination.COACH },
                    onOpenDashboard = { currentDestination = AppDestination.DASHBOARD },
                    onStartWorkout = { currentDestination = AppDestination.WORKOUTS }
                )
                AppDestination.COACH -> CoachPRL09Screen()
                AppDestination.CARDIO -> CardioScreen(viewModel)
                AppDestination.AGENDA -> AgendaScreen(
                    viewModel = viewModel,
                    onStartScheduledWorkout = { template, location, scheduledId, dateEpoch ->
                        viewModel.startWorkoutFromTemplate(template, location, scheduledId, dateEpoch)
                        currentDestination = AppDestination.ACTIVE_WORKOUT
                    }
                )
                AppDestination.EVOLUTION -> EvolutionScreen(viewModel)
                AppDestination.ACTIVE_WORKOUT -> ActiveWorkoutScreen(
                    viewModel = viewModel,
                    onBack = { currentDestination = AppDestination.WORKOUTS },
                    onFinished = { currentDestination = AppDestination.HOME }
                )
            }

            if (activeCardio.isActive && !showActiveCardioModal && showBottomBar) {
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(horizontal = 20.dp, vertical = 82.dp)
                        .fillMaxWidth().clip(RoundedCornerShape(18.dp)).clickable { showActiveCardioModal = true }.testTag("floating_active_cardio_bar"),
                    color = PurpleDeepCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess)
                ) {
                    Row(Modifier.fillMaxWidth().padding(14.dp, 10.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(9.dp).clip(CircleShape).background(EmeraldSuccess))
                            Spacer(Modifier.size(9.dp))
                            Column {
                                Text("${activeCardio.type.title} em andamento", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(String.format("%02d:%02d • %.2f km", activeCardio.durationSeconds / 60, activeCardio.durationSeconds % 60, activeCardio.distanceKm), color = EmeraldSuccess, fontSize = 11.sp)
                            }
                        }
                        Text("RETOMAR", color = EmeraldSuccess, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                SimplifiedBottomNav(
                    currentDestination = currentDestination,
                    onNavigate = { currentDestination = it }
                )
            }
        }
    }

    if (activeCardio.isActive && showActiveCardioModal) {
        ActiveCardioTrackerModal(viewModel = viewModel, onDismiss = { showActiveCardioModal = false })
    }
}

@Composable
fun SimplifiedBottomNav(
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        AppDestination.HOME,
        AppDestination.WORKOUTS,
        AppDestination.DASHBOARD,
        AppDestination.JOURNEY,
        AppDestination.AGENDA
    )
    Surface(
        modifier = modifier.fillMaxWidth().shadow(14.dp, RoundedCornerShape(28.dp), ambientColor = GlowPurple),
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent
    ) {
        Row(
            Modifier.fillMaxWidth().background(GlassSurfaceDark).border(1.dp, GlassBorder, RoundedCornerShape(28.dp)).padding(horizontal = 8.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { destination ->
                NavButton(destination, currentDestination == destination) { onNavigate(destination) }
            }
        }
    }
}

@Composable
private fun NavButton(destination: AppDestination, selected: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Column(
        modifier = Modifier.scale(if (pressed) .92f else 1f).clip(RoundedCornerShape(14.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp).testTag(destination.testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(destination.icon, destination.title, tint = if (selected) LilacAccent else TextMuted, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(3.dp))
        Text(destination.title, fontSize = 9.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, color = if (selected) LilacAccent else TextMuted)
        if (selected) {
            Spacer(Modifier.height(3.dp))
            Box(Modifier.size(width = 24.dp, height = 3.dp).clip(CircleShape).background(LilacAccent))
        }
    }
}
