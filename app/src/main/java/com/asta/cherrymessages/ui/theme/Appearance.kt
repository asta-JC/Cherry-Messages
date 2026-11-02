package com.asta.cherrymessages.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberAppearance(): State<String> {
    val context = LocalContext.current
    val preferences = remember(context) { context.getSharedPreferences("appearance", Context.MODE_PRIVATE) }
    val mode = remember { mutableStateOf(preferences.getString("theme", "system") ?: "system") }
    DisposableEffect(preferences) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == "theme") mode.value = prefs.getString("theme", "system") ?: "system"
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    return mode
}

fun setAppearance(context: Context, mode: String) {
    context.getSharedPreferences("appearance", Context.MODE_PRIVATE).edit().putString("theme", mode).apply()
}
