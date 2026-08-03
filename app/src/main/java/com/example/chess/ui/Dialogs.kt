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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.app.Activity
import com.example.chess.model.GameStatus
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.ui.theme.ChessDarkBg
import com.example.ui.theme.MedievalCrimson
import com.example.ui.theme.MedievalCrimsonBright
import com.example.ui.theme.MedievalDarkWood
import com.example.ui.theme.MedievalEmerald
import com.example.ui.theme.MedievalGold
import com.example.ui.theme.MedievalGoldLight
import com.example.ui.theme.MedievalMidWood
import com.example.ui.theme.MedievalParchment
import com.example.ui.theme.MedievalParchmentDark
import com.example.ui.theme.MedievalSteel

@Composable
fun HideSystemBarsInDialog() {
    val view = LocalView.current
    DisposableEffect(view) {
        fun applyHide(win: android.view.Window) {
            WindowCompat.setDecorFitsSystemWindows(win, false)
            win.navigationBarColor = android.graphics.Color.TRANSPARENT
            win.statusBarColor = android.graphics.Color.TRANSPARENT

            win.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            win.clearFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            
            @Suppress("DEPRECATION")
            win.decorView.systemUiVisibility = (
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )

            WindowCompat.getInsetsController(win, win.decorView).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
                hide(WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.statusBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }

        // Walk view parent tree to find DialogWindowProvider
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
            win.setFlags(
                android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            )
            applyHide(win)
            win.clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)

            win.decorView.post { applyHide(win) }
            win.decorView.setOnSystemUiVisibilityChangeListener {
                applyHide(win)
            }
        }

        (view.context as? Activity)?.window?.let { mainWin ->
            applyHide(mainWin)
        }

        onDispose {
            (view.context as? Activity)?.window?.let { mainWin ->
                applyHide(mainWin)
            }
        }
    }
}

/**
 * Medieval Heraldic Side Selection Dialog
 */
@Composable
fun SideSelectionDialog(
    onSideSelected: (PieceColor) -> Unit,
    onDismiss: (() -> Unit)? = null
) {
    HideSystemBarsInDialog()
    Dialog(
        onDismissRequest = { onDismiss?.invoke() },
        properties = DialogProperties(decorFitsSystemWindows = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
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
                            colors = listOf(Color(0xFF2C190E), Color(0xFF1B0F08))
                        )
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header medieval emblem
                Text(
                    text = "⚔️ CỜ VUA TRUNG CỔ ⚔️",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedievalGoldLight,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "CHỌN BÊN BẮT ĐẦU",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MedievalGold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, MedievalGold, Color.Transparent)
                            )
                        )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Hãy chọn phe quân hoàng gia của bạn:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MedievalParchment,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Option 1: White (Goes first)
                SideOptionCard(
                    title = "Bạch Vương - Đi trước (♔)",
                    subtitle = "Cầm quân Trắng, xuất quân đánh trước",
                    iconSymbol = "♔",
                    badgeColor = Color(0xFFF7F4EB),
                    textColor = Color(0xFF1E130B),
                    borderColor = MedievalGold,
                    onClick = { onSideSelected(PieceColor.WHITE) },
                    testTag = "select_white_button"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Option 2: Black (Goes second)
                SideOptionCard(
                    title = "Hắc Vương - Đi sau (♚)",
                    subtitle = "Cầm quân Đen, phòng thủ đánh sau",
                    iconSymbol = "♚",
                    badgeColor = Color(0xFF231810),
                    textColor = MedievalGoldLight,
                    borderColor = MedievalGold.copy(alpha = 0.7f),
                    onClick = { onSideSelected(PieceColor.BLACK) },
                    testTag = "select_black_button"
                )

                if (onDismiss != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(onClick = onDismiss) {
                        Text("Hủy bỏ", color = MedievalSteel)
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
        color = Color(0xFF2F1D12)
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

/**
 * Medieval Royal Parchment Game Over Announcement Dialog
 */
@Composable
fun GameOverDialog(
    gameStatus: GameStatus,
    winner: PieceColor?,
    userColor: PieceColor,
    onPlayAgain: () -> Unit
) {
    val isWin = winner == userColor
    val isDraw = gameStatus == GameStatus.STALEMATE
    val isResigned = gameStatus == GameStatus.RESIGNED

    val bannerTitle = when {
        isWin -> "👑 THÔNG BÁO CHIẾN THẮNG 👑"
        isDraw -> "⚖️ HÒA CỜ TRUNG CỔ ⚖️"
        isResigned && !isWin -> "🏳️ BẠN ĐÃ ĐẦU HÀNG 🏳️"
        else -> "⚔️ THÔNG BÁO BẠI TRẬN ⚔️"
    }

    val mainStatusText = when {
        isWin -> "BẠN ĐÃ CHIẾN THẮNG!"
        isDraw -> "TRẬN ĐẤU BẤT PHÂN THẮNG BẠI!"
        isResigned && !isWin -> "BẠN ĐÃ CHỦ ĐỘNG ĐẦU HÀNG!"
        else -> "BẠN ĐÃ THẤT THỦ!"
    }

    val bodyText = when {
        isWin -> "Xuất sắc! Bằng mưu trí và chiến thuật kiệt xuất, bạn đã chiếu bí quân địch và giành toàn thắng."
        isDraw -> "Cả hai phe đã chiến đấu ngoan cường. Thế cờ hòa (Stalemate) không còn nước đi hợp lệ."
        isResigned && !isWin -> "Bạn đã giơ cờ trắng chịu thua ván đấu này. Hãy chuẩn bị lực lượng cho ván mới!"
        else -> "Chiến vương địch đã chiếu bí bạn! Đừng nản lòng, hãy chấn chỉnh quân ngũ để phục thù."
    }

    val accentColor = when {
        isWin -> MedievalEmerald
        isDraw -> MedievalGold
        else -> MedievalCrimsonBright
    }

    HideSystemBarsInDialog()
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(decorFitsSystemWindows = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
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
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Scroll Banner
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
                        letterSpacing = 1.sp
                    )
                }

                // Center Medieval Emblem
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
                    color = Color(0xFF2C190E),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = bodyText,
                    fontSize = 13.sp,
                    color = Color(0xFF4A3423),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Medieval Gold Button
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
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = MedievalGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CHƠI VÁN MỚI",
                        color = MedievalGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

/**
 * Medieval Center Check (Chiếu Tướng) Popup Dialog.
 * Appears in the center of the screen, styled like an urgent royal medieval shield banner.
 * Auto-dismisses after 2.5 seconds or when touched.
 */
@Composable
fun CheckPopupDialog(
    onDismiss: () -> Unit
) {
    // Auto dismiss after 2.5s
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2500)
        onDismiss()
    }

    // Glowing animation for edge border
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
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .clickable { onDismiss() }
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF3B0B0B),
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
                // Shield / Flash Emblem
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

/**
 * Medieval Pawn Promotion Ceremony Dialog ("Phong Cấp Hoàng Gia")
 */
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
        properties = DialogProperties(decorFitsSystemWindows = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
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

/**
 * Medieval Resign Confirmation Dialog ("Xác Nhận Đầu Hàng")
 */
@Composable
fun ResignConfirmationDialog(
    onConfirmResign: () -> Unit,
    onCancel: () -> Unit
) {
    HideSystemBarsInDialog()
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(decorFitsSystemWindows = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
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
                        .border(2.dp, Color(0xFFEF4444), CircleShape),
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
    }
}
