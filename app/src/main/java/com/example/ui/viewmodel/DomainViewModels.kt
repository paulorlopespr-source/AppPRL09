package com.example.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.FitnessRepository
import com.example.domain.cardio.OutdoorActivityEngine
import com.example.service.OutdoorLocationService
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

abstract class DomainViewModel(application: Application) : AndroidViewModel(application) {
    protected val repository: FitnessRepository by lazy { FitnessRepository(AppDatabase.getDatabase(application, viewModelScope).fitnessDao()) }
}
class WorkoutViewModel(application: Application) : DomainViewModel(application) {
    val workoutTemplates = repository.allWorkoutTemplates.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val workoutSessions = repository.allWorkoutSessions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val exercises = repository.allExercises.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class CardioViewModel(application: Application) : DomainViewModel(application) {
    val cardioSessions: StateFlow<List<CardioSession>> = repository.allCardioSessions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val liveRoute = OutdoorLocationService.points
    private val _activeType = MutableStateFlow<CardioType?>(null); val activeType = _activeType.asStateFlow()
    private val _startedAt = MutableStateFlow<Long?>(null); val startedAt = _startedAt.asStateFlow()
    private val _paused = MutableStateFlow(false); val paused = _paused.asStateFlow()
    val liveSummary = liveRoute.map { OutdoorActivityEngine.summarize(it) }.stateIn(viewModelScope, SharingStarted.Eagerly, OutdoorActivityEngine.summarize(emptyList()))

    fun startOutdoor(type: CardioType) {
        OutdoorLocationService.clearRoute(); _activeType.value = type; _startedAt.value = System.currentTimeMillis(); _paused.value = false
        ContextCompat.startForegroundService(getApplication(), Intent(getApplication(), OutdoorLocationService::class.java).setAction(OutdoorLocationService.ACTION_START))
    }
    fun pauseOutdoor() { _paused.value = true; getApplication<Application>().startService(Intent(getApplication(), OutdoorLocationService::class.java).setAction(OutdoorLocationService.ACTION_PAUSE)) }
    fun resumeOutdoor() { _paused.value = false; getApplication<Application>().startService(Intent(getApplication(), OutdoorLocationService::class.java).setAction(OutdoorLocationService.ACTION_RESUME)) }

    fun finishOutdoor(notes: String = "") = viewModelScope.launch {
        val type = _activeType.value ?: return@launch
        val route = liveRoute.value
        val summary = OutdoorActivityEngine.summarize(route)
        val moshi = Moshi.Builder().build()
        val routeAdapter = moshi.adapter<List<RoutePoint>>(Types.newParameterizedType(List::class.java, RoutePoint::class.java))
        val splitAdapter = moshi.adapter<List<ActivitySplit>>(Types.newParameterizedType(List::class.java, ActivitySplit::class.java))
        repository.saveCardioSession(CardioSession(type = type, dateEpochDay = LocalDate.now().toEpochDay(), timestampMillis = _startedAt.value ?: System.currentTimeMillis(), durationMinutes = summary.elapsedSeconds / 60, distanceKm = summary.distanceKm, location = "Outdoor / GPS", notes = notes, movingTimeSeconds = summary.movingSeconds, routeJson = routeAdapter.toJson(route), splitsJson = splitAdapter.toJson(summary.splits), elevationGainMeters = summary.elevationGainMeters, minElevationMeters = summary.minElevationMeters, maxElevationMeters = summary.maxElevationMeters, avgSpeedKmh = summary.averageSpeedKmh, avgPaceSecondsPerKm = summary.averagePaceSecondsPerKm))
        getApplication<Application>().startService(Intent(getApplication(), OutdoorLocationService::class.java).setAction(OutdoorLocationService.ACTION_STOP))
        _activeType.value = null; _startedAt.value = null; _paused.value = false; OutdoorLocationService.clearRoute()
    }
}

class EvolutionViewModel(application: Application) : DomainViewModel(application) {
    val bodyMeasurements = repository.allBodyMeasurements.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val progressPhotos = repository.allProgressPhotos.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
class NutritionViewModel(application: Application) : DomainViewModel(application) { val mealLogs = repository.allMealLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()) }
class ProfileViewModel(application: Application) : DomainViewModel(application) { val profile = repository.userProfile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null) }
class GamificationViewModel(application: Application) : DomainViewModel(application) { val medals = repository.allMedals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()) }
class HealthViewModel(application: Application) : DomainViewModel(application)
class AITrainerViewModel(application: Application) : DomainViewModel(application)
