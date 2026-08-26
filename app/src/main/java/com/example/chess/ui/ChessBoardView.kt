package com.example.chess.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chess.engine.ChessBoard
import com.example.chess.model.*
import com.example.ui.theme.MyApplicationTheme

@Composable
fun ChessBoardView(
    board: ChessBoard,
    userColor: PieceColor,
    selectedPosition: Position?,
    legalMoves: List<Move>,
    playerLastMove: Move? = null,
    aiLastMove: Move? = null,
    hintMove: Move? = null,
    isCheck: Boolean,
    currentTurn: PieceColor,
    checkingPieces: List<Position> = emptyList(),
    gameStatus: GameStatus = GameStatus.IN_PROGRESS,
    winner: PieceColor? = null,
    gameMode: GameMode = GameMode.VS_AI,
    onSquareClick: (Position) -> Unit,
    theme: ChessTheme = ChessTheme.CLASSIC,
    viewMode: BoardViewMode = BoardViewMode.VIEW_2D,
    isMoveHintsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val kingInCheckPos = if (isCheck) board.findKing(currentTurn) else null

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Enforce 2D view for Two Players mode
        val effectiveViewMode = if (gameMode == GameMode.TWO_PLAYERS) BoardViewMode.VIEW_2D else viewMode

        when (effectiveViewMode) {
            BoardViewMode.VIEW_2D -> {
                ChessBoard2D(
                    board = board,
                    userColor = userColor,
                    selectedPosition = selectedPosition,
                    legalMoves = legalMoves,
                    playerLastMove = playerLastMove,
                    aiLastMove = aiLastMove,
                    hintMove = hintMove,
                    kingInCheckPos = kingInCheckPos,
                    checkingPieces = checkingPieces,
                    gameStatus = gameStatus,
                    winner = winner,
                    gameMode = gameMode,
                    currentTurn = currentTurn,
                    theme = theme,
                    isMoveHintsEnabled = isMoveHintsEnabled,
                    onSquareClick = onSquareClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
            BoardViewMode.VIEW_3D -> {
                ChessBoard3D(
                    board = board,
                    userColor = userColor,
                    selectedPosition = selectedPosition,
                    legalMoves = legalMoves,
                    playerLastMove = playerLastMove,
                    aiLastMove = aiLastMove,
                    hintMove = hintMove,
                    kingInCheckPos = kingInCheckPos,
                    checkingPieces = checkingPieces,
                    gameStatus = gameStatus,
                    winner = winner,
                    gameMode = gameMode,
                    currentTurn = currentTurn,
                    theme = theme,
                    isMoveHintsEnabled = isMoveHintsEnabled,
                    onSquareClick = onSquareClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChessBoardView2DPreview() {
    MyApplicationTheme {
        ChessBoardView(
            board = ChessBoard(true),
            userColor = PieceColor.WHITE,
            selectedPosition = null,
            legalMoves = emptyList(),
            isCheck = false,
            currentTurn = PieceColor.WHITE,
            onSquareClick = {},
            theme = ChessTheme.CLASSIC,
            viewMode = BoardViewMode.VIEW_2D,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChessBoardView3DPreview() {
    MyApplicationTheme {
        ChessBoardView(
            board = ChessBoard(true),
            userColor = PieceColor.WHITE,
            selectedPosition = null,
            legalMoves = emptyList(),
            isCheck = false,
            currentTurn = PieceColor.WHITE,
            onSquareClick = {},
            theme = ChessTheme.CLASSIC,
            viewMode = BoardViewMode.VIEW_3D,
            modifier = Modifier.fillMaxSize()
        )
    }
}
