package com.nguyenmoclam.cocktailrecipes.data.local

import com.nguyenmoclam.cocktailrecipes.data.local.entity.CocktailEntity
import com.nguyenmoclam.cocktailrecipes.data.local.entity.FavoriteCocktailEntity

// Workaround: Direct replacement for kotlinx imports
// Remove these after Gradle sync fixes the dependency issues
// These are simplified placeholder versions for compile-time only
interface Flow<T> {
    suspend fun collect(collector: (T) -> Unit)
}

class MutableStateFlow<T>(var value: T) : Flow<T> {
    override suspend fun collect(collector: (T) -> Unit) {
        collector(value)
    }
    
    fun update(function: (T) -> T) {
        value = function(value)
    }
}

fun <T, R> Flow<T>.map(transform: (T) -> R): Flow<R> = object : Flow<R> {
    override suspend fun collect(collector: (R) -> Unit) {
        if (this@map is MutableStateFlow) {
            collector(transform(this@map.value))
        }
    }
}

/**
 * Fake implementation of CocktailLocalDataSource for testing
 * Simulates local database operations without requiring an actual database
 */
class FakeCocktailLocalDataSource {
    
    // In-memory storage for cocktails
    private val cocktailsFlow = MutableStateFlow<List<CocktailEntity>>(emptyList())
    private val favoritesFlow = MutableStateFlow<List<FavoriteCocktailEntity>>(emptyList())
    
    // Test control flags
    private var shouldFailOperations = false
    private var simulateExpiredCache = false
    
    // Cache validity period - can be modified for testing different scenarios
    private var cacheValidityPeriod = 86400000L // 1 day in milliseconds
    
    /**
     * Save cocktails to the fake database
     */
    suspend fun saveCocktails(cocktails: List<CocktailEntity>) {
        if (shouldFailOperations) {
            throw RuntimeException("Test database error while saving cocktails")
        }
        
        val existingIds = cocktailsFlow.value.map { it.id }.toSet()
        val existingCocktails = cocktailsFlow.value.filter { !cocktails.map { c -> c.id }.contains(it.id) }
        val updatedCocktails = existingCocktails + cocktails
        
        cocktailsFlow.update { updatedCocktails }
    }
    
    /**
     * Save a single cocktail to the fake database
     */
    suspend fun saveCocktail(cocktail: CocktailEntity) {
        if (shouldFailOperations) {
            throw RuntimeException("Test database error while saving cocktail")
        }
        
        val updatedCocktails = cocktailsFlow.value.toMutableList()
        val index = updatedCocktails.indexOfFirst { it.id == cocktail.id }
        
        if (index != -1) {
            updatedCocktails[index] = cocktail
        } else {
            updatedCocktails.add(cocktail)
        }
        
        cocktailsFlow.update { updatedCocktails }
    }
    
    /**
     * Get all cached cocktails as a Flow
     */
    fun getCocktails(): Flow<List<CocktailEntity>> {
        return cocktailsFlow
    }
    
    /**
     * Get a specific cocktail by ID
     */
    suspend fun getCocktailById(id: String): CocktailEntity? {
        if (shouldFailOperations) {
            throw RuntimeException("Test database error while fetching cocktail")
        }
        
        return cocktailsFlow.value.find { it.id == id }
    }
    
    /**
     * Search for cocktails by name
     */
    suspend fun searchCocktailsByName(query: String): List<CocktailEntity> {
        if (shouldFailOperations) {
            throw RuntimeException("Test database error while searching cocktails")
        }
        
        return cocktailsFlow.value.filter { 
            it.name.contains(query, ignoreCase = true) 
        }
    }
    
    /**
     * Check if the cache is valid (not older than the cache validity period)
     */
    suspend fun isCacheValid(cocktailId: String? = null): Boolean {
        if (simulateExpiredCache) {
            return false
        }
        
        val currentTime = System.currentTimeMillis()
        val validSince = currentTime - cacheValidityPeriod
        
        return if (cocktailId != null) {
            // Check specific cocktail
            val cocktail = getCocktailById(cocktailId)
            cocktail != null && cocktail.lastUpdated > validSince
        } else {
            // Cache is valid if we have any cocktails that are not stale
            cocktailsFlow.value.any { it.lastUpdated > validSince }
        }
    }
    
    /**
     * Clear stale cache entries
     */
    suspend fun clearStaleCache() {
        if (shouldFailOperations) {
            throw RuntimeException("Test database error while clearing stale cache")
        }
        
        val validSince = System.currentTimeMillis() - cacheValidityPeriod
        val freshCocktails = cocktailsFlow.value.filter { it.lastUpdated > validSince }
        
        cocktailsFlow.update { freshCocktails }
    }
    
    /**
     * Add a cocktail to favorites
     */
    suspend fun addFavorite(cocktailId: String) {
        if (shouldFailOperations) {
            throw RuntimeException("Test database error while adding favorite")
        }
        
        if (!isFavorite(cocktailId)) {
            val favorite = FavoriteCocktailEntity(cocktailId)
            val updatedFavorites = favoritesFlow.value + favorite
            favoritesFlow.update { updatedFavorites }
        }
    }
    
    /**
     * Remove a cocktail from favorites
     */
    suspend fun removeFavorite(cocktailId: String) {
        if (shouldFailOperations) {
            throw RuntimeException("Test database error while removing favorite")
        }
        
        val updatedFavorites = favoritesFlow.value.filter { it.cocktailId != cocktailId }
        favoritesFlow.update { updatedFavorites }
    }
    
    /**
     * Check if a cocktail is in favorites
     */
    suspend fun isFavorite(cocktailId: String): Boolean {
        if (shouldFailOperations) {
            throw RuntimeException("Test database error while checking favorite status")
        }
        
        return favoritesFlow.value.any { it.cocktailId == cocktailId }
    }
    
    /**
     * Get all favorite cocktails
     */
    fun getFavorites(): Flow<List<CocktailEntity>> {
        val favoriteIds = favoritesFlow.value.map { it.cocktailId }.toSet()
        val favoriteCocktails = cocktailsFlow.value.filter { favoriteIds.contains(it.id) }
        return MutableStateFlow(favoriteCocktails)
    }
    
    // Test helper methods
    
    /**
     * Clear all test data
     */
    fun clearAllData() {
        cocktailsFlow.update { emptyList() }
        favoritesFlow.update { emptyList() }
    }
    
    /**
     * Set the flag to simulate database operation failures
     */
    fun simulateFailures(shouldFail: Boolean) {
        shouldFailOperations = shouldFail
    }
    
    /**
     * Set the flag to simulate expired cache
     */
    fun simulateExpiredCache(isExpired: Boolean) {
        simulateExpiredCache = isExpired
    }
    
    /**
     * Modify the cache validity period for testing
     */
    fun setCacheValidityPeriod(periodMillis: Long) {
        cacheValidityPeriod = periodMillis
    }
    
    /**
     * Helper to get the current test data (not as a Flow)
     */
    fun getCurrentCocktails(): List<CocktailEntity> {
        return cocktailsFlow.value
    }
    
    /**
     * Helper to get the current favorites (not as a Flow)
     */
    fun getCurrentFavorites(): List<FavoriteCocktailEntity> {
        return favoritesFlow.value
    }
    
    /**
     * Initialize with test data
     */
    fun populateWithTestData(cocktails: List<CocktailEntity>) {
        cocktailsFlow.update { cocktails }
    }
} 