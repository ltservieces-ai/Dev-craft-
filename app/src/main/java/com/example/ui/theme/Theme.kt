package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = IdeAccentPeach,
    onPrimary = Color(0xFF28180E),
    primaryContainer = IdeDarkSurfaceVariant,
    onPrimaryContainer = IdeAccentPeach,
    secondary = IdeAccentGreen,
    onSecondary = Color(0xFF00391D),
    secondaryContainer = Color(0xFF1B3828),
    onSecondaryContainer = Color(0xFF8CF3B4),
    tertiary = IdeAccentCyan,
    onTertiary = Color(0xFF00363F),
    background = IdeDarkBackground,
    onBackground = IdeTextPrimary,
    surface = IdeDarkSurface,
    onSurface = IdeTextPrimary,
    surfaceVariant = IdeDarkSurfaceVariant,
    onSurfaceVariant = IdeTextSecondary,
    outline = IdeDarkOutline,
    outlineVariant = Color(0xFF2E2C37),
    error = IdeAccentRed,
    onError = Color(0xFF410002)
)

private val LightColorScheme = lightColorScheme(
    primary = IdeAccentPeachDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBC8),
    onPrimaryContainer = Color(0xFF321300),
    secondary = Color(0xFF1E824C),
    onSecondary = Color.White,
    tertiary = Color(0xFF0284C7),
    background = Color(0xFFF9F9FB),
    onBackground = Color(0xFF1A1A1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1E),
    surfaceVariant = Color(0xFFEFEFF3),
    onSurfaceVariant = Color(0xFF4A4952),
    outline = Color(0xFFCCCAD6),
    error = Color(0xFFDC2626)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to deep IDE dark theme matching screenshot
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
