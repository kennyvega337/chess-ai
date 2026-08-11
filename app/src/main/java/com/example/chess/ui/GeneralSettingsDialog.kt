package com.example.chess.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

@Composable
fun GeneralSettingsDialog(
    isSoundEnabled: Boolean,
    isMoveHintsEnabled: Boolean,
    isSaveGameEnabled: Boolean,
    onSoundToggled: (Boolean) -> Unit,
    onMoveHintsToggled: (Boolean) -> Unit,
    onSaveGameToggled: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
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
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (isLandscape) 0.45f else 0.85f)
                    .wrapContentHeight(),
                contentAlignment = Alignment.TopEnd
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, MedievalGold, RoundedCornerShape(16.dp))
                        .testTag("general_settings_dialog"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ColorDarkDeep)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
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
                                tint = MedievalGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "CÀI ĐẶT CHUNG",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedievalGoldLight
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Row 1: Sound
                        SettingsRow(
                            icon = Icons.Default.VolumeUp,
                            label = "Âm thanh",
                            checked = isSoundEnabled,
                            onCheckedChange = onSoundToggled
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Row 2: Move Hints
                        SettingsRow(
                            icon = Icons.Default.Lightbulb,
                            label = "Gợi ý ô đi được",
                            checked = isMoveHintsEnabled,
                            onCheckedChange = onMoveHintsToggled
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Row 3: Save Game
                        SettingsRow(
                            icon = Icons.Default.Save,
                            label = "Lưu ván cờ",
                            checked = isSaveGameEnabled,
                            onCheckedChange = onSaveGameToggled
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "ĐÓNG",
                                color = ColorDarkDeep,
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
                        .offset(x = 10.dp, y = (-10).dp)
                        .size(34.dp)
                        .background(ColorWoodMid, CircleShape)
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
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF22140A), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0x33D4AF37), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MedievalGoldLight,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                color = MedievalParchment,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MedievalGold,
                checkedTrackColor = MedievalGold.copy(alpha = 0.4f),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.DarkGray
            )
        )
    }
}
