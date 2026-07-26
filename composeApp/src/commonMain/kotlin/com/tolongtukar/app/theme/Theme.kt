package com.tolongtukar.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF006B5F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF74F8E4),
    onPrimaryContainer = Color(0xFF00201B),
    secondary = Color(0xFF4A635B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCDE8DC),
    onSecondaryContainer = Color(0xFF072018),
    tertiary = Color(0xFF3E6373),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC1E8FB),
    onTertiaryContainer = Color(0xFF001F29),
    background = Color(0xFFF8FBF9),
    onBackground = Color(0xFF191C1B),
    surface = Color(0xFFFAFCFB),
    onSurface = Color(0xFF191C1B),
    surfaceVariant = Color(0xFFDAE5E0),
    onSurfaceVariant = Color(0xFF3F4945),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    outline = Color(0xFF707974)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF54DBC8),
    onPrimary = Color(0xFF003730),
    primaryContainer = Color(0xFF005047),
    onPrimaryContainer = Color(0xFF74F8E4),
    secondary = Color(0xFFB1CCBF),
    onSecondary = Color(0xFF1C352C),
    secondaryContainer = Color(0xFF334B42),
    onSecondaryContainer = Color(0xFFCDE8DC),
    tertiary = Color(0xFFA5CDE0),
    onTertiary = Color(0xFF063544),
    tertiaryContainer = Color(0xFF254B5B),
    onTertiaryContainer = Color(0xFFC1E8FB),
    background = Color(0xFF0E1513),
    onBackground = Color(0xFFE0E3E0),
    surface = Color(0xFF111816),
    onSurface = Color(0xFFE0E3E0),
    surfaceVariant = Color(0xFF3F4945),
    onSurfaceVariant = Color(0xFFBFC9C3),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    outline = Color(0xFF8A938E)
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
