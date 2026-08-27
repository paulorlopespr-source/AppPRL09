package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ==========================================
// PALETA OFICIAL - JWHA PURPLE FITNESS
// ==========================================
val PurpleDarkest = Color(0xFF10002B)  // Fundo principal (roxo extremamente escuro)
val PurpleDarkSurface = Color(0xFF240046)  // Superfície secundária (roxo escuro)
val PurpleDeepCard = Color(0xFF3C096C)  // Superfície de cards (roxo profundo)
val PurplePrimaryDark = Color(0xFF5A189A)  // Roxo principal escuro
val PurplePrimary = Color(0xFF7B2CBF)  // Roxo principal
val PurpleVibrant = Color(0xFF9D4EDD)  // Roxo vibrante (ações e elementos ativos)
val LilacAccent = Color(0xFFC77DFF)  // Lilás (destaques e brilho)
val LilacSoft = Color(0xFFE0AAFF)  // Lilás claro (destaques suaves e legendas)

// Superfícies e Vidro (Liquid Glass)
val GlassSurfaceDark = Color(0xD9240046)
val GlassSurfaceDeep = Color(0xCC3C096C)
val GlassBorder = Color(0x40C77DFF)
val GlassBorderSubtle = Color(0x20E0AAFF)
val GlowPurple = Color(0x669D4EDD)

// Status & Feedback com harmonia roxa
val EmeraldSuccess = Color(0xFF10B981)
val EmeraldDark = Color(0xFF059669)
val EmeraldSubtle = Color(0xFF064E3B)
val AmberWarning = Color(0xFFF59E0B)
val AmberSubtle = Color(0xFF78350F)
val RedDestructive = Color(0xFFEF4444)
val RedSubtle = Color(0xFF7F1D1D)

// Tipografia & Contrastes
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFE0AAFF).copy(alpha = 0.85f)
val TextMuted = Color(0xFFCBD5E1).copy(alpha = 0.65f)
val TextDark = Color(0xFF10002B)

// Gradientes Oficiais
val GradientHeroPrimary = Brush.horizontalGradient(
    colors = listOf(PurplePrimaryDark, PurplePrimary, PurpleVibrant)
)

val GradientAction = Brush.horizontalGradient(
    colors = listOf(PurplePrimary, LilacAccent)
)

val GradientVibrant = Brush.horizontalGradient(
    colors = listOf(PurpleVibrant, LilacAccent, LilacSoft)
)

val GradientCardDeep = Brush.verticalGradient(
    colors = listOf(PurpleDarkSurface, PurpleDeepCard)
)

val GradientGlassCard = Brush.verticalGradient(
    colors = listOf(PurpleDarkSurface.copy(alpha = 0.90f), PurpleDeepCard.copy(alpha = 0.75f))
)

// Legacy Aliases for backwards compatibility
val RoyalBlue = PurpleVibrant
val RoyalBlueDark = PurplePrimaryDark
val RoyalBlueLight = LilacAccent
val RoyalBlueSubtle = PurpleDeepCard.copy(alpha = 0.4f)
val OrangePrimary = PurpleVibrant
val OrangeHover = PurplePrimary
val OrangeLight = LilacAccent
val BackgroundLight = PurpleDarkest
val BackgroundDark = PurpleDarkest
val SurfaceCard = PurpleDarkSurface
val SurfaceDark = PurpleDarkSurface
val SurfaceSubtle = PurpleDeepCard
val SurfaceSubtleDark = PurpleDeepCard
val BorderSubtle = GlassBorderSubtle
val BorderDark = GlassBorder
val BorderMedium = GlassBorder
val TextPrimaryLight = TextPrimary
val TextSecondaryLight = TextSecondary
val TextMutedLight = TextMuted
val TextPrimaryDark = TextPrimary
val TextSecondaryDark = TextSecondary
val TextMutedDark = TextMuted
val CyanAccent = LilacAccent
val CyanDark = PurplePrimary
val CyanLight = LilacSoft
val ElectricBlue = LilacAccent
val LimeSuccess = EmeraldSuccess
val SlateDark = PurpleDarkest
val SlateSurface = PurpleDarkSurface
val SlateSurfaceVariant = PurpleDeepCard
val SlateBorder = GlassBorder
val SlateLight = PurpleDarkSurface
val SlateSurfaceLight = PurpleDarkSurface
val SlateSurfaceVariantLight = PurpleDeepCard
val SlateBorderLight = GlassBorderSubtle


