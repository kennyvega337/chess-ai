package com.example.chess.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.app.Activity
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.tooling.preview.Preview
import com.example.R
import com.example.ui.theme.MyApplicationTheme
import com.example.chess.model.*
import com.example.chess.data.*
import com.example.ui.theme.*

@Composable
fun HideSystemBarsInDialog() {
    val view = LocalView.current
    SideEffect {
        fun applyHide(win: android.view.Window, isDialog: Boolean) {
            WindowCompat.setDecorFitsSystemWindows(win, false)
            win.navigationBarColor = android.graphics.Color.TRANSPARENT
            
            if (isDialog) {
                win.setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            WindowCompat.getInsetsController(win, win.decorView).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
                hide(WindowInsetsCompat.Type.navigationBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }

        var parentView: android.view.ViewParent? = view.parent
        var dialogWindow: android.view.Window? = null
        while (parentView != null) {
            if (parentView is DialogWindowProvider) {
                dialogWindow = parentView.window
                break
            }
            parentView = parentView.parent
        }

        if (dialogWindow != null) {
            applyHide(dialogWindow!!, isDialog = true)
        } else {
            (view.context as? Activity)?.window?.let { applyHide(it, isDialog = false) }
        }
    }
}

@Composable
fun SideSelectionDialog(
    onDismiss: () -> Unit,
    onSelect: (PieceColor) -> Unit = {},
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColor = Color(selectedTheme.accentColor)
    val surfaceColor = Color(selectedTheme.surfaceColor)
    HideSystemBarsInDialog()
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        HideSystemBarsInDialog()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .widthIn(max = 400.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = surfaceColor,
                border = androidx.compose.foundation.BorderStroke(3.dp, accentColor),
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CHỌN PHE QUÂN",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor,
                        letterSpacing = 2.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SideSelectionItem("TRẮNG", "♔", PieceColor.WHITE, onSelect, selectedTheme)
                        SideSelectionItem("ĐEN", "♚", PieceColor.BLACK, onSelect, selectedTheme)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    TextButton(onClick = onDismiss) {
                        Text("ĐÓNG", color = accentColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SideSelectionItem(
    label: String,
    icon: String,
    color: PieceColor,
    onSelect: (PieceColor) -> Unit,
    selectedTheme: ChessTheme
) {
    val accentColor = Color(selectedTheme.accentColor)
    val bgColor = if (color == PieceColor.WHITE) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.2f)
    val textColor = if (color == PieceColor.WHITE) Color.White else Color.Black
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(2.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable { onSelect(color) }
            .padding(16.dp)
            .width(100.dp)
    ) {
        Text(text = icon, fontSize = 48.sp, color = textColor)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontWeight = FontWeight.Bold, color = accentColor)
    }
}

@Composable
fun PawnPromotionDialog(
    color: PieceColor,
    onSelectPiece: (PieceType) -> Unit,
    viewMode: BoardViewMode = BoardViewMode.VIEW_2D,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColor = Color(selectedTheme.accentColor)
    val surfaceColor = Color(selectedTheme.surfaceColor)
    HideSystemBarsInDialog()
    val choices = listOf(
        PieceType.QUEEN to "Hậu",
        PieceType.ROOK to "Xe",
        PieceType.BISHOP to "Tượng",
        PieceType.KNIGHT to "Mã"
    )

    Dialog(
        onDismissRequest = { /* Force selection */ },
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false, dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        HideSystemBarsInDialog()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .widthIn(max = 450.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                color = surfaceColor,
                border = androidx.compose.foundation.BorderStroke(2.dp, accentColor)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(40.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "PHONG CẤP QUÂN CỜ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Chọn binh chủng thăng cấp cho Tốt:",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        choices.forEach { (type, name) ->
                            val resId = if (viewMode == BoardViewMode.VIEW_3D) {
                                getPieceDrawable3D(Piece(type, color), color)
                            } else {
                                getPieceResource(Piece(type, color))
                            }
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.2f))
                                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .clickable { onSelectPiece(type) }
                                    .padding(vertical = 12.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = resId),
                                    contentDescription = name,
                                    modifier = Modifier.size(if (viewMode == BoardViewMode.VIEW_3D) 50.dp else 40.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = name, 
                                    fontSize = 10.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

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
        else -> R.drawable.white_pawn
    }
}

@Composable
fun GameOverDialog(
    gameStatus: GameStatus,
    winner: PieceColor?,
    userColor: PieceColor,
    gameMode: GameMode = GameMode.VS_AI,
    difficulty: DifficultyLevel = DifficultyLevel.LEVEL_2,
    timestamp: Long = 0,
    onPlayAgain: () -> Unit,
    onRestart: () -> Unit,
    onDismiss: () -> Unit,
    onNextMatch: (() -> Unit)? = null,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColorTheme = Color(selectedTheme.accentColor)
    val surfaceColor = Color(selectedTheme.surfaceColor)
    val isWin = winner == userColor
    val isDraw = gameStatus == GameStatus.STALEMATE || gameStatus == GameStatus.DRAW
    val isResigned = gameStatus == GameStatus.RESIGNED

    val randomEntry = remember(gameStatus, winner, gameMode, difficulty, timestamp) {
        val filtered = EndGameMessages.list.filter { msg ->
            when (gameMode) {
                GameMode.VS_AI -> {
                    if (isDraw) {
                        msg.Mode == "AI" && msg.Level == 0
                    } else {
                        msg.Mode == "AI" && msg.IsWon == isWin && msg.Level == difficulty.level
                    }
                }
                GameMode.PUZZLE -> msg.Mode == "PUZZLES" && msg.IsWon == isWin
                GameMode.ONE_MOVE -> msg.Mode == "ONE_MOVE" && msg.IsWon == isWin
                else -> false
            }
        }
        if (filtered.isNotEmpty()) filtered.random() else null
    }

    val bannerTitle = randomEntry?.Title ?: when {
        gameMode == GameMode.TWO_PLAYERS -> if (winner != null) "👑 THÔNG BÁO KẾT QUẢ 👑" else "⚖️ HÒA CỜ TRUNG CỔ ⚖️"
        gameMode == GameMode.PUZZLE -> if (isWin) "👑 THIÊN TÀI (HAY MAY MẮN?) 👑" else "💀 QUÁ SỨC RỒI SAO? 💀"
        gameMode == GameMode.ONE_MOVE -> if (isWin) "🎯 ĐOÁN MÒ THÀNH CÔNG 🎯" else "🤡 BÓ TAY TOÀN TẬP 🤡"
        else -> when {
            isWin -> "👑 THÔNG BÁO CHIẾN THẮNG 👑"
            isDraw -> "⚖️ HÒA CỜ TRUNG CỔ ⚖️"
            isResigned && !isWin -> "🏳️ BẠN ĐÃ ĐẦU HÀNG 🏳️"
            else -> "⚔️ THÔNG BÁO BẠI TRẬN ⚔️"
        }
    }

    val mainStatusText = when {
        gameMode == GameMode.TWO_PLAYERS -> {
            val p1ColorName = if (userColor == PieceColor.WHITE) "TRẮNG" else "ĐEN"
            val p2ColorName = if (userColor == PieceColor.WHITE) "ĐEN" else "TRẮNG"
            when (winner) {
                userColor -> "NGƯỜI CHƠI 1 ($p1ColorName) THẮNG!"
                userColor.opposite -> "NGƯỜI CHƠI 2 ($p2ColorName) THẮNG!"
                else -> "TRẬN ĐẤU BẤT PHÂN THẮNG BẠI!"
            }
        }
        randomEntry != null -> if (isWin) "CHIẾN THẮNG!" else if (isDraw) "HÒA CỜ!" else "BẠN ĐÃ THẤT THỦ!"
        gameMode == GameMode.PUZZLE -> if (isWin) "TRÌNH ĐỘ... CŨNG TẠM!" else "NÃO ĐANG 'LOAD' SAO?"
        gameMode == GameMode.ONE_MOVE -> if (isWin) "CHẮC LÀ ĂN MAY THÔI!" else "THUA TRONG TỨC TƯỞI!"
        else -> when {
            isWin -> "BẠN ĐÃ CHIẾN THẮNG!"
            isDraw -> "TRẬN ĐẤU BẤT PHÂN THẮNG BẠI!"
            isResigned && !isWin -> "BẠN ĐÃ CHỦ ĐỘNG ĐẦU HÀNG!"
            else -> "BẠN ĐÃ THẤT THỦ!"
        }
    }

    val bodyText = randomEntry?.Message ?: when {
        gameMode == GameMode.TWO_PLAYERS -> {
            val p1ColorVi = if (userColor == PieceColor.WHITE) "Quân Trắng" else "Quân Đen"
            val p2ColorVi = if (userColor == PieceColor.WHITE) "Quân Đen" else "Quân Trắng"
            when (winner) {
                userColor -> "Chúc mừng Người chơi 1 ($p1ColorVi) đã bằng chiến thuật kiệt xuất giành thắng lợi toàn diện!"
                userColor.opposite -> "Chúc mừng Người chơi 2 ($p2ColorVi) đã bằng chiến thuật kiệt xuất giành thắng lợi toàn diện!"
                else -> "Cả hai người chơi đã chiến đấu ngoan cường và hòa ván cờ này."
            }
        }
        gameMode == GameMode.PUZZLE -> if (isWin) "Chiếu bí thần sầu! Cuối cùng thì nhà ngươi cũng đã chịu dùng tới bộ não của mình rồi đấy." else "Câu đố này có vẻ hơi 'quá tầm' với bộ óc của ngươi rồi. Thử lại hay đi ngủ cho đỡ nhức đầu?"
        gameMode == GameMode.ONE_MOVE -> if (isWin) "Chỉ một nước mà cũng thắng? Chắc chắn là nhìn trộm đáp án ở đâu đó rồi chứ gì, đừng hòng lừa ta!" else "Có đúng một nước đi mà cũng làm không xong. Ngươi nên về tập đi quân Tốt cho vững trước khi mơ làm Đại kiện tướng!"
        else -> when {
            isWin -> "Xuất sắc! Bằng mưu trí và chiến thuật kiệt xuất, bạn đã chiếu bí quân địch và giành toàn thắng."
            isDraw -> "Cả hai phe đã chiến đấu ngoan cường. Thế cờ hòa (Stalemate) không còn nước đi hợp lệ."
            isResigned && !isWin -> "Bạn đã giơ cờ trắng chịu thua ván đấu này. Hãy chuẩn bị lực lượng cho ván mới!"
            else -> "Chiến vương địch đã chiếu bí bạn! Đừng nản lòng, hãy chấn chỉnh quân ngũ để phục thù."
        }
    }

    val accentColor = if (gameMode == GameMode.TWO_PLAYERS) {
        if (winner != null) ColorEmeraldLight else accentColorTheme
    } else {
        when {
            isWin -> ColorEmeraldLight
            isDraw -> accentColorTheme
            else -> ColorCrimsonSoft
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val isLargeScreen = configuration.smallestScreenWidthDp >= 600
    val cardWidthAlpha = if (isLargeScreen) 0.65f else if (isLandscape) 0.55f else 0.9f

    HideSystemBarsInDialog()
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false, 
            decorFitsSystemWindows = false
        )
    ) {
        HideSystemBarsInDialog()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(vertical = if (isLandscape) 4.dp else 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(cardWidthAlpha)
                    .widthIn(max = if (isLandscape) 420.dp else 500.dp)
                    .wrapContentHeight(),
                contentAlignment = Alignment.TopEnd
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(3.dp, accentColorTheme, RoundedCornerShape(18.dp))
                        .shadow(24.dp, RoundedCornerShape(18.dp))
                        .testTag("game_over_dialog"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(surfaceColor, Color.Black)
                                )
                            )
                            .padding(if (isLandscape) 16.dp else 24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = accentColorTheme.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColorTheme)
                        ) {
                            Text(
                                text = bannerTitle,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = if (isLandscape) 4.dp else 6.dp),
                                color = accentColorTheme,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = if (isLandscape) 12.sp else 14.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 20.dp))

                        Box(
                            modifier = Modifier
                                .size(if (isLandscape) 56.dp else 72.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.15f))
                                .border(2.5.dp, accentColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when {
                                    isWin -> Icons.Default.EmojiEvents
                                    isDraw -> Icons.Default.Security
                                    else -> Icons.Default.SentimentDissatisfied
                                },
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(if (isLandscape) 30.dp else 40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(if (isLandscape) 10.dp else 16.dp))

                        Text(
                            text = mainStatusText,
                            fontSize = if (isLandscape) 18.sp else 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(if (isLandscape) 4.dp else 8.dp))

                        Text(
                            text = bodyText,
                            fontSize = if (isLandscape) 12.sp else 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            lineHeight = if (isLandscape) 18.sp else 20.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 24.dp))

                        if (onNextMatch != null) {
                            Button(
                                onClick = onNextMatch,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(if (isLandscape) 44.dp else 50.dp)
                                    .shadow(8.dp, RoundedCornerShape(12.dp))
                                    .testTag("next_puzzle_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = ColorEmeraldLight),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColorTheme)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(if (isLandscape) 18.dp else 24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "VÁN TIẾP THEO",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = if (isLandscape) 12.sp else 14.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 12.dp))
                        }

                        Button(
                            onClick = onRestart,
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .height(if (isLandscape) 44.dp else 50.dp)
                                    .shadow(8.dp, RoundedCornerShape(12.dp))
                                    .testTag("restart_game_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColorTheme)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(if (isLandscape) 18.dp else 24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CHƠI LẠI",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isLandscape) 12.sp else 14.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 12.dp))

                        Button(
                            onClick = onPlayAgain,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (isLandscape) 44.dp else 50.dp)
                                .shadow(8.dp, RoundedCornerShape(12.dp))
                                .testTag("play_again_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = surfaceColor),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColorTheme)
                        ) {
                            Icon(
                                imageVector = if (gameMode == GameMode.PUZZLE || gameMode == GameMode.ONE_MOVE) Icons.Default.Extension else Icons.Default.Refresh,
                                contentDescription = null,
                                tint = accentColorTheme,
                                modifier = Modifier.size(if (isLandscape) 18.dp else 24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (gameMode == GameMode.PUZZLE || gameMode == GameMode.ONE_MOVE) "ĐỔI CHẾ ĐỘ" else "CHƠI VÁN MỚI",
                                color = accentColorTheme,
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isLandscape) 12.sp else 14.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .offset(x = 10.dp, y = (-10).dp)
                        .size(34.dp)
                        .background(Color.Black.copy(alpha = 0.8f), CircleShape)
                        .border(2.dp, accentColorTheme, CircleShape)
                        .shadow(12.dp, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Đóng",
                        tint = accentColorTheme,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RestartConfirmationDialog(
    onConfirmRestart: () -> Unit,
    onCancel: () -> Unit,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColor = Color(selectedTheme.accentColor)
    val surfaceColor = Color(selectedTheme.surfaceColor)
    HideSystemBarsInDialog()
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        HideSystemBarsInDialog()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(0.88f).wrapContentHeight(),
                contentAlignment = Alignment.TopEnd
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, accentColor, RoundedCornerShape(16.dp))
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .testTag("restart_confirmation_dialog"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(accentColor.copy(alpha = 0.2f), CircleShape)
                                .border(2.2.dp, accentColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "CHƠI LẠI TRẬN ĐẤU?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Bạn có chắc chắn muốn làm mới ván cờ này không? Tiến độ hiện tại sẽ bị hủy bỏ.",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onCancel,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor)
                            ) {
                                Text("HỦY", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Button(
                                onClick = onConfirmRestart,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                            ) {
                                Text("ĐỒNG Ý", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResignConfirmationDialog(
    onConfirmResign: () -> Unit,
    onCancel: () -> Unit,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColor = Color(selectedTheme.accentColor)
    val surfaceColor = Color(selectedTheme.surfaceColor)
    HideSystemBarsInDialog()
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        HideSystemBarsInDialog()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(0.88f).wrapContentHeight(),
                contentAlignment = Alignment.TopEnd
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, accentColor, RoundedCornerShape(16.dp))
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .testTag("resign_confirmation_dialog"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(surfaceColor, Color.Black)
                                )
                            )
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(ColorCrimsonSoft.copy(alpha = 0.15f))
                                .border(2.2.dp, ColorCrimsonSoft, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = ColorCrimsonSoft,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "🏳️ XÁC NHẬN ĐẦU HÀNG 🏳️",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Bạn có chắc chắn muốn giơ cờ trắng chịu thua ván đấu này không?",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onCancel,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("cancel_resign_button"),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor)
                            ) {
                                Text(
                                    text = "Đánh Tiếp",
                                    color = accentColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Button(
                                onClick = onConfirmResign,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("confirm_resign_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = ColorCrimsonDeep),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor)
                            ) {
                                Text(
                                    text = "Đầu Hàng",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .offset(x = 10.dp, y = (-10).dp)
                        .size(34.dp)
                        .background(Color.Black.copy(alpha = 0.8f), CircleShape)
                        .border(2.2.dp, accentColor, CircleShape)
                        .shadow(8.dp, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = accentColor, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun CapturedPiecesDialog(
    state: ChessUiState,
    onDismiss: () -> Unit
) {
    val accentColor = MaterialTheme.colorScheme.tertiary
    HideSystemBarsInDialog()
    val userColor = state.userColor
    val opponentColor = state.userColor.opposite
    val isTwoPlayers = state.gameMode == GameMode.TWO_PLAYERS

    val capturedByP1 = if (isTwoPlayers) state.capturedBlackPieces else if (userColor == PieceColor.WHITE) state.capturedBlackPieces else state.capturedWhitePieces
    val p1Score = capturedByP1.sumOf { it.pointValue }
    val capturedByP2 = if (isTwoPlayers) state.capturedWhitePieces else if (userColor == PieceColor.WHITE) state.capturedWhitePieces else state.capturedBlackPieces
    val p2Score = capturedByP2.sumOf { it.pointValue }
    val netDiff = p1Score - p2Score
    val pieceOrder = listOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT, PieceType.PAWN)

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val isLargeScreen = configuration.smallestScreenWidthDp >= 600
    val cardWidthAlpha = if (isLargeScreen) 0.7f else if (isLandscape) 0.7f else 0.9f

    HideSystemBarsInDialog()
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false, 
            decorFitsSystemWindows = false
        )
    ) {
        HideSystemBarsInDialog()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(cardWidthAlpha)
                    .widthIn(max = 520.dp)
                    .wrapContentHeight(),
                contentAlignment = Alignment.TopEnd
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, accentColor, RoundedCornerShape(16.dp))
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .testTag("captured_pieces_dialog"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(if (isLandscape) 0.9f else 0.85f)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(MaterialTheme.colorScheme.surface, Color.Black)
                                )
                            )
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.15f))
                                .border(2.2.dp, accentColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "⚔️ BẢNG CHIẾN TÍCH & ĐIỂM SỐ ⚔️",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor,
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Thống kê lực lượng bị tiêu diệt & điểm số trận đấu",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.3f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    val p1ColorName = if (userColor == PieceColor.WHITE) "TRẮNG" else "ĐEN"
                                    Text(if (isTwoPlayers) "N.CHƠI 1 ($p1ColorName)" else "NGƯỜI CHƠI", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = accentColor, maxLines = 1)
                                    Text("${p1Score} điểm", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = ColorEmeraldLight, maxLines = 1)
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when { netDiff > 0 -> ColorEmeraldDark.copy(alpha = 0.3f); netDiff < 0 -> ColorCrimsonMuted.copy(alpha = 0.3f); else -> Color.Gray.copy(alpha = 0.3f) },
                                    border = androidx.compose.foundation.BorderStroke(1.dp, when { netDiff > 0 -> ColorEmeraldLight; netDiff < 0 -> ColorCrimsonSoft; else -> accentColor })
                                ) {
                                    Text(
                                        text = when { netDiff > 0 -> if (isTwoPlayers) "+${netDiff} (Trắng ưu thế)" else "+${netDiff} (Ưu thế)"; netDiff < 0 -> if (isTwoPlayers) "+${-netDiff} (Đen ưu thế)" else "${netDiff} (Thất thế)"; else -> "Cân bằng (0)" },
                                        fontSize = 11.sp, fontWeight = FontWeight.Bold, color = when { netDiff > 0 -> ColorEmeraldPale; netDiff < 0 -> ColorCrimsonPale; else -> accentColor },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), maxLines = 1, overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    val p2ColorName = if (userColor == PieceColor.WHITE) "ĐEN" else "TRẮNG"
                                    Text(if (isTwoPlayers) "N.CHƠI 2 ($p2ColorName)" else "MÁY (AI)", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = accentColor, maxLines = 1)
                                    Text("${p2Score} điểm", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = ColorCrimsonSoft, maxLines = 1)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        val p1Title = if (isTwoPlayers) {
                            val colorName = if (userColor == PieceColor.WHITE) "Trắng" else "Đen"
                            "🗡️ Quân Người chơi 1 ($colorName) đã ăn:"
                        } else "🗡️ Quân người chơi đã ăn (của Máy):"
                        CapturedPieceSection(p1Title, capturedByP1, if (isTwoPlayers) userColor.opposite else opponentColor, pieceOrder, ColorEmeraldDark.copy(alpha = 0.2f), ColorEmeraldLight)
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        val p2Title = if (isTwoPlayers) {
                            val colorName = if (userColor == PieceColor.WHITE) "Đen" else "Trắng"
                            "🛡️ Quân Người chơi 2 ($colorName) đã ăn:"
                        } else "🛡️ Quân máy đã ăn (của Người chơi):"
                        CapturedPieceSection(p2Title, capturedByP2, if (isTwoPlayers) userColor else userColor, pieceOrder, ColorCrimsonMuted.copy(alpha = 0.2f), ColorCrimsonSoft)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Quy đổi điểm: Tốt = 1đ | Mã = 3đ | Tượng = 3đ | Xe = 5đ | Hậu = 9đ", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f), textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(0.6f).height(44.dp).testTag("close_captured_dialog_button"), colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.5f)), shape = RoundedCornerShape(10.dp), border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor)) {
                            Text("Đóng", color = accentColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .offset(x = 10.dp, y = (-10).dp)
                        .size(34.dp)
                        .background(Color.Black.copy(alpha = 0.8f), CircleShape)
                        .border(2.2.dp, accentColor, CircleShape)
                        .shadow(4.dp, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = accentColor, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun CapturedPieceSection(title: String, capturedList: List<PieceType>, opponentColor: PieceColor, pieceOrder: List<PieceType>, badgeColor: Color, borderColor: Color) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color.Black.copy(alpha = 0.2f)).border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)).padding(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f, fill = false), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Tổng: ${capturedList.sumOf { it.pointValue }}đ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = borderColor, maxLines = 1)
        }
        Spacer(modifier = Modifier.height(6.dp))
        if (capturedList.isEmpty()) {
            Text("Chưa ăn được quân cờ nào.", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))
        } else {
            val counts = pieceOrder.mapNotNull { type -> val count = capturedList.count { it == type }; if (count > 0) Triple(type, count, count * type.pointValue) else null }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                counts.forEach { (type, count, subtotal) ->
                    val symbol = if (opponentColor == PieceColor.WHITE) type.symbolWhite else type.symbolBlack
                    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(badgeColor).padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(symbol, fontSize = 20.sp, color = if (opponentColor == PieceColor.WHITE) Color.White else Color(0xFFD4D4D4))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("${type.displayNameVi} (${type.pointValue}đ)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Text("x${count} = +${subtotal}đ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = borderColor, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
fun SaveGameConfirmationDialog(
    onConfirm: (Boolean) -> Unit,
    onCancel: () -> Unit,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColor = Color(selectedTheme.accentColor)
    val surfaceColor = Color(selectedTheme.surfaceColor)
    HideSystemBarsInDialog()
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            decorFitsSystemWindows = false
        )
    ) {
        HideSystemBarsInDialog()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                color = surfaceColor,
                border = androidx.compose.foundation.BorderStroke(2.dp, accentColor)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "LƯU VÁN CỜ?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Bạn có muốn lưu lại ván cờ hiện tại để tiếp tục vào lần sau không?",
                        textAlign = TextAlign.Center,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onConfirm(false) },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedievalCrimson),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, MedievalCrimsonBright)
                        ) {
                            Text("KHÔNG LƯU", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { onConfirm(true) },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ColorEmeraldLight),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                        ) {
                            Text("ĐỒNG Ý LƯU", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TextButton(onClick = onCancel) {
                        Text("QUAY LẠI", color = accentColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CheckPopupDialog(
    onDismiss: () -> Unit,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColor = Color(selectedTheme.accentColor)
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(700)
        onDismiss()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "check_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    HideSystemBarsInDialog()
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        HideSystemBarsInDialog()
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onDismiss() }
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                ColorCrimsonRich,
                                Color(0xFF1E0505)
                            )
                        )
                    )
                    .border(
                        3.dp,
                        ColorCrimsonSoft.copy(alpha = glowAlpha),
                        RoundedCornerShape(20.dp)
                    )
                    .border(
                        1.dp,
                        accentColor.copy(alpha = 0.8f),
                        RoundedCornerShape(20.dp)
                    )
                    .shadow(24.dp, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(ColorCrimsonDeep.copy(alpha = 0.4f))
                            .border(2.2.dp, accentColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Chiếu Tướng",
                            tint = accentColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "⚔️ CHIẾU TƯỚNG! ⚔️",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Vua đang nằm trong tầm ngắm!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GameOverDialogTwoPlayersPreview() {
    MyApplicationTheme {
        GameOverDialog(
            gameStatus = GameStatus.CHECKMATE,
            winner = PieceColor.BLACK,
            userColor = PieceColor.BLACK,
            gameMode = GameMode.TWO_PLAYERS,
            onPlayAgain = {},
            onRestart = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameOverDialogWinPreview() {
    MyApplicationTheme {
        GameOverDialog(
            gameStatus = GameStatus.CHECKMATE,
            winner = PieceColor.WHITE,
            userColor = PieceColor.WHITE,
            gameMode = GameMode.VS_AI,
            onPlayAgain = {},
            onRestart = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameOverDialogLossPreview() {
    MyApplicationTheme {
        GameOverDialog(
            gameStatus = GameStatus.CHECKMATE,
            winner = PieceColor.BLACK,
            userColor = PieceColor.WHITE,
            gameMode = GameMode.VS_AI,
            onPlayAgain = {},
            onRestart = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameOverDialogDrawPreview() {
    MyApplicationTheme {
        GameOverDialog(
            gameStatus = GameStatus.STALEMATE,
            winner = null,
            userColor = PieceColor.WHITE,
            gameMode = GameMode.VS_AI,
            onPlayAgain = {},
            onRestart = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 384, heightDp = 854)
@Composable
fun CapturedPiecesDialogLandscapePreview() {
    MyApplicationTheme {
        CapturedPiecesDialog(
            state = ChessUiState(
                capturedWhitePieces = listOf(PieceType.PAWN, PieceType.PAWN, PieceType.KNIGHT),
                capturedBlackPieces = listOf(PieceType.QUEEN, PieceType.ROOK)
            ),
            onDismiss = {}
        )
    }
}
