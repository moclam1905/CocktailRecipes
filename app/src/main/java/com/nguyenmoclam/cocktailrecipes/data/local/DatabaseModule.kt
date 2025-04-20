package com.nguyenmoclam.cocktailrecipes.data.local

import android.content.Context
import androidx.room.Room
import com.nguyenmoclam.cocktailrecipes.data.local.dao.CocktailDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provides the Room database instance
     */
    @Provides
    @Singleton
    fun provideCocktailDatabase(@ApplicationContext context: Context): CocktailDatabase {
        return Room.databaseBuilder(
            context,
            CocktailDatabase::class.java,
            CocktailDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration() // For simplicity; in production, proper migrations would be implemented
        .build()
    }
    
    /**
     * Provides the CocktailDao
     */
    @Provides
    @Singleton
    fun provideCocktailDao(database: CocktailDatabase): CocktailDao {
        return database.cocktailDao()
    }
} 