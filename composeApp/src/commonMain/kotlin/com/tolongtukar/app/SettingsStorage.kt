package com.tolongtukar.app

/**
 * Cross-platform key-value settings storage.
 * Android: SharedPreferences. iOS: NSUserDefaults.
 */
expect class SettingsStorage() {
    fun getString(key: String, default: String = ""): String
    fun putString(key: String, value: String)
    fun getBoolean(key: String, default: Boolean = false): Boolean
    fun putBoolean(key: String, value: Boolean)
}

/**
 * App-wide settings keys.
 */
object SettingsKeys {
    const val DARK_MODE = "dark_mode"
    const val UNIT_ORDER_PREFIX = "unit_order_"
    const val CATEGORY_ORDER = "category_order"
}
