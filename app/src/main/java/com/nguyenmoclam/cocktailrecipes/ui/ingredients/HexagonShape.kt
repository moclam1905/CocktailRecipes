package com.nguyenmoclam.cocktailrecipes.ui.ingredients

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Custom hexagon shape for the ingredient grid
 */
class HexagonShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(Path().apply {
            // Calculate dimensions
            val width = size.width
            val height = size.height
            val minDimension = min(width, height)
            
            // Calculate hexagon points
            val radius = minDimension / 2f
            val centerX = width / 2f
            val centerY = height / 2f
            val hexagonCorners = 6
            
            // Move to the first corner
            val angle = 2.0 * Math.PI / hexagonCorners
            val startAngle = Math.PI / 6.0 // 30 degrees to make a regular hexagon
            
            moveTo(
                (centerX + radius * Math.cos(startAngle)).toFloat(),
                (centerY + radius * Math.sin(startAngle)).toFloat()
            )
            
            // Draw lines to each corner
            for (i in 1 until hexagonCorners) {
                val degrees = startAngle + angle * i
                lineTo(
                    (centerX + radius * Math.cos(degrees)).toFloat(),
                    (centerY + radius * Math.sin(degrees)).toFloat()
                )
            }
            
            // Close the path
            close()
        })
    }
} 