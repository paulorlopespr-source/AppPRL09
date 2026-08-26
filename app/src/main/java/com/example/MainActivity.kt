package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.RoyalBlueSubtle
import com.example.ui.theme.TextSecondaryLight
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ActiveWorkoutScreen
import com.example.ui.screens.AgendaScreen
import com.example.ui.screens.CardioScreen
import com.example.ui.screens.EvolutionScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.WorkoutTemplatesScreen
import com.example.ui.theme.FitTreinoTheme
import com.example.ui.theme.OrangeLight
import com.example.ui.theme.OrangePrimary
import com.example.ui.viewmodel.FitnessViewModel

enum class AppDestination(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    HOME("Início", Icons.Default.Home, "tab_home"),
    WORKOUTS("Musculação", Icons.Default.FitnessCenter, "tab_workouts"),
    CARDIO("Cardio", Icons.Default.DirectionsBike, "tab_cardio"),
    AGENDA("Agenda", Icons.Default.CalendarMonth, "tab_agenda"),
    EVOLUTION("Evolução", Icons.Default.TrendingUp, "tab_evolution"),
    ACTIVE_WORKOUT("Treino Ativo", Icons.Default.FitnessCenter, "tab_active_workout")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitTreinoTheme {
                val viewModel: FitnessViewModel = viewModel()
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: FitnessViewModel) {
    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }
    val activeWorkoutState by viewModel.activeWorkout.collectAsStateWithLifecycle()

    val showBottomBar = currentDestination != AppDestination.ACTIVE_WORKOUT

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = BorderSubtle,
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                ) {
                    val navItems = listOf(
                        AppDestination.HOME,
                        AppDestination.WORKOUTS,
                        AppDestination.CARDIO,
                        AppDestination.AGENDA,
                        AppDestination.EVOLUTION
                    )

                    navItems.forEach { destination ->
                        val isSelected = currentDestination == destination
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentDestination = destination },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.title
                                )
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = RoyalBlue,
                                selectedTextColor = RoyalBlue,
                                indicatorColor = RoyalBlueSubtle,
                                unselectedIconColor = TextSecondaryLight,
                                unselectedTextColor = TextSecondaryLight
                            ),
                            modifier = Modifier.testTag(destination.testTag)
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomBar) paddingValues.calculateBottomPadding() else 0.dp)
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
    }
}
