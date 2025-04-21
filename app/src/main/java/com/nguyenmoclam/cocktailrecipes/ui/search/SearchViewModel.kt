package com.nguyenmoclam.cocktailrecipes.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Minimal info for recently viewed cocktails
data class CocktailMinimalInfo(
    val id: String,
    val name: String,
    val imageUrl: String?
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: CocktailRepository
) : ViewModel() {

    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    
    // Search history state
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory = _searchHistory.asStateFlow()
    
    // Filter type state (name or ingredient)
    private val _isSearchByIngredient = MutableStateFlow(false)
    val isSearchByIngredient = _isSearchByIngredient.asStateFlow()

    // UI state for search results - Primary state source for the UI
    private val _uiState = MutableStateFlow<SearchUIState>(SearchUIState.Initial)
    val uiState: StateFlow<SearchUIState> = _uiState.asStateFlow()

    // Recently viewed cocktails state
    private val _recentlyViewedCocktails = MutableStateFlow<List<CocktailMinimalInfo>>(emptyList())
    val recentlyViewedCocktails = _recentlyViewedCocktails.asStateFlow()

    init {
        observeSearchQuery()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeSearchQuery() {
        combine(
            searchQuery.debounce(500).distinctUntilChanged(), // Debounce and check for actual changes
            isSearchByIngredient
        ) { query, isByIngredient ->
            Pair(query, isByIngredient)
        }.onEach { (query, _) ->
            // Show loading state immediately when query is not blank
            if (query.isNotBlank()) {
                _uiState.value = SearchUIState.Loading
            }
        }.flatMapLatest { (query, isByIngredient) ->
            if (query.isBlank()) {
                // If query is blank, reset state to Initial (will show history/recently viewed)
                flowOf(Resource.Success<List<Cocktail>>(emptyList())) // Return empty success to map to Initial/Empty state
            } else {
                // Perform search based on type
                val searchFlow = if (isByIngredient) {
                    repository.searchCocktailsByIngredient(query)
                } else {
                    repository.searchCocktailsByName(query)
                }
                // Add error handling within the search flow
                searchFlow.catch { e -> 
                    emit(Resource.error("Search failed: ${e.message}")) 
                }
            }
        }.onEach { resource ->
            // Map the Resource result to the appropriate SearchUIState
            _uiState.value = when (resource) {
                is Resource.Loading -> SearchUIState.Loading // Should ideally not happen here due to outer loading state, but handle defensively
                is Resource.Success -> {
                    val cocktails = resource.data ?: emptyList()
                    if (cocktails.isEmpty() && searchQuery.value.isNotBlank()) {
                        SearchUIState.Empty // Search was performed but no results
                    } else if (cocktails.isEmpty() && searchQuery.value.isBlank()){
                        SearchUIState.Initial // Query is blank, show initial state
                    } else {
                        SearchUIState.Success(cocktails)
                    }
                }
                is Resource.Error -> SearchUIState.Error(resource.apiError.message ?: "Unknown error during search")
            }
        }.launchIn(viewModelScope) // Collect the flow within the ViewModel's scope
    }

    // Set search query
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    // Toggle search by ingredient
    fun toggleSearchByIngredient() {
        _isSearchByIngredient.update { !it }
        // Trigger search immediately when filter changes if query exists
        if (_searchQuery.value.isNotBlank()) {
             _searchQuery.value = _searchQuery.value // Re-emit current query to trigger combine
        }
    }

    // Add query to search history
    fun addToSearchHistory(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            val currentHistory = _searchHistory.value.toMutableList()
            currentHistory.remove(query) // Remove if exists to avoid duplicates
            currentHistory.add(0, query) // Add to the beginning
            _searchHistory.value = currentHistory.take(10) // Keep only the last 10 searches
        }
    }

    // Clear search history
    fun clearSearchHistory() {
        _searchHistory.value = emptyList()
    }

    // Add cocktail to recently viewed list
    fun addToRecentlyViewed(cocktail: Cocktail) {
         if (cocktail.id.isBlank()) return // Ensure valid ID
        
         viewModelScope.launch {
             val minimalInfo = CocktailMinimalInfo(cocktail.id, cocktail.name, cocktail.imageUrl)
             val currentList = _recentlyViewedCocktails.value.toMutableList()
             
             // Remove if exists (by ID) to bring it to the front
             currentList.removeAll { it.id == minimalInfo.id }
             
             // Add to the beginning
             currentList.add(0, minimalInfo)
             
             // Limit the list size (e.g., keep the 5 most recent)
             _recentlyViewedCocktails.value = currentList.take(5) 
         }
    }
    
    // Add favorite functionality
    fun saveFavorite(cocktail: Cocktail) {
        viewModelScope.launch {
            repository.saveFavorite(cocktail).collectLatest { /* Optional: Handle favorite result */ }
        }
    }
    
    // Remove favorite functionality
    fun removeFavorite(id: String) {
        viewModelScope.launch {
            repository.removeFavorite(id).collectLatest { /* Optional: Handle favorite result */ }
        }
    }
}

// Sealed class to represent different states of the search UI
sealed class SearchUIState {
    data object Initial : SearchUIState() // Nothing searched yet, show history or recently viewed
    data object Loading : SearchUIState() // Search in progress
    data class Success(val cocktails: List<Cocktail>) : SearchUIState() // Search successful with results
    data object Empty : SearchUIState() // Search successful but no results found
    data class Error(val message: String) : SearchUIState() // Search failed
} 