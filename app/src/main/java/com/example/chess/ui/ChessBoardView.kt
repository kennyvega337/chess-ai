package com.example.chess.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.chess.engine.ChessBoard
import com.example.chess.model.*

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
    modifier: Modifier = Modifier
) {
    val kingInCheckPos = if (isCheck) board.findKing(currentTurn) else null

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        when (viewMode) {
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
                    onSquareClick = onSquareClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
