package com.example.kmp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightNewsColorScheme = lightColorScheme(
    primary = Color(0xFF6C3BFF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE7DEFF),
    onPrimaryContainer = Color(0xFF22005D),
    secondary = Color(0xFFFF6F61),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDED8),
    onSecondaryContainer = Color(0xFF5B130B),
    tertiary = Color(0xFF14B8A6),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFCCFBF3),
    onTertiaryContainer = Color(0xFF003731),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF1E1A23),
    surface = Color(0xFFFFF8FD),
    onSurface = Color(0xFF1E1A23),
    surfaceVariant = Color(0xFFF2EAF7),
    onSurfaceVariant = Color(0xFF4B4455),
    outline = Color(0xFF7D7488),
    error = Color(0xFFD93F5E),
    onError = Color(0xFFFFFFFF)
)

private val DarkNewsColorScheme = darkColorScheme(
    primary = Color(0xFFCFBCFF),
    onPrimary = Color(0xFF381B8F),
    primaryContainer = Color(0xFF5030C0),
    onPrimaryContainer = Color(0xFFE7DEFF),
    secondary = Color(0xFFFFB3A8),
    onSecondary = Color(0xFF5B130B),
    secondaryContainer = Color(0xFF7A2418),
    onSecondaryContainer = Color(0xFFFFDED8),
    tertiary = Color(0xFF7BE7D8),
    onTertiary = Color(0xFF003731),
    tertiaryContainer = Color(0xFF005149),
    onTertiaryContainer = Color(0xFFCCFBF3),
    background = Color(0xFF141218),
    onBackground = Color(0xFFEAE0F3),
    surface = Color(0xFF141218),
    onSurface = Color(0xFFEAE0F3),
    surfaceVariant = Color(0xFF332D3A),
    onSurfaceVariant = Color(0xFFD0C4D8),
    outline = Color(0xFF9A8FA4),
    error = Color(0xFFFFB3BD),
    onError = Color(0xFF67001F)
)

@Composable
fun NewsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkNewsColorScheme else LightNewsColorScheme,
        content = content
    )
}
