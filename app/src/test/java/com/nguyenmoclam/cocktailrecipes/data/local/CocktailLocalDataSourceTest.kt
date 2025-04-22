package com.nguyenmoclam.cocktailrecipes.data.local

import com.nguyenmoclam.cocktailrecipes.data.local.dao.CocktailDao
import com.nguyenmoclam.cocktailrecipes.data.local.entity.CocktailEntity
import com.nguyenmoclam.cocktailrecipes.data.local.entity.SimpleFavoriteCocktailEntity
import com.nguyenmoclam.cocktailrecipes.data.Ingredient
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest // Use runTest for coroutine testing
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.* // Import Mockito-Kotlin helpers
import java.util.concurrent.TimeUnit

// Use MockitoJUnitRunner to initialize mocks
@RunWith(MockitoJUnitRunner::class)
class CocktailLocalDataSourceTest {

    // Use @Mock annotation from Mockito
    @Mock
    lateinit var cocktailDao: CocktailDao

    private lateinit var localDataSource: CocktailLocalDataSource

    private val cacheValidityPeriod = TimeUnit.DAYS.toMillis(1)

    // Use @Captor for ArgumentCaptor
    @Captor
    private lateinit var cocktailCaptor: ArgumentCaptor<CocktailEntity>

    @Captor
    private lateinit var cocktailsCaptor: ArgumentCaptor<List<CocktailEntity>>

    @Before
    fun setUp() {
        // No need for MockKAnnotations.init(this) when using @RunWith(MockitoJUnitRunner::class)
        localDataSource = CocktailLocalDataSource(cocktailDao)
    }

    // Helper to create CocktailEntity with default values for non-tested fields
    private fun createCocktailEntity(
        id: String,
        name: String,
        lastUpdated: Long,
        imageUrl: String? = null, // Add defaults for missing fields
        instructions: String? = null,
        ingredients: List<Ingredient>? = null
    ): CocktailEntity {
        return CocktailEntity(
            id = id,
            name = name,
            imageUrl = imageUrl ?: "http://example.com/$id.jpg",
            instructions = instructions ?: "Instructions for $name",
            ingredients = ingredients ?: listOf(Ingredient("Ingredient1", "Measure1")),
            lastUpdated = lastUpdated
        )
    }

    @Test
    fun `isCacheValid returns true for recent cocktail`() = runTest { // Use runTest
        val recentTimestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)
        val cocktail = createCocktailEntity(id = "1", name = "Mojito", lastUpdated = recentTimestamp)
        // Use whenever from mockito-kotlin for suspend functions
        whenever(cocktailDao.getCocktailById("1")).thenReturn(cocktail)

        val isValid = localDataSource.isCacheValid("1")

        assertTrue(isValid)
    }

    @Test
    fun `isCacheValid returns false for stale cocktail`() = runTest {
        val staleTimestamp = System.currentTimeMillis() - cacheValidityPeriod - TimeUnit.HOURS.toMillis(1)
        val cocktail = createCocktailEntity(id = "1", name = "Mojito", lastUpdated = staleTimestamp)
        whenever(cocktailDao.getCocktailById("1")).thenReturn(cocktail)

        val isValid = localDataSource.isCacheValid("1")

        assertFalse(isValid)
    }

    @Test
    fun `isCacheValid returns false for non-existent cocktail`() = runTest {
        whenever(cocktailDao.getCocktailById("1")).thenReturn(null)

        val isValid = localDataSource.isCacheValid("1")

        assertFalse(isValid)
    }

    @Test
    fun `isCacheValid returns true if any cocktail in general cache is recent`() = runTest {
        val recentTimestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)
        val staleTimestamp = System.currentTimeMillis() - cacheValidityPeriod - TimeUnit.HOURS.toMillis(1)
        val cocktails = listOf(
            createCocktailEntity(id = "1", name = "Mojito", lastUpdated = staleTimestamp),
            createCocktailEntity(id = "2", name = "Daiquiri", lastUpdated = recentTimestamp)
        )
        // Use whenever for non-suspend functions returning Flow
        whenever(cocktailDao.getCocktails()).thenReturn(flowOf(cocktails))

        val isValid = localDataSource.isCacheValid()

        assertTrue(isValid)
    }

    @Test
    fun `isCacheValid returns false if all cocktails in general cache are stale`() = runTest {
        val staleTimestamp1 = System.currentTimeMillis() - cacheValidityPeriod - TimeUnit.HOURS.toMillis(1)
        val staleTimestamp2 = System.currentTimeMillis() - cacheValidityPeriod - TimeUnit.MINUTES.toMillis(30)
        val cocktails = listOf(
            createCocktailEntity(id = "1", name = "Mojito", lastUpdated = staleTimestamp1),
            createCocktailEntity(id = "2", name = "Daiquiri", lastUpdated = staleTimestamp2)
        )
        whenever(cocktailDao.getCocktails()).thenReturn(flowOf(cocktails))

        val isValid = localDataSource.isCacheValid()

        assertFalse(isValid)
    }

    @Test
    fun `isCacheValid returns false if general cache is empty`() = runTest {
        whenever(cocktailDao.getCocktails()).thenReturn(flowOf(emptyList()))

        val isValid = localDataSource.isCacheValid()

        assertFalse(isValid)
    }

    @Test
    fun `invalidateCache updates timestamp for specific cocktail`() = runTest {
        val initialTimestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)
        val cocktail = createCocktailEntity(id = "1", name = "Mojito", lastUpdated = initialTimestamp)

        whenever(cocktailDao.getCocktailById("1")).thenReturn(cocktail)
        // Mockito doesn't have a direct equivalent of 'just Runs', verify interaction instead
        // whenever(cocktailDao.insertCocktail(any())).then { } // Alternative if needed

        localDataSource.invalidateCache("1")

        // Use verify with captor
        verify(cocktailDao).insertCocktail(cocktailCaptor.capture())
        assertTrue(cocktailCaptor.value.lastUpdated < (System.currentTimeMillis() - cacheValidityPeriod))
        assertEquals("1", cocktailCaptor.value.id)
    }

    @Test
    fun `invalidateCache does nothing if specific cocktail not found`() = runTest {
        whenever(cocktailDao.getCocktailById("1")).thenReturn(null)

        localDataSource.invalidateCache("1")

        // Use verify with never()
        verify(cocktailDao, never()).insertCocktail(any())
    }

    @Test
    fun `invalidateCache updates timestamps for all cocktails when no ID provided`() = runTest {
        val timestamp1 = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)
        val timestamp2 = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30)
        val cocktails = listOf(
            createCocktailEntity(id = "1", name = "Mojito", lastUpdated = timestamp1),
            createCocktailEntity(id = "2", name = "Daiquiri", lastUpdated = timestamp2)
        )

        whenever(cocktailDao.getCocktails()).thenReturn(flowOf(cocktails))

        localDataSource.invalidateCache()

        verify(cocktailDao).insertCocktails(cocktailsCaptor.capture())
        assertEquals(2, cocktailsCaptor.value.size)
        assertTrue(cocktailsCaptor.value.all { it.lastUpdated < (System.currentTimeMillis() - cacheValidityPeriod) })
    }

    @Test
    fun `invalidateCache does nothing for all cocktails if cache is empty`() = runTest {
        whenever(cocktailDao.getCocktails()).thenReturn(flowOf(emptyList()))

        localDataSource.invalidateCache()

        verify(cocktailDao, never()).insertCocktails(any())
    }

    // Add tests for saveCocktails, getCocktailById, searchCocktailsByName, add/remove/is Favorite etc. if needed
} 