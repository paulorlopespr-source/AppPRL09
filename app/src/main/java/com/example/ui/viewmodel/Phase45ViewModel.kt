package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.health.HealthConnectDashboardReader
import com.example.data.local.AppDatabase
import com.example.data.model.ExerciseExecutionRecord
import com.example.data.model.SessionStatus
import com.example.data.model.WorkoutExercisePlan
import com.example.data.repository.FitnessRepository
import com.example.domain.dashboard.DashboardEngine
import com.example.domain.dashboard.DashboardSummary
import com.example.domain.dashboard.EvolutionTrend
import com.example.domain.dashboard.HealthDashboardSnapshot
import com.example.domain.dashboard.MuscleVolume
import com.example.domain.readiness.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/** ViewModel isolado das Fases 4/5 para não voltar a inflar o FitnessViewModel legado. */
class Phase45ViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = FitnessRepository(database.fitnessDao())
    private val healthReader = HealthConnectDashboardReader(application)
    private val checkIns = CheckInStore(application)
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val planListType = Types.newParameterizedType(List::class.java, WorkoutExercisePlan::class.java)
    private val plansAdapter = moshi.adapter<List<WorkoutExercisePlan>>(planListType)

    private val _health = MutableStateFlow(HealthDashboardSnapshot())
    val health = _health.asStateFlow()
    private val _checkInHistory = MutableStateFlow(checkIns.history())
    val checkInHistory = _checkInHistory.asStateFlow()

    val summary: StateFlow<DashboardSummary?> = combine(repository.userProfile, repository.allWorkoutSessions, repository.allCardioSessions, repository.allBodyMeasurements, _health) { profile, workouts, cardio, measures, health ->
        val local = DashboardEngine.home(profile, workouts, cardio, measures)
        local.copy(currentWeightKg = health.weightKg ?: local.currentWeightKg)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val evolution: StateFlow<EvolutionTrend?> = combine(repository.allWorkoutSessions, repository.allCardioSessions, repository.allBodyMeasurements, repository.allProgressPhotos) { workouts, cardio, measures, photos ->
        DashboardEngine.evolution(workouts, cardio, measures, photos)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /**
     * Fonte real do volume muscular: os WorkoutExercisePlan persistidos em exercisesDoneJson
     * são convertidos nos mesmos ExerciseExecutionRecord usados pelo histórico do treino.
     */
    val muscleVolumes: StateFlow<List<MuscleVolume>> = repository.allWorkoutSessions
        .map { sessions ->
            val records = sessions
                .asSequence()
                .filter { it.status == SessionStatus.COMPLETED }
                .flatMap { session ->
                    val plans = runCatching { plansAdapter.fromJson(session.exercisesDoneJson).orEmpty() }
                        .getOrDefault(emptyList())
                    plans.asSequence()
                        .filter { plan -> plan.sets.any { it.isCompleted } }
                        .map { plan ->
                            ExerciseExecutionRecord(
                                sessionId = session.id,
                                workoutTitle = session.title,
                                sessionDateEpochDay = session.dateEpochDay,
                                sessionStartTimeMillis = session.startTimeMillis,
                                location = session.location,
                                exerciseId = plan.exerciseId,
                                exerciseName = plan.exerciseName,
                                muscleGroup = plan.muscleGroup,
                                sets = plan.sets,
                                notes = plan.notes,
                                targetRestSeconds = plan.targetRestSeconds
                            )
                        }
                }
                .toList()
            DashboardEngine.muscleVolume(records)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val readiness: StateFlow<ReadinessResult?> = combine(repository.allWorkoutSessions, repository.allCardioSessions, _checkInHistory) { workouts, cardio, history ->
        val today = history.firstOrNull() ?: return@combine null
        ReadinessEngine.calculate(today, TrainingLoadCalculator.currentWeek(workouts, cardio), TrainingLoadCalculator.previousWeek(workouts, cardio))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init { refreshHealthConnect() }

    fun refreshHealthConnect() = viewModelScope.launch {
        runCatching { healthReader.readToday() }.onSuccess { _health.value = it }
    }

    fun saveCheckIn(checkIn: DailyCheckIn) {
        checkIns.save(checkIn)
        _checkInHistory.value = checkIns.history()
    }

    fun healthPermissions(): Set<String> = healthReader.permissions
}
