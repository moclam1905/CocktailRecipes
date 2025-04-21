package com.nguyenmoclam.cocktailrecipes.data.local.mapper

import com.nguyenmoclam.cocktailrecipes.data.local.entity.FavoriteCocktailEntity
import com.nguyenmoclam.cocktailrecipes.data.local.entity.IngredientEntity
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.model.Ingredient

/**
 * Mapper for converting between domain models and local database entities
 */
object FavoriteCocktailMapper {
    
    /**
     * Maps a domain Cocktail to a FavoriteCocktailEntity
     */
    fun mapToEntity(cocktail: Cocktail): FavoriteCocktailEntity {
        return FavoriteCocktailEntity(
            id = cocktail.id,
            name = cocktail.name,
            imageUrl = cocktail.imageUrl,
            instructions = cocktail.instructions,
            ingredients = cocktail.ingredients.map { 
                IngredientEntity(it.name, it.measure) 
            },
            dateAdded = System.currentTimeMillis()
        )
    }
    
    /**
     * Maps a FavoriteCocktailEntity to a domain Cocktail
     */
    fun mapToDomain(entity: FavoriteCocktailEntity): Cocktail {
        return Cocktail(
            id = entity.id,
            name = entity.name,
            imageUrl = entity.imageUrl.toString(),
            instructions = entity.instructions,
            ingredients = entity.ingredients.map { 
                Ingredient(it.name, it.measurement ?: "") 
            },
            isFavorite = true
        )
    }
    
    /**
     * Maps a list of FavoriteCocktailEntity to a list of domain Cocktails
     */
    fun mapToDomainList(entities: List<FavoriteCocktailEntity>): List<Cocktail> {
        return entities.map { mapToDomain(it) }
    }
} 