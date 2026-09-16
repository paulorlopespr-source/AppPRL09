package com.example.service

import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.data.model.RoutePoint
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OutdoorLocationService : Service() {
    private lateinit var client: FusedLocationProviderClient
    private var paused = false

    override fun onCreate() {
        super.onCreate()
        client = LocationServices.getFusedLocationProviderClient(this)
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PAUSE -> paused = true
            ACTION_RESUME -> paused = false
            ACTION_STOP -> { stopLocation(); stopSelf(); return START_NOT_STICKY }
            else -> startLocation()
        }
        return START_STICKY
    }

    @Suppress("MissingPermission")
    private fun startLocation() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Atividade em andamento")
            .setContentText("AppPRL09 está registrando sua rota por GPS")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true).build()
        if (Build.VERSION.SDK_INT >= 29) startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        else startForeground(NOTIFICATION_ID, notification)

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3_000L)
            .setMinUpdateDistanceMeters(3f).build()
        client.requestLocationUpdates(request, callback, mainLooper)
    }

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            if (paused) return
            result.locations.forEach { location ->
                if (location.accuracy <= 50f) {
                    val point = RoutePoint(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        timestampMillis = location.time,
                        accuracyMeters = location.accuracy,
                        speedMetersPerSecond = if (location.hasSpeed()) location.speed else null,
                        altitudeMeters = if (location.hasAltitude()) location.altitude else null
                    )
                    _points.value = _points.value + point
                }
            }
        }
    }

    private fun stopLocation() { if (::client.isInitialized) client.removeLocationUpdates(callback) }
    override fun onDestroy() { stopLocation(); super.onDestroy() }
    override fun onBind(intent: Intent?): IBinder? = null

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(CHANNEL_ID, "Atividade GPS", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    companion object {
        const val ACTION_START = "com.example.cardio.START"
        const val ACTION_PAUSE = "com.example.cardio.PAUSE"
        const val ACTION_RESUME = "com.example.cardio.RESUME"
        const val ACTION_STOP = "com.example.cardio.STOP"
        private const val CHANNEL_ID = "outdoor_activity"
        private const val NOTIFICATION_ID = 903
        private val _points = MutableStateFlow<List<RoutePoint>>(emptyList())
        val points = _points.asStateFlow()
        fun clearRoute() { _points.value = emptyList() }
    }
}
