package ru.normal.trans34.domain.usecase

import jakarta.inject.Inject
import ru.normal.trans34.domain.entity.Stop
import ru.normal.trans34.domain.repository.TransportRepository


class GetUnitArrivalsUseCase  @Inject constructor(
private val repository: TransportRepository
) {
    suspend operator fun invoke(unitId: String): List<Stop> = repository.getUnitArriveList(unitId)
}