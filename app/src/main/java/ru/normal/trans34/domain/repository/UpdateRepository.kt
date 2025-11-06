package ru.normal.trans34.domain.repository

import ru.normal.trans34.domain.entity.ReleaseInfo

interface UpdateRepository {
    suspend fun getLatestRelease(): ReleaseInfo?
}
