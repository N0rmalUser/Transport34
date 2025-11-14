package ru.normal.trans34.data.local.stops

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedStopsDao {

    @Query("SELECT * FROM saved_stops")
    fun getAllStops(): Flow<List<SavedStopEntity>>

    @Query("SELECT * FROM saved_stops WHERE tabNumber = :tab")
    fun getStopsByTab(tab: Int): Flow<List<SavedStopEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_stops WHERE stopId = :stopId)")
    fun isStopSaved(stopId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertStop(stop: SavedStopEntity)

    @Delete
    suspend fun deleteStop(stop: SavedStopEntity)

    @Query("DELETE FROM saved_stops WHERE stopId = :stopId")
    suspend fun deleteById(stopId: Int)
}