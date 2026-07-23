package com.enigmabottle.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enigmabottle.data.*
import com.enigmabottle.ui.components.BottleGlassware
import com.enigmabottle.ui.components.GameThemeBackground
import com.enigmabottle.ui.components.TutorialDialog
import com.enigmabottle.viewmodel.GameViewModel
import com.enigmabottle.viewmodel.Screen

import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

@Composable
fun GamePlayView(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val swapHistory by viewModel.swapHistoryList
    val haptic = LocalHapticFeedback.current

    var showConfirmPowerUpDialog by remember { mutableStateOf(false) }
    var pendingPowerUpToConfirm by remember { mutableStateOf<String?>(null) } // "hint", "reveal", "xray", "freeze"
    var showManualTutorial by remember { mutableStateOf(false) }

    // Calculate match info
    val activeBottlesCount = viewModel.boardSequence.size
    val currentMatches = viewModel.boardSequence.zip(viewModel.targetSequence).count { it.first == it.second }

    // Format time (MM:SS)
    val minutes = viewModel.elapsedTimeSeconds / 60
    val seconds = viewModel.elapsedTimeSeconds % 60
    val timerText = String.format("%02d:%02d", minutes, seconds)

    val isLight = profile.activeBgId == "sleek_interface" || profile.activeBgId.startsWith("clear_")

    GameThemeBackground(bgId = profile.activeBgId) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = 40.dp)
            ) {
            // Screen Top Header (Quit button, Score statistics, moves index)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Lado Esquerdo: Botão Voltar (Sair/Desistir)
                IconButton(
                    onClick = { 
                        viewModel.pauseGame()
                        viewModel.quitDialogVisible = true 
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (isLight) Color(0xFFF1F5F9) else Color.Black.copy(alpha = 0.5f),
                            CircleShape
                        )
                        .align(Alignment.CenterStart)
                        .testTag("exit_game_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Exit",
                        tint = if (isLight) Color(0xFF475569) else Color.White
                    )
                }

                // Centro: Título do nível e Timer centralizado abaixo
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (viewModel.isDailyChallenge) {
                            TextRes.get("daily_challenge", viewModel.currentLanguage)
                        } else {
                            TextRes.get("difficulty", viewModel.currentLanguage) + ": " +
                            TextRes.get(
                                when (viewModel.currentDifficulty) {
                                    "Fácil" -> "easy_short"
                                    "Médio" -> "medium_short"
                                    "Difícil" -> "hard_short"
                                    "Especialista" -> "expert_short"
                                    "Épico" -> "epic_short"
                                    else -> "master_short"
                                },
                                viewModel.currentLanguage
                            )
                        },
                        color = if (isLight) Color(0xFF1E293B) else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (viewModel.isDailyChallenge) {
                        Text(
                            text = viewModel.dailyDayKey,
                            color = Color(0xFFF97316),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Timer centralizado abaixo do título
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Time",
                            tint = if (viewModel.isFreezeActive) Color(0xFF3B82F6) else (if (isLight) Color(0xFFB45309) else Color.White),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = timerText,
                            color = if (viewModel.isFreezeActive) Color(0xFF3B82F6) else (if (isLight) Color(0xFFB45309) else Color.White),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Lado Direito: Botões Ajuda e Pause
                if (!viewModel.isGameOver) {
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { showManualTutorial = true },
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (isLight) Color(0xFFF1F5F9) else Color.Black.copy(alpha = 0.5f),
                                    CircleShape
                                )
                                .testTag("help_game_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Help,
                                contentDescription = "Ajuda",
                                tint = if (isLight) Color(0xFF475569) else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.pauseGame() },
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (isLight) Color(0xFFF1F5F9) else Color.Black.copy(alpha = 0.5f),
                                    CircleShape
                                )
                                .testTag("pause_game_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = "Pausar",
                                tint = if (isLight) Color(0xFF475569) else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Dynamic Scrollable Layout containing shelves and history logs
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Main stats overlay matching "Sleek Interface" Feedback UI layout and dividers
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isLight) Color(0xFFF1F5F9) else Color.White.copy(alpha = 0.1f)
                        ),
                        elevation = CardDefaults.cardElevation(if (isLight) 1.dp else 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp, horizontal = 24.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = TextRes.get("moves", viewModel.currentLanguage).uppercase(),
                                    color = if (isLight) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${viewModel.movesCount}",
                                    color = if (isLight) Color(0xFF1E293B) else Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp,
                                    modifier = Modifier.testTag("game_moves_count")
                                )
                            }
                            
                            // Sleek vertical line divider
                            Box(
                                modifier = Modifier
                                    .height(32.dp)
                                    .width(1.dp)
                                    .background(if (isLight) Color(0xFFF1F5F9) else Color.White.copy(alpha = 0.15f))
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = TextRes.get("status_label", viewModel.currentLanguage),
                                    color = if (isLight) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically, 
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "$currentMatches",
                                        color = if (currentMatches == activeBottlesCount) Color(0xFF10B981) else Color(0xFF4F46E5),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 24.sp,
                                        modifier = Modifier.testTag("correct_bottles_count")
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = TextRes.get("bottles_positioned", viewModel.currentLanguage),
                                        color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.7f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottles Shelves item
                item {
                    if (viewModel.isRevealActive) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                                .animateContentSize(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF59E0B).copy(alpha = 0.15f)),
                            border = BorderStroke(1.5.dp, Color(0xFFF59E0B)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = String.format(TextRes.get("reveal_active_banner", viewModel.currentLanguage), viewModel.revealTimeRemaining),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color(0xFFD97706)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { viewModel.revealTimeRemaining.toFloat() / 4f },
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = Color(0xFFF59E0B),
                                    trackColor = Color(0xFFF59E0B).copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Determine adaptive column bounds based on screen width (responsiveness)
                    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
                    val screenWidthDp = configuration.screenWidthDp
                    val maxColumnsPossible = ((screenWidthDp - 32) / 66).coerceAtLeast(3)
                    val columnsCount = maxColumnsPossible.coerceAtMost(activeBottlesCount)

                    val bottleIndices = viewModel.boardSequence.indices.toList()
                    val gridChunks = bottleIndices.chunked(columnsCount)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        gridChunks.forEach { rowIndices ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Row of bottles
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    rowIndices.forEach { index ->
                                        val colorName = if (viewModel.isRevealActive) {
                                            viewModel.targetSequence[index]
                                        } else {
                                            viewModel.boardSequence[index]
                                        }
                                        val color = viewModel.getColorHex(colorName)
                                        val isSelected = viewModel.selectedIndex == index
                                        val isHint = viewModel.activeHintIndex == index

                                        // Apply stunning, responsive bottle pop-up lift animation when selected
                                        val offsetY by animateDpAsState(
                                            targetValue = if (isSelected) (-14).dp else 0.dp,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            ),
                                            label = "selectedBottleOffset"
                                        )

                                        Box(
                                            modifier = Modifier
                                                .size(width = 54.dp, height = 90.dp)
                                                .offset(y = offsetY)
                                                .testTag("bottle_tap_$index")
                                                .clickable { 
                                                    if (profile.isVibrationEnabled) {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    }
                                                    viewModel.selectBottle(index)
                                                }
                                        ) {
                                             val targetColorName = viewModel.targetSequence.getOrNull(index)
                                             val targetColor = if (targetColorName != null) viewModel.getColorHex(targetColorName) else null
                                             BottleGlassware(
                                                 liquidColor = color,
                                                 skinId = profile.activeSkinId,
                                                 isSelected = isSelected,
                                                 isHintFlag = isHint,
                                                 isLight = isLight,
                                                 hintTargetColor = targetColor
                                             )
                                        }
                                    }
                                }

                                // Interactive shelf wood/blue border plank underneath the row
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.95f)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            when (profile.activeBgId) {
                                                "sleek_interface" -> Color(0xFFCBD5E1) // Slate 300
                                                "lab" -> Color(0xFF00ACC1).copy(alpha = 0.5f)
                                                "magic" -> Color(0xFF9C27B0).copy(alpha = 0.5f)
                                                "neon_grid" -> Color(0xFFFF007F).copy(alpha = 0.5f)
                                                else -> Color(0xFF8B5A2B) // wood shelf
                                            }
                                        )
                                )
                            }
                        }
                    }
                }

                // Simulated active X-Ray helper
                item {
                    AnimatedVisibility(
                        visible = viewModel.xRayFeedbackText != null,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        Surface(
                            modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 32.dp),
                            color = if (isLight) Color(0xFFEEF2FF) else Color.Black.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF4F46E5))
                        ) {
                            Text(
                                text = viewModel.xRayFeedbackText ?: "",
                                color = if (isLight) Color(0xFF3730A3) else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                            )
                        }
                    }

                    if (viewModel.isXRayActive) {
                        Row(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = Color(0xFF4F46E5),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = TextRes.get("xray_active_prompt", viewModel.currentLanguage),
                                color = Color(0xFF4F46E5),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // In-Game Store Powerups Action Area
                item {
                    Spacer(modifier = Modifier.height(26.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = TextRes.get("powerups", viewModel.currentLanguage),
                            color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PowerUpItemButton(
                            title = TextRes.get("hint_btn", viewModel.currentLanguage),
                            cost = 30,
                            icon = Icons.Default.Lightbulb,
                            color = Color(0xFF10B981),
                            isLight = isLight,
                            modifier = Modifier.weight(1f),
                            ownedCount = profile.hintCount,
                            language = viewModel.currentLanguage,
                            onClick = {
                                pendingPowerUpToConfirm = "hint"
                                showConfirmPowerUpDialog = true
                            }
                        )

                        PowerUpItemButton(
                            title = TextRes.get("reveal_btn", viewModel.currentLanguage),
                            cost = 50,
                            icon = Icons.Default.Visibility,
                            color = Color(0xFFF59E0B),
                            isLight = isLight,
                            modifier = Modifier.weight(1f),
                            ownedCount = profile.revealCount,
                            language = viewModel.currentLanguage,
                            onClick = {
                                pendingPowerUpToConfirm = "reveal"
                                showConfirmPowerUpDialog = true
                            }
                        )

                        PowerUpItemButton(
                            title = TextRes.get("xray_btn", viewModel.currentLanguage),
                            cost = 20,
                            icon = Icons.Default.OfflineBolt,
                            color = Color(0xFF4F46E5),
                            isLight = isLight,
                            modifier = Modifier.weight(1f),
                            ownedCount = profile.xRayCount,
                            language = viewModel.currentLanguage,
                            onClick = {
                                pendingPowerUpToConfirm = "xray"
                                showConfirmPowerUpDialog = true
                            }
                        )

                        PowerUpItemButton(
                            title = TextRes.get("freeze_btn", viewModel.currentLanguage),
                            cost = 15,
                            icon = Icons.Default.AcUnit,
                            color = Color(0xFF3B82F6),
                            isLight = isLight,
                            modifier = Modifier.weight(1f),
                            ownedCount = profile.freezeCount,
                            language = viewModel.currentLanguage,
                            onClick = {
                                pendingPowerUpToConfirm = "freeze"
                                showConfirmPowerUpDialog = true
                            }
                        )
                    }
                }

                // Moves log table header
                item {
                    Spacer(modifier = Modifier.height(28.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = TextRes.get("swap_history_title", viewModel.currentLanguage),
                            color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = String.format(TextRes.get("total_lbl", viewModel.currentLanguage), swapHistory.size),
                            color = if (isLight) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }

                    if (swapHistory.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isLight) Color.White else Color.White.copy(alpha = 0.05f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, if (isLight) Color(0xFFF1F5F9) else Color.Transparent)
                        ) {
                            Text(
                                text = TextRes.get("rules_desc", viewModel.currentLanguage),
                                color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Interactive guessing swap lists
                items(swapHistory) { log ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            1.dp, 
                            if (isLight) Color(0xFFF1F5F9) else Color.White.copy(alpha = 0.05f)
                        ),
                        elevation = CardDefaults.cardElevation(if (isLight) 0.5.dp else 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Swap",
                                    tint = Color(0xFF4F46E5),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = TextRes.get("swap_txt", viewModel.currentLanguage) + ": " + 
                                            TextRes.get("bottle_txt", viewModel.currentLanguage) + " ${log.swappedIndices.first + 1} ⇄ " + 
                                            TextRes.get("bottle_txt", viewModel.currentLanguage) + " ${log.swappedIndices.second + 1}",
                                    color = if (isLight) Color(0xFF1E293B) else Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Match answers count
                            Badge(
                                containerColor = if (log.correctCount == activeBottlesCount) Color(0xFF10B981) else Color(0xFFF59E0B),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = "${log.correctCount} / $activeBottlesCount",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- POWER-UP USE CONFIRMATION DIALOG ---
        if (showConfirmPowerUpDialog && pendingPowerUpToConfirm != null) {
            val powerUp = pendingPowerUpToConfirm!!
            val title = TextRes.get("confirm_powerup_title", viewModel.currentLanguage)
            val confirmText = TextRes.get("confirm_btn", viewModel.currentLanguage)
            val cancelText = TextRes.get("cancel_btn", viewModel.currentLanguage)
            
            val (descriptionKey, ownedCount, coinCost) = when (powerUp) {
                "hint" -> Triple("confirm_use_hint", profile.hintCount, 30)
                "reveal" -> Triple("confirm_use_reveal", profile.revealCount, 50)
                "xray" -> Triple("confirm_use_xray", profile.xRayCount, 20)
                else -> Triple("confirm_use_freeze", profile.freezeCount, 15)
            }
            
            val description = TextRes.get(descriptionKey, viewModel.currentLanguage)
            
            AlertDialog(
                onDismissRequest = { showConfirmPowerUpDialog = false },
                title = { Text(text = title, fontWeight = FontWeight.Bold, color = if (isLight) Color(0xFF1E293B) else Color.White) },
                text = {
                    Column {
                        Text(text = description, color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = TextRes.get("cost", viewModel.currentLanguage) + ": ",
                                fontWeight = FontWeight.Bold,
                                color = if (isLight) Color(0xFF475569) else Color.White
                            )
                            if (ownedCount > 0) {
                                Text(
                                    text = String.format(TextRes.get("from_inventory", viewModel.currentLanguage), ownedCount),
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = String.format(TextRes.get("buy_with_coins", viewModel.currentLanguage), coinCost),
                                    color = Color(0xFFF59E0B),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showConfirmPowerUpDialog = false
                            when (powerUp) {
                                "hint" -> viewModel.buyHintPowerUp()
                                "reveal" -> viewModel.buyRevealPowerUp()
                                "xray" -> viewModel.buyXRayPowerUp()
                                "freeze" -> viewModel.buyFreezePowerUp()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text(text = confirmText, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmPowerUpDialog = false }) {
                        Text(text = cancelText, color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.6f))
                    }
                },
                containerColor = if (isLight) Color.White else Color(0xFF1E1E24)
            )
        }

        if ((!profile.tutorialCompleted || showManualTutorial) && !viewModel.isGameOver) {
            TutorialDialog(
                currentLanguage = viewModel.currentLanguage,
                isLight = isLight,
                onComplete = {
                    if (!profile.tutorialCompleted) {
                        viewModel.completeTutorial()
                    }
                    showManualTutorial = false
                }
            )
        }

        // --- WINNER CELEBRATION GAME DIALOG ---
        if (viewModel.isGameOver && viewModel.isGameWon) {
            AlertDialog(
                onDismissRequest = { /* force action */ },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!profile.isAdFree && !viewModel.hasDoubledCoinsThisTurn) {
                            Button(
                                onClick = { viewModel.launchAdRewardFlow("double_victory_coins") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("double_coins_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = TextRes.get("double_coins_btn", viewModel.currentLanguage),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        val hasNextGame = if (viewModel.isDailyChallenge) {
                            viewModel.isNextDailyChallengeAvailable()
                        } else {
                            true
                        }

                        if (hasNextGame) {
                            Button(
                                onClick = {
                                    if (viewModel.isDailyChallenge) {
                                        viewModel.startNextDailyChallenge()
                                    } else {
                                        viewModel.startNewGame(viewModel.currentDifficulty)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("celebrate_next_game_button")
                            ) {
                                Text(TextRes.get("next_game_btn", viewModel.currentLanguage), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { viewModel.navigateTo(Screen.HOME) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasNextGame) Color.Transparent else Color(0xFF4F46E5)
                            ),
                            border = if (hasNextGame) BorderStroke(1.dp, if (isLight) Color(0xFFCBD5E1) else Color.White.copy(alpha = 0.3f)) else null,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("celebrate_ok_button")
                        ) {
                            Text(
                                text = TextRes.get("back_home_btn", viewModel.currentLanguage),
                                color = if (hasNextGame) (if (isLight) Color(0xFF1E293B) else Color.White) else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = TextRes.get("win", viewModel.currentLanguage),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF10B981),
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Gold Cup",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = TextRes.get("moves", viewModel.currentLanguage) + ": ${viewModel.movesCount}",
                            fontWeight = FontWeight.Bold,
                            color = if (isLight) Color(0xFF1E293B) else Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = TextRes.get("time", viewModel.currentLanguage) + ": $timerText",
                            fontWeight = FontWeight.Bold,
                            color = if (isLight) Color(0xFF1E293B) else Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = TextRes.get("score", viewModel.currentLanguage) + ": +${viewModel.lastCalculatedScore} PTS",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4F46E5),
                            fontSize = 16.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+${viewModel.lastCoinsEarned}",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFD97706),
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        if (viewModel.unlockedNewDifficultyThisTurn) {
                            val nextDifficultyName = when(profile.unlockedDifficulty) {
                                1 -> TextRes.get("medium_short", viewModel.currentLanguage)
                                2 -> TextRes.get("hard_short", viewModel.currentLanguage)
                                3 -> TextRes.get("expert_short", viewModel.currentLanguage)
                                4 -> TextRes.get("epic_short", viewModel.currentLanguage)
                                5 -> TextRes.get("master_short", viewModel.currentLanguage)
                                else -> ""
                            }
                            Text(
                                text = String.format(TextRes.get("unlocked_new_difficulty", viewModel.currentLanguage), nextDifficultyName),
                                fontSize = 13.sp,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            val praiseMessage = when {
                                viewModel.movesCount <= 4 -> TextRes.get("win_praise_moves_low", viewModel.currentLanguage)
                                viewModel.movesCount <= 8 -> TextRes.get("win_praise_moves_medium", viewModel.currentLanguage)
                                else -> TextRes.get("win_praise_moves_high", viewModel.currentLanguage)
                            }
                            Text(
                                text = praiseMessage,
                                fontSize = 13.sp,
                                color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                },
                containerColor = if (isLight) Color.White else Color(0xFF1E1E1E),
                textContentColor = if (isLight) Color(0xFF1E293B) else Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }

        // --- QUIT CONFLICT DIALOG CONFIRMATION ---
        if (viewModel.quitDialogVisible) {
            AlertDialog(
                onDismissRequest = { viewModel.quitDialogVisible = false },
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = TextRes.get("pause_or_quit_title", viewModel.currentLanguage),
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = if (isLight) Color(0xFF1E293B) else Color.White
                        )
                    }
                },
                text = {
                    val promptText = if (viewModel.movesCount == 0) {
                        TextRes.get("exit_no_moves_warn", viewModel.currentLanguage)
                    } else if (viewModel.isDailyChallenge) {
                        TextRes.get("pause_prompt_daily", viewModel.currentLanguage)
                    } else {
                        TextRes.get("pause_prompt_classic", viewModel.currentLanguage)
                    }
                    Text(
                        text = promptText,
                        fontSize = 14.sp,
                        color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.82f),
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (viewModel.movesCount > 0) {
                            Button(
                                onClick = { viewModel.pauseAndExitGame() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_and_pause_button")
                            ) {
                                Text(TextRes.get("save_and_pause_btn", viewModel.currentLanguage), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { viewModel.exitGameVoluntarily() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("confirm_quit_button")
                        ) {
                            val btnText = if (viewModel.movesCount == 0) {
                                TextRes.get("exit_without_penalty_btn", viewModel.currentLanguage)
                            } else {
                                TextRes.get("exit_lose_life_btn", viewModel.currentLanguage)
                            }
                            Text(btnText, color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        TextButton(
                            onClick = { 
                                viewModel.quitDialogVisible = false 
                                viewModel.resumeGame()
                            }
                        ) {
                            Text(
                                text = TextRes.get("continue_playing_btn", viewModel.currentLanguage),
                                color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.6f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                containerColor = if (isLight) Color.White else Color(0xFF1E1E1E),
                shape = RoundedCornerShape(20.dp)
            )
        }

        if (viewModel.isGamePaused) {
            // Overlay de Pause
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F172A),
                                Color(0xFF1E1B4B),
                                Color(0xFF311042)
                            )
                        )
                    )
                    .clickable(enabled = true, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ENIGMA BOTTLE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFD700),
                            letterSpacing = 4.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = TextRes.get("game_paused_title", viewModel.currentLanguage),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = { viewModel.resumeGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = TextRes.get("resume_game_btn", viewModel.currentLanguage),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.quitDialogVisible = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = TextRes.get("exit", viewModel.currentLanguage),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
fun PowerUpItemButton(
    title: String,
    cost: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isLight: Boolean,
    modifier: Modifier = Modifier,
    ownedCount: Int = 0,
    language: String = "pt",
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .testTag("powerup_$title")
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            if (isLight) Color(0xFFF1F5F9) else color.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(if (isLight) 1.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = if (isLight) Color(0xFF1E293B) else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            if (ownedCount > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = String.format(TextRes.get("powerup_owned", language), ownedCount),
                        color = Color(0xFF10B981),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Cost",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "$cost",
                        color = Color(0xFFD97706),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
