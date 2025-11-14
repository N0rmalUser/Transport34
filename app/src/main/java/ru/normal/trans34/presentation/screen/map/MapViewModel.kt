package ru.normal.trans34.presentation.screen.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.VisibleRegion
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.normal.trans34.domain.entity.MapBorders
import ru.normal.trans34.domain.entity.SavedRoute
import ru.normal.trans34.domain.entity.SavedStop
import ru.normal.trans34.domain.entity.StopPoint
import ru.normal.trans34.domain.entity.UnitPoint
import ru.normal.trans34.domain.repository.RoutesRepository
import ru.normal.trans34.domain.repository.SettingsRepository
import ru.normal.trans34.domain.usecase.AddSavedStopUseCase
import ru.normal.trans34.domain.usecase.CheckIsStopSavedUseCase
import ru.normal.trans34.domain.usecase.GetStopArrivalsUseCase
import ru.normal.trans34.domain.usecase.GetStopsOnMapUseCase
import ru.normal.trans34.domain.usecase.GetUnitArrivalsUseCase
import ru.normal.trans34.domain.usecase.GetUnitsOnMapUseCase
import ru.normal.trans34.domain.usecase.RemoveStopUseCase
import ru.normal.trans34.presentation.computeMinutesUntil
import ru.normal.trans34.presentation.mapTransportType
import ru.normal.trans34.presentation.model.StopCardUiModel
import ru.normal.trans34.presentation.model.StopPointUiModel
import ru.normal.trans34.presentation.model.UnitCardUiModel
import ru.normal.trans34.presentation.model.UnitPointUiModel
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getStopsOnMapUseCase: GetStopsOnMapUseCase,
    private val getUnitsOnMapUseCase: GetUnitsOnMapUseCase,
    private val removeStopUseCase: RemoveStopUseCase,
    private val getStopArrivalsUseCase: GetStopArrivalsUseCase,
    private val getUnitArrivalsUseCase: GetUnitArrivalsUseCase,
    private val addSavedStopUseCase: AddSavedStopUseCase,
    private val checkIsStopSavedUseCase: CheckIsStopSavedUseCase,
    private val settingsRepository: SettingsRepository,
    private val routesRepository: RoutesRepository,
    @param:ApplicationContext private val context: Context
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

    private val _savedRoutes = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val savedRoutes: StateFlow<Map<String, Boolean>> = _savedRoutes.asStateFlow()


    private var lastBorders: MapBorders? = null

    init {
        viewModelScope.launch {
            settingsRepository.showUnitsFlow().collect { show ->
                _state.update { current ->
                    current.copy(
                        showUnits = show, units = if (!show) emptyList() else current.units
                    )
                }
                if (show) {
                    lastBorders?.let { refreshUnits(it) }
                }
            }
        }
    }

    fun handleIntent(intent: MapIntent) {
        when (intent) {
            is MapIntent.LoadData -> {
                val borders = calculateBorders(intent.center, intent.visibleRegion)
                lastBorders = borders
                loadData(borders)
            }

            is MapIntent.DismissBottomSheet -> dismissBottomSheet()
            is MapIntent.SelectStop -> selectStop(intent.stop)
            is MapIntent.SelectUnit -> selectUnit(intent.unit)
            is MapIntent.ToggleStop -> toggleStop(intent.stop)
            is MapIntent.RefreshUnits -> {
                lastBorders?.let { borders ->
                    refreshUnits(borders)
                }
            }

            is MapIntent.ToggleUnit -> (toggleUnit(intent.unit))
            is MapIntent.SetVisibility -> setVisibility(intent.showSavedRoutes, intent.showUnits)
        }
    }

    private fun calculateBorders(center: Point, visible: VisibleRegion): MapBorders {
        val latHeight = abs(visible.topLeft.latitude - visible.bottomLeft.latitude)
        val delta = latHeight * 2

        return MapBorders(
            minLatitude = center.latitude - delta,
            maxLatitude = center.latitude + delta,
            minLongitude = center.longitude - delta,
            maxLongitude = center.longitude + delta
        )
    }

    private fun setVisibility(showSavedRoutes: Boolean, showUnits: Boolean) {
        val effectiveShowUnits = showSavedRoutes || showUnits
        _state.update {
            it.copy(
                showSavedRoutes = showSavedRoutes, showUnits = effectiveShowUnits, units = it.units
            )
        }
        viewModelScope.launch {
            settingsRepository.saveShowUnits(effectiveShowUnits)
            if (effectiveShowUnits) {
                lastBorders?.let { refreshUnits(it) }
            }
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

    private fun toggleUnit(unit: UnitPointUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val isSaved = savedRoutes.value[unit.routeNumber] ?: false
            if (isSaved) {
                routesRepository.removeRoute(unit.routeNumber)
                if (state.value.showSavedRoutes) {
                    viewModelScope.launch {
                        lastBorders?.let { refreshUnits(it) }
                    }
                }
            } else {
                routesRepository.saveRoute(
                    SavedRoute(
                        id = unit.routeNumber, title = unit.destination
                    )
                )
            }
        }
    }

    private fun selectStop(stop: StopPointUiModel) {
        _state.update {
            it.copy(
                selectedStop = stop,
                selectedUnit = null,
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val arrivals = getStopArrivalsUseCase(stop.id)
                val list = arrivals.map { route ->
                    val currentLocale = context.resources.configuration.locales[0].language
                    val destination =
                        if (currentLocale == "ru") route.destinationRu else route.destinationEn

                    UnitCardUiModel(
                        routeId = route.id,
                        routeNumber = route.routeNumber,
                        destination = destination,
                        arrivalTime = route.arrivalTime,
                        minutesUntilArrival = computeMinutesUntil(route.arrivalTime),
                        transportType = mapTransportType(route.transportType)
                    )
                }
                _state.update {
                    it.copy(
                        routesByStop = _state.value.routesByStop + (stop.id to list), error = null
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Loading error"
                )
            }
        }
    }

    fun selectUnit(unit: UnitPointUiModel) {
        _state.update {
            it.copy(
                selectedUnit = unit, selectedStop = null
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val arrivals = getUnitArrivalsUseCase(unit.id)
                val list = arrivals.map { unit ->
                    val currentLocale = context.resources.configuration.locales[0].language
                    val title = if (currentLocale == "ru") unit.titleRu else unit.titleEn

                    StopCardUiModel(
                        id = unit.id,
                        arrivalTime = unit.arriveTime,
                        title = title,
                        minutesUntilArrival = computeMinutesUntil(unit.arriveTime)
                    )
                }
                _state.update {
                    it.copy(
                        stopsByUnit = _state.value.stopsByUnit + (unit.id to list), error = null
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
                selectedStop = null,
                selectedUnit = null,
                routesByStop = it.routesByStop - (it.selectedStop?.id ?: 0),
                stopsByUnit = it.stopsByUnit - (it.selectedUnit?.id ?: "")
            )
        }
    }

    fun refreshUnits(borders: MapBorders) {
        if (!_state.value.showUnits) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val unitsList: List<UnitPoint> = getUnitsOnMapUseCase(borders)
                val mapped = unitsList.map { unit ->
                    val currentLocale = context.resources.configuration.locales[0].language
                    val destination =
                        if (currentLocale == "ru") unit.destinationRu else unit.destinationEn

                    UnitPointUiModel(
                        id = unit.id,
                        routeNumber = unit.routeNumber,
                        destination = destination,
                        point = Point(unit.latitude, unit.longitude),
                        azimuth = unit.azimuth,
                        speed = unit.speed,
                        systemTime = unit.systemTime,
                        transportType = mapTransportType(unit.transportType),
                    )
                }
                val filtered = mapped.filter { unitUi ->
                    val isSaved = routesRepository.isRouteSaved(unitUi.routeNumber).first()
                    _savedRoutes.update { currentMap ->
                        currentMap + (unitUi.routeNumber to isSaved)
                    }
                    if (state.value.showSavedRoutes) {
                        isSaved
                    } else true
                }
                _state.update { it.copy(units = filtered, error = null) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "UnitPoint Loading error") }
            }
        }
    }

    fun loadData(borders: MapBorders) {
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
                _state.update { it.copy(error = e.message ?: "StopPoint Loading error") }
            }
        }
        refreshUnits(borders)
    }
}
