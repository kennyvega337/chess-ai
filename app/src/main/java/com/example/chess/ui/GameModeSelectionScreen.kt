package com.example.chess.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.ui.theme.*

@Composable
fun GameModeSelectionScreen(
    onSelectMode: (GameMode) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    gameStatus: GameStatus = GameStatus.NOT_STARTED,
    onReturnToCurrentGame: () -> Unit,
    hasPersistedGame: Boolean = false,
    onLoadPersistedGame: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ColorDarkDeep,
                        MedievalDarkWood,
                        ColorDarkBlack
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SetupHeader()
            
            Spacer(modifier = Modifier.height(30.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(if (isLandscape) 0.6f else 1f)
                    .border(1.5.dp, MedievalGold, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MedievalMidWood.copy(alpha = 0.85f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = MedievalGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CHỌN CHẾ ĐỘ CHƠI",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MedievalGold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onOpenHistory,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "Lịch sử",
                                    tint = MedievalGoldLight,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (gameStatus == GameStatus.IN_PROGRESS) {
                        ResumeButton(
                            label = "TIẾP TỤC TRẬN ĐẤU",
                            onClick = onReturnToCurrentGame
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    } else if (hasPersistedGame) {
                        ResumeButton(
                            label = "KHÔI PHỤC VÁN CỜ",
                            onClick = onLoadPersistedGame
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    GameMode.values().forEach { mode ->
                        ModeButton(
                            mode = mode,
                            onClick = { onSelectMode(mode) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    // Manually add Settings at the bottom
                    SettingsButton(onClick = onOpenSettings)
                }
            }
        }
    }
}

@Composable
private fun ResumeButton(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(2.dp, Color(0xFF22C55E), RoundedCornerShape(12.dp)),
        color = Color(0xFF14532D).copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(32.dp), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color(0xFF22C55E),
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                color = Color(0xFF4ADE80),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun ModeButton(
    mode: GameMode,
    onClick: () -> Unit
) {
    val (label, icon) = when (mode) {
        GameMode.VS_AI -> "ĐẤU VỚI MÁY (AI)" to "⚔️"
        GameMode.TWO_PLAYERS -> "HAI NGƯỜI CHƠI" to "👥"
        GameMode.PUZZLE -> "GIẢI ĐỐ CỜ VUA" to "🧩"
        GameMode.TUTORIAL -> "HƯỚNG DẪN QUÂN CỜ" to "📖"
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(1.5.dp, MedievalGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
        color = Color(0xFF382315).copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(32.dp), contentAlignment = Alignment.Center) {
                Text(text = icon, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                color = MedievalGoldLight,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun SettingsButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(1.5.dp, MedievalGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
        color = Color(0xFF382315).copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(32.dp), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MedievalGoldLight,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "CÀI ĐẶT CHUNG",
                color = MedievalGoldLight,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameModeSelectionScreenPreview() {
    MyApplicationTheme {
        GameModeSelectionScreen(
            onSelectMode = {},
            onOpenHistory = {},
            onOpenSettings = {},
            gameStatus = GameStatus.NOT_STARTED,
            onReturnToCurrentGame = {},
            hasPersistedGame = false,
            onLoadPersistedGame = {}
        )
    }
}
