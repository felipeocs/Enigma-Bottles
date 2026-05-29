package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.BottleGlassware
import com.example.ui.components.GameThemeBackground
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.Screen

@Composable
fun StoreView(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0 = Skins, 1 = Backgrounds

    // Google Play Billing simulated states
    var showBillingDialog by remember { mutableStateOf(false) }
    var billingStep by remember { mutableStateOf(0) } // 0 = Confirm Pay, 1 = Processing, 2 = Success

    if (showBillingDialog && billingStep == 1) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(1800)
            billingStep = 2
        }
    }
    if (showBillingDialog && billingStep == 2) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(1500)
            viewModel.buyAdFreePlan()
            showBillingDialog = false
            billingStep = 0
        }
    }

    val skinItems = listOf(
        SkinCommodity("classic", "Garrafa Clássica", "Vidro tubular clássico com tampa branca.", 0, Color(0xFFE53935)),
        SkinCommodity("test_tube", "Tubo de Ensaio", "Tubo de vidro longo e arredondado tradicional de laboratório.", 120, Color(0xFF22C55E)),
        SkinCommodity("flask", "Erlenmeyer de Química", "Bico estreito de laboratório para cientistas.", 80, Color(0xFF43A047)),
        SkinCommodity("potion", "Frasco de Alquimia", "Redonda medieval para poções místicas.", 150, Color(0xFF9C27B0)),
        SkinCommodity("potion_cork", "Frasco com Rolha", "Oval clássico fechado por rolha rústica de cortiça.", 250, Color(0xFFFF7043)),
        SkinCommodity("neon", "Cápsula Cyber Neon", "Dispositivo futurista com núcleos ultra luminescentes.", 400, Color(0xFF00ACC1)),
        SkinCommodity("crystals", "Gema de Cristal Arcana", "Vidro lapidado em facetas místicas e reluzentes.", 500, Color(0xFFD946EF)),
        SkinCommodity("royal", "Ânfora Real de Ouro", "Frasco deluxe aristocrático banhado a ouro puro.", 650, Color(0xFFFFD700))
    )

    val bgItems = listOf(
        BgCommodity("sleek_interface", "Visual Minimalista", "Tema leve e elegante Sleek Interface com tons de azul e violeta.", 0),
        BgCommodity("clear_aurora", "Aurora Boreal Clara", "Mistura suave de verde menta pastel e violeta celestial.", 60),
        BgCommodity("clear_sunset", "Pôr do Sol Suave", "Degradê acolhedor de pêssego, coral e dourado claro.", 80),
        BgCommodity("clear_mint", "Menta Fresca", "Tema leve de tons verdes refrescantes e azul glacial.", 120),
        BgCommodity("clear_lavender", "Névoa de Lavanda", "Combinação pacífica de roxo lavanda e azul suave.", 150),
        BgCommodity("clear_sakura", "Brisa de Sakura", "Rosa delicado inspirado no desabrochar das cerejeiras.", 180),
        BgCommodity("wood", "Prateleira Rústica", "Madeira aconchegante de taverna clássica de jogos.", 40),
        BgCommodity("lab", "Bancada Química", "Chapa de metal azulada de laboratório de experimentos.", 100),
        BgCommodity("magic", "Santuário de Magia", "Quarto violeta profundo com partículas de poeira cósmica.", 200),
        BgCommodity("abyss", "Abismo Profundo", "Profundezas marinhas azuis escuras com correntes de bolhas térmicas.", 300),
        BgCommodity("neon_grid", "Grelha Cyber Neon", "Grid de laser rosa choque de fliperama dos anos 80.", 350),
        BgCommodity("cosmic", "Nebulosa Cósmica", "Voz do infinito em tons magenta escuro repletos de estrelas brilhantes.", 500)
    )

    val isLight = profile.activeBgId == "sleek_interface" || profile.activeBgId.startsWith("clear_")

    GameThemeBackground(bgId = profile.activeBgId) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
        ) {
            // Store Title Header Panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (isLight) Color(0xFFF1F5F9) else Color.Black.copy(alpha = 0.5f),
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = if (isLight) Color(0xFF475569) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = TextRes.get("shop", viewModel.currentLanguage),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isLight) Color(0xFF1E293B) else Color.White
                    )
                }

                // Wallet indicator styled inside "Sleek" golden container capsule
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isLight) Color(0xFFFFF9DB) else Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, if (isLight) Color(0xFFFFF0B3) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Carteira",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${profile.coins}",
                            color = if (isLight) Color(0xFF92400E) else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.testTag("store_coins_text")
                        )
                    }
                }
            }

            // Tabs Selection controller matching Sleek style navigation capsules
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .background(
                        if (isLight) Color(0xFFF1F5F9) else Color.Black.copy(alpha = 0.4f), 
                        RoundedCornerShape(24.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { selectedTab = 0 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 0) Color(0xFF4F46E5) else Color.Transparent // Indigo active
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Text(
                        text = TextRes.get("skins_tab_title", viewModel.currentLanguage),
                        color = if (selectedTab == 0) Color.White else (if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.6f)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Button(
                    onClick = { selectedTab = 1 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 1) Color(0xFF4F46E5) else Color.Transparent // Indigo active
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Text(
                        text = TextRes.get("bgs_tab_title", viewModel.currentLanguage),
                        color = if (selectedTab == 1) Color.White else (if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.6f)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Button(
                    onClick = { selectedTab = 2 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 2) Color(0xFF4F46E5) else Color.Transparent // Indigo active
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1.3f),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Text(
                        text = TextRes.get("premium_tab_title", viewModel.currentLanguage),
                        color = if (selectedTab == 2) Color.White else (if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.6f)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Products list Grid view
            if (selectedTab == 0) {
                // Bottles Skins Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(skinItems) { skin ->
                        val purchasedSkins = profile.getPurchasedSkins()
                        val isPurchased = purchasedSkins.contains(skin.id)
                        val isActive = profile.activeSkinId == skin.id

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                            ),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isActive) Color(0xFF4F46E5) else (if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.1f))
                            ),
                            elevation = CardDefaults.cardElevation(if (isLight) 1.dp else 0.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Live Vector preview
                                Box(
                                    modifier = Modifier
                                        .size(48.dp, 75.dp)
                                        .padding(bottom = 8.dp)
                                ) {
                                    BottleGlassware(
                                        liquidColor = skin.previewColor,
                                        skinId = skin.id,
                                        isSelected = false,
                                        isHintFlag = false,
                                        isLight = isLight
                                    )
                                }

                                Text(
                                    text = TextRes.get("skin_${skin.id}_name", viewModel.currentLanguage),
                                    color = if (isLight) Color(0xFF1E293B) else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = TextRes.get("skin_${skin.id}_desc", viewModel.currentLanguage),
                                    color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.5f),
                                    fontSize = 10.sp,
                                    maxLines = 2,
                                    lineHeight = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp).height(24.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Transaction multi-action trigger button
                                Button(
                                    onClick = { viewModel.buySkin(skin.id, skin.cost) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = when {
                                            isActive -> Color(0xFF10B981).copy(alpha = 0.15f)
                                            isPurchased -> if (isLight) Color(0xFFF1F5F9) else Color(0xFF334155)
                                            else -> Color(0xFFFBBF24) // Amber 400
                                        }
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp)
                                        .testTag("buy_skin_${skin.id}"),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    when {
                                        isActive -> {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = TextRes.get("equipped", viewModel.currentLanguage),
                                                    tint = Color(0xFF10B981),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(TextRes.get("equipped", viewModel.currentLanguage), color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        isPurchased -> {
                                            Text(
                                                text = TextRes.get("equip", viewModel.currentLanguage), 
                                                color = if (isLight) Color(0xFF1E293B) else Color.White, 
                                                fontSize = 11.sp, 
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        else -> {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.MonetizationOn,
                                                    contentDescription = "Preco",
                                                    tint = Color(0xFF78350F),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text("${skin.cost}", color = Color(0xFF78350F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (selectedTab == 1) {
                // Background Themes Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(bgItems) { bg ->
                        val purchasedBgs = profile.getPurchasedBgs()
                        val isPurchased = purchasedBgs.contains(bg.id)
                        val isActive = profile.activeBgId == bg.id

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                            ),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isActive) Color(0xFF4F46E5) else (if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.1f))
                            ),
                            elevation = CardDefaults.cardElevation(if (isLight) 1.dp else 0.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Miniature abstract drawing preview of the background gradient/aspect ratio
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(55.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when (bg.id) {
                                                "sleek_interface" -> Brush.verticalGradient(listOf(Color(0xFFFDFBFF), Color(0xFFF5F3FF)))
                                                "clear_aurora" -> Brush.verticalGradient(listOf(Color(0xFFF0FDF4), Color(0xFFFAE8FF)))
                                                "clear_sunset" -> Brush.verticalGradient(listOf(Color(0xFFFFF7ED), Color(0xFFFEF3C7)))
                                                "clear_mint" -> Brush.verticalGradient(listOf(Color(0xFFF0FDFA), Color(0xFFECFDF5)))
                                                "clear_lavender" -> Brush.verticalGradient(listOf(Color(0xFFF5F3FF), Color(0xFFEEF2FF)))
                                                "clear_sakura" -> Brush.verticalGradient(listOf(Color(0xFFFFF5F5), Color(0xFFFFF0F6)))
                                                "lab" -> Brush.verticalGradient(listOf(Color(0xFF111E25), Color(0xFF070B0E)))
                                                "magic" -> Brush.radialGradient(listOf(Color(0xFF1D0E3D), Color(0xFF06030F)))
                                                "neon_grid" -> Brush.verticalGradient(listOf(Color(0xFF140220), Color(0xFF040008)))
                                                "abyss" -> Brush.verticalGradient(listOf(Color(0xFF021720), Color(0xFF00080C)))
                                                "cosmic" -> Brush.radialGradient(listOf(Color(0xFF200C40), Color(0xFF050110)))
                                                else -> Brush.verticalGradient(listOf(Color(0xFF332014), Color(0xFF180E08)))
                                            }
                                        )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = TextRes.get("bg_${bg.id}_name", viewModel.currentLanguage),
                                    color = if (isLight) Color(0xFF1E293B) else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = TextRes.get("bg_${bg.id}_desc", viewModel.currentLanguage),
                                    color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.5f),
                                    fontSize = 10.sp,
                                    maxLines = 2,
                                    lineHeight = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp).height(24.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { viewModel.buyBgTheme(bg.id, bg.cost) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = when {
                                            isActive -> Color(0xFF10B981).copy(alpha = 0.15f)
                                            isPurchased -> if (isLight) Color(0xFFF1F5F9) else Color(0xFF334155)
                                            else -> Color(0xFFFBBF24) // Amber 400
                                        }
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp)
                                        .testTag("buy_bg_${bg.id}"),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    when {
                                        isActive -> {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = TextRes.get("equipped", viewModel.currentLanguage),
                                                    tint = Color(0xFF10B981),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(TextRes.get("equipped", viewModel.currentLanguage), color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        isPurchased -> {
                                            Text(
                                                text = TextRes.get("equip", viewModel.currentLanguage), 
                                                color = if (isLight) Color(0xFF1E293B) else Color.White, 
                                                fontSize = 11.sp, 
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        else -> {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.MonetizationOn,
                                                    contentDescription = "Custo",
                                                    tint = Color(0xFF78350F),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text("${bg.cost}", color = Color(0xFF78350F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (selectedTab == 2) {
                // Premium & Rewards Tab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Lifetime Ad-Free Plan Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLight) Color(0xFFEFF6FF) else Color(0xFF1E293B).copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(
                            2.dp,
                            Brush.linearGradient(
                                listOf(Color(0xFF8B5CF6), Color(0xFF3B82F6), Color(0xFFEC4899))
                            )
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = TextRes.get("premium_lifetime_title", viewModel.currentLanguage),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isLight) Color(0xFF1E3A8A) else Color(0xFFFFD700)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Bullet benefits
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🚫 ", fontSize = 14.sp)
                                    Text(
                                        text = TextRes.get("premium_benefit_1", viewModel.currentLanguage),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLight) Color(0xFF1E293B) else Color.White
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⚡ ", fontSize = 14.sp)
                                    Text(
                                        text = TextRes.get("premium_benefit_2", viewModel.currentLanguage),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLight) Color(0xFF1E293B) else Color.White
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🪙 ", fontSize = 14.sp)
                                    Text(
                                        text = TextRes.get("premium_benefit_3", viewModel.currentLanguage),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLight) Color(0xFF1E293B) else Color.White
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            val isAdFree = profile.isAdFree
                            if (isAdFree) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.2f)),
                                    border = BorderStroke(1.dp, Color(0xFF10B981)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Ativo",
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = TextRes.get("premium_activated_badge", viewModel.currentLanguage),
                                            color = Color(0xFF10B981),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                
                                val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
                                val isInfiniteActive = profile.infiniteLivesEndTime > System.currentTimeMillis()
                                val alreadyActivatedToday = profile.lastInfiniteLivesActivationDate == todayStr
                                
                                if (isInfiniteActive) {
                                    val remainingMin = ((profile.infiniteLivesEndTime - System.currentTimeMillis()) / 1000 / 60).coerceAtLeast(0)
                                    val remainingSec = (((profile.infiniteLivesEndTime - System.currentTimeMillis()) / 1000) % 60).coerceAtLeast(0)
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEC4899).copy(alpha = 0.15f)),
                                        border = BorderStroke(1.dp, Color(0xFFEC4899)),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth(0.9f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = TextRes.get("premium_infinite_lives_active", viewModel.currentLanguage),
                                                fontWeight = FontWeight.Black,
                                                fontSize = 12.sp,
                                                color = Color(0xFFEC4899)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = String.format(TextRes.get("premium_time_remaining", viewModel.currentLanguage), "${remainingMin}m ${remainingSec}s"),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = if (isLight) Color(0xFF1E293B) else Color.White
                                            )
                                        }
                                    }
                                } else if (alreadyActivatedToday) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = if (isLight) Color(0xFFE2E8F0) else Color(0xFF334155)),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth(0.9f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = TextRes.get("premium_already_activated", viewModel.currentLanguage),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 11.sp,
                                                color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.6f)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = TextRes.get("premium_come_back_tomorrow", viewModel.currentLanguage),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = if (isLight) Color(0xFF1E293B) else Color.White
                                            )
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { viewModel.activateInfiniteLives() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth(0.9f)
                                    ) {
                                        Text(
                                            text = TextRes.get("premium_activate_btn", viewModel.currentLanguage),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { 
                                        billingStep = 0
                                        showBillingDialog = true 
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = TextRes.get("premium_buy_btn", viewModel.currentLanguage),
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- GOOGLE PLAY BILLING SIMULATED DIALOG ---
        if (showBillingDialog) {
            Dialog(onDismissRequest = { if (billingStep == 0) showBillingDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (billingStep == 0) {
                            // Header with GPlay icon indicator
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF01875F)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.OfflineBolt,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Google Play",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF01875F)
                                    )
                                }
                                IconButton(onClick = { showBillingDialog = false }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = Color(0xFF64748B)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Product Details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Enigma Bottles - Premium Vitalício",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "com.aistudio.enigmabottles",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Text(
                                    text = "R$ 9,90",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF01875F),
                                    textAlign = TextAlign.Right
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE2E8F0)))
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Payment Method selector (Google Play Balance / GPay)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = Color(0xFF4F46E5),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Google Play Balance",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = "Saldo disponível: R$ 25,00",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                                Text(
                                    text = "Ativo",
                                    color = Color(0xFF01875F),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            // 1-Tap Purchase GPay button with beautiful emerald green color
                            Button(
                                onClick = { billingStep = 1 },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF01875F)),
                                shape = RoundedCornerShape(28.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 12.dp)
                            ) {
                                Text(
                                    text = "COMPRAR EM 1 CLIQUE",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Pagamento processado de forma segura pelo Google Play Billing",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8),
                                textAlign = TextAlign.Center
                            )
                        } else if (billingStep == 1) {
                            Spacer(modifier = Modifier.height(24.dp))
                            CircularProgressIndicator(
                                color = Color(0xFF01875F),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Processando pagamento...",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Por favor, não feche o aplicativo.",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        } else if (billingStep == 2) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Sucesso",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Pagamento Concluído!",
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A),
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Seu Plano Premium Vitalício foi ativado com sucesso!",
                                fontSize = 12.sp,
                                color = Color(0xFF475569),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

data class SkinCommodity(
    val id: String,
    val name: String,
    val description: String,
    val cost: Int,
    val previewColor: Color
)

data class BgCommodity(
    val id: String,
    val name: String,
    val description: String,
    val cost: Int
)
