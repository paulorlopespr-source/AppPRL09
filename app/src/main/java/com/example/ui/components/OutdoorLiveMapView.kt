package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CardioType
import com.example.data.model.GpsPoint
import com.example.data.model.KmSplit
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun OutdoorLiveMapView(
    routePoints: List<GpsPoint>,
    currentLocation: GpsPoint?,
    splits: List<KmSplit> = emptyList(),
    cardioType: CardioType = CardioType.CORRIDA,
    isPaused: Boolean = false,
    speedKmh: Double = 0.0,
    paceMinKm: String = "--:--",
    elevationGainMeters: Double = 0.0,
    accuracyMeters: Float? = null,
    isExpanded: Boolean = false,
    onToggleExpand: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedMapType by remember {
        mutableStateOf(if (cardioType == CardioType.TRILHA) MapType.TERRAIN else MapType.NORMAL)
    }
    var isAutoFollowEnabled by remember { mutableStateOf(true) }

    // Map points to LatLng
    val latLngPoints = remember(routePoints) {
        routePoints.map { LatLng(it.latitude, it.longitude) }
    }

    // Default initial location: Brazil standard or user's current loc
    val initialTarget = remember(currentLocation, latLngPoints) {
        when {
            currentLocation != null -> LatLng(currentLocation.latitude, currentLocation.longitude)
            latLngPoints.isNotEmpty() -> latLngPoints.last()
            else -> LatLng(-23.5505, -46.6333) // São Paulo fallback
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialTarget, 16.5f)
    }

    // Auto-follow user's location when new coordinates arrive
    LaunchedEffect(currentLocation, isAutoFollowEnabled) {
        if (isAutoFollowEnabled && currentLocation != null) {
            val target = LatLng(currentLocation.latitude, currentLocation.longitude)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLng(target),
                durationMs = 600
            )
        }
    }

    // Route polyline color based on outdoor activity type
    val routeColor = remember(cardioType) {
        when (cardioType) {
            CardioType.CORRIDA -> Color(0xFF00E5FF) // Vivid Cyan
            CardioType.CAMINHADA_AR_LIVRE -> Color(0xFF10B981) // Emerald
            CardioType.TRILHA -> Color(0xFFFF9100) // Trail Vivid Orange
            else -> Color(0xFFA855F7) // Purple
        }
    }

    // Pulsing animation for current live position marker
    val infiniteTransition = rememberInfiniteTransition(label = "markerPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.2.dp, if (isPaused) AmberWarning.copy(alpha = 0.5f) else LilacAccent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .background(PurpleDeepCard)
            .testTag("outdoor_live_map_container")
    ) {
        // Google Map Composable
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = remember(selectedMapType) {
                MapProperties(
                    mapType = selectedMapType,
                    isMyLocationEnabled = false // Custom marker used for custom neon styling
                )
            },
            uiSettings = remember {
                MapUiSettings(
                    zoomControlsEnabled = false,
                    compassEnabled = true,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled = false
                )
            },
            onMapClick = {
                // User interacted with map; temporarily pause auto-follow
                isAutoFollowEnabled = false
            }
        ) {
            // Polyline representing the tracked route
            if (latLngPoints.size >= 2) {
                Polyline(
                    points = latLngPoints,
                    color = routeColor,
                    width = 14f,
                    geodesic = true,
                    startCap = RoundCap(),
                    endCap = RoundCap(),
                    jointType = JointType.ROUND
                )
            }

            // Start Position Marker
            if (latLngPoints.isNotEmpty()) {
                val startPos = latLngPoints.first()
                val startMarkerState = rememberMarkerState(position = startPos)
                MarkerComposable(
                    state = startMarkerState,
                    title = "Ponto de Partida"
                ) {
                    Surface(
                        shape = CircleShape,
                        color = EmeraldSuccess,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                        shadowElevation = 6.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = "Início",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Kilometer Split Markers along the route
            splits.forEach { split ->
                // Approximate position for split: roughly at index (split.kmIndex * 1000m approx)
                val splitRatio = (split.kmIndex.toDouble() / (splits.size.coerceAtLeast(1))).coerceIn(0.0, 1.0)
                val splitIndex = ((latLngPoints.size - 1) * splitRatio).toInt().coerceIn(0, (latLngPoints.size - 1).coerceAtLeast(0))
                if (latLngPoints.isNotEmpty() && splitIndex < latLngPoints.size) {
                    val splitPos = latLngPoints[splitIndex]
                    val splitMarkerState = rememberMarkerState(position = splitPos)
                    MarkerComposable(
                        state = splitMarkerState,
                        title = "Km ${split.kmIndex} - Ritmo: ${split.avgPaceMinKm}"
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PurpleDarkest,
                            border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent),
                            shadowElevation = 4.dp
                        ) {
                            Text(
                                text = "${split.kmIndex}k",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Current Live Position Marker
            val currentPos = currentLocation?.let { LatLng(it.latitude, it.longitude) }
                ?: latLngPoints.lastOrNull()

            if (currentPos != null) {
                val currentMarkerState = rememberMarkerState(position = currentPos)
                currentMarkerState.position = currentPos

                MarkerComposable(
                    state = currentMarkerState,
                    title = "Você está aqui"
                ) {
                    Box(
                        modifier = Modifier.size(54.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer Pulsing Ring
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .scale(if (isPaused) 1f else pulseScale)
                                .clip(CircleShape)
                                .background(
                                    if (isPaused) AmberWarning.copy(alpha = 0.25f)
                                    else routeColor.copy(alpha = 0.35f)
                                )
                        )

                        // Inner Solid Dot
                        Surface(
                            shape = CircleShape,
                            color = if (isPaused) AmberWarning else routeColor,
                            border = androidx.compose.foundation.BorderStroke(2.5.dp, Color.White),
                            shadowElevation = 8.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (cardioType) {
                                        CardioType.TRILHA -> Icons.Default.Landscape
                                        CardioType.CAMINHADA_AR_LIVRE -> Icons.Default.Park
                                        else -> Icons.Default.DirectionsRun
                                    },
                                    contentDescription = "Posição Atual",
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // FLOATING TOP HUD (Status & Sport Indicator)
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Activity Pill Badge
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = PurpleDarkest.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isPaused) AmberWarning else EmeraldSuccess)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (cardioType) {
                            CardioType.TRILHA -> "TRILHA • GPS ATIVO"
                            CardioType.CAMINHADA_AR_LIVRE -> "CAMINHADA • GPS ATIVO"
                            else -> "CORRIDA • GPS ATIVO"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Controls on top-right: Map Type toggle & Fullscreen expand
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Map Type Selector (Normal / Satélite / Relevo Terreno)
                Surface(
                    shape = CircleShape,
                    color = PurpleDarkest.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier
                        .size(38.dp)
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
                            contentDescription = "Mudar camada do mapa",
                            tint = LilacAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Expand / Collapse Fullscreen Button
                if (onToggleExpand != null) {
                    Surface(
                        shape = CircleShape,
                        color = PurpleDarkest.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier
                            .size(38.dp)
                            .clickable { onToggleExpand() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = if (isExpanded) "Reduzir mapa" else "Expandir mapa",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // FLOATING BOTTOM OVERLAY (HUD Metrics + Recenter FAB)
        // ==========================================
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Re-center Auto-Follow FAB Button
            AnimatedVisibility(
                visible = !isAutoFollowEnabled,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.End)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = EmeraldDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .clickable {
                            isAutoFollowEnabled = true
                            currentLocation?.let { loc ->
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        update = CameraUpdateFactory.newLatLngZoom(
                                            LatLng(loc.latitude, loc.longitude),
                                            16.5f
                                        ),
                                        durationMs = 500
                                    )
                                }
                            }
                        }
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Centralizar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Bottom Live Metrics Overlay Bar
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PurpleDarkest.copy(alpha = 0.9f),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, GlassBorderSubtle),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Metric 1: Pace
                    Column {
                        Text("RITMO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(
                            text = paceMinKm,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = LilacAccent
                        )
                    }

                    // Divider
                    Box(modifier = Modifier.size(width = 1.dp, height = 24.dp).background(GlassBorder))

                    // Metric 2: Speed
                    Column {
                        Text("VELOCIDADE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(
                            text = String.format(Locale.US, "%.1f km/h", speedKmh),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberWarning
                        )
                    }

                    // Divider
                    Box(modifier = Modifier.size(width = 1.dp, height = 24.dp).background(GlassBorder))

                    // Metric 3: Desnível / Elevação
                    Column {
                        Text("ELEVAÇÃO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(
                            text = String.format(Locale.US, "+%.0fm", elevationGainMeters),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = EmeraldSuccess
                        )
                    }

                    // GPS Accuracy Pill
                    if (accuracyMeters != null) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldSubtle,
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, EmeraldSuccess)
                        ) {
                            Text(
                                text = "±${accuracyMeters.toInt()}m",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Waiting for GPS message if no points yet
        if (latLngPoints.isEmpty() && currentLocation == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PurpleDarkest.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = null,
                        tint = LilacAccent,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Conectando ao sinal GPS...",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Vá para um local a céu aberto para iniciar o trajeto.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}
