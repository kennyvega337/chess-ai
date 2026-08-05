package com.example.chess.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.chess.model.BoardViewMode
import com.example.chess.model.ChessTheme
import com.example.ui.theme.MedievalGold
import com.example.ui.theme.MedievalGoldLight
import com.example.ui.theme.MedievalParchment

@Composable
fun ChessThemeDialog(
    selectedTheme: ChessTheme,
    viewMode: BoardViewMode,
    onThemeSelect: (ChessTheme) -> Unit,
    onViewModeChange: (BoardViewMode) -> Unit,
    onDismiss: () -> Unit
) {
    // Trạng thái tạm thời khi người dùng chọn (chưa nhấn Áp dụng)
    var tempTheme by remember { mutableStateOf(selectedTheme) }
    var tempViewMode by remember { mutableStateOf(viewMode) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = true, 
            decorFitsSystemWindows = false // Thay đổi thành false để kiểm soát insets tốt hơn
        )
    ) {
        HideSystemBarsInDialog() // Gọi lại hàm ẩn thanh hệ thống cho Dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight() // Tự động co dãn theo nội dung để nằm giữa
                .border(2.dp, MedievalGold, RoundedCornerShape(16.dp))
                .testTag("chess_theme_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1D0E06))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header - SỬ DỤNG SPACE BETWEEN ĐỂ ĐẨY 2 BÊN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MedievalGold,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GIAO DIỆN BÀN CỜ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedievalGoldLight
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF382315), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = MedievalGoldLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // === CHẾ ĐỘ HIỂN THỊ (2D / 3D) ===
                Text(
                    text = "CHẾ ĐỘ HIỂN THỊ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedievalParchment,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ViewModeButton(
                        text = "CHẾ ĐỘ 2D",
                        isSelected = tempViewMode == BoardViewMode.VIEW_2D,
                        onClick = { tempViewMode = BoardViewMode.VIEW_2D },
                        modifier = Modifier.weight(1f)
                    )
                    ViewModeButton(
                        text = "CHẾ ĐỘ 3D",
                        isSelected = tempViewMode == BoardViewMode.VIEW_3D,
                        onClick = { tempViewMode = BoardViewMode.VIEW_3D },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // === CHỦ ĐỀ BÀN CỜ ===
                Text(
                    text = "CHỦ ĐỀ BÀN CỜ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedievalParchment,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Bây giờ tất cả các theme đều được hiển thị cho cả 2D và 3D
                val allThemes = ChessTheme.themes

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allThemes) { theme ->
                        ThemeItem(
                            theme = theme,
                            isSelected = theme.name == tempTheme.name,
                            onClick = { tempTheme = theme }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onThemeSelect(tempTheme)
                        onViewModeChange(tempViewMode)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ÁP DỤNG THAY ĐỔI", color = Color(0xFF1D0E06), fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
private fun ViewModeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MedievalGold else Color(0xFF382315)
        ),
        shape = RoundedCornerShape(8.dp),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MedievalGold.copy(alpha = 0.3f)) else null
    ) {
        Text(
            text = text,
            color = if (isSelected) Color(0xFF1D0E06) else MedievalGoldLight,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ThemeItem(
    theme: ChessTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0x33D4AF37) else Color(0xFF28180E))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MedievalGold else Color(0x33D4AF37),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mini Board Preview
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, Color.Black.copy(alpha = 0.5f))
        ) {
            Column {
                repeat(4) { r ->
                    Row {
                        repeat(4) { c ->
                            val color = if ((r + c) % 2 == 0) theme.lightSquareColor else theme.darkSquareColor
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(Color(color))
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = theme.displayName,
            color = if (isSelected) MedievalGoldLight else MedievalParchment,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
