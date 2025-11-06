package ru.normal.trans34.domain.repository

import ru.normal.trans34.domain.entity.Route
import ru.normal.trans34.domain.entity.MapBorders
import ru.normal.trans34.domain.entity.StopPoint

interface TransportRepository {
    suspend fun getStops(mapBorders: MapBorders): List<StopPoint>
    suspend fun getStopArriveList(stopId: Int): List<Route>
}