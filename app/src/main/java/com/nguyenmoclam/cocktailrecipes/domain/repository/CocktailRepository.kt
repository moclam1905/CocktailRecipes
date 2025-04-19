package com.nguyenmoclam.cocktailrecipes.domain.repository

import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
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
     * Search for cocktails by name
     */
    suspend fun searchCocktailsByName(query: String): Flow<Resource<List<Cocktail>>>
    
    /**
     * Search for cocktails by ingredient
     */
    suspend fun searchCocktailsByIngredient(ingredient: String): Flow<Resource<List<Cocktail>>>
    
    /**
     * Get cocktail details by ID
     */
    suspend fun getCocktailDetails(id: String): Flow<Resource<Cocktail>>
    
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
} 