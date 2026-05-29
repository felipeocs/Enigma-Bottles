package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun GameThemeBackground(
    bgId: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                when (bgId) {
                    "lab" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF111E25), Color(0xFF070B0E))
                        )
                    )
                    "magic" -> Modifier.background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF1D0E3D), Color(0xFF06030F)),
                            center = Offset(500f, 600f),
                            radius = 1200f
                        )
                    )
                    "neon_grid" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF140220), Color(0xFF040008))
                        )
                    )
                    "sleek_interface" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFFDFBFF), Color(0xFFF5F3FF))
                        )
                    )
                    "clear_aurora" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFF0FDF4), Color(0xFFFAE8FF))
                        )
                    )
                    "clear_sunset" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFF7ED), Color(0xFFFEF3C7))
                        )
                    )
                    "clear_mint" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFF0FDFA), Color(0xFFECFDF5))
                        )
                    )
                    "clear_lavender" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFF5F3FF), Color(0xFFEEF2FF))
                        )
                    )
                    "clear_sakura" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFF5F5), Color(0xFFFFF0F6))
                        )
                    )
                    "abyss" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF021720), Color(0xFF00080C))
                        )
                    )
                    "cosmic" -> Modifier.background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF200C40), Color(0xFF050110)),
                            center = Offset(400f, 500f),
                            radius = 1100f
                        )
                    )
                    else -> Modifier.background( // "wood"
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF332014), Color(0xFF180E08))
                        )
                    )
                }
            )
    ) {
        // Draw responsive visual elements unique to each theme on Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            when (bgId) {
                "sleek_interface" -> {
                    // Draw soft organic accent gradients and fine minimalist guides
                    drawCircle(
                        color = Color(0xFFEEF2FF),
                        radius = width * 0.45f,
                        center = Offset(width * 0.05f, height * 0.2f)
                    )
                    drawCircle(
                        color = Color(0xFFE0E7FF).copy(alpha = 0.6f),
                        radius = width * 0.4f,
                        center = Offset(width * 0.95f, height * 0.8f)
                    )
                    drawLine(
                        color = Color(0xFFE2E8F0),
                        start = Offset(0f, height * 0.58f),
                        end = Offset(width, height * 0.58f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                "clear_aurora" -> {
                    // Soft pastel northern lights circles
                    drawCircle(
                        color = Color(0xFFA7F3D0).copy(alpha = 0.4f), // light mint
                        radius = width * 0.5f,
                        center = Offset(width * 0.1f, height * 0.15f)
                    )
                    drawCircle(
                        color = Color(0xFFF3E8FF).copy(alpha = 0.5f), // light violet
                        radius = width * 0.45f,
                        center = Offset(width * 0.9f, height * 0.75f)
                    )
                }
                "clear_sunset" -> {
                    // Soft warm peach circles
                    drawCircle(
                        color = Color(0xFFFFEDD5).copy(alpha = 0.7f), // soft orange
                        radius = width * 0.4f,
                        center = Offset(width * 0.8f, height * 0.2f)
                    )
                    drawCircle(
                        color = Color(0xFFFEF3C7).copy(alpha = 0.6f), // soft yellow
                        radius = width * 0.35f,
                        center = Offset(width * 0.2f, height * 0.8f)
                    )
                }
                "clear_mint" -> {
                    // Fresh clean lines & soft seafoam circles
                    drawCircle(
                        color = Color(0xFFCCFBF1).copy(alpha = 0.6f), // teal/mint
                        radius = width * 0.48f,
                        center = Offset(width * 0.15f, height * 0.7f)
                    )
                    drawCircle(
                        color = Color(0xFFE0F2FE).copy(alpha = 0.5f), // ice blue
                        radius = width * 0.4f,
                        center = Offset(width * 0.85f, height * 0.25f)
                    )
                }
                "clear_lavender" -> {
                    // Soft lavender and periwinkle dreams
                    drawCircle(
                        color = Color(0xFFEDE9FE).copy(alpha = 0.7f),
                        radius = width * 0.52f,
                        center = Offset(width * 0.5f, height * 0.85f)
                    )
                    drawCircle(
                        color = Color(0xFFE0E7FF).copy(alpha = 0.5f),
                        radius = width * 0.38f,
                        center = Offset(width * 0.1f, height * 0.2f)
                    )
                }
                "clear_sakura" -> {
                    // Delicate pink blossom drops
                    drawCircle(
                        color = Color(0xFFFFE4E6).copy(alpha = 0.7f), // soft rose pink
                        radius = width * 0.45f,
                        center = Offset(width * 0.9f, height * 0.15f)
                    )
                    drawCircle(
                        color = Color(0xFFFFF1F2).copy(alpha = 0.5f), // softer white rose
                        radius = width * 0.4f,
                        center = Offset(width * 0.15f, height * 0.78f)
                    )
                }
                "lab" -> {
                    // Draw clean geometric circuitry/blueprint lines
                    val path = Path().apply {
                        moveTo(0f, height * 0.3f)
                        lineTo(width * 0.25f, height * 0.3f)
                        lineTo(width * 0.4f, height * 0.45f)
                        lineTo(width, height * 0.45f)

                        moveTo(width, height * 0.75f)
                        lineTo(width * 0.7f, height * 0.75f)
                        lineTo(width * 0.55f, height * 0.6f)
                        lineTo(0f, height * 0.6f)
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF00ACC1).copy(alpha = 0.08f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
                "magic" -> {
                    // Draw sparkling stars in the sky
                    drawCircle(Color.White.copy(alpha = 0.4f), 4f, Offset(width * 0.2f, height * 0.15f))
                    drawCircle(Color.White.copy(alpha = 0.6f), 6f, Offset(width * 0.85f, height * 0.25f))
                    drawCircle(Color.White.copy(alpha = 0.3f), 3f, Offset(width * 0.15f, height * 0.75f))
                    drawCircle(Color.White.copy(alpha = 0.5f), 5f, Offset(width * 0.65f, height * 0.85f))
                    drawCircle(Color.White.copy(alpha = 0.7f), 8f, Offset(width * 0.5f, height * 0.1f))
                }
                "neon_grid" -> {
                    // Draw retro perspective grid lines (synthwave style)
                    val gridColor = Color(0xFFFF007F).copy(alpha = 0.12f)
                    val strokeW = 1.dp.toPx()

                    // Horizontal lines
                    val lineCount = 12
                    for (i in 0 until lineCount) {
                        val ratio = i.toFloat() / lineCount
                        val y = height * (0.4f + 0.6f * ratio)
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = strokeW
                        )
                    }
                    // Vertical lines originating from middle horizon
                    val verticalLines = 8
                    val horizonY = height * 0.38f
                    for (i in 0..verticalLines) {
                        val fraction = i.toFloat() / verticalLines
                        val startX = width * fraction
                        drawLine(
                            color = gridColor,
                            start = Offset(startX, horizonY),
                            end = Offset(width * 0.5f + (startX - width * 0.5f) * 4.5f, height),
                            strokeWidth = strokeW
                        )
                    }
                }
                "abyss" -> {
                    // Deep sea rising glowing bubbles
                    val bubbleOffset = (System.currentTimeMillis() % 4000) / 4000f
                    drawCircle(Color(0xFF26C6DA).copy(alpha = 0.15f), width * 0.08f, Offset(width * 0.25f, height * (1f - bubbleOffset)))
                    drawCircle(Color(0xFF00ACC1).copy(alpha = 0.18f), width * 0.05f, Offset(width * 0.75f, height * (1.2f - bubbleOffset * 1.3f).coerceIn(0f, 1f)))
                    drawCircle(Color(0xFF80DEEA).copy(alpha = 0.12f), width * 0.04f, Offset(width * 0.45f, height * (0.8f - bubbleOffset * 0.8f)))
                }
                "cosmic" -> {
                    // Nebulous galaxy/space particles and starry field
                    drawCircle(Color(0xFFEC4899).copy(alpha = 0.06f), width * 0.6f, Offset(width * 0.8f, height * 0.15f))
                    drawCircle(Color(0xFF8B5CF6).copy(alpha = 0.09f), width * 0.7f, Offset(width * 0.1f, height * 0.75f))
                    
                    // Tiny shining stars
                    drawCircle(Color.White.copy(alpha = 0.8f), 3f, Offset(width * 0.1f, height * 0.15f))
                    drawCircle(Color.White.copy(alpha = 0.4f), 2f, Offset(width * 0.4f, height * 0.35f))
                    drawCircle(Color.White.copy(alpha = 0.9f), 4f, Offset(width * 0.78f, height * 0.65f))
                }
                else -> {
                    // "wood" shelf background drawing
                    // Draw solid horizontal shelving planks underneath bottle placement area
                    val shelfColor = Color(0xFF4E342E) // Solid, rich dark wood color
                    val shelfTopY = height * 0.58f
                    val shelfH = height * 0.035f

                    // Main wooden plank shelf 1
                    // Underneath dark drop shadow
                    drawRoundRect(
                        color = Color.Black.copy(alpha = 0.25f),
                        topLeft = Offset(width * 0.05f, shelfTopY + 4f),
                        size = Size(width * 0.9f, shelfH),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                    // Solid filled plank
                    drawRoundRect(
                        color = shelfColor,
                        topLeft = Offset(width * 0.05f, shelfTopY),
                        size = Size(width * 0.9f, shelfH),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                    // Highlights on top edge of plank
                    drawLine(
                        color = Color(0xFF8D6E63).copy(alpha = 0.4f),
                        start = Offset(width * 0.08f, shelfTopY + 2f),
                        end = Offset(width * 0.92f, shelfTopY + 2f),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Secondary wooden plank shelf 2
                    val shelfTopY2 = height * 0.85f
                    // Underneath dark drop shadow
                    drawRoundRect(
                        color = Color.Black.copy(alpha = 0.25f),
                        topLeft = Offset(width * 0.05f, shelfTopY2 + 4f),
                        size = Size(width * 0.9f, shelfH),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                    // Solid filled plank
                    drawRoundRect(
                        color = shelfColor,
                        topLeft = Offset(width * 0.05f, shelfTopY2),
                        size = Size(width * 0.9f, shelfH),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                    // Highlights on top edge of plank
                    drawLine(
                        color = Color(0xFF8D6E63).copy(alpha = 0.4f),
                        start = Offset(width * 0.08f, shelfTopY2 + 2f),
                        end = Offset(width * 0.92f, shelfTopY2 + 2f),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        }
        content()
    }
}
