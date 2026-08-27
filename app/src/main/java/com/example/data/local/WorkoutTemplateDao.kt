package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutTemplateDao {

    @Query("SELECT * FROM workout_templates ORDER BY isFavorite DESC, isPreset DESC, id ASC")
    fun getAllWorkoutTemplates(): Flow<List<WorkoutTemplate>>

    @Query("SELECT * FROM workout_templates WHERE isFavorite = 1 ORDER BY isPreset DESC, id DESC")
    fun getFavoriteWorkoutTemplates(): Flow<List<WorkoutTemplate>>

    @Query("SELECT * FROM workout_templates WHERE isPreset = 0 ORDER BY isFavorite DESC, id DESC")
    fun getCustomWorkoutTemplates(): Flow<List<WorkoutTemplate>>

    @Query("SELECT * FROM workout_templates WHERE isPreset = 1 ORDER BY isFavorite DESC, id ASC")
    fun getPresetWorkoutTemplates(): Flow<List<WorkoutTemplate>>

    @Query("SELECT * FROM workout_templates WHERE category = :category ORDER BY isFavorite DESC, id ASC")
    fun getWorkoutTemplatesByCategory(category: WorkoutCategory): Flow<List<WorkoutTemplate>>

    @Query("SELECT * FROM workout_templates WHERE id = :id")
    suspend fun getWorkoutTemplateById(id: Long): WorkoutTemplate?

    @Query("SELECT * FROM workout_templates WHERE id = :id")
    fun getWorkoutTemplateByIdFlow(id: Long): Flow<WorkoutTemplate?>

    @Query("SELECT * FROM workout_templates WHERE title LIKE '%' || :query || '%' OR subtitle LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY isFavorite DESC, id ASC")
    fun searchWorkoutTemplates(query: String): Flow<List<WorkoutTemplate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutTemplate(template: WorkoutTemplate): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutTemplates(templates: List<WorkoutTemplate>)

    @Update
    suspend fun updateWorkoutTemplate(template: WorkoutTemplate)

    @Query("UPDATE workout_templates SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE workout_templates SET timesCompleted = timesCompleted + 1 WHERE id = :id")
    suspend fun incrementTimesCompleted(id: Long)

    @Delete
    suspend fun deleteWorkoutTemplate(template: WorkoutTemplate)

    @Query("DELETE FROM workout_templates WHERE id = :id")
    suspend fun deleteWorkoutTemplateById(id: Long)

    @Query("DELETE FROM workout_templates WHERE isPreset = 0")
    suspend fun deleteAllCustomTemplates()
}
