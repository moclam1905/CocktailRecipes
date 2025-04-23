package com.nguyenmoclam.cocktailrecipes.data.mapper

import com.nguyenmoclam.cocktailrecipes.data.model.AlcoholicDto
import com.nguyenmoclam.cocktailrecipes.data.model.AlcoholicListResponse
import com.nguyenmoclam.cocktailrecipes.data.model.CategoryDto
import com.nguyenmoclam.cocktailrecipes.data.model.CategoryListResponse
import com.nguyenmoclam.cocktailrecipes.data.model.GlassDto
import com.nguyenmoclam.cocktailrecipes.data.model.GlassListResponse
import com.nguyenmoclam.cocktailrecipes.domain.model.AlcoholicFilter
import com.nguyenmoclam.cocktailrecipes.domain.model.Category
import com.nguyenmoclam.cocktailrecipes.domain.model.Glass

/**
 * Maps category API response to domain models
 */
fun CategoryListResponse.toDomainModels(): List<Category> {
    return this.drinks?.mapNotNull { dto ->
        dto.toDomainModel()
    } ?: emptyList()
}

/**
 * Maps a single category DTO to domain model
 */
fun CategoryDto.toDomainModel(): Category? {
    return if (!name.isNullOrBlank()) {
        Category(name = name)
    } else {
        null
    }
}

/**
 * Maps glass type API response to domain models
 */
fun GlassListResponse.toDomainModels(): List<Glass> {
    return this.drinks?.mapNotNull { dto ->
        dto.toDomainModel()
    } ?: emptyList()
}

/**
 * Maps a single glass DTO to domain model
 */
fun GlassDto.toDomainModel(): Glass? {
    return if (!name.isNullOrBlank()) {
        Glass(name = name)
    } else {
        null
    }
}

/**
 * Maps alcoholic filter API response to domain models
 */
fun AlcoholicListResponse.toDomainModels(): List<AlcoholicFilter> {
    return this.drinks?.mapNotNull { dto ->
        dto.toDomainModel()
    } ?: emptyList()
}

/**
 * Maps a single alcoholic filter DTO to domain model
 */
fun AlcoholicDto.toDomainModel(): AlcoholicFilter? {
    return if (!name.isNullOrBlank()) {
        AlcoholicFilter(
            name = name,
            isAlcoholic = name.equals("Alcoholic", ignoreCase = true)
        )
    } else {
        null
    }
} 