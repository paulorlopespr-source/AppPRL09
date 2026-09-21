package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R

/**
 * Ícones e ilustrações vetoriais 100% customizados em Canvas para a tela Jornada.
 * Nenhuma imagem stock ou bitmap genérico: estética geométrica, minimalista e neon dark roxo/lilás.
 */

// ==========================================
// 1. Avatar do Pintinho da jornada
// ==========================================

@Composable
fun WolfAvatar(
    avatarRes: Int = R.drawable.journey_avatar_pintinho,
    modifier: Modifier = Modifier,
    size: Dp = 86.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(avatarRes),
            contentDescription = "Avatar da jornada",
            // O asset possui moldura circular; o clip impede qualquer pixel externo
            // (inclusive resíduos de fundo) de aparecer como um quadrado branco.
            modifier = Modifier.fillMaxSize().clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        /* Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val center = Offset(w / 2f, h / 2f)
            val radius = (w / 2f) - 4.dp.toPx()

            // 1. Aura / Glow de Neon exterior
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFA855F7).copy(alpha = glowAlpha * 0.45f),
                        Color(0xFF7C3AED).copy(alpha = glowAlpha * 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius + 10.dp.toPx()
                ),
                radius = radius + 6.dp.toPx(),
                center = center
            )

            // 2. Fundo circular escuro
            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1D1135),
                        Color(0xFF0F081D)
                    )
                ),
                radius = radius,
                center = center
            )

            // 3. Anel de borda em degradê Neon Roxo
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFFC084FC),
                        Color(0xFF7C3AED),
                        Color(0xFFA855F7),
                        Color(0xFFE9D5FF),
                        Color(0xFF7C3AED),
                        Color(0xFFC084FC)
                    ),
                    center = center
                ),
                radius = radius,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // 4. Desenho Vetorial do Lobo Geométrico (Facetas poligonais em escala w, h)
            val sx = w / 100f
            val sy = h / 100f

            // Orelha Esquerda
            val leftEar = Path().apply {
                moveTo(28f * sx, 42f * sy)
                lineTo(22f * sx, 18f * sy)
                lineTo(40f * sx, 30f * sy)
                close()
            }
            drawPath(leftEar, Color(0xFF2A1B4E))
            val leftEarInner = Path().apply {
                moveTo(28f * sx, 38f * sy)
                lineTo(24f * sx, 22f * sy)
                lineTo(37f * sx, 30f * sy)
                close()
            }
            drawPath(leftEarInner, Color(0xFFA855F7).copy(alpha = 0.7f))

            // Orelha Direita
            val rightEar = Path().apply {
                moveTo(72f * sx, 42f * sy)
                lineTo(78f * sx, 18f * sy)
                lineTo(60f * sx, 30f * sy)
                close()
            }
            drawPath(rightEar, Color(0xFF1E1538))
            val rightEarInner = Path().apply {
                moveTo(72f * sx, 38f * sy)
                lineTo(76f * sx, 22f * sy)
                lineTo(63f * sx, 30f * sy)
                close()
            }
            drawPath(rightEarInner, Color(0xFF7C3AED).copy(alpha = 0.7f))

            // Testa e Topo da Cabeça
            val forehead = Path().apply {
                moveTo(40f * sx, 30f * sy)
                lineTo(50f * sx, 24f * sy)
                lineTo(60f * sx, 30f * sy)
                lineTo(50f * sx, 42f * sy)
                close()
            }
            drawPath(forehead, Color(0xFFCBD5E1))

            val foreheadLeft = Path().apply {
                moveTo(40f * sx, 30f * sy)
                lineTo(50f * sx, 42f * sy)
                lineTo(34f * sx, 45f * sy)
                close()
            }
            drawPath(foreheadLeft, Color(0xFF94A3B8))

            val foreheadRight = Path().apply {
                moveTo(60f * sx, 30f * sy)
                lineTo(50f * sx, 42f * sy)
                lineTo(66f * sx, 45f * sy)
                close()
            }
            drawPath(foreheadRight, Color(0xFF64748B))

            // Bochechas e Pêlos laterais
            val leftCheek = Path().apply {
                moveTo(34f * sx, 45f * sy)
                lineTo(20f * sx, 52f * sy)
                lineTo(32f * sx, 58f * sy)
                lineTo(18f * sx, 66f * sy)
                lineTo(35f * sx, 68f * sy)
                lineTo(42f * sx, 60f * sy)
                close()
            }
            drawPath(leftCheek, Color(0xFFE2E8F0))

            val rightCheek = Path().apply {
                moveTo(66f * sx, 45f * sy)
                lineTo(80f * sx, 52f * sy)
                lineTo(68f * sx, 58f * sy)
                lineTo(82f * sx, 66f * sy)
                lineTo(65f * sx, 68f * sy)
                lineTo(58f * sx, 60f * sy)
                close()
            }
            drawPath(rightCheek, Color(0xFF475569))

            // Focinho e Ponte Nasal
            val snoutBridge = Path().apply {
                moveTo(50f * sx, 42f * sy)
                lineTo(44f * sx, 56f * sy)
                lineTo(50f * sx, 68f * sy)
                lineTo(56f * sx, 56f * sy)
                close()
            }
            drawPath(snoutBridge, Color(0xFFCBD5E1))

            val snoutLeft = Path().apply {
                moveTo(50f * sx, 42f * sy)
                lineTo(44f * sx, 56f * sy)
                lineTo(42f * sx, 60f * sy)
                lineTo(34f * sx, 45f * sy)
                close()
            }
            drawPath(snoutLeft, Color(0xFF94A3B8))

            val snoutRight = Path().apply {
                moveTo(50f * sx, 42f * sy)
                lineTo(56f * sx, 56f * sy)
                lineTo(58f * sx, 60f * sy)
                lineTo(66f * sx, 45f * sy)
                close()
            }
            drawPath(snoutRight, Color(0xFF334155))

            // Nariz
            val nose = Path().apply {
                moveTo(46f * sx, 68f * sy)
                lineTo(54f * sx, 68f * sy)
                lineTo(50f * sx, 74f * sy)
                close()
            }
            drawPath(nose, Color(0xFF0F172A))

            // Queixo e Mandíbula
            val chin = Path().apply {
                moveTo(50f * sx, 74f * sy)
                lineTo(44f * sx, 70f * sy)
                lineTo(40f * sx, 78f * sy)
                lineTo(50f * sx, 86f * sy)
                lineTo(60f * sx, 78f * sy)
                lineTo(56f * sx, 70f * sy)
                close()
            }
            drawPath(chin, Color(0xFFF1F5F9))

            val chinShadow = Path().apply {
                moveTo(50f * sx, 74f * sy)
                lineTo(50f * sx, 86f * sy)
                lineTo(60f * sx, 78f * sy)
                lineTo(56f * sx, 70f * sy)
                close()
            }
            drawPath(chinShadow, Color(0xFF94A3B8))

            // Olhos Neon com Destaque Roxo
            val leftEye = Path().apply {
                moveTo(38f * sx, 49f * sy)
                lineTo(45f * sx, 51f * sy)
                lineTo(40f * sx, 54f * sy)
                close()
            }
            drawPath(leftEye, Color(0xFFC084FC))

            val rightEye = Path().apply {
                moveTo(62f * sx, 49f * sy)
                lineTo(55f * sx, 51f * sy)
                lineTo(60f * sx, 54f * sy)
                close()
            }
            drawPath(rightEye, Color(0xFFA855F7))

            // Brilho da pupila
            drawCircle(Color.White, radius = 1.2f * sx, center = Offset(41.5f * sx, 51.5f * sy))
            drawCircle(Color.White, radius = 1.2f * sx, center = Offset(58.5f * sx, 51.5f * sy))
        } */
    }
}

// Miniatura simplificada para marcos da trilha
@Composable
fun WolfIconSmall(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val sx = w / 100f
        val sy = h / 100f

        // Cabeça minimalista
        val head = Path().apply {
            moveTo(24f * sx, 20f * sy) // Orelha esq
            lineTo(40f * sx, 32f * sy)
            lineTo(50f * sx, 24f * sy)
            lineTo(60f * sx, 32f * sy)
            lineTo(76f * sx, 20f * sy) // Orelha dir
            lineTo(68f * sx, 50f * sy)
            lineTo(80f * sx, 64f * sy)
            lineTo(50f * sx, 86f * sy) // Focinho
            lineTo(20f * sx, 64f * sy)
            lineTo(32f * sx, 50f * sy)
            close()
        }
        drawPath(head, Color.White)

        // Detalhes escuros
        val innerEar = Path().apply {
            moveTo(28f * sx, 26f * sy)
            lineTo(38f * sx, 34f * sy)
            lineTo(34f * sx, 44f * sy)
            close()
        }
        drawPath(innerEar, Color(0xFF7C3AED))

        val nose = Path().apply {
            moveTo(46f * sx, 72f * sy)
            lineTo(54f * sx, 72f * sy)
            lineTo(50f * sx, 80f * sy)
            close()
        }
        drawPath(nose, Color(0xFF1E1B4B))

        // Olhos
        drawCircle(Color(0xFF7C3AED), radius = 2.5f * sx, center = Offset(40f * sx, 52f * sy))
        drawCircle(Color(0xFF7C3AED), radius = 2.5f * sx, center = Offset(60f * sx, 52f * sy))
    }
}

// ==========================================
// 2. Coroa com Coroa de Louros
// ==========================================

@Composable
fun CrownLaurelIcon(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val sx = w / 100f
        val sy = h / 100f

        // Coroa central
        val crown = Path().apply {
            moveTo(30f * sx, 44f * sy)
            lineTo(33f * sx, 26f * sy)
            lineTo(42f * sx, 34f * sy)
            lineTo(50f * sx, 20f * sy)
            lineTo(58f * sx, 34f * sy)
            lineTo(67f * sx, 26f * sy)
            lineTo(70f * sx, 44f * sy)
            close()
        }
        drawPath(crown, Color(0xFFE2E8F0))

        // Faixa base da coroa
        drawRoundRect(
            color = Color(0xFFCBD5E1),
            topLeft = Offset(29f * sx, 44f * sy),
            size = Size(42f * sx, 6f * sy),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f * sx, 2f * sy)
        )

        // Jóias da coroa
        drawCircle(Color(0xFFA78BFA), radius = 2f * sx, center = Offset(50f * sx, 24f * sy))
        drawCircle(Color(0xFFA78BFA), radius = 1.5f * sx, center = Offset(35f * sx, 29f * sy))
        drawCircle(Color(0xFFA78BFA), radius = 1.5f * sx, center = Offset(65f * sx, 29f * sy))

        // Ramos de Louro Esquerdo
        val laurelLeft = Path().apply {
            moveTo(50f * sx, 78f * sy)
            cubicTo(20f * sx, 76f * sy, 14f * sx, 48f * sy, 22f * sx, 28f * sy)
        }
        drawPath(
            laurelLeft,
            color = Color(0xFFC4B5FD),
            style = Stroke(width = 2.5f * sx, cap = StrokeCap.Round)
        )

        // Folhas da esquerda
        val leafOffsetsLeft = listOf(
            Offset(20f * sx, 34f * sy),
            Offset(17f * sx, 44f * sy),
            Offset(19f * sx, 56f * sy),
            Offset(26f * sx, 67f * sy),
            Offset(37f * sx, 75f * sy)
        )
        leafOffsetsLeft.forEach { pos ->
            drawCircle(Color(0xFFE2E8F0), radius = 3f * sx, center = pos)
        }

        // Ramos de Louro Direito
        val laurelRight = Path().apply {
            moveTo(50f * sx, 78f * sy)
            cubicTo(80f * sx, 76f * sy, 86f * sx, 48f * sy, 78f * sx, 28f * sy)
        }
        drawPath(
            laurelRight,
            color = Color(0xFFC4B5FD),
            style = Stroke(width = 2.5f * sx, cap = StrokeCap.Round)
        )

        // Folhas da direita
        val leafOffsetsRight = listOf(
            Offset(80f * sx, 34f * sy),
            Offset(83f * sx, 44f * sy),
            Offset(81f * sx, 56f * sy),
            Offset(74f * sx, 67f * sy),
            Offset(63f * sx, 75f * sy)
        )
        leafOffsetsRight.forEach { pos ->
            drawCircle(Color(0xFFE2E8F0), radius = 3f * sx, center = pos)
        }
    }
}

// ==========================================
// 3. Fundo de Montanhas ao Entardecer (Canvas)
// ==========================================

@Composable
fun MountainLandscapeCanvas(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Céu com degradê do entardecer: Azul meia-noite -> Roxo profundo -> Magenta -> Laranja dourado
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0F0A1F), // Noite profunda topo
                    Color(0xFF2E1065), // Violeta escuro
                    Color(0xFF581C87), // Púrpura
                    Color(0xFF831843), // Magenta crepúsculo
                    Color(0xFFC2410C), // Laranja fogo
                    Color(0xFFF97316)  // Laranja dourado na linha do horizonte
                ),
                startY = 0f,
                endY = h * 0.65f
            )
        )

        // 2. Brilho do sol no horizonte
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFED7AA).copy(alpha = 0.5f),
                    Color(0xFFF97316).copy(alpha = 0.25f),
                    Color.Transparent
                ),
                center = Offset(w * 0.7f, h * 0.45f),
                radius = w * 0.35f
            ),
            radius = w * 0.35f,
            center = Offset(w * 0.7f, h * 0.45f)
        )

        // Estrelas discretas no topo do céu
        val starPositions = listOf(
            Offset(w * 0.12f, h * 0.08f),
            Offset(w * 0.25f, h * 0.14f),
            Offset(w * 0.45f, h * 0.06f),
            Offset(w * 0.62f, h * 0.12f),
            Offset(w * 0.88f, h * 0.09f),
            Offset(w * 0.35f, h * 0.22f),
            Offset(w * 0.80f, h * 0.20f)
        )
        starPositions.forEach { pos ->
            drawCircle(Color.White.copy(alpha = 0.65f), radius = 1.2.dp.toPx(), center = pos)
        }

        // 3. Camada 1 de Montanhas (Distantes - Facetadas em tons púrpura-crepúsculo)
        val distantMountains = Path().apply {
            moveTo(0f, h * 0.58f)
            lineTo(w * 0.15f, h * 0.42f)
            lineTo(w * 0.32f, h * 0.52f)
            lineTo(w * 0.48f, h * 0.35f) // Pico mais alto ao fundo
            lineTo(w * 0.65f, h * 0.48f)
            lineTo(w * 0.82f, h * 0.38f)
            lineTo(w, h * 0.55f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            distantMountains,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065)),
                startY = h * 0.35f,
                endY = h
            )
        )

        // Facetas iluminadas das montanhas distantes (lado iluminado pelo sol à direita)
        val distantLightFacets = Path().apply {
            moveTo(w * 0.48f, h * 0.35f)
            lineTo(w * 0.65f, h * 0.48f)
            lineTo(w * 0.58f, h * 0.52f)
            close()
        }
        drawPath(distantLightFacets, Color(0xFFC084FC).copy(alpha = 0.28f))

        // 4. Camada 2 de Montanhas (Médias - Picos agudos e facetas contrastantes)
        val midMountains = Path().apply {
            moveTo(0f, h * 0.68f)
            lineTo(w * 0.08f, h * 0.50f)
            lineTo(w * 0.22f, h * 0.62f)
            lineTo(w * 0.38f, h * 0.45f)
            lineTo(w * 0.54f, h * 0.60f)
            lineTo(w * 0.72f, h * 0.43f)
            lineTo(w * 0.88f, h * 0.58f)
            lineTo(w, h * 0.48f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            midMountains,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF24153E), Color(0xFF150C28)),
                startY = h * 0.43f,
                endY = h
            )
        )

        // Arestas iluminadas em degradê lilás/rosa
        val ridgeLine1 = Path().apply {
            moveTo(w * 0.38f, h * 0.45f)
            lineTo(w * 0.42f, h * 0.58f)
            lineTo(w * 0.45f, h * 0.68f)
        }
        drawPath(ridgeLine1, Color(0xFFE9D5FF).copy(alpha = 0.35f), style = Stroke(width = 1.5.dp.toPx()))

        val ridgeLine2 = Path().apply {
            moveTo(w * 0.72f, h * 0.43f)
            lineTo(w * 0.76f, h * 0.56f)
            lineTo(w * 0.80f, h * 0.68f)
        }
        drawPath(ridgeLine2, Color(0xFFE9D5FF).copy(alpha = 0.4f), style = Stroke(width = 1.5.dp.toPx()))

        // 5. Camada 3 de Montanhas (Primeiro plano - Silhueta escura e árvores coníferas)
        val foregroundHills = Path().apply {
            moveTo(0f, h * 0.75f)
            lineTo(w * 0.28f, h * 0.68f)
            lineTo(w * 0.62f, h * 0.76f)
            lineTo(w, h * 0.65f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(foregroundHills, Color(0xFF0F0A1C))

        // Silhueta estilizada de pinheiros na base
        val pineXs = listOf(
            0.04f, 0.08f, 0.12f, 0.16f, 0.22f, 0.26f, 0.78f, 0.82f, 0.86f, 0.90f, 0.94f
        )
        pineXs.forEach { ratio ->
            val px = w * ratio
            val py = h * 0.74f
            val tree = Path().apply {
                moveTo(px, py - 12.dp.toPx())
                lineTo(px + 4.dp.toPx(), py)
                lineTo(px - 4.dp.toPx(), py)
                close()
            }
            drawPath(tree, Color(0xFF090514))
        }

        // Névoa sutil na base da montanha
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color(0xFF15111F).copy(alpha = 0.85f)),
                startY = h * 0.75f,
                endY = h
            )
        )
    }
}

// ==========================================
// 4. Baú do Tesouro Mágico (Canvas Vetorial)
// ==========================================

@Composable
fun TreasureChestGraphic(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "chest_glow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val sx = w / 100f
            val sy = h / 100f
            val center = Offset(w / 2f, h / 2f)

            // 1. Aura mística roxa/azul pulsante
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFA855F7).copy(alpha = pulseAlpha * 0.65f),
                        Color(0xFF6366F1).copy(alpha = pulseAlpha * 0.35f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = w * 0.48f
                ),
                radius = w * 0.48f,
                center = center
            )

            // Feixes de luz mágica saindo do baú
            val beam1 = Path().apply {
                moveTo(38f * sx, 42f * sy)
                lineTo(18f * sx, 15f * sy)
                lineTo(28f * sx, 12f * sy)
                close()
            }
            drawPath(beam1, Color(0xFFC084FC).copy(alpha = pulseAlpha * 0.35f))

            val beam2 = Path().apply {
                moveTo(62f * sx, 42f * sy)
                lineTo(82f * sx, 15f * sy)
                lineTo(72f * sx, 12f * sy)
                close()
            }
            drawPath(beam2, Color(0xFF818CF8).copy(alpha = pulseAlpha * 0.35f))

            // 2. Base do Baú (Madeira escura rica)
            val chestBase = Path().apply {
                moveTo(18f * sx, 46f * sy)
                lineTo(82f * sx, 46f * sy)
                lineTo(80f * sx, 84f * sy)
                lineTo(20f * sx, 84f * sy)
                close()
            }
            drawPath(
                chestBase,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF3B1842), Color(0xFF1E0E24)),
                    startY = 46f * sy,
                    endY = 84f * sy
                )
            )

            // 3. Tampa Curva do Baú
            val chestLid = Path().apply {
                moveTo(16f * sx, 46f * sy)
                cubicTo(16f * sx, 28f * sy, 84f * sx, 28f * sy, 84f * sx, 46f * sy)
                close()
            }
            drawPath(
                chestLid,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF581C87), Color(0xFF2E1065)),
                    startY = 28f * sy,
                    endY = 46f * sy
                )
            )

            // Destaque de luz mágica na fresta entre a tampa e a base
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF7C3AED),
                        Color(0xFFE9D5FF),
                        Color(0xFF67E8F9),
                        Color(0xFFE9D5FF),
                        Color(0xFF7C3AED)
                    )
                ),
                start = Offset(16f * sx, 46f * sy),
                end = Offset(84f * sx, 46f * sy),
                strokeWidth = 3.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // 4. Ferragens e Cintas Metálicas (Dourado/Bronze e Ferro)
            // Cinta Esquerda
            drawRoundRect(
                color = Color(0xFFD97706),
                topLeft = Offset(28f * sx, 31f * sy),
                size = Size(8f * sx, 53f * sy),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f * sx, 2f * sy)
            )
            // Cinta Direita
            drawRoundRect(
                color = Color(0xFFB45309),
                topLeft = Offset(64f * sx, 31f * sy),
                size = Size(8f * sx, 53f * sy),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f * sx, 2f * sy)
            )

            // Rebites metálicos
            val rivets = listOf(
                Offset(32f * sx, 35f * sy),
                Offset(32f * sx, 52f * sy),
                Offset(32f * sx, 78f * sy),
                Offset(68f * sx, 35f * sy),
                Offset(68f * sx, 52f * sy),
                Offset(68f * sx, 78f * sy)
            )
            rivets.forEach { r ->
                drawCircle(Color(0xFFFDE68A), radius = 1.4f * sx, center = r)
            }

            // 5. Fechadura e Placa Central de Ouro
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFCD34D), Color(0xFFB45309))
                ),
                topLeft = Offset(43f * sx, 42f * sy),
                size = Size(14f * sx, 18f * sy),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f * sx, 3f * sy)
            )

            // Buraco da fechadura
            drawCircle(Color(0xFF1E0E24), radius = 2.2f * sx, center = Offset(50f * sx, 49f * sy))
            val keyholeBase = Path().apply {
                moveTo(48.5f * sx, 49f * sy)
                lineTo(51.5f * sx, 49f * sy)
                lineTo(52.5f * sx, 55f * sy)
                lineTo(47.5f * sx, 55f * sy)
                close()
            }
            drawPath(keyholeBase, Color(0xFF1E0E24))

            // Partículas mágicas flutuantes
            val particles = listOf(
                Offset(18f * sx, 24f * sy),
                Offset(30f * sx, 16f * sy),
                Offset(50f * sx, 10f * sy),
                Offset(74f * sx, 18f * sy),
                Offset(84f * sx, 26f * sy)
            )
            particles.forEach { p ->
                drawCircle(Color(0xFFDDD6FE).copy(alpha = pulseAlpha), radius = 1.6f * sx, center = p)
            }
        }
    }
}

// ==========================================
// 5. Ícones Semânticos Padronizados
// ==========================================

@Composable
fun FlameIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val sx = w / 100f
        val sy = h / 100f

        // Chama externa laranja
        val flameOuter = Path().apply {
            moveTo(50f * sx, 10f * sy)
            cubicTo(65f * sx, 25f * sy, 85f * sx, 45f * sy, 85f * sx, 65f * sy)
            cubicTo(85f * sx, 84f * sy, 69f * sx, 95f * sy, 50f * sx, 95f * sy)
            cubicTo(31f * sx, 95f * sy, 15f * sx, 84f * sy, 15f * sx, 65f * sy)
            cubicTo(15f * sx, 45f * sy, 35f * sx, 25f * sy, 50f * sx, 10f * sy)
            close()
        }
        drawPath(
            flameOuter,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFF97316), Color(0xFFEA580C))
            )
        )

        // Chama interna amarela
        val flameInner = Path().apply {
            moveTo(50f * sx, 38f * sy)
            cubicTo(60f * sx, 48f * sy, 72f * sx, 60f * sy, 72f * sx, 72f * sy)
            cubicTo(72f * sx, 84f * sy, 62f * sx, 90f * sy, 50f * sx, 90f * sy)
            cubicTo(38f * sx, 90f * sy, 28f * sx, 84f * sy, 28f * sx, 72f * sy)
            cubicTo(28f * sx, 60f * sy, 40f * sx, 48f * sy, 50f * sx, 38f * sy)
            close()
        }
        drawPath(
            flameInner,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFDE047), Color(0xFFF59E0B))
            )
        )
    }
}

@Composable
fun TrophyIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val sx = w / 100f
        val sy = h / 100f

        // Taça
        val cup = Path().apply {
            moveTo(24f * sx, 16f * sy)
            lineTo(76f * sx, 16f * sy)
            lineTo(70f * sx, 52f * sy)
            cubicTo(68f * sx, 66f * sy, 58f * sx, 72f * sy, 50f * sx, 72f * sy)
            cubicTo(42f * sx, 72f * sy, 32f * sx, 66f * sy, 30f * sx, 52f * sy)
            close()
        }
        drawPath(
            cup,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFBBF24), Color(0xFFD97706))
            )
        )

        // Alça esquerda
        val leftHandle = Path().apply {
            moveTo(26f * sx, 24f * sy)
            cubicTo(10f * sx, 24f * sy, 10f * sx, 48f * sy, 28f * sx, 48f * sy)
        }
        drawPath(leftHandle, Color(0xFFFBBF24), style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))

        // Alça direita
        val rightHandle = Path().apply {
            moveTo(74f * sx, 24f * sy)
            cubicTo(90f * sx, 24f * sy, 90f * sx, 48f * sy, 72f * sx, 48f * sy)
        }
        drawPath(rightHandle, Color(0xFFFBBF24), style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))

        // Haste
        drawRect(Color(0xFFB45309), topLeft = Offset(46f * sx, 72f * sy), size = Size(8f * sx, 12f * sy))

        // Base
        drawRoundRect(
            color = Color(0xFFF59E0B),
            topLeft = Offset(30f * sx, 84f * sy),
            size = Size(40f * sx, 8f * sy),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f * sx, 3f * sy)
        )
    }
}

@Composable
fun TrendingChartIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val sx = w / 100f
        val sy = h / 100f

        // Barras crescentes verdes
        val barColor = Color(0xFF10B981)

        // Barra 1
        drawRoundRect(
            color = barColor.copy(alpha = 0.65f),
            topLeft = Offset(16f * sx, 60f * sy),
            size = Size(14f * sx, 30f * sy),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f * sx, 3f * sy)
        )
        // Barra 2
        drawRoundRect(
            color = barColor.copy(alpha = 0.85f),
            topLeft = Offset(36f * sx, 42f * sy),
            size = Size(14f * sx, 48f * sy),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f * sx, 3f * sy)
        )
        // Barra 3
        drawRoundRect(
            color = barColor,
            topLeft = Offset(56f * sx, 24f * sy),
            size = Size(14f * sx, 66f * sy),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f * sx, 3f * sy)
        )

        // Linha com seta ascendente
        val arrow = Path().apply {
            moveTo(18f * sx, 52f * sy)
            lineTo(38f * sx, 36f * sy)
            lineTo(60f * sx, 18f * sy)
            lineTo(82f * sx, 12f * sy)
        }
        drawPath(arrow, Color(0xFF34D399), style = Stroke(width = 2.5f * sx, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Ponta da seta
        val arrowHead = Path().apply {
            moveTo(82f * sx, 12f * sy)
            lineTo(72f * sx, 14f * sy)
            moveTo(82f * sx, 12f * sy)
            lineTo(80f * sx, 22f * sy)
        }
        drawPath(arrowHead, Color(0xFF34D399), style = Stroke(width = 2.5f * sx, cap = StrokeCap.Round))
    }
}

@Composable
fun StarIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val cy = h / 2f
        val outerRadius = w * 0.46f
        val innerRadius = w * 0.22f

        val star = Path().apply {
            for (i in 0 until 10) {
                val r = if (i % 2 == 0) outerRadius else innerRadius
                val angle = (i * 36 - 90) * Math.PI / 180.0
                val x = (cx + r * Math.cos(angle)).toFloat()
                val y = (cy + r * Math.sin(angle)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }

        drawPath(
            star,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFC084FC), Color(0xFF7C3AED))
            )
        )
    }
}
