package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DefaultFitnessData
import com.example.data.model.BodyMeasurement
import com.example.data.model.CardioSession
import com.example.data.model.CardioType
import com.example.data.model.DailyWorkoutSuggestion
import com.example.data.model.Exercise
import com.example.data.model.ExerciseEvolutionSummary
import com.example.data.model.ExerciseExecutionRecord
import com.example.data.model.ExerciseSetEntry
import com.example.data.model.FitnessGoal
import com.example.data.model.IntensityLevel
import com.example.data.model.ProgressionSuggestion
import com.example.data.model.ProgressPhoto
import com.example.data.model.SessionStatus
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutExercisePlan
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutTemplate
import com.example.data.model.AgendaCustomAppointment
import com.example.data.model.AICoachMessage
import com.example.data.model.AICoachSender
import com.example.data.model.AIWorkoutPlanResult
import com.example.data.model.VolumeNutritionEvaluationResult
import com.example.data.model.ExerciseExecutionGuideResult
import com.example.data.model.GamificationOverview
import com.example.data.model.MedalRarity
import com.example.data.model.GoalPeriod
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import com.example.data.model.WorkoutReminderSettings
import com.example.util.WorkoutReminderManager
import com.example.util.HealthConnectManager
import com.example.util.HealthConnectAvailability
import com.example.util.HealthConnectDailyMetrics
import com.example.util.HealthSyncResult
import com.example.data.repository.FitnessRepository
import com.example.ui.components.DateUtils
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.data.local.OutdoorCardioPersistence
import com.example.data.model.OutdoorSessionState
import com.example.data.model.OutdoorTrackingStatus
import com.example.service.OutdoorLocationService
import com.example.util.PhotoStorageHelper
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.example.data.model.MuscleGroup
import com.example.data.model.SetTag
import com.example.ui.components.MuscleWeeklyVolume
import com.example.utils.TTSVoiceManager
import com.example.domain.gamification.Phase7JourneyEngine
import com.example.domain.gamification.WorkoutJourneyImpact
import com.example.data.model.JourneyUnlock
import org.json.JSONObject

data class ProgressiveOverloadSuggestion(
    val exerciseName: String,
    val previousWeightKg: Double,
    val previousReps: Int,
    val suggestedWeightKg: Double,
    val suggestedReps: Int,
    val strategy: String,
    val tip: String,
    val estimated1RM: Double
)

data class ActiveWorkoutState(
    val isActive: Boolean = false,
    val templateId: Long? = null,
    val scheduledSessionId: Long? = null,
    val agendaAppointmentId: String? = null,
    val scheduledDateEpochDay: Long? = null,
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
    val isPaused: Boolean = false,
    val type: CardioType = CardioType.BICICLETA_INDOOR,
    val durationSeconds: Int = 0,
    val distanceKm: Double = 0.0,
    val intensity: IntensityLevel = IntensityLevel.MODERADA,
    val location: String = "Academia Smart Fit",
    val caloriesBurned: Int = 0,
    val speedKmh: Double = 0.0,
    val paceMinKm: Double = 0.0,
    val gpsEnabled: Boolean = false,
    val gpsAccuracyMeters: Float? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val gpsPointsCount: Int = 0,
    val targetMinutes: Int? = null
)

class FitnessViewModel(application: Application) : AndroidViewModel(application) {

    private var outdoorServiceBinder: OutdoorLocationService.OutdoorLocationBinder? = null
    private val _outdoorSessionState = MutableStateFlow(OutdoorSessionState())
    val outdoorSessionState: StateFlow<OutdoorSessionState> = _outdoorSessionState.asStateFlow()

    private val outdoorServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            outdoorServiceBinder = service as? OutdoorLocationService.OutdoorLocationBinder
            outdoorServiceBinder?.let { binder ->
                viewModelScope.launch {
                    binder.sessionState.collect { state ->
                        _outdoorSessionState.value = state
                        syncActiveCardioWithOutdoorState(state)
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            outdoorServiceBinder = null
        }
    }

    private val repository: FitnessRepository
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val planListType = Types.newParameterizedType(List::class.java, WorkoutExercisePlan::class.java)
    private val plansAdapter = moshi.adapter<List<WorkoutExercisePlan>>(planListType)

    // Persistent Preferences: Keep Screen On & Backup Unlocked Medals
    private val prefs = application.getSharedPreferences("fitpr09_prefs", Context.MODE_PRIVATE)
    private val _keepScreenOn = MutableStateFlow(prefs.getBoolean("pref_keep_screen_on", true))
    val keepScreenOn: StateFlow<Boolean> = _keepScreenOn.asStateFlow()

    fun toggleKeepScreenOn() {
        val newVal = !_keepScreenOn.value
        _keepScreenOn.value = newVal
        prefs.edit().putBoolean("pref_keep_screen_on", newVal).apply()
    }

    fun setKeepScreenOn(enabled: Boolean) {
        _keepScreenOn.value = enabled
        prefs.edit().putBoolean("pref_keep_screen_on", enabled).apply()
    }

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = FitnessRepository(db.fitnessDao())
        viewModelScope.launch(Dispatchers.IO) {
            val exercises = DefaultFitnessData.getDefaultExercises()
            db.fitnessDao().insertExercises(exercises)
            importExerciseLibraryFromAssets(db.fitnessDao())
            val templates = DefaultFitnessData.getDefaultWorkoutTemplates(exercises)
            templates.forEach { db.fitnessDao().insertWorkoutTemplate(it) }

            // Ensure predefined medals are populated without overwriting unlocked ones
            val defaultMedals = DefaultFitnessData.getDefaultMedals()
            repository.insertMedals(defaultMedals)

            // Restore any backed-up unlocked medals so achievements never disappear
            val backedUpUnlocked = prefs.getStringSet("unlocked_medals_backup", emptySet()) ?: emptySet()
            val todayEpoch = DateUtils.todayEpochDay()
            backedUpUnlocked.forEach { medalId ->
                repository.unlockMedal(medalId, todayEpoch)
            }

            // Ensure User Profile is present and restored from backup if needed
            val currentProfile = db.fitnessDao().getUserProfile().firstOrNull()
            if (currentProfile == null) {
                val backupJson = prefs.getString("backup_user_profile_json", null)
                val restoredProfile = if (!backupJson.isNullOrBlank()) {
                    try {
                        moshi.adapter(UserProfile::class.java).fromJson(backupJson)
                    } catch (_: Exception) { null }
                } else null

                val profileToSave = restoredProfile ?: DefaultFitnessData.getDefaultUserProfile()
                repository.saveUserProfile(profileToSave)
                persistUserProfileBackup(profileToSave)
            } else {
                persistUserProfileBackup(currentProfile)
            }

            // Ensure Workout Sessions are restored if database was newly created
            val sessionsInDb = db.fitnessDao().getAllWorkoutSessions().firstOrNull()
            if (sessionsInDb.isNullOrEmpty()) {
                val backupSessionsJson = prefs.getString("backup_workout_sessions_json", null)
                if (!backupSessionsJson.isNullOrBlank()) {
                    try {
                        val listType = Types.newParameterizedType(List::class.java, WorkoutSession::class.java)
                        val adapter: JsonAdapter<List<WorkoutSession>> = moshi.adapter(listType)
                        val restoredSessions = adapter.fromJson(backupSessionsJson)
                        restoredSessions?.forEach { repository.saveWorkoutSession(it) }
                    } catch (_: Exception) {}
                }
            } else {
                backupWorkoutSessions()
            }

            // Ensure Body Measurements are restored if database was newly created
            val measurementsInDb = db.fitnessDao().getAllBodyMeasurements().firstOrNull()
            if (measurementsInDb.isNullOrEmpty()) {
                val backupMeasurementsJson = prefs.getString("backup_body_measurements_json", null)
                if (!backupMeasurementsJson.isNullOrBlank()) {
                    try {
                        val listType = Types.newParameterizedType(List::class.java, BodyMeasurement::class.java)
                        val adapter: JsonAdapter<List<BodyMeasurement>> = moshi.adapter(listType)
                        val restoredMeasurements = adapter.fromJson(backupMeasurementsJson)
                        restoredMeasurements?.forEach { repository.saveBodyMeasurement(it) }
                    } catch (_: Exception) {}
                }
            } else {
                backupBodyMeasurements()
            }

            syncGamificationAndGoals()
            checkHealthConnectStatus()
            initCustomAppointmentsIfNeeded()
        }

        try {
            val serviceIntent = Intent(application, OutdoorLocationService::class.java)
            application.bindService(serviceIntent, outdoorServiceConnection, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            Log.e("FitnessViewModel", "Error binding to OutdoorLocationService: ${e.message}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            getApplication<Application>().unbindService(outdoorServiceConnection)
        } catch (_: Exception) {}
    }

    // --- Backup & Data Migration ---
    val backupManager by lazy { com.example.data.backup.DataBackupManager(getApplication(), AppDatabase.getDatabase(getApplication(), viewModelScope)) }

    val workoutTemplates: StateFlow<List<WorkoutTemplate>> = repository.allWorkoutTemplates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteWorkoutTemplates: StateFlow<List<WorkoutTemplate>> = repository.favoriteWorkoutTemplates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customWorkoutTemplates: StateFlow<List<WorkoutTemplate>> = repository.customWorkoutTemplates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExercises: StateFlow<List<Exercise>> = repository.allExercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWorkoutSessions: StateFlow<List<WorkoutSession>> = repository.allWorkoutSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCardioSessions: StateFlow<List<CardioSession>> = repository.allCardioSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun loadCachedProfile(): UserProfile {
        val backupJson = prefs.getString("backup_user_profile_json", null)
        if (!backupJson.isNullOrBlank()) {
            try {
                moshi.adapter(UserProfile::class.java).fromJson(backupJson)?.let { return it }
            } catch (_: Exception) {}
        }
        val cachedName = prefs.getString("cached_athlete_name", null)
        if (!cachedName.isNullOrBlank()) {
            return DefaultFitnessData.getDefaultUserProfile().copy(
                name = cachedName,
                photoUri = prefs.getString("cached_athlete_photo", null),
                currentWeightKg = prefs.getFloat("cached_athlete_weight", 78.5f).toDouble(),
                heightCm = prefs.getFloat("cached_athlete_height", 178f).toDouble()
            )
        }
        return DefaultFitnessData.getDefaultUserProfile()
    }

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .map { it ?: loadCachedProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), loadCachedProfile())

    // --- Daily Workout Suggestion (Intelligent muscle recovery rotation) ---
    private val _manualSuggestedTemplate = MutableStateFlow<WorkoutTemplate?>(null)

    val dailyWorkoutSuggestion: StateFlow<DailyWorkoutSuggestion> = combine(
        repository.allWorkoutTemplates,
        repository.allWorkoutSessions,
        repository.userProfile,
        _manualSuggestedTemplate
    ) { templates, sessions, profile, manualOverride ->
        buildDailyWorkoutSuggestion(templates, sessions, profile, manualOverride)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DailyWorkoutSuggestion(
            template = null,
            title = "Treino do Dia",
            subtitle = "Sugestão inteligente com recuperação muscular",
            muscleGroups = listOf(MuscleGroup.PEITO, MuscleGroup.TRICEPS),
            explanationReason = "Calculando a melhor divisão para o seu dia.",
            estimatedDurationMinutes = 45,
            intensity = IntensityLevel.MODERADA,
            dayOfWeekName = "Hoje"
        )
    )

    fun overrideDailyWorkoutSuggestion(template: WorkoutTemplate) {
        _manualSuggestedTemplate.value = template
    }

    fun resetDailyWorkoutSuggestion() {
        _manualSuggestedTemplate.value = null
    }

    private fun buildDailyWorkoutSuggestion(
        templates: List<WorkoutTemplate>,
        sessions: List<WorkoutSession>,
        profile: UserProfile?,
        manualOverride: WorkoutTemplate?
    ): DailyWorkoutSuggestion {
        if (manualOverride != null) {
            val alternatives = templates.filter { it.id != manualOverride.id }
            return DailyWorkoutSuggestion(
                template = manualOverride,
                title = manualOverride.title,
                subtitle = "Substituição selecionada por você",
                muscleGroups = listOf(MuscleGroup.PEITO, MuscleGroup.TRICEPS),
                explanationReason = "💡 Treino personalizado selecionado para a sua sessão de hoje.",
                estimatedDurationMinutes = manualOverride.executionDurationMinutes,
                intensity = IntensityLevel.INTENSA,
                dayOfWeekName = "Hoje",
                alternativeTemplates = alternatives
            )
        }

        val today = java.time.LocalDate.now()
        val dayOfWeek = today.dayOfWeek.value // 1 (Mon) .. 7 (Sun)
        val dayName = when (dayOfWeek) {
            1 -> "Segunda-feira"
            2 -> "Terça-feira"
            3 -> "Quarta-feira"
            4 -> "Quinta-feira"
            5 -> "Sexta-feira"
            6 -> "Sábado"
            else -> "Domingo"
        }

        val (focusName, targetGroups, keywords, explanation) = when (dayOfWeek) {
            1 -> Quadruple(
                "Push: Peito, Ombros & Tríceps",
                listOf(MuscleGroup.PEITO, MuscleGroup.OMBROS, MuscleGroup.TRICEPS),
                listOf("Peito", "Push", "A", "Iniciante - Treino A"),
                "💡 Segunda-feira com alta intensidade mecânica. Foco em empurrar com Peito, Ombros e Tríceps com descanso completo no fim de semana."
            )
            2 -> Quadruple(
                "Pull: Costas & Bíceps",
                listOf(MuscleGroup.COSTAS, MuscleGroup.BICEPS),
                listOf("Costas", "Pull", "B", "Iniciante - Treino B"),
                "💡 Puxadas completas para dorsais e bíceps. Grande ativação de cadeia posterior superior para postura e força."
            )
            3 -> Quadruple(
                "Legs: Quadríceps, Posterior & Panturrilhas",
                listOf(MuscleGroup.QUADRICEPS, MuscleGroup.POSTERIOR_GLUTEOS, MuscleGroup.PANTURRILHA),
                listOf("Pernas", "Legs", "C", "Inferiores"),
                "💡 Membros inferiores: estímulo anabólico com agachamentos, leg press e flexoras para força e gasto calórico."
            )
            4 -> Quadruple(
                "Ombros & Abdômen",
                listOf(MuscleGroup.OMBROS, MuscleGroup.ABDOMEN),
                listOf("Ombros", "Deltoides", "D", "Push"),
                "💡 Foco em largura escapular e estabilidade do core, enquanto braços e pernas se recuperam."
            )
            5 -> Quadruple(
                "Superiores: Peito & Costas",
                listOf(MuscleGroup.PEITO, MuscleGroup.COSTAS),
                listOf("Peito", "Superiores", "Upper", "A"),
                "💡 Membros superiores com alta densidade e volume para fechar a semana com aceleração metabólica."
            )
            6 -> Quadruple(
                "Braços Completos: Bíceps & Tríceps",
                listOf(MuscleGroup.BICEPS, MuscleGroup.TRICEPS),
                listOf("Braços", "Bíceps", "Tríceps", "B"),
                "💡 Treino focado em bíceps, tríceps e pegada com repetições controladas e pump muscular."
            )
            else -> Quadruple(
                "Recuperação Ativa & Pernas Leve",
                listOf(MuscleGroup.QUADRICEPS, MuscleGroup.POSTERIOR_GLUTEOS),
                listOf("Funcional", "Cardio", "Alongamento", "A"),
                "💡 Domingo para mobilidade, corrida/caminhada leve de 30-40 min ou restauração metabólica."
            )
        }

        val matchedTemplate = templates.find { t ->
            keywords.any { k -> t.title.contains(k, ignoreCase = true) }
        } ?: templates.firstOrNull { it.isFavorite } ?: templates.firstOrNull()

        val alternatives = templates.filter { it.id != matchedTemplate?.id }

        return DailyWorkoutSuggestion(
            template = matchedTemplate,
            title = matchedTemplate?.title ?: focusName,
            subtitle = focusName,
            muscleGroups = targetGroups,
            explanationReason = explanation,
            estimatedDurationMinutes = matchedTemplate?.executionDurationMinutes ?: 45,
            intensity = IntensityLevel.INTENSA,
            dayOfWeekName = dayName,
            alternativeTemplates = alternatives
        )
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    val allBodyMeasurements: StateFlow<List<BodyMeasurement>> = repository.allBodyMeasurements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProgressPhotos: StateFlow<List<ProgressPhoto>> = repository.allProgressPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val initialProgressPhoto: StateFlow<ProgressPhoto?> = repository.initialProgressPhoto
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allExerciseTargets: StateFlow<List<com.example.data.model.ExercisePerformanceTarget>> = repository.allExerciseTargets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Meals & Nutrition (Gemini AI) ---
    val allMealLogs: StateFlow<List<com.example.data.model.MealLog>> = repository.allMealLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isAnalyzingMeal = MutableStateFlow(false)
    val isAnalyzingMeal: StateFlow<Boolean> = _isAnalyzingMeal.asStateFlow()

    private val _lastMealAnalysis = MutableStateFlow<com.example.data.model.MealAnalysisResult?>(null)
    val lastMealAnalysis: StateFlow<com.example.data.model.MealAnalysisResult?> = _lastMealAnalysis.asStateFlow()

    // --- Gamification & Medals ---
    val allMedals: StateFlow<List<com.example.data.model.UserMedal>> = repository.allMedals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gamificationOverview: StateFlow<GamificationOverview> = repository.allMedals.map { medals ->
        val unlockedMedals = medals.filter { it.isUnlocked }
        val totalXp = unlockedMedals.sumOf { it.xpReward }
        val level = 1 + (totalXp / 500)
        val levelXp = totalXp % 500
        val nextLevelXp = 500
        val levelProgress = levelXp.toFloat() / nextLevelXp.toFloat()

        val title = when {
            level >= 25 -> "Ouro Master da Superação Suprema ⚡"
            level >= 18 -> "Titã Imortal dos Pesos 👑"
            level >= 12 -> "Mestre da Força & Volume ⚔️"
            level >= 8 -> "Gladiador de Aço 🛡️"
            level >= 4 -> "Guerreiro de Ferro 🔥"
            else -> "Novato Determinado 🚀"
        }

        val bronze = unlockedMedals.count { it.rarity.contains("Bronze", ignoreCase = true) }
        val prata = unlockedMedals.count { it.rarity.contains("Prata", ignoreCase = true) }
        val ouro = unlockedMedals.count { it.rarity.equals("Ouro", ignoreCase = true) }
        val master = unlockedMedals.count { it.rarity.contains("Master", ignoreCase = true) || it.rarity.contains("Superação", ignoreCase = true) }

        GamificationOverview(
            totalXp = totalXp,
            currentLevel = level,
            currentLevelTitle = title,
            currentLevelXp = levelXp,
            nextLevelXp = nextLevelXp,
            levelProgressPercent = levelProgress,
            unlockedMedalsCount = unlockedMedals.size,
            totalMedalsCount = medals.size,
            bronzeCount = bronze,
            prataCount = prata,
            ouroCount = ouro,
            masterCount = master
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        GamificationOverview(
            totalXp = 450,
            currentLevel = 1,
            currentLevelTitle = "Novato Determinado 🚀",
            currentLevelXp = 450,
            nextLevelXp = 500,
            levelProgressPercent = 0.9f,
            unlockedMedalsCount = 3,
            totalMedalsCount = 20,
            bronzeCount = 2,
            prataCount = 1,
            ouroCount = 0,
            masterCount = 0
        )
    )

    private val _selectedMedalForDetail = MutableStateFlow<com.example.data.model.UserMedal?>(null)
    val selectedMedalForDetail: StateFlow<com.example.data.model.UserMedal?> = _selectedMedalForDetail.asStateFlow()

    private val _activeMedalUnlocked = MutableStateFlow<com.example.data.model.UserMedal?>(null)
    val activeMedalUnlocked: StateFlow<com.example.data.model.UserMedal?> = _activeMedalUnlocked.asStateFlow()

    private val _activePRCelebration = MutableStateFlow<com.example.data.model.PersonalRecordCelebration?>(null)
    val activePRCelebration: StateFlow<com.example.data.model.PersonalRecordCelebration?> = _activePRCelebration.asStateFlow()

    // --- Voice & Audio TTS Coach ---
    private val ttsVoiceManager = TTSVoiceManager(application)
    private val _isTtsVoiceEnabled = MutableStateFlow(true)
    val isTtsVoiceEnabled: StateFlow<Boolean> = _isTtsVoiceEnabled.asStateFlow()

    fun toggleTtsVoice() {
        _isTtsVoiceEnabled.value = !_isTtsVoiceEnabled.value
        ttsVoiceManager.isEnabled = _isTtsVoiceEnabled.value
        if (_isTtsVoiceEnabled.value) {
            ttsVoiceManager.speak("Voz do treinador ativada! Vamos com tudo!")
        }
    }

    fun testCoachVoice() {
        ttsVoiceManager.speakMotivation()
    }

    // --- Weekly Muscle Volume Heatmap Flow ---
    val weeklyMuscleVolumes: StateFlow<List<MuscleWeeklyVolume>> = repository.allWorkoutSessions.map { sessions ->
        val todayEpoch = DateUtils.todayEpochDay()
        val startOfWeekEpoch = todayEpoch - java.time.LocalDate.now().dayOfWeek.value + 1
        val weekSessions = sessions.filter { it.dateEpochDay >= startOfWeekEpoch && it.status == SessionStatus.COMPLETED }

        val setCountsByMuscle = mutableMapOf<MuscleGroup, Int>()
        MuscleGroup.entries.forEach { setCountsByMuscle[it] = 0 }

        weekSessions.forEach { session ->
            try {
                val plans = plansAdapter.fromJson(session.exercisesDoneJson) ?: emptyList()
                plans.forEach { plan ->
                    val matchedGroup = MuscleGroup.entries.find { it.displayName.equals(plan.muscleGroup, ignoreCase = true) }
                        ?: MuscleGroup.PEITO
                    val completedSets = plan.sets.count { it.isCompleted && it.setTag != SetTag.WARMUP }
                    setCountsByMuscle[matchedGroup] = (setCountsByMuscle[matchedGroup] ?: 0) + completedSets
                }
            } catch (_: Exception) {}
        }

        MuscleGroup.entries.map { group ->
            MuscleWeeklyVolume(
                muscleGroup = group,
                completedSetsThisWeek = setCountsByMuscle[group] ?: 0,
                targetMinSets = 10,
                targetMaxSets = 20
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Active Workout State ---
    private val _activeWorkout = MutableStateFlow(ActiveWorkoutState())
    val activeWorkout: StateFlow<ActiveWorkoutState> = _activeWorkout.asStateFlow()

    /** Último cálculo gerado automaticamente ao concluir um treino livre ou agendado. */
    private val _lastWorkoutJourneyImpact = MutableStateFlow<WorkoutJourneyImpact?>(null)
    val lastWorkoutJourneyImpact: StateFlow<WorkoutJourneyImpact?> = _lastWorkoutJourneyImpact.asStateFlow()
    val journeyUnlocks: StateFlow<List<JourneyUnlock>> = repository.journeyUnlocks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    // --- AI Workout Generator State ---
    private val _isGeneratingAIWorkout = MutableStateFlow(false)
    val isGeneratingAIWorkout: StateFlow<Boolean> = _isGeneratingAIWorkout.asStateFlow()

    private val _generatedAIWorkout = MutableStateFlow<AIWorkoutPlanResult?>(null)
    val generatedAIWorkout: StateFlow<AIWorkoutPlanResult?> = _generatedAIWorkout.asStateFlow()

    // --- Volume-Based AI Nutrition Evaluation ---
    private val _volumeNutritionEvaluation = MutableStateFlow<VolumeNutritionEvaluationResult?>(null)
    val volumeNutritionEvaluation: StateFlow<VolumeNutritionEvaluationResult?> = _volumeNutritionEvaluation.asStateFlow()

    private val _isEvaluatingVolumeNutrition = MutableStateFlow(false)
    val isEvaluatingVolumeNutrition: StateFlow<Boolean> = _isEvaluatingVolumeNutrition.asStateFlow()

    // --- Gemini AI Exercise Execution Guide ---
    private val _activeExerciseExecutionGuide = MutableStateFlow<ExerciseExecutionGuideResult?>(null)
    val activeExerciseExecutionGuide: StateFlow<ExerciseExecutionGuideResult?> = _activeExerciseExecutionGuide.asStateFlow()

    private val _isLoadingExerciseExecutionGuide = MutableStateFlow(false)
    val isLoadingExerciseExecutionGuide: StateFlow<Boolean> = _isLoadingExerciseExecutionGuide.asStateFlow()

    // --- AI Coach Chat Assistant State ---
    private val _isAICoachThinking = MutableStateFlow(false)
    val isAICoachThinking: StateFlow<Boolean> = _isAICoachThinking.asStateFlow()

    private val _aiCoachChatHistory = MutableStateFlow<List<AICoachMessage>>(
        listOf(
            AICoachMessage(
                sender = AICoachSender.COACH,
                text = "Olá, Atleta! Sou seu Coach IA do FitPr09. Como posso otimizar seus treinos, periodização, cargas ou nutrição hoje?",
                keyPoints = listOf(
                    "Tire dúvidas sobre execução e biomecânica de qualquer exercício",
                    "Peça ajustes de dieta e timing de macros para seu objetivo",
                    "Receba orientações para quebrar platôs de força e hipertrofia"
                ),
                suggestedFollowUps = listOf(
                    "Como quebrar platô no supino?",
                    "O que comer no pré-treino para mais energia?",
                    "Como substituir agachamento livre por dores no joelho?"
                )
            )
        )
    )
    val aiCoachChatHistory: StateFlow<List<AICoachMessage>> = _aiCoachChatHistory.asStateFlow()

    // --- Daily Workout Reminders State ---
    private val _reminderSettings = MutableStateFlow(WorkoutReminderManager.loadSettings(application))
    val reminderSettings: StateFlow<WorkoutReminderSettings> = _reminderSettings.asStateFlow()

    // --- Health Connect (Smartwatch & Google Health Sync) State ---
    val healthConnectManager = HealthConnectManager(application)
    private val _healthAvailability = MutableStateFlow(healthConnectManager.checkAvailability())
    val healthAvailability: StateFlow<HealthConnectAvailability> = _healthAvailability.asStateFlow()

    private val _healthPermissionsGranted = MutableStateFlow(false)
    val healthPermissionsGranted: StateFlow<Boolean> = _healthPermissionsGranted.asStateFlow()

    private val _healthDailyMetrics = MutableStateFlow(HealthConnectDailyMetrics())
    val healthDailyMetrics: StateFlow<HealthConnectDailyMetrics> = _healthDailyMetrics.asStateFlow()

    private val _isHealthSyncing = MutableStateFlow(false)
    val isHealthSyncing: StateFlow<Boolean> = _isHealthSyncing.asStateFlow()

    private val _healthSyncResultMessage = MutableStateFlow<String?>(null)
    val healthSyncResultMessage: StateFlow<String?> = _healthSyncResultMessage.asStateFlow()

    // --- Active Workout Functions ---
    fun startWorkoutFromTemplate(
        template: WorkoutTemplate,
        location: String = "Academia Smart Fit",
        scheduledSessionId: Long? = null,
        scheduledDateEpochDay: Long? = null,
        agendaAppointmentId: String? = null
    ) {
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
            scheduledSessionId = scheduledSessionId,
            agendaAppointmentId = agendaAppointmentId,
            scheduledDateEpochDay = scheduledDateEpochDay,
            title = template.title,
            location = location,
            durationSeconds = 0,
            exercises = freshPlans,
            perceivedExertion = 7,
            notes = "",
            restTimerVisible = false
        )

        persistActiveWorkoutDraft()

        startWorkoutDurationTimer()
        ttsVoiceManager.speakStartWorkout(template.title)
    }

    fun startEmptyWorkout(title: String = "Treino Personalizado", location: String = "Academia Smart Fit") {
        startCustomWorkout(title = title, location = location, selectedExercises = emptyList())
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

        persistActiveWorkoutDraft()

        startWorkoutDurationTimer()
        ttsVoiceManager.speakStartWorkout(_activeWorkout.value.title)
    }

    fun startWorkoutWithPlans(title: String, location: String, plans: List<WorkoutExercisePlan>) {
        _activeWorkout.value = ActiveWorkoutState(
            isActive = true,
            templateId = null,
            title = title.ifBlank { "Treino Rápido" },
            location = location,
            durationSeconds = 0,
            exercises = plans,
            perceivedExertion = 7,
            notes = "",
            restTimerVisible = false
        )

        startWorkoutDurationTimer()
        ttsVoiceManager.speakStartWorkout(_activeWorkout.value.title)
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

    fun updateSet(
        exerciseIndex: Int,
        setIndex: Int,
        weight: Double,
        reps: Int,
        completed: Boolean,
        tag: SetTag? = null,
        autoRest: Boolean = true,
        rir: Int? = null,
        rpe: Double? = null,
        notes: String? = null,
        restSeconds: Int? = null
    ) {
        val current = _activeWorkout.value
        val updatedExercises = current.exercises.toMutableList()
        if (exerciseIndex in updatedExercises.indices) {
            val plan = updatedExercises[exerciseIndex]
            val updatedSets = plan.sets.toMutableList()
            if (setIndex in updatedSets.indices) {
                val oldSet = updatedSets[setIndex]
                val wasCompleted = oldSet.isCompleted
                val e1RM = if (reps <= 1) weight else Math.round(weight * (1.0 + reps / 30.0) * 10.0) / 10.0

                updatedSets[setIndex] = oldSet.copy(
                    weightKg = weight,
                    reps = reps,
                    isCompleted = completed,
                    setTag = tag ?: oldSet.setTag,
                    rir = rir ?: oldSet.rir,
                    rpe = rpe ?: oldSet.rpe,
                    notes = notes ?: oldSet.notes,
                    restSeconds = restSeconds ?: oldSet.restSeconds,
                    estimated1RM = e1RM
                )
                updatedExercises[exerciseIndex] = plan.copy(sets = updatedSets)
                _activeWorkout.value = current.copy(exercises = updatedExercises)
                persistActiveWorkoutDraft()

                // Sound and PR check when completing a set for the first time
                if (completed && !wasCompleted) {
                    com.example.utils.SoundEffectManager.playSetCompleted()

                    // Check if this performance is a Personal Record (PR)
                    checkForPersonalRecord(exerciseIndex, setIndex, plan.exerciseName, weight, reps)

                    // 100kg Club medal check
                    if (weight >= 100.0) {
                        unlockMedalIfNotUnlocked("club_100kg")
                    }

                    if (autoRest) {
                        val restTime = if (oldSet.restSeconds > 0) oldSet.restSeconds else plan.targetRestSeconds
                        // Find next upcoming set details for TTS prompt
                        val nextSetInExercise = updatedSets.getOrNull(setIndex + 1)
                        val nextExercise = if (nextSetInExercise == null) updatedExercises.getOrNull(exerciseIndex + 1) else null
                        val nextExName = nextSetInExercise?.let { plan.exerciseName } ?: nextExercise?.exerciseName ?: plan.exerciseName
                        val nextWeight = nextSetInExercise?.weightKg ?: nextExercise?.sets?.firstOrNull()?.weightKg ?: weight
                        val nextReps = nextSetInExercise?.reps ?: nextExercise?.sets?.firstOrNull()?.reps ?: reps

                        triggerRestTimer(
                            seconds = restTime,
                            nextExerciseName = nextExName,
                            nextWeightKg = nextWeight,
                            nextReps = nextReps
                        )
                    }
                }
            }
        }
    }

    fun updateSetTag(exerciseIndex: Int, setIndex: Int, tag: SetTag) {
        val current = _activeWorkout.value
        val updatedExercises = current.exercises.toMutableList()
        if (exerciseIndex in updatedExercises.indices) {
            val plan = updatedExercises[exerciseIndex]
            val updatedSets = plan.sets.toMutableList()
            if (setIndex in updatedSets.indices) {
                updatedSets[setIndex] = updatedSets[setIndex].copy(setTag = tag)
                updatedExercises[exerciseIndex] = plan.copy(sets = updatedSets)
                _activeWorkout.value = current.copy(exercises = updatedExercises)
            }
        }
    }

    fun applyWarmupSets(exerciseIndex: Int, generatedSets: List<ExerciseSetEntry>) {
        val current = _activeWorkout.value
        val updatedExercises = current.exercises.toMutableList()
        if (exerciseIndex in updatedExercises.indices) {
            val plan = updatedExercises[exerciseIndex]
            updatedExercises[exerciseIndex] = plan.copy(sets = generatedSets)
            _activeWorkout.value = current.copy(exercises = updatedExercises)
        }
    }

    fun applyWeightToAllRemainingSets(exerciseIndex: Int, startingFromSetIndex: Int, newWeightKg: Double) {
        val current = _activeWorkout.value
        val updatedExercises = current.exercises.toMutableList()
        if (exerciseIndex in updatedExercises.indices) {
            val plan = updatedExercises[exerciseIndex]
            val updatedSets = plan.sets.mapIndexed { idx, set ->
                if (idx >= startingFromSetIndex && !set.isCompleted) {
                    set.copy(weightKg = newWeightKg)
                } else {
                    set
                }
            }
            updatedExercises[exerciseIndex] = plan.copy(sets = updatedSets)
            _activeWorkout.value = current.copy(exercises = updatedExercises)
        }
    }

    fun getProgressiveOverloadSuggestion(exerciseName: String): ProgressiveOverloadSuggestion? {
        val sessions = allWorkoutSessions.value
        var lastWeight = 0.0
        var lastReps = 0
        var found = false

        // Search in reverse chronological order
        for (session in sessions.sortedByDescending { it.dateEpochDay }) {
            try {
                val plans = plansAdapter.fromJson(session.exercisesDoneJson) ?: emptyList()
                val targetPlan = plans.find { it.exerciseName.equals(exerciseName, ignoreCase = true) }
                if (targetPlan != null) {
                    val completedWorkingSets = targetPlan.sets.filter { it.isCompleted && it.setTag != SetTag.WARMUP }
                    if (completedWorkingSets.isNotEmpty()) {
                        val topSet = completedWorkingSets.maxByOrNull { it.weightKg } ?: completedWorkingSets.first()
                        lastWeight = topSet.weightKg
                        lastReps = topSet.reps
                        found = true
                        break
                    }
                }
            } catch (_: Exception) {}
        }

        if (!found || lastWeight <= 0.0) return null

        val isHighReps = lastReps >= 12
        val suggestedWeight = if (isHighReps) lastWeight + 2.5 else lastWeight
        val suggestedReps = if (isHighReps) 8 else lastReps + 1
        val strategy = if (isHighReps) "Micro-Sobrecarga de Peso (+2.5 kg)" else "Aumento de Densidade (+1 repetição)"
        val tip = if (isHighReps) "Você atingiu o teto da faixa de repetições na última sessão! Suba a carga e execute 8-10 reps sólidas com RPE 8."
                  else "Mantenha a carga de ${lastWeight.toInt()}kg e busque 1 repetição extra com cadência controlada na fase excêntrica."
        val e1RM = lastWeight * (1.0 + (lastReps / 30.0))

        return ProgressiveOverloadSuggestion(
            exerciseName = exerciseName,
            previousWeightKg = lastWeight,
            previousReps = lastReps,
            suggestedWeightKg = suggestedWeight,
            suggestedReps = suggestedReps,
            strategy = strategy,
            tip = tip,
            estimated1RM = e1RM
        )
    }

    private fun checkForPersonalRecord(exerciseIndex: Int, setIndex: Int, exerciseName: String, currentWeightKg: Double, reps: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val sessions = repository.allWorkoutSessions.firstOrNull() ?: emptyList()
            val prResult = com.example.data.engine.PersonalRecordEngine.evaluateSetForPR(
                exerciseName = exerciseName,
                weightKg = currentWeightKg,
                reps = reps,
                sessions = sessions
            )

            if (prResult != null && prResult.isPR) {
                // Update active workout set with PR tag
                withContext(Dispatchers.Main) {
                    val current = _activeWorkout.value
                    if (exerciseIndex in current.exercises.indices) {
                        val plan = current.exercises[exerciseIndex]
                        if (setIndex in plan.sets.indices) {
                            val updatedSets = plan.sets.toMutableList()
                            updatedSets[setIndex] = updatedSets[setIndex].copy(
                                isPR = true,
                                prType = prResult.prType
                            )
                            val updatedExercises = current.exercises.toMutableList()
                            updatedExercises[exerciseIndex] = plan.copy(sets = updatedSets)
                            _activeWorkout.value = current.copy(exercises = updatedExercises)
                        }
                    }

                    _activePRCelebration.value = com.example.data.model.PersonalRecordCelebration(
                        exerciseName = exerciseName,
                        previousWeightKg = prResult.previousRecordValue,
                        newWeightKg = prResult.newRecordValue,
                        reps = reps,
                        title = "Novo Recorde: ${prResult.badgeLabel}! 🏆",
                        description = prResult.celebrationDescription
                    )
                }

                unlockMedalIfNotUnlocked("pr_breaker")
                ttsVoiceManager.speakPersonalRecord(exerciseName, currentWeightKg)
            }
        }
    }

    fun dismissPRCelebration() {
        _activePRCelebration.value = null
    }

    fun selectMedalForDetail(medal: com.example.data.model.UserMedal) {
        _selectedMedalForDetail.value = medal
    }

    fun dismissMedalDetail() {
        _selectedMedalForDetail.value = null
    }

    fun dismissMedalUnlockedDialog() {
        _activeMedalUnlocked.value = null
    }

    fun triggerCelebrationForMedal(medal: com.example.data.model.UserMedal) {
        _activeMedalUnlocked.value = medal
    }

    fun unlockMedalIfNotUnlocked(medalId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Persist to backup SharedPreferences so achievements never reset
            val currentBackup = prefs.getStringSet("unlocked_medals_backup", emptySet())?.toMutableSet() ?: mutableSetOf()
            if (!currentBackup.contains(medalId)) {
                currentBackup.add(medalId)
                prefs.edit().putStringSet("unlocked_medals_backup", currentBackup).apply()
            }

            val medals = repository.allMedals.firstOrNull() ?: emptyList()
            val targetMedal = medals.find { it.id == medalId }
            if (targetMedal != null && !targetMedal.isUnlocked) {
                repository.unlockMedal(medalId)
                _activeMedalUnlocked.value = targetMedal.copy(isUnlocked = true)
            }
        }
    }

    fun syncGamificationAndGoals() {
        viewModelScope.launch(Dispatchers.IO) {
            val todayEpoch = DateUtils.todayEpochDay()
            val now = java.time.LocalDate.now()
            val startOfWeekEpoch = todayEpoch - now.dayOfWeek.value + 1
            val startOfMonthEpoch = java.time.LocalDate.of(now.year, now.month, 1).toEpochDay()
            val startOfYearEpoch = java.time.LocalDate.of(now.year, 1, 1).toEpochDay()

            val sessions = repository.allWorkoutSessions.firstOrNull()?.filter { it.status == SessionStatus.COMPLETED } ?: emptyList()
            val cardios = repository.allCardioSessions.firstOrNull() ?: emptyList()

            // Weekly metrics
            val weeklyWorkouts = sessions.count { it.dateEpochDay >= startOfWeekEpoch }
            val weeklyVolume = sessions.filter { it.dateEpochDay >= startOfWeekEpoch }.sumOf { it.totalWeightLiftedKg }.toInt()
            val weeklyCardioMin = cardios.filter { it.dateEpochDay >= startOfWeekEpoch }.sumOf { it.durationMinutes }

            // Monthly metrics
            val monthlyWorkouts = sessions.count { it.dateEpochDay >= startOfMonthEpoch }
            val monthlyVolume = sessions.filter { it.dateEpochDay >= startOfMonthEpoch }.sumOf { it.totalWeightLiftedKg }.toInt()

            // Annual metrics
            val annualWorkouts = sessions.count { it.dateEpochDay >= startOfYearEpoch }
            val annualVolume = sessions.filter { it.dateEpochDay >= startOfYearEpoch }.sumOf { it.totalWeightLiftedKg }.toInt()

            // Weekly Frequency Medals (Bronze, Prata, Ouro, Master)
            repository.updateMedalProgress("weekly_freq_bronze", weeklyWorkouts.coerceAtLeast(1), todayEpoch)
            repository.updateMedalProgress("weekly_freq_prata", weeklyWorkouts, todayEpoch)
            repository.updateMedalProgress("weekly_freq_ouro", weeklyWorkouts, todayEpoch)
            repository.updateMedalProgress("weekly_master_superacao", if (weeklyWorkouts >= 6 && weeklyCardioMin >= 60) 6 else weeklyWorkouts, todayEpoch)

            // Weekly Volume Medals (Bronze, Prata, Ouro, Master)
            repository.updateMedalProgress("weekly_volume_bronze", weeklyVolume, todayEpoch)
            repository.updateMedalProgress("weekly_volume_prata", weeklyVolume, todayEpoch)
            repository.updateMedalProgress("weekly_volume_ouro", weeklyVolume, todayEpoch)
            repository.updateMedalProgress("weekly_volume_master", weeklyVolume, todayEpoch)

            // Monthly Frequency Medals (Bronze, Prata, Ouro, Master)
            repository.updateMedalProgress("monthly_freq_bronze", monthlyWorkouts, todayEpoch)
            repository.updateMedalProgress("monthly_freq_prata", monthlyWorkouts, todayEpoch)
            repository.updateMedalProgress("monthly_freq_ouro", monthlyWorkouts, todayEpoch)
            repository.updateMedalProgress("monthly_freq_master", monthlyWorkouts, todayEpoch)

            // Annual Workouts Medals (Bronze, Prata, Ouro, Master)
            repository.updateMedalProgress("annual_workouts_bronze", annualWorkouts, todayEpoch)
            repository.updateMedalProgress("annual_workouts_prata", annualWorkouts, todayEpoch)
            repository.updateMedalProgress("annual_workouts_ouro", annualWorkouts, todayEpoch)
            repository.updateMedalProgress("annual_workouts_master", annualWorkouts, todayEpoch)

            // Annual Volume Medals (Bronze, Prata, Ouro, Master)
            repository.updateMedalProgress("annual_tonnage_bronze", annualVolume, todayEpoch)
            repository.updateMedalProgress("annual_tonnage_prata", annualVolume, todayEpoch)
            repository.updateMedalProgress("annual_tonnage_ouro", annualVolume, todayEpoch)
            repository.updateMedalProgress("annual_tonnage_master", annualVolume, todayEpoch)
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

    // Substitution of an exercise in the active workout for another in the same muscle group
    fun substituteExerciseInActiveWorkout(exerciseIndex: Int, newExercise: Exercise) {
        val current = _activeWorkout.value
        val updatedExercises = current.exercises.toMutableList()
        if (exerciseIndex in updatedExercises.indices) {
            val oldPlan = updatedExercises[exerciseIndex]
            val setList = oldPlan.sets.map {
                it.copy(restSeconds = newExercise.defaultRestSeconds)
            }.ifEmpty {
                (1..newExercise.defaultSets).map {
                    ExerciseSetEntry(
                        setNumber = it,
                        weightKg = 20.0,
                        reps = newExercise.defaultReps,
                        isCompleted = false,
                        restSeconds = newExercise.defaultRestSeconds
                    )
                }
            }
            val newPlan = oldPlan.copy(
                exerciseId = newExercise.id,
                exerciseName = newExercise.name,
                muscleGroup = newExercise.muscleGroup.displayName,
                sets = setList,
                targetRestSeconds = newExercise.defaultRestSeconds,
                notes = newExercise.executionTips
            )
            updatedExercises[exerciseIndex] = newPlan
            _activeWorkout.value = current.copy(exercises = updatedExercises)
        }
    }

    fun substituteExerciseInActiveWorkout(oldExerciseId: Long, newExercise: Exercise) {
        val idx = _activeWorkout.value.exercises.indexOfFirst { it.exerciseId == oldExerciseId }
        if (idx != -1) {
            substituteExerciseInActiveWorkout(idx, newExercise)
        }
    }

    fun getExercisesForMuscleGroup(muscleGroupDisplayName: String): List<Exercise> {
        val all = allExercises.value
        val matches = all.filter {
            it.muscleGroup.displayName.equals(muscleGroupDisplayName, ignoreCase = true) ||
                    muscleGroupDisplayName.contains(it.muscleGroup.displayName, ignoreCase = true) ||
                    it.muscleGroup.displayName.contains(muscleGroupDisplayName, ignoreCase = true)
        }
        return if (matches.isNotEmpty()) matches else all
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
    fun triggerRestTimer(
        seconds: Int,
        nextExerciseName: String? = null,
        nextWeightKg: Double? = null,
        nextReps: Int? = null
    ) {
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
                    // Voice audio countdown at 5s, 3s, 2s, 1s
                    if (remaining in listOf(5, 3, 2, 1)) {
                        ttsVoiceManager.speakCountdown(remaining)
                    }
                }
            }
            // Finished - Voice cue
            ttsVoiceManager.speakRestFinished(nextExerciseName, nextWeightKg, nextReps)
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
    /**
     * Persists the active session continuously. This prevents losing a workout
     * when the process is closed before the final completion action.
     */
    private fun persistActiveWorkoutDraft() {
        val current = _activeWorkout.value
        if (!current.isActive) return
        val exercisesJson = runCatching { plansAdapter.toJson(current.exercises) }.getOrDefault("[]")
        val draft = WorkoutSession(
            id = current.scheduledSessionId ?: 0L,
            templateId = current.templateId,
            agendaAppointmentId = current.agendaAppointmentId,
            title = current.title.ifBlank { "Treino de Musculação" },
            dateEpochDay = current.scheduledDateEpochDay ?: DateUtils.todayEpochDay(),
            startTimeMillis = System.currentTimeMillis() - (current.durationSeconds * 1000L),
            endTimeMillis = System.currentTimeMillis(),
            durationSeconds = current.durationSeconds,
            location = current.location,
            status = SessionStatus.IN_PROGRESS,
            totalWeightLiftedKg = current.exercises.sumOf { plan ->
                plan.sets.filter { it.isCompleted }.sumOf { it.volumeKg }
            },
            perceivedExertion = current.perceivedExertion,
            notes = current.notes,
            exercisesDoneJson = exercisesJson
        )
        viewModelScope.launch {
            val savedId = repository.saveWorkoutSession(draft)
            if (current.scheduledSessionId == null && _activeWorkout.value.isActive) {
                _activeWorkout.value = _activeWorkout.value.copy(scheduledSessionId = savedId)
            }
        }
    }

    private suspend fun importExerciseLibraryFromAssets(dao: com.example.data.local.FitnessDao) {
        try {
            val existing = dao.getAllExercises().firstOrNull()?.map { it.name.lowercase() }?.toSet() ?: emptySet()
            val root = JSONObject(getApplication<Application>().assets.open("exercise_library/exercises.pt-BR.json").bufferedReader().use { it.readText() })
            val items = root.optJSONArray("exercises") ?: return
            val additions = buildList {
                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val name = item.optString("name").trim()
                    if (name.isBlank() || name.lowercase() in existing) continue
                    val group = when (item.optString("groupId")) {
                        "costas" -> MuscleGroup.COSTAS; "ombros" -> MuscleGroup.OMBROS; "biceps" -> MuscleGroup.BICEPS
                        "triceps" -> MuscleGroup.TRICEPS; "quadriceps" -> MuscleGroup.QUADRICEPS; "posterior_gluteos" -> MuscleGroup.POSTERIOR_GLUTEOS
                        "panturrilhas" -> MuscleGroup.PANTURRILHA; "core" -> MuscleGroup.ABDOMEN; "antebracos_funcional" -> MuscleGroup.BICEPS
                        else -> MuscleGroup.PEITO
                    }
                    val equipment = when (item.optJSONArray("equipment")?.optString(0)?.lowercase()) {
                        "halteres" -> com.example.data.model.Equipment.HALTERES
                        "máquina", "maquina" -> com.example.data.model.Equipment.MAQUINA
                        "polia", "cabo" -> com.example.data.model.Equipment.POLIA
                        "peso corporal" -> com.example.data.model.Equipment.PESO_CORPO
                        else -> com.example.data.model.Equipment.BARRA
                    }
                    add(com.example.data.model.Exercise(name = name, muscleGroup = group, equipment = equipment, instructions = item.optString("instructions"), executionTips = item.optString("tips")))
                }
            }
            if (additions.isNotEmpty()) dao.insertExercises(additions)
        } catch (_: Exception) { }
    }

    fun finishActiveWorkout(onSaved: (WorkoutSession) -> Unit = {}) {
        val current = _activeWorkout.value
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()

        com.example.utils.SoundEffectManager.playWorkoutCompleted()

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
            id = current.scheduledSessionId ?: 0L,
            templateId = current.templateId,
            agendaAppointmentId = current.agendaAppointmentId,
            title = current.title.ifBlank { "Treino de Musculação" },
            dateEpochDay = current.scheduledDateEpochDay ?: DateUtils.todayEpochDay(),
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
            val savedSession = session.copy(id = if (session.id != 0L) session.id else insertedId)
            backupWorkoutSessions()

            _activeWorkout.value = ActiveWorkoutState() // Reset
            onSaved(savedSession)

            // Voice announcement
            ttsVoiceManager.speakWorkoutCompleted(durationMin, totalWeight)

            // Unlock First Workout Medal
            unlockMedalIfNotUnlocked("first_workout")

            // Check consistency medals (10 workouts, 3-day streak, 10-ton volume)
            val allSessions = repository.allWorkoutSessions.firstOrNull() ?: emptyList()
            val previousSessions = allSessions.filter { it.id != savedSession.id }
            val journeyImpact = Phase7JourneyEngine.evaluateWorkout(
                session = savedSession,
                sessionsBefore = previousSessions,
                cardio = repository.allCardioSessions.firstOrNull() ?: emptyList(),
                medals = repository.allMedals.firstOrNull() ?: emptyList()
            )
            _lastWorkoutJourneyImpact.value = journeyImpact
            journeyImpact.completedLevels.forEach { cardLevel ->
                repository.saveJourneyUnlock(
                    JourneyUnlock(
                        cardLevel = cardLevel,
                        journeyRank = com.example.domain.gamification.PintinhoJourneyCatalog.rankFor(cardLevel),
                        sessionId = savedSession.id
                    )
                )
            }
            if (allSessions.size >= 10) {
                unlockMedalIfNotUnlocked("legend_10workouts")
            }
            if (allSessions.size >= 3) {
                unlockMedalIfNotUnlocked("streak_3days")
            }
            val totalVolume = allSessions.sumOf { it.totalWeightLiftedKg } + totalWeight
            if (totalVolume >= 10000.0) {
                unlockMedalIfNotUnlocked("volume_10ton")
            }

            // Request AI Evaluation
            evaluateSessionWithAI(savedSession, profile)

            // Auto-sync with Health Connect if enabled
            try {
                if (_healthPermissionsGranted.value) {
                    healthConnectManager.writeStrengthWorkoutSession(savedSession)
                    refreshHealthDailyMetrics()
                }
            } catch (_: Exception) {}
        }
    }

    // --- Meals & Nutrition (Gemini AI Calorie Estimator) ---
    fun analyzeMealWithGemini(mealText: String, mealType: String) {
        viewModelScope.launch {
            _isAnalyzingMeal.value = true
            val profile = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()
            try {
                val result = repository.analyzeMealWithGemini(mealText, mealType, profile)
                _lastMealAnalysis.value = result
                unlockMedalIfNotUnlocked("smart_nutrition")
            } catch (_: Exception) {
            } finally {
                _isAnalyzingMeal.value = false
            }
        }
    }

    fun saveMealLog(meal: com.example.data.model.MealLog) {
        viewModelScope.launch {
            repository.saveMealLog(meal)
            _lastMealAnalysis.value = null
        }
    }

    fun deleteMealLog(id: Long) {
        viewModelScope.launch {
            repository.deleteMealLogById(id)
        }
    }

    fun discardActiveWorkout() {
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()
        _activeWorkout.value = ActiveWorkoutState()
    }

    // --- Live Cardio Session Functions (OutdoorLocationService) ---
    private fun syncActiveCardioWithOutdoorState(state: OutdoorSessionState) {
        if (state.isActive) {
            _activeCardio.value = ActiveCardioState(
                isActive = true,
                isPaused = state.isPaused,
                type = state.cardioType,
                durationSeconds = state.durationSeconds.toInt(),
                distanceKm = state.distanceKm,
                intensity = state.intensity,
                location = state.locationName,
                caloriesBurned = state.caloriesBurned,
                speedKmh = state.currentSpeedKmh,
                paceMinKm = if (state.avgSpeedKmh > 0.5) (60.0 / state.avgSpeedKmh) else 0.0,
                gpsEnabled = state.routePoints.isNotEmpty() || state.status == OutdoorTrackingStatus.RUNNING,
                gpsAccuracyMeters = state.accuracyMeters,
                latitude = state.currentLocation?.latitude,
                longitude = state.currentLocation?.longitude,
                gpsPointsCount = state.routePoints.size,
                targetMinutes = state.targetMinutes
            )
        } else if (_activeCardio.value.isActive && state.isStopped) {
            _activeCardio.value = ActiveCardioState()
        }
    }

    fun startLiveCardio(
        type: CardioType,
        location: String,
        intensity: IntensityLevel,
        targetMinutes: Int? = null,
        enableGps: Boolean = true
    ) {
        val app = getApplication<Application>()
        val startIntent = OutdoorLocationService.startServiceIntent(
            app, type, intensity, location, targetMinutes
        )
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                app.startForegroundService(startIntent)
            } else {
                app.startService(startIntent)
            }
        } catch (e: Exception) {
            Log.e("FitnessViewModel", "Error starting OutdoorLocationService: ${e.message}")
        }
        outdoorServiceBinder?.start(type, intensity, location, targetMinutes)
    }

    fun onPauseClicked() {
        if (!_outdoorSessionState.value.isActive || _outdoorSessionState.value.isPaused) return
        val app = getApplication<Application>()
        try {
            app.startService(Intent(app, OutdoorLocationService::class.java).apply {
                action = OutdoorLocationService.ACTION_PAUSE
            })
        } catch (e: Exception) {
            Log.e("FitnessViewModel", "Error sending ACTION_PAUSE: ${e.message}")
        }
        outdoorServiceBinder?.pause()
    }

    fun onResumeClicked() {
        if (!_outdoorSessionState.value.isPaused) return
        val app = getApplication<Application>()
        try {
            app.startService(Intent(app, OutdoorLocationService::class.java).apply {
                action = OutdoorLocationService.ACTION_RESUME
            })
        } catch (e: Exception) {
            Log.e("FitnessViewModel", "Error sending ACTION_RESUME: ${e.message}")
        }
        outdoorServiceBinder?.resume()
    }

    fun onStopClicked(
        distanceKm: Double? = null,
        heartRate: Int? = null,
        notes: String = "",
        onFinished: ((CardioSession) -> Unit)? = null
    ) {
        val currentState = outdoorServiceBinder?.stop() ?: _outdoorSessionState.value
        val app = getApplication<Application>()
        try {
            app.startService(Intent(app, OutdoorLocationService::class.java).apply {
                action = OutdoorLocationService.ACTION_STOP
            })
        } catch (e: Exception) {
            Log.e("FitnessViewModel", "Error sending ACTION_STOP: ${e.message}")
        }

        if (currentState.durationSeconds == 0L && currentState.distanceKm == 0.0 && !currentState.isActive) {
            _activeCardio.value = ActiveCardioState()
            _outdoorSessionState.value = OutdoorSessionState()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val finalDurationMin = (currentState.durationSeconds / 60).toInt().coerceAtLeast(1)
                val finalDist = distanceKm ?: if (currentState.distanceKm > 0.0) currentState.distanceKm else null
                val profile = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()

                val session = OutdoorCardioPersistence.buildSession(
                    type = currentState.cardioType,
                    dateEpochDay = DateUtils.todayEpochDay(),
                    timestampMillis = System.currentTimeMillis(),
                    durationMinutes = finalDurationMin,
                    distanceKm = finalDist,
                    avgHeartRateBpm = heartRate,
                    intensity = currentState.intensity,
                    location = currentState.locationName,
                    caloriesBurned = currentState.caloriesBurned,
                    notes = notes,
                    routePoints = currentState.routePoints,
                    splits = currentState.splits,
                    elevationGainMeters = currentState.elevationGainMeters,
                    avgPaceMinKm = currentState.avgPaceMinKm
                )

                val id = repository.saveCardioSession(session)
                val savedSession = session.copy(id = id)

                withContext(Dispatchers.Main) {
                    _activeCardio.value = ActiveCardioState()
                    _outdoorSessionState.value = OutdoorSessionState()
                    onFinished?.invoke(savedSession)
                }

                // AI cardio evaluation
                evaluateCardioWithAI(savedSession, profile)

                // Sync Health Connect
                try {
                    if (_healthPermissionsGranted.value) {
                        healthConnectManager.writeCardioSession(savedSession)
                        refreshHealthDailyMetrics()
                    }
                } catch (e: Exception) {
                    Log.w("FitnessViewModel", "Health Connect write error: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("FitnessViewModel", "Error saving outdoor cardio session: ${e.message}", e)
            }
        }
    }

    fun pauseLiveCardio() = onPauseClicked()

    fun resumeLiveCardio() = onResumeClicked()

    fun finishLiveCardio(
        distanceKm: Double?,
        heartRate: Int?,
        notes: String,
        onFinished: (CardioSession) -> Unit = {}
    ) = onStopClicked(distanceKm, heartRate, notes, onFinished)

    fun toggleCardioGps(enable: Boolean): Boolean {
        return _outdoorSessionState.value.isActive
    }

    fun discardLiveCardio() {
        val app = getApplication<Application>()
        try {
            app.startService(Intent(app, OutdoorLocationService::class.java).apply {
                action = OutdoorLocationService.ACTION_STOP
            })
        } catch (e: Exception) {
            Log.e("FitnessViewModel", "Error stopping service on discard: ${e.message}")
        }
        outdoorServiceBinder?.stop()
        _activeCardio.value = ActiveCardioState()
        _outdoorSessionState.value = OutdoorSessionState()
    }

    fun logManualCardio(cardio: CardioSession) {
        viewModelScope.launch {
            val id = repository.saveCardioSession(cardio)
            val savedCardio = cardio.copy(id = id)
            val profile = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()
            evaluateCardioWithAI(savedCardio, profile)

            try {
                if (_healthPermissionsGranted.value) {
                    healthConnectManager.writeCardioSession(savedCardio)
                    refreshHealthDailyMetrics()
                }
            } catch (_: Exception) {}
        }
    }

    fun deleteCardioSession(id: Long) {
        viewModelScope.launch {
            repository.deleteCardioSessionById(id)
        }
    }

    // --- Schedule & Agenda ---
    fun scheduleWorkout(
        title: String,
        epochDay: Long,
        location: String,
        templateId: Long? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        locationAddress: String? = null
    ) {
        viewModelScope.launch {
            val session = WorkoutSession(
                templateId = templateId,
                title = title,
                dateEpochDay = epochDay,
                location = location,
                latitude = latitude,
                longitude = longitude,
                locationAddress = locationAddress,
                status = SessionStatus.SCHEDULED,
                durationSeconds = 0,
                totalWeightLiftedKg = 0.0,
                estimatedCalories = 0
            )
            repository.saveWorkoutSession(session)
        }
    }

    fun logRetroactiveWorkout(
        title: String,
        epochDay: Long,
        muscleGroups: List<MuscleGroup>,
        durationMinutes: Int = 45,
        location: String = "Academia Smart Fit",
        rpe: Int = 7,
        notes: String = "",
        templateId: Long? = null,
        customVolumeKg: Double? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()
            val durationSec = durationMinutes * 60
            val estimatedCal = ((6.0 * 3.5 * profile.currentWeightKg / 200.0) * durationMinutes).toInt()

            // Generate representative completed exercises for the chosen muscle groups
            val availableExercises = allExercises.value
            val generatedPlans = mutableListOf<WorkoutExercisePlan>()
            var calculatedVolume = 0.0

            for (group in muscleGroups) {
                val matching = availableExercises.filter { it.muscleGroup == group }
                val chosenExercises = if (matching.isNotEmpty()) matching.take(2) else emptyList()
                for (ex in chosenExercises) {
                    val defaultWeight = when (group) {
                        MuscleGroup.QUADRICEPS -> 60.0
                        MuscleGroup.PEITO -> 40.0
                        MuscleGroup.COSTAS -> 45.0
                        MuscleGroup.POSTERIOR_GLUTEOS -> 40.0
                        MuscleGroup.OMBROS -> 16.0
                        MuscleGroup.BICEPS, MuscleGroup.TRICEPS -> 14.0
                        MuscleGroup.PANTURRILHA -> 50.0
                        MuscleGroup.ABDOMEN -> 0.0
                    }
                    val sets = (1..3).map { setNum ->
                        ExerciseSetEntry(
                            setNumber = setNum,
                            weightKg = defaultWeight,
                            reps = 10,
                            isCompleted = true,
                            restSeconds = 60,
                            setTag = SetTag.NORMAL
                        )
                    }
                    val exerciseVolume = sets.sumOf { it.weightKg * it.reps }
                    calculatedVolume += exerciseVolume
                    generatedPlans.add(
                        WorkoutExercisePlan(
                            exerciseId = ex.id,
                            exerciseName = ex.name,
                            muscleGroup = group.displayName,
                            sets = sets,
                            targetRestSeconds = 60,
                            notes = "Realizado em sessão retroativa"
                        )
                    )
                }
            }

            val totalWeight = customVolumeKg ?: (if (calculatedVolume > 0.0) calculatedVolume else (durationMinutes * 55.0).coerceAtLeast(1000.0))

            val exercisesJson = try {
                plansAdapter.toJson(generatedPlans)
            } catch (_: Exception) {
                "[]"
            }

            val finalTitle = if (title.isNotBlank()) {
                title
            } else if (muscleGroups.isNotEmpty()) {
                "Treino de " + muscleGroups.joinToString(", ") { it.displayName }
            } else {
                "Treino de Musculação"
            }

            val groupNames = muscleGroups.joinToString(", ") { it.displayName }
            val finalNotes = buildString {
                if (groupNames.isNotBlank()) {
                    append("Grupos Musculares: $groupNames")
                }
                if (notes.isNotBlank()) {
                    if (isNotEmpty()) append(" • ")
                    append(notes)
                }
                if (isEmpty()) {
                    append("Treino registrado retroativamente")
                }
            }

            val session = WorkoutSession(
                templateId = templateId,
                title = finalTitle,
                dateEpochDay = epochDay,
                startTimeMillis = (epochDay * 86400000L) + (18 * 3600000L), // 18:00
                endTimeMillis = (epochDay * 86400000L) + (18 * 3600000L) + (durationSec * 1000L),
                durationSeconds = durationSec,
                location = location.ifBlank { "Academia" },
                status = SessionStatus.COMPLETED,
                totalWeightLiftedKg = totalWeight,
                estimatedCalories = estimatedCal,
                perceivedExertion = rpe,
                notes = finalNotes,
                exercisesDoneJson = exercisesJson,
                aiCaloricEvaluation = ""
            )

            val savedId = repository.saveWorkoutSession(session)
            val savedSession = session.copy(id = savedId)
            backupWorkoutSessions()

            // Sync gamification and achievements
            syncGamificationAndGoals()

            // Unlock first workout medal
            unlockMedalIfNotUnlocked("first_workout")

            // AI evaluation in background
            evaluateSessionWithAI(savedSession, profile)

            // Health Connect if granted
            try {
                if (_healthPermissionsGranted.value) {
                    healthConnectManager.writeStrengthWorkoutSession(savedSession)
                    refreshHealthDailyMetrics()
                }
            } catch (_: Exception) {}
        }
    }

    // --- Goal Setting & Exercise Targets ---
    fun updateWeeklyGoalDays(days: Int) {
        val currentProfile = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()
        viewModelScope.launch {
            repository.saveUserProfile(currentProfile.copy(weeklyGoalDays = days.coerceIn(1, 7)))
        }
    }

    fun saveExerciseTarget(target: com.example.data.model.ExercisePerformanceTarget) {
        viewModelScope.launch {
            if (target.id == 0L) {
                repository.saveExerciseTarget(target)
            } else {
                repository.updateExerciseTarget(target)
            }
        }
    }

    fun toggleExerciseTargetAchieved(target: com.example.data.model.ExercisePerformanceTarget) {
        viewModelScope.launch {
            val newAchieved = !target.isAchieved
            val achievedEpochDay = if (newAchieved) DateUtils.todayEpochDay() else null
            repository.setExerciseTargetAchieved(target.id, newAchieved, achievedEpochDay)
        }
    }

    fun deleteExerciseTarget(id: Long) {
        viewModelScope.launch {
            repository.deleteExerciseTargetById(id)
        }
    }

    fun deleteWorkoutSession(id: Long) {
        viewModelScope.launch {
            repository.deleteWorkoutSessionById(id)
            backupWorkoutSessions()
        }
    }

    // --- Custom Template Creation & Favorite Management ---
    fun createAndSaveWorkoutTemplate(
        title: String,
        subtitle: String,
        category: WorkoutCategory,
        exercises: List<WorkoutExercisePlan>,
        description: String,
        durationMin: Int = 50,
        restSec: Int = 60,
        isFavorite: Boolean = false
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
                isFavorite = isFavorite,
                exercisesJson = json,
                description = description,
                createdAtEpochDay = DateUtils.todayEpochDay(),
                timesCompleted = 0
            )
            repository.saveWorkoutTemplate(template)
        }
    }

    fun toggleFavoriteWorkoutTemplate(template: WorkoutTemplate) {
        viewModelScope.launch {
            repository.setWorkoutTemplateFavorite(template.id, !template.isFavorite)
        }
    }

    fun duplicateWorkoutTemplate(template: WorkoutTemplate) {
        viewModelScope.launch {
            repository.duplicateWorkoutTemplate(template)
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
            persistUserProfileBackup(profile)
        }
    }

    fun persistUserProfileBackup(profile: UserProfile) {
        try {
            val json = moshi.adapter(UserProfile::class.java).toJson(profile)
            prefs.edit()
                .putString("backup_user_profile_json", json)
                .putString("cached_athlete_name", profile.name)
                .putString("cached_athlete_photo", profile.photoUri)
                .putFloat("cached_athlete_weight", profile.currentWeightKg.toFloat())
                .putFloat("cached_athlete_height", profile.heightCm.toFloat())
                .apply()
        } catch (_: Exception) {}
    }

    private fun backupWorkoutSessions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sessions = repository.allWorkoutSessions.firstOrNull() ?: return@launch
                val listType = Types.newParameterizedType(List::class.java, WorkoutSession::class.java)
                val adapter: JsonAdapter<List<WorkoutSession>> = moshi.adapter(listType)
                val json = adapter.toJson(sessions)
                prefs.edit().putString("backup_workout_sessions_json", json).apply()
            } catch (_: Exception) {}
        }
    }

    private fun backupBodyMeasurements() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val measurements = repository.allBodyMeasurements.firstOrNull() ?: return@launch
                val listType = Types.newParameterizedType(List::class.java, BodyMeasurement::class.java)
                val adapter: JsonAdapter<List<BodyMeasurement>> = moshi.adapter(listType)
                val json = adapter.toJson(measurements)
                prefs.edit().putString("backup_body_measurements_json", json).apply()
            } catch (_: Exception) {}
        }
    }

    // --- Custom Agenda Appointments Persistence & Operations ---
    private val _customAppointments = MutableStateFlow<List<AgendaCustomAppointment>>(emptyList())
    val customAppointments: StateFlow<List<AgendaCustomAppointment>> = _customAppointments.asStateFlow()

    fun initCustomAppointmentsIfNeeded() {
        val json = prefs.getString("backup_custom_appointments_json", null)
        if (!json.isNullOrBlank()) {
            try {
                val listType = Types.newParameterizedType(List::class.java, AgendaCustomAppointment::class.java)
                val adapter: JsonAdapter<List<AgendaCustomAppointment>> = moshi.adapter(listType)
                val list = adapter.fromJson(json)
                if (!list.isNullOrEmpty()) {
                    _customAppointments.value = list
                    return
                }
            } catch (_: Exception) {}
        }
        val todayEpoch = DateUtils.todayEpochDay()
        val initialList = listOf(
            AgendaCustomAppointment(
                id = "agenda_strength_$todayEpoch",
                epochDay = todayEpoch,
                typeName = "STRENGTH",
                startTime = "06:00",
                endTime = "07:00",
                title = "Treino de Força",
                subtitle = "Peito e Tríceps",
                isCompleted = true
            ),
            AgendaCustomAppointment(
                id = "agenda_cardio_$todayEpoch",
                epochDay = todayEpoch,
                typeName = "CARDIO",
                startTime = "12:00",
                endTime = "12:30",
                title = "Cardio",
                subtitle = "30 minutos",
                isCompleted = false
            ),
            AgendaCustomAppointment(
                id = "agenda_meal_$todayEpoch",
                epochDay = todayEpoch,
                typeName = "MEAL",
                startTime = "19:00",
                endTime = "19:30",
                title = "Refeição",
                subtitle = "Pós-treino",
                isCompleted = false
            ),
            AgendaCustomAppointment(
                id = "agenda_rest_$todayEpoch",
                epochDay = todayEpoch,
                typeName = "REST",
                startTime = "21:00",
                endTime = null,
                title = "Descanso",
                subtitle = "Hora de recuperar",
                isCompleted = false
            )
        )
        _customAppointments.value = initialList
        persistCustomAppointments(initialList)
    }

    private fun persistCustomAppointments(list: List<AgendaCustomAppointment>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listType = Types.newParameterizedType(List::class.java, AgendaCustomAppointment::class.java)
                val adapter: JsonAdapter<List<AgendaCustomAppointment>> = moshi.adapter(listType)
                val json = adapter.toJson(list)
                prefs.edit().putString("backup_custom_appointments_json", json).apply()
            } catch (_: Exception) {}
        }
    }

    fun addCustomAppointment(appointment: AgendaCustomAppointment) {
        val updated = _customAppointments.value + appointment
        _customAppointments.value = updated
        persistCustomAppointments(updated)
    }

    fun toggleCustomAppointmentStatus(id: String) {
        val updated = _customAppointments.value.map {
            if (it.id == id) {
                val completed = !it.isCompleted
                it.copy(isCompleted = completed, status = if (completed) com.example.data.model.AgendaAppointmentStatus.COMPLETED else com.example.data.model.AgendaAppointmentStatus.PLANNED)
            } else it
        }
        _customAppointments.value = updated
        persistCustomAppointments(updated)
    }

    fun cancelCustomAppointment(id: String) {
        val updated = _customAppointments.value.map {
            if (it.id == id) it.copy(isCompleted = false, status = com.example.data.model.AgendaAppointmentStatus.CANCELLED) else it
        }
        _customAppointments.value = updated
        persistCustomAppointments(updated)
    }

    fun markCustomAppointmentRescheduled(id: String) {
        val updated = _customAppointments.value.map {
            if (it.id == id) it.copy(isCompleted = false, status = com.example.data.model.AgendaAppointmentStatus.RESCHEDULED) else it
        }
        _customAppointments.value = updated
        persistCustomAppointments(updated)
    }

    fun deleteCustomAppointment(id: String) {
        val updated = _customAppointments.value.filterNot { it.id == id }
        _customAppointments.value = updated
        persistCustomAppointments(updated)
    }

    fun updateWorkoutSession(session: WorkoutSession) {
        viewModelScope.launch {
            repository.updateWorkoutSession(session)
            backupWorkoutSessions()
        }
    }

    fun toggleWorkoutSessionStatus(sessionId: Long) {
        viewModelScope.launch {
            val session = allWorkoutSessions.value.find { it.id == sessionId } ?: return@launch
            val newStatus = if (session.status == SessionStatus.COMPLETED) SessionStatus.SCHEDULED else SessionStatus.COMPLETED
            val updated = session.copy(
                status = newStatus,
                durationSeconds = if (newStatus == SessionStatus.COMPLETED && session.durationSeconds == 0) 3600 else session.durationSeconds,
                estimatedCalories = if (newStatus == SessionStatus.COMPLETED && session.estimatedCalories == 0) 420 else session.estimatedCalories
            )
            repository.updateWorkoutSession(updated)
            backupWorkoutSessions()
        }
    }

    fun updateUserPhoto(photoUri: Uri) {
        viewModelScope.launch {
            val app = getApplication<Application>()
            val permanentPath = withContext(Dispatchers.IO) {
                PhotoStorageHelper.saveImageToInternalStorage(app, photoUri)
            }
            val current = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()
            val updated = current.copy(photoUri = permanentPath)
            repository.saveUserProfile(updated)
            persistUserProfileBackup(updated)
        }
    }

    fun removeUserPhoto() {
        val current = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()
        val updated = current.copy(photoUri = null)
        viewModelScope.launch {
            repository.saveUserProfile(updated)
            persistUserProfileBackup(updated)
        }
    }

    fun addBodyMeasurement(measurement: BodyMeasurement) {
        viewModelScope.launch {
            repository.saveBodyMeasurement(measurement)
            backupBodyMeasurements()
            // Also update current weight on user profile
            val currentProf = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()
            val updatedProf = currentProf.copy(currentWeightKg = measurement.weightKg)
            repository.saveUserProfile(updatedProf)
            persistUserProfileBackup(updatedProf)

            // Sync weight to Health Connect
            try {
                if (_healthPermissionsGranted.value) {
                    healthConnectManager.writeWeight(measurement.weightKg, measurement.timestampMillis)
                    refreshHealthDailyMetrics()
                }
            } catch (_: Exception) {}
        }
    }

    fun deleteBodyMeasurement(id: Long) {
        viewModelScope.launch {
            repository.deleteBodyMeasurementById(id)
            backupBodyMeasurements()
        }
    }

    // --- Progress & Body Photos (Evolution) ---
    fun saveProgressPhoto(
        sourceUri: Uri,
        weightKg: Double?,
        monthLabel: String,
        isInitial: Boolean,
        bodyFatPercentage: Double?,
        notes: String,
        onComplete: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            val app = getApplication<Application>()
            val permanentPath = withContext(Dispatchers.IO) {
                try {
                    val photosDir = File(app.filesDir, "progress_photos")
                    if (!photosDir.exists()) {
                        photosDir.mkdirs()
                    }
                    val fileName = "photo_${System.currentTimeMillis()}.jpg"
                    val destFile = File(photosDir, fileName)

                    app.contentResolver.openInputStream(sourceUri)?.use { input ->
                        FileOutputStream(destFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    destFile.absolutePath
                } catch (e: Exception) {
                    sourceUri.toString()
                }
            }

            val photoRecord = ProgressPhoto(
                dateEpochDay = DateUtils.todayEpochDay(),
                timestampMillis = System.currentTimeMillis(),
                imageUri = permanentPath,
                weightKg = weightKg,
                monthLabel = monthLabel.ifBlank {
                    if (isInitial) "Foto Inicial" else DateUtils.formatEpochDayToMonthYear(DateUtils.todayEpochDay())
                },
                isInitial = isInitial,
                bodyFatPercentage = bodyFatPercentage,
                notes = notes
            )
            repository.saveProgressPhoto(photoRecord)

            // Update user profile weights if provided
            userProfile.value?.let { prof ->
                if (isInitial && weightKg != null) {
                    repository.saveUserProfile(prof.copy(startingWeightKg = weightKg))
                } else if (!isInitial && weightKg != null) {
                    repository.saveUserProfile(prof.copy(currentWeightKg = weightKg))
                }
            }

            onComplete(true)
        }
    }

    fun deleteProgressPhoto(photo: ProgressPhoto) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val file = File(photo.imageUri)
                    if (file.exists() && file.isFile) {
                        file.delete()
                    }
                } catch (_: Exception) {}
            }
            repository.deleteProgressPhotoById(photo.id)
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

    // --- Progression & Overload State ---
    private val _dismissedProgressions = MutableStateFlow<Set<String>>(emptySet())
    val dismissedProgressions: StateFlow<Set<String>> = _dismissedProgressions.asStateFlow()

    private val _appliedProgressions = MutableStateFlow<Map<String, Double>>(emptyMap())
    val appliedProgressions: StateFlow<Map<String, Double>> = _appliedProgressions.asStateFlow()

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

    // --- Automatic Exercise History & Overload Engine ---

    fun getExerciseHistoryRecords(exerciseName: String, exerciseId: Long? = null): List<ExerciseExecutionRecord> {
        val sessions = allWorkoutSessions.value
            .filter { it.status == SessionStatus.COMPLETED }
            .sortedWith(compareByDescending<WorkoutSession> { it.dateEpochDay }.thenByDescending { it.startTimeMillis })

        val records = mutableListOf<ExerciseExecutionRecord>()

        for (session in sessions) {
            val plans = try {
                plansAdapter.fromJson(session.exercisesDoneJson) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            val matchingPlan = plans.firstOrNull { plan ->
                (exerciseId != null && plan.exerciseId == exerciseId) ||
                    plan.exerciseName.trim().equals(exerciseName.trim(), ignoreCase = true)
            }

            if (matchingPlan != null && matchingPlan.sets.any { it.isCompleted }) {
                records.add(
                    ExerciseExecutionRecord(
                        sessionId = session.id,
                        workoutTitle = session.title,
                        sessionDateEpochDay = session.dateEpochDay,
                        sessionStartTimeMillis = session.startTimeMillis,
                        location = session.location,
                        exerciseId = matchingPlan.exerciseId,
                        exerciseName = matchingPlan.exerciseName,
                        muscleGroup = matchingPlan.muscleGroup,
                        sets = matchingPlan.sets,
                        notes = matchingPlan.notes.ifBlank { session.notes },
                        targetRestSeconds = matchingPlan.targetRestSeconds
                    )
                )
            }
        }

        return records
    }

    fun getLastExerciseExecution(exerciseName: String, exerciseId: Long? = null): ExerciseExecutionRecord? {
        return getExerciseHistoryRecords(exerciseName, exerciseId).firstOrNull()
    }

    fun getProgressionSuggestion(
        exerciseName: String,
        currentPlan: WorkoutExercisePlan
    ): ProgressionSuggestion? {
        if (_dismissedProgressions.value.contains(exerciseName)) {
            return null
        }

        val history = getExerciseHistoryRecords(exerciseName, currentPlan.exerciseId)
        if (history.isEmpty()) return null

        val lastRecord = history.first()
        val lastCompletedSets = lastRecord.completedSets
        if (lastCompletedSets.isEmpty()) return null

        val currentWeight = currentPlan.sets.firstOrNull()?.weightKg ?: lastRecord.primaryWeightKg
        val targetReps = currentPlan.sets.firstOrNull()?.reps ?: 10

        // Check if user hit the target reps across all completed sets in the last session
        val allSetsHitTarget = lastCompletedSets.all { it.reps >= targetReps }
        val highRepConsistency = lastCompletedSets.size >= 2 && lastCompletedSets.count { it.reps >= targetReps } >= (lastCompletedSets.size - 1)

        if (allSetsHitTarget || highRepConsistency) {
            // Determine smart progressive increment (+2.5kg standard, +2kg for light dumbbells, +5kg for heavy compounds)
            val increment = when {
                currentWeight >= 80.0 -> 5.0
                currentWeight in 20.0..79.0 -> 2.5
                currentWeight > 0.0 -> 2.0
                else -> 2.5
            }

            val suggestedWeight = currentWeight + increment
            val isAlreadyApplied = _appliedProgressions.value[exerciseName] == suggestedWeight

            val repsSummary = lastCompletedSets.joinToString("/") { "${it.reps}" }

            return ProgressionSuggestion(
                exerciseName = exerciseName,
                exerciseId = currentPlan.exerciseId,
                currentWeightKg = currentWeight,
                suggestedWeightKg = suggestedWeight,
                weightDeltaKg = increment,
                reason = "Você atingiu sua meta nas últimas sessões ($repsSummary reps com ${formatWeightDisplay(currentWeight)}kg).",
                recentSessionsSummary = "Última sessão: ${lastCompletedSets.size} séries completadas com sucesso.",
                suggestedReps = targetReps,
                isAccepted = isAlreadyApplied,
                isDismissed = false
            )
        }

        return null
    }

    fun applyProgressionSuggestion(exerciseIndex: Int, suggestedWeight: Double) {
        val current = _activeWorkout.value
        val updatedExercises = current.exercises.toMutableList()
        if (exerciseIndex in updatedExercises.indices) {
            val plan = updatedExercises[exerciseIndex]
            val updatedSets = plan.sets.map { set ->
                if (!set.isCompleted) {
                    set.copy(weightKg = suggestedWeight)
                } else set
            }
            updatedExercises[exerciseIndex] = plan.copy(sets = updatedSets)
            _activeWorkout.value = current.copy(exercises = updatedExercises)

            val map = _appliedProgressions.value.toMutableMap()
            map[plan.exerciseName] = suggestedWeight
            _appliedProgressions.value = map
        }
    }

    fun dismissProgressionSuggestion(exerciseName: String) {
        val set = _dismissedProgressions.value.toMutableSet()
        set.add(exerciseName)
        _dismissedProgressions.value = set
    }

    private fun formatWeightDisplay(weight: Double): String {
        return if (weight % 1.0 == 0.0) weight.toInt().toString() else String.format(java.util.Locale.US, "%.1f", weight)
    }

    // --- Daily Workout Reminders Configuration ---
    fun updateReminderSettings(settings: WorkoutReminderSettings) {
        val app = getApplication<Application>()
        _reminderSettings.value = settings
        WorkoutReminderManager.saveSettings(app, settings)
    }

    fun triggerTestReminderNotification() {
        val app = getApplication<Application>()
        WorkoutReminderManager.showNotification(app, _reminderSettings.value, isTest = true)
    }

    // --- AI Workout Routine Generator ---
    fun generateAIWorkout(prompt: String) {
        viewModelScope.launch {
            _isGeneratingAIWorkout.value = true
            val profile = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()
            try {
                val result = repository.generateAIWorkoutRoutine(prompt, profile)
                _generatedAIWorkout.value = result
                unlockMedalIfNotUnlocked("ai_architect")
            } catch (_: Exception) {
            } finally {
                _isGeneratingAIWorkout.value = false
            }
        }
    }

    fun clearGeneratedAIWorkout() {
        _generatedAIWorkout.value = null
    }

    fun saveGeneratedAIWorkoutAsTemplate(workout: AIWorkoutPlanResult, isFavorite: Boolean = false) {
        viewModelScope.launch {
            val json = try {
                plansAdapter.toJson(workout.exercises)
            } catch (_: Exception) {
                "[]"
            }
            val template = WorkoutTemplate(
                title = workout.title,
                subtitle = workout.subtitle,
                category = workout.category,
                defaultRestSeconds = 60,
                executionDurationMinutes = workout.durationMinutes,
                isPreset = false,
                isFavorite = isFavorite,
                exercisesJson = json,
                description = workout.description,
                createdAtEpochDay = DateUtils.todayEpochDay(),
                timesCompleted = 0
            )
            repository.saveWorkoutTemplate(template)
            _generatedAIWorkout.value = null
        }
    }

    fun startWorkoutFromAIPlan(workout: AIWorkoutPlanResult, location: String = "Academia Smart Fit") {
        val freshPlans = workout.exercises.map { plan ->
            plan.copy(
                sets = plan.sets.map { set ->
                    set.copy(isCompleted = false)
                }
            )
        }
        _activeWorkout.value = ActiveWorkoutState(
            isActive = true,
            templateId = null,
            title = workout.title,
            location = location,
            durationSeconds = 0,
            exercises = freshPlans,
            perceivedExertion = 7,
            notes = "Criado por IA: ${workout.subtitle}",
            restTimerVisible = false
        )
        _generatedAIWorkout.value = null
    }

    // --- AI Coach Chat Assistant ---
    fun sendAICoachMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = AICoachMessage(
            sender = AICoachSender.USER,
            text = userText
        )
        val currentHistory = _aiCoachChatHistory.value
        _aiCoachChatHistory.value = currentHistory + userMsg

        viewModelScope.launch {
            _isAICoachThinking.value = true
            val profile = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()
            val sessions = allWorkoutSessions.value.filter { it.status == SessionStatus.COMPLETED }
            val cardios = allCardioSessions.value
            val totalVolume = sessions.sumOf { it.totalWeightLiftedKg }

            val contextSummary = "Atleta com ${sessions.size} treinos realizados (${totalVolume.toInt()}kg levantados), ${cardios.size} sessões de cardio e meta semanal de ${profile.weeklyGoalDays} dias."

            try {
                val coachResponse = repository.askAICoach(userText, profile, contextSummary)
                _aiCoachChatHistory.value = _aiCoachChatHistory.value + coachResponse
            } catch (_: Exception) {
                _aiCoachChatHistory.value = _aiCoachChatHistory.value + AICoachMessage(
                    sender = AICoachSender.COACH,
                    text = "Mantenha o foco nos princípios fundamentais: sobrecarga progressiva, boa ingestão proteica e descanso adequado.",
                    keyPoints = listOf("Treine com intensidade controlada", "Priorize a boa postura em cada repetição")
                )
            } finally {
                _isAICoachThinking.value = false
            }
        }
    }

    fun clearAICoachChat() {
        _aiCoachChatHistory.value = listOf(
            AICoachMessage(
                sender = AICoachSender.COACH,
                text = "Conversa reiniciada! Como posso te ajudar a atingir seu próximo nível de performance?",
                keyPoints = listOf("Tire dúvidas sobre exercícios", "Ajuste sua alimentação", "Peça sugestões de progressão"),
                suggestedFollowUps = listOf(
                    "Como quebrar platô no supino?",
                    "O que comer no pré-treino para mais energia?",
                    "Como substituir agachamento livre por dores no joelho?"
                )
            )
        )
    }

    // --- Health Connect (Smartwatch Integration) Methods ---
    fun checkHealthConnectStatus() {
        viewModelScope.launch {
            val avail = healthConnectManager.checkAvailability()
            _healthAvailability.value = avail
            if (avail == HealthConnectAvailability.AVAILABLE) {
                val hasPerms = healthConnectManager.hasAllPermissions()
                _healthPermissionsGranted.value = hasPerms
                if (hasPerms) {
                    refreshHealthDailyMetrics()
                }
            }
        }
    }

    fun updateHealthPermissionsGranted(granted: Boolean) {
        _healthPermissionsGranted.value = granted
        if (granted) {
            refreshHealthDailyMetrics()
        }
    }

    fun refreshHealthDailyMetrics() {
        viewModelScope.launch {
            val metrics = healthConnectManager.readTodayHealthMetrics()
            _healthDailyMetrics.value = metrics
        }
    }

    fun syncAllWorkoutsWithHealthConnect() {
        viewModelScope.launch {
            _isHealthSyncing.value = true
            try {
                val workouts = allWorkoutSessions.value
                val cardios = allCardioSessions.value
                val result = healthConnectManager.syncAllHistory(workouts, cardios)
                _healthSyncResultMessage.value = result.message
                refreshHealthDailyMetrics()
                unlockMedalIfNotUnlocked("health_sync")
            } catch (e: Exception) {
                _healthSyncResultMessage.value = "Erro ao sincronizar com Health Connect: ${e.localizedMessage}"
            } finally {
                _isHealthSyncing.value = false
            }
        }
    }

    fun dismissHealthSyncMessage() {
        _healthSyncResultMessage.value = null
    }

    fun writeWeightToHealthConnect(weightKg: Double) {
        viewModelScope.launch {
            healthConnectManager.writeWeight(weightKg)
            refreshHealthDailyMetrics()
        }
    }

    // --- Volume-Based AI Nutrition Module ---
    fun requestVolumeNutritionEvaluation() {
        viewModelScope.launch {
            _isEvaluatingVolumeNutrition.value = true
            val profile = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()
            val sessions = allWorkoutSessions.value
            val cardios = allCardioSessions.value
            try {
                val result = repository.evaluateNutritionFromVolumeHistory(profile, sessions, cardios)
                _volumeNutritionEvaluation.value = result
            } catch (_: Exception) {
                val fallback = repository.evaluateNutritionFromVolumeHistory(profile, sessions, cardios)
                _volumeNutritionEvaluation.value = fallback
            } finally {
                _isEvaluatingVolumeNutrition.value = false
            }
        }
    }

    fun applyNutritionTargetsToProfile(advice: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: DefaultFitnessData.getDefaultUserProfile()
            val updated = current.copy(aiCaloricAdvice = advice)
            repository.saveUserProfile(updated)
        }
    }

    // --- Gemini AI Exercise Execution Guide ---
    fun requestExerciseExecutionGuide(exerciseName: String, muscleGroup: String = "", equipment: String = "") {
        viewModelScope.launch {
            _isLoadingExerciseExecutionGuide.value = true
            try {
                val guide = repository.generateExerciseExecutionGuide(exerciseName, muscleGroup, equipment)
                _activeExerciseExecutionGuide.value = guide
            } catch (_: Exception) {
                val fallbackGuide = repository.generateExerciseExecutionGuide(exerciseName, muscleGroup, equipment)
                _activeExerciseExecutionGuide.value = fallbackGuide
            } finally {
                _isLoadingExerciseExecutionGuide.value = false
            }
        }
    }

    fun clearExerciseExecutionGuide() {
        _activeExerciseExecutionGuide.value = null
    }

    fun speakExerciseTip(text: String) {
        ttsVoiceManager.speak(text)
    }
}
