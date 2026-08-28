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
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.chess.model.GameMode
import com.example.chess.model.ChessTheme
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun GameHistoryDialog(
    onDismiss: () -> Unit,
    selectedTheme: ChessTheme? = null
) {
    val context = LocalContext.current
    val historyManager = remember { GameHistoryManager(context) }
    var historyList by remember { mutableStateOf(historyManager.getHistoryList()) }

    GameHistoryDialogContent(
        historyList = historyList,
        onDismiss = onDismiss,
        onClearHistory = {
            historyManager.clearHistory()
            historyList = emptyList()
        },
        selectedTheme = selectedTheme
    )
}

@Composable
fun GameHistoryDialogContent(
    historyList: List<GameHistoryItem>,
    onDismiss: () -> Unit,
    onClearHistory: () -> Unit,
    selectedTheme: ChessTheme? = null
) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val accentColor = selectedTheme?.let { Color(it.accentColor) } ?: MedievalGold
    val textColor = selectedTheme?.let { Color(it.textColor) } ?: MedievalGoldLight
    val surfaceColor = selectedTheme?.let { Color(it.surfaceColor) } ?: MedievalGold
    val buttonColor = selectedTheme?.let { Color(it.buttonColor) } ?: surfaceColor
    val useDarkIcons = selectedTheme?.let { isThemeLight(it) } ?: false
    val statusBarColorInt = selectedTheme?.backgroundColors?.first()?.toInt() ?: android.graphics.Color.TRANSPARENT
    val btnColor =  selectedTheme?.let { Color(it.lightSquareColor) } ?: Color.White;


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        HideSystemBarsInDialog(useDarkIcons, statusBarColorInt)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (isLandscape) 0.7f else 0.88f)
                    .fillMaxHeight(0.85f)
                    .clickable(indication = null, interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }) {},
                contentAlignment = Alignment.TopEnd
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, accentColor, RoundedCornerShape(16.dp))
                        .testTag("game_history_dialog"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(surfaceColor)
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
                                tint = accentColor,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "📜 LỊCH SỬ ĐẤU",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
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
                                        tint = accentColor.copy(alpha = 0.4f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Chưa có lịch sử ván đấu nào",
                                        fontSize = 14.sp,
                                        color = textColor.copy(alpha = 0.7f),
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
                                    HistoryCardItem(item, selectedTheme)
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
                                    onClick = onClearHistory,
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
                                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                            ) {
                                Text(
                                    text = "Đóng",
                                    color = if (selectedTheme != null) Color(selectedTheme.onAccentColor) else ColorDarkDeep,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Close Button (X)
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .offset(x = 5.dp, y = (-5).dp)
                        .size(24.dp)
                        .background(btnColor, CircleShape)
                            .border(2.dp, accentColor, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Đóng",
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun HistoryCardItem(item: GameHistoryItem, selectedTheme: ChessTheme? = null) {
    val modeTag = if (item.gameMode == GameMode.VS_AI) "Đấu máy" else "2 người"
    val isWin = item.text.contains("thắng")
    val isLoss = item.text.contains("thua")
    val isQuit = item.text.contains("bỏ cuộc")

    val accentColor = selectedTheme?.let { Color(it.accentColor) } ?: MedievalGold
    val textColor = selectedTheme?.let { Color(it.textColor) } ?: MedievalGoldLight
    val surfaceColor = selectedTheme?.let { Color(it.surfaceColor) } ?: Color.Black.copy(alpha = 0.3f)

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
        color = surfaceColor,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.2f))
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
                        color = accentColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = modeTag,
                            fontSize = 10.sp,
                            color = textColor.copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
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

@Preview(showBackground = true)
@Composable
fun GameHistoryDialogPreview() {
    val sampleHistory = remember {
        listOf(
            GameHistoryItem(
                id = "1",
                dateFormatted = "24/05/2024 14:30",
                gameMode = GameMode.VS_AI,
                text = "Bạn thắng Máy (Dễ)"
            ),
            GameHistoryItem(
                id = "2",
                dateFormatted = "23/05/2024 10:15",
                gameMode = GameMode.TWO_PLAYERS,
                text = "Trắng thắng Đen"
            ),
            GameHistoryItem(
                id = "3",
                dateFormatted = "22/05/2024 20:00",
                gameMode = GameMode.VS_AI,
                text = "Máy (Khó) thắng Bạn"
            )
        )
    }

    MyApplicationTheme(selectedTheme = ChessTheme.CLASSIC) {
        GameHistoryDialogContent(
            historyList = sampleHistory,
            onDismiss = {},
            onClearHistory = {},
            selectedTheme = ChessTheme.CLASSIC
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameHistoryDialogEmptyPreview() {
    MyApplicationTheme(selectedTheme = ChessTheme.CLASSIC) {
        GameHistoryDialogContent(
            historyList = emptyList(),
            onDismiss = {},
            onClearHistory = {},
            selectedTheme = ChessTheme.CLASSIC
        )
    }
}
