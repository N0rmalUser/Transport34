package ru.normal.trans34.presentation.screen.main

import ru.normal.trans34.domain.entity.ReleaseInfo

data class UpdateState(
    val available: Boolean = false,
    val showDialog: Boolean = false,
    val info: ReleaseInfo? = null,
// TODO: Перенести во ViewModel
//    val isDownloading: Boolean = false,
//    val progress: Float = 0f,
    val error: String? = null
)
