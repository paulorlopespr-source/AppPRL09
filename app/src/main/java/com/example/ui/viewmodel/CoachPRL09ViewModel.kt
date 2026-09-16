package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.FitnessRepository
import com.example.domain.coach.*
import com.example.domain.readiness.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CoachUiState(
    val context: CoachPRL09Context? = null,
    val isLoading: Boolean = false,
    val answer: AICoachMessage? = null,
    val generatedWorkout: AIWorkoutPlanResult? = null,
    val error: String? = null
)

class CoachPRL09ViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = FitnessRepository(database.fitnessDao())
    private val coach = CoachPRL09Service(repository)
    private val checkIns = CheckInStore(application)
    private val checkInHistory = MutableStateFlow(checkIns.history())
    private val _ui = MutableStateFlow(CoachUiState())
    val ui: StateFlow<CoachUiState> = _ui.asStateFlow()

    private val readinessFlow = combine(repository.allWorkoutSessions, repository.allCardioSessions, checkInHistory) { workouts, cardio, history ->
        val checkIn = history.firstOrNull() ?: return@combine null
        ReadinessEngine.calculate(checkIn, TrainingLoadCalculator.currentWeek(workouts, cardio), TrainingLoadCalculator.previousWeek(workouts, cardio))
    }

    private val contextFlow = combine(
        repository.userProfile,
        repository.allWorkoutSessions,
        repository.allCardioSessions,
        repository.allBodyMeasurements,
        readinessFlow
    ) { profile, workouts, cardio, measurements, readiness ->
        CoachContextBuilder.build(profile, workouts, cardio, measurements, readiness)
    }

    init {
        viewModelScope.launch { contextFlow.collect { context -> _ui.update { it.copy(context = context) } } }
    }

    fun ask(question: String) = viewModelScope.launch {
        val context = _ui.value.context ?: return@launch
        val profile = repository.userProfile.first() ?: run {
            _ui.update { it.copy(error = "Cadastre seu perfil antes de usar o Coach PRL09.") }; return@launch
        }
        _ui.update { it.copy(isLoading = true, error = null) }
        runCatching { coach.ask(question.trim(), profile, context) }
            .onSuccess { answer -> _ui.update { it.copy(isLoading = false, answer = answer) } }
            .onFailure { error -> _ui.update { it.copy(isLoading = false, error = error.message ?: "Falha ao consultar o Coach PRL09") } }
    }

    fun generateWorkout(availableMinutes: Int, equipment: List<String>, focus: String?) = viewModelScope.launch {
        val context = _ui.value.context ?: return@launch
        val profile = repository.userProfile.first() ?: run {
            _ui.update { it.copy(error = "Cadastre seu perfil antes de gerar um treino.") }; return@launch
        }
        _ui.update { it.copy(isLoading = true, error = null) }
        runCatching { coach.generateAdaptedWorkout(profile, context.copy(equipment = equipment), availableMinutes, equipment, focus) }
            .onSuccess { workout -> _ui.update { it.copy(isLoading = false, generatedWorkout = workout) } }
            .onFailure { error -> _ui.update { it.copy(isLoading = false, error = error.message ?: "Falha ao gerar treino adaptado") } }
    }
}
