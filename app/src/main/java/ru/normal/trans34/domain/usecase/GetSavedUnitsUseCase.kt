package ru.normal.trans34.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.normal.trans34.domain.entity.SavedRoute
import ru.normal.trans34.domain.repository.RoutesRepository
import javax.inject.Inject

class GetSavedUnitsUseCase @Inject constructor(
    private val repository: RoutesRepository
) {
    suspend operator fun invoke(): Flow<List<SavedRoute>> = repository.getSavedRoutes()
}