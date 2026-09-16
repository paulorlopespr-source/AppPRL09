package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.repository.FitnessRepository
import com.example.domain.gamification.JourneySnapshot
import com.example.domain.gamification.Phase7JourneyEngine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class Phase7ViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = FitnessRepository(database.fitnessDao())

    val journey: StateFlow<JourneySnapshot?> = combine(
        repository.allWorkoutSessions,
        repository.allCardioSessions,
        repository.allMedals
    ) { workouts, cardio, medals ->
        Phase7JourneyEngine.build(workouts, cardio, medals)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
