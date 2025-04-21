package com.nguyenmoclam.cocktailrecipes.data.local

import com.nguyenmoclam.cocktailrecipes.data.local.dao.FavoriteCocktailDao
import com.nguyenmoclam.cocktailrecipes.data.local.entity.FavoriteCocktailEntity
import com.nguyenmoclam.cocktailrecipes.data.local.mapper.FavoriteCocktailMapper
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Local data source implementation for favorite cocktails
 */
class FavoritesLocalDataSource @Inject constructor(
    private val favoriteCocktailDao: FavoriteCocktailDao
) {
    /**
     * Save a cocktail to favorites
     */
    suspend fun saveFavorite(cocktail: Cocktail) {
        val entity = FavoriteCocktailMapper.mapToEntity(cocktail)
        favoriteCocktailDao.insertFavorite(entity)
    }
    
    /**
     * Remove a cocktail from favorites
     */
    suspend fun removeFavorite(cocktailId: String) {
        favoriteCocktailDao.deleteFavoriteById(cocktailId)
    }
    
    /**
     * Get all favorite cocktails
     */
    fun getAllFavorites(): Flow<List<Cocktail>> {
        return favoriteCocktailDao.getAllFavorites()
            .map { entities ->
                FavoriteCocktailMapper.mapToDomainList(entities)
            }
    }
    
    /**
     * Check if a cocktail is in favorites
     */
    suspend fun isFavorite(cocktailId: String): Boolean {
        return favoriteCocktailDao.isFavorite(cocktailId)
    }
    
    /**
     * Get a favorite cocktail by ID
     */
    suspend fun getFavoriteById(cocktailId: String): Cocktail? {
        val entity = favoriteCocktailDao.getFavoriteById(cocktailId) ?: return null
        return FavoriteCocktailMapper.mapToDomain(entity)
    }
} 