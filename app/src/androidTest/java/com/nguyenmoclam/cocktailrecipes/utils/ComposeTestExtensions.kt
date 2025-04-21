package com.nguyenmoclam.cocktailrecipes.utils

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule

/**
 * Extensions for finding and interacting with UI components in Compose UI tests.
 */

// Find node with text
fun ComposeTestRule.onNodeWithTextExactly(text: String) = onNode(hasText(text, true))

// Find nodes with content description
fun ComposeTestRule.onNodeWithContentDescriptionExactly(description: String) = 
    onNode(hasContentDescription(description, true))

// Wait until a node with specific text exists
fun ComposeTestRule.waitUntilNodeWithTextExists(text: String, timeoutMillis: Long = 5000) {
    waitUntil(timeoutMillis) {
        onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
    }
}

// Wait until a node with specific content description exists
fun ComposeTestRule.waitUntilNodeWithContentDescriptionExists(description: String, timeoutMillis: Long = 5000) {
    waitUntil(timeoutMillis) {
        onAllNodesWithContentDescription(description).fetchSemanticsNodes().isNotEmpty()
    }
}

// Perform scroll to find an item
fun ComposeTestRule.scrollToNodeWithText(scrollableParentTag: String, targetText: String) {
    onNodeWithTag(scrollableParentTag)
        .performScrollToNode(hasText(targetText))
} 