package com.nguyenmoclam.cocktailrecipes.data.repository

import android.util.Log
import com.nguyenmoclam.cocktailrecipes.data.common.ApiError
import com.nguyenmoclam.cocktailrecipes.data.common.NetworkMonitor
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.data.local.CocktailLocalDataSource
import com.nguyenmoclam.cocktailrecipes.data.local.FavoritesLocalDataSource
import com.nguyenmoclam.cocktailrecipes.data.mapper.CocktailMapper
import com.nguyenmoclam.cocktailrecipes.data.mapper.EntityMapper
import com.nguyenmoclam.cocktailrecipes.data.remote.CocktailRemoteDataSource
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CocktailRepository
 * Coordinates between remote and local data sources with caching strategy and error handling
 */
@Singleton
class CocktailRepositoryImpl @Inject constructor(
    private val remoteDataSource: CocktailRemoteDataSource,
    private val localDataSource: CocktailLocalDataSource,
    private val favoritesLocalDataSource: FavoritesLocalDataSource,
    private val networkMonitor: NetworkMonitor
) : CocktailRepository {

    override suspend fun getPopularCocktails(): Flow<Resource<List<Cocktail>>> = 
        getPopularCocktails(forceRefresh = false)
    
    override suspend fun getPopularCocktails(forceRefresh: Boolean): Flow<Resource<List<Cocktail>>> = flow {
        emit(Resource.Loading)
        
        // If force refresh, invalidate cache
        if (forceRefresh) {
            localDataSource.invalidateCache()
        }
        
        // Try to get data from cache first if not forced and cache is valid
        if (!forceRefresh && localDataSource.isCacheValid()) {
            val cachedCocktails = localDataSource.getCocktails().first()
            val domainCocktails = EntityMapper.mapEntitiesToCocktails(cachedCocktails)
            emit(Resource.Success(domainCocktails))
        }
        
        // If offline and cache is not valid, return offline error
        if (!networkMonitor.isNetworkAvailable() && (!localDataSource.isCacheValid() || forceRefresh)) {
            emit(Resource.error(ApiError.networkError("No internet connection. Using cached data.")))
            return@flow
        }
        
        // Fetch from network
        when (val apiResult = remoteDataSource.getPopularCocktails()) {
            is Resource.Success -> {
                val cocktails = CocktailMapper.mapDrinkListResponseToCocktails(apiResult.data)
                
                // Cache the results
                if (cocktails.isNotEmpty()) {
                    val entities = EntityMapper.mapCocktailsToEntities(cocktails)
                    localDataSource.saveCocktails(entities)
                }
                
                emit(Resource.Success(cocktails))
            }
            is Resource.Error -> {
                // If cache is available, don't emit error but inform about using cached data
                if (localDataSource.isCacheValid()) {
                    val cachedCocktails = localDataSource.getCocktails().first()
                    val domainCocktails = EntityMapper.mapEntitiesToCocktails(cachedCocktails)
                    emit(Resource.Success(domainCocktails))
                } else {
                    emit(apiResult)
                }
            }
            is Resource.Loading -> {
                // This shouldn't happen since remoteDataSource.getPopularCocktails() 
                // returns either Success or Error
            }
        }
    }

    override suspend fun searchCocktailsByName(query: String): Flow<Resource<List<Cocktail>>> =
        searchCocktailsByName(query, forceRefresh = false)
    
    override suspend fun searchCocktailsByName(query: String, forceRefresh: Boolean): Flow<Resource<List<Cocktail>>> = flow {
        emit(Resource.Loading)
        
        // If force refresh, invalidate the cache
        if (forceRefresh) {
            localDataSource.invalidateCache()
        }
        
        // Try local search first for better responsiveness if not forcing refresh
        if (!forceRefresh) {
            val localResults = localDataSource.searchCocktailsByName(query)
            if (localResults.isNotEmpty()) {
                emit(Resource.Success(EntityMapper.mapEntitiesToCocktails(localResults)))
            }
        }
        
        // If offline, return only local results with a message
        if (!networkMonitor.isNetworkAvailable()) {
            val localResults = localDataSource.searchCocktailsByName(query)
            if (localResults.isEmpty()) {
                emit(Resource.error(ApiError.networkError("No internet connection. No matching cocktails found in cache.")))
            } else if (!forceRefresh) {
                // We've already emitted the local results above
            } else {
                // We're forcing refresh but offline, so emit the local results now
                emit(Resource.Success(EntityMapper.mapEntitiesToCocktails(localResults)))
            }
            return@flow
        }
        
        // Then fetch from network
        when (val apiResult = remoteDataSource.searchCocktailsByName(query)) {
            is Resource.Success -> {
                val cocktails = CocktailMapper.mapDrinkListResponseToCocktails(apiResult.data)
                
                // Cache the results
                if (cocktails.isNotEmpty()) {
                    val entities = EntityMapper.mapCocktailsToEntities(cocktails)
                    localDataSource.saveCocktails(entities)
                }
                
                emit(Resource.Success(cocktails))
            }
            is Resource.Error -> {
                // If we already emitted local results, don't emit error
                val localResults = localDataSource.searchCocktailsByName(query)
                if (localResults.isEmpty() || forceRefresh) {
                    emit(apiResult)
                }
            }
            is Resource.Loading -> {
                // This shouldn't happen since remoteDataSource returns either Success or Error
            }
        }
    }

    override suspend fun searchCocktailsByIngredient(ingredient: String): Flow<Resource<List<Cocktail>>> =
        searchCocktailsByIngredient(ingredient, forceRefresh = false)
    
    override suspend fun searchCocktailsByIngredient(ingredient: String, forceRefresh: Boolean): Flow<Resource<List<Cocktail>>> = flow {
        emit(Resource.Loading)
        
        // If offline, inform user
        if (!networkMonitor.isNetworkAvailable()) {
            emit(Resource.error(ApiError.networkError("No internet connection. Cannot search by ingredient offline.")))
            return@flow
        }
        
        // For ingredient search, we primarily rely on the API
        when (val apiResult = remoteDataSource.searchCocktailsByIngredient(ingredient)) {
            is Resource.Success -> {
                val cocktails = CocktailMapper.mapDrinkListResponseToCocktails(apiResult.data)
                
                // Cache the results
                if (cocktails.isNotEmpty()) {
                    val entities = EntityMapper.mapCocktailsToEntities(cocktails)
                    localDataSource.saveCocktails(entities)
                }
                
                emit(Resource.Success(cocktails))
            }
            is Resource.Error -> {
                emit(apiResult)
            }
            is Resource.Loading -> {
                // This shouldn't happen since remoteDataSource returns either Success or Error
            }
        }
    }

    override suspend fun getCocktailDetails(id: String): Flow<Resource<Cocktail>> =
        getCocktailDetails(id, forceRefresh = false)
    
    override suspend fun getCocktailDetails(id: String, forceRefresh: Boolean): Flow<Resource<Cocktail>> = flow {
        emit(Resource.Loading)
        
        // If force refresh, invalidate the specific cocktail cache
        if (forceRefresh) {
            localDataSource.invalidateCache(id)
        }
        
        // Check if the cocktail is in the local cache and valid
        val cachedCocktail = localDataSource.getCocktailById(id)
        val isCacheValid = localDataSource.isCacheValid(id)
        
        if (cachedCocktail != null && isCacheValid && !forceRefresh) {
            // Get favorite status from local data source
            val isFavorite = localDataSource.isFavorite(id) || favoritesLocalDataSource.isFavorite(id)
            emit(Resource.Success(EntityMapper.mapEntityToCocktail(cachedCocktail, isFavorite)))
            
            // Try to get updated data from API in the background
            try {
                when (val remoteResult = remoteDataSource.getCocktailDetails(id)) {
                    is Resource.Success -> {
                        // Map DrinkListResponse to Cocktail object
                        val cocktailFromApi = CocktailMapper.mapDrinkListResponseToCocktails(remoteResult.data).firstOrNull()
                        
                        if (cocktailFromApi != null) {
                            // Update the cache with the latest data
                            val entity = EntityMapper.mapCocktailToEntity(cocktailFromApi)
                            localDataSource.saveCocktail(entity)
                            
                            // Get updated cocktail from cache
                            val updatedCocktail = localDataSource.getCocktailById(id)
                            if (updatedCocktail != null) {
                                // Maintain favorite status
                                val updatedIsFavorite = localDataSource.isFavorite(id) || favoritesLocalDataSource.isFavorite(id)
                                emit(Resource.Success(EntityMapper.mapEntityToCocktail(updatedCocktail, updatedIsFavorite)))
                            }
                        }
                    }
                    is Resource.Error -> {
                        // If remote fetch fails, we already emitted the cached data, so no need to emit error
                    }
                    is Resource.Loading -> {
                        // This shouldn't happen since remoteDataSource returns either Success or Error
                    }
                }
            } catch (e: Exception) {
                // If fetching from remote fails, we already emitted cached data, so just log the error
                Log.e("CocktailRepositoryImpl", "Error updating cocktail details: ${e.message}")
            }
        } else {
            // If not in cache, fetch from API
            try {
                when (val remoteResult = remoteDataSource.getCocktailDetails(id)) {
                    is Resource.Success -> {
                        // Map DrinkListResponse to Cocktail object
                        val cocktailFromApi = CocktailMapper.mapDrinkListResponseToCocktails(remoteResult.data).firstOrNull()
                        
                        if (cocktailFromApi != null) {
                            // Update the cache
                            val entity = EntityMapper.mapCocktailToEntity(cocktailFromApi)
                            localDataSource.saveCocktail(entity)
                            
                            // Update with favorite status
                            val isFavorite = localDataSource.isFavorite(id) || favoritesLocalDataSource.isFavorite(id)
                            emit(Resource.Success(cocktailFromApi.copy(isFavorite = isFavorite)))
                        } else {
                            emit(Resource.error("Cocktail details not found"))
                        }
                    }
                    is Resource.Error -> {
                        emit(remoteResult)
                    }
                    is Resource.Loading -> {
                        // This shouldn't happen since remoteDataSource returns either Success or Error
                    }
                }
            } catch (e: Exception) {
                // If the API call fails and we don't have cached data, emit the error
                val cachedCocktail = localDataSource.getCocktailById(id)
                if (cachedCocktail != null) {
                    val isFavorite = localDataSource.isFavorite(id) || favoritesLocalDataSource.isFavorite(id)
                    emit(Resource.Success(EntityMapper.mapEntityToCocktail(cachedCocktail, isFavorite)))
                } else {
                    emit(Resource.error("Failed to get cocktail details: ${e.message}"))
                }
            }
        }
    }

    override suspend fun saveFavorite(cocktail: Cocktail): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        try {
            // Save to both favorite systems for consistency
            favoritesLocalDataSource.saveFavorite(cocktail)
            localDataSource.addFavorite(cocktail.id)
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.error("Failed to save favorite: ${e.message}"))
        }
    }

    override suspend fun removeFavorite(id: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        try {
            // Remove from both favorite systems for consistency
            favoritesLocalDataSource.removeFavorite(id)
            localDataSource.removeFavorite(id)
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.error("Failed to remove favorite: ${e.message}"))
        }
    }

    override suspend fun getFavorites(): Flow<Resource<List<Cocktail>>> = flow {
        emit(Resource.Loading)
        try {
            val favorites = localDataSource.getFavorites()
            favorites.collect { cocktailEntities ->
                val domainCocktails = EntityMapper.mapEntitiesToCocktails(
                    cocktailEntities,
                    favoriteIds = cocktailEntities.map { it.id }.toSet()
                )
                emit(Resource.Success(domainCocktails))
            }
        } catch (e: Exception) {
            emit(Resource.error("Failed to get favorites: ${e.message}"))
        }
    }

    override suspend fun isFavorite(id: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        try {
            val isSimpleFavorite = localDataSource.isFavorite(id)
            val isFavoriteEntity = favoritesLocalDataSource.isFavorite(id)
            
            // Either system having it marked as favorite is sufficient
            emit(Resource.Success(isSimpleFavorite || isFavoriteEntity))
        } catch (e: Exception) {
            emit(Resource.error("Failed to check favorite status: ${e.message}"))
        }
    }

    override suspend fun invalidateAllCaches(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        try {
            localDataSource.invalidateCache()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.error("Failed to invalidate caches: ${e.message}"))
        }
    }
    
    override suspend fun getRandomCocktail(): Flow<Resource<Cocktail>> =
        getRandomCocktail(forceRefresh = false)
    
    override suspend fun getRandomCocktail(forceRefresh: Boolean): Flow<Resource<Cocktail>> = flow {
        emit(Resource.Loading)
        
        // If offline, inform user
        if (!networkMonitor.isNetworkAvailable()) {
            emit(Resource.error(ApiError.networkError("No internet connection. Cannot get a random cocktail offline.")))
            return@flow
        }
        
        // For random cocktail, we always rely on the API
        when (val apiResult = remoteDataSource.getRandomCocktail()) {
            is Resource.Success -> {
                val cocktails = CocktailMapper.mapDrinkListResponseToCocktails(apiResult.data)
                val randomCocktail = cocktails.firstOrNull()
                
                if (randomCocktail != null) {
                    // Cache the result
                    val entity = EntityMapper.mapCocktailToEntity(randomCocktail)
                    localDataSource.saveCocktail(entity)
                    
                    // Check if the cocktail is a favorite
                    val isFavorite = localDataSource.isFavorite(randomCocktail.id) || 
                                    favoritesLocalDataSource.isFavorite(randomCocktail.id)
                    
                    emit(Resource.Success(randomCocktail.copy(isFavorite = isFavorite)))
                } else {
                    emit(Resource.error("No random cocktail found."))
                }
            }
            is Resource.Error -> {
                emit(apiResult)
            }
            is Resource.Loading -> {
                // This shouldn't happen since remoteDataSource returns either Success or Error
            }
        }
    }
}