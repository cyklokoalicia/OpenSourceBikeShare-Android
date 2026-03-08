package com.bikeshare.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Green700,
    onPrimary = Gray50,
    primaryContainer = GreenLight,
    onPrimaryContainer = Green900,
    secondary = Blue500,
    onSecondary = Gray50,
    secondaryContainer = Blue700,
    background = Gray50,
    onBackground = Gray900,
    surface = Gray50,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    error = Red500,
)

private val DarkColorScheme = darkColorScheme(
    primary = Green500,
    onPrimary = Gray900,
    primaryContainer = Green900,
    onPrimaryContainer = GreenLight,
    secondary = Blue500,
    onSecondary = Gray900,
    background = Gray900,
    onBackground = Gray50,
    surface = Gray800,
    onSurface = Gray50,
    error = Red500,
)

@Composable
fun BikeShareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
