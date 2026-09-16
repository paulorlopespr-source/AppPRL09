package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlowPurple
import com.example.ui.theme.GradientAction
import com.example.ui.theme.GradientHeroPrimary
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.data.model.GamificationOverview
import androidx.compose.material3.rememberModalBottomSheetState

data class MenuItemDefinition(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val icon: ImageVector,
    val badge: String? = null,
    val iconTint: Color = LilacAccent,
    val testTag: String,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentralMenuSheet(
    userProfile: UserProfile?,
    gamificationOverview: GamificationOverview? = null,
    isSmartwatchConnected: Boolean = false,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
    onOpenEditProfile: () -> Unit = {},
    onNavigateToWorkouts: () -> Unit = {},
    onNavigateToActiveWorkout: () -> Unit = {},
    onNavigateToCardio: () -> Unit = {},
    onNavigateToAgenda: () -> Unit = {},
    onNavigateToEvolution: () -> Unit = {},
    onOpenAiWorkoutGenerator: () -> Unit = {},
    onOpenAiCoach: () -> Unit = {},
    onOpenNutritionAi: () -> Unit = {},
    onOpenHealthConnect: () -> Unit = {},
    onOpenReminders: () -> Unit = {},
    onOpenAudioPlayer: () -> Unit = {},
    onOpenQuickWorkout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val allMenuItems = remember(userProfile, gamificationOverview, isSmartwatchConnected) {
        listOf(
            // 🏋️ Treinos & Força
            MenuItemDefinition(
                id = "quick_workout",
                title = "⚡ Treino Rápido (Express)",
                description = "Força ou Cardio de 10 a 20 min com início direto em 1 toque",
                category = "Treinos & Força",
                icon = Icons.Default.Bolt,
                badge = "⚡ Rápido",
                iconTint = AmberWarning,
                testTag = "menu_item_quick_workout",
                onClick = {
                    onDismiss()
                    onOpenQuickWorkout()
                }
            ),
            MenuItemDefinition(
                id = "today_workout",
                title = "Treino de Hoje",
                description = "Iniciar sessão programada para hoje com cronômetro e superséries",
                category = "Treinos & Força",
                icon = Icons.Default.PlayArrow,
                badge = "Destaque",
                iconTint = EmeraldSuccess,
                testTag = "menu_item_today_workout",
                onClick = {
                    onDismiss()
                    onNavigateToActiveWorkout()
                }
            ),
            MenuItemDefinition(
                id = "all_workouts",
                title = "Fichas & Rotinas de Treino",
                description = "Full Body A/B, Divisões ABC, Push/Pull/Legs e rotinas personalizadas",
                category = "Treinos & Força",
                icon = Icons.Default.FitnessCenter,
                badge = null,
                iconTint = LilacAccent,
                testTag = "menu_item_workouts_list",
                onClick = {
                    onDismiss()
                    onNavigateToWorkouts()
                }
            ),
            MenuItemDefinition(
                id = "ai_workout_generator",
                title = "Criador de Treino IA (Gemini)",
                description = "Gere uma rotina personalizada inteligente informando seus objetivos",
                category = "Treinos & Força",
                icon = Icons.Default.AutoAwesome,
                badge = "✦ IA",
                iconTint = LilacSoft,
                testTag = "menu_item_ai_generator",
                onClick = {
                    onDismiss()
                    onOpenAiWorkoutGenerator()
                }
            ),
            MenuItemDefinition(
                id = "coach_ai",
                title = "Coach IA & Dúvidas de Treino",
                description = "Chat assistente para tirar dúvidas de biomecânica, periodização e cargas",
                category = "Treinos & Força",
                icon = Icons.Default.Psychology,
                badge = "✦ Chat",
                iconTint = PurpleVibrant,
                testTag = "menu_item_coach_ai",
                onClick = {
                    onDismiss()
                    onOpenAiCoach()
                }
            ),

            // 🏃 Cardio & GPS
            MenuItemDefinition(
                id = "cardio_live",
                title = "Sessão de Cardio em Tempo Real",
                description = "Corrida, Ciclismo, Esteira, HIIT com monitor de METs e gasto calórico",
                category = "Cardio & GPS",
                icon = Icons.Default.DirectionsBike,
                badge = null,
                iconTint = LilacAccent,
                testTag = "menu_item_cardio_live",
                onClick = {
                    onDismiss()
                    onNavigateToCardio()
                }
            ),
            MenuItemDefinition(
                id = "cardio_map",
                title = "Mapa de Rotas & Treinos GPS",
                description = "Visualização no Google Maps com trajetos ao ar livre e academias",
                category = "Cardio & GPS",
                icon = Icons.Default.Map,
                badge = "GPS",
                iconTint = EmeraldSuccess,
                testTag = "menu_item_cardio_map",
                onClick = {
                    onDismiss()
                    onNavigateToCardio()
                }
            ),

            // 📈 Evolução, Medidas & Fotos
            MenuItemDefinition(
                id = "evolution_photos",
                title = "Fotos de Progresso (Antes & Depois)",
                description = "Upload seguro e comparador lado a lado de fotos corporais",
                category = "Evolução & Biometria",
                icon = Icons.Default.AddAPhoto,
                badge = null,
                iconTint = LilacAccent,
                testTag = "menu_item_progress_photos",
                onClick = {
                    onDismiss()
                    onNavigateToEvolution()
                }
            ),
            MenuItemDefinition(
                id = "evolution_measurements",
                title = "Medidas Corporais & Biometria",
                description = "Registro de Tórax, Cintura, Braço, Coxa, Panturrilha e % de Gordura",
                category = "Evolução & Biometria",
                icon = Icons.Default.Straighten,
                badge = null,
                iconTint = LilacSoft,
                testTag = "menu_item_body_measurements",
                onClick = {
                    onDismiss()
                    onNavigateToEvolution()
                }
            ),
            MenuItemDefinition(
                id = "exercise_targets",
                title = "Metas de Carga & PRs",
                description = "Metas de 1RM e recordes pessoais de força nos exercícios",
                category = "Evolução & Biometria",
                icon = Icons.Default.TrendingUp,
                badge = null,
                iconTint = EmeraldSuccess,
                testTag = "menu_item_exercise_targets",
                onClick = {
                    onDismiss()
                    onNavigateToEvolution()
                }
            ),

            // 🏆 Gamificação & Conquistas
            MenuItemDefinition(
                id = "gamification_medals",
                title = "Galeria de Medalhas & Conquistas",
                description = "${gamificationOverview?.unlockedMedalsCount ?: 8}/${gamificationOverview?.totalMedalsCount ?: 12} medalhas desbloqueadas • ${gamificationOverview?.totalXp ?: 1450} XP",
                category = "Gamificação & Recompensas",
                icon = Icons.Default.EmojiEvents,
                badge = "Nível ${gamificationOverview?.currentLevel ?: 3}",
                iconTint = Color(0xFFFFD700),
                testTag = "menu_item_medals_gallery",
                onClick = {
                    onDismiss()
                    onNavigateToEvolution()
                }
            ),

            // 🥗 Nutrição & Dieta IA
            MenuItemDefinition(
                id = "nutrition_ai",
                title = "Nutrição & Estimador de Calorias IA",
                description = "Estime calorias e macronutrientes de refeições com Gemini 2.5",
                category = "Nutrição & Saúde",
                icon = Icons.Default.Restaurant,
                badge = "✦ IA",
                iconTint = EmeraldSuccess,
                testTag = "menu_item_nutrition_ai",
                onClick = {
                    onDismiss()
                    onOpenNutritionAi()
                }
            ),

            // ⌚ Conectividade & Dispositivos
            MenuItemDefinition(
                id = "smartwatch_sync",
                title = "Smartwatch & Health Connect",
                description = if (isSmartwatchConnected) "Sincronizado • Galaxy Watch, Wear OS, Garmin" else "Conectar ao Google Health Connect e relógio",
                category = "Dispositivos & Sensores",
                icon = Icons.Default.Watch,
                badge = if (isSmartwatchConnected) "Ativo" else "Pendente",
                iconTint = if (isSmartwatchConnected) EmeraldSuccess else LilacAccent,
                testTag = "menu_item_smartwatch_sync",
                onClick = {
                    onDismiss()
                    onOpenHealthConnect()
                }
            ),

            // 📅 Agenda & Notificações
            MenuItemDefinition(
                id = "agenda_calendar",
                title = "Agenda & Calendário Semanal",
                description = "Planeje dias de treino, agende sessões e veja o histórico completo",
                category = "Planejamento & Notificações",
                icon = Icons.Default.CalendarMonth,
                badge = null,
                iconTint = LilacAccent,
                testTag = "menu_item_agenda_calendar",
                onClick = {
                    onDismiss()
                    onNavigateToAgenda()
                }
            ),
            MenuItemDefinition(
                id = "workout_reminders",
                title = "Lembretes & Notificações Diárias",
                description = "Configure alarmes e notificações motivacionais nos seus horários de treino",
                category = "Planejamento & Notificações",
                icon = Icons.Default.Notifications,
                badge = null,
                iconTint = LilacSoft,
                testTag = "menu_item_reminders",
                onClick = {
                    onDismiss()
                    onOpenReminders()
                }
            ),

            // 🎵 Áudio & Foco
            MenuItemDefinition(
                id = "audio_player",
                title = "Player de Áudio & Músicas de Treino",
                description = "Batidas energéticas e controle sonoro durante as repetições",
                category = "Foco & Mídia",
                icon = Icons.Default.Headphones,
                badge = null,
                iconTint = PurpleVibrant,
                testTag = "menu_item_audio_player",
                onClick = {
                    onDismiss()
                    onOpenAudioPlayer()
                }
            )
        )
    }

    val filteredItems = remember(searchQuery, allMenuItems) {
        if (searchQuery.isBlank()) {
            allMenuItems
        } else {
            allMenuItems.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val groupedItems = remember(filteredItems) {
        filteredItems.groupBy { it.category }
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
        },
        modifier = modifier.testTag("sheet_central_menu")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            // Header: Title + Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Central de Informações",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Todos os recursos, ferramentas e métricas do FitPr09",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar Menu",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Athlete Profile Card with Custom Photo & Edit Action
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onOpenEditProfile()
                    }
                    .testTag("card_menu_user_profile"),
                shape = RoundedCornerShape(20.dp),
                color = PurpleDeepCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        UserAvatarView(
                            photoUri = userProfile?.photoUri,
                            userName = userProfile?.name ?: "Atleta",
                            size = 52.dp,
                            showEditBadge = true
                        )

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = userProfile?.name ?: "Atleta Fit",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(EmeraldSubtle)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Nível ${gamificationOverview?.currentLevel ?: 3}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSuccess
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${userProfile?.goal?.label ?: "Hipertrofia"} • ${userProfile?.currentWeightKg ?: 78.0} kg",
                                style = MaterialTheme.typography.bodySmall,
                                color = LilacSoft,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PurpleDarkSurface)
                            .border(1.dp, GlassBorderSubtle, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Foto e Perfil",
                            tint = LilacAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar ferramenta, treino ou métrica...", fontSize = 13.sp, color = TextMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = LilacAccent,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpar busca", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = LilacAccent,
                    unfocusedBorderColor = GlassBorder,
                    focusedContainerColor = PurpleDeepCard,
                    unfocusedContainerColor = PurpleDeepCard
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_menu_search")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Categorized Items Lazy Column
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedItems.forEach { (category, items) ->
                    item {
                        Text(
                            text = category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = LilacSoft,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                    }

                    items(items) { itemDef ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable { itemDef.onClick() }
                                .testTag(itemDef.testTag),
                            shape = RoundedCornerShape(16.dp),
                            color = PurpleDeepCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(PurpleDarkSurface)
                                            .border(1.dp, itemDef.iconTint.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = itemDef.icon,
                                            contentDescription = null,
                                            tint = itemDef.iconTint,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = itemDef.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            itemDef.badge?.let { badgeText ->
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(LilacAccent.copy(alpha = 0.2f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = badgeText,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = LilacAccent
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = itemDef.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Abrir",
                                    tint = TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
