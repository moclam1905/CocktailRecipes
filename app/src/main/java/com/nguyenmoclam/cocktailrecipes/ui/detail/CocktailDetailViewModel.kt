package com.nguyenmoclam.cocktailrecipes.ui.detail

import androidx.lifecycle.viewModelScope
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import com.nguyenmoclam.cocktailrecipes.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CocktailDetailViewModel @Inject constructor(
    private val cocktailRepository: CocktailRepository
) : BaseViewModel<CocktailDetailViewModel.UiState, CocktailDetailViewModel.UiEvent>(UiState.Loading) {

    override suspend fun processEvent(event: UiEvent) {
        when (event) {
            is UiEvent.LoadCocktailDetails -> loadCocktailDetails(event.id)
            is UiEvent.ToggleFavorite -> toggleFavorite(event.cocktail)
            is UiEvent.Refresh -> loadCocktailDetails(event.id)
        }
    }

    fun loadCocktailDetails(id: String) {
        viewModelScope.launch {
            updateState { UiState.Loading }
            
            cocktailRepository.getCocktailDetails(id)
                .catch { error -> 
                    handleApiError(error)
                    updateState { UiState.Error(error.message ?: "Unknown error") } 
                }
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> updateState { UiState.Success(resource.data) }
                        is Resource.Error -> {
                            // Check for rate limit errors
                            resource.apiError.throwable?.let { handleApiError(it) }
                            updateState { UiState.Error(resource.apiError.message) }
                        }
                        is Resource.Loading -> updateState { UiState.Loading }
                    }
                }
        }
    }
    
    private fun toggleFavorite(cocktail: Cocktail) {
        viewModelScope.launch {
            if (cocktail.isFavorite) {
                cocktailRepository.removeFavorite(cocktail.id)
            } else {
                cocktailRepository.saveFavorite(cocktail)
            }
            
            // Refresh the details to update the favorite status
            loadCocktailDetails(cocktail.id)
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val data: Cocktail) : UiState()
        data class Error(val message: String) : UiState()
    }
    
    sealed class UiEvent {
        data class LoadCocktailDetails(val id: String) : UiEvent()
        data class ToggleFavorite(val cocktail: Cocktail) : UiEvent()
        data class Refresh(val id: String) : UiEvent()
    }
} 