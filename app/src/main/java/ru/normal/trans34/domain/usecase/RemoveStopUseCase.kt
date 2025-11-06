package ru.normal.trans34.domain.usecase

import ru.normal.trans34.domain.repository.StopsRepository
import javax.inject.Inject

class RemoveStopUseCase @Inject constructor(
    private val repository: StopsRepository
) {
    suspend operator fun invoke(stopId: Int): Unit = repository.removeStopById(stopId)
}