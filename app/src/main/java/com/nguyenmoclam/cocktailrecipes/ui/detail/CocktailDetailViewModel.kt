package com.nguyenmoclam.cocktailrecipes.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CocktailDetailViewModel @Inject constructor(
    private val cocktailRepository: CocktailRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state

    fun loadCocktailDetails(id: String) {
        viewModelScope.launch {
            cocktailRepository.getCocktailDetails(id)
                .onStart { _state.value = UiState.Loading }
                .catch { error -> _state.value = UiState.Error(error.message ?: "Unknown error") }
                .collect { resource ->
                    _state.value = when (resource) {
                        is Resource.Success -> UiState.Success(resource.data)
                        is Resource.Error -> UiState.Error(resource.apiError.message)
                        is Resource.Loading -> UiState.Loading
                    }
                }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val data: Cocktail) : UiState()
        data class Error(val message: String) : UiState()
    }
} 