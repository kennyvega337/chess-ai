package com.example.chess.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.People
import com.example.chess.model.DifficultyLevel
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.chess.model.PieceType
import com.example.chess.model.SideOption
import com.example.ui.theme.MedievalDarkWood
import com.example.ui.theme.MedievalGold
import com.example.ui.theme.MedievalGoldLight
import com.example.ui.theme.MedievalMidWood
import com.example.ui.theme.MedievalParchment
import com.example.ui.theme.MedievalParchmentDark

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.platform.LocalConfiguration

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.IconButton
@Composable
fun GameSetupScreen(
    initialSideOption: SideOption = SideOption.WHITE,
    initialDifficulty: DifficultyLevel = DifficultyLevel.MEDIUM,
    initialGameMode: GameMode = GameMode.VS_AI,
    gameStatus: GameStatus = GameStatus.NOT_STARTED,
    onStartGame: (SideOption, DifficultyLevel, GameMode) -> Unit,
    onStartTutorialPiece: ((PieceType) -> Unit)? = null,
    onReturnToCurrentGame: (() -> Unit)? = null,
    onOpenHistory: (() -> Unit)? = null
) {
    var selectedSide by rememberSaveable { mutableStateOf(initialSideOption) }
    var selectedDifficulty by rememberSaveable { mutableStateOf(initialDifficulty) }
    var selectedGameMode by rememberSaveable { mutableStateOf(initialGameMode) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1D0E06),
                        MedievalDarkWood,
                        Color(0xFF140A05)
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("game_setup_screen")
    ) {
        if (isLandscape) {
            // === LANDSCAPE LAYOUT ===
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // LEFT SIDE COLUMN
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SetupHeader()

                        Spacer(modifier = Modifier.height(12.dp))

                        if (gameStatus == GameStatus.IN_PROGRESS && onReturnToCurrentGame != null) {
                            ResumeGameButton(onReturnToCurrentGame)
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        MatchPreviewCard(selectedSide, selectedDifficulty, selectedGameMode)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    StartGameButton(
                        gameMode = selectedGameMode,
                        onClick = {
                            if (selectedGameMode == GameMode.TUTORIAL) {
                                onStartTutorialPiece?.invoke(PieceType.ROOK)
                            } else {
                                onStartGame(selectedSide, selectedDifficulty, selectedGameMode)
                            }
                        }
                    )
                }

                // RIGHT SIDE COLUMN
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GameModeSelectionCard(
                        selectedGameMode = selectedGameMode,
                        onSelectGameMode = { selectedGameMode = it },
                        onOpenHistory = onOpenHistory
                    )

                    when (selectedGameMode) {
                        GameMode.VS_AI -> {
                            DifficultySectionCard(
                                selectedDifficulty = selectedDifficulty,
                                onSelectDifficulty = { selectedDifficulty = it }
                            )

                            SideSelectionCard(
                                selectedSide = selectedSide,
                                onSelectSide = { selectedSide = it }
                            )
                        }
                        GameMode.TWO_PLAYERS -> {
                            TwoPlayersInfoCard()
                        }
                        GameMode.TUTORIAL -> {
                            TutorialPieceSelectionCard(
                                onSelectPiece = { pieceType ->
                                    onStartTutorialPiece?.invoke(pieceType)
                                }
                            )
                        }
                    }
                }
            }
        } else {
            // === PORTRAIT LAYOUT ===
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    SetupHeader()

                    Spacer(modifier = Modifier.height(16.dp))

                    if (gameStatus == GameStatus.IN_PROGRESS && onReturnToCurrentGame != null) {
                        ResumeGameButton(onReturnToCurrentGame)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    GameModeSelectionCard(
                        selectedGameMode = selectedGameMode,
                        onSelectGameMode = { selectedGameMode = it },
                        onOpenHistory = onOpenHistory
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    when (selectedGameMode) {
                        GameMode.VS_AI -> {
                            DifficultySectionCard(
                                selectedDifficulty = selectedDifficulty,
                                onSelectDifficulty = { selectedDifficulty = it }
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            SideSelectionCard(
                                selectedSide = selectedSide,
                                onSelectSide = { selectedSide = it }
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                        }
                        GameMode.TWO_PLAYERS -> {
                            TwoPlayersInfoCard()

                            Spacer(modifier = Modifier.height(14.dp))
                        }
                        GameMode.TUTORIAL -> {
                            TutorialPieceSelectionCard(
                                onSelectPiece = { pieceType ->
                                    onStartTutorialPiece?.invoke(pieceType)
                                }
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    MatchPreviewCard(selectedSide, selectedDifficulty, selectedGameMode)

                    Spacer(modifier = Modifier.height(20.dp))
                }

                StartGameButton(
                    gameMode = selectedGameMode,
                    onClick = {
                        if (selectedGameMode == GameMode.TUTORIAL) {
                            onStartTutorialPiece?.invoke(PieceType.ROOK)
                        } else {
                            onStartGame(selectedSide, selectedDifficulty, selectedGameMode)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SetupHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(MedievalGold.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
                .border(2.dp, MedievalGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = MedievalGold,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "⚔️ CỜ VUA TRUNG CỔ ⚔️",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MedievalGoldLight,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "THIẾT LẬP TRẬN ĐẤU",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MedievalGold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, MedievalGold, Color.Transparent)
                    )
                )
        )
    }
}

@Composable
private fun ResumeGameButton(onReturnToCurrentGame: () -> Unit) {
    OutlinedButton(
        onClick = onReturnToCurrentGame,
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag("resume_game_button"),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF22C55E)),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0x2222C55E))
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color(0xFF22C55E),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "▶ TIẾP TỤC TRẬN ĐẤU ĐANG CHƠI",
            color = Color(0xFF22C55E),
            fontWeight = FontWeight.Bold,
            fontSize = 12.5.sp
        )
    }
}

@Composable
private fun DifficultySectionCard(
    selectedDifficulty: DifficultyLevel,
    onSelectDifficulty: (DifficultyLevel) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MedievalGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MedievalMidWood.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = MedievalGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "1. CẤP ĐỘ ĐỐI THỦ (MÁY AI)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MedievalGold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DifficultyLevel.values().forEach { level ->
                    val isSelected = selectedDifficulty == level
                    val activeColor = when (level) {
                        DifficultyLevel.EASY -> Color(0xFF22C55E)
                        DifficultyLevel.MEDIUM -> MedievalGold
                        DifficultyLevel.HARD -> Color(0xFFEF4444)
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectDifficulty(level) }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) activeColor else Color(0x44D4AF37),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .testTag("setup_difficulty_${level.name.lowercase()}"),
                        color = if (isSelected) activeColor.copy(alpha = 0.25f) else Color(0xFF22140A)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = level.displayNameVi,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) activeColor else MedievalParchment
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = when (level) {
                                    DifficultyLevel.EASY -> "Tập chơi"
                                    DifficultyLevel.MEDIUM -> "Cân bằng"
                                    DifficultyLevel.HARD -> "⚡ Tính 5+ nước"
                                },
                                fontSize = 10.sp,
                                fontWeight = if (level == DifficultyLevel.HARD) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected && level == DifficultyLevel.HARD) Color(0xFFFCA5A5) else MedievalParchmentDark,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SideSelectionCard(
    selectedSide: SideOption,
    onSelectSide: (SideOption) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MedievalGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MedievalMidWood.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MedievalGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "2. CHỌN PHE QUÂN CỦA BẠN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MedievalGold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SideOption.values().forEach { option ->
                val isSelected = selectedSide == option
                val activeBorderColor = if (isSelected) MedievalGold else Color(0x44D4AF37)
                val activeBgColor = if (isSelected) MedievalGold.copy(alpha = 0.2f) else Color(0xFF22140A)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectSide(option) }
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = activeBorderColor,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .testTag("setup_side_${option.name.lowercase()}"),
                    color = activeBgColor
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (option) {
                                            SideOption.WHITE -> Color(0xFFF7F4EB)
                                            SideOption.BLACK -> Color(0xFF1E130B)
                                            SideOption.RANDOM -> Color(0xFF4A3525)
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        if (option == SideOption.WHITE) Color(0xFF1E130B) else MedievalGold,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option.iconSymbol,
                                    fontSize = 18.sp,
                                    color = when (option) {
                                        SideOption.WHITE -> Color(0xFF1E130B)
                                        SideOption.BLACK -> MedievalGoldLight
                                        SideOption.RANDOM -> MedievalGold
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = option.displayNameVi,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MedievalGoldLight else MedievalParchment,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = option.subtitleVi,
                                    fontSize = 10.5.sp,
                                    color = MedievalParchmentDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MedievalGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameModeSelectionCard(
    selectedGameMode: GameMode,
    onSelectGameMode: (GameMode) -> Unit,
    onOpenHistory: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, MedievalGold, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MedievalMidWood.copy(alpha = 0.85f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
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
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CHẾ ĐỘ CHƠI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MedievalGold
                    )
                }

                if (onOpenHistory != null && selectedGameMode != GameMode.TUTORIAL) {
                    IconButton(
                        onClick = onOpenHistory,
                        modifier = Modifier.size(32.dp).testTag("history_icon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Xem lịch sử đấu",
                            tint = MedievalGoldLight,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GameMode.values().forEach { mode ->
                    val isSelected = selectedGameMode == mode
                    val activeColor = MedievalGold

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectGameMode(mode) }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) activeColor else Color(0x44D4AF37),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .testTag("setup_mode_${mode.name.lowercase()}"),
                        color = if (isSelected) activeColor.copy(alpha = 0.25f) else Color(0xFF22140A)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = when (mode) {
                                    GameMode.VS_AI -> "⚔️ Đấu Máy"
                                    GameMode.TWO_PLAYERS -> "👥 2 Người"
                                    GameMode.TUTORIAL -> "📖 Hướng Dẫn"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) MedievalGoldLight else MedievalParchment,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = when (mode) {
                                    GameMode.VS_AI -> "Chơi với Máy"
                                    GameMode.TWO_PLAYERS -> "Cùng 1 máy"
                                    GameMode.TUTORIAL -> "Học 6 quân cờ"
                                },
                                fontSize = 9.5.sp,
                                color = MedievalParchmentDark,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TwoPlayersInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MedievalGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MedievalMidWood.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = null,
                    tint = MedievalGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CHẾ ĐỘ 2 NGƯỜI CHƠI TRÊN 1 MÁY",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MedievalGold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                color = Color(0xFF1D1109),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33D4AF37))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "👥 Cùng đấu cờ vua trực tiếp trên một thiết bị:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedievalGoldLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Người chơi 1: Cầm quân Trắng (♔) - Đi trước\n• Người chơi 2: Cầm quân Đen (♚) - Đi sau\n• Hoàn toàn không có sự can thiệp của máy AI.",
                        fontSize = 11.5.sp,
                        color = MedievalParchment,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TutorialPieceSelectionCard(
    onSelectPiece: (PieceType) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, MedievalGold, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MedievalMidWood.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MedievalGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CHỌN QUÂN CỜ ĐỂ HỌC NƯỚC ĐI",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MedievalGold
                )
            }

            Text(
                text = "Chọn quân cờ để chuyển sang bàn cờ giả định (gợi ý đi liên tục):",
                fontSize = 11.5.sp,
                color = MedievalParchmentDark,
                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
            )

            val pieces = listOf(
                Triple(PieceType.ROOK, "Xe (♜)", "Đi thẳng, ngang không giới hạn ô"),
                Triple(PieceType.BISHOP, "Tượng (♝)", "Đi chéo không giới hạn ô"),
                Triple(PieceType.QUEEN, "Hậu (♛)", "Đi thẳng, ngang, chéo tự do"),
                Triple(PieceType.KNIGHT, "Mã (♞)", "Đi hình chữ L (2 ô thẳng + 1 ô ngang/dọc)"),
                Triple(PieceType.KING, "Vua (♚)", "Đi 1 ô theo mọi hướng"),
                Triple(PieceType.PAWN, "Tốt (♟)", "Đi thẳng 1 ô (ô đầu đi 2 ô), ăn chéo")
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                pieces.forEach { (pieceType, title, note) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectPiece(pieceType) }
                            .border(1.dp, MedievalGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .testTag("tutorial_piece_${pieceType.name.lowercase()}"),
                        color = Color(0xFF23150B)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(MedievalGold.copy(alpha = 0.2f))
                                    .border(1.dp, MedievalGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (pieceType) {
                                        PieceType.ROOK -> "♜"
                                        PieceType.KNIGHT -> "♞"
                                        PieceType.BISHOP -> "♝"
                                        PieceType.QUEEN -> "♛"
                                        PieceType.KING -> "♚"
                                        PieceType.PAWN -> "♟"
                                    },
                                    fontSize = 18.sp,
                                    color = MedievalGoldLight
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = title,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedievalParchment
                                )
                                Text(
                                    text = note,
                                    fontSize = 11.sp,
                                    color = MedievalGoldLight,
                                    lineHeight = 14.sp
                                )
                            }

                            Text(
                                text = "Tập đi ➔",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedievalGold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchPreviewCard(
    selectedSide: SideOption,
    selectedDifficulty: DifficultyLevel,
    selectedGameMode: GameMode
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MedievalGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        color = Color(0xFF1F1209)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when (selectedGameMode) {
                        GameMode.TWO_PLAYERS -> "NGƯỜI CHƠI 1"
                        GameMode.TUTORIAL -> "CHẾ ĐỘ"
                        else -> "BẠN CẦM QUÂN"
                    },
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedievalGoldLight
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = when (selectedGameMode) {
                        GameMode.TWO_PLAYERS -> "Quân Trắng (♔)"
                        GameMode.TUTORIAL -> "Hướng Dẫn Quân Cờ"
                        else -> selectedSide.displayNameVi
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MedievalParchment,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = if (selectedGameMode == GameMode.TUTORIAL) "📖" else "⚔️ VS ⚔️",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MedievalGold,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when (selectedGameMode) {
                        GameMode.TWO_PLAYERS -> "NGƯỜI CHƠI 2"
                        GameMode.TUTORIAL -> "MỤC TIÊU"
                        else -> "ĐỐI THỦ MÁY"
                    },
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedievalGoldLight
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = when (selectedGameMode) {
                        GameMode.TWO_PLAYERS -> "Quân Đen (♚)"
                        GameMode.TUTORIAL -> "Tập Luyện Lực Lượng"
                        else -> "Cấp ${selectedDifficulty.displayNameVi}"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (selectedGameMode == GameMode.TWO_PLAYERS || selectedGameMode == GameMode.TUTORIAL) MedievalParchment else when (selectedDifficulty) {
                        DifficultyLevel.EASY -> Color(0xFF22C55E)
                        DifficultyLevel.MEDIUM -> MedievalGold
                        DifficultyLevel.HARD -> Color(0xFFEF4444)
                    },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StartGameButton(
    gameMode: GameMode = GameMode.VS_AI,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(12.dp, RoundedCornerShape(14.dp))
            .testTag("start_game_button"),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2312)),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, MedievalGold)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (gameMode == GameMode.TUTORIAL) "📖 BẮT ĐẦU HƯỚNG DẪN 📖" else "⚔️ BẮT ĐẦU CHƠI ⚔️",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MedievalGoldLight,
                letterSpacing = 1.sp
            )
        }
    }
}
