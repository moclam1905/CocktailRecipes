package com.nguyenmoclam.cocktailrecipes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nguyenmoclam.cocktailrecipes.data.local.dao.CocktailDao
import com.nguyenmoclam.cocktailrecipes.data.local.entity.CocktailEntity
import com.nguyenmoclam.cocktailrecipes.data.local.entity.FavoriteCocktailEntity
import com.nguyenmoclam.cocktailrecipes.data.local.util.Converters

/**
 * Room database for storing cocktail data
 */
@Database(
    entities = [CocktailEntity::class, FavoriteCocktailEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CocktailDatabase : RoomDatabase() {
    
    abstract fun cocktailDao(): CocktailDao

    companion object {
        const val DATABASE_NAME = "cocktail_db"
    }
} 