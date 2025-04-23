package com.nguyenmoclam.cocktailrecipes.domain.model

/**
 * Domain model for an Ingredient item in the Ingredient Explorer
 */
data class IngredientItem(
    val id: String,
    val name: String,
    val description: String?,
    val type: String?,
    val isAlcoholic: Boolean
) {
    /**
     * Get the image URL for this ingredient from TheCocktailDB
     * Format: www.thecocktaildb.com/images/ingredients/{name}.png
     */
    fun getImageUrl(): String {
        return "https://www.thecocktaildb.com/images/ingredients/$name.png"
    }
} 