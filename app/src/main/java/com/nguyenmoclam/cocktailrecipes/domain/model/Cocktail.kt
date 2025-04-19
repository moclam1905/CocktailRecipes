package com.nguyenmoclam.cocktailrecipes.domain.model

/**
 * Domain model for a Cocktail
 */
data class Cocktail(
    val id: String,
    val name: String,
    val imageUrl: String,
    val instructions: String,
    val ingredients: List<Ingredient>,
    val isFavorite: Boolean = false
)

/**
 * Domain model for an Ingredient
 */
data class Ingredient(
    val name: String,
    val measure: String
) 