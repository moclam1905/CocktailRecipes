package com.nguyenmoclam.cocktailrecipes.data.common

import com.nguyenmoclam.cocktailrecipes.data.model.DrinkDto
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import java.lang.reflect.Type

/**
 * Custom Moshi JsonAdapter to handle cases where the API returns an empty string
 * instead of an empty array for the 'drinks' list.
 */
class DrinksListAdapter {

    @FromJson
    fun fromJson(reader: JsonReader, delegate: JsonAdapter<List<DrinkDto>>): List<DrinkDto>? {
        return if (reader.peek() == JsonReader.Token.STRING) {
            // Consume the empty string and return an empty list
            reader.nextString()
            emptyList<DrinkDto>()
        } else {
            // Otherwise, delegate to the default adapter
            delegate.fromJson(reader)
        }
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: List<DrinkDto>?, delegate: JsonAdapter<List<DrinkDto>>) {
        // For serialization, always use the default adapter
        delegate.toJson(writer, value)
    }

    companion object {
        val FACTORY: JsonAdapter.Factory = object : JsonAdapter.Factory {
            override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
                if (annotations.isEmpty() && type == Types.newParameterizedType(List::class.java, DrinkDto::class.java)) {
                    val delegate = moshi.nextAdapter<List<DrinkDto>>(this, type, annotations)
                    return object : JsonAdapter<List<DrinkDto>?>() {
                        private val adapter = DrinksListAdapter()

                        override fun fromJson(reader: JsonReader): List<DrinkDto>? {
                            return adapter.fromJson(reader, delegate)
                        }

                        override fun toJson(writer: JsonWriter, value: List<DrinkDto>?) {
                            adapter.toJson(writer, value, delegate)
                        }
                    }
                }
                return null
            }
        }
    }
} 