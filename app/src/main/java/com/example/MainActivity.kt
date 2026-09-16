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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.example.ui.screens.EvolutionScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.Phase45HubScreen
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
    DASHBOARD("Painel", Icons.Default.TrendingUp, "tab_dashboard"),
    EVOLUTION("Progresso", Icons.Default.TrendingUp, "tab_evolution"),
    AGENDA("Agenda", Icons.Default.CalendarMonth, "tab_agenda"),
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
                        onNavigateToEvolution = { currentDestination = AppDestination.EVOLUTION }
                    )
                    AppDestination.WORKOUTS -> WorkoutTemplatesScreen(
                        viewModel = viewModel,
                        onStartWorkout = { currentDestination = AppDestination.ACTIVE_WORKOUT }
                    )
                    AppDestination.DASHBOARD -> Phase45HubScreen(
                        onStartTodayWorkout = { currentDestination = AppDestination.WORKOUTS }
                    )
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
                                    fontSize = 11.sp,
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
                modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                FloatingLiquidGlassNav(
                    currentDestination = currentDestination,
                    hasActiveWorkout = activeWorkoutState.isActive,
                    onNavigate = { currentDestination = it },
                    onCenterActionClick = {
                        if (activeWorkoutState.isActive) currentDestination = AppDestination.ACTIVE_WORKOUT
                        else if (activeCardioState.isActive) showActiveCardioModal = true
                        else showQuickStartSheet = true
                    }
                )
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
fun FloatingLiquidGlassNav(
    currentDestination: AppDestination,
    hasActiveWorkout: Boolean,
    onNavigate: (AppDestination) -> Unit,
    onCenterActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navItemsLeft = listOf(AppDestination.HOME, AppDestination.WORKOUTS)
    val navItemsRight = listOf(AppDestination.DASHBOARD, AppDestination.EVOLUTION, AppDestination.AGENDA)

    Surface(
        modifier = modifier.fillMaxWidth().shadow(16.dp, RoundedCornerShape(32.dp), ambientColor = GlowPurple, spotColor = PurpleVibrant),
        shape = RoundedCornerShape(32.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().background(GlassSurfaceDark).border(1.dp, GlassBorder, RoundedCornerShape(32.dp)).padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
                navItemsLeft.forEach { dest -> NavButton(dest, currentDestination == dest) { onNavigate(dest) } }
                CenterActionFAB(hasActiveWorkout = hasActiveWorkout, onClick = onCenterActionClick)
                navItemsRight.forEach { dest -> NavButton(dest, currentDestination == dest) { onNavigate(dest) } }
            }
        }
    }
}

@Composable
private fun NavButton(destination: AppDestination, isSelected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.90f else 1f, label = "nav_btn_scale")
    Column(
        modifier = Modifier.scale(scale).clip(RoundedCornerShape(16.dp)).clickable(interactionSource = interactionSource, indication = null, onClick = onClick).padding(horizontal = 8.dp, vertical = 6.dp).testTag(destination.testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(destination.icon, destination.title, tint = if (isSelected) LilacAccent else TextMuted, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(3.dp))
        Text(destination.title, fontSize = 9.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) LilacAccent else TextMuted)
        if (isSelected) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(Modifier.size(4.dp).clip(CircleShape).background(LilacAccent))
        }
    }
}

@Composable
private fun CenterActionFAB(hasActiveWorkout: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.92f else 1f, animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f), label = "fab_scale")
    Box(
        modifier = Modifier.scale(scale).shadow(10.dp, CircleShape, ambientColor = GlowPurple, spotColor = LilacAccent).size(52.dp).clip(CircleShape).background(GradientAction).border(1.5.dp, LilacSoft.copy(alpha = 0.6f), CircleShape).clickable(interactionSource = interactionSource, indication = null, onClick = onClick).testTag("btn_center_floating_action"),
        contentAlignment = Alignment.Center
    ) {
        Icon(if (hasActiveWorkout) Icons.Default.PlayArrow else Icons.Default.Add, if (hasActiveWorkout) "Treino em andamento" else "Adicionar/Iniciar", tint = Color.White, modifier = Modifier.size(28.dp))
    }
}
