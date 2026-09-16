package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CardioType
import com.example.data.model.GpsPoint
import com.example.data.model.KmSplit
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.RedDestructive
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun OutdoorRouteMapView(
    routePoints: List<GpsPoint>,
    cardioType: CardioType = CardioType.CORRIDA,
    splits: List<KmSplit> = emptyList(),
    modifier: Modifier = Modifier
) {
    var selectedMapType by remember {
        mutableStateOf(if (cardioType == CardioType.TRILHA) MapType.TERRAIN else MapType.NORMAL)
    }

    val latLngPoints = remember(routePoints) {
        routePoints.map { LatLng(it.latitude, it.longitude) }
    }

    val initialPos = remember(latLngPoints) {
        latLngPoints.firstOrNull() ?: LatLng(-23.5505, -46.6333)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPos, 15f)
    }

    // Fit camera to full route bounds once loaded
    LaunchedEffect(latLngPoints) {
        if (latLngPoints.size >= 2) {
            try {
                val boundsBuilder = LatLngBounds.builder()
                latLngPoints.forEach { boundsBuilder.include(it) }
                val bounds = boundsBuilder.build()
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngBounds(bounds, 60),
                    durationMs = 800
                )
            } catch (_: Exception) {}
        }
    }

    val routeColor = remember(cardioType) {
        when (cardioType) {
            CardioType.CORRIDA -> Color(0xFF00E5FF)
            CardioType.CAMINHADA_AR_LIVRE -> Color(0xFF10B981)
            CardioType.TRILHA -> Color(0xFFFF9100)
            else -> Color(0xFFA855F7)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .background(PurpleDeepCard)
            .testTag("outdoor_static_route_map")
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = remember(selectedMapType) {
                MapProperties(mapType = selectedMapType)
            },
            uiSettings = remember {
                MapUiSettings(
                    zoomControlsEnabled = false,
                    compassEnabled = true,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled = false
                )
            }
        ) {
            if (latLngPoints.size >= 2) {
                Polyline(
                    points = latLngPoints,
                    color = routeColor,
                    width = 12f,
                    geodesic = true,
                    startCap = RoundCap(),
                    endCap = RoundCap(),
                    jointType = JointType.ROUND
                )
            }

            // Start Marker
            if (latLngPoints.isNotEmpty()) {
                val startPos = latLngPoints.first()
                val startMarkerState = rememberMarkerState(position = startPos)
                MarkerComposable(state = startMarkerState, title = "Largada") {
                    Surface(
                        shape = CircleShape,
                        color = EmeraldSuccess,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                        shadowElevation = 4.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = "Largada",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Finish Marker
            if (latLngPoints.size >= 2) {
                val endPos = latLngPoints.last()
                val endMarkerState = rememberMarkerState(position = endPos)
                MarkerComposable(state = endMarkerState, title = "Chegada") {
                    Surface(
                        shape = CircleShape,
                        color = RedDestructive,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                        shadowElevation = 4.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Chegada",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Layer Switcher overlay
        Surface(
            shape = CircleShape,
            color = PurpleDarkest.copy(alpha = 0.85f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(34.dp)
                .clickable {
                    selectedMapType = when (selectedMapType) {
                        MapType.NORMAL -> MapType.TERRAIN
                        MapType.TERRAIN -> MapType.HYBRID
                        else -> MapType.NORMAL
                    }
                }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = when (selectedMapType) {
                        MapType.TERRAIN -> Icons.Default.Landscape
                        MapType.HYBRID -> Icons.Default.Layers
                        else -> Icons.Default.AltRoute
                    },
                    contentDescription = "Mudar camada",
                    tint = LilacAccent,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
