package com.nguyenmoclam.cocktailrecipes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nguyenmoclam.cocktailrecipes.data.local.converter.CocktailIngredientsConverter
import com.nguyenmoclam.cocktailrecipes.data.local.converter.IngredientsConverter
import com.nguyenmoclam.cocktailrecipes.data.local.dao.CocktailDao
import com.nguyenmoclam.cocktailrecipes.data.local.dao.FavoriteCocktailDao
import com.nguyenmoclam.cocktailrecipes.data.local.entity.CocktailEntity
import com.nguyenmoclam.cocktailrecipes.data.local.entity.FavoriteCocktailEntity
import com.nguyenmoclam.cocktailrecipes.data.local.entity.SimpleFavoriteCocktailEntity

/**
 * Room database for the application
 */
@Database(
    entities = [
        CocktailEntity::class, 
        FavoriteCocktailEntity::class,
        SimpleFavoriteCocktailEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(IngredientsConverter::class, CocktailIngredientsConverter::class)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Gets the DAO for favorite cocktails
     */
    abstract fun favoriteCocktailDao(): FavoriteCocktailDao
    
    /**
     * Gets the DAO for cocktails
     */
    abstract fun cocktailDao(): CocktailDao
    
    companion object {
        const val DATABASE_NAME = "cocktail_recipes_db"
    }
} 