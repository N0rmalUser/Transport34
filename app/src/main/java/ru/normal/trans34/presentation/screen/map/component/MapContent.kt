package ru.normal.trans34.presentation.screen.map.component

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import ru.normal.trans34.R
import ru.normal.trans34.presentation.screen.map.MapIntent
import ru.normal.trans34.presentation.screen.map.MapViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapContent(
    mapView: MapView
) {
    val viewModel: MapViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    var selectedIndex by remember {
        mutableIntStateOf(
            when {
                state.showSavedRoutes -> 1
                state.showUnits -> 2
                else -> 0
            }
        )
    }

    val userLocationLayerState = remember { mutableStateOf<UserLocationLayer?>(null) }
    val currentLayer by rememberUpdatedState(userLocationLayerState.value)

    val locationPermission = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(locationPermission.status.isGranted, mapView) {
        if (!locationPermission.status.isGranted) {
            currentLayer?.isVisible = false
            return@LaunchedEffect
        }
        val layer =
            currentLayer ?: MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)
                .also {
                    userLocationLayerState.value = it
                }

        try {
            layer.setDefaultSource()
            layer.isVisible = true
            layer.isHeadingModeActive = true
//          TODO: Сделать иконки для местоположения
//            layer.setObjectListener(object : UserLocationObjectListener {
//                val bitmap = bitmapFromVector(context, R.drawable.qwe, 100, Color.Red.toArgb())
//                val icon = ImageProvider.fromBitmap(bitmap)
//                override fun onObjectAdded(view: UserLocationView) {
//                    view.pin.setIcon(icon)
//                    view.arrow.setIcon(icon)
//                    view.accuracyCircle.fillColor = 0x3300AEEF
//                }
//
//                override fun onObjectRemoved(view: UserLocationView) {}
//                override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {}
//            })
        } catch (e: Exception) {
            Log.w("MapContent", "Failed to enable user location layer", e)
        }
    }

    DisposableEffect(mapView) {
        onDispose {
            userLocationLayerState.value?.isVisible = false
            userLocationLayerState.value = null
        }
    }

    val options = listOf(
        stringResource(R.string.nothing),
        stringResource(R.string.my_transport),
        stringResource(R.string.all_transport)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView }, modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    LocationButton(
                        onLocationAvailable = {
                            val layer = userLocationLayerState.value ?: return@LocationButton
                            val target = layer.cameraPosition()?.target ?: return@LocationButton
                            val currentCamera = mapView.mapWindow.map.cameraPosition

                            val newCamera = CameraPosition(
                                target, 15.5f, currentCamera.azimuth, currentCamera.tilt
                            )

                            mapView.mapWindow.map.move(
                                newCamera, Animation(Animation.Type.SMOOTH, 0.7f), null
                            )
                        })
                }
            }

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                        index = index, count = options.size
                    ), colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(
                            alpha = 0.8f
                        ),
                        inactiveContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    ), onClick = {
                        selectedIndex = index
                        viewModel.handleIntent(
                            MapIntent.SetVisibility(
                                showSavedRoutes = index == 1, showUnits = index == 2
                            )
                        )
                    }, selected = index == selectedIndex, label = { Text(label) })
                }
            }
        }
    }
}
