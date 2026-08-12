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
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.theme.MyApplicationTheme
import com.example.chess.model.DifficultyLevel
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.ui.theme.MedievalCrimson
import com.example.ui.theme.MedievalCrimsonBright
import com.example.ui.theme.MedievalDarkWood
import com.example.ui.theme.MedievalEmerald
import com.example.ui.theme.*

@Composable
fun HideSystemBarsInDialog() {
    val view = LocalView.current
    SideEffect {
        fun applyHide(win: android.view.Window) {
            WindowCompat.setDecorFitsSystemWindows(win, false)
            win.navigationBarColor = android.graphics.Color.TRANSPARENT
            
            // Critical for preventing jumps: ensure window is full screen before drawing
            win.setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )

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

        dialogWindow?.let { win ->
            applyHide(win)
        }
    }
}

@Composable
fun SideSelectionDialog(
    initialDifficulty: DifficultyLevel = DifficultyLevel.LEVEL_2,
    onSideSelected: (PieceColor) -> Unit = {},
    onSideAndDifficultySelected: (PieceColor, DifficultyLevel) -> Unit = { color, level -> onSideSelected(color) },
    onDismiss: (() -> Unit)? = null
) {
    HideSystemBarsInDialog()
    var selectedDifficulty by remember { mutableStateOf(initialDifficulty) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val isLargeScreen = configuration.smallestScreenWidthDp >= 600
    val cardWidthAlpha = if (isLargeScreen) 0.6f else if (isLandscape) 0.75f else 0.88f

    Dialog(
        onDismissRequest = { onDismiss?.invoke() },
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
                    .fillMaxWidth(cardWidthAlpha)
                    .widthIn(max = 480.dp)
                    .wrapContentHeight(),
                contentAlignment = Alignment.TopEnd
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(if (isLandscape) 0.92f else Float.NaN)
                        .border(2.dp, MedievalGold, RoundedCornerShape(16.dp))
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .testTag("side_selection_dialog"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MedievalMidWood)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(ColorWoodDark, ColorWoodDeep)
                                )
                            )
                            .padding(if (isLandscape) 16.dp else 20.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚔️ CỜ VUA TRUNG CỔ ⚔️",
                            fontSize = if (isLandscape) 12.sp else 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedievalGoldLight,
                            letterSpacing = 2.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 14.dp))

                        Text(
                            text = "1. Cấp độ đối thủ (Máy):",
                            style = if (isLandscape) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MedievalParchment,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(2.dp))
                        
                        Text(
                            text = selectedDifficulty.displayNameVi,
                            fontWeight = FontWeight.ExtraBold,
                            color = MedievalGoldLight,
                            fontSize = if (isLandscape) 12.sp else 13.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )

                        Slider(
                            value = selectedDifficulty.level.toFloat(),
                            onValueChange = { selectedDifficulty = DifficultyLevel.fromInt(it.toInt()) },
                            valueRange = 1f..7f,
                            steps = 5,
                            colors = SliderDefaults.colors(
                                thumbColor = MedievalGold,
                                activeTrackColor = MedievalGold,
                                inactiveTrackColor = ColorDarkBrown,
                            ),
                            modifier = Modifier.fillMaxWidth().height(if (isLandscape) 32.dp else 40.dp)
                        )

                        Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 16.dp))

                        Text(
                            text = "2. Phe quân của bạn:",
                            style = if (isLandscape) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MedievalParchment,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(if (isLandscape) 4.dp else 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Triple(PieceColor.WHITE, "Bạch Vương", "♔"),
                                Triple(PieceColor.BLACK, "Hắc Vương", "♚")
                            ).forEach { (color, label, icon) ->
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { onSideAndDifficultySelected(color, selectedDifficulty) }
                                        .border(1.dp, MedievalGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                                    color = ColorWoodVariant
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = if (isLandscape) 6.dp else 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = icon, fontSize = if (isLandscape) 18.sp else 20.sp, color = MedievalGoldLight)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MedievalParchment)
                                    }
                                }
                            }
                        }

                        if (onDismiss != null) {
                            Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 12.dp))
                            TextButton(
                                onClick = onDismiss,
                                modifier = Modifier.height(if (isLandscape) 36.dp else 48.dp)
                            ) {
                                Text("Hủy bỏ", color = MedievalSteel, fontSize = 12.sp)
                            }
                        }
                    }
                }

                if (onDismiss != null) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .offset(x = 10.dp, y = (-10).dp)
                            .size(34.dp)
                            .background(ColorWoodMid, CircleShape)
                            .border(2.dp, MedievalGold, CircleShape)
                            .shadow(8.dp, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = MedievalGoldLight, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SideOptionCard(
    title: String,
    subtitle: String,
    iconSymbol: String,
    badgeColor: Color,
    textColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .testTag(testTag),
        color = ColorWoodSoft
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
                    .border(2.dp, MedievalGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconSymbol,
                    fontSize = 28.sp,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MedievalGoldLight
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MedievalParchmentDark
                )
            }
        }
    }
}

@Composable
fun GameOverDialog(
    gameStatus: GameStatus,
    winner: PieceColor?,
    userColor: PieceColor,
    gameMode: GameMode = GameMode.VS_AI,
    onPlayAgain: () -> Unit,
    onRestart: () -> Unit,
    onDismiss: () -> Unit,
    onNextMatch: (() -> Unit)? = null
) {
    val isWin = winner == userColor
    val isDraw = gameStatus == GameStatus.STALEMATE || gameStatus == GameStatus.DRAW
    val isResigned = gameStatus == GameStatus.RESIGNED

    val bannerTitle = if (gameMode == GameMode.TWO_PLAYERS) {
        if (winner != null) "👑 THÔNG BÁO KẾT QUẢ 👑" else "⚖️ HÒA CỜ TRUNG CỔ ⚖️"
    } else {
        when {
            isWin -> "👑 THÔNG BÁO CHIẾN THẮNG 👑"
            isDraw -> "⚖️ HÒA CỜ TRUNG CỔ ⚖️"
            isResigned && !isWin -> "🏳️ BẠN ĐÃ ĐẦU HÀNG 🏳️"
            else -> "⚔️ THÔNG BÁO BẠI TRẬN ⚔️"
        }
    }

    val mainStatusText = if (gameMode == GameMode.TWO_PLAYERS) {
        val p1ColorName = if (userColor == PieceColor.WHITE) "TRẮNG" else "ĐEN"
        val p2ColorName = if (userColor == PieceColor.WHITE) "ĐEN" else "TRẮNG"
        when (winner) {
            userColor -> "NGƯỜI CHƠI 1 ($p1ColorName) THẮNG!"
            userColor.opposite -> "NGƯỜI CHƠI 2 ($p2ColorName) THẮNG!"
            else -> "TRẬN ĐẤU BẤT PHÂN THẮNG BẠI!"
        }
    } else {
        when {
            isWin -> "BẠN ĐÃ CHIẾN THẮNG!"
            isDraw -> "TRẬN ĐẤU BẤT PHÂN THẮNG BẠI!"
            isResigned && !isWin -> "BẠN ĐÃ CHỦ ĐỘNG ĐẦU HÀNG!"
            else -> "BẠN ĐÃ THẤT THỦ!"
        }
    }

    val bodyText = if (gameMode == GameMode.TWO_PLAYERS) {
        val p1ColorVi = if (userColor == PieceColor.WHITE) "Quân Trắng" else "Quân Đen"
        val p2ColorVi = if (userColor == PieceColor.WHITE) "Quân Đen" else "Quân Trắng"
        when (winner) {
            userColor -> "Chúc mừng Người chơi 1 ($p1ColorVi) đã bằng chiến thuật kiệt xuất giành thắng lợi toàn diện!"
            userColor.opposite -> "Chúc mừng Người chơi 2 ($p2ColorVi) đã bằng chiến thuật kiệt xuất giành thắng lợi toàn diện!"
            else -> "Cả hai người chơi đã chiến đấu ngoan cường và hòa ván cờ này."
        }
    } else {
        when {
            isWin -> "Xuất sắc! Bằng mưu trí và chiến thuật kiệt xuất, bạn đã chiếu bí quân địch và giành toàn thắng."
            isDraw -> "Cả hai phe đã chiến đấu ngoan cường. Thế cờ hòa (Stalemate) không còn nước đi hợp lệ."
            isResigned && !isWin -> "Bạn đã giơ cờ trắng chịu thua ván đấu này. Hãy chuẩn bị lực lượng cho ván mới!"
            else -> "Chiến vương địch đã chiếu bí bạn! Đừng nản lòng, hãy chấn chỉnh quân ngũ để phục thù."
        }
    }

    val accentColor = if (gameMode == GameMode.TWO_PLAYERS) {
        if (winner != null) MedievalEmerald else MedievalGold
    } else {
        when {
            isWin -> MedievalEmerald
            isDraw -> MedievalGold
            else -> MedievalCrimsonBright
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val isLargeScreen = configuration.smallestScreenWidthDp >= 600
    val cardWidthAlpha = if (isLargeScreen) 0.65f else if (isLandscape) 0.6f else 0.9f

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
            // Container for centered dialog and close button
            Box(
                modifier = Modifier
                    .fillMaxWidth(cardWidthAlpha)
                    .widthIn(max = 500.dp)
                    .wrapContentHeight(),
                contentAlignment = Alignment.TopEnd
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(3.dp, MedievalGold, RoundedCornerShape(18.dp))
                        .shadow(24.dp, RoundedCornerShape(18.dp))
                        .testTag("game_over_dialog"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MedievalParchment)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(MedievalParchment, MedievalParchmentDark)
                                )
                            )
                            .verticalScroll(rememberScrollState())
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MedievalDarkWood,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, MedievalGold),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = bannerTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedievalGold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                letterSpacing = 1.sp,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.15f))
                                .border(2.5.dp, accentColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isWin) Icons.Default.EmojiEvents else if (isDraw) Icons.Default.Security else Icons.Default.SentimentDissatisfied,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = mainStatusText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ColorWoodDark,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = bodyText,
                            fontSize = 13.sp,
                            color = ColorBrownMuted,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        if (onNextMatch != null && isWin) {
                            Button(
                                onClick = onNextMatch,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .shadow(8.dp, RoundedCornerShape(12.dp))
                                    .testTag("next_puzzle_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = MedievalEmerald),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, MedievalGold)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "VÁN TIẾP THEO",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Button(
                            onClick = onRestart,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .shadow(8.dp, RoundedCornerShape(12.dp))
                                .testTag("restart_game_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF93C5FD))
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CHƠI LẠI",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onPlayAgain,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .shadow(8.dp, RoundedCornerShape(12.dp))
                                .testTag("play_again_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MedievalDarkWood),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, MedievalGold)
                        ) {
                            Icon(
                                imageVector = if (gameMode == GameMode.PUZZLE) Icons.Default.Extension else Icons.Default.Refresh,
                                contentDescription = null,
                                tint = MedievalGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (gameMode == GameMode.PUZZLE) "ĐỔI CHẾ ĐỘ" else "CHƠI VÁN MỚI",
                                color = MedievalGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // Overflowing Close Button (X) - Stuck to Top-Right corner
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .offset(x = 10.dp, y = (-10).dp)
                        .size(34.dp)
                        .background(Color(0xFF382315), CircleShape)
                        .border(2.dp, MedievalGold, CircleShape)
                        .shadow(4.dp, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Đóng",
                        tint = MedievalGoldLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CheckPopupDialog(
    onDismiss: () -> Unit
) {
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
                        MedievalCrimsonBright.copy(alpha = glowAlpha),
                        RoundedCornerShape(20.dp)
                    )
                    .border(
                        1.dp,
                        MedievalGold.copy(alpha = 0.8f),
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
                            .background(MedievalCrimson.copy(alpha = 0.4f))
                            .border(2.dp, MedievalGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Chiếu Tướng",
                            tint = MedievalGold,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "⚔️ CHIẾU TƯỚNG! ⚔️",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MedievalGold,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Vua đang nằm trong tầm ngắm!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MedievalParchment,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun PawnPromotionDialog(
    color: PieceColor,
    onSelectPiece: (PieceType) -> Unit
) {
    val choices = listOf(
        PieceType.QUEEN to "Hậu ♛",
        PieceType.ROOK to "Xe ♜",
        PieceType.BISHOP to "Tượng ♝",
        PieceType.KNIGHT to "Mã ♞"
    )

    HideSystemBarsInDialog()
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        HideSystemBarsInDialog()
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .padding(12.dp)
                    .border(2.dp, MedievalGold, RoundedCornerShape(16.dp))
                    .shadow(16.dp, RoundedCornerShape(16.dp))
                    .testTag("pawn_promotion_dialog"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MedievalDarkWood)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF2C190E), Color(0xFF190F08))
                            )
                        )
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "👑 PHONG CẤP HOÀNG GIA 👑",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MedievalGold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Chọn binh chủng thăng cấp cho Tốt:",
                        fontSize = 12.sp,
                        color = MedievalParchmentDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        choices.forEach { (type, name) ->
                            val symbol = if (color == PieceColor.WHITE) type.symbolWhite else type.symbolBlack
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF382315))
                                    .border(1.dp, MedievalGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .clickable { onSelectPiece(type) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text(text = symbol, fontSize = 34.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MedievalGoldLight)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RestartConfirmationDialog(
    onConfirmRestart: () -> Unit,
    onCancel: () -> Unit
) {
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
                        .border(2.dp, MedievalGold, RoundedCornerShape(16.dp))
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .testTag("restart_confirmation_dialog"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MedievalDarkWood)
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
                                .background(Color(0xFF1E3A8A).copy(alpha = 0.2f), CircleShape)
                                .border(2.dp, Color(0xFF93C5FD), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color(0xFF93C5FD),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "CHƠI LẠI TRẬN ĐẤU?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MedievalGold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Bạn có chắc chắn muốn làm mới ván cờ này không? Tiến độ hiện tại sẽ bị hủy bỏ.",
                            fontSize = 14.sp,
                            color = MedievalParchment,
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
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, MedievalGold.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MedievalGold)
                            ) {
                                Text("HỦY", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Button(
                                onClick = onConfirmRestart,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
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
    onCancel: () -> Unit
) {
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
                        .border(2.dp, MedievalGold, RoundedCornerShape(16.dp))
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .testTag("resign_confirmation_dialog"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MedievalDarkWood)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF2C190E), Color(0xFF190F08))
                                )
                            )
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                                .border(2.2.dp, Color(0xFFEF4444), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "🏳️ XÁC NHẬN ĐẦU HÀNG 🏳️",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MedievalGold,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Bạn có chắc chắn muốn giơ cờ trắng chịu thua ván đấu này không?",
                            fontSize = 13.sp,
                            color = MedievalParchment,
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
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, MedievalGold)
                            ) {
                                Text(
                                    text = "Đánh Tiếp",
                                    color = MedievalGold,
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
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF991B1B)),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, MedievalGold)
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
                        .background(Color(0xFF382315), CircleShape)
                        .border(2.dp, MedievalGold, CircleShape)
                        .shadow(8.dp, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = MedievalGoldLight, modifier = Modifier.size(18.dp))
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
                        .border(2.dp, MedievalGold, RoundedCornerShape(16.dp))
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .testTag("captured_pieces_dialog"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MedievalDarkWood)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(if (isLandscape) 0.9f else 0.85f)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF2C190E), Color(0xFF190F08))
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
                                .background(MedievalGold.copy(alpha = 0.15f))
                                .border(2.dp, MedievalGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = MedievalGold,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "⚔️ BẢNG CHIẾN TÍCH & ĐIỂM SỐ ⚔️",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MedievalGold,
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
                            color = MedievalParchmentDark,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF22140A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MedievalGold.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    val p1ColorName = if (userColor == PieceColor.WHITE) "TRẮNG" else "ĐEN"
                                    Text(if (isTwoPlayers) "N.CHƠI 1 ($p1ColorName)" else "NGƯỜI CHƠI", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = MedievalGoldLight, maxLines = 1)
                                    Text("${p1Score} điểm", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = ColorEmeraldLight, maxLines = 1)
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when { netDiff > 0 -> ColorEmeraldDark.copy(alpha = 0.3f); netDiff < 0 -> ColorCrimsonMuted.copy(alpha = 0.3f); else -> ColorGoldMuted.copy(alpha = 0.3f) },
                                    border = androidx.compose.foundation.BorderStroke(1.dp, when { netDiff > 0 -> ColorEmeraldLight; netDiff < 0 -> ColorCrimsonSoft; else -> MedievalGold })
                                ) {
                                    Text(
                                        text = when { netDiff > 0 -> if (isTwoPlayers) "+${netDiff} (Trắng ưu thế)" else "+${netDiff} (Ưu thế)"; netDiff < 0 -> if (isTwoPlayers) "+${-netDiff} (Đen ưu thế)" else "${netDiff} (Thất thế)"; else -> "Cân bằng (0)" },
                                        fontSize = 11.sp, fontWeight = FontWeight.Bold, color = when { netDiff > 0 -> ColorEmeraldPale; netDiff < 0 -> ColorCrimsonPale; else -> MedievalGoldLight },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), maxLines = 1, overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    val p2ColorName = if (userColor == PieceColor.WHITE) "ĐEN" else "TRẮNG"
                                    Text(if (isTwoPlayers) "N.CHƠI 2 ($p2ColorName)" else "MÁY (AI)", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = MedievalGoldLight, maxLines = 1)
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
                        Text("Quy đổi điểm: Tốt = 1đ | Mã = 3đ | Tượng = 3đ | Xe = 5đ | Hậu = 9đ", fontSize = 10.sp, color = MedievalParchmentDark, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(0.6f).height(44.dp).testTag("close_captured_dialog_button"), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF382315)), shape = RoundedCornerShape(10.dp), border = androidx.compose.foundation.BorderStroke(1.5.dp, MedievalGold)) {
                            Text("Đóng", color = MedievalGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .offset(x = 10.dp, y = (-10).dp)
                        .size(34.dp)
                        .background(Color(0xFF382315), CircleShape)
                        .border(2.dp, MedievalGold, CircleShape)
                        .shadow(4.dp, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = MedievalGoldLight, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun CapturedPieceSection(title: String, capturedList: List<PieceType>, opponentColor: PieceColor, pieceOrder: List<PieceType>, badgeColor: Color, borderColor: Color) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFF22140A)).border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)).padding(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MedievalParchment, modifier = Modifier.weight(1f, fill = false), maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                            Text("${type.displayNameVi} (${type.pointValue}đ)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MedievalParchment, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Text("x${count} = +${subtotal}đ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = borderColor, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
fun SideSelectionDialogLandscapePreview() {
    MyApplicationTheme {
        SideSelectionDialog(onDismiss = {})
    }
}

@Preview(showBackground = true)
@Composable
fun GameOverDialogTwoPlayersPreview() {
    MyApplicationTheme {
        GameOverDialog(
            gameStatus = GameStatus.CHECKMATE,
            winner = PieceColor.BLACK,
            userColor = PieceColor.BLACK, // Player 1 is Black and won
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

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
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
