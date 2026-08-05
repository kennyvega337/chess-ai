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
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.ui.theme.MedievalGold
import com.example.ui.theme.MedievalGoldLight
import com.example.ui.theme.MedievalParchment
import com.example.ui.theme.MedievalParchmentDark

@Composable
fun PlayerCard(
    isUser: Boolean,
    playerColor: PieceColor,
    isCurrentTurn: Boolean,
    isAiThinking: Boolean = false,
    capturedPieces: List<PieceType>,
    difficulty: DifficultyLevel? = null,
    gameMode: GameMode = GameMode.VS_AI,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
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
        color = if (isCurrentTurn) Color(0xFF382315) else Color(0xFF22150C)
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
                        .background(if (isHumanPlayer) Color(0xFF1E3A8A) else Color(0xFF881337))
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val titleText = when {
                            gameMode == GameMode.TWO_PLAYERS && playerColor == PieceColor.WHITE -> "Người chơi 1"
                            gameMode == GameMode.TWO_PLAYERS && playerColor == PieceColor.BLACK -> "Người chơi 2"
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

                    // Turn status / Thinking indicator
                    if (gameMode == GameMode.VS_AI && !isUser && isAiThinking) {
                        Text(
                            text = "Đang tính nước đi...",
                            color = MedievalGold,
                            fontSize = 11.sp,
                            modifier = Modifier.alpha(alphaAnim)
                        )
                    } else if (isCurrentTurn) {
                        Text(
                            text = "⚡ Đến lượt đi",
                            color = Color(0xFF22C55E),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "Chờ đến lượt...",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Captured pieces list
            CapturedPiecesRow(capturedPieces = capturedPieces, pieceColor = playerColor.opposite)
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
                color = if (pieceColor == PieceColor.WHITE) Color.White else Color(0xFFB0A8A0)
            )
        }
    }
}
