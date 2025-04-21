package com.nguyenmoclam.cocktailrecipes.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Worker for synchronizing cocktail data in the background
 * 
 * This worker handles syncing popular cocktails and updating favorites
 * for offline use. It can be scheduled to run periodically or on demand.
 */
@HiltWorker
class CocktailSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val cocktailRepository: CocktailRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val WORK_NAME = "cocktail_sync_worker"
        private const val TAG = "CocktailSyncWorker"
        
        // Constants for worker data
        const val KEY_SYNC_TYPE = "sync_type"
        const val SYNC_TYPE_POPULAR = "popular"
        const val SYNC_TYPE_FAVORITES = "favorites"
        const val SYNC_TYPE_ALL = "all"

        /**
         * Schedule periodic sync of cocktail data
         */
        fun schedulePeriodic(workManager: WorkManager) {
            val constraints = createConstraints()

            val periodicSyncRequest = PeriodicWorkRequestBuilder<CocktailSyncWorker>(
                repeatInterval = 12,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setInputData(workDataOf(KEY_SYNC_TYPE to SYNC_TYPE_ALL))
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    30,
                    TimeUnit.MINUTES
                )
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicSyncRequest
            )
            
            Timber.d("Scheduled periodic cocktail sync")
        }

        /**
         * Request an immediate one-time sync
         */
        fun requestImmediateSync(workManager: WorkManager, syncType: String = SYNC_TYPE_ALL) {
            val constraints = createConstraints()

            val syncRequest = OneTimeWorkRequestBuilder<CocktailSyncWorker>()
                .setConstraints(constraints)
                .setInputData(workDataOf(KEY_SYNC_TYPE to syncType))
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

            workManager.enqueueUniqueWork(
                "immediate_$syncType",
                ExistingWorkPolicy.REPLACE,
                syncRequest
            )
            
            Timber.d("Requested immediate cocktail sync for: $syncType")
        }

        /**
         * Cancel any pending work
         */
        fun cancelWork(workManager: WorkManager) {
            workManager.cancelUniqueWork(WORK_NAME)
            Timber.d("Cancelled cocktail sync workers")
        }

        /**
         * Create constraints for when the worker should run
         */
        private fun createConstraints() = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val syncType = inputData.getString(KEY_SYNC_TYPE) ?: SYNC_TYPE_ALL
            Timber.d("Starting cocktail sync for: $syncType")

            when (syncType) {
                SYNC_TYPE_POPULAR -> syncPopularCocktails()
                SYNC_TYPE_FAVORITES -> syncFavorites()
                SYNC_TYPE_ALL -> {
                    syncPopularCocktails()
                    syncFavorites()
                }
            }
            
            Timber.d("Cocktail sync completed for: $syncType")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error syncing cocktails")
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    /**
     * Sync popular cocktails for offline use
     */
    private suspend fun syncPopularCocktails() {
        Timber.d("Syncing popular cocktails")
        val resource = cocktailRepository.getPopularCocktails().first()
        
        if (resource is Resource.Success) {
            val cocktails = resource.data
            // The repository will handle caching in the database
            Timber.d("Synced ${cocktails.size} popular cocktails")
        } else {
            // Check if it's specifically an error state
            if (resource is Resource.Error) {
                Timber.w("Failed to sync popular cocktails: ${resource.apiError.message}")
            }
        }
    }

    /**
     * Sync favorites with full details for offline use
     */
    private suspend fun syncFavorites() {
        Timber.d("Syncing favorite cocktails")
        val favoritesResource = cocktailRepository.getFavorites().first()
        
        if (favoritesResource is Resource.Success) {
            val favorites = favoritesResource.data
            // For each favorite, get and cache full details
            for (favorite in favorites) {
                val detailsResource = cocktailRepository.getCocktailDetails(favorite.id).first()
                if (detailsResource !is Resource.Success) {
                    // Check if it's specifically an error state
                    if (detailsResource is Resource.Error) {
                        Timber.w("Failed to sync details for favorite ${favorite.id}: ${detailsResource.apiError.message}")
                    }
                }
            }
            Timber.d("Synced ${favorites.size} favorite cocktails")
        } else {
            // Check if it's specifically an error state
            if (favoritesResource is Resource.Error) {
                Timber.w("Failed to fetch favorites list for sync: ${favoritesResource.apiError.message}")
            }
        }
    }
} 