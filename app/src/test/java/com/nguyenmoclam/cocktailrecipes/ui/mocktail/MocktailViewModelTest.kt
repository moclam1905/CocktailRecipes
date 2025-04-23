package com.nguyenmoclam.cocktailrecipes.ui.mocktail

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
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

@ExperimentalCoroutinesApi
class MocktailViewModelTest {

    // SUT
    private lateinit var viewModel: MocktailViewModel

    // Dependencies
    private lateinit var cocktailRepository: CocktailRepository

    // Test rule
    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    @Before
    fun setUp() = runTest {
        cocktailRepository = mock()
        
        // Default stub for repository calls
        whenever(cocktailRepository.getCocktailsByAlcoholicFilter(any())).thenReturn(
            flowOf(Resource.Success(emptyList()))
        )
    }

    @Test
    fun `init loads mocktails`() = runTest {
        // Arrange
        val mocktails = TestData.cocktailList
        whenever(cocktailRepository.getCocktailsByAlcoholicFilter(eq("Non_Alcoholic"))).thenReturn(
            flowOf(Resource.Success(mocktails))
        )
        
        // Act
        viewModel = MocktailViewModel(cocktailRepository)
        
        // Allow coroutines to complete
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.uiState.value
        assertEquals(mocktails, state.mocktails)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `loadMocktails updates state with loading and success`() = runTest {
        // Arrange
        val mocktails = TestData.cocktailList
        whenever(cocktailRepository.getCocktailsByAlcoholicFilter(eq("Non_Alcoholic"))).thenReturn(
            flowOf(Resource.Loading, Resource.Success(mocktails))
        )
        
        // Act
        viewModel = MocktailViewModel(cocktailRepository)
        
        // Assert
        viewModel.uiState.test {
            // Skip initial state
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)
            
            // Get final state after loading completes
            val loadedState = awaitItem()
            assertEquals(mocktails, loadedState.mocktails)
            assertFalse(loadedState.isLoading)
            assertNull(loadedState.error)
            
            // No more emissions
            cancel()
        }
    }

    @Test
    fun `loadMocktails handles errors`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        whenever(cocktailRepository.getCocktailsByAlcoholicFilter(eq("Non_Alcoholic"))).thenReturn(
            flowOf(Resource.Loading, Resource.error(errorMessage))
        )
        
        // Act
        viewModel = MocktailViewModel(cocktailRepository)
        
        // Allow coroutines to complete
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.uiState.value
        assertTrue(state.mocktails.isEmpty())
        assertFalse(state.isLoading)
        assertNotNull(state.error)
    }

    @Test
    fun `refreshMocktails updates isRefreshing state`() = runTest {
        // Arrange
        val mocktails = TestData.cocktailList
        whenever(cocktailRepository.getCocktailsByAlcoholicFilter(eq("Non_Alcoholic")))
            .thenReturn(flowOf(Resource.Success(emptyList()))) // For init
        
        whenever(cocktailRepository.getCocktailsByAlcoholicFilter(eq("Non_Alcoholic")))
            .thenReturn(flowOf(Resource.Success(mocktails))) // For refresh
        
        viewModel = MocktailViewModel(cocktailRepository)
        advanceUntilIdle()
        
        // Act
        viewModel.handleEvent(MocktailEvent.RefreshMocktails)
        
        // Assert
        viewModel.uiState.test {
            val refreshingState = awaitItem()
            assertTrue(refreshingState.isRefreshing)
            
            val refreshedState = awaitItem()
            assertEquals(mocktails, refreshedState.mocktails)
            assertFalse(refreshedState.isRefreshing)
            
            cancel()
        }
    }

    @Test
    fun `toggleFavorite updates cocktail favorite status`() = runTest {
        // Arrange
        val mocktails = TestData.cocktailList
        val cocktailToToggle = mocktails.first()
        val cocktailId = cocktailToToggle.id
        
        whenever(cocktailRepository.getCocktailsByAlcoholicFilter(eq("Non_Alcoholic"),)).thenReturn(
            flowOf(Resource.Success(mocktails))
        )
        
        whenever(cocktailRepository.isFavorite(cocktailId)).thenReturn(
            flowOf(Resource.Success(false))
        )
        
        whenever(cocktailRepository.saveFavorite(any())).thenReturn(
            flowOf(Resource.Success(true))
        )
        
        viewModel = MocktailViewModel(cocktailRepository)
        advanceUntilIdle()
        
        // Act
        viewModel.handleEvent(MocktailEvent.ToggleFavorite(cocktailId))
        advanceUntilIdle()
        
        // Assert
        val updatedCocktail = viewModel.uiState.value.mocktails.first { it.id == cocktailId }
        assertTrue(updatedCocktail.isFavorite)
        verify(cocktailRepository).saveFavorite(any())
    }
    
    @Test
    fun `toggleAnimations updates animation state`() = runTest {
        // Arrange
        whenever(cocktailRepository.getCocktailsByAlcoholicFilter(any())).thenReturn(
            flowOf(Resource.Success(emptyList()))
        )
        
        viewModel = MocktailViewModel(cocktailRepository)
        advanceUntilIdle()
        
        // Act - Toggle animations off
        viewModel.handleEvent(MocktailEvent.ToggleAnimations(false))
        
        // Assert
        assertFalse(viewModel.uiState.value.showAnimations)
        
        // Act - Toggle animations back on
        viewModel.handleEvent(MocktailEvent.ToggleAnimations(true))
        
        // Assert
        assertTrue(viewModel.uiState.value.showAnimations)
    }
}