package com.nguyenmoclam.cocktailrecipes.data.repository

import android.content.Context
import android.content.pm.PackageManager
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.data.local.AppDatabase
import com.nguyenmoclam.cocktailrecipes.data.local.PreferencesManager
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import com.nguyenmoclam.cocktailrecipes.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val appDatabase: AppDatabase,
    private val cocktailRepository: CocktailRepository,
    @ApplicationContext private val context: Context
) : SettingsRepository {

    override fun getThemeMode(): Flow<String> = preferencesManager.themeMode
        .catch { e ->
            Timber.e(e, "Error retrieving theme mode")
            emit(PreferencesManager.THEME_MODE_SYSTEM) // Default to system theme if error
        }

    override suspend fun setThemeMode(mode: String) {
        try {
            preferencesManager.setThemeMode(mode)
        } catch (e: Exception) {
            Timber.e(e, "Error setting theme mode")
            // Consider rethrowing or handling specific error cases 
        }
    }

    override suspend fun clearAppCache(): Boolean {
        return try {
            // Clear database cache but keep favorites
            // Switch to IO dispatcher to avoid running database operations on main thread
            withContext(Dispatchers.IO) {
                appDatabase.clearAllTables()
                // Mark cache as cleared in preferences
                preferencesManager.setCacheCleared(true)
            }
            Timber.d("App cache cleared successfully")
            true
        } catch (e: Exception) {
            Timber.e(e, "Error clearing app cache")
            false
        }
    }

    override suspend fun clearApiCache(): Boolean {
        return try {
            // Invalidate all API caches to force refresh on next fetch
            val result = withContext(Dispatchers.IO) {
                cocktailRepository.invalidateAllCaches().first()
            }
            val success = if (result is Resource.Success) {
                result.data == true
            } else {
                false // Treat non-success (Error, Loading) as failure
            }
            
            if (success) {
                Timber.d("API cache invalidated successfully")
            } else {
                Timber.w("API cache invalidation failed or returned false. Result: $result")
            }
            success
        } catch (e: Exception) {
            Timber.e(e, "Error clearing API cache: ${e.message}")
            false
        }
    }

    override suspend fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.versionCode})"
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e, "Error retrieving app version")
            "Unknown"
        }
    }
} 