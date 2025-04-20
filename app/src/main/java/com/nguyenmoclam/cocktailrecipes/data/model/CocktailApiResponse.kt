package com.nguyenmoclam.cocktailrecipes.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Top-level API response for drink searches
 */
@JsonClass(generateAdapter = true)
data class DrinkListResponse(
    @Json(name = "drinks")
    val drinks: List<DrinkDto>?
)

/**
 * Data Transfer Object for a drink/cocktail from the API
 */
@JsonClass(generateAdapter = true)
data class DrinkDto(
    @Json(name = "idDrink")
    val id: String?,
    
    @Json(name = "strDrink")
    val name: String?,
    
    @Json(name = "strDrinkThumb")
    val imageUrl: String?,
    
    @Json(name = "strInstructions")
    val instructions: String?,
    
    @Json(name = "strIngredient1")
    val ingredient1: String?,
    
    @Json(name = "strIngredient2")
    val ingredient2: String?,
    
    @Json(name = "strIngredient3")
    val ingredient3: String?,
    
    @Json(name = "strIngredient4")
    val ingredient4: String?,
    
    @Json(name = "strIngredient5")
    val ingredient5: String?,
    
    @Json(name = "strIngredient6")
    val ingredient6: String?,
    
    @Json(name = "strIngredient7")
    val ingredient7: String?,
    
    @Json(name = "strIngredient8")
    val ingredient8: String?,
    
    @Json(name = "strIngredient9")
    val ingredient9: String?,
    
    @Json(name = "strIngredient10")
    val ingredient10: String?,
    
    @Json(name = "strIngredient11")
    val ingredient11: String?,
    
    @Json(name = "strIngredient12")
    val ingredient12: String?,
    
    @Json(name = "strIngredient13")
    val ingredient13: String?,
    
    @Json(name = "strIngredient14")
    val ingredient14: String?,
    
    @Json(name = "strIngredient15")
    val ingredient15: String?,
    
    @Json(name = "strMeasure1")
    val measure1: String?,
    
    @Json(name = "strMeasure2")
    val measure2: String?,
    
    @Json(name = "strMeasure3")
    val measure3: String?,
    
    @Json(name = "strMeasure4")
    val measure4: String?,
    
    @Json(name = "strMeasure5")
    val measure5: String?,
    
    @Json(name = "strMeasure6")
    val measure6: String?,
    
    @Json(name = "strMeasure7")
    val measure7: String?,
    
    @Json(name = "strMeasure8")
    val measure8: String?,
    
    @Json(name = "strMeasure9")
    val measure9: String?,
    
    @Json(name = "strMeasure10")
    val measure10: String?,
    
    @Json(name = "strMeasure11")
    val measure11: String?,
    
    @Json(name = "strMeasure12")
    val measure12: String?,
    
    @Json(name = "strMeasure13")
    val measure13: String?,
    
    @Json(name = "strMeasure14")
    val measure14: String?,
    
    @Json(name = "strMeasure15")
    val measure15: String?
)

/**
 * API response for ingredient searches
 */
@JsonClass(generateAdapter = true)
data class IngredientListResponse(
    @Json(name = "ingredients")
    val ingredients: List<IngredientDto>?
)

/**
 * Data Transfer Object for an ingredient from the API
 */
@JsonClass(generateAdapter = true)
data class IngredientDto(
    @Json(name = "idIngredient")
    val id: String?,
    
    @Json(name = "strIngredient")
    val name: String?,
    
    @Json(name = "strDescription")
    val description: String?,
    
    @Json(name = "strType")
    val type: String?,
    
    @Json(name = "strAlcohol")
    val isAlcoholic: String?
) 