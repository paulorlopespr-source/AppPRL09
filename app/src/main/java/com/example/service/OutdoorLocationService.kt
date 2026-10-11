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
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.provider.Settings
import android.os.SystemClock
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.OutdoorCardioPersistence
import com.example.data.model.ActiveCardioSessionRecord
import com.example.domain.cardio.CardioSessionMath
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.roundToInt
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class OutdoorLocationService : Service() {

    private val binder = OutdoorLocationBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var tickerJob: Job? = null

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var systemLocationManager: LocationManager? = null
    private var systemLocationListener: LocationListener? = null
    private var sensorManager: SensorManager? = null
    private var activeStepSensor: Sensor? = null
    private var stepSensorListener: SensorEventListener? = null
    private var stepSensorBaseline: Float? = null
    private var stepsAtSensorStart: Long = 0L
    private var lastCheckpointElapsedRealtime = 0L
    private var lastRoomCheckpointElapsedRealtime = 0L
    private val roomWriteMutex = Mutex()
    private val database by lazy { AppDatabase.getDatabase(applicationContext, serviceScope) }
    private val statePrefs by lazy { getSharedPreferences(STATE_PREFS, Context.MODE_PRIVATE) }
    private val stateAdapter by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(OutdoorSessionState::class.java) }

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
            targetMinutes: Int?,
            plannedSessionCode: String? = null,
            userWeightKg: Double? = null,
            enableGps: Boolean = true
        ) {
            this@OutdoorLocationService.startTracking(type, intensity, locationName, targetMinutes, plannedSessionCode, userWeightKg, enableGps)
        }

        fun pause() = this@OutdoorLocationService.pauseTracking()
        fun resume() = this@OutdoorLocationService.resumeTracking()
        fun setGpsEnabled(enabled: Boolean): Boolean = this@OutdoorLocationService.setGpsEnabled(enabled)
        fun enableStepCounting() = this@OutdoorLocationService.startStepCounting()
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
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        restoreSession()
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
                val userWeightKg = intent.getDoubleExtra(EXTRA_USER_WEIGHT_KG, 0.0).takeIf { it > 0.0 }
                val enableGps = intent.getBooleanExtra(EXTRA_ENABLE_GPS, true)

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

                val plannedCode = intent.getStringExtra(EXTRA_PLANNED_SESSION_CODE)
                startTracking(cardioType, intensity, locName, targetMin, plannedCode, userWeightKg, enableGps)
            }
            ACTION_PAUSE -> {
                pauseTracking()
            }
            ACTION_RESUME -> {
                resumeTracking()
            }
            ACTION_STOP -> {
                stopTracking()
                stopSelf()
            }
        }
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    fun startTracking(
        type: CardioType = CardioType.CORRIDA,
        intensity: IntensityLevel = IntensityLevel.MODERADA,
        locationName: String = "Corrida ao Ar Livre",
        targetMinutes: Int? = null,
        plannedSessionCode: String? = null,
        userWeightKg: Double? = null,
        enableGps: Boolean = true
    ) {
        if (_sessionState.value.isActive) {
            return // Never replace a running or paused session with a second session.
        }

        // Reset state
        accumulatedDistanceMeters = 0.0
        accumulatedElevationGainMeters = 0.0
        lastValidLocation = null
        lastAltitude = null
        nextKmSplitIndex = 1
        lastSplitDurationSeconds = 0

        val nowElapsed = SystemClock.elapsedRealtime()
        _sessionState.value = OutdoorSessionState(
            status = OutdoorTrackingStatus.RUNNING,
            durationSeconds = 0L,
            distanceKm = 0.0,
            cardioType = type,
            intensity = intensity,
            locationName = locationName,
            targetMinutes = targetMinutes,
            splits = emptyList(),
            routePoints = emptyList(),
            sessionUuid = java.util.UUID.randomUUID().toString(),
            startedAtMillis = System.currentTimeMillis(),
            accumulatedDurationSeconds = 0L,
            resumeStartedElapsedRealtime = nowElapsed,
            bootCount = currentBootCount(),
            steps = 0L,
            stepSensorAvailable = null,
            plannedSessionCode = plannedSessionCode,
            userWeightKg = userWeightKg,
            gpsEnabled = enableGps
        )

        // Start Foreground Service with Notification
        startAsForeground()

        // Start Location Updates
        if (enableGps) startLocationUpdates()
        startStepCounting()

        // Start 1-second ticker
        startTimerTicker()
        persistSession()
    }

    fun pauseTracking() {
        if (_sessionState.value.status != OutdoorTrackingStatus.RUNNING) return
        val now = SystemClock.elapsedRealtime()
        val currentDuration = calculateDurationSeconds(_sessionState.value, now)
        tickerJob?.cancel()
        if (_sessionState.value.gpsEnabled) stopLocationUpdates()
        stopStepCounting()
        _sessionState.value = _sessionState.value.copy(
            status = OutdoorTrackingStatus.PAUSED,
            durationSeconds = currentDuration,
            accumulatedDurationSeconds = currentDuration,
            resumeStartedElapsedRealtime = 0L,
            currentSpeedKmh = 0.0,
            currentPaceMinKm = "--:--"
        )
        persistSession()
        updateNotification()
    }

    fun resumeTracking() {
        if (_sessionState.value.status != OutdoorTrackingStatus.PAUSED) return
        _sessionState.value = _sessionState.value.copy(
            status = OutdoorTrackingStatus.RUNNING,
            resumeStartedElapsedRealtime = SystemClock.elapsedRealtime(),
            bootCount = currentBootCount(),
            pausedBySystem = false
        )
        if (_sessionState.value.gpsEnabled) startLocationUpdates()
        startStepCounting()
        startTimerTicker()
        persistSession()
        updateNotification()
    }

    fun setGpsEnabled(enabled: Boolean): Boolean {
        val current = _sessionState.value
        if (!current.isActive) return false
        if (current.gpsEnabled == enabled) return true
        _sessionState.value = current.copy(gpsEnabled = enabled, accuracyMeters = if (enabled) current.accuracyMeters else null)
        if (current.status == OutdoorTrackingStatus.RUNNING) {
            if (enabled) {
                startAsForeground()
                if (_sessionState.value.gpsEnabled) startLocationUpdates()
            } else stopLocationUpdates()
        }
        persistSession()
        updateNotification()
        return _sessionState.value.gpsEnabled == enabled
    }

    fun stopTracking(): OutdoorSessionState {
        val state = _sessionState.value
        val duration = if (state.status == OutdoorTrackingStatus.RUNNING) calculateDurationSeconds(state, SystemClock.elapsedRealtime()) else state.durationSeconds
        tickerJob?.cancel()
        if (_sessionState.value.gpsEnabled) stopLocationUpdates()
        stopStepCounting()

        val finalState = _sessionState.value.copy(
            status = OutdoorTrackingStatus.STOPPED,
            durationSeconds = duration,
            accumulatedDurationSeconds = duration,
            resumeStartedElapsedRealtime = 0L,
            currentSpeedKmh = 0.0,
            currentPaceMinKm = "--:--"
        )
        _sessionState.value = finalState
        persistSession()

        try {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping foreground: ${e.message}")
        }
        return finalState
    }

    private fun startAsForeground() {
        val notification = buildNotification()
        val foregroundType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (_sessionState.value.gpsEnabled) ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            else if (Build.VERSION.SDK_INT >= 34) ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH else 0
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
                    val newDuration = calculateDurationSeconds(current, SystemClock.elapsedRealtime())
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
                    val userWeightKg = current.userWeightKg
                    val calories = if (userWeightKg != null && userWeightKg > 0.0) ((met * 3.5 * userWeightKg / 200.0) * (newDuration / 60.0)).roundToInt() else 0

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
                    if (SystemClock.elapsedRealtime() - lastCheckpointElapsedRealtime >= CHECKPOINT_INTERVAL_MILLIS) persistSession()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (!_sessionState.value.gpsEnabled) return
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
        } catch (e: SecurityException) {
            _sessionState.value = _sessionState.value.copy(gpsEnabled = false, accuracyMeters = null)
            Log.w(TAG, "Location permission unavailable; continuing cardio without GPS", e)
            persistSession()
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
        } catch (e: SecurityException) {
            _sessionState.value = _sessionState.value.copy(gpsEnabled = false, accuracyMeters = null)
            Log.w(TAG, "Location permission revoked; continuing cardio without GPS", e)
            persistSession()
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

    private fun calculateDurationSeconds(state: OutdoorSessionState, nowElapsed: Long): Long =
        CardioSessionMath.elapsedSeconds(
            accumulatedSeconds = state.accumulatedDurationSeconds,
            resumedAtElapsedRealtime = state.resumeStartedElapsedRealtime,
            nowElapsedRealtime = nowElapsed,
            isRunning = state.status == OutdoorTrackingStatus.RUNNING,
            sameBoot = state.bootCount == currentBootCount()
        )

    private fun currentBootCount(): Int = try {
        Settings.Global.getInt(contentResolver, Settings.Global.BOOT_COUNT, -1)
    } catch (_: Exception) { -1 }

    private fun persistSession() {
        val state = _sessionState.value
        val now = SystemClock.elapsedRealtime()
        val duration = if (state.status == OutdoorTrackingStatus.RUNNING) calculateDurationSeconds(state, now) else state.durationSeconds
        val snapshot = state.copy(durationSeconds = duration)
        statePrefs.edit()
            .putString(KEY_ACTIVE_STATE, stateAdapter.toJson(snapshot))
            .putInt(KEY_BOOT_COUNT, currentBootCount())
            .apply()
        lastCheckpointElapsedRealtime = now
        if (now - lastRoomCheckpointElapsedRealtime >= CHECKPOINT_INTERVAL_MILLIS || state.status != OutdoorTrackingStatus.RUNNING) {
            lastRoomCheckpointElapsedRealtime = now
            val record = snapshot.toActiveRecord()
            serviceScope.launch(Dispatchers.IO) {
                roomWriteMutex.withLock {
                    if (snapshot.status != OutdoorTrackingStatus.STOPPED) runCatching {
                        val latest = database.fitnessDao().getActiveCardioSession()
                        if (latest == null || latest.sessionUuid != record.sessionUuid || latest.updatedAtMillis <= record.updatedAtMillis) {
                            database.fitnessDao().saveActiveCardioSession(record)
                        }
                    }
                        .onFailure { Log.w(TAG, "Unable to persist active cardio checkpoint in Room", it) }
                }
            }
        }
    }

    private fun restoreSession() {
        val json = statePrefs.getString(KEY_ACTIVE_STATE, null)
        val savedFromPreferences = if (!json.isNullOrBlank()) runCatching { stateAdapter.fromJson(json) }.getOrNull() else null
        val recovered = savedFromPreferences ?: runCatching {
            runBlocking(Dispatchers.IO) { database.fitnessDao().getActiveCardioSession()?.toOutdoorState() }
        }.getOrNull()
        val savedState = recovered ?: return
        if (savedState.status == OutdoorTrackingStatus.STOPPED && savedState.startedAtMillis <= 0L) return
        if (!savedState.isActive) {
            _sessionState.value = savedState
            return
        }
        val sameBoot = savedState.bootCount >= 0 && savedState.bootCount == currentBootCount()
        val nowElapsed = SystemClock.elapsedRealtime()
        val restored = if (!sameBoot && savedState.status == OutdoorTrackingStatus.RUNNING) {
            savedState.copy(
                status = OutdoorTrackingStatus.PAUSED,
                durationSeconds = savedState.durationSeconds,
                accumulatedDurationSeconds = savedState.durationSeconds,
                resumeStartedElapsedRealtime = 0L,
                pausedBySystem = true
            )
        } else if (savedState.status == OutdoorTrackingStatus.RUNNING) {
            savedState.copy(durationSeconds = calculateDurationSeconds(savedState, nowElapsed), bootCount = currentBootCount())
        } else savedState
        accumulatedDistanceMeters = restored.distanceKm.coerceAtLeast(0.0) * 1_000.0
        accumulatedElevationGainMeters = restored.elevationGainMeters.coerceAtLeast(0.0)
        lastValidLocation = restored.currentLocation?.let { point ->
            Location("FITPR09-cardio").apply {
                latitude = point.latitude
                longitude = point.longitude
                if (point.altitudeMeters != null) altitude = point.altitudeMeters
                time = point.timestampMillis
            }
        }
        lastAltitude = restored.currentLocation?.altitudeMeters
        nextKmSplitIndex = restored.distanceKm.toInt().coerceAtLeast(0) + 1
        lastSplitDurationSeconds = restored.splits.lastOrNull()?.totalElapsedSeconds ?: 0
        _sessionState.value = restored
        if (restored.status == OutdoorTrackingStatus.RUNNING) {
            startAsForeground()
            if (restored.gpsEnabled) startLocationUpdates()
            startStepCounting()
            startTimerTicker()
        }
        persistSession()
    }

    private fun startStepCounting() {
        if (_sessionState.value.status != OutdoorTrackingStatus.RUNNING) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && checkSelfPermission(android.Manifest.permission.ACTIVITY_RECOGNITION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            _sessionState.value = _sessionState.value.copy(steps = null, stepSensorAvailable = false)
            persistSession()
            return
        }
        val manager = sensorManager ?: return
        val sensor = manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            ?: manager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        if (sensor == null) {
            _sessionState.value = _sessionState.value.copy(steps = null, stepSensorAvailable = false)
            persistSession()
            return
        }
        activeStepSensor = sensor
        stepsAtSensorStart = _sessionState.value.steps ?: 0L
        stepSensorBaseline = null
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (_sessionState.value.status != OutdoorTrackingStatus.RUNNING) return
                val steps = if (sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                    (_sessionState.value.steps ?: 0L) + 1L
                } else {
                    val (sessionSteps, baseline) = CardioSessionMath.stepCounterUpdate(
                        sessionBase = stepsAtSensorStart,
                        baseline = stepSensorBaseline,
                        sensorTotal = event.values.firstOrNull() ?: stepSensorBaseline ?: 0f
                    )
                    stepSensorBaseline = baseline
                    sessionSteps
                }
                _sessionState.value = _sessionState.value.copy(steps = steps, stepSensorAvailable = true)
                persistSession()
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        stepSensorListener = listener
        try {
            manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            _sessionState.value = _sessionState.value.copy(stepSensorAvailable = true)
        } catch (_: SecurityException) {
            _sessionState.value = _sessionState.value.copy(steps = null, stepSensorAvailable = false)
        }
    }

    private fun stopStepCounting() {
        stepSensorListener?.let { sensorManager?.unregisterListener(it) }
        stepSensorListener = null
        activeStepSensor = null
        stepSensorBaseline = null
    }

    private fun OutdoorSessionState.toActiveRecord() = ActiveCardioSessionRecord(
        sessionUuid = sessionUuid,
        cardioType = cardioType.name,
        intensity = intensity.name,
        status = status.name,
        startedAtMillis = startedAtMillis,
        durationSeconds = durationSeconds,
        accumulatedDurationSeconds = accumulatedDurationSeconds,
        resumeStartedElapsedRealtime = resumeStartedElapsedRealtime,
        bootCount = bootCount,
        distanceKm = distanceKm,
        currentSpeedKmh = currentSpeedKmh,
        avgSpeedKmh = avgSpeedKmh,
        currentPaceMinKm = currentPaceMinKm,
        avgPaceMinKm = avgPaceMinKm,
        caloriesBurned = caloriesBurned,
        elevationGainMeters = elevationGainMeters,
        accuracyMeters = accuracyMeters,
        currentLocationJson = null,
        routePointsJson = OutdoorCardioPersistence.serializeRoutePoints(routePoints),
        splitsJson = OutdoorCardioPersistence.serializeSplits(splits),
        locationName = locationName,
        targetMinutes = targetMinutes,
        steps = steps,
        stepSensorAvailable = stepSensorAvailable,
        plannedSessionCode = plannedSessionCode,
        pausedBySystem = pausedBySystem,
        userWeightKg = userWeightKg,
        gpsEnabled = gpsEnabled
    )

    private fun ActiveCardioSessionRecord.toOutdoorState(): OutdoorSessionState {
        val route = OutdoorCardioPersistence.parseRoutePoints(routePointsJson)
        return OutdoorSessionState(
            status = runCatching { OutdoorTrackingStatus.valueOf(status) }.getOrDefault(OutdoorTrackingStatus.PAUSED),
            durationSeconds = durationSeconds,
            distanceKm = distanceKm,
            currentSpeedKmh = currentSpeedKmh,
            avgSpeedKmh = avgSpeedKmh,
            currentPaceMinKm = currentPaceMinKm,
            avgPaceMinKm = avgPaceMinKm,
            caloriesBurned = caloriesBurned,
            elevationGainMeters = elevationGainMeters,
            accuracyMeters = accuracyMeters,
            currentLocation = route.lastOrNull(),
            routePoints = route,
            splits = OutdoorCardioPersistence.parseSplits(splitsJson),
            cardioType = runCatching { CardioType.valueOf(cardioType) }.getOrDefault(CardioType.CORRIDA),
            intensity = runCatching { IntensityLevel.valueOf(intensity) }.getOrDefault(IntensityLevel.MODERADA),
            locationName = locationName,
            targetMinutes = targetMinutes,
            sessionUuid = sessionUuid,
            startedAtMillis = startedAtMillis,
            accumulatedDurationSeconds = accumulatedDurationSeconds,
            resumeStartedElapsedRealtime = resumeStartedElapsedRealtime,
            bootCount = bootCount,
            steps = steps,
            stepSensorAvailable = stepSensorAvailable,
            plannedSessionCode = plannedSessionCode,
            pausedBySystem = pausedBySystem,
            userWeightKg = userWeightKg,
            gpsEnabled = gpsEnabled
        )
    }

    private fun onNewLocation(location: Location) {
        if (_sessionState.value.status != OutdoorTrackingStatus.RUNNING) return
        if (location.accuracy > 40.0f) return // Discard inaccurate fixes

        val lastLoc = lastValidLocation
        if (lastLoc != null) {
            val distance = lastLoc.distanceTo(location)
            if (distance < 0.5f || distance > 60.0f) return
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
        persistSession()
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
            "%.2f km • %s • %s/km%s",
            state.distanceKm,
            formatDuration(state.durationSeconds),
            state.avgPaceMinKm,
            state.steps?.let { " • $it passos" } ?: ""
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

        builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Abrir e finalizar", openAppPendingIntent)

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
        persistSession()
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
        const val EXTRA_PLANNED_SESSION_CODE = "extra_planned_session_code"
        const val EXTRA_USER_WEIGHT_KG = "extra_user_weight_kg"
        const val EXTRA_ENABLE_GPS = "extra_enable_gps"
        const val STATE_PREFS = "outdoor_cardio_session_state"
        const val KEY_ACTIVE_STATE = "active_state_json"
        const val KEY_BOOT_COUNT = "saved_boot_count"
        private const val CHECKPOINT_INTERVAL_MILLIS = 5_000L

        fun clearPersistedSession(context: Context, sessionUuid: String) {
            val prefs = context.getSharedPreferences(STATE_PREFS, Context.MODE_PRIVATE)
            val saved = runCatching {
                Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    .adapter(OutdoorSessionState::class.java).fromJson(prefs.getString(KEY_ACTIVE_STATE, null).orEmpty())
            }.getOrNull()
            if (saved?.sessionUuid == sessionUuid) prefs.edit().remove(KEY_ACTIVE_STATE).remove(KEY_BOOT_COUNT).apply()
        }

        fun startServiceIntent(
            context: Context,
            type: CardioType,
            intensity: IntensityLevel,
            locationName: String,
            targetMinutes: Int? = null,
            plannedSessionCode: String? = null,
            userWeightKg: Double? = null,
            enableGps: Boolean = true
        ): Intent {
            return Intent(context, OutdoorLocationService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_CARDIO_TYPE, type.name)
                putExtra(EXTRA_INTENSITY, intensity.name)
                putExtra(EXTRA_LOCATION_NAME, locationName)
                targetMinutes?.let { putExtra(EXTRA_TARGET_MINUTES, it) }
                plannedSessionCode?.let { putExtra(EXTRA_PLANNED_SESSION_CODE, it) }
                userWeightKg?.let { putExtra(EXTRA_USER_WEIGHT_KG, it) }
                putExtra(EXTRA_ENABLE_GPS, enableGps)
            }
        }
    }
}
