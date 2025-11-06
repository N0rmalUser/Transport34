package ru.normal.trans34

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.normal.trans34.presentation.navigation.NavRoutes
import ru.normal.trans34.presentation.screen.main.MainScreen
import ru.normal.trans34.presentation.theme.AppTheme

@AndroidEntryPoint
class MainActivity() : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val navController = rememberNavController()

                val screenTransitionMillis = 1000
                NavHost(
                    navController = navController,
                    startDestination = NavRoutes.Main.route,
                    modifier = Modifier.Companion.fillMaxSize(),

                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up, tween(
                                screenTransitionMillis
                            )
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up, tween(
                                screenTransitionMillis
                            )
                        )
                    },
                ) {
                    composable(NavRoutes.Main.route) { MainScreen() }
                }
            }
        }
    }
}