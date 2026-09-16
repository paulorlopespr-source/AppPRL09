package com.example.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE cardio_sessions ADD COLUMN movingTimeSeconds INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE cardio_sessions ADD COLUMN routeJson TEXT NOT NULL DEFAULT '[]'")
            db.execSQL("ALTER TABLE cardio_sessions ADD COLUMN splitsJson TEXT NOT NULL DEFAULT '[]'")
            db.execSQL("ALTER TABLE cardio_sessions ADD COLUMN elevationGainMeters REAL NOT NULL DEFAULT 0.0")
            db.execSQL("ALTER TABLE cardio_sessions ADD COLUMN minElevationMeters REAL")
            db.execSQL("ALTER TABLE cardio_sessions ADD COLUMN maxElevationMeters REAL")
            db.execSQL("ALTER TABLE cardio_sessions ADD COLUMN avgSpeedKmh REAL")
            db.execSQL("ALTER TABLE cardio_sessions ADD COLUMN avgPaceSecondsPerKm INTEGER")
        }
    }

    val ALL: Array<Migration> = arrayOf(MIGRATION_7_8)
}
