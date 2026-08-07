package com.example.chess.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.Text
import com.example.R
import com.example.chess.engine.ChessBoard
import com.example.chess.model.*

@Composable
fun ChessBoard3D(
    board: ChessBoard,
    userColor: PieceColor,
    selectedPosition: Position?,
    legalMoves: List<Move>,
    playerLastMove: Move? = null,
    aiLastMove: Move? = null,
    hintMove: Move?,
    kingInCheckPos: Position? = null,
    checkingPieces: List<Position> = emptyList(),
    onSquareClick: (Position) -> Unit,
    theme: ChessTheme = ChessTheme.CLASSIC,
    modifier: Modifier = Modifier
) {
    val lightSquareColor = Color(theme.lightSquareColor)
    val darkSquareColor = Color(theme.darkSquareColor)

    val rows = if (userColor == PieceColor.WHITE) (0..7).toList() else (7 downTo 0).toList()

    val configuration = LocalConfiguration.current
    val isLargeScreen = configuration.smallestScreenWidthDp >= 600

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val maxAvailable = minOf(this.maxWidth, this.maxHeight)

        val availableSize = if (isLargeScreen) {
            // Nhận diện trạng thái màn hình gập hoặc máy tính bảng dựa trên chênh lệch kích thước
            val diffPx = Math.abs(maxHeight.value - maxWidth.value)
            Log.d("LOG_Chess", "W" +maxWidth.value + " H" + maxHeight.value )
            if (diffPx < 300) {
                maxAvailable * 0.8f  // Màn hình gập mở rộng hoặc Tablet vuông: 80%
            } else {
                maxAvailable * 0.95f  // Tablet/Foldable trạng thái thuôn dài: 90%
            }
        } else {
            maxAvailable // Điện thoại thường không đổi
        }

        // Thu nhỏ border chỉ vừa đủ cho số và chữ (15dp thay vì 20dp)
        val boardBorderSize = 15.dp
        val boardSize = availableSize - (boardBorderSize * 2) - 5.dp
        val squareSize = boardSize / 8f

        // 1. LỚP NỀN VÀ BIÊN NGOÀI (Nằm dưới cùng)
        Box(
            modifier = Modifier
                .size(boardSize + (boardBorderSize * 2))
                .background(Color(0xFF2C190E), RoundedCornerShape(4.dp))
                .border(2.5.dp, Color(0xFFD4AF37), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Vẽ tọa độ Chữ (a-h)
            for (i in 0..7) {
                val letter = if (userColor == PieceColor.WHITE) ('a' + i).toString() else ('h' - i).toString()
                Text(
                    text = letter,
                    color = Color(0xEEFFFFFF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = boardBorderSize + (squareSize * i) + (squareSize / 2) - 4.dp, y = (-5).dp)
                )
            }

            // Vẽ tọa độ Số (1-8)
            for (i in 0..7) {
                val number = if (userColor == PieceColor.WHITE) (8 - i).toString() else (i + 1).toString()
                Text(
                    text = number,
                    color = Color(0xEEFFFFFF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 5.dp, y = boardBorderSize + (squareSize * i) + (squareSize / 2) - 6.dp)
                )
            }

            // 2. BÀN CỜ CHÍNH (Ô cờ và Highlights)
            Box(modifier = Modifier.size(boardSize)) {
                // Lớp biên đen mờ cho bàn cờ
                Box(Modifier.fillMaxSize().border(1.dp, Color.Black.copy(alpha = 0.4f)))

                // Lớp ô cờ
                Column(modifier = Modifier.fillMaxSize()) {
                    for (r in rows) {
                        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            for (vc in 0..7) {
                                val c = if (userColor == PieceColor.WHITE) vc else 7 - vc
                                val isLightSquare = (r + c) % 2 == 0
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(if (isLightSquare) lightSquareColor else darkSquareColor)
                                )
                            }
                        }
                    }
                }

                // Lớp Highlights
                Column(modifier = Modifier.fillMaxSize()) {
                    for (r in rows) {
                        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            for (vc in 0..7) {
                                val c = if (userColor == PieceColor.WHITE) vc else 7 - vc
                                val pos = Position(r, c)
                                val currentPiece = board.getPiece(pos)
                                
                                val isSelected = selectedPosition == pos
                                val isLegalTarget = legalMoves.any { it.to == pos }
                                val isHint = hintMove?.to == pos
                                val isPlayerLastMove = playerLastMove?.from == pos || playerLastMove?.to == pos
                                val isAiLastMove = aiLastMove?.from == pos || aiLastMove?.to == pos
                                val isCheckSquare = kingInCheckPos == pos
                                val isCheckingPiece = checkingPieces.contains(pos)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable { onSquareClick(pos) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    val move = legalMoves.find { it.to == pos }
                                    val isCastling = move?.isCastling == true
                                    val isCapture = move?.capturedPiece != null

                                    // Special highlight for the ROOK participating in a castling move
                                    val selectedPiece = selectedPosition?.let { board.getPiece(it) }
                                    val isParticipatingRook = if (selectedPiece?.type == PieceType.KING) {
                                        val row = selectedPosition.row
                                        val isKingsideLegal = legalMoves.any { it.to.col == 6 && it.isCastling }
                                        val isQueensideLegal = legalMoves.any { it.to.col == 2 && it.isCastling }
                                        (isKingsideLegal && pos.row == row && pos.col == 7) || 
                                        (isQueensideLegal && pos.row == row && pos.col == 0)
                                    } else false

                                    if (isParticipatingRook) {
                                        Box(Modifier.fillMaxSize().background(Color(0x440EA5E9)))
                                        Box(Modifier.fillMaxSize().border(2.dp, Color(0xFF0EA5E9).copy(alpha = 0.6f)))
                                    }

                                    if (isCheckSquare) {
                                        Box(Modifier.fillMaxSize().background(Color(0xFFB91C1C)))
                                        val borderColor = if (currentPiece?.color == userColor) Color(0xFF22C55E) else Color(0xFFF59E0B)
                                        Box(Modifier.fillMaxSize().border(4.dp, borderColor))
                                    }
                                    if (isCheckingPiece) {
                                        Box(Modifier.fillMaxSize().background(Color(0xFFEF4444).copy(alpha = 0.8f)))
                                        val borderColor = if (currentPiece?.color == userColor) Color(0xFF22C55E) else Color(0xFFF59E0B)
                                        Box(Modifier.fillMaxSize().border(4.dp, borderColor))
                                    }
                                    if (isSelected) Box(Modifier.fillMaxSize().background(Color(0x8816A34A)))
                                    if (isAiLastMove) Box(Modifier.fillMaxSize().background(Color(0x66F59E0B)))
                                    if (isPlayerLastMove) Box(Modifier.fillMaxSize().background(Color(0x6622C55E)))
                                    if (isLegalTarget) {
                                        if (isCastling) {
                                            // Special Blue highlight for Castling target
                                            Box(
                                                modifier = Modifier
                                                    .size(squareSize * 0.45f)
                                                    .background(Color(0xFF0EA5E9), CircleShape)
                                                    .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                                            )
                                            Text("🛡️", fontSize = 10.sp)
                                        } else if (isCapture) {
                                            Box(modifier = Modifier.fillMaxSize().border(3.dp, Color.Red))
                                        } else {
                                            Box(modifier = Modifier.size(squareSize * 0.35f).background(Color(0xAA22C55E), CircleShape))
                                        }
                                    }
                                    if (isHint) Box(Modifier.fillMaxSize().border(3.dp, Color.Yellow.copy(alpha = 0.7f)))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. QUÂN CỜ (Lớp trên cùng - Vẽ ngoài Box bàn cờ để KHÔNG bị Border che khuất)
        Box(modifier = Modifier.size(boardSize)) {
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
                                    val pieceScale = get3DPieceScale(piece.type, piece.color, userColor)

                                    Image(
                                        painter = painterResource(id = resId),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize(1f)
                                            .graphicsLayer {
                                                scaleX = pieceScale
                                                scaleY = pieceScale
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
            userColor = PieceColor.BLACK,
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

private fun get3DPieceScale(type: PieceType, color: PieceColor, perspective: PieceColor): Float {
    return if (perspective == PieceColor.WHITE) {
        when (color) {
            PieceColor.WHITE -> when (type) {
                PieceType.KING -> 1.5f
                PieceType.QUEEN -> 1.5f
                PieceType.ROOK -> 1.4f
                PieceType.BISHOP -> 1.5f
                PieceType.KNIGHT -> 1.5f
                PieceType.PAWN -> 1.5f
            }
            PieceColor.BLACK -> when (type) {
                PieceType.KING -> 1.5f
                PieceType.QUEEN -> 1.5f
                PieceType.ROOK -> 1.2f
                PieceType.BISHOP -> 1.5f
                PieceType.KNIGHT -> 1.4f
                PieceType.PAWN -> 1.2f
            }
        }
    } else {
        when (color) {
            PieceColor.WHITE -> when (type) {
                PieceType.KING -> 1.5f
                PieceType.QUEEN -> 1.5f
                PieceType.ROOK -> 1.3f
                PieceType.BISHOP -> 1.5f
                PieceType.KNIGHT -> 1.5f
                PieceType.PAWN -> 1.4f
            }
            PieceColor.BLACK -> when (type) {
                PieceType.KING -> 1.5f
                PieceType.QUEEN -> 1.5f
                PieceType.ROOK -> 1.5f
                PieceType.BISHOP -> 1.5f
                PieceType.KNIGHT -> 1.5f
                PieceType.PAWN -> 1.5f
            }
        }
    }
}
