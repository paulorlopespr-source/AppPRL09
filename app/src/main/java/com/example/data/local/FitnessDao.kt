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
    @Query("SELECT * FROM workout_templates ORDER BY isPreset DESC, id ASC")
    fun getAllWorkoutTemplates(): Flow<List<WorkoutTemplate>>

    @Query("SELECT * FROM workout_templates WHERE id = :id")
    suspend fun getWorkoutTemplateById(id: Long): WorkoutTemplate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutTemplate(template: WorkoutTemplate): Long

    @Update
    suspend fun updateWorkoutTemplate(template: WorkoutTemplate)

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
}
