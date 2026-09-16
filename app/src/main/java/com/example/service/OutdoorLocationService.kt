package com.example.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.CardioType
import com.example.data.model.GpsPoint
import com.example.data.model.IntensityLevel
import com.example.data.model.KmSplit
import com.example.data.model.OutdoorSessionState
import com.example.data.model.OutdoorTrackingStatus
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class OutdoorLocationService : Service() {

    private val binder = OutdoorLocationBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var tickerJob: Job? = null

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var systemLocationManager: LocationManager? = null
    private var systemLocationListener: LocationListener? = null

    private val _sessionState = MutableStateFlow(OutdoorSessionState())
    val sessionState: StateFlow<OutdoorSessionState> = _sessionState.asStateFlow()

    private var lastValidLocation: Location? = null
    private var accumulatedDistanceMeters: Double = 0.0
    private var accumulatedElevationGainMeters: Double = 0.0
    private var lastAltitude: Double? = null

    // Splits tracking
    private var nextKmSplitIndex = 1
    private var lastSplitDurationSeconds = 0

    inner class OutdoorLocationBinder : Binder() {
        val service: OutdoorLocationService get() = this@OutdoorLocationService
        val sessionState: StateFlow<OutdoorSessionState> get() = this@OutdoorLocationService.sessionState

        fun start(
            type: CardioType,
            intensity: IntensityLevel,
            locationName: String,
            targetMinutes: Int?
        ) {
            this@OutdoorLocationService.startTracking(type, intensity, locationName, targetMinutes)
        }

        fun pause() = this@OutdoorLocationService.pauseTracking()
        fun resume() = this@OutdoorLocationService.resumeTracking()
        fun stop(): OutdoorSessionState = this@OutdoorLocationService.stopTracking()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        } catch (e: Exception) {
            Log.w(TAG, "FusedLocationProviderClient init error: ${e.message}")
        }
        systemLocationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val typeName = intent.getStringExtra(EXTRA_CARDIO_TYPE)
                val intensityName = intent.getStringExtra(EXTRA_INTENSITY)
                val locName = intent.getStringExtra(EXTRA_LOCATION_NAME) ?: "Corrida ao Ar Livre"
                val targetMin = if (intent.hasExtra(EXTRA_TARGET_MINUTES)) intent.getIntExtra(EXTRA_TARGET_MINUTES, 30) else null

                val cardioType = try {
                    CardioType.valueOf(typeName ?: CardioType.CORRIDA.name)
                } catch (_: Exception) {
                    CardioType.CORRIDA
                }

                val intensity = try {
                    IntensityLevel.valueOf(intensityName ?: IntensityLevel.MODERADA.name)
                } catch (_: Exception) {
                    IntensityLevel.MODERADA
                }

                startTracking(cardioType, intensity, locName, targetMin)
            }
            ACTION_PAUSE -> {
                pauseTracking()
            }
            ACTION_RESUME -> {
                resumeTracking()
            }
            ACTION_STOP -> {
                stopTracking()
            }
        }
        return START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    fun startTracking(
        type: CardioType = CardioType.CORRIDA,
        intensity: IntensityLevel = IntensityLevel.MODERADA,
        locationName: String = "Corrida ao Ar Livre",
        targetMinutes: Int? = null
    ) {
        if (_sessionState.value.status == OutdoorTrackingStatus.RUNNING) {
            return // Already running
        }

        // Reset state
        accumulatedDistanceMeters = 0.0
        accumulatedElevationGainMeters = 0.0
        lastValidLocation = null
        lastAltitude = null
        nextKmSplitIndex = 1
        lastSplitDurationSeconds = 0

        _sessionState.value = OutdoorSessionState(
            status = OutdoorTrackingStatus.RUNNING,
            durationSeconds = 0L,
            distanceKm = 0.0,
            cardioType = type,
            intensity = intensity,
            locationName = locationName,
            targetMinutes = targetMinutes,
            splits = emptyList(),
            routePoints = emptyList()
        )

        // Start Foreground Service with Notification
        startAsForeground()

        // Start Location Updates
        startLocationUpdates()

        // Start 1-second ticker
        startTimerTicker()
    }

    fun pauseTracking() {
        if (_sessionState.value.status != OutdoorTrackingStatus.RUNNING) return
        tickerJob?.cancel()
        _sessionState.value = _sessionState.value.copy(
            status = OutdoorTrackingStatus.PAUSED,
            currentSpeedKmh = 0.0,
            currentPaceMinKm = "--:--"
        )
        updateNotification()
    }

    fun resumeTracking() {
        if (_sessionState.value.status != OutdoorTrackingStatus.PAUSED) return
        _sessionState.value = _sessionState.value.copy(
            status = OutdoorTrackingStatus.RUNNING
        )
        startTimerTicker()
        updateNotification()
    }

    fun stopTracking(): OutdoorSessionState {
        tickerJob?.cancel()
        stopLocationUpdates()

        val finalState = _sessionState.value.copy(
            status = OutdoorTrackingStatus.STOPPED,
            currentSpeedKmh = 0.0,
            currentPaceMinKm = "--:--"
        )
        _sessionState.value = finalState

        try {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping foreground: ${e.message}")
        }
        stopSelf()
        return finalState
    }

    private fun startAsForeground() {
        val notification = buildNotification()
        val foregroundType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        } else {
            0
        }

        try {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                foregroundType
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to startForeground: ${e.message}")
        }
    }

    private fun startTimerTicker() {
        tickerJob?.cancel()
        tickerJob = serviceScope.launch {
            while (isActive) {
                delay(1000L)
                if (_sessionState.value.status == OutdoorTrackingStatus.RUNNING) {
                    val current = _sessionState.value
                    val newDuration = current.durationSeconds + 1
                    val distKm = accumulatedDistanceMeters / 1000.0

                    val avgSpeedKmh = if (newDuration > 0 && distKm > 0.0) {
                        distKm / (newDuration / 3600.0)
                    } else {
                        0.0
                    }

                    val avgPace = formatPace(avgSpeedKmh)
                    val met = when (current.intensity) {
                        IntensityLevel.LEVE -> current.cardioType.metLight
                        IntensityLevel.MODERADA -> current.cardioType.metModerate
                        IntensityLevel.INTENSA -> current.cardioType.metIntense
                    }
                    val userWeightKg = 75.0 // Base average athlete weight
                    val calories = ((met * 3.5 * userWeightKg / 200.0) * (newDuration / 60.0)).roundToInt()

                    _sessionState.value = current.copy(
                        durationSeconds = newDuration,
                        distanceKm = distKm,
                        avgSpeedKmh = avgSpeedKmh,
                        avgPaceMinKm = avgPace,
                        caloriesBurned = calories,
                        elevationGainMeters = accumulatedElevationGainMeters
                    )

                    // Update notification periodically
                    if (newDuration % 2L == 0L) {
                        updateNotification()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                2000L
            ).setMinUpdateIntervalMillis(1000L)
                .setMinUpdateDistanceMeters(2.0f)
                .build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    for (location in result.locations) {
                        onNewLocation(location)
                    }
                }
            }

            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            Log.w(TAG, "Fused location updates failed, using fallback: ${e.message}")
            startSystemLocationFallback()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startSystemLocationFallback() {
        try {
            systemLocationListener = LocationListener { location ->
                onNewLocation(location)
            }
            systemLocationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000L,
                2.0f,
                systemLocationListener!!,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Fallback location updates error: ${e.message}")
        }
    }

    private fun stopLocationUpdates() {
        locationCallback?.let {
            try {
                fusedLocationClient?.removeLocationUpdates(it)
            } catch (_: Exception) {}
            locationCallback = null
        }
        systemLocationListener?.let {
            try {
                systemLocationManager?.removeUpdates(it)
            } catch (_: Exception) {}
            systemLocationListener = null
        }
    }

    private fun onNewLocation(location: Location) {
        if (_sessionState.value.status != OutdoorTrackingStatus.RUNNING) return
        if (location.accuracy > 40.0f) return // Discard inaccurate fixes

        val lastLoc = lastValidLocation
        if (lastLoc != null) {
            val distance = lastLoc.distanceTo(location)
            // Sanity check: discard teleportation artifacts (> 60 m in 2 sec ~ 108 km/h)
            if (distance in 1.5..60.0) {
                accumulatedDistanceMeters += distance

                // Check altitude for elevation gain
                if (location.hasAltitude()) {
                    val prevAlt = lastAltitude
                    if (prevAlt != null) {
                        val deltaAlt = location.altitude - prevAlt
                        if (deltaAlt > 0.5 && deltaAlt < 50.0) {
                            accumulatedElevationGainMeters += deltaAlt
                        }
                    }
                    lastAltitude = location.altitude
                }
            }
        } else {
            if (location.hasAltitude()) {
                lastAltitude = location.altitude
            }
        }

        lastValidLocation = location

        val speedKmh = if (location.hasSpeed()) {
            (location.speed * 3.6).coerceAtLeast(0.0)
        } else {
            _sessionState.value.currentSpeedKmh
        }

        val gpsPoint = GpsPoint(
            latitude = location.latitude,
            longitude = location.longitude,
            altitudeMeters = if (location.hasAltitude()) location.altitude else null,
            timestampMillis = location.time,
            speedKmh = speedKmh,
            accuracyMeters = if (location.hasAccuracy()) location.accuracy else null
        )

        val updatedPoints = _sessionState.value.routePoints + gpsPoint
        val currentDistKm = accumulatedDistanceMeters / 1000.0

        // Check for km splits
        checkKmSplits(currentDistKm)

        _sessionState.value = _sessionState.value.copy(
            distanceKm = currentDistKm,
            currentSpeedKmh = speedKmh,
            currentPaceMinKm = formatPace(speedKmh),
            accuracyMeters = if (location.hasAccuracy()) location.accuracy else null,
            currentLocation = gpsPoint,
            routePoints = updatedPoints,
            elevationGainMeters = accumulatedElevationGainMeters
        )
    }

    private fun checkKmSplits(currentDistKm: Double) {
        if (currentDistKm >= nextKmSplitIndex) {
            val currentElapsed = _sessionState.value.durationSeconds.toInt()
            val splitDuration = (currentElapsed - lastSplitDurationSeconds).coerceAtLeast(1)
            val splitSpeed = 1.0 / (splitDuration / 3600.0)
            val splitPace = formatPace(splitSpeed)

            val split = KmSplit(
                kmIndex = nextKmSplitIndex,
                splitDurationSeconds = splitDuration,
                totalElapsedSeconds = currentElapsed,
                avgSpeedKmh = splitSpeed,
                avgPaceMinKm = splitPace,
                elevationGainMeters = accumulatedElevationGainMeters
            )

            val newSplits = _sessionState.value.splits + split
            _sessionState.value = _sessionState.value.copy(splits = newSplits)

            lastSplitDurationSeconds = currentElapsed
            nextKmSplitIndex++
        }
    }

    private fun formatPace(speedKmh: Double): String {
        if (speedKmh <= 0.8) return "--:--"
        val paceSecPerKm = (3600.0 / speedKmh).roundToInt()
        if (paceSecPerKm > 1800) return "--:--" // slower than 30 min/km
        val min = paceSecPerKm / 60
        val sec = paceSecPerKm % 60
        return String.format("%d:%02d", min, sec)
    }

    private fun formatDuration(totalSeconds: Long): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    private fun buildNotification(): Notification {
        val state = _sessionState.value
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseIntent = Intent(this, OutdoorLocationService::class.java).apply {
            action = ACTION_PAUSE
        }
        val pausePendingIntent = PendingIntent.getService(
            this,
            1,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val resumeIntent = Intent(this, OutdoorLocationService::class.java).apply {
            action = ACTION_RESUME
        }
        val resumePendingIntent = PendingIntent.getService(
            this,
            2,
            resumeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, OutdoorLocationService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            3,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (state.status) {
            OutdoorTrackingStatus.PAUSED -> "${state.cardioType.title} (Pausado)"
            OutdoorTrackingStatus.RUNNING -> "${state.cardioType.title} em Andamento"
            OutdoorTrackingStatus.STOPPED -> "${state.cardioType.title} Finalizado"
        }

        val content = String.format(
            "%.2f km • %s • %s/km • %d kcal",
            state.distanceKm,
            formatDuration(state.durationSeconds),
            state.avgPaceMinKm,
            state.caloriesBurned
        )

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(state.status != OutdoorTrackingStatus.STOPPED)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)

        if (state.status == OutdoorTrackingStatus.RUNNING) {
            builder.addAction(
                android.R.drawable.ic_media_pause,
                "Pausar",
                pausePendingIntent
            )
        } else if (state.status == OutdoorTrackingStatus.PAUSED) {
            builder.addAction(
                android.R.drawable.ic_media_play,
                "Retomar",
                resumePendingIntent
            )
        }

        builder.addAction(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Finalizar",
            stopPendingIntent
        )

        return builder.build()
    }

    private fun updateNotification() {
        try {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.notify(NOTIFICATION_ID, buildNotification())
        } catch (_: Exception) {}
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Monitoramento de Treino Outdoor",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificação ativa durante o monitoramento de corrida e cardio outdoor com GPS."
                setShowBadge(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tickerJob?.cancel()
        stopLocationUpdates()
    }

    companion object {
        const val TAG = "OutdoorLocationService"
        const val NOTIFICATION_CHANNEL_ID = "outdoor_cardio_tracking"
        const val NOTIFICATION_ID = 2002

        const val ACTION_START = "com.example.service.action.START"
        const val ACTION_PAUSE = "com.example.service.action.PAUSE"
        const val ACTION_RESUME = "com.example.service.action.RESUME"
        const val ACTION_STOP = "com.example.service.action.STOP"

        const val EXTRA_CARDIO_TYPE = "extra_cardio_type"
        const val EXTRA_INTENSITY = "extra_intensity"
        const val EXTRA_LOCATION_NAME = "extra_location_name"
        const val EXTRA_TARGET_MINUTES = "extra_target_minutes"

        fun startServiceIntent(
            context: Context,
            type: CardioType,
            intensity: IntensityLevel,
            locationName: String,
            targetMinutes: Int? = null
        ): Intent {
            return Intent(context, OutdoorLocationService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_CARDIO_TYPE, type.name)
                putExtra(EXTRA_INTENSITY, intensity.name)
                putExtra(EXTRA_LOCATION_NAME, locationName)
                targetMinutes?.let { putExtra(EXTRA_TARGET_MINUTES, it) }
            }
        }
    }
}
