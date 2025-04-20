package com.nguyenmoclam.cocktailrecipes.data.remote

import com.nguyenmoclam.cocktailrecipes.data.model.DrinkDto
import com.nguyenmoclam.cocktailrecipes.data.model.DrinkListResponse
import com.nguyenmoclam.cocktailrecipes.data.model.IngredientDto
import com.nguyenmoclam.cocktailrecipes.data.model.IngredientListResponse
import kotlinx.coroutines.delay

// Temporary workarounds - to be removed after fixing the dependency issues
// Simplified substitutes for the actual classes to allow compilation

class ApiError(
    val message: String,
    val code: Int? = null,
    val errorType: ErrorType = ErrorType.GENERIC,
    val endpoint: String? = null,
    val throwable: Throwable? = null
) {
    companion object {
        fun networkError(message: String) = ApiError(message, errorType = ErrorType.NETWORK)
        fun notFoundError(message: String) = ApiError(message, errorType = ErrorType.NOT_FOUND)
    }
}

enum class ErrorType {
    GENERIC, NETWORK, NOT_FOUND, SERVER, AUTHENTICATION, VALIDATION
}

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val error: ApiError) : Resource<Nothing>()
    
    companion object {
        fun <T> error(apiError: ApiError): Resource<T> = Error(apiError)
    }
}

/**
 * Fake implementation of CocktailRemoteDataSource for testing
 * Simulates API responses without making actual network calls
 */
class FakeCocktailRemoteDataSource {

    // Test flags to simulate different scenarios
    var shouldReturnError: Boolean = false
    var shouldReturnEmptyList: Boolean = false
    var networkDelayMs: Long = 0
    var errorType: ApiError = ApiError.networkError("Test network error")

    // Test data
    private val drinks = mutableListOf<DrinkDto>()
    private val ingredients = mutableListOf<IngredientDto>()

    /**
     * Initialize with default test data
     */
    fun initialize() {
        // Clear any existing data
        clearData()

        // Add some default cocktails for testing
        addDrink(
            id = "11007",
            name = "Margarita",
            imageUrl = "https://www.thecocktaildb.com/images/media/drink/5noda61589575158.jpg",
            instructions = "Rub the rim of the glass with the lime slice to make the salt stick to it. Take care to moisten only the outer rim and sprinkle the salt on it. The salt should present to the lips of the imbiber and never mix into the cocktail. Shake the other ingredients with ice, then carefully pour into the glass.",
            ingredients = listOf("Tequila", "Triple sec", "Lime juice", "Salt"),
            measures = listOf("1 1/2 oz", "1/2 oz", "1 oz", "")
        )
        
        addDrink(
            id = "11001",
            name = "Old Fashioned",
            imageUrl = "https://www.thecocktaildb.com/images/media/drink/vrwquq1478252802.jpg",
            instructions = "Place sugar cube in old fashioned glass and saturate with bitters, add a dash of plain water. Muddle until dissolved. Fill the glass with ice cubes and add whiskey. Garnish with orange twist, and a cocktail cherry.",
            ingredients = listOf("Bourbon", "Angostura bitters", "Sugar", "Water"),
            measures = listOf("4.5 cL", "2 dashes", "1 cube", "dash")
        )
        
        // Add some ingredients for testing
        ingredients.add(
            IngredientDto(
                id = "1",
                name = "Vodka",
                description = "Vodka is a distilled beverage composed primarily of water and ethanol.",
                type = "Spirit",
                isAlcoholic = "Yes"
            )
        )
        
        ingredients.add(
            IngredientDto(
                id = "2",
                name = "Gin",
                description = "Gin is a distilled alcoholic drink that derives its predominant flavour from juniper berries.",
                type = "Spirit",
                isAlcoholic = "Yes"
            )
        )
    }
    
    /**
     * Clear all test data
     */
    fun clearData() {
        drinks.clear()
        ingredients.clear()
        shouldReturnError = false
        shouldReturnEmptyList = false
        networkDelayMs = 0
    }

    /**
     * Add a drink to the test data
     */
    fun addDrink(
        id: String,
        name: String,
        imageUrl: String,
        instructions: String,
        ingredients: List<String>,
        measures: List<String>
    ) {
        // Create a list of 15 ingredients and measures with nulls for unused positions
        val fullIngredients = ingredients.map { if (it.isBlank()) null else it }.toMutableList<String?>()
        val fullMeasures = measures.map { if (it.isBlank()) null else it }.toMutableList<String?>()
        
        // Pad the lists to 15 elements with nulls
        while (fullIngredients.size < 15) fullIngredients.add(null)
        while (fullMeasures.size < 15) fullMeasures.add(null)
        
        // Create and add the DrinkDto
        drinks.add(
            DrinkDto(
                id = id,
                name = name,
                imageUrl = imageUrl,
                instructions = instructions,
                ingredient1 = fullIngredients[0],
                ingredient2 = fullIngredients[1],
                ingredient3 = fullIngredients[2],
                ingredient4 = fullIngredients[3],
                ingredient5 = fullIngredients[4],
                ingredient6 = fullIngredients[5],
                ingredient7 = fullIngredients[6],
                ingredient8 = fullIngredients[7],
                ingredient9 = fullIngredients[8],
                ingredient10 = fullIngredients[9],
                ingredient11 = fullIngredients[10],
                ingredient12 = fullIngredients[11],
                ingredient13 = fullIngredients[12],
                ingredient14 = fullIngredients[13],
                ingredient15 = fullIngredients[14],
                measure1 = fullMeasures[0],
                measure2 = fullMeasures[1],
                measure3 = fullMeasures[2],
                measure4 = fullMeasures[3],
                measure5 = fullMeasures[4],
                measure6 = fullMeasures[5],
                measure7 = fullMeasures[6],
                measure8 = fullMeasures[7],
                measure9 = fullMeasures[8],
                measure10 = fullMeasures[9],
                measure11 = fullMeasures[10],
                measure12 = fullMeasures[11],
                measure13 = fullMeasures[12],
                measure14 = fullMeasures[13],
                measure15 = fullMeasures[14]
            )
        )
    }

    /**
     * Simulate API response handling with delays and errors
     */
    private suspend fun <T> simulateResponse(data: T): Resource<T> {
        // Simulate network delay if specified
        if (networkDelayMs > 0) {
            delay(networkDelayMs)
        }

        return when {
            shouldReturnError -> Resource.error(errorType)
            else -> Resource.Success(data)
        }
    }

    /**
     * Get popular cocktails
     */
    suspend fun getPopularCocktails(category: String = "Cocktail"): Resource<DrinkListResponse> {
        val response = if (shouldReturnEmptyList) {
            DrinkListResponse(emptyList())
        } else {
            DrinkListResponse(drinks)
        }
        
        return simulateResponse(response)
    }

    /**
     * Search for cocktails by name
     */
    suspend fun searchCocktailsByName(query: String): Resource<DrinkListResponse> {
        val filteredDrinks = if (shouldReturnEmptyList) {
            emptyList()
        } else {
            drinks.filter { it.name?.contains(query, ignoreCase = true) == true }
        }
        
        return simulateResponse(DrinkListResponse(filteredDrinks))
    }

    /**
     * Search for cocktails by ingredient
     */
    suspend fun searchCocktailsByIngredient(ingredient: String): Resource<DrinkListResponse> {
        val filteredDrinks = if (shouldReturnEmptyList) {
            emptyList()
        } else {
            drinks.filter { drink ->
                getIngredientsList(drink).any { 
                    it.contains(ingredient, ignoreCase = true) 
                }
            }
        }
        
        return simulateResponse(DrinkListResponse(filteredDrinks))
    }
    
    /**
     * Helper method to safely extract all non-null ingredients from a DrinkDto
     */
    private fun getIngredientsList(drink: DrinkDto): List<String> {
        return listOf(
            drink.ingredient1, drink.ingredient2, drink.ingredient3, 
            drink.ingredient4, drink.ingredient5, drink.ingredient6,
            drink.ingredient7, drink.ingredient8, drink.ingredient9,
            drink.ingredient10, drink.ingredient11, drink.ingredient12,
            drink.ingredient13, drink.ingredient14, drink.ingredient15
        ).filterNotNull()
    }

    /**
     * Get cocktail details by ID
     */
    suspend fun getCocktailDetails(id: String): Resource<DrinkListResponse> {
        val drink = drinks.find { it.id == id }
        
        val response = if (shouldReturnEmptyList || drink == null) {
            DrinkListResponse(emptyList())
        } else {
            DrinkListResponse(listOf(drink))
        }
        
        return simulateResponse(response)
    }

    /**
     * Get a list of all ingredients
     */
    suspend fun getIngredientsList(): Resource<IngredientListResponse> {
        val response = if (shouldReturnEmptyList) {
            IngredientListResponse(emptyList())
        } else {
            IngredientListResponse(ingredients)
        }
        
        return simulateResponse(response)
    }

    /**
     * Get a random cocktail
     */
    suspend fun getRandomCocktail(): Resource<DrinkListResponse> {
        val response = if (shouldReturnEmptyList || drinks.isEmpty()) {
            DrinkListResponse(emptyList())
        } else {
            val randomDrink = drinks.random()
            DrinkListResponse(listOf(randomDrink))
        }
        
        return simulateResponse(response)
    }

    /**
     * Get cocktails by first letter
     */
    suspend fun getCocktailsByFirstLetter(letter: String): Resource<DrinkListResponse> {
        val filteredDrinks = if (shouldReturnEmptyList) {
            emptyList()
        } else {
            drinks.filter { it.name?.startsWith(letter, ignoreCase = true) == true }
        }
        
        return simulateResponse(DrinkListResponse(filteredDrinks))
    }
} 