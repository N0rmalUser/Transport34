package ru.normal.trans34.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import ru.normal.trans34.data.local.stops.SavedStopsDao
import ru.normal.trans34.data.remote.TransportApi
import ru.normal.trans34.data.repository.SettingsRepositoryImpl
import ru.normal.trans34.data.repository.StopsRepositoryImpl
import ru.normal.trans34.data.repository.TransportRepositoryImpl
import ru.normal.trans34.domain.repository.SettingsRepository
import ru.normal.trans34.domain.repository.StopsRepository
import ru.normal.trans34.domain.repository.TransportRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTransportApi(client: HttpClient): TransportApi = TransportApi(client)

    @Provides
    @Singleton
    fun provideTransportRepository(api: TransportApi): TransportRepository =
        TransportRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideStopsRepository(
        savedStopsDao: SavedStopsDao
    ): StopsRepository {
        return StopsRepositoryImpl(
            savedStopsDao = savedStopsDao
        )
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataStore: DataStore<Preferences>
    ): SettingsRepository = SettingsRepositoryImpl(
        dataStore
    )
}
