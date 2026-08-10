package com.tolongtukar.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * TolongTukar design language:
 * - White-first, premium-clean utility look
 * - Solid colors + 1px borders + tints for depth (NO gradients)
 * - Accents pulled from logo: Navy #1B3A5C, Orange #E8892B
 */

// Brand accents (shared across themes)
val Navy = Color(0xFF1B3A5C)
val NavySoft = Color(0xFF2C4F73)
val Orange = Color(0xFFE8892B)
val OrangeSoft = Color(0xFFF5A54E)

// Light palette
val LightBackground = Color(0xFFFAFAF8)
val LightSurface = Color(0xFFFFFFFF)
val LightBorder = Color(0xFFE8E6E1)
val LightText = Color(0xFF1A1C1A)
val LightTextMuted = Color(0xFF5F6660)

// Dark palette
val DarkBackground = Color(0xFF12181F)
val DarkSurface = Color(0xFF1A2230)
val DarkBorder = Color(0xFF2C3A4D)
val DarkText = Color(0xFFECEEE9)
val DarkTextMuted = Color(0xFF9AA5B1)

private val LightColors = lightColorScheme(
    primary = Navy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE4EDF6),
    onPrimaryContainer = Navy,
    secondary = Orange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFDEBD7),
    onSecondaryContainer = Color(0xFF7A4710),
    tertiary = NavySoft,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE4EDF6),
    onTertiaryContainer = Navy,
    background = LightBackground,
    onBackground = LightText,
    surface = LightSurface,
    onSurface = LightText,
    surfaceVariant = Color(0xFFF2F1ED),
    onSurfaceVariant = LightTextMuted,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    outline = LightBorder,
    outlineVariant = LightBorder
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9FC3E8),
    onPrimary = Color(0xFF0E2438),
    primaryContainer = Color(0xFF253B54),
    onPrimaryContainer = Color(0xFFD3E4F6),
    secondary = OrangeSoft,
    onSecondary = Color(0xFF4A2A05),
    secondaryContainer = Color(0xFF5C3A12),
    onSecondaryContainer = Color(0xFFFDEBD7),
    tertiary = Color(0xFF9FC3E8),
    onTertiary = Color(0xFF0E2438),
    tertiaryContainer = Color(0xFF253B54),
    onTertiaryContainer = Color(0xFFD3E4F6),
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = Color(0xFF222C3B),
    onSurfaceVariant = DarkTextMuted,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    outline = DarkBorder,
    outlineVariant = DarkBorder
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
