package com.nguyenmoclam.cocktailrecipes.util

import com.nguyenmoclam.cocktailrecipes.data.model.DrinkDto
import com.nguyenmoclam.cocktailrecipes.data.model.DrinkListResponse
import com.nguyenmoclam.cocktailrecipes.data.model.IngredientDto
import com.nguyenmoclam.cocktailrecipes.data.local.entity.CocktailEntity
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.model.Ingredient

/**
 * Test data that can be used across test classes
 */
object TestData {
    // API response DTOs
    val drinkDto1 = DrinkDto(
        id = "1",
        name = "Mojito",
        imageUrl = "https://example.com/mojito.jpg",
        instructions = "Muddle mint leaves with sugar and lime juice. Add a splash of soda water and fill the glass with cracked ice. Pour the rum and top with soda water. Garnish with mint leaves and lime wedge.",
        ingredient1 = "White rum",
        ingredient2 = "Mint",
        ingredient3 = "Lime juice",
        ingredient4 = "Sugar",
        ingredient5 = "Soda water",
        ingredient6 = null,
        ingredient7 = null,
        ingredient8 = null,
        ingredient9 = null,
        ingredient10 = null,
        ingredient11 = null,
        ingredient12 = null,
        ingredient13 = null,
        ingredient14 = null,
        ingredient15 = null,
        measure1 = "2-3 oz",
        measure2 = "4-6 leaves",
        measure3 = "0.75 oz",
        measure4 = "2 tsp",
        measure5 = "To taste",
        measure6 = null,
        measure7 = null,
        measure8 = null,
        measure9 = null,
        measure10 = null,
        measure11 = null,
        measure12 = null,
        measure13 = null,
        measure14 = null,
        measure15 = null
    )
    
    val drinkDto2 = DrinkDto(
        id = "2",
        name = "Margarita",
        imageUrl = "https://example.com/margarita.jpg",
        instructions = "Rub the rim of the glass with the lime slice to make the salt stick to it. Shake the other ingredients with ice, then carefully pour into the glass.",
        ingredient1 = "Tequila",
        ingredient2 = "Triple sec",
        ingredient3 = "Lime juice",
        ingredient4 = "Salt",
        ingredient5 = null,
        ingredient6 = null,
        ingredient7 = null,
        ingredient8 = null,
        ingredient9 = null,
        ingredient10 = null,
        ingredient11 = null,
        ingredient12 = null,
        ingredient13 = null,
        ingredient14 = null,
        ingredient15 = null,
        measure1 = "1.5 oz",
        measure2 = "0.5 oz",
        measure3 = "1 oz",
        measure4 = "Pinch",
        measure5 = null,
        measure6 = null,
        measure7 = null,
        measure8 = null,
        measure9 = null,
        measure10 = null,
        measure11 = null,
        measure12 = null,
        measure13 = null,
        measure14 = null,
        measure15 = null
    )
    
    val drinkListResponse = DrinkListResponse(
        drinks = listOf(drinkDto1, drinkDto2)
    )
    
    val ingredientDto1 = IngredientDto(
        id = "1",
        name = "White rum",
        description = "A clear rum distilled from sugarcane juice or molasses.",
        type = "Spirit",
        isAlcoholic = "Yes"
    )
    
    val ingredientDto2 = IngredientDto(
        id = "2",
        name = "Tequila",
        description = "A distilled beverage made from the blue agave plant.",
        type = "Spirit",
        isAlcoholic = "Yes"
    )
    
    // Local entities
    val mojitoCocktailEntity = CocktailEntity(
        id = "1",
        name = "Mojito",
        imageUrl = "https://example.com/mojito.jpg",
        instructions = "Muddle mint leaves with sugar and lime juice. Add a splash of soda water and fill the glass with cracked ice. Pour the rum and top with soda water. Garnish with mint leaves and lime wedge.",
        ingredients = listOf(
            com.nguyenmoclam.cocktailrecipes.data.Ingredient("White rum", "2-3 oz"),
            com.nguyenmoclam.cocktailrecipes.data.Ingredient("Mint", "4-6 leaves"),
            com.nguyenmoclam.cocktailrecipes.data.Ingredient("Lime juice", "0.75 oz"),
            com.nguyenmoclam.cocktailrecipes.data.Ingredient("Sugar", "2 tsp"),
            com.nguyenmoclam.cocktailrecipes.data.Ingredient("Soda water", "To taste")
        ),
        lastUpdated = System.currentTimeMillis()
    )
    
    val margaritaCocktailEntity = CocktailEntity(
        id = "2",
        name = "Margarita",
        imageUrl = "https://example.com/margarita.jpg",
        instructions = "Rub the rim of the glass with the lime slice to make the salt stick to it. Shake the other ingredients with ice, then carefully pour into the glass.",
        ingredients = listOf(
            com.nguyenmoclam.cocktailrecipes.data.Ingredient("Tequila", "1.5 oz"),
            com.nguyenmoclam.cocktailrecipes.data.Ingredient("Triple sec", "0.5 oz"),
            com.nguyenmoclam.cocktailrecipes.data.Ingredient("Lime juice", "1 oz"),
            com.nguyenmoclam.cocktailrecipes.data.Ingredient("Salt", "Pinch")
        ),
        lastUpdated = System.currentTimeMillis()
    )
    
    // Domain models - Check actual implementation before using
    // These might need to be adjusted based on actual domain model implementation
    val mojitoCocktail = Cocktail(
        id = "1",
        name = "Mojito",
        imageUrl = "https://example.com/mojito.jpg",
        instructions = "Muddle mint leaves with sugar and lime juice. Add a splash of soda water and fill the glass with cracked ice. Pour the rum and top with soda water. Garnish with mint leaves and lime wedge.",
        ingredients = listOf(
            Ingredient(name = "White rum", measure = "2-3 oz"),
            Ingredient(name = "Mint", measure = "4-6 leaves"),
            Ingredient(name = "Lime juice", measure = "0.75 oz"),
            Ingredient(name = "Sugar", measure = "2 tsp"),
            Ingredient(name = "Soda water", measure = "To taste")
        ),
        isFavorite = false
    )
    
    val margaritaCocktail = Cocktail(
        id = "2",
        name = "Margarita",
        imageUrl = "https://example.com/margarita.jpg",
        instructions = "Rub the rim of the glass with the lime slice to make the salt stick to it. Shake the other ingredients with ice, then carefully pour into the glass.",
        ingredients = listOf(
            Ingredient(name = "Tequila", measure = "1.5 oz"),
            Ingredient(name = "Triple sec", measure = "0.5 oz"),
            Ingredient(name = "Lime juice", measure = "1 oz"),
            Ingredient(name = "Salt", measure = "Pinch")
        ),
        isFavorite = true
    )
    
    val cocktailList = listOf(mojitoCocktail, margaritaCocktail)
} 