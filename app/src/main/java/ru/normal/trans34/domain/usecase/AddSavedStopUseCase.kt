package ru.normal.trans34.domain.usecase

import ru.normal.trans34.domain.entity.SavedStop
import ru.normal.trans34.domain.repository.StopsRepository
import javax.inject.Inject

class AddSavedStopUseCase  @Inject constructor(
    private val repository: StopsRepository
) {
    suspend operator fun invoke(stop: SavedStop): Unit = repository.addStop(stop)
}