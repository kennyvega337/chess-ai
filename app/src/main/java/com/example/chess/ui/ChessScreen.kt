package com.example.chess.ui

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chess.model.GameStatus
import com.example.chess.model.PieceColor
import com.example.ui.theme.MedievalDarkWood
import com.example.ui.theme.MedievalGold
import com.example.ui.theme.MedievalGoldLight
import com.example.ui.theme.MedievalParchment
import com.example.ui.theme.MedievalParchmentDark

import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.app.Activity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessScreen(
    viewModel: ChessViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Ensure system navigation bar is hidden and top status bar icons (time, battery, etc.) are white
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MedievalDarkWood,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "⚔️ CỜ VUA TRUNG CỔ ⚔️",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MedievalGold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Thách Đấu Máy Hoàng Gia",
                            fontSize = 11.sp,
                            color = MedievalParchmentDark
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (state.gameStatus == GameStatus.IN_PROGRESS && state.currentTurn == state.userColor && !state.isAiThinking) {
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
                    IconButton(
                        onClick = { viewModel.openSideSelectionModal() },
                        modifier = Modifier.testTag("new_game_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Ván mới",
                            tint = MedievalGoldLight
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF22140A)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Opponent (AI) Card at Top
            PlayerCard(
                isUser = false,
                playerColor = state.userColor.opposite,
                isCurrentTurn = state.currentTurn == state.userColor.opposite,
                isAiThinking = state.isAiThinking,
                capturedPieces = if (state.userColor == PieceColor.WHITE) state.capturedWhitePieces else state.capturedBlackPieces
            )

            // Small warning banner for ongoing Check
            AnimatedVisibility(
                visible = state.isCheck && state.gameStatus == GameStatus.IN_PROGRESS,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = Color(0xFF8B0000),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MedievalGold),
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .testTag("check_warning_banner")
                ) {
                    Text(
                        text = if (state.currentTurn == state.userColor) "⚠️ VUA CỦA BẠN ĐANG BỊ CHIẾU!" else "⚠️ CHIẾU VUA MÁY!",
                        color = MedievalGoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                    )
                }
            }

            // Chess Board View in Center
            ChessBoardView(
                board = state.board,
                userColor = state.userColor,
                selectedPosition = state.selectedPosition,
                legalMoves = state.legalMovesForSelected,
                lastMove = state.lastMove,
                hintMove = state.hintMove,
                isCheck = state.isCheck,
                currentTurn = state.currentTurn,
                onSquareClick = { pos -> viewModel.onSquareClick(pos) },
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // User Player Card
            PlayerCard(
                isUser = true,
                playerColor = state.userColor,
                isCurrentTurn = state.currentTurn == state.userColor,
                capturedPieces = if (state.userColor == PieceColor.WHITE) state.capturedBlackPieces else state.capturedWhitePieces
            )

            // Game Action Controls at Bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hoàn Tác
                OutlinedButton(
                    onClick = { viewModel.undoMove() },
                    enabled = state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("undo_button"),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (state.moveHistory.isNotEmpty() && !state.isAiThinking && state.gameStatus == GameStatus.IN_PROGRESS) MedievalGold else MedievalGold.copy(alpha = 0.4f)),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Undo, contentDescription = null, tint = MedievalGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hoàn Tác", color = MedievalGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // Đầu Hàng
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

                // Ván Mới
                Button(
                    onClick = { viewModel.openSideSelectionModal() },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("new_match_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF382315)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MedievalGold),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = MedievalGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ván Mới", color = MedievalGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Modals and Dialogs
        val isUserWinner = state.winner == state.userColor && (state.gameStatus == GameStatus.CHECKMATE || state.gameStatus == GameStatus.RESIGNED)
        if (isUserWinner) {
            FireworksOverlay(modifier = Modifier.fillMaxSize())
        }

        if (state.showSideSelectionModal) {
            SideSelectionDialog(
                onSideSelected = { side -> viewModel.selectSide(side) },
                onDismiss = if (state.gameStatus == GameStatus.IN_PROGRESS) {
                    { viewModel.closeSideSelectionModal() }
                } else null
            )
        }

        if (state.showResignConfirmationModal) {
            ResignConfirmationDialog(
                onConfirmResign = { viewModel.confirmResign() },
                onCancel = { viewModel.cancelResign() }
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
                onPlayAgain = { viewModel.openSideSelectionModal() }
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
