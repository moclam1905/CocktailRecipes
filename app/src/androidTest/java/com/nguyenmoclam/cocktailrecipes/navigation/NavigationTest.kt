package com.nguyenmoclam.cocktailrecipes.navigation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.nguyenmoclam.cocktailrecipes.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class NavigationTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun navigationBar_ClickOnSearchTab_NavigatesToSearchScreen() {
        // Wait for the app to initialize and the home screen to appear
        composeTestRule.waitForIdle()
        
        // Click on the Search tab in the bottom navigation
        composeTestRule.onNodeWithContentDescription("Search").performClick()
        
        // Verify that we navigated to the Search screen by checking for search screen content
        composeTestRule.onNodeWithText("Search Cocktails").assertIsDisplayed()
    }
    
    @Test
    fun navigationBar_ClickOnFavoritesTab_NavigatesToFavoritesScreen() {
        // Wait for the app to initialize and the home screen to appear
        composeTestRule.waitForIdle()
        
        // Click on the Favorites tab in the bottom navigation
        composeTestRule.onNodeWithContentDescription("Favorites").performClick()
        
        // Verify that we navigated to the Favorites screen
        composeTestRule.onNodeWithText("Favorites").assertIsDisplayed()
    }
    
    @Test
    fun navigationBar_ClickOnHomeTab_NavigatesToHomeScreen() {
        // Start with navigation to another tab first
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Search").performClick()
        
        // Then navigate back to home
        composeTestRule.onNodeWithContentDescription("Home").performClick()
        
        // Verify that we navigated to the Home screen
        composeTestRule.onNodeWithText("Popular Cocktails").assertIsDisplayed()
    }
} 