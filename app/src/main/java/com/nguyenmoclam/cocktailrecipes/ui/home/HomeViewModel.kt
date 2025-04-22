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
    val error: String? = null,
    val randomCocktail: Cocktail? = null,
    val isLoadingRandom: Boolean = false,
    val randomCocktailError: String? = null,
    val showRandomCocktail: Boolean = false
)

/**
 * UI events for the Home screen
 */
sealed class HomeEvent {
    object LoadCocktails : HomeEvent()
    object RefreshCocktails : HomeEvent()
    data class ToggleFavorite(val cocktailId: String) : HomeEvent()
    data class NavigateToDetails(val cocktailId: String) : HomeEvent()
    object GetRandomCocktail : HomeEvent()
    object DismissRandomCocktail : HomeEvent()
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

    public override suspend fun processEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadCocktails -> loadCocktails()
            is HomeEvent.RefreshCocktails -> refreshCocktails()
            is HomeEvent.ToggleFavorite -> toggleFavorite(event.cocktailId)
            is HomeEvent.NavigateToDetails -> {
                // Navigation will be handled by the NavController in the UI
            }
            is HomeEvent.GetRandomCocktail -> getRandomCocktail()
            is HomeEvent.DismissRandomCocktail -> {
                updateState { it.copy(showRandomCocktail = false) }
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
            // First try to find the cocktail in the main list
            var cocktail = uiState.value.cocktails.find { it.id == cocktailId }
            
            // If not found in the main list, check if it's the random cocktail
            if (cocktail == null && uiState.value.randomCocktail?.id == cocktailId) {
                cocktail = uiState.value.randomCocktail
            }
            
            // If still not found, return early
            if (cocktail == null) return@launch
            
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
                            
                            // Also update the random cocktail if that's what was favorited
                            val updatedRandomCocktail = if (it.randomCocktail?.id == cocktailId) {
                                it.randomCocktail.copy(isFavorite = !it.randomCocktail.isFavorite)
                            } else {
                                it.randomCocktail
                            }
                            
                            it.copy(
                                cocktails = updatedCocktails,
                                randomCocktail = updatedRandomCocktail
                            )
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    /**
     * Get a random cocktail
     */
    private fun getRandomCocktail() {
        viewModelScope.launch {
            updateState { 
                it.copy(
                    isLoadingRandom = true, 
                    randomCocktailError = null,
                    showRandomCocktail = true
                ) 
            }

            cocktailRepository.getRandomCocktail()
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            updateState { 
                                it.copy(
                                    randomCocktail = result.data,
                                    isLoadingRandom = false,
                                    randomCocktailError = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            updateState { 
                                it.copy(
                                    isLoadingRandom = false,
                                    randomCocktailError = result.apiError.getUserFriendlyMessage()
                                )
                            }
                        }
                        is Resource.Loading -> {
                            updateState { it.copy(isLoadingRandom = true) }
                        }
                    }
                }
                .catch { e ->
                    updateState { 
                        it.copy(
                            isLoadingRandom = false,
                            randomCocktailError = e.message ?: "Unknown error occurred"
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }
} 