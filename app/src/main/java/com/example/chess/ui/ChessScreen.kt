package com.example.chess.ui

import android.content.Intent
import android.widget.Toast
import com.example.MainActivity
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.chess.model.DifficultyLevel
import com.example.chess.model.ChessTheme
import com.example.chess.model.BoardViewMode
import com.example.chess.model.Position
import com.example.chess.model.SideOption
import com.example.ui.theme.*

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentHeight
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

    ChessScreenContent(
        state = state,
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
        },
        onOpenCapturedPiecesModal = { viewModel.openCapturedPiecesModal() },
        onShowHint = { viewModel.showHint() },
        onOpenThemeModal = { viewModel.openThemeModal() },
        onUndoMove = { viewModel.undoMove() },
        onRestartGame = { viewModel.restartGame() },
        onResignRequest = { viewModel.requestResign() },
        onSquareClick = { pos -> viewModel.onSquareClick(pos) },
        onSelectTheme = { viewModel.selectTheme(it) },
        onSetBoardViewMode = { viewModel.setBoardViewMode(it) },
        onCloseThemeModal = { viewModel.closeThemeModal() },
        onCloseCapturedPiecesModal = { viewModel.closeCapturedPiecesModal() },
        onCloseHistoryModal = { viewModel.closeHistoryModal() },
        onConfirmResign = { viewModel.confirmResign() },
        onCancelResign = { viewModel.cancelResign() },
        onDismissCheckPopup = { viewModel.dismissCheckPopup() },
        onCloseGameOverModal = { viewModel.closeGameOverModal() },
        onCompletePromotion = { type -> viewModel.completePromotion(type) },
        onOpenSetupActivity = { openSetupActivity(context, state) },
        onShowMessage = { message -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() }
    )
}

@Composable
fun ChessScreenContent(
    state: ChessUiState,
    onStartGame: (SideOption, DifficultyLevel, GameMode) -> Unit,
    onStartTutorialPiece: (PieceType) -> Unit,
    onReturnToCurrentGame: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenCapturedPiecesModal: () -> Unit,
    onShowHint: () -> Unit,
    onOpenThemeModal: () -> Unit,
    onUndoMove: () -> Unit,
    onRestartGame: () -> Unit,
    onResignRequest: () -> Unit,
    onSquareClick: (Position) -> Unit,
    onSelectTheme: (ChessTheme) -> Unit,
    onSetBoardViewMode: (BoardViewMode) -> Unit,
    onCloseThemeModal: () -> Unit,
    onCloseCapturedPiecesModal: () -> Unit,
    onCloseHistoryModal: () -> Unit,
    onConfirmResign: () -> Unit,
    onCancelResign: () -> Unit,
    onDismissCheckPopup: () -> Unit,
    onCloseGameOverModal: () -> Unit,
    onCompletePromotion: (PieceType) -> Unit,
    onOpenSetupActivity: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    when (state.currentScreen) {
        AppScreen.SETUP -> {
            GameSetupScreen(
                initialSideOption = state.selectedSideOption,
                initialDifficulty = state.difficulty,
                initialGameMode = state.gameMode,
                gameStatus = state.gameStatus,
                onStartGame = onStartGame,
                onStartTutorialPiece = onStartTutorialPiece,
                onReturnToCurrentGame = onReturnToCurrentGame,
                onOpenHistory = onOpenHistory
            )
        }
        AppScreen.GAME -> {
            ChessBoardScreenContent(
                state = state,
                onOpenCapturedPiecesModal = onOpenCapturedPiecesModal,
                onShowHint = onShowHint,
                onOpenThemeModal = onOpenThemeModal,
                onUndoMove = onUndoMove,
                onRestartGame = onRestartGame,
                onResignRequest = onResignRequest,
                onSquareClick = onSquareClick,
                onSelectTheme = onSelectTheme,
                onSetBoardViewMode = onSetBoardViewMode,
                onCloseThemeModal = onCloseThemeModal,
                onCloseCapturedPiecesModal = onCloseCapturedPiecesModal,
                onCloseHistoryModal = onCloseHistoryModal,
                onConfirmResign = onConfirmResign,
                onCancelResign = onCancelResign,
                onDismissCheckPopup = onDismissCheckPopup,
                onCloseGameOverModal = onCloseGameOverModal,
                onCompletePromotion = onCompletePromotion,
                onStartTutorialPiece = onStartTutorialPiece,
                onOpenSetupActivity = onOpenSetupActivity,
                onShowMessage = onShowMessage
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessBoardScreenContent(
    state: ChessUiState,
    onOpenCapturedPiecesModal: () -> Unit,
    onShowHint: () -> Unit,
    onOpenThemeModal: () -> Unit,
    onUndoMove: () -> Unit,
    onRestartGame: () -> Unit,
    onResignRequest: () -> Unit,
    onSquareClick: (Position) -> Unit,
    onSelectTheme: (ChessTheme) -> Unit,
    onSetBoardViewMode: (BoardViewMode) -> Unit,
    onCloseThemeModal: () -> Unit,
    onCloseCapturedPiecesModal: () -> Unit,
    onCloseHistoryModal: () -> Unit,
    onConfirmResign: () -> Unit,
    onCancelResign: () -> Unit,
    onDismissCheckPopup: () -> Unit,
    onCloseGameOverModal: () -> Unit,
    onCompletePromotion: (PieceType) -> Unit,
    onStartTutorialPiece: (PieceType) -> Unit,
    onOpenSetupActivity: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val useLandscapeLayout = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(state.selectedTheme.darkSquareColor).copy(alpha = 0.4f),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (!useLandscapeLayout) {
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
                                onClick = { onOpenCapturedPiecesModal() },
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
                                        onShowHint()
                                    } else {
                                        onShowMessage("Chưa đến lượt bạn hoặc trò chơi chưa bắt đầu")
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
                            onClick = { onOpenThemeModal() },
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
                        containerColor = ColorDarkBrown
                    )
                )
            }
        }
    ) { innerPadding ->
        if (useLandscapeLayout) {
            // === LANDSCAPE LAYOUT (3 COLUMNS) ===
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(start = 3.dp, end = 3.dp, top = 0.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // COL 1: Controls (Row 1) & Player 1 (Row 2)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    // Row 1: Icons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionIconButton(
                            icon = Icons.Default.Undo,
                            contentDesc = "Hoàn tác",
                            enabled = state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS,
                            isLandscape = true,
                            onClick = { onUndoMove() }
                        )
                        ActionIconButton(
                            icon = Icons.Default.Refresh,
                            contentDesc = "Chơi lại",
                            enabled = !state.isAiThinking && (state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal),
                            isLandscape = true,
                            onClick = { onRestartGame() }
                        )
                        ActionIconButton(
                            icon = Icons.Default.Flag,
                            contentDesc = "Đầu hàng",
                            enabled = state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking,
                            isLandscape = true,
                            onClick = { onResignRequest() }
                        )
                        ActionIconButton(
                            icon = Icons.Default.Lightbulb,
                            contentDesc = "Gợi ý",
                            enabled = state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking && state.currentTurn == state.userColor,
                            isLandscape = true,
                            onClick = { onShowHint() }
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Row 2: Player 1 Info & Score
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .background(Color(0xFF1E3A8A).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .border(1.5.dp, Color(0xFF93C5FD).copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                            .padding(6.dp)
                    ) {
                        val userScoreVal = if (state.userColor == PieceColor.WHITE)
                            state.capturedBlackPieces.sumOf { it.pointValue }
                        else
                            state.capturedWhitePieces.sumOf { it.pointValue }

                        val userLabel = if (state.gameMode == GameMode.TWO_PLAYERS) "N.CHƠI 1" else "BẠN"

                        Text(
                            text = "$userLabel: ${userScoreVal}đ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF93C5FD)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        PlayerCard(
                            isUser = true,
                            playerColor = state.userColor,
                            isCurrentTurn = state.currentTurn == state.userColor,
                            isAiThinking = state.isAiThinking,
                            capturedPieces = if (state.userColor == PieceColor.WHITE) state.capturedBlackPieces else state.capturedWhitePieces,
                            gameMode = state.gameMode,
                            gameStatus = state.gameStatus,
                            winner = state.winner,
                            title = if (state.gameMode == GameMode.TWO_PLAYERS) "NGƯỜI CHƠI 1" else null,
                            onClick = { onOpenCapturedPiecesModal() }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // COL 2: CHESSBOARD (Central Focus)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .padding(top = 0.dp, bottom = 2.dp),
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
                        checkingPieces = state.checkingPieces,
                        gameStatus = state.gameStatus,
                        winner = state.winner,
                        gameMode = state.gameMode,
                        onSquareClick = { pos -> onSquareClick(pos) },
                        theme = state.selectedTheme,
                        viewMode = state.boardViewMode,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // COL 3: Settings (Row 1) & Player 2 (Row 2)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    // Row 1: Icons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionIconButton(
                            icon = Icons.Default.Settings,
                            contentDesc = "Thiết lập",
                            enabled = (state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal) && !state.isAiThinking,
                            isLandscape = true,
                            onClick = { onOpenSetupActivity() }
                        )
                        ActionIconButton(
                            icon = Icons.Default.Palette,
                            contentDesc = "Giao diện",
                            isLandscape = true,
                            onClick = { onOpenThemeModal() }
                        )
                        ActionIconButton(
                            icon = Icons.Default.EmojiEvents,
                            contentDesc = "Chiến tích",
                            isLandscape = true,
                            onClick = { onOpenCapturedPiecesModal() }
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Row 2: Player 2 Info & Score
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .background(Color(0xFF450A0A).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .border(1.5.dp, Color(0xFF450A0A).copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                            .padding(6.dp)
                    ) {
                        val opponentScoreVal = if (state.userColor == PieceColor.WHITE)
                            state.capturedWhitePieces.sumOf { it.pointValue }
                        else
                            state.capturedBlackPieces.sumOf { it.pointValue }

                        val opponentLabel = when (state.gameMode) {
                            GameMode.TWO_PLAYERS -> "N.CHƠI 2"
                            GameMode.TUTORIAL -> "TUTORIAL"
                            else -> "MÁY (${state.difficulty.displayNameVi})"
                        }

                        Text(
                            text = "$opponentLabel: ${opponentScoreVal}đ",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFCA5A5),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        PlayerCard(
                            isUser = false,
                            playerColor = state.userColor.opposite,
                            isCurrentTurn = state.currentTurn == state.userColor.opposite,
                            isAiThinking = state.isAiThinking,
                            capturedPieces = if (state.userColor == PieceColor.WHITE) state.capturedWhitePieces else state.capturedBlackPieces,
                            difficulty = state.difficulty,
                            gameMode = state.gameMode,
                            gameStatus = state.gameStatus,
                            winner = state.winner,
                            title = if (state.gameMode == GameMode.TWO_PLAYERS) "NGƯỜI CHƠI 2" else null,
                            onClick = { onOpenCapturedPiecesModal() }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        } else {
            // === PORTRAIT LAYOUT (Xoay dọc) ===
            val whiteScore = state.capturedBlackPieces.sumOf { it.pointValue }
            val blackScore = state.capturedWhitePieces.sumOf { it.pointValue }
            val userScoreVal = if (state.userColor == PieceColor.WHITE) whiteScore else blackScore
            val opponentScoreVal = if (state.userColor == PieceColor.WHITE) blackScore else whiteScore

            // Chia màn hình làm 3 phần: Đối thủ (Top), Bàn cờ (Middle), Bạn & Điều khiển (Bottom)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // PHẦN 1: TOP (Đối thủ hoặc Tutorial Header)
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (state.gameMode == GameMode.TUTORIAL) {
                        TutorialHeaderBar(
                            currentTutorialPiece = state.tutorialPiece,
                            onSelectPiece = { pieceType -> onStartTutorialPiece(pieceType) }
                        )
                    } else {
                        Log.d("Chess_Winner", ""+ state.winner)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PlayerCard(
                                isUser = false,
                                playerColor = state.userColor.opposite,
                                isCurrentTurn = state.currentTurn == state.userColor.opposite,
                                isAiThinking = state.isAiThinking,
                                capturedPieces = if (state.userColor == PieceColor.WHITE) state.capturedWhitePieces else state.capturedBlackPieces,
                                difficulty = state.difficulty,
                                gameMode = state.gameMode,
                                gameStatus = state.gameStatus,
                                winner = state.winner,
                                title = if (state.gameMode == GameMode.TWO_PLAYERS) "NGƯỜI CHƠI 2" else null,
                                onClick = { onOpenCapturedPiecesModal() }
                            )

                            // Opponent Score Badge - Sát Máy
                            Surface(
                                color = Color(0xFF450A0A),
                                shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.2.dp,
                                    Color(0xFFFCA5A5)
                                ),
                                modifier = Modifier.padding(top = 0.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Text(
                                        text = if (state.gameMode == GameMode.TWO_PLAYERS) "⚔️ N.Chơi 2:" else "⚔️ Máy:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFCA5A5)
                                    )
                                    Text(
                                        text = "${opponentScoreVal}đ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // PHẦN 2: MIDDLE (Bàn cờ trung tâm)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
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
                        checkingPieces = state.checkingPieces,
                        gameStatus = state.gameStatus,
                        winner = state.winner,
                        gameMode = state.gameMode,
                        onSquareClick = { pos -> onSquareClick(pos) },
                        theme = state.selectedTheme,
                        viewMode = state.boardViewMode,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // PHẦN 3: BOTTOM (Cảnh báo chiếu, Điểm người chơi, Thẻ của bạn & Các nút chức năng)
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- 1. THẺ NGƯỜI CHƠI ---
                    if (state.gameMode != GameMode.TUTORIAL) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // User Score Badge - Sát Bạn
                            Surface(
                                color = Color(0xFF1E3A8A),
                                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.2.dp,
                                    Color(0xFF93C5FD)
                                ),
                                modifier = Modifier.padding(bottom = 0.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Text(
                                        text = if (state.gameMode == GameMode.TWO_PLAYERS) "👑 N.Chơi 1:" else "👑 Bạn:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF93C5FD)
                                    )
                                    Text(
                                        text = "${userScoreVal}đ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }

                            PlayerCard(
                                isUser = true,
                                playerColor = state.userColor,
                                isCurrentTurn = state.currentTurn == state.userColor,
                                isAiThinking = state.isAiThinking,
                                capturedPieces = if (state.userColor == PieceColor.WHITE) state.capturedBlackPieces else state.capturedWhitePieces,
                                gameMode = state.gameMode,
                                gameStatus = state.gameStatus,
                                winner = state.winner,
                                title = if (state.gameMode == GameMode.TWO_PLAYERS) "NGƯỜI CHƠI 1" else null,
                                onClick = { onOpenCapturedPiecesModal() }
                            )
                        }
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
                                onClick = { onOpenSetupActivity() },
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
                            // 1. NÚT HOÀN TÁC
                            Button(
                                onClick = { onUndoMove() },
                                enabled = state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("undo_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF382315),
                                    disabledContainerColor = Color(0xFF382315).copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS) MedievalGold else MedievalGold.copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Undo, contentDescription = "Hoàn tác", tint = if (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS) MedievalGold else MedievalGold.copy(alpha = 0.4f), modifier = Modifier.size(22.dp))
                            }

                            // 2. NÚT CHƠI LẠI (RESET)
                            Button(
                                onClick = { onRestartGame() },
                                enabled = !state.isAiThinking && (state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal),
                                modifier = Modifier
                                    .weight(1.1f)
                                    .height(46.dp)
                                    .testTag("restart_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1E3A8A),
                                    disabledContainerColor = Color(0xFF1E3A8A).copy(alpha = 0.5f)
                                ), // Xanh dương đậm
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (!state.isAiThinking && (state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal)) Color(0xFF93C5FD) else Color(0xFF93C5FD).copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Chơi lại", tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }

                            // 3. NÚT ĐẦU HÀNG
                            Button(
                                onClick = { onResignRequest() },
                                enabled = state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("resign_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF382315),
                                    disabledContainerColor = Color(0xFF382315).copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking) Color(0xFFEF4444) else Color(0xFFEF4444).copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Flag, contentDescription = "Đầu hàng", tint = if (state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking) Color(0xFFEF4444) else Color(0xFFEF4444).copy(alpha = 0.4f), modifier = Modifier.size(22.dp))
                            }

                            // 4. NÚT THIẾT LẬP
                            Button(
                                onClick = { onOpenSetupActivity() },
                                enabled = (state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal) && !state.isAiThinking,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("settings_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF382315),
                                    disabledContainerColor = Color(0xFF382315).copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, if ((state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal) && !state.isAiThinking) MedievalGold else MedievalGold.copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Settings, contentDescription = "Thiết lập", tint = if ((state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal) && !state.isAiThinking) MedievalGold else MedievalGold.copy(alpha = 0.4f), modifier = Modifier.size(22.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Modals and Dialogs
    androidx.compose.runtime.key(configuration.orientation) {
        val isGameOver = state.gameStatus == GameStatus.CHECKMATE || state.gameStatus == GameStatus.RESIGNED
        val isCelebrationWinner = if (state.gameMode == GameMode.TWO_PLAYERS) {
            state.winner != null
        } else {
            state.winner == state.userColor
        }

        if (isGameOver && isCelebrationWinner) {
            FireworksOverlay(modifier = Modifier.fillMaxSize())
        }

        if (state.showResignConfirmationModal) {
            ResignConfirmationDialog(
                onConfirmResign = { onConfirmResign() },
                onCancel = { onCancelResign() }
            )
        }

        if (state.showCapturedPiecesModal) {
            CapturedPiecesDialog(
                state = state,
                onDismiss = { onCloseCapturedPiecesModal() }
            )
        }

        if (state.showHistoryModal) {
            GameHistoryDialog(
                onDismiss = { onCloseHistoryModal() }
            )
        }

        if (state.showThemeModal) {
            ChessThemeDialog(
                selectedTheme = state.selectedTheme,
                viewMode = state.boardViewMode,
                onThemeSelect = { onSelectTheme(it) },
                onViewModeChange = { onSetBoardViewMode(it) },
                onDismiss = { onCloseThemeModal() }
            )
        }

        // Center Medieval Check Popup Dialog
        if (state.showCheckPopup) {
            CheckPopupDialog(
                onDismiss = { onDismissCheckPopup() }
            )
        }

        // Game Over Announcement Dialog
        if (state.showGameOverModal) {
            GameOverDialog(
                gameStatus = state.gameStatus,
                winner = state.winner,
                userColor = state.userColor,
                gameMode = state.gameMode,
                onPlayAgain = { onOpenSetupActivity() },
                onRestart = { onRestartGame() },
                onDismiss = { onCloseGameOverModal() }
            )
        }

        state.pendingPromotionMove?.let {
            PawnPromotionDialog(
                color = state.userColor,
                onSelectPiece = { type -> onCompletePromotion(type) }
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

@Composable
private fun ActionIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDesc: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color = MedievalGold,
    showBorder: Boolean = true,
    isLandscape: Boolean = false
) {
    val alpha = if (enabled) 1f else 0.4f
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(20.dp)
            .then(
                if (isLandscape) {
                    Modifier
                } else {
                    Modifier
                        .background(MedievalDarkWood.copy(alpha = 0.6f * alpha), CircleShape)
                        .then(
                            if (showBorder) Modifier.border(1.2.dp, color.copy(alpha = alpha), CircleShape)
                            else Modifier
                        )
                }
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDesc,
            tint = color.copy(alpha = alpha),
            modifier = Modifier.size(if (isLandscape) 22.dp else 20.dp)
        )
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

@Preview(showBackground = true, widthDp = 674, heightDp = 830)
@Composable
fun ChessScreenPreview() {
    MyApplicationTheme {
        ChessScreenContent(
            state = ChessUiState(
                currentScreen = AppScreen.GAME,
                gameMode = GameMode.VS_AI
            ),
            onStartGame = { _, _, _ -> },
            onStartTutorialPiece = {},
            onReturnToCurrentGame = {},
            onOpenHistory = {},
            onOpenCapturedPiecesModal = {},
            onShowHint = {},
            onOpenThemeModal = {},
            onUndoMove = {},
            onRestartGame = {},
            onResignRequest = {},
            onSquareClick = {},
            onSelectTheme = {},
            onSetBoardViewMode = {},
            onCloseThemeModal = {},
            onCloseCapturedPiecesModal = {},
            onCloseHistoryModal = {},
            onConfirmResign = {},
            onCancelResign = {},
            onDismissCheckPopup = {},
            onCloseGameOverModal = {},
            onCompletePromotion = {},
            onOpenSetupActivity = {},
            onShowMessage = {}
        )
    }
}
