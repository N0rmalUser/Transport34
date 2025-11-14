package ru.normal.trans34.data.local.stops

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_stops")
data class SavedStopEntity(
    @PrimaryKey(autoGenerate = true) val tabNumber: Int = 0,
    val stopId: Int,
    val destinationRu: String,
    val destinationEn: String
)