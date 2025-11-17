package ru.normal.trans34.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import ru.normal.trans34.domain.repository.SettingsRepository
import ru.normal.trans34.presentation.model.MapVisibilityMode

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private val VISIBILITY_KEY = stringPreferencesKey("visibility_mode")

    override suspend fun saveVisibilityMode(mode: MapVisibilityMode) {
        dataStore.edit { prefs ->
            prefs[VISIBILITY_KEY] = mode.name
        }
    }

    override fun visibilityModeFlow(): Flow<MapVisibilityMode> {
        return dataStore.data
            .catch { e ->
                if (e is IOException) emit(emptyPreferences()) else throw e
            }
            .map { prefs ->
                prefs[VISIBILITY_KEY]?.let {
                    runCatching { MapVisibilityMode.valueOf(it) }.getOrNull()
                } ?: MapVisibilityMode.ALL_TRANSPORT
            }
    }

    override suspend fun getVisibilityMode(): MapVisibilityMode {
        return visibilityModeFlow().first()
    }
}
