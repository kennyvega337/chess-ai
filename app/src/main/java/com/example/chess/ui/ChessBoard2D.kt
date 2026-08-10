package com.example.chess.ui

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.chess.engine.ChessBoard
import com.example.chess.model.ChessTheme
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.chess.model.Move
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Position
import com.example.ui.theme.*

private fun getPieceResource(piece: Piece): Int {
    return when (piece.color) {
        PieceColor.WHITE -> when (piece.type) {
            PieceType.PAWN -> R.drawable.white_pawn
            PieceType.KNIGHT -> R.drawable.white_knight
            PieceType.BISHOP -> R.drawable.white_bishop
            PieceType.ROOK -> R.drawable.white_rook
            PieceType.QUEEN -> R.drawable.white_queen
            PieceType.KING -> R.drawable.white_king
        }
        PieceColor.BLACK -> when (piece.type) {
            PieceType.PAWN -> R.drawable.black_pawn
            PieceType.KNIGHT -> R.drawable.black_knight
            PieceType.BISHOP -> R.drawable.black_bishop
            PieceType.ROOK -> R.drawable.black_rook
            PieceType.QUEEN -> R.drawable.black_queen
            PieceType.KING -> R.drawable.black_king
        }
    }
}

@Composable
fun ChessBoard2D(
    board: ChessBoard,
    userColor: PieceColor,
    selectedPosition: Position?,
    legalMoves: List<Move>,
    aiLastMove: Move?,
    playerLastMove: Move?,
    hintMove: Move?,
    kingInCheckPos: Position?,
    checkingPieces: List<Position> = emptyList(),
    gameStatus: GameStatus = GameStatus.IN_PROGRESS,
    winner: PieceColor? = null,
    gameMode: GameMode = GameMode.VS_AI,
    currentTurn: PieceColor,
    onSquareClick: (Position) -> Unit,
    theme: ChessTheme,
    modifier: Modifier = Modifier
) {
    val lightSquareColor = Color(theme.lightSquareColor)
    val darkSquareColor = Color(theme.darkSquareColor)

    val rows = if (userColor == PieceColor.WHITE) (0..7).toList() else (7 downTo 0).toList()

    val animProgress = remember { Animatable(1f) }
    val currentMove = aiLastMove ?: playerLastMove

    LaunchedEffect(currentMove) {
        if (currentMove != null) {
            animProgress.snapTo(0f)
            animProgress.animateTo(1f, animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing))
        }
    }

    val progress = animProgress.value

    val configuration = LocalConfiguration.current
    val isLargeScreen = configuration.smallestScreenWidthDp >= 600
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    BoxWithConstraints(
        modifier = modifier.padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        val maxAvailable = minOf(this.maxWidth, this.maxHeight)
        
        val availableSize = if (isLandscape) {
            maxAvailable // Ở chế độ xoay ngang, tận dụng tối đa chiều cao
        } else {
            maxAvailable // Màn hình dọc: Luôn lấy 100%
        }
        
        // Thu nhỏ border chỉ vừa đủ cho số và chữ (12dp thay vì 15dp để tăng diện tích bàn cờ)
        val boardBorderSize = 12.dp
        val boardSize = availableSize - (boardBorderSize * 2)
        val squareSize = boardSize / 8f

        // 1. LỚP NỀN VÀ BIÊN NGOÀI (Nằm dưới cùng)

        val borderColor = if (winner != null) {
            if (winner == userColor) {
                ColorRoyalBlue // Người chơi 1 thắng (Xanh dương đậm)
            } else {
                Color.Red         // Đối thủ thắng (Đỏ)
            }
        } else {
            MedievalGold // Mặc định Vàng Gold trong khi chơi
        }

        Box(
            modifier = Modifier
                .size(availableSize) // Sử dụng chính xác availableSize
                .background(ColorWoodDark, RoundedCornerShape(2.dp))
                .border(2.dp, borderColor, RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Vẽ tọa độ Chữ (a-h) - Căn giữa trong phần lề
            for (i in 0..7) {
                val letter = if (userColor == PieceColor.WHITE) ('a' + i).toString() else ('h' - i).toString()
                Text(
                    text = letter,
                    color = Color(0xEEFFFFFF),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = boardBorderSize + (squareSize * i) + (squareSize / 2) - 4.dp, y = (-2).dp)
                )
            }

            // Vẽ tọa độ Số (1-8) - Căn giữa trong phần lề
            for (i in 0..7) {
                val number = if (userColor == PieceColor.WHITE) (8 - i).toString() else (i + 1).toString()
                Text(
                    text = number,
                    color = Color(0xEEFFFFFF),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 4.dp, y = boardBorderSize + (squareSize * i) + (squareSize / 2) - 6.dp)
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
                                        Box(Modifier.fillMaxSize().background(ColorSkyBlue.copy(alpha = 0.27f)))
                                        Box(Modifier.fillMaxSize().border(2.dp, ColorSkyBlue.copy(alpha = 0.6f)))
                                    }

                                    if (isCheckSquare) {
                                        Box(Modifier.fillMaxSize().background(ColorRedCheck))
                                        val borderColor = if (currentPiece?.color == userColor) ColorEmeraldLight else ColorGoldAmber
                                        Box(Modifier.fillMaxSize().border(4.dp, borderColor))
                                    }
                                    if (isCheckingPiece) {
                                        Box(Modifier.fillMaxSize().background(ColorCrimsonSoft.copy(alpha = 0.8f)))
                                        val borderColor = if (currentPiece?.color == userColor) ColorEmeraldLight else ColorGoldAmber
                                        Box(Modifier.fillMaxSize().border(4.dp, borderColor))
                                    }
                                    if (isSelected) Box(Modifier.fillMaxSize().background(Color(0x8816A34A)))
                                    if (isAiLastMove) Box(Modifier.fillMaxSize().background(Color(0x66F59E0B)))
                                    if (isLegalTarget) {
                                        if (isCastling) {
                                            // Special Blue highlight for Castling target
                                            Box(
                                                modifier = Modifier
                                                    .size(squareSize * 0.45f)
                                                    .background(ColorSkyBlue, CircleShape)
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
                for (vr in 0..7) {
                    val r = rows[vr]
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
                                    val isAtDestination = currentMove != null && pos == currentMove.to && progress < 1f
                                    val isAtSource = currentMove != null && pos == currentMove.from && progress < 1f
                                    
                                    if (!isAtDestination && !isAtSource) {
                                        Image(
                                            painter = painterResource(id = getPieceResource(piece)),
                                            contentDescription = piece.type.name,
                                            modifier = Modifier.size(squareSize * 0.85f)
                                        )
                                    } else if (isAtDestination) {
                                        // Captured piece \"kick out\" animation towards nearest edge
                                        val capturedPiece = currentMove.capturedPiece
                                        if (capturedPiece != null) {
                                            val animScale = 1f - (0.3f * progress) // From 1.0 to 0.7
                                            val animAlpha = 1f - progress
                                            
                                            // Visual distances to edges (in squares)
                                            val vDistLeft = vc
                                            val vDistRight = 7 - vc
                                            val vDistTop = vr
                                            val vDistBottom = 7 - vr
                                            
                                            val minDist = minOf(vDistLeft, vDistRight, vDistTop, vDistBottom)
                                            // Calculate distance to clear the board (+2 squares ensures it's fully out)
                                            val kickDistance = squareSize.value * (minDist + 2f)
                                            
                                            val (targetX, targetY) = when (minDist) {
                                                vDistLeft -> (-kickDistance * progress).dp to 0.dp
                                                vDistRight -> (kickDistance * progress).dp to 0.dp
                                                vDistTop -> 0.dp to (-kickDistance * progress).dp
                                                else -> 0.dp to (kickDistance * progress).dp
                                            }

                                            // Parabolic height (simulated by negative Y offset)
                                            val arcHeight = (squareSize.value * 0.8f).dp
                                            val jumpY = (-arcHeight * kotlin.math.sin(progress * kotlin.math.PI).toFloat())

                                            val kickOffsetX = targetX
                                            val kickOffsetY = targetY + jumpY
                                            
                                            Image(
                                                painter = painterResource(id = getPieceResource(capturedPiece)),
                                                contentDescription = capturedPiece.type.name,
                                                modifier = Modifier
                                                    .size(squareSize * 0.85f)
                                                    .offset(x = kickOffsetX, y = kickOffsetY)
                                                    .graphicsLayer {
                                                        scaleX = animScale
                                                        scaleY = animScale
                                                        alpha = animAlpha
                                                    }
                                            )
                                        }
                                    }
                                }

                                // Overlay the moving piece from source to destination
                                if (currentMove != null && pos == currentMove.from && progress < 1f) {
                                    val movingPiece = currentMove.piece
                                    val animScale = 1f + 0.1f * kotlin.math.sin(progress * kotlin.math.PI).toFloat()
                                    
                                    // Adjust distance calculation for board orientation (Black perspective)
                                    val rawColDist = currentMove.to.col - currentMove.from.col
                                    val rawRowDist = currentMove.to.row - currentMove.from.row
                                    
                                    val colDist = if (userColor == PieceColor.WHITE) rawColDist else -rawColDist
                                    val rowDist = if (userColor == PieceColor.WHITE) rawRowDist else -rawRowDist
                                    
                                    val offsetX = (colDist * squareSize.value * progress).dp
                                    val offsetY = (rowDist * squareSize.value * progress).dp

                                    Image(
                                        painter = painterResource(id = getPieceResource(movingPiece)),
                                        contentDescription = movingPiece.type.name,
                                        modifier = Modifier
                                            .size(squareSize * 0.85f)
                                            .offset(x = offsetX, y = offsetY)
                                            .graphicsLayer {
                                                scaleX = animScale
                                                scaleY = animScale
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

@Preview(showBackground = true)
@Composable
fun ChessBoard2DPreview() {
    MyApplicationTheme {
        ChessBoard2D(
            board = ChessBoard(initialize = true),
            userColor = PieceColor.WHITE,
            selectedPosition = null,
            legalMoves = emptyList(),
            aiLastMove = null,
            playerLastMove = null,
            hintMove = null,
            kingInCheckPos = null,
            currentTurn = PieceColor.WHITE,
            onSquareClick = {},
            theme = ChessTheme.CLASSIC,
            modifier = Modifier.padding(16.dp)
        )
    }
}
