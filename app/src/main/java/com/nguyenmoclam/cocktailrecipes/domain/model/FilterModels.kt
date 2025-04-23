package com.nguyenmoclam.cocktailrecipes.domain.model

/**
 * Domain model for cocktail categories
 */
data class Category(
    val name: String
)

/**
 * Domain model for glass types
 */
data class Glass(
    val name: String
)

/**
 * Domain model for alcoholic filter types
 */
data class AlcoholicFilter(
    val name: String,
    val isAlcoholic: Boolean
)

/**
 * Represents combined filter options for cocktail searches
 */
data class CocktailFilter(
    val category: String? = null,
    val glass: String? = null,
    val alcoholic: String? = null
) {
    /**
     * Checks if any filter is active
     */
    fun hasActiveFilters(): Boolean {
        return !category.isNullOrEmpty() || !glass.isNullOrEmpty() || !alcoholic.isNullOrEmpty()
    }
} 