package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.runtime.*
import com.example.chess.model.ChessTheme
import com.example.chess.model.DifficultyLevel
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.chess.model.GameTimerOption
import com.example.chess.model.PieceType
import com.example.chess.model.SideOption
import com.example.chess.model.SpecialTutorialType
import com.example.chess.ui.GameSetupScreen
import com.example.chess.ui.isThemeLight
import com.example.ui.theme.MyApplicationTheme

class GameSetupActivity : ComponentActivity() {

    private lateinit var themeManager: com.example.chess.data.ChessThemeManager
    private val currentThemeState = mutableStateOf(ChessTheme.CLASSIC)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        themeManager = com.example.chess.data.ChessThemeManager(this)
        currentThemeState.value = themeManager.getSelectedTheme()
        hideSystemNavigationBars()

        val modeStr = intent.getStringExtra(MainActivity.EXTRA_GAME_MODE) ?: themeManager.getSelectedGameMode().name
        val selectedGameMode = try { GameMode.valueOf(modeStr) } catch (e: Exception) { GameMode.VS_AI }
        
        val initialSide = themeManager.getSelectedSideOption()
        val initialDifficulty = themeManager.getSelectedDifficulty()
        val initialTimer = themeManager.getSelectedTimerOption()
        val initialCustomMinutes = themeManager.getSelectedCustomMinutes()
        val initialScoringSide = themeManager.getScoringSideOption()
        val initialScoringPiece = themeManager.getScoringPieceType()
        val initialScoringSeconds = themeManager.getScoringSeconds()

        setContent {
            val currentTheme by currentThemeState

            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = isThemeLight(currentTheme)
                    isAppearanceLightNavigationBars = isThemeLight(currentTheme)
                }
            }

            MyApplicationTheme(selectedTheme = currentTheme) {
                GameSetupScreen(
                    initialSideOption = if (selectedGameMode == GameMode.SCORING) initialScoringSide else initialSide,
                    initialDifficulty = initialDifficulty,
                    initialGameMode = selectedGameMode,
                    initialTimerOption = initialTimer,
                    initialCustomMinutes = initialCustomMinutes,
                    initialScoringPiece = initialScoringPiece,
                    initialScoringSeconds = initialScoringSeconds,
                    selectedTheme = currentTheme,
                    completedPuzzles = themeManager.getCompletedPuzzles(selectedGameMode),
                    onStartGame = { sideOption, difficulty, gameMode, timerOption, customMinutes ->
                        themeManager.saveSideOption(sideOption)
                        themeManager.saveDifficulty(difficulty)
                        themeManager.saveGameMode(gameMode)
                        themeManager.saveTimerOption(timerOption)
                        if (customMinutes != null) {
                            themeManager.saveCustomMinutes(customMinutes)
                        }
                        launchGame(sideOption, difficulty, gameMode, timerOption, customMinutes, null)
                    },
                    onStartScoring = { side, piece, seconds ->
                        themeManager.saveScoringSideOption(side)
                        themeManager.saveScoringPieceType(piece)
                        themeManager.saveScoringSeconds(seconds)
                        themeManager.saveGameMode(GameMode.SCORING)
                        launchScoring(side, piece, seconds)
                    },
                    onStartTutorialPiece = { pieceType ->
                        launchGame(SideOption.WHITE, DifficultyLevel.LEVEL_1, GameMode.TUTORIAL, GameTimerOption.NONE, null, pieceType)
                    },
                    onStartSpecialMove = { specialType ->
                        launchSpecialMove(specialType)
                    },
                    onStartPuzzle = { fen, category, level ->
                        launchPuzzle(fen, category, level, selectedGameMode)
                    },
                    onBack = {
                        finish()
                    }
                )
            }
        }
    }

    private fun launchPuzzle(fen: String, category: String, level: Int, mode: GameMode) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("PUZZLE_FEN", fen)
            putExtra("PUZZLE_CATEGORY", category)
            putExtra("PUZZLE_LEVEL", level)
            putExtra(MainActivity.EXTRA_GAME_MODE, mode.name)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

    private fun launchScoring(side: SideOption, piece: PieceType, seconds: Int) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_GAME_MODE, GameMode.SCORING.name)
            putExtra(MainActivity.EXTRA_SIDE_OPTION, side.name)
            putExtra("EXTRA_SCORING_PIECE", piece.name)
            putExtra(MainActivity.EXTRA_SCORING_SECONDS, seconds)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

    private fun launchSpecialMove(type: SpecialTutorialType) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_GAME_MODE, GameMode.SPECIAL_MOVE.name)
            putExtra("EXTRA_SPECIAL_MOVE_TYPE", type.name)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

    private fun launchGame(
        sideOption: SideOption,
        difficulty: DifficultyLevel,
        gameMode: GameMode,
        timerOption: GameTimerOption,
        customMinutes: Int?,
        tutorialPiece: PieceType?
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_SIDE_OPTION, sideOption.name)
            putExtra(MainActivity.EXTRA_DIFFICULTY, difficulty.name)
            putExtra(MainActivity.EXTRA_GAME_MODE, gameMode.name)
            putExtra(MainActivity.EXTRA_TIMER_OPTION, timerOption.name)
            if (customMinutes != null) {
                putExtra(MainActivity.EXTRA_CUSTOM_MINUTES, customMinutes)
            }
            if (tutorialPiece != null) {
                putExtra(MainActivity.EXTRA_TUTORIAL_PIECE, tutorialPiece.name)
            }
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        currentThemeState.value = themeManager.getSelectedTheme()
        hideSystemNavigationBars()
    }

    private fun hideSystemNavigationBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView).apply {
            val currentTheme = themeManager.getSelectedTheme()
            isAppearanceLightStatusBars = isThemeLight(currentTheme)
            isAppearanceLightNavigationBars = isThemeLight(currentTheme)
            show(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
