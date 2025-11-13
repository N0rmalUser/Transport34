package ru.normal.trans34.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.first
import ru.normal.trans34.domain.repository.SettingsRepository

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private val SHOW_UNITS_KEY = booleanPreferencesKey("show_units")

    override suspend fun saveShowUnits(show: Boolean) {
        dataStore.edit { prefs ->
            prefs[SHOW_UNITS_KEY] = show
        }
    }

    override fun showUnitsFlow(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .map { prefs ->
                prefs[SHOW_UNITS_KEY] ?: true
            }
    }

    override suspend fun getShowUnits(): Boolean {
        return showUnitsFlow().first()
    }
}
