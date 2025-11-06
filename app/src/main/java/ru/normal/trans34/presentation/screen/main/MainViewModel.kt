package ru.normal.trans34.presentation.screen.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.normal.trans34.domain.repository.UpdateRepository
import ru.normal.trans34.presentation.screen.main.utils.isNewerVersion

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: UpdateRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(UpdateState())
    val state: StateFlow<UpdateState> = _state

    init {
        checkForUpdate()
    }

    private fun checkForUpdate() {
        viewModelScope.launch {
            try {
                val latest = repository.getLatestRelease()
                val currentVersion = context.packageManager
                    .getPackageInfo(context.packageName, 0)
                    .versionName!!
                if (latest != null && isNewerVersion(latest.version.removePrefix("v").removeSuffix("-debug"), currentVersion)) {
                    _state.update { it.copy(available = true, info = latest) }
                }
            } catch (e: Exception) {
                Log.e("checkForUpdate", "Error: $e")
            }
        }
    }

    fun dismissUpdateDialog() {
        _state.update { it.copy(showDialog = false) }
    }

    fun openUpdateDialog() {
        _state.update { it.copy(showDialog = true) }
    }
}
