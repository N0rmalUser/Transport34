package ru.normal.trans34.presentation.screen.schedule

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import ru.normal.trans34.domain.usecase.GetSavedStopsUseCase
import ru.normal.trans34.domain.usecase.GetStopArrivalsUseCase
import ru.normal.trans34.domain.usecase.RemoveStopUseCase
import ru.normal.trans34.presentation.computeMinutesUntil
import ru.normal.trans34.presentation.mapTransportType
import ru.normal.trans34.presentation.model.UnitCardUiModel
import ru.normal.trans34.presentation.model.StopScheduleUiModel
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val getStopArrivalsUseCase: GetStopArrivalsUseCase,
    private val getSavedStopsUseCase: GetSavedStopsUseCase,
    private val removeStopUseCase: RemoveStopUseCase,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(ScheduleState())
    val state: StateFlow<ScheduleState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val currentLocale = context.resources.configuration.locales[0].language
            getSavedStopsUseCase().collect { savedStops ->
                val list = savedStops.map {
                    val destination =
                        if (currentLocale == "ru") it.destinationRu else it.destinationEn

                    StopScheduleUiModel(
                        id = it.id,
                        tabId = it.tabId,
                        destination = destination
                    )
                }
                _state.value = _state.value.copy(
                    stops = list,
                    isInitialLoading = false
                )
                _state.value.selectedStop.let { loadArrivals(it) }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(20_000)
                val stop = _state.value.selectedStop
                loadArrivals(stop)
            }
        }
    }

    fun handleIntent(intent: ScheduleIntent) {
        when (intent) {
            is ScheduleIntent.LoadData -> loadArrivals(intent.stopId, intent.indicator)
            is ScheduleIntent.SelectStop -> selectStop(intent.stopId)
            is ScheduleIntent.Refresh -> refresh()
            is ScheduleIntent.DelStop -> delStop(intent.stopId)
        }
    }

    private fun delStop(stopId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            removeStopUseCase(stopId)
        }
    }


    private fun selectStop(stopId: Int) {
        val hasCache = _state.value.routesByStop[stopId]?.isNotEmpty() == true
        _state.value = _state.value.copy(selectedStop = stopId, error = null)
        if (!hasCache) loadArrivals(stopId)
    }

    private fun refresh() {
        _state.value.selectedStop.let { loadArrivals(it) }
    }

    private fun loadArrivals(stopId: Int, indicator: Boolean = false) {
        if (indicator) {
            _state.value = _state.value.copy(
                loadingStops = _state.value.loadingStops + stopId
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            delay(700)
            try {
                val arrivals = getStopArrivalsUseCase(stopId)
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

                _state.value = _state.value.copy(
                    routesByStop = _state.value.routesByStop + (stopId to list),
                    selectedStop = stopId,
                    isInitialLoading = false,
                    loadingStops = _state.value.loadingStops - stopId,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loadingStops = _state.value.loadingStops - stopId,
                    error = e.message ?: "Ошибка загрузки"
                )
            }
        }
    }
}
