package com.nguyenmoclam.cocktailrecipes.utils

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import com.nguyenmoclam.cocktailrecipes.MainActivity
import org.junit.Rule

/**
 * A JUnit rule that creates an Android Compose Rule linked to an activity for tests
 * that need a complete activity with Compose content.
 */
fun createMainActivityComposeRule() = createAndroidComposeRule<MainActivity>()

/**
 * A JUnit rule that creates a simple Compose Rule for tests that only need a Compose runtime
 * without a full activity (lighter-weight tests).
 */
fun createSimpleComposeRule() = createComposeRule() 