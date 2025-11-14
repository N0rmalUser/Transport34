package ru.normal.trans34.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.normal.trans34.data.local.stops.SavedStopEntity
import ru.normal.trans34.data.local.stops.SavedStopsDao

@Database(
    entities = [SavedStopEntity::class], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedStopsDao(): SavedStopsDao
}
