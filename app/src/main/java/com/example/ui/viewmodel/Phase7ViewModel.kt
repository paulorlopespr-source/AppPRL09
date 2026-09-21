package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.repository.FitnessRepository
import com.example.domain.gamification.JourneySnapshot
import com.example.domain.gamification.Phase7JourneyEngine
import com.example.data.model.AgendaCustomAppointment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class Phase7ViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = FitnessRepository(database.fitnessDao())
    private val agendaAppointments = MutableStateFlow<List<AgendaCustomAppointment>>(emptyList())

    fun updateAgendaAppointments(appointments: List<AgendaCustomAppointment>) {
        agendaAppointments.value = appointments
    }

    val journey: StateFlow<JourneySnapshot?> = combine(
        repository.allWorkoutSessions,
        repository.allCardioSessions,
        repository.allMedals,
        repository.userProfile,
        agendaAppointments
    ) { workouts, cardio, medals, profile, appointments ->
        Phase7JourneyEngine.build(workouts, cardio, medals, weeklyGoalDays = profile?.weeklyGoalDays, agendaAppointments = appointments)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
