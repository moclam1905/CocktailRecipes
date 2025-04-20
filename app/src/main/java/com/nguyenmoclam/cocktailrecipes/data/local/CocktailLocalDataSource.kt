package com.nguyenmoclam.cocktailrecipes.data.local

import com.nguyenmoclam.cocktailrecipes.data.local.dao.CocktailDao
import com.nguyenmoclam.cocktailrecipes.data.local.entity.CocktailEntity
import com.nguyenmoclam.cocktailrecipes.data.local.entity.FavoriteCocktailEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data source for cocktail data
 * Handles all interactions with the Room database
 */
@Singleton
class CocktailLocalDataSource @Inject constructor(
    private val cocktailDao: CocktailDao
) {
    /**
     * Cache timeout - data older than this will be considered stale
     */
    private val cacheValidityPeriod = TimeUnit.DAYS.toMillis(1) // 1 day in milliseconds
    
    /**
     * Save cocktails to local database
     */
    suspend fun saveCocktails(cocktails: List<CocktailEntity>) {
        cocktailDao.insertCocktails(cocktails)
    }
    
    /**
     * Save a single cocktail to local database
     */
    suspend fun saveCocktail(cocktail: CocktailEntity) {
        cocktailDao.insertCocktail(cocktail)
    }
    
    /**
     * Get all cached cocktails as a Flow
     */
    fun getCocktails(): Flow<List<CocktailEntity>> {
        return cocktailDao.getCocktails()
    }
    
    /**
     * Get a specific cocktail by ID
     */
    suspend fun getCocktailById(id: String): CocktailEntity? {
        return cocktailDao.getCocktailById(id)
    }
    
    /**
     * Search for cocktails by name
     */
    suspend fun searchCocktailsByName(query: String): List<CocktailEntity> {
        return cocktailDao.searchCocktailsByName(query)
    }
    
    /**
     * Check if the cache is valid (not older than the cache validity period)
     */
    suspend fun isCacheValid(cocktailId: String? = null): Boolean {
        val currentTime = System.currentTimeMillis()
        val validSince = currentTime - cacheValidityPeriod
        
        return if (cocktailId != null) {
            // Check specific cocktail
            val cocktail = getCocktailById(cocktailId)
            cocktail != null && cocktail.lastUpdated > validSince
        } else {
            // We'll say cache is valid if we have any cocktails that are not stale
            // This is a simple approach - could be more sophisticated based on requirements
            val cocktails = cocktailDao.getCocktails().firstOrNull() ?: emptyList()
            cocktails.any { it.lastUpdated > validSince }
        }
    }
    
    /**
     * Clear stale cache entries
     */
    suspend fun clearStaleCache() {
        val validSince = System.currentTimeMillis() - cacheValidityPeriod
        cocktailDao.deleteOldCocktails(validSince)
    }
    
    /**
     * Add a cocktail to favorites
     */
    suspend fun addFavorite(cocktailId: String) {
        cocktailDao.addFavorite(FavoriteCocktailEntity(cocktailId))
    }
    
    /**
     * Remove a cocktail from favorites
     */
    suspend fun removeFavorite(cocktailId: String) {
        cocktailDao.removeFavorite(cocktailId)
    }
    
    /**
     * Check if a cocktail is in favorites
     */
    suspend fun isFavorite(cocktailId: String): Boolean {
        return cocktailDao.isFavorite(cocktailId)
    }
    
    /**
     * Get all favorite cocktails
     */
    fun getFavorites(): Flow<List<CocktailEntity>> {
        return cocktailDao.getFavoriteCocktails()
    }
} 