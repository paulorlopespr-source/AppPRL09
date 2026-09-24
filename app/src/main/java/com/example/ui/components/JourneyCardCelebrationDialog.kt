package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.gamification.PintinhoJourneyCard
import com.example.utils.SoundEffectManager

/** Celebration displayed only once for each locally recorded Journey card unlock. */
@Composable
fun JourneyCardCelebrationDialog(
    card: PintinhoJourneyCard,
    onOpenCollection: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var entered by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (entered) 1f else 0.72f,
        animationSpec = spring(dampingRatio = 0.58f, stiffness = 360f),
        label = "journey_card_unlock_scale"
    )
    val artworkResId = context.resources.getIdentifier(card.assetKey, "drawable", context.packageName)

    LaunchedEffect(card.level) {
        entered = true
        SoundEffectManager.playCelebrationFanfare()
        SoundEffectManager.vibrateCelebration(context)
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxWidth(0.9f), contentAlignment = Alignment.Center) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF100A1D),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Brush.linearGradient(listOf(Color(0xFFFFD166), Color(0xFFA855F7))), RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("CARD DESBLOQUEADO", color = Color(0xFFFFD166), fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 1.5.sp)
                    Text("✨", fontSize = 28.sp)
                    if (artworkResId != 0) {
                        Image(
                            painter = painterResource(artworkResId),
                            contentDescription = "Card desbloqueado: ${card.title}",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(width = 176.dp, height = 258.dp).scale(cardScale).clip(RoundedCornerShape(14.dp))
                        )
                    }
                    Text(card.title, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                    Text(card.description, color = Color(0xFFD8CBEF), fontSize = 14.sp, textAlign = TextAlign.Center)
                    Text("+${card.xpReward} XP${card.chest?.let { " • ${it.label}" } ?: ""}", color = Color(0xFFC084FC), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Button(
                        onClick = onOpenCollection,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("VER NA COLEÇÃO", fontWeight = FontWeight.Bold) }
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color(0xFFD8CBEF)),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Continuar") }
                }
            }
            AchievementConfettiCelebration(
                modifier = Modifier.fillMaxSize(),
                particleCount = 58,
                primaryColor = Color(0xFFFFD166)
            )
        }
    }
}

@Composable
fun JourneyCardDetailsDialog(
    card: PintinhoJourneyCard,
    onDismiss: () -> Unit,
    unlockInfo: String? = null
) {
    val context = LocalContext.current
    val artworkResId = context.resources.getIdentifier(card.assetKey, "drawable", context.packageName)
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxSize(0.94f),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F0A1D),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C3AED))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Nível ${card.level} • ${card.rank}", color = Color(0xFFC4B5FD), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(14.dp))
                if (artworkResId != 0) {
                    Image(
                        painter = painterResource(artworkResId),
                        contentDescription = card.title,
                        modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(14.dp))
                Text(card.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(card.description, color = Color(0xFFB8A9D8), fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                Text("Objetivo: ${card.target} • Recompensa: ${card.xpReward} XP${card.chest?.let { " • ${it.label}" } ?: ""}", color = Color(0xFFC4B5FD), fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 10.dp))
                unlockInfo?.let { Text(it, color = Color(0xFF34D399), fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp)) }
                Spacer(Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))) { Text("Fechar", color = Color.White) }
            }
        }
    }
}
