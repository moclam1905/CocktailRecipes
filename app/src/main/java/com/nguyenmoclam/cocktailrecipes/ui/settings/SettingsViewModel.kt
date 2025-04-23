package com.nguyenmoclam.cocktailrecipes.ui.settings

import androidx.lifecycle.viewModelScope
import com.nguyenmoclam.cocktailrecipes.data.local.PreferencesManager
import com.nguyenmoclam.cocktailrecipes.domain.repository.SettingsRepository
import com.nguyenmoclam.cocktailrecipes.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Settings screen
 */
data class SettingsUiState(
    val themeMode: String = PreferencesManager.THEME_MODE_SYSTEM,
    val appVersion: String = "",
    val isCacheClearing: Boolean = false,
    val cacheCleared: Boolean = false,
    val showClearCacheConfirmation: Boolean = false,
    val isApiCacheClearing: Boolean = false,
    val apiCacheCleared: Boolean = false
)

/**
 * Events for the Settings screen
 */
sealed class SettingsEvent {
    object LoadSettings : SettingsEvent()
    data class SetThemeMode(val mode: String) : SettingsEvent()
    object ShowClearCacheConfirmation : SettingsEvent()
    object DismissClearCacheConfirmation : SettingsEvent()
    object ClearCache : SettingsEvent()
}

/**
 * ViewModel for the Settings screen
 */
@HiltViewModel
open class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : BaseViewModel<SettingsUiState, SettingsEvent>(SettingsUiState()) {

    init {
        handleEvent(SettingsEvent.LoadSettings)
    }

    override suspend fun processEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.LoadSettings -> loadSettings()
            is SettingsEvent.SetThemeMode -> setThemeMode(event.mode)
            is SettingsEvent.ShowClearCacheConfirmation -> showClearCacheConfirmation()
            is SettingsEvent.DismissClearCacheConfirmation -> dismissClearCacheConfirmation()
            is SettingsEvent.ClearCache -> clearCache()
        }
    }

    /**
     * Load settings from repository
     */
    private suspend fun loadSettings() {
        // Load app version
        val appVersion = settingsRepository.getAppVersion()
        updateState { it.copy(appVersion = appVersion) }
        
        // Observe theme mode
        viewModelScope.launch {
            settingsRepository.getThemeMode()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = PreferencesManager.THEME_MODE_SYSTEM
                )
                .collect { themeMode ->
                    updateState { it.copy(themeMode = themeMode) }
                }
        }
    }

    /**
     * Set the theme mode
     */
    internal suspend fun setThemeMode(mode: String) {
        settingsRepository.setThemeMode(mode)
    }

    /**
     * Show confirmation dialog for clearing cache
     */
    internal fun showClearCacheConfirmation() {
        updateState { it.copy(showClearCacheConfirmation = true) }
    }

    /**
     * Dismiss confirmation dialog for clearing cache
     */
    internal fun dismissClearCacheConfirmation() {
        updateState { it.copy(showClearCacheConfirmation = false) }
    }

    /**
     * Clear app and API cache
     */
    internal suspend fun clearCache() {
        updateState { 
            it.copy(
                isCacheClearing = true,
                isApiCacheClearing = true,
                showClearCacheConfirmation = false,
                // Reset previously cleared status if trying to clear again
                cacheCleared = false,
                apiCacheCleared = false
            )
        }
        
        // Clear app database cache
        val appCacheSuccess = settingsRepository.clearAppCache()
        
        // Clear API HTTP cache
        val apiCacheSuccess = settingsRepository.clearApiCache()
        
        // Update with success results
        updateState { 
            it.copy(
                isCacheClearing = false, 
                isApiCacheClearing = false,
                cacheCleared = appCacheSuccess,
                apiCacheCleared = apiCacheSuccess
            ) 
        }
        
        // Reset the cache cleared flags after 3 seconds
        // This ensures the snackbar is displayed and then the flag is reset
        // so it can be triggered again on the next clear
        delay(3000)
        updateState {
            it.copy(
                cacheCleared = false,
                apiCacheCleared = false
            )
        }
    }
} 