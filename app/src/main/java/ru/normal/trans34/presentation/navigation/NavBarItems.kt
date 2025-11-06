package ru.normal.trans34.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import ru.normal.trans34.R


@Composable
fun navBarItems(): List<BarItem> = listOf(
    BarItem(
        title = stringResource(R.string.schedule),
        filledIcon = ImageVector.vectorResource(R.drawable.schedule_24px),
        outlinedIcon = Icons.Rounded.Schedule,
        route = NavRoutes.Schedule.route
    ),
    BarItem(
        title = stringResource(R.string.map),
        filledIcon = Icons.Filled.Map,
        outlinedIcon = Icons.Outlined.Map,
        route = NavRoutes.Map.route
    )
)