package com.tolongtukar.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.tolongtukar.app.navigation.Screen
import com.tolongtukar.app.screens.ConverterScreen
import com.tolongtukar.app.screens.HomeScreen
import com.tolongtukar.app.screens.SettingsScreen
import com.tolongtukar.app.screens.SplashScreen
import com.tolongtukar.app.theme.TolongTukarTheme
import com.tolongtukar.app.util.BackHandler

@Composable
fun App() {
    val settings = remember { SettingsStorage() }

    // Splash state — show splash on app launch
    var showSplash by remember { mutableStateOf(true) }

    // Default to light mode ("false"); user can toggle in Settings
    val darkModePref = remember { settings.getString(SettingsKeys.DARK_MODE, "false") }
    val systemDark = isSystemInDarkTheme()
    var darkMode by remember {
        mutableStateOf(
            if (darkModePref == "system") systemDark
            else darkModePref == "true"
        )
    }
    var followSystem by remember { mutableStateOf(darkModePref == "system") }

    val effectiveDark = if (followSystem) systemDark else darkMode

    if (showSplash) {
        SplashScreen(onFinished = { showSplash = false })
        return
    }

    TolongTukarTheme(darkTheme = effectiveDark) {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
        val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }

        // Track Pro status (ads removed)
        var isPro by remember { mutableStateOf(settings.getBoolean(SettingsKeys.IS_PRO, false)) }

        fun navigateTo(screen: Screen) {
            backStack.add(screen)
            currentScreen = screen
        }

        fun goBack() {
            if (backStack.size > 1) {
                backStack.removeLast()
                currentScreen = backStack.last()
            }
        }

        BackHandler(enabled = backStack.size > 1) { goBack() }

        when (currentScreen) {
            is Screen.Home -> HomeScreen(
                onNavigate = { screen -> navigateTo(screen) },
                darkMode = effectiveDark,
                followSystem = followSystem,
                onToggleDarkMode = { dark ->
                    darkMode = dark
                    followSystem = false
                    settings.putString(SettingsKeys.DARK_MODE, if (dark) "true" else "false")
                },
                onToggleFollowSystem = { fs ->
                    followSystem = fs
                    settings.putString(SettingsKeys.DARK_MODE, if (fs) "system" else if (darkMode) "true" else "false")
                },
                settings = settings,
                isPro = isPro
            )
            is Screen.Settings -> SettingsScreen(
                onBack = { goBack() },
                darkMode = effectiveDark,
                followSystem = followSystem,
                onToggleDarkMode = { dark ->
                    darkMode = dark
                    followSystem = false
                    settings.putString(SettingsKeys.DARK_MODE, if (dark) "true" else "false")
                },
                onToggleFollowSystem = { fs ->
                    followSystem = fs
                    settings.putString(SettingsKeys.DARK_MODE, if (fs) "system" else if (darkMode) "true" else "false")
                },
                settings = settings,
                onProStatusChanged = { isPro = settings.getBoolean(SettingsKeys.IS_PRO, false) }
            )
            is Screen.Converter -> {
                val category = (currentScreen as Screen.Converter).category
                ConverterScreen(
                    category = category,
                    onBack = { goBack() },
                    settings = settings
                )
            }
        }
    }
}
