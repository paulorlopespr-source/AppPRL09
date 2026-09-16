package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
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
    version = 8,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fitnessDao(): FitnessDao
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    abstract fun exerciseTargetDao(): ExerciseTargetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `cardio_sessions` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `type` TEXT NOT NULL,
                        `dateEpochDay` INTEGER NOT NULL,
                        `timestampMillis` INTEGER NOT NULL,
                        `durationMinutes` INTEGER NOT NULL,
                        `distanceKm` REAL,
                        `avgHeartRateBpm` INTEGER,
                        `intensity` TEXT NOT NULL,
                        `location` TEXT NOT NULL,
                        `caloriesBurned` INTEGER NOT NULL,
                        `notes` TEXT NOT NULL,
                        `aiEvaluation` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `body_measurements` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `dateEpochDay` INTEGER NOT NULL,
                        `timestampMillis` INTEGER NOT NULL,
                        `weightKg` REAL NOT NULL,
                        `bodyFatPercentage` REAL,
                        `chestCm` REAL,
                        `waistCm` REAL,
                        `armCm` REAL,
                        `thighCm` REAL,
                        `calfCm` REAL,
                        `notes` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `progress_photos` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `dateEpochDay` INTEGER NOT NULL,
                        `timestampMillis` INTEGER NOT NULL,
                        `imageUri` TEXT NOT NULL,
                        `weightKg` REAL,
                        `monthLabel` TEXT NOT NULL,
                        `isInitial` INTEGER NOT NULL,
                        `bodyFatPercentage` REAL,
                        `notes` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `exercise_targets` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `exerciseId` INTEGER,
                        `exerciseName` TEXT NOT NULL,
                        `targetWeightKg` REAL NOT NULL,
                        `targetReps` INTEGER NOT NULL,
                        `currentWeightKg` REAL NOT NULL,
                        `currentReps` INTEGER NOT NULL,
                        `targetDateEpochDay` INTEGER,
                        `isAchieved` INTEGER NOT NULL,
                        `achievedDateEpochDay` INTEGER,
                        `notes` TEXT NOT NULL,
                        `createdAtEpochDay` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `workout_sessions` ADD COLUMN `latitude` REAL")
                database.execSQL("ALTER TABLE `workout_sessions` ADD COLUMN `longitude` REAL")
                database.execSQL("ALTER TABLE `workout_sessions` ADD COLUMN `locationAddress` TEXT")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `meal_logs` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `dateEpochDay` INTEGER NOT NULL,
                        `timestampMillis` INTEGER NOT NULL,
                        `mealType` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `estimatedCalories` INTEGER NOT NULL,
                        `proteinGrams` REAL NOT NULL,
                        `carbsGrams` REAL NOT NULL,
                        `fatsGrams` REAL NOT NULL,
                        `fiberGrams` REAL NOT NULL,
                        `aiInsight` TEXT NOT NULL,
                        `healthRating` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user_medals` (
                        `id` TEXT PRIMARY KEY NOT NULL,
                        `title` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `period` TEXT NOT NULL,
                        `iconEmoji` TEXT NOT NULL,
                        `isUnlocked` INTEGER NOT NULL,
                        `unlockedDateEpochDay` INTEGER,
                        `progressCurrent` INTEGER NOT NULL,
                        `progressMax` INTEGER NOT NULL,
                        `rarity` TEXT NOT NULL,
                        `xpReward` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                database.execSQL("ALTER TABLE `workout_sessions` ADD COLUMN `aiCaloricEvaluation` TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Ensure index on dateEpochDay and foreign lookups
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_sessions_dateEpochDay` ON `workout_sessions` (`dateEpochDay`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_cardio_sessions_dateEpochDay` ON `cardio_sessions` (`dateEpochDay`)")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fittreino_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8
                    )
                    .fallbackToDestructiveMigrationOnDowngrade(true)
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
