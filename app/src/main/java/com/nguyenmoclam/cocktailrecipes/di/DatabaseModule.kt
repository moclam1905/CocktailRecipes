package com.nguyenmoclam.cocktailrecipes.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nguyenmoclam.cocktailrecipes.data.local.AppDatabase
import com.nguyenmoclam.cocktailrecipes.data.local.dao.CocktailDao
import com.nguyenmoclam.cocktailrecipes.data.local.dao.FavoriteCocktailDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import androidx.room.migration.Migration
import android.util.Log

/**
 * Hilt module for providing database-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Migration from database version 1 to 2
     * Adds the favorite_cocktails_simple table
     */
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            try {
                // Create the favorite_cocktails_simple table if it doesn't exist
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS favorite_cocktails_simple (" +
                            "cocktailId TEXT NOT NULL PRIMARY KEY, " +
                            "addedAt INTEGER NOT NULL DEFAULT 0)"
                )
                Log.d("DatabaseModule", "Migration 1-2 completed successfully")
            } catch (e: Exception) {
                Log.e("DatabaseModule", "Error during migration 1-2: ${e.message}")
            }
        }
    }
    
    /**
     * Provides the Room database instance
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        // Add our migration
        .addMigrations(MIGRATION_1_2)
        // As a last resort, if migrations fail
        .fallbackToDestructiveMigration()
        // Add callback to verify table creation
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d("DatabaseModule", "Database created")
            }
            
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Verify the table exists
                try {
                    val cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='favorite_cocktails_simple'")
                    val tableExists = cursor.count > 0
                    cursor.close()
                    
                    if (!tableExists) {
                        Log.w("DatabaseModule", "favorite_cocktails_simple table not found, creating it")
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS favorite_cocktails_simple (" +
                                    "cocktailId TEXT NOT NULL PRIMARY KEY, " +
                                    "addedAt INTEGER NOT NULL DEFAULT 0)"
                        )
                    } else {
                        Log.d("DatabaseModule", "favorite_cocktails_simple table exists")
                    }
                } catch (e: Exception) {
                    Log.e("DatabaseModule", "Error checking table: ${e.message}")
                }
            }
        })
        .build()
    }
    
    /**
     * Provides the Favorite Cocktail DAO
     */
    @Provides
    @Singleton
    fun provideFavoriteCocktailDao(database: AppDatabase): FavoriteCocktailDao {
        return database.favoriteCocktailDao()
    }
    
    /**
     * Provides the Cocktail DAO
     */
    @Provides
    @Singleton
    fun provideCocktailDao(database: AppDatabase): CocktailDao {
        return database.cocktailDao()
    }
} 