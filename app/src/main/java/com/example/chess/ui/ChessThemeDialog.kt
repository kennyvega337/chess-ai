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
import androidx.compose.ui.draw.shadow
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
    var tempTheme by remember { mutableStateOf(selectedTheme) }
    var tempViewMode by remember { mutableStateOf(viewMode) }
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

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
                    .fillMaxWidth(if (isLandscape) 0.75f else 0.88f)
                    .fillMaxHeight(if (isLandscape) 0.9f else 0.8f)
                    .wrapContentHeight(),
                contentAlignment = Alignment.TopEnd
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, MedievalGold, RoundedCornerShape(16.dp))
                        .testTag("chess_theme_dialog"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1D0E06))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(if (isLandscape) 12.dp else 16.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = MedievalGold,
                                modifier = Modifier.size(if (isLandscape) 22.dp else 26.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GIAO DIỆN BÀN CỜ",
                                fontSize = if (isLandscape) 16.sp else 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedievalGoldLight
                            )
                        }

                        Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 20.dp))

                        Text(
                            text = "CHẾ ĐỘ HIỂN THỊ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedievalParchment,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ViewModeButton(
                                text = "CHẾ ĐỘ 2D",
                                isSelected = tempViewMode == BoardViewMode.VIEW_2D,
                                onClick = { tempViewMode = BoardViewMode.VIEW_2D },
                                modifier = Modifier.weight(1f),
                                height = if (isLandscape) 36.dp else 44.dp
                            )
                            ViewModeButton(
                                text = "CHẾ ĐỘ 3D",
                                isSelected = tempViewMode == BoardViewMode.VIEW_3D,
                                onClick = { tempViewMode = BoardViewMode.VIEW_3D },
                                modifier = Modifier.weight(1f),
                                height = if (isLandscape) 36.dp else 44.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(if (isLandscape) 14.dp else 24.dp))

                        Text(
                            text = "CHỦ ĐỀ BÀN CỜ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedievalParchment,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(if (isLandscape) 3 else 2),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(if (isLandscape) 8.dp else 12.dp),
                            verticalArrangement = Arrangement.spacedBy(if (isLandscape) 8.dp else 12.dp)
                        ) {
                            items(ChessTheme.themes) { theme ->
                                ThemeItem(
                                    theme = theme,
                                    isSelected = theme.name == tempTheme.name,
                                    onClick = { tempTheme = theme },
                                    isLandscape = isLandscape
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 16.dp))

                        Button(
                            onClick = {
                                onThemeSelect(tempTheme)
                                onViewModeChange(tempViewMode)
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth().height(if (isLandscape) 40.dp else 48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "ÁP DỤNG THAY ĐỔI", 
                                color = Color(0xFF1D0E06), 
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = if (isLandscape) 13.sp else 14.sp
                            )
                        }
                    }
                }

                // Overflowing Close Button (X) - Stuck to corner
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .offset(x = 10.dp, y = (-10).dp)
                        .size(34.dp)
                        .background(Color(0xFF382315), CircleShape)
                        .border(2.dp, MedievalGold, CircleShape)
                        .shadow(8.dp, CircleShape)
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
private fun ViewModeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 44.dp
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(height),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MedievalGold else Color(0xFF382315)
        ),
        shape = RoundedCornerShape(8.dp),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MedievalGold.copy(alpha = 0.3f)) else null,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color(0xFF1D0E06) else MedievalGoldLight,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ThemeItem(
    theme: ChessTheme,
    isSelected: Boolean,
    onClick: () -> Unit,
    isLandscape: Boolean = false
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
            .padding(if (isLandscape) 4.dp else 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (isLandscape) 60.dp else 80.dp)
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
        Spacer(modifier = Modifier.height(if (isLandscape) 4.dp else 8.dp))
        Text(
            text = theme.displayName,
            color = if (isSelected) MedievalGoldLight else MedievalParchment,
            fontSize = if (isLandscape) 12.sp else 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
