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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.example.R
import com.example.chess.engine.ChessBoard
import com.example.chess.model.*
import com.example.ui.theme.*

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
    gameStatus: GameStatus = GameStatus.IN_PROGRESS,
    winner: PieceColor? = null,
    gameMode: GameMode = GameMode.VS_AI,
    currentTurn: PieceColor,
    onSquareClick: (Position) -> Unit,
    theme: ChessTheme = ChessTheme.CLASSIC,
    isMoveHintsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val lightSquareColor = Color(theme.lightSquareColor)
    val darkSquareColor = Color(theme.darkSquareColor)

    var currentBorderColor by remember { mutableStateOf(MedievalGold) }

    LaunchedEffect(winner) {
        if (winner != null) {
            val resultColor = if (winner == userColor) ColorRoyalBlue else Color.Red
            val flashColor = MedievalGold
            
            // Nhấp nháy trong 5 giây (10 chu kỳ, mỗi chu kỳ 500ms)
            repeat(10) {
                currentBorderColor = resultColor
                kotlinx.coroutines.delay(250)
                currentBorderColor = flashColor
                kotlinx.coroutines.delay(250)
            }
            currentBorderColor = resultColor
        } else {
            currentBorderColor = MedievalGold
        }
    }

    val rows = if (userColor == PieceColor.WHITE) (0..7).toList() else (7 downTo 0).toList()

    val animProgress = remember { Animatable(1f) }
    val currentMove = aiLastMove ?: playerLastMove

    LaunchedEffect(currentMove) {
        if (currentMove != null) {
            animProgress.snapTo(0f)
            animProgress.animateTo(1f, animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing))
        }
    }

    val progress = animProgress.value

    val configuration = LocalConfiguration.current
    val isLargeScreen = configuration.smallestScreenWidthDp >= 600
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Surface(
        color = Color.Transparent,
        modifier = modifier.padding(2.dp)
    ) {
        BoxWithConstraints(contentAlignment = Alignment.Center) {
            val maxAvailable = minOf(this.maxWidth, this.maxHeight)

            val availableSize = if (isLandscape) {
                maxAvailable // Tận dụng tối đa không gian khi xoay ngang
            } else {
                maxAvailable // Màn hình dọc: Luôn lấy 100%
            }


            val boardBorderSize = 16.dp
            val boardSize = availableSize - (boardBorderSize * 2)
            val squareSize = boardSize / 8f

            // Thêm Box bao ngoài cùng và gỡ bỏ clipToBounds để quân cờ có thể tràn ra ngoài
            Box(
                modifier = Modifier
                    .size(availableSize)
            ) {
                // 1. LỚP NỀN VÀ BIÊN NGOÀI (Dưới cùng)
                Box(
                    modifier = Modifier
                        .size(availableSize)
                        .background(ColorWoodDark, RoundedCornerShape(2.dp))
                        .border(2.dp, currentBorderColor, RoundedCornerShape(2.dp))
                        .zIndex(0f)
                )

                // 2. TỌA ĐỘ (Nằm trên nền)
                Box(modifier = Modifier.size(availableSize).zIndex(1f)) {
                    // Vẽ tọa độ Chữ (a-h)
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

                    // Vẽ tọa độ Số (1-8)
                    for (i in 0..7) {
                        val number = if (userColor == PieceColor.WHITE) (8 - i).toString() else (i + 1).toString()
                        Text(
                            text = number,
                            color = Color(0xEEFFFFFF),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(x = 6.dp, y = boardBorderSize + (squareSize * i) + (squareSize / 2) - 6.dp) // Tăng x margin lên 6.dp
                        )
                    }
                }

                // 3. BÀN CỜ CHÍNH (Ô cờ và Highlights)
                Box(
                    modifier = Modifier
                        .size(availableSize)
                        .padding(boardBorderSize)
                        .zIndex(2f)
                ) {
                    Box(Modifier.fillMaxSize().border(1.dp, Color.Black.copy(alpha = 0.4f)))

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

                    Column(modifier = Modifier.fillMaxSize()) {
                        for (r in rows) {
                            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                                for (vc in 0..7) {
                                    val c = if (userColor == PieceColor.WHITE) vc else 7 - vc
                                    val pos = Position(r, c)
                                    val currentPiece = board.getPiece(pos)
                                    
                                    val isSelected = selectedPosition == pos
                                    val isLegalTarget = legalMoves.any { it.to == pos }
                                    val isHint = hintMove?.from == pos || hintMove?.to == pos
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
                                            val bColor = if (currentPiece?.color == userColor) ColorEmeraldLight else ColorGoldAmber
                                            Box(Modifier.fillMaxSize().border(4.dp, bColor))
                                        }
                                        if (isCheckingPiece) {
                                            Box(Modifier.fillMaxSize().background(ColorCrimsonSoft.copy(alpha = 0.8f)))
                                            val bColor = if (currentPiece?.color == userColor) ColorEmeraldLight else ColorGoldAmber
                                            Box(Modifier.fillMaxSize().border(4.dp, bColor))
                                        }
                                        if (isSelected) Box(Modifier.fillMaxSize().background(ColorEmeraldDark.copy(alpha = 0.54f)))
                                        if (isAiLastMove) Box(Modifier.fillMaxSize().background(ColorGoldAmber.copy(alpha = 0.4f)))
                                        if (isPlayerLastMove) Box(Modifier.fillMaxSize().background(ColorEmeraldLight.copy(alpha = 0.4f)))
                                        if (isMoveHintsEnabled && isLegalTarget) {
                                            if (isCastling) {
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
                                                Box(modifier = Modifier.size(squareSize * 0.35f).background(ColorEmeraldLight.copy(alpha = 0.67f), CircleShape))
                                            }
                                        }
                                        if (isHint) Box(Modifier.fillMaxSize().border(3.dp, Color.Yellow.copy(alpha = 0.7f)))
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. QUÂN CỜ (Nằm ở layer cao hơn hẳn để không bị border đè)
                Box(
                    modifier = Modifier
                        .size(availableSize)
                        .padding(boardBorderSize)
                        .zIndex(10f) // Gán zIndex cao hẳn cho lớp quân cờ
                ) {
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
                                            val isAtDestinationSource = currentMove != null && pos == currentMove.from && progress < 1f
                                            
                                            if (!isAtDestination && !isAtDestinationSource) {
                                                val resId = getPieceDrawable3D(piece, userColor)
                                                val pieceScale = get3DPieceScale(piece.type, piece.color, userColor)

                                                Image(
                                                    painter = painterResource(id = resId),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .fillMaxSize(1f)
                                                        .padding(bottom = 6.dp)
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

                    // 4. LỚP QUÂN CỜ DI CHUYỂN (Nằm trên cùng)
                    if (currentMove != null && progress < 1f) {
                        val density = LocalDensity.current
                        val squareSizePx = with(density) { squareSize.toPx() }

                        // A. Quân bị đá văng
                        val capturedPiece = currentMove.capturedPiece
                        if (capturedPiece != null) {
                            val row = currentMove.to.row
                            val col = currentMove.to.col
                            val vr = rows.indexOf(row)
                            val vc = if (userColor == PieceColor.WHITE) col else 7 - col

                            val animScale = 1f - (0.3f * progress)
                            val animAlpha = 1f - progress
                            val vDistLeft = vc
                            val vDistRight = 7 - vc
                            val vDistTop = vr
                            val vDistBottom = 7 - vr
                            val minDist = minOf(vDistLeft, vDistRight, vDistTop, vDistBottom)
                            val kickDistancePx = squareSizePx * (minDist + 2f)
                            
                            val (targetX, targetY) = when (minDist) {
                                vDistLeft -> (-kickDistancePx * progress) to 0f
                                vDistRight -> (kickDistancePx * progress) to 0f
                                vDistTop -> 0f to (-kickDistancePx * progress)
                                else -> 0f to (kickDistancePx * progress)
                            }
                            val arcHeightPx = squareSizePx * 0.8f
                            val jumpY = (-arcHeightPx * kotlin.math.sin(progress * kotlin.math.PI).toFloat())

                            Image(
                                painter = painterResource(id = getPieceDrawable3D(capturedPiece, userColor)),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(squareSize)
                                    .offset(x = squareSize * vc, y = squareSize * vr)
                                    .graphicsLayer {
                                        val s = get3DPieceScale(capturedPiece.type, capturedPiece.color, userColor)
                                        scaleX = s * animScale
                                        scaleY = s * animScale
                                        alpha = animAlpha
                                        translationX = targetX
                                        translationY = targetY + jumpY
                                        transformOrigin = TransformOrigin(0.5f, 1f)
                                    }
                            )
                        }

                        // B. Quân đang di chuyển
                        val movingPiece = currentMove.piece
                        val fromVr = rows.indexOf(currentMove.from.row)
                        val fromVc = if (userColor == PieceColor.WHITE) currentMove.from.col else 7 - currentMove.from.col
                        val toVr = rows.indexOf(currentMove.to.row)
                        val toVc = if (userColor == PieceColor.WHITE) currentMove.to.col else 7 - currentMove.to.col

                        val animScale = 1f + 0.1f * kotlin.math.sin(progress * kotlin.math.PI).toFloat()
                        val offsetX = (toVc - fromVc) * squareSizePx * progress
                        val offsetY = (toVr - fromVr) * squareSizePx * progress

                        Image(
                            painter = painterResource(id = getPieceDrawable3D(movingPiece, userColor)),
                            contentDescription = null,
                            modifier = Modifier
                                .size(squareSize)
                                .offset(x = squareSize * fromVc, y = squareSize * fromVr)
                                .graphicsLayer {
                                    val s = get3DPieceScale(movingPiece.type, movingPiece.color, userColor)
                                    scaleX = s * animScale
                                    scaleY = s * animScale
                                    translationX = offsetX
                                    translationY = offsetY
                                    transformOrigin = TransformOrigin(0.5f, 1f)
                                }
                        )
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
            currentTurn = PieceColor.WHITE,
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
        "user_bishop_white" -> R.drawable.bishop_white
        "user_bishop_black" -> R.drawable.bishop_black
        "enemy_bishop_white" -> R.drawable.bishop_white
        "enemy_bishop_black" -> R.drawable.bishop_black
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
                PieceType.ROOK -> 1.5f
                PieceType.BISHOP -> 1.5f
                PieceType.KNIGHT -> 1.5f
                PieceType.PAWN -> 1.2f
            }
            PieceColor.BLACK -> when (type) {
                PieceType.KING -> 1.5f
                PieceType.QUEEN -> 1.5f
                PieceType.ROOK -> 1.5f
                PieceType.BISHOP -> 1.5f
                PieceType.KNIGHT -> 1.5f
                PieceType.PAWN -> 1.2f
            }
        }
    } else {
        when (color) {
            PieceColor.WHITE -> when (type) {
                PieceType.KING -> 1.5f
                PieceType.QUEEN -> 1.5f
                PieceType.ROOK -> 1.5f
                PieceType.BISHOP -> 1.5f
                PieceType.KNIGHT -> 1.5f
                PieceType.PAWN -> 1.1f
            }
            PieceColor.BLACK -> when (type) {
                PieceType.KING -> 1.5f
                PieceType.QUEEN -> 1.5f
                PieceType.ROOK -> 1.5f
                PieceType.BISHOP -> 1.5f
                PieceType.KNIGHT -> 1.5f
                PieceType.PAWN -> 1.1f
            }
        }
    }
}
