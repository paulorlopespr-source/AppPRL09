package com.example.data.repository

import com.example.data.ai.GeminiCalorieService
import com.example.data.local.FitnessDao
import com.example.data.model.BodyMeasurement
import com.example.data.model.CardioSession
import com.example.data.model.Exercise
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutTemplate
import kotlinx.coroutines.flow.Flow

class FitnessRepository(
    private val dao: FitnessDao,
    private val geminiService: GeminiCalorieService = GeminiCalorieService()
) {

    // --- Workout Templates (Favorites & Custom Routines) ---
    val allWorkoutTemplates: Flow<List<WorkoutTemplate>> = dao.getAllWorkoutTemplates()
    val favoriteWorkoutTemplates: Flow<List<WorkoutTemplate>> = dao.getFavoriteWorkoutTemplates()
    val customWorkoutTemplates: Flow<List<WorkoutTemplate>> = dao.getCustomWorkoutTemplates()

    suspend fun getWorkoutTemplateById(id: Long): WorkoutTemplate? = dao.getWorkoutTemplateById(id)

    suspend fun saveWorkoutTemplate(template: WorkoutTemplate): Long = dao.insertWorkoutTemplate(template)

    suspend fun updateWorkoutTemplate(template: WorkoutTemplate) = dao.updateWorkoutTemplate(template)

    suspend fun setWorkoutTemplateFavorite(id: Long, isFavorite: Boolean) =
        dao.setWorkoutTemplateFavorite(id, isFavorite)

    suspend fun deleteWorkoutTemplateById(id: Long) = dao.deleteWorkoutTemplateById(id)

    suspend fun duplicateWorkoutTemplate(template: WorkoutTemplate): Long {
        val duplicated = template.copy(
            id = 0,
            title = "${template.title} (Cópia)",
            isPreset = false,
            isFavorite = false,
            timesCompleted = 0
        )
        return dao.insertWorkoutTemplate(duplicated)
    }

    // --- Exercises ---
    val allExercises: Flow<List<Exercise>> = dao.getAllExercises()

    suspend fun insertExercise(exercise: Exercise): Long = dao.insertExercise(exercise)

    // --- Workout Sessions ---
    val allWorkoutSessions: Flow<List<WorkoutSession>> = dao.getAllWorkoutSessions()

    fun getWorkoutSessionsForDate(epochDay: Long): Flow<List<WorkoutSession>> =
        dao.getWorkoutSessionsForDate(epochDay)

    fun getWorkoutSessionsInRange(startEpochDay: Long, endEpochDay: Long): Flow<List<WorkoutSession>> =
        dao.getWorkoutSessionsInRange(startEpochDay, endEpochDay)

    suspend fun getWorkoutSessionById(id: Long): WorkoutSession? = dao.getWorkoutSessionById(id)

    suspend fun saveWorkoutSession(session: WorkoutSession): Long = dao.insertWorkoutSession(session)

    suspend fun updateWorkoutSession(session: WorkoutSession) = dao.updateWorkoutSession(session)

    suspend fun deleteWorkoutSessionById(id: Long) = dao.deleteWorkoutSessionById(id)

    // --- Cardio Sessions ---
    val allCardioSessions: Flow<List<CardioSession>> = dao.getAllCardioSessions()

    fun getCardioSessionsForDate(epochDay: Long): Flow<List<CardioSession>> =
        dao.getCardioSessionsForDate(epochDay)

    fun getCardioSessionsInRange(startEpochDay: Long, endEpochDay: Long): Flow<List<CardioSession>> =
        dao.getCardioSessionsInRange(startEpochDay, endEpochDay)

    suspend fun saveCardioSession(session: CardioSession): Long = dao.insertCardioSession(session)

    suspend fun deleteCardioSessionById(id: Long) = dao.deleteCardioSessionById(id)

    // --- User Profile & Measurements ---
    val userProfile: Flow<UserProfile?> = dao.getUserProfile()

    suspend fun saveUserProfile(profile: UserProfile) = dao.insertUserProfile(profile)

    val allBodyMeasurements: Flow<List<BodyMeasurement>> = dao.getAllBodyMeasurements()

    suspend fun saveBodyMeasurement(measurement: BodyMeasurement): Long =
        dao.insertBodyMeasurement(measurement)

    suspend fun deleteBodyMeasurementById(id: Long) = dao.deleteBodyMeasurementById(id)

    // --- Progress Photos (Evolution) ---
    val allProgressPhotos: Flow<List<com.example.data.model.ProgressPhoto>> = dao.getAllProgressPhotos()
    val initialProgressPhoto: Flow<com.example.data.model.ProgressPhoto?> = dao.getInitialProgressPhoto()

    suspend fun saveProgressPhoto(photo: com.example.data.model.ProgressPhoto): Long =
        dao.insertProgressPhoto(photo)

    suspend fun deleteProgressPhotoById(id: Long) = dao.deleteProgressPhotoById(id)

    // --- Exercise Performance Targets (Goals) ---
    val allExerciseTargets: Flow<List<com.example.data.model.ExercisePerformanceTarget>> = dao.getAllExerciseTargets()

    suspend fun saveExerciseTarget(target: com.example.data.model.ExercisePerformanceTarget): Long =
        dao.insertExerciseTarget(target)

    suspend fun updateExerciseTarget(target: com.example.data.model.ExercisePerformanceTarget) =
        dao.updateExerciseTarget(target)

    suspend fun setExerciseTargetAchieved(id: Long, isAchieved: Boolean, achievedEpochDay: Long?) =
        dao.setExerciseTargetAchieved(id, isAchieved, achievedEpochDay)

    suspend fun deleteExerciseTargetById(id: Long) = dao.deleteExerciseTargetById(id)

    // --- Gemini AI ---
    suspend fun calculateCaloricExpenditure(
        exercisesDoneJson: String,
        durationMinutes: Int,
        perceivedExertion: Int,
        totalWeightKg: Double,
        profile: UserProfile,
        recentCardio: CardioSession? = null
    ): String {
        val dummySession = WorkoutSession(
            title = "Treino Analisado",
            dateEpochDay = java.time.LocalDate.now().toEpochDay(),
            durationSeconds = durationMinutes * 60,
            perceivedExertion = perceivedExertion,
            totalWeightLiftedKg = totalWeightKg,
            exercisesDoneJson = exercisesDoneJson
        )
        return geminiService.evaluateWorkoutCaloriesAndPerformance(dummySession, profile, recentCardio)
    }

    suspend fun evaluateWorkoutWithAI(
        session: WorkoutSession,
        profile: UserProfile,
        recentCardio: CardioSession? = null
    ): String {
        return geminiService.evaluateWorkoutCaloriesAndPerformance(session, profile, recentCardio)
    }

    suspend fun evaluateCardioWithAI(
        cardio: CardioSession,
        profile: UserProfile
    ): String {
        return geminiService.evaluateCardioCalories(cardio, profile)
    }

    suspend fun getAICoachAdvice(
        profile: UserProfile,
        workoutsCount: Int,
        totalVolumeKg: Double,
        cardioMinutes: Int
    ): String {
        return geminiService.getPersonalizedAICoachAdvice(profile, workoutsCount, totalVolumeKg, cardioMinutes)
    }

    suspend fun generateAIWorkoutRoutine(
        prompt: String,
        profile: UserProfile
    ): com.example.data.model.AIWorkoutPlanResult {
        return geminiService.generateAIWorkoutRoutine(prompt, profile)
    }

    suspend fun askAICoach(
        query: String,
        profile: UserProfile,
        contextSummary: String
    ): com.example.data.model.AICoachMessage {
        return geminiService.askAICoach(query, profile, contextSummary)
    }

    // --- Meals & Nutrition (Gemini AI Calorie Estimator) ---
    val allMealLogs: Flow<List<com.example.data.model.MealLog>> = dao.getAllMealLogs()

    fun getMealLogsForDate(epochDay: Long): Flow<List<com.example.data.model.MealLog>> =
        dao.getMealLogsForDate(epochDay)

    suspend fun saveMealLog(meal: com.example.data.model.MealLog): Long = dao.insertMealLog(meal)

    suspend fun deleteMealLogById(id: Long) = dao.deleteMealLogById(id)

    suspend fun analyzeMealWithGemini(
        mealText: String,
        mealType: String,
        userProfile: UserProfile
    ): com.example.data.model.MealAnalysisResult {
        return geminiService.analyzeMealDescription(mealText, mealType, userProfile)
    }

    suspend fun evaluateNutritionFromVolumeHistory(
        userProfile: UserProfile,
        workoutSessions: List<WorkoutSession>,
        cardioSessions: List<CardioSession>
    ): com.example.data.model.VolumeNutritionEvaluationResult {
        return geminiService.evaluateNutritionFromVolumeHistory(userProfile, workoutSessions, cardioSessions)
    }

    suspend fun generateExerciseExecutionGuide(
        exerciseName: String,
        muscleGroup: String,
        equipment: String
    ): com.example.data.model.ExerciseExecutionGuideResult {
        return geminiService.generateExerciseExecutionGuide(exerciseName, muscleGroup, equipment)
    }

    // --- Gamification & Medals ---
    val allMedals: Flow<List<com.example.data.model.UserMedal>> = dao.getAllMedals()

    suspend fun getMedalById(medalId: String): com.example.data.model.UserMedal? = dao.getMedalById(medalId)

    suspend fun saveMedal(medal: com.example.data.model.UserMedal) = dao.insertMedal(medal)

    suspend fun insertMedals(medals: List<com.example.data.model.UserMedal>) = dao.insertMedals(medals)

    suspend fun unlockMedal(medalId: String, epochDay: Long = java.time.LocalDate.now().toEpochDay()) =
        dao.unlockMedal(medalId, epochDay)

    suspend fun updateMedalProgress(medalId: String, current: Int, epochDay: Long = java.time.LocalDate.now().toEpochDay()) =
        dao.updateMedalProgress(medalId, current, epochDay)
}
