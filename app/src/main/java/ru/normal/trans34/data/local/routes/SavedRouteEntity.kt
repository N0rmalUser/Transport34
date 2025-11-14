package ru.normal.trans34.data.local.routes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_routes")
data class SavedRouteEntity (
    @PrimaryKey val id: String,
    val title: String,
)