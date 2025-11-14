package ru.normal.trans34.presentation.screen.map

import android.graphics.PointF
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yandex.mapkit.ConflictResolutionMode
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.normal.trans34.R
import ru.normal.trans34.presentation.model.StopPointUiModel
import ru.normal.trans34.presentation.model.UnitPointUiModel
import ru.normal.trans34.presentation.screen.map.component.MapContent
import ru.normal.trans34.presentation.screen.map.component.StopBottomSheetContent
import ru.normal.trans34.presentation.screen.map.component.UnitBottomSheetContent
import ru.normal.trans34.presentation.screen.map.utils.animatePlacemarkMove
import ru.normal.trans34.presentation.screen.map.utils.bitmapFromVector
import ru.normal.trans34.presentation.screen.map.utils.moveCameraToPoint
import kotlin.math.abs

private const val MIN_ZOOM_TO_SHOW = 13f

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
        MapObjectTapListener { obj, _ ->
            (obj.userData as? StopPointUiModel)?.let {
                viewModel.handleIntent(MapIntent.SelectStop(it))
                moveCameraToPoint(
                    it.point, mapView
                )
            }
            true
        }
    }

    LaunchedEffect(state.stops, currentZoom.floatValue) {
        if (!mapView.isAttachedToWindow) return@LaunchedEffect
        delay(100)

        val stops = state.stops ?: emptyList()

        if (currentZoom.floatValue < MIN_ZOOM_TO_SHOW + 1) {
            stopPlacemarks.values.forEach { it.isVisible = false }
            return@LaunchedEffect
        } else {
            stopPlacemarks.values.forEach { it.isVisible = true }
        }

        val bitmap = bitmapFromVector(
            context = context, drawableRes = R.drawable.ic_bus_stop
        )
        val icon = ImageProvider.fromBitmap(bitmap)

        val toRemove = stopPlacemarks.keys - stops.map { it.id }
        toRemove.forEach {
            stopPlacemarks[it]?.let { placemark -> stopObjects.remove(placemark) }
            stopPlacemarks.remove(it)
        }

        stops.forEach { stop ->
            val existing = stopPlacemarks[stop.id]
            if (existing == null || !existing.isValid) {
                val placemark = stopObjects.addPlacemark().apply {
                    geometry = stop.point
                    setIcon(icon)
                    userData = stop
                    addTapListener(stopTapListener)
                    isVisible = currentZoom.floatValue >= MIN_ZOOM_TO_SHOW
                }
                stopPlacemarks[stop.id] = placemark
            }
        }
    }


    val unitPlacemarks = remember { mutableStateMapOf<String, PlacemarkMapObject>() }
    val unitTapListener = remember {
        MapObjectTapListener { obj, _ ->
            (obj.userData as? UnitPointUiModel)?.let {
                viewModel.handleIntent(MapIntent.SelectUnit(it))
                moveCameraToPoint(
                    it.point, mapView
                )
            }
            true
        }
    }

    LaunchedEffect(state.units, currentZoom.floatValue, state.showUnits) {
        if (!mapView.isAttachedToWindow) return@LaunchedEffect
        delay(100)

        if (currentZoom.floatValue < MIN_ZOOM_TO_SHOW || !state.showUnits) {
            unitPlacemarks.values.forEach { it.isVisible = false }
            return@LaunchedEffect
        } else {
            unitPlacemarks.values.forEach { it.isVisible = true }
        }

        val currentUnitIds = state.units?.map { it.id }?.toSet() ?: emptySet()
        val toRemove = unitPlacemarks.keys - currentUnitIds
        toRemove.forEach {
            unitPlacemarks[it]?.let { placemark -> unitObjects.remove(placemark) }
            unitPlacemarks.remove(it)
        }

        coroutineScope {
            state.units?.forEach { unit ->
                val end = unit.point
                val existing = unitPlacemarks[unit.id]

                if (existing != null && existing.isValid) {
                    existing.userData = unit

                    val newDirection = unit.azimuth.toFloatOrNull() ?: 0f
                    if (abs(existing.direction - newDirection) > 0.001f) {
                        existing.direction = newDirection
                    }

                    val currentPoint = existing.geometry
                    if (abs(currentPoint.latitude - end.latitude) > 1e-7 || abs(currentPoint.longitude - end.longitude) > 1e-7) {
                        launch {
                            animatePlacemarkMove(existing, currentPoint, end)
                        }
                    }
                } else {
                    val bitmap = bitmapFromVector(
                        context = context,
                        drawableRes = R.drawable.ic_transport_marker,
                        size = 160,
                        tintColor = unit.transportType.color.toArgb()
                    )
                    val icon = ImageProvider.fromBitmap(bitmap)

                    val placemark = unitObjects.addPlacemark().apply {
                        geometry = end
                        userData = unit
                        setIcon(icon)
                        setIconStyle(
                            IconStyle().apply {
                                rotationType = RotationType.ROTATE
                                anchor = PointF(0.5f, 0.5f)
                                zIndex = 2.0f
                            })
                        setText(
                            unit.routeNumber, TextStyle().apply {
                                size = 8f
                                placement = TextStyle.Placement.CENTER
                                offset = 5f
                                color = Color.White.toArgb()
                                zIndex = 3.0f
                            })
                        direction = unit.azimuth.toFloatOrNull() ?: 0f
                        addTapListener(unitTapListener)
                    }
                    unitPlacemarks[unit.id] = placemark
                }
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
                    MapIntent.LoadData(
                        center = cameraPosition.target, visibleRegion = map.visibleRegion
                    )
                )
            }
        }
        mapView.mapWindow.map.addCameraListener(cameraListener)
        val inputListener = object : InputListener {
            override fun onMapTap(map: Map, point: Point) {
                Log.d("MapTap", "Tap on map at $point")
            }

            override fun onMapLongTap(map: Map, point: Point) {}
        }
        mapView.mapWindow.map.addInputListener(inputListener)

        onDispose {
            mapView.mapWindow.map.removeCameraListener(cameraListener)
            mapView.mapWindow.map.removeInputListener(inputListener)
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }


    val userLocationLayerState = remember { mutableStateOf<UserLocationLayer?>(null) }

    DisposableEffect(mapView) {
        val layer = MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)
        layer.isVisible = true
        layer.isHeadingModeActive = true

//        TODO: Сделать иконки для местоположения
//        layer.setObjectListener(object : UserLocationObjectListener {
//            val bitmap = bitmapFromVector(context, R.drawable.qwe, 100, Color.Red.toArgb())
//            val icon = ImageProvider.fromBitmap(bitmap)
//            override fun onObjectAdded(view: UserLocationView) {
//                view.pin.setIcon(icon)
//                view.arrow.setIcon(icon)
//                view.accuracyCircle.fillColor = 0x3300AEEF
//            }

//            override fun onObjectRemoved(view: UserLocationView) {}
//            override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {}
//        }
//    )
        userLocationLayerState.value = layer
        onDispose {
            layer.isVisible = false
        }
    }


    MapContent(
        mapView,
        userLocationLayerState
    )


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
            modifier = Modifier.fillMaxHeight(),
        ) {
            state.selectedStop?.let { selectedStop ->
                StopBottomSheetContent(
                    stop = selectedStop,
                    timetable = state.routesByStop[selectedStop.id] ?: emptyList(),
                    isSaved = savedStops[selectedStop.id] ?: false,
                    onSaveStop = { stop -> viewModel.handleIntent(MapIntent.ToggleStop(stop)) })
            }
        }
    }

    val savedRoutes by viewModel.savedRoutes.collectAsState()
    if (state.selectedUnit != null) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { viewModel.handleIntent(MapIntent.DismissBottomSheet) },
            modifier = Modifier.fillMaxHeight(),
        ) {
            state.selectedUnit?.let { selectedUnit ->
                Log.e("savedRoutes", savedRoutes.toString())
                UnitBottomSheetContent(
                    unit = selectedUnit,
                    timetable = state.stopsByUnit[selectedUnit.id] ?: emptyList(),
                    isSaved = savedRoutes[selectedUnit.routeNumber] ?: false,
                    onSaveRoute = { unit -> viewModel.handleIntent(MapIntent.ToggleUnit(unit)) })
            }
        }
    }
}
