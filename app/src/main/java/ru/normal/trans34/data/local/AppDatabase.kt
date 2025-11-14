package ru.normal.trans34.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.normal.trans34.data.local.stops.SavedStopEntity
import ru.normal.trans34.data.local.stops.SavedStopsDao
import ru.normal.trans34.data.local.routes.SavedRouteEntity
import ru.normal.trans34.data.local.routes.SavedRoutesDao

@Database(
    entities = [
        SavedStopEntity::class,
        SavedRouteEntity::class
    ], version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedStopsDao(): SavedStopsDao
    abstract fun savedRoutesDao(): SavedRoutesDao
}
