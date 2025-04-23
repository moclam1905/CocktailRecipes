package com.nguyenmoclam.cocktailrecipes.ui.mocktail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onAllNodesWithContentDescription
import com.nguyenmoclam.cocktailrecipes.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class MocktailCornerScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Test để kiểm tra xem Mocktail Corner có hiển thị đúng không
     */
    @Test
    fun mocktailCorner_DisplaysContent() {
        // Navigate to Mocktail Corner
        composeTestRule.onNodeWithContentDescription("Open navigation drawer").performClick()
        composeTestRule.onNodeWithText("Mocktail Corner").performClick()
        composeTestRule.waitForIdle()
        
        // Verify that the screen title is displayed
        composeTestRule.onNodeWithText("Mocktail Corner").assertIsDisplayed()
        
        // Verify that the benefits section is displayed
        composeTestRule.onNodeWithText("Benefits of Mocktails").assertExists()
        
        // Wait for content to load
        Thread.sleep(3000)
        
        // Verify that the description is displayed
        composeTestRule.onNodeWithText("Discover delicious non-alcoholic alternatives").assertExists()
    }
    
    /**
     * Test chức năng quay lại từ Mocktail Corner
     */
    @Test
    fun mocktailCorner_BackButton_NavigatesBack() {
        // Navigate to Mocktail Corner
        composeTestRule.onNodeWithContentDescription("Open navigation drawer").performClick()
        composeTestRule.onNodeWithText("Mocktail Corner").performClick()
        composeTestRule.waitForIdle()
        
        // Verify we're on the Mocktail Corner screen
        composeTestRule.onNodeWithText("Mocktail Corner").assertIsDisplayed()
        
        // Click back button
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()
        
        // Verify we've returned to the Home screen
        composeTestRule.onNodeWithText("Popular Cocktails").assertExists()
    }
    
    /**
     * Test nhấp vào một mocktail sẽ điều hướng đến màn hình chi tiết
     */
    @Test
    fun mocktailCorner_ClickOnMocktail_NavigatesToDetail() {
        // Navigate to Mocktail Corner
        composeTestRule.onNodeWithContentDescription("Open navigation drawer").performClick()
        composeTestRule.onNodeWithText("Mocktail Corner").performClick()
        composeTestRule.waitForIdle()
        
        // Wait for mocktail list to load
        Thread.sleep(3000)
        
        // Find and click on a mocktail item
        val mocktailNodes = composeTestRule.onAllNodesWithContentDescription("Mocktail item")
            .fetchSemanticsNodes()
        
        if (mocktailNodes.isNotEmpty()) {
            // Click on the first mocktail item
            composeTestRule.onAllNodesWithContentDescription("Mocktail item")[0].performClick()
            
            // Wait for detail screen to load
            Thread.sleep(2000)
            
            // Verify that we navigate to the detail screen
            composeTestRule.onNodeWithText("Ingredients").assertExists()
            composeTestRule.onNodeWithText("Instructions").assertExists()
        }
    }
    
    /**
     * Test điều khiển animation toggle
     */
    @Test
    fun mocktailCorner_AnimationControlWorks() {
        // Mở navigation drawer
        composeTestRule.onNodeWithContentDescription("Open navigation drawer").performClick()
        
        // Mở Mocktail Corner
        composeTestRule.onNodeWithText("Mocktail Corner").performClick()
        composeTestRule.waitForIdle()
        
        // Kiểm tra nút điều khiển animation có hiển thị không
        composeTestRule.onNodeWithContentDescription("Disable animations").assertExists()
        
        // Click để tắt animation
        composeTestRule.onNodeWithContentDescription("Disable animations").performClick()
        
        // Kiểm tra nút đã chuyển sang trạng thái Enable
        composeTestRule.onNodeWithContentDescription("Enable animations").assertExists()
    }
} 