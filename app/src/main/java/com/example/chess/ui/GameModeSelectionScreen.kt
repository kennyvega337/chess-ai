package com.example.chess.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Palette
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.ChessTheme
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.ui.theme.*

@Composable
fun GameModeSelectionScreen(
    onSelectMode: (GameMode) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTheme: () -> Unit,
    gameStatus: GameStatus = GameStatus.NOT_STARTED,
    onReturnToCurrentGame: () -> Unit,
    hasPersistedGame: Boolean = false,
    onLoadPersistedGame: () -> Unit,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = selectedTheme.backgroundColors.map { Color(it) }
                )
            )
            .padding(horizontal = 16.dp, vertical = if (isLandscape) 4.dp else 12.dp)
    ) {
        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Column 1: SetupHeader
                Column(
                    modifier = Modifier
                        .weight(0.75f)
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    SetupHeader(selectedTheme = selectedTheme)
                }

                // Column 2: Selection Box
                Column(
                    modifier = Modifier
                        .weight(1.25f)
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    GameModeSelectionBox(
                        onSelectMode = onSelectMode,
                        onOpenHistory = onOpenHistory,
                        onOpenSettings = onOpenSettings,
                        onOpenTheme = onOpenTheme,
                        gameStatus = gameStatus,
                        onReturnToCurrentGame = onReturnToCurrentGame,
                        hasPersistedGame = hasPersistedGame,
                        onLoadPersistedGame = onLoadPersistedGame,
                        isLandscape = true,
                        selectedTheme = selectedTheme
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                SetupHeader(selectedTheme = selectedTheme)
                
                Spacer(modifier = Modifier.height(24.dp))

                GameModeSelectionBox(
                    onSelectMode = onSelectMode,
                    onOpenHistory = onOpenHistory,
                    onOpenSettings = onOpenSettings,
                    onOpenTheme = onOpenTheme,
                    gameStatus = gameStatus,
                    onReturnToCurrentGame = onReturnToCurrentGame,
                    hasPersistedGame = hasPersistedGame,
                    onLoadPersistedGame = onLoadPersistedGame,
                    isLandscape = false,
                    selectedTheme = selectedTheme
                )
            }
        }
    }
}

@Composable
private fun GameModeSelectionBox(
    onSelectMode: (GameMode) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTheme: () -> Unit,
    gameStatus: GameStatus,
    onReturnToCurrentGame: () -> Unit,
    hasPersistedGame: Boolean,
    onLoadPersistedGame: () -> Unit,
    isLandscape: Boolean,
    selectedTheme: ChessTheme
) {
    val accentColor = Color(selectedTheme.accentColor)
    val borderColor = Color(selectedTheme.borderColor)
    val iconColor = Color(selectedTheme.iconColor)
    val iconActiveColor = Color(selectedTheme.iconActiveColor)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, borderColor, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(selectedTheme.surfaceColor).copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
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
                        tint = iconActiveColor,
                        modifier = Modifier.size(if (isLandscape) 18.dp else 24.dp)
                    )
                    Spacer(modifier = Modifier.width(if (isLandscape) 6.dp else 8.dp))
                    Text(
                        text = "CHỌN CHẾ ĐỘ CHƠI",
                        style = if (isLandscape) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor,
                        maxLines = 1
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenTheme,
                        modifier = Modifier.size(if (isLandscape) 30.dp else 36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Giao diện",
                            tint = iconColor,
                            modifier = Modifier.size(if (isLandscape) 22.dp else 26.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenHistory,
                        modifier = Modifier.size(if (isLandscape) 30.dp else 36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Lịch sử",
                            tint = iconColor,
                            modifier = Modifier.size(if (isLandscape) 22.dp else 26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 20.dp))

            if (isLandscape) {
                // Row 0: Resume
                if (gameStatus == GameStatus.IN_PROGRESS) {
                    ResumeButton(label = "TIẾP TỤC TRẬN ĐẤU", isLandscape = true, onClick = onReturnToCurrentGame, selectedTheme = selectedTheme)
                    Spacer(modifier = Modifier.height(8.dp))
                } else if (hasPersistedGame) {
                    ResumeButton(label = "TIẾP TỤC TRẬN ĐẤU", isLandscape = true, onClick = onLoadPersistedGame, selectedTheme = selectedTheme)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Row 1
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        ModeButton(mode = GameMode.VS_AI, isLandscape = true, onClick = { onSelectMode(GameMode.VS_AI) }, accentColor = accentColor, selectedTheme = selectedTheme)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        ModeButton(mode = GameMode.TWO_PLAYERS, isLandscape = true, onClick = { onSelectMode(GameMode.TWO_PLAYERS) }, accentColor = accentColor, selectedTheme = selectedTheme)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Row 2
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        ModeButton(mode = GameMode.PUZZLE, isLandscape = true, onClick = { onSelectMode(GameMode.PUZZLE) }, accentColor = accentColor, selectedTheme = selectedTheme)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        ModeButton(mode = GameMode.ONE_MOVE, isLandscape = true, onClick = { onSelectMode(GameMode.ONE_MOVE) }, accentColor = accentColor, selectedTheme = selectedTheme)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Row 3
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        ModeButton(mode = GameMode.TUTORIAL, isLandscape = true, onClick = { onSelectMode(GameMode.TUTORIAL) }, accentColor = accentColor, selectedTheme = selectedTheme)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SettingsButton(isLandscape = true, onClick = onOpenSettings, accentColor = accentColor, selectedTheme = selectedTheme)
                    }
                }
            } else {
                if (gameStatus == GameStatus.IN_PROGRESS) {
                    ResumeButton(label = "TIẾP TỤC TRẬN ĐẤU", isLandscape = false, onClick = onReturnToCurrentGame, selectedTheme = selectedTheme)
                    Spacer(modifier = Modifier.height(12.dp))
                } else if (hasPersistedGame) {
                    ResumeButton(label = "TIẾP TỤC TRẬN ĐẤU", isLandscape = false, onClick = onLoadPersistedGame, selectedTheme = selectedTheme)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                GameMode.values().filter { it != GameMode.SPECIAL_MOVE }.forEach { mode ->
                    ModeButton(mode = mode, isLandscape = false, onClick = { onSelectMode(mode) }, accentColor = accentColor, selectedTheme = selectedTheme)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                SettingsButton(isLandscape = false, onClick = onOpenSettings, accentColor = accentColor, selectedTheme = selectedTheme)
            }
        }
    }
}

@Composable
private fun ResumeButton(
    label: String,
    isLandscape: Boolean,
    onClick: () -> Unit,
    selectedTheme: ChessTheme
) {
    val accentColor = Color(selectedTheme.accentColor)
    val surfaceColor = Color(selectedTheme.surfaceColor)
    val textColor = Color(selectedTheme.textColor)
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isLandscape) 40.dp else 60.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(2.dp, accentColor, RoundedCornerShape(12.dp)),
        color = surfaceColor.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isLandscape) 12.dp else 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(if (isLandscape) 28.dp else 32.dp), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(if (isLandscape) 20.dp else 28.dp)
                )
            }
            Spacer(modifier = Modifier.width(if (isLandscape) 8.dp else 16.dp))
            Text(
                text = label,
                color = textColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = if (isLandscape) 13.sp else 17.sp,
                letterSpacing = 1.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ModeButton(
    mode: GameMode,
    isLandscape: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    selectedTheme: ChessTheme
) {
    val borderColor = Color(selectedTheme.borderColor)
    val textColor = Color(selectedTheme.textColor)
    val (label, icon) = when (mode) {
        GameMode.VS_AI -> "ĐẤU VỚI MÁY (AI)" to "⚔️"
        GameMode.TWO_PLAYERS -> "HAI NGƯỜI CHƠI" to "👥"
        GameMode.PUZZLE -> "GIẢI ĐỐ CỜ VUA" to "🧩"
        GameMode.ONE_MOVE -> "THỬ THÁCH 1 NƯỚC" to "🎯"
        GameMode.TUTORIAL -> "HƯỚNG DẪN QUÂN CỜ" to "📖"
        else -> "" to "" // GameMode.SPECIAL_MOVE handled by filter
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isLandscape) 40.dp else 60.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(1.5.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
        color = Color(selectedTheme.surfaceColor).copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isLandscape) 12.dp else 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(if (isLandscape) 28.dp else 32.dp), contentAlignment = Alignment.Center) {
                Text(text = icon, fontSize = if (isLandscape) 16.sp else 24.sp)
            }
            Spacer(modifier = Modifier.width(if (isLandscape) 8.dp else 16.dp))
            Text(
                text = label,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = if (isLandscape) 12.sp else 16.sp,
                letterSpacing = 1.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SettingsButton(isLandscape: Boolean, onClick: () -> Unit, accentColor: Color, selectedTheme: ChessTheme) {
    val borderColor = Color(selectedTheme.borderColor)
    val textColor = Color(selectedTheme.textColor)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isLandscape) 40.dp else 60.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(1.5.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
        color = Color(selectedTheme.surfaceColor).copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isLandscape) 12.dp else 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(if (isLandscape) 28.dp else 32.dp), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(if (isLandscape) 18.dp else 24.dp)
                )
            }
            Spacer(modifier = Modifier.width(if (isLandscape) 8.dp else 16.dp))
            Text(
                text = "CÀI ĐẶT CHUNG",
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = if (isLandscape) 12.sp else 16.sp,
                letterSpacing = 1.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 384, heightDp = 830)
@Composable
fun GameModeSelectionScreenPreview() {
    MyApplicationTheme {
        GameModeSelectionScreen(
            onSelectMode = {},
            onOpenHistory = {},
            onOpenSettings = {},
            onOpenTheme = {},
            gameStatus = GameStatus.NOT_STARTED,
            onReturnToCurrentGame = {},
            hasPersistedGame = false,
            onLoadPersistedGame = {}
        )
    }
}
