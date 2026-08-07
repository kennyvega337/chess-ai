package com.example.chess.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.engine.ChessBoard
import com.example.chess.model.ChessTheme
import com.example.chess.model.Move
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Position

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
    checkingPieces: List<Position> = emptyList(),
    theme: ChessTheme,
    onSquareClick: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    val lightSquareColor = Color(theme.lightSquareColor)
    val darkSquareColor = Color(theme.darkSquareColor)

    val rows = if (userColor == PieceColor.WHITE) (0..7).toList() else (7 downTo 0).toList()

    val configuration = LocalConfiguration.current
    val isLargeScreen = configuration.smallestScreenWidthDp >= 600

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val maxAvailable = minOf(maxWidth, maxHeight)
        
        val availableSize = if (isLargeScreen) {
            // Tính toán chênh lệch kích thước để nhận diện trạng thái màn hình gập/tablet
            val diffPx = Math.abs(maxHeight.value - maxWidth.value)
            if (diffPx < 300) {
                maxAvailable * 0.8f  // Màn hình gần vuông (Gập mở/Tablet dọc): 80%
            } else {
                maxAvailable * 0.9f  // Màn hình thuôn dài: 90%
            }
        } else {
            maxAvailable // Điện thoại thường: 100%
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
            // Vẽ tọa độ Chữ (a-h) - Căn giữa trong phần lề
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

            // Vẽ tọa độ Số (1-8) - Căn giữa trong phần lề
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
                // Lớp biên đen mờ cho bàn cờ (không đè quân cờ vì quân cờ sẽ vẽ ở layer sau)
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
                                val isPlayerLastMove = playerLastMove?.from == pos || playerLastMove?.to == pos
                                val isAiLastMove = aiLastMove?.from == pos || aiLastMove?.to == pos
                                val isCheckSquare = kingInCheckPos == pos
                                val isHint = hintMove?.to == pos
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
                                            Box(modifier = Modifier.size(squareSize * 0.3f).background(Color(0xAA22C55E), CircleShape))
                                        }
                                    }
                                    if (isHint) Box(Modifier.fillMaxSize().border(3.dp, Color.Yellow))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. QUÂN CỜ (Vẽ ngoài Box bàn cờ để đảm bảo KHÔNG bị Border đè lên khi scale lớn)
        Box(modifier = Modifier.size(boardSize)) {
            Column(modifier = Modifier.fillMaxSize()) {
                for (r in rows) {
                    Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        for (vc in 0..7) {
                            val c = if (userColor == PieceColor.WHITE) vc else 7 - vc
                            val piece = board.getPiece(Position(r, c))
                            Box(
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (piece != null) {
                                    Text(
                                        text = piece.symbol,
                                        fontSize = (squareSize.value * 0.75f).sp,
                                        color = if (piece.color == PieceColor.WHITE) Color.White else Color.Black,
                                        fontWeight = FontWeight.Bold
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
