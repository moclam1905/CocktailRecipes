package com.nguyenmoclam.cocktailrecipes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Simplified entity for tracking favorite cocktails by ID only
 * This is used by the CocktailDao for simple favorite operations
 */
@Entity(tableName = "favorite_cocktails_simple")
data class SimpleFavoriteCocktailEntity(
    @PrimaryKey
    val cocktailId: String,
    val addedAt: Long = System.currentTimeMillis()
) 