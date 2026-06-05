package com.enigmabottle.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp

@Composable
fun BottleGlassware(
    liquidColor: Color,
    skinId: String,
    isSelected: Boolean,
    isHintFlag: Boolean,
    isLight: Boolean = false,
    hintTargetColor: Color? = null,
    modifier: Modifier = Modifier
) {
    // Detect whether we are in a light theme to dynamically contrast glassware frames
    val glassOutlineColor = if (isLight) Color(0xFF475569).copy(alpha = 0.55f) else Color.White.copy(alpha = 0.85f)
    val glassShineColor = if (isLight) Color(0xFF475569).copy(alpha = 0.45f) else Color.White.copy(alpha = 0.35f)
    val capColor = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.85f)

    // Selection pulse and bubble animation variables
    val infiniteTransition = rememberInfiniteTransition(label = "bottleAnimations")
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "selectPulse"
    )

    val bubbleOffset by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubbleHeight"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f

        // Draw halo first
        if (isSelected) {
            drawCircle(
                color = Color(0xFFFFC107).copy(alpha = 0.3f * pulseScale),
                radius = (width * 0.55f).coerceAtMost(height * 0.5f),
                center = Offset(centerX, height * 0.55f)
            )
        } else if (isHintFlag) {
            drawCircle(
                color = Color(0xFF2ECC71).copy(alpha = 0.4f * pulseScale),
                radius = (width * 0.60f).coerceAtMost(height * 0.5f),
                center = Offset(centerX, height * 0.55f)
            )
        }

        when (skinId) {
            "flask" -> {
                // Erlenmeyer Lab Flask (Science vibe)
                val neckW = width * 0.22f
                val neckH = height * 0.22f
                val neckY = height * 0.15f
                val baseW = width * 0.72f
                val baseH = height * 0.55f
                val baseY = height * 0.88f

                val flaskPath = Path().apply {
                    moveTo(centerX - neckW / 2f, neckY)
                    lineTo(centerX + neckW / 2f, neckY)
                    lineTo(centerX + neckW / 2f, neckY + neckH)
                    lineTo(centerX + baseW / 2f, baseY - baseH * 0.2f)
                    // base curve
                    quadraticTo(centerX + baseW / 2f, baseY, centerX, baseY)
                    quadraticTo(centerX - baseW / 2f, baseY, centerX - baseW / 2f, baseY - baseH * 0.2f)
                    lineTo(centerX - neckW / 2f, neckY + neckH)
                    close()
                }

                // Clip liquid path inside flask outline
                drawContext.canvas.save()
                drawContext.canvas.clipPath(flaskPath)

                // Fill liquid (65% volume)
                val liquidTopY = baseY - baseH * 0.55f
                val liquidPath = Path().apply {
                    moveTo(centerX - baseW * 0.4f, liquidTopY)
                    lineTo(centerX + baseW * 0.4f, liquidTopY)
                    lineTo(centerX + baseW / 2f, baseY)
                    lineTo(centerX - baseW / 2f, baseY)
                    close()
                }
                drawPath(
                    path = liquidPath,
                    brush = Brush.linearGradient(
                        colors = listOf(liquidColor.copy(alpha = 0.95f), liquidColor.copy(alpha = 0.7f)),
                        start = Offset(centerX, baseY),
                        end = Offset(centerX, liquidTopY)
                    )
                )

                // Lab bubbles
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = width * 0.04f,
                    center = Offset(centerX - baseW * 0.15f, baseY - baseH * bubbleOffset)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = width * 0.03f,
                    center = Offset(centerX + baseW * 0.2f, baseY - baseH * (bubbleOffset + 0.1f).coerceIn(0f, 1f))
                )

                drawContext.canvas.restore()

                // Outline beaker exterior
                drawPath(
                    path = flaskPath,
                    color = glassOutlineColor,
                    style = Stroke(width = 3.dp.toPx())
                )
                // Lip rim
                drawRoundRect(
                    color = capColor,
                    topLeft = Offset(centerX - neckW * 0.7f, neckY - height * 0.03f),
                    size = Size(neckW * 1.4f, height * 0.04f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
            "potion" -> {
                // Medieval Potion Vial (Starry magical glow)
                val neckW = width * 0.18f
                val neckH = height * 0.18f
                val neckY = height * 0.18f
                val sphereR = (width * 0.38f).coerceAtMost(height * 0.35f)
                val sphereCenter = Offset(centerX, height * 0.60f)

                // Liquid fill clipping bounds
                drawContext.canvas.save()
                val clipCircle = Path().apply {
                    addOval(Rect(sphereCenter, sphereR))
                }
                drawContext.canvas.clipPath(clipCircle)

                // Fill liquid bottom half
                val liquidHeight = height * 0.58f
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(liquidColor.copy(alpha = 0.95f), liquidColor.copy(alpha = 0.5f)),
                        center = sphereCenter,
                        radius = sphereR
                    ),
                    topLeft = Offset(centerX - sphereR, liquidHeight),
                    size = Size(sphereR * 2, sphereR * 2)
                )

                // Rising fantasy sparkles
                val sparklyY = height * (0.8f - 0.5f * bubbleOffset)
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = width * 0.03f,
                    center = Offset(centerX - sphereR * 0.25f, sparklyY)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = width * 0.035f,
                    center = Offset(centerX + sphereR * 0.35f, sparklyY + height * 0.05f)
                )

                drawContext.canvas.restore()

                // Glass container lineart
                drawCircle(
                    color = glassOutlineColor,
                    radius = sphereR,
                    center = sphereCenter,
                    style = Stroke(width = 3.dp.toPx())
                )
                // Draw neck
                drawRect(
                    color = glassOutlineColor,
                    topLeft = Offset(centerX - neckW / 2f, neckY),
                    size = Size(neckW, neckH),
                    style = Stroke(width = 3.dp.toPx())
                )
                // Draw brown retro cork stopper
                drawRoundRect(
                    color = Color(0xFF8D6E63),
                    topLeft = Offset(centerX - neckW * 0.45f, neckY - height * 0.06f),
                    size = Size(neckW * 0.9f, height * 0.07f),
                    cornerRadius = CornerRadius(5f, 5f)
                )
            }
            "potion_cork" -> {
                // Oval glass vials with cork stoppers (Rustic cork format)
                val bodyW = width * 0.58f
                val bodyH = height * 0.65f
                val bodyY = height * 0.26f
                val neckW = width * 0.20f
                val neckY = height * 0.12f
                val neckH = height * 0.14f

                val bottlePath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(Offset(centerX - bodyW / 2f, bodyY), Size(bodyW, bodyH)),
                            topLeft = CornerRadius(width * 0.12f, width * 0.12f),
                            topRight = CornerRadius(width * 0.12f, width * 0.12f),
                            bottomLeft = CornerRadius(width * 0.06f, width * 0.06f),
                            bottomRight = CornerRadius(width * 0.06f, width * 0.06f)
                        )
                    )
                }

                drawContext.canvas.save()
                drawContext.canvas.clipPath(bottlePath)

                // Fill liquid 75%
                val liquidTopY = bodyY + bodyH * 0.28f
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(liquidColor.copy(alpha = 0.95f), liquidColor.copy(alpha = 0.6f)),
                        start = Offset(centerX, bodyY + bodyH),
                        end = Offset(centerX, liquidTopY)
                    ),
                    topLeft = Offset(centerX - bodyW / 2f, liquidTopY),
                    size = Size(bodyW, bodyY + bodyH - liquidTopY)
                )

                // Potion cork bubbles
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = width * 0.03f,
                    center = Offset(centerX - bodyW * 0.15f, bodyY + bodyH * 0.9f - bodyH * 0.6f * bubbleOffset)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = width * 0.025f,
                    center = Offset(centerX + bodyW * 0.2f, bodyY + bodyH * 0.95f - bodyH * 0.55f * bubbleOffset)
                )

                drawContext.canvas.restore()

                // Outline Glass
                drawPath(
                    path = bottlePath,
                    color = glassOutlineColor,
                    style = Stroke(width = 3.dp.toPx())
                )
                // Neck outline
                drawRect(
                    color = glassOutlineColor,
                    topLeft = Offset(centerX - neckW / 2f, neckY),
                    size = Size(neckW, neckH),
                    style = Stroke(width = 3.dp.toPx())
                )
                // Brown cork stopper
                drawRect(
                    color = Color(0xFF6D4C41),
                    topLeft = Offset(centerX - neckW * 0.4f, neckY - height * 0.04f),
                    size = Size(neckW * 0.8f, height * 0.06f)
                )
            }
            "neon" -> {
                // Futuristic Glowing Cyber Neon Capsule
                val capsuleW = width * 0.52f
                val capsuleH = height * 0.76f
                val capsuleY = height * 0.11f

                val capsulePath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(Offset(centerX - capsuleW / 2f, capsuleY), Size(capsuleW, capsuleH)),
                            cornerRadius = CornerRadius(capsuleW / 2f, capsuleW / 2f)
                        )
                    )
                }

                drawContext.canvas.save()
                drawContext.canvas.clipPath(capsulePath)

                // Solid intense digital center
                val lTopY = capsuleY + capsuleH * 0.3f
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(liquidColor.copy(alpha = 0.95f), liquidColor.copy(alpha = 0.8f)),
                        startY = capsuleY + capsuleH,
                        endY = lTopY
                    ),
                    topLeft = Offset(centerX - capsuleW / 2f, lTopY),
                    size = Size(capsuleW, capsuleY + capsuleH - lTopY)
                )

                // Neon bubbles
                drawCircle(
                    color = Color.White.copy(alpha = 0.65f),
                    radius = width * 0.025f,
                    center = Offset(centerX - capsuleW * 0.2f, capsuleY + capsuleH * 0.85f - capsuleH * 0.5f * bubbleOffset)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.65f),
                    radius = width * 0.018f,
                    center = Offset(centerX + capsuleW * 0.18f, capsuleY + capsuleH * 0.9f - capsuleH * 0.45f * bubbleOffset)
                )

                drawContext.canvas.restore()

                // Neon highly intense outer glow
                drawPath(
                    path = capsulePath,
                    color = liquidColor,
                    style = Stroke(width = 4.dp.toPx())
                )
                drawPath(
                    path = capsulePath,
                    color = Color.White.copy(alpha = 0.7f),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            "crystals" -> {
                // Crystal Shard (Hexagonal polygonal look)
                val crystalW = width * 0.54f
                val crystalH = height * 0.70f
                val crystalY = height * 0.17f
                
                val crystalPath = Path().apply {
                    moveTo(centerX, crystalY)
                    lineTo(centerX + crystalW / 2f, crystalY + crystalH * 0.25f)
                    lineTo(centerX + crystalW / 2f, crystalY + crystalH * 0.75f)
                    lineTo(centerX, crystalY + crystalH)
                    lineTo(centerX - crystalW / 2f, crystalY + crystalH * 0.75f)
                    lineTo(centerX - crystalW / 2f, crystalY + crystalH * 0.25f)
                    close()
                }

                drawContext.canvas.save()
                drawContext.canvas.clipPath(crystalPath)

                // Fill liquid 70%
                val liquidY = crystalY + crystalH * 0.35f
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(liquidColor.copy(alpha = 0.95f), liquidColor.copy(alpha = 0.65f)),
                        startY = crystalY + crystalH,
                        endY = liquidY
                    ),
                    topLeft = Offset(centerX - crystalW / 2f, liquidY),
                    size = Size(crystalW, crystalY + crystalH - liquidY)
                )
                
                // Crystal bubbles
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = width * 0.025f,
                    center = Offset(centerX - crystalW * 0.18f, crystalY + crystalH * 0.85f - crystalH * 0.5f * bubbleOffset)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = width * 0.02f,
                    center = Offset(centerX + crystalW * 0.15f, crystalY + crystalH * 0.9f - crystalH * 0.45f * bubbleOffset)
                )
                
                // Crystal facets
                drawLine(
                    color = Color.White.copy(alpha = 0.3f),
                    start = Offset(centerX, crystalY),
                    end = Offset(centerX, crystalY + crystalH),
                    strokeWidth = 2.dp.toPx()
                )

                drawContext.canvas.restore()

                // Outline Exterior
                drawPath(
                    path = crystalPath,
                    color = glassOutlineColor,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
            "royal" -> {
                // Royal Golden Amphora (Ancient Greek vibe)
                val neckW = width * 0.20f
                val neckY = height * 0.16f
                val neckH = height * 0.18f
                
                val bodyW = width * 0.62f
                val bodyH = height * 0.54f
                val bodyY = height * 0.34f
                
                val amphoraPath = Path().apply {
                    moveTo(centerX - neckW / 2f, neckY)
                    lineTo(centerX + neckW / 2f, neckY)
                    lineTo(centerX + neckW * 0.4f, bodyY)
                    quadraticTo(centerX + bodyW / 2f, bodyY, centerX + bodyW / 2f, bodyY + bodyH * 0.4f)
                    quadraticTo(centerX + bodyW / 2f, bodyY + bodyH, centerX, bodyY + bodyH)
                    quadraticTo(centerX - bodyW / 2f, bodyY + bodyH, centerX - bodyW / 2f, bodyY + bodyH * 0.4f)
                    quadraticTo(centerX - bodyW / 2f, bodyY, centerX - neckW * 0.4f, bodyY)
                    close()
                }

                drawContext.canvas.save()
                drawContext.canvas.clipPath(amphoraPath)

                // Fill liquid 70%
                val liquidTop = bodyY + bodyH * 0.3f
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(liquidColor.copy(alpha = 0.95f), liquidColor.copy(alpha = 0.6f)),
                        startY = bodyY + bodyH,
                        endY = liquidTop
                    ),
                    topLeft = Offset(centerX - bodyW / 2f, liquidTop),
                    size = Size(bodyW, bodyY + bodyH - liquidTop)
                )

                // Royal bubbles
                drawCircle(
                    color = Color.White.copy(alpha = 0.7f),
                    radius = width * 0.03f,
                    center = Offset(centerX - bodyW * 0.15f, bodyY + bodyH * 0.85f - bodyH * 0.5f * bubbleOffset)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.7f),
                    radius = width * 0.02f,
                    center = Offset(centerX + bodyW * 0.18f, bodyY + bodyH * 0.9f - bodyH * 0.45f * bubbleOffset)
                )

                drawContext.canvas.restore()

                // Outline Exterior (In elegant golden color!)
                drawPath(
                    path = amphoraPath,
                    color = Color(0xFFFFD700), // Pure Gold
                    style = Stroke(width = 3.5.dp.toPx())
                )

                // Golden neck handles on sides
                drawArc(
                    color = Color(0xFFFFD700),
                    startAngle = 120f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(centerX - bodyW * 0.52f, bodyY - height * 0.05f),
                    size = Size(bodyW * 0.3f, height * 0.2f),
                    style = Stroke(width = 2.5.dp.toPx())
                )
                drawArc(
                    color = Color(0xFFFFD700),
                    startAngle = 240f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(centerX + bodyW * 0.22f, bodyY - height * 0.05f),
                    size = Size(bodyW * 0.3f, height * 0.2f),
                    style = Stroke(width = 2.5.dp.toPx())
                )
            }
            "test_tube" -> {
                // Straight Test Tube with rounded bottom (Tubo de Ensaio)
                val tubeW = width * 0.44f
                val tubeH = height * 0.76f
                val tubeY = height * 0.12f
                val radius = tubeW / 2f
                val rectBottomY = tubeY + tubeH - radius

                val tubePath = Path().apply {
                    moveTo(centerX - radius, tubeY)
                    lineTo(centerX + radius, tubeY)
                    lineTo(centerX + radius, rectBottomY)
                    // Semi-circle curved bottom
                    arcTo(
                        rect = Rect(Offset(centerX - radius, rectBottomY - radius), Size(tubeW, tubeW)),
                        startAngleDegrees = 0f,
                        sweepAngleDegrees = 180f,
                        forceMoveTo = false
                    )
                    lineTo(centerX - radius, tubeY)
                    close()
                }

                drawContext.canvas.save()
                drawContext.canvas.clipPath(tubePath)

                // Fill liquid 65% height
                val liquidTopY = tubeY + tubeH * 0.35f
                val liquidPath = Path().apply {
                    moveTo(centerX - radius, liquidTopY)
                    lineTo(centerX + radius, liquidTopY)
                    lineTo(centerX + radius, rectBottomY)
                    arcTo(
                        rect = Rect(Offset(centerX - radius, rectBottomY - radius), Size(tubeW, tubeW)),
                        startAngleDegrees = 0f,
                        sweepAngleDegrees = 180f,
                        forceMoveTo = false
                    )
                    lineTo(centerX - radius, liquidTopY)
                    close()
                }
                drawPath(
                    path = liquidPath,
                    brush = Brush.linearGradient(
                        colors = listOf(liquidColor.copy(alpha = 0.95f), liquidColor.copy(alpha = 0.65f)),
                        start = Offset(centerX, tubeY + tubeH),
                        end = Offset(centerX, liquidTopY)
                    )
                )

                // Lab bubbles rising up standard anim
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f),
                    radius = width * 0.03f,
                    center = Offset(centerX - tubeW * 0.15f, tubeY + tubeH * 0.8f - tubeH * 0.5f * bubbleOffset)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f),
                    radius = width * 0.025f,
                    center = Offset(centerX + tubeW * 0.2f, tubeY + tubeH * 0.9f - tubeH * 0.45f * bubbleOffset)
                )

                drawContext.canvas.restore()

                // Outline tube glass
                drawPath(
                    path = tubePath,
                    color = glassOutlineColor,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Lip rim at the top of test tube
                drawRoundRect(
                    color = capColor,
                    topLeft = Offset(centerX - radius * 1.15f, tubeY - height * 0.015f),
                    size = Size(tubeW * 1.15f, height * 0.03f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                
                // Subtle shine line
                val glowPath = Path().apply {
                    moveTo(centerX + radius * 0.6f, tubeY + tubeH * 0.15f)
                    lineTo(centerX + radius * 0.6f, tubeY + tubeH * 0.7f)
                }
                drawPath(
                    glowPath,
                    color = glassShineColor,
                    style = Stroke(width = width * 0.04f, cap = StrokeCap.Round)
                )
            }
            "hourglass" -> {
                // Alchemical Hourglass (Estreitamento no meio e líquido na base)
                val bodyW = width * 0.55f
                val bodyH = height * 0.72f
                val bodyY = height * 0.15f
                val waistW = width * 0.18f
                
                val hourglassPath = Path().apply {
                    moveTo(centerX - bodyW * 0.4f, bodyY)
                    lineTo(centerX + bodyW * 0.4f, bodyY)
                    quadraticTo(centerX + bodyW * 0.4f, bodyY + bodyH * 0.25f, centerX + waistW / 2f, bodyY + bodyH * 0.5f)
                    quadraticTo(centerX + bodyW * 0.45f, bodyY + bodyH * 0.75f, centerX + bodyW * 0.45f, bodyY + bodyH)
                    lineTo(centerX - bodyW * 0.45f, bodyY + bodyH)
                    quadraticTo(centerX - bodyW * 0.45f, bodyY + bodyH * 0.75f, centerX - waistW / 2f, bodyY + bodyH * 0.5f)
                    quadraticTo(centerX - bodyW * 0.4f, bodyY + bodyH * 0.25f, centerX - bodyW * 0.4f, bodyY)
                    close()
                }

                drawContext.canvas.save()
                drawContext.canvas.clipPath(hourglassPath)

                // Fill liquid (bottom bulb - 48% height)
                val liquidTopY = bodyY + bodyH * 0.52f
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(liquidColor.copy(alpha = 0.95f), liquidColor.copy(alpha = 0.65f)),
                        start = Offset(centerX, bodyY + bodyH),
                        end = Offset(centerX, liquidTopY)
                    ),
                    topLeft = Offset(centerX - bodyW * 0.5f, liquidTopY),
                    size = Size(bodyW * 1f, bodyY + bodyH - liquidTopY)
                )

                // Hourglass bubbles rising up through the waist
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = width * 0.025f,
                    center = Offset(centerX - waistW * 0.15f, bodyY + bodyH * 0.85f - bodyH * 0.55f * bubbleOffset)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = width * 0.02f,
                    center = Offset(centerX + waistW * 0.2f, bodyY + bodyH * 0.9f - bodyH * 0.5f * bubbleOffset)
                )

                drawContext.canvas.restore()

                // Glass outline
                drawPath(
                    path = hourglassPath,
                    color = glassOutlineColor,
                    style = Stroke(width = 3.dp.toPx())
                )
                // Rim top & bottom cork stoppers
                drawRoundRect(
                    color = capColor,
                    topLeft = Offset(centerX - bodyW * 0.45f, bodyY - height * 0.02f),
                    size = Size(bodyW * 0.9f, height * 0.03f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                drawRoundRect(
                    color = capColor,
                    topLeft = Offset(centerX - bodyW * 0.5f, bodyY + bodyH - height * 0.01f),
                    size = Size(bodyW * 1f, height * 0.03f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
            "diamond_flask" -> {
                // Faceted Diamond Flask (Sharp geometric crystal style)
                val diaW = width * 0.62f
                val diaH = height * 0.68f
                val diaY = height * 0.18f
                val neckW = width * 0.18f
                val neckH = height * 0.12f
                val neckY = height * 0.06f

                val diamondPath = Path().apply {
                    moveTo(centerX - neckW / 2f, diaY)
                    lineTo(centerX + neckW / 2f, diaY)
                    lineTo(centerX + diaW / 2f, diaY + diaH * 0.35f)
                    lineTo(centerX, diaY + diaH)
                    lineTo(centerX - diaW / 2f, diaY + diaH * 0.35f)
                    close()
                }

                drawContext.canvas.save()
                drawContext.canvas.clipPath(diamondPath)

                // Fill liquid 60%
                val liquidTopY = diaY + diaH * 0.4f
                val liquidPath = Path().apply {
                    moveTo(centerX - diaW * 0.45f, liquidTopY)
                    lineTo(centerX + diaW * 0.45f, liquidTopY)
                    lineTo(centerX, diaY + diaH)
                    close()
                }
                drawPath(
                    path = liquidPath,
                    brush = Brush.linearGradient(
                        colors = listOf(liquidColor.copy(alpha = 0.95f), liquidColor.copy(alpha = 0.6f)),
                        start = Offset(centerX, diaY + diaH),
                        end = Offset(centerX, liquidTopY)
                    )
                )

                // Diamond bubbles
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = width * 0.025f,
                    center = Offset(centerX - diaW * 0.15f, diaY + diaH * 0.85f - diaH * 0.55f * bubbleOffset)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = width * 0.02f,
                    center = Offset(centerX + diaW * 0.15f, diaY + diaH * 0.9f - diaH * 0.5f * bubbleOffset)
                )

                // Facet inner shine lines
                drawLine(glassShineColor, Offset(centerX, diaY), Offset(centerX, diaY + diaH), 1.5.dp.toPx())
                drawLine(glassShineColor, Offset(centerX - diaW / 2f, diaY + diaH * 0.35f), Offset(centerX, diaY + diaH * 0.35f), 1.dp.toPx())
                drawLine(glassShineColor, Offset(centerX + diaW / 2f, diaY + diaH * 0.35f), Offset(centerX, diaY + diaH * 0.35f), 1.dp.toPx())

                drawContext.canvas.restore()

                // Outline glass
                drawPath(
                    path = diamondPath,
                    color = glassOutlineColor,
                    style = Stroke(width = 3.dp.toPx())
                )
                // Draw neck
                drawRect(
                    color = glassOutlineColor,
                    topLeft = Offset(centerX - neckW / 2f, neckY),
                    size = Size(neckW, neckH),
                    style = Stroke(width = 3.dp.toPx())
                )
                // Cap cork stopper
                drawRoundRect(
                    color = Color(0xFF795548),
                    topLeft = Offset(centerX - neckW * 0.45f, neckY - height * 0.02f),
                    size = Size(neckW * 0.9f, height * 0.04f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
            "skull" -> {
                // Alchemical Skull Flask (Macabre Alchemist theme)
                val headW = width * 0.58f
                val headH = height * 0.50f
                val headY = height * 0.22f
                val jawW = width * 0.32f
                val jawH = height * 0.18f
                val jawY = headY + headH * 0.85f
                val neckW = width * 0.16f
                val neckH = height * 0.12f
                val neckY = height * 0.10f

                val skullPath = Path().apply {
                    // Top round cranium head
                    arcTo(
                        rect = Rect(Offset(centerX - headW / 2f, headY), Size(headW, headH)),
                        startAngleDegrees = 160f,
                        sweepAngleDegrees = 220f,
                        forceMoveTo = true
                    )
                    // Down to jaw
                    lineTo(centerX + jawW / 2f, jawY)
                    lineTo(centerX + jawW / 2f, jawY + jawH)
                    // Teeth base
                    lineTo(centerX - jawW / 2f, jawY + jawH)
                    lineTo(centerX - jawW / 2f, jawY)
                    close()
                }

                drawContext.canvas.save()
                drawContext.canvas.clipPath(skullPath)

                // Fill liquid 65%
                val liquidTopY = headY + headH * 0.35f
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(liquidColor.copy(alpha = 0.95f), liquidColor.copy(alpha = 0.65f)),
                        startY = jawY + jawH,
                        endY = liquidTopY
                    ),
                    topLeft = Offset(centerX - headW / 2f, liquidTopY),
                    size = Size(headW, jawY + jawH - liquidTopY)
                )

                // Skull bubbles rising from eyes
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = width * 0.025f,
                    center = Offset(centerX - headW * 0.16f, jawY + jawH * 0.5f - headH * 0.55f * bubbleOffset)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = width * 0.02f,
                    center = Offset(centerX + headW * 0.16f, jawY + jawH * 0.6f - headH * 0.5f * bubbleOffset)
                )

                // Dark skull eyes (holes)
                drawCircle(Color.Black.copy(alpha = 0.6f), headW * 0.12f, Offset(centerX - headW * 0.18f, headY + headH * 0.55f))
                drawCircle(Color.Black.copy(alpha = 0.6f), headW * 0.12f, Offset(centerX + headW * 0.18f, headY + headH * 0.55f))

                // Nose cavity
                val nosePath = Path().apply {
                    moveTo(centerX, headY + headH * 0.68f)
                    lineTo(centerX + headW * 0.04f, headY + headH * 0.76f)
                    lineTo(centerX - headW * 0.04f, headY + headH * 0.76f)
                    close()
                }
                drawPath(nosePath, Color.Black.copy(alpha = 0.6f))

                // Teeth lines
                drawLine(glassOutlineColor, Offset(centerX - jawW * 0.3f, jawY + jawH * 0.5f), Offset(centerX + jawW * 0.3f, jawY + jawH * 0.5f), 1.5.dp.toPx())
                drawLine(glassOutlineColor, Offset(centerX, jawY + jawH * 0.2f), Offset(centerX, jawY + jawH * 0.8f), 1.5.dp.toPx())
                drawLine(glassOutlineColor, Offset(centerX - jawW * 0.2f, jawY + jawH * 0.2f), Offset(centerX - jawW * 0.2f, jawY + jawH * 0.8f), 1.dp.toPx())
                drawLine(glassOutlineColor, Offset(centerX + jawW * 0.2f, jawY + jawH * 0.2f), Offset(centerX + jawW * 0.2f, jawY + jawH * 0.8f), 1.dp.toPx())

                drawContext.canvas.restore()

                // Outline skull glass
                drawPath(
                    path = skullPath,
                    color = glassOutlineColor,
                    style = Stroke(width = 3.dp.toPx())
                )
                // Neck opening outline
                drawRect(
                    color = glassOutlineColor,
                    topLeft = Offset(centerX - neckW / 2f, neckY),
                    size = Size(neckW, neckH),
                    style = Stroke(width = 3.dp.toPx())
                )
                // Cap rim
                drawRoundRect(
                    color = capColor,
                    topLeft = Offset(centerX - neckW * 0.65f, neckY - height * 0.015f),
                    size = Size(neckW * 1.3f, height * 0.03f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
            "heart_vial" -> {
                // Healing Heart Potion (Love/Life magic vial)
                val bodyW = width * 0.64f
                val bodyH = height * 0.64f
                val bodyY = height * 0.26f
                val neckW = width * 0.16f
                val neckH = height * 0.16f
                val neckY = height * 0.10f

                // Precise smooth heart shape path
                val heartPath = Path().apply {
                    moveTo(centerX, bodyY + bodyH * 0.25f)
                    // Left curve
                    cubicTo(
                        centerX - bodyW * 0.35f, bodyY - bodyH * 0.1f,
                        centerX - bodyW * 0.62f, bodyY + bodyH * 0.3f,
                        centerX, bodyY + bodyH
                    )
                    // Right curve
                    cubicTo(
                        centerX + bodyW * 0.62f, bodyY + bodyH * 0.3f,
                        centerX + bodyW * 0.35f, bodyY - bodyH * 0.1f,
                        centerX, bodyY + bodyH * 0.25f
                    )
                    close()
                }

                drawContext.canvas.save()
                drawContext.canvas.clipPath(heartPath)

                // Fill liquid bottom 70%
                val liquidTopY = bodyY + bodyH * 0.22f
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(liquidColor.copy(alpha = 0.95f), liquidColor.copy(alpha = 0.55f)),
                        center = Offset(centerX, bodyY + bodyH * 0.6f),
                        radius = bodyW * 0.5f
                    ),
                    topLeft = Offset(centerX - bodyW / 2f, liquidTopY),
                    size = Size(bodyW, bodyY + bodyH - liquidTopY)
                )

                // Heart love bubbles (small hearts or round bubbles)
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = width * 0.03f,
                    center = Offset(centerX - bodyW * 0.16f, bodyY + bodyH * 0.8f - bodyH * 0.5f * bubbleOffset)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = width * 0.025f,
                    center = Offset(centerX + bodyW * 0.2f, bodyY + bodyH * 0.85f - bodyH * 0.45f * bubbleOffset)
                )

                drawContext.canvas.restore()

                // Outline glass
                drawPath(
                    path = heartPath,
                    color = glassOutlineColor,
                    style = Stroke(width = 3.dp.toPx())
                )
                // Draw neck
                drawRect(
                    color = glassOutlineColor,
                    topLeft = Offset(centerX - neckW / 2f, neckY),
                    size = Size(neckW, neckH),
                    style = Stroke(width = 3.dp.toPx())
                )
                // Gold collar ring
                drawRect(
                    color = Color(0xFFFFD700),
                    topLeft = Offset(centerX - neckW * 0.55f, neckY + neckH - height * 0.03f),
                    size = Size(neckW * 1.1f, height * 0.04f)
                )
                // Stopper
                drawRoundRect(
                    color = Color(0xFF6D4C41),
                    topLeft = Offset(centerX - neckW * 0.4f, neckY - height * 0.03f),
                    size = Size(neckW * 0.8f, height * 0.05f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
            "cauldron_flask" -> {
                // Alchemical Cauldron (Chubby wide pot with handles)
                val bodyW = width * 0.66f
                val bodyH = height * 0.52f
                val bodyY = height * 0.36f
                val neckW = width * 0.38f
                val neckH = height * 0.12f
                val neckY = height * 0.24f

                val cauldronPath = Path().apply {
                    moveTo(centerX - neckW * 0.45f, neckY)
                    lineTo(centerX + neckW * 0.45f, neckY)
                    lineTo(centerX + neckW * 0.42f, bodyY)
                    // wide rounded belly
                    quadraticTo(centerX + bodyW / 2f, bodyY + bodyH * 0.1f, centerX + bodyW / 2f, bodyY + bodyH * 0.5f)
                    quadraticTo(centerX + bodyW / 2f, bodyY + bodyH, centerX + bodyW * 0.25f, bodyY + bodyH)
                    lineTo(centerX - bodyW * 0.25f, bodyY + bodyH)
                    quadraticTo(centerX - bodyW / 2f, bodyY + bodyH, centerX - bodyW / 2f, bodyY + bodyH * 0.5f)
                    quadraticTo(centerX - bodyW / 2f, bodyY + bodyH * 0.1f, centerX - neckW * 0.42f, bodyY)
                    close()
                }

                drawContext.canvas.save()
                drawContext.canvas.clipPath(cauldronPath)

                // Fill liquid 62%
                val liquidTopY = bodyY + bodyH * 0.24f
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(liquidColor.copy(alpha = 0.95f), liquidColor.copy(alpha = 0.6f)),
                        startY = bodyY + bodyH,
                        endY = liquidTopY
                    ),
                    topLeft = Offset(centerX - bodyW / 2f, liquidTopY),
                    size = Size(bodyW, bodyY + bodyH - liquidTopY)
                )

                // Cauldron bubbles
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = width * 0.035f,
                    center = Offset(centerX - bodyW * 0.2f, bodyY + bodyH * 0.8f - bodyH * 0.5f * bubbleOffset)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = width * 0.025f,
                    center = Offset(centerX + bodyW * 0.2f, bodyY + bodyH * 0.85f - bodyH * 0.45f * bubbleOffset)
                )

                drawContext.canvas.restore()

                // Outline glass (darker heavy bronze frame)
                drawPath(
                    path = cauldronPath,
                    color = if (isLight) Color(0xFF334155) else Color(0xFF475569),
                    style = Stroke(width = 3.5.dp.toPx())
                )
                // Collar neck lip
                drawRoundRect(
                    color = if (isLight) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                    topLeft = Offset(centerX - neckW * 0.55f, neckY - height * 0.02f),
                    size = Size(neckW * 1.1f, height * 0.04f),
                    cornerRadius = CornerRadius(5f, 5f)
                )
                // Left & Right circular handles
                drawArc(
                    color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.8f),
                    startAngle = 90f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(centerX - bodyW * 0.58f, bodyY + bodyH * 0.2f),
                    size = Size(bodyW * 0.2f, bodyW * 0.2f),
                    style = Stroke(width = 2.5.dp.toPx())
                )
                drawArc(
                    color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.8f),
                    startAngle = 270f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(centerX + bodyW * 0.38f, bodyY + bodyH * 0.2f),
                    size = Size(bodyW * 0.2f, bodyW * 0.2f),
                    style = Stroke(width = 2.5.dp.toPx())
                )
            }
            "prism_flask" -> {
                // Prism/Triangle Alchemy Shard (Sharp futuristic glassware)
                val bodyW = width * 0.66f
                val bodyH = height * 0.65f
                val bodyY = height * 0.22f
                val neckW = width * 0.16f
                val neckY = height * 0.10f
                val neckH = height * 0.12f

                val prismPath = Path().apply {
                    moveTo(centerX - neckW / 2f, bodyY)
                    lineTo(centerX + neckW / 2f, bodyY)
                    lineTo(centerX + bodyW / 2f, bodyY + bodyH)
                    lineTo(centerX - bodyW / 2f, bodyY + bodyH)
                    close()
                }

                drawContext.canvas.save()
                drawContext.canvas.clipPath(prismPath)

                // Fill liquid 60%
                val liquidTopY = bodyY + bodyH * 0.4f
                val liquidPath = Path().apply {
                    moveTo(centerX - bodyW * 0.3f, liquidTopY)
                    lineTo(centerX + bodyW * 0.3f, liquidTopY)
                    lineTo(centerX + bodyW / 2f, bodyY + bodyH)
                    lineTo(centerX - bodyW / 2f, bodyY + bodyH)
                    close()
                }
                drawPath(
                    path = liquidPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(liquidColor.copy(alpha = 0.95f), liquidColor.copy(alpha = 0.65f)),
                        startY = bodyY + bodyH,
                        endY = liquidTopY
                    )
                )

                // Prism bubbles rising up
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = width * 0.025f,
                    center = Offset(centerX - bodyW * 0.15f, bodyY + bodyH * 0.85f - bodyH * 0.55f * bubbleOffset)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = width * 0.02f,
                    center = Offset(centerX + bodyW * 0.15f, bodyY + bodyH * 0.9f - bodyH * 0.5f * bubbleOffset)
                )

                // Subtle inner prism refraction line
                drawLine(
                    color = Color.White.copy(alpha = 0.25f),
                    start = Offset(centerX, bodyY),
                    end = Offset(centerX, bodyY + bodyH),
                    strokeWidth = 1.5.dp.toPx()
                )

                drawContext.canvas.restore()

                // Outline glass
                drawPath(
                    path = prismPath,
                    color = glassOutlineColor,
                    style = Stroke(width = 3.dp.toPx())
                )
                // Draw neck
                drawRect(
                    color = glassOutlineColor,
                    topLeft = Offset(centerX - neckW / 2f, neckY),
                    size = Size(neckW, neckH),
                    style = Stroke(width = 3.dp.toPx())
                )
                // Flat cap rim
                drawRoundRect(
                    color = capColor,
                    topLeft = Offset(centerX - neckW * 0.65f, neckY - height * 0.01f),
                    size = Size(neckW * 1.3f, height * 0.025f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
            }
            else -> {
                // "classic" Original Glass Bottle Shape
                val neckW = width * 0.18f
                val neckH = height * 0.18f
                val neckY = height * 0.16f
                val bodyW = width * 0.55f
                val bodyH = height * 0.58f
                val bodyY = height * 0.32f

                val outerPath = Path().apply {
                    moveTo(centerX - neckW / 2f, neckY)
                    lineTo(centerX + neckW / 2f, neckY)
                    lineTo(centerX + neckW / 2f, bodyY)
                    // glass shoulder curves
                    quadraticTo(centerX + bodyW * 0.45f, bodyY, centerX + bodyW / 2f, bodyY + height * 0.08f)
                    lineTo(centerX + bodyW / 2f, bodyY + bodyH)
                    lineTo(centerX - bodyW / 2f, bodyY + bodyH)
                    lineTo(centerX - bodyW / 2f, bodyY + height * 0.08f)
                    quadraticTo(centerX - bodyW * 0.45f, bodyY, centerX - neckW / 2f, bodyY)
                    close()
                }

                // Clip liquid content
                drawContext.canvas.save()
                drawContext.canvas.clipPath(outerPath)

                // Draw solid liquid base filled from bottom representing 72% liquid level
                val liquidTopY = bodyY + bodyH * 0.28f
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(liquidColor.copy(alpha = 0.95f), liquidColor.copy(alpha = 0.6f)),
                        start = Offset(centerX, bodyY + bodyH),
                        end = Offset(centerX, liquidTopY)
                    ),
                    topLeft = Offset(centerX - bodyW / 2f, liquidTopY),
                    size = Size(bodyW, bodyY + bodyH - liquidTopY)
                )

                // Classic bubbles
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f),
                    radius = width * 0.03f,
                    center = Offset(centerX - bodyW * 0.15f, bodyY + bodyH * 0.9f - bodyH * 0.6f * bubbleOffset)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f),
                    radius = width * 0.02f,
                    center = Offset(centerX + bodyW * 0.2f, bodyY + bodyH * 0.95f - bodyH * 0.55f * bubbleOffset)
                )

                // White glossy shine line
                val glowPath = Path().apply {
                    moveTo(centerX + bodyW * 0.35f, bodyY + bodyH * 0.1f)
                    quadraticTo(centerX + bodyW * 0.35f, bodyY + bodyH * 0.8f, centerX + bodyW * 0.35f, bodyY + bodyH * 0.85f)
                }
                drawPath(
                    glowPath,
                    color = glassShineColor,
                    style = Stroke(width = width * 0.06f, cap = StrokeCap.Round)
                )

                drawContext.canvas.restore()

                // Outline glass contour
                drawPath(
                    path = outerPath,
                    color = glassOutlineColor,
                    style = Stroke(width = 3.dp.toPx())
                )
                // Cap stopper details
                drawRoundRect(
                    color = capColor,
                    topLeft = Offset(centerX - neckW * 0.65f, neckY - height * 0.03f),
                    size = Size(neckW * 1.3f, height * 0.04f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
        }

        // Draw hint target color bubble if active and provided
        if (isHintFlag && hintTargetColor != null) {
            val bubbleCenter = Offset(centerX, height * 0.07f)
            val bubbleRadius = width * 0.12f
            
            // Halo de brilho da bolha
            drawCircle(
                color = hintTargetColor.copy(alpha = 0.3f * pulseScale),
                radius = bubbleRadius * 1.4f,
                center = bubbleCenter
            )
            // Bolha colorida
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, hintTargetColor, hintTargetColor.copy(alpha = 0.8f)),
                    center = Offset(bubbleCenter.x - bubbleRadius * 0.2f, bubbleCenter.y - bubbleRadius * 0.2f),
                    radius = bubbleRadius
                ),
                radius = bubbleRadius,
                center = bubbleCenter
            )
            // Borda branca brilhante
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = bubbleRadius,
                center = bubbleCenter,
                style = Stroke(width = 2.dp.toPx())
            )
            // Brilho místico
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = bubbleRadius * 0.3f,
                center = Offset(bubbleCenter.x - bubbleRadius * 0.3f, bubbleCenter.y - bubbleRadius * 0.3f)
            )
        }
    }
}
