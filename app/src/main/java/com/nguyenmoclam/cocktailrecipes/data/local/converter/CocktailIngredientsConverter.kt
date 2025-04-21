package com.nguyenmoclam.cocktailrecipes.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import com.nguyenmoclam.cocktailrecipes.domain.model.Ingredient
import java.lang.reflect.Type

/**
 * Type converter for Room to convert between List<Ingredient> and String for database storage
 * Handles property name conversion between 'measure' in domain model and any other format
 */
class CocktailIngredientsConverter {
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Ingredient::class.java, IngredientTypeAdapter())
        .create()
    
    @TypeConverter
    fun fromIngredientsList(ingredients: List<Ingredient>): String {
        return gson.toJson(ingredients)
    }
    
    @TypeConverter
    fun toIngredientsList(json: String): List<Ingredient> {
        val listType = object : TypeToken<List<Ingredient>>() {}.type
        return gson.fromJson(json, listType) ?: emptyList()
    }
    
    /**
     * Custom type adapter to handle the property name difference
     */
    private class IngredientTypeAdapter : JsonSerializer<Ingredient>, JsonDeserializer<Ingredient> {
        override fun serialize(src: Ingredient, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            val jsonObject = JsonObject()
            jsonObject.addProperty("name", src.name)
            jsonObject.addProperty("measure", src.measure)
            return jsonObject
        }
        
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Ingredient {
            val jsonObject = json.asJsonObject
            val name = jsonObject.get("name").asString
            val measure = if (jsonObject.has("measure")) {
                jsonObject.get("measure").asString
            } else if (jsonObject.has("measurement")) {
                jsonObject.get("measurement").asString
            } else {
                ""
            }
            return Ingredient(name, measure)
        }
    }
} 