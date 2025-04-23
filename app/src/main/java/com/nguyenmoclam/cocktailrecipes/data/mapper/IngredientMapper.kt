package com.nguyenmoclam.cocktailrecipes.data.mapper

import com.nguyenmoclam.cocktailrecipes.data.model.IngredientDto
import com.nguyenmoclam.cocktailrecipes.data.model.IngredientNameDto
import com.nguyenmoclam.cocktailrecipes.domain.model.IngredientItem

/**
 * Mapper for converting between Ingredient DTOs and domain models
 */
fun IngredientDto.toDomainModel(): IngredientItem {
    return IngredientItem(
        id = id ?: "",
        name = name ?: "",
        description = description,
        type = type,
        isAlcoholic = isAlcoholic?.equals("yes", ignoreCase = true) ?: false
    )
}

/**
 * Mapper for converting IngredientNameDto (from list.php?i=list) to domain IngredientItem
 * Only name is available, other fields are set to defaults.
 */
fun IngredientNameDto.toDomainItem(): IngredientItem? {
    return name?.let {
        IngredientItem(
            id = it, // Use name as ID for simplicity
            name = it,
            description = null,
            type = null,
            isAlcoholic = false // Default
        )
    }
}

/**
 * Convert a list of IngredientNameDtos to domain IngredientItem models
 */
fun List<IngredientNameDto?>.toDomainItems(): List<IngredientItem> {
    return this.mapNotNull { it?.toDomainItem() }
}

/**
 * Convert a list of IngredientDtos to domain models
 */
fun List<IngredientDto?>.toDomainModels(): List<IngredientItem> {
    return this.filterNotNull()
        .filter { it.name?.isNotBlank() == true }
        .map { it.toDomainModel() }
} 