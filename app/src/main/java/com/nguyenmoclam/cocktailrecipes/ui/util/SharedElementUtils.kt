package com.nguyenmoclam.cocktailrecipes.ui.util

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

/**
 * Generate a unique key for shared element transitions
 */
fun createSharedElementKey(id: String, elementType: String): String {
    return "shared_element_${elementType}_$id"
}

/**
 * Default spring animation spec for shared element transitions
 */
fun <T> sharedElementSpring(visibilityThreshold: T? = null): FiniteAnimationSpec<T> = spring(
    dampingRatio = Spring.DampingRatioLowBouncy,
    stiffness = Spring.StiffnessMediumLow,
    visibilityThreshold = visibilityThreshold
)

/**
 * Common spring specs for shared element transitions
 */
object SharedElementTransitionSpec {
    val OffsetSpring = sharedElementSpring<Offset>(visibilityThreshold = Offset.VisibilityThreshold)
    val IntOffsetSpring = sharedElementSpring<IntOffset>(visibilityThreshold = IntOffset.VisibilityThreshold)
    val IntSizeSpring = sharedElementSpring<IntSize>(visibilityThreshold = IntSize.VisibilityThreshold)
    
    // Common animation duration 
    const val DefaultDuration = 500
} 