package ru.normal.trans34.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.normal.trans34.data.local.routes.SavedRouteEntity
import ru.normal.trans34.data.local.routes.SavedRoutesDao
import ru.normal.trans34.domain.entity.SavedRoute
import ru.normal.trans34.domain.repository.RoutesRepository
import javax.inject.Inject

class RoutesRepositoryImpl @Inject constructor(
    private val savedRoutesDao: SavedRoutesDao
) : RoutesRepository {
    override fun getSavedRoutes(): Flow<List<SavedRoute>> {
        return savedRoutesDao.getAllSavedRoute().map { routeEntities ->
            Log.d("getAllSavedRoute", routeEntities.toString())
                routeEntities.map { entity ->
                    Log.d("getAllSavedRoute", entity.toString())
                    SavedRoute(
                        id = entity.id, title = entity.title
                    )
                }
            }
    }
    override suspend fun saveRoute(route: SavedRoute) {
        Log.d("saveRoute", route.toString())
        savedRoutesDao.insertSavedRoute(
            SavedRouteEntity(
                id = route.id, title = route.title
            )
        )
    }
    override fun isRouteSaved(id: String): Flow<Boolean> = savedRoutesDao.isSavedRouteExists(id)
    override suspend fun removeRoute(id: String) = savedRoutesDao.deleteSavedRoute(id)
}