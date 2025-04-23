package com.nguyenmoclam.cocktailrecipes.ui.ingredients

import androidx.lifecycle.viewModelScope
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.model.IngredientItem
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import com.nguyenmoclam.cocktailrecipes.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

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

/**
 * Events for the Ingredient Explorer screen
 */
sealed class IngredientExplorerEvent {
    data class LoadIngredients(val forceRefresh: Boolean = false) : IngredientExplorerEvent()
    data class SelectIngredient(val ingredient: IngredientItem) : IngredientExplorerEvent()
    object ClearSelectedIngredient : IngredientExplorerEvent()
}

/**
 * ViewModel for Ingredient Explorer feature
 */
@HiltViewModel
class IngredientExplorerViewModel @Inject constructor(
    private val repository: CocktailRepository
) : BaseViewModel<IngredientExplorerUiState, IngredientExplorerEvent>(IngredientExplorerUiState()) {
    
    init {
        handleEvent(IngredientExplorerEvent.LoadIngredients())
    }
    
    override suspend fun processEvent(event: IngredientExplorerEvent) {
        when (event) {
            is IngredientExplorerEvent.LoadIngredients -> loadIngredients(event.forceRefresh)
            is IngredientExplorerEvent.SelectIngredient -> selectIngredient(event.ingredient)
            is IngredientExplorerEvent.ClearSelectedIngredient -> clearSelectedIngredient()
        }
    }
    
    /**
     * Load all available ingredients
     */
    internal suspend fun loadIngredients(forceRefresh: Boolean = false) {
        updateState { it.copy(
            isLoading = true, 
            error = null,
            selectedIngredient = null,
            cocktails = emptyList()
        )}
        
        repository.getAllIngredients(forceRefresh).collect { result ->
            when (result) {
                is Resource.Success -> {
                    updateState { it.copy(
                        isLoading = false,
                        ingredients = result.data,
                        error = null
                    )}
                }
                is Resource.Error -> {
                    updateState { it.copy(
                        isLoading = false,
                        error = result.apiError.message
                    )}
                    result.apiError.throwable?.let { throwable ->
                        handleApiError(throwable)
                    }
                }
                is Resource.Loading -> {
                    updateState { it.copy(isLoading = true) }
                }
            }
        }
    }
    
    /**
     * Select an ingredient and load related cocktails
     */
    internal suspend fun selectIngredient(ingredient: IngredientItem) {
        updateState { it.copy(
            isLoading = true,
            selectedIngredient = ingredient,
            cocktails = emptyList(),
            error = null
        )}
        
        repository.getCocktailsByIngredient(ingredient.name).collect { result ->
            when (result) {
                is Resource.Success -> {
                    updateState { it.copy(
                        isLoading = false,
                        cocktails = result.data,
                        error = null
                    )}
                }
                is Resource.Error -> {
                    updateState { it.copy(
                        isLoading = false,
                        error = result.apiError.message
                    )}
                    result.apiError.throwable?.let { throwable ->
                        handleApiError(throwable)
                    }
                }
                is Resource.Loading -> {
                    updateState { it.copy(isLoading = true) }
                }
            }
        }
    }
    
    /**
     * Clear selected ingredient
     */
    internal fun clearSelectedIngredient() {
        updateState { it.copy(
            selectedIngredient = null,
            cocktails = emptyList()
        )}
    }
} 