package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DefaultFitnessData
import com.example.data.model.BodyMeasurement
import com.example.data.model.CardioSession
import com.example.data.model.CardioType
import com.example.data.model.Exercise
import com.example.data.model.ExerciseSetEntry
import com.example.data.model.FitnessGoal
import com.example.data.model.IntensityLevel
import com.example.data.model.SessionStatus
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutExercisePlan
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutTemplate
import com.example.data.repository.FitnessRepository
import com.example.ui.components.DateUtils
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActiveWorkoutState(
    val isActive: Boolean = false,
    val templateId: Long? = null,
    val title: String = "",
    val location: String = "Academia Smart Fit",
    val durationSeconds: Int = 0,
    val exercises: List<WorkoutExercisePlan> = emptyList(),
    val perceivedExertion: Int = 7,
    val notes: String = "",
    val restTimerVisible: Boolean = false,
    val restTimerRemainingSeconds: Int = 0,
    val restTimerTotalSeconds: Int = 60,
    val restTimerPaused: Boolean = false
)

data class ActiveCardioState(
    val isActive: Boolean = false,
    val type: CardioType = CardioType.BICICLETA_INDOOR,
    val durationSeconds: Int = 0,
    val distanceKm: Double = 0.0,
    val intensity: IntensityLevel = IntensityLevel.MODERADA,
    val location: String = "Academia Smart Fit",
    val caloriesBurned: Int = 0
)

class FitnessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitnessRepository
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val planListType = Types.newParameterizedType(List::class.java, WorkoutExercisePlan::class.java)
    private val plansAdapter = moshi.adapter<List<WorkoutExercisePlan>>(planListType)

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = FitnessRepository(db.fitnessDao())
    }

    val workoutTemplates: StateFlow<List<WorkoutTemplate>> = repository.allWorkoutTemplates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExercises: StateFlow<List<Exercise>> = repository.allExercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWorkoutSessions: StateFlow<List<WorkoutSession>> = repository.allWorkoutSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCardioSessions: StateFlow<List<CardioSession>> = repository.allCardioSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DefaultFitnessData.getDefaultUserProfile())

    val allBodyMeasurements: StateFlow<List<BodyMeasurement>> = repository.allBodyMeasurements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Active Workout State ---
    private val _activeWorkout = MutableStateFlow(ActiveWorkoutState())
    val activeWorkout: StateFlow<ActiveWorkoutState> = _activeWorkout.asStateFlow()

    private var workoutTimerJob: Job? = null
    private var restTimerJob: Job? = null

    // --- Active Cardio State ---
    private val _activeCardio = MutableStateFlow(ActiveCardioState())
    val activeCardio: StateFlow<ActiveCardioState> = _activeCardio.asStateFlow()
    private var cardioTimerJob: Job? = null

    // --- AI State ---
    private val _isAIEvaluating = MutableStateFlow(false)
    val isAIEvaluating: StateFlow<Boolean> = _isAIEvaluating.asStateFlow()

    private val _lastAiEvaluation = MutableStateFlow<String?>(null)
    val lastAiEvaluation: StateFlow<String?> = _lastAiEvaluation.asStateFlow()

    private val _aiCoachAdvice = MutableStateFlow<String?>(null)
    val aiCoachAdvice: StateFlow<String?> = _aiCoachAdvice.asStateFlow()

    // --- Active Workout Functions ---
    fun startWorkoutFromTemplate(template: WorkoutTemplate, location: String) {
        val parsedPlans = try {
            plansAdapter.fromJson(template.exercisesJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        // Reset completion status for fresh session
        val freshPlans = parsedPlans.map { plan ->
            plan.copy(
                sets = plan.sets.map { set ->
                    set.copy(isCompleted = false)
                }
            )
        }

        _activeWorkout.value = ActiveWorkoutState(
            isActive = true,
            templateId = template.id,
            title = template.title,
            location = location,
            durationSeconds = 0,
            exercises = freshPlans,
            perceivedExertion = 7,
            notes = "",
            restTimerVisible = false
        )

        startWorkoutDurationTimer()
    }

    fun startCustomWorkout(title: String, location: String, selectedExercises: List<Exercise>, defaultRest: Int = 60) {
        val plans = selectedExercises.map { ex ->
            val setList = (1..ex.defaultSets).map { setNum ->
                ExerciseSetEntry(
                    setNumber = setNum,
                    weightKg = 20.0,
                    reps = ex.defaultReps,
                    isCompleted = false,
                    restSeconds = ex.defaultRestSeconds
                )
            }
            WorkoutExercisePlan(
                exerciseId = ex.id,
                exerciseName = ex.name,
                muscleGroup = ex.muscleGroup.displayName,
                sets = setList,
                targetRestSeconds = ex.defaultRestSeconds,
                notes = ex.executionTips
            )
        }

        _activeWorkout.value = ActiveWorkoutState(
            isActive = true,
            templateId = null,
            title = title.ifBlank { "Treino Personalizado" },
            location = location,
            durationSeconds = 0,
            exercises = plans,
            perceivedExertion = 7,
            notes = "",
            restTimerVisible = false
        )

        startWorkoutDurationTimer()
    }

    private fun startWorkoutDurationTimer() {
        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (_activeWorkout.value.isActive) {
                delay(1000)
                _activeWorkout.value = _activeWorkout.value.copy(
                    durationSeconds = _activeWorkout.value.durationSeconds + 1
                )
            }
        }
    }

    fun updateSet(exerciseIndex: Int, setIndex: Int, weight: Double, reps: Int, completed: Boolean, autoRest: Boolean = true) {
        val current = _activeWorkout.value
        val updatedExercises = current.exercises.toMutableList()
        if (exerciseIndex in updatedExercises.indices) {
            val plan = updatedExercises[exerciseIndex]
            val updatedSets = plan.sets.toMutableList()
            if (setIndex in updatedSets.indices) {
                val oldSet = updatedSets[setIndex]
                val wasCompleted = oldSet.isCompleted
                updatedSets[setIndex] = oldSet.copy(
                    weightKg = weight,
                    reps = reps,
                    isCompleted = completed
                )
                updatedExercises[exerciseIndex] = plan.copy(sets = updatedSets)
                _activeWorkout.value = current.copy(exercises = updatedExercises)

                // Trigger rest countdown when completing a set for the first time
                if (completed && !wasCompleted && autoRest) {
                    val restTime = if (oldSet.restSeconds > 0) oldSet.restSeconds else plan.targetRestSeconds
                    triggerRestTimer(restTime)
                }
            }
        }
    }

    fun addSetToExercise(exerciseIndex: Int) {
        val current = _activeWorkout.value
        val updatedExercises = current.exercises.toMutableList()
        if (exerciseIndex in updatedExercises.indices) {
            val plan = updatedExercises[exerciseIndex]
            val lastSet = plan.sets.lastOrNull()
            val newSet = ExerciseSetEntry(
                setNumber = plan.sets.size + 1,
                weightKg = lastSet?.weightKg ?: 20.0,
                reps = lastSet?.reps ?: 10,
                isCompleted = false,
                restSeconds = plan.targetRestSeconds
            )
            updatedExercises[exerciseIndex] = plan.copy(sets = plan.sets + newSet)
            _activeWorkout.value = current.copy(exercises = updatedExercises)
        }
    }

    fun removeSetFromExercise(exerciseIndex: Int, setIndex: Int) {
        val current = _activeWorkout.value
        val updatedExercises = current.exercises.toMutableList()
        if (exerciseIndex in updatedExercises.indices) {
            val plan = updatedExercises[exerciseIndex]
            if (plan.sets.size > 1 && setIndex in plan.sets.indices) {
                val updatedSets = plan.sets.toMutableList().apply { removeAt(setIndex) }
                // Re-index
                val reindexedSets = updatedSets.mapIndexed { idx, setEntry ->
                    setEntry.copy(setNumber = idx + 1)
                }
                updatedExercises[exerciseIndex] = plan.copy(sets = reindexedSets)
                _activeWorkout.value = current.copy(exercises = updatedExercises)
            }
        }
    }

    fun addExerciseToActiveWorkout(exercise: Exercise) {
        val current = _activeWorkout.value
        val setList = (1..exercise.defaultSets).map {
            ExerciseSetEntry(
                setNumber = it,
                weightKg = 20.0,
                reps = exercise.defaultReps,
                isCompleted = false,
                restSeconds = exercise.defaultRestSeconds
            )
        }
        val newPlan = WorkoutExercisePlan(
            exerciseId = exercise.id,
            exerciseName = exercise.name,
            muscleGroup = exercise.muscleGroup.displayName,
            sets = setList,
            targetRestSeconds = exercise.defaultRestSeconds,
            notes = exercise.executionTips
        )
        _activeWorkout.value = current.copy(exercises = current.exercises + newPlan)
    }

    fun updateActiveLocation(location: String) {
        _activeWorkout.value = _activeWorkout.value.copy(location = location)
    }

    fun updateActiveRPE(rpe: Int) {
        _activeWorkout.value = _activeWorkout.value.copy(perceivedExertion = rpe)
    }

    fun updateActiveNotes(notes: String) {
        _activeWorkout.value = _activeWorkout.value.copy(notes = notes)
    }

    // --- Rest Timer Functions ---
    fun triggerRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        val total = if (seconds > 0) seconds else 60
        _activeWorkout.value = _activeWorkout.value.copy(
            restTimerVisible = true,
            restTimerRemainingSeconds = total,
            restTimerTotalSeconds = total,
            restTimerPaused = false
        )

        restTimerJob = viewModelScope.launch {
            while (_activeWorkout.value.restTimerRemainingSeconds > 0) {
                delay(1000)
                if (!_activeWorkout.value.restTimerPaused) {
                    val remaining = _activeWorkout.value.restTimerRemainingSeconds - 1
                    _activeWorkout.value = _activeWorkout.value.copy(
                        restTimerRemainingSeconds = remaining
                    )
                }
            }
            // Finished
            delay(500)
            _activeWorkout.value = _activeWorkout.value.copy(restTimerVisible = false)
        }
    }

    fun addRestSeconds(secondsToAdd: Int) {
        val current = _activeWorkout.value
        val newRemaining = (current.restTimerRemainingSeconds + secondsToAdd).coerceAtLeast(0)
        if (newRemaining <= 0) {
            dismissRestTimer()
            return
        }
        val newTotal = maxOf(current.restTimerTotalSeconds, newRemaining)
        _activeWorkout.value = current.copy(
            restTimerRemainingSeconds = newRemaining,
            restTimerTotalSeconds = newTotal
        )
    }

    fun toggleRestTimerPause() {
        val current = _activeWorkout.value
        _activeWorkout.value = current.copy(restTimerPaused = !current.restTimerPaused)
    }

    fun dismissRestTimer() {
        restTimerJob?.cancel()
        _activeWorkout.value = _activeWorkout.value.copy(restTimerVisible = false)
    }

    // --- Finish Workout ---
    fun finishActiveWorkout(onSaved: (WorkoutSession) -> Unit = {}) {
        val current = _activeWorkout.value
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()

        // Calculate total lifted weight
        var totalWeight = 0.0
        current.exercises.forEach { plan ->
            plan.sets.forEach { set ->
                if (set.isCompleted) {
                    totalWeight += (set.weightKg * set.reps)
                }
            }
        }

        val exercisesJson = try {
            plansAdapter.toJson(current.exercises)
        } catch (e: Exception) {
            "[]"
        }

        val profile = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()
        val durationMin = (current.durationSeconds / 60).coerceAtLeast(1)
        val estimatedCal = ((6.0 * 3.5 * profile.currentWeightKg / 200.0) * durationMin).toInt()

        val session = WorkoutSession(
            templateId = current.templateId,
            title = current.title.ifBlank { "Treino de Musculação" },
            dateEpochDay = DateUtils.todayEpochDay(),
            startTimeMillis = System.currentTimeMillis() - (current.durationSeconds * 1000L),
            endTimeMillis = System.currentTimeMillis(),
            durationSeconds = current.durationSeconds,
            location = current.location,
            status = SessionStatus.COMPLETED,
            totalWeightLiftedKg = totalWeight,
            estimatedCalories = estimatedCal,
            perceivedExertion = current.perceivedExertion,
            notes = current.notes,
            exercisesDoneJson = exercisesJson
        )

        viewModelScope.launch {
            val insertedId = repository.saveWorkoutSession(session)
            val savedSession = session.copy(id = insertedId)

            _activeWorkout.value = ActiveWorkoutState() // Reset
            onSaved(savedSession)

            // Request AI Evaluation
            evaluateSessionWithAI(savedSession, profile)
        }
    }

    fun discardActiveWorkout() {
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()
        _activeWorkout.value = ActiveWorkoutState()
    }

    // --- Live Cardio Session Functions ---
    fun startLiveCardio(type: CardioType, location: String, intensity: IntensityLevel) {
        cardioTimerJob?.cancel()
        _activeCardio.value = ActiveCardioState(
            isActive = true,
            type = type,
            durationSeconds = 0,
            distanceKm = 0.0,
            intensity = intensity,
            location = location,
            caloriesBurned = 0
        )

        cardioTimerJob = viewModelScope.launch {
            while (_activeCardio.value.isActive) {
                delay(1000)
                val newSec = _activeCardio.value.durationSeconds + 1
                val profile = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()
                val met = when (_activeCardio.value.intensity) {
                    IntensityLevel.LEVE -> _activeCardio.value.type.metLight
                    IntensityLevel.MODERADA -> _activeCardio.value.type.metModerate
                    IntensityLevel.INTENSA -> _activeCardio.value.type.metIntense
                }
                val cal = ((met * 3.5 * profile.currentWeightKg / 200.0) * (newSec / 60.0)).toInt()

                _activeCardio.value = _activeCardio.value.copy(
                    durationSeconds = newSec,
                    caloriesBurned = cal
                )
            }
        }
    }

    fun finishLiveCardio(distanceKm: Double?, heartRate: Int?, notes: String, onFinished: (CardioSession) -> Unit = {}) {
        cardioTimerJob?.cancel()
        val current = _activeCardio.value
        val durationMin = (current.durationSeconds / 60).coerceAtLeast(1)
        val profile = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()

        val met = when (current.intensity) {
            IntensityLevel.LEVE -> current.type.metLight
            IntensityLevel.MODERADA -> current.type.metModerate
            IntensityLevel.INTENSA -> current.type.metIntense
        }
        val cal = ((met * 3.5 * profile.currentWeightKg / 200.0) * durationMin).toInt()

        val cardioSession = CardioSession(
            type = current.type,
            dateEpochDay = DateUtils.todayEpochDay(),
            timestampMillis = System.currentTimeMillis(),
            durationMinutes = durationMin,
            distanceKm = distanceKm ?: if (current.distanceKm > 0) current.distanceKm else null,
            avgHeartRateBpm = heartRate,
            intensity = current.intensity,
            location = current.location,
            caloriesBurned = cal,
            notes = notes
        )

        viewModelScope.launch {
            val id = repository.saveCardioSession(cardioSession)
            val savedCardio = cardioSession.copy(id = id)
            _activeCardio.value = ActiveCardioState()
            onFinished(savedCardio)

            // AI cardio evaluation
            evaluateCardioWithAI(savedCardio, profile)
        }
    }

    fun discardLiveCardio() {
        cardioTimerJob?.cancel()
        _activeCardio.value = ActiveCardioState()
    }

    fun logManualCardio(cardio: CardioSession) {
        viewModelScope.launch {
            val id = repository.saveCardioSession(cardio)
            val profile = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()
            evaluateCardioWithAI(cardio.copy(id = id), profile)
        }
    }

    fun deleteCardioSession(id: Long) {
        viewModelScope.launch {
            repository.deleteCardioSessionById(id)
        }
    }

    // --- Schedule & Agenda ---
    fun scheduleWorkout(title: String, epochDay: Long, location: String, templateId: Long? = null) {
        viewModelScope.launch {
            val session = WorkoutSession(
                templateId = templateId,
                title = title,
                dateEpochDay = epochDay,
                location = location,
                status = SessionStatus.SCHEDULED,
                durationSeconds = 0,
                totalWeightLiftedKg = 0.0,
                estimatedCalories = 0
            )
            repository.saveWorkoutSession(session)
        }
    }

    fun deleteWorkoutSession(id: Long) {
        viewModelScope.launch {
            repository.deleteWorkoutSessionById(id)
        }
    }

    // --- Custom Template Creation ---
    fun createAndSaveWorkoutTemplate(
        title: String,
        subtitle: String,
        category: WorkoutCategory,
        exercises: List<WorkoutExercisePlan>,
        description: String,
        durationMin: Int = 50,
        restSec: Int = 60
    ) {
        viewModelScope.launch {
            val json = plansAdapter.toJson(exercises)
            val template = WorkoutTemplate(
                title = title,
                subtitle = subtitle,
                category = category,
                defaultRestSeconds = restSec,
                executionDurationMinutes = durationMin,
                isPreset = false,
                exercisesJson = json,
                description = description
            )
            repository.saveWorkoutTemplate(template)
        }
    }

    fun deleteWorkoutTemplate(id: Long) {
        viewModelScope.launch {
            repository.deleteWorkoutTemplateById(id)
        }
    }

    // --- User Profile & Measurements ---
    fun updateUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }

    fun addBodyMeasurement(measurement: BodyMeasurement) {
        viewModelScope.launch {
            repository.saveBodyMeasurement(measurement)
            // Also update current weight on user profile
            val currentProf = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()
            repository.saveUserProfile(currentProf.copy(currentWeightKg = measurement.weightKg))
        }
    }

    fun deleteBodyMeasurement(id: Long) {
        viewModelScope.launch {
            repository.deleteBodyMeasurementById(id)
        }
    }

    // --- AI Evaluations ---
    fun evaluateSessionWithAI(session: WorkoutSession, profile: UserProfile) {
        viewModelScope.launch {
            _isAIEvaluating.value = true
            val aiResult = repository.evaluateWorkoutWithAI(session, profile)
            _lastAiEvaluation.value = aiResult
            _isAIEvaluating.value = false

            // Update session with AI feedback
            repository.updateWorkoutSession(session.copy(aiCaloricEvaluation = aiResult))
        }
    }

    fun evaluateCardioWithAI(cardio: CardioSession, profile: UserProfile) {
        viewModelScope.launch {
            _isAIEvaluating.value = true
            val result = repository.evaluateCardioWithAI(cardio, profile)
            _lastAiEvaluation.value = result
            _isAIEvaluating.value = false
        }
    }

    fun requestAICoachAdvice() {
        val profile = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()
        val sessions = allWorkoutSessions.value
        val cardios = allCardioSessions.value
        val count = sessions.filter { it.status == SessionStatus.COMPLETED }.size
        val totalKg = sessions.sumOf { it.totalWeightLiftedKg }
        val cardioMin = cardios.sumOf { it.durationMinutes }

        viewModelScope.launch {
            _isAIEvaluating.value = true
            val advice = repository.getAICoachAdvice(profile, count, totalKg, cardioMin)
            _aiCoachAdvice.value = advice
            _isAIEvaluating.value = false
        }
    }
}
