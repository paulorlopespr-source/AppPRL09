package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Exercise::class, WorkoutTemplate::class, WorkoutSession::class, CardioSession::class, UserProfile::class, BodyMeasurement::class, ProgressPhoto::class, ExercisePerformanceTarget::class, com.example.data.model.MealLog::class, com.example.data.model.UserMedal::class],
    version = 9,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fitnessDao(): FitnessDao
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    abstract fun exerciseTargetDao(): ExerciseTargetDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) { override fun migrate(database: SupportSQLiteDatabase) { database.execSQL("CREATE TABLE IF NOT EXISTS `cardio_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `type` TEXT NOT NULL, `dateEpochDay` INTEGER NOT NULL, `timestampMillis` INTEGER NOT NULL, `durationMinutes` INTEGER NOT NULL, `distanceKm` REAL, `avgHeartRateBpm` INTEGER, `intensity` TEXT NOT NULL, `location` TEXT NOT NULL, `caloriesBurned` INTEGER NOT NULL, `notes` TEXT NOT NULL, `aiEvaluation` TEXT NOT NULL)") } }
        val MIGRATION_2_3 = object : Migration(2, 3) { override fun migrate(database: SupportSQLiteDatabase) { database.execSQL("CREATE TABLE IF NOT EXISTS `body_measurements` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dateEpochDay` INTEGER NOT NULL, `timestampMillis` INTEGER NOT NULL, `weightKg` REAL NOT NULL, `bodyFatPercentage` REAL, `chestCm` REAL, `waistCm` REAL, `armCm` REAL, `thighCm` REAL, `calfCm` REAL, `notes` TEXT NOT NULL)") } }
        val MIGRATION_3_4 = object : Migration(3, 4) { override fun migrate(database: SupportSQLiteDatabase) { database.execSQL("CREATE TABLE IF NOT EXISTS `progress_photos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dateEpochDay` INTEGER NOT NULL, `timestampMillis` INTEGER NOT NULL, `imageUri` TEXT NOT NULL, `weightKg` REAL, `monthLabel` TEXT NOT NULL, `isInitial` INTEGER NOT NULL, `bodyFatPercentage` REAL, `notes` TEXT NOT NULL)"); database.execSQL("CREATE TABLE IF NOT EXISTS `exercise_targets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `exerciseId` INTEGER, `exerciseName` TEXT NOT NULL, `targetWeightKg` REAL NOT NULL, `targetReps` INTEGER NOT NULL, `currentWeightKg` REAL NOT NULL, `currentReps` INTEGER NOT NULL, `targetDateEpochDay` INTEGER, `isAchieved` INTEGER NOT NULL, `achievedDateEpochDay` INTEGER, `notes` TEXT NOT NULL, `createdAtEpochDay` INTEGER NOT NULL)") } }
        val MIGRATION_4_5 = object : Migration(4, 5) { override fun migrate(database: SupportSQLiteDatabase) { database.execSQL("ALTER TABLE `workout_sessions` ADD COLUMN `latitude` REAL"); database.execSQL("ALTER TABLE `workout_sessions` ADD COLUMN `longitude` REAL"); database.execSQL("ALTER TABLE `workout_sessions` ADD COLUMN `locationAddress` TEXT") } }
        val MIGRATION_5_6 = object : Migration(5, 6) { override fun migrate(database: SupportSQLiteDatabase) { database.execSQL("CREATE TABLE IF NOT EXISTS `meal_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dateEpochDay` INTEGER NOT NULL, `timestampMillis` INTEGER NOT NULL, `mealType` TEXT NOT NULL, `description` TEXT NOT NULL, `estimatedCalories` INTEGER NOT NULL, `proteinGrams` REAL NOT NULL, `carbsGrams` REAL NOT NULL, `fatsGrams` REAL NOT NULL, `fiberGrams` REAL NOT NULL, `aiInsight` TEXT NOT NULL, `healthRating` TEXT NOT NULL)") } }
        val MIGRATION_6_7 = object : Migration(6, 7) { override fun migrate(database: SupportSQLiteDatabase) { database.execSQL("CREATE TABLE IF NOT EXISTS `user_medals` (`id` TEXT PRIMARY KEY NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `category` TEXT NOT NULL, `period` TEXT NOT NULL, `iconEmoji` TEXT NOT NULL, `isUnlocked` INTEGER NOT NULL, `unlockedDateEpochDay` INTEGER, `progressCurrent` INTEGER NOT NULL, `progressMax` INTEGER NOT NULL, `rarity` TEXT NOT NULL, `xpReward` INTEGER NOT NULL)"); database.execSQL("ALTER TABLE `workout_sessions` ADD COLUMN `aiCaloricEvaluation` TEXT NOT NULL DEFAULT ''") } }
        val MIGRATION_7_8 = object : Migration(7, 8) { override fun migrate(database: SupportSQLiteDatabase) { database.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_sessions_dateEpochDay` ON `workout_sessions` (`dateEpochDay`)"); database.execSQL("CREATE INDEX IF NOT EXISTS `index_cardio_sessions_dateEpochDay` ON `cardio_sessions` (`dateEpochDay`)") } }
        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `cardio_sessions` ADD COLUMN `movingTimeSeconds` INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `cardio_sessions` ADD COLUMN `routeJson` TEXT NOT NULL DEFAULT '[]'")
                database.execSQL("ALTER TABLE `cardio_sessions` ADD COLUMN `splitsJson` TEXT NOT NULL DEFAULT '[]'")
                database.execSQL("ALTER TABLE `cardio_sessions` ADD COLUMN `elevationGainMeters` REAL NOT NULL DEFAULT 0.0")
                database.execSQL("ALTER TABLE `cardio_sessions` ADD COLUMN `minElevationMeters` REAL")
                database.execSQL("ALTER TABLE `cardio_sessions` ADD COLUMN `maxElevationMeters` REAL")
                database.execSQL("ALTER TABLE `cardio_sessions` ADD COLUMN `avgSpeedKmh` REAL")
                database.execSQL("ALTER TABLE `cardio_sessions` ADD COLUMN `avgPaceSecondsPerKm` INTEGER")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase = INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "fittreino_database")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                .addCallback(DatabaseCallback(scope)).build()
            INSTANCE = instance; instance
        }
    }

    private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) { super.onCreate(db); INSTANCE?.let { database -> scope.launch(Dispatchers.IO) { populateInitialData(database.fitnessDao()) } } }
        private suspend fun populateInitialData(dao: FitnessDao) {
            val exercises = DefaultFitnessData.getDefaultExercises(); dao.insertExercises(exercises)
            DefaultFitnessData.getDefaultWorkoutTemplates(exercises).forEach { dao.insertWorkoutTemplate(it) }
            dao.insertUserProfile(DefaultFitnessData.getDefaultUserProfile())
            DefaultFitnessData.getDefaultExerciseTargets().forEach { dao.insertExerciseTarget(it) }
            DefaultFitnessData.getDefaultWorkoutSessions().forEach { dao.insertWorkoutSession(it) }
            DefaultFitnessData.getDefaultMedals().forEach { dao.insertMedal(it) }
            DefaultFitnessData.getDefaultMealLogs().forEach { dao.insertMealLog(it) }
        }
    }
}
