package ru.normal.trans34.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun saveShowUnits(show: Boolean)
    fun showUnitsFlow(): Flow<Boolean>
    suspend fun getShowUnits(): Boolean
}
