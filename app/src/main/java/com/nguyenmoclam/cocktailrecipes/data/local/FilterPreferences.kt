package com.nguyenmoclam.cocktailrecipes.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nguyenmoclam.cocktailrecipes.domain.model.CocktailFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "filter_preferences")

/**
 * Class that handles saving and retrieving filter preferences using DataStore
 * Managed as a singleton by Hilt.
 */
@Singleton
class FilterPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object PreferencesKeys {
        val SELECTED_CATEGORY = stringPreferencesKey("selected_category")
        val SELECTED_GLASS = stringPreferencesKey("selected_glass")
        val SELECTED_ALCOHOLIC = stringPreferencesKey("selected_alcoholic")
    }

    /**
     * Get saved filter preferences as a Flow of CocktailFilter
     */
    val filterFlow: Flow<CocktailFilter> = context.dataStore.data.map { preferences ->
        CocktailFilter(
            category = preferences[PreferencesKeys.SELECTED_CATEGORY],
            glass = preferences[PreferencesKeys.SELECTED_GLASS],
            alcoholic = preferences[PreferencesKeys.SELECTED_ALCOHOLIC]
        )
    }

    /**
     * Save filter preferences to DataStore
     */
    suspend fun saveFilter(filter: CocktailFilter) {
        context.dataStore.edit { preferences ->
            filter.category?.let { preferences[PreferencesKeys.SELECTED_CATEGORY] = it }
            filter.glass?.let { preferences[PreferencesKeys.SELECTED_GLASS] = it }
            filter.alcoholic?.let { preferences[PreferencesKeys.SELECTED_ALCOHOLIC] = it }
            
            // If value is null, remove the key
            if (filter.category == null) preferences.remove(PreferencesKeys.SELECTED_CATEGORY)
            if (filter.glass == null) preferences.remove(PreferencesKeys.SELECTED_GLASS)
            if (filter.alcoholic == null) preferences.remove(PreferencesKeys.SELECTED_ALCOHOLIC)
        }
    }

    /**
     * Clear all saved filter preferences
     */
    suspend fun clearFilters() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.SELECTED_CATEGORY)
            preferences.remove(PreferencesKeys.SELECTED_GLASS)
            preferences.remove(PreferencesKeys.SELECTED_ALCOHOLIC)
        }
    }
} 