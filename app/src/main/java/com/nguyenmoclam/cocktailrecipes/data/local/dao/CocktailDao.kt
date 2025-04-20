package com.nguyenmoclam.cocktailrecipes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nguyenmoclam.cocktailrecipes.data.local.entity.CocktailEntity
import com.nguyenmoclam.cocktailrecipes.data.local.entity.FavoriteCocktailEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for cocktail-related database operations
 */
@Dao
interface CocktailDao {
    
    /**
     * Insert cocktails into database, replacing any existing entries
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCocktails(cocktails: List<CocktailEntity>)
    
    /**
     * Insert a single cocktail into database, replacing if it exists
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCocktail(cocktail: CocktailEntity)
    
    /**
     * Get all cocktails as a Flow
     */
    @Query("SELECT * FROM cocktails")
    fun getCocktails(): Flow<List<CocktailEntity>>
    
    /**
     * Get a cocktail by ID
     */
    @Query("SELECT * FROM cocktails WHERE id = :id")
    suspend fun getCocktailById(id: String): CocktailEntity?
    
    /**
     * Get cocktails where name contains the search query
     */
    @Query("SELECT * FROM cocktails WHERE name LIKE '%' || :query || '%'")
    suspend fun searchCocktailsByName(query: String): List<CocktailEntity>
    
    /**
     * Delete cocktails older than a given timestamp
     */
    @Query("DELETE FROM cocktails WHERE lastUpdated < :timestamp")
    suspend fun deleteOldCocktails(timestamp: Long)
    
    /**
     * Add a cocktail to favorites
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteCocktailEntity)
    
    /**
     * Remove a cocktail from favorites
     */
    @Query("DELETE FROM favorite_cocktails WHERE cocktailId = :cocktailId")
    suspend fun removeFavorite(cocktailId: String)
    
    /**
     * Check if a cocktail is favorited
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cocktails WHERE cocktailId = :cocktailId LIMIT 1)")
    suspend fun isFavorite(cocktailId: String): Boolean
    
    /**
     * Get all favorite cocktails
     */
    @Transaction
    @Query("SELECT c.* FROM cocktails c INNER JOIN favorite_cocktails f ON c.id = f.cocktailId ORDER BY f.addedAt DESC")
    fun getFavoriteCocktails(): Flow<List<CocktailEntity>>
} 