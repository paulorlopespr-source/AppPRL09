package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.CardioType
import com.example.data.model.Equipment
import com.example.data.model.FitnessGoal
import com.example.data.model.IntensityLevel
import com.example.data.model.MuscleGroup
import com.example.data.model.SessionStatus
import com.example.data.model.WorkoutCategory

class Converters {
    @TypeConverter
    fun fromMuscleGroup(value: MuscleGroup): String = value.name

    @TypeConverter
    fun toMuscleGroup(value: String): MuscleGroup = try {
        MuscleGroup.valueOf(value)
    } catch (e: Exception) {
        MuscleGroup.PEITO
    }

    @TypeConverter
    fun fromEquipment(value: Equipment): String = value.name

    @TypeConverter
    fun toEquipment(value: String): Equipment = try {
        Equipment.valueOf(value)
    } catch (e: Exception) {
        Equipment.BARRA
    }

    @TypeConverter
    fun fromWorkoutCategory(value: WorkoutCategory): String = value.name

    @TypeConverter
    fun toWorkoutCategory(value: String): WorkoutCategory = try {
        WorkoutCategory.valueOf(value)
    } catch (e: Exception) {
        WorkoutCategory.HIPERTROFIA
    }

    @TypeConverter
    fun fromSessionStatus(value: SessionStatus): String = value.name

    @TypeConverter
    fun toSessionStatus(value: String): SessionStatus = try {
        SessionStatus.valueOf(value)
    } catch (e: Exception) {
        SessionStatus.COMPLETED
    }

    @TypeConverter
    fun fromCardioType(value: CardioType): String = value.name

    @TypeConverter
    fun toCardioType(value: String): CardioType = try {
        CardioType.valueOf(value)
    } catch (e: Exception) {
        CardioType.BICICLETA_INDOOR
    }

    @TypeConverter
    fun fromIntensityLevel(value: IntensityLevel): String = value.name

    @TypeConverter
    fun toIntensityLevel(value: String): IntensityLevel = try {
        IntensityLevel.valueOf(value)
    } catch (e: Exception) {
        IntensityLevel.MODERADA
    }

    @TypeConverter
    fun fromFitnessGoal(value: FitnessGoal): String = value.name

    @TypeConverter
    fun toFitnessGoal(value: String): FitnessGoal = try {
        FitnessGoal.valueOf(value)
    } catch (e: Exception) {
        FitnessGoal.GANHO_PESO_HIPERTROFIA
    }
}
