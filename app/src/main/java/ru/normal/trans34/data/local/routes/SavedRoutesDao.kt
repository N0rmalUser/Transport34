package ru.normal.trans34.data.local.routes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedRoutesDao {

    @Query("SELECT * FROM saved_routes")
    fun getAllSavedRoute(): Flow<List<SavedRouteEntity>>

    @Query("SELECT * FROM saved_routes WHERE id = :id")
    fun getSavedUnitById(id: String): Flow<List<SavedRouteEntity>>

    @Query("SELECT EXISTS (SELECT 1 FROM saved_routes WHERE id = :id)")
    fun isSavedRouteExists(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedRoute(savedUnit: SavedRouteEntity)

    @Query("DELETE FROM saved_routes WHERE id = :id")
    suspend fun deleteSavedRoute(id: String)
}