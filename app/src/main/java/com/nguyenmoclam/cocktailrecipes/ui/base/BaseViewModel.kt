package com.nguyenmoclam.cocktailrecipes.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // Process UI events
    protected abstract suspend fun processEvent(event: UiEvent)
} 