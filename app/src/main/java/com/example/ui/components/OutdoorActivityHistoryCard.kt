package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.CardioSession
import com.example.domain.cardio.OutdoorCardioPersistence
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.Locale

/** Resumo consultável de uma atividade outdoor salva, incluindo rota, splits e altimetria. */
@Composable
fun OutdoorActivityHistoryCard(session: CardioSession, modifier: Modifier = Modifier) {
    val route = remember(session.routeJson) { OutdoorCardioPersistence.decodeRoute(session.routeJson) }
    val splits = remember(session.splitsJson) { OutdoorCardioPersistence.decodeSplits(session.splitsJson) }
    val camera = rememberCameraPositionState()

    ElevatedCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(session.type.title, style = MaterialTheme.typography.titleMedium)
            Text("${fmt(session.distanceKm)} km • ${session.durationMinutes} min • movimento ${formatSeconds(session.movingTimeSeconds)}")
            Text("Pace ${formatPace(session.avgPaceSecondsPerKm)} • ${session.avgSpeedKmh?.let { String.format(Locale.US, "%.1f km/h", it) } ?: "-- km/h"}")
            Text("Elevação +${session.elevationGainMeters.toInt()} m • mín ${session.minElevationMeters?.toInt() ?: 0} m • máx ${session.maxElevationMeters?.toInt() ?: 0} m")

            if (route.isNotEmpty()) {
                GoogleMap(modifier = Modifier.fillMaxWidth().height(220.dp), cameraPositionState = camera) {
                    Polyline(points = route.map { LatLng(it.latitude, it.longitude) })
                    Marker(state = MarkerState(LatLng(route.first().latitude, route.first().longitude)), title = "Início")
                    if (route.size > 1) Marker(state = MarkerState(LatLng(route.last().latitude, route.last().longitude)), title = "Fim")
                }
                LaunchedEffect(route) { camera.position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(LatLng(route.first().latitude, route.first().longitude), 15f) }
            }
            if (splits.isNotEmpty()) {
                HorizontalDivider()
                Text("Splits", style = MaterialTheme.typography.titleSmall)
                splits.forEach { split -> Text("Km ${split.kilometer}: ${formatPace(split.paceSecondsPerKm)}/km") }
            }
        }
    }
}

private fun fmt(value: Double?): String = value?.let { String.format(Locale.US, "%.2f", it) } ?: "--"
private fun formatPace(seconds: Int?): String = seconds?.let { "%d:%02d".format(it / 60, it % 60) } ?: "--:--"
private fun formatSeconds(seconds: Int): String = "%d:%02d".format(seconds / 60, seconds % 60)
