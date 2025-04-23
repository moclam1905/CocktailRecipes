package com.nguyenmoclam.cocktailrecipes.ui.filter

import androidx.lifecycle.viewModelScope
import com.nguyenmoclam.cocktailrecipes.data.common.ApiError
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.data.local.FilterPreferences
import com.nguyenmoclam.cocktailrecipes.domain.model.AlcoholicFilter
import com.nguyenmoclam.cocktailrecipes.domain.model.Category
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.model.CocktailFilter
import com.nguyenmoclam.cocktailrecipes.domain.model.Glass
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import com.nguyenmoclam.cocktailrecipes.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val cocktailRepository: CocktailRepository,
    private val filterPreferences: FilterPreferences
) : BaseViewModel<FilterUiState, FilterUiEvent>(FilterUiState()) {
    
    init {
        loadSavedFilters()
        loadFilterOptions()
    }
    
    /**
     * Process UI events
     */
    override suspend fun processEvent(event: FilterUiEvent) {
        when (event) {
            is FilterUiEvent.SelectCategory -> selectCategory(event.category)
            is FilterUiEvent.SelectGlassType -> selectGlassType(event.glassType)
            is FilterUiEvent.SelectAlcoholicFilter -> selectAlcoholicFilter(event.alcoholicFilter)
            is FilterUiEvent.ClearFilters -> clearFilters()
            is FilterUiEvent.Refresh -> refresh()
        }
    }
    
    /**
     * Handle API error
     */
    private fun handleError(apiError: ApiError) {
        // Check for rate limit errors first
        apiError.throwable?.let { throwable ->
            handleApiError(throwable)
        }
        
        // Then update UI state with error message
        updateState { currentState ->
            currentState.copy(
                error = apiError.message
            )
        }
    }
    
    /**
     * Load saved filter preferences from DataStore
     */
    private fun loadSavedFilters() {
        viewModelScope.launch {
            filterPreferences.filterFlow.collectLatest { savedFilter ->
                updateState { currentState ->
                    currentState.copy(
                        selectedCategory = savedFilter.category,
                        selectedGlassType = savedFilter.glass,
                        selectedAlcoholicFilter = savedFilter.alcoholic,
                        filter = savedFilter
                    )
                }
                // Apply filters if any are active
                if (savedFilter.hasActiveFilters()) {
                    applyFilters()
                }
            }
        }
    }
    
    /**
     * Load all available filter options from API in parallel
     */
    private fun loadFilterOptions() {
        viewModelScope.launch {
            // Start loading
            updateState { it.copy(isLoading = true) }
            
            try {
                // Load categories
                launch {
                    cocktailRepository.getAllCategories().collectLatest { result ->
                        when (result) {
                            is Resource.Success -> {
                                updateState { currentState ->
                                    currentState.copy(
                                        categories = result.data,
                                        error = null
                                    )
                                }
                            }
                            is Resource.Error -> {
                                updateState { currentState ->
                                    currentState.copy(
                                        error = result.apiError.message
                                    )
                                }
                            }
                            is Resource.Loading -> {
                                // Already set loading state
                            }
                        }
                    }
                }
                
                // Load glass types
                launch {
                    cocktailRepository.getAllGlassTypes().collectLatest { result ->
                        when (result) {
                            is Resource.Success -> {
                                updateState { currentState ->
                                    currentState.copy(
                                        glassTypes = result.data,
                                        error = null
                                    )
                                }
                            }
                            is Resource.Error -> {
                                updateState { currentState ->
                                    if (currentState.error == null) {
                                        currentState.copy(error = result.apiError.message)
                                    } else {
                                        currentState
                                    }
                                }
                            }
                            is Resource.Loading -> {
                                // Already set loading state
                            }
                        }
                    }
                }
                
                // Load alcoholic filters
                launch {
                    cocktailRepository.getAllAlcoholicFilters().collectLatest { result ->
                        when (result) {
                            is Resource.Success -> {
                                updateState { currentState ->
                                    currentState.copy(
                                        alcoholicFilters = result.data,
                                        error = null
                                    )
                                }
                            }
                            is Resource.Error -> {
                                updateState { currentState ->
                                    if (currentState.error == null) {
                                        currentState.copy(error = result.apiError.message)
                                    } else {
                                        currentState
                                    }
                                }
                            }
                            is Resource.Loading -> {
                                // Already set loading state
                            }
                        }
                    }
                }
                
                // Set loading to false after a delay to ensure data is loaded
                launch {
                    kotlinx.coroutines.delay(2000) // Wait for data loading to complete
                    updateState { it.copy(isLoading = false) }
                }
                
            } catch (e: Exception) {
                updateState { it.copy(
                    error = "Failed to load filter options: ${e.message}",
                    isLoading = false
                ) }
            }
        }
    }
    
    /**
     * Select a category filter
     */
    fun selectCategory(category: String?) {
        val updatedFilter = uiState.value.filter.copy(category = category)
        updateState { 
            it.copy(
                selectedCategory = category,
                filter = updatedFilter
            )
        }
        // Save to preferences
        viewModelScope.launch {
            filterPreferences.saveFilter(updatedFilter)
        }
        applyFilters()
    }
    
    /**
     * Select a glass type filter
     */
    fun selectGlassType(glassType: String?) {
        val updatedFilter = uiState.value.filter.copy(glass = glassType)
        updateState { 
            it.copy(
                selectedGlassType = glassType,
                filter = updatedFilter
            )
        }
        // Save to preferences
        viewModelScope.launch {
            filterPreferences.saveFilter(updatedFilter)
        }
        applyFilters()
    }
    
    /**
     * Select an alcoholic filter
     */
    fun selectAlcoholicFilter(alcoholicFilter: String?) {
        val updatedFilter = uiState.value.filter.copy(alcoholic = alcoholicFilter)
        updateState { 
            it.copy(
                selectedAlcoholicFilter = alcoholicFilter,
                filter = updatedFilter
            )
        }
        // Save to preferences
        viewModelScope.launch {
            filterPreferences.saveFilter(updatedFilter)
        }
        applyFilters()
    }
    
    /**
     * Clear all selected filters
     */
    fun clearFilters() {
        updateState { 
            it.copy(
                selectedCategory = null,
                selectedGlassType = null,
                selectedAlcoholicFilter = null,
                filter = CocktailFilter(),
                filteredCocktails = emptyList()
            )
        }
        // Clear saved preferences
        viewModelScope.launch {
            filterPreferences.clearFilters()
        }
    }
    
    /**
     * Apply the selected filters and retrieve matching cocktails
     */
    private fun applyFilters() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            
            val filter = uiState.value.filter
            
            if (filter.hasActiveFilters()) {
                cocktailRepository.getCocktailsByFilter(filter).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            updateState { 
                                it.copy(
                                    filteredCocktails = result.data,
                                    isLoading = false
                                )
                            }
                        }
                        is Resource.Error -> {
                            handleError(result.apiError)
                            updateState { it.copy(isLoading = false) }
                        }
                        is Resource.Loading -> {
                            // Already set loading state
                        }
                    }
                }
            } else {
                // Clear results if no filters selected
                updateState { 
                    it.copy(
                        filteredCocktails = emptyList(),
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * Reload filter options and results
     */
    fun refresh() {
        loadFilterOptions()
        if (uiState.value.filter.hasActiveFilters()) {
            applyFilters()
        }
    }
}

/**
 * UI state for filter screen
 */
data class FilterUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val glassTypes: List<Glass> = emptyList(),
    val alcoholicFilters: List<AlcoholicFilter> = emptyList(),
    val selectedCategory: String? = null,
    val selectedGlassType: String? = null,
    val selectedAlcoholicFilter: String? = null,
    val filter: CocktailFilter = CocktailFilter(),
    val filteredCocktails: List<Cocktail> = emptyList(),
    val error: String? = null
)

/**
 * UI events for filter screen
 */
sealed class FilterUiEvent {
    data class SelectCategory(val category: String?) : FilterUiEvent()
    data class SelectGlassType(val glassType: String?) : FilterUiEvent()
    data class SelectAlcoholicFilter(val alcoholicFilter: String?) : FilterUiEvent()
    object ClearFilters : FilterUiEvent()
    object Refresh : FilterUiEvent()
} 