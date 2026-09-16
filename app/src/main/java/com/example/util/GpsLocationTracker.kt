package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GpsPoint(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speedKmh: Double,
    val timestamp: Long
)

class GpsLocationTracker(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _accumulatedDistanceMeters = MutableStateFlow(0.0)
    val accumulatedDistanceMeters: StateFlow<Double> = _accumulatedDistanceMeters.asStateFlow()

    private val _currentSpeedKmh = MutableStateFlow(0.0)
    val currentSpeedKmh: StateFlow<Double> = _currentSpeedKmh.asStateFlow()

    private val _accuracyMeters = MutableStateFlow<Float?>(null)
    val accuracyMeters: StateFlow<Float?> = _accuracyMeters.asStateFlow()

    private val _routePoints = MutableStateFlow<List<GpsPoint>>(emptyList())
    val routePoints: StateFlow<List<GpsPoint>> = _routePoints.asStateFlow()

    private var lastLocation: Location? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            _currentLocation.value = location
            _accuracyMeters.value = if (location.hasAccuracy()) location.accuracy else null

            // Speed in km/h (location.speed is in m/s)
            val speed = if (location.hasSpeed()) (location.speed * 3.6).coerceAtLeast(0.0) else 0.0
            _currentSpeedKmh.value = speed

            val previous = lastLocation
            if (previous != null) {
                // Filter out large jumps or poor accuracy fixes
                val distance = location.distanceTo(previous)
                val isReasonableAccuracy = !location.hasAccuracy() || location.accuracy <= 50f
                if (distance > 1.0 && isReasonableAccuracy) {
                    _accumulatedDistanceMeters.value += distance
                }
            }
            lastLocation = location

            // Record route point
            val point = GpsPoint(
                latitude = location.latitude,
                longitude = location.longitude,
                altitude = if (location.hasAltitude()) location.altitude else 0.0,
                speedKmh = speed,
                timestamp = System.currentTimeMillis()
            )
            _routePoints.value = _routePoints.value + point
        }
    }

    @SuppressLint("MissingPermission")
    fun startTracking(): Boolean {
        if (!LocationHelper.hasLocationPermission(context)) {
            return false
        }

        try {
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
                .setMinUpdateIntervalMillis(1000L)
                .setMinUpdateDistanceMeters(1.5f)
                .build()

            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
            _isTracking.value = true
            return true
        } catch (e: Exception) {
            _isTracking.value = false
            return false
        }
    }

    fun stopTracking() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (_: Exception) {}
        _isTracking.value = false
        lastLocation = null
    }

    fun reset() {
        stopTracking()
        _accumulatedDistanceMeters.value = 0.0
        _currentSpeedKmh.value = 0.0
        _accuracyMeters.value = null
        _currentLocation.value = null
        _routePoints.value = emptyList()
        lastLocation = null
    }

    fun getDistanceKm(): Double {
        return _accumulatedDistanceMeters.value / 1000.0
    }
}
