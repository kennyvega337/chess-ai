package com.example.chess.ui

import android.content.Intent
import android.widget.Toast
import com.example.MainActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Undo
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.example.chess.model.AppScreen
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.ui.theme.MedievalDarkWood
import com.example.ui.theme.MedievalGold
import com.example.ui.theme.MedievalGoldLight
import com.example.ui.theme.MedievalParchment
import com.example.ui.theme.MedievalParchmentDark

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Palette
import com.example.chess.ui.GameHistoryDialog
import com.example.chess.ui.ChessThemeDialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessScreen(
    viewModel: ChessViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Ensure system navigation bar is hidden and top status bar icons are white
    SideEffect {
        (context as? Activity)?.window?.let { window ->
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = false // White status bar icons
                isAppearanceLightNavigationBars = false
                hide(WindowInsetsCompat.Type.navigationBars()) // Hide bottom nav bar
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    when (state.currentScreen) {
        AppScreen.SETUP -> {
            GameSetupScreen(
                initialSideOption = state.selectedSideOption,
                initialDifficulty = state.difficulty,
                initialGameMode = state.gameMode,
                gameStatus = state.gameStatus,
                onStartGame = { sideOption, difficulty, gameMode ->
                    viewModel.startNewGame(sideOption, difficulty, gameMode)
                },
                onStartTutorialPiece = { pieceType ->
                    viewModel.startTutorialMode(pieceType)
                },
                onReturnToCurrentGame = {
                    viewModel.returnToCurrentGame()
                },
                onOpenHistory = {
                    viewModel.openHistoryModal()
                }
            )
        }
        AppScreen.GAME -> {
            ChessBoardScreenContent(
                state = state,
                viewModel = viewModel,
                context = context
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessBoardScreenContent(
    state: ChessUiState,
    viewModel: ChessViewModel,
    context: Context
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(state.selectedTheme.darkSquareColor).copy(alpha = 0.4f),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (!isLandscape) {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "⚔️ CỜ VUA TRUNG CỔ ⚔️",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MedievalGold,
                                letterSpacing = 0.5.sp,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = when (state.gameMode) {
                                    GameMode.TWO_PLAYERS -> "Chế Độ 2 Người Chơi"
                                    GameMode.TUTORIAL -> "Hướng Dẫn Quân Cờ"
                                    else -> "Thách Đấu Máy (${state.difficulty.displayNameVi})"
                                },
                                fontSize = 11.sp,
                                color = MedievalParchmentDark,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    actions = {
                        if (state.gameMode != GameMode.TUTORIAL) {
                            IconButton(
                                onClick = { viewModel.openCapturedPiecesModal() },
                                modifier = Modifier.testTag("score_captured_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Bảng Chiến Tích & Điểm Số",
                                    tint = MedievalGold
                                )
                            }
                        }
                        if (state.gameMode != GameMode.TWO_PLAYERS && state.gameMode != GameMode.TUTORIAL) {
                            IconButton(
                                onClick = {
                                    if (state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking && state.currentTurn == state.userColor) {
                                        viewModel.showHint()
                                    } else {
                                        Toast.makeText(context, "Chưa đến lượt bạn hoặc trò chơi chưa bắt đầu", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.testTag("hint_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Gợi ý nước đi",
                                    tint = MedievalGold
                                )
                            }
                        }
                        IconButton(
                            onClick = { viewModel.openThemeModal() },
                            modifier = Modifier.testTag("theme_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Đổi chủ đề bàn cờ",
                                tint = MedievalGold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF22140A)
                    )
                )
            }
        }
    ) { innerPadding ->
        if (isLandscape) {
            // === LANDSCAPE LAYOUT (Xoay ngang màn hình) ===
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT SIDE: Chessboard view fills left half cleanly
                Box(
                    modifier = Modifier
                        .weight(1.15f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    ChessBoardView(
                        board = state.board,
                        userColor = state.userColor,
                        selectedPosition = state.selectedPosition,
                        legalMoves = state.legalMovesForSelected,
                        playerLastMove = state.playerLastMove,
                        aiLastMove = state.aiLastMove,
                        hintMove = state.hintMove,
                        isCheck = state.isCheck,
                        currentTurn = state.currentTurn,
                        onSquareClick = { pos -> viewModel.onSquareClick(pos) },
                        theme = state.selectedTheme,
                        viewMode = state.boardViewMode,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // RIGHT SIDE: Header controls, Player cards & Action buttons
                Column(
                    modifier = Modifier
                        .weight(0.85f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar in Landscape
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "⚔️ CỜ VUA TRUNG CỔ",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MedievalGold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (state.gameMode == GameMode.TWO_PLAYERS) "Chế Độ 2 Người Chơi" else "Cấp ${state.difficulty.displayNameVi}",
                                fontSize = 10.5.sp,
                                color = MedievalParchmentDark,
                                maxLines = 1
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            if (state.gameMode != GameMode.TUTORIAL) {
                                IconButton(
                                    onClick = { viewModel.openCapturedPiecesModal() },
                                    modifier = Modifier.size(32.dp).testTag("score_captured_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = "Chiến Tích",
                                        tint = MedievalGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            if (state.gameMode != GameMode.TWO_PLAYERS && state.gameMode != GameMode.TUTORIAL) {
                                IconButton(
                                    onClick = {
                                        if (state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking && state.currentTurn == state.userColor) {
                                            viewModel.showHint()
                                        } else {
                                            Toast.makeText(context, "Chưa đến lượt bạn hoặc trò chơi chưa bắt đầu", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.size(32.dp).testTag("hint_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = "Gợi Ý",
                                        tint = MedievalGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            IconButton(
                                onClick = { viewModel.openThemeModal() },
                                modifier = Modifier.size(32.dp).testTag("theme_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "Chủ Đề",
                                    tint = MedievalGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (state.gameMode == GameMode.TUTORIAL) {
                        TutorialHeaderBar(
                            currentTutorialPiece = state.tutorialPiece,
                            onSelectPiece = { pieceType -> viewModel.startTutorialMode(pieceType) }
                        )
                    } else {
                        // Opponent Card
                        PlayerCard(
                            isUser = false,
                            playerColor = state.userColor.opposite,
                            isCurrentTurn = state.currentTurn == state.userColor.opposite,
                            isAiThinking = state.isAiThinking,
                            capturedPieces = if (state.userColor == PieceColor.WHITE) state.capturedWhitePieces else state.capturedBlackPieces,
                            difficulty = state.difficulty,
                            gameMode = state.gameMode,
                            onClick = { viewModel.openCapturedPiecesModal() }
                        )

                        // --- LANDSCAPE SCORE BOARD (BETWEEN CARDS) ---
                        val whiteScore = state.capturedBlackPieces.sumOf { it.pointValue }
                        val blackScore = state.capturedWhitePieces.sumOf { it.pointValue }
                        val userScoreVal = if (state.userColor == PieceColor.WHITE) whiteScore else blackScore
                        val opponentScoreVal = if (state.userColor == PieceColor.WHITE) blackScore else whiteScore

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFF2D1B0E),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MedievalGold.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "${opponentScoreVal}đ",
                                        color = if (opponentScoreVal >= userScoreVal) Color(0xFFEF4444) else MedievalParchment,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "VS",
                                        color = MedievalGold,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "${userScoreVal}đ",
                                        color = if (userScoreVal >= opponentScoreVal) Color(0xFF22C55E) else MedievalParchment,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        // Check Warning Banner
                        AnimatedVisibility(
                            visible = state.isCheck && state.gameStatus == GameStatus.IN_PROGRESS,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Surface(
                                color = Color(0xFF8B0000),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MedievalGold),
                                modifier = Modifier.padding(vertical = 2.dp).testTag("check_warning_banner")
                            ) {
                                Text(
                                    text = if (state.gameMode == GameMode.TWO_PLAYERS) {
                                        if (state.currentTurn == PieceColor.WHITE) "⚠️ VUA NGƯỜI CHƠI 1 ĐANG BỊ CHIẾU!" else "⚠️ VUA NGƯỜI CHƠI 2 ĐANG BỊ CHIẾU!"
                                    } else if (state.currentTurn == state.userColor) "⚠️ VUA ĐANG BỊ CHIẾU!" else "⚠️ CHIẾU VUA MÁY!",
                                    color = MedievalGoldLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // User Card
                        PlayerCard(
                            isUser = true,
                            playerColor = state.userColor,
                            isCurrentTurn = state.currentTurn == state.userColor,
                            capturedPieces = if (state.userColor == PieceColor.WHITE) state.capturedBlackPieces else state.capturedWhitePieces,
                            gameMode = state.gameMode,
                            onClick = { viewModel.openCapturedPiecesModal() }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Action Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.gameMode == GameMode.TUTORIAL) {
                            Button(
                                onClick = { openSetupActivity(context, state) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .testTag("new_match_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF382315)),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, MedievalGold),
                                contentPadding = PaddingValues(horizontal = 2.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = MedievalGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Đổi Chế Độ", color = MedievalGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.undoMove() },
                                enabled = state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("undo_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF382315),
                                    disabledContainerColor = Color(0xFF382315).copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS) MedievalGold else MedievalGold.copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(horizontal = 2.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Undo, contentDescription = null, tint = if (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS) MedievalGold else MedievalGold.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Hoàn Tác", color = if (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS) MedievalGold else MedievalGold.copy(alpha = 0.4f), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            if (state.gameMode != GameMode.TWO_PLAYERS) {
                                OutlinedButton(
                                    onClick = { viewModel.requestResign() },
                                    enabled = state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .testTag("resign_button"),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking) Color(0xFFEF4444) else Color(0xFFEF4444).copy(alpha = 0.4f)),
                                    contentPadding = PaddingValues(horizontal = 2.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Đầu Hàng", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }

                            Button(
                                onClick = { openSetupActivity(context, state) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("new_match_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF382315)),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, MedievalGold),
                                contentPadding = PaddingValues(horizontal = 2.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = MedievalGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Thiết Lập", color = MedievalGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        } else {
            // === PORTRAIT LAYOUT (Xoay dọc) ===
            // Chia màn hình làm 3 phần: Đối thủ (Top), Bàn cờ (Middle), Bạn & Điều khiển (Bottom)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // PHẦN 1: TOP (Đối thủ hoặc Tutorial Header)
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (state.gameMode == GameMode.TUTORIAL) {
                        TutorialHeaderBar(
                            currentTutorialPiece = state.tutorialPiece,
                            onSelectPiece = { pieceType -> viewModel.startTutorialMode(pieceType) }
                        )
                    } else {
                        PlayerCard(
                            isUser = false,
                            playerColor = state.userColor.opposite,
                            isCurrentTurn = state.currentTurn == state.userColor.opposite,
                            isAiThinking = state.isAiThinking,
                            capturedPieces = if (state.userColor == PieceColor.WHITE) state.capturedWhitePieces else state.capturedBlackPieces,
                            difficulty = state.difficulty,
                            gameMode = state.gameMode,
                            onClick = { viewModel.openCapturedPiecesModal() }
                        )
                    }
                }

                // PHẦN 2: MIDDLE (Bàn cờ trung tâm)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val whiteScore = state.capturedBlackPieces.sumOf { it.pointValue }
                    val blackScore = state.capturedWhitePieces.sumOf { it.pointValue }
                    val opponentScoreVal = if (state.userColor == PieceColor.WHITE) blackScore else whiteScore
                    val opponentColor = state.userColor.opposite

                    // 1. OPPONENT SCORE BADGE (TOP OF BOARD)
                    if (state.gameMode != GameMode.TUTORIAL) {
                        Surface(
                            color = Color(0xFF450A0A),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFFCA5A5)),
                            modifier = Modifier.padding(bottom = 5.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(text = if (state.gameMode == GameMode.TWO_PLAYERS) "⚔️ N.Chơi 2:" else "⚔️ Máy:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCA5A5))
                                Surface(color = Color(0xFF991B1B), shape = RoundedCornerShape(8.dp)) {
                                    Text(text = "${opponentScoreVal}đ", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp))
                                }
                                Text(text = if (opponentColor == PieceColor.WHITE) "♔" else "♚", fontSize = 13.sp, color = Color.White)
                            }
                        }
                    }

                    ChessBoardView(
                        board = state.board,
                        userColor = state.userColor,
                        selectedPosition = state.selectedPosition,
                        legalMoves = state.legalMovesForSelected,
                        playerLastMove = state.playerLastMove,
                        aiLastMove = state.aiLastMove,
                        hintMove = state.hintMove,
                        isCheck = state.isCheck,
                        currentTurn = state.currentTurn,
                        onSquareClick = { pos -> viewModel.onSquareClick(pos) },
                        theme = state.selectedTheme,
                        viewMode = state.boardViewMode,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // PHẦN 3: BOTTOM (Cảnh báo chiếu, Điểm người chơi, Thẻ của bạn & Các nút chức năng)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 2. USER SCORE BADGE (BOTTOM OF BOARD)
                    if (state.gameMode != GameMode.TUTORIAL) {
                        val whiteScore = state.capturedBlackPieces.sumOf { it.pointValue }
                        val blackScore = state.capturedWhitePieces.sumOf { it.pointValue }
                        val userScoreVal = if (state.userColor == PieceColor.WHITE) whiteScore else blackScore

                        Surface(
                            color = Color(0xFF1E3A8A),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF93C5FD)),
                            modifier = Modifier.padding(top = 5.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(text = if (state.gameMode == GameMode.TWO_PLAYERS) "👑 N.Chơi 1:" else "👑 Bạn:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF93C5FD))
                                Surface(color = Color(0xFF1D4ED8), shape = RoundedCornerShape(8.dp)) {
                                    Text(text = "${userScoreVal}đ", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp))
                                }
                                Text(text = if (state.userColor == PieceColor.WHITE) "♔" else "♚", fontSize = 13.sp, color = Color.White)
                            }
                        }
                    }

                    // --- 1. CẢNH BÁO CHIẾU TƯỚNG (Phía trên Thẻ người chơi) ---
                    AnimatedVisibility(
                        visible = state.isCheck && state.gameStatus == GameStatus.IN_PROGRESS && state.gameMode != GameMode.TUTORIAL,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val isUserInCheck = state.currentTurn == state.userColor
                        Surface(
                            color = if (isUserInCheck) Color(0xFF8B0000) else Color(0xFF382315),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MedievalGold),
                            modifier = Modifier.testTag("check_warning_banner")
                        ) {
                            Text(
                                text = if (state.gameMode == GameMode.TWO_PLAYERS) {
                                    if (state.currentTurn == PieceColor.WHITE) "⚠️ VUA NGƯỜI CHƠI 1 ĐANG BỊ CHIẾU!" else "⚠️ VUA NGƯỜI CHƠI 2 ĐANG BỊ CHIẾU!"
                                } else if (isUserInCheck) "⚠️ VUA CỦA BẠN ĐANG BỊ CHIẾU!" else "⚠️ CHIẾU VUA MÁY!",
                                color = MedievalGoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                            )
                        }
                    }

                    // (Đã xóa bảng điểm VS cũ ở đây)

                    // --- 2. THẺ NGƯỜI CHƠI ---
                    if (state.gameMode != GameMode.TUTORIAL) {
                        PlayerCard(
                            isUser = true,
                            playerColor = state.userColor,
                            isCurrentTurn = state.currentTurn == state.userColor,
                            capturedPieces = if (state.userColor == PieceColor.WHITE) state.capturedBlackPieces else state.capturedWhitePieces,
                            gameMode = state.gameMode,
                            onClick = { viewModel.openCapturedPiecesModal() }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // --- 4. CÁC NÚT ĐIỀU KHIỂN ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.gameMode == GameMode.TUTORIAL) {
                            Button(
                                onClick = { openSetupActivity(context, state) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("new_match_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF382315)),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, MedievalGold),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = MedievalGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Đổi Chế Độ", color = MedievalGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.undoMove() },
                                enabled = state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("undo_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF382315),
                                    disabledContainerColor = Color(0xFF382315).copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS) MedievalGold else MedievalGold.copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Undo, contentDescription = null, tint = if (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS) MedievalGold else MedievalGold.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Hoàn Tác", color = if (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS) MedievalGold else MedievalGold.copy(alpha = 0.4f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            if (state.gameMode != GameMode.TWO_PLAYERS) {
                                OutlinedButton(
                                    onClick = { viewModel.requestResign() },
                                    enabled = state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .testTag("resign_button"),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking) Color(0xFFEF4444) else Color(0xFFEF4444).copy(alpha = 0.4f)),
                                    contentPadding = PaddingValues(horizontal = 4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Đầu Hàng", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            Button(
                                onClick = { openSetupActivity(context, state) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("new_match_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF382315)),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, MedievalGold),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = MedievalGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Thiết Lập", color = MedievalGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Modals and Dialogs
        val isUserWinner = state.winner == state.userColor && (state.gameStatus == GameStatus.CHECKMATE || state.gameStatus == GameStatus.RESIGNED)
        if (isUserWinner) {
            FireworksOverlay(modifier = Modifier.fillMaxSize())
        }

        if (state.showResignConfirmationModal) {
            ResignConfirmationDialog(
                onConfirmResign = { viewModel.confirmResign() },
                onCancel = { viewModel.cancelResign() }
            )
        }

        if (state.showCapturedPiecesModal) {
            CapturedPiecesDialog(
                state = state,
                onDismiss = { viewModel.closeCapturedPiecesModal() }
            )
        }

        if (state.showHistoryModal) {
            GameHistoryDialog(
                onDismiss = { viewModel.closeHistoryModal() }
            )
        }

        if (state.showThemeModal) {
            ChessThemeDialog(
                selectedTheme = state.selectedTheme,
                viewMode = state.boardViewMode,
                onThemeSelect = { viewModel.selectTheme(it) },
                onViewModeChange = { viewModel.setBoardViewMode(it) },
                onDismiss = { viewModel.closeThemeModal() }
            )
        }

        // Center Medieval Check Popup Dialog
        if (state.showCheckPopup) {
            CheckPopupDialog(
                onDismiss = { viewModel.dismissCheckPopup() }
            )
        }

        // Game Over Announcement Dialog
        if (state.gameStatus == GameStatus.CHECKMATE || state.gameStatus == GameStatus.STALEMATE || state.gameStatus == GameStatus.RESIGNED) {
            GameOverDialog(
                gameStatus = state.gameStatus,
                winner = state.winner,
                userColor = state.userColor,
                gameMode = state.gameMode,
                onPlayAgain = { openSetupActivity(context, state) }
            )
        }

        state.pendingPromotionMove?.let {
            PawnPromotionDialog(
                color = state.userColor,
                onSelectPiece = { type -> viewModel.completePromotion(type) }
            )
        }
    }
}

@Composable
private fun TutorialHeaderBar(
    currentTutorialPiece: PieceType?,
    onSelectPiece: (PieceType) -> Unit
) {
    val selectedPiece = currentTutorialPiece ?: PieceType.ROOK
    val pieces = listOf(
        PieceType.ROOK to "Xe (♜)",
        PieceType.BISHOP to "Tượng (♝)",
        PieceType.QUEEN to "Hậu (♛)",
        PieceType.KNIGHT to "Mã (♞)",
        PieceType.KING to "Vua (♚)",
        PieceType.PAWN to "Tốt (♟)"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, MedievalGold, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF22140A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                pieces.forEach { (type, label) ->
                    val isSelected = selectedPiece == type
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectPiece(type) }
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) MedievalGold else Color(0x44D4AF37),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .testTag("tutorial_bar_${type.name.lowercase()}"),
                        color = if (isSelected) MedievalGold.copy(alpha = 0.25f) else Color(0xFF190C05)
                    ) {
                        Text(
                            text = label,
                            fontSize = 13.5.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) MedievalGoldLight else MedievalParchment,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val ruleNote = when (selectedPiece) {
                PieceType.ROOK -> "♜ Xe: Đi thẳng, ngang không giới hạn ô. Bị chặn bởi quân cờ cùng màu, ăn quân khác màu."
                PieceType.BISHOP -> "♝ Tượng: Đi chéo không giới hạn ô. Luôn di chuyển trên các ô cùng màu."
                PieceType.QUEEN -> "♛ Hậu: Quân mạnh nhất! Đi tự do theo đường thẳng, ngang và chéo không giới hạn ô."
                PieceType.KNIGHT -> "♞ Mã: Đi hình chữ L (2 ô thẳng + 1 ô ngang/dọc). Duy nhất có thể NHẢY QUA quân khác!"
                PieceType.KING -> "♚ Vua: Quân tối cao! Đi 1 ô theo mọi hướng. Cần được bảo vệ tránh bị chiếu."
                PieceType.PAWN -> "♟ Tốt: Đi thẳng 1 ô (nước đầu được đi 2 ô). Chỉ ĂN CHÉO 1 ô phía trước."
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF170B04), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0x44D4AF37), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = ruleNote,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MedievalParchment,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "💡 Chấm vàng phát sáng gợi ý nước đi hợp lệ liên tục",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = MedievalGold
                    )
                }
            }
        }
    }
}

private fun openSetupActivity(context: Context, state: ChessUiState) {
    val intent = Intent(context, GameSetupActivity::class.java).apply {
        putExtra(MainActivity.EXTRA_GAME_MODE, state.gameMode.name)
        putExtra(MainActivity.EXTRA_SIDE_OPTION, state.selectedSideOption.name)
        putExtra(MainActivity.EXTRA_DIFFICULTY, state.difficulty.name)
        putExtra(MainActivity.EXTRA_IS_GAME_IN_PROGRESS, state.gameStatus == GameStatus.IN_PROGRESS)
    }
    context.startActivity(intent)
}
