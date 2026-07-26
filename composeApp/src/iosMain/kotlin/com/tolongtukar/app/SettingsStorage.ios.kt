package com.tolongtukar.app

import platform.Foundation.NSUserDefaults

actual class SettingsStorage {
    private val defaults = NSUserDefaults.standardUserDefaults()

    actual fun getString(key: String, default: String): String =
        defaults.stringForKey(key) ?: default

    actual fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }

    actual fun getBoolean(key: String, default: Boolean): Boolean {
        val value = defaults.objectForKey(key) as? Boolean
        return value ?: default
    }

    actual fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, forKey = key)
    }
}
