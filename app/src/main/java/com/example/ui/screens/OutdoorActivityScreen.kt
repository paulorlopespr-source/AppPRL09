package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.CardioType
import com.example.ui.viewmodel.CardioViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.Locale

@Composable
fun OutdoorActivityScreen(viewModel: CardioViewModel, initialType: CardioType, onFinished: () -> Unit) {
    val route by viewModel.liveRoute.collectAsState()
    val summary by viewModel.liveSummary.collectAsState()
    val activeType by viewModel.activeType.collectAsState()
    val paused by viewModel.paused.collectAsState()

    LaunchedEffect(initialType) { if (activeType == null) viewModel.startOutdoor(initialType) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(activeType?.title ?: initialType.title, style = MaterialTheme.typography.headlineSmall)
        Card(Modifier.fillMaxWidth().height(280.dp)) {
            if (route.isNotEmpty()) {
                val last = route.last(); val camera = rememberCameraPositionState()
                LaunchedEffect(last) { camera.position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(LatLng(last.latitude, last.longitude), 16f) }
                GoogleMap(Modifier.fillMaxSize(), cameraPositionState = camera) {
                    if (route.size > 1) Polyline(points = route.map { LatLng(it.latitude, it.longitude) })
                    Marker(state = MarkerState(LatLng(last.latitude, last.longitude)), title = "Você")
                }
            } else Box(Modifier.fillMaxSize().padding(24.dp)) { Text("Aguardando sinal GPS…") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Metric("Distância", "%.2f km".format(Locale.US, summary.distanceKm))
            Metric("Movimento", formatTime(summary.movingSeconds))
            Metric("Pace", summary.averagePaceSecondsPerKm?.let { formatPace(it) } ?: "--")
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Metric("Velocidade", "%.1f km/h".format(Locale.US, summary.averageSpeedKmh))
            Metric("Elevação +", "%.0f m".format(Locale.US, summary.elevationGainMeters))
            Metric("GPS", if (route.isEmpty()) "buscando" else "ativo")
        }
        Text("Splits", style = MaterialTheme.typography.titleMedium)
        LazyColumn(Modifier.weight(1f)) { items(summary.splits) { split -> ListItem(headlineContent = { Text("Km ${split.kilometer}") }, trailingContent = { Text(formatPace(split.paceSecondsPerKm)) }) } }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { if (paused) viewModel.resumeOutdoor() else viewModel.pauseOutdoor() }, modifier = Modifier.weight(1f)) { Text(if (paused) "Continuar" else "Pausar") }
            Button(onClick = { viewModel.finishOutdoor(); onFinished() }, modifier = Modifier.weight(1f)) { Text("Finalizar") }
        }
    }
}

@Composable private fun Metric(label: String, value: String) { Column { Text(value, style = MaterialTheme.typography.titleMedium); Text(label, style = MaterialTheme.typography.labelSmall) } }
private fun formatTime(seconds: Int) = "%02d:%02d:%02d".format(seconds / 3600, (seconds % 3600) / 60, seconds % 60)
private fun formatPace(seconds: Int) = "%d:%02d/km".format(seconds / 60, seconds % 60)
