package com.nguyenmoclam.cocktailrecipes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nguyenmoclam.cocktailrecipes.data.local.converter.IngredientsConverter

/**
 * Room entity for storing favorite cocktails in the local database
 */
@Entity(tableName = "favorite_cocktails")
@TypeConverters(IngredientsConverter::class)
data class FavoriteCocktailEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val imageUrl: String?,
    val instructions: String,
    val ingredients: List<IngredientEntity>,
    val dateAdded: Long = System.currentTimeMillis()
) 