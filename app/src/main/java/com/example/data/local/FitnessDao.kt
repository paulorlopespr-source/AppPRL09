package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BodyMeasurement
import com.example.data.model.CardioSession
import com.example.data.model.Exercise
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessDao {

    // --- Workout Templates ---
    @Query("SELECT * FROM workout_templates ORDER BY isFavorite DESC, isPreset DESC, id ASC")
    fun getAllWorkoutTemplates(): Flow<List<WorkoutTemplate>>

    @Query("SELECT * FROM workout_templates WHERE isFavorite = 1 ORDER BY isPreset DESC, id DESC")
    fun getFavoriteWorkoutTemplates(): Flow<List<WorkoutTemplate>>

    @Query("SELECT * FROM workout_templates WHERE isPreset = 0 ORDER BY isFavorite DESC, id DESC")
    fun getCustomWorkoutTemplates(): Flow<List<WorkoutTemplate>>

    @Query("SELECT * FROM workout_templates WHERE id = :id")
    suspend fun getWorkoutTemplateById(id: Long): WorkoutTemplate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutTemplate(template: WorkoutTemplate): Long

    @Update
    suspend fun updateWorkoutTemplate(template: WorkoutTemplate)

    @Query("UPDATE workout_templates SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setWorkoutTemplateFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM workout_templates WHERE id = :id")
    suspend fun deleteWorkoutTemplateById(id: Long)

    // --- Exercise Library ---
    @Query("SELECT * FROM exercises ORDER BY muscleGroup ASC, name ASC")
    fun getAllExercises(): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise): Long

    // --- Workout Sessions (History & Agenda) ---
    @Query("SELECT * FROM workout_sessions ORDER BY dateEpochDay DESC, startTimeMillis DESC")
    fun getAllWorkoutSessions(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE dateEpochDay = :epochDay")
    fun getWorkoutSessionsForDate(epochDay: Long): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE dateEpochDay BETWEEN :startEpochDay AND :endEpochDay ORDER BY dateEpochDay ASC")
    fun getWorkoutSessionsInRange(startEpochDay: Long, endEpochDay: Long): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    suspend fun getWorkoutSessionById(id: Long): WorkoutSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSession(session: WorkoutSession): Long

    @Update
    suspend fun updateWorkoutSession(session: WorkoutSession)

    @Query("DELETE FROM workout_sessions WHERE id = :id")
    suspend fun deleteWorkoutSessionById(id: Long)

    // --- Cardio Sessions ---
    @Query("SELECT * FROM cardio_sessions ORDER BY dateEpochDay DESC, timestampMillis DESC")
    fun getAllCardioSessions(): Flow<List<CardioSession>>

    @Query("SELECT * FROM cardio_sessions WHERE dateEpochDay = :epochDay")
    fun getCardioSessionsForDate(epochDay: Long): Flow<List<CardioSession>>

    @Query("SELECT * FROM cardio_sessions WHERE dateEpochDay BETWEEN :startEpochDay AND :endEpochDay ORDER BY dateEpochDay ASC")
    fun getCardioSessionsInRange(startEpochDay: Long, endEpochDay: Long): Flow<List<CardioSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardioSession(session: CardioSession): Long

    @Query("DELETE FROM cardio_sessions WHERE id = :id")
    suspend fun deleteCardioSessionById(id: Long)

    // --- User Profile & Goals ---
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    // --- Body Measurements (Evolution) ---
    @Query("SELECT * FROM body_measurements ORDER BY dateEpochDay DESC, timestampMillis DESC")
    fun getAllBodyMeasurements(): Flow<List<BodyMeasurement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyMeasurement(measurement: BodyMeasurement): Long

    @Query("DELETE FROM body_measurements WHERE id = :id")
    suspend fun deleteBodyMeasurementById(id: Long)

    // --- Progress & Body Photos (Evolution) ---
    @Query("SELECT * FROM progress_photos ORDER BY isInitial DESC, dateEpochDay DESC, timestampMillis DESC")
    fun getAllProgressPhotos(): Flow<List<com.example.data.model.ProgressPhoto>>

    @Query("SELECT * FROM progress_photos WHERE isInitial = 1 LIMIT 1")
    fun getInitialProgressPhoto(): Flow<com.example.data.model.ProgressPhoto?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressPhoto(photo: com.example.data.model.ProgressPhoto): Long

    @Query("DELETE FROM progress_photos WHERE id = :id")
    suspend fun deleteProgressPhotoById(id: Long)

    // --- Exercise Performance Targets (Goals) ---
    @Query("SELECT * FROM exercise_targets ORDER BY isAchieved ASC, id DESC")
    fun getAllExerciseTargets(): Flow<List<com.example.data.model.ExercisePerformanceTarget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseTarget(target: com.example.data.model.ExercisePerformanceTarget): Long

    @Update
    suspend fun updateExerciseTarget(target: com.example.data.model.ExercisePerformanceTarget)

    @Query("UPDATE exercise_targets SET isAchieved = :isAchieved, achievedDateEpochDay = :achievedEpochDay WHERE id = :id")
    suspend fun setExerciseTargetAchieved(id: Long, isAchieved: Boolean, achievedEpochDay: Long?)

    @Query("DELETE FROM exercise_targets WHERE id = :id")
    suspend fun deleteExerciseTargetById(id: Long)

    // --- Meal Logs & Caloric Intake (Gemini AI Nutrition) ---
    @Query("SELECT * FROM meal_logs ORDER BY dateEpochDay DESC, timestampMillis DESC")
    fun getAllMealLogs(): Flow<List<com.example.data.model.MealLog>>

    @Query("SELECT * FROM meal_logs WHERE dateEpochDay = :epochDay ORDER BY timestampMillis DESC")
    fun getMealLogsForDate(epochDay: Long): Flow<List<com.example.data.model.MealLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealLog(meal: com.example.data.model.MealLog): Long

    @Query("DELETE FROM meal_logs WHERE id = :id")
    suspend fun deleteMealLogById(id: Long)

    // --- Gamification & Medals ---
    @Query("SELECT * FROM user_medals ORDER BY isUnlocked DESC, id ASC")
    fun getAllMedals(): Flow<List<com.example.data.model.UserMedal>>

    @Query("SELECT * FROM user_medals WHERE id = :medalId LIMIT 1")
    suspend fun getMedalById(medalId: String): com.example.data.model.UserMedal?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMedals(medals: List<com.example.data.model.UserMedal>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMedal(medal: com.example.data.model.UserMedal)

    @Query("UPDATE user_medals SET isUnlocked = 1, unlockedDateEpochDay = :epochDay WHERE id = :medalId")
    suspend fun unlockMedal(medalId: String, epochDay: Long)

    @Query("UPDATE user_medals SET progressCurrent = :current, isUnlocked = CASE WHEN :current >= progressMax THEN 1 ELSE isUnlocked END, unlockedDateEpochDay = CASE WHEN :current >= progressMax AND isUnlocked = 0 THEN :epochDay ELSE unlockedDateEpochDay END WHERE id = :medalId")
    suspend fun updateMedalProgress(medalId: String, current: Int, epochDay: Long)

    @Query("SELECT * FROM journey_unlocks ORDER BY unlockedAtMillis DESC")
    fun getJourneyUnlocks(): Flow<List<com.example.data.model.JourneyUnlock>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertJourneyUnlock(unlock: com.example.data.model.JourneyUnlock): Long
}
