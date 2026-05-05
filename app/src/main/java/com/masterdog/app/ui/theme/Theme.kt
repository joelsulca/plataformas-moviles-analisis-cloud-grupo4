package com.masterdog.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary          = Color(0xFF00C9A7),
    onPrimary        = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB2EFE4),
    onPrimaryContainer = Color(0xFF00201A),
    secondary        = Color(0xFF5B2D8E),
    onSecondary      = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEADDFF),
    onSecondaryContainer = Color(0xFF21005D),
    background       = Color(0xFFF0FBF9),
    onBackground     = Color(0xFF1A1C1B),
    surface          = Color(0xFFFFFFFF),
    onSurface        = Color(0xFF1A1C1B),
    surfaceVariant   = Color(0xFFD6F5EF),
    onSurfaceVariant = Color(0xFF3F4947),
    error            = Color(0xFFB00020),
    onError          = Color(0xFFFFFFFF),
    outline          = Color(0xFF6F7976),
)

private val DarkColorScheme = darkColorScheme(
    primary          = Color(0xFF6EDFBF),
    onPrimary        = Color(0xFF003829),
    primaryContainer = Color(0xFF00513C),
    onPrimaryContainer = Color(0xFF8DFBD9),
    secondary        = Color(0xFFD0BCFF),
    onSecondary      = Color(0xFF381E72),
    secondaryContainer = Color(0xFF4F378B),
    onSecondaryContainer = Color(0xFFEADDFF),
    background       = Color(0xFF191C1B),
    onBackground     = Color(0xFFE1E3E1),
    surface          = Color(0xFF191C1B),
    onSurface        = Color(0xFFE1E3E1),
    surfaceVariant   = Color(0xFF3F4947),
    onSurfaceVariant = Color(0xFFBEC9C5),
)

@Composable
fun MasterDogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
