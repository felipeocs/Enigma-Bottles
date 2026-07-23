package com.enigmabottle.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.enigmabottle.data.TextRes

@Composable
fun TutorialDialog(
    currentLanguage: String,
    isLight: Boolean,
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    
    val steps = listOf(
        Pair(Icons.Default.SwapHoriz, "tutorial_step1"),
        Pair(Icons.Default.CheckCircle, "tutorial_step2"),
        Pair(Icons.Default.AutoAwesome, "tutorial_step3")
    )
    
    val currentPair = steps[currentStep]

    Dialog(
        onDismissRequest = { /* Require explicit finish */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isLight) Color(0xFFF8FAFC) else Color(0xFF18181B)
            ),
            border = BorderStroke(1.dp, if (isLight) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.15f)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = TextRes.get("tutorial_title", currentLanguage),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isLight) Color(0xFF1E293B) else Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                Icon(
                    imageVector = currentPair.first,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp).padding(bottom = 16.dp),
                    tint = if (isLight) Color(0xFF4F46E5) else Color(0xFF818CF8)
                )
                
                Text(
                    text = TextRes.get(currentPair.second, currentLanguage),
                    fontSize = 16.sp,
                    color = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp).heightIn(min = 60.dp)
                )
                
                // Dots indicator
                Row(
                    modifier = Modifier.padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in steps.indices) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (i == currentStep) {
                                        if (isLight) Color(0xFF4F46E5) else Color(0xFF818CF8)
                                    } else {
                                        if (isLight) Color(0xFFCBD5E1) else Color(0xFF3F3F46)
                                    }
                                )
                        )
                    }
                }
                
                Button(
                    onClick = {
                        if (currentStep < steps.size - 1) {
                            currentStep++
                        } else {
                            onComplete()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLight) Color(0xFF4F46E5) else Color(0xFF6366F1)
                    )
                ) {
                    val btnText = if (currentStep < steps.size - 1) {
                        TextRes.get("tutorial_btn_next", currentLanguage)
                    } else {
                        TextRes.get("tutorial_btn_finish", currentLanguage)
                    }
                    
                    Text(
                        text = btnText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
