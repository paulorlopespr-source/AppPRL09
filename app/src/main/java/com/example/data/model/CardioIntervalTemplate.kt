package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cardio_interval_templates")
data class CardioIntervalTemplate(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val cardioType: String,
    val configJson: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)
