package com.formpic.app.ui.theme

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
    primary = NavyDeep,
    onPrimary = SurfaceLight,
    primaryContainer = BlueSoftBg,
    onPrimaryContainer = NavyDeep,
    secondary = BlueAccent,
    onSecondary = SurfaceLight,
    secondaryContainer = BlueSoftBg,
    onSecondaryContainer = BlueAccent,
    background = BackgroundLight,
    onBackground = SlateTextPrimary,
    surface = SurfaceLight,
    onSurface = SlateTextPrimary,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = BlueAccent,
    onPrimary = SurfaceLight,
    primaryContainer = NavyLight,
    onPrimaryContainer = DarkTextPrimary,
    secondary = BlueAccent,
    onSecondary = SurfaceLight,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder
)

@Composable
fun FormPicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FormPicTypography,
        content = content
    )
}
