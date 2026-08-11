package com.example.chess.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.MainActivity
import com.example.chess.model.DifficultyLevel
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.chess.model.GameTimerOption
import com.example.chess.model.PieceType
import com.example.chess.model.SideOption
import com.example.ui.theme.MyApplicationTheme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
class GameSetupActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemNavigationBars()

        val themeManager = com.example.chess.data.ChessThemeManager(this)

        val initialGameModeStr = intent.getStringExtra(MainActivity.EXTRA_GAME_MODE) ?: themeManager.getSelectedGameMode().name
        val initialSideStr = intent.getStringExtra(MainActivity.EXTRA_SIDE_OPTION) ?: themeManager.getSelectedSideOption().name
        val initialDifficultyStr = intent.getStringExtra(MainActivity.EXTRA_DIFFICULTY) ?: themeManager.getSelectedDifficulty().name
        val isGameInProgress = intent.getBooleanExtra(MainActivity.EXTRA_IS_GAME_IN_PROGRESS, false)

        val initialGameMode = try { GameMode.valueOf(initialGameModeStr) } catch (e: Exception) { GameMode.VS_AI }
        val initialSide = try { SideOption.valueOf(initialSideStr) } catch (e: Exception) { SideOption.WHITE }
        val initialDifficulty = try { DifficultyLevel.valueOf(initialDifficultyStr) } catch (e: Exception) { DifficultyLevel.LEVEL_2 }
        val initialTimer = themeManager.getSelectedTimerOption()
        val initialCustomMinutes = themeManager.getSelectedCustomMinutes()

        setContent {
            MyApplicationTheme {
                var showHistory by remember { mutableStateOf(false) }

                GameSetupScreen(
                    initialSideOption = initialSide,
                    initialDifficulty = initialDifficulty,
                    initialGameMode = initialGameMode,
                    initialTimerOption = initialTimer,
                    initialCustomMinutes = initialCustomMinutes,
                    gameStatus = if (isGameInProgress) GameStatus.IN_PROGRESS else GameStatus.NOT_STARTED,
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
                    onReturnToCurrentGame = {
                        finish()
                    },
                    onOpenHistory = {
                        showHistory = true
                    },
                    hasPersistedGame = themeManager.getPersistedGameState() != null,
                    onLoadPersistedGame = {
                        launchPersistedGame()
                    }
                )

                if (showHistory) {
                    GameHistoryDialog(
                        onDismiss = { showHistory = false }
                    )
                }
            }
        }
    }

    private fun launchPersistedGame() {
        val intent = Intent(this, com.example.MainActivity::class.java).apply {
            putExtra("LOAD_PERSISTED", true)
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        hideSystemNavigationBars()
    }

    private fun hideSystemNavigationBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
            show(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
