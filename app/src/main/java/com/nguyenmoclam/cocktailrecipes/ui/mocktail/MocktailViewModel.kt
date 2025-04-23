package com.nguyenmoclam.cocktailrecipes.ui.mocktail

import androidx.lifecycle.viewModelScope
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import com.nguyenmoclam.cocktailrecipes.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Mocktail Corner screen
 */
data class MocktailUiState(
    val mocktails: List<Cocktail> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val showAnimations: Boolean = true // Default to showing animations
)

/**
 * Events that can be triggered from the UI
 */
sealed class MocktailEvent {
    object LoadMocktails : MocktailEvent()
    object RefreshMocktails : MocktailEvent()
    data class ToggleFavorite(val cocktailId: String) : MocktailEvent()
    data class ToggleAnimations(val enabled: Boolean) : MocktailEvent()
}

/**
 * ViewModel for the Mocktail Corner screen
 */
@HiltViewModel
class MocktailViewModel @Inject constructor(
    private val cocktailRepository: CocktailRepository
) : BaseViewModel<MocktailUiState, MocktailEvent>(MocktailUiState()) {

    // Keep track of ongoing favorites jobs to avoid multiple simultaneous updates
    private var favoritesUpdateJob: Job? = null
    
    // In-memory cache for faster access
    private var cachedMocktails: List<Cocktail> = emptyList()

    init {
        // Load initial data
        handleEvent(MocktailEvent.LoadMocktails)
    }

    override suspend fun processEvent(event: MocktailEvent) {
        when (event) {
            is MocktailEvent.LoadMocktails -> loadMocktails(useCache = true)
            is MocktailEvent.RefreshMocktails -> loadMocktails(forceRefresh = true)
            is MocktailEvent.ToggleFavorite -> toggleFavorite(event.cocktailId)
            is MocktailEvent.ToggleAnimations -> toggleAnimations(event.enabled)
        }
    }

    /**
     * Load non-alcoholic drinks
     * @param forceRefresh If true, bypass cache and force a new API call
     * @param useCache If true, use cached results first if available
     */
    private suspend fun loadMocktails(forceRefresh: Boolean = false, useCache: Boolean = false) {
        // Use cached data first if available and requested
        if (useCache && cachedMocktails.isNotEmpty() && !forceRefresh) {
            updateState { 
                it.copy(
                    mocktails = cachedMocktails,
                    isLoading = false,
                    isRefreshing = false,
                    error = null
                )
            }
            // Still update favorites in background
            updateFavoritesStatus()
            return
        }
        
        // Otherwise load from repository
        if (forceRefresh) {
            updateState { it.copy(isRefreshing = true, error = null) }
        } else {
            updateState { it.copy(isLoading = true, error = null) }
        }
        
        cocktailRepository.getCocktailsByAlcoholicFilter("Non_Alcoholic")
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val mocktails = result.data
                        // Update cache
                        cachedMocktails = mocktails
                        
                        updateState { 
                            it.copy(
                                mocktails = mocktails,
                                isLoading = false,
                                isRefreshing = false,
                                error = null
                            )
                        }
                        // After loading the mocktails, update favorites status
                        updateFavoritesStatus()
                    }
                    is Resource.Error -> {
                        // Provide user-friendly error messages
                        val errorMessage = getUserFriendlyErrorMessage(result.apiError.message)
                        updateState { 
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = errorMessage
                            )
                        }
                        result.apiError.throwable?.let { throwable ->
                            handleApiError(throwable)
                        }
                    }
                    is Resource.Loading -> {
                        if (!forceRefresh) {
                            updateState { it.copy(isLoading = true) }
                        }
                    }
                }
            }
    }

    /**
     * Convert technical error messages to user-friendly messages
     */
    private fun getUserFriendlyErrorMessage(technicalMessage: String?): String {
        return when {
            technicalMessage == null -> "An unknown error occurred. Please try again."
            technicalMessage.contains("timeout", ignoreCase = true) -> 
                "Connection timed out. Please check your internet and try again."
            technicalMessage.contains("internet") || 
            technicalMessage.contains("connection") ||
            technicalMessage.contains("network") -> 
                "No internet connection. Please check your connection and try again."
            technicalMessage.contains("404") -> 
                "The mocktail information could not be found. Please try again later."
            technicalMessage.contains("server") -> 
                "The server is currently unavailable. Please try again later."
            technicalMessage.contains("rate limit") || technicalMessage.contains("429") -> 
                "Too many requests. Please wait a moment before trying again."
            else -> "Error loading mocktails. Please try again."
        }
    }

    /**
     * Toggle the favorite status of a cocktail
     */
    private suspend fun toggleFavorite(cocktailId: String) {
        val cocktail = uiState.value.mocktails.find { it.id == cocktailId } ?: return
        
        // Check if the cocktail is already a favorite
        val favoriteResult = cocktailRepository.isFavorite(cocktailId).first()
        if (favoriteResult is Resource.Success) {
            val isFavorite = favoriteResult.data
            
            if (isFavorite) {
                // Remove from favorites
                cocktailRepository.removeFavorite(cocktailId).first()
            } else {
                // Add to favorites
                cocktailRepository.saveFavorite(cocktail).first()
            }
            
            // Update UI with optimistic update without waiting for background check
            updateCocktailFavoriteStatus(cocktailId, !isFavorite)
        }
    }

    /**
     * Update a single cocktail's favorite status in the UI
     */
    private fun updateCocktailFavoriteStatus(cocktailId: String, isFavorite: Boolean) {
        val updatedList = uiState.value.mocktails.map { cocktail ->
            if (cocktail.id == cocktailId) {
                cocktail.copy(isFavorite = isFavorite)
            } else {
                cocktail
            }
        }
        
        // Update both UI state and cache
        cachedMocktails = updatedList
        updateState { it.copy(mocktails = updatedList) }
    }

    /**
     * Update the favorite status of all mocktails in the list more efficiently
     * using parallel processing
     */
    private fun updateFavoritesStatus() {
        val cocktails = uiState.value.mocktails
        if (cocktails.isEmpty()) return
        
        // Cancel any ongoing job before starting a new one
        favoritesUpdateJob?.cancel()
        
        favoritesUpdateJob = viewModelScope.launch {
            // Create a mutable copy for updates
            val updatedCocktails = cocktails.toMutableList()
            val favoriteStatuses = cocktails.mapIndexed { index, cocktail ->
                async {
                    val result = cocktailRepository.isFavorite(cocktail.id).first()
                    if (result is Resource.Success) {
                        val isFavorite = result.data
                        if (cocktail.isFavorite != isFavorite) {
                            Pair(index, cocktail.copy(isFavorite = isFavorite))
                        } else null
                    } else null
                }
            }.awaitAll().filterNotNull()
            
            // Apply all updates at once if there are any changes
            if (favoriteStatuses.isNotEmpty()) {
                // Update the items that changed
                favoriteStatuses.forEach { (index, updatedCocktail) ->
                    updatedCocktails[index] = updatedCocktail
                }
                
                // Update both UI state and cache with a single update
                cachedMocktails = updatedCocktails
                updateState { it.copy(mocktails = updatedCocktails) }
            }
        }
    }
    
    /**
     * Toggle animations on the UI
     */
    private fun toggleAnimations(enabled: Boolean) {
        updateState { it.copy(showAnimations = enabled) }
    }
    
    override fun onCleared() {
        super.onCleared()
        favoritesUpdateJob?.cancel()
    }
} 