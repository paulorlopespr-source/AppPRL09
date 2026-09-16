package com.example.data.backup

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.Converters
import com.example.data.model.BodyMeasurement
import com.example.data.model.CardioSession
import com.example.data.model.Exercise
import com.example.data.model.ExercisePerformanceTarget
import com.example.data.model.MealLog
import com.example.data.model.ProgressPhoto
import com.example.data.model.UserMedal
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutTemplate
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest

@JsonClass(generateAdapter = true)
data class CompleteAppBackup(
    val appIdentifier: String = "FitPr09",
    val backupSchemaVersion: Int = 8,
    val backupTimestampMillis: Long = System.currentTimeMillis(),
    val totalWorkoutsCount: Int = 0,
    val totalCardiosCount: Int = 0,
    val userProfile: UserProfile? = null,
    val exercises: List<Exercise> = emptyList(),
    val workoutTemplates: List<WorkoutTemplate> = emptyList(),
    val workoutSessions: List<WorkoutSession> = emptyList(),
    val cardioSessions: List<CardioSession> = emptyList(),
    val bodyMeasurements: List<BodyMeasurement> = emptyList(),
    val progressPhotos: List<ProgressPhoto> = emptyList(),
    val exerciseTargets: List<ExercisePerformanceTarget> = emptyList(),
    val mealLogs: List<MealLog> = emptyList(),
    val userMedals: List<UserMedal> = emptyList(),
    val unlockedMedalIds: List<String> = emptyList(),
    val prefKeepScreenOn: Boolean = true
)

data class BackupImportResult(
    val isSuccess: Boolean,
    val summaryMessage: String,
    val workoutsRestored: Int = 0,
    val cardiosRestored: Int = 0,
    val templatesRestored: Int = 0,
    val measurementsRestored: Int = 0,
    val errorMessage: String? = null
)

class DataBackupManager(
    private val context: Context,
    private val database: AppDatabase
) {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val backupAdapter = moshi.adapter(CompleteAppBackup::class.java)

    /**
     * Gera o backup JSON completo com todos os dados persistidos pelo atleta.
     */
    suspend fun generateCompleteBackupJson(): String = withContext(Dispatchers.IO) {
        val dao = database.fitnessDao()

        val profile = dao.getUserProfile().first()
        val exercises = dao.getAllExercises().first()
        val templates = dao.getAllWorkoutTemplates().first()
        val sessions = dao.getAllWorkoutSessions().first()
        val cardios = dao.getAllCardioSessions().first()
        val measurements = dao.getAllBodyMeasurements().first()
        val photos = dao.getAllProgressPhotos().first()
        val targets = dao.getAllExerciseTargets().first()
        val meals = dao.getAllMealLogs().first()
        val medals = dao.getAllMedals().first()

        val prefs = context.getSharedPreferences("fitpr09_prefs", Context.MODE_PRIVATE)
        val unlockedMedals = prefs.getStringSet("unlocked_medals_backup", emptySet())?.toList() ?: emptyList()
        val keepScreenOn = prefs.getBoolean("pref_keep_screen_on", true)

        val backup = CompleteAppBackup(
            appIdentifier = "FitPr09",
            backupSchemaVersion = 8,
            backupTimestampMillis = System.currentTimeMillis(),
            totalWorkoutsCount = sessions.size,
            totalCardiosCount = cardios.size,
            userProfile = profile,
            exercises = exercises,
            workoutTemplates = templates,
            workoutSessions = sessions,
            cardioSessions = cardios,
            bodyMeasurements = measurements,
            progressPhotos = photos,
            exerciseTargets = targets,
            mealLogs = meals,
            userMedals = medals,
            unlockedMedalIds = unlockedMedals,
            prefKeepScreenOn = keepScreenOn
        )

        backupAdapter.indent("  ").toJson(backup)
    }

    /**
     * Valida e restaura completamente o banco a partir do arquivo/string de backup JSON.
     */
    suspend fun restoreCompleteBackup(jsonString: String): BackupImportResult = withContext(Dispatchers.IO) {
        try {
            val backup = backupAdapter.fromJson(jsonString)
                ?: return@withContext BackupImportResult(
                    isSuccess = false,
                    summaryMessage = "Arquivo de backup inválido ou corrompido.",
                    errorMessage = "Parser retornou nulo."
                )

            if (backup.appIdentifier != "FitPr09" && backup.workoutSessions.isEmpty() && backup.userProfile == null) {
                return@withContext BackupImportResult(
                    isSuccess = false,
                    summaryMessage = "O arquivo não foi reconhecido como um backup legítimo do FitPr09.",
                    errorMessage = "Identificador ausente ou conteúdo incompatível."
                )
            }

            val dao = database.fitnessDao()

            // Restauração atômica
            if (backup.userProfile != null) {
                dao.insertUserProfile(backup.userProfile)
            }

            if (backup.exercises.isNotEmpty()) {
                dao.insertExercises(backup.exercises)
            }

            if (backup.workoutTemplates.isNotEmpty()) {
                backup.workoutTemplates.forEach { dao.insertWorkoutTemplate(it) }
            }

            if (backup.workoutSessions.isNotEmpty()) {
                backup.workoutSessions.forEach { dao.insertWorkoutSession(it) }
            }

            if (backup.cardioSessions.isNotEmpty()) {
                backup.cardioSessions.forEach { dao.insertCardioSession(it) }
            }

            if (backup.bodyMeasurements.isNotEmpty()) {
                backup.bodyMeasurements.forEach { dao.insertBodyMeasurement(it) }
            }

            if (backup.progressPhotos.isNotEmpty()) {
                backup.progressPhotos.forEach { dao.insertProgressPhoto(it) }
            }

            if (backup.exerciseTargets.isNotEmpty()) {
                backup.exerciseTargets.forEach { dao.insertExerciseTarget(it) }
            }

            if (backup.mealLogs.isNotEmpty()) {
                backup.mealLogs.forEach { dao.insertMealLog(it) }
            }

            if (backup.userMedals.isNotEmpty()) {
                dao.insertMedals(backup.userMedals)
            }

            // Restaura preferências persistentes
            val prefs = context.getSharedPreferences("fitpr09_prefs", Context.MODE_PRIVATE)
            val editor = prefs.edit()
            if (backup.unlockedMedalIds.isNotEmpty()) {
                editor.putStringSet("unlocked_medals_backup", backup.unlockedMedalIds.toSet())
            }
            editor.putBoolean("pref_keep_screen_on", backup.prefKeepScreenOn)
            editor.apply()

            BackupImportResult(
                isSuccess = true,
                summaryMessage = "Restauração concluída com sucesso! ${backup.workoutSessions.size} treinos e ${backup.cardioSessions.size} cardios recuperados.",
                workoutsRestored = backup.workoutSessions.size,
                cardiosRestored = backup.cardioSessions.size,
                templatesRestored = backup.workoutTemplates.size,
                measurementsRestored = backup.bodyMeasurements.size
            )
        } catch (e: Exception) {
            BackupImportResult(
                isSuccess = false,
                summaryMessage = "Erro ao processar arquivo de backup: ${e.localizedMessage}",
                errorMessage = e.message
            )
        }
    }

    /**
     * Salva o backup em um arquivo local em cache para compartilhamento externo.
     */
    suspend fun saveBackupToLocalFile(jsonString: String): File = withContext(Dispatchers.IO) {
        val backupDir = File(context.cacheDir, "backups")
        if (!backupDir.exists()) backupDir.mkdirs()
        val file = File(backupDir, "FitPr09_Backup_${System.currentTimeMillis()}.json")
        file.writeText(jsonString)
        file
    }
}
