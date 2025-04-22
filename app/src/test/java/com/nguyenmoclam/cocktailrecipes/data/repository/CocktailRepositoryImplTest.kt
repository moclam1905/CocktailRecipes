package com.nguyenmoclam.cocktailrecipes.data.repository

// Coroutine & Testing Utilities
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

// JUnit Assertions and Annotations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// Mockito-Kotlin for mocking
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.times
import org.mockito.kotlin.never

// Project specific classes
import com.nguyenmoclam.cocktailrecipes.data.common.NetworkMonitor
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.data.local.CocktailLocalDataSource
import com.nguyenmoclam.cocktailrecipes.data.local.FavoritesLocalDataSource
import com.nguyenmoclam.cocktailrecipes.data.model.DrinkListResponse
import com.nguyenmoclam.cocktailrecipes.data.remote.CocktailRemoteDataSource
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.util.TestData
import com.nguyenmoclam.cocktailrecipes.util.TestDispatcherRule

@ExperimentalCoroutinesApi
class CocktailRepositoryImplTest {

    // SUT (System Under Test)
    private lateinit var repository: CocktailRepositoryImpl

    // Dependencies (Mocks)
    private lateinit var remoteDataSource: CocktailRemoteDataSource
    private lateinit var localDataSource: CocktailLocalDataSource
    private lateinit var favoritesLocalDataSource: FavoritesLocalDataSource
    private lateinit var networkMonitor: NetworkMonitor

    // Test rule for managing Coroutine Dispatchers
    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    @Before
    fun setUp() {
        // Initialize mocks using Mockito-Kotlin's mock()
        remoteDataSource = mock()
        localDataSource = mock()
        favoritesLocalDataSource = mock()
        networkMonitor = mock()

        // Create repository instance with mocked dependencies
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
            assertTrue(awaitItem() is Resource.Loading)
            val successResult = awaitItem() as Resource.Success
            assertEquals(2, successResult.data.size)
            awaitComplete()
        }

        // Verify
        verify(localDataSource, times(1)).isCacheValid()
        verify(localDataSource, times(1)).getCocktails()
        verify(remoteDataSource, never()).getPopularCocktails()
        verify(localDataSource, never()).invalidateCache()
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
            assertTrue(awaitItem() is Resource.Loading)
            val successResult = awaitItem() as Resource.Success
            assertTrue(successResult.data.isNotEmpty())
            awaitComplete()
        }

        // Verify
        verify(localDataSource, times(1)).isCacheValid()
        verify(remoteDataSource, times(1)).getPopularCocktails()
        verify(localDataSource, times(1)).saveCocktails(any()) // Verify caching happens
        verify(localDataSource, never()).invalidateCache()
    }
    
    @Test
    fun `getPopularCocktails with forceRefresh invalidates cache and fetches from remote`() = runTest {
        // Arrange
        val remoteResponse = TestData.drinkListResponse
        
        whenever(networkMonitor.isNetworkAvailable()).thenReturn(true)
        whenever(remoteDataSource.getPopularCocktails()).thenReturn(Resource.Success(remoteResponse))
        whenever(localDataSource.isCacheValid()).thenReturn(true)

        // Act & Assert
        repository.getPopularCocktails(forceRefresh = true).test {
            assertTrue(awaitItem() is Resource.Loading)
            val successResult = awaitItem() as Resource.Success
            assertTrue(successResult.data.isNotEmpty())
            awaitComplete()
        }

        // Verify
        verify(localDataSource, times(1)).invalidateCache()
        verify(remoteDataSource, times(1)).getPopularCocktails()
        verify(localDataSource, times(1)).saveCocktails(any()) // Verify caching happens
        verify(localDataSource, never()).getCocktails() // Should not read from cache
    }

    @Test
    fun `getPopularCocktails returns error when offline and cache invalid`() = runTest {
        // Arrange
        whenever(localDataSource.isCacheValid()).thenReturn(false)
        whenever(networkMonitor.isNetworkAvailable()).thenReturn(false)

        // Act & Assert
        repository.getPopularCocktails().test {
            assertTrue(awaitItem() is Resource.Loading)
            val errorResult = awaitItem() as Resource.Error
            assertNotNull(errorResult.apiError)
            assertTrue(errorResult.apiError.message.contains("No internet connection"))
            awaitComplete()
        }
    }
    
    @Test
    fun `searchCocktailsByName with forceRefresh invalidates cache and fetches from remote`() = runTest {
        // Arrange
        val query = "Mojito"
        val remoteResponse = TestData.drinkListResponse // Assuming this contains Mojito
        whenever(networkMonitor.isNetworkAvailable()).thenReturn(true)
        whenever(remoteDataSource.searchCocktailsByName(query)).thenReturn(Resource.Success(remoteResponse))

        // Act & Assert
        repository.searchCocktailsByName(query, forceRefresh = true).test {
            assertTrue(awaitItem() is Resource.Loading)
            val successResult = awaitItem() as Resource.Success
            assertTrue(successResult.data.isNotEmpty())
            awaitComplete()
        }

        // Verify
        verify(localDataSource, times(1)).invalidateCache()
        verify(remoteDataSource, times(1)).searchCocktailsByName(query)
        verify(localDataSource, times(1)).saveCocktails(any())
        verify(localDataSource, never()).searchCocktailsByName(query) // Should not search locally first
    }
    
    @Test
    fun `getCocktailDetails with forceRefresh invalidates specific cache and fetches from remote`() = runTest {
        // Arrange
        val id = "11007"
        val remoteResponse = TestData.drinkListResponse // Assuming this contains Mojito (ID 11007)
        val mojitoEntity = TestData.mojitoCocktailEntity

        whenever(networkMonitor.isNetworkAvailable()).thenReturn(true)
        whenever(remoteDataSource.getCocktailDetails(id)).thenReturn(Resource.Success(remoteResponse))
        whenever(localDataSource.isCacheValid(id)).thenReturn(true)
        whenever(localDataSource.getCocktailById(id)).thenReturn(mojitoEntity)

        // Act & Assert
        repository.getCocktailDetails(id, forceRefresh = true).test {
            assertTrue(awaitItem() is Resource.Loading)
            val successResult = awaitItem() as Resource.Success
            assertNotNull(successResult.data)
            assertEquals(id, successResult.data.id)
            awaitComplete()
        }

        // Verify
        verify(localDataSource, times(1)).invalidateCache(id)
        verify(remoteDataSource, times(1)).getCocktailDetails(id)
        verify(localDataSource, times(1)).saveCocktail(any())
        verify(localDataSource, times(1)).isCacheValid(id) // Checked before deciding fetch strategy
        verify(localDataSource, never()).isFavorite(id) // isFavorite check happens after successful fetch in this path
    }
    
    @Test
    fun `invalidateAllCaches calls localDataSource invalidateCache`() = runTest {
        // Arrange
        // No specific arrangement needed, just verify the call

        // Act & Assert
        repository.invalidateAllCaches().test {
            assertTrue(awaitItem() is Resource.Loading)
            val successResult = awaitItem() as Resource.Success
            assertTrue(successResult.data)
            awaitComplete()
        }
        
        // Verify
        verify(localDataSource, times(1)).invalidateCache()
    }

    @Test
    fun `saveFavorite adds cocktail to favorites`() = runTest {
        // Arrange
        val cocktail = TestData.mojitoCocktail
        
        // Act & Assert
        repository.saveFavorite(cocktail).test {
            assertTrue(awaitItem() is Resource.Loading)
            val successResult = awaitItem() as Resource.Success
            assertTrue(successResult.data)
            awaitComplete()
        }

        // Verify
        verify(favoritesLocalDataSource, times(1)).saveFavorite(cocktail)
        verify(localDataSource, times(1)).addFavorite(cocktail.id)
    }
} 