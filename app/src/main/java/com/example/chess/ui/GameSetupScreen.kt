package com.example.chess.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.People
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import com.example.chess.data.*
import com.example.chess.model.ChessTheme
import com.example.chess.model.DifficultyLevel
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.chess.model.GameTimerOption
import com.example.chess.model.PieceType
import com.example.chess.model.SideOption
import com.example.chess.model.SpecialTutorialType
import com.example.ui.theme.*

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.platform.LocalConfiguration

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.ui.draw.rotate
import androidx.compose.material3.ExperimentalMaterial3Api
import kotlin.math.roundToInt
import android.app.Activity
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun GameSetupScreen(
    initialSideOption: SideOption = SideOption.WHITE,
    initialDifficulty: DifficultyLevel = DifficultyLevel.LEVEL_2,
    initialGameMode: GameMode = GameMode.VS_AI,
    initialTimerOption: GameTimerOption = GameTimerOption.NONE,
    initialCustomMinutes: Int = 10,
    onStartGame: (SideOption, DifficultyLevel, GameMode, GameTimerOption, Int?) -> Unit,
    onStartTutorialPiece: ((PieceType) -> Unit)? = null,
    onStartSpecialMove: ((SpecialTutorialType) -> Unit)? = null,
    onStartPuzzle: ((String, String, Int) -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    completedPuzzles: Set<String> = emptySet(),
    lastPuzzleCategory: String? = null,
    lastPuzzleLevel: Int? = null,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    var selectedSide by rememberSaveable { mutableStateOf(initialSideOption) }
    var selectedDifficulty by rememberSaveable { mutableStateOf(initialDifficulty) }
    val selectedGameMode = initialGameMode
    var selectedTimerOption by rememberSaveable { mutableStateOf(initialTimerOption) }
    var customMinutes by rememberSaveable { mutableStateOf(initialCustomMinutes) }
    var showCustomTimerDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = selectedTheme.backgroundColors.map { Color(it) }
                )
            )
            .padding(horizontal = if (isLandscape) 12.dp else 16.dp, vertical = if (isLandscape) 4.dp else 12.dp)
            .testTag("game_setup_screen")
    ) {
        if (isLandscape) {
            // === LANDSCAPE LAYOUT ===
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT SIDE COLUMN
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        SetupHeader(isLandscape = true, selectedTheme = selectedTheme)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (selectedGameMode == GameMode.PUZZLE || selectedGameMode == GameMode.ONE_MOVE || selectedGameMode == GameMode.TUTORIAL || selectedGameMode == GameMode.SPECIAL_MOVE) {
                        BackHomeButton(onBack, isLandscape = true, selectedTheme = selectedTheme)
                    }

                    if (selectedGameMode != GameMode.PUZZLE && selectedGameMode != GameMode.TUTORIAL && selectedGameMode != GameMode.ONE_MOVE && selectedGameMode != GameMode.SPECIAL_MOVE) {
                        MatchPreviewCard(
                            selectedSide,
                            selectedDifficulty,
                            selectedGameMode,
                            selectedTimerOption,
                            customMinutes,
                            isLandscape = true,
                            selectedTheme = selectedTheme
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BackHomeButton(onBack, modifier = Modifier.weight(0.15f), isLandscape = true, showText = false, selectedTheme = selectedTheme)
                            StartGameButton(
                                gameMode = selectedGameMode,
                                onClick = {
                                    onStartGame(
                                        selectedSide,
                                        selectedDifficulty,
                                        selectedGameMode,
                                        selectedTimerOption,
                                        customMinutes
                                    )
                                },
                                isLandscape = true,
                                modifier = Modifier.weight(0.85f),
                                selectedTheme = selectedTheme
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // RIGHT SIDE COLUMN
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (selectedGameMode) {
                        GameMode.VS_AI -> {
                            DifficultySectionCard(
                                selectedDifficulty = selectedDifficulty,
                                onSelectDifficulty = { selectedDifficulty = it },
                                isLandscape = true,
                                selectedTheme = selectedTheme
                            )

                            SideSelectionCard(
                                selectedSide = selectedSide,
                                onSelectSide = { selectedSide = it },
                                isLandscape = true,
                                selectedTheme = selectedTheme
                            )

                            TimerSelectionCard(
                                selectedTimerOption = selectedTimerOption,
                                onSelectTimerOption = { option ->
                                    if (option == GameTimerOption.CUSTOM) {
                                        showCustomTimerDialog = true
                                    } else {
                                        selectedTimerOption = option
                                    }
                                },
                                customMinutes = if (selectedTimerOption == GameTimerOption.CUSTOM) customMinutes else null,
                                isLandscape = true,
                                selectedTheme = selectedTheme
                            )
                        }
                        GameMode.TWO_PLAYERS -> {
                            SideSelectionCard(
                                selectedSide = selectedSide,
                                onSelectSide = { selectedSide = it },
                                title = "1. CHỌN PHE CHO NGƯỜI CHƠI 1",
                                isLandscape = true,
                                selectedTheme = selectedTheme
                            )
                            TimerSelectionCard(
                                selectedTimerOption = selectedTimerOption,
                                onSelectTimerOption = { option ->
                                    if (option == GameTimerOption.CUSTOM) {
                                        showCustomTimerDialog = true
                                    } else {
                                        selectedTimerOption = option
                                    }
                                },
                                customMinutes = if (selectedTimerOption == GameTimerOption.CUSTOM) customMinutes else null,
                                title = "2. THỜI GIAN TRẬN ĐẤU",
                                isLandscape = true,
                                selectedTheme = selectedTheme
                            )
                            TwoPlayersInfoCard(isLandscape = true, selectedTheme = selectedTheme)
                        }
                        GameMode.TUTORIAL, GameMode.SPECIAL_MOVE -> {
                            TutorialPieceSelectionCard(
                                onSelectPiece = { pieceType ->
                                    onStartTutorialPiece?.invoke(pieceType)
                                },
                                isLandscape = true,
                                selectedTheme = selectedTheme
                            )
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            SpecialMovesSelectionCard(
                                onSelectSpecialMove = { moveType ->
                                    onStartSpecialMove?.invoke(moveType)
                                },
                                isLandscape = true,
                                selectedTheme = selectedTheme
                            )
                        }
                        GameMode.PUZZLE -> {
                            PuzzleSelectionCard(
                                gameMode = GameMode.PUZZLE,
                                completedPuzzles = completedPuzzles,
                                onSelectPuzzle = { fen, cat, lvl -> onStartPuzzle?.invoke(fen, cat, lvl) },
                                isLandscape = true,
                                lastPuzzleCategory = lastPuzzleCategory,
                                lastPuzzleLevel = lastPuzzleLevel,
                                selectedTheme = selectedTheme
                            )
                        }
                        GameMode.ONE_MOVE -> {
                            PuzzleSelectionCard(
                                gameMode = GameMode.ONE_MOVE,
                                completedPuzzles = completedPuzzles,
                                onSelectPuzzle = { fen, cat, lvl -> onStartPuzzle?.invoke(fen, cat, lvl) },
                                isLandscape = true,
                                lastPuzzleCategory = lastPuzzleCategory,
                                lastPuzzleLevel = lastPuzzleLevel,
                                selectedTheme = selectedTheme
                            )
                        }
                    }
                    

                }
            }
        } else {
            // === PORTRAIT LAYOUT ===
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        SetupHeader(selectedTheme = selectedTheme)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (selectedGameMode) {
                        GameMode.VS_AI -> {
                            DifficultySectionCard(
                                selectedDifficulty = selectedDifficulty,
                                onSelectDifficulty = { selectedDifficulty = it },
                                selectedTheme = selectedTheme
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            SideSelectionCard(
                                selectedSide = selectedSide,
                                onSelectSide = { selectedSide = it },
                                selectedTheme = selectedTheme
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            TimerSelectionCard(
                                selectedTimerOption = selectedTimerOption,
                                onSelectTimerOption = { option ->
                                    if (option == GameTimerOption.CUSTOM) {
                                        showCustomTimerDialog = true
                                    } else {
                                        selectedTimerOption = option
                                    }
                                },
                                customMinutes = if (selectedTimerOption == GameTimerOption.CUSTOM) customMinutes else null,
                                selectedTheme = selectedTheme
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                        }
                        GameMode.TWO_PLAYERS -> {
                            SideSelectionCard(
                                selectedSide = selectedSide,
                                onSelectSide = { selectedSide = it },
                                title = "1. CHỌN PHE CHO NGƯỜI CHƠI 1",
                                selectedTheme = selectedTheme
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            TimerSelectionCard(
                                selectedTimerOption = selectedTimerOption,
                                onSelectTimerOption = { option ->
                                    if (option == GameTimerOption.CUSTOM) {
                                        showCustomTimerDialog = true
                                    } else {
                                        selectedTimerOption = option
                                    }
                                },
                                customMinutes = if (selectedTimerOption == GameTimerOption.CUSTOM) customMinutes else null,
                                title = "2. THỜI GIAN TRẬN ĐẤU",
                                selectedTheme = selectedTheme
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            TwoPlayersInfoCard(selectedTheme = selectedTheme)

                            Spacer(modifier = Modifier.height(14.dp))
                        }
                        GameMode.TUTORIAL, GameMode.SPECIAL_MOVE -> {
                            TutorialPieceSelectionCard(
                                onSelectPiece = { pieceType ->
                                    onStartTutorialPiece?.invoke(pieceType)
                                },
                                selectedTheme = selectedTheme
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            SpecialMovesSelectionCard(
                                onSelectSpecialMove = { moveType ->
                                    onStartSpecialMove?.invoke(moveType)
                                },
                                selectedTheme = selectedTheme
                            )

                            BackHomeButton(onBack, selectedTheme = selectedTheme)

                            Spacer(modifier = Modifier.height(14.dp))
                        }
                        GameMode.PUZZLE -> {
                            PuzzleSelectionCard(
                                gameMode = GameMode.PUZZLE,
                                completedPuzzles = completedPuzzles,
                                onSelectPuzzle = { fen, cat, lvl -> onStartPuzzle?.invoke(fen, cat, lvl) },
                                lastPuzzleCategory = lastPuzzleCategory,
                                lastPuzzleLevel = lastPuzzleLevel,
                                selectedTheme = selectedTheme
                            )

                            BackHomeButton(onBack, selectedTheme = selectedTheme)

                            Spacer(modifier = Modifier.height(14.dp))
                        }
                        GameMode.ONE_MOVE -> {
                            PuzzleSelectionCard(
                                gameMode = GameMode.ONE_MOVE,
                                completedPuzzles = completedPuzzles,
                                onSelectPuzzle = { fen, cat, lvl -> onStartPuzzle?.invoke(fen, cat, lvl) },
                                lastPuzzleCategory = lastPuzzleCategory,
                                lastPuzzleLevel = lastPuzzleLevel,
                                selectedTheme = selectedTheme
                            )

                            BackHomeButton(onBack, selectedTheme = selectedTheme)

                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    if (selectedGameMode != GameMode.PUZZLE && selectedGameMode != GameMode.TUTORIAL && selectedGameMode != GameMode.ONE_MOVE && selectedGameMode != GameMode.SPECIAL_MOVE) {
                        MatchPreviewCard(
                            selectedSide,
                            selectedDifficulty,
                            selectedGameMode,
                            selectedTimerOption,
                            customMinutes,
                            selectedTheme = selectedTheme
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                if (selectedGameMode != GameMode.PUZZLE && selectedGameMode != GameMode.TUTORIAL && selectedGameMode != GameMode.ONE_MOVE && selectedGameMode != GameMode.SPECIAL_MOVE) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BackHomeButton(onBack, modifier = Modifier.weight(0.15f), showText = false, selectedTheme = selectedTheme)
                        StartGameButton(
                            gameMode = selectedGameMode,
                            onClick = {
                                onStartGame(
                                    selectedSide,
                                    selectedDifficulty,
                                    selectedGameMode,
                                    selectedTimerOption,
                                    customMinutes
                                )
                            },
                            modifier = Modifier.weight(0.85f),
                            selectedTheme = selectedTheme
                        )
                    }
                }
            }
        }
    }

    if (showCustomTimerDialog) {
        CustomTimerDialog(
            initialMinutes = customMinutes,
            onConfirm = {
                customMinutes = it
                selectedTimerOption = GameTimerOption.CUSTOM
                showCustomTimerDialog = false
            },
            onDismiss = {
                selectedTimerOption = GameTimerOption.NONE
                showCustomTimerDialog = false
            },
            selectedTheme = selectedTheme
        )
    }
}

@Composable
fun SetupHeader(isLandscape: Boolean = false, selectedTheme: ChessTheme = ChessTheme.CLASSIC) {
    val accentColor = Color(selectedTheme.accentColor)
    val iconActiveColor = Color(selectedTheme.iconActiveColor)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (isLandscape) 36.dp else 50.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(accentColor.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
                .border(if (isLandscape) 1.5.dp else 2.dp, accentColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = iconActiveColor,
                modifier = Modifier.size(if (isLandscape) 22.dp else 30.dp)
            )
        }
        Spacer(modifier = Modifier.height(if (isLandscape) 2.dp else 6.dp))

        Text(
            text = "⚔️ CỜ VUA TRUNG CỔ ⚔️",
            fontSize = if (isLandscape) 11.sp else 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(selectedTheme.textColor).copy(alpha = 0.8f),
            letterSpacing = if (isLandscape) 1.5.sp else 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = "THIẾT LẬP TRẬN ĐẤU",
            style = if (isLandscape) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = accentColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(if (isLandscape) 2.dp else 4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.4f else 0.5f)
                .height(if (isLandscape) 1.dp else 2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, accentColor, Color.Transparent)
                    )
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DifficultySectionCard(
    selectedDifficulty: DifficultyLevel,
    onSelectDifficulty: (DifficultyLevel) -> Unit,
    isLandscape: Boolean = false,
    selectedTheme: ChessTheme
) {
    val accentColor = Color(selectedTheme.accentColor)
    val borderColor = Color(selectedTheme.borderColor)
    val textColor = Color(selectedTheme.textColor)
    val secondaryTextColor = Color(selectedTheme.secondaryTextColor)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, borderColor, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(selectedTheme.surfaceColor).copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isLandscape) 8.dp else 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = Color(selectedTheme.iconActiveColor),
                    modifier = Modifier.size(if (isLandscape) 18.dp else 20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "1. CẤP ĐỘ ĐỐI THỦ (MÁY AI)",
                    style = if (isLandscape) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(if (isLandscape) 4.dp else 10.dp))

            val interactionSource = remember { MutableInteractionSource() }
            Slider(
                value = selectedDifficulty.level.toFloat(),
                onValueChange = { onSelectDifficulty(DifficultyLevel.fromInt(it.roundToInt())) },
                valueRange = 1f..7f,
                steps = 5,
                interactionSource = interactionSource,
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = secondaryTextColor.copy(alpha = 0.3f),
                    activeTickColor = accentColor,
                    inactiveTickColor = accentColor.copy(alpha = 0.4f)
                ),
                track = { sliderState ->
                    SliderDefaults.Track(
                        colors = SliderDefaults.colors(
                            activeTrackColor = accentColor,
                            inactiveTrackColor = secondaryTextColor.copy(alpha = 0.3f),
                            activeTickColor = accentColor,
                            inactiveTickColor = accentColor.copy(alpha = 0.4f)
                        ),
                        sliderState = sliderState,
                        thumbTrackGapSize = 0.dp,
                        modifier = Modifier.height(4.dp)
                    )
                },
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(if (isLandscape) 12.dp else 14.dp)
                            .rotate(45f)
                            .background(accentColor)
                            .border(1.dp, Color.White.copy(alpha = 0.8f))
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DifficultyLevel.entries.forEach { level ->
                    Text(
                        text = level.displayNameVi,
                        fontSize = if (isLandscape) 8.5.sp else 9.sp,
                        fontWeight = if (selectedDifficulty == level) FontWeight.ExtraBold else FontWeight.Normal,
                        color = if (selectedDifficulty == level) textColor else secondaryTextColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(if (isLandscape) 36.dp else 40.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SideSelectionCard(
    selectedSide: SideOption,
    onSelectSide: (SideOption) -> Unit,
    title: String = "2. CHỌN PHE QUÂN CỦA BẠN",
    isLandscape: Boolean = false,
    selectedTheme: ChessTheme
) {
    val accentColor = Color(selectedTheme.accentColor)
    val borderColor = Color(selectedTheme.borderColor)
    val textColor = Color(selectedTheme.textColor)
    val secondaryTextColor = Color(selectedTheme.secondaryTextColor)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (isLandscape) 4.dp else 0.dp)
            .border(1.2.dp, borderColor, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(selectedTheme.surfaceColor).copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isLandscape) 8.dp else 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(selectedTheme.iconActiveColor),
                    modifier = Modifier.size(if (isLandscape) 18.dp else 20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = if (isLandscape) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(if (isLandscape) 6.dp else 10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SideOption.entries.forEach { option ->
                    val isSelected = selectedSide == option
                    
                    val activeBgColor = when (option) {
                        SideOption.WHITE -> Color(selectedTheme.lightSquareColor).copy(alpha = if (isSelected) 0.9f else 0.4f)
                        SideOption.BLACK -> Color(selectedTheme.darkSquareColor).copy(alpha = if (isSelected) 0.9f else 0.4f)
                        SideOption.RANDOM -> Color(selectedTheme.surfaceColor).copy(alpha = if (isSelected) 0.95f else 0.5f)
                    }
                    
                    val contentColor = when (option) {
                        SideOption.WHITE -> if (isSelected) Color.Black else textColor
                        SideOption.BLACK -> if (isSelected) Color.White else textColor
                        SideOption.RANDOM -> if (isSelected) textColor else secondaryTextColor
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectSide(option) }
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) accentColor else borderColor.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .testTag("setup_side_${option.name.lowercase()}"),
                        color = activeBgColor
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = if (isLandscape) 6.dp else 10.dp, horizontal = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = option.iconSymbol,
                                fontSize = if (isLandscape) 16.sp else 18.sp,
                                color = contentColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = when(option) {
                                    SideOption.WHITE -> "Bạch Vương"
                                    SideOption.BLACK -> "Hắc Vương"
                                    SideOption.RANDOM -> "Ngẫu nhiên"
                                },
                                fontSize = if (isLandscape) 10.sp else 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = contentColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TwoPlayersInfoCard(isLandscape: Boolean = false, selectedTheme: ChessTheme = ChessTheme.CLASSIC) {
    val accentColor = Color(selectedTheme.accentColor)
    val borderColor = Color(selectedTheme.borderColor)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (isLandscape) 4.dp else 0.dp)
            .border(1.2.dp, borderColor, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(selectedTheme.surfaceColor).copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isLandscape) 10.dp else 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = null,
                    tint = Color(selectedTheme.iconActiveColor),
                    modifier = Modifier.size(if (isLandscape) 18.dp else 20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CHẾ ĐỘ 2 NGƯỜI CHƠI TRÊN 1 MÁY",
                    style = if (isLandscape) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(if (isLandscape) 6.dp else 10.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                color = Color(selectedTheme.surfaceColor).copy(alpha = 0.4f),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, borderColor.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(if (isLandscape) 8.dp else 12.dp)) {
                    Text(
                        text = "👥 Cùng đấu cờ vua trực tiếp trên một thiết bị:",
                        fontSize = if (isLandscape) 11.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(selectedTheme.textColor)
                    )
                    Spacer(modifier = Modifier.height(if (isLandscape) 4.dp else 6.dp))
                    Text(
                        text = "• Người chơi 1: Cầm quân Trắng (♔) - Đi trước\n• Người chơi 2: Cầm quân Đen (♚) - Đi sau\n• Hoàn toàn không có sự can thiệp của máy AI.",
                        fontSize = if (isLandscape) 10.5.sp else 11.5.sp,
                        color = Color(selectedTheme.secondaryTextColor),
                        lineHeight = if (isLandscape) 15.sp else 17.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TutorialPieceSelectionCard(
    onSelectPiece: (PieceType) -> Unit,
    isLandscape: Boolean = false,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColor = Color(selectedTheme.accentColor)
    val borderColor = Color(selectedTheme.borderColor)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, borderColor, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(selectedTheme.surfaceColor).copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isLandscape) 10.dp else 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(selectedTheme.iconActiveColor),
                    modifier = Modifier.size(if (isLandscape) 18.dp else 20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CHỌN QUÂN CỜ ĐỂ HỌC NƯỚC ĐI",
                    style = if (isLandscape) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Text(
                text = "Chọn quân cờ để chuyển sang bàn cờ giả định (gợi ý đi liên tục):",
                fontSize = if (isLandscape) 10.5.sp else 11.5.sp,
                color = Color(selectedTheme.secondaryTextColor),
                modifier = Modifier.padding(top = 2.dp, bottom = if (isLandscape) 6.dp else 10.dp)
            )

            val pieces = listOf(
                Triple(PieceType.ROOK, "Xe (♜)", "Đi thẳng, ngang không giới hạn ô"),
                Triple(PieceType.BISHOP, "Tượng (♝)", "Đi chéo không giới hạn ô"),
                Triple(PieceType.QUEEN, "Hậu (♛)", "Đi thẳng, ngang, chéo tự do"),
                Triple(PieceType.KNIGHT, "Mã (♞)", "Đi hình chữ L (2 ô thẳng + 1 ô ngang/dọc)"),
                Triple(PieceType.KING, "Vua (♚)", "Đi 1 ô theo mọi hướng"),
                Triple(PieceType.PAWN, "Tốt (♟)", "Đi thẳng 1 ô (ô đầu đi 2 ô), ăn chéo")
            )

            Column(verticalArrangement = Arrangement.spacedBy(if (isLandscape) 4.dp else 8.dp)) {
                pieces.forEach { (pieceType, title, note) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectPiece(pieceType) }
                            .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .testTag("tutorial_piece_${pieceType.name.lowercase()}"),
                        color = Color(selectedTheme.surfaceColor).copy(alpha = 0.4f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = if (isLandscape) 8.dp else 12.dp, vertical = if (isLandscape) 6.dp else 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(if (isLandscape) 28.dp else 34.dp)
                                    .clip(CircleShape)
                                    .background(Color(selectedTheme.iconActiveColor).copy(alpha = 0.2f))
                                    .border(1.dp, Color(selectedTheme.iconActiveColor), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (pieceType) {
                                        PieceType.ROOK -> "♜"
                                        PieceType.KNIGHT -> "♞"
                                        PieceType.BISHOP -> "♝"
                                        PieceType.QUEEN -> "♛"
                                        PieceType.KING -> "♚"
                                        PieceType.PAWN -> "♟"
                                    },
                                    fontSize = if (isLandscape) 15.sp else 18.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = title,
                                    fontSize = if (isLandscape) 12.5.sp else 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(selectedTheme.textColor)
                                )
                                Text(
                                    text = note,
                                    fontSize = if (isLandscape) 10.sp else 11.sp,
                                    color = Color(selectedTheme.secondaryTextColor),
                                    lineHeight = if (isLandscape) 12.sp else 14.sp
                                )
                            }

                            Text(
                                text = "➔",
                                fontSize = if (isLandscape) 10.sp else 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecialMovesSelectionCard(
    onSelectSpecialMove: (SpecialTutorialType) -> Unit,
    isLandscape: Boolean = false,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColor = Color(selectedTheme.accentColor)
    val borderColor = Color(selectedTheme.borderColor)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, borderColor, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(selectedTheme.surfaceColor).copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isLandscape) 10.dp else 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(selectedTheme.iconActiveColor),
                    modifier = Modifier.size(if (isLandscape) 18.dp else 20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "NƯỚC ĐI ĐẶC BIỆT",
                    style = if (isLandscape) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Text(
                text = "Các quy tắc di chuyển nâng cao trong cờ vua:",
                fontSize = if (isLandscape) 10.5.sp else 11.5.sp,
                color = Color(selectedTheme.secondaryTextColor),
                modifier = Modifier.padding(top = 2.dp, bottom = if (isLandscape) 6.dp else 10.dp)
            )

            val specialMoves = listOf(
                SpecialTutorialType.CASTLING_KINGSIDE,
                SpecialTutorialType.CASTLING_QUEENSIDE,
                SpecialTutorialType.PAWN_PROMOTION,
                SpecialTutorialType.EN_PASSANT
            )

            Column(verticalArrangement = Arrangement.spacedBy(if (isLandscape) 4.dp else 8.dp)) {
                specialMoves.forEach { moveType ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectSpecialMove(moveType) }
                            .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        color = Color(selectedTheme.surfaceColor).copy(alpha = 0.4f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = if (isLandscape) 8.dp else 12.dp, vertical = if (isLandscape) 6.dp else 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(if (isLandscape) 28.dp else 34.dp)
                                    .clip(CircleShape)
                                    .background(Color(selectedTheme.iconActiveColor).copy(alpha = 0.2f))
                                    .border(1.dp, Color(selectedTheme.iconActiveColor), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(if (isLandscape) 16.dp else 20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = moveType.displayNameVi,
                                    fontSize = if (isLandscape) 12.5.sp else 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(selectedTheme.textColor)
                                )
                                Text(
                                    text = moveType.description,
                                    fontSize = if (isLandscape) 10.sp else 11.sp,
                                    color = Color(selectedTheme.secondaryTextColor),
                                    lineHeight = if (isLandscape) 12.sp else 14.sp
                                )
                            }

                            Text(
                                text = "➔",
                                fontSize = if (isLandscape) 10.sp else 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchPreviewCard(
    selectedSide: SideOption,
    selectedDifficulty: DifficultyLevel,
    selectedGameMode: GameMode,
    selectedTimerOption: GameTimerOption,
    customMinutes: Int?,
    isLandscape: Boolean = false,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColor = Color(selectedTheme.accentColor)
    val borderColor = Color(selectedTheme.borderColor)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        color = Color(selectedTheme.surfaceColor).copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isLandscape) 6.dp else 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when (selectedGameMode) {
                        GameMode.TWO_PLAYERS -> "NGƯỜI CHƠI 1"
                        GameMode.TUTORIAL -> "CHẾ ĐỘ"
                        GameMode.PUZZLE -> "THỬ THÁCH"
                        else -> "BẠN CẦM QUÂN"
                    },
                    fontSize = if (isLandscape) 8.5.sp else 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = when (selectedGameMode) {
                        GameMode.TWO_PLAYERS -> {
                            when (selectedSide) {
                                SideOption.WHITE -> "Quân Trắng (♔)"
                                SideOption.BLACK -> "Quân Đen (♚)"
                                SideOption.RANDOM -> "Ngẫu nhiên"
                            }
                        }
                        GameMode.TUTORIAL -> "Hướng Dẫn Quân Cờ"
                        GameMode.PUZZLE -> "Chiếu Bí"
                        else -> selectedSide.displayNameVi
                    },
                    fontSize = if (isLandscape) 11.sp else 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(selectedTheme.textColor),
                    textAlign = TextAlign.Center
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = if (selectedGameMode == GameMode.TUTORIAL || selectedGameMode == GameMode.PUZZLE) "🧩" else "⚔️ VS ⚔️",
                    fontSize = if (isLandscape) 10.sp else 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
                
                if (selectedGameMode != GameMode.TUTORIAL && selectedTimerOption != GameTimerOption.NONE) {
                    val timerLabel = if (selectedTimerOption == GameTimerOption.CUSTOM) {
                        "${customMinutes ?: 10}p"
                    } else {
                        selectedTimerOption.displayNameVi
                    }
                    Text(
                        text = "($timerLabel)",
                        fontSize = if (isLandscape) 8.sp else 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(selectedTheme.textColor).copy(alpha = 0.8f)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when (selectedGameMode) {
                        GameMode.TWO_PLAYERS -> "NGƯỜI CHƠI 2"
                        GameMode.TUTORIAL -> "MỤC TIÊU"
                        GameMode.PUZZLE -> "CẤP ĐỘ"
                        else -> "ĐỐI THỦ MÁY"
                    },
                    fontSize = if (isLandscape) 8.5.sp else 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = when (selectedGameMode) {
                        GameMode.TWO_PLAYERS -> {
                            when (selectedSide) {
                                SideOption.WHITE -> "Quân Đen (♚)"
                                SideOption.BLACK -> "Quân Trắng (♔)"
                                SideOption.RANDOM -> "Ngẫu nhiên"
                            }
                        }
                        GameMode.TUTORIAL -> "Tập Luyện Lực Lượng"
                        GameMode.PUZZLE -> "Cơ Bản"
                        else -> selectedDifficulty.displayNameVi
                    },
                    fontSize = if (isLandscape) 11.sp else 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (selectedGameMode == GameMode.TWO_PLAYERS || selectedGameMode == GameMode.TUTORIAL || selectedGameMode == GameMode.PUZZLE) Color(selectedTheme.textColor) else when (selectedDifficulty) {
                        DifficultyLevel.LEVEL_1 -> ColorEmeraldLight
                        DifficultyLevel.LEVEL_2 -> accentColor
                        else -> ColorCrimsonSoft
                    },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun BackHomeButton(
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false,
    showText: Boolean = true,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColor = Color(selectedTheme.accentColor)
    if (onBack != null) {
        if (showText) {
            Spacer(modifier = Modifier.height(if (isLandscape) 6.dp else 10.dp))
        }
        OutlinedButton(
            onClick = onBack,
            modifier = modifier
                .then(if (showText) Modifier.fillMaxWidth() else Modifier)
                .height(if (isLandscape) 40.dp else 48.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = accentColor
            ),
            border = androidx.compose.foundation.BorderStroke(if (isLandscape) 1.5.dp else 2.dp, accentColor),
            shape = RoundedCornerShape(14.dp),
            contentPadding = if (showText) (if (isLandscape) PaddingValues(0.dp) else ButtonDefaults.ContentPadding) else PaddingValues(0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(if (isLandscape) 18.dp else 20.dp)
                )
                if (showText) {
                    Spacer(modifier = Modifier.width(if (isLandscape) 6.dp else 8.dp))
                    Text(
                        text = "VỀ TRANG CHỦ",
                        fontSize = if (isLandscape) 14.sp else 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(selectedTheme.textColor),
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StartGameButton(
    gameMode: GameMode = GameMode.VS_AI,
    onClick: () -> Unit,
    isLandscape: Boolean = false,
    modifier: Modifier = Modifier,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColor = Color(selectedTheme.accentColor)
    val buttonColor = Color(selectedTheme.buttonColor)
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(if (isLandscape) 44.dp else 50.dp)
            .shadow(if (isLandscape) 8.dp else 12.dp, RoundedCornerShape(14.dp))
            .testTag("start_game_button"),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(if (isLandscape) 1.5.dp else 2.dp, accentColor),
        contentPadding = if (isLandscape) PaddingValues(0.dp) else ButtonDefaults.ContentPadding
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val text = when (gameMode) {
                GameMode.TUTORIAL -> "📖 BẮT ĐẦU HƯỚNG DẪN 📖"
                GameMode.PUZZLE -> "🧩 BẮT ĐẦU GIẢI ĐỐ 🧩"
                else -> "⚔️ BẮT ĐẦU CHƠI ⚔️"
            }
            Text(
                text = text,
                fontSize = if (isLandscape) 14.sp else 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(selectedTheme.textColor),
                letterSpacing = 1.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MatchPreviewCardTwoPlayersPreview() {
    MyApplicationTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("P1: Trắng -> P2: Đen")
            MatchPreviewCard(SideOption.WHITE, DifficultyLevel.LEVEL_2, GameMode.TWO_PLAYERS, GameTimerOption.M10, null)
            
            Text("P1: Đen -> P2: Trắng")
            MatchPreviewCard(SideOption.BLACK, DifficultyLevel.LEVEL_2, GameMode.TWO_PLAYERS, GameTimerOption.CUSTOM, 15)
            
            Text("Ngẫu nhiên - Không giới hạn")
            MatchPreviewCard(SideOption.RANDOM, DifficultyLevel.LEVEL_2, GameMode.TWO_PLAYERS, GameTimerOption.NONE, null)
        }
    }
}

@Composable
private fun TimerSelectionCard(
    selectedTimerOption: GameTimerOption,
    onSelectTimerOption: (GameTimerOption) -> Unit,
    customMinutes: Int? = null,
    title: String = "3. THỜI GIAN TRẬN ĐẤU",
    isLandscape: Boolean = false,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColor = Color(selectedTheme.accentColor)
    val borderColor = Color(selectedTheme.borderColor)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (isLandscape) 4.dp else 0.dp)
            .border(1.2.dp, borderColor, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(selectedTheme.surfaceColor).copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isLandscape) 8.dp else 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = null,
                    tint = Color(selectedTheme.iconActiveColor),
                    modifier = Modifier.size(if (isLandscape) 18.dp else 20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = if (isLandscape) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(if (isLandscape) 6.dp else 10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                GameTimerOption.values().forEach { option ->
                    val isSelected = selectedTimerOption == option
                    val label = option.displayNameVi

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectTimerOption(option) }
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) accentColor else borderColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        color = if (isSelected) accentColor.copy(alpha = 0.2f) else Color(selectedTheme.surfaceColor).copy(alpha = 0.4f)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = if (isLandscape) 6.dp else 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = if (isLandscape) 9.5.sp else 10.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected) Color(selectedTheme.textColor) else Color(selectedTheme.secondaryTextColor)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomTimerDialog(
    initialMinutes: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    var textValue by remember { mutableStateOf(initialMinutes.toString()) }
    val mins = textValue.toIntOrNull()
    val isValid = mins != null && mins in 1..180

    val accentColor = Color(selectedTheme.accentColor)
    val surfaceColor = Color(selectedTheme.surfaceColor)

    // Use a state to trigger the animation
    var isVisible by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        isVisible = true
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = {
            isVisible = false
        },
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            decorFitsSystemWindows = false
        )
    ) {
        HideSystemBarsInDialog()
        
        // Launch dismissal when animation finishes
        androidx.compose.runtime.LaunchedEffect(isVisible) {
            if (!isVisible) {
                onDismiss()
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .width(280.dp)
                        .wrapContentHeight()
                        .padding(16.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = surfaceColor.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TÙY CHỈNH THỜI GIAN",
                                color = accentColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                            color = Color(selectedTheme.surfaceColor).copy(alpha = 0.3f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Nhập số phút (1 - 180):",
                                    color = Color(selectedTheme.textColor).copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                OutlinedTextField(
                                    value = textValue,
                                    onValueChange = {
                                        if (it.isEmpty() || (it.length <= 3 && it.all { char -> char.isDigit() })) {
                                            textValue = it
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(0.9f),
                                    singleLine = true,
                                    isError = !isValid && textValue.isNotEmpty(),
                                    supportingText = {
                                        if (!isValid && textValue.isNotEmpty()) {
                                            Text(
                                                "1 - 180 phút",
                                                color = Color.Red,
                                                fontSize = 10.sp,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color(selectedTheme.textColor),
                                        unfocusedTextColor = Color(selectedTheme.textColor),
                                        focusedBorderColor = accentColor,
                                        unfocusedBorderColor = accentColor.copy(alpha = 0.5f),
                                        errorBorderColor = Color.Red,
                                        errorSupportingTextColor = Color.Red,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent
                                    ),
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Button Hủy
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { isVisible = false }
                                    .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                color = Color(selectedTheme.surfaceColor).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "HỦY",
                                    color = Color(selectedTheme.textColor).copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                            
                            // Button Xác nhận
                            Surface(
                                modifier = Modifier
                                    .weight(1.2f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(enabled = isValid) {
                                        if (isValid) onConfirm(mins!!)
                                    }
                                    .border(
                                        width = if (isValid) 2.dp else 1.dp,
                                        color = if (isValid) accentColor else accentColor.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                color = if (isValid) accentColor.copy(alpha = 0.25f) else Color(selectedTheme.surfaceColor).copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "XÁC NHẬN",
                                    color = if (isValid) Color(selectedTheme.textColor) else Color(selectedTheme.textColor).copy(alpha = 0.4f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PuzzleSelectionCard(
    gameMode: GameMode,
    completedPuzzles: Set<String>,
    onSelectPuzzle: (String, String, Int) -> Unit,
    isLandscape: Boolean = false,
    lastPuzzleCategory: String? = null,
    lastPuzzleLevel: Int? = null,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColor = Color(selectedTheme.accentColor)
    val borderColor = Color(selectedTheme.borderColor)
    val surfaceColor = Color(selectedTheme.surfaceColor)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, borderColor, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isLandscape) 12.dp else 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (gameMode == GameMode.ONE_MOVE) Icons.Default.FlashOn else Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(selectedTheme.iconActiveColor),
                    modifier = Modifier.size(if (isLandscape) 20.dp else 24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (gameMode == GameMode.ONE_MOVE) "THỬ THÁCH 1 NƯỚC" else "THỬ THÁCH GIẢI ĐỐ",
                    style = if (isLandscape) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(if (isLandscape) 10.dp else 16.dp))

            val categories = if (gameMode == GameMode.ONE_MOVE) {
                listOf(
                    "Nhập môn" to OneMoveBeginner.list,
                    "Dễ" to OneMoveEasy.list,
                    "Trung bình" to OneMoveMedium.list,
                    "Khó" to OneMoveHard.list,
                    "Cao thủ" to OneMoveExpert.list
                )
            } else {
                listOf(
                    "Nhập môn" to PuzzlesBeginner.list,
                    "Dễ" to PuzzlesEasy.list,
                    "Trung bình" to PuzzlesMedium.list,
                    "Khó" to PuzzlesHard.list,
                    "Cao thủ" to PuzzlesExpert.list
                )
            }

            var expandedCategory by remember { mutableStateOf<String?>(lastPuzzleCategory) }
            // Theo dõi xem đã thực hiện cuộn lần đầu khi quay lại chưa
            var hasAutoScrolled by remember(lastPuzzleCategory, lastPuzzleLevel) { mutableStateOf(false) }

            Column(verticalArrangement = Arrangement.spacedBy(if (isLandscape) 6.dp else 8.dp)) {
                categories.forEach { (name, puzzles) ->
                    PuzzleCategoryItem(
                        name = name,
                        puzzles = puzzles,
                        completedPuzzles = completedPuzzles,
                        isExpanded = expandedCategory == name,
                        onToggle = { 
                            expandedCategory = if (expandedCategory == name) null else name 
                            // Nếu người dùng nhấn vào danh mục khác hoặc đóng lại, coi như đã xử lý xong trạng thái tự động
                            if (expandedCategory != lastPuzzleCategory) hasAutoScrolled = true
                        },
                        onSelectPuzzle = onSelectPuzzle,
                        isLandscape = isLandscape,
                        lastPuzzleLevel = if (expandedCategory == name && name == lastPuzzleCategory && !hasAutoScrolled) lastPuzzleLevel else null,
                        onInitialScrollDone = { if (name == lastPuzzleCategory) hasAutoScrolled = true },
                        selectedTheme = selectedTheme
                    )
                }
            }
        }
    }
}

@Composable
private fun PuzzleCategoryItem(
    name: String,
    puzzles: List<Puzzles>,
    completedPuzzles: Set<String>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onSelectPuzzle: (String, String, Int) -> Unit,
    isLandscape: Boolean = false,
    lastPuzzleLevel: Int? = null,
    onInitialScrollDone: (() -> Unit)? = null,
    selectedTheme: ChessTheme = ChessTheme.CLASSIC
) {
    val accentColor = Color(selectedTheme.accentColor)
    val borderColor = Color(selectedTheme.borderColor)
    // Khởi tạo index để tránh bị nhảy (chớp) từ level 1
    val initialIndex = remember(lastPuzzleLevel) {
        if (lastPuzzleLevel != null) {
            puzzles.indexOfFirst { it.level == lastPuzzleLevel }.coerceAtLeast(0)
        } else 0
    }
    
    val gridState = androidx.compose.foundation.lazy.grid.rememberLazyGridState(
        initialFirstVisibleItemIndex = initialIndex
    )

    // Sử dụng animateScrollToItem để có hiệu ứng mượt mà nếu cần cuộn lại
    if (isExpanded && lastPuzzleLevel != null) {
        androidx.compose.runtime.LaunchedEffect(lastPuzzleLevel) {
            val index = puzzles.indexOfFirst { it.level == lastPuzzleLevel }
            if (index >= 0 && gridState.firstVisibleItemIndex != index) {
                gridState.animateScrollToItem(index)
            }
            onInitialScrollDone?.invoke()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(selectedTheme.surfaceColor).copy(alpha = 0.4f))
            .border(1.dp, borderColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(if (isLandscape) 8.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                color = if (isExpanded) Color(selectedTheme.textColor) else Color(selectedTheme.textColor).copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                fontSize = if (isLandscape) 14.sp else 15.sp
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(if (isLandscape) 20.dp else 24.dp)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(if (isLandscape) 8 else 5),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = if (isLandscape) 200.dp else 300.dp)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(puzzles) { puzzle ->
                    val isCompleted = completedPuzzles.contains("${name}_${puzzle.level}")
                    Surface(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onSelectPuzzle(puzzle.fen, name, puzzle.level) }
                            .border(
                                width = if (isCompleted) 2.dp else 1.dp,
                                color = if (isCompleted) ColorEmeraldLight else accentColor.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(4.dp)
                            ),
                        color = Color(selectedTheme.surfaceColor).copy(alpha = 0.6f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = puzzle.level.toString(),
                                color = if (isCompleted) ColorEmeraldLight else Color(selectedTheme.textColor),
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isLandscape) 13.sp else 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PuzzleSelectionPlaceholderCard(selectedTheme: ChessTheme = ChessTheme.CLASSIC) {
    val accentColor = Color(selectedTheme.accentColor)
    val surfaceColor = Color(selectedTheme.surfaceColor)
    val secondaryTextColor = Color(selectedTheme.secondaryTextColor)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, accentColor, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "CHẾ ĐỘ GIẢI ĐỐ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tính năng đang được phát triển. Hãy quay lại sau để thử thách khả năng chiếu bí của bạn!",
                fontSize = 14.sp,
                color = secondaryTextColor,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Preview(showBackground = true, name = "Setup Portrait")
@Composable
fun GameSetupScreenPreview() {
    MyApplicationTheme {
        GameSetupScreen(
            initialSideOption = SideOption.WHITE,
            initialDifficulty = DifficultyLevel.LEVEL_2,
            initialGameMode = GameMode.VS_AI,
            initialTimerOption = GameTimerOption.NONE,
            initialCustomMinutes = 10,
            onStartGame = { _, _, _, _, _ -> },
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Setup Landscape", widthDp = 800, heightDp = 400)
@Composable
fun GameSetupScreenLandscapePreview() {
    MyApplicationTheme {
        GameSetupScreen(
            initialSideOption = SideOption.WHITE,
            initialDifficulty = DifficultyLevel.LEVEL_2,
            initialGameMode = GameMode.VS_AI,
            initialTimerOption = GameTimerOption.NONE,
            initialCustomMinutes = 10,
            onStartGame = { _, _, _, _, _ -> },
            onBack = {}
        )
    }
}
