package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BodyMeasurement
import com.example.data.model.CardioSession
import com.example.data.model.Exercise
import com.example.data.model.ExercisePerformanceTarget
import com.example.data.model.ProgressPhoto
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Exercise::class,
        WorkoutTemplate::class,
        WorkoutSession::class,
        CardioSession::class,
        UserProfile::class,
        BodyMeasurement::class,
        ProgressPhoto::class,
        ExercisePerformanceTarget::class,
        com.example.data.model.MealLog::class,
        com.example.data.model.UserMedal::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fitnessDao(): FitnessDao
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    abstract fun exerciseTargetDao(): ExerciseTargetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fittreino_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.fitnessDao())
                }
            }
        }

        private suspend fun populateInitialData(dao: FitnessDao) {
            val exercises = DefaultFitnessData.getDefaultExercises()
            dao.insertExercises(exercises)

            val templates = DefaultFitnessData.getDefaultWorkoutTemplates(exercises)
            templates.forEach { dao.insertWorkoutTemplate(it) }

            dao.insertUserProfile(DefaultFitnessData.getDefaultUserProfile())

            DefaultFitnessData.getDefaultExerciseTargets().forEach {
                dao.insertExerciseTarget(it)
            }

            DefaultFitnessData.getDefaultWorkoutSessions().forEach {
                dao.insertWorkoutSession(it)
            }

            DefaultFitnessData.getDefaultMedals().forEach {
                dao.insertMedal(it)
            }

            DefaultFitnessData.getDefaultMealLogs().forEach {
                dao.insertMealLog(it)
            }
        }
    }
}
