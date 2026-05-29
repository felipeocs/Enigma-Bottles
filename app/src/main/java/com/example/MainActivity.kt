package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.example.data.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.*
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TextRes.init(applicationContext)
        enableEdgeToEdge()

        // Room Database initialization
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.databaseDao()
        val repository = GameRepository(dao)

        setContent {
            // Instantiate GameViewModel with simple constructor factory injection
            val viewModel: GameViewModel by viewModels {
                GameViewModelFactory(repository)
            }
            val profile by viewModel.userProfile.collectAsState()
            val isLight = profile.activeBgId == "sleek_interface" || profile.activeBgId.startsWith("clear_")

            MyApplicationTheme(darkTheme = !isLight) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Dynamic Screen state router
                        when (viewModel.currentScreen) {
                            Screen.SPLASH -> SplashView(viewModel = viewModel, onEnterGame = { viewModel.navigateTo(Screen.HOME) })
                            Screen.HOME -> HomeView(viewModel = viewModel)
                            Screen.GAME_PLAY -> GamePlayView(viewModel = viewModel)
                            Screen.STORE -> StoreView(viewModel = viewModel)
                            Screen.STATS -> StatsView(viewModel = viewModel)
                            Screen.SETTINGS -> SettingsView(viewModel = viewModel)
                            Screen.DAILY_CALENDAR -> DailyChallengeView(viewModel = viewModel)
                        }

                        // --- DAILY LOGIN REWARD CALENDAR DIALOG ---
                        if (viewModel.showDailyRewardDialog) {
                            val currentStreak = viewModel.currentStreakDay
                            val claimed = viewModel.dailyRewardClaimedThisTurn

                            AlertDialog(
                                onDismissRequest = { 
                                    if (claimed) {
                                        viewModel.showDailyRewardDialog = false 
                                    }
                                },
                                title = {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = TextRes.get("daily_bonus_title", viewModel.currentLanguage),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isLight) Color(0xFF1E293B) else Color.White
                                        )
                                        Text(
                                            text = TextRes.get("daily_bonus_sub", viewModel.currentLanguage),
                                            fontSize = 11.sp,
                                            color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.6f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                },
                                text = {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // Row 1 (Days 1 to 4)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            for (day in 1..4) {
                                                val isCurrent = (day == currentStreak)
                                                val isPast = (day < currentStreak)
                                                val isClaimedToday = (isCurrent && claimed)

                                                Card(
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = if (isCurrent) Color(0xFF4F46E5) else if (isPast || isClaimedToday) (if (isLight) Color(0xFFD1FAE5) else Color(0xFF10B981).copy(alpha = 0.2f)) else (if (isLight) Color(0xFFF1F5F9) else Color.White.copy(alpha = 0.08f))
                                                    ),
                                                    shape = RoundedCornerShape(10.dp),
                                                    border = BorderStroke(
                                                        1.dp,
                                                        if (isCurrent) (if (isLight) Color(0xFF4F46E5) else Color.White) else if (isPast) Color(0xFF10B981) else (if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.15f))
                                                    ),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 8.dp, horizontal = 2.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            text = String.format(TextRes.get("day_indicator", viewModel.currentLanguage), day),
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isCurrent) Color.White else (if (isLight) Color(0xFF475569) else Color.White)
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        val iconText = when(day) {
                                                            1 -> "🪙20"
                                                            2 -> "🪙35"
                                                            3 -> "💡" + TextRes.get("hint_btn", viewModel.currentLanguage)
                                                            else -> "🪙65"
                                                        }
                                                        Text(
                                                            text = iconText,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Black,
                                                            color = if (isCurrent) Color.White else (if (isLight) Color(0xFF1E293B) else Color.White)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Row 2 (Days 5 to 7)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            for (day in 5..7) {
                                                val isCurrent = (day == currentStreak)
                                                val isPast = (day < currentStreak)
                                                val isClaimedToday = (isCurrent && claimed)

                                                Card(
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = if (isCurrent) Color(0xFF4F46E5) else if (isPast || isClaimedToday) (if (isLight) Color(0xFFD1FAE5) else Color(0xFF10B981).copy(alpha = 0.2f)) else (if (isLight) Color(0xFFF1F5F9) else Color.White.copy(alpha = 0.08f))
                                                    ),
                                                    shape = RoundedCornerShape(10.dp),
                                                    border = BorderStroke(
                                                        1.dp,
                                                        if (isCurrent) (if (isLight) Color(0xFF4F46E5) else Color.White) else if (isPast) Color(0xFF10B981) else (if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.15f))
                                                    ),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 8.dp, horizontal = 2.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            text = String.format(TextRes.get("day_indicator", viewModel.currentLanguage), day),
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isCurrent) Color.White else (if (isLight) Color(0xFF475569) else Color.White)
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        val iconText = when(day) {
                                                            5 -> "⚡" + TextRes.get("reveal_btn", viewModel.currentLanguage)
                                                            6 -> "🪙110"
                                                            else -> TextRes.get("day_indicator_mega", viewModel.currentLanguage)
                                                        }
                                                        Text(
                                                            text = iconText,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Black,
                                                            color = if (isCurrent) Color.White else (if (isLight) Color(0xFF1E293B) else Color.White)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        if (claimed) {
                                            Text(
                                                text = viewModel.claimMessage,
                                                color = Color(0xFF10B981),
                                                fontWeight = FontWeight.Black,
                                                textAlign = TextAlign.Center,
                                                fontSize = 13.sp,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        } else {
                                            Text(
                                                text = String.format(TextRes.get("day_streak_label", viewModel.currentLanguage), currentStreak),
                                                color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.70f),
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                                            )
                                        }
                                    }
                                },
                                confirmButton = {
                                    if (!claimed) {
                                        Button(
                                            onClick = { viewModel.claimDailyReward() },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(TextRes.get("claim_bonus_btn", viewModel.currentLanguage), color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Button(
                                            onClick = { viewModel.showDailyRewardDialog = false },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(TextRes.get("lets_play_game_btn", viewModel.currentLanguage), color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                },
                                containerColor = if (isLight) Color(0xFFFFFBEB) else Color(0xFF18181B),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }

                        // --- CLASSIC GAME CONFLICT RESOLUTION DIALOG ---
                        if (viewModel.showClassicConfirmDialog) {
                            AlertDialog(
                                onDismissRequest = { viewModel.showClassicConfirmDialog = false },
                                title = {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "🧪 Jogo em Andamento",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isLight) Color(0xFF1E293B) else Color.White
                                        )
                                    }
                                },
                                text = {
                                    Text(
                                        text = "Você já tem uma partida clássica salva de nível " +
                                                "\"${viewModel.activeSavedGame?.difficulty ?: ""}\" em andamento " +
                                                "com ${viewModel.activeSavedGame?.movesCount ?: 0} jogadas.\n\n" +
                                                "Deseja continuar a partida anterior ou iniciar um novo jogo?\n\n" +
                                                "⚠️ Nota: se você optar por iniciar um novo jogo, perderá 1 vida!",
                                        fontSize = 14.sp,
                                        color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.85f),
                                        textAlign = TextAlign.Center
                                    )
                                },
                                confirmButton = {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Button(
                                            onClick = {
                                                viewModel.showClassicConfirmDialog = false
                                                viewModel.resumeSavedProgress()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth().height(48.dp)
                                        ) {
                                            Text("Continuar Partida Salva", color = Color.White, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                viewModel.abandonSavedAndStartNewClassic()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth().height(48.dp)
                                        ) {
                                            Text("Descartar e Iniciar Novo (-1 Vida)", color = Color.White, fontWeight = FontWeight.Bold)
                                        }

                                        TextButton(
                                            onClick = { viewModel.showClassicConfirmDialog = false }
                                        ) {
                                            Text(
                                                text = "Cancelar",
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

                        // --- CUSTOM ANIMATED COMPOSABLE TOASTS ---
                        val toastMsg = viewModel.toastMessage
                        AnimatedVisibility(
                            visible = toastMsg != null,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 72.dp)
                        ) {
                            if (toastMsg != null) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.95f)),
                                    shape = RoundedCornerShape(24.dp),
                                    border = BorderStroke(1.dp, Color(0xFF00ACC1)),
                                    elevation = CardDefaults.cardElevation(8.dp)
                                ) {
                                    Text(
                                        text = toastMsg,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }

                        // --- AD REWARD SIMULATED OVERLAY DIALOG ---
                        if (viewModel.adSimulationVisible) {
                            var progress by remember { mutableStateOf(0f) }
                            LaunchedEffect(Unit) {
                                var elapsed = 0
                                while (elapsed < 30) {
                                    delay(100)
                                    elapsed++
                                    progress = elapsed.toFloat() / 30f
                                }
                                viewModel.completeAdRewardSimulation()
                            }

                            AlertDialog(
                                onDismissRequest = { /* force complete */ },
                                confirmButton = { /* lock progression */ },
                                title = {
                                    Text(
                                        "🎬 Assistindo ao Vídeo...",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                text = {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Aguarde o término do anúncio para receber seu prêmio Enigma Bottles.",
                                            color = Color.White.copy(alpha = 0.75f),
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )
                                        LinearProgressIndicator(
                                            progress = { progress },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = Color(0xFFFF9800),
                                            trackColor = Color.White.copy(alpha = 0.15f)
                                        )
                                    }
                                },
                                containerColor = Color(0xFF1E1E1E),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }

                        // --- INTERSTITIAL COMPULSORY AD OVERLAY ---
                        if (viewModel.interstitialAdVisible) {
                            AlertDialog(
                                onDismissRequest = { 
                                    if (viewModel.interstitialAdCountdown <= 0) {
                                        viewModel.forceCloseInterstitial()
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = { viewModel.forceCloseInterstitial() },
                                        enabled = (viewModel.interstitialAdCountdown <= 0),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF4F46E5),
                                            disabledContainerColor = Color.White.copy(alpha = 0.15f)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (viewModel.interstitialAdCountdown > 0) {
                                                "Aguarde ${viewModel.interstitialAdCountdown}s..."
                                            } else {
                                                "Fechar Anúncio ✕"
                                            },
                                            fontWeight = FontWeight.Bold,
                                            color = if (viewModel.interstitialAdCountdown > 0) Color.White.copy(alpha = 0.5f) else Color.White
                                        )
                                    }
                                },
                                title = {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "📺 ANÚNCIO OBRIGATÓRIO",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFFBBF24)
                                        )
                                        Text(
                                            text = "A cada 5 níveis finalizados! Conclua para prosseguir.",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.6f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                },
                                text = {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF4F46E5).copy(alpha = 0.15f)),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, Color(0xFF4F46E5)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(14.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "👑 ENIGMA BOTTLES PRIME 👑",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color(0xFFFFD700)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Trabalhos, poções e desafios divertidos sem NENHUM anúncio obrigatório!",
                                                    fontSize = 11.sp,
                                                    color = Color.White,
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Button(
                                                    onClick = { 
                                                        viewModel.buyAdFreePlan()
                                                        viewModel.forceCloseInterstitial()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        text = "Comprar Plano Sem Ads - R$ 6,90", 
                                                        fontSize = 10.sp, 
                                                        fontWeight = FontWeight.ExtraBold, 
                                                        color = Color.Black
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            text = "Você apoia nosso estúdio independente assistindo os anúncios!",
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.Center,
                                            color = Color.White.copy(alpha = 0.70f)
                                        )
                                    }
                                },
                                containerColor = Color(0xFF18181B),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
