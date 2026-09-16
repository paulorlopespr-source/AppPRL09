package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CardioType
import com.example.data.model.ExerciseSetEntry
import com.example.data.model.IntensityLevel
import com.example.data.model.WorkoutExercisePlan
import com.example.ui.theme.AmberSubtle
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GradientAction
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurplePrimaryDark
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class QuickWorkoutTab {
    CARDIO,
    STRENGTH
}

data class QuickStrengthPreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val durationMinutes: Int,
    val estimatedCalories: Int,
    val icon: ImageVector,
    val exercisesSummary: List<String>,
    val plansGenerator: () -> List<WorkoutExercisePlan>
)

data class QuickCardioPreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: CardioType,
    val defaultMinutes: Int,
    val estimatedCalories: Int,
    val intensity: IntensityLevel,
    val icon: ImageVector,
    val targetHeartRate: String,
    val enableGps: Boolean = false,
    val badge: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickWorkoutSheet(
    onDismiss: () -> Unit,
    onStartStrengthWorkout: (title: String, location: String, plans: List<WorkoutExercisePlan>) -> Unit,
    onStartCardio: (type: CardioType, location: String, intensity: IntensityLevel, targetMinutes: Int?, enableGps: Boolean) -> Unit,
    defaultLocation: String = "Academia Smart Fit",
    initialTab: QuickWorkoutTab = QuickWorkoutTab.CARDIO,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    var selectedLocation by remember { mutableStateOf(defaultLocation) }

    // Custom Generator states
    var customDurationMinutes by remember { mutableIntStateOf(15) }
    var customStrengthFocus by remember { mutableStateOf("Full Body Express") }
    var customCardioIntensity by remember { mutableStateOf(IntensityLevel.MODERADA) }
    var customCardioGpsEnabled by remember { mutableStateOf(true) }

    // Quick Strength Presets
    val strengthPresets = remember {
        listOf(
            QuickStrengthPreset(
                id = "full_body_20",
                title = "Full Body Express",
                subtitle = "Exercícios compostos multiarticulares de alta eficiência",
                durationMinutes = 20,
                estimatedCalories = 190,
                icon = Icons.Default.Bolt,
                exercisesSummary = listOf("Supino Reto", "Agachamento", "Remada Curvada", "Prancha Core"),
                plansGenerator = {
                    listOf(
                        WorkoutExercisePlan(
                            exerciseId = 1,
                            exerciseName = "Supino reto com barra",
                            muscleGroup = "Peito",
                            sets = listOf(
                                ExerciseSetEntry(1, 40.0, 10, restSeconds = 45),
                                ExerciseSetEntry(2, 45.0, 10, restSeconds = 45),
                                ExerciseSetEntry(3, 50.0, 8, restSeconds = 45)
                            ),
                            targetRestSeconds = 45,
                            notes = "Cadência controlada, foco na contração máxima."
                        ),
                        WorkoutExercisePlan(
                            exerciseId = 11,
                            exerciseName = "Agachamento livre com barra",
                            muscleGroup = "Pernas",
                            sets = listOf(
                                ExerciseSetEntry(1, 40.0, 10, restSeconds = 45),
                                ExerciseSetEntry(2, 50.0, 10, restSeconds = 45),
                                ExerciseSetEntry(3, 50.0, 10, restSeconds = 45)
                            ),
                            targetRestSeconds = 45,
                            notes = "Profundidade abaixo de 90 graus com tronco firme."
                        ),
                        WorkoutExercisePlan(
                            exerciseId = 5,
                            exerciseName = "Remada curvada com barra",
                            muscleGroup = "Costas",
                            sets = listOf(
                                ExerciseSetEntry(1, 35.0, 10, restSeconds = 45),
                                ExerciseSetEntry(2, 40.0, 10, restSeconds = 45),
                                ExerciseSetEntry(3, 40.0, 10, restSeconds = 45)
                            ),
                            targetRestSeconds = 45,
                            notes = "Puxe a barra em direção ao umbigo fechando as escápulas."
                        ),
                        WorkoutExercisePlan(
                            exerciseId = 22,
                            exerciseName = "Prancha abdominal isométrica",
                            muscleGroup = "Abdômen",
                            sets = listOf(
                                ExerciseSetEntry(1, 0.0, 45, restSeconds = 30),
                                ExerciseSetEntry(2, 0.0, 45, restSeconds = 30)
                            ),
                            targetRestSeconds = 30,
                            notes = "Glúteos e abdômen contraídos sem arquear a lombar."
                        )
                    )
                }
            ),
            QuickStrengthPreset(
                id = "upper_pump_15",
                title = "Superiores Pump Express",
                subtitle = "Peito, costas, ombros e braços sem pausas longas",
                durationMinutes = 15,
                estimatedCalories = 150,
                icon = Icons.Default.FitnessCenter,
                exercisesSummary = listOf("Supino Halteres", "Desenvolvimento", "Puxada / Remada", "Rosca Bíceps"),
                plansGenerator = {
                    listOf(
                        WorkoutExercisePlan(
                            exerciseId = 2,
                            exerciseName = "Supino inclinado com halteres",
                            muscleGroup = "Peito",
                            sets = listOf(
                                ExerciseSetEntry(1, 20.0, 10, restSeconds = 30),
                                ExerciseSetEntry(2, 22.0, 10, restSeconds = 30),
                                ExerciseSetEntry(3, 24.0, 8, restSeconds = 30)
                            ),
                            targetRestSeconds = 30
                        ),
                        WorkoutExercisePlan(
                            exerciseId = 6,
                            exerciseName = "Puxada alta frontal no pulley",
                            muscleGroup = "Costas",
                            sets = listOf(
                                ExerciseSetEntry(1, 45.0, 10, restSeconds = 30),
                                ExerciseSetEntry(2, 50.0, 10, restSeconds = 30),
                                ExerciseSetEntry(3, 50.0, 10, restSeconds = 30)
                            ),
                            targetRestSeconds = 30
                        ),
                        WorkoutExercisePlan(
                            exerciseId = 9,
                            exerciseName = "Desenvolvimento de ombros com halteres",
                            muscleGroup = "Ombros",
                            sets = listOf(
                                ExerciseSetEntry(1, 14.0, 10, restSeconds = 30),
                                ExerciseSetEntry(2, 16.0, 8, restSeconds = 30)
                            ),
                            targetRestSeconds = 30
                        )
                    )
                }
            ),
            QuickStrengthPreset(
                id = "legs_glutes_15",
                title = "Pernas & Glúteos Rápido",
                subtitle = "Agachamento, terra romeno e elevação pélvica",
                durationMinutes = 15,
                estimatedCalories = 165,
                icon = Icons.Default.FitnessCenter,
                exercisesSummary = listOf("Agachamento", "Levantamento Terra Romeno", "Elevação Pélvica"),
                plansGenerator = {
                    listOf(
                        WorkoutExercisePlan(
                            exerciseId = 11,
                            exerciseName = "Agachamento livre com barra",
                            muscleGroup = "Pernas",
                            sets = listOf(
                                ExerciseSetEntry(1, 40.0, 12, restSeconds = 40),
                                ExerciseSetEntry(2, 50.0, 10, restSeconds = 40),
                                ExerciseSetEntry(3, 50.0, 10, restSeconds = 40)
                            ),
                            targetRestSeconds = 40
                        ),
                        WorkoutExercisePlan(
                            exerciseId = 13,
                            exerciseName = "Levantamento terra romeno",
                            muscleGroup = "Pernas",
                            sets = listOf(
                                ExerciseSetEntry(1, 35.0, 10, restSeconds = 40),
                                ExerciseSetEntry(2, 40.0, 10, restSeconds = 40)
                            ),
                            targetRestSeconds = 40
                        ),
                        WorkoutExercisePlan(
                            exerciseId = 14,
                            exerciseName = "Elevação pélvica",
                            muscleGroup = "Glúteos",
                            sets = listOf(
                                ExerciseSetEntry(1, 30.0, 12, restSeconds = 40),
                                ExerciseSetEntry(2, 35.0, 12, restSeconds = 40)
                            ),
                            targetRestSeconds = 40
                        )
                    )
                }
            ),
            QuickStrengthPreset(
                id = "core_abs_10",
                title = "Core & Abdômen Intenso",
                subtitle = "Circuito de prancha e abdominais sem intervalo",
                durationMinutes = 10,
                estimatedCalories = 95,
                icon = Icons.Default.Bolt,
                exercisesSummary = listOf("Prancha Isométrica", "Abdominal Supra", "Elevação de Pernas"),
                plansGenerator = {
                    listOf(
                        WorkoutExercisePlan(
                            exerciseId = 22,
                            exerciseName = "Prancha abdominal isométrica",
                            muscleGroup = "Abdômen",
                            sets = listOf(
                                ExerciseSetEntry(1, 0.0, 45, restSeconds = 20),
                                ExerciseSetEntry(2, 0.0, 45, restSeconds = 20),
                                ExerciseSetEntry(3, 0.0, 45, restSeconds = 20)
                            ),
                            targetRestSeconds = 20
                        ),
                        WorkoutExercisePlan(
                            exerciseId = 23,
                            exerciseName = "Abdominal supra no solo",
                            muscleGroup = "Abdômen",
                            sets = listOf(
                                ExerciseSetEntry(1, 0.0, 20, restSeconds = 20),
                                ExerciseSetEntry(2, 0.0, 20, restSeconds = 20),
                                ExerciseSetEntry(3, 0.0, 20, restSeconds = 20)
                            ),
                            targetRestSeconds = 20
                        ),
                        WorkoutExercisePlan(
                            exerciseId = 24,
                            exerciseName = "Elevação de pernas (infra)",
                            muscleGroup = "Abdômen",
                            sets = listOf(
                                ExerciseSetEntry(1, 0.0, 15, restSeconds = 20),
                                ExerciseSetEntry(2, 0.0, 15, restSeconds = 20)
                            ),
                            targetRestSeconds = 20
                        )
                    )
                }
            )
        )
    }

    // Quick Cardio Presets - Prominently surfacing Caminhada & Corrida
    val cardioPresets = remember {
        listOf(
            // CAMINHADA (Ar livre / GPS e Esteira)
            QuickCardioPreset(
                id = "cardio_walk_outdoor_20",
                title = "🚶 Caminhada ao Ar Livre",
                subtitle = "Rastreamento GPS do percurso + cronômetro de tempo real",
                type = CardioType.CAMINHADA_AR_LIVRE,
                defaultMinutes = 20,
                estimatedCalories = 135,
                intensity = IntensityLevel.LEVE,
                icon = Icons.Default.Park,
                targetHeartRate = "110 - 130 BPM",
                enableGps = true,
                badge = "🛰️ GPS + TEMPO"
            ),
            QuickCardioPreset(
                id = "cardio_walk_treadmill_15",
                title = "🚶 Caminhada na Esteira",
                subtitle = "Esteira com inclinação na Zona 2 de queima calórica",
                type = CardioType.CAMINHADA_ESTEIRA,
                defaultMinutes = 15,
                estimatedCalories = 110,
                intensity = IntensityLevel.LEVE,
                icon = Icons.Default.DirectionsWalk,
                targetHeartRate = "115 - 135 BPM",
                enableGps = false,
                badge = "⏱️ ZONA 2"
            ),
            // CORRIDA (Rua / GPS e Esteira)
            QuickCardioPreset(
                id = "cardio_run_outdoor_20",
                title = "🏃 Corrida ao Ar Livre / Rua",
                subtitle = "GPS ativo + cálculo automático de Ritmo (Pace) e percurso",
                type = CardioType.CORRIDA,
                defaultMinutes = 20,
                estimatedCalories = 240,
                intensity = IntensityLevel.MODERADA,
                icon = Icons.Default.DirectionsRun,
                targetHeartRate = "145 - 170 BPM",
                enableGps = true,
                badge = "🛰️ GPS + PACE"
            ),
            QuickCardioPreset(
                id = "cardio_run_treadmill_15",
                title = "🏃 Corrida na Esteira Express",
                subtitle = "Treino aeróbico de alta velocidade e consumo máximo de oxigênio",
                type = CardioType.CORRIDA,
                defaultMinutes = 15,
                estimatedCalories = 180,
                intensity = IntensityLevel.INTENSA,
                icon = Icons.Default.DirectionsRun,
                targetHeartRate = "150 - 175 BPM",
                enableGps = false,
                badge = "⏱️ INTENSO"
            ),
            // OUTROS CÁRDIOS DO APP
            QuickCardioPreset(
                id = "cardio_bike_20",
                title = "🚴 Bicicleta Indoor / Spinning",
                subtitle = "Pedalada com cadência ritmada e zero impacto articular",
                type = CardioType.BICICLETA_INDOOR,
                defaultMinutes = 20,
                estimatedCalories = 195,
                intensity = IntensityLevel.MODERADA,
                icon = Icons.Default.DirectionsBike,
                targetHeartRate = "130 - 155 BPM",
                enableGps = false,
                badge = "🚴 BIKE"
            ),
            QuickCardioPreset(
                id = "cardio_sports_outdoor_45",
                title = "⚽ Futebol & Esportes ao Ar Livre",
                subtitle = "Atividade dinâmica em campo aberto com GPS e ritmo",
                type = CardioType.FUTEBOL,
                defaultMinutes = 45,
                estimatedCalories = 420,
                intensity = IntensityLevel.INTENSA,
                icon = Icons.Default.SportsSoccer,
                targetHeartRate = "140 - 170 BPM",
                enableGps = true,
                badge = "🛰️ GPS"
            )
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PurpleDarkSurface,
        contentColor = TextPrimary,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 44.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(LilacSoft.copy(alpha = 0.4f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .testTag("sheet_quick_workout")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(AmberWarning, PurplePrimary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Treino Rápido",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Caminhada, Corrida, Cárdios & Força Express",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = LilacSoft
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Selector: Cardio (Caminhada/Corrida/Bike) vs Força Express
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                color = PurpleDeepCard.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    // Tab 1: Cardio Express (Caminhada, Corrida, etc.)
                    val isCardio = selectedTab == QuickWorkoutTab.CARDIO
                    val cardioBg by animateColorAsState(
                        targetValue = if (isCardio) PurpleVibrant else Color.Transparent,
                        label = "cardioTabBg"
                    )

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedTab = QuickWorkoutTab.CARDIO }
                            .testTag("tab_quick_cardio"),
                        color = cardioBg
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = null,
                                tint = if (isCardio) Color.White else LilacSoft,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Cardio & GPS",
                                fontSize = 13.sp,
                                fontWeight = if (isCardio) FontWeight.Black else FontWeight.Medium,
                                color = if (isCardio) Color.White else LilacSoft
                            )
                        }
                    }

                    // Tab 2: Treino de Força
                    val isStrength = selectedTab == QuickWorkoutTab.STRENGTH
                    val strengthBg by animateColorAsState(
                        targetValue = if (isStrength) PurpleVibrant else Color.Transparent,
                        label = "strengthTabBg"
                    )

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedTab = QuickWorkoutTab.STRENGTH }
                            .testTag("tab_quick_strength"),
                        color = strengthBg
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = if (isStrength) Color.White else LilacSoft,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Treino de Força",
                                fontSize = 13.sp,
                                fontWeight = if (isStrength) FontWeight.Black else FontWeight.Medium,
                                color = if (isStrength) Color.White else LilacSoft
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Location Selector Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PurpleDeepCard.copy(alpha = 0.5f))
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Local:",
                    fontSize = 12.sp,
                    color = LilacSoft,
                    fontWeight = FontWeight.Medium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Parque / Rua", "Academia Smart Fit", "Casa / Home").forEach { loc ->
                        val isSel = selectedLocation == loc
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) PurplePrimary else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (isSel) LilacAccent else GlassBorderSubtle,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedLocation = loc }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = loc,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Presets List and Custom Options
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(460.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if (selectedTab == QuickWorkoutTab.CARDIO) {
                    // CARDIO PRESETS (Caminhada, Corrida, Bike, etc.)
                    items(cardioPresets, key = { it.id }) { preset ->
                        CardioPresetCard(
                            preset = preset,
                            onStart = {
                                onStartCardio(
                                    preset.type,
                                    selectedLocation,
                                    preset.intensity,
                                    preset.defaultMinutes,
                                    preset.enableGps
                                )
                                onDismiss()
                            }
                        )
                    }

                    // Quick Cardio Free Start
                    item {
                        CustomQuickCardioBox(
                            selectedIntensity = customCardioIntensity,
                            onIntensityChanged = { customCardioIntensity = it },
                            enableGps = customCardioGpsEnabled,
                            onToggleGps = { customCardioGpsEnabled = it },
                            onStartFreeCardio = {
                                onStartCardio(
                                    CardioType.CAMINHADA_AR_LIVRE,
                                    selectedLocation,
                                    customCardioIntensity,
                                    null,
                                    customCardioGpsEnabled
                                )
                                onDismiss()
                            }
                        )
                    }
                } else {
                    // STRENGTH PRESETS
                    items(strengthPresets, key = { it.id }) { preset ->
                        StrengthPresetCard(
                            preset = preset,
                            onStart = {
                                onStartStrengthWorkout(
                                    preset.title,
                                    selectedLocation,
                                    preset.plansGenerator()
                                )
                                onDismiss()
                            }
                        )
                    }

                    // Custom Strength Quick Timer Picker
                    item {
                        CustomQuickStrengthBox(
                            selectedDuration = customDurationMinutes,
                            onDurationChanged = { customDurationMinutes = it },
                            selectedFocus = customStrengthFocus,
                            onFocusChanged = { customStrengthFocus = it },
                            onStartCustom = {
                                val generatedPlans = generateCustomQuickStrengthPlans(
                                    customStrengthFocus,
                                    customDurationMinutes
                                )
                                onStartStrengthWorkout(
                                    "Treino Rápido $customStrengthFocus (${customDurationMinutes}m)",
                                    selectedLocation,
                                    generatedPlans
                                )
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StrengthPresetCard(
    preset: QuickStrengthPreset,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, LilacAccent.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .clickable { onStart() }
            .testTag("btn_preset_${preset.id}"),
        color = PurpleDeepCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PurplePrimaryDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = preset.icon,
                            contentDescription = null,
                            tint = LilacAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = preset.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = preset.subtitle,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                }

                // Direct Start Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(GradientAction),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Iniciar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Exercise chips preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                preset.exercisesSummary.take(3).forEach { exName ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PurpleDarkSurface.copy(alpha = 0.7f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = exName,
                            fontSize = 10.sp,
                            color = LilacSoft,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                if (preset.exercisesSummary.size > 3) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PurpleDarkSurface.copy(alpha = 0.7f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "+${preset.exercisesSummary.size - 3}",
                            fontSize = 10.sp,
                            color = LilacAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = LilacAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${preset.durationMinutes} min",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = AmberWarning,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "~${preset.estimatedCalories} kcal",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                Text(
                    text = "Toque para Iniciar ➔",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LilacAccent
                )
            }
        }
    }
}

@Composable
fun CardioPresetCard(
    preset: QuickCardioPreset,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, EmeraldSuccess.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .clickable { onStart() }
            .testTag("btn_preset_${preset.id}"),
        color = PurpleDeepCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(EmeraldSubtle),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = preset.icon,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = preset.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (preset.badge != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (preset.enableGps) EmeraldSubtle else PurpleDarkSurface)
                                        .border(
                                            0.8.dp,
                                            if (preset.enableGps) EmeraldSuccess else GlassBorderSubtle,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = preset.badge,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (preset.enableGps) EmeraldSuccess else LilacAccent
                                    )
                                }
                            }
                        }
                        Text(
                            text = preset.subtitle,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                }

                // Direct Start Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(EmeraldDark, EmeraldSuccess)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Iniciar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = LilacAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${preset.defaultMinutes} min",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = AmberWarning,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "~${preset.estimatedCalories} kcal",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    if (preset.enableGps) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GpsFixed,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "GPS Ativo",
                                fontSize = 10.sp,
                                color = EmeraldSuccess,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = "Iniciar Agora ➔",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldSuccess
                )
            }
        }
    }
}

@Composable
fun CustomQuickStrengthBox(
    selectedDuration: Int,
    onDurationChanged: (Int) -> Unit,
    selectedFocus: String,
    onFocusChanged: (String) -> Unit,
    onStartCustom: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(20.dp)),
        color = PurpleDarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "⚡ Gerador Rápido por Tempo",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Personalize os minutos disponíveis para seu treino de força",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Duration options
            Text(
                text = "Duração Disponível:",
                fontSize = 11.sp,
                color = LilacSoft,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(10, 15, 20, 30).forEach { mins ->
                    val isSel = selectedDuration == mins
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) PurpleVibrant else PurpleDeepCard)
                            .border(
                                1.dp,
                                if (isSel) LilacAccent else GlassBorderSubtle,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onDurationChanged(mins) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$mins min",
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Black else FontWeight.Medium,
                            color = if (isSel) Color.White else TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Focus options
            Text(
                text = "Foco Muscular:",
                fontSize = 11.sp,
                color = LilacSoft,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Full Body", "Superiores", "Inferiores", "Core").forEach { focus ->
                    val isSel = selectedFocus.contains(focus, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) PurplePrimary else PurpleDeepCard.copy(alpha = 0.5f))
                            .border(
                                1.dp,
                                if (isSel) LilacAccent else GlassBorderSubtle,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onFocusChanged("$focus Express") }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = focus,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) Color.White else TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Start custom button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onStartCustom() }
                    .testTag("btn_start_custom_quick_workout"),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = PurplePrimaryDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GERAR & INICIAR (${selectedDuration} MIN)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = PurplePrimaryDark
                    )
                }
            }
        }
    }
}

@Composable
fun CustomQuickCardioBox(
    selectedIntensity: IntensityLevel,
    onIntensityChanged: (IntensityLevel) -> Unit,
    enableGps: Boolean,
    onToggleGps: (Boolean) -> Unit,
    onStartFreeCardio: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(20.dp)),
        color = PurpleDarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "⚡ Cardio Livre / Cronômetro Aberto",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Inicie a contagem de tempo, ritmo (pace), calorias e GPS em tempo real",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // GPS Checkbox / Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(PurpleDeepCard)
                    .clickable { onToggleGps(!enableGps) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = null,
                        tint = if (enableGps) EmeraldSuccess else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rastreamento GPS do Celular",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (enableGps) EmeraldSuccess else PurpleDarkSurface)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (enableGps) "ATIVO" else "DESATIVADO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (enableGps) Color.White else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Intensity Selector
            Text(
                text = "Intensidade Desejada:",
                fontSize = 11.sp,
                color = LilacSoft,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IntensityLevel.entries.forEach { level ->
                    val isSel = selectedIntensity == level
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) EmeraldDark else PurpleDeepCard)
                            .border(
                                1.dp,
                                if (isSel) EmeraldSuccess else GlassBorderSubtle,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onIntensityChanged(level) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = level.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) Color.White else TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onStartFreeCardio() }
                    .testTag("btn_start_free_cardio"),
                color = EmeraldSuccess
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "INICIAR CARDIO AGORA",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }
    }
}

fun generateCustomQuickStrengthPlans(focus: String, durationMinutes: Int): List<WorkoutExercisePlan> {
    val setsPerExercise = if (durationMinutes <= 10) 2 else 3
    val restSecs = if (durationMinutes <= 15) 30 else 45

    return when {
        focus.contains("Superiores", ignoreCase = true) -> {
            listOf(
                WorkoutExercisePlan(
                    exerciseId = 1,
                    exerciseName = "Supino reto com barra",
                    muscleGroup = "Peito",
                    sets = (1..setsPerExercise).map { ExerciseSetEntry(it, 40.0, 10, restSeconds = restSecs) },
                    targetRestSeconds = restSecs
                ),
                WorkoutExercisePlan(
                    exerciseId = 5,
                    exerciseName = "Remada curvada com barra",
                    muscleGroup = "Costas",
                    sets = (1..setsPerExercise).map { ExerciseSetEntry(it, 35.0, 10, restSeconds = restSecs) },
                    targetRestSeconds = restSecs
                ),
                WorkoutExercisePlan(
                    exerciseId = 9,
                    exerciseName = "Desenvolvimento de ombros",
                    muscleGroup = "Ombros",
                    sets = (1..setsPerExercise).map { ExerciseSetEntry(it, 20.0, 10, restSeconds = restSecs) },
                    targetRestSeconds = restSecs
                )
            )
        }
        focus.contains("Inferiores", ignoreCase = true) -> {
            listOf(
                WorkoutExercisePlan(
                    exerciseId = 11,
                    exerciseName = "Agachamento livre com barra",
                    muscleGroup = "Pernas",
                    sets = (1..setsPerExercise).map { ExerciseSetEntry(it, 45.0, 10, restSeconds = restSecs) },
                    targetRestSeconds = restSecs
                ),
                WorkoutExercisePlan(
                    exerciseId = 13,
                    exerciseName = "Levantamento terra romeno",
                    muscleGroup = "Pernas",
                    sets = (1..setsPerExercise).map { ExerciseSetEntry(it, 40.0, 10, restSeconds = restSecs) },
                    targetRestSeconds = restSecs
                ),
                WorkoutExercisePlan(
                    exerciseId = 14,
                    exerciseName = "Elevação pélvica",
                    muscleGroup = "Glúteos",
                    sets = (1..setsPerExercise).map { ExerciseSetEntry(it, 35.0, 12, restSeconds = restSecs) },
                    targetRestSeconds = restSecs
                )
            )
        }
        focus.contains("Core", ignoreCase = true) -> {
            listOf(
                WorkoutExercisePlan(
                    exerciseId = 22,
                    exerciseName = "Prancha abdominal isométrica",
                    muscleGroup = "Abdômen",
                    sets = (1..setsPerExercise).map { ExerciseSetEntry(it, 0.0, 45, restSeconds = 20) },
                    targetRestSeconds = 20
                ),
                WorkoutExercisePlan(
                    exerciseId = 23,
                    exerciseName = "Abdominal supra no solo",
                    muscleGroup = "Abdômen",
                    sets = (1..setsPerExercise).map { ExerciseSetEntry(it, 0.0, 20, restSeconds = 20) },
                    targetRestSeconds = 20
                )
            )
        }
        else -> {
            // Full Body
            listOf(
                WorkoutExercisePlan(
                    exerciseId = 1,
                    exerciseName = "Supino reto com barra",
                    muscleGroup = "Peito",
                    sets = (1..setsPerExercise).map { ExerciseSetEntry(it, 40.0, 10, restSeconds = restSecs) },
                    targetRestSeconds = restSecs
                ),
                WorkoutExercisePlan(
                    exerciseId = 11,
                    exerciseName = "Agachamento livre com barra",
                    muscleGroup = "Pernas",
                    sets = (1..setsPerExercise).map { ExerciseSetEntry(it, 45.0, 10, restSeconds = restSecs) },
                    targetRestSeconds = restSecs
                ),
                WorkoutExercisePlan(
                    exerciseId = 5,
                    exerciseName = "Remada curvada",
                    muscleGroup = "Costas",
                    sets = (1..setsPerExercise).map { ExerciseSetEntry(it, 35.0, 10, restSeconds = restSecs) },
                    targetRestSeconds = restSecs
                ),
                WorkoutExercisePlan(
                    exerciseId = 22,
                    exerciseName = "Prancha abdominal",
                    muscleGroup = "Abdômen",
                    sets = (1..2).map { ExerciseSetEntry(it, 0.0, 40, restSeconds = 20) },
                    targetRestSeconds = 20
                )
            )
        }
    }
}
