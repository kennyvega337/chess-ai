package com.example.chess.ui

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.chess.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzlesScreen(
    state: ChessUiState,
    onOpenCapturedPiecesModal: () -> Unit,
    onShowHint: () -> Unit,
    onOpenThemeModal: () -> Unit,
    onOpenGeneralSettingsModal: () -> Unit,
    onUndoMove: () -> Unit,
    onRestartGame: () -> Unit,
    onSquareClick: (Position) -> Unit,
    onSelectTheme: (ChessTheme) -> Unit,
    onSetBoardViewMode: (BoardViewMode) -> Unit,
    onSetSoundEnabled: (Boolean) -> Unit,
    onSetMoveHintsEnabled: (Boolean) -> Unit,
    onSetSaveGameEnabled: (Boolean) -> Unit,
    onCloseThemeModal: () -> Unit,
    onCloseGeneralSettingsModal: () -> Unit,
    onConfirmRestart: () -> Unit,
    onCancelRestart: () -> Unit,
    onDismissCheckPopup: () -> Unit,
    onCloseGameOverModal: () -> Unit,
    onCompletePromotion: (PieceType) -> Unit,
    onNavigateToSetup: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNextPuzzle: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val useLandscapeLayout = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Ensure system navigation bar is hidden and top status bar icons are white
    SideEffect {
        (context as? Activity)?.window?.let { window ->
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
                hide(WindowInsetsCompat.Type.navigationBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(state.selectedTheme.darkSquareColor).copy(alpha = 0.4f),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            if (!useLandscapeLayout) {
                Surface(
                    color = ColorDarkBrown,
                    tonalElevation = 4.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚔️ CỜ VUA TRUNG CỔ ⚔️",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MedievalGold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(
                                    onClick = { onNavigateToSetup() },
                                    enabled = !state.isAiThinking,
                                    modifier = Modifier.size(36.dp).testTag("back_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Logout,
                                        contentDescription = "Quay lại thiết lập",
                                        tint = MedievalGold,
                                        modifier = Modifier.size(22.dp).graphicsLayer(rotationZ = 180f)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (state.gameMode == GameMode.ONE_MOVE) "Thử Thách 1 Nước" else "Giải Đố Cờ Vua",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedievalParchmentDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        if (state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking) {
                                            onShowHint()
                                        } else {
                                            onShowMessage("Trò chơi chưa bắt đầu")
                                        }
                                    },
                                    modifier = Modifier.size(36.dp).testTag("hint_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = "Gợi ý nước đi",
                                        tint = MedievalGold,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onOpenThemeModal() },
                                    modifier = Modifier.size(36.dp).testTag("theme_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = "Đổi chủ đề bàn cờ",
                                        tint = MedievalGold,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onOpenGeneralSettingsModal() },
                                    modifier = Modifier.size(36.dp).testTag("general_settings_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Cài đặt chung",
                                        tint = MedievalGold,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (useLandscapeLayout) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(innerPadding)
                    .padding(start = 3.dp, end = 3.dp, top = 0.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isUserWin = state.gameStatus == GameStatus.CHECKMATE && state.winner == state.userColor
                        val showNextButton = isUserWin && !state.isLastPuzzleInCategory
                        
                        ActionIconButton(
                            icon = if (showNextButton) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Undo,
                            contentDesc = if (showNextButton) "Ván tiếp theo" else "Hoàn tác",
                            enabled = if (showNextButton) true else (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS),
                            isLandscape = true,
                            color = if (showNextButton) MedievalEmerald else MedievalGold,
                            onClick = { if (showNextButton) onNextPuzzle() else onUndoMove() }
                        )
                        ActionIconButton(
                            icon = Icons.Default.Refresh,
                            contentDesc = "Chơi lại",
                            enabled = !state.isAiThinking && (state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal),
                            isLandscape = true,
                            onClick = { onRestartGame() }
                        )
                        ActionIconButton(
                            icon = Icons.Default.Lightbulb,
                            contentDesc = "Gợi ý",
                            enabled = state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking,
                            isLandscape = true,
                            onClick = { onShowHint() }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(8.dp))
                }

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
                        isMoveHintsEnabled = state.isMoveHintsEnabled,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionIconButton(
                            icon = Icons.Default.Home,
                            contentDesc = "Trang chủ",
                            enabled = (state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal) && !state.isAiThinking,
                            isLandscape = true,
                            onClick = { onNavigateToMenu() }
                        )
                        ActionIconButton(
                            icon = Icons.Default.Palette,
                            contentDesc = "Giao diện",
                            isLandscape = true,
                            onClick = { onOpenThemeModal() }
                        )
                        ActionIconButton(
                            icon = Icons.Default.Settings,
                            contentDesc = "Cài đặt",
                            isLandscape = true,
                            onClick = { onOpenGeneralSettingsModal() }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = Color(0xFF450A0A),
                        shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFFCA5A5)),
                        modifier = Modifier.padding(top = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = if (state.gameMode == GameMode.ONE_MOVE) "🎯 Mục tiêu: Kết thúc ván đấu trong 1 nước" else "🎯 Mục tiêu: Thắng thế cờ",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFCA5A5)
                            )
                        }
                    }
                }

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
                        isMoveHintsEnabled = state.isMoveHintsEnabled,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        PlayerCard(
                            isUser = true,
                            playerColor = state.userColor,
                            isCurrentTurn = state.currentTurn == state.userColor,
                            isAiThinking = state.isAiThinking,
                            capturedPieces = if (state.userColor == PieceColor.WHITE) state.capturedBlackPieces else state.capturedWhitePieces,
                            gameMode = state.gameMode,
                            gameStatus = state.gameStatus,
                            winner = state.winner,
                            title = "THỬ THÁCH",
                            timeMillis = if (state.userColor == PieceColor.WHITE) state.whiteTimeMillis else state.blackTimeMillis,
                            timerOption = state.timerOption,
                            onClick = {}
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isUserWin = state.gameStatus == GameStatus.CHECKMATE && state.winner == state.userColor
                        val showNextButton = isUserWin && !state.isLastPuzzleInCategory

                        Button(
                            onClick = { if (showNextButton) onNextPuzzle() else onUndoMove() },
                            enabled = if (showNextButton) true else (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS),
                            modifier = Modifier.weight(1f).height(46.dp).testTag(if (showNextButton) "next_puzzle_button_inline" else "undo_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (showNextButton) Color(0xFF065F46) else Color(0xFF382315),
                                disabledContainerColor = (if (showNextButton) Color(0xFF065F46) else Color(0xFF382315)).copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (showNextButton) MedievalEmerald else if (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS) MedievalGold else MedievalGold.copy(alpha = 0.4f)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(imageVector = if (showNextButton) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Undo, contentDescription = if (showNextButton) "Ván tiếp theo" else "Hoàn tác", tint = if (showNextButton) Color.White else if (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS) MedievalGold else MedievalGold.copy(alpha = 0.4f), modifier = Modifier.size(22.dp))
                        }

                        Button(
                            onClick = { onRestartGame() },
                            enabled = !state.isAiThinking && (state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal),
                            modifier = Modifier.weight(1.1f).height(46.dp).testTag("restart_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E3A8A),
                                disabledContainerColor = Color(0xFF1E3A8A).copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (!state.isAiThinking && (state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal)) Color(0xFF93C5FD) else Color(0xFF93C5FD).copy(alpha = 0.4f)),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Chơi lại", tint = Color.White, modifier = Modifier.size(20.dp))
                        }

                        Button(
                            onClick = { onNavigateToMenu() },
                            enabled = (state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal) && !state.isAiThinking,
                            modifier = Modifier.weight(1f).height(46.dp).testTag("home_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF382315),
                                disabledContainerColor = Color(0xFF382315).copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, if ((state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal) && !state.isAiThinking) MedievalGold else MedievalGold.copy(alpha = 0.4f)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Home, contentDescription = "Trang chủ", tint = if ((state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal) && !state.isAiThinking) MedievalGold else MedievalGold.copy(alpha = 0.4f), modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }
        }
    }

    androidx.compose.runtime.key(configuration.orientation) {
        val isGameOver = state.gameStatus == GameStatus.CHECKMATE || state.gameStatus == GameStatus.RESIGNED
        val isCelebrationWinner = state.winner == state.userColor

        if (isGameOver && isCelebrationWinner) {
            FireworksOverlay(modifier = Modifier.fillMaxSize())
        }

        if (state.showRestartConfirmationModal) {
            RestartConfirmationDialog(
                onConfirmRestart = onConfirmRestart,
                onCancel = onCancelRestart
            )
        }

        if (state.showThemeModal) {
            ChessThemeDialog(
                selectedTheme = state.selectedTheme,
                viewMode = state.boardViewMode,
                gameMode = state.gameMode,
                onThemeSelect = { onSelectTheme(it) },
                onViewModeChange = { onSetBoardViewMode(it) },
                onDismiss = { onCloseThemeModal() }
            )
        }

        if (state.showGeneralSettingsModal) {
            GeneralSettingsDialog(
                isSoundEnabled = state.isSoundEnabled,
                isMoveHintsEnabled = state.isMoveHintsEnabled,
                isSaveGameEnabled = state.isSaveGameEnabled,
                onSoundToggled = onSetSoundEnabled,
                onMoveHintsToggled = onSetMoveHintsEnabled,
                onSaveGameToggled = onSetSaveGameEnabled,
                onDismiss = onCloseGeneralSettingsModal
            )
        }

        if (state.showCheckPopup) {
            CheckPopupDialog(onDismiss = { onDismissCheckPopup() })
        }

        if (state.showGameOverModal) {
            GameOverDialog(
                gameStatus = state.gameStatus,
                winner = state.winner,
                userColor = state.userColor,
                gameMode = state.gameMode,
                difficulty = state.difficulty,
                timestamp = state.matchEndTimestamp,
                onPlayAgain = onNavigateToSetup, // "Đổi chế độ"
                onRestart = onRestartGame,        // "Chơi lại"
                onDismiss = onCloseGameOverModal,
                onNextMatch = if (state.gameStatus == GameStatus.CHECKMATE && state.winner == state.userColor && !state.isLastPuzzleInCategory) onNextPuzzle else null
            )
        }

        state.pendingPromotionMove?.let { move ->
            PawnPromotionDialog(
                color = move.piece.color,
                onSelectPiece = { type -> onCompletePromotion(type) },
                viewMode = if (state.gameMode == GameMode.TWO_PLAYERS) BoardViewMode.VIEW_2D else state.boardViewMode
            )
        }
    }
}

@Composable
private fun ActionIconButton(
    icon: ImageVector,
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

@Preview(showBackground = true, name = "Puzzles Screen Portrait", widthDp = 1024, heightDp = 1600)
@Composable
fun PuzzlesScreenPreview() {
    MyApplicationTheme {
        PuzzlesScreen(
            state = ChessUiState(
                gameMode = GameMode.PUZZLE,
                gameStatus = GameStatus.IN_PROGRESS,
                selectedTheme = ChessTheme.CLASSIC
            ),
            onOpenCapturedPiecesModal = {},
            onShowHint = {},
            onOpenThemeModal = {},
            onOpenGeneralSettingsModal = {},
            onUndoMove = {},
            onRestartGame = {},
            onSquareClick = {},
            onSelectTheme = {},
            onSetBoardViewMode = {},
            onSetSoundEnabled = {},
            onSetMoveHintsEnabled = {},
            onSetSaveGameEnabled = {},
            onCloseThemeModal = {},
            onCloseGeneralSettingsModal = {},
            onConfirmRestart = {},
            onCancelRestart = {},
            onDismissCheckPopup = {},
            onCloseGameOverModal = {},
            onCompletePromotion = {},
            onNavigateToSetup = {},
            onNavigateToMenu = {},
            onNextPuzzle = {},
            onShowMessage = {}
        )
    }
}

@Preview(showBackground = true, name = "Puzzles Screen Landscape", widthDp = 1600, heightDp = 1024)
@Composable
fun PuzzlesScreenLandscapePreview() {
    MyApplicationTheme {
        PuzzlesScreen(
            state = ChessUiState(
                gameMode = GameMode.PUZZLE,
                gameStatus = GameStatus.IN_PROGRESS,
                selectedTheme = ChessTheme.CLASSIC
            ),
            onOpenCapturedPiecesModal = {},
            onShowHint = {},
            onOpenThemeModal = {},
            onOpenGeneralSettingsModal = {},
            onUndoMove = {},
            onRestartGame = {},
            onSquareClick = {},
            onSelectTheme = {},
            onSetBoardViewMode = {},
            onSetSoundEnabled = {},
            onSetMoveHintsEnabled = {},
            onSetSaveGameEnabled = {},
            onCloseThemeModal = {},
            onCloseGeneralSettingsModal = {},
            onConfirmRestart = {},
            onCancelRestart = {},
            onDismissCheckPopup = {},
            onCloseGameOverModal = {},
            onCompletePromotion = {},
            onNavigateToSetup = {},
            onNavigateToMenu = {},
            onNextPuzzle = {},
            onShowMessage = {}
        )
    }
}
