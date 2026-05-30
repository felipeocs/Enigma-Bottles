package com.enigmabottle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.enigmabottle.ui.components.GameThemeBackground
import com.enigmabottle.viewmodel.GameViewModel
import com.enigmabottle.viewmodel.Screen

@Composable
fun StatsView(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val gameList by viewModel.gameRecords.collectAsState()

    // Aggregate statistics from history
    val totalGamesPlayed = gameList.size
    val totalWins = gameList.count { it.won }
    val winRatio = if (totalGamesPlayed > 0) (totalWins.toFloat() / totalGamesPlayed * 100).toInt() else 0

    val averageMoves = if (totalWins > 0) {
        gameList.filter { it.won }.map { it.moves }.average().toInt()
    } else 0

    val averageTime = if (totalWins > 0) {
        val totalSeconds = gameList.filter { it.won }.map { it.timeInSeconds }.average().toInt()
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        String.format("%02d:%02d", m, s)
    } else "00:00"

    val maxDifficultyUnlocked = when (profile.unlockedDifficulty) {
        0 -> TextRes.get("easy_short", viewModel.currentLanguage)
        1 -> TextRes.get("medium_short", viewModel.currentLanguage)
        2 -> TextRes.get("hard_short", viewModel.currentLanguage)
        3 -> TextRes.get("expert_short", viewModel.currentLanguage)
        4 -> TextRes.get("epic_short", viewModel.currentLanguage)
        else -> TextRes.get("master_short", viewModel.currentLanguage)
    }

    val isLight = profile.activeBgId == "sleek_interface" || profile.activeBgId.startsWith("clear_")

    GameThemeBackground(bgId = profile.activeBgId) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
        ) {
            // Header panel layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.HOME) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (isLight) Color(0xFFF1F5F9) else Color.Black.copy(alpha = 0.5f), 
                            RoundedCornerShape(10.dp)
                        )
                        .testTag("stats_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = if (isLight) Color(0xFF475569) else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = TextRes.get("stats", viewModel.currentLanguage),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isLight) Color(0xFF1E293B) else Color.White
                )

                Spacer(modifier = Modifier.width(36.dp))
            }

            // Statistics Content
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Main stats cards row summary
                item {
                    val metrics = listOf(
                        MetricPair(TextRes.get("total_games", viewModel.currentLanguage), "$totalGamesPlayed", Icons.Default.Casino, Color(0xFF3B82F6)), // Indigo/Blue
                        MetricPair(TextRes.get("win_ratio", viewModel.currentLanguage), "$winRatio%", Icons.Default.CheckCircle, Color(0xFF10B981)), // Emerald
                        MetricPair(TextRes.get("avg_moves", viewModel.currentLanguage), "$averageMoves", Icons.Default.SwapHoriz, Color(0xFFF59E0B)), // Amber
                        MetricPair(TextRes.get("avg_time", viewModel.currentLanguage), averageTime, Icons.Default.Timer, Color(0xFF8B5CF6)) // Purple
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard(item = metrics[0], isLight = isLight, modifier = Modifier.weight(1f))
                            MetricCard(item = metrics[1], isLight = isLight, modifier = Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard(item = metrics[2], isLight = isLight, modifier = Modifier.weight(1f))
                            MetricCard(item = metrics[3], isLight = isLight, modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Stage Progression Unlock status
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.10f)),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(if (isLight) 1.dp else 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = "Progresso Unlocked",
                                tint = Color(0xFFFFC107), // Golden Accent
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = TextRes.get("max_diff_unlocked", viewModel.currentLanguage), 
                                    color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.6f), 
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = maxDifficultyUnlocked,
                                    color = if (isLight) Color(0xFF1E293B) else Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = String.format(TextRes.get("profile_progress_level", viewModel.currentLanguage), profile.unlockedDifficulty + 1),
                                    color = if (isLight) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // History Title Label
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = TextRes.get("history_challenges", viewModel.currentLanguage),
                        color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                    )
                    if (gameList.isEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isLight) Color.White else Color.White.copy(alpha = 0.05f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, if (isLight) Color(0xFFE2E8F0) else Color.Transparent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = TextRes.get("empty_history", viewModel.currentLanguage),
                                modifier = Modifier.padding(24.dp),
                                color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Records iteration list
                items(gameList) { item ->
                    val min = item.timeInSeconds / 60
                    val sec = item.timeInSeconds % 60
                    val durText = String.format("%02d:%02dm", min, sec)

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, if (isLight) Color(0xFFF1F5F9) else Color.White.copy(alpha = 0.05f)),
                        elevation = CardDefaults.cardElevation(if (isLight) 0.5.dp else 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
						) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (item.won) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (item.won) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        contentDescription = "Status",
                                        tint = if (item.won) Color(0xFF10B981) else Color(0xFFEF4444),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    val translatedDiff = when (item.difficulty) {
                                        "Fácil" -> TextRes.get("easy_short", viewModel.currentLanguage)
                                        "Médio" -> TextRes.get("medium_short", viewModel.currentLanguage)
                                        "Difícil" -> TextRes.get("hard_short", viewModel.currentLanguage)
                                        "Especialista" -> TextRes.get("expert_short", viewModel.currentLanguage)
                                        "Épico" -> TextRes.get("epic_short", viewModel.currentLanguage)
                                        else -> TextRes.get("master_short", viewModel.currentLanguage)
                                    }
                                    val modeLabel = if (item.mode == "daily") TextRes.get("daily", viewModel.currentLanguage) else TextRes.get("classic", viewModel.currentLanguage)
                                    Text(
                                        text = "$translatedDiff ($modeLabel)",
                                        color = if (isLight) Color(0xFF1E293B) else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = TextRes.get("concluded_at", viewModel.currentLanguage) + java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(item.timestamp)),
                                        color = if (isLight) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.4f),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "PTS: ${item.score}",
                                    color = Color(0xFF4F46E5), // Indigo 600
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = String.format(TextRes.get("swaps_and_time", viewModel.currentLanguage), item.moves, durText),
                                    color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class MetricPair(
    val title: String,
    val value: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

@Composable
fun MetricCard(
    item: MetricPair,
    isLight: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.10f)),
        elevation = CardDefaults.cardElevation(if (isLight) 1.dp else 0.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = item.color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.title, 
                color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.5f), 
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.value,
                color = if (isLight) Color(0xFF1E293B) else Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
        }
    }
}
