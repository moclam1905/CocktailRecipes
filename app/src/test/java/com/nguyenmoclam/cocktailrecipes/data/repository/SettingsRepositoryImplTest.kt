package com.nguyenmoclam.cocktailrecipes.data.repository

import android.content.Context
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.data.local.AppDatabase
import com.nguyenmoclam.cocktailrecipes.data.local.PreferencesManager
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
class SettingsRepositoryImplTest {

    @Mock
    lateinit var preferencesManager: PreferencesManager
    
    @Mock
    lateinit var appDatabase: AppDatabase
    
    @Mock
    lateinit var cocktailRepository: CocktailRepository
    
    @Mock
    lateinit var context: Context // Mock Context if needed, RelaxedMockK equivalent isn't standard Mockito

    private lateinit var settingsRepository: SettingsRepositoryImpl

    @Before
    fun setUp() {
        // MockitoJUnitRunner handles initialization
        settingsRepository = SettingsRepositoryImpl(
            preferencesManager,
            appDatabase,
            cocktailRepository,
            context
        )
    }
    
    @Test
    fun `clearAppCache calls database clear and preferences set`() = runTest {
        settingsRepository.clearAppCache()
        
        // Use verify from mockito-kotlin
        verify(appDatabase).clearAllTables()
        verify(preferencesManager).setCacheCleared(eq(true)) // Use eq() for specific values
    }
    
    @Test
    fun `clearAppCache returns true on success`() = runTest {
        // Mock suspend functions using whenever
        whenever(appDatabase.clearAllTables()).thenReturn(Unit) // Mock void suspend fun
        whenever(preferencesManager.setCacheCleared(any())).thenReturn(Unit) // Mock void suspend fun
        
        val result = settingsRepository.clearAppCache()
        
        assertTrue(result)
    }
    
    @Test
    fun `clearAppCache returns false on database exception`() = runTest {
        val exception = RuntimeException("DB Error")
        // Mock throwing exception for suspend function
        whenever(appDatabase.clearAllTables()).thenThrow(exception)
        
        val result = settingsRepository.clearAppCache()
        
        assertFalse(result)
        // Verify preferencesManager.setCacheCleared was never called
        verify(preferencesManager, never()).setCacheCleared(any())
    }
    
    @Test
    fun `clearApiCache calls cocktailRepository invalidateAllCaches`() = runTest {
        // Mock suspend function returning Flow
        whenever(cocktailRepository.invalidateAllCaches()).thenReturn(flowOf(Resource.Success(true)))
        
        settingsRepository.clearApiCache()
        
        verify(cocktailRepository).invalidateAllCaches()
    }
    
    @Test
    fun `clearApiCache returns true when repository call succeeds`() = runTest {
        whenever(cocktailRepository.invalidateAllCaches()).thenReturn(flowOf(Resource.Success(true)))
        
        val result = settingsRepository.clearApiCache()
        
        assertTrue(result)
    }
    
    @Test
    fun `clearApiCache returns false when repository call returns false`() = runTest {
        whenever(cocktailRepository.invalidateAllCaches()).thenReturn(flowOf(Resource.Success(false)))
        
        val result = settingsRepository.clearApiCache()
        
        assertFalse(result)
    }
    
    @Test
    fun `clearApiCache returns false when repository call returns error`() = runTest {
        whenever(cocktailRepository.invalidateAllCaches()).thenReturn(flowOf(Resource.error("API Cache Error")))
        
        val result = settingsRepository.clearApiCache()
        
        assertFalse(result) // Correct based on updated implementation logic
    }
    
    @Test
    fun `clearApiCache returns false on repository exception`() = runTest {
        val exception = RuntimeException("Repo Error")
        whenever(cocktailRepository.invalidateAllCaches()).thenThrow(exception)
        
        val result = settingsRepository.clearApiCache()
        
        assertFalse(result)
    }
    
    // Add tests for getThemeMode, setThemeMode, getAppVersion if needed
}