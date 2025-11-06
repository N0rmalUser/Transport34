package ru.normal.trans34.domain.usecase

import jakarta.inject.Inject
import ru.normal.trans34.domain.repository.TransportRepository
import ru.normal.trans34.domain.entity.MapBorders
import ru.normal.trans34.domain.entity.StopPoint

class GetStopsOnMapUseCase @Inject constructor(
    private val repository: TransportRepository
) {
    suspend operator fun invoke(
        mapBorders: MapBorders
    ): List<StopPoint> = repository.getStops(mapBorders)
}