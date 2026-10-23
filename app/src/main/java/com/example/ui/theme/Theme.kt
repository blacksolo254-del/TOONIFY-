package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ToonifyViolet,
    onPrimary = Color(0xFF1E004B),
    primaryContainer = ToonifyVioletDark,
    onPrimaryContainer = Color(0xFFEDE7F6),
    secondary = ToonifyCyan,
    onSecondary = Color(0xFF00363D),
    secondaryContainer = Color(0xFF004D5A),
    onSecondaryContainer = Color(0xFFE0F7FA),
    tertiary = ToonifyPink,
    onTertiary = Color(0xFF4A001E),
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkOutline,
    surfaceContainer = DarkSurfaceElevated,
    surfaceContainerHigh = DarkSurfaceCard
)

@Composable
fun ToonifyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

// Alias for template compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    ToonifyTheme(content = content)
}
