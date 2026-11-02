package com.asta.cherrymessages.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val Light = lightColorScheme(
 primary=Color(0xFFAF294A), onPrimary=Color.White, primaryContainer=Color(0xFFF9DEE5), onPrimaryContainer=Color(0xFF652039),
 secondary=Color(0xFF78616B), onSecondary=Color.White, secondaryContainer=Color(0xFFF1E7E7), onSecondaryContainer=Color(0xFF422731),
 tertiary=Color(0xFF526C52), onTertiary=Color.White, tertiaryContainer=Color(0xFFD5E8D0), onTertiaryContainer=Color(0xFF263D26),
 background=Color(0xFFFCF8F5), onBackground=Color(0xFF422731), surface=Color(0xFFFFFCFA), onSurface=Color(0xFF422731),
 surfaceVariant=Color(0xFFF1E7E7), onSurfaceVariant=Color(0xFF78616B), outline=Color(0xFF927780), outlineVariant=Color(0xFFE9DDDF),
 surfaceContainerLowest=Color(0xFFFFFCFA), surfaceContainerLow=Color(0xFFFCF8F5), surfaceContainer=Color(0xFFF6EEEE), surfaceContainerHigh=Color(0xFFF1E7E7), surfaceContainerHighest=Color(0xFFE9DDDF)
)
private val Dark = darkColorScheme(
 primary=Color(0xFFFF9DB5), onPrimary=Color(0xFF3B1528), primaryContainer=Color(0xFF502A3B), onPrimaryContainer=Color(0xFFFFD9E3),
 secondary=Color(0xFFCAB6C0), onSecondary=Color(0xFF392A36), secondaryContainer=Color(0xFF392A36), onSecondaryContainer=Color(0xFFF9E8EE),
 tertiary=Color(0xFFB7D0AC), onTertiary=Color(0xFF263D26), tertiaryContainer=Color(0xFF384C35), onTertiaryContainer=Color(0xFFD5E8D0),
 background=Color(0xFF1C151D), onBackground=Color(0xFFF9E8EE), surface=Color(0xFF261D27), onSurface=Color(0xFFF9E8EE),
 surfaceVariant=Color(0xFF392A36), onSurfaceVariant=Color(0xFFCAB6C0), outline=Color(0xFF9B828F), outlineVariant=Color(0xFF4B3845),
 surfaceContainerLowest=Color(0xFF171119), surfaceContainerLow=Color(0xFF211923), surfaceContainer=Color(0xFF261D27), surfaceContainerHigh=Color(0xFF302430), surfaceContainerHighest=Color(0xFF392A36)
)
@Composable
fun CherryMessagesTheme(darkTheme: Boolean? = null, dynamicColor: Boolean = false, content: @Composable () -> Unit) {
 val appearance by rememberAppearance()
 val resolvedDark = darkTheme ?: when (appearance) { "dark" -> true; "light" -> false; else -> isSystemInDarkTheme() }
 val view = LocalView.current
 SideEffect {
  (view.context as? Activity)?.window?.let {
   WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !resolvedDark
   WindowCompat.getInsetsController(it, view).isAppearanceLightNavigationBars = !resolvedDark
  }
 }
 MaterialTheme(colorScheme=if(resolvedDark) Dark else Light, typography=Typography, content=content)
}
