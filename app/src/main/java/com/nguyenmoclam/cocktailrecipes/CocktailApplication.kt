package com.nguyenmoclam.cocktailrecipes

import android.app.Application
import android.content.pm.ApplicationInfo
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.nguyenmoclam.cocktailrecipes.data.worker.CocktailSyncWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Main application class for the Cocktail Recipes app
 *
 * Initializes core dependencies, sets up background sync via WorkManager,
 * and configures WorkManager to use Hilt for dependency injection.
 */
@HiltAndroidApp
class CocktailApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

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
        // Note: WorkManager initialization happens lazily via Configuration.Provider
        CocktailSyncWorker.schedulePeriodic(workManager)
        Timber.d("Cocktail sync scheduled")
    }


    private fun isDebugBuild(): Boolean {
        return packageManager.getApplicationInfo(
            packageName,
            0
        ).flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(if (isDebugBuild()) android.util.Log.DEBUG else android.util.Log.INFO)
            .build()
} 