package ru.normal.trans34.presentation.screen.map

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yandex.mapkit.ConflictResolutionMode
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.delay
import ru.normal.trans34.R
import ru.normal.trans34.presentation.model.StopPointUiModel
import ru.normal.trans34.presentation.model.TransportType
import ru.normal.trans34.presentation.model.UnitPointUiModel
import ru.normal.trans34.presentation.screen.map.component.StopScheduleBottomSheetContent
import ru.normal.trans34.presentation.screen.map.utils.animatePlacemarkMove
import ru.normal.trans34.presentation.screen.map.utils.bitmapFromMipmap

private const val MIN_ZOOM_TO_SHOW = 14f

@Composable
fun MapScreen() {
    val viewModel: MapViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        while (true) {
            viewModel.handleIntent(MapIntent.RefreshUnits)
            delay(15_000)
        }
    }

    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val isDarkTheme = isSystemInDarkTheme()
    val currentTheme by rememberUpdatedState(isDarkTheme)

    LaunchedEffect(currentTheme) {
        mapView.mapWindow.map.isNightModeEnabled = currentTheme
    }


    LaunchedEffect(state.position) {
        mapView.mapWindow.map.move(
            state.position, state.animation, null
        )
    }

    val rootCollection = mapView.mapWindow.map.mapObjects
    val stopObjects = remember { rootCollection.addCollection() }
    val unitObjects = remember { rootCollection.addCollection() }
    rootCollection.conflictResolutionMode = ConflictResolutionMode.MAJOR

    val currentZoom = remember { mutableFloatStateOf(mapView.mapWindow.map.cameraPosition.zoom) }

    val stopPlacemarks = remember { mutableStateMapOf<Int, PlacemarkMapObject>() }
    val stopTapListener = remember {
        MapObjectTapListener { p0, p1 ->
            val s = p0.userData as? StopPointUiModel
            if (s != null) {
                Log.d("MapTap", "Tap on stop: ${s.id}")
                viewModel.handleIntent(MapIntent.SelectStop(s))
            }
            true
        }
    }


    val unitPlacemarks = remember { mutableStateMapOf<String, PlacemarkMapObject>() }
    val unitTapListener = remember {
        MapObjectTapListener { obj, _ ->
            (obj.userData as? UnitPointUiModel)?.let {
                viewModel.handleIntent(MapIntent.SelectUnit(it))
            }
            true
        }
    }

    LaunchedEffect(state.stops, currentZoom.floatValue) {
        delay(100)
        if (!mapView.isAttachedToWindow) return@LaunchedEffect

        if (currentZoom.floatValue < MIN_ZOOM_TO_SHOW) {
            stopPlacemarks.values.forEach { it.isVisible = false }
            return@LaunchedEffect
        }

        val busBitmap = bitmapFromMipmap(context, R.mipmap.ic_stop)
        val icon = ImageProvider.fromBitmap(busBitmap)

        val stopsIds = state.stops?.map { it.id } ?: emptyList()

        val toRemove = stopPlacemarks.keys - stopsIds
        toRemove.forEach {
            stopPlacemarks[it]?.let { placemark -> stopObjects.remove(placemark) }
            stopPlacemarks.remove(it)
        }

        state.stops?.forEach { stop ->
            val placemark = stopObjects.addPlacemark().apply {
                geometry = stop.point
                setIcon(icon)
                userData = stop
            }
            placemark.addTapListener(stopTapListener)
            stopPlacemarks[stop.id] = placemark
        }
    }

    LaunchedEffect(state.units, currentZoom.floatValue) {
        delay(100)
        if (!mapView.isAttachedToWindow) return@LaunchedEffect

        if (currentZoom.floatValue < MIN_ZOOM_TO_SHOW) {
            unitPlacemarks.values.forEach { it.isVisible = false }
            return@LaunchedEffect
        } else {
            unitPlacemarks.values.forEach { it.isVisible = true }
        }

        val unitIds = state.units?.map { it.id } ?: emptyList()

        val toRemove = unitPlacemarks.keys - unitIds
        toRemove.forEach {
            unitPlacemarks[it]?.let { placemark -> unitObjects.remove(placemark) }
            unitPlacemarks.remove(it)
        }

        val iconCache = mutableMapOf<TransportType, ImageProvider>()

        state.units?.forEach { unit ->
            val icon = iconCache.getOrPut(unit.transportType) {
                val res = when (unit.transportType) {
                    TransportType.BUS -> R.drawable.ic_bus
                    TransportType.TROLLEY -> R.drawable.ic_bus
                    TransportType.TRAM -> R.drawable.ic_bus
                    TransportType.ELECTROBUS -> R.drawable.ic_bus
                    TransportType.OTHER -> R.drawable.ic_bus
                }
                val bitmap = bitmapFromMipmap(context, res)
                ImageProvider.fromBitmap(bitmap)
            }

            val existing = unitPlacemarks[unit.id]
            if (existing != null) {
                val start = existing.geometry
                val end = unit.point

                if (start.latitude != end.latitude || start.longitude != end.longitude) {
                    animatePlacemarkMove(existing, start, end)
                }

                existing.direction = unit.azimuth.toFloatOrNull() ?: 0f
                existing.userData = unit
            } else {
                val placemark = unitObjects.addPlacemark().apply {
                    userData = unit
                    geometry = unit.point
                    setIcon(icon)
                    setIconStyle(
                        IconStyle().apply {
                            rotationType = RotationType.ROTATE
                            anchor = android.graphics.PointF()
                            zIndex = 2.0f
                        }
                    )
                    direction = unit.azimuth.toFloatOrNull() ?: 0f
                    addTapListener(unitTapListener)
                }
                unitPlacemarks[unit.id] = placemark
            }
        }
    }



    DisposableEffect(mapView) {
        MapKitFactory.getInstance().onStart()
        mapView.onStart()

        val cameraListener = CameraListener { map, cameraPosition, _, finished ->
            currentZoom.floatValue = cameraPosition.zoom
            if (finished && cameraPosition.zoom >= MIN_ZOOM_TO_SHOW) {
                viewModel.handleIntent(
                    MapIntent.LoadStops(
                        center = cameraPosition.target, visibleRegion = map.visibleRegion
                    )
                )
            }
        }
        mapView.mapWindow.map.addCameraListener(cameraListener)
        val inputListener = object : InputListener {
            override fun onMapTap(map: com.yandex.mapkit.map.Map, point: Point) {
                Log.d("MapTap", "Tap on map at $point")
            }

            override fun onMapLongTap(map: com.yandex.mapkit.map.Map, point: Point) {}
        }
        mapView.mapWindow.map.addInputListener(inputListener)

        onDispose {
            mapView.mapWindow.map.removeCameraListener(cameraListener)
            mapView.mapWindow.map.removeInputListener(inputListener)
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }

    AndroidView(factory = { mapView })


    val sheetState = rememberModalBottomSheetState()
    val savedStops by viewModel.savedStops.collectAsState()

    LaunchedEffect(state.selectedStop) {
        if (state.selectedStop != null) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    if (state.selectedStop != null) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { viewModel.handleIntent(MapIntent.DismissBottomSheet) },
            modifier = Modifier.fillMaxHeight()
        ) {
            state.selectedStop?.let { selectedStop ->
                StopScheduleBottomSheetContent(
                    stop = selectedStop,
                    timetable = state.routesByStop[selectedStop.id] ?: emptyList(),
                    isSaved = savedStops[selectedStop.id] ?: false,
                    onSaveStop = { stop -> viewModel.handleIntent(MapIntent.ToggleStop(stop)) })
            }
        }
    }
}
