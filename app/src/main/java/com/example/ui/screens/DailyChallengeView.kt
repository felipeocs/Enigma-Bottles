package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.GameThemeBackground
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.Screen
import java.util.*

@Composable
fun DailyChallengeView(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val wonDailyRecords by viewModel.wonDailyRecords.collectAsState()

    // Setup dates based on calendarMonth and calendarYear states
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, viewModel.calendarYear)
        set(Calendar.MONTH, viewModel.calendarMonth)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val monthName = when (viewModel.calendarMonth) {
        Calendar.JANUARY -> TextRes.get("jan", viewModel.currentLanguage)
        Calendar.FEBRUARY -> TextRes.get("feb", viewModel.currentLanguage)
        Calendar.MARCH -> TextRes.get("mar", viewModel.currentLanguage)
        Calendar.APRIL -> TextRes.get("apr", viewModel.currentLanguage)
        Calendar.MAY -> TextRes.get("may", viewModel.currentLanguage)
        Calendar.JUNE -> TextRes.get("jun", viewModel.currentLanguage)
        Calendar.JULY -> TextRes.get("jul", viewModel.currentLanguage)
        Calendar.AUGUST -> TextRes.get("aug", viewModel.currentLanguage)
        Calendar.SEPTEMBER -> TextRes.get("sep", viewModel.currentLanguage)
        Calendar.OCTOBER -> TextRes.get("oct", viewModel.currentLanguage)
        Calendar.NOVEMBER -> TextRes.get("nov", viewModel.currentLanguage)
        else -> TextRes.get("dec", viewModel.currentLanguage)
    }

    val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 (Sunday) to 6 (Saturday)

    // Current local clock bounds
    val todayCalendar = Calendar.getInstance()
    val todayYear = todayCalendar.get(Calendar.YEAR)
    val todayMonth = todayCalendar.get(Calendar.MONTH)
    val todayDay = todayCalendar.get(Calendar.DAY_OF_MONTH)

    var selectedDay by remember(viewModel.calendarMonth, viewModel.calendarYear) {
        mutableStateOf(
            if (viewModel.calendarYear == todayYear && viewModel.calendarMonth == todayMonth) {
                todayDay
            } else {
                1
            }
        )
    }

    // Calculate completions
    val monthPrefix = String.format("%04d-%02d", viewModel.calendarYear, viewModel.calendarMonth + 1)
    val solvedDaysInMonth = wonDailyRecords
        .filter { it.dayKey.startsWith(monthPrefix) }
        .map {
            // parse day out of "YYYY-MM-DD"
            val parts = it.dayKey.split("-")
            if (parts.size == 3) parts[2].toInt() else 0
        }
        .toSet()

    val totalSolvedOfThisMonth = solvedDaysInMonth.size
    val isMonthFullyCompleted = totalSolvedOfThisMonth == maxDays

    val canGoPrev = true 
    val canGoNext = true

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
                        .testTag("daily_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = if (isLight) Color(0xFF475569) else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = TextRes.get("daily_challenge", viewModel.currentLanguage),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isLight) Color(0xFF1E293B) else Color.White
                )

                IconButton(
                    onClick = { viewModel.showToast(TextRes.get("daily_tip", viewModel.currentLanguage)) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (isLight) Color(0xFFF1F5F9) else Color.Black.copy(alpha = 0.5f), 
                            RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "Info",
                        tint = if (isLight) Color(0xFF475569) else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Month navigation selector layout
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(if (isLight) 1.dp else 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (viewModel.calendarMonth == Calendar.JANUARY) {
                                viewModel.calendarMonth = Calendar.DECEMBER
                                viewModel.calendarYear -= 1
                            } else {
                                viewModel.calendarMonth -= 1
                            }
                        },
                        enabled = canGoPrev
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft, 
                            contentDescription = "Mês Anterior", 
                            tint = if (isLight) Color(0xFF475569) else Color.White
                        )
                    }

                    val monthAndYear = if (viewModel.currentLanguage == "en" || viewModel.currentLanguage == "de" || viewModel.currentLanguage == "fr") {
                        "$monthName ${viewModel.calendarYear}"
                    } else {
                        "$monthName de ${viewModel.calendarYear}"
                    }

                    Text(
                        text = monthAndYear,
                        color = if (isLight) Color(0xFF1E293B) else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    IconButton(
                        onClick = {
                            if (viewModel.calendarMonth == Calendar.DECEMBER) {
                                viewModel.calendarMonth = Calendar.JANUARY
                                viewModel.calendarYear += 1
                            } else {
                                viewModel.calendarMonth += 1
                            }
                        },
                        enabled = canGoNext
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight, 
                            contentDescription = "Próximo Mês", 
                            tint = if (isLight) Color(0xFF475569) else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress status indicator panel
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                ),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(if (isLight) 1.dp else 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = TextRes.get("monthly_progress", viewModel.currentLanguage),
                            color = if (isLight) Color(0xFF475569) else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$totalSolvedOfThisMonth / $maxDays dias",
                            color = if (isLight) Color(0xFFEA580C) else Color(0xFFFF9800), // Orange-600
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { totalSolvedOfThisMonth.toFloat() / maxDays },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (isLight) Color(0xFF4F46E5) else Color(0xFFFF9800), // Indigo-600
                        trackColor = if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.15f)
                    )
                    
                    // Claim Monthly trophy feedback if completed
                    if (isMonthFullyCompleted) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents, 
                                contentDescription = "Troféu", 
                                tint = Color(0xFFFFC107), 
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = TextRes.get("congrat_badge", viewModel.currentLanguage) + " (+150 Moedas creditadas!)",
                                color = Color(0xFF10B981),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Grid column titles (DOM, SEG, TER, QUA, QUI, SEX, SAB)
            val weekHeaders = listOf(
                TextRes.get("week_sunday", viewModel.currentLanguage),
                TextRes.get("week_monday", viewModel.currentLanguage),
                TextRes.get("week_tuesday", viewModel.currentLanguage),
                TextRes.get("week_wednesday", viewModel.currentLanguage),
                TextRes.get("week_thursday", viewModel.currentLanguage),
                TextRes.get("week_friday", viewModel.currentLanguage),
                TextRes.get("week_saturday", viewModel.currentLanguage)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                weekHeaders.forEach { header ->
                    Text(
                        text = header,
                        modifier = Modifier.weight(1f),
                        color = if (isLight) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.4f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Calendar dates grid cell logic
            val totalCellsCount = 42 // 6 rows of 7 days
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(totalCellsCount) { cellIdx ->
                    val dayNum = cellIdx - firstDayOfWeek + 1

                    if (dayNum in 1..maxDays) {
                        val dayKey = String.format("%04d-%02d-%02d", viewModel.calendarYear, viewModel.calendarMonth + 1, dayNum)

                        val isLockedFuture = if (viewModel.calendarYear > todayYear) {
                            true
                        } else if (viewModel.calendarYear == todayYear && viewModel.calendarMonth > todayMonth) {
                            true
                        } else {
                            viewModel.calendarYear == todayYear && viewModel.calendarMonth == todayMonth && dayNum > todayDay
                        }

                        val isCleared = solvedDaysInMonth.contains(dayNum)
                        val isSelected = selectedDay == dayNum

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isCleared -> Color(0xFF10B981) // Pure solid Green of Cleared status
                                    isLockedFuture -> if (isLight) Color(0xFFF1F5F9).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f)
                                    else -> if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                                }
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = when {
                                    isSelected -> if (isLight) Color(0xFF4F46E5) else Color(0xFFFFD700)
                                    isCleared -> Color(0xFF10B981)
                                    isLockedFuture -> Color.Transparent
                                    else -> if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.15f)
                                }
                            ),
                            elevation = CardDefaults.cardElevation(if (isLight && !isLockedFuture && !isSelected) 1.dp else 0.dp),
                            modifier = Modifier
                                .aspectRatio(1.5f) // slightly wider for compact cell aesthetics
                                .testTag("calendar_day_$dayNum")
                                .clickable(enabled = !isLockedFuture) {
                                    selectedDay = dayNum
                                }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "$dayNum",
                                        color = when {
                                            isCleared -> Color.White
                                            isLockedFuture -> if (isLight) Color(0xFFCBD5E1) else Color.White.copy(alpha = 0.25f)
                                            else -> if (isLight) Color(0xFF1E293B) else Color.White
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (isCleared) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Cleared",
                                            tint = Color.White,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    } else if (isLockedFuture) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Bloqueado",
                                            tint = if (isLight) Color(0xFF94A3B8).copy(alpha = 0.4f) else Color.White.copy(alpha = 0.2f),
                                            modifier = Modifier.size(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Empty cell preceding first of month or succeeding last
                        Box(modifier = Modifier.aspectRatio(1.5f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botão "Jogar" e detalhes do desafio selecionado
            selectedDay?.let { day ->
                val dayKey = String.format("%04d-%02d-%02d", viewModel.calendarYear, viewModel.calendarMonth + 1, day)
                val calIndex = Calendar.getInstance().apply {
                    set(Calendar.YEAR, viewModel.calendarYear)
                    set(Calendar.MONTH, viewModel.calendarMonth)
                    set(Calendar.DAY_OF_MONTH, day)
                }.get(Calendar.DAY_OF_WEEK)

                val diffSelection = when (calIndex) {
                    Calendar.MONDAY, Calendar.FRIDAY -> "Difícil"
                    Calendar.TUESDAY, Calendar.SATURDAY -> "Especialista"
                    Calendar.WEDNESDAY -> "Épico"
                    else -> "Mestre" // Sunday, Thursday
                }
                
                val diffColor = when (diffSelection) {
                    "Difícil" -> Color(0xFFFF9800)
                    "Especialista" -> Color(0xFFEC4899)
                    "Épico" -> Color(0xFF8B5CF6)
                    else -> Color(0xFFEF4444) // Mestre
                }

                val isCleared = solvedDaysInMonth.contains(day)

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.08f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = TextRes.get("daily_distillate", viewModel.currentLanguage).replace("%1\$d", "$day"),
                                color = if (isLight) Color(0xFF1E293B) else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(diffColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = TextRes.get("difficulty_label", viewModel.currentLanguage).replace("%1\$s", diffSelection),
                                    color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.65f),
                                    fontSize = 12.sp
                                )
                            }
                            if (isCleared) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Cleared",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = TextRes.get("daily_completed", viewModel.currentLanguage),
                                        color = Color(0xFF10B981),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        
                        Button(
                            onClick = {
                                viewModel.startDailyChallenge(dayKey, diffSelection)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLight) Color(0xFF4F46E5) else Color(0xFFFFD700),
                                contentColor = if (isLight) Color.White else Color(0xFF121212)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("play_daily_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Jogar",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = TextRes.get("play_btn", viewModel.currentLanguage),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
