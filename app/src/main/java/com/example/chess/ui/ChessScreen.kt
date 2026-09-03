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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.chess.model.GameTimerOption
import com.example.chess.model.SpecialTutorialType
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

import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Palette
import androidx.compose.ui.graphics.graphicsLayer
import com.example.GameSetupActivity
import com.example.chess.ui.GameHistoryDialog
import com.example.chess.ui.ChessThemeDialog
import com.example.chess.ui.GeneralSettingsDialog
import com.example.chess.ui.ScoringScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessScreen(
    viewModel: ChessViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Handle navigation trigger for Menu
    LaunchedEffect(state.navigateToMenuTrigger) {
        if (state.navigateToMenuTrigger) {
            viewModel.onMenuNavigationHandled()
            openSetupActivity(context, state)
        }
    }

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

    ChessScreenContent(
        state = state,
        onStartGame = { sideOption, difficulty, gameMode, timerOption, customMinutes ->
            viewModel.startNewGame(sideOption, difficulty, gameMode, timerOption, customMinutes)
        },
        onStartScoring = { side, piece, seconds ->
            viewModel.startScoringMode(side, piece, seconds)
        },
        onStartTutorialPiece = { pieceType ->
            viewModel.startTutorialMode(pieceType)
        },
        onStartSpecialMove = { moveType ->
            viewModel.startSpecialMoveTutorial(moveType)
        },
        onStartPuzzle = { fen, cat, lvl ->
            viewModel.startPuzzleMode(fen, cat, lvl)
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
        onOpenGeneralSettingsModal = { viewModel.openGeneralSettingsModal() },
        onUndoMove = { viewModel.undoMove() },
        onRestartGame = { viewModel.restartGame() },
        onResignRequest = { viewModel.requestResign() },
        onSquareClick = { pos -> viewModel.onSquareClick(pos) },
        onSelectTheme = { viewModel.selectTheme(it) },
        onSetBoardViewMode = { viewModel.setBoardViewMode(it) },
        onSetSoundEnabled = { viewModel.setSoundEnabled(it) },
        onSetMoveHintsEnabled = { viewModel.setMoveHintsEnabled(it) },
        onSetSaveGameEnabled = { viewModel.setSaveGameEnabled(it) },
        onSetHintEnabled = { viewModel.setHintEnabled(it) },
        onSetResignEnabled = { viewModel.setResignEnabled(it) },
        onSetUndoEnabled = { viewModel.setUndoEnabled(it) },
        onCloseThemeModal = { viewModel.closeThemeModal() },
        onCloseCapturedPiecesModal = { viewModel.closeCapturedPiecesModal() },
        onCloseHistoryModal = { viewModel.closeHistoryModal() },
        onCloseGeneralSettingsModal = { viewModel.closeGeneralSettingsModal() },
        onConfirmResign = { viewModel.confirmResign() },
        onCancelResign = { viewModel.cancelResign() },
        onConfirmRestart = { viewModel.confirmRestart() },
        onCancelRestart = { viewModel.cancelRestart() },
        onDismissCheckPopup = { viewModel.dismissCheckPopup() },
        onCloseGameOverModal = { viewModel.closeGameOverModal() },
        onCloseSpecialMoveResult = { viewModel.closeSpecialMoveResult() },
        onCompletePromotion = { type -> viewModel.completePromotion(type) },
        onOpenSetupActivity = { 
            viewModel.requestNavigation(com.example.chess.model.NavigationTarget.MENU)
        },
        onNavigateToSetup = { 
            viewModel.requestNavigation(com.example.chess.model.NavigationTarget.SETUP)
        },
        onConfirmSaveGame = { viewModel.confirmSaveGame(it) },
        onCancelSaveGame = { viewModel.cancelSaveGameDialog() },
        onNextPuzzle = { viewModel.startNextPuzzle() },
        onLoadPersistedGame = { viewModel.loadPersistedGame() },
        onShowMessage = { message -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() }
    )
}

@Composable
fun ChessScreenContent(
    state: ChessUiState,
    onStartGame: (SideOption, DifficultyLevel, GameMode, GameTimerOption, Int?) -> Unit,
    onStartScoring: (SideOption, PieceType, Int) -> Unit,
    onStartTutorialPiece: (PieceType) -> Unit,
    onStartSpecialMove: (SpecialTutorialType) -> Unit,
    onStartPuzzle: (String, String, Int) -> Unit,
    onReturnToCurrentGame: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenCapturedPiecesModal: () -> Unit,
    onShowHint: () -> Unit,
    onOpenThemeModal: () -> Unit,
    onOpenGeneralSettingsModal: () -> Unit,
    onUndoMove: () -> Unit,
    onRestartGame: () -> Unit,
    onResignRequest: () -> Unit,
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
    onCloseCapturedPiecesModal: () -> Unit,
    onCloseHistoryModal: () -> Unit,
    onCloseGeneralSettingsModal: () -> Unit,
    onConfirmResign: () -> Unit,
    onCancelResign: () -> Unit,
    onConfirmRestart: () -> Unit,
    onCancelRestart: () -> Unit,
    onDismissCheckPopup: () -> Unit,
    onCloseGameOverModal: () -> Unit,
    onCloseSpecialMoveResult: () -> Unit,
    onCompletePromotion: (PieceType) -> Unit,
    onOpenSetupActivity: () -> Unit,
    onNavigateToSetup: () -> Unit,
    onConfirmSaveGame: (Boolean) -> Unit,
    onCancelSaveGame: () -> Unit,
    onNextPuzzle: () -> Unit,
    onLoadPersistedGame: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    when (state.currentScreen) {
        AppScreen.SETUP -> {
            GameSetupScreen(
                initialSideOption = state.selectedSideOption,
                initialDifficulty = state.difficulty,
                initialGameMode = state.gameMode,
                initialTimerOption = state.timerOption,
                initialCustomMinutes = 10, // Default for in-app setup
                initialScoringPiece = state.tutorialPiece ?: PieceType.QUEEN,
                initialScoringSeconds = state.selectedScoringMode.time.toInt(),
                onStartGame = onStartGame,
                onStartScoring = onStartScoring,
                onStartTutorialPiece = onStartTutorialPiece,
                onStartSpecialMove = onStartSpecialMove,
                onStartPuzzle = onStartPuzzle,
                onBack = { onOpenSetupActivity() },
                completedPuzzles = state.completedPuzzles,
                lastPuzzleCategory = state.puzzleCategory,
                lastPuzzleLevel = state.puzzleLevel,
                selectedTheme = state.selectedTheme
            )
        }
        AppScreen.GAME -> {
            ChessBoardScreenContent(
                state = state,
                onOpenCapturedPiecesModal = onOpenCapturedPiecesModal,
                onShowHint = onShowHint,
                onOpenThemeModal = onOpenThemeModal,
                onOpenGeneralSettingsModal = onOpenGeneralSettingsModal,
                onUndoMove = onUndoMove,
                onRestartGame = onRestartGame,
                onResignRequest = onResignRequest,
                onSquareClick = onSquareClick,
                onSelectTheme = onSelectTheme,
                onSetBoardViewMode = onSetBoardViewMode,
                onSetSoundEnabled = onSetSoundEnabled,
                onSetMoveHintsEnabled = onSetMoveHintsEnabled,
                onSetSaveGameEnabled = onSetSaveGameEnabled,
                onSetHintEnabled = onSetHintEnabled,
                onSetResignEnabled = onSetResignEnabled,
                onSetUndoEnabled = onSetUndoEnabled,
                onCloseThemeModal = onCloseThemeModal,
                onCloseCapturedPiecesModal = onCloseCapturedPiecesModal,
                onCloseHistoryModal = onCloseHistoryModal,
                onCloseGeneralSettingsModal = onCloseGeneralSettingsModal,
                onConfirmResign = onConfirmResign,
                onCancelResign = onCancelResign,
                onConfirmRestart = onConfirmRestart,
                onCancelRestart = onCancelRestart,
                onDismissCheckPopup = onDismissCheckPopup,
                onCloseGameOverModal = onCloseGameOverModal,
                onCloseSpecialMoveResult = onCloseSpecialMoveResult,
                onCompletePromotion = onCompletePromotion,
                onStartTutorialPiece = onStartTutorialPiece,
                onStartSpecialMove = onStartSpecialMove,
                onNavigateToSetup = onNavigateToSetup,
                onOpenSetupActivity = onOpenSetupActivity,
                onConfirmSaveGame = onConfirmSaveGame,
                onCancelSaveGame = onCancelSaveGame,
                onShowMessage = onShowMessage
            )
        }
        AppScreen.PUZZLE -> {
            PuzzlesScreen(
                state = state,
                onOpenCapturedPiecesModal = onOpenCapturedPiecesModal,
                onShowHint = onShowHint,
                onOpenThemeModal = onOpenThemeModal,
                onOpenGeneralSettingsModal = onOpenGeneralSettingsModal,
                onUndoMove = onUndoMove,
                onRestartGame = onRestartGame,
                onSquareClick = onSquareClick,
                onSelectTheme = onSelectTheme,
                onSetBoardViewMode = onSetBoardViewMode,
                onSetSoundEnabled = onSetSoundEnabled,
                onSetMoveHintsEnabled = onSetMoveHintsEnabled,
                onSetSaveGameEnabled = onSetSaveGameEnabled,
                onSetHintEnabled = onSetHintEnabled,
                onSetResignEnabled = onSetResignEnabled,
                onSetUndoEnabled = onSetUndoEnabled,
                onCloseThemeModal = onCloseThemeModal,
                onCloseGeneralSettingsModal = onCloseGeneralSettingsModal,
                onConfirmRestart = onConfirmRestart,
                onCancelRestart = onCancelRestart,
                onDismissCheckPopup = onDismissCheckPopup,
                onCloseGameOverModal = onCloseGameOverModal,
                onCompletePromotion = onCompletePromotion,
                onNavigateToSetup = onNavigateToSetup,
                onNavigateToMenu = onOpenSetupActivity,
                onNextPuzzle = onNextPuzzle,
                onShowMessage = onShowMessage
            )
        }
        AppScreen.SCORING -> {
            ScoringScreen(
                state = state,
                onOpenCapturedPiecesModal = onOpenCapturedPiecesModal,
                onShowHint = onShowHint,
                onOpenThemeModal = onOpenThemeModal,
                onOpenGeneralSettingsModal = onOpenGeneralSettingsModal,
                onUndoMove = onUndoMove,
                onRestartGame = onRestartGame,
                onSquareClick = onSquareClick,
                onSelectTheme = onSelectTheme,
                onSetBoardViewMode = onSetBoardViewMode,
                onSetSoundEnabled = onSetSoundEnabled,
                onSetMoveHintsEnabled = onSetMoveHintsEnabled,
                onSetSaveGameEnabled = onSetSaveGameEnabled,
                onSetHintEnabled = onSetHintEnabled,
                onSetResignEnabled = onSetResignEnabled,
                onSetUndoEnabled = onSetUndoEnabled,
                onCloseThemeModal = onCloseThemeModal,
                onCloseGeneralSettingsModal = onCloseGeneralSettingsModal,
                onConfirmRestart = onConfirmRestart,
                onCancelRestart = onCancelRestart,
                onDismissCheckPopup = onDismissCheckPopup,
                onCloseGameOverModal = onCloseGameOverModal,
                onCompletePromotion = onCompletePromotion,
                onNavigateToSetup = {
                    navigateToSetupActivity(context, GameMode.SCORING)
                },
                onNavigateToMenu = {
                    openSetupActivity(context, state)
                },
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
    onOpenGeneralSettingsModal: () -> Unit,
    onUndoMove: () -> Unit,
    onRestartGame: () -> Unit,
    onResignRequest: () -> Unit,
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
    onCloseCapturedPiecesModal: () -> Unit,
    onCloseHistoryModal: () -> Unit,
    onCloseGeneralSettingsModal: () -> Unit,
    onConfirmResign: () -> Unit,
    onCancelResign: () -> Unit,
    onConfirmRestart: () -> Unit,
    onCancelRestart: () -> Unit,
    onDismissCheckPopup: () -> Unit,
    onCloseGameOverModal: () -> Unit,
    onCloseSpecialMoveResult: () -> Unit,
    onCompletePromotion: (PieceType) -> Unit,
    onStartTutorialPiece: (PieceType) -> Unit,
    onStartSpecialMove: (SpecialTutorialType) -> Unit,
    onNavigateToSetup: () -> Unit,
    onOpenSetupActivity: () -> Unit,
    onConfirmSaveGame: (Boolean) -> Unit,
    onCancelSaveGame: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val useLandscapeLayout = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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
                            // Row 1: Main Title
                            Text(
                                text = "⚔️ CỜ VUA TRUNG CỔ ⚔️",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = accentColor,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            // Row 2: Mode and Icons
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                            // Left: Back button and Game Mode
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
                                    text = when (state.gameMode) {
                                        GameMode.TWO_PLAYERS -> "Chế Độ 2 Người Chơi"
                                        GameMode.TUTORIAL -> "Hướng Dẫn Quân Cờ"
                                        GameMode.SPECIAL_MOVE -> "Nước Đi Đặc Biệt"
                                        else -> "Thách Đấu Máy (${state.difficulty.displayNameVi})"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(state.selectedTheme.textColor),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Right: Icons
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (state.gameMode != GameMode.TUTORIAL && state.gameMode != GameMode.SPECIAL_MOVE) {
                                    IconButton(
                                        onClick = { onOpenCapturedPiecesModal() },
                                        modifier = Modifier.size(36.dp).testTag("score_captured_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.EmojiEvents,
                                            contentDescription = "Bảng Chiến Tích & Điểm Số",
                                            tint = iconColor,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                if (state.isHintEnabled && state.gameMode != GameMode.TWO_PLAYERS && state.gameMode != GameMode.TUTORIAL && state.gameMode != GameMode.SPECIAL_MOVE) {
                                    IconButton(
                                        onClick = {
                                            if (state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking && state.currentTurn == state.userColor) {
                                                onShowHint()
                                            } else {
                                                onShowMessage("Chưa đến lượt bạn hoặc trò chơi chưa bắt đầu")
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
            // === LANDSCAPE LAYOUT (3 COLUMNS) ===
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
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
                    if (state.gameMode != GameMode.SPECIAL_MOVE) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (state.isUndoEnabled) {
                                ActionIconButton(
                                    icon = Icons.Default.Undo,
                                    contentDesc = "Hoàn tác",
                                    enabled = state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS,
                                    isLandscape = true,
                                    onClick = { onUndoMove() },
                                    color = iconColor,
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
                            if (state.isResignEnabled) {
                                ActionIconButton(
                                    icon = Icons.Default.Flag,
                                    contentDesc = "Đầu hàng",
                                    enabled = state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking,
                                    isLandscape = true,
                                    onClick = { onResignRequest() },
                                    color = Color(0xFFEF4444),
                                    selectedTheme = state.selectedTheme
                                )
                            }
                            if (state.isHintEnabled) {
                                ActionIconButton(
                                    icon = Icons.Default.Lightbulb,
                                    contentDesc = "Gợi ý",
                                    enabled = state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking && state.currentTurn == state.userColor,
                                    isLandscape = true,
                                    onClick = { onShowHint() },
                                    color = iconColor,
                                    selectedTheme = state.selectedTheme
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Row 2: Player 1 Info & Score
                    if (state.gameMode != GameMode.TUTORIAL && state.gameMode != GameMode.SPECIAL_MOVE) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .background(Color(state.selectedTheme.surfaceColor).copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                                .border(1.2.dp, Color(state.selectedTheme.borderColor), RoundedCornerShape(12.dp))
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
                                color = accentColor
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
                                timeMillis = if (state.userColor == PieceColor.WHITE) state.whiteTimeMillis else state.blackTimeMillis,
                                timerOption = state.timerOption,
                                selectedTheme = state.selectedTheme,
                                onClick = if (state.gameMode != GameMode.SPECIAL_MOVE) {{ onOpenCapturedPiecesModal() }} else null
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
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
                        isMoveHintsEnabled = state.isMoveHintsEnabled,
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
                            icon = Icons.Default.Home,
                            contentDesc = "Trang chủ",
                            enabled = !state.isAiThinking,
                            isLandscape = true,
                            onClick = { onOpenSetupActivity() },
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
                            icon = Icons.Default.EmojiEvents,
                            contentDesc = "Chiến tích",
                            isLandscape = true,
                            onClick = { if (state.gameMode != GameMode.SPECIAL_MOVE) onOpenCapturedPiecesModal() },
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

                    // Row 2: Player 2 Info & Score
                    if (state.gameMode != GameMode.TUTORIAL && state.gameMode != GameMode.SPECIAL_MOVE) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .background(Color(state.selectedTheme.surfaceColor).copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                                .border(1.2.dp, Color(state.selectedTheme.borderColor), RoundedCornerShape(12.dp))
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
                                color = Color.White.copy(alpha = 0.8f),
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
                                timeMillis = if (state.userColor == PieceColor.WHITE) state.blackTimeMillis else state.whiteTimeMillis,
                                timerOption = state.timerOption,
                                selectedTheme = state.selectedTheme,
                                onClick = if (state.gameMode != GameMode.SPECIAL_MOVE) {{ onOpenCapturedPiecesModal() }} else null
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
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
                            onSelectPiece = { pieceType -> onStartTutorialPiece(pieceType) },
                            selectedTheme = state.selectedTheme
                        )
                    } else if (state.gameMode == GameMode.SPECIAL_MOVE) {
                        SpecialMoveHeaderBar(
                            currentType = state.specialTutorialType,
                            onSelectType = { type -> onStartSpecialMove(type) },
                            selectedTheme = state.selectedTheme
                        )
                    } else {
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
                                timeMillis = if (state.userColor == PieceColor.WHITE) state.blackTimeMillis else state.whiteTimeMillis,
                                timerOption = state.timerOption,
                                selectedTheme = state.selectedTheme,
                                onClick = if (state.gameMode != GameMode.SPECIAL_MOVE) {{ onOpenCapturedPiecesModal() }} else null
                            )

                            // Opponent Score Badge - Sát Máy
                            Surface(
                                color = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.8f),
                                shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.2.dp,
                                    accentColor.copy(alpha = 0.5f)
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
                                        color = Color(state.selectedTheme.iconActiveColor)
                                    )
                                    Text(
                                        text = "${opponentScoreVal}đ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(state.selectedTheme.textColor)
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
                        isMoveHintsEnabled = state.isMoveHintsEnabled,
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
                    if (state.gameMode != GameMode.TUTORIAL && state.gameMode != GameMode.SPECIAL_MOVE) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // User Score Badge - Sát Bạn
                            Surface(
                                color = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.8f),
                                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.2.dp,
                                    accentColor
                                ),
                                modifier = Modifier.padding(bottom = 0.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    val badgeLabel = if (state.gameMode == GameMode.TWO_PLAYERS) "👑 N.Chơi 1:" else "👑 Bạn:"
                                    Text(
                                        text = badgeLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(state.selectedTheme.iconActiveColor)
                                    )
                                    Text(
                                        text = "${userScoreVal}đ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(state.selectedTheme.textColor)
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
                                timeMillis = if (state.userColor == PieceColor.WHITE) state.whiteTimeMillis else state.blackTimeMillis,
                                timerOption = state.timerOption,
                                selectedTheme = state.selectedTheme,
                                onClick = if (state.gameMode != GameMode.SPECIAL_MOVE) {{ onOpenCapturedPiecesModal() }} else null
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
                        if (state.gameMode == GameMode.TUTORIAL || state.gameMode == GameMode.SPECIAL_MOVE) {
                            Button(
                                onClick = { onOpenSetupActivity() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("new_match_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Trang Chủ", color = accentColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        } else {
                            // 1. NÚT HOÀN TÁC
                            if (state.isUndoEnabled) {
                                Button(
                                    onClick = { onUndoMove() },
                                    enabled = state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("undo_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.4f),
                                        disabledContainerColor = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.2f)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS) accentColor else accentColor.copy(alpha = 0.4f)),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Undo, contentDescription = "Hoàn tác", tint = if (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS) iconColor else iconColor.copy(alpha = 0.4f), modifier = Modifier.size(22.dp))
                                }
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
                                    containerColor = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.4f),
                                    disabledContainerColor = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (!state.isAiThinking && (state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal)) accentColor else accentColor.copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Chơi lại", tint = iconColor, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }

                            // 3. NÚT ĐẦU HÀNG
                            if (state.isResignEnabled) {
                                Button(
                                    onClick = { onResignRequest() },
                                    enabled = state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("resign_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.4f),
                                        disabledContainerColor = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.2f)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        if (state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking) Color(0xFFEF4444) else Color(0xFFEF4444).copy(alpha = 0.4f)
                                    ),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Flag,
                                        contentDescription = "Đầu hàng",
                                        tint = if (state.gameStatus == GameStatus.IN_PROGRESS && !state.isAiThinking) Color(0xFFEF4444) else Color(0xFFEF4444).copy(alpha = 0.4f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // 4. NÚT TRANG CHỦ
                            Button(
                                onClick = { onOpenSetupActivity() },
                                enabled = !state.isAiThinking,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("home_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.4f),
                                    disabledContainerColor = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, if ((state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal) && !state.isAiThinking) accentColor else accentColor.copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(0.0.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Home, contentDescription = "Trang chủ", tint = if ((state.gameStatus == GameStatus.IN_PROGRESS || state.isGameEndControlsEnabled || state.showGameOverModal) && !state.isAiThinking) iconColor else iconColor.copy(alpha = 0.4f), modifier = Modifier.size(22.dp))
                            }
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
                onCancel = { onCancelResign() },
                selectedTheme = state.selectedTheme
            )
        }

        if (state.showRestartConfirmationModal) {
            RestartConfirmationDialog(
                onConfirmRestart = { onConfirmRestart() },
                onCancel = { onCancelRestart() },
                selectedTheme = state.selectedTheme
            )
        }

        if (state.showCapturedPiecesModal && state.gameMode != GameMode.SPECIAL_MOVE) {
            CapturedPiecesDialog(
                state = state,
                onDismiss = { onCloseCapturedPiecesModal() }
            )
        }

        if (state.showHistoryModal) {
            GameHistoryDialog(
                onDismiss = { onCloseHistoryModal() },
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

        // Center Medieval Check Popup Dialog
        if (state.showCheckPopup) {
            CheckPopupDialog(
                onDismiss = { onDismissCheckPopup() },
                selectedTheme = state.selectedTheme
            )
        }

        // Game Over Announcement Dialog
        if (state.showGameOverModal) {
            if (state.gameMode == GameMode.SCORING) {
                ChallengeResultDialog(
                    score = state.scoringScore,
                    gameMode = state.gameMode,
                    selectedTheme = state.selectedTheme,
                    scoringMode = state.selectedScoringMode,
                    onRestart = { onRestartGame() },
                    onHome = { onNavigateToSetup() },
                    onDismiss = { onCloseGameOverModal() }
                )
            } else {
                GameOverDialog(
                    gameStatus = state.gameStatus,
                    winner = state.winner,
                    userColor = state.userColor,
                    gameMode = state.gameMode,
                    difficulty = state.difficulty,
                    timestamp = state.matchEndTimestamp,
                    scoringScore = state.scoringScore,
                    onPlayAgain = { onNavigateToSetup() },
                    onRestart = { onRestartGame() },
                    onDismiss = { onCloseGameOverModal() },
                    selectedTheme = state.selectedTheme
                )
            }
        }

        // Special Move Result Dialog
        if (state.showSpecialMoveResult) {
            SpecialMoveResultDialog(
                isSuccess = state.isSpecialMoveSuccess,
                message = state.specialMoveResultMessage,
                selectedTheme = state.selectedTheme,
                onRestart = { onRestartGame() },
                onHome = { onNavigateToSetup() },
                onDismiss = { onCloseSpecialMoveResult() }
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

        if (state.showSaveGameConfirmationModal) {
            SaveGameConfirmationDialog(
                onConfirm = onConfirmSaveGame,
                onCancel = onCancelSaveGame
            )
        }
    }
}

@Composable
private fun TutorialHeaderBar(
    currentTutorialPiece: PieceType?,
    onSelectPiece: (PieceType) -> Unit,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColor = Color(selectedTheme.accentColor)
    val surfaceColor = Color(selectedTheme.surfaceColor)
    val textColor = Color(selectedTheme.textColor)
    
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
            .border(1.5.dp, accentColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor)
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
                                color = if (isSelected) accentColor else accentColor.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .testTag("tutorial_bar_${type.name.lowercase()}"),
                        color = if (isSelected) accentColor.copy(alpha = 0.25f) else surfaceColor
                    ) {
                        Text(
                            text = label,
                            fontSize = 13.5.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) textColor else textColor.copy(alpha = 0.7f),
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
                    .background(surfaceColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = ruleNote,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "💡 Chấm xanh phát sáng gợi ý nước đi hợp lệ liên tục",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = accentColor
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecialMoveHeaderBar(
    currentType: SpecialTutorialType?,
    onSelectType: (SpecialTutorialType) -> Unit,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColor = Color(selectedTheme.accentColor)
    val surfaceColor = Color(selectedTheme.surfaceColor)
    val textColor = Color(selectedTheme.textColor)

    val selectedType = currentType ?: SpecialTutorialType.CASTLING_KINGSIDE
    val types = SpecialTutorialType.entries

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, accentColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor)
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
                types.forEach { type ->
                    val isSelected = selectedType == type
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectType(type) }
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) accentColor else accentColor.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .testTag("special_move_bar_${type.name.lowercase()}"),
                        color = if (isSelected) accentColor.copy(alpha = 0.25f) else surfaceColor
                    ) {
                        Text(
                            text = type.displayNameVi,
                            fontSize = 13.5.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) textColor else textColor.copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(surfaceColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "⚡ " + selectedType.description,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "💡 Thực hành các nước đi đặc biệt trong cờ vua",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = accentColor
                    )
                }
            }
        }
    }
}

@Composable
internal fun ActionIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDesc: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.tertiary,
    showBorder: Boolean = true,
    isLandscape: Boolean = false,
    selectedTheme: ChessTheme? = null
) {
    val alpha = if (enabled) 1f else 0.4f
    val surfaceColor = selectedTheme?.let { Color(it.surfaceColor) } ?: Color.Black
    
    // Hide border and background automatically in landscape mode
    val effectiveShowBorder = if (isLandscape) false else showBorder

    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(if (isLandscape) 28.dp else 20.dp)
            .then(
                if (isLandscape) Modifier 
                else Modifier.background(surfaceColor.copy(alpha = 0.4f * alpha), CircleShape)
            )
            .then(
                if (effectiveShowBorder) Modifier.border(1.2.dp, color.copy(alpha = alpha), CircleShape)
                else Modifier
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
    val isEligibleMode = state.gameMode == GameMode.VS_AI || state.gameMode == GameMode.TWO_PLAYERS
    val isGameInProgress = state.gameStatus == GameStatus.IN_PROGRESS && isEligibleMode

    val intent = Intent(context, com.example.GameModeSelectionActivity::class.java).apply {
        putExtra(MainActivity.EXTRA_IS_GAME_IN_PROGRESS, isGameInProgress)
        flags = if (isGameInProgress) {
            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        } else {
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
    }
    context.startActivity(intent)
    if (!isGameInProgress) {
        (context as? Activity)?.finish()
    }
}

private fun navigateToSetupActivity(context: Context, gameMode: GameMode) {
    val intent = Intent(context, com.example.GameSetupActivity::class.java).apply {
        putExtra(MainActivity.EXTRA_GAME_MODE, gameMode.name)
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    context.startActivity(intent)
    (context as? Activity)?.finish()
}

@Preview(showBackground = true, widthDp =  418, heightDp = 892)
@Composable
fun ChessScreenPreview() {
    MyApplicationTheme {
        ChessScreenContent(
            state = ChessUiState(
                currentScreen = AppScreen.GAME,
                gameMode = GameMode.VS_AI
            ),
            onStartGame = { _, _, _, _, _ -> },
            onStartScoring = { _, _, _ -> },
            onStartTutorialPiece = {},
            onStartSpecialMove = {},
            onStartPuzzle = { _, _, _ -> },
            onReturnToCurrentGame = {},
            onOpenHistory = {},
            onOpenCapturedPiecesModal = {},
            onShowHint = {},
            onOpenThemeModal = {},
            onOpenGeneralSettingsModal = {},
            onUndoMove = {},
            onRestartGame = {},
            onResignRequest = {},
            onSquareClick = {},
            onSelectTheme = {},
            onSetBoardViewMode = {},
            onSetSoundEnabled = {},
            onSetMoveHintsEnabled = {},
            onSetSaveGameEnabled = {},
            onCloseThemeModal = {},
            onCloseCapturedPiecesModal = {},
            onCloseHistoryModal = {},
            onCloseGeneralSettingsModal = {},
            onConfirmResign = {},
            onCancelResign = {},
            onConfirmRestart = {},
            onCancelRestart = {},
            onDismissCheckPopup = {},
            onCloseGameOverModal = {},
            onCloseSpecialMoveResult = {},
            onCompletePromotion = {},
            onOpenSetupActivity = {},
            onNavigateToSetup = {},
            onConfirmSaveGame = {},
            onCancelSaveGame = {},
            onNextPuzzle = {},
            onLoadPersistedGame = {},
            onShowMessage = {}
        )
    }
}
