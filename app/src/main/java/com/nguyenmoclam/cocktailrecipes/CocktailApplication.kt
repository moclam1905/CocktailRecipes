package com.nguyenmoclam.cocktailrecipes

import android.app.Application
import androidx.work.WorkManager
import com.nguyenmoclam.cocktailrecipes.data.worker.CocktailSyncWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Main application class for the Cocktail Recipes app
 * 
 * Initializes core dependencies and sets up background sync via WorkManager
 */
@HiltAndroidApp
class CocktailApplication : Application() {
    
    @Inject
    lateinit var workManager: WorkManager
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber logging
        if (isDebugBuild()) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Timber initialized in debug mode")
        }
        
        // Schedule background sync for offline data
        CocktailSyncWorker.schedulePeriodic(workManager)
        Timber.d("Cocktail sync scheduled")
    }
    
    private fun isDebugBuild(): Boolean {
        return packageManager.getApplicationInfo(packageName, 0).flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0
    }
} 