package com.example.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

data class WorkoutLocationCoordinates(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val locality: String? = null,
    val formattedAddress: String? = null
) {
    fun getDisplayCoordinates(): String {
        return String.format(Locale.US, "%.5f, %.5f", latitude, longitude)
    }

    fun getFullLocationDescription(fallbackGymName: String = "Academia"): String {
        return when {
            !formattedAddress.isNullOrBlank() -> "$fallbackGymName ($formattedAddress)"
            !locality.isNullOrBlank() -> "$fallbackGymName ($locality - ${getDisplayCoordinates()})"
            else -> "$fallbackGymName (${getDisplayCoordinates()})"
        }
    }
}

object LocationHelper {

    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun hasLocationPermission(context: Context): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): WorkoutLocationCoordinates? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission(context)) {
            return@withContext null
        }

        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val cancellationTokenSource = CancellationTokenSource()

            val location: Location? = suspendCancellableCoroutine<Location?> { continuation ->
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { loc ->
                    if (loc != null) {
                        if (continuation.isActive) continuation.resume(loc)
                    } else {
                        // Fallback to last known location if immediate fix is unavailable
                        fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                            if (continuation.isActive) continuation.resume(lastLoc)
                        }.addOnFailureListener {
                            if (continuation.isActive) continuation.resume(null)
                        }
                    }
                }.addOnFailureListener {
                    if (continuation.isActive) continuation.resume(null)
                }

                continuation.invokeOnCancellation {
                    cancellationTokenSource.cancel()
                }
            }

            if (location == null) return@withContext null

            val geocodeResult = resolveAddress(context, location.latitude, location.longitude)

            WorkoutLocationCoordinates(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracy = location.accuracy,
                locality = geocodeResult?.locality ?: geocodeResult?.subAdminArea,
                formattedAddress = geocodeResult?.getAddressLine(0)
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun resolveAddress(context: Context, latitude: Double, longitude: Double): Address? {
        return try {
            val geocoder = Geocoder(context, Locale("pt", "BR"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Synchronous fallback for simple resolution in IO dispatcher
                @Suppress("DEPRECATION")
                val list = geocoder.getFromLocation(latitude, longitude, 1)
                list?.firstOrNull()
            } else {
                @Suppress("DEPRECATION")
                val list = geocoder.getFromLocation(latitude, longitude, 1)
                list?.firstOrNull()
            }
        } catch (e: Exception) {
            null
        }
    }
}
