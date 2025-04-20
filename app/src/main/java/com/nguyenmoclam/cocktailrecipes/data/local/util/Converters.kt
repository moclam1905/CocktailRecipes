package com.nguyenmoclam.cocktailrecipes.data.local.util

import androidx.room.TypeConverter
import com.nguyenmoclam.cocktailrecipes.domain.model.Ingredient
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * Type converters for Room database
 * Handles conversion between complex objects and database storable primitives
 */
class Converters {
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    private val ingredientsType = Types.newParameterizedType(
        List::class.java,
        Ingredient::class.java
    )
    
    private val ingredientsAdapter = moshi.adapter<List<Ingredient>>(ingredientsType)
    
    /**
     * Convert list of ingredients to JSON string for storage in database
     */
    @TypeConverter
    fun fromIngredientsList(ingredients: List<Ingredient>): String {
        return ingredientsAdapter.toJson(ingredients)
    }
    
    /**
     * Convert JSON string from database to list of ingredients
     */
    @TypeConverter
    fun toIngredientsList(json: String): List<Ingredient> {
        return ingredientsAdapter.fromJson(json) ?: emptyList()
    }
} 