package com.nguyenmoclam.cocktailrecipes.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyenmoclam.cocktailrecipes.data.local.PreferencesManager
import com.nguyenmoclam.cocktailrecipes.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val themeMode: String = PreferencesManager.THEME_MODE_SYSTEM,
    val appVersion: String = "",
    val isCacheClearing: Boolean = false,
    val cacheCleared: Boolean = false,
    val showClearCacheConfirmation: Boolean = false
)

@HiltViewModel
open class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    open val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // Load app version
            val appVersion = settingsRepository.getAppVersion()
            _uiState.update { it.copy(appVersion = appVersion) }
            
            // Observe theme mode
            settingsRepository.getThemeMode()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = PreferencesManager.THEME_MODE_SYSTEM
                )
                .collect { themeMode ->
                    _uiState.update { it.copy(themeMode = themeMode) }
                }
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun showClearCacheConfirmation() {
        _uiState.update { it.copy(showClearCacheConfirmation = true) }
    }

    fun dismissClearCacheConfirmation() {
        _uiState.update { it.copy(showClearCacheConfirmation = false) }
    }

    fun clearCache() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCacheClearing = true, showClearCacheConfirmation = false) }
            val success = settingsRepository.clearAppCache()
            _uiState.update { it.copy(isCacheClearing = false, cacheCleared = success) }
        }
    }
} 