package com.tolongtukar.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Ice Blue + Charcoal - Premium techy dashboard aesthetic
private val LightColors = lightColorScheme(
    primary = Color(0xFF0EA5E9),           // Cyan-blue (vibrant)
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE), // Light cyan container
    onPrimaryContainer = Color(0xFF075985),
    secondary = Color(0xFF06B6D4),         // Bright cyan accent
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = Color(0xFF164E63),
    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFBAE6FD),
    onTertiaryContainer = Color(0xFF0C4A6E),
    background = Color(0xFFF8FAFC),        // Soft slate background
    onBackground = Color(0xFF0F172A),      // Deep navy text
    surface = Color.White,                 // Pure white cards
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),    // Subtle slate variant
    onSurfaceVariant = Color(0xFF475569),  // Medium slate text
    error = Color(0xFFDC2626),
    onError = Color.White,
    outline = Color(0xFFCBD5E1)            // Soft slate border
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF0EA5E9),           // Same vibrant cyan (pops on dark)
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF075985),  // Deep cyan container
    onPrimaryContainer = Color(0xFF7DD3FC),
    secondary = Color(0xFF06B6D4),         // Bright cyan
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF164E63),
    onSecondaryContainer = Color(0xFF67E8F9),
    tertiary = Color(0xFF38BDF8),
    onTertiary = Color(0xFF0F172A),
    tertiaryContainer = Color(0xFF0C4A6E),
    onTertiaryContainer = Color(0xFFBAE6FD),
    background = Color(0xFF0F172A),        // Deep navy background
    onBackground = Color(0xFFF1F5F9),      // Light slate text
    surface = Color(0xFF1E293B),           // Dark slate cards
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF334155),    // Medium slate variant
    onSurfaceVariant = Color(0xFFCBD5E1), // Light slate secondary text
    error = Color(0xFFEF4444),
    onError = Color.White,
    outline = Color(0xFF475569)            // Darker border for dark mode
)

@Composable
fun TolongTukarTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
