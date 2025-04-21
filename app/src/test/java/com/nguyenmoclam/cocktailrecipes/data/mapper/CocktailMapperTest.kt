package com.nguyenmoclam.cocktailrecipes.data.mapper

import com.nguyenmoclam.cocktailrecipes.data.model.DrinkDto
import com.nguyenmoclam.cocktailrecipes.data.model.DrinkListResponse
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.model.Ingredient
import com.nguyenmoclam.cocktailrecipes.util.TestData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CocktailMapperTest {

    @Test
    fun `mapDrinkListResponseToCocktails maps response correctly`() {
        // Arrange
        val response = TestData.drinkListResponse
        
        // Act
        val result = CocktailMapper.mapDrinkListResponseToCocktails(response)
        
        // Assert
        assertEquals(2, result.size)
        
        // Verify first cocktail was mapped correctly
        val mojito = result[0]
        assertEquals("1", mojito.id)
        assertEquals("Mojito", mojito.name)
        assertEquals("https://example.com/mojito.jpg", mojito.imageUrl)
        
        // Verify ingredients were extracted correctly
        val mojitoIngredients = mojito.ingredients
        assertEquals(5, mojitoIngredients.size)
        
        // Check a couple of ingredients
        val rum = mojitoIngredients.find { it.name == "White rum" }
        assertNotNull(rum)
        assertEquals("2-3 oz", rum?.measure)
        
        val mint = mojitoIngredients.find { it.name == "Mint" }
        assertNotNull(mint)
        assertEquals("4-6 leaves", mint?.measure)
    }
    
    @Test
    fun `mapDrinkDtoToCocktail extracts ingredients correctly`() {
        // Arrange
        val drinkDto = DrinkDto(
            id = "100",
            name = "Test Drink",
            imageUrl = "https://example.com/test.jpg",
            instructions = "Mix it all together",
            ingredient1 = "Ingredient 1",
            ingredient2 = "Ingredient 2",
            ingredient3 = "Ingredient 3",
            ingredient4 = null,
            ingredient5 = "",  // Empty string should be ignored
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
            measure1 = "1 oz",
            measure2 = "2 oz",
            measure3 = "3 oz",
            measure4 = null,
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
        
        // Act
        val result = CocktailMapper.mapDrinkDtoToCocktail(drinkDto)
        
        // Assert
        assertEquals("100", result?.id)
        assertEquals("Test Drink", result?.name)
        
        // Only 3 ingredients should be extracted (null and empty are skipped)
        assertEquals(3, result?.ingredients?.size)
        
        // Check each ingredient
        assertEquals(Ingredient("Ingredient 1", "1 oz"), result?.ingredients[0])
        assertEquals(Ingredient("Ingredient 2", "2 oz"), result?.ingredients[1])
        assertEquals(Ingredient("Ingredient 3", "3 oz"), result?.ingredients[2])
    }
    
    @Test
    fun `mapDrinkListResponseToCocktails handles null drinks gracefully`() {
        // Arrange
        val emptyResponse = DrinkListResponse(drinks = null)
        
        // Create DTOs that will be filtered out by the mapper (missing ID or name)
        val invalidDto1 = DrinkDto(id = null, name = "Invalid Drink 1", imageUrl = null, instructions = null, ingredient1 = null, ingredient2 = null, ingredient3 = null, ingredient4 = null, ingredient5 = null, ingredient6 = null, ingredient7 = null, ingredient8 = null, ingredient9 = null, ingredient10 = null, ingredient11 = null, ingredient12 = null, ingredient13 = null, ingredient14 = null, ingredient15 = null, measure1 = null, measure2 = null, measure3 = null, measure4 = null, measure5 = null, measure6 = null, measure7 = null, measure8 = null, measure9 = null, measure10 = null, measure11 = null, measure12 = null, measure13 = null, measure14 = null, measure15 = null)
        val invalidDto2 = DrinkDto(id = "invalid2", name = null, imageUrl = null, instructions = null, ingredient1 = null, ingredient2 = null, ingredient3 = null, ingredient4 = null, ingredient5 = null, ingredient6 = null, ingredient7 = null, ingredient8 = null, ingredient9 = null, ingredient10 = null, ingredient11 = null, ingredient12 = null, ingredient13 = null, ingredient14 = null, ingredient15 = null, measure1 = null, measure2 = null, measure3 = null, measure4 = null, measure5 = null, measure6 = null, measure7 = null, measure8 = null, measure9 = null, measure10 = null, measure11 = null, measure12 = null, measure13 = null, measure14 = null, measure15 = null)
        val validDto = TestData.drinkDto1 // Include one valid DTO to ensure it's not fully empty
        
        val responseWithInvalidItems = DrinkListResponse(drinks = listOf(invalidDto1, validDto, invalidDto2))
        
        // Act
        val emptyResult = CocktailMapper.mapDrinkListResponseToCocktails(emptyResponse)
        val invalidItemsResult = CocktailMapper.mapDrinkListResponseToCocktails(responseWithInvalidItems)
        
        // Assert
        assertTrue(emptyResult.isEmpty())
        // Should contain only the valid DTO after filtering
        assertEquals(1, invalidItemsResult.size) 
        assertEquals(TestData.drinkDto1.id, invalidItemsResult.first().id)
    }
} 