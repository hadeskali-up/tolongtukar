package com.tolongtukar.app

import androidx.compose.runtime.*
import com.tolongtukar.app.navigation.Screen
import com.tolongtukar.app.screens.ConverterScreen
import com.tolongtukar.app.screens.HomeScreen
import com.tolongtukar.app.theme.TolongTukarTheme
import com.tolongtukar.app.util.BackHandler

@Composable
fun App() {
    TolongTukarTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
        val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }

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
                onNavigate = { screen -> navigateTo(screen) }
            )
            is Screen.Converter -> {
                val category = (currentScreen as Screen.Converter).category
                ConverterScreen(
                    category = category,
                    onBack = { goBack() }
                )
            }
        }
    }
}
