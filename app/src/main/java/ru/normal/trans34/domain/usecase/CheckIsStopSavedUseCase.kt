package ru.normal.trans34.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.normal.trans34.domain.repository.StopsRepository
import javax.inject.Inject

class CheckIsStopSavedUseCase @Inject constructor(
    private val repository: StopsRepository
) {
    suspend operator fun invoke(stopId: Int): Flow<Boolean> = repository.isStopSaved(stopId)
}