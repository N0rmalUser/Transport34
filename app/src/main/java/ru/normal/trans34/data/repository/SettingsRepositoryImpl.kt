package ru.normal.trans34.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import ru.normal.trans34.domain.repository.SettingsRepository
import androidx.core.content.edit

class SettingsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SettingsRepository {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    override suspend fun saveShowUnits(show: Boolean) {
        prefs.edit { putBoolean("show_units", show) }
    }

    override suspend fun getShowUnits(): Boolean {
        return prefs.getBoolean("show_units", true)
    }
}
