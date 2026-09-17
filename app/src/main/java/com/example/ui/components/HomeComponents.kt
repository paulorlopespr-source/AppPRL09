package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacSoft
import com.example.ui.theme.PurpleDarkSurface
import com.example.ui.theme.PurpleDarkest
import com.example.ui.theme.PurpleDeepCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Presentation UI State for the Home Screen.
 * Centralizes presentation-only data to keep the composables declarative and clean.
 */
data class HomeUiState(
    val userName: String = "Atleta",
    val todayWorkoutTitle: String = "Treino do Dia",
    val todayMuscleGroup: String = "",
    val hasPlannedWorkout: Boolean = false,
    val isWorkoutActive: Boolean = false,
    val activeWorkoutTitle: String = "",
    val activeWorkoutDurationSeconds: Long = 0L,
    val isCardioActive: Boolean = false,
    val activeCardioTitle: String = "",
    val activeCardioDurationMinutes: Int = 0,
    val streakDays: Int = 0,
    val weeklyDoneCount: Int = 0,
    val weeklyGoalTarget: Int = 5,
    val readinessScore: Int? = null,
    val readinessLabel: String = "",
    val hasReadinessData: Boolean = false,
    val hasSufficientData: Boolean = true,
    val weeklyDayStatuses: List<DayProgressStatus> = emptyList()
)

data class DayProgressStatus(
    val dayLetter: String,
    val isCompleted: Boolean,
    val isCurrentOrNext: Boolean,
    val isFuture: Boolean
)

/**
 * 1. HomeHeader
 * - Hamburger menu icon on left (opens AppSideDrawer)
 * - Personalized greeting: "Olá, Paulo" (24sp, bold, white)
 * - Subtitle: "Disciplina hoje, resultados amanhã." (14sp, light lilac)
 * - Notification bell on right with subtle badge
 */
@Composable
fun HomeHeader(
    userName: String,
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit,
    hasUnreadNotification: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(42.dp)
                    .testTag("btn_home_drawer")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menu lateral",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Olá, $userName",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Disciplina hoje, resultados amanhã.",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier
                .size(42.dp)
                .testTag("btn_home_notifications")
        ) {
            BadgedBox(
                badge = {
                    if (hasUnreadNotification) {
                        Badge(
                            containerColor = Color(0xFFA855F7),
                            modifier = Modifier.size(7.dp)
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificações e Lembretes",
                    tint = Color(0xFFC084FC),
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

/**
 * 2. TodayWorkoutHero
 * High-impact motivational hero card matching the reference image:
 * - Mountain sunset background with athletic man silhouette
 * - Typography: "UM DIA\nMAIS FORTE\nCOMEÇA\nAGORA." (AGORA. in purple/lilac, rest white)
 * - Subtitle: "Disciplina é liberdade."
 * - Full-width CTA pill button: "Iniciar treino de hoje" (or contextual state)
 */
@Composable
fun TodayWorkoutHero(
    state: HomeUiState,
    onStartWorkout: () -> Unit,
    onResumeWorkout: () -> Unit,
    onResumeCardio: () -> Unit,
    onSelectWorkout: () -> Unit,
    onConfigureData: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isWorkoutActive) {
        ActiveSessionBanner(
            title = state.activeWorkoutTitle.ifBlank { "Treino em Andamento" },
            subtitle = "Cronômetro inteligente ativo",
            tag = "EM ANDAMENTO",
            durationSeconds = state.activeWorkoutDurationSeconds,
            actionLabel = "Retomar treino",
            icon = Icons.Default.PlayArrow,
            onAction = onResumeWorkout,
            modifier = modifier
        )
        return
    }

    if (state.isCardioActive) {
        ActiveSessionBanner(
            title = state.activeCardioTitle.ifBlank { "Atividade ao Ar Livre" },
            subtitle = "GPS e monitoramento em tempo real",
            tag = "CARDIO ATIVO",
            durationSeconds = state.activeCardioDurationMinutes.toLong() * 60,
            actionLabel = "Retomar atividade",
            icon = Icons.Default.DirectionsRun,
            onAction = onResumeCardio,
            modifier = modifier
        )
        return
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_today_workout_hero"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF130E22)),
        border = BorderStroke(1.dp, Color(0xFF281C40))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            // Background Artwork: Asset oficial ou Artwork de altíssima fidelidade à referência
            HeroBackgroundContainer(
                modifier = Modifier.fillMaxSize()
            )

            // Gradiente escuro suave no lado esquerdo para contraste e legibilidade impecável dos textos
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0F0B1A).copy(alpha = 0.92f),
                                Color(0xFF0F0B1A).copy(alpha = 0.70f),
                                Color(0xFF0F0B1A).copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            startX = 0f,
                            endX = 750f
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF0F0B1A).copy(alpha = 0.40f),
                                Color(0xFF0F0B1A).copy(alpha = 0.85f)
                            ),
                            startY = 180f
                        )
                    )
            )

            // Foreground Content idêntico à referência visual do usuário
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    val headline = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp,
                                letterSpacing = 0.5.sp
                            )
                        ) {
                            append("UM DIA\nMAIS FORTE\nCOMEÇA\n")
                        }
                        withStyle(
                            SpanStyle(
                                color = Color(0xFFC084FC), // Lilás / violeta luminoso da referência
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp,
                                letterSpacing = 0.5.sp
                            )
                        ) {
                            append("AGORA.")
                        }
                    }

                    Text(
                        text = headline,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Disciplina é liberdade.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFCBD5E1)
                    )
                }

                // CTA Button: Estilo vibrante idêntico à referência
                val (ctaLabel, ctaAction) = when {
                    !state.hasSufficientData -> Pair("Configurar / registrar dados", onConfigureData)
                    !state.hasPlannedWorkout -> Pair("Escolher treino", onSelectWorkout)
                    else -> Pair("Iniciar treino de hoje", onStartWorkout)
                }

                Button(
                    onClick = ctaAction,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6D28D9) // Roxo sólido vibrante da referência
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_hero_cta")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ctaLabel,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Container de fundo para o Hero da Tela Início.
 *
 * Suporta tanto imagem externa em drawable (img_hero_workout_card / hero_home_bg)
 * quanto a renderização procedural de alta fidelidade da cena exata (montanhas, pôr do sol e silhueta atlética).
 */
@Composable
fun HeroBackgroundContainer(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val heroDrawableId = remember(context) {
        val id1 = context.resources.getIdentifier("img_hero_workout_card", "drawable", context.packageName)
        if (id1 != 0) id1 else {
            val id2 = context.resources.getIdentifier("hero_home_bg", "drawable", context.packageName)
            if (id2 != 0) id2 else 0
        }
    }

    if (heroDrawableId != 0) {
        Image(
            painter = painterResource(id = heroDrawableId),
            contentDescription = "Hero Background",
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxSize()
        )
    } else {
        HeroMountainSunsetAthleteArtwork(modifier = modifier)
    }
}

/**
 * Arte procedural em alta fidelidade reproduzindo a imagem de referência:
 * - Céu crepuscular roxo/violeta
 * - Brilho do sol poente quente (âmbar/dourado) atrás das montanhas
 * - Cordilheira rochosa com silhuetas de pinheiros na encosta esquerda
 * - Silhueta atlética masculina de costas com iluminação rim light dourada nos ombros e trapézio
 */
@Composable
fun HeroMountainSunsetAthleteArtwork(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Céu base do crepúsculo (gradiente vertical roxo profundo a magenta)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF120726), // Topo noturno
                    Color(0xFF260D47), // Roxo profundo
                    Color(0xFF4C1572), // Violeta médio
                    Color(0xFF8B2671), // Magenta do pôr do sol
                    Color(0xFF200B36)  // Base inferior
                )
            )
        )

        // 2. Brilho do sol poente no horizonte (centro à direita, atrás do atleta)
        val sunCenter = Offset(w * 0.74f, h * 0.38f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFD494).copy(alpha = 0.85f), // Núcleo dourado/pêssego
                    Color(0xFFF97316).copy(alpha = 0.65f), // Laranja fogo
                    Color(0xFFEA580C).copy(alpha = 0.40f), // Âmbar
                    Color(0xFFA855F7).copy(alpha = 0.18f), // Difusão lilás
                    Color.Transparent
                ),
                center = sunCenter,
                radius = w * 0.42f
            ),
            radius = w * 0.42f,
            center = sunCenter
        )

        // Faixas suaves de nuvens quentes no horizonte direito
        val cloudBrush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                Color(0xFFFF9E3D).copy(alpha = 0.30f),
                Color(0xFFC026D3).copy(alpha = 0.25f),
                Color.Transparent
            ),
            startX = w * 0.45f,
            endX = w
        )
        drawRect(
            brush = cloudBrush,
            topLeft = Offset(w * 0.40f, h * 0.32f),
            size = androidx.compose.ui.geometry.Size(w * 0.60f, h * 0.08f)
        )

        // 3. Montanhas distantes (picos alpinos pontiagudos em roxo-azul escuro)
        val distantMountains = Path().apply {
            moveTo(0f, h * 0.64f)
            lineTo(w * 0.15f, h * 0.58f)
            lineTo(w * 0.30f, h * 0.48f)
            lineTo(w * 0.42f, h * 0.54f)
            lineTo(w * 0.58f, h * 0.41f) // Pico central atrás do horizonte
            lineTo(w * 0.70f, h * 0.50f)
            lineTo(w * 0.82f, h * 0.44f) // Pico direito
            lineTo(w * 0.94f, h * 0.52f)
            lineTo(w, h * 0.48f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(distantMountains, color = Color(0xFF261245))

        // 4. Encosta montanhosa da esquerda com silhuetas de pinheiros
        val leftRidge = Path().apply {
            moveTo(0f, h * 0.78f)
            lineTo(w * 0.12f, h * 0.66f)
            lineTo(w * 0.26f, h * 0.55f)
            lineTo(w * 0.40f, h * 0.68f)
            lineTo(w * 0.55f, h * 0.78f)
            lineTo(w, h * 0.82f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(leftRidge, color = Color(0xFF170A2A))

        // Silhuetas de pinheiros na encosta esquerda (como na imagem de referência)
        fun drawPineTree(x: Float, baseY: Float, treeH: Float, treeW: Float) {
            val treePath = Path().apply {
                moveTo(x, baseY - treeH)
                lineTo(x + treeW * 0.3f, baseY - treeH * 0.65f)
                lineTo(x + treeW * 0.2f, baseY - treeH * 0.65f)
                lineTo(x + treeW * 0.45f, baseY - treeH * 0.35f)
                lineTo(x + treeW * 0.3f, baseY - treeH * 0.35f)
                lineTo(x + treeW * 0.5f, baseY)
                lineTo(x - treeW * 0.5f, baseY)
                lineTo(x - treeW * 0.3f, baseY - treeH * 0.35f)
                lineTo(x - treeW * 0.45f, baseY - treeH * 0.35f)
                lineTo(x - treeW * 0.2f, baseY - treeH * 0.65f)
                lineTo(x - treeW * 0.3f, baseY - treeH * 0.65f)
                close()
            }
            drawPath(treePath, color = Color(0xFF0F061C))
        }

        drawPineTree(w * 0.04f, h * 0.68f, h * 0.18f, w * 0.05f)
        drawPineTree(w * 0.09f, h * 0.64f, h * 0.22f, w * 0.06f)
        drawPineTree(w * 0.15f, h * 0.61f, h * 0.16f, w * 0.045f)
        drawPineTree(w * 0.20f, h * 0.58f, h * 0.14f, w * 0.04f)
        drawPineTree(w * 0.25f, h * 0.57f, h * 0.12f, w * 0.035f)

        // 5. Silhueta Atlética Masculina de Costas (Corpo musculoso, ombros largos e trapézio)
        val manX = w * 0.71f
        val manY = h * 0.48f
        val scale = h * 0.0035f

        val athleteBody = Path().apply {
            val headRadius = 15f * scale
            val headTop = manY - 58f * scale

            // Início no pescoço/trapézio esquerdo
            moveTo(manX - 10f * scale, headTop + headRadius * 1.5f)
            // Trapézio subindo até o ombro esquerdo largo
            lineTo(manX - 38f * scale, manY - 24f * scale)
            // Deltoide posterior esquerdo (curva musculosa)
            lineTo(manX - 44f * scale, manY - 10f * scale)
            // Manga da camiseta atlética esquerda
            lineTo(manX - 42f * scale, manY + 16f * scale)
            // Braço / tríceps descendo
            lineTo(manX - 40f * scale, manY + 48f * scale)
            // Entrada para a cintura (V-Taper acentuado)
            lineTo(manX - 25f * scale, manY + 68f * scale)
            // Base inferior da camiseta / tronco
            lineTo(manX - 22f * scale, h)
            lineTo(manX + 26f * scale, h)
            // Cintura direita
            lineTo(manX + 28f * scale, manY + 68f * scale)
            // Braço / tríceps direito
            lineTo(manX + 44f * scale, manY + 48f * scale)
            // Manga da camiseta atlética direita
            lineTo(manX + 46f * scale, manY + 16f * scale)
            // Deltoide direito
            lineTo(manX + 48f * scale, manY - 10f * scale)
            // Trapézio direito descendo em direção ao pescoço
            lineTo(manX + 42f * scale, manY - 24f * scale)
            lineTo(manX + 10f * scale, headTop + headRadius * 1.5f)
            close()
        }

        // Desenhar corpo da silhueta (cor da camiseta atlética escura)
        drawPath(athleteBody, color = Color(0xFF100B1A))

        // Cabeça com corte atlético
        val headCenter = Offset(manX + 1f * scale, manY - 44f * scale)
        drawCircle(
            color = Color(0xFF0F0818),
            radius = 15f * scale,
            center = headCenter
        )

        // Detalhes de costura/sombra na camiseta atlética (costas musculosas)
        val backSeam = Path().apply {
            moveTo(manX - 26f * scale, manY - 18f * scale)
            lineTo(manX, manY - 8f * scale)
            lineTo(manX + 28f * scale, manY - 18f * scale)
        }
        drawPath(
            path = backSeam,
            color = Color(0xFF1D152A),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f * scale)
        )

        // 6. Rim Light Dourada do Pôr do Sol (contorno de luz nas bordas direitas)
        // Destaca ombro, trapézio, pescoço e cabeça banhados pelo sol
        drawLine(
            color = Color(0xFFFFBF7F).copy(alpha = 0.95f),
            start = Offset(manX + 8f * scale, manY - 32f * scale),
            end = Offset(manX + 42f * scale, manY - 24f * scale),
            strokeWidth = 3.5f * scale
        )
        drawLine(
            color = Color(0xFFFFBF7F).copy(alpha = 0.95f),
            start = Offset(manX + 42f * scale, manY - 24f * scale),
            end = Offset(manX + 48f * scale, manY - 10f * scale),
            strokeWidth = 3.5f * scale
        )
        drawLine(
            color = Color(0xFFFF9E3D).copy(alpha = 0.85f),
            start = Offset(manX + 48f * scale, manY - 10f * scale),
            end = Offset(manX + 46f * scale, manY + 22f * scale),
            strokeWidth = 3.0f * scale
        )

        // Rim light na orelha e lateral da cabeça
        drawLine(
            color = Color(0xFFFFBF7F).copy(alpha = 0.90f),
            start = Offset(manX + 12f * scale, manY - 52f * scale),
            end = Offset(manX + 16f * scale, manY - 42f * scale),
            strokeWidth = 2.5f * scale
        )

        // Rim light suave e lilás no lado esquerdo
        drawLine(
            color = Color(0xFFA855F7).copy(alpha = 0.40f),
            start = Offset(manX - 8f * scale, manY - 32f * scale),
            end = Offset(manX - 38f * scale, manY - 24f * scale),
            strokeWidth = 2.0f * scale
        )
    }
}

/**
 * Banner for Active Workout or Active Cardio session
 */
@Composable
fun ActiveSessionBanner(
    title: String,
    subtitle: String,
    tag: String,
    durationSeconds: Long,
    actionLabel: String,
    icon: ImageVector,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("banner_active_session"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF170F2A)),
        border = BorderStroke(1.2.dp, Color(0xFFA855F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF22C55E).copy(alpha = 0.2f))
                        .border(1.dp, Color(0xFF22C55E), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = tag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF22C55E),
                        letterSpacing = 1.sp
                    )
                }

                val minutes = durationSeconds / 60
                val seconds = durationSeconds % 60
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC4B5FD)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAction,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF7C3AED),
                                Color(0xFF9333EA),
                                Color(0xFFA855F7)
                            )
                        )
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = actionLabel, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

/**
 * Ícone concêntrico vetorial de Alvo / Bullseye verde (foco/readiness)
 */
@Composable
fun TargetFocusIcon(tint: Color, modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f
        val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)

        // Anel externo
        drawCircle(
            color = tint,
            radius = radius * 0.88f,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.2.dp.toPx())
        )
        // Anel intermediário
        drawCircle(
            color = tint,
            radius = radius * 0.52f,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.0.dp.toPx())
        )
        // Ponto central
        drawCircle(
            color = tint,
            radius = radius * 0.22f,
            center = center
        )
    }
}

/**
 * 3. EssentialMetricsRow (3 cards compactos, Row com weight igual)
 * - Card 1: 🔥 flame (orange) + streak real (ex: "12" ou "0") + "Dias seguidos"
 * - Card 2: 🏋️ dumbbell (purple) + "2 / 5" + "Treinos semanais"
 * - Card 3: 🎯 target (green) + real score (ex: "78" ou "—") + "Seu foco hoje"
 */
@Composable
fun EssentialMetricsRow(
    streakDays: Int,
    weeklyDone: Int,
    weeklyGoal: Int,
    readinessScore: Int?,
    readinessLabel: String = "",
    hasReadinessData: Boolean,
    onReadinessClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Card 1: Streak (dados reais)
        EssentialMetricCard(
            icon = Icons.Default.LocalFireDepartment,
            iconTint = Color(0xFFF97316), // Laranja vibrante da referência
            value = if (streakDays > 0) "$streakDays" else "0",
            label = "Dias seguidos",
            testTag = "card_metric_streak",
            modifier = Modifier.weight(1f)
        )

        // Card 2: Weekly Workouts (dados reais)
        EssentialMetricCard(
            icon = Icons.Default.FitnessCenter,
            iconTint = Color(0xFFC084FC), // Lilás / violeta luminoso da referência
            value = "$weeklyDone / $weeklyGoal",
            label = "Treinos semanais",
            testTag = "card_metric_weekly",
            modifier = Modifier.weight(1f)
        )

        // Card 3: Focus / Readiness (dados reais com affordance para check-in)
        EssentialMetricCard(
            icon = Icons.Default.GpsFixed,
            iconTint = Color(0xFF22C55E), // Verde esmeralda da referência
            customIcon = {
                TargetFocusIcon(
                    tint = Color(0xFF22C55E),
                    modifier = Modifier.size(24.dp)
                )
            },
            value = if (hasReadinessData && readinessScore != null) "$readinessScore" else "—",
            label = "Seu foco hoje",
            testTag = "card_metric_focus",
            onClick = onReadinessClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EssentialMetricsRow(
    streakDays: Int,
    weeklyDone: Int,
    weeklyGoal: Int,
    readinessScore: Int,
    hasSufficientData: Boolean,
    modifier: Modifier = Modifier
) {
    EssentialMetricsRow(
        streakDays = streakDays,
        weeklyDone = weeklyDone,
        weeklyGoal = weeklyGoal,
        readinessScore = if (hasSufficientData) readinessScore else null,
        readinessLabel = if (hasSufficientData) "Readiness" else "",
        hasReadinessData = hasSufficientData,
        onReadinessClick = {},
        modifier = modifier
    )
}

@Composable
fun EssentialMetricCard(
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String,
    testTag: String,
    customIcon: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF130E22)),
        border = BorderStroke(1.dp, Color(0xFF281C40))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (customIcon != null) {
                customIcon()
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 14.sp
            )
        }
    }
}

/**
 * 4. WeeklyProgressCard
 * - Header: Bar chart icon + "Seu progresso" + Chevron right (leads to Dashboard)
 * - Row: "Treinos da semana" ... "2 / 5"
 * - Horizontal Progress Bar
 * - 7 Day circles (S T Q Q S S D)
 */
@Composable
fun WeeklyProgressCard(
    weeklyDone: Int,
    weeklyGoal: Int,
    dayStatuses: List<DayProgressStatus>,
    onOpenDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenDashboard() }
            .testTag("card_weekly_progress"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF130E22)),
        border = BorderStroke(1.dp, Color(0xFF281C40))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = Color(0xFFA855F7),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Seu progresso",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Ir para o painel",
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Workouts count line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Treinos da semana",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )

                val countText = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)) {
                        append("$weeklyDone ")
                    }
                    withStyle(SpanStyle(color = Color(0xFF94A3B8), fontWeight = FontWeight.Normal, fontSize = 14.sp)) {
                        append("/ $weeklyGoal")
                    }
                }
                Text(text = countText)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal Progress Bar
            val progressFraction = if (weeklyGoal > 0) (weeklyDone.toFloat() / weeklyGoal).coerceIn(0f, 1f) else 0f
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF251A3C))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progressFraction)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF8B5CF6),
                                    Color(0xFFA855F7)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7 Circular Day Indicators (S T Q Q S S D)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                dayStatuses.forEach { day ->
                    DayCircleIndicator(status = day)
                }
            }
        }
    }
}

@Composable
fun DayCircleIndicator(status: DayProgressStatus) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (status.isCompleted) {
            // Círculo Verde Preenchido com Checkmark
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Concluído",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = status.dayLetter,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
            )
        } else {
            // Círculo com Anel Roxo Fino (Dias futuros ou pendentes)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color(0xFF581C87), CircleShape)
                    .background(Color.Transparent)
            )
            Text(
                text = status.dayLetter,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

/**
 * 5. MotivationCard
 * - Quote icon in purple
 * - Italic text: "Pequenas ações diárias constroem grandes resultados."
 * - Subtle purple decorative bar
 */
@Composable
fun MotivationCard(
    quote: String = "Pequenas ações diárias\nconstroem grandes resultados.",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_motivation"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF130E22)),
        border = BorderStroke(1.dp, Color(0xFF281C40))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "“",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFA855F7),
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quote,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(2.5.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFA855F7))
                )
            }
        }
    }
}

/**
 * 6. AppSideDrawer
 * Clean, organized side navigation drawer hosting all secondary features:
 * - User Profile & Avatar
 * - Workout History
 * - Health Connect
 * - Coach PRL09
 * - AI Nutrition & Workouts
 * - Settings & Reminders
 * - Data Export
 * - About / Version
 */
@Composable
fun AppSideDrawer(
    userProfile: UserProfile?,
    onCloseDrawer: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenHealthConnect: () -> Unit,
    onOpenCoach: () -> Unit,
    onOpenNutrition: () -> Unit,
    onOpenWorkoutGenerator: () -> Unit,
    onOpenReminders: () -> Unit,
    onOpenDataExport: () -> Unit,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
            .width(310.dp)
            .fillMaxHeight(),
        drawerContainerColor = Color(0xFF0F0B1A),
        drawerTonalElevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Header: User Profile
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onCloseDrawer()
                                onOpenProfile()
                            }
                    ) {
                        UserAvatarView(
                            photoUri = userProfile?.photoUri,
                            userName = userProfile?.name ?: "Paulo",
                            size = 46.dp,
                            showEditBadge = false
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = userProfile?.name ?: "Paulo",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = userProfile?.goal?.label ?: "Atleta FitPro PRL09",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    IconButton(onClick = onCloseDrawer) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar menu",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFF2E204A).copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(12.dp))

                // Menu Items
                DrawerMenuItem(
                    icon = Icons.Default.Person,
                    title = "Meu Perfil & Metas",
                    subtitle = "Dados corporais e foco semanal",
                    tint = Color(0xFFA855F7),
                    onClick = {
                        onCloseDrawer()
                        onOpenProfile()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.History,
                    title = "Histórico de Treinos",
                    subtitle = "Todas as sessões e cardio anteriores",
                    tint = Color(0xFF38BDF8),
                    onClick = {
                        onCloseDrawer()
                        onOpenHistory()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.Watch,
                    title = "Saúde & Health Connect",
                    subtitle = "Sincronização com relógio e sensores",
                    tint = Color(0xFF22C55E),
                    onClick = {
                        onCloseDrawer()
                        onOpenHealthConnect()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.Psychology,
                    title = "Coach IA PRL09",
                    subtitle = "Instruções inteligentes de treino",
                    tint = Color(0xFFC084FC),
                    onClick = {
                        onCloseDrawer()
                        onOpenCoach()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.Restaurant,
                    title = "Nutrição & Dieta IA",
                    subtitle = "Sugestões de calorias e macros",
                    tint = Color(0xFFF59E0B),
                    onClick = {
                        onCloseDrawer()
                        onOpenNutrition()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.AutoAwesome,
                    title = "Gerador de Fichas IA",
                    subtitle = "Criar novos treinos personalizados",
                    tint = Color(0xFFA78BFA),
                    onClick = {
                        onCloseDrawer()
                        onOpenWorkoutGenerator()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.Notifications,
                    title = "Configurações & Lembretes",
                    subtitle = "Horários de treino e alertas",
                    tint = Color(0xFFE2E8F0),
                    onClick = {
                        onCloseDrawer()
                        onOpenReminders()
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.CloudDownload,
                    title = "Exportar Dados",
                    subtitle = "Backup em JSON e relatórios",
                    tint = Color(0xFF94A3B8),
                    onClick = {
                        onCloseDrawer()
                        onOpenDataExport()
                    }
                )
            }

            // Footer: Version & Info
            Column {
                HorizontalDivider(color = Color(0xFF2E204A).copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onCloseDrawer()
                            onOpenAbout()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF71717A),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FitPro PRL09 • v1.0",
                            fontSize = 12.sp,
                            color = Color(0xFF71717A)
                        )
                    }
                    Text(
                        text = "Sobre",
                        fontSize = 12.sp,
                        color = Color(0xFFA855F7),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
