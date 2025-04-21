package com.nguyenmoclam.cocktailrecipes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nguyenmoclam.cocktailrecipes.data.local.converter.CocktailIngredientsConverter
import com.nguyenmoclam.cocktailrecipes.domain.model.Ingredient

/**
 * Database entity for storing cocktail data
 */
@Entity(tableName = "cocktails")
@TypeConverters(CocktailIngredientsConverter::class)
data class CocktailEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val imageUrl: String,
    val instructions: String,
    val ingredients: List<Ingredient>,
    val lastUpdated: Long = System.currentTimeMillis()
) 