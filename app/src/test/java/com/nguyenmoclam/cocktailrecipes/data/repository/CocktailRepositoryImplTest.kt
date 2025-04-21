package com.nguyenmoclam.cocktailrecipes.data.repository

import app.cash.turbine.test
import com.nguyenmoclam.cocktailrecipes.data.common.NetworkMonitor
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.data.local.CocktailLocalDataSource
import com.nguyenmoclam.cocktailrecipes.data.local.FavoritesLocalDataSource
import com.nguyenmoclam.cocktailrecipes.data.local.entity.CocktailEntity
import com.nguyenmoclam.cocktailrecipes.data.model.DrinkListResponse
import com.nguyenmoclam.cocktailrecipes.data.remote.CocktailRemoteDataSource
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.util.TestData
import com.nguyenmoclam.cocktailrecipes.util.TestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.times

@ExperimentalCoroutinesApi
class CocktailRepositoryImplTest {

    // SUT
    private lateinit var repository: CocktailRepositoryImpl

    // Dependencies
    private lateinit var remoteDataSource: CocktailRemoteDataSource
    private lateinit var localDataSource: CocktailLocalDataSource
    private lateinit var favoritesLocalDataSource: FavoritesLocalDataSource
    private lateinit var networkMonitor: NetworkMonitor

    // Test rule
    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    @Before
    fun setUp() {
        // Initialize mocks
        remoteDataSource = mock()
        localDataSource = mock()
        favoritesLocalDataSource = mock()
        networkMonitor = mock()

        // Create repository with mocks
        repository = CocktailRepositoryImpl(
            remoteDataSource,
            localDataSource,
            favoritesLocalDataSource,
            networkMonitor
        )
    }

    @Test
    fun `getPopularCocktails returns cached data when cache is valid`() = runTest {
        // Arrange
        val cachedEntities = listOf(TestData.mojitoCocktailEntity, TestData.margaritaCocktailEntity)
        
        whenever(localDataSource.isCacheValid()).thenReturn(true)
        whenever(localDataSource.getCocktails()).thenReturn(flowOf(cachedEntities))
        whenever(networkMonitor.isNetworkAvailable()).thenReturn(true)

        // Act & Assert
        repository.getPopularCocktails().test {
            // First emission should be Loading
            assertTrue(awaitItem() is Resource.Loading)
            
            // Second emission should be Success with cached data
            val successResult = awaitItem() as Resource.Success
            assertEquals(2, successResult.data.size)
            
            // No more emissions
            awaitComplete()
        }

        // Verify local data source was called but not remote
        verify(localDataSource, times(1)).isCacheValid()
        verify(localDataSource, times(1)).getCocktails()
        verify(remoteDataSource, times(0)).getPopularCocktails()
    }

    @Test
    fun `getPopularCocktails fetches from remote when cache is not valid`() = runTest {
        // Arrange
        val remoteResponse = TestData.drinkListResponse
        val entities = listOf(TestData.mojitoCocktailEntity, TestData.margaritaCocktailEntity)
        
        whenever(localDataSource.isCacheValid()).thenReturn(false)
        whenever(networkMonitor.isNetworkAvailable()).thenReturn(true)
        whenever(remoteDataSource.getPopularCocktails()).thenReturn(Resource.Success(remoteResponse))

        // Act & Assert
        repository.getPopularCocktails().test {
            // First emission should be Loading
            assertTrue(awaitItem() is Resource.Loading)
            
            // Second emission should be Success with remote data
            val successResult = awaitItem() as Resource.Success
            assertTrue(successResult.data.isNotEmpty())
            
            // No more emissions
            awaitComplete()
        }

        // Verify both sources were called
        verify(localDataSource, times(1)).isCacheValid()
        verify(remoteDataSource, times(1)).getPopularCocktails()
    }

    @Test
    fun `getPopularCocktails returns error when offline and cache invalid`() = runTest {
        // Arrange
        whenever(localDataSource.isCacheValid()).thenReturn(false)
        whenever(networkMonitor.isNetworkAvailable()).thenReturn(false)

        // Act & Assert
        repository.getPopularCocktails().test {
            // First emission should be Loading
            assertTrue(awaitItem() is Resource.Loading)
            
            // Second emission should be Error
            val errorResult = awaitItem() as Resource.Error
            assertNotNull(errorResult.apiError)
            assertTrue(errorResult.apiError.message.contains("No internet connection"))
            
            // No more emissions
            awaitComplete()
        }
    }

    @Test
    fun `saveFavorite adds cocktail to favorites`() = runTest {
        // Arrange
        val cocktail = TestData.mojitoCocktail
        
        // Act & Assert
        repository.saveFavorite(cocktail).test {
            // First emission should be Loading
            assertTrue(awaitItem() is Resource.Loading)
            
            // Second emission should be Success
            val successResult = awaitItem() as Resource.Success
            assertTrue(successResult.data)
            
            // No more emissions
            awaitComplete()
        }

        // Verify both favorite systems were updated
        verify(favoritesLocalDataSource, times(1)).saveFavorite(cocktail)
        verify(localDataSource, times(1)).addFavorite(cocktail.id)
    }
} 