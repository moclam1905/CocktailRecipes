package com.nguyenmoclam.cocktailrecipes.data.mapper

import com.nguyenmoclam.cocktailrecipes.data.local.entity.CocktailEntity
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail

/**
 * Mapper functions to convert between domain models and database entities
 */
object EntityMapper {
    
    /**
     * Convert domain Cocktail model to CocktailEntity for database storage
     */
    fun mapCocktailToEntity(cocktail: Cocktail): CocktailEntity {
        return CocktailEntity(
            id = cocktail.id,
            name = cocktail.name,
            imageUrl = cocktail.imageUrl,
            instructions = cocktail.instructions,
            ingredients = cocktail.ingredients,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Convert list of domain Cocktail models to CocktailEntity objects
     */
    fun mapCocktailsToEntities(cocktails: List<Cocktail>): List<CocktailEntity> {
        return cocktails.map { mapCocktailToEntity(it) }
    }
    
    /**
     * Convert CocktailEntity from database to domain Cocktail model
     */
    fun mapEntityToCocktail(entity: CocktailEntity, isFavorite: Boolean = false): Cocktail {
        return Cocktail(
            id = entity.id,
            name = entity.name,
            imageUrl = entity.imageUrl,
            instructions = entity.instructions,
            ingredients = entity.ingredients,
            isFavorite = isFavorite
        )
    }
    
    /**
     * Convert list of CocktailEntity objects to domain Cocktail models
     */
    fun mapEntitiesToCocktails(
        entities: List<CocktailEntity>,
        favoriteIds: Set<String> = emptySet()
    ): List<Cocktail> {
        return entities.map {
            mapEntityToCocktail(it, favoriteIds.contains(it.id))
        }
    }
} 