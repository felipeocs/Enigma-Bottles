package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.*
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashView(
    viewModel: GameViewModel,
    onEnterGame: () -> Unit
) {
    // Elegant pulsing scaling factor for the alchemy flask logo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Animated glow overlay alpha
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Automatically transition if not done already (though viewModel also has a timer fallback)
    LaunchedEffect(Unit) {
        delay(2500)
        onEnterGame()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Midnight Dark Navy
                        Color(0xFF1E1B4B), // Rich Deep Indigo
                        Color(0xFF311042)  // Soft Alchemist Crimson/Violet
                    )
                )
            )
            .testTag("splash_screen_root"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // 1. Icon Container with Double Glowing Ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .scale(scale)
            ) {
                // outer magical portal halo
                Box(
                    modifier = Modifier
                        .size(175.dp)
                        .clip(CircleShape)
                        .alpha(glowAlpha * 0.4f)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFFFD700), Color.Transparent)
                            )
                        )
                )

                // inner portal background
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Background Glow",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                )

                // floating foreground magic flask
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Enigma Bottles Potion Logo",
                    modifier = Modifier
                        .size(130.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Title & Brand Identity
            Text(
                text = "ENIGMA BOTTLES",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFFD700), // Sparkling Gold
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("splash_title")
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = TextRes.get("alquimia_sub", viewModel.currentLanguage),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                color = Color(0xFFA5B4FC), // Lavender Cyan
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 3. Custom magical minimalist dots loading bar
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dotScale1 by infiniteTransition.animateFloat(
                    initialValue = 0.5f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, delayMillis = 0, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot1"
                )
                val dotScale2 by infiniteTransition.animateFloat(
                    initialValue = 0.5f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, delayMillis = 200, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot2"
                )
                val dotScale3 by infiniteTransition.animateFloat(
                    initialValue = 0.5f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, delayMillis = 400, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot3"
                )

                Box(modifier = Modifier.size(8.dp).scale(dotScale1).clip(CircleShape).background(Color(0xFFE0E7FF)))
                Box(modifier = Modifier.size(8.dp).scale(dotScale2).clip(CircleShape).background(Color(0xFF818CF8)))
                Box(modifier = Modifier.size(8.dp).scale(dotScale3).clip(CircleShape).background(Color(0xFFAC5CE2)))
            }
        }

        // 4. Studio copyright text
        Text(
            text = "Enigma Studio Games • Premium App",
            fontSize = 11.sp,
            fontWeight = FontWeight.Light,
            color = Color.White.copy(alpha = 0.4f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            letterSpacing = 1.sp
        )
    }
}
