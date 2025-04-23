package com.nguyenmoclam.cocktailrecipes.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyenmoclam.cocktailrecipes.ui.common.isRateLimitError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel that provides common functionality for all ViewModels
 */
abstract class BaseViewModel<UiState, UiEvent>(initialState: UiState) : ViewModel() {

    // UI state as StateFlow
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    // Rate limit error flow
    private val _rateLimitError = MutableStateFlow<Throwable?>(null)
    val rateLimitError: StateFlow<Throwable?> = _rateLimitError.asStateFlow()

    // Handle UI events
    fun handleEvent(event: UiEvent) {
        viewModelScope.launch {
            processEvent(event)
        }
    }

    // Update UI state
    protected fun updateState(update: (UiState) -> UiState) {
        _uiState.value = update(_uiState.value)
    }
    
    // Handle API errors, checking for rate limit errors
    protected fun handleApiError(error: Throwable) {
        if (error.isRateLimitError()) {
            _rateLimitError.value = error
        }
    }
    
    // Clear rate limit error after it's been shown
    fun clearRateLimitError() {
        _rateLimitError.value = null
    }

    // Process UI events
    protected abstract suspend fun processEvent(event: UiEvent)
} 