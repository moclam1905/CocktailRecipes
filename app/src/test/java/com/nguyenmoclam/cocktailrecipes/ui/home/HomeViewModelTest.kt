package com.nguyenmoclam.cocktailrecipes.ui.home

import app.cash.turbine.test
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import com.nguyenmoclam.cocktailrecipes.util.TestData
import com.nguyenmoclam.cocktailrecipes.util.TestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    // SUT
    private lateinit var viewModel: HomeViewModel

    // Dependencies
    private lateinit var cocktailRepository: CocktailRepository

    // Test rule
    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    @Before
    fun setUp() = runTest {
        cocktailRepository = mock()
        
        // Default stub for repository calls
        whenever(cocktailRepository.getPopularCocktails()).thenReturn(
            flowOf(Resource.Success(emptyList()))
        )
    }

    @Test
    fun `init loads cocktails`() = runTest {
        // Arrange
        val cocktails = TestData.cocktailList
        whenever(cocktailRepository.getPopularCocktails()).thenReturn(
            flowOf(Resource.Success(cocktails))
        )
        
        // Act
        viewModel = HomeViewModel(cocktailRepository)
        
        // Allow coroutines to complete
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.uiState.value
        assertEquals(cocktails, state.cocktails)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `loadCocktails updates state with loading and success`() = runTest {
        // Arrange
        val cocktails = TestData.cocktailList
        whenever(cocktailRepository.getPopularCocktails()).thenReturn(
            flowOf(Resource.Loading, Resource.Success(cocktails))
        )
        
        // Act
        viewModel = HomeViewModel(cocktailRepository)
        
        // Assert
        viewModel.uiState.test {
            // Skip initial state
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)
            
            // Get final state after loading completes
            val loadedState = awaitItem()
            assertEquals(cocktails, loadedState.cocktails)
            assertFalse(loadedState.isLoading)
            assertNull(loadedState.error)
            
            // No more emissions
            cancel()
        }
    }

    @Test
    fun `loadCocktails handles errors`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        whenever(cocktailRepository.getPopularCocktails()).thenReturn(
            flowOf(Resource.Loading, Resource.error(errorMessage))
        )
        
        // Act
        viewModel = HomeViewModel(cocktailRepository)
        
        // Allow coroutines to complete
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.uiState.value
        assertTrue(state.cocktails.isEmpty())
        assertFalse(state.isLoading)
        assertNotNull(state.error)
    }

    @Test
    fun `refreshCocktails updates isRefreshing state`() = runTest {
        // Arrange
        val cocktails = TestData.cocktailList
        whenever(cocktailRepository.getPopularCocktails())
            .thenReturn(flowOf(Resource.Success(emptyList()))) // For init
            .thenReturn(flowOf(Resource.Success(cocktails))) // For refresh
        
        viewModel = HomeViewModel(cocktailRepository)
        advanceUntilIdle()
        
        // Act
        viewModel.processEvent(HomeEvent.RefreshCocktails)
        
        // Assert
        viewModel.uiState.test {
            val refreshingState = awaitItem()
            assertTrue(refreshingState.isRefreshing)
            
            val refreshedState = awaitItem()
            assertEquals(cocktails, refreshedState.cocktails)
            assertFalse(refreshedState.isRefreshing)
            
            cancel()
        }
    }

    @Test
    fun `toggleFavorite updates cocktail favorite status`() = runTest {
        // Arrange
        val cocktails = TestData.cocktailList
        val cocktailToToggle = cocktails.first()
        val cocktailId = cocktailToToggle.id
        
        whenever(cocktailRepository.getPopularCocktails()).thenReturn(
            flowOf(Resource.Success(cocktails))
        )
        
        whenever(cocktailRepository.saveFavorite(cocktailToToggle)).thenReturn(
            flowOf(Resource.Success(true))
        )
        
        viewModel = HomeViewModel(cocktailRepository)
        advanceUntilIdle()
        
        // Act
        viewModel.processEvent(HomeEvent.ToggleFavorite(cocktailId))
        advanceUntilIdle()
        
        // Assert
        val updatedCocktail = viewModel.uiState.value.cocktails.first { it.id == cocktailId }
        assertEquals(!cocktailToToggle.isFavorite, updatedCocktail.isFavorite)
    }
} 