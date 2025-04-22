package com.nguyenmoclam.cocktailrecipes.ui.settings

import app.cash.turbine.test
import com.nguyenmoclam.cocktailrecipes.data.local.PreferencesManager
import com.nguyenmoclam.cocktailrecipes.domain.repository.SettingsRepository
import com.nguyenmoclam.cocktailrecipes.util.TestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var repository: SettingsRepository
    private lateinit var viewModel: SettingsViewModel

    private val testAppVersion = "1.0.0 (1)"

    @Before
    fun setup() {
        repository = mock {
            onBlocking { getAppVersion() } doReturn testAppVersion
            on { getThemeMode() } doReturn flowOf(PreferencesManager.THEME_MODE_SYSTEM)
        }
        viewModel = SettingsViewModel(repository)
    }

    @Test
    fun `initial state has correct values`() = runTest {
        val initialState = viewModel.uiState.value
        
        assertEquals(PreferencesManager.THEME_MODE_SYSTEM, initialState.themeMode)
        assertEquals("", initialState.appVersion) // App version is loaded async
        assertFalse(initialState.isCacheClearing)
        assertFalse(initialState.cacheCleared)
        assertFalse(initialState.showClearCacheConfirmation)
    }

    @Test
    fun `loads app version on init`() = runTest {
        // Allow time for the viewModel to load the app version
        viewModel.uiState.test {
            val initialState = awaitItem()
            // Skip first emission if it doesn't have the version yet
            if (initialState.appVersion.isEmpty()) {
                val stateWithVersion = awaitItem()
                assertEquals(testAppVersion, stateWithVersion.appVersion)
            } else {
                assertEquals(testAppVersion, initialState.appVersion)
            }
        }
    }

    @Test
    fun `setThemeMode updates repository`() = runTest {
        val themeMode = PreferencesManager.THEME_MODE_DARK
        viewModel.setThemeMode(themeMode)
        
        verify(repository).setThemeMode(themeMode)
    }

    @Test
    fun `showClearCacheConfirmation updates state`() = runTest {
        viewModel.showClearCacheConfirmation()
        
        assertTrue(viewModel.uiState.value.showClearCacheConfirmation)
    }

    @Test
    fun `dismissClearCacheConfirmation updates state`() = runTest {
        viewModel.showClearCacheConfirmation()
        viewModel.dismissClearCacheConfirmation()
        
        assertFalse(viewModel.uiState.value.showClearCacheConfirmation)
    }

    @Test
    fun `clearCache calls repository and updates state`() = runTest {
        whenever(repository.clearAppCache()).thenReturn(true)
        
        viewModel.clearCache()
        
        viewModel.uiState.test {
            // First state might be with isCacheClearing = true
            val initialItem = awaitItem()
            if (initialItem.isCacheClearing) {
                val nextItem = awaitItem()
                assertFalse(nextItem.isCacheClearing)
                assertTrue(nextItem.cacheCleared)
            } else {
                assertFalse(initialItem.isCacheClearing)
                assertTrue(initialItem.cacheCleared)
            }
        }
        
        verify(repository).clearAppCache()
    }
} 