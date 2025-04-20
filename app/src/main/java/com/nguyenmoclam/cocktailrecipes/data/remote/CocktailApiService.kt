package com.nguyenmoclam.cocktailrecipes.data.remote

import com.nguyenmoclam.cocktailrecipes.data.model.DrinkListResponse
import com.nguyenmoclam.cocktailrecipes.data.model.IngredientListResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for TheCocktailDB API
 * API documentation: https://www.thecocktaildb.com/api.php
 */
interface CocktailApiService {
    
    /**
     * Get a list of popular cocktails (by default, this fetches drinks from the 'Popular' filter)
     */
    @GET("filter.php")
    suspend fun getPopularCocktails(@Query("c") category: String = "Cocktail"): DrinkListResponse
    
    /**
     * Search for cocktails by name
     */
    @GET("search.php")
    suspend fun searchCocktailsByName(@Query("s") searchQuery: String): DrinkListResponse
    
    /**
     * Search for cocktails containing a specific ingredient
     */
    @GET("filter.php")
    suspend fun searchCocktailsByIngredient(@Query("i") ingredient: String): DrinkListResponse
    
    /**
     * Get detailed information about a specific cocktail by ID
     */
    @GET("lookup.php")
    suspend fun getCocktailDetails(@Query("i") id: String): DrinkListResponse
    
    /**
     * List all available ingredients
     */
    @GET("list.php")
    suspend fun getIngredientsList(@Query("i") list: String = "list"): IngredientListResponse
    
    /**
     * Get a random cocktail
     */
    @GET("random.php")
    suspend fun getRandomCocktail(): DrinkListResponse
    
    /**
     * Get a list of cocktails by first letter
     */
    @GET("search.php")
    suspend fun getCocktailsByFirstLetter(@Query("f") firstLetter: String): DrinkListResponse
}