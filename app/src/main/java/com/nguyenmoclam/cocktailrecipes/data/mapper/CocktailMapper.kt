package com.nguyenmoclam.cocktailrecipes.data.mapper

import com.nguyenmoclam.cocktailrecipes.data.model.DrinkDto
import com.nguyenmoclam.cocktailrecipes.data.model.DrinkListResponse
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.model.Ingredient

/**
 * Maps API response data to domain models with validation
 */
object CocktailMapper {
    
    /**
     * Maps a DrinkListResponse to a list of domain Cocktail objects
     * Filters out any invalid drinks (missing required fields)
     */
    fun mapDrinkListResponseToCocktails(response: DrinkListResponse?): List<Cocktail> {
        // Handle null response or empty drinks list
        val drinkDtos = response?.drinks ?: return emptyList()
        
        // Map each DTO to domain model, filtering out invalid ones
        return drinkDtos.mapNotNull { drinkDto ->
            mapDrinkDtoToCocktail(drinkDto)
        }
    }
    
    /**
     * Maps a single DrinkDto to a domain Cocktail object
     * Returns null if any required fields are missing
     */
    fun mapDrinkDtoToCocktail(drinkDto: DrinkDto?): Cocktail? {
        // Validate required fields - only id and name are mandatory
        if (drinkDto == null ||
            drinkDto.id.isNullOrBlank() ||
            drinkDto.name.isNullOrBlank()) {
            return null
        }
        
        // Extract and pair ingredients with their measures
        val ingredients = extractIngredients(drinkDto)
        
        // Create domain model with validated data
        return Cocktail(
            id = drinkDto.id,
            name = drinkDto.name,
            imageUrl = drinkDto.imageUrl ?: "",
            instructions = drinkDto.instructions ?: "No instructions available",
            ingredients = ingredients.ifEmpty { listOf(Ingredient("Unknown", "Not specified")) },
            isFavorite = false // Default value for new cocktails from API
        )
    }
    
    /**
     * Extracts ingredients and measures from a DrinkDto
     * Pairs them together and filters out empty or null values
     */
    private fun extractIngredients(drinkDto: DrinkDto): List<Ingredient> {
        val ingredientsList = mutableListOf<Ingredient>()
        
        // Add all non-null ingredient-measure pairs
        addIngredientIfValid(drinkDto.ingredient1, drinkDto.measure1, ingredientsList)
        addIngredientIfValid(drinkDto.ingredient2, drinkDto.measure2, ingredientsList)
        addIngredientIfValid(drinkDto.ingredient3, drinkDto.measure3, ingredientsList)
        addIngredientIfValid(drinkDto.ingredient4, drinkDto.measure4, ingredientsList)
        addIngredientIfValid(drinkDto.ingredient5, drinkDto.measure5, ingredientsList)
        addIngredientIfValid(drinkDto.ingredient6, drinkDto.measure6, ingredientsList)
        addIngredientIfValid(drinkDto.ingredient7, drinkDto.measure7, ingredientsList)
        addIngredientIfValid(drinkDto.ingredient8, drinkDto.measure8, ingredientsList)
        addIngredientIfValid(drinkDto.ingredient9, drinkDto.measure9, ingredientsList)
        addIngredientIfValid(drinkDto.ingredient10, drinkDto.measure10, ingredientsList)
        addIngredientIfValid(drinkDto.ingredient11, drinkDto.measure11, ingredientsList)
        addIngredientIfValid(drinkDto.ingredient12, drinkDto.measure12, ingredientsList)
        addIngredientIfValid(drinkDto.ingredient13, drinkDto.measure13, ingredientsList)
        addIngredientIfValid(drinkDto.ingredient14, drinkDto.measure14, ingredientsList)
        addIngredientIfValid(drinkDto.ingredient15, drinkDto.measure15, ingredientsList)
        
        return ingredientsList
    }
    
    /**
     * Helper function to add an ingredient-measure pair to the list if the ingredient is valid
     */
    private fun addIngredientIfValid(
        ingredientName: String?,
        measure: String?,
        ingredientsList: MutableList<Ingredient>
    ) {
        if (!ingredientName.isNullOrBlank()) {
            ingredientsList.add(
                Ingredient(
                    name = ingredientName.trim(),
                    measure = measure?.trim() ?: "To taste"
                )
            )
        }
    }
} 