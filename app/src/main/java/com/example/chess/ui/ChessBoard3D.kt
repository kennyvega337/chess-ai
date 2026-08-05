package com.example.chess.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.chess.engine.ChessBoard
import com.example.chess.model.*

@Composable
fun ChessBoard3D(
    board: ChessBoard,
    userColor: PieceColor,
    selectedPosition: Position?,
    legalMoves: List<Move>,
    hintMove: Move?,
    onSquareClick: (Position) -> Unit,
    theme: ChessTheme = ChessTheme.CLASSIC,
    modifier: Modifier = Modifier
) {
    val lightSquareColor = Color(theme.lightSquareColor)
    val darkSquareColor = Color(theme.darkSquareColor)

    // Xác định thứ tự vẽ hàng dựa trên màu người chơi (để người chơi luôn ở dưới)
    val rows = if (userColor == PieceColor.WHITE) (0..7).toList() else (7 downTo 0).toList()

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val boardSize = minOf(this.maxWidth, this.maxHeight)
        val squareSize = boardSize / 8f

        // Container chính cho bàn cờ hình vuông chuẩn
        Box(modifier = Modifier.size(boardSize)) {
            // Lớp 1: Bàn cờ, Highlight và Border (Luôn nằm dưới)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(3.dp, darkSquareColor.copy(alpha = 0.7f)) // Màu viền theo theme
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    for (r in rows) {
                        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            for (vc in 0..7) {
                                val c = if (userColor == PieceColor.WHITE) vc else 7 - vc
                                val pos = Position(r, c)
                                val isLightSquare = (r + c) % 2 == 0
                                
                                val isSelected = selectedPosition == pos
                                val isLegalTarget = legalMoves.any { it.to == pos }
                                val isHint = hintMove?.to == pos

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(if (isLightSquare) lightSquareColor else darkSquareColor)
                                        .clickable { onSquareClick(pos) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Hiệu ứng Highlight
                                    if (isSelected) Box(Modifier.fillMaxSize().background(Color(0x8816A34A)))
                                    if (isLegalTarget) {
                                        Box(
                                            modifier = Modifier
                                                .size(squareSize * 0.35f)
                                                .background(Color(0xAA22C55E), CircleShape)
                                        )
                                    }
                                    if (isHint) Box(Modifier.fillMaxSize().border(3.dp, Color.Yellow.copy(alpha = 0.7f)))
                                }
                            }
                        }
                    }
                }
            }

            // Lớp 2: Quân cờ (Luôn nằm trên cùng, sử dụng ảnh PNG cho chế độ 3D)
            Column(modifier = Modifier.fillMaxSize()) {
                for (r in rows) {
                    Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        for (vc in 0..7) {
                            val c = if (userColor == PieceColor.WHITE) vc else 7 - vc
                            val pos = Position(r, c)
                            val piece = board.getPiece(pos)

                            Box(
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (piece != null) {
                                    val resId = getPieceDrawable3D(piece, userColor)
                                    Image(
                                        painter = painterResource(id = resId),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize(1f)
                                            .graphicsLayer {
                                                scaleX = 1.5f
                                                scaleY = 1.5f
                                                transformOrigin = TransformOrigin(0.5f, 1f)
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF22140A)
@Composable
fun ChessBoard3DPreview() {
    val mockBoard = remember { ChessBoard() }
    Box(modifier = Modifier.size(400.dp)) {
        ChessBoard3D(
            board = mockBoard,
            userColor = PieceColor.WHITE,
            selectedPosition = Position(6, 4), 
            legalMoves = listOf(
                Move(Position(6, 4), Position(4, 4), mockBoard.getPiece(Position(6, 4))!!),
                Move(Position(6, 4), Position(5, 4), mockBoard.getPiece(Position(6, 4))!!)
            ),
            hintMove = null,
            onSquareClick = {},
            theme = ChessTheme.WOOD
        )
    }
}

@Composable
private fun getPieceDrawable3D(piece: Piece, userColor: PieceColor): Int {
    val prefix = if (piece.color == userColor) "user" else "enemy"
    val typeStr = when (piece.type) {
        PieceType.PAWN -> "pawn"
        PieceType.KNIGHT -> "knight"
        PieceType.BISHOP -> "bishop"
        PieceType.ROOK -> "rook"
        PieceType.QUEEN -> "queen"
        PieceType.KING -> "king"
    }
    val colorStr = if (piece.color == PieceColor.WHITE) "white" else "black"
    
    return when ("${prefix}_${typeStr}_${colorStr}") {
        "user_pawn_white" -> R.drawable.user_pawn_white
        "user_pawn_black" -> R.drawable.user_pawn_black
        "enemy_pawn_white" -> R.drawable.enemy_pawn_white
        "enemy_pawn_black" -> R.drawable.enemy_pawn_black
        "user_knight_white" -> R.drawable.user_knight_white
        "user_knight_black" -> R.drawable.user_knight_black
        "enemy_knight_white" -> R.drawable.enemy_knight_white
        "enemy_knight_black" -> R.drawable.enemy_knight_black
        "user_bishop_white" -> R.drawable.user_bishop_white
        "user_bishop_black" -> R.drawable.user_bishop_black
        "enemy_bishop_white" -> R.drawable.enemy_bishop_white
        "enemy_bishop_black" -> R.drawable.enemy_bishop_black
        "user_rook_white" -> R.drawable.user_rook_white
        "user_rook_black" -> R.drawable.user_rook_black
        "enemy_rook_white" -> R.drawable.enemy_rook_white
        "enemy_rook_black" -> R.drawable.enemy_rook_black
        "user_queen_white" -> R.drawable.user_queen_white
        "user_queen_black" -> R.drawable.user_queen_black
        "enemy_queen_white" -> R.drawable.enemy_queen_white
        "enemy_queen_black" -> R.drawable.enemy_queen_black
        "user_king_white" -> R.drawable.user_king_white
        "user_king_black" -> R.drawable.user_king_black
        "enemy_king_white" -> R.drawable.enemy_king_white
        "enemy_king_black" -> R.drawable.enemy_king_black
        else -> R.drawable.user_pawn_white
    }
}
