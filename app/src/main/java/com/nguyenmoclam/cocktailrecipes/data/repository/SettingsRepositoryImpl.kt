package com.nguyenmoclam.cocktailrecipes.data.repository

import android.content.Context
import android.content.pm.PackageManager
import com.nguyenmoclam.cocktailrecipes.data.local.AppDatabase
import com.nguyenmoclam.cocktailrecipes.data.local.PreferencesManager
import com.nguyenmoclam.cocktailrecipes.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val appDatabase: AppDatabase,
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
            appDatabase.clearAllTables()
            // Mark cache as cleared in preferences
            preferencesManager.setCacheCleared(true)
            Timber.d("App cache cleared successfully")
            true
        } catch (e: Exception) {
            Timber.e(e, "Error clearing app cache")
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