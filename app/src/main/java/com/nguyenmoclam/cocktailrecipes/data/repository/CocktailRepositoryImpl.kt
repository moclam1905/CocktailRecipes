package com.nguyenmoclam.cocktailrecipes.data.repository

import com.nguyenmoclam.cocktailrecipes.data.common.ApiError
import com.nguyenmoclam.cocktailrecipes.data.common.NetworkMonitor
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.data.local.CocktailLocalDataSource
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
    private val networkMonitor: NetworkMonitor
) : CocktailRepository {

    override suspend fun getPopularCocktails(): Flow<Resource<List<Cocktail>>> = flow {
        emit(Resource.Loading)
        
        // Try to get data from cache first
        if (localDataSource.isCacheValid()) {
            val cachedCocktails = localDataSource.getCocktails().first()
            val domainCocktails = EntityMapper.mapEntitiesToCocktails(cachedCocktails)
            emit(Resource.Success(domainCocktails))
        }
        
        // If offline and cache is not valid, return offline error
        if (!networkMonitor.isNetworkAvailable() && !localDataSource.isCacheValid()) {
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

    override suspend fun searchCocktailsByName(query: String): Flow<Resource<List<Cocktail>>> = flow {
        emit(Resource.Loading)
        
        // Try local search first for better responsiveness
        val localResults = localDataSource.searchCocktailsByName(query)
        if (localResults.isNotEmpty()) {
            emit(Resource.Success(EntityMapper.mapEntitiesToCocktails(localResults)))
        }
        
        // If offline, return only local results with a message
        if (!networkMonitor.isNetworkAvailable()) {
            if (localResults.isEmpty()) {
                emit(Resource.error(ApiError.networkError("No internet connection. No matching cocktails found in cache.")))
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
                if (localResults.isEmpty()) {
                    emit(apiResult)
                }
            }
            is Resource.Loading -> {
                // This shouldn't happen since remoteDataSource returns either Success or Error
            }
        }
    }

    override suspend fun searchCocktailsByIngredient(ingredient: String): Flow<Resource<List<Cocktail>>> = flow {
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

    override suspend fun getCocktailDetails(id: String): Flow<Resource<Cocktail>> = flow {
        emit(Resource.Loading)
        
        // Try to get data from cache first
        val cachedCocktail = localDataSource.getCocktailById(id)
        if (cachedCocktail != null && localDataSource.isCacheValid(id)) {
            val isFavorite = localDataSource.isFavorite(id)
            emit(Resource.Success(EntityMapper.mapEntityToCocktail(cachedCocktail, isFavorite)))
        }
        
        // If offline and no valid cache, return offline error
        if (!networkMonitor.isNetworkAvailable() && (cachedCocktail == null || !localDataSource.isCacheValid(id))) {
            if (cachedCocktail != null) {
                // If we have a cached cocktail but it's stale, use it but inform the user
                val isFavorite = localDataSource.isFavorite(id)
                emit(Resource.Success(EntityMapper.mapEntityToCocktail(cachedCocktail, isFavorite)))
                emit(Resource.error(ApiError.networkError("Using cached data. Some information may be outdated.")))
            } else {
                emit(Resource.error(ApiError.networkError("No internet connection. Cocktail not found in cache.")))
            }
            return@flow
        }
        
        // Fetch from network
        when (val apiResult = remoteDataSource.getCocktailDetails(id)) {
            is Resource.Success -> {
                val cocktails = CocktailMapper.mapDrinkListResponseToCocktails(apiResult.data)
                if (cocktails.isNotEmpty()) {
                    val cocktail = cocktails.first()
                    
                    // Cache the result
                    val entity = EntityMapper.mapCocktailToEntity(cocktail)
                    localDataSource.saveCocktail(entity)
                    
                    // Update with favorite status
                    val isFavorite = localDataSource.isFavorite(id)
                    emit(Resource.Success(cocktail.copy(isFavorite = isFavorite)))
                } else {
                    emit(Resource.error(ApiError.notFoundError("Cocktail not found")))
                }
            }
            is Resource.Error -> {
                // If cache is available, don't emit error but use cached data
                if (cachedCocktail != null) {
                    val isFavorite = localDataSource.isFavorite(id)
                    emit(Resource.Success(EntityMapper.mapEntityToCocktail(cachedCocktail, isFavorite)))
                } else {
                    emit(apiResult)
                }
            }
            is Resource.Loading -> {
                // This shouldn't happen since remoteDataSource returns either Success or Error
            }
        }
    }

    override suspend fun saveFavorite(cocktail: Cocktail): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        
        try {
            // Save to local database first
            val entity = EntityMapper.mapCocktailToEntity(cocktail)
            localDataSource.saveCocktail(entity)
            
            // Add to favorites
            localDataSource.addFavorite(cocktail.id)
            
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.error(ApiError(
                message = "Failed to save favorite: ${e.localizedMessage}",
                throwable = e
            )))
        }
    }

    override suspend fun removeFavorite(id: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        
        try {
            // Remove from favorites
            localDataSource.removeFavorite(id)
            
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.error(ApiError(
                message = "Failed to remove favorite: ${e.localizedMessage}",
                throwable = e
            )))
        }
    }

    override suspend fun getFavorites(): Flow<Resource<List<Cocktail>>> = flow {
        emit(Resource.Loading)
        
        try {
            // Get all favorites from local database
            localDataSource.getFavorites()
                .map { entities ->
                    Resource.Success(
                        EntityMapper.mapEntitiesToCocktails(
                            entities,
                            entities.map { it.id }.toSet() // All these are favorites
                        )
                    )
                }
                .collect { emit(it) }
        } catch (e: Exception) {
            emit(Resource.error(ApiError(
                message = "Failed to get favorites: ${e.localizedMessage}",
                throwable = e
            )))
        }
    }
}