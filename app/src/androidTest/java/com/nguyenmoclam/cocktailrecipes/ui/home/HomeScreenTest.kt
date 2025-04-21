package com.nguyenmoclam.cocktailrecipes.ui.home

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onAllNodesWithContentDescription
import com.nguyenmoclam.cocktailrecipes.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun homeScreen_DisplaysPopularCocktails() {
        // Wait for the home screen to load
        composeTestRule.waitForIdle()
        
        // Verify that the home screen title is displayed
        composeTestRule.onNodeWithText("Popular Cocktails").assertIsDisplayed()
        
        // Verify that some cocktails are loaded and displayed
        // We'll wait a bit to make sure the content loads
        Thread.sleep(5000)
        
        // Verify there's at least one cocktail item
        val cocktailNodes = composeTestRule.onAllNodesWithContentDescription("Cocktail item")
            .fetchSemanticsNodes()
        assert(cocktailNodes.isNotEmpty()) { "Expected at least one cocktail item" }
    }
    
    @Test
    fun homeScreen_ClickOnCocktail_NavigatesToDetailScreen() {
        // Wait for the home screen to load and cocktails to appear
        composeTestRule.waitForIdle()
        Thread.sleep(5000) // Wait for cocktails to load
        
        // Find and click on a cocktail item
        val cocktailNodes = composeTestRule.onAllNodesWithContentDescription("Cocktail item")
            .fetchSemanticsNodes()
        
        if (cocktailNodes.isNotEmpty()) {
            // Click on the first cocktail item
            composeTestRule.onAllNodesWithContentDescription("Cocktail item")[0].performClick()
            
            // Wait for detail screen to load
            Thread.sleep(2000)
            
            // Verify that we navigate to the detail screen
            composeTestRule.onNodeWithText("Ingredients").assertExists()
            composeTestRule.onNodeWithText("Instructions").assertIsDisplayed()
        }
    }
    
    @Test
    fun homeScreen_VerifyCocktailListExists() {
        // Simplified test that just verifies the list exists
        composeTestRule.waitForIdle()
        
        // Verify home screen is displayed
        composeTestRule.onNodeWithText("Popular Cocktails").assertIsDisplayed()
        
        // Verify the cocktail list exists
        Thread.sleep(5000) // Wait for content to load
        composeTestRule.onNodeWithContentDescription("Cocktail list").assertExists()
    }
} 