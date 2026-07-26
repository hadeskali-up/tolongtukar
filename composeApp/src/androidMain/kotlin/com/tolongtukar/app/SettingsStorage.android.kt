package com.tolongtukar.app

import android.content.Context
import androidx.compose.ui.platform.LocalContext

actual class SettingsStorage {
    private val prefs: android.content.SharedPreferences =
        ContextHolder.context.getSharedPreferences("tolongtukar_prefs", Context.MODE_PRIVATE)

    actual fun getString(key: String, default: String): String =
        prefs.getString(key, default) ?: default

    actual fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun getBoolean(key: String, default: Boolean): Boolean =
        prefs.getBoolean(key, default)

    actual fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
}

/**
 * Holds the Android Context for SettingsStorage.
 * Initialized in MainActivity.
 */
object ContextHolder {
    lateinit var context: Context
}
