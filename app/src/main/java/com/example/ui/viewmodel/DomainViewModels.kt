package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.BodyMeasurement
import com.example.data.model.CardioSession
import com.example.data.model.Exercise
import com.example.data.model.MealLog
import com.example.data.model.ProgressPhoto
import com.example.data.model.UserMedal
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutTemplate
import com.example.data.repository.FitnessRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/** Base temporária para a migração gradual do FitnessViewModel monolítico. */
abstract class DomainViewModel(application: Application) : AndroidViewModel(application) {
    protected val repository: FitnessRepository by lazy {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        FitnessRepository(db.fitnessDao())
    }
}

class WorkoutViewModel(application: Application) : DomainViewModel(application) {
    val workoutTemplates: StateFlow<List<WorkoutTemplate>> = repository.allWorkoutTemplates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val workoutSessions: StateFlow<List<WorkoutSession>> = repository.allWorkoutSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val exercises: StateFlow<List<Exercise>> = repository.allExercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class CardioViewModel(application: Application) : DomainViewModel(application) {
    val cardioSessions: StateFlow<List<CardioSession>> = repository.allCardioSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class EvolutionViewModel(application: Application) : DomainViewModel(application) {
    val bodyMeasurements: StateFlow<List<BodyMeasurement>> = repository.allBodyMeasurements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val progressPhotos: StateFlow<List<ProgressPhoto>> = repository.allProgressPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class NutritionViewModel(application: Application) : DomainViewModel(application) {
    val mealLogs: StateFlow<List<MealLog>> = repository.allMealLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class ProfileViewModel(application: Application) : DomainViewModel(application) {
    val profile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}

class GamificationViewModel(application: Application) : DomainViewModel(application) {
    val medals: StateFlow<List<UserMedal>> = repository.allMedals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

/** Health terá Health Connect e sincronização migrados do FitnessViewModel na próxima etapa. */
class HealthViewModel(application: Application) : DomainViewModel(application)

/** AI Trainer receberá geração de treino, coach, execução e análise após a migração das telas. */
class AITrainerViewModel(application: Application) : DomainViewModel(application)
