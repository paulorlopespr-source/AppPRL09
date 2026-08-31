package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PurpleVibrant,
    onPrimary = Color.White,
    primaryContainer = PurplePrimaryDark,
    onPrimaryContainer = LilacSoft,
    secondary = LilacAccent,
    onSecondary = PurpleDarkest,
    secondaryContainer = PurpleDeepCard,
    onSecondaryContainer = LilacSoft,
    tertiary = LilacSoft,
    onTertiary = PurpleDarkest,
    background = PurpleDarkest,
    onBackground = TextPrimary,
    surface = PurpleDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = PurpleDeepCard,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = GlassBorderSubtle,
    error = RedDestructive,
    onError = Color.White
)

private val LightColorScheme = DarkColorScheme // Default to dark mode premium

@Composable
fun FitPr09Theme(
    darkTheme: Boolean = true, // Dark mode premium by default
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun FitTreinoTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = FitPr09Theme(darkTheme, dynamicColor, content)


