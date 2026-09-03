package com.example.chess.ui

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun ScoringScreen(
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
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val useLandscapeLayout = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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
                                        text = "Thử Thách Ghi Điểm",
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
                    // COL 1: Điều khiển & Điểm số
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(horizontal = 3.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        if (state.isHintEnabled) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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

                        ScoringProgress(
                            score = state.scoringScore,
                            timeMillis = if (state.userColor == PieceColor.WHITE) state.whiteTimeMillis else state.blackTimeMillis,
                            accentColor = accentColor,
                            selectedTheme = state.selectedTheme,
                            scoringMode = state.selectedScoringMode
                        )

                        Spacer(modifier = Modifier.height(8.dp))

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
                                title = "ĐIỂM: ${state.scoringScore}",
                                timeMillis = if (state.userColor == PieceColor.WHITE) state.whiteTimeMillis else state.blackTimeMillis,
                                timerOption = state.timerOption,
                                selectedTheme = state.selectedTheme,
                                onClick = {}
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // COL 2: Bàn cờ
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
                            gameStatus = GameStatus.IN_PROGRESS, // Keep it in progress for preview
                            winner = state.winner,
                            gameMode = state.gameMode,
                            onSquareClick = { pos -> onSquareClick(pos) },
                            theme = state.selectedTheme,
                            viewMode = state.boardViewMode,
                            isMoveHintsEnabled = state.isMoveHintsEnabled,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // COL 3: Menu & Settings
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
                                enabled = !state.isAiThinking,
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
                        ScoringProgress(
                            score = state.scoringScore,
                            timeMillis = if (state.userColor == PieceColor.WHITE) state.whiteTimeMillis else state.blackTimeMillis,
                            accentColor = accentColor,
                            selectedTheme = state.selectedTheme,
                            scoringMode = state.selectedScoringMode,
                            modifier = Modifier.padding(top = 8.dp)
                        )
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
                            gameStatus = GameStatus.IN_PROGRESS,
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
                        PlayerCard(
                            isUser = true,
                            playerColor = state.userColor,
                            isCurrentTurn = state.currentTurn == state.userColor,
                            isAiThinking = state.isAiThinking,
                            capturedPieces = if (state.userColor == PieceColor.WHITE) state.capturedBlackPieces else state.capturedWhitePieces,
                            gameMode = state.gameMode,
                            gameStatus = state.gameStatus,
                            winner = state.winner,
                            title = "Người chơi",
                            timeMillis = if (state.userColor == PieceColor.WHITE) state.whiteTimeMillis else state.blackTimeMillis,
                            timerOption = state.timerOption,
                            selectedTheme = state.selectedTheme,
                            onClick = {}
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val isGameOver = state.gameStatus != GameStatus.IN_PROGRESS && state.gameStatus != GameStatus.NOT_STARTED

                            if (isGameOver) {
                                Button(
                                    onClick = { onRestartGame() },
                                    modifier = Modifier.weight(1.1f).height(46.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Chơi lại", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = { onNavigateToMenu() },
                                enabled = !state.isAiThinking,
                                modifier = if (isGameOver) Modifier.weight(1f).height(46.dp) else Modifier.fillMaxWidth().height(46.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(state.selectedTheme.surfaceColor).copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor)
                            ) {
                                Icon(imageVector = Icons.Default.Home, contentDescription = "Trang chủ", tint = accentColor, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Trang chủ", color = accentColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Dialogs
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

        if (state.showGameOverModal) {
            ChallengeResultDialog(
                score = state.scoringScore,
                gameMode = state.gameMode,
                selectedTheme = state.selectedTheme,
                scoringMode = state.selectedScoringMode,
                onRestart = onRestartGame,
                onHome = onNavigateToSetup,
                onDismiss = onCloseGameOverModal
            )
        }

        if (state.showRestartConfirmationModal) {
            RestartConfirmationDialog(
                onConfirmRestart = { onConfirmRestart() },
                onCancel = { onCancelRestart() },
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

@Composable
private fun ScoringProgress(
    score: Int,
    timeMillis: Long,
    accentColor: Color,
    selectedTheme: ChessTheme,
    scoringMode: ChessScoreMode,
    modifier: Modifier = Modifier
) {
    val maxScore = scoringMode.score
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scoreAnimation"
    )
    val progress = (animatedScore / maxScore).coerceIn(0f, 1f)
    
    val minutes = (timeMillis / 1000) / 60
    val seconds = (timeMillis / 1000) % 60
    val timeStr = String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Styled Timer Badge
        Surface(
            color = Color(selectedTheme.surfaceColor).copy(alpha = 0.9f),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor),
            modifier = Modifier.shadow(8.dp, RoundedCornerShape(20.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "⏳",
                    fontSize = 16.sp
                )
                Text(
                    text = timeStr,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Bar Container
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Track with Inner Shadow look
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(Color.Black.copy(alpha = 0.2f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(7.dp))
                )

                // Milestones markers
                val milestones = scoringMode.progressLevel
                BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)) {
                    val width = this.maxWidth
                    milestones.forEach { milestone ->
                        val isReached = score >= milestone
                        val milestoneProgress = milestone.toFloat() / maxScore
                        val xOffset = width * milestoneProgress
                        
                        Box(
                            modifier = Modifier
                                .offset(x = xOffset - 6.dp)
                                .size(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Glow effect for reached milestones
                            if (isReached) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .blur(4.dp)
                                        .background(accentColor.copy(alpha = 0.5f), CircleShape)
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .size(if (isReached) 10.dp else 7.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isReached) accentColor 
                                        else Color(selectedTheme.surfaceColor).copy(alpha = 0.8f)
                                    )
                                    .border(
                                        width = if (isReached) 1.5.dp else 1.dp,
                                        color = if (isReached) Color.White else accentColor.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }

                // Animated Progress Indicator
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp)),
                    color = accentColor,
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Round
                )

                // Diamond Thumb (Square rotated 45 degrees)
                BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)) {
                    val width = this.maxWidth
                    val xOffset = width * progress
                    
                    Box(
                        modifier = Modifier
                            .offset(x = xOffset - 8.dp)
                            .size(16.dp)
                            .graphicsLayer(rotationZ = 45f)
                            .shadow(4.dp, RoundedCornerShape(2.dp))
                            .background(accentColor, RoundedCornerShape(2.dp))
                            .border(1.5.dp, Color.White, RoundedCornerShape(2.dp))
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Score Badge
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = accentColor
                )
                Text(
                    text = "/$maxScore",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // AI Level Info
        Text(
            text = "Hãy cố gắng bắt nhiều quân nhất có thể",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = accentColor.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScoringScreenPreview() {
    val state = ChessUiState(
        scoringScore = 15,
        whiteTimeMillis = 300000, // 5 minutes
        gameStatus = GameStatus.IN_PROGRESS,
        selectedTheme = ChessTheme.CLASSIC,
        selectedScoringMode = ChessScoreMode.Score300s
    )
    MyApplicationTheme(selectedTheme = state.selectedTheme) {
        ScoringScreen(
            state = state,
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
            onShowMessage = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 892, heightDp = 411)
@Composable
fun ScoringScreenLandscapePreview() {
    val state = ChessUiState(
        scoringScore = 25,
        whiteTimeMillis = 600000, // 10 minutes
        gameStatus = GameStatus.IN_PROGRESS,
        selectedTheme = ChessTheme.MYSTIC,
        selectedScoringMode = ChessScoreMode.Score300s
    )
    MyApplicationTheme(selectedTheme = state.selectedTheme) {
        ScoringScreen(
            state = state,
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
            onShowMessage = {}
        )
    }
}
