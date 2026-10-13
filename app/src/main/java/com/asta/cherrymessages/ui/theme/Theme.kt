package com.asta.cherrymessages.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CherryDarkColorScheme = darkColorScheme(
    primary = CherryPrimary,
    onPrimary = Color.White,
    primaryContainer = CherryGlow,
    onPrimaryContainer = Color.White,
    secondary = CherryTextSecondary,
    onSecondary = Color.White,
    background = CherryBackground,
    onBackground = CherryTextPrimary,
    surface = CherrySurface,
    onSurface = CherryTextPrimary,
    surfaceVariant = CherrySurfaceVariant,
    onSurfaceVariant = CherryTextSecondary,
    error = CherryError,
    onError = Color.White
)

@Composable
fun CherryMessagesTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = CherryDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
