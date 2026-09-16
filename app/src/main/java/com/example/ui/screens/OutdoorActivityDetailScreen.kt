package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.util.Locale

@Composable
fun OutdoorActivityDetailScreen(session: CardioSession) {
    val moshi = Moshi.Builder().build()
    val routeAdapter = moshi.adapter<List<RoutePoint>>(Types.newParameterizedType(List::class.java, RoutePoint::class.java))
    val splitAdapter = moshi.adapter<List<ActivitySplit>>(Types.newParameterizedType(List::class.java, ActivitySplit::class.java))
    val route = runCatching { routeAdapter.fromJson(session.routeJson).orEmpty() }.getOrDefault(emptyList())
    val splits = runCatching { splitAdapter.fromJson(session.splitsJson).orEmpty() }.getOrDefault(emptyList())
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text(session.type.title, style = MaterialTheme.typography.headlineSmall) }
        if (route.isNotEmpty()) item {
            Card(Modifier.fillMaxWidth().height(300.dp)) { GoogleMap(Modifier.fillMaxSize()) { Polyline(points = route.map { LatLng(it.latitude, it.longitude) }) } }
        }
        item { Text("%.2f km • %d min • %.1f km/h".format(Locale.US, session.distanceKm ?: 0.0, session.durationMinutes, session.avgSpeedKmh ?: 0.0), style = MaterialTheme.typography.titleMedium) }
        item { Text("Tempo em movimento: ${formatDetailTime(session.movingTimeSeconds)}  |  Pace: ${session.avgPaceSecondsPerKm?.let { formatDetailPace(it) } ?: "--"}") }
        item { Text("Elevação: +%.0f m  •  mín %.0f m  •  máx %.0f m".format(Locale.US, session.elevationGainMeters, session.minElevationMeters ?: 0.0, session.maxElevationMeters ?: 0.0)) }
        item { Text("Parciais", style = MaterialTheme.typography.titleMedium) }
        items(splits) { split -> ListItem(headlineContent = { Text("Km ${split.kilometer}") }, supportingContent = { Text("Tempo acumulado ${formatDetailTime(split.elapsedSeconds)}") }, trailingContent = { Text(formatDetailPace(split.paceSecondsPerKm)) }) }
    }
}
private fun formatDetailTime(seconds: Int) = "%02d:%02d:%02d".format(seconds / 3600, (seconds % 3600) / 60, seconds % 60)
private fun formatDetailPace(seconds: Int) = "%d:%02d/km".format(seconds / 60, seconds % 60)
