package com.nguyenmoclam.cocktailrecipes

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.junit.Rule
import org.junit.Test

class SimpleComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun simpleTextTest() {
        // Start the app
        composeTestRule.setContent {
            SimpleTextComposable("Hello World")
        }

        // Assert that the text is displayed
        composeTestRule.onNodeWithText("Hello World").assertIsDisplayed()
    }
}

@Composable
fun SimpleTextComposable(text: String) {
    Box {
        Text(text = text)
    }
} 