package ru.normal.trans34.domain.usecase

import jakarta.inject.Inject
import ru.normal.trans34.domain.entity.Route
import ru.normal.trans34.domain.repository.TransportRepository

class GetStopArrivalsUseCase @Inject constructor(
    private val repository: TransportRepository
) {
    suspend operator fun invoke(stopId: Int): List<Route> = repository.getStopArriveList(stopId)
}