package ru.normal.trans34.domain.repository

import ru.normal.trans34.domain.entity.Route
import ru.normal.trans34.domain.entity.MapBorders
import ru.normal.trans34.domain.entity.StopPoint
import ru.normal.trans34.domain.entity.UnitPoint

interface TransportRepository {
    suspend fun getUnits(mapBorders: MapBorders): List<UnitPoint>
    suspend fun getStops(mapBorders: MapBorders): List<StopPoint>
    suspend fun getStopArriveList(stopId: Int): List<Route>
}