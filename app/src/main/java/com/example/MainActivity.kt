package com.example

import android.os.Bundle
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.WorkoutTemplate
import com.example.ui.components.LiquidGlassSurface
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SecondaryButton
import com.example.ui.screens.ActiveWorkoutScreen
import com.example.ui.screens.AgendaScreen
import com.example.ui.screens.CardioScreen
import com.example.ui.screens.EvolutionScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.WorkoutTemplatesScreen
import com.example.ui.theme.FitPr09Theme
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassSurfaceDeep
import com.example.ui.theme.GlowPurple
import com.example.ui.theme.GradientAction
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FitnessViewModel

enum class AppDestination(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    HOME("Início", Icons.Default.Home, "tab_home"),
    WORKOUTS("Treinos", Icons.Default.FitnessCenter, "tab_workouts"),
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
    val workoutTemplates by viewModel.workoutTemplates.collectAsStateWithLifecycle()

    var showQuickStartSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val showBottomBar = currentDestination != AppDestination.ACTIVE_WORKOUT

    Scaffold(
        containerColor = PurpleDarkest,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main Screen Content (flows behind the floating liquid glass bar)
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when (currentDestination) {
                    AppDestination.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToWorkouts = { currentDestination = AppDestination.WORKOUTS },
                            onNavigateToActiveWorkout = { currentDestination = AppDestination.ACTIVE_WORKOUT },
                            onNavigateToCardio = { currentDestination = AppDestination.CARDIO },
                            onNavigateToAgenda = { currentDestination = AppDestination.AGENDA },
                            onNavigateToEvolution = { currentDestination = AppDestination.EVOLUTION }
                        )
                    }
                    AppDestination.WORKOUTS -> {
                        WorkoutTemplatesScreen(
                            viewModel = viewModel,
                            onStartWorkout = { currentDestination = AppDestination.ACTIVE_WORKOUT }
                        )
                    }
                    AppDestination.CARDIO -> {
                        CardioScreen(viewModel = viewModel)
                    }
                    AppDestination.AGENDA -> {
                        AgendaScreen(
                            viewModel = viewModel,
                            onStartScheduledWorkout = { template, location ->
                                viewModel.startWorkoutFromTemplate(template, location)
                                currentDestination = AppDestination.ACTIVE_WORKOUT
                            }
                        )
                    }
                    AppDestination.EVOLUTION -> {
                        EvolutionScreen(viewModel = viewModel)
                    }
                    AppDestination.ACTIVE_WORKOUT -> {
                        ActiveWorkoutScreen(
                            viewModel = viewModel,
                            onBack = { currentDestination = AppDestination.WORKOUTS },
                            onFinished = { currentDestination = AppDestination.HOME }
                        )
                    }
                }
            }

            // Floating Liquid Glass Bottom Navigation
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                FloatingLiquidGlassNav(
                    currentDestination = currentDestination,
                    hasActiveWorkout = activeWorkoutState.isActive,
                    onNavigate = { currentDestination = it },
                    onCenterActionClick = {
                        if (activeWorkoutState.isActive) {
                            currentDestination = AppDestination.ACTIVE_WORKOUT
                        } else {
                            showQuickStartSheet = true
                        }
                    }
                )
            }
        }
    }

    // Quick Workout Start Modal Sheet
    if (showQuickStartSheet) {
        ModalBottomSheet(
            onDismissRequest = { showQuickStartSheet = false },
            sheetState = sheetState,
            containerColor = PurpleDarkSurface,
            contentColor = TextPrimary,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(LilacSoft.copy(alpha = 0.4f))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Iniciar Atividade",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Escolha um treino para iniciar agora:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Option 1: Start Full Body A (Default template or first template)
                val firstTemplate = workoutTemplates.firstOrNull()
                if (firstTemplate != null) {
                    LiquidGlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.startWorkoutFromTemplate(firstTemplate)
                                showQuickStartSheet = false
                                currentDestination = AppDestination.ACTIVE_WORKOUT
                            },
                        backgroundColor = PurpleDeepCard,
                        borderColor = LilacAccent.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "⚡ Iniciar Treino de Hoje",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LilacAccent,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = firstTemplate.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${firstTemplate.exerciseCount} exercícios • ~${firstTemplate.executionDurationMinutes} min",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(GradientAction),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Iniciar",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SecondaryButton(
                        text = "Ver Todos os Treinos",
                        icon = Icons.Default.FitnessCenter,
                        onClick = {
                            showQuickStartSheet = false
                            currentDestination = AppDestination.WORKOUTS
                        },
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text = "Novo Treino Livre",
                        icon = Icons.Default.Add,
                        onClick = {
                            viewModel.startEmptyWorkout("Treino Personalizado")
                            showQuickStartSheet = false
                            currentDestination = AppDestination.ACTIVE_WORKOUT
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
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
    val navItemsRight = listOf(AppDestination.EVOLUTION, AppDestination.AGENDA)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = GlowPurple,
                spotColor = PurpleVibrant
            ),
        shape = RoundedCornerShape(32.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GlassSurfaceDark)
                .border(
                    width = 1.dp,
                    color = GlassBorder,
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Left nav items
                navItemsLeft.forEach { dest ->
                    NavButton(
                        destination = dest,
                        isSelected = currentDestination == dest,
                        onClick = { onNavigate(dest) }
                    )
                }

                // Center Action Button (+) with glowing gradient
                CenterActionFAB(
                    hasActiveWorkout = hasActiveWorkout,
                    onClick = onCenterActionClick
                )

                // Right nav items
                navItemsRight.forEach { dest ->
                    NavButton(
                        destination = dest,
                        isSelected = currentDestination == dest,
                        onClick = { onNavigate(dest) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavButton(
    destination: AppDestination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        label = "nav_btn_scale"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(destination.testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = destination.title,
            tint = if (isSelected) LilacAccent else TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = destination.title,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) LilacAccent else TextMuted
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(LilacAccent)
            )
        }
    }
}

@Composable
private fun CenterActionFAB(
    hasActiveWorkout: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "fab_scale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .shadow(
                elevation = 10.dp,
                shape = CircleShape,
                ambientColor = GlowPurple,
                spotColor = LilacAccent
            )
            .size(52.dp)
            .clip(CircleShape)
            .background(GradientAction)
            .border(1.5.dp, LilacSoft.copy(alpha = 0.6f), CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("btn_center_floating_action"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (hasActiveWorkout) Icons.Default.PlayArrow else Icons.Default.Add,
            contentDescription = if (hasActiveWorkout) "Treino em andamento" else "Adicionar/Iniciar",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

