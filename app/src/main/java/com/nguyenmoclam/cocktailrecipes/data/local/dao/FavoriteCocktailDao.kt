package com.nguyenmoclam.cocktailrecipes.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nguyenmoclam.cocktailrecipes.data.local.entity.FavoriteCocktailEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for favorite cocktails in Room database
 */
@Dao
interface FavoriteCocktailDao {
    /**
     * Insert a cocktail into favorites
     * If a cocktail with the same ID already exists, it will be replaced
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(cocktail: FavoriteCocktailEntity)
    
    /**
     * Delete a cocktail from favorites by its ID
     */
    @Query("DELETE FROM favorite_cocktails WHERE id = :cocktailId")
    suspend fun deleteFavoriteById(cocktailId: String)
    
    /**
     * Get all favorite cocktails
     * Returns a Flow that will emit whenever the data changes
     */
    @Query("SELECT * FROM favorite_cocktails ORDER BY dateAdded DESC")
    fun getAllFavorites(): Flow<List<FavoriteCocktailEntity>>
    
    /**
     * Check if a cocktail is in favorites
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cocktails WHERE id = :cocktailId LIMIT 1)")
    suspend fun isFavorite(cocktailId: String): Boolean
    
    /**
     * Get a single favorite cocktail by ID
     */
    @Query("SELECT * FROM favorite_cocktails WHERE id = :cocktailId LIMIT 1")
    suspend fun getFavoriteById(cocktailId: String): FavoriteCocktailEntity?
} 