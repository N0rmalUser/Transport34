package ru.normal.trans34.domain.repository

interface SettingsRepository {
    suspend fun saveShowUnits(show: Boolean)
    suspend fun getShowUnits(): Boolean
}
