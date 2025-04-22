package com.nguyenmoclam.cocktailrecipes.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    // Theme settings
    fun getThemeMode(): Flow<String>
    suspend fun setThemeMode(mode: String)
    
    // Cache management
    suspend fun clearAppCache(): Boolean
    
    // App info
    suspend fun getAppVersion(): String
} 