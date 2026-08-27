package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ExercisePerformanceTarget
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseTargetDao {

    @Query("SELECT * FROM exercise_targets ORDER BY isAchieved ASC, id DESC")
    fun getAllExerciseTargets(): Flow<List<ExercisePerformanceTarget>>

    @Query("SELECT * FROM exercise_targets WHERE isAchieved = 0 ORDER BY id DESC")
    fun getActiveExerciseTargets(): Flow<List<ExercisePerformanceTarget>>

    @Query("SELECT * FROM exercise_targets WHERE isAchieved = 1 ORDER BY achievedDateEpochDay DESC")
    fun getAchievedExerciseTargets(): Flow<List<ExercisePerformanceTarget>>

    @Query("SELECT * FROM exercise_targets WHERE exerciseId = :exerciseId")
    fun getTargetsByExerciseId(exerciseId: Long): Flow<List<ExercisePerformanceTarget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarget(target: ExercisePerformanceTarget): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTargets(targets: List<ExercisePerformanceTarget>)

    @Update
    suspend fun updateTarget(target: ExercisePerformanceTarget)

    @Query("UPDATE exercise_targets SET isAchieved = :isAchieved, achievedDateEpochDay = :achievedEpochDay WHERE id = :id")
    suspend fun setTargetAchieved(id: Long, isAchieved: Boolean, achievedEpochDay: Long?)

    @Query("UPDATE exercise_targets SET currentWeightKg = :currentWeight, currentReps = :currentReps WHERE id = :id")
    suspend fun updateCurrentPerformance(id: Long, currentWeight: Double, currentReps: Int)

    @Delete
    suspend fun deleteTarget(target: ExercisePerformanceTarget)

    @Query("DELETE FROM exercise_targets WHERE id = :id")
    suspend fun deleteTargetById(id: Long)
}
