package com.nguyenmoclam.cocktailrecipes.data.local.entity

/**
 * Entity class representing a cocktail ingredient with name and measurement
 * Used within FavoriteCocktailEntity
 */
data class IngredientEntity(
    val name: String,
    val measurement: String?
) 