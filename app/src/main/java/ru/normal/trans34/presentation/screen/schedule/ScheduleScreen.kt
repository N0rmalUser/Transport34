package ru.normal.trans34.presentation.screen.schedule

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.normal.trans34.R
import ru.normal.trans34.presentation.screen.schedule.component.RouteList

@Composable
fun ScheduleScreen() {
    val viewModel: ScheduleViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    state.error?.let { errorMsg ->
        AlertDialog(
            onDismissRequest = { viewModel.handleIntent(ScheduleIntent.Refresh) },
            confirmButton = {
                Button(onClick = { viewModel.handleIntent(ScheduleIntent.Refresh) }) {
                    Text(stringResource(R.string.dialog_retry))
                }
            },
            dismissButton = {
                Button(onClick = {
                    viewModel.handleIntent(ScheduleIntent.SelectStop(state.selectedStop))
                }) {
                    Text(stringResource(R.string.dialog_close))
                }
            },
            title = { Text(stringResource(R.string.dialog_error_title)) },
            text = { Text(errorMsg) })
    }

    if (state.isInitialLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingIndicator()
        }
        return
    }

    if (state.error != null && state.stops.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(state.error ?: stringResource(R.string.error_generic))
        }
        return
    }

    if (state.stops.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.no_stops_saved),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .animateContentSize()
            )
        }
        return
    }

    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val pullState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = state.stops.indexOfFirst { it.id == state.selectedStop }.coerceAtLeast(0),
        pageCount = { state.stops.size })


    Column(Modifier.fillMaxSize()) {
        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage.coerceIn(
                0, state.stops.lastIndex.coerceAtLeast(0)
            ), containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            state.stops.forEachIndexed { index, stop ->
                Tab(selected = index == pagerState.settledPage, onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }, text = { Text(stop.destination) })
            }
        }

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val stopId = state.stops.getOrNull(page)?.id ?: return@HorizontalPager
            val routesForPage = state.routesByStop[stopId] ?: emptyList()

            LaunchedEffect(lifecycleOwner) {
                lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    coroutineScope.launch {
                        viewModel.handleIntent(
                            ScheduleIntent.LoadData(
                                stopId = stopId, indicator = true
                            )
                        )
                    }
                }
            }
            if (state.isStopLoading(stopId)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            } else {
                Box(
                    Modifier
                        .fillMaxSize()
                        .pullToRefresh(
                            state = pullState, isRefreshing = isRefreshing, onRefresh = {
                                isRefreshing = true
                                coroutineScope.launch {
                                    viewModel.handleIntent(ScheduleIntent.LoadData(stopId))
                                    delay(1_500)
                                    isRefreshing = false
                                }
                            })
                ) {
                    RouteList(routesForPage, modifier = Modifier.fillMaxSize())

                    Box(
                        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                    ) {
                        PullToRefreshDefaults.LoadingIndicator(
                            state = pullState,
                            isRefreshing = isRefreshing,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
