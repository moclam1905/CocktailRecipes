package com.nguyenmoclam.cocktailrecipes.accessibility

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
class AccessibilityTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun basicNavigationTest() {
        // Wait for the app to start
        composeTestRule.waitForIdle()
        
        // Verify home screen is displayed
        composeTestRule.onNodeWithText("Popular Cocktails").assertIsDisplayed()
        
        // Navigate to Search screen
        composeTestRule.onNodeWithContentDescription("Search").performClick()
        composeTestRule.waitForIdle()
        
        // Verify on Search screen
        composeTestRule.onNodeWithText("Search Cocktails").assertIsDisplayed()
        
        // Navigate to Favorites screen
        composeTestRule.onNodeWithContentDescription("Favorites").performClick()
        composeTestRule.waitForIdle()
        
        // Verify on Favorites screen
        composeTestRule.onNodeWithText("Favorites").assertIsDisplayed()
    }
} 