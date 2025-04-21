package com.nguyenmoclam.cocktailrecipes.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nguyenmoclam.cocktailrecipes.data.local.entity.IngredientEntity

/**
 * Type converter for Room to convert between List<IngredientEntity> and String for database storage
 */
class IngredientsConverter {
    private val gson = Gson()
    
    @TypeConverter
    fun fromIngredientsList(ingredients: List<IngredientEntity>): String {
        return gson.toJson(ingredients)
    }
    
    @TypeConverter
    fun toIngredientsList(json: String): List<IngredientEntity> {
        val listType = object : TypeToken<List<IngredientEntity>>() {}.type
        return gson.fromJson(json, listType) ?: emptyList()
    }
} 