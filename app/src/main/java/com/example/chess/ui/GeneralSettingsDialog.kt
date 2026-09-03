package com.example.chess.ui

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
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.tooling.preview.Preview
import com.example.chess.model.ChessTheme
import com.example.ui.theme.*

@Composable
fun GeneralSettingsDialog(
    isSoundEnabled: Boolean,
    isMoveHintsEnabled: Boolean,
    isSaveGameEnabled: Boolean,
    onSoundToggled: (Boolean) -> Unit,
    onMoveHintsToggled: (Boolean) -> Unit,
    onSaveGameToggled: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    selectedTheme: ChessTheme? = null,
    isHintEnabled: Boolean = true,
    isResignEnabled: Boolean = true,
    isUndoEnabled: Boolean = true,
    onHintToggled: (Boolean) -> Unit = {},
    onResignToggled: (Boolean) -> Unit = {},
    onUndoToggled: (Boolean) -> Unit = {}
) {
    val useDarkIcons = selectedTheme?.let { isThemeLight(it) } ?: false
    val statusBarColorInt = selectedTheme?.backgroundColors?.first()?.toInt() ?: android.graphics.Color.TRANSPARENT
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        HideSystemBarsInDialog(useDarkIcons, statusBarColorInt)
        GeneralSettingsDialogContent(
            isSoundEnabled = isSoundEnabled,
            isMoveHintsEnabled = isMoveHintsEnabled,
            isSaveGameEnabled = isSaveGameEnabled,
            onSoundToggled = onSoundToggled,
            onMoveHintsToggled = onMoveHintsToggled,
            onSaveGameToggled = onSaveGameToggled,
            onDismiss = onDismiss,
            selectedTheme = selectedTheme,
            isHintEnabled = isHintEnabled,
            isResignEnabled = isResignEnabled,
            isUndoEnabled = isUndoEnabled,
            onHintToggled = onHintToggled,
            onResignToggled = onResignToggled,
            onUndoToggled = onUndoToggled
        )
    }
}

@Composable
fun GeneralSettingsDialogContent(
    isSoundEnabled: Boolean,
    isMoveHintsEnabled: Boolean,
    isSaveGameEnabled: Boolean,
    onSoundToggled: (Boolean) -> Unit,
    onMoveHintsToggled: (Boolean) -> Unit,
    onSaveGameToggled: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    selectedTheme: ChessTheme? = null,
    isHintEnabled: Boolean = true,
    isResignEnabled: Boolean = true,
    isUndoEnabled: Boolean = true,
    onHintToggled: (Boolean) -> Unit = {},
    onResignToggled: (Boolean) -> Unit = {},
    onUndoToggled: (Boolean) -> Unit = {}
) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val accentColor = selectedTheme?.let { Color(it.accentColor) } ?: MaterialTheme.colorScheme.tertiary
    val textColor = selectedTheme?.let { Color(it.textColor) } ?: Color.White
    val surfaceColor = selectedTheme?.let { Color(it.surfaceColor) } ?: MedievalGold
    val bgColors = selectedTheme?.backgroundColors?.map { Color(it) } ?: listOf(Color(0xFF1A1A1A), Color.Black)
    val btnColor =  selectedTheme?.let { Color(it.lightSquareColor) } ?: Color.White;

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.45f else 0.85f)
                .wrapContentHeight()
                .clickable(indication = null, interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }) {},
            contentAlignment = Alignment.TopEnd
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, accentColor, RoundedCornerShape(16.dp))
                    .testTag("general_settings_dialog"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = bgColors.first())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(bgColors))
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "CÀI ĐẶT CHUNG",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Row 1: Sound
                    SettingsRow(
                        icon = Icons.Default.VolumeUp,
                        label = "Âm thanh",
                        checked = isSoundEnabled,
                        onCheckedChange = onSoundToggled,
                        selectedTheme = selectedTheme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Row 2: Move Hints
                    SettingsRow(
                        icon = Icons.Default.Lightbulb,
                        label = "Gợi ý ô đi được",
                        checked = isMoveHintsEnabled,
                        onCheckedChange = onMoveHintsToggled,
                        selectedTheme = selectedTheme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Row 3: Save Game
                    SettingsRow(
                        icon = Icons.Default.Save,
                        label = "Lưu ván cờ",
                        checked = isSaveGameEnabled,
                        onCheckedChange = onSaveGameToggled,
                        selectedTheme = selectedTheme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Row 4: Hint
                    SettingsRow(
                        icon = Icons.Default.Lightbulb,
                        label = "Gợi ý",
                        checked = isHintEnabled,
                        onCheckedChange = onHintToggled,
                        selectedTheme = selectedTheme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Row 5: Resign
                    SettingsRow(
                        icon = Icons.Default.Flag,
                        label = "Đầu hàng",
                        checked = isResignEnabled,
                        onCheckedChange = onResignToggled,
                        selectedTheme = selectedTheme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Row 6: Undo
                    SettingsRow(
                        icon = Icons.Default.Undo,
                        label = "Hoàn tác",
                        checked = isUndoEnabled,
                        onCheckedChange = onUndoToggled,
                        selectedTheme = selectedTheme
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = surfaceColor),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "ĐÓNG",
                            color = if (selectedTheme != null) Color(selectedTheme.textColor) else ColorDarkDeep,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Close Button (X)
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .offset(x = 5.dp, y = (-5).dp)
                    .size(24.dp)
                    .background(btnColor, CircleShape)
                    .border(2.dp, accentColor, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Đóng",
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    selectedTheme: ChessTheme? = null
) {
    val accentColor = selectedTheme?.let { Color(it.accentColor) } ?: MaterialTheme.colorScheme.tertiary
    val textColor = selectedTheme?.let { Color(it.textColor) } ?: Color.White
    val surfaceColor = selectedTheme?.let { Color(it.surfaceColor) } ?: MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaceColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.Black.copy(alpha = 0.4f)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GeneralSettingsDialogPreview() {
    MyApplicationTheme(selectedTheme = ChessTheme.CLASSIC) {
        GeneralSettingsDialogContent(
            isSoundEnabled = true,
            isMoveHintsEnabled = true,
            isSaveGameEnabled = false,
            onSoundToggled = {},
            onMoveHintsToggled = {},
            onSaveGameToggled = {},
            onDismiss = {},
            selectedTheme = ChessTheme.CLASSIC
        )
    }
}
