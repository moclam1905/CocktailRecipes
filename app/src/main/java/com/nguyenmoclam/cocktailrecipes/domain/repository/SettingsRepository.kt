package com.nguyenmoclam.cocktailrecipes.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    // Theme settings
    fun getThemeMode(): Flow<String>
    suspend fun setThemeMode(mode: String)
    
    // Cache management
    suspend fun clearAppCache(): Boolean
    
    // API cache management
    suspend fun clearApiCache(): Boolean
    
    // App info
    suspend fun getAppVersion(): String
} 