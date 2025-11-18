package ru.normal.trans34.presentation.screen.main

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.normal.trans34.presentation.navigation.NavRoutes
import ru.normal.trans34.presentation.navigation.navBarItems
import ru.normal.trans34.presentation.screen.map.MapScreen
import ru.normal.trans34.presentation.screen.schedule.ScheduleScreen


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route

            NavigationBar {
                navBarItems().forEach { navItem ->
                    val selected = currentRoute == navItem.route

                    NavigationBarItem(selected = selected, onClick = {
                        navController.navigate(navItem.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }, icon = {
                        Icon(
                            imageVector = if (selected) navItem.filledIcon else navItem.outlinedIcon,
                            contentDescription = navItem.title,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }, label = {
                        Text(text = navItem.title)
                    })
                }
            }
        }) { innerPadding ->
        val screenTransitionMillis = 500
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Schedule.route,
            modifier = Modifier
                .padding(
                    bottom = innerPadding.calculateBottomPadding()
                )
                .fillMaxSize(),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(
                        screenTransitionMillis
                    )
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(
                        screenTransitionMillis
                    )
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(
                        screenTransitionMillis
                    )
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(
                        screenTransitionMillis
                    )
                )
            }) {
            composable(NavRoutes.Schedule.route) { ScheduleScreen() }
            composable(NavRoutes.Map.route) { MapScreen() }
        }
    }
}