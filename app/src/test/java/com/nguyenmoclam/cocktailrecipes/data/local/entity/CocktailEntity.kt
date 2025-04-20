package com.nguyenmoclam.cocktailrecipes.data.local.entity

import com.nguyenmoclam.cocktailrecipes.data.Ingredient

/**
 * Database entity for storing cocktail data
 * Simplified test version
 */
data class CocktailEntity(
    val id: String,
    val name: String,
    val imageUrl: String,
    val instructions: String,
    val ingredients: List<Ingredient>,
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Database entity for storing favorite cocktails
 * Simplified test version
 */
data class FavoriteCocktailEntity(
    val cocktailId: String,
    val addedAt: Long = System.currentTimeMillis()
) 