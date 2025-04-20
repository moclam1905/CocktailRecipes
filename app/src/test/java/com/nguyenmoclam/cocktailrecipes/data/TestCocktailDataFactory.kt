package com.nguyenmoclam.cocktailrecipes.data

import com.nguyenmoclam.cocktailrecipes.data.local.entity.CocktailEntity
import com.nguyenmoclam.cocktailrecipes.data.model.DrinkDto
import com.nguyenmoclam.cocktailrecipes.data.model.DrinkListResponse
import com.nguyenmoclam.cocktailrecipes.data.model.IngredientDto
import com.nguyenmoclam.cocktailrecipes.data.model.IngredientListResponse

// Simplified versions of domain models needed for tests
data class Cocktail(
    val id: String,
    val name: String,
    val imageUrl: String,
    val instructions: String,
    val ingredients: List<Ingredient>,
    val isFavorite: Boolean = false
)

data class Ingredient(
    val name: String,
    val measure: String
)

/**
 * Factory class that provides test data for unit tests
 */
object TestCocktailDataFactory {

    /**
     * Create test DrinkDto objects for remote data source testing
     */
    fun createTestDrinkDtos(): List<DrinkDto> {
        return listOf(
            DrinkDto(
                id = "1",
                name = "Mojito",
                imageUrl = "https://www.example.com/mojito.jpg",
                instructions = "Mix mint, sugar, lime juice. Add rum and soda water.",
                ingredient1 = "White rum",
                ingredient2 = "Lime juice",
                ingredient3 = "Sugar",
                ingredient4 = "Mint",
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
                measure1 = "2 oz",
                measure2 = "1 oz",
                measure3 = "2 tsp",
                measure4 = "6 leaves",
                measure5 = "Top",
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
            ),
            DrinkDto(
                id = "2",
                name = "Margarita",
                imageUrl = "https://www.example.com/margarita.jpg",
                instructions = "Rim glass with salt. Shake tequila, triple sec, and lime juice with ice. Strain into glass.",
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
                measure1 = "2 oz",
                measure2 = "1 oz",
                measure3 = "1 oz",
                measure4 = "For rim",
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
            ),
            DrinkDto(
                id = "3",
                name = "Old Fashioned",
                imageUrl = "https://www.example.com/oldfashioned.jpg",
                instructions = "Muddle sugar cube with bitters. Add whiskey and ice. Garnish with orange peel.",
                ingredient1 = "Bourbon",
                ingredient2 = "Angostura bitters",
                ingredient3 = "Sugar cube",
                ingredient4 = "Orange peel",
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
                measure1 = "2 oz",
                measure2 = "2-3 dashes",
                measure3 = "1",
                measure4 = "1",
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
        )
    }

    /**
     * Create a test DrinkListResponse for remote data source testing
     */
    fun createTestDrinkListResponse(): DrinkListResponse {
        return DrinkListResponse(drinks = createTestDrinkDtos())
    }

    /**
     * Create test IngredientDto objects for remote data source testing
     */
    fun createTestIngredientDtos(): List<IngredientDto> {
        return listOf(
            IngredientDto(
                id = "1",
                name = "Vodka",
                description = "A clear, high-strength alcoholic beverage made from fermented grains or potatoes.",
                type = "Spirit",
                isAlcoholic = "Yes"
            ),
            IngredientDto(
                id = "2",
                name = "Gin",
                description = "A distilled alcoholic drink that derives its predominant flavor from juniper berries.",
                type = "Spirit",
                isAlcoholic = "Yes"
            ),
            IngredientDto(
                id = "3",
                name = "Rum",
                description = "A liquor made by fermenting and distilling sugarcane molasses or sugarcane juice.",
                type = "Spirit",
                isAlcoholic = "Yes"
            ),
            IngredientDto(
                id = "4",
                name = "Tequila",
                description = "A distilled beverage made from the blue agave plant primarily in the area surrounding the city of Tequila.",
                type = "Spirit",
                isAlcoholic = "Yes"
            )
        )
    }

    /**
     * Create a test IngredientListResponse for remote data source testing
     */
    fun createTestIngredientListResponse(): IngredientListResponse {
        return IngredientListResponse(ingredients = createTestIngredientDtos())
    }

    /**
     * Create test CocktailEntity objects for local data source testing
     */
    fun createTestCocktailEntities(): List<CocktailEntity> {
        return listOf(
            CocktailEntity(
                id = "1",
                name = "Mojito",
                imageUrl = "https://www.example.com/mojito.jpg",
                instructions = "Mix mint, sugar, lime juice. Add rum and soda water.",
                ingredients = listOf(
                    Ingredient("White rum", "2 oz"),
                    Ingredient("Lime juice", "1 oz"),
                    Ingredient("Sugar", "2 tsp"),
                    Ingredient("Mint", "6 leaves"),
                    Ingredient("Soda water", "Top")
                ),
                lastUpdated = System.currentTimeMillis()
            ),
            CocktailEntity(
                id = "2",
                name = "Margarita",
                imageUrl = "https://www.example.com/margarita.jpg",
                instructions = "Rim glass with salt. Shake tequila, triple sec, and lime juice with ice. Strain into glass.",
                ingredients = listOf(
                    Ingredient("Tequila", "2 oz"),
                    Ingredient("Triple sec", "1 oz"),
                    Ingredient("Lime juice", "1 oz"),
                    Ingredient("Salt", "For rim")
                ),
                lastUpdated = System.currentTimeMillis()
            ),
            CocktailEntity(
                id = "3",
                name = "Old Fashioned",
                imageUrl = "https://www.example.com/oldfashioned.jpg",
                instructions = "Muddle sugar cube with bitters. Add whiskey and ice. Garnish with orange peel.",
                ingredients = listOf(
                    Ingredient("Bourbon", "2 oz"),
                    Ingredient("Angostura bitters", "2-3 dashes"),
                    Ingredient("Sugar cube", "1"),
                    Ingredient("Orange peel", "1")
                ),
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    /**
     * Create test Cocktail domain objects
     */
    fun createTestCocktails(favoriteCocktailIds: Set<String> = emptySet()): List<Cocktail> {
        return listOf(
            Cocktail(
                id = "1",
                name = "Mojito",
                imageUrl = "https://www.example.com/mojito.jpg",
                instructions = "Mix mint, sugar, lime juice. Add rum and soda water.",
                ingredients = listOf(
                    Ingredient("White rum", "2 oz"),
                    Ingredient("Lime juice", "1 oz"),
                    Ingredient("Sugar", "2 tsp"),
                    Ingredient("Mint", "6 leaves"),
                    Ingredient("Soda water", "Top")
                ),
                isFavorite = favoriteCocktailIds.contains("1")
            ),
            Cocktail(
                id = "2",
                name = "Margarita",
                imageUrl = "https://www.example.com/margarita.jpg",
                instructions = "Rim glass with salt. Shake tequila, triple sec, and lime juice with ice. Strain into glass.",
                ingredients = listOf(
                    Ingredient("Tequila", "2 oz"),
                    Ingredient("Triple sec", "1 oz"),
                    Ingredient("Lime juice", "1 oz"),
                    Ingredient("Salt", "For rim")
                ),
                isFavorite = favoriteCocktailIds.contains("2")
            ),
            Cocktail(
                id = "3",
                name = "Old Fashioned",
                imageUrl = "https://www.example.com/oldfashioned.jpg",
                instructions = "Muddle sugar cube with bitters. Add whiskey and ice. Garnish with orange peel.",
                ingredients = listOf(
                    Ingredient("Bourbon", "2 oz"),
                    Ingredient("Angostura bitters", "2-3 dashes"),
                    Ingredient("Sugar cube", "1"),
                    Ingredient("Orange peel", "1")
                ),
                isFavorite = favoriteCocktailIds.contains("3")
            )
        )
    }

    /**
     * Create a stale CocktailEntity for testing cache expiration
     */
    fun createStaleCocktailEntity(id: String = "4", daysOld: Int = 2): CocktailEntity {
        val millis = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L)
        return CocktailEntity(
            id = id,
            name = "Stale Cocktail",
            imageUrl = "https://www.example.com/stale.jpg",
            instructions = "This cocktail data is stale and should be refreshed.",
            ingredients = listOf(
                Ingredient("Ingredient 1", "1 oz"),
                Ingredient("Ingredient 2", "1 oz")
            ),
            lastUpdated = millis
        )
    }
} 