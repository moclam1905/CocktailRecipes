package com.nguyenmoclam.cocktailrecipes.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response data structure for category list from TheCocktailDB API
 */
@JsonClass(generateAdapter = true)
data class CategoryListResponse(
    @Json(name = "drinks")
    val drinks: List<CategoryDto>? = null
)

/**
 * Data transfer object for category items
 */
@JsonClass(generateAdapter = true)
data class CategoryDto(
    @Json(name = "strCategory")
    val name: String? = null
)

/**
 * Response data structure for glass type list from TheCocktailDB API
 */
@JsonClass(generateAdapter = true)
data class GlassListResponse(
    @Json(name = "drinks")
    val drinks: List<GlassDto>? = null
)

/**
 * Data transfer object for glass type items
 */
@JsonClass(generateAdapter = true)
data class GlassDto(
    @Json(name = "strGlass")
    val name: String? = null
)

/**
 * Response data structure for alcoholic filter list from TheCocktailDB API
 */
@JsonClass(generateAdapter = true)
data class AlcoholicListResponse(
    @Json(name = "drinks")
    val drinks: List<AlcoholicDto>? = null
)

/**
 * Data transfer object for alcoholic filter items
 */
@JsonClass(generateAdapter = true)
data class AlcoholicDto(
    @Json(name = "strAlcoholic")
    val name: String? = null
) 