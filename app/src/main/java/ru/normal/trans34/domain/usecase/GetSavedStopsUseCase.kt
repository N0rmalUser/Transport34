package ru.normal.trans34.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.normal.trans34.domain.entity.SavedStop
import ru.normal.trans34.domain.repository.StopsRepository
import javax.inject.Inject

class GetSavedStopsUseCase @Inject constructor(
    private val repository: StopsRepository
) {
    suspend operator fun invoke(): Flow<List<SavedStop>> = repository.getSavedStops()
}