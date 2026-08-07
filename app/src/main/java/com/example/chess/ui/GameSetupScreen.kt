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
import androidx.compose.material3.TextButton
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.ui.draw.rotate
import androidx.compose.material3.ExperimentalMaterial3Api
import kotlin.math.roundToInt

@Composable
fun GameSetupScreen(
    initialSideOption: SideOption = SideOption.WHITE,
    initialDifficulty: DifficultyLevel = DifficultyLevel.LEVEL_2,
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
                        .weight(1.1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    SetupHeader()

                    Spacer(modifier = Modifier.height(10.dp))

                    MatchPreviewCard(selectedSide, selectedDifficulty, selectedGameMode)

                    Spacer(modifier = Modifier.height(16.dp))

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
                        .weight(1.1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GameModeSelectionCard(
                        selectedGameMode = selectedGameMode,
                        onSelectGameMode = { selectedGameMode = it },
                        onOpenHistory = onOpenHistory,
                        gameStatus = gameStatus,
                        onReturnToCurrentGame = onReturnToCurrentGame
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

                    GameModeSelectionCard(
                        selectedGameMode = selectedGameMode,
                        onSelectGameMode = { selectedGameMode = it },
                        onOpenHistory = onOpenHistory,
                        gameStatus = gameStatus,
                        onReturnToCurrentGame = onReturnToCurrentGame
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (!isLandscape) {
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
        }

        Text(
            text = "⚔️ CỜ VUA TRUNG CỔ ⚔️",
            fontSize = if (isLandscape) 11.sp else 13.sp,
            fontWeight = FontWeight.Bold,
            color = MedievalGoldLight,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        if (!isLandscape) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "THIẾT LẬP TRẬN ĐẤU",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MedievalGold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(if (isLandscape) 2.dp else 4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.8f else 0.5f)
                .height(if (isLandscape) 1.dp else 2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, MedievalGold, Color.Transparent)
                    )
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

            val interactionSource = remember { MutableInteractionSource() }
            Slider(
                value = selectedDifficulty.level.toFloat(),
                onValueChange = { onSelectDifficulty(DifficultyLevel.fromInt(it.roundToInt())) },
                valueRange = 1f..7f,
                steps = 5,
                interactionSource = interactionSource,
                colors = SliderDefaults.colors(
                    thumbColor = MedievalGold,
                    activeTrackColor = MedievalGold,
                    inactiveTrackColor = Color(0xFF22140A),
                    activeTickColor = MedievalGold,
                    inactiveTickColor = MedievalGold.copy(alpha = 0.4f)
                ),
                track = { sliderState ->
                    SliderDefaults.Track(
                        colors = SliderDefaults.colors(
                            activeTrackColor = MedievalGold,
                            inactiveTrackColor = Color(0xFF22140A),
                            activeTickColor = MedievalGold,
                            inactiveTickColor = MedievalGold.copy(alpha = 0.4f)
                        ),
                        sliderState = sliderState,
                        thumbTrackGapSize = 0.dp,
                        modifier = Modifier.height(4.dp)
                    )
                },
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .rotate(45f)
                            .background(MedievalGold)
                            .border(1.dp, MedievalGoldLight)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DifficultyLevel.entries.forEach { level ->
                    Text(
                        text = level.displayNameVi,
                        fontSize = 9.sp,
                        fontWeight = if (selectedDifficulty == level) FontWeight.ExtraBold else FontWeight.Normal,
                        color = if (selectedDifficulty == level) MedievalGoldLight else MedievalParchmentDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(40.dp)
                    )
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

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SideOption.values().forEach { option ->
                    val isSelected = selectedSide == option
                    val activeBorderColor = if (isSelected) MedievalGold else Color(0x44D4AF37)
                    val activeBgColor = if (isSelected) MedievalGold.copy(alpha = 0.2f) else Color(0xFF22140A)

                    Surface(
                        modifier = Modifier
                            .weight(1f)
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
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = option.iconSymbol,
                                fontSize = 18.sp,
                                color = if (isSelected) MedievalGoldLight else MedievalParchment
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when(option) {
                                    SideOption.WHITE -> "Bạch Vương"
                                    SideOption.BLACK -> "Hắc Vương"
                                    SideOption.RANDOM -> "Ngẫu nhiên"
                                },
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MedievalGoldLight else MedievalParchment,
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
private fun GameModeSelectionCard(
    selectedGameMode: GameMode,
    onSelectGameMode: (GameMode) -> Unit,
    onOpenHistory: (() -> Unit)? = null,
    gameStatus: GameStatus = GameStatus.NOT_STARTED,
    onReturnToCurrentGame: (() -> Unit)? = null
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

                Row(verticalAlignment = Alignment.CenterVertically) {
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

                    if (gameStatus == GameStatus.IN_PROGRESS && onReturnToCurrentGame != null && selectedGameMode != GameMode.TUTORIAL) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = onReturnToCurrentGame,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF22C55E).copy(alpha = 0.2f), CircleShape)
                                .border(2.dp, Color(0xFF22C55E), CircleShape)
                                .shadow(8.dp, CircleShape)
                                .testTag("return_to_game_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Tiếp tục trận đấu hiện tại",
                                tint = Color(0xFF22C55E),
                                modifier = Modifier.size(22.dp)
                            )
                        }
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
                        else -> selectedDifficulty.displayNameVi
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (selectedGameMode == GameMode.TWO_PLAYERS || selectedGameMode == GameMode.TUTORIAL) MedievalParchment else when (selectedDifficulty) {
                        DifficultyLevel.LEVEL_1 -> Color(0xFF22C55E) // Xanh lá tươi
                        DifficultyLevel.LEVEL_2 -> MedievalGold     // Vàng kim
                        DifficultyLevel.LEVEL_3 -> Color(0xFFFCA5A5) // Đỏ hồng rất nhạt
                        DifficultyLevel.LEVEL_4 -> Color(0xFFF87171) // Đỏ hồng tươi
                        DifficultyLevel.LEVEL_5 -> Color(0xFFEF4444) // Đỏ tươi tiêu chuẩn
                        DifficultyLevel.LEVEL_6 -> Color(0xFFFF2424) // Đỏ rực rỡ
                        DifficultyLevel.LEVEL_7 -> Color(0xFFFF0000) // Đỏ rực cực độ (tươi nhất)
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

@Preview(showBackground = true, name = "Setup Portrait")
@Composable
fun GameSetupScreenPreview() {
    MyApplicationTheme {
        GameSetupScreen(
            onStartGame = { _, _, _ -> },
            onOpenHistory = {}
        )
    }
}

@Preview(showBackground = true, name = "Setup Landscape", widthDp = 800, heightDp = 400)
@Composable
fun GameSetupScreenLandscapePreview() {
    MyApplicationTheme {
        GameSetupScreen(
            onStartGame = { _, _, _ -> },
            onOpenHistory = {}
        )
    }
}
