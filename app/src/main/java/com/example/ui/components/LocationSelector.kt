package com.example.ui.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.LocationHelper
import com.example.util.WorkoutLocationCoordinates
import kotlinx.coroutines.launch

data class GymLocationPreset(
    val name: String,
    val icon: ImageVector
)

val defaultLocationPresets = listOf(
    GymLocationPreset("Academia Smart Fit", Icons.Default.FitnessCenter),
    GymLocationPreset("Academia Bluefit", Icons.Default.FitnessCenter),
    GymLocationPreset("Academia do Bairro", Icons.Default.FitnessCenter),
    GymLocationPreset("Ao Ar Livre / Parque", Icons.Default.Park),
    GymLocationPreset("Em Casa", Icons.Default.Home),
    GymLocationPreset("Centro Esportivo", Icons.Default.SportsSoccer)
)

@Composable
fun LocationSelector(
    selectedLocation: String,
    onLocationSelected: (String) -> Unit,
    capturedCoordinates: WorkoutLocationCoordinates? = null,
    onCoordinatesCaptured: ((WorkoutLocationCoordinates?) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showCustomDialog by remember { mutableStateOf(false) }
    var customText by remember { mutableStateOf("") }
    var isFetchingGps by remember { mutableStateOf(false) }

    fun captureLiveGps() {
        isFetchingGps = true
        coroutineScope.launch {
            val coords = LocationHelper.getCurrentLocation(context)
            isFetchingGps = false
            if (coords != null) {
                onCoordinatesCaptured?.invoke(coords)
                val fullLocation = coords.getFullLocationDescription(selectedLocation.ifBlank { "Academia" })
                onLocationSelected(fullLocation)
                Toast.makeText(context, "📍 Localização capturada: ${coords.getDisplayCoordinates()}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Não foi possível obter a localização GPS no momento.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            captureLiveGps()
        } else {
            Toast.makeText(context, "Permissão de localização necessária para registrar coordenadas.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = LilacAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Local:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = selectedLocation,
                    fontSize = 12.sp,
                    color = LilacAccent,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            // GPS Capture Button
            Surface(
                onClick = {
                    if (LocationHelper.hasLocationPermission(context)) {
                        captureLiveGps()
                    } else {
                        locationPermissionLauncher.launch(LocationHelper.REQUIRED_PERMISSIONS)
                    }
                },
                enabled = !isFetchingGps,
                shape = RoundedCornerShape(10.dp),
                color = PurpleDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (capturedCoordinates != null) EmeraldSuccess.copy(alpha = 0.6f) else LilacAccent.copy(alpha = 0.3f)),
                modifier = Modifier.testTag("btn_capture_gps_location")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isFetchingGps) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = LilacAccent)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Buscando...", fontSize = 11.sp, color = LilacAccent)
                    } else {
                        Icon(
                            imageVector = if (capturedCoordinates != null) Icons.Default.GpsFixed else Icons.Default.MyLocation,
                            contentDescription = "Capturar GPS",
                            tint = if (capturedCoordinates != null) EmeraldSuccess else LilacAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (capturedCoordinates != null) "GPS Ativo" else "Usar GPS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (capturedCoordinates != null) EmeraldSuccess else LilacAccent
                        )
                    }
                }
            }
        }

        // Captured GPS coordinates badge
        if (capturedCoordinates != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PurpleDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Coordenadas: ${capturedCoordinates.getDisplayCoordinates()}${if (!capturedCoordinates.locality.isNullOrBlank()) " (${capturedCoordinates.locality})" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            defaultLocationPresets.forEach { preset ->
                val isSelected = selectedLocation.startsWith(preset.name)
                FilterChip(
                    selected = isSelected,
                    onClick = { onLocationSelected(preset.name) },
                    label = { Text(preset.name, fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = preset.icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isSelected) LilacAccent else TextSecondary
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PurpleDeepCard,
                        selectedLabelColor = LilacAccent,
                        containerColor = PurpleDarkSurface,
                        labelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("chip_location_${preset.name.replace(" ", "_")}")
                )
            }

            FilterChip(
                selected = false,
                onClick = {
                    customText = ""
                    showCustomDialog = true
                },
                label = { Text("+ Outro Local", fontSize = 11.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AddLocation,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = PurpleDarkSurface,
                    labelColor = TextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("chip_location_custom")
            )
        }
    }

    if (showCustomDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            title = { Text("Adicionar Local de Treino", fontWeight = FontWeight.Black, color = TextPrimary) },
            text = {
                OutlinedTextField(
                    value = customText,
                    onValueChange = { customText = it },
                    label = { Text("Nome da Academia / Local") },
                    placeholder = { Text("Ex: Academia Iron, Studio Cross, etc.") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = LilacAccent,
                        unfocusedBorderColor = GlassBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_custom_location")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customText.isNotBlank()) {
                            onLocationSelected(customText.trim())
                        }
                        showCustomDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_confirm_custom_location")
                ) {
                    Text("Salvar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDialog = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = PurpleDarkSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}
