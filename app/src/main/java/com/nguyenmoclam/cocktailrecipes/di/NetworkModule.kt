package com.nguyenmoclam.cocktailrecipes.di

import android.content.Context
import androidx.work.WorkManager
import com.nguyenmoclam.cocktailrecipes.data.network.NetworkMonitor
import com.nguyenmoclam.cocktailrecipes.data.network.NetworkMonitorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides network-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides NetworkMonitor implementation for dependency injection
     */
    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor {
        return NetworkMonitorImpl(context)
    }

    /**
     * Provides WorkManager instance for dependency injection
     */
    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }
} 