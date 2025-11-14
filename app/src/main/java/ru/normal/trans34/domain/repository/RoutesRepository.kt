package ru.normal.trans34.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.normal.trans34.domain.entity.SavedRoute

interface RoutesRepository {
    fun getSavedRoutes(): Flow<List<SavedRoute>>
    fun isRouteSaved(id: String): Flow<Boolean>
    suspend fun saveRoute(route: SavedRoute)
    suspend fun removeRoute(id: String)
}
