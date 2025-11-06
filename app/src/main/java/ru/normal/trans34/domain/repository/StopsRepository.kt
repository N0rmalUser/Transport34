package ru.normal.trans34.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.normal.trans34.domain.entity.SavedStop

interface StopsRepository {
    fun getSavedStops(): Flow<List<SavedStop>>
    fun getStopsByTab(tab: Int): Flow<List<SavedStop>>
    fun isStopSaved(stopId: Int): Flow<Boolean>
    suspend fun addStop(stop: SavedStop)
    suspend fun removeStop(stop: SavedStop)
    suspend fun removeStopById(stopId: Int)
}