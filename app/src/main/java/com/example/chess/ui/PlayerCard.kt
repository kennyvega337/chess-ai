package com.example.chess.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.DifficultyLevel
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.ui.theme.*

import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration

@Composable
fun PlayerCard(
    isUser: Boolean,
    playerColor: PieceColor,
    isCurrentTurn: Boolean,
    isAiThinking: Boolean = false,
    capturedPieces: List<PieceType>,
    difficulty: DifficultyLevel? = null,
    gameMode: GameMode = GameMode.VS_AI,
    gameStatus: GameStatus = GameStatus.IN_PROGRESS,
    winner: PieceColor? = null,
    title: String? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val isHumanPlayer = gameMode == GameMode.TWO_PLAYERS || isUser

    val surfaceModifier = if (onClick != null) {
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    } else {
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    }

    Surface(
        modifier = surfaceModifier
            .border(
                width = if (isCurrentTurn) 2.dp else 1.dp,
                color = if (isCurrentTurn) MedievalGold else Color(0x33D4AF37),
                shape = RoundedCornerShape(12.dp)
            ),
        color = if (isCurrentTurn) ColorWoodMid else ColorWoodLight
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar Shield Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isHumanPlayer) ColorRoyalBlue else ColorCrimsonDeep)
                        .border(1.dp, MedievalGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isHumanPlayer) Icons.Default.Person else Icons.Default.Computer,
                        contentDescription = null,
                        tint = MedievalGoldLight,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    val isGameOver = gameStatus != GameStatus.IN_PROGRESS && gameStatus != GameStatus.NOT_STARTED
                    val statusText = when {
                        isGameOver -> {
                            when {
                                winner == playerColor -> "Chiến thắng"
                                winner != null -> "Thua cuộc"
                                gameStatus == GameStatus.DRAW || gameStatus == GameStatus.STALEMATE -> "Hòa cờ"
                                else -> "Kết thúc"
                            }
                        }
                        isAiThinking && isCurrentTurn -> if (isUser) "Đang tìm gợi ý..." else "Đang tính nước đi..."
                        isCurrentTurn -> if (isLandscape) "Đến lượt đi" else "⚡ Đến lượt đi"
                        else -> if (isLandscape) "Đang chờ..." else "Chờ đến lượt..."
                    }

                    val statusColor = when {
                        isGameOver && winner == playerColor -> ColorEmeraldLight // Green for win
                        isGameOver && winner != null -> ColorCrimsonSoft // Red for loss
                        isGameOver -> Color.Gray
                        isCurrentTurn -> ColorEmeraldLight
                        else -> Color.Gray
                    }

                    if (isLandscape) {
                        Text(
                            text = title ?: (if (playerColor == PieceColor.WHITE) "Quân Trắng" else "Quân Đen"),
                            color = MedievalGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = if (isCurrentTurn || isGameOver) FontWeight.Bold else FontWeight.Normal
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val titleText = title ?: when {
                                isUser -> "Bàn Cờ Bạn"
                                else -> "Máy (${difficulty?.displayNameVi ?: "Trung Bình"})"
                            }

                            Text(
                                text = titleText,
                                color = MedievalParchment,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (playerColor == PieceColor.WHITE) "(Trắng ♔)" else "(Đen ♚)",
                                color = MedievalGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Status / Thinking indicator
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = if (isCurrentTurn || isGameOver) FontWeight.Bold else FontWeight.Normal,
                            modifier = if (isAiThinking && isCurrentTurn && !isGameOver) Modifier.alpha(alphaAnim) else Modifier
                        )
                    }
                }
            }

            // Captured pieces list - Only show in Portrait
            if (!isLandscape) {
                CapturedPiecesRow(capturedPieces = capturedPieces, pieceColor = playerColor.opposite)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerCardWinnerPreview() {
    MyApplicationTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Winner (User):")
            PlayerCard(
                isUser = true,
                playerColor = PieceColor.WHITE,
                isCurrentTurn = true,
                capturedPieces = emptyList(),
                gameStatus = GameStatus.CHECKMATE,
                winner = PieceColor.WHITE
            )
            Text("Loser (AI):")
            PlayerCard(
                isUser = false,
                playerColor = PieceColor.BLACK,
                isCurrentTurn = false,
                capturedPieces = emptyList(),
                gameStatus = GameStatus.CHECKMATE,
                winner = PieceColor.WHITE
            )
        }
    }
}

@Composable
fun CapturedPiecesRow(
    capturedPieces: List<PieceType>,
    pieceColor: PieceColor
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy((-4).dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.testTag("captured_pieces_row")
    ) {
        capturedPieces.takeLast(8).forEach { type ->
            val symbol = if (pieceColor == PieceColor.WHITE) type.symbolWhite else type.symbolBlack
            Text(
                text = symbol,
                fontSize = 18.sp,
                color = if (pieceColor == PieceColor.WHITE) Color.White else ColorGreyWarm
            )
        }
    }
}
