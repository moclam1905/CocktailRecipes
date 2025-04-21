package com.nguyenmoclam.cocktailrecipes.ui.home

import androidx.lifecycle.viewModelScope
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import com.nguyenmoclam.cocktailrecipes.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Home screen
 */
data class HomeUiState(
    val cocktails: List<Cocktail> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

/**
 * UI events for the Home screen
 */
sealed class HomeEvent {
    object LoadCocktails : HomeEvent()
    object RefreshCocktails : HomeEvent()
    data class ToggleFavorite(val cocktailId: String) : HomeEvent()
    data class NavigateToDetails(val cocktailId: String) : HomeEvent()
}

/**
 * ViewModel for the Home screen that manages UI state and handles events
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cocktailRepository: CocktailRepository
) : BaseViewModel<HomeUiState, HomeEvent>(HomeUiState()) {

    init {
        loadCocktails()
    }

    override suspend fun processEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadCocktails -> loadCocktails()
            is HomeEvent.RefreshCocktails -> refreshCocktails()
            is HomeEvent.ToggleFavorite -> toggleFavorite(event.cocktailId)
            is HomeEvent.NavigateToDetails -> {
                // Navigation will be handled by the NavController in the UI
            }
        }
    }

    /**
     * Load the list of popular cocktails
     */
    private fun loadCocktails() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }

            cocktailRepository.getPopularCocktails()
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            updateState { 
                                it.copy(
                                    cocktails = result.data,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            updateState { 
                                it.copy(
                                    isLoading = false,
                                    error = result.apiError.getUserFriendlyMessage()
                                )
                            }
                        }
                        is Resource.Loading -> {
                            updateState { it.copy(isLoading = true) }
                        }
                    }
                }
                .catch { e ->
                    updateState { 
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Unknown error occurred"
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    /**
     * Refresh the list of cocktails (for pull-to-refresh)
     */
    private fun refreshCocktails() {
        viewModelScope.launch {
            updateState { it.copy(isRefreshing = true, error = null) }

            cocktailRepository.getPopularCocktails()
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            updateState { 
                                it.copy(
                                    cocktails = result.data,
                                    isRefreshing = false,
                                    error = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            updateState { 
                                it.copy(
                                    isRefreshing = false,
                                    error = result.apiError.getUserFriendlyMessage()
                                )
                            }
                        }
                        is Resource.Loading -> {
                            // Keep isRefreshing true during loading
                        }
                    }
                }
                .catch { e ->
                    updateState { 
                        it.copy(
                            isRefreshing = false,
                            error = e.message ?: "Unknown error occurred"
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    /**
     * Toggle the favorite status of a cocktail
     */
    private fun toggleFavorite(cocktailId: String) {
        viewModelScope.launch {
            val cocktail = uiState.value.cocktails.find { it.id == cocktailId } ?: return@launch
            
            val repository = if (cocktail.isFavorite) {
                cocktailRepository.removeFavorite(cocktailId)
            } else {
                cocktailRepository.saveFavorite(cocktail)
            }

            repository
                .onEach { result ->
                    if (result is Resource.Success && result.data == true) {
                        // Update the cocktail in the list with the toggled favorite status
                        updateState {
                            val updatedCocktails = it.cocktails.map { c ->
                                if (c.id == cocktailId) c.copy(isFavorite = !c.isFavorite) else c
                            }
                            it.copy(cocktails = updatedCocktails)
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }
} 