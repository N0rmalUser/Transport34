package ru.normal.trans34.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.normal.trans34.data.local.stops.SavedStopEntity
import ru.normal.trans34.data.local.stops.SavedStopsDao
import ru.normal.trans34.domain.entity.SavedStop
import ru.normal.trans34.domain.repository.StopsRepository
import kotlin.Int

@Singleton
class StopsRepositoryImpl @Inject constructor(
    private val savedStopsDao: SavedStopsDao
) : StopsRepository {
    override fun getSavedStops(): Flow<List<SavedStop>> {
        return savedStopsDao.getAllStops()
            .map { stopEntities ->
                stopEntities.map { entity ->
                    SavedStop(
                        id = entity.stopId,
                        tabId = entity.tabNumber,
                        destinationRu = entity.destinationRu,
                        destinationEn = entity.destinationEn
                    )
                }
            }
    }
    override fun getStopsByTab(tab: Int): Flow<List<SavedStop>> {
        return savedStopsDao.getStopsByTab(tab)
            .map { stopEntities ->
                stopEntities.map { entity ->
                    SavedStop(
                        id = entity.stopId,
                        tabId = entity.tabNumber,
                        destinationRu = entity.destinationRu,
                        destinationEn = entity.destinationEn
                    )
                }
            }
    }
    override fun isStopSaved(stopId: Int): Flow<Boolean> = savedStopsDao.isStopSaved(stopId)
    override suspend fun addStop(stop: SavedStop) {
        savedStopsDao.insertStop(
            SavedStopEntity(
                tabNumber = stop.tabId,
                stopId = stop.id,
                destinationRu = stop.destinationRu,
                destinationEn = stop.destinationEn
            )
        )
    }
    override suspend fun removeStop(stop: SavedStop) {
        savedStopsDao.deleteStop(
            SavedStopEntity(
                tabNumber = stop.tabId,
                stopId = stop.id,
                destinationRu = stop.destinationRu,
                destinationEn = stop.destinationEn
            )
        )
    }
    override suspend fun removeStopById(stopId: Int) = savedStopsDao.deleteById(stopId)
}
