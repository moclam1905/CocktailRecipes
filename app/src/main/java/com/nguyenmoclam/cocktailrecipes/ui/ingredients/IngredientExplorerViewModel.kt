package com.nguyenmoclam.cocktailrecipes.ui.ingredients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyenmoclam.cocktailrecipes.data.common.ApiError
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.model.IngredientItem
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Ingredient Explorer feature
 */
@HiltViewModel
class IngredientExplorerViewModel @Inject constructor(
    private val repository: CocktailRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(IngredientExplorerUiState())
    val uiState: StateFlow<IngredientExplorerUiState> = _uiState.asStateFlow()
    
    init {
        loadIngredients()
    }
    
    /**
     * Load all available ingredients
     */
    fun loadIngredients(forceRefresh: Boolean = false) {
        _uiState.update { it.copy(
            isLoading = true, 
            error = null,
            selectedIngredient = null,
            cocktails = emptyList()
        )}
        
        viewModelScope.launch {
            repository.getAllIngredients(forceRefresh).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            ingredients = result.data,
                            error = null
                        )}
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = result.apiError.message
                        )}
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }
    
    /**
     * Select an ingredient and load related cocktails
     */
    fun selectIngredient(ingredient: IngredientItem) {
        _uiState.update { it.copy(
            isLoading = true,
            selectedIngredient = ingredient,
            cocktails = emptyList(),
            error = null
        )}
        
        viewModelScope.launch {
            repository.getCocktailsByIngredient(ingredient.name).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            cocktails = result.data,
                            error = null
                        )}
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = result.apiError.message
                        )}
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }
    
    /**
     * Clear selected ingredient
     */
    fun clearSelectedIngredient() {
        _uiState.update { it.copy(
            selectedIngredient = null,
            cocktails = emptyList()
        )}
    }
}

/**
 * UI state for the Ingredient Explorer screen
 */
data class IngredientExplorerUiState(
    val isLoading: Boolean = false,
    val ingredients: List<IngredientItem> = emptyList(),
    val selectedIngredient: IngredientItem? = null,
    val cocktails: List<Cocktail> = emptyList(),
    val error: String? = null
) 