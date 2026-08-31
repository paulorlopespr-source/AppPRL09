package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.WorkoutSession
import com.example.ui.theme.EmeraldDark
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
import com.example.ui.theme.YellowWarning

/**
 * Card Visual Estilizado para Compartilhamento em Stories do Instagram, WhatsApp e Redes Sociais.
 * Transmite autoridade visual com volume em toneladas levantadas, medalhas e métricas.
 */
@Composable
fun WorkoutShareStoryDialog(
    session: WorkoutSession,
    userName: String = "Atleta FitPr09",
    prsCount: Int = 0,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val durationFormatted = DateUtils.formatSecondsToTime(session.durationSeconds)
    val tonsLifted = (session.totalWeightLiftedKg / 1000.0)
    val tonsFormatted = if (tonsLifted >= 1.0) "%.1f TONELADAS".format(tonsLifted) else "${session.totalWeightLiftedKg.toInt()} KG"

    val shareText = """
        🔥 Treino Concluído no FitPr09!
        🏋️ Sessão: ${session.title}
        ⏱️ Duração: $durationFormatted
        ⚡ Volume Total: $tonsFormatted levantadas
        🔥 Calorias: ~${session.estimatedCalories} kcal
        📍 Local: ${session.location}
        ${if (prsCount > 0) "🏆 $prsCount Novos Recordes Pessoais Batidos!\n" else ""}
        #FitPr09 #Musculação #Hipertrofia #FitnessGoals
    """.trimIndent()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = PurpleDarkest,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, GlassBorder),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top close bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Compartilhar Conquista",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // The 9:16 vertical Story Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    PurpleDarkSurface,
                                    PurpleDeepCard,
                                    PurpleDarkest
                                )
                            )
                        )
                        .border(
                            1.5.dp,
                            Brush.linearGradient(listOf(LilacAccent, PurpleVibrant, EmeraldSuccess)),
                            RoundedCornerShape(24.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Brand & Athlete Tag
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(GradientAction),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FitnessCenter,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "FITTREINO PRO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = LilacAccent,
                                    letterSpacing = 1.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EmeraldDark.copy(alpha = 0.3f))
                                    .border(0.8.dp, EmeraldSuccess, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "CONCLUÍDO",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldSuccess
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Workout Title & Athlete Name
                        Text(
                            text = session.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Atleta: $userName • ${session.location}",
                            style = MaterialTheme.typography.bodySmall,
                            color = LilacSoft,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Hero Tons Lifted Big Metric
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            PurplePrimary.copy(alpha = 0.35f),
                                            PurpleVibrant.copy(alpha = 0.25f)
                                        )
                                    )
                                )
                                .border(1.dp, LilacAccent.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                                .padding(vertical = 16.dp, horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "CARGA TOTAL LEVANTADA",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LilacAccent,
                                    letterSpacing = 1.2.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = tonsFormatted,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Grid (Time & Calories)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Duration
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(PurpleDarkSurface)
                                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp))
                                    .padding(10.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = LilacAccent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = "TEMPO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                    Text(text = durationFormatted, fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                }
                            }

                            // Calories
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(PurpleDarkSurface)
                                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp))
                                    .padding(10.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = YellowWarning, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = "GASTO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                    Text(text = "${session.estimatedCalories} kcal", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                }
                            }
                        }

                        if (prsCount > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(YellowWarning.copy(alpha = 0.15f))
                                    .border(1.dp, YellowWarning, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = YellowWarning, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$prsCount Recordes Pessoais Batidos!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = YellowWarning
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Sharing Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PrimaryButton(
                        text = "Compartilhar no Stories / WhatsApp",
                        icon = Icons.Default.Share,
                        onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Compartilhar Treino")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_share_workout_social")
                    )

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Resumo Treino FitPr09", shareText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Resumo copiado para a área de transferência!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = LilacAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copiar Resumo em Texto", color = LilacAccent, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
