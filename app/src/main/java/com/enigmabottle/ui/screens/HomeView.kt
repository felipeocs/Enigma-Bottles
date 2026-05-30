package com.enigmabottle.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Brush
import com.enigmabottle.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enigmabottle.data.*
import com.enigmabottle.ui.components.GameThemeBackground
import com.enigmabottle.viewmodel.GameViewModel
import com.enigmabottle.viewmodel.Screen

@Composable
fun HomeView(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val recordList by viewModel.gameRecords.collectAsState()
    
    // Check if there is an active saved game
    val hasSavedMatch = viewModel.checkPendingMatchSuspended()

    val isLight = profile.activeBgId == "sleek_interface" || profile.activeBgId.startsWith("clear_")

    GameThemeBackground(bgId = profile.activeBgId) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 40.dp, bottom = 48.dp)
        ) {
            // Header: Dynamic Wallet and Hearts indicators inside Sleek Rounded-Full pills
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Currency Amber Capsule - robust gaming style
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (isLight) Color(0xFFFFFBEB) else Color(0xFF1E1E24).copy(alpha = 0.9f))
                            .border(3.dp, Color(0xFFFFC107), RoundedCornerShape(24.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🪙", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${profile.coins}",
                                color = if (isLight) Color(0xFF92400E) else Color(0xFFFCD34D),
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                modifier = Modifier.testTag("currency_text")
                            )
                        }
                    }

                    // Row of: Daily Reward Gift Capsule + Lives Capsule
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Compact Gift Capsule
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (isLight) Color(0xFFECFDF5) else Color(0xFF047857).copy(alpha = 0.6f))
                                .border(3.dp, Color(0xFF10B981), RoundedCornerShape(24.dp))
                                .clickable { viewModel.showDailyRewardDialog = true }
                                .testTag("top_gift_bonus_capsule")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🎁", fontSize = 18.sp)
                            }
                        }

                        // Lives Red Capsule - robust gaming style
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (isLight) Color(0xFFFEF2F2) else Color(0xFF1E1E24).copy(alpha = 0.9f))
                                .border(3.dp, Color(0xFFEF4444), RoundedCornerShape(24.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("❤️", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                val hasInfinite = profile.isAdFree && profile.infiniteLivesEndTime > System.currentTimeMillis()
                                val remainingMin = if (hasInfinite) {
                                    ((profile.infiniteLivesEndTime - System.currentTimeMillis()) / 1000 / 60).coerceAtLeast(0)
                                } else 0
                                Text(
                                    text = if (hasInfinite) "∞" else (if (profile.lives >= 5) "${profile.lives}" else "${profile.lives}/5"),
                                    color = if (isLight) Color(0xFFB91C1C) else Color(0xFFFCA5A5),
                                    fontWeight = FontWeight.Black,
                                    fontSize = if (hasInfinite) 19.sp else 15.sp,
                                    modifier = Modifier.testTag("lives_text")
                                )
                                if (hasInfinite) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "(${remainingMin}m)",
                                        color = if (isLight) Color(0xFFB91C1C).copy(alpha = 0.82f) else Color(0xFFFCA5A5).copy(alpha = 0.8f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else if (profile.lives < 5) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "(${viewModel.livesRegenCountDown})",
                                        color = if (isLight) Color(0xFFB91C1C).copy(alpha = 0.82f) else Color(0xFFFCA5A5).copy(alpha = 0.8f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Beautiful Game Logo heading (Not minimalist, gorgeous visual layout)
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    // Actual gorgeous custom logo brand badge
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .size(110.dp)
                    ) {
                        // Outer subtle glow halo
                        Box(
                            modifier = Modifier
                                .size(105.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFFD700).copy(alpha = 0.25f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        // Background circle of the launcher
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_background),
                            contentDescription = "Background Glow",
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, Color(0xFFFFD700), CircleShape)
                        )
                        // Foreground bottle logo
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Enigma Bottles Potion Logo",
                            modifier = Modifier
                                .size(88.dp)
                        )
                    }
                    Text(
                        text = "ENIGMA BOTTLES",
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = if (isLight) {
                                    listOf(Color(0xFF3730A3), Color(0xFF4F46E5), Color(0xFF6366F1))
                                } else {
                                    listOf(Color(0xFFFFD700), Color(0xFFA78BFA), Color(0xFF60A5FA))
                                }
                            ),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            lineHeight = 36.sp,
                            letterSpacing = 2.5.sp,
                            shadow = Shadow(
                                color = if (isLight) Color(0x27000000) else Color(0xFF0D0d1c),
                                offset = Offset(2f, 4f),
                                blurRadius = 8f
                            )
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                if (isLight) Color(0xFFEEF2FF) else Color(0xFF312E81).copy(alpha = 0.6f),
                                RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, if (isLight) Color(0xFFC7D2FE) else Color(0xFF4338CA), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = TextRes.get("magic_alchemy_formula", viewModel.currentLanguage),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isLight) Color(0xFF3730A3) else Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Indicação visual de bônus acumulados das recompensas diárias
                if (profile.hintCount > 0 || profile.xRayCount > 0) {
                    Row(
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .background(
                                if (isLight) Color(0xFFF1F5F9) else Color.White.copy(alpha = 0.08f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = TextRes.get("free_items_label", viewModel.currentLanguage),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.6f)
                        )
                        if (profile.hintCount > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Dicas",
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${profile.hintCount} " + TextRes.get("hints_plural", viewModel.currentLanguage),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isLight) Color(0xFF1E293B) else Color.White
                                )
                            }
                        }
                        if (profile.xRayCount > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "Raio-X",
                                    tint = Color(0xFF00ACC1),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${profile.xRayCount} " + TextRes.get("xrays_plural", viewModel.currentLanguage),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isLight) Color(0xFF1E293B) else Color.White
                                )
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Resume button if valid saved session exists
            if (hasSavedMatch) {
                item {
                    Button(
                        onClick = { viewModel.resumeSavedProgress() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)), // Emerald 500
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .testTag("resume_match_button")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Resume")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = TextRes.get("resume", viewModel.currentLanguage),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Featured Daily Challenge mode at the top (First game mode)
            item {
                Text(
                    text = TextRes.get("today_challenge_title", viewModel.currentLanguage),
                    color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp, start = 4.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .testTag("featured_daily_challenge_card")
                        .clickable { viewModel.navigateTo(Screen.DAILY_CALENDAR) },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        width = 3.dp,
                        color = Color(0xFFF97316) // Vibrant Orange border
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLight) Color(0xFFFFF7ED) else Color(0xFF2C190D).copy(alpha = 0.8f)
                    ),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left calendar visual badge
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF97316).copy(alpha = 0.15f))
                                .border(2.dp, Color(0xFFF97316), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📅", fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Text explanation and label
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = TextRes.get("daily_challenge", viewModel.currentLanguage),
                                color = if (isLight) Color(0xFF9A3412) else Color(0xFFFF9735),
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = TextRes.get("solve_exclusive_puzzle", viewModel.currentLanguage),
                                color = if (isLight) Color(0xFF7C2D12).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.75f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Chevron play icon
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF97316)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Unified Game Mode Hub & Difficulty Grid
            val classicLabelSuffix = if (viewModel.currentLanguage == "pt") "Modo Clássico" else if (viewModel.currentLanguage == "es") "Modo Clásico" else "Classic Mode"
            item {
                Text(
                    text = TextRes.get("new_game", viewModel.currentLanguage) + " ($classicLabelSuffix)",
                    color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp, start = 4.dp)
                )
            }

            // Easy, Medium, Hard, Expert, Epic, Master blocks
            val levels = listOf(
                LevelItem("Fácil", 0, "easy", Color(0xFF10B981)), // emerald
                LevelItem("Médio", 1, "medium", Color(0xFF3B82F6)), // blue
                LevelItem("Difícil", 2, "hard", Color(0xFFF59E0B)), // amber
                LevelItem("Especialista", 3, "expert", Color(0xFFF97316)), // orange
                LevelItem("Épico", 4, "epic", Color(0xFFEF4444)), // red
                LevelItem("Mestre", 5, "master", Color(0xFF8B5CF6)) // violet
            )

            items(levels.size) { index ->
                val lvl = levels[index]
                val unlockedTier = profile.unlockedDifficulty
                val isUnlocked = unlockedTier >= lvl.tierIndex

                val winsCount = if (isUnlocked) {
                    viewModel.getWinsForDifficulty(lvl.name, profile)
                } else {
                    viewModel.getWinsForDifficulty(levels[if (index > 0) index - 1 else 0].name, profile)
                }
                
                val winsNeeded = if (isUnlocked) {
                    viewModel.getWinsTargetForDifficulty(lvl.name)
                } else {
                    viewModel.getWinsTargetForDifficulty(levels[if (index > 0) index - 1 else 0].name)
                }

                val (stageEmoji, stageQuestName, starRating) = when (lvl.name) {
                    "Fácil" -> Triple("🟢", if (viewModel.currentLanguage == "pt") "Iniciação do Aprendiz 🧪" else if (viewModel.currentLanguage == "es") "Iniciación de Aprendiz 🧪" else "Apprentice Initiation 🧪", "⭐")
                    "Médio" -> Triple("🔵", if (viewModel.currentLanguage == "pt") "Mistura Concentrada 🔮" else if (viewModel.currentLanguage == "es") "Mezcla Concentrada 🔮" else "Concentrated Mix 🔮", "⭐⭐")
                    "Difícil" -> Triple("🟡", if (viewModel.currentLanguage == "pt") "Fórmula Instável ⚙️" else if (viewModel.currentLanguage == "es") "Fórmula Inestable ⚙️" else "Unstable Formula ⚙️", "⭐⭐⭐")
                    "Especialista" -> Triple("🟠", if (viewModel.currentLanguage == "pt") "Reação em Cadeia ⚡" else if (viewModel.currentLanguage == "es") "Reacción en Cadena ⚡" else "Chain Reaction ⚡", "⭐⭐⭐⭐")
                    "Épico" -> Triple("🔴", if (viewModel.currentLanguage == "pt") "Elixir Proibido 💀" else if (viewModel.currentLanguage == "es") "Elixir Prohibido 💀" else "Forbidden Elixir 💀", "⭐⭐⭐⭐⭐")
                    else -> Triple("🟣", if (viewModel.currentLanguage == "pt") "Alquimia Lendária 👑" else if (viewModel.currentLanguage == "es") "Alquimia Legendaria 👑" else "Legendary Alchemy 👑", "🏆🏆🏆🏆🏆")
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("level_card_${lvl.name}")
                        .clickable(enabled = isUnlocked) {
                            viewModel.startNewGame(lvl.name)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUnlocked) {
                            if (isLight) Color(0xFFF8FAFC) else Color(0xFF1E293B).copy(alpha = 0.85f)
                        } else {
                            if (isLight) Color(0xFFF1F5F9).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f)
                        }
                    ),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(
                        width = if (isUnlocked) 3.dp else 1.dp,
                        color = if (isUnlocked) lvl.accentColor else (if (isLight) Color(0xFFCBD5E1) else Color.White.copy(alpha = 0.15f))
                    ),
                    elevation = CardDefaults.cardElevation(if (isUnlocked) 4.dp else 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Large circular stage indicator with a cool emoji
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isUnlocked) lvl.accentColor.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f))
                                .border(2.dp, if (isUnlocked) lvl.accentColor else Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (isUnlocked) stageEmoji else "🔒", fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = TextRes.get(lvl.langKey, viewModel.currentLanguage).uppercase(),
                                    color = if (isUnlocked) (if (isLight) Color(0xFF0F172A) else Color.White) else (if (isLight) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.4f)),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isUnlocked) starRating else "",
                                    fontSize = 11.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(2.dp))
                            
                            Text(
                                text = if (isUnlocked) stageQuestName else TextRes.get("fase_bloqueada", viewModel.currentLanguage),
                                color = if (isUnlocked) lvl.accentColor else (if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.4f)),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            if (isUnlocked) {
                                val bottlesCount = viewModel.getBottleCountForDifficulty(lvl.name)
                                Text(
                                    text = String.format(java.util.Locale.getDefault(), if (viewModel.currentLanguage == "pt") "Desafio: %d garrafas" else if (viewModel.currentLanguage == "es") "Reto: %d botellas" else "Challenge: %d bottles", lvl.name.let {
                                        when(it) {
                                            "Fácil" -> 3
                                            "Médio" -> 5
                                            "Difícil" -> 8
                                            "Especialista" -> 10
                                            "Épico" -> 13
                                            else -> 16
                                        }
                                    }),
                                    color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            } else {
                                val prevLevelName = levels[if (index > 0) index - 1 else 0].name
                                val prevLevelLangKey = levels[if (index > 0) index - 1 else 0].langKey
                                Text(
                                    text = String.format(TextRes.get("unlock_with_wins", viewModel.currentLanguage), winsNeeded - winsCount) + TextRes.get(prevLevelLangKey, viewModel.currentLanguage),
                                    color = if (isLight) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.4f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        if (isUnlocked) {
                            if (winsNeeded > 0) {
                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🏆", fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "$winsCount/$winsNeeded",
                                            color = if (isLight) Color(0xFF1E293B) else Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            } else {
                                // Mestre (master) mode unlocked free
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                        .border(1.5.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "FREE PLAY",
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Actions section
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = TextRes.get("other_modes_shop", viewModel.currentLanguage),
                    color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp, start = 4.dp)
                )

                // Row 1: Loja (Com destaque total de 2 colunas)
                QuickActionCard(
                    title = TextRes.get("shop", viewModel.currentLanguage),
                    icon = Icons.Default.Storefront,
                    color = Color(0xFFEC4899),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.navigateTo(Screen.STORE) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Row 2: Estatísticas & Configurações
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = TextRes.get("stats", viewModel.currentLanguage),
                        icon = Icons.Default.BarChart,
                        color = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.STATS) }
                    )
                    QuickActionCard(
                        title = TextRes.get("settings", viewModel.currentLanguage),
                        icon = Icons.Default.Settings,
                        color = Color(0xFF64748B),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.SETTINGS) }
                    )
                }
            }

            // Monetization Rewarded Ad Card - styled inside beautiful Indigo container matching Tailwind style
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLight) Color(0xFFEEF2FF) else Color(0xFF1E1E1E).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        1.dp,
                        SolidColor(if (isLight) Color(0xFFC7D2FE) else Color.White.copy(alpha = 0.15f))
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎬 " + TextRes.get("watch_ad", viewModel.currentLanguage),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isLight) Color(0xFF3730A3) else Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = TextRes.get("watch_ad_desc", viewModel.currentLanguage),
                            fontSize = 12.sp,
                            color = if (isLight) Color(0xFF4F46E5) else Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { viewModel.launchAdRewardFlow("coins") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24)), // Amber 400
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("+75 Moedas", color = Color(0xFF78350F), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Button(
                                onClick = { viewModel.launchAdRewardFlow("lives") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)), // Red 500
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("+1 Vida", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class LevelItem(
    val name: String,
    val tierIndex: Int,
    val langKey: String,
    val accentColor: Color
)

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isLight = MaterialTheme.colorScheme.background.luminance() > 0.5f
    Card(
        modifier = modifier
            .height(96.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
        ),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            1.dp,
            SolidColor(if (isLight) Color(0xFFE2E8F0) else color.copy(alpha = 0.35f))
        ),
        elevation = CardDefaults.cardElevation(if (isLight) 1.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = if (isLight) Color(0xFF1E293B) else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 16.sp
            )
        }
    }
}
