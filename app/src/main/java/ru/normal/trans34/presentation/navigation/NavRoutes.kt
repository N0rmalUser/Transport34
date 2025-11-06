package ru.normal.trans34.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoutes(val route: String) {
    @Serializable
    object Main : NavRoutes("main")

    @Serializable
    object Schedule : NavRoutes("schedule")

    @Serializable
    object Map : NavRoutes("map")
}

