package com.nguyenmoclam.cocktailrecipes.data.repository

import com.nguyenmoclam.cocktailrecipes.data.common.ApiError
import com.nguyenmoclam.cocktailrecipes.data.common.ErrorType
import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.model.Ingredient
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Fake implementation of CocktailRepository for testing
 * Allows for controlled testing without actual network or database access
 */
class FakeCocktailRepository : CocktailRepository {
    
    // Test data for different scenarios
    val cocktails = mutableListOf<Cocktail>()
    val favorites = mutableSetOf<String>()
    
    // Testing flags to simulate different states
    var shouldReturnError = false
    var shouldReturnEmpty = false
    var shouldReturnLoading = false
    var networkError = false
    var delayTimeMillis = 0L
    
    /**
     * Set up the repository with predefined test data
     */
    fun populateWithTestData() {
        cocktails.clear()
        cocktails.addAll(createTestCocktails())
    }
    
    /**
     * Clear all test data
     */
    fun clearData() {
        cocktails.clear()
        favorites.clear()
    }
    
    /**
     * Simulate a network error
     */
    fun simulateNetworkError(isNetworkError: Boolean = true) {
        shouldReturnError = true
        networkError = isNetworkError
    }
    
    /**
     * Simulate an empty response
     */
    fun simulateEmptyResponse(isEmpty: Boolean = true) {
        shouldReturnEmpty = isEmpty
    }
    
    /**
     * Simulate a loading state
     */
    fun simulateLoading(isLoading: Boolean = true) {
        shouldReturnLoading = isLoading
    }
    
    /**
     * Add a specific cocktail to test data
     */
    fun addCocktail(cocktail: Cocktail) {
        cocktails.add(cocktail)
    }
    
    override suspend fun getPopularCocktails(): Flow<Resource<List<Cocktail>>> = flow {
        if (shouldReturnLoading) {
            emit(Resource.Loading)
        }
        
        if (shouldReturnError) {
            emit(createErrorResource())
            return@flow
        }
        
        val data = if (shouldReturnEmpty) emptyList() else cocktails
        emit(Resource.Success(data))
    }
    
    override suspend fun searchCocktailsByName(query: String): Flow<Resource<List<Cocktail>>> = flow {
        if (shouldReturnLoading) {
            emit(Resource.Loading)
        }
        
        if (shouldReturnError) {
            emit(createErrorResource())
            return@flow
        }
        
        val filteredCocktails = cocktails.filter { 
            it.name.contains(query, ignoreCase = true) 
        }
        
        val data = if (shouldReturnEmpty) emptyList() else filteredCocktails
        emit(Resource.Success(data))
    }
    
    override suspend fun searchCocktailsByIngredient(ingredient: String): Flow<Resource<List<Cocktail>>> = flow {
        if (shouldReturnLoading) {
            emit(Resource.Loading)
        }
        
        if (shouldReturnError) {
            emit(createErrorResource())
            return@flow
        }
        
        val filteredCocktails = cocktails.filter { cocktail ->
            cocktail.ingredients.any { it.name.contains(ingredient, ignoreCase = true) }
        }
        
        val data = if (shouldReturnEmpty) emptyList() else filteredCocktails
        emit(Resource.Success(data))
    }
    
    override suspend fun getCocktailDetails(id: String): Flow<Resource<Cocktail>> = flow {
        if (shouldReturnLoading) {
            emit(Resource.Loading)
        }
        
        if (shouldReturnError) {
            emit(createErrorResource())
            return@flow
        }
        
        val cocktail = cocktails.find { it.id == id }
        
        if (cocktail != null) {
            val isFavorite = favorites.contains(id)
            emit(Resource.Success(cocktail.copy(isFavorite = isFavorite)))
        } else {
            emit(Resource.error(ApiError.notFoundError("Cocktail not found")))
        }
    }
    
    override suspend fun saveFavorite(cocktail: Cocktail): Flow<Resource<Boolean>> = flow {
        if (shouldReturnLoading) {
            emit(Resource.Loading)
        }
        
        if (shouldReturnError) {
            emit(createErrorResource())
            return@flow
        }
        
        favorites.add(cocktail.id)
        
        // Ensure we have the cocktail in our test data
        if (!cocktails.any { it.id == cocktail.id }) {
            cocktails.add(cocktail)
        }
        
        emit(Resource.Success(true))
    }
    
    override suspend fun removeFavorite(id: String): Flow<Resource<Boolean>> = flow {
        if (shouldReturnLoading) {
            emit(Resource.Loading)
        }
        
        if (shouldReturnError) {
            emit(createErrorResource())
            return@flow
        }
        
        favorites.remove(id)
        emit(Resource.Success(true))
    }
    
    override suspend fun getFavorites(): Flow<Resource<List<Cocktail>>> = flow {
        if (shouldReturnLoading) {
            emit(Resource.Loading)
        }
        
        if (shouldReturnError) {
            emit(createErrorResource())
            return@flow
        }
        
        val favoriteCocktails = cocktails.filter { favorites.contains(it.id) }
            .map { it.copy(isFavorite = true) }
        
        val data = if (shouldReturnEmpty) emptyList() else favoriteCocktails
        emit(Resource.Success(data))
    }

    override suspend fun isFavorite(id: String): Flow<Resource<Boolean>> = flow {
        if (shouldReturnLoading) {
            emit(Resource.Loading)
            // Don't return here, allow subsequent emits
        }

        if (shouldReturnError) {
            emit(createErrorResource<Boolean>()) // Specify type argument for generic function
            return@flow
        }
        
        // Simulate potential delay if configured
        if (delayTimeMillis > 0) {
            kotlinx.coroutines.delay(delayTimeMillis)
        }
        
        val isFav = favorites.contains(id)
        emit(Resource.Success(isFav))
    }

    /**
     * Helper function to create appropriate error resource based on settings
     */
    private fun <T> createErrorResource(): Resource<T> {
        return if (networkError) {
            Resource.error(ApiError.networkError("Test network error"))
        } else {
            Resource.error(ApiError(
                message = "Test error",
                errorType = ErrorType.GENERIC
            ))
        }
    }
    
    /**
     * Create test cocktail data
     */
    private fun createTestCocktails(): List<Cocktail> {
        return listOf(
            Cocktail(
                id = "1",
                name = "Mojito",
                imageUrl = "https://www.example.com/mojito.jpg",
                instructions = "Mix mint, sugar, lime juice. Add rum and soda water.",
                ingredients = listOf(
                    Ingredient("White rum", "2 oz"),
                    Ingredient("Lime juice", "1 oz"),
                    Ingredient("Sugar", "2 tsp"),
                    Ingredient("Mint", "6 leaves"),
                    Ingredient("Soda water", "Top")
                )
            ),
            Cocktail(
                id = "2",
                name = "Margarita",
                imageUrl = "https://www.example.com/margarita.jpg",
                instructions = "Rim glass with salt. Shake tequila, triple sec, and lime juice with ice. Strain into glass.",
                ingredients = listOf(
                    Ingredient("Tequila", "2 oz"),
                    Ingredient("Triple sec", "1 oz"),
                    Ingredient("Lime juice", "1 oz"),
                    Ingredient("Salt", "For rim")
                )
            ),
            Cocktail(
                id = "3",
                name = "Old Fashioned",
                imageUrl = "https://www.example.com/oldfashioned.jpg",
                instructions = "Muddle sugar cube with bitters. Add whiskey and ice. Garnish with orange peel.",
                ingredients = listOf(
                    Ingredient("Bourbon", "2 oz"),
                    Ingredient("Angostura bitters", "2-3 dashes"),
                    Ingredient("Sugar cube", "1"),
                    Ingredient("Orange peel", "1")
                )
            )
        )
    }
} 