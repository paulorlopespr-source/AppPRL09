package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedalRarity
import com.example.data.model.UserMedal
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Metallic palette definition for scalable vector medals.
 */
object MedalPalette {
    // Bronze
    val BronzeDark = Color(0xFF5A2A0C)
    val BronzeBase = Color(0xFFCD7F32)
    val BronzeLight = Color(0xFFE8A87C)
    val BronzeHighlight = Color(0xFFFFD1B3)
    val BronzeRim = Color(0xFF8C4A1C)

    // Prata
    val SilverDark = Color(0xFF4A5568)
    val SilverBase = Color(0xFFC0C0C0)
    val SilverLight = Color(0xFFE2E8F0)
    val SilverHighlight = Color(0xFFFFFFFF)
    val SilverRim = Color(0xFF94A3B8)

    // Ouro
    val GoldDark = Color(0xFF8A5A00)
    val GoldBase = Color(0xFFFFD700)
    val GoldLight = Color(0xFFFFF099)
    val GoldHighlight = Color(0xFFFFFFFF)
    val GoldRim = Color(0xFFD4AF37)

    // Master da Superação (Royal Cosmic Gold + Magenta/Purple Neon)
    val MasterDark = Color(0xFF3B0764)
    val MasterBase = Color(0xFFFF4081)
    val MasterLight = Color(0xFFFFD700)
    val MasterHighlight = Color(0xFF00E5FF)
    val MasterRim = Color(0xFFA855F7)

    // Locked / Steel
    val SteelDark = Color(0xFF1E1E2E)
    val SteelBase = Color(0xFF33334D)
    val SteelLight = Color(0xFF4D4D6B)
    val SteelRim = Color(0xFF262638)
}

/**
 * Scalable Vector Medal Badge drawn via Compose Canvas and Vector Layers.
 * Supports Bronze, Prata, Ouro, and Master da Superação with dynamic lighting,
 * metallic gradients, bevels, sunburst rays, and unlock animations.
 */
@Composable
fun ScalableVectorMedalBadge(
    rarity: MedalRarity,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    iconEmoji: String = "🏆",
    showShimmer: Boolean = true,
    animated: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "medal_shimmer")
    
    // Rotating ray burst for Gold / Master
    val rayRotation by if (animated && isUnlocked && (rarity == MedalRarity.OURO || rarity == MedalRarity.MASTER_SUPERAÇÃO)) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(12000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "ray_rotation"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    // Shimmer sweep across the coin face
    val shimmerPhase by if (animated && isUnlocked && showShimmer) {
        infiniteTransition.animateFloat(
            initialValue = -1f,
            targetValue = 2f,
            animationSpec = infiniteRepeatable(
                animation = tween(2800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_phase"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    // Gentle pulse for Master medals
    val masterPulse by if (animated && isUnlocked && rarity == MedalRarity.MASTER_SUPERAÇÃO) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "master_pulse"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(1f) }
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(masterPulse),
        contentAlignment = Alignment.Center
    ) {
        // --- 1. VECTOR CANVAS (Backing Rays, Medallion Rim, Bevel, Inner Coin, Metallic Gradient) ---
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = (this.size.minDimension / 2f) * 0.92f

            if (isUnlocked) {
                // A. Sunburst / Starburst rays for Ouro & Master
                if (rarity == MedalRarity.OURO || rarity == MedalRarity.MASTER_SUPERAÇÃO) {
                    val rayColor = if (rarity == MedalRarity.MASTER_SUPERAÇÃO) {
                        MedalPalette.MasterRim.copy(alpha = 0.25f)
                    } else {
                        MedalPalette.GoldBase.copy(alpha = 0.22f)
                    }
                    drawSunburstRays(center = center, maxRadius = radius * 1.08f, raysCount = 12, rotationDegrees = rayRotation, color = rayColor)
                }

                // B. Outer Glow Halo
                val glowColor = when (rarity) {
                    MedalRarity.BRONZE -> MedalPalette.BronzeBase.copy(alpha = 0.3f)
                    MedalRarity.PRATA -> MedalPalette.SilverLight.copy(alpha = 0.35f)
                    MedalRarity.OURO -> MedalPalette.GoldBase.copy(alpha = 0.45f)
                    MedalRarity.MASTER_SUPERAÇÃO -> MedalPalette.MasterBase.copy(alpha = 0.5f)
                }
                drawCircle(
                    color = glowColor,
                    radius = radius * 1.02f,
                    center = center
                )

                // C. Outer Metallic Rim with Multi-stop Angle / Linear Gradient
                val outerBrush = when (rarity) {
                    MedalRarity.BRONZE -> Brush.linearGradient(
                        colors = listOf(MedalPalette.BronzeLight, MedalPalette.BronzeBase, MedalPalette.BronzeDark, MedalPalette.BronzeRim),
                        start = Offset(0f, 0f),
                        end = Offset(this.size.width, this.size.height)
                    )
                    MedalRarity.PRATA -> Brush.linearGradient(
                        colors = listOf(MedalPalette.SilverHighlight, MedalPalette.SilverLight, MedalPalette.SilverDark, MedalPalette.SilverBase),
                        start = Offset(0f, 0f),
                        end = Offset(this.size.width, this.size.height)
                    )
                    MedalRarity.OURO -> Brush.linearGradient(
                        colors = listOf(MedalPalette.GoldHighlight, MedalPalette.GoldBase, MedalPalette.GoldDark, MedalPalette.GoldRim),
                        start = Offset(0f, 0f),
                        end = Offset(this.size.width, this.size.height)
                    )
                    MedalRarity.MASTER_SUPERAÇÃO -> Brush.linearGradient(
                        colors = listOf(MedalPalette.MasterHighlight, MedalPalette.MasterBase, MedalPalette.MasterDark, MedalPalette.MasterLight),
                        start = Offset(0f, 0f),
                        end = Offset(this.size.width, this.size.height)
                    )
                }
                drawCircle(
                    brush = outerBrush,
                    radius = radius,
                    center = center
                )

                // D. Beveled Intermediate Step Ring
                val bevelBrush = Brush.linearGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.4f), Color.White.copy(alpha = 0.5f)),
                    start = Offset(0f, 0f),
                    end = Offset(this.size.width, this.size.height)
                )
                drawCircle(
                    brush = bevelBrush,
                    radius = radius * 0.90f,
                    center = center,
                    style = Stroke(width = radius * 0.06f)
                )

                // E. Inner Coin Face (Subtle radial gradient for metallic depth)
                val innerBrush = when (rarity) {
                    MedalRarity.BRONZE -> Brush.radialGradient(
                        colors = listOf(MedalPalette.BronzeBase, MedalPalette.BronzeDark),
                        center = center,
                        radius = radius * 0.82f
                    )
                    MedalRarity.PRATA -> Brush.radialGradient(
                        colors = listOf(MedalPalette.SilverLight, MedalPalette.SilverDark),
                        center = center,
                        radius = radius * 0.82f
                    )
                    MedalRarity.OURO -> Brush.radialGradient(
                        colors = listOf(MedalPalette.GoldLight, MedalPalette.GoldDark),
                        center = center,
                        radius = radius * 0.82f
                    )
                    MedalRarity.MASTER_SUPERAÇÃO -> Brush.radialGradient(
                        colors = listOf(MedalPalette.MasterDark, Color(0xFF1E0038)),
                        center = center,
                        radius = radius * 0.82f
                    )
                }
                drawCircle(
                    brush = innerBrush,
                    radius = radius * 0.84f,
                    center = center
                )

                // F. Concentric Ribbed Star Inset (Vector Dots / Star Ring)
                drawConcentricRibbonDetails(center = center, radius = radius * 0.74f, rarity = rarity)

                // G. Dynamic Shimmer Streak Sweep
                if (showShimmer) {
                    val shimmerX = this.size.width * shimmerPhase
                    val shimmerBrush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.7f),
                            Color.White.copy(alpha = 0.35f),
                            Color.Transparent
                        ),
                        start = Offset(shimmerX - radius * 0.5f, 0f),
                        end = Offset(shimmerX + radius * 0.5f, this.size.height)
                    )
                    drawCircle(
                        brush = shimmerBrush,
                        radius = radius * 0.84f,
                        center = center
                    )
                }

                // H. Specular Crescent Light Sheen (Upper-left shine arc)
                val sheenPath = Path().apply {
                    val rInner = radius * 0.80f
                    moveTo(center.x - rInner * 0.7f, center.y - rInner * 0.2f)
                    cubicTo(
                        center.x - rInner * 0.6f, center.y - rInner * 0.85f,
                        center.x + rInner * 0.2f, center.y - rInner * 0.85f,
                        center.x + rInner * 0.7f, center.y - rInner * 0.3f
                    )
                    cubicTo(
                        center.x + rInner * 0.2f, center.y - rInner * 0.6f,
                        center.x - rInner * 0.4f, center.y - rInner * 0.6f,
                        center.x - rInner * 0.7f, center.y - rInner * 0.2f
                    )
                    close()
                }
                drawPath(
                    path = sheenPath,
                    color = Color.White.copy(alpha = 0.28f)
                )

            } else {
                // --- LOCKED STEEL / CARBON STATE ---
                // Outer steel rim
                drawCircle(
                    brush = Brush.linearGradient(
                        listOf(MedalPalette.SteelLight, MedalPalette.SteelDark, MedalPalette.SteelRim),
                        start = Offset(0f, 0f),
                        end = Offset(this.size.width, this.size.height)
                    ),
                    radius = radius,
                    center = center
                )
                // Inner dark carbon coin
                drawCircle(
                    color = MedalPalette.SteelDark,
                    radius = radius * 0.84f,
                    center = center
                )
                // Inactive dashed border
                drawCircle(
                    color = MedalPalette.SteelLight.copy(alpha = 0.5f),
                    radius = radius * 0.74f,
                    center = center,
                    style = Stroke(width = 1.5f)
                )
            }
        }

        // --- 2. FOREGROUND ICON / EMOJI / LOCK ---
        if (isUnlocked) {
            val emojiSize = (size.value * 0.44f).sp
            Text(
                text = iconEmoji,
                fontSize = emojiSize,
                modifier = Modifier.align(Alignment.Center)
            )

            // Small checkmark badge at bottom-right for unlocked medals
            if (size >= 52.dp) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(size * 0.28f)
                        .clip(CircleShape)
                        .background(Color(0xFF0D1117))
                        .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val badgeColor = when (rarity) {
                        MedalRarity.BRONZE -> MedalPalette.BronzeBase
                        MedalRarity.PRATA -> MedalPalette.SilverLight
                        MedalRarity.OURO -> MedalPalette.GoldBase
                        MedalRarity.MASTER_SUPERAÇÃO -> MedalPalette.MasterLight
                    }
                    Text(
                        text = when (rarity) {
                            MedalRarity.BRONZE -> "🥉"
                            MedalRarity.PRATA -> "🥈"
                            MedalRarity.OURO -> "🥇"
                            MedalRarity.MASTER_SUPERAÇÃO -> "👑"
                        },
                        fontSize = (size.value * 0.16f).sp
                    )
                }
            }
        } else {
            // Locked icon + translucent ghost emoji
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = iconEmoji,
                    fontSize = (size.value * 0.36f).sp,
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.align(Alignment.Center)
                )
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Bloqueada",
                    tint = MedalPalette.SteelLight,
                    modifier = Modifier.size(size * 0.35f)
                )
            }
        }
    }
}

/**
 * Draws sunburst star rays for Gold and Master medals on Canvas.
 */
private fun DrawScope.drawSunburstRays(
    center: Offset,
    maxRadius: Float,
    raysCount: Int = 12,
    rotationDegrees: Float = 0f,
    color: Color
) {
    rotate(degrees = rotationDegrees, pivot = center) {
        val angleStep = (2 * Math.PI / raysCount).toFloat()
        for (i in 0 until raysCount) {
            val startAngle = i * angleStep
            val p1 = Offset(
                center.x + maxRadius * cos(startAngle),
                center.y + maxRadius * sin(startAngle)
            )
            val p2 = Offset(
                center.x + maxRadius * cos(startAngle + angleStep * 0.4f),
                center.y + maxRadius * sin(startAngle + angleStep * 0.4f)
            )
            val rayPath = Path().apply {
                moveTo(center.x, center.y)
                lineTo(p1.x, p1.y)
                lineTo(p2.x, p2.y)
                close()
            }
            drawPath(path = rayPath, color = color)
        }
    }
}

/**
 * Draws decorative concentric dots around the inner medal rim.
 */
private fun DrawScope.drawConcentricRibbonDetails(
    center: Offset,
    radius: Float,
    rarity: MedalRarity
) {
    val dotCount = 18
    val dotRadius = radius * 0.035f
    val dotColor = when (rarity) {
        MedalRarity.BRONZE -> MedalPalette.BronzeDark.copy(alpha = 0.6f)
        MedalRarity.PRATA -> MedalPalette.SilverDark.copy(alpha = 0.5f)
        MedalRarity.OURO -> MedalPalette.GoldDark.copy(alpha = 0.6f)
        MedalRarity.MASTER_SUPERAÇÃO -> MedalPalette.MasterHighlight.copy(alpha = 0.7f)
    }

    for (i in 0 until dotCount) {
        val angle = (i * (2 * Math.PI / dotCount)).toFloat()
        val x = center.x + radius * cos(angle)
        val y = center.y + radius * sin(angle)
        drawCircle(
            color = dotColor,
            radius = dotRadius,
            center = Offset(x, y)
        )
    }
}

/**
 * Physics particle for achievement celebration explosion.
 */
data class ConfettiParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val size: Float,
    val color: Color,
    var rotation: Float,
    val rotationSpeed: Float,
    var alpha: Float = 1f,
    val isStar: Boolean = false
)

/**
 * Dynamic Confetti Particle Explosion Canvas for achievement moments.
 */
@Composable
fun AchievementConfettiCelebration(
    modifier: Modifier = Modifier,
    particleCount: Int = 45,
    primaryColor: Color = MedalPalette.GoldBase
) {
    val particles = remember {
        mutableStateListOf<ConfettiParticle>().apply {
            val palette = listOf(
                primaryColor,
                MedalPalette.GoldHighlight,
                MedalPalette.MasterBase,
                MedalPalette.MasterHighlight,
                Color(0xFF00E676),
                Color(0xFF2979FF),
                Color(0xFFFFEA00)
            )
            val random = Random(System.currentTimeMillis())
            for (i in 0 until particleCount) {
                val angle = random.nextFloat() * 2 * Math.PI.toFloat()
                val speed = random.nextFloat() * 8f + 4f
                add(
                    ConfettiParticle(
                        x = 0.5f,
                        y = 0.5f,
                        vx = cos(angle) * speed,
                        vy = sin(angle) * speed - 3f, // upward bias
                        size = random.nextFloat() * 10f + 6f,
                        color = palette[random.nextInt(palette.size)],
                        rotation = random.nextFloat() * 360f,
                        rotationSpeed = (random.nextFloat() - 0.5f) * 15f,
                        isStar = random.nextBoolean()
                    )
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (true) {
            val elapsed = (System.currentTimeMillis() - startTime) / 1000f
            if (elapsed > 4.5f) break

            particles.forEach { p ->
                p.x += p.vx * 0.003f
                p.y += p.vy * 0.003f
                p.vy += 0.18f // gravity
                p.rotation += p.rotationSpeed
                p.alpha = (1f - (elapsed / 4.2f)).coerceIn(0f, 1f)
            }
            kotlinx.coroutines.delay(16)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEach { p ->
            if (p.alpha > 0.01f) {
                val px = p.x * w
                val py = p.y * h
                rotate(degrees = p.rotation, pivot = Offset(px, py)) {
                    if (p.isStar) {
                        drawCircle(
                            color = p.color.copy(alpha = p.alpha),
                            radius = p.size / 2f,
                            center = Offset(px, py)
                        )
                    } else {
                        drawRect(
                            color = p.color.copy(alpha = p.alpha),
                            topLeft = Offset(px - p.size / 2f, py - p.size / 4f),
                            size = Size(p.size, p.size * 0.6f)
                        )
                    }
                }
            }
        }
    }
}
