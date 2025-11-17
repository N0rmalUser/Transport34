package ru.normal.trans34.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.normal.trans34.presentation.model.MapVisibilityMode

interface SettingsRepository {
    suspend fun saveVisibilityMode(mode: MapVisibilityMode)
    fun visibilityModeFlow(): Flow<MapVisibilityMode>
    suspend fun getVisibilityMode(): MapVisibilityMode
}
