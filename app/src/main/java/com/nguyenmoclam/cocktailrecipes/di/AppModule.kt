package com.nguyenmoclam.cocktailrecipes.di

import com.nguyenmoclam.cocktailrecipes.data.repository.CocktailRepositoryImpl
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides application-wide dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindCocktailRepository(
        cocktailRepositoryImpl: CocktailRepositoryImpl
    ): CocktailRepository
} 