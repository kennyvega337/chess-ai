package com.example.chess.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.chess.data.GameHistoryItem
import com.example.chess.data.GameHistoryManager
import com.example.chess.model.GameMode
import com.example.ui.theme.*

@Composable
fun GameHistoryDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val historyManager = remember { GameHistoryManager(context) }
    var historyList by remember { mutableStateOf(historyManager.getHistoryList()) }
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false, 
            decorFitsSystemWindows = false
        )
    ) {
        HideSystemBarsInDialog()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (isLandscape) 0.7f else 0.88f) // Reduced slightly to ensure overflow stays on screen
                    .fillMaxHeight(0.85f)
                    .wrapContentHeight(),
                contentAlignment = Alignment.TopEnd
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, MedievalGold, RoundedCornerShape(16.dp))
                        .testTag("game_history_dialog"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ColorDarkDeep)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = MedievalGold,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "📜 LỊCH SỬ ĐẤU",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedievalGoldLight
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (historyList.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.SportsEsports,
                                        contentDescription = null,
                                        tint = Color(0x66D4AF37),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Chưa có lịch sử ván đấu nào",
                                        fontSize = 14.sp,
                                        color = MedievalParchment,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(historyList, key = { it.id }) { item ->
                                    HistoryCardItem(item)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bottom Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (historyList.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = {
                                        historyManager.clearHistory()
                                        historyList = emptyList()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MedievalCrimson),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MedievalCrimson)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Xóa",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Xóa lịch sử", fontSize = 13.sp)
                                }
                            }

                            Button(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MedievalGold)
                            ) {
                                Text("Đóng", color = ColorDarkDeep, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Overflowing Close Button (X) - Stuck to corner
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .offset(x = 10.dp, y = (-10).dp)
                        .size(34.dp)
                        .background(ColorWoodMid, CircleShape)
                        .border(2.dp, MedievalGold, CircleShape)
                        .shadow(4.dp, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Đóng",
                        tint = MedievalGoldLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryCardItem(item: GameHistoryItem) {
    val modeTag = if (item.gameMode == GameMode.VS_AI) "Đấu máy" else "2 người"
    val isWin = item.text.contains("thắng")
    val isLoss = item.text.contains("thua")
    val isQuit = item.text.contains("bỏ cuộc")

    val badgeBg = when {
        isWin -> ColorEmeraldDark
        isLoss -> ColorCrimsonMuted
        isQuit -> ColorGoldBrown
        else -> ColorRoyalBlue
    }

    val badgeText = when {
        isWin -> "THẮNG"
        isLoss -> "THUA"
        isQuit -> "BỎ CUỘC"
        else -> "HÒA"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ColorWoodWarm,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33D4AF37))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.dateFormatted,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MedievalGoldLight
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = ColorWoodRich,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = modeTag,
                            fontSize = 10.sp,
                            color = MedievalParchment,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedievalParchment
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                color = badgeBg,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
