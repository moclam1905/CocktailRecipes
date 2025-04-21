package com.nguyenmoclam.cocktailrecipes.ui.favorites

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.nguyenmoclam.cocktailrecipes.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class FavoritesScreenTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun favoritesScreen_DisplaysFavoriteTab() {
        // Navigate to favorites screen
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Favorites").performClick()
        
        // Verify favorites screen is displayed
        composeTestRule.onNodeWithText("Favorites").assertIsDisplayed()
        
        // We can't test for specific favorites content because of reference issues,
        // but we can verify the screen loads correctly
        Thread.sleep(2000)
    }
    
    @Test
    fun favoritesScreen_NavigationWorks() {
        // Test navigation between screens
        composeTestRule.waitForIdle()
        
        // Go to favorites
        composeTestRule.onNodeWithContentDescription("Favorites").performClick()
        composeTestRule.onNodeWithText("Favorites").assertIsDisplayed()
        
        // Go to home
        composeTestRule.onNodeWithContentDescription("Home").performClick()
        composeTestRule.onNodeWithText("Popular Cocktails").assertIsDisplayed()
        
        // Go back to favorites
        composeTestRule.onNodeWithContentDescription("Favorites").performClick()
        composeTestRule.onNodeWithText("Favorites").assertIsDisplayed()
    }
} 