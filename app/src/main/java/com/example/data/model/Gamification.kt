package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import com.squareup.moshi.JsonClass

enum class MedalRarity(val displayName: String, val badgeLabel: String, val defaultXp: Int) {
    BRONZE("Bronze", "🥉 Bronze", 100),
    PRATA("Prata", "🥈 Prata", 250),
    OURO("Ouro", "🥇 Ouro", 500),
    MASTER_SUPERAÇÃO("Master da Superação", "👑 Master da Superação", 1000);

    companion object {
        fun fromString(value: String): MedalRarity {
            return when {
                value.contains("Master", ignoreCase = true) || value.contains("Superação", ignoreCase = true) -> MASTER_SUPERAÇÃO
                value.contains("Ouro", ignoreCase = true) -> OURO
                value.contains("Prata", ignoreCase = true) -> PRATA
                else -> BRONZE
            }
        }
    }
}

enum class GoalPeriod(val displayName: String, val tag: String) {
    SEMANAL("Semanal", "📅 Metas Semanais"),
    MENSAL("Mensal", "🗓️ Metas Mensais"),
    ANUAL("Anual", "🏆 Metas Anuais"),
    ESPECIAL("Especial", "⚡ Marcos Especiais");

    companion object {
        fun fromString(value: String): GoalPeriod {
            return when {
                value.contains("Semanal", ignoreCase = true) -> SEMANAL
                value.contains("Mensal", ignoreCase = true) -> MENSAL
                value.contains("Anual", ignoreCase = true) -> ANUAL
                else -> ESPECIAL
            }
        }
    }
}

@Entity(tableName = "user_medals")
@JsonClass(generateAdapter = true)
data class UserMedal(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val category: String, // CONSISTENCIA, FORCA, VOLUME, CARDIO, NUTRICAO, ESPECIAL
    val period: String = "SEMANAL", // SEMANAL, MENSAL, ANUAL, ESPECIAL
    val iconEmoji: String,
    val isUnlocked: Boolean = false,
    val unlockedDateEpochDay: Long? = null,
    val progressCurrent: Int = 0,
    val progressMax: Int = 1,
    val rarity: String = "Bronze", // Bronze, Prata, Ouro, Master da Superação
    val xpReward: Int = 100
) {
    val progressPercent: Float
        get() = if (progressMax > 0) (progressCurrent.toFloat() / progressMax.toFloat()).coerceIn(0f, 1f) else if (isUnlocked) 1f else 0f

    val rarityEnum: MedalRarity
        get() = MedalRarity.fromString(rarity)

    val periodEnum: GoalPeriod
        get() = GoalPeriod.fromString(period)
}

data class GamificationOverview(
    val totalXp: Int,
    val currentLevel: Int,
    val currentLevelTitle: String,
    val currentLevelXp: Int,
    val nextLevelXp: Int,
    val levelProgressPercent: Float,
    val unlockedMedalsCount: Int,
    val totalMedalsCount: Int,
    val bronzeCount: Int,
    val prataCount: Int,
    val ouroCount: Int,
    val masterCount: Int
)

@Entity(tableName = "journey_unlocks", indices = [Index(value = ["cardLevel"], unique = true)])
data class JourneyUnlock(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardLevel: Int,
    val journeyRank: String,
    val sessionId: Long,
    val unlockedAtMillis: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class PersonalRecordCelebration(
    val exerciseName: String,
    val previousWeightKg: Double,
    val newWeightKg: Double,
    val reps: Int,
    val title: String = "Novo Recorde Pessoal! 🏆",
    val description: String = "Você superou seu limite anterior com maestria!"
)

