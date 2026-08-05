package com.example.chess.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.engine.ChessBoard
import com.example.chess.model.ChessTheme
import com.example.chess.model.Move
import com.example.chess.model.PieceColor
import com.example.chess.model.Position
import com.example.ui.theme.BoardCheckRed

@Composable
fun ChessBoard2D(
    board: ChessBoard,
    userColor: PieceColor,
    selectedPosition: Position?,
    legalMoves: List<Move>,
    playerLastMove: Move?,
    aiLastMove: Move?,
    hintMove: Move?,
    kingInCheckPos: Position?,
    theme: ChessTheme,
    onSquareClick: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    val lightSquareColor = Color(theme.lightSquareColor)
    val darkSquareColor = Color(theme.darkSquareColor)

    val rows = if (userColor == PieceColor.WHITE) (0..7).toList() else (7 downTo 0).toList()
    val cols = if (userColor == PieceColor.WHITE) (0..7).toList() else (7 downTo 0).toList()

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val boardSize = minOf(maxWidth, maxHeight)
        val squareSize = boardSize / 8f

        Column(modifier = Modifier.size(boardSize)) {
            for (r in rows) {
                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    for (c in cols) {
                        val pos = Position(r, c)
                        val piece = board.getPiece(pos)
                        val isLightSquare = (r + c) % 2 == 0
                        
                        val isSelected = selectedPosition == pos
                        val isLegalTarget = legalMoves.any { it.to == pos }
                        val isPlayerLastMove = playerLastMove?.from == pos || playerLastMove?.to == pos
                        val isAiLastMove = aiLastMove?.from == pos || aiLastMove?.to == pos
                        val isCheckSquare = kingInCheckPos == pos
                        val isHint = hintMove?.to == pos

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(if (isLightSquare) lightSquareColor else darkSquareColor)
                                .clickable { onSquareClick(pos) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCheckSquare) Box(Modifier.fillMaxSize().background(BoardCheckRed))
                            if (isSelected) Box(Modifier.fillMaxSize().background(Color(0x8816A34A)))
                            if (isAiLastMove) Box(Modifier.fillMaxSize().background(Color(0x66F59E0B)))
                            if (isPlayerLastMove) Box(Modifier.fillMaxSize().background(Color(0x6622C55E)))

                            if (piece != null) {
                                Text(
                                    text = piece.symbol,
                                    fontSize = (squareSize.value * 0.7f).sp,
                                    color = if (piece.color == PieceColor.WHITE) Color.White else Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (isLegalTarget) {
                                Box(
                                    modifier = Modifier
                                        .size(squareSize * 0.3f)
                                        .background(Color(0xAA22C55E), CircleShape)
                                )
                            }
                            
                            if (isHint) {
                                Box(Modifier.fillMaxSize().border(3.dp, Color.Yellow))
                            }
                        }
                    }
                }
            }
        }
    }
}
