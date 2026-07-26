package com.tolongtukar.app.navigation

/**
 * Simple sealed-class navigation without external dependencies.
 * Home shows the category grid; Converter(category) shows the converter for a given category.
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Settings : Screen("settings")

    data class Converter(val category: String) : Screen("converter/$category") {
        companion object {
            const val BASE_ROUTE = "converter"
            fun createRoute(category: String) = "converter/$category"
        }
    }
}
