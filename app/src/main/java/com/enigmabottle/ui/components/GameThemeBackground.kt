package com.enigmabottle.ui.components

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
                    "dark_interface" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0F172A), Color(0xFF020617))
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
                    "cyberpunk" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0F0B1E), Color(0xFF020106))
                        )
                    )
                    "mystic_swamp" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0D1F11), Color(0xFF030A05))
                        )
                    )
                    "volcano" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF1E0B05), Color(0xFF0C0200))
                        )
                    )
                    "frozen_glacier" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF132F3C), Color(0xFF051119))
                        )
                    )
                    "ancient_temple" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF1C1D18), Color(0xFF0D0E0B))
                        )
                    )
                    "enchanted_forest" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0A1F0D), Color(0xFF030A04))
                        )
                    )
                    "steampunk" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF261D15), Color(0xFF100B07))
                        )
                    )
                    "underwater_reef" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF003049), Color(0xFF001524))
                        )
                    )
                    "supernova" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF3B0D2E), Color(0xFF0F0218))
                        )
                    )
                    "retro_arcade" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0C041C), Color(0xFF010005))
                        )
                    )
                    "starry_night" -> Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0B132B), Color(0xFF010409))
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
                "dark_interface" -> {
                    // Draw soft dark organic accent gradients and fine minimalist guides
                    drawCircle(
                        color = Color(0xFF1E293B).copy(alpha = 0.4f),
                        radius = width * 0.45f,
                        center = Offset(width * 0.05f, height * 0.2f)
                    )
                    drawCircle(
                        color = Color(0xFF1E293B).copy(alpha = 0.25f),
                        radius = width * 0.4f,
                        center = Offset(width * 0.95f, height * 0.8f)
                    )
                    drawLine(
                        color = Color(0xFF334155).copy(alpha = 0.5f),
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
                "cyberpunk" -> {
                    // Minimalist tall building silhouettes in deep background
                    val bldColor = Color(0xFF6D28D9).copy(alpha = 0.08f)
                    drawRect(bldColor, Offset(width * 0.05f, height * 0.4f), Size(width * 0.25f, height * 0.6f))
                    drawRect(bldColor, Offset(width * 0.35f, height * 0.55f), Size(width * 0.3f, height * 0.45f))
                    drawRect(bldColor, Offset(width * 0.7f, height * 0.35f), Size(width * 0.25f, height * 0.65f))
                    
                    // Cyber neon rain drops
                    val rainTime = (System.currentTimeMillis() % 2000) / 2000f
                    val magentaNeon = Color(0xFFEC4899).copy(alpha = 0.18f)
                    val cyanNeon = Color(0xFF06B6D4).copy(alpha = 0.15f)
                    
                    drawLine(cyanNeon, Offset(width * 0.15f, height * ((rainTime + 0.1f) % 1.0f)), Offset(width * 0.15f, height * ((rainTime + 0.15f) % 1.0f)), 2.dp.toPx())
                    drawLine(magentaNeon, Offset(width * 0.45f, height * ((rainTime + 0.6f) % 1.0f)), Offset(width * 0.45f, height * ((rainTime + 0.68f) % 1.0f)), 2.dp.toPx())
                    drawLine(cyanNeon, Offset(width * 0.8f, height * ((rainTime + 0.3f) % 1.0f)), Offset(width * 0.8f, height * ((rainTime + 0.37f) % 1.0f)), 2.dp.toPx())
                }
                "mystic_swamp" -> {
                    // Floating wisps/fairy fire particles (pure green mystic magic)
                    val time = (System.currentTimeMillis() % 3000) / 3000f
                    val wispGreen = Color(0xFF2ECC71).copy(alpha = 0.25f)
                    
                    drawCircle(wispGreen, 12.dp.toPx(), Offset(width * 0.2f, height * (0.8f - 0.4f * time)))
                    drawCircle(wispGreen, 8.dp.toPx(), Offset(width * 0.75f, height * (0.75f - 0.5f * ((time + 0.5f) % 1f))))
                    drawCircle(wispGreen, 14.dp.toPx(), Offset(width * 0.5f, height * (0.9f - 0.3f * ((time + 0.2f) % 1f))))
                }
                "volcano" -> {
                    // Rising glowing volcano ash particles
                    val time = (System.currentTimeMillis() % 2500) / 2500f
                    val emberColor = Color(0xFFF97316).copy(alpha = 0.3f)
                    
                    // Bottom hot magma glow
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xFFEA580C).copy(alpha = 0.15f)),
                            startY = height * 0.7f,
                            endY = height
                        )
                    )
                    
                    drawCircle(emberColor, 5.dp.toPx(), Offset(width * 0.3f, height * (0.95f - 0.6f * time)))
                    drawCircle(emberColor, 4.dp.toPx(), Offset(width * 0.65f, height * (0.9f - 0.7f * ((time + 0.3f) % 1f))))
                    drawCircle(emberColor, 6.dp.toPx(), Offset(width * 0.1f, height * (0.85f - 0.5f * ((time + 0.7f) % 1f))))
                    drawCircle(emberColor, 3.dp.toPx(), Offset(width * 0.85f, height * (0.98f - 0.65f * ((time + 0.15f) % 1f))))
                }
                "frozen_glacier" -> {
                    // Ice geometric sparkles
                    val iceColor = Color(0xFF38BDF8).copy(alpha = 0.1f)
                    val whiteIce = Color.White.copy(alpha = 0.08f)
                    
                    // Top-left shard
                    val path1 = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(width * 0.35f, 0f)
                        lineTo(0f, height * 0.28f)
                        close()
                    }
                    drawPath(path1, iceColor)
                    
                    // Bottom-right shard
                    val path2 = Path().apply {
                        moveTo(width, height)
                        lineTo(width * 0.6f, height)
                        lineTo(width, height * 0.7f)
                        close()
                    }
                    drawPath(path2, iceColor)
                    
                    // Floating ice diamond sparkles
                    drawCircle(whiteIce, 15f, Offset(width * 0.2f, height * 0.4f))
                    drawCircle(whiteIce, 20f, Offset(width * 0.8f, height * 0.3f))
                    drawCircle(whiteIce, 12f, Offset(width * 0.5f, height * 0.75f))
                }
                "ancient_temple" -> {
                    // Golden geometric ancient mystical frames
                    val goldLine = Color(0xFFD4AF37).copy(alpha = 0.09f)
                    
                    // Left vertical line
                    drawLine(goldLine, Offset(width * 0.08f, 0f), Offset(width * 0.08f, height), 2.dp.toPx())
                    // Right vertical line
                    drawLine(goldLine, Offset(width * 0.92f, 0f), Offset(width * 0.92f, height), 2.dp.toPx())
                    
                    // Mystic circle center
                    drawCircle(goldLine, width * 0.25f, Offset(width * 0.5f, height * 0.5f), style = Stroke(width = 1.5.dp.toPx()))
                }
                "enchanted_forest" -> {
                    // Golden diagonal fairy sunbeams
                    val goldBeam = Color(0xFFFEF08A).copy(alpha = 0.08f)
                    val beamW = width * 0.15f
                    
                    val beam1 = Path().apply {
                        moveTo(width * 0.1f, 0f)
                        lineTo(width * 0.1f + beamW, 0f)
                        lineTo(width * 0.6f + beamW, height)
                        lineTo(width * 0.6f, height)
                        close()
                    }
                    drawPath(beam1, goldBeam)
                    
                    val beam2 = Path().apply {
                        moveTo(width * 0.5f, 0f)
                        lineTo(width * 0.5f + beamW, 0f)
                        lineTo(width * 0.95f + beamW, height)
                        lineTo(width * 0.95f, height)
                        close()
                    }
                    drawPath(beam2, goldBeam)
                }
                "steampunk" -> {
                    // Vintage Victorian bronze gear wheels
                    val bronzeColor = Color(0xFFB45309).copy(alpha = 0.07f)
                    
                    // Large gear bottom-left
                    drawCircle(bronzeColor, width * 0.35f, Offset(0f, height * 0.85f), style = Stroke(width = 3.dp.toPx()))
                    drawCircle(bronzeColor, width * 0.12f, Offset(0f, height * 0.85f), style = Stroke(width = 2.dp.toPx()))
                    
                    // Medium gear top-right
                    drawCircle(bronzeColor, width * 0.25f, Offset(width, height * 0.15f), style = Stroke(width = 3.dp.toPx()))
                    drawCircle(bronzeColor, width * 0.08f, Offset(width, height * 0.15f), style = Stroke(width = 2.dp.toPx()))
                }
                "underwater_reef" -> {
                    // Underwater light beams & ocean bubble stream
                    val blueBeam = Color(0xFF60A5FA).copy(alpha = 0.07f)
                    val pathBeam = Path().apply {
                        moveTo(width * 0.3f, 0f)
                        lineTo(width * 0.7f, 0f)
                        lineTo(width * 0.9f, height)
                        lineTo(width * 0.1f, height)
                        close()
                    }
                    drawPath(pathBeam, blueBeam)
                    
                    // Ocean bubbles
                    val time = (System.currentTimeMillis() % 4000) / 4000f
                    drawCircle(Color.White.copy(alpha = 0.06f), 10.dp.toPx(), Offset(width * 0.2f, height * (1.1f - time)))
                    drawCircle(Color.White.copy(alpha = 0.09f), 6.dp.toPx(), Offset(width * 0.8f, height * (1.2f - time * 1.2f)))
                    drawCircle(Color.White.copy(alpha = 0.05f), 14.dp.toPx(), Offset(width * 0.45f, height * (0.95f - time * 0.8f)))
                }
                "supernova" -> {
                    // Shockwaves of cosmic explosions
                    val purpleWave = Color(0xFFC084FC).copy(alpha = 0.06f)
                    val orangeWave = Color(0xFFFB923C).copy(alpha = 0.05f)
                    
                    drawCircle(purpleWave, width * 0.45f, Offset(width * 0.5f, height * 0.45f), style = Stroke(width = 8.dp.toPx()))
                    drawCircle(orangeWave, width * 0.55f, Offset(width * 0.5f, height * 0.45f), style = Stroke(width = 4.dp.toPx()))
                    
                    // Center core
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent),
                            center = Offset(width * 0.5f, height * 0.45f),
                            radius = width * 0.2f
                        ),
                        radius = width * 0.2f,
                        center = Offset(width * 0.5f, height * 0.45f)
                    )
                }
                "retro_arcade" -> {
                    // Retro Synthwave Horizon Grid
                    val gridColor = Color(0xFFEC4899).copy(alpha = 0.07f)
                    val horizonY = height * 0.6f
                    
                    // Grid background
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xFF6D28D9).copy(alpha = 0.06f)),
                            startY = horizonY,
                            endY = height
                        )
                    )
                    
                    // Horizon line
                    drawLine(gridColor, Offset(0f, horizonY), Offset(width, horizonY), 2.dp.toPx())
                    
                    // Horizontal lines
                    for (i in 1..8) {
                        val y = horizonY + (height - horizonY) * (i.toFloat() / 8f) * (i.toFloat() / 8f)
                        drawLine(gridColor, Offset(0f, y), Offset(width, y), 1.dp.toPx())
                    }
                    
                    // Perspective lines
                    for (i in 0..10) {
                        val startX = width * (i.toFloat() / 10f)
                        drawLine(
                            gridColor,
                            Offset(startX, horizonY),
                            Offset(width * 0.5f + (startX - width * 0.5f) * 4f, height),
                            1.dp.toPx()
                        )
                    }
                }
                "starry_night" -> {
                    // Starry night with stars and a glowing gold crescent moon
                    val time = (System.currentTimeMillis() % 6000) / 6000f
                    val starColor = Color.White.copy(alpha = 0.5f + 0.4f * kotlin.math.sin(time * 2f * kotlin.math.PI.toFloat()))
                    
                    // Shiny stars
                    drawCircle(starColor, 3f, Offset(width * 0.15f, height * 0.12f))
                    drawCircle(starColor.copy(alpha = starColor.alpha * 0.7f), 4f, Offset(width * 0.35f, height * 0.28f))
                    drawCircle(starColor, 5f, Offset(width * 0.55f, height * 0.08f))
                    drawCircle(starColor.copy(alpha = starColor.alpha * 0.5f), 3f, Offset(width * 0.72f, height * 0.32f))
                    drawCircle(starColor, 4f, Offset(width * 0.9f, height * 0.22f))
                    drawCircle(starColor, 3.5f, Offset(width * 0.25f, height * 0.45f))
                    drawCircle(starColor, 3f, Offset(width * 0.8f, height * 0.48f))

                    // Gold crescent moon
                    val moonCenter = Offset(width * 0.8f, height * 0.18f)
                    val moonRadius = width * 0.07f
                    
                    // Golden base circle
                    drawCircle(
                        color = Color(0xFFFDE047).copy(alpha = 0.88f),
                        radius = moonRadius,
                        center = moonCenter
                    )
                    // Shadow overlay circle (color matching top of gradient)
                    drawCircle(
                        color = Color(0xFF0B132B),
                        radius = moonRadius * 0.92f,
                        center = Offset(moonCenter.x - moonRadius * 0.38f, moonCenter.y - moonRadius * 0.08f)
                    )
                }
                else -> {
                    // "wood" shelf drawing removed by request
                }
            }
        }
        content()
    }
}
