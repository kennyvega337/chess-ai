package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.chess.model.DifficultyLevel
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.chess.model.GameTimerOption
import com.example.chess.model.PieceType
import com.example.chess.model.SideOption
import com.example.ui.theme.MyApplicationTheme
import com.example.chess.ui.GameSetupScreen

class GameSetupActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemNavigationBars()

        val themeManager = com.example.chess.data.ChessThemeManager(this)

        val modeStr = intent.getStringExtra(MainActivity.EXTRA_GAME_MODE) ?: themeManager.getSelectedGameMode().name
        val selectedGameMode = try { GameMode.valueOf(modeStr) } catch (e: Exception) { GameMode.VS_AI }
        
        val initialSide = themeManager.getSelectedSideOption()
        val initialDifficulty = themeManager.getSelectedDifficulty()
        val initialTimer = themeManager.getSelectedTimerOption()
        val initialCustomMinutes = themeManager.getSelectedCustomMinutes()

        setContent {
            MyApplicationTheme {
                GameSetupScreen(
                    initialSideOption = initialSide,
                    initialDifficulty = initialDifficulty,
                    initialGameMode = selectedGameMode,
                    initialTimerOption = initialTimer,
                    initialCustomMinutes = initialCustomMinutes,
                    completedPuzzles = themeManager.getCompletedPuzzles(),
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
                    onStartTutorialPiece = { pieceType ->
                        launchGame(SideOption.WHITE, DifficultyLevel.LEVEL_1, GameMode.TUTORIAL, GameTimerOption.NONE, null, pieceType)
                    },
                    onStartPuzzle = { fen, category, level ->
                        launchPuzzle(fen, category, level)
                    },
                    onBack = {
                        finish()
                    }
                )
            }
        }
    }

    private fun launchPuzzle(fen: String, category: String, level: Int) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("PUZZLE_FEN", fen)
            putExtra("PUZZLE_CATEGORY", category)
            putExtra("PUZZLE_LEVEL", level)
            putExtra(MainActivity.EXTRA_GAME_MODE, GameMode.PUZZLE.name)
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
        hideSystemNavigationBars()
    }

    private fun hideSystemNavigationBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
            show(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
