package com.nguyenmoclam.cocktailrecipes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nguyenmoclam.cocktailrecipes.domain.model.Ingredient

/**
 * Database entity for storing cocktail data
 */
@Entity(tableName = "cocktails")
data class CocktailEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val imageUrl: String,
    val instructions: String,
    val ingredients: List<Ingredient>,
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Database entity for storing favorite cocktails
 */
@Entity(tableName = "favorite_cocktails")
data class FavoriteCocktailEntity(
    @PrimaryKey
    val cocktailId: String,
    val addedAt: Long = System.currentTimeMillis()
) 