package ru.normal.trans34.presentation.screen.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.normal.trans34.domain.entity.MapBorders
import ru.normal.trans34.domain.entity.SavedStop
import ru.normal.trans34.domain.entity.StopPoint
import ru.normal.trans34.domain.usecase.AddSavedStopUseCase
import ru.normal.trans34.domain.usecase.CheckIsStopSavedUseCase
import ru.normal.trans34.domain.usecase.GetStopArrivalsUseCase
import ru.normal.trans34.domain.usecase.GetStopsOnMapUseCase
import ru.normal.trans34.domain.usecase.RemoveStopUseCase
import ru.normal.trans34.presentation.computeMinutesUntil
import ru.normal.trans34.presentation.mapTransportType
import ru.normal.trans34.presentation.model.RouteUiModel
import ru.normal.trans34.presentation.model.StopPointUiModel
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getStopsOnMapUseCase: GetStopsOnMapUseCase,
    private val removeStopUseCase: RemoveStopUseCase,
    private val getStopArrivalsUseCase: GetStopArrivalsUseCase,
    private val addSavedStopUseCase: AddSavedStopUseCase,
    private val checkIsStopSavedUseCase: CheckIsStopSavedUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(
        MapState(
            CameraPosition(Point(48.7038, 44.55815), 10f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 2f)
        )
    )
    val state: StateFlow<MapState> = _state.asStateFlow()

    private val _savedStops = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val savedStops: StateFlow<Map<Int, Boolean>> = _savedStops.asStateFlow()

    fun handleIntent(intent: MapIntent) {
        when (intent) {
            is MapIntent.LoadStops -> {
                val center = intent.center
                val visible = intent.visibleRegion

                val latHeight = abs(visible.topLeft.latitude - visible.bottomLeft.latitude)
                val delta = latHeight * 2

                val borders = MapBorders(
                    minLatitude = center.latitude - delta,
                    maxLatitude = center.latitude + delta,
                    minLongitude = center.longitude - delta,
                    maxLongitude = center.longitude + delta
                )
                loadStops(borders)
            }

            is MapIntent.DismissBottomSheet -> dismissBottomSheet()
            is MapIntent.SelectStop -> selectStop(intent.stop)
            is MapIntent.ToggleStop -> toggleStop(intent.stop)
        }
    }

    fun toggleStop(stop: StopPointUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val isSaved = savedStops.value[stop.id] ?: false
            if (isSaved) {
                removeStopUseCase(stop.id)
            } else {
                addSavedStopUseCase(
                    SavedStop(
                        id = stop.id,
                        tabId = 0,
                        destinationRu = stop.destination,
                        destinationEn = stop.destination
                    )
                )
            }
        }
    }

    private fun selectStop(stop: StopPointUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val arrivals = getStopArrivalsUseCase(stop.id)
                val list = arrivals.map { route ->
                    val currentLocale = context.resources.configuration.locales[0].language
                    val destination =
                        if (currentLocale == "ru") route.destinationRu else route.destinationEn

                    RouteUiModel(
                        routeId = route.id,
                        routeNumber = route.routeNumber,
                        destination = destination,
                        arrivalTime = route.arrivalTime,
                        minutesUntilArrival = computeMinutesUntil(route.arrivalTime),
                        transportType = mapTransportType(route)
                    )
                }
                _state.update {
                    it.copy(
                        selectedStop = stop,
                        routesByStop = _state.value.routesByStop + (stop.id to list),
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Loading error"
                )
            }
        }
    }

    private fun dismissBottomSheet() {
        _state.update {
            it.copy(
                selectedStop = null, routesByStop = it.routesByStop - (it.selectedStop?.id ?: 0)
            )
        }
    }

    fun loadStops(borders: MapBorders) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val stopsList: List<StopPoint> = getStopsOnMapUseCase(borders)

                val list = stopsList.map { stop ->
                    val currentLocale = context.resources.configuration.locales[0].language
                    val destination =
                        if (currentLocale == "ru") stop.destinationRu else stop.destinationEn

                    StopPointUiModel(
                        id = stop.id,
                        point = Point(stop.latitude, stop.longitude),
                        destination = destination
                    ).also { stopUi ->
                        viewModelScope.launch(Dispatchers.IO) {
                            checkIsStopSavedUseCase(stop.id).collect { isSaved ->
                                _savedStops.update { currentMap ->
                                    currentMap + (stop.id to isSaved)
                                }
                            }
                        }
                    }
                }

                _state.update { it.copy(stops = list, error = null) }

            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Loading error") }
            }
        }
    }

}
