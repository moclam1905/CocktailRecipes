package com.nguyenmoclam.cocktailrecipes.ui.ingredients

import app.cash.turbine.test
import com.nguyenmoclam.cocktailrecipes.data.common.ApiError
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.model.IngredientItem
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

@ExperimentalCoroutinesApi
class IngredientExplorerViewModelTest {

    @Mock
    private lateinit var repository: CocktailRepository
    
    private lateinit var viewModel: IngredientExplorerViewModel
    
    private val testDispatcher = StandardTestDispatcher()
    
    private val testIngredients = listOf(
        IngredientItem(
            id = "1",
            name = "Vodka",
            description = "Clear spirit",
            type = "Spirit",
            isAlcoholic = true
        ),
        IngredientItem(
            id = "2",
            name = "Orange Juice",
            description = "Fruit juice",
            type = "Juice",
            isAlcoholic = false
        )
    )
    
    private val testCocktails = listOf(
        Cocktail(
            id = "1",
            name = "Screwdriver",
            imageUrl = "",
            instructions = "Mix vodka and orange juice",
            ingredients = emptyList(),
            isFavorite = false
        )
    )
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Mock repository responses using runTest for suspend functions
        runTest {
            `when`(repository.getAllIngredients(any())).thenReturn(flowOf(Resource.Success(testIngredients)))
            `when`(repository.getCocktailsByIngredient(any())).thenReturn(flowOf(Resource.Success(testCocktails)))
        }
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `loadIngredients should update state with ingredients`() = runTest(testDispatcher) {
        // Mock repository responses inside runTest
        `when`(repository.getAllIngredients(any())).thenReturn(flowOf(Resource.Success(testIngredients)))
        
        // Given
        viewModel = IngredientExplorerViewModel(repository)
        
        // When
        viewModel.uiState.test {
            // Initial loading state
            val initialState = awaitItem()
            assert(initialState.isLoading)
            
            // Success state with ingredients
            val successState = awaitItem()
            assertEquals(testIngredients, successState.ingredients)
            assertEquals(false, successState.isLoading)
            assertNull(successState.error)
            
            cancelAndConsumeRemainingEvents()
        }
        
        // Then
        verify(repository).getAllIngredients(false)
    }
    
    @Test
    fun `selectIngredient should update state with selected ingredient and cocktails`() = runTest(testDispatcher) {
        // Mock repository responses inside runTest
        `when`(repository.getAllIngredients(any())).thenReturn(flowOf(Resource.Success(testIngredients)))
        `when`(repository.getCocktailsByIngredient(any())).thenReturn(flowOf(Resource.Success(testCocktails)))
        
        // Given
        viewModel = IngredientExplorerViewModel(repository)
        
        // When
        viewModel.selectIngredient(testIngredients[0])
        
        // Then
        viewModel.uiState.test {
            // Initial loading state might be skipped depending on timing
            val state = expectMostRecentItem()
            assertEquals(testIngredients[0], state.selectedIngredient)
            assertEquals(testCocktails, state.cocktails)
        }
        
        verify(repository).getCocktailsByIngredient(testIngredients[0].name)
    }
    
    @Test
    fun `clearSelectedIngredient should reset selected ingredient and cocktails`() = runTest(testDispatcher) {
        // Mock repository responses inside runTest
        `when`(repository.getAllIngredients(any())).thenReturn(flowOf(Resource.Success(testIngredients)))
        `when`(repository.getCocktailsByIngredient(any())).thenReturn(flowOf(Resource.Success(testCocktails)))
        
        // Given
        viewModel = IngredientExplorerViewModel(repository)
        viewModel.selectIngredient(testIngredients[0]) // Select first to then clear
        
        // Wait for the selection to process
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.clearSelectedIngredient()
        
        // Then
        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertNull(state.selectedIngredient)
            assert(state.cocktails.isEmpty())
        }
    }
    
    @Test
    fun `loadIngredients should handle error`() = runTest(testDispatcher) {
        // Given
        val errorMessage = "Network error"
        `when`(repository.getAllIngredients(any())).thenReturn(
            flowOf(Resource.error(ApiError.networkError(errorMessage)))
        )
        
        // When
        viewModel = IngredientExplorerViewModel(repository)
        
        // Then
        viewModel.uiState.test {
            // Initial loading state
            val initialState = awaitItem()
            assert(initialState.isLoading)
            
            // Error state
            val errorState = awaitItem()
            assertEquals(errorMessage, errorState.error)
            assertEquals(false, errorState.isLoading)
            
            cancelAndConsumeRemainingEvents()
        }
    }
} 