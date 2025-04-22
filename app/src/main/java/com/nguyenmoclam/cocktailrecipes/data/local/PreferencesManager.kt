package com.nguyenmoclam.cocktailrecipes.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cocktail_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    // Theme preferences
    val themeMode: Flow<String> = dataStore.data.map { preferences: Preferences ->
        preferences[THEME_MODE_KEY] ?: THEME_MODE_SYSTEM
    }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences: MutablePreferences ->
            preferences[THEME_MODE_KEY] = mode
        }
    }

    // Clear cache flag
    suspend fun setCacheCleared(cleared: Boolean) {
        dataStore.edit { preferences: MutablePreferences ->
            preferences[CACHE_CLEARED_KEY] = cleared
        }
    }

    val cacheCleared: Flow<Boolean> = dataStore.data.map { preferences: Preferences ->
        preferences[CACHE_CLEARED_KEY] ?: false
    }

    companion object {
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val CACHE_CLEARED_KEY = booleanPreferencesKey("cache_cleared")
        
        const val THEME_MODE_SYSTEM = "system"
        const val THEME_MODE_LIGHT = "light"
        const val THEME_MODE_DARK = "dark"
    }
} 