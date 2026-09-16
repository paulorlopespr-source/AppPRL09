package com.example.domain.dashboard

/** Modelo único consumido pela Home. Pode ser preenchido pelo Health Connect ou por fallback local. */
data class HealthDashboardSnapshot(
 val stepsToday:Long=0,
 val restingHeartRateBpm:Int?=null,
 val activeCaloriesToday:Int=0,
 val exerciseMinutesToday:Int=0,
 val weightKg:Double?=null,
 val lastSyncMillis:Long?=null,
 val healthConnectAvailable:Boolean=false
)
