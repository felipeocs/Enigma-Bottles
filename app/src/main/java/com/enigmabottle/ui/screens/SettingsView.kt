package com.enigmabottle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun SettingsView(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    var termsDialogVisible by remember { mutableStateOf(false) }
    var privacyDialogVisible by remember { mutableStateOf(false) }

    val languagesList = listOf(
        Pair("Português", "pt"),
        Pair("English", "en"),
        Pair("Español", "es"),
        Pair("Français", "fr"),
        Pair("Deutsch", "de")
    )

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
                        .testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = if (isLight) Color(0xFF475569) else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = TextRes.get("settings", viewModel.currentLanguage),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isLight) Color(0xFF1E293B) else Color.White
                )

                Spacer(modifier = Modifier.width(36.dp))
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Language Selection selector
                item {
                    Text(
                        text = TextRes.get("lang_title", viewModel.currentLanguage),
                        color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.1f)),
                        elevation = CardDefaults.cardElevation(if (isLight) 1.dp else 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            languagesList.forEach { pair ->
                                val isSelected = viewModel.currentLanguage == pair.second
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("lang_select_${pair.second}")
                                        .clickable { viewModel.setLanguage(pair.second) }
                                        .padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = pair.first,
                                        color = if (isSelected) Color(0xFF4F46E5) else (if (isLight) Color(0xFF1E293B) else Color.White),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Ativo",
                                            tint = Color(0xFF4F46E5),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                if (pair != languagesList.last()) {
                                    HorizontalDivider(
                                        color = if (isLight) Color(0xFFF1F5F9) else Color.White.copy(alpha = 0.08f), 
                                        thickness = 1.dp
                                    )
                                }
                            }
                        }
                    }
                }

                // Game Audio preferences using sleek cards
                item {
                    Text(
                        text = TextRes.get("game_preferences", viewModel.currentLanguage),
                        color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.1f)),
                        elevation = CardDefaults.cardElevation(if (isLight) 1.dp else 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            // Audio Toggle Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (viewModel.isMuted) TextRes.get("audio_disabled", viewModel.currentLanguage) else TextRes.get("audio_enabled", viewModel.currentLanguage),
                                    color = if (isLight) Color(0xFF1E293B) else Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Switch(
                                    checked = !viewModel.isMuted,
                                    onCheckedChange = { viewModel.isMuted = !it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF4F46E5), // Indigo Accent
                                        uncheckedThumbColor = if (isLight) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.4f),
                                        uncheckedTrackColor = if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.1f)
                                    )
                                )
                            }

                            HorizontalDivider(
                                color = if (isLight) Color(0xFFF1F5F9) else Color.White.copy(alpha = 0.08f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            // Vibration Toggle Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (profile.isVibrationEnabled) TextRes.get("vibration_enabled", viewModel.currentLanguage) else TextRes.get("vibration_disabled", viewModel.currentLanguage),
                                    color = if (isLight) Color(0xFF1E293B) else Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Switch(
                                    checked = profile.isVibrationEnabled,
                                    onCheckedChange = { viewModel.toggleVibration(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF4F46E5), // Indigo Accent
                                        uncheckedThumbColor = if (isLight) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.4f),
                                        uncheckedTrackColor = if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.1f)
                                    )
                                )
                            }
                        }
                    }
                }

                // GDPR Compliance and Legal Sections
                item {
                    Text(
                        text = TextRes.get("legal_info", viewModel.currentLanguage),
                        color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.1f)),
                        elevation = CardDefaults.cardElevation(if (isLight) 1.dp else 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("terms_trigger")
                                    .clickable { termsDialogVisible = true }
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = TextRes.get("terms_of_use", viewModel.currentLanguage),
                                    color = if (isLight) Color(0xFF1E293B) else Color.White,
                                    fontSize = 14.sp
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Launch,
                                    contentDescription = "Read",
                                    tint = if (isLight) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.4f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            HorizontalDivider(
                                color = if (isLight) Color(0xFFF1F5F9) else Color.White.copy(alpha = 0.08f), 
                                thickness = 1.dp
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("privacy_trigger")
                                    .clickable { privacyDialogVisible = true }
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = TextRes.get("privacy_policy", viewModel.currentLanguage),
                                    color = if (isLight) Color(0xFF1E293B) else Color.White,
                                    fontSize = 14.sp
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Launch,
                                    contentDescription = "Read",
                                    tint = if (isLight) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.4f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Technical diagnostic credentials
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = TextRes.get("app_version_info", viewModel.currentLanguage),
                        color = if (isLight) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.35f),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 12.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // --- TERMS OF USE MODAL WITH DYNAMIC SCHEMES ---
        if (termsDialogVisible) {
            AlertDialog(
                onDismissRequest = { termsDialogVisible = false },
                confirmButton = {
                    Button(
                        onClick = { termsDialogVisible = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(TextRes.get("accept", viewModel.currentLanguage), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Text(
                        text = TextRes.get("terms_of_use", viewModel.currentLanguage),
                        fontWeight = FontWeight.Bold,
                        color = if (isLight) Color(0xFF1E293B) else Color.White
                    )
                },
                text = {
                    Box(modifier = Modifier.heightIn(max = 300.dp).verticalScroll(rememberScrollState())) {
                        Text(
                            text = TextRes.get("terms_text", viewModel.currentLanguage),
                            color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Left
                        )
                    }
                },
                containerColor = if (isLight) Color.White else Color(0xFF1E1E1E),
                shape = RoundedCornerShape(20.dp)
            )
        }

        // --- PRIVACY POLICY MODAL WITH DYNAMIC SCHEMES ---
        if (privacyDialogVisible) {
            AlertDialog(
                onDismissRequest = { privacyDialogVisible = false },
                confirmButton = {
                    Button(
                        onClick = { privacyDialogVisible = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(TextRes.get("aware", viewModel.currentLanguage), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Text(
                        text = TextRes.get("privacy_policy", viewModel.currentLanguage),
                        fontWeight = FontWeight.Bold,
                        color = if (isLight) Color(0xFF1E293B) else Color.White
                    )
                },
                text = {
                    Box(modifier = Modifier.heightIn(max = 300.dp).verticalScroll(rememberScrollState())) {
                        Text(
                            text = TextRes.get("privacy_text", viewModel.currentLanguage),
                            color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Left
                        )
                    }
                },
                containerColor = if (isLight) Color.White else Color(0xFF1E1E1E),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}
