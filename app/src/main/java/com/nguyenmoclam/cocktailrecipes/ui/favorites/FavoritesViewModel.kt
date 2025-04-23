package com.nguyenmoclam.cocktailrecipes.ui.favorites

import androidx.lifecycle.viewModelScope
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import com.nguyenmoclam.cocktailrecipes.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: CocktailRepository
) : BaseViewModel<FavoritesUiState, FavoritesUiEvent>(FavoritesUiState()) {
    
    init {
        loadFavorites()
    }
    
    override suspend fun processEvent(event: FavoritesUiEvent) {
        when (event) {
            is FavoritesUiEvent.LoadFavorites -> loadFavorites()
            is FavoritesUiEvent.RemoveFavorite -> removeFavorite(event.cocktailId)
        }
    }
    
    fun loadFavorites() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            
            repository.getFavorites().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        updateState { 
                            it.copy(
                                isLoading = false,
                                favorites = result.data ?: emptyList(),
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        // Check for rate limit errors
                        result.apiError.throwable?.let { throwable -> handleApiError(throwable) }
                        
                        updateState { 
                            it.copy(
                                isLoading = false,
                                error = result.apiError.message
                            ) 
                        }
                    }
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }
    
    fun removeFavorite(cocktailId: String) {
        viewModelScope.launch {
            repository.removeFavorite(cocktailId).collect { result ->
                if (result is Resource.Success) {
                    // Reload favorites after removal
                    loadFavorites()
                } else if (result is Resource.Error) {
                    // Check for rate limit errors
                    result.apiError.throwable?.let { throwable -> handleApiError(throwable) }
                }
            }
        }
    }
}

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val favorites: List<Cocktail> = emptyList(),
    val error: String? = null
)

sealed class FavoritesUiEvent {
    object LoadFavorites : FavoritesUiEvent()
    data class RemoveFavorite(val cocktailId: String) : FavoritesUiEvent()
} 