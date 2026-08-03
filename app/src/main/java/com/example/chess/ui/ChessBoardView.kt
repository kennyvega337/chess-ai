package com.example.chess.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.engine.ChessBoard
import com.example.chess.model.Move
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.Position
import com.example.ui.theme.BoardCheckRed
import com.example.ui.theme.BoardDarkSquare
import com.example.ui.theme.BoardHighlightSelected
import com.example.ui.theme.BoardLastMove
import com.example.ui.theme.BoardLightSquare
import com.example.ui.theme.MedievalGold
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun ChessBoardView(
    board: ChessBoard,
    userColor: PieceColor,
    selectedPosition: Position?,
    legalMoves: List<Move>,
    lastMove: Move?,
    hintMove: Move? = null,
    isCheck: Boolean,
    currentTurn: PieceColor,
    onSquareClick: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    // Flip rows/cols if user plays Black
    val rows = if (userColor == PieceColor.WHITE) (0..7).toList() else (7 downTo 0).toList()
    val cols = if (userColor == PieceColor.WHITE) (0..7).toList() else (7 downTo 0).toList()

    val kingInCheckPos = if (isCheck) board.findKing(currentTurn) else null

    // Blinking pulsing animation for target hint square
    val infiniteTransition = rememberInfiniteTransition(label = "hint_pulse")
    val hintPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hint_pulse_alpha"
    )

    // Animation states for piece movement (traveling in air) and captured piece knockout
    var activeAnimatingMove by remember { mutableStateOf<Move?>(null) }
    val moveProgress = remember { Animatable(0f) }

    var activeCapturedPiece by remember { mutableStateOf<Piece?>(null) }
    var activeCapturedPos by remember { mutableStateOf<Position?>(null) }
    val captureKickProgress = remember { Animatable(0f) }

    // Trigger animation when lastMove changes
    LaunchedEffect(lastMove) {
        if (lastMove != null) {
            activeAnimatingMove = lastMove

            coroutineScope {
                if (lastMove.capturedPiece != null) {
                    activeCapturedPiece = lastMove.capturedPiece
                    activeCapturedPos = lastMove.to
                    launch {
                        captureKickProgress.snapTo(0f)
                        captureKickProgress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(850, easing = FastOutSlowInEasing)
                        )
                        activeCapturedPiece = null
                        activeCapturedPos = null
                    }
                }

                launch {
                    moveProgress.snapTo(0f)
                    moveProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(950, easing = FastOutSlowInEasing)
                    )
                    activeAnimatingMove = null
                }
            }
        }
    }

    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .border(3.5.dp, MedievalGold, RoundedCornerShape(12.dp))
            .testTag("chessboard_view")
    ) {
        val squareSizePx = with(density) { (maxWidth / 8f).toPx() }
        val squareSizeDp = maxWidth / 8f

        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
            // Main 8x8 Board Grid
            Column(modifier = Modifier.fillMaxSize()) {
                for (r in rows) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        for (c in cols) {
                            val currentPos = Position(r, c)
                            val piece = board.getPiece(currentPos)
                            val isLightSquare = (r + c) % 2 == 0

                            val isSelected = selectedPosition == currentPos
                            val targetMove = legalMoves.find { it.to == currentPos }
                            val isLegalTarget = targetMove != null
                            val isLastMoveSquare = lastMove?.from == currentPos || lastMove?.to == currentPos
                            val isCheckSquare = kingInCheckPos == currentPos
                            val isHintToSquare = hintMove?.to == currentPos

                            // Hide destination piece temporarily while mid-air travel animation is playing
                            val isBeingAnimatedTo = activeAnimatingMove?.to == currentPos && moveProgress.value < 1f

                            // Square Background Color
                            val squareBg = when {
                                isCheckSquare -> BoardCheckRed
                                isSelected -> BoardHighlightSelected
                                isLastMoveSquare -> BoardLastMove
                                isLightSquare -> BoardLightSquare
                                else -> BoardDarkSquare
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(squareBg)
                                    .clickable { onSquareClick(currentPos) }
                                    .testTag("square_${r}_${c}"),
                                contentAlignment = Alignment.Center
                            ) {
                                // Rank coordinate label (1-8)
                                if (c == cols.first()) {
                                    Text(
                                        text = "${8 - r}",
                                        color = if (isLightSquare) BoardDarkSquare else BoardLightSquare,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(start = 2.dp, top = 1.dp)
                                    )
                                }
                                // File coordinate label (a-h)
                                if (r == rows.last()) {
                                    Text(
                                        text = "${'a' + c}",
                                        color = if (isLightSquare) BoardDarkSquare else BoardLightSquare,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(end = 2.dp, bottom = 1.dp)
                                    )
                                }

                                // Piece Symbol
                                if (piece != null && !isBeingAnimatedTo) {
                                    Text(
                                        text = piece.symbol,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = if (piece.color == PieceColor.WHITE) Color.White else Color.Black
                                    )
                                }

                                // Target indicator dots / rings
                                if (isLegalTarget) {
                                    if (piece != null) {
                                        // Capture ring
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(3.dp)
                                                .border(3.dp, Color(0xCCDC2626), CircleShape)
                                        )
                                    } else {
                                        // Move dot
                                        Box(
                                            modifier = Modifier
                                                .size(squareSizeDp * 0.35f)
                                                .clip(CircleShape)
                                                .background(Color(0x8810B981))
                                        )
                                    }
                                }

                                // Blinking hint target overlay for destination square ("nhấp nháy")
                                if (isHintToSquare) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xFFF59E0B).copy(alpha = hintPulseAlpha * 0.55f))
                                            .border(4.dp, Color(0xFFF59E0B).copy(alpha = hintPulseAlpha), RoundedCornerShape(4.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Mid-air Moving Piece Overlay (smooth hand-guided arc flight from `from` square to `to` square)
            activeAnimatingMove?.let { move ->
                val fromRowIdx = rows.indexOf(move.from.row)
                val fromColIdx = cols.indexOf(move.from.col)
                val toRowIdx = rows.indexOf(move.to.row)
                val toColIdx = cols.indexOf(move.to.col)

                if (fromRowIdx >= 0 && fromColIdx >= 0 && toRowIdx >= 0 && toColIdx >= 0) {
                    val progress = moveProgress.value
                    val startX = fromColIdx * squareSizePx
                    val startY = fromRowIdx * squareSizePx
                    val targetX = toColIdx * squareSizePx
                    val targetY = toRowIdx * squareSizePx

                    val currentX = startX + (targetX - startX) * progress
                    val currentY = startY + (targetY - startY) * progress

                    // Arc lift in the air mid-flight (hand picking piece up and dropping it on destination)
                    val arcHeight = sin(progress * PI).toFloat()
                    val flightLiftPx = with(density) { -20.dp.toPx() } * arcHeight
                    val flightScale = 1.0f + 0.35f * arcHeight

                    Box(
                        modifier = Modifier
                            .size(squareSizeDp)
                            .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) },
                        contentAlignment = Alignment.Center
                    ) {
                        // Drop shadow on board during mid-air flight
                        Box(
                            modifier = Modifier
                                .size(squareSizeDp * 0.5f)
                                .offset { IntOffset(0, (squareSizePx * 0.1f).roundToInt()) }
                                .background(Color.Black.copy(alpha = 0.3f * arcHeight), CircleShape)
                        )

                        Text(
                            text = move.piece.symbol,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Normal,
                            color = if (move.piece.color == PieceColor.WHITE) Color.White else Color.Black,
                            modifier = Modifier.graphicsLayer {
                                translationY = flightLiftPx
                                scaleX = flightScale
                                scaleY = flightScale
                            }
                        )
                    }
                }
            }

            // Captured Piece Knockout / Kick Off Board Effect ("đá con cờ ra khỏi bàn cờ")
            activeCapturedPiece?.let { capturedPiece ->
                activeCapturedPos?.let { capPos ->
                    val capRowIdx = rows.indexOf(capPos.row)
                    val capColIdx = cols.indexOf(capPos.col)
                    if (capRowIdx >= 0 && capColIdx >= 0) {
                        val kickProg = captureKickProgress.value
                        val capX = capColIdx * squareSizePx
                        val capY = capRowIdx * squareSizePx

                        // Fly off diagonally right/downwards while spinning fast & fading out
                        val kickOffsetX = with(density) { 110.dp.toPx() } * kickProg
                        val kickOffsetY = with(density) { 70.dp.toPx() } * kickProg
                        val rotationAngle = 240f * kickProg
                        val scaleFactor = (1f - kickProg * 0.6f).coerceAtLeast(0.1f)
                        val fadeAlpha = (1f - kickProg).coerceIn(0f, 1f)

                        Box(
                            modifier = Modifier
                                .size(squareSizeDp)
                                .offset { IntOffset((capX + kickOffsetX).roundToInt(), (capY + kickOffsetY).roundToInt()) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = capturedPiece.symbol,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (capturedPiece.color == PieceColor.WHITE) Color.White else Color.Black,
                                modifier = Modifier.graphicsLayer {
                                    rotationZ = rotationAngle
                                    scaleX = scaleFactor
                                    scaleY = scaleFactor
                                    alpha = fadeAlpha
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
