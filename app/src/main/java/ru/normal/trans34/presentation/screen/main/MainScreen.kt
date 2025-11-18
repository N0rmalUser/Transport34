
package ru.normal.trans34.presentation.screen.main

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.normal.trans34.R
import ru.normal.trans34.presentation.navigation.NavRoutes
import ru.normal.trans34.presentation.navigation.navBarItems
import ru.normal.trans34.presentation.screen.map.MapScreen
import ru.normal.trans34.presentation.screen.schedule.ScheduleIntent
import ru.normal.trans34.presentation.screen.schedule.ScheduleScreen
import ru.normal.trans34.presentation.screen.schedule.ScheduleViewModel
import ru.normal.trans34.presentation.screen.main.component.DeleteAlert
import ru.normal.trans34.presentation.screen.main.component.UpdateDialog


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun MainScreen() {
    val viewModel: MainViewModel = hiltViewModel()
    val updateState by viewModel.state.collectAsState()

    val navController = rememberNavController()

    Scaffold(
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val title = when (currentRoute) {
                NavRoutes.Schedule.route -> stringResource(R.string.schedule)
                NavRoutes.Map.route -> stringResource(R.string.map)
                else -> stringResource(R.string.app_name)
            }

            TopAppBar(
                title = {
                    Text(
                        title, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                actions = {
                    if (updateState.available) {
                        IconButton(onClick = { viewModel.openUpdateDialog() }) {
                            Icon(
                                imageVector = Icons.Default.SystemUpdate,
                                contentDescription = stringResource(R.string.update_available),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (updateState.showDialog && updateState.info != null) {
                            UpdateDialog(
                                info = updateState.info!!,
                                onDismiss = { viewModel.dismissUpdateDialog() }
                            )
                        }
                    }

                    if (currentRoute == NavRoutes.Schedule.route) {
                        val scheduleBackStackEntry = navController.getBackStackEntry(NavRoutes.Schedule.route)
                        val scheduleViewModel: ScheduleViewModel = hiltViewModel(scheduleBackStackEntry)
                        val scheduleState by scheduleViewModel.state.collectAsState()
                        var showConfirm by remember { mutableStateOf(false) }

                        if (scheduleState.stops.isNotEmpty()) {
                            IconButton(
                                onClick = { showConfirm = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete_stop),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (showConfirm) {
                            DeleteAlert(
                                onConfirm = {
                                    scheduleState.selectedStop.let { id ->
                                        scheduleViewModel.handleIntent(ScheduleIntent.DelStop(id))
                                    }
                                    showConfirm = false
                                },
                                onDismiss = { showConfirm = false }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route

            NavigationBar {
                navBarItems().forEach { navItem ->
                    val selected = currentRoute == navItem.route

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(navItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) navItem.filledIcon else navItem.outlinedIcon,
                                contentDescription = navItem.title,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        },
                        label = {
                            Text(text = navItem.title)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        val screenTransitionMillis = 500
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Schedule.route,
            modifier = Modifier
                .padding(innerPadding)
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