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
import androidx.compose.ui.graphics.Brush
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
    onSetHintEnabled: (Boolean) -> Unit = {},
    onSetResignEnabled: (Boolean) -> Unit = {},
    onSetUndoEnabled: (Boolean) -> Unit = {},
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
                isAppearanceLightStatusBars = isThemeLight(state.selectedTheme)
                isAppearanceLightNavigationBars = isThemeLight(state.selectedTheme)
                hide(WindowInsetsCompat.Type.navigationBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    val accentColor = Color(state.selectedTheme.accentColor)

    val iconColor = Color(state.selectedTheme.iconColor)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = state.selectedTheme.backgroundColors.map { Color(it) }
                )
            )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                if (!useLandscapeLayout) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.9f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                        Text(
                            text = "⚔️ CỜ VUA TRUNG CỔ ⚔️",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
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
                                        tint = iconColor,
                                        modifier = Modifier.size(22.dp).graphicsLayer(rotationZ = 180f)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (state.gameMode == GameMode.ONE_MOVE) "Thử Thách 1 Nước" else "Giải Đố Cờ Vua",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (state.isHintEnabled) {
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
                                            tint = iconColor,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { onOpenThemeModal() },
                                    modifier = Modifier.size(36.dp).testTag("theme_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = "Đổi chủ đề bàn cờ",
                                        tint = iconColor,
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
                                        tint = iconColor,
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
                // COL 1: Điều khiển & Thông tin câu đố
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // 1. Dãy nút chức năng (Undo, Refresh, Hint)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isUserWin = state.gameStatus == GameStatus.CHECKMATE && state.winner == state.userColor
                        val showNextButton = isUserWin && !state.isLastPuzzleInCategory
                        
                        if (state.isUndoEnabled || showNextButton) {
                            ActionIconButton(
                                icon = if (showNextButton) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Undo,
                                contentDesc = if (showNextButton) "Ván tiếp theo" else "Hoàn tác",
                                enabled = if (showNextButton) true else (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS),
                                isLandscape = true,
                                color = if (showNextButton) MedievalEmerald else iconColor,
                                onClick = { if (showNextButton) onNextPuzzle() else onUndoMove() },
                                selectedTheme = state.selectedTheme
                            )
                        }
                        ActionIconButton(
                            icon = Icons.Default.Refresh,
                            contentDesc = "Chơi lại",
                            enabled = !state.isAiThinking && (state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal),
                            isLandscape = true,
                            onClick = { onRestartGame() },
                            color = iconColor,
                            selectedTheme = state.selectedTheme
                        )
                        if (state.isHintEnabled) {
                            ActionIconButton(
                                icon = Icons.Default.Lightbulb,
                                contentDesc = "Gợi ý",
                                enabled = state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking,
                                isLandscape = true,
                                onClick = { onShowHint() },
                                color = iconColor,
                                selectedTheme = state.selectedTheme
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Chế độ & Cấp độ (Đồng nhất với giao diện dọc)
                    Surface(
                        color = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, accentColor.copy(alpha = 0.5f)),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        val categoryInfo = if (state.puzzleCategory != null && state.puzzleLevel != null) {
                            " | ${state.puzzleCategory} - Lv.${state.puzzleLevel}"
                        } else ""
                        
                        Text(
                            text = (if (state.gameMode == GameMode.ONE_MOVE) "🎯 1 Nước" else "🎯 Giải Đố") + categoryInfo,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // 3. Thông tin người chơi & Lượt đi (Sử dụng PlayerCard đồng nhất với giao diện dọc)
                    Column(
                        modifier = Modifier.fillMaxWidth(0.95f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                            selectedTheme = state.selectedTheme,
                            onClick = {}
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }

                // COL 2: Bàn cờ (Trung tâm)
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

                // COL 3: Cài đặt & Trang chủ
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionIconButton(
                            icon = Icons.Default.Home,
                            contentDesc = "Trang chủ",
                            enabled = (state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal) && !state.isAiThinking,
                            isLandscape = true,
                            onClick = { onNavigateToMenu() },
                            color = iconColor,
                            selectedTheme = state.selectedTheme
                        )
                        ActionIconButton(
                            icon = Icons.Default.Palette,
                            contentDesc = "Giao diện",
                            isLandscape = true,
                            onClick = { onOpenThemeModal() },
                            color = iconColor,
                            selectedTheme = state.selectedTheme
                        )
                        ActionIconButton(
                            icon = Icons.Default.Settings,
                            contentDesc = "Cài đặt",
                            isLandscape = true,
                            onClick = { onOpenGeneralSettingsModal() },
                            color = iconColor,
                            selectedTheme = state.selectedTheme
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
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
                        color = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, accentColor.copy(alpha = 0.5f)),
                        modifier = Modifier.padding(top = 0.dp)
                    ) {
                        val categoryInfo = if (state.puzzleCategory != null && state.puzzleLevel != null) {
                            " | ${state.puzzleCategory} - Lv.${state.puzzleLevel}"
                        } else ""
                        
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = (if (state.gameMode == GameMode.ONE_MOVE) "🎯 1 Nước" else "🎯 Giải Đố") + categoryInfo,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
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
                            selectedTheme = state.selectedTheme,
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

                        if (state.isUndoEnabled || showNextButton) {
                            Button(
                                onClick = { if (showNextButton) onNextPuzzle() else onUndoMove() },
                                enabled = if (showNextButton) true else (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS),
                                modifier = Modifier.weight(1f).height(46.dp).testTag(if (showNextButton) "next_puzzle_button_inline" else "undo_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (showNextButton) MedievalEmerald else Color(state.selectedTheme.surfaceColor).copy(alpha = 0.4f),
                                    disabledContainerColor = (if (showNextButton) MedievalEmerald else Color(state.selectedTheme.surfaceColor).copy(alpha = 0.4f)).copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (showNextButton) ColorEmeraldLight else if (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS) accentColor else accentColor.copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(imageVector = if (showNextButton) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Undo, contentDescription = if (showNextButton) "Ván tiếp theo" else "Hoàn tác", tint = if (showNextButton) Color.White else if (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS) accentColor else accentColor.copy(alpha = 0.4f), modifier = Modifier.size(22.dp))
                            }
                        }

                        Button(
                            onClick = { onRestartGame() },
                            enabled = !state.isAiThinking && (state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal),
                            modifier = Modifier.weight(1.1f).height(46.dp).testTag("restart_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.4f),
                                disabledContainerColor = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (!state.isAiThinking && (state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal)) accentColor else accentColor.copy(alpha = 0.4f)),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Chơi lại", tint = accentColor, modifier = Modifier.size(20.dp))
                        }

                        Button(
                            onClick = { onNavigateToMenu() },
                            enabled = (state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal) && !state.isAiThinking,
                            modifier = Modifier.weight(1f).height(46.dp).testTag("home_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.4f),
                                disabledContainerColor = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, if ((state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal) && !state.isAiThinking) accentColor else accentColor.copy(alpha = 0.4f)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Home, contentDescription = "Trang chủ", tint = if ((state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal) && !state.isAiThinking) accentColor else accentColor.copy(alpha = 0.4f), modifier = Modifier.size(22.dp))
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
                onCancel = onCancelRestart,
                selectedTheme = state.selectedTheme
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
                onDismiss = onCloseGeneralSettingsModal,
                selectedTheme = state.selectedTheme,
                isHintEnabled = state.isHintEnabled,
                isResignEnabled = state.isResignEnabled,
                isUndoEnabled = state.isUndoEnabled,
                onHintToggled = onSetHintEnabled,
                onResignToggled = onSetResignEnabled,
                onUndoToggled = onSetUndoEnabled
            )
        }

        if (state.showCheckPopup) {
            CheckPopupDialog(onDismiss = { onDismissCheckPopup() }, selectedTheme = state.selectedTheme)
        }

        if (state.showGameOverModal) {
            GameOverDialog(
                gameStatus = state.gameStatus,
                winner = state.winner,
                userColor = state.userColor,
                gameMode = state.gameMode,
                difficulty = state.difficulty,
                timestamp = state.matchEndTimestamp,
                scoringScore = state.scoringScore,
                onPlayAgain = onNavigateToSetup, // "Đổi chế độ"
                onRestart = onRestartGame,        // "Chơi lại"
                onDismiss = onCloseGameOverModal,
                onNextMatch = if (state.gameStatus == GameStatus.CHECKMATE && state.winner == state.userColor && !state.isLastPuzzleInCategory) onNextPuzzle else null,
                selectedTheme = state.selectedTheme
            )
        }

        state.pendingPromotionMove?.let { move ->
            PawnPromotionDialog(
                color = move.piece.color,
                onSelectPiece = { type -> onCompletePromotion(type) },
                viewMode = if (state.gameMode == GameMode.TWO_PLAYERS) BoardViewMode.VIEW_2D else state.boardViewMode,
                selectedTheme = state.selectedTheme
            )
        }
    }
}
}


@Preview(showBackground = true, name = "Puzzles Screen Portrait", widthDp = 384, heightDp = 854)
@Composable
fun PuzzlesScreenPreview() {
    MyApplicationTheme {
        PuzzlesScreen(
            state = ChessUiState(
                gameMode = GameMode.PUZZLE,
                gameStatus = GameStatus.IN_PROGRESS,
                selectedTheme = ChessTheme.CLASSIC,
                puzzleCategory = "Trung bình",
                puzzleLevel = 5
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

@Preview(showBackground = true, name = "Puzzles Screen Landscape", widthDp = 854, heightDp = 384)
@Composable
fun PuzzlesScreenLandscapePreview() {
    MyApplicationTheme {
        PuzzlesScreen(
            state = ChessUiState(
                gameMode = GameMode.PUZZLE,
                gameStatus = GameStatus.IN_PROGRESS,
                selectedTheme = ChessTheme.CLASSIC,
                puzzleCategory = "Trung bình",
                puzzleLevel = 5
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
