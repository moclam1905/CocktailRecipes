package com.nguyenmoclam.cocktailrecipes.data.remote

import com.nguyenmoclam.cocktailrecipes.data.common.ApiError
import com.nguyenmoclam.cocktailrecipes.data.common.ErrorMapper
import com.nguyenmoclam.cocktailrecipes.data.common.NetworkMonitor
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.data.common.RetryHandler
import com.nguyenmoclam.cocktailrecipes.data.model.DrinkListResponse
import com.nguyenmoclam.cocktailrecipes.data.model.IngredientListResponse
import com.nguyenmoclam.cocktailrecipes.data.model.IngredientNameListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for cocktail data from the API
 * Handles API calls with error handling and retry logic
 */
@Singleton
class CocktailRemoteDataSource @Inject constructor(
    private val apiService: CocktailApiService,
    private val errorMapper: ErrorMapper,
    private val networkMonitor: NetworkMonitor,
    private val retryHandler: RetryHandler
) {
    /**
     * Generic function to handle API requests and map to Resource type
     * Includes retry logic for transient errors
     */
    private suspend fun <T> safeApiCall(
        endpoint: String,
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            // First check if network is available to avoid unnecessary API calls
            if (!networkMonitor.isNetworkAvailable()) {
                return@withContext Resource.error(
                    ApiError.networkError("No internet connection available")
                )
            }
            
            // Execute the call with retry support
            retryHandler.executeWithRetry(
                operation = { apiCall() }
            )
                .fold(
                    onSuccess = { Resource.Success(it) },
                    onFailure = { throwable ->
                        val apiError = errorMapper.mapThrowableToApiError(throwable, endpoint)
                        Resource.error(apiError)
                    }
                )
        }
    }

    /**
     * Get popular cocktails from the API
     */
    suspend fun getPopularCocktails(category: String = "Cocktail"): Resource<DrinkListResponse> {
        return safeApiCall("filter.php?c=$category") { 
            apiService.getPopularCocktails(category) 
        }
    }

    /**
     * Search for cocktails by name
     */
    suspend fun searchCocktailsByName(query: String): Resource<DrinkListResponse> {
        return safeApiCall("search.php?s=$query") { 
            apiService.searchCocktailsByName(query) 
        }
    }

    /**
     * Search for cocktails by ingredient
     */
    suspend fun searchCocktailsByIngredient(ingredient: String): Resource<DrinkListResponse> {
        return safeApiCall("filter.php?i=$ingredient") { 
            apiService.searchCocktailsByIngredient(ingredient) 
        }
    }

    /**
     * Get cocktail details by ID
     */
    suspend fun getCocktailDetails(id: String): Resource<DrinkListResponse> {
        return safeApiCall("lookup.php?i=$id") { 
            apiService.getCocktailDetails(id) 
        }
    }

    /**
     * Get a list of all ingredient names
     */
    suspend fun getIngredientsList(): Resource<IngredientNameListResponse> {
        return safeApiCall("list.php?i=list") { 
            apiService.getIngredientsList() 
        }
    }

    /**
     * Get a random cocktail
     */
    suspend fun getRandomCocktail(): Resource<DrinkListResponse> {
        return safeApiCall("random.php") { 
            apiService.getRandomCocktail() 
        }
    }
} 