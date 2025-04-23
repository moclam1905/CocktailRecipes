package com.nguyenmoclam.cocktailrecipes.domain.repository

import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.model.IngredientItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing cocktail data.
 * Abstracts the data sources (API, local database) from the rest of the app.
 */
interface CocktailRepository {
    /**
     * Get a list of popular cocktails
     */
    suspend fun getPopularCocktails(): Flow<Resource<List<Cocktail>>>
    
    /**
     * Get a list of popular cocktails with force refresh option
     * @param forceRefresh If true, ignores cache and fetches fresh data
     */
    suspend fun getPopularCocktails(forceRefresh: Boolean): Flow<Resource<List<Cocktail>>>
    
    /**
     * Search for cocktails by name
     */
    suspend fun searchCocktailsByName(query: String): Flow<Resource<List<Cocktail>>>
    
    /**
     * Search for cocktails by name with force refresh option
     * @param query The search query
     * @param forceRefresh If true, ignores cache and fetches fresh data
     */
    suspend fun searchCocktailsByName(query: String, forceRefresh: Boolean): Flow<Resource<List<Cocktail>>>
    
    /**
     * Search for cocktails by ingredient
     */
    suspend fun searchCocktailsByIngredient(ingredient: String): Flow<Resource<List<Cocktail>>>
    
    /**
     * Search for cocktails by ingredient with force refresh option
     * @param ingredient The ingredient to search for
     * @param forceRefresh If true, ignores cache and fetches fresh data
     */
    suspend fun searchCocktailsByIngredient(ingredient: String, forceRefresh: Boolean): Flow<Resource<List<Cocktail>>>
    
    /**
     * Get cocktail details by ID
     */
    suspend fun getCocktailDetails(id: String): Flow<Resource<Cocktail>>
    
    /**
     * Get cocktail details by ID with force refresh option
     * @param id The cocktail ID
     * @param forceRefresh If true, ignores cache and fetches fresh data
     */
    suspend fun getCocktailDetails(id: String, forceRefresh: Boolean): Flow<Resource<Cocktail>>
    
    /**
     * Save a cocktail as favorite
     */
    suspend fun saveFavorite(cocktail: Cocktail): Flow<Resource<Boolean>>
    
    /**
     * Remove a cocktail from favorites
     */
    suspend fun removeFavorite(id: String): Flow<Resource<Boolean>>
    
    /**
     * Get all favorite cocktails
     */
    suspend fun getFavorites(): Flow<Resource<List<Cocktail>>>
    
    /**
     * Check if a cocktail is in favorites
     */
    suspend fun isFavorite(id: String): Flow<Resource<Boolean>>
    
    /**
     * Invalidate all caches to force a refresh on next data fetch
     */
    suspend fun invalidateAllCaches(): Flow<Resource<Boolean>>
    
    /**
     * Get a random cocktail
     */
    suspend fun getRandomCocktail(): Flow<Resource<Cocktail>>
    
    /**
     * Get a random cocktail with force refresh option
     * @param forceRefresh If true, ignores cache and fetches fresh data
     */
    suspend fun getRandomCocktail(forceRefresh: Boolean): Flow<Resource<Cocktail>>
    
    /**
     * Get a list of all available ingredients
     */
    suspend fun getAllIngredients(): Flow<Resource<List<IngredientItem>>>
    
    /**
     * Get a list of all available ingredients with force refresh option
     * @param forceRefresh If true, ignores cache and fetches fresh data
     */
    suspend fun getAllIngredients(forceRefresh: Boolean): Flow<Resource<List<IngredientItem>>>
    
    /**
     * Get cocktails by selected ingredient name
     * @param ingredientName The name of the ingredient to filter by
     */
    suspend fun getCocktailsByIngredient(ingredientName: String): Flow<Resource<List<Cocktail>>>
} 