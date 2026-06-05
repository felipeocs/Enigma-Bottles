package com.enigmabottle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
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
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import com.enigmabottle.data.*
import com.enigmabottle.ui.components.BottleGlassware
import com.enigmabottle.ui.components.GameThemeBackground
import com.enigmabottle.viewmodel.GameViewModel
import com.enigmabottle.viewmodel.Screen

@Composable
fun StoreView(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile by viewModel.userProfile.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0 = Skins, 1 = Backgrounds

    // Google Play Billing simulated states
    var showBillingDialog by remember { mutableStateOf(false) }
    var billingStep by remember { mutableStateOf(0) } // 0 = Confirm Pay, 1 = Processing, 2 = Success
    var simulatedProductId by remember { mutableStateOf("") }

    if (showBillingDialog && billingStep == 1) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(1800)
            billingStep = 2
        }
    }
    if (showBillingDialog && billingStep == 2) {
        LaunchedEffect(simulatedProductId) {
            kotlinx.coroutines.delay(1500)
            when (simulatedProductId) {
                BillingManager.PRODUCT_PACK_HINTS -> viewModel.addHints(10)
                BillingManager.PRODUCT_PACK_XRAY -> viewModel.addXRay(10)
                BillingManager.PRODUCT_PACK_REVEAL -> viewModel.addReveal(10)
                BillingManager.PRODUCT_PACK_FREEZE -> viewModel.addFreeze(10)
                BillingManager.PRODUCT_COINS_500 -> viewModel.addCoins(500)
                BillingManager.PRODUCT_COINS_1000 -> viewModel.addCoins(1000)
                BillingManager.PRODUCT_COINS_5000 -> viewModel.addCoins(5000)
                BillingManager.PRODUCT_COMBO_PACK -> viewModel.addComboPack()
                else -> viewModel.buyAdFreePlan()
            }
            showBillingDialog = false
            billingStep = 0
        }
    }

    val billingManager = viewModel.billingManager

    LaunchedEffect(billingManager) {
        billingManager?.queryPurchases()
        billingManager?.queryProductDetails()
    }

    val productsDetailsMap by if (billingManager != null) {
        billingManager.productsDetailsMapState.collectAsState()
    } else {
        remember { mutableStateOf(emptyMap<String, com.android.billingclient.api.ProductDetails>()) }
    }

    val premiumPrice = productsDetailsMap[BillingManager.PRODUCT_LIFETIME_PREMIUM]
        ?.oneTimePurchaseOfferDetails?.formattedPrice ?: "R$ 9,90"
    
    val hintsPackPrice = productsDetailsMap[BillingManager.PRODUCT_PACK_HINTS]
        ?.oneTimePurchaseOfferDetails?.formattedPrice ?: "R$ 4,90"
        
    val xrayPackPrice = productsDetailsMap[BillingManager.PRODUCT_PACK_XRAY]
        ?.oneTimePurchaseOfferDetails?.formattedPrice ?: "R$ 3,90"

    val revealPackPrice = productsDetailsMap[BillingManager.PRODUCT_PACK_REVEAL]
        ?.oneTimePurchaseOfferDetails?.formattedPrice ?: "R$ 5,90"

    val freezePackPrice = productsDetailsMap[BillingManager.PRODUCT_PACK_FREEZE]
        ?.oneTimePurchaseOfferDetails?.formattedPrice ?: "R$ 2,90"

    val coins500Price = productsDetailsMap[BillingManager.PRODUCT_COINS_500]
        ?.oneTimePurchaseOfferDetails?.formattedPrice ?: "R$ 6,90"
    
    val coins1000Price = productsDetailsMap[BillingManager.PRODUCT_COINS_1000]
        ?.oneTimePurchaseOfferDetails?.formattedPrice ?: "R$ 11,90"

    val coins5000Price = productsDetailsMap[BillingManager.PRODUCT_COINS_5000]
        ?.oneTimePurchaseOfferDetails?.formattedPrice ?: "R$ 49,90"

    val comboPackPrice = productsDetailsMap[BillingManager.PRODUCT_COMBO_PACK]
        ?.oneTimePurchaseOfferDetails?.formattedPrice ?: "R$ 29,90"

    val skinItems = listOf(
        SkinCommodity("classic", TextRes.get("skin_classic_name", viewModel.currentLanguage), TextRes.get("skin_classic_desc", viewModel.currentLanguage), 0, Color(0xFFE53935)),
        SkinCommodity("test_tube", TextRes.get("skin_test_tube_name", viewModel.currentLanguage), TextRes.get("skin_test_tube_desc", viewModel.currentLanguage), 400, Color(0xFF22C55E)),
        SkinCommodity("flask", TextRes.get("skin_flask_name", viewModel.currentLanguage), TextRes.get("skin_flask_desc", viewModel.currentLanguage), 400, Color(0xFF43A047)),
        SkinCommodity("potion", TextRes.get("skin_potion_name", viewModel.currentLanguage), TextRes.get("skin_potion_desc", viewModel.currentLanguage), 400, Color(0xFF9C27B0)),
        SkinCommodity("potion_cork", TextRes.get("skin_potion_cork_name", viewModel.currentLanguage), TextRes.get("skin_potion_cork_desc", viewModel.currentLanguage), 400, Color(0xFFFF7043)),
        SkinCommodity("hourglass", TextRes.get("skin_hourglass_name", viewModel.currentLanguage), TextRes.get("skin_hourglass_desc", viewModel.currentLanguage), 400, Color(0xFFFB923C)),
        SkinCommodity("heart_vial", TextRes.get("skin_heart_vial_name", viewModel.currentLanguage), TextRes.get("skin_heart_vial_desc", viewModel.currentLanguage), 400, Color(0xFFF43F5E)),
        SkinCommodity("neon", TextRes.get("skin_neon_name", viewModel.currentLanguage), TextRes.get("skin_neon_desc", viewModel.currentLanguage), 400, Color(0xFF00ACC1)),
        SkinCommodity("diamond_flask", TextRes.get("skin_diamond_flask_name", viewModel.currentLanguage), TextRes.get("skin_diamond_flask_desc", viewModel.currentLanguage), 400, Color(0xFF38BDF8)),
        SkinCommodity("crystals", TextRes.get("skin_crystals_name", viewModel.currentLanguage), TextRes.get("skin_crystals_desc", viewModel.currentLanguage), 400, Color(0xFFD946EF)),
        SkinCommodity("cauldron_flask", TextRes.get("skin_cauldron_flask_name", viewModel.currentLanguage), TextRes.get("skin_cauldron_flask_desc", viewModel.currentLanguage), 400, Color(0xFF2DD4BF)),
        SkinCommodity("skull", TextRes.get("skin_skull_name", viewModel.currentLanguage), TextRes.get("skin_skull_desc", viewModel.currentLanguage), 400, Color(0xFF64748B)),
        SkinCommodity("royal", TextRes.get("skin_royal_name", viewModel.currentLanguage), TextRes.get("skin_royal_desc", viewModel.currentLanguage), 400, Color(0xFFFFD700)),
        SkinCommodity("prism_flask", TextRes.get("skin_prism_flask_name", viewModel.currentLanguage), TextRes.get("skin_prism_flask_desc", viewModel.currentLanguage), 400, Color(0xFFA855F7))
    )

    val bgItems = listOf(
        BgCommodity("sleek_interface", TextRes.get("bg_sleek_interface_name", viewModel.currentLanguage), TextRes.get("bg_sleek_interface_desc", viewModel.currentLanguage), 0),
        BgCommodity("dark_interface", TextRes.get("bg_dark_interface_name", viewModel.currentLanguage), TextRes.get("bg_dark_interface_desc", viewModel.currentLanguage), 0),
        BgCommodity("clear_aurora", TextRes.get("bg_clear_aurora_name", viewModel.currentLanguage), TextRes.get("bg_clear_aurora_desc", viewModel.currentLanguage), 400),
        BgCommodity("clear_sunset", TextRes.get("bg_clear_sunset_name", viewModel.currentLanguage), TextRes.get("bg_clear_sunset_desc", viewModel.currentLanguage), 400),
        BgCommodity("clear_mint", TextRes.get("bg_clear_mint_name", viewModel.currentLanguage), TextRes.get("bg_clear_mint_desc", viewModel.currentLanguage), 400),
        BgCommodity("clear_lavender", TextRes.get("bg_clear_lavender_name", viewModel.currentLanguage), TextRes.get("bg_clear_lavender_desc", viewModel.currentLanguage), 400),
        BgCommodity("clear_sakura", TextRes.get("bg_clear_sakura_name", viewModel.currentLanguage), TextRes.get("bg_clear_sakura_desc", viewModel.currentLanguage), 400),
        BgCommodity("wood", TextRes.get("bg_wood_name", viewModel.currentLanguage), TextRes.get("bg_wood_desc", viewModel.currentLanguage), 400),
        BgCommodity("lab", TextRes.get("bg_lab_name", viewModel.currentLanguage), TextRes.get("bg_lab_desc", viewModel.currentLanguage), 400),
        BgCommodity("magic", TextRes.get("bg_magic_name", viewModel.currentLanguage), TextRes.get("bg_magic_desc", viewModel.currentLanguage), 400),
        BgCommodity("mystic_swamp", TextRes.get("bg_mystic_swamp_name", viewModel.currentLanguage), TextRes.get("bg_mystic_swamp_desc", viewModel.currentLanguage), 400),
        BgCommodity("cyberpunk", TextRes.get("bg_cyberpunk_name", viewModel.currentLanguage), TextRes.get("bg_cyberpunk_desc", viewModel.currentLanguage), 400),
        BgCommodity("enchanted_forest", TextRes.get("bg_enchanted_forest_name", viewModel.currentLanguage), TextRes.get("bg_enchanted_forest_desc", viewModel.currentLanguage), 400),
        BgCommodity("volcano", TextRes.get("bg_volcano_name", viewModel.currentLanguage), TextRes.get("bg_volcano_desc", viewModel.currentLanguage), 400),
        BgCommodity("abyss", TextRes.get("bg_abyss_name", viewModel.currentLanguage), TextRes.get("bg_abyss_desc", viewModel.currentLanguage), 400),
        BgCommodity("frozen_glacier", TextRes.get("bg_frozen_glacier_name", viewModel.currentLanguage), TextRes.get("bg_frozen_glacier_desc", viewModel.currentLanguage), 400),
        BgCommodity("steampunk", TextRes.get("bg_steampunk_name", viewModel.currentLanguage), TextRes.get("bg_steampunk_desc", viewModel.currentLanguage), 400),
        BgCommodity("neon_grid", TextRes.get("bg_neon_grid_name", viewModel.currentLanguage), TextRes.get("bg_neon_grid_desc", viewModel.currentLanguage), 400),
        BgCommodity("ancient_temple", TextRes.get("bg_ancient_temple_name", viewModel.currentLanguage), TextRes.get("bg_ancient_temple_desc", viewModel.currentLanguage), 400),
        BgCommodity("underwater_reef", TextRes.get("bg_underwater_reef_name", viewModel.currentLanguage), TextRes.get("bg_underwater_reef_desc", viewModel.currentLanguage), 400),
        BgCommodity("retro_arcade", TextRes.get("bg_retro_arcade_name", viewModel.currentLanguage), TextRes.get("bg_retro_arcade_desc", viewModel.currentLanguage), 400),
        BgCommodity("supernova", TextRes.get("bg_supernova_name", viewModel.currentLanguage), TextRes.get("bg_supernova_desc", viewModel.currentLanguage), 400),
        BgCommodity("cosmic", TextRes.get("bg_cosmic_name", viewModel.currentLanguage), TextRes.get("bg_cosmic_desc", viewModel.currentLanguage), 400),
        BgCommodity("starry_night", TextRes.get("bg_starry_night_name", viewModel.currentLanguage), TextRes.get("bg_starry_night_desc", viewModel.currentLanguage), 400)
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = TextRes.get("back", viewModel.currentLanguage),
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
                            contentDescription = TextRes.get("wallet_label", viewModel.currentLanguage),
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
                                                    contentDescription = TextRes.get("cost", viewModel.currentLanguage),
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
                                                "dark_interface" -> Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF020617)))
                                                "clear_aurora" -> Brush.verticalGradient(listOf(Color(0xFFF0FDF4), Color(0xFFFAE8FF)))
                                                "clear_sunset" -> Brush.verticalGradient(listOf(Color(0xFFFFF7ED), Color(0xFFFEF3C7)))
                                                "clear_mint" -> Brush.verticalGradient(listOf(Color(0xFFF0FDFA), Color(0xFFECFDF5)))
                                                "clear_lavender" -> Brush.verticalGradient(listOf(Color(0xFFF5F3FF), Color(0xFFEEF2FF)))
                                                "clear_sakura" -> Brush.verticalGradient(listOf(Color(0xFFFFF5F5), Color(0xFFFFF0F6)))
                                                "wood" -> Brush.verticalGradient(listOf(Color(0xFF332014), Color(0xFF180E08)))
                                                "lab" -> Brush.verticalGradient(listOf(Color(0xFF111E25), Color(0xFF070B0E)))
                                                "magic" -> Brush.radialGradient(listOf(Color(0xFF1D0E3D), Color(0xFF06030F)))
                                                "mystic_swamp" -> Brush.verticalGradient(listOf(Color(0xFF0D1F11), Color(0xFF030A05)))
                                                "cyberpunk" -> Brush.verticalGradient(listOf(Color(0xFF0F0B1E), Color(0xFF020106)))
                                                "enchanted_forest" -> Brush.verticalGradient(listOf(Color(0xFF0A1F0D), Color(0xFF030A04)))
                                                "volcano" -> Brush.verticalGradient(listOf(Color(0xFF1E0B05), Color(0xFF0C0200)))
                                                "abyss" -> Brush.verticalGradient(listOf(Color(0xFF021720), Color(0xFF00080C)))
                                                "frozen_glacier" -> Brush.verticalGradient(listOf(Color(0xFF132F3C), Color(0xFF051119)))
                                                "steampunk" -> Brush.verticalGradient(listOf(Color(0xFF261D15), Color(0xFF100B07)))
                                                "neon_grid" -> Brush.verticalGradient(listOf(Color(0xFF140220), Color(0xFF040008)))
                                                "ancient_temple" -> Brush.verticalGradient(listOf(Color(0xFF1C1D18), Color(0xFF0D0E0B)))
                                                "underwater_reef" -> Brush.verticalGradient(listOf(Color(0xFF003049), Color(0xFF001524)))
                                                "retro_arcade" -> Brush.verticalGradient(listOf(Color(0xFF0C041C), Color(0xFF010005)))
                                                "supernova" -> Brush.verticalGradient(listOf(Color(0xFF3B0D2E), Color(0xFF0F0218)))
                                                "cosmic" -> Brush.radialGradient(listOf(Color(0xFF200C40), Color(0xFF050110)))
                                                "starry_night" -> Brush.verticalGradient(listOf(Color(0xFF0B132B), Color(0xFF010409)))
                                                else -> Brush.verticalGradient(listOf(Color(0xFFFDFBFF), Color(0xFFF5F3FF)))
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
                                                    contentDescription = TextRes.get("cost", viewModel.currentLanguage),
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
                        .verticalScroll(rememberScrollState())
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
                                color = if (isLight) Color(0xFF1E3A8A) else Color.White
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Bullet benefits
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Block, null, tint = if (isLight) Color(0xFF475569) else Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = TextRes.get("premium_benefit_1", viewModel.currentLanguage),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLight) Color(0xFF1E293B) else Color.White
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Bolt, null, tint = Color(0xFF4F46E5), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = TextRes.get("premium_benefit_2", viewModel.currentLanguage),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLight) Color(0xFF1E293B) else Color.White
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MonetizationOn, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
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
                                            contentDescription = TextRes.get("active", viewModel.currentLanguage),
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
                                        val activity = context as? Activity
                                        if (activity != null) {
                                            val success = viewModel.billingManager?.launchBillingFlow(activity, BillingManager.PRODUCT_LIFETIME_PREMIUM)
                                            if (success != true) {
                                                simulatedProductId = BillingManager.PRODUCT_LIFETIME_PREMIUM
                                                billingStep = 0
                                                showBillingDialog = true
                                            }
                                        } else {
                                            simulatedProductId = BillingManager.PRODUCT_LIFETIME_PREMIUM
                                            billingStep = 0
                                            showBillingDialog = true
                                        }
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
                                            text = String.format(TextRes.get("premium_buy_btn", viewModel.currentLanguage), premiumPrice),
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (isLight) Color(0xFF4F46E5) else Color(0xFF818CF8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = TextRes.get("alchemy_resources_title", viewModel.currentLanguage),
                            color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }

                    // Card de 10 Hints
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.10f)
                        ),
                        elevation = CardDefaults.cardElevation(if (isLight) 1.dp else 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(Color.Unspecified.run { androidx.compose.foundation.shape.CircleShape })
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = TextRes.get("hints_pack_title", viewModel.currentLanguage),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLight) Color(0xFF1E293B) else Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = TextRes.get("hints_pack_desc", viewModel.currentLanguage),
                                    fontSize = 11.sp,
                                    color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.6f),
                                    lineHeight = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        val success = viewModel.billingManager?.launchBillingFlow(activity, BillingManager.PRODUCT_PACK_HINTS)
                                        if (success != true) {
                                            simulatedProductId = BillingManager.PRODUCT_PACK_HINTS
                                            billingStep = 0
                                            showBillingDialog = true
                                        }
                                    } else {
                                        simulatedProductId = BillingManager.PRODUCT_PACK_HINTS
                                        billingStep = 0
                                        showBillingDialog = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = hintsPackPrice,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Card de 10 X-Rays
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.10f)
                        ),
                        elevation = CardDefaults.cardElevation(if (isLight) 1.dp else 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(Color.Unspecified.run { androidx.compose.foundation.shape.CircleShape })
                                    .background(Color(0xFF4F46E5).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OfflineBolt,
                                    contentDescription = null,
                                    tint = Color(0xFF4F46E5),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = TextRes.get("xray_pack_title", viewModel.currentLanguage),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLight) Color(0xFF1E293B) else Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = TextRes.get("xray_pack_desc", viewModel.currentLanguage),
                                    fontSize = 11.sp,
                                    color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.6f),
                                    lineHeight = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        val success = viewModel.billingManager?.launchBillingFlow(activity, BillingManager.PRODUCT_PACK_XRAY)
                                        if (success != true) {
                                            simulatedProductId = BillingManager.PRODUCT_PACK_XRAY
                                            billingStep = 0
                                            showBillingDialog = true
                                        }
                                    } else {
                                        simulatedProductId = BillingManager.PRODUCT_PACK_XRAY
                                        billingStep = 0
                                        showBillingDialog = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = xrayPackPrice,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Card de 10 Reveladores
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.10f)
                        ),
                        elevation = CardDefaults.cardElevation(if (isLight) 1.dp else 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF59E0B).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = TextRes.get("reveal_pack_title", viewModel.currentLanguage),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLight) Color(0xFF1E293B) else Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = TextRes.get("reveal_pack_desc", viewModel.currentLanguage),
                                    fontSize = 11.sp,
                                    color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.6f),
                                    lineHeight = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        val success = viewModel.billingManager?.launchBillingFlow(activity, BillingManager.PRODUCT_PACK_REVEAL)
                                        if (success != true) {
                                            simulatedProductId = BillingManager.PRODUCT_PACK_REVEAL
                                            billingStep = 0
                                            showBillingDialog = true
                                        }
                                    } else {
                                        simulatedProductId = BillingManager.PRODUCT_PACK_REVEAL
                                        billingStep = 0
                                        showBillingDialog = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = revealPackPrice,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Card de 10 Congeladores
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.10f)
                        ),
                        elevation = CardDefaults.cardElevation(if (isLight) 1.dp else 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF3B82F6).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AcUnit,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = TextRes.get("freeze_pack_title", viewModel.currentLanguage),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLight) Color(0xFF1E293B) else Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = TextRes.get("freeze_pack_desc", viewModel.currentLanguage),
                                    fontSize = 11.sp,
                                    color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.6f),
                                    lineHeight = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        val success = viewModel.billingManager?.launchBillingFlow(activity, BillingManager.PRODUCT_PACK_FREEZE)
                                        if (success != true) {
                                            simulatedProductId = BillingManager.PRODUCT_PACK_FREEZE
                                            billingStep = 0
                                            showBillingDialog = true
                                        }
                                    } else {
                                        simulatedProductId = BillingManager.PRODUCT_PACK_FREEZE
                                        billingStep = 0
                                        showBillingDialog = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = freezePackPrice,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Seção de moedas
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Icon(Icons.Default.MonetizationOn, null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = TextRes.get("buy_coins_title", viewModel.currentLanguage),
                            color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }

                    // Card de 500 Moedas
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.10f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF59E0B).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.MonetizationOn, null, tint = Color(0xFFFFC107), modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = TextRes.get("coins_500_pack_title", viewModel.currentLanguage),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLight) Color(0xFF1E293B) else Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = TextRes.get("coins_500_pack_desc", viewModel.currentLanguage),
                                    fontSize = 11.sp,
                                    color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.6f)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        val success = viewModel.billingManager?.launchBillingFlow(activity, BillingManager.PRODUCT_COINS_500)
                                        if (success != true) {
                                            simulatedProductId = BillingManager.PRODUCT_COINS_500
                                            billingStep = 0
                                            showBillingDialog = true
                                        }
                                    } else {
                                        simulatedProductId = BillingManager.PRODUCT_COINS_500
                                        billingStep = 0
                                        showBillingDialog = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(text = coins500Price, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    // Card de 1000 Moedas
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.10f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD97706).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.MonetizationOn, null, tint = Color(0xFFFFC107), modifier = Modifier.size(26.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = TextRes.get("coins_1000_pack_title", viewModel.currentLanguage),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLight) Color(0xFF1E293B) else Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = TextRes.get("coins_1000_pack_desc", viewModel.currentLanguage),
                                    fontSize = 11.sp,
                                    color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.6f)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        val success = viewModel.billingManager?.launchBillingFlow(activity, BillingManager.PRODUCT_COINS_1000)
                                        if (success != true) {
                                            simulatedProductId = BillingManager.PRODUCT_COINS_1000
                                            billingStep = 0
                                            showBillingDialog = true
                                        }
                                    } else {
                                        simulatedProductId = BillingManager.PRODUCT_COINS_1000
                                        billingStep = 0
                                        showBillingDialog = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(text = coins1000Price, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    // Card de 5000 Moedas
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLight) Color.White else Color.Black.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.10f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFB45309).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.MonetizationOn, null, tint = Color(0xFFFFC107), modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = TextRes.get("coins_5000_pack_title", viewModel.currentLanguage),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLight) Color(0xFF1E293B) else Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = TextRes.get("coins_5000_pack_desc", viewModel.currentLanguage),
                                    fontSize = 11.sp,
                                    color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.6f)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        val success = viewModel.billingManager?.launchBillingFlow(activity, BillingManager.PRODUCT_COINS_5000)
                                        if (success != true) {
                                            simulatedProductId = BillingManager.PRODUCT_COINS_5000
                                            billingStep = 0
                                            showBillingDialog = true
                                        }
                                    } else {
                                        simulatedProductId = BillingManager.PRODUCT_COINS_5000
                                        billingStep = 0
                                        showBillingDialog = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(text = coins5000Price, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Seção de Ofertas Especiais
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Icon(Icons.Default.Whatshot, null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = TextRes.get("special_offers_title", viewModel.currentLanguage),
                            color = if (isLight) Color(0xFF64748B) else Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }

                    // Combo Alquimista Especial Card (Super Premium com gradiente na borda)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLight) Color(0xFFFDF2F8) else Color(0xFF831843).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(
                            2.dp,
                            Brush.linearGradient(
                                listOf(Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFFFBBF24))
                            )
                        )
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEC4899).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CardGiftcard, null, tint = Color(0xFFEC4899), modifier = Modifier.size(28.dp))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = TextRes.get("combo_pack_title", viewModel.currentLanguage),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isLight) Color(0xFF9D174D) else Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = TextRes.get("combo_pack_desc", viewModel.currentLanguage),
                                        fontSize = 11.sp,
                                        color = if (isLight) Color(0xFF86198F) else Color.White.copy(alpha = 0.7f),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            // Visual horizontal layout showing included items
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isLight) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lightbulb, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                    Text(" x5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isLight) Color.Black else Color.White)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.OfflineBolt, null, tint = Color(0xFF00ACC1), modifier = Modifier.size(16.dp))
                                    Text(" x5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isLight) Color.Black else Color.White)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Visibility, null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                    Text(" x5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isLight) Color.Black else Color.White)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AcUnit, null, tint = Color(0xFF3B82F6), modifier = Modifier.size(16.dp))
                                    Text(" x5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isLight) Color.Black else Color.White)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MonetizationOn, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                                    Text(" +1000", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isLight) Color.Black else Color.White)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            Button(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        val success = viewModel.billingManager?.launchBillingFlow(activity, BillingManager.PRODUCT_COMBO_PACK)
                                        if (success != true) {
                                            simulatedProductId = BillingManager.PRODUCT_COMBO_PACK
                                            billingStep = 0
                                            showBillingDialog = true
                                        }
                                    } else {
                                        simulatedProductId = BillingManager.PRODUCT_COMBO_PACK
                                        billingStep = 0
                                        showBillingDialog = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Text(
                                    text = comboPackPrice,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
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
                                        contentDescription = TextRes.get("cancel_btn", viewModel.currentLanguage),
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
                                        text = when (simulatedProductId) {
                                            BillingManager.PRODUCT_PACK_HINTS -> TextRes.get("hints_pack_title", viewModel.currentLanguage)
                                            BillingManager.PRODUCT_PACK_XRAY -> TextRes.get("xray_pack_title", viewModel.currentLanguage)
                                            BillingManager.PRODUCT_PACK_REVEAL -> TextRes.get("reveal_pack_title", viewModel.currentLanguage)
                                            BillingManager.PRODUCT_PACK_FREEZE -> TextRes.get("freeze_pack_title", viewModel.currentLanguage)
                                            else -> TextRes.get("premium_lifetime_vial", viewModel.currentLanguage)
                                        },
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "com.enigmabottle",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Text(
                                    text = when (simulatedProductId) {
                                        BillingManager.PRODUCT_PACK_HINTS -> hintsPackPrice
                                        BillingManager.PRODUCT_PACK_XRAY -> xrayPackPrice
                                        BillingManager.PRODUCT_PACK_REVEAL -> revealPackPrice
                                        BillingManager.PRODUCT_PACK_FREEZE -> freezePackPrice
                                        else -> premiumPrice
                                    },
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
                                            text = TextRes.get("gplay_balance", viewModel.currentLanguage),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = TextRes.get("gplay_balance_sub", viewModel.currentLanguage),
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                                Text(
                                    text = TextRes.get("equipped", viewModel.currentLanguage),
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
                                    text = TextRes.get("buy_1_click", viewModel.currentLanguage),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = TextRes.get("billing_secure_note", viewModel.currentLanguage),
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
                                text = TextRes.get("processing_payment", viewModel.currentLanguage),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155),
                                fontSize = 14.sp
                            )
                            Text(
                                text = TextRes.get("do_not_close_app", viewModel.currentLanguage),
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        } else if (billingStep == 2) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = TextRes.get("success", viewModel.currentLanguage),
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = TextRes.get("payment_completed", viewModel.currentLanguage),
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A),
                                fontSize = 18.sp
                            )
                            Text(
                                text = TextRes.get("premium_activated_success_desc", viewModel.currentLanguage),
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
