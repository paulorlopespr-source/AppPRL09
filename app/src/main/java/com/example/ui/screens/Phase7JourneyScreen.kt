package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.viewmodel.FitnessViewModel
import com.example.ui.viewmodel.Phase7ViewModel

/**
 * Delegador de compatibilidade para a tela Jornada.
 * Redireciona para JornadaScreen preservando parâmetros e ViewModels.
 */
@Composable
fun Phase7JourneyScreen(
    onOpenCoach: () -> Unit,
    onOpenDashboard: () -> Unit,
    onStartWorkout: () -> Unit,
    vm: Phase7ViewModel = viewModel(),
    fitnessVm: FitnessViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    JornadaScreen(
        onOpenCoach = onOpenCoach,
        onOpenDashboard = onOpenDashboard,
        onStartWorkout = onStartWorkout,
        phase7Vm = vm,
        fitnessVm = fitnessVm,
        modifier = modifier
    )
}
